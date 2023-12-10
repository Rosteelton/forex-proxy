package forex.programs.rates

import cats.MonadThrow
import cats.data.EitherT
import cats.syntax.either._
import forex.domain.Rate.Pair
import forex.domain._
import forex.programs.rates.errors.Error.InvalidData
import forex.programs.rates.errors._
import forex.services.{ RatesCacheService, RatesService }

class Program[F[_]: MonadThrow](
    ratesService: RatesService[F],
    ratesCache: RatesCacheService[F]
) extends Algebra[F] {

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    val pair = Pair(request.from, request.to)

    EitherT
      .fromEither[F](request.validate.leftMap(InvalidData(_)))
      .flatMap { _ =>
        EitherT.liftF(ratesCache.getIfValid(pair)).flatMap {
          case Some(rate) => EitherT.fromEither[F](rate.asRight[Error])
          case None =>
            for {
              newCache <- EitherT(ratesService.getRates(Currency.allPairs)).leftMap(toProgramError(_))
              result <- EitherT.fromEither(
                         newCache
                           .get(pair)
                           .toRight[Error](Error.RateLookupFailed(s"Api didn't return desired pair"))
                       )
              _ <- EitherT.liftF[F, Error, Unit](ratesCache.set(newCache))
            } yield result
        }
      }
      .value
  }
}

object Program {

  def apply[F[_]: MonadThrow](
      ratesService: RatesService[F],
      ratesCache: RatesCacheService[F]
  ): Algebra[F] = new Program[F](ratesService, ratesCache)
}
