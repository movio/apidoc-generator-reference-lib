/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.0-SNAPSHOT
 * apidoc:0.9.48 http://localhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/kafka_0_8
 */

import java.util.Properties

import scala.language.postfixOps
import scala.util.{ Try, Success, Failure }
import scala.annotation.tailrec

import com.typesafe.config.Config

import kafka.consumer._
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder

import play.api.libs.json.Json

import movio.api.kafka_0_8.KafkaConsumer

package movio.apidoc.generator.reference.v0.kafka {
  import movio.apidoc.generator.reference.v0.models._
  import movio.apidoc.generator.reference.v0.models.json._

  object KafkaMovieTopic {
    /**
      The version of the api - apidoc generator enforces this value.
      For use when creating a topic name.
      Example: "v2"
      */
    val apiVersion = "v0"

    /**
      The name of the kafka topic to publish and consume messages from.
      This is a scala statedment/code that that gets executed
      Example: `s"mc-servicename-${apiVersion}-${tenant}"` 

      @param tenant is the customer id, eg vc_regalus
      */
    def topic(tenant: String) = s"mc-person-master-${tenant}"

    val topicRegex = s"mc-person-master-" + "(.*)"
  }

  object KafkaMovieConsumer {
    val base = "movio.apidoc.generator.reference.kafka.consumer"
    val KafkaOffsetStorageType = s"$base.offset-storage-type"
    val KafkaOffsetStorageDualCommit = s"$base.offset-storage-dual-commit"
    val ConsumerTimeoutKey = s"$base.timeout.ms"
    val ConsumerZookeeperConnectionKey = s"$base.zookeeper.connection"
  }

  class KafkaMovieConsumer (
    config: Config,
    consumerGroupId: String
  ) extends KafkaConsumer[KafkaMovie] {
    import KafkaMovieConsumer._

    val topicFilter = new Whitelist(KafkaMovieTopic.topicRegex)

    lazy val consumerConfig = new ConsumerConfig(readConsumerPropertiesFromConfig)
    lazy val consumer = Consumer.create(consumerConfig)

    lazy val stream: KafkaStream[String, String] =
      consumer.createMessageStreamsByFilter(topicFilter, 1, new StringDecoder, new StringDecoder).head

    lazy val iterator = stream.iterator()

    def readConsumerPropertiesFromConfig = {
      val properties = new Properties

      properties.put("group.id", consumerGroupId)
      properties.put("zookeeper.connect", config.getString(ConsumerZookeeperConnectionKey))
      properties.put("auto.offset.reset", "smallest")
      properties.put("consumer.timeout.ms", config.getString(ConsumerTimeoutKey))
      properties.put("consumer.timeout", config.getString(ConsumerTimeoutKey))
      properties.put("auto.commit.enable", "false")

      properties.put("offsets.storage", config.getString(KafkaOffsetStorageType))
      properties.put("dual.commit.enabled", config.getString(KafkaOffsetStorageDualCommit))

      properties
    }

    def processBatchThenCommit(
      processor: Map[String, Seq[KafkaMovie]] ⇒ Try[Map[String, Seq[KafkaMovie]]],
      batchSize: Int = 1
    ): Try[Map[String, Seq[KafkaMovie]]] = {
      @tailrec
      def fetchBatch(remainingInBatch: Int, messages: Map[String, Seq[KafkaMovie]]): Try[Map[String, Seq[KafkaMovie]]] ={
        if (remainingInBatch == 0) {
          Success(messages)
        } else {
          // FIXME test
          Try {
            iterator.next()
          } match {
            case Success(message) =>
              val entity = Json.parse(message.message).as[KafkaMovie]
              val KafkaMovieTopic.topicRegex.r(tenant) = message.topic

              val newSeq = messages.get(tenant).getOrElse(Seq.empty) :+ entity
              val newMessages = messages + (tenant -> newSeq)

              fetchBatch(remainingInBatch - 1, newMessages)
            case Failure(ex) => ex match {
              case ex: ConsumerTimeoutException ⇒
                // Consumer timed out waiting for a message. Ending batch.
                Success(messages)
              case ex =>
                Failure(ex)
            }
          }
        }
      }

      fetchBatch(batchSize, Map.empty) match {
        case Success(messages) =>
          processor(messages) map { allMessages =>
            consumer.commitOffsets(true)
            allMessages
          }
        case Failure(ex) => Failure(ex)
      }
    }

    def shutdown = { consumer.shutdown }
  }
}
