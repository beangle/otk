import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.27-SNAPSHOT"

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

val beangle_she = "org.beangle.she" % "beangle-she" % "0.0.1"
val beangle_cache = "org.beangle.cache" % "beangle-cache" % "0.1.19"
val beangle_serializer = "org.beangle.serializer" % "beangle-serializer" % "0.1.24"
val beangle_doc_pdf = "org.beangle.doc" % "beangle-doc-pdf" % "0.5.2"
val beangle_doc_excel = "org.beangle.doc" % "beangle-doc-excel" % "0.5.2"

val pinyin4j = "com.belerweb" % "pinyin4j" % "2.5.1"
val zxing = "com.google.zxing" % "javase" % "3.5.4"
val language_en = "org.languagetool" % "language-en" % "6.7"
val guava = "com.google.guava" % "guava" % "33.2.1-jre"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(beangle_she, beangle_serializer),
    libraryDependencies ++= Seq(beangle_doc_pdf, beangle_doc_excel, jodconverter_local, libreoffice),
    libraryDependencies ++= Seq(pinyin4j, zxing),
    libraryDependencies ++= Seq(beangle_cache, caffeine, jedis),
    libraryDependencies ++= Seq(language_en, guava),
    libraryDependencies ++= Seq(scalatest)
  )
