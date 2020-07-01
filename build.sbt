name := "malinowka"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "pl.com.krx",
  scalaVersion := "2.12.10",
  test in assembly := {},
  libraryDependencies ++= Seq(
    "com.pi4j" % "pi4j-core" % "1.1",
    "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.1",
    "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.1",
    "org.apache.httpcomponents" % "httpclient" % "4.5.7"
  )
)

lazy val app = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("pl.com.krx.malinowka.Application"),
  )
