package forex

import cats.effect.IO
import cats.effect.concurrent.Ref
import cats.syntax.either._
import forex.config.RatesCacheConfig
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.domain.Rate.Pair
import forex.programs.rates.Program
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.errors.Error
import forex.services.cache.interpreters.RatesCacheImpl
import forex.services.rates.errors
import weaver._

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt

object ProgramSpec extends SimpleIOSuite {
  val conf = RatesCacheConfig(5.minutes)
  val pair = Pair(Currency.JPY, Currency.USD)
  val rate = Rate(pair, Price(BigDecimal(1)), Timestamp(OffsetDateTime.now()))

  val oneFrameServiceMock = new forex.services.RatesService[IO] {
    def getRates(pairs: List[Pair]): IO[Either[errors.Error, Map[Pair, Rate]]] =
      IO(Map(pair -> rate).asRight)
  }

  test("should return desired pair (happy path)") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map.empty)
      cacheService = new RatesCacheImpl[IO](ref, conf)
      program      = Program[IO](oneFrameServiceMock, cacheService)
      result <- program.get(GetRatesRequest(Currency.JPY, Currency.USD))
    } yield expect(result == Right(rate))
  }

  test("should return error if rates service didn't return desired pair") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map.empty)
      cacheService = new RatesCacheImpl[IO](ref, conf)
      program      = Program[IO](oneFrameServiceMock, cacheService)
      result <- program.get(GetRatesRequest(Currency.USD, Currency.JPY))
    } yield expect(result == Left(Error.RateLookupFailed(s"Api didn't return desired pair")))
  }

  test("should return error if request contains the same currency") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map.empty)
      cacheService = new RatesCacheImpl[IO](ref, conf)
      program      = Program[IO](oneFrameServiceMock, cacheService)
      result <- program.get(GetRatesRequest(Currency.USD, Currency.USD))
    } yield expect(result == Left(Error.InvalidData(s"You should provide different currencies")))
  }
}
