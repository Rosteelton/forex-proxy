package forex.services.rates

import cats.Applicative
import cats.effect.Sync
import forex.config.OneFrameApiConfig
import interpreters._
import sttp.client3.SttpBackend

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]()
  def impl[F[_]: Sync](client: SttpBackend[F, Any], config: OneFrameApiConfig): Algebra[F] =
    new OneFrameImpl[F](client, config)
}
