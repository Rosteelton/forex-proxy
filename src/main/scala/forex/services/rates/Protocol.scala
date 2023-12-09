package forex.services.rates

import forex.domain.{ Currency, Price, Rate, Timestamp }
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import forex.http._

object Protocol {
  final case class CurrencyResponse(
      from: Currency,
      to: Currency,
      price: Price,
      time_stamp: Timestamp
  ) {
    def toDomain: Rate =
      Rate(Rate.Pair(from, to), price, time_stamp)
  }

  implicit val currencyResponseDecoder: Decoder[CurrencyResponse] = deriveDecoder[CurrencyResponse]
}
