import Implicits._
import ProjectKeys._

val scala3Version = "3.3.1"

ThisBuild / tlBaseVersion := "0.0"

ThisBuild / projectName := "cdp-scala"
ThisBuild / organization := "dev.capslock"
ThisBuild / organizationName := "cdp-scala authors"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("windymelt", "Windymelt")
)

ThisBuild / scalaVersion := scala3Version
ThisBuild / crossScalaVersions := Seq(scala3Version)

lazy val compileSettings = Def.settings(
  tlFatalWarnings := true,
  scalacOptions --= Seq(
    // Scala 3.0.1以降だとうまく動かない
    // https://github.com/lampepfl/dotty/issues/14952
    "-Ykind-projector:underscores",
    "-Wunused:locals"
  ),
  Test / scalacOptions --= Seq(
    // テストだとちょっと厳しすぎる
    "-Wunused:locals"
  ),
  Compile / console / scalacOptions --= Seq(
    // コンソールで import した瞬間はまだ使ってないから当然許したい
    "-Wunused:imports"
  )
)

lazy val root = tlCrossRootProject
  .aggregate(core)
  .settings(compileSettings)
  .settings(
    console := (core.jvm / Compile / console).value,
    Test / console := (core.jvm / Test / console).value
  )

val http4sVersion = "0.23.25"
val circeVersion = "0.14.1"
lazy val core = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .asModuleWithoutSuffix
  .settings(compileSettings)
  .settings(
    description := "Chrome DevTools Protocol wrapper for Scala",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "os-lib" % "0.11.3",
      "org.http4s" %%% "http4s-jdk-http-client" % "0.9.1",
      "org.http4s" %%% "http4s-dsl" % http4sVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "com.github.tarao" %%% "record4s" % "0.11.0",
      "com.github.tarao" %%% "record4s-circe" % "0.11.0"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
    // addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full), // for circe
  )

ThisBuild / githubWorkflowJavaVersions := Seq(
  // No support for 8 because JDK Http Client comes from 11
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)

ThisBuild / githubWorkflowTargetBranches := Seq("main")

ThisBuild / tlCiReleaseBranches := Seq()

//
// Document
//

import laika.helium.Helium

import laika.helium.config._
import laika.ast.Path.Root

lazy val docs = project
  .in(file("site"))
  .dependsOn(core.jvm)
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    scalacOptions --= Seq(
      // ドキュメントの例ではローカル変数を使わないこともあるだろう
      "-Wunused:locals"
    ),
    laikaTheme := Helium.defaults.site
      .topNavigationBar(
        homeLink = IconLink.internal(Root / "index.md", HeliumIcon.home),
        navLinks = Seq(
        ),
        versionMenu = VersionMenu.create(
          versionedLabelPrefix = "Version:",
          unversionedLabel = "Choose Version"
        ),
        highContrast = true
      )
      .build
  )
