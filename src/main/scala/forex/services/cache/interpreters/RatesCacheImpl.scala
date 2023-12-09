package forex.services.cache.interpreters

import cats.Monad
import cats.effect.Clock
import cats.effect.concurrent.Ref
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import forex.config.RatesCacheConfig
import forex.domain.Rate.Pair
import forex.domain.{ Rate, Timestamp }
import forex.services.cache.Algebra

import java.time.Instant
import java.util.concurrent.TimeUnit

class RatesCacheImpl[F[_]: Monad: Clock](state: Ref[F, Map[Pair, Rate]], cacheConfig: RatesCacheConfig)
    extends Algebra[F, Pair, Rate] {

  private def isValid(rateTimestamp: Timestamp, nowSeconds: Long): Boolean =
    Instant
      .ofEpochSecond(nowSeconds)
      .isBefore(rateTimestamp.value.toInstant.plusSeconds(cacheConfig.ttl.toSeconds))

  def getIfValid(key: Pair): F[Option[Rate]] =
    state.get.map(_.get(key)).flatMap {
      case result @ Some(rate) =>
        Clock[F].realTime(TimeUnit.SECONDS).map { now =>
          if (isValid(rate.timestamp, now)) result else None
        }

      case _ => none[Rate].pure[F]
    }

  def set(newCache: Map[Pair, Rate]): F[Unit] = state.set(newCache)
}
