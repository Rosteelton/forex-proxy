package forex.programs.rates

import forex.domain.Currency
import cats.syntax.either._

object Protocol {
  final case class GetRatesRequest(
      from: Currency,
      to: Currency
  ) {
    def validate: Either[String, Unit] =
      if (from == to) "You should provide different currencies".asLeft[Unit] else ().asRight
  }
}
