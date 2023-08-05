import OtkDepends.*
import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.8-SNAPSHOT"

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
  .aggregate(captcha, doc, sns, code, sys, ws)

lazy val captcha = (project in file("captcha"))
  .settings(
    name := "beangle-otk-captcha",
    common,
    libraryDependencies ++= Seq(webmvcSupport, b_cache_caffeine, scalatest)
  )

lazy val sns = (project in file("sns"))
  .settings(
    name := "beangle-otk-sns",
    common,
    libraryDependencies ++= Seq(webmvcSupport, pinyin4j, scalatest),
    libraryDependencies ++= Seq(b_serializer_text)
  )

lazy val sys = (project in file("sys"))
  .settings(
    name := "beangle-otk-sys",
    common,
    libraryDependencies ++= Seq(webmvcSupport, scalatest)
  )

lazy val doc = (project in file("doc"))
  .settings(
    name := "beangle-otk-doc",
    common,
    libraryDependencies ++= Seq(webmvcSupport, b_doc_pdf)
  )

lazy val code = (project in file("code"))
  .settings(
    name := "beangle-otk-code",
    common,
    libraryDependencies ++= Seq(webmvcSupport, "com.google.zxing" % "javase" % "3.5.1")
  )

lazy val ws = (project in file("ws"))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(logback_classic, logback_core)
  ).dependsOn(captcha, doc, sns, sys, code)

publish / skip := true
