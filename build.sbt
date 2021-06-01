name := """data-parser-service"""
organization := "com.ecorp"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.13"

libraryDependencies += caffeine
libraryDependencies += guice
libraryDependencies += "com.github.cb372" %% "scalacache-caffeine" % "0.28.0"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.0.4"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.0.4"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.ecorp.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.ecorp.binders._"
