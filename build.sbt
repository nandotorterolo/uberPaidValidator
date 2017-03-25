name := "uberPaidValidator"

herokuAppName := "uberpaidvalidator"   // should only contain lowercase letters.

version := "1.0"

scalaVersion := "2.11.8"

crossPaths := false

val vaadinVersion = "7.6.5"

resolvers += "Scaladin Snapshots" at "http://henrikerola.github.io/repository/snapshots/"    // scaladin

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
  "com.vaadin" % "vaadin-server" % vaadinVersion,
  "com.vaadin" % "vaadin-client-compiled" % vaadinVersion,
  "com.vaadin" % "vaadin-themes" % vaadinVersion,
  "org.vaadin.addons" %% "scaladin" % "3.2-SNAPSHOT",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8.1",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

enablePlugins(JettyPlugin)

enablePlugins(HerokuDeploy)


