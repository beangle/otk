import org.beangle.tools.sbt.Sas
import org.beangle.parent.Settings._
import org.beangle.parent.Dependencies._
import EtkDepends._

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.2-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/otk"),
    "scm:git@github.com:beangle/otk.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "beangle Starter"
ThisBuild / homepage := Some(url("http://beangle.github.io/otk/index.html"))

val pinyin4j = "com.belerweb" % "pinyin4j" % "2.5.1" % "test"

lazy val root = (project in file("."))
  .settings()
  .aggregate(captcha,doc  ,ws)

lazy val captcha = (project in file("captcha"))
  .settings(
    name := "beangle-otk-captcha",
    common,
    libraryDependencies ++= Seq(beangle_webmvc_support,cache_caffeine,scalatest)
  )

lazy val doc = (project in file("doc"))
  .settings(
    name := "beangle-otk-doc",
    common,
    libraryDependencies ++= Seq(beangle_webmvc_support,beangle_doc_pdf)
  )

lazy val ws = (project in file("ws"))
  .enablePlugins(WarPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(Sas.Tomcat % "test")
  ).dependsOn(captcha,doc)

publish / skip := true
