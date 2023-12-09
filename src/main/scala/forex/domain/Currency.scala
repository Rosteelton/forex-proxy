package forex.domain

import enumeratum.EnumEntry.Uppercase
import enumeratum._

sealed trait Currency extends EnumEntry with Uppercase
object Currency extends Enum[Currency] with CirceEnum[Currency] {
  case object AUD extends Currency
  case object CAD extends Currency
  case object CHF extends Currency
  case object EUR extends Currency
  case object GBP extends Currency
  case object NZD extends Currency
  case object JPY extends Currency
  case object SGD extends Currency
  case object USD extends Currency

  val values: IndexedSeq[Currency] = findValues

  val allPairs: List[(Currency, Currency)] = {
    (for {
      value1 <- values
      value2 <- values
    } yield {
      if (value1 != value2) List((value1, value2)) else List.empty
    }).flatten.toList
  }
}
