package forex

import cats.effect.IO
import cats.effect.concurrent.Ref
import forex.config.RatesCacheConfig
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.domain.Rate.Pair
import forex.services.cache.interpreters.RatesCacheImpl
import weaver._

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt

object CacheRatesSpec extends SimpleIOSuite {

  val conf = RatesCacheConfig(5.minutes)

  val pair = Pair(Currency.JPY, Currency.USD)
  val rate = Rate(pair, Price(BigDecimal(1)), Timestamp(OffsetDateTime.now().minusMinutes(1)))

  test("should return None if cache is empty") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map.empty)
      service = new RatesCacheImpl[IO](ref, conf)
      result <- service.getIfValid(pair)
    } yield expect(result.isEmpty)
  }

  test("should return value if valid") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map(pair -> rate))
      service = new RatesCacheImpl[IO](ref, conf)
      result <- service.getIfValid(pair)
    } yield expect(result.contains(rate))
  }

  test("should not return value if older than ttl") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](
              Map(pair -> rate.copy(timestamp = Timestamp(OffsetDateTime.now().minusMinutes(6))))
            )
      service = new RatesCacheImpl[IO](ref, conf)
      result <- service.getIfValid(pair)
    } yield expect(result.isEmpty)
  }

  test("should update cache correctly") {
    for {
      ref <- Ref.of[IO, Map[Pair, Rate]](Map.empty)
      service = new RatesCacheImpl[IO](ref, conf)
      _ <- service.set(Map(pair -> rate))
      result <- service.getIfValid(pair)
    } yield expect(result.contains(rate))
  }

}
