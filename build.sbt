name := "CloudSim_hw1"

version := "1.0"

scalaVersion := "2.12.6"

lazy val commonMathVersion = "3.6.1"
lazy val slf4jVersion = "1.7.5"
lazy val logbackVersion = "1.2.3"
lazy val typeSafeConfigVersion = "1.2.1"
lazy val pureConfigVersion = "0.10.0"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-math3" % commonMathVersion,
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  //"com.typesafe" % "config" % typeSafeConfigVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"

)