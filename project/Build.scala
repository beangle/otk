import sbt.Keys._
import sbt._
import org.beangle.parent.Dependencies._

object EtkDepends {
  val commonsVer = "5.2.11"
  val dataVer = "5.3.27"
  val cdiVer = "0.3.4"
  val webVer = "0.0.4"
  val serializerVer= "0.0.22"
  val cacheVer= "0.0.25"
  val templateVer ="0.0.36"
  val webmvcVer="0.4.8"
  val securityVer="4.2.32"
  val docVer="0.0.8"

  val commonsCore = "org.beangle.commons" %% "beangle-commons-core" % commonsVer
  val commonsFile = "org.beangle.commons" %% "beangle-commons-file" % commonsVer
  val dataJdbc = "org.beangle.data" %% "beangle-data-jdbc" % dataVer
  val dataOrm = "org.beangle.data" %% "beangle-data-orm" % dataVer
  val dataModel = "org.beangle.data" %% "beangle-data-model" % dataVer
  val dataHibernate = "org.beangle.data" %% "beangle-data-hibernate" % dataVer
  val dataTransfer = "org.beangle.data" %% "beangle-data-transfer" % dataVer
  val cdiApi = "org.beangle.cdi" %% "beangle-cdi-api" % cdiVer
  val cdiSpring = "org.beangle.cdi" %% "beangle-cdi-spring" % cdiVer
  val cache_api = "org.beangle.cache" %% "beangle-cache-api" % cacheVer
  val cache_caffeine = "org.beangle.cache" %% "beangle-cache-caffeine" % cacheVer
  val templateApi = "org.beangle.template" %% "beangle-template-api" % templateVer
  val templateFreemarker = "org.beangle.template" %% "beangle-template-freemarker" % templateVer
  val webAction = "org.beangle.web" %% "beangle-web-action" % webVer
  val webServlet = "org.beangle.web" %% "beangle-web-servlet" % webVer
  val webmvcCore= "org.beangle.webmvc" %% "beangle-webmvc-core" % webmvcVer
  val webmvcFreemarker= "org.beangle.webmvc" %% "beangle-webmvc-freemarker" % webmvcVer
  val beangle_webmvc_support= "org.beangle.webmvc" %% "beangle-webmvc-support" % webmvcVer
  val serializerText = "org.beangle.serializer" %% "beangle-serializer-text" % serializerVer
  val beangle_doc_pdf = "org.beangle.doc" %% "beangle-doc-pdf" % docVer

  val appDepends = Seq(commonsCore, logback_classic, logback_core, scalatest, webAction,cdiApi)
}
