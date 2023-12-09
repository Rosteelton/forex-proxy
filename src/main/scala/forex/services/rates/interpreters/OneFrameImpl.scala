package forex.services.rates.interpreters

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.functor._
import forex.config.OneFrameApiConfig
import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.Protocol.CurrencyResponse
import forex.services.rates.errors._
import sttp.client3.circe._
import sttp.client3.{ SttpBackend, _ }

class OneFrameImpl[F[_]: Sync](client: SttpBackend[F, Any], config: OneFrameApiConfig) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    val req = basicRequest
      .get(uri"${config.baseUrl}/rates?pair=${pair.concatenate}")
      .header("token", config.token)
      .response(asJsonAlways[List[CurrencyResponse]])
      .readTimeout(config.timeout)

    client.send(req).map { resp =>
      resp.body
        .leftMap(err => Error.OneFrameLookupFailed(s"${resp.code.code}: ${err.body}"))
        .flatMap(
          _.headOption
            .fold((Error.OneFrameLookupFailed("Empty response"): Error).asLeft[CurrencyResponse])(_.asRight[Error])
        )
        .map(_.toDomain)
    }
  }
}
