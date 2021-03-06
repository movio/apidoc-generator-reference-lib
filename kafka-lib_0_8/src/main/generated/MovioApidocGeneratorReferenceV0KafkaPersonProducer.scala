 /**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.0-SNAPSHOT
 * apidoc:0.11.21 http://dockerhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/test_kafka_0_8
 */

import kafka.producer._
import kafka.serializer.StringEncoder

import play.api.libs.json.Json
import play.api.libs.json.Writes

import java.util.Properties
import com.typesafe.config.Config

import movio.api.kafka_0_8.KafkaProducer
import movio.api.kafka_0_8.KafkaProducerException
import movio.core.utils.TryHelpers.TryOps

package movio.apidoc.generator.reference.v0.kafka {
  import movio.apidoc.generator.reference.v0.models._
  import movio.apidoc.generator.reference.v0.models.json._

  object KafkaPersonProducer {
    val base = "movio.apidoc.generator.reference.kafka.producer"
    val BrokerListKey = s"$base.broker-connection-string"
    val TopicInstanceKey = s"$base.topic-instance"
  }

  class KafkaPersonProducer(
    config: Config
  ) extends KafkaProducer[KafkaPerson, Person] {
    import KafkaPersonProducer._

    lazy val topicResolver = KafkaPersonTopic.topic(config.getString(TopicInstanceKey))(_)

    lazy val producerConfig = new ProducerConfig(readProducerPropertiesFromConfig(config))
    lazy val producer = new Producer[String, String](producerConfig)


    def readProducerPropertiesFromConfig(config: Config) = {
      val properties = new Properties
      properties.put("producer.type", "sync")
      properties.put("metadata.broker.list", config.getString(BrokerListKey))
      properties.put("request.required.acks", "-1")
      properties.put("serializer.class", classOf[StringEncoder].getName)
      properties
    }

    def send(single: Person, tenant: String): scala.util.Try[Person] = {
      send(Seq(single), tenant).map(_.head)
    }

    def sendWrapped(single: KafkaPerson, tenant: String): scala.util.Try[KafkaPerson] = {
      sendWrapped(Seq(single), tenant).map(_.head)
    }

    def send(batch: Seq[Person], tenant: String): scala.util.Try[Seq[Person]] = {
      val topic = topicResolver(tenant)
      val messages = batch.map(KafkaPerson(_))
      scala.util.Try {
        producer.send(messages map { message =>
                        new KeyedMessage[String, String](topic, message.generateKey(tenant), Json.stringify(Json.toJson(message)))
                      }: _*)
        batch
      } recoverWith {
        case ex => scala.util.Failure(new KafkaProducerException(s"Failed to publish $topic message, to kafka queue.", ex))
      }
    }

    def sendWrapped(batch: Seq[KafkaPerson], tenant: String): scala.util.Try[Seq[KafkaPerson]] = {
      val topic = topicResolver(tenant)
      scala.util.Try {
        producer.send(batch map { message =>
                        new KeyedMessage[String, String](topic, message.generateKey(tenant), Json.stringify(Json.toJson(message)))
                      }: _*)
        batch
      } recoverWith {
        case ex => scala.util.Failure(new KafkaProducerException(s"Failed to publish $topic message, to kafka queue.", ex))
      }
    }

    def shutdown() = producer.close()
  }

}
