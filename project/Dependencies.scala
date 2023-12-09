import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.6.1"
    val catsEffect = "2.5.1"
    val fs2        = "2.5.4"
    val http4s     = "0.22.15"
    val circe      = "0.14.2"
    val pureConfig = "0.17.4"
    val sttp       = "3.9.1"

    val kindProjector  = "0.13.2"
    val logback        = "1.2.3"
    val scalaCheck     = "1.15.3"
    val scalaTest      = "3.2.7"
    val catsScalaCheck = "0.3.2"
    val enumeratum     = "1.7.3"
  }

  object Libraries {
    def circe(artifact: String): ModuleID      = "io.circe"                      %% artifact % Versions.circe
    def http4s(artifact: String): ModuleID     = "org.http4s"                    %% artifact % Versions.http4s
    def enumeratum(artifact: String): ModuleID = "com.beachape"                  %% artifact % Versions.enumeratum
    def sttp(artifact: String): ModuleID       = "com.softwaremill.sttp.client3" %% artifact % Versions.sttp
    def pureConfig(artifact: String): ModuleID = "com.github.pureconfig"         %% artifact % Versions.pureConfig

    lazy val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect
    lazy val fs2        = "co.fs2"        %% "fs2-core"    % Versions.fs2

    lazy val http4sDsl       = http4s("http4s-dsl")
    lazy val http4sServer    = http4s("http4s-blaze-server")
    lazy val http4sCirce     = http4s("http4s-circe")
    lazy val http4sClient    = http4s("http4s-blaze-client")
    lazy val circeCore       = circe("circe-core")
    lazy val circeGeneric    = circe("circe-generic")
    lazy val circeGenericExt = circe("circe-generic-extras")
    lazy val circeParser     = circe("circe-parser")

    lazy val enumeratum: ModuleID       = enumeratum("enumeratum")
    lazy val enumeratumCirce: ModuleID  = enumeratum("enumeratum-circe")
    lazy val sttp: ModuleID             = sttp("http4s-ce2-backend")
    lazy val sttpCirce: ModuleID        = sttp("circe")
    lazy val sttpLogging: ModuleID      = sttp("slf4j-backend")
    lazy val pureConfig: ModuleID       = pureConfig("pureconfig")
    lazy val pureConfigHttp4s: ModuleID = pureConfig("pureconfig-http4s022")

    // Compiler plugins
    lazy val kindProjector = "org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full

    // Runtime
    lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    lazy val scalaTest      = "org.scalatest"     %% "scalatest"       % Versions.scalaTest
    lazy val scalaCheck     = "org.scalacheck"    %% "scalacheck"      % Versions.scalaCheck
    lazy val catsScalaCheck = "io.chrisdavenport" %% "cats-scalacheck" % Versions.catsScalaCheck
  }

}
