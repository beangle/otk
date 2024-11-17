import OtkDepends.*
import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.13"

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

ThisBuild / description := "Beangle online toolkit"
ThisBuild / homepage := Some(url("http://beangle.github.io/otk/index.html"))

val pinyin4j = "com.belerweb" % "pinyin4j" % "2.5.1"
val zxing = "com.google.zxing" % "javase" % "3.5.3"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(b_webmvc, b_cache, b_serializer, b_doc_pdf),
    libraryDependencies ++= Seq(pinyin4j, zxing, scalatest),
    libraryDependencies ++= Seq(spring_context, spring_beans, caffeine),
    libraryDependencies ++= Seq(logback_classic, logback_core)
  )
