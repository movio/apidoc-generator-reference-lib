package movio.cinema.apidoc.generator.reference

import scala.util.Try
import scala.util.Success

import org.joda.time.LocalDateTime
import org.mockito.Matchers.any
import org.mockito.Matchers.{ eq ⇒ is }

import com.typesafe.config.ConfigFactory

import movio.testtools.MovioSpec


class ValiationTest extends MovioSpec {
  import movio.apidoc.generator.reference.v0.models._

  describe("regex validation") {
    it("should validiate id field") {
      intercept[IllegalArgumentException] {
        Person("test-name", "name", None, None, Seq.empty, Gender.Male)
      }
    }
    it("should validiate regexs within a [string]") {
      // fail
    }
  }

  describe("array validation") {
    it("should validiate max length in array") {
      intercept[IllegalArgumentException] {
        Address("street", List(
                  "123456789012345678901"
                ))
      }
    }
    it("should validiate max length if one element is ok") {
      intercept[IllegalArgumentException] {
        Address("street", List(
                  "123",
                  "123456"
                ))
      }
    }
  }
}
