package movio.apidoc.generator.reference

import scala.util.Try
import scala.util.Success

import org.joda.time.DateTime
import org.joda.time.format._
import org.mockito.Matchers.any
import org.mockito.Matchers.{ eq ⇒ is }
import play.api.libs.json._
import com.fasterxml.jackson.core.JsonParseException

import com.typesafe.config.ConfigFactory
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat

import movio.testtools.MovioSpec


class JsonParsingTest extends MovioSpec {
  import movio.apidoc.generator.reference.v0.models._
  import movio.apidoc.generator.reference.v0.models.json._

  describe("datetime validation") {

    it("should throw an jsresultexception if the datetime has no tz offset") {
      intercept[JsResultException] {
        val json = """{"id":"1","name":"John","lastActiveTime":"2016-12-24T15:36:54","addresses":[], "gender": "male"}"""

        Json.parse(json).as[Person]
      }
    }

    it("should UTC as tz offset") {
      val json = """{"id":"1","name":"John","lastActiveTime":"2016-12-24T15:36:54Z","addresses":[], "gender": "male"}"""
      val expected = new DateTime(2016, 12, 24, 15, 36, 54, DateTimeZone.UTC)

      Json.parse(json).as[Person].lastActiveTime.get shouldBe expected
    }

    it("should retain offsets") {
      val json = """{"id":"1","name":"John","lastActiveTime":"2016-12-24T15:36:54+0400","addresses":[], "gender": "male"}"""

      Json.parse(json).as[Person].lastActiveTime.get.getZone.getID shouldBe "+04:00"
    }

    it("should support milliseconds") {
      val json = """{"id":"1","name":"John","lastActiveTime":"2016-12-24T15:36:54.932+0400","addresses":[], "gender": "male"}"""
      val expected = new DateTime(2016, 12, 24, 15, 36, 54, 932, DateTimeZone.forOffsetHours(4))

      Json.parse(json).as[Person].lastActiveTime.get shouldBe expected
    }

  }

  describe("enum validation") {

    it("should throw an JsonParseException if the enum value is invalid") {
      intercept[JsonParseException] {
        val json = """{"id":"1","name":"John","lastActiveTime":"2016-12-24T15:36:54Z","addresses":[], gender: "not_real"}"""

        Json.parse(json).as[Person]
      }
    }
  }
}
