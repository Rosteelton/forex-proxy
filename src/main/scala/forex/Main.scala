package forex

import scala.concurrent.ExecutionContext
import cats.effect._
import forex.config._
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.client3.SttpBackend
import sttp.client3.http4s.Http4sBackend
import sttp.client3.logging.slf4j.Slf4jLoggingBackend

object Main extends IOApp {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  val blocker: cats.effect.Blocker  = Blocker.liftExecutionContext(ExecutionContext.global)

  override def run(args: List[String]): IO[ExitCode] = {
    val sttpClient = Http4sBackend.usingDefaultBlazeClientBuilder[IO](blocker).map(Slf4jLoggingBackend(_))

    sttpClient.use { sttp =>
      new Application[IO].stream(executionContext, sttp).compile.drain.as(ExitCode.Success)
    }
  }
}

class Application[F[_]: ConcurrentEffect: Timer] {

  def stream(ec: ExecutionContext, sttpBackend: SttpBackend[F, Any]): Stream[F, Unit] =
    for {
      config <- Config.stream("app")
      module = new Module[F](config, sttpBackend)
      _ <- BlazeServerBuilder[F](ec)
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()
}
