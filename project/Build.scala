import sbt._

object OtkDepends {
  val commonsVer = "5.6.10"
  val dataVer = "5.8.0"
  val cdiVer = "0.6.2"
  val webVer = "0.4.10"
  val serializerVer = "0.1.8"
  val cacheVer = "0.1.7"
  val templateVer = "0.1.10"
  val webmvcVer = "0.9.23"
  val securityVer = "4.3.16"
  val docVer="0.3.2"

  val commonsCore = "org.beangle.commons" %% "beangle-commons-core" % commonsVer
  val commonsFile = "org.beangle.commons" %% "beangle-commons-file" % commonsVer
  val dataJdbc = "org.beangle.data" %% "beangle-data-jdbc" % dataVer
  val dataOrm = "org.beangle.data" %% "beangle-data-orm" % dataVer
  val dataTransfer = "org.beangle.data" %% "beangle-data-transfer" % dataVer
  val cdiApi = "org.beangle.cdi" %% "beangle-cdi-api" % cdiVer
  val cdiSpring = "org.beangle.cdi" %% "beangle-cdi-spring" % cdiVer
  val cache_api = "org.beangle.cache" %% "beangle-cache-api" % cacheVer
  val b_cache_caffeine = "org.beangle.cache" %% "beangle-cache-caffeine" % cacheVer
  val templateApi = "org.beangle.template" %% "beangle-template-api" % templateVer
  val templateFreemarker = "org.beangle.template" %% "beangle-template-freemarker" % templateVer
  val webAction = "org.beangle.web" %% "beangle-web-action" % webVer
  val webServlet = "org.beangle.web" %% "beangle-web-servlet" % webVer
  val webmvcCore= "org.beangle.webmvc" %% "beangle-webmvc-core" % webmvcVer
  val webmvcSupport= "org.beangle.webmvc" %% "beangle-webmvc-support" % webmvcVer
  val b_serializer_text = "org.beangle.serializer" %% "beangle-serializer-text" % serializerVer
  val b_doc_pdf = "org.beangle.doc" %% "beangle-doc-pdf" % docVer
}
