package forex.config

import org.http4s.Uri

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    oneFrameApi: OneFrameApiConfig,
    ratesCache: RatesCacheConfig
)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)

case class OneFrameApiConfig(
    baseUrl: Uri,
    token: String,
    timeout: FiniteDuration
)

case class RatesCacheConfig(
    ttl: FiniteDuration
)
