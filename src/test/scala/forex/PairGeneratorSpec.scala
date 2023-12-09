package forex

import forex.domain.Currency
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PairGeneratorSpec extends AnyFlatSpec with Matchers {

  it should "generate all available pairs" in {
    Currency.allPairs.size shouldEqual Currency.values.size * Currency.values.size - Currency.values.size
    Currency.allPairs should contain((Currency.USD, Currency.JPY))
    Currency.allPairs should contain((Currency.JPY, Currency.USD))
  }
}
