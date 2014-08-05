name := """lib-emr-manager"""

version := "0.0.1"

scalaVersion := "2.10.4"

organization := "com.gilt"

libraryDependencies ++= Seq(
  "com.amazonaws"               % "aws-java-sdk"  % "1.8.7"
)

libraryDependencies ++= Seq(
  "junit"         % "junit"         % "4.8.1" % "test",
  "org.scalatest" %% "scalatest"    % "2.1.3" % "test" ,
  "org.mockito"   % "mockito-core"  % "1.9.5" % "test"
)

