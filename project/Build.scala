import sbt._

object OtkDepends {
  val commonsVer = "5.6.16"
  val jdbcVer = "1.0.1"
  val dataVer = "5.8.10"
  val cdiVer = "0.6.6"
  val webVer = "0.4.12"
  val serializerVer = "0.1.10"
  val cacheVer = "0.1.9"
  val templateVer = "0.1.15"
  val webmvcVer = "0.9.27"
  val securityVer = "4.3.20"
  val idsVer = "0.3.17"
  val eventVer = "0.0.5"
  val docVer = "0.3.4"

  val b_commons = "org.beangle.commons" % "beangle-commons" % commonsVer
  val b_jdbc = "org.beangle.jdbc" % "beangle-jdbc" % jdbcVer
  val b_model = "org.beangle.data" % "beangle-model" % dataVer
  val b_cdi = "org.beangle.cdi" % "beangle-cdi" % cdiVer
  val b_cache = "org.beangle.cache" % "beangle-cache" % cacheVer
  val b_template = "org.beangle.template" % "beangle-template" % templateVer
  val b_web = "org.beangle.web" % "beangle-web" % webVer
  val b_webmvc = "org.beangle.webmvc" % "beangle-webmvc" % webmvcVer
  val b_serializer = "org.beangle.serializer" % "beangle-serializer" % serializerVer
  val b_security = "org.beangle.security" % "beangle-security" % securityVer
  val b_ids = "org.beangle.ids" % "beangle-ids" % idsVer
  val b_event = "org.beangle.event" % "beangle-event" % eventVer
  val b_doc_transfer = "org.beangle.doc" % "beangle-doc-transfer" % docVer
  val b_doc_pdf = "org.beangle.doc" % "beangle-doc-pdf" % docVer
}
