name := "ch1"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.3",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test",
    "org.scalatest" % "scalatest_2.10" % "3.0.0" % "test"
)
