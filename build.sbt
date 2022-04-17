import OtkDepends._
import org.beangle.parent.Dependencies._
import org.beangle.parent.Settings._
import org.beangle.tools.sbt.UndertowPlugin

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.5-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/otk"),
    "scm:git@github.com:beangle/otk.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "beangle Starter"
ThisBuild / homepage := Some(url("http://beangle.github.io/otk/index.html"))

val pinyin4j = "com.belerweb" % "pinyin4j" % "2.5.1"

lazy val root = (project in file("."))
  .settings()
  .aggregate(captcha, doc, sns, code, ws)

lazy val captcha = (project in file("captcha"))
  .settings(
    name := "beangle-otk-captcha",
    common,
    libraryDependencies ++= Seq(b_webmvc_spring, b_cache_caffeine, scalatest)
  )

lazy val sns = (project in file("sns"))
  .settings(
    name := "beangle-otk-sns",
    common,
    libraryDependencies ++= Seq(b_webmvc_spring, pinyin4j, scalatest),
    libraryDependencies ++= Seq(b_serializer_text)
  )

lazy val doc = (project in file("doc"))
  .settings(
    name := "beangle-otk-doc",
    common,
    libraryDependencies ++= Seq(b_webmvc_spring, b_doc_pdf)
  )

lazy val code = (project in file("code"))
  .settings(
    name := "beangle-otk-code",
    common,
    libraryDependencies ++= Seq(b_webmvc_spring, "com.google.zxing" % "javase" % "3.4.1")
  )

lazy val ws = (project in file("ws"))
  .enablePlugins(WarPlugin, UndertowPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(logback_classic, logback_core)
  ).dependsOn(captcha, doc, sns, code)

publish / skip := true
