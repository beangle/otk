import OtkDepends.*
import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*
import sbt.Keys.libraryDependencies

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.18-SNAPSHOT"

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
val language_en = "org.languagetool" % "language-en" % "6.6"
val guava = "com.google.guava" % "guava" % "33.2.1-jre"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(beangle_webmvc, beangle_cache, beangle_serializer, beangle_template),
    libraryDependencies ++= Seq(beangle_doc_pdf, beangle_doc_excel),
    libraryDependencies ++= Seq(pinyin4j, zxing, scalatest),
    libraryDependencies ++= Seq(spring_context, spring_beans, caffeine),
    libraryDependencies ++= Seq(logback_classic, logback_core),
    libraryDependencies ++= Seq(language_en, guava)
  )
