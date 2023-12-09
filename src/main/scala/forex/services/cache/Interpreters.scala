package forex.services.cache

import cats.effect.concurrent.Ref
import cats.effect.{ Clock, Sync }
import cats.syntax.functor._
import forex.config.RatesCacheConfig
import forex.domain.Rate
import forex.domain.Rate.Pair
import forex.services.cache.interpreters.RatesCacheImpl

object Interpreters {
  def impl[F[_]: Sync: Clock](config: RatesCacheConfig): F[Algebra[F, Pair, Rate]] =
    Ref.of(Map.empty[Pair, Rate]).map(new RatesCacheImpl[F](_, config))
}
