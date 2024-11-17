import sbt._


object OtkDepends {
  val commonsVer = "5.6.22"
  val jdbcVer = "1.0.6"
  val dataVer = "5.8.17"
  val cdiVer = "0.7.0"
  val webVer = "0.6.0"
  val serializerVer = "0.1.13"
  val cacheVer = "0.1.12"
  val templateVer = "0.1.21"
  val webmvcVer = "0.10.0"
  val buiVer = "0.0.1"
  val securityVer = "4.3.25"
  val idsVer = "0.3.21"
  val eventVer = "0.0.9"
  val docVer = "0.4.5"

  val b_commons = "org.beangle.commons" % "beangle-commons" % commonsVer
  val b_jdbc = "org.beangle.jdbc" % "beangle-jdbc" % jdbcVer
  val b_model = "org.beangle.data" % "beangle-model" % dataVer
  val b_cdi = "org.beangle.cdi" % "beangle-cdi" % cdiVer
  val b_cache = "org.beangle.cache" % "beangle-cache" % cacheVer
  val b_template = "org.beangle.template" % "beangle-template" % templateVer
  val b_web = "org.beangle.web" % "beangle-web" % webVer
  val b_webmvc = "org.beangle.webmvc" % "beangle-webmvc" % webmvcVer
  val b_bui_bootstrap = "org.beangle.bui" % "beangle-bui-bootstrap" % buiVer
  val b_serializer = "org.beangle.serializer" % "beangle-serializer" % serializerVer
  val b_security = "org.beangle.security" % "beangle-security" % securityVer
  val b_ids = "org.beangle.ids" % "beangle-ids" % idsVer
  val b_event = "org.beangle.event" % "beangle-event" % eventVer
  val b_doc_transfer = "org.beangle.doc" % "beangle-doc-transfer" % docVer
  val b_doc_pdf = "org.beangle.doc" % "beangle-doc-pdf" % docVer
}
