/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.0-SNAPSHOT
 * apidoc:0.9.48 http://localhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/play_2_4_client
 */
package movio.apidoc.generator.reference.v0.models {

  case class Address(
    street: String,
    tags: Seq[String]
  )

  /**
   * An error message from the API.
   */
  case class Error(
    status: String,
    message: String
  )

  case class Healthcheck(
    status: String = "healthy"
  )

  case class KafkaMovie(
    v0: movio.apidoc.generator.reference.v0.models.Person,
    utcGeneratedTime: _root_.org.joda.time.LocalDate
  )

  /**
   * This is a person
   */
  case class Person(
    id: String,
    name: String,
    dob: _root_.scala.Option[_root_.org.joda.time.LocalDate] = None,
    addresses: Seq[movio.apidoc.generator.reference.v0.models.Address]
  )

}

package movio.apidoc.generator.reference.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import movio.apidoc.generator.reference.v0.models.json._

    private[v0] implicit val jsonReadsUUID = __.read[String].map(java.util.UUID.fromString)

    private[v0] implicit val jsonWritesUUID = new Writes[java.util.UUID] {
      def writes(x: java.util.UUID) = JsString(x.toString)
    }

    private[v0] implicit val jsonReadsJodaDateTime = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateTimeParser
      dateTimeParser.parseDateTime(str)
    }

    private[v0] implicit val jsonWritesJodaDateTime = new Writes[org.joda.time.DateTime] {
      def writes(x: org.joda.time.DateTime) = {
        import org.joda.time.format.ISODateTimeFormat.dateTime
        val str = dateTime.print(x)
        JsString(str)
      }
    }

    implicit def jsonReadsApidocGeneratorReferenceAddress: play.api.libs.json.Reads[Address] = {
      (
        (__ \ "street").read[String] and
        (__ \ "tags").read[Seq[String]]
      )(Address.apply _)
    }

    implicit def jsonWritesApidocGeneratorReferenceAddress: play.api.libs.json.Writes[Address] = {
      (
        (__ \ "street").write[String] and
        (__ \ "tags").write[Seq[String]]
      )(unlift(Address.unapply _))
    }

    implicit def jsonReadsApidocGeneratorReferenceError: play.api.libs.json.Reads[Error] = {
      (
        (__ \ "status").read[String] and
        (__ \ "message").read[String]
      )(Error.apply _)
    }

    implicit def jsonWritesApidocGeneratorReferenceError: play.api.libs.json.Writes[Error] = {
      (
        (__ \ "status").write[String] and
        (__ \ "message").write[String]
      )(unlift(Error.unapply _))
    }

    implicit def jsonReadsApidocGeneratorReferenceHealthcheck: play.api.libs.json.Reads[Healthcheck] = {
      (__ \ "status").read[String].map { x => new Healthcheck(status = x) }
    }

    implicit def jsonWritesApidocGeneratorReferenceHealthcheck: play.api.libs.json.Writes[Healthcheck] = new play.api.libs.json.Writes[Healthcheck] {
      def writes(x: Healthcheck) = play.api.libs.json.Json.obj(
        "status" -> play.api.libs.json.Json.toJson(x.status)
      )
    }

    implicit def jsonReadsApidocGeneratorReferenceKafkaMovie: play.api.libs.json.Reads[KafkaMovie] = {
      (
        (__ \ "v0").read[movio.apidoc.generator.reference.v0.models.Person] and
        (__ \ "utc_generated_time").read[_root_.org.joda.time.LocalDate]
      )(KafkaMovie.apply _)
    }

    implicit def jsonWritesApidocGeneratorReferenceKafkaMovie: play.api.libs.json.Writes[KafkaMovie] = {
      (
        (__ \ "v0").write[movio.apidoc.generator.reference.v0.models.Person] and
        (__ \ "utc_generated_time").write[_root_.org.joda.time.LocalDate]
      )(unlift(KafkaMovie.unapply _))
    }

    implicit def jsonReadsApidocGeneratorReferencePerson: play.api.libs.json.Reads[Person] = {
      (
        (__ \ "id").read[String] and
        (__ \ "name").read[String] and
        (__ \ "dob").readNullable[_root_.org.joda.time.LocalDate] and
        (__ \ "addresses").read[Seq[movio.apidoc.generator.reference.v0.models.Address]]
      )(Person.apply _)
    }

    implicit def jsonWritesApidocGeneratorReferencePerson: play.api.libs.json.Writes[Person] = {
      (
        (__ \ "id").write[String] and
        (__ \ "name").write[String] and
        (__ \ "dob").writeNullable[_root_.org.joda.time.LocalDate] and
        (__ \ "addresses").write[Seq[movio.apidoc.generator.reference.v0.models.Address]]
      )(unlift(Person.unapply _))
    }
  }
}

package movio.apidoc.generator.reference.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}
    import org.joda.time.{DateTime, LocalDate}
    import org.joda.time.format.ISODateTimeFormat
    import movio.apidoc.generator.reference.v0.models._

    // Type: date-time-iso8601
    implicit val pathBindableTypeDateTimeIso8601 = new PathBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    implicit val queryStringBindableTypeDateTimeIso8601 = new QueryStringBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    // Type: date-iso8601
    implicit val pathBindableTypeDateIso8601 = new PathBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )

    implicit val queryStringBindableTypeDateIso8601 = new QueryStringBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )



  }

}


