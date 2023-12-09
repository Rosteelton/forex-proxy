package forex.services.rates.interpreters

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.applicativeError._
import forex.config.OneFrameApiConfig
import forex.domain.Rate
import forex.domain.Rate.Pair
import forex.services.rates.Algebra
import forex.services.rates.Protocol.CurrencyResponse
import forex.services.rates.errors._
import sttp.client3.circe._
import sttp.client3.{ SttpBackend, _ }
import sttp.model.QueryParams

class OneFrameImpl[F[_]: Sync](client: SttpBackend[F, Any], config: OneFrameApiConfig) extends Algebra[F] {

  def getRates(pairs: List[Pair]): F[Either[Error, Map[Pair, Rate]]] = {
    val queryParams = QueryParams(Seq(("pair", pairs.map(_.concatenate))))

    val req = basicRequest
      .get(uri"${config.baseUrl}/rates".addParams(queryParams))
      .header("token", config.token)
      .response(asJsonAlways[List[CurrencyResponse]])
      .readTimeout(config.timeout)

    client
      .send(req)
      .map { resp =>
        resp.body
          .leftMap(err => Error.OneFrameLookupFailed(s"${resp.code.code}: ${err.body}"): Error)
          .map(_.map(resp => (Pair(resp.from, resp.to), resp.toDomain)).toMap)
      }
      .handleError { th =>
        Error.OneFrameLookupFailed(th.getMessage).asLeft[Map[Pair, Rate]]
      }
  }
}
