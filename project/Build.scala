import sbt.Keys._
import sbt._

object OrderCheckoutBundles extends Build {
  import Deps._
  import Reps._

  val buildOrganization = "org.hfgiii"
  val buildVersion = "0.1.0-SNAPSHOT"
  val buildPublishTo = None
  val buildScalaVersion = "2.11.5"

  val buildParentName = "parent"

  val BaseSettings = Project.defaultSettings ++ Seq(
    organization := buildOrganization,
    publishTo := buildPublishTo,
    scalaVersion := buildScalaVersion,
    crossScalaVersions := Seq("2.10.2", "2.11.5"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature"),
    resolvers := reps)

  def ocBundlesProject(projectName: String): Project = {
    Project(
      id = projectName,
      base = file(projectName),
      settings = BaseSettings ++ Seq(
        name := projectName,
        version := buildVersion))
  }

  lazy val root = Project(
    id = buildParentName,
    base = file("."),
    settings = BaseSettings) aggregate ocBundlerProject


  lazy val ocBundlerProject =
    ocBundlesProject("bundler").
      settings(libraryDependencies := deps ++ logDeps ++ testingDeps)

}

object Reps {
  val reps = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/")
}

object Deps {

  val logDeps = List(
    "org.slf4j" % "jcl-over-slf4j" % "1.7.7",
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.0.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
    "ch.qos.logback" % "logback-core" % "1.1.2" % "runtime")

  val deps = List(
    "com.typesafe" % "config" % "1.2.0",
    "joda-time" % "joda-time" % "2.8",
    "org.joda" % "joda-convert" % "1.7")

  val testingDeps = List(
    "org.specs2" %% "specs2" % "2.4.2" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test")
}
