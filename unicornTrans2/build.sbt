import sbt._
import Keys._

val commonDeps = Seq(
	"org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

lazy val unicornTrans = Project("unicornTrans", file("./")).settings(
  version := "0.0.1",
  organization := "unicornTrans",
  scalaVersion := "2.11.5",
  crossPaths := false,
  parallelExecution in Test := false,
  scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked"),
  javacOptions in Compile ++= Seq("-g", "-Xlint:unchecked", "-Xlint:deprecation"),
  libraryDependencies ++= commonDeps,
  resolvers ++= Seq(
	    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    )
  )
