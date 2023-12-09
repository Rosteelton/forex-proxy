package forex.programs.rates

import cats.MonadThrow
import cats.data.EitherT
import errors._
import forex.domain.Rate.Pair
import forex.domain._
import cats.syntax.flatMap._
import cats.syntax.either._
import cats.syntax.applicative._
import forex.services.{ RatesCacheService, RatesService }

class Program[F[_]: MonadThrow](
    ratesService: RatesService[F],
    ratesCache: RatesCacheService[F]
) extends Algebra[F] {

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    val pair = Pair(request.from, request.to)

    ratesCache.getIfValid(pair).flatMap {
      case Some(rate) => rate.asRight[Error].pure[F]
      case None =>
        (for {
          newCache <- EitherT(ratesService.getRates(Currency.allPairs)).leftMap(toProgramError(_))
          result <- EitherT.fromEither(
                     newCache
                       .get(pair)
                       .toRight[Error](Error.RateLookupFailed(s"Api didn't return desired pair"))
                   )
          _ <- EitherT.liftF[F, Error, Unit](ratesCache.set(newCache))
        } yield result).value
    }
  }
}

object Program {

  def apply[F[_]: MonadThrow](
      ratesService: RatesService[F],
      ratesCache: RatesCacheService[F]
  ): Algebra[F] = new Program[F](ratesService, ratesCache)

}
