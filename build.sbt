import OtkDepends.*
import org.beangle.build.sbt.SnapshotPlugin.autoImport.{snapshotCredentials, snapshotRepoUrl}
import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*
import sbt.Keys.libraryDependencies

ThisBuild / organization := "org.beangle.otk"
ThisBuild / version := "0.0.26"

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
val zxing = "com.google.zxing" % "javase" % "3.5.4"
val language_en = "org.languagetool" % "language-en" % "6.7"
val guava = "com.google.guava" % "guava" % "33.2.1-jre"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    snapshotCredentials := Path.userHome / ".sbt" / "snapshot_credentials",
    snapshotRepoUrl := "http://sas.openurp.net/sas/repo/snapshot/upload/{fileName}",
    libraryDependencies ++= Seq(beangle_webmvc, beangle_cache, beangle_serializer, beangle_template),
    libraryDependencies ++= Seq(beangle_commons, beangle_doc_pdf, beangle_doc_excel, beangle_cdi),
    libraryDependencies ++= Seq(pinyin4j, zxing, scalatest, jedis),
    libraryDependencies ++= Seq(spring_context, spring_beans, caffeine),
    libraryDependencies ++= Seq(slf4j, logback_classic, logback_core),
    libraryDependencies ++= Seq(language_en, guava, beangle_config, typesafe_config),
    libraryDependencies ++= Seq(jodconverter_local, libreoffice)
  )
