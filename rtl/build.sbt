
name := "NRV-chisel"

scalaVersion := "2.13.12"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:reflectiveCalls")

addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % "6.4.0" cross CrossVersion.full)

libraryDependencies += "org.chipsalliance" %% "chisel" % "6.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test
libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "6.0.0" % Test

resolvers ++= Resolver.sonatypeOssRepos("snapshots")
resolvers ++= Resolver.sonatypeOssRepos("releases")

trapExit := false