package movio.apidoc.generator.reference.v0 {

  object Constants {

    val UserAgent = "apidoc:0.9.48 http://localhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/play_2_4_client"
    val Version = "0.1.0-SNAPSHOT"
    val VersionMajor = 0

  }

  class Client(
    apiUrl: String,
    auth: scala.Option[movio.apidoc.generator.reference.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) {
    import movio.apidoc.generator.reference.v0.models.json._

    private[this] val logger = play.api.Logger("movio.apidoc.generator.reference.v0.Client")

    logger.info(s"Initializing movio.apidoc.generator.reference.v0.Client for url $apiUrl")

    def healthchecks: Healthchecks = Healthchecks

    def people: People = People

    object Healthchecks extends Healthchecks {
      override def getInternalAndHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[movio.apidoc.generator.reference.v0.models.Healthcheck] = {
        _executeRequest("GET", s"/_internal_/healthcheck").map {
          case r if r.status == 200 => _root_.movio.apidoc.generator.reference.v0.Client.parseJson("movio.apidoc.generator.reference.v0.models.Healthcheck", r, _.validate[movio.apidoc.generator.reference.v0.models.Healthcheck])
          case r => throw new movio.apidoc.generator.reference.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }
    }

    object People extends People {
      override def postV0AndPersonByTenant(
        tenant: String,
        person: movio.apidoc.generator.reference.v0.models.Person
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[movio.apidoc.generator.reference.v0.models.Person] = {
        val payload = play.api.libs.json.Json.toJson(person)

        _executeRequest("POST", s"/${play.utils.UriEncoding.encodePathSegment(tenant, "UTF-8")}/v0/person/", body = Some(payload)).map {
          case r if r.status == 200 => _root_.movio.apidoc.generator.reference.v0.Client.parseJson("movio.apidoc.generator.reference.v0.models.Person", r, _.validate[movio.apidoc.generator.reference.v0.models.Person])
          case r if r.status == 404 => throw new movio.apidoc.generator.reference.v0.errors.ErrorResponse(r)
          case r => throw new movio.apidoc.generator.reference.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 404")
        }
      }

      override def postV0ByTenant(
        tenant: String,
        people: Seq[movio.apidoc.generator.reference.v0.models.Person]
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Int] = {
        val payload = play.api.libs.json.Json.toJson(people)

        _executeRequest("POST", s"/${play.utils.UriEncoding.encodePathSegment(tenant, "UTF-8")}/v0/people/", body = Some(payload)).map {
          case r if r.status == 200 => _root_.movio.apidoc.generator.reference.v0.Client.parseJson("Int", r, _.validate[Int])
          case r if r.status == 404 => throw new movio.apidoc.generator.reference.v0.errors.ErrorResponse(r)
          case r => throw new movio.apidoc.generator.reference.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 404")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {
      import play.api.Play.current

      val holder = play.api.libs.ws.WS.url(apiUrl + path).withHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).withHeaders(defaultHeaders : _*)
      auth.fold(holder) {
        case Authorization.Basic(username, password) => {
          holder.withAuth(username, password.getOrElse(""), play.api.libs.ws.WSAuthScheme.BASIC)
        }
        case a => sys.error("Invalid authorization scheme[" + a.getClass + "]")
      }
    }

    def _logRequest(method: String, req: play.api.libs.ws.WSRequest)(implicit ec: scala.concurrent.ExecutionContext): play.api.libs.ws.WSRequest = {
      val queryComponents = for {
        (name, values) <- req.queryString
        value <- values
      } yield s"$name=$value"
      val url = s"${req.url}${queryComponents.mkString("?", "&", "")}"
      auth.fold(logger.info(s"curl -X $method $url")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' $url")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Seq.empty,
      body: Option[play.api.libs.json.JsValue] = None
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).withQueryString(queryParameters:_*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).withQueryString(queryParameters:_*).withHeaders("Content-Type" -> "application/json; charset=UTF-8")).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).withQueryString(queryParameters:_*).withHeaders("Content-Type" -> "application/json; charset=UTF-8")).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).withQueryString(queryParameters:_*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).withQueryString(queryParameters:_*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).withQueryString(queryParameters:_*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).withQueryString(queryParameters:_*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).withQueryString(queryParameters:_*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

  }

  object Client {

    def parseJson[T](
      className: String,
      r: play.api.libs.ws.WSResponse,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.body)) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw new movio.apidoc.generator.reference.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization
  object Authorization {
    case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  trait Healthchecks {
    def getInternalAndHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[movio.apidoc.generator.reference.v0.models.Healthcheck]
  }

  trait People {
    def postV0AndPersonByTenant(
      tenant: String,
      person: movio.apidoc.generator.reference.v0.models.Person
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[movio.apidoc.generator.reference.v0.models.Person]

    def postV0ByTenant(
      tenant: String,
      people: Seq[movio.apidoc.generator.reference.v0.models.Person]
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Int]
  }

  package errors {

    import movio.apidoc.generator.reference.v0.models.json._

    case class ErrorResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val error = _root_.movio.apidoc.generator.reference.v0.Client.parseJson("movio.apidoc.generator.reference.v0.models.Error", response, _.validate[movio.apidoc.generator.reference.v0.models.Error])
    }

    case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends Exception(s"HTTP $responseCode: $message")

  }

}