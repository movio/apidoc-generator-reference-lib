/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.0-SNAPSHOT
 * apidoc:0.11.21 http://dockerhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/test_kafka_0_8
 */

import java.util.Properties

import scala.language.postfixOps
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.matching.Regex

import com.typesafe.config.Config

import kafka.consumer._
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder

import play.api.libs.json.Json

import movio.api.kafka_0_8.KafkaConsumer

package movio.apidoc.generator.reference.v0.kafka {
  import movio.apidoc.generator.reference.v0.models._
  import movio.apidoc.generator.reference.v0.models.json._

  object KafkaPersonTopic {
    /**
      The version of the api - apidoc generator enforces this value.
      For use when creating a topic name.
      Example: "v2"
      */
    val apiVersion = "v0"

    /**
      The name of the kafka topic to publish and consume messages from.
      This is a scala statedment/code that that gets executed
      Example: `s"mc-servicename-${apiVersion}-${instance}-${tenant}"`

      @param instance an instance of the topic, eg uat, prod. It's read from the config.
      @param tenant is the customer id, eg vc_regalus
      */
    def topic(instance: String)(tenant: String) = s"mc.data.person.${apiVersion}.${instance}.${tenant}"

    /**
      The regex for the kafka consumer to match topics.

      @param instance an instance of the topic, eg uat, prod. It's read from the config.
      @param tenants the tenants of the topics from which the consumer consumes. If it's empty,
             all tenants are matched.
      */
    def topicRegex(inst: String, tenants: Seq[String]) = {
      val instance = Regex.quote(inst)
      val tenantsPattern = if (tenants.isEmpty) ".*"
                           else tenants.map(Regex.quote(_)).mkString("|")

      s"mc.data.person.${apiVersion}.${instance}.($tenantsPattern)"
    }
  }

  object KafkaPersonConsumer {
    val base = "movio.apidoc.generator.reference.kafka.consumer"
    val KafkaOffsetStorageType = s"$base.offset-storage-type"
    val KafkaOffsetStorageDualCommit = s"$base.offset-storage-dual-commit"
    val ConsumerTimeoutKey = s"$base.timeout.ms"
    val ConsumerZookeeperConnectionKey = s"$base.zookeeper.connection"
    val TopicInstanceKey = s"$base.topic-instance"
    val TenantsKey = s"$base.tenants"
  }

  class KafkaPersonConsumer (
    config: Config,
    consumerGroupId: String,
    tenants: Option[Seq[String]] = None
  ) extends KafkaConsumer[KafkaPerson] {
    import KafkaPersonConsumer._

    lazy val topicRegex: Regex =
      KafkaPersonTopic.topicRegex(
        config.getString(TopicInstanceKey),
        tenants.getOrElse(config.getStringList(TenantsKey))
      ).r

    lazy val topicFilter = new Whitelist(topicRegex.toString)

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

    /**
      * Process a batch of messages with given processor function and commit
      * offsets if it succeeds. Messages with null payloads are ignored.
      *
      * @param processor processor function that takes a map of messages for different tenants
      * @param batchSize the maximum number of messages to process
      */
    def processBatchThenCommit(
      processor: Map[String, Seq[KafkaPerson]] ⇒ scala.util.Try[Map[String, Seq[KafkaPerson]]],
      batchSize: Int = 1
    ): scala.util.Try[Map[String, Seq[KafkaPerson]]] =
      doProcess[KafkaPerson] { message ⇒
        Option(message.message).map(Json.parse(_).as[KafkaPerson])
      }(processor, batchSize)

    /**
      * Process a batch of messages with given processor function and commit
      * offsets if it succeeds.
      *
      * Each message is a tuple of the key and the payload deserialised to
      * `Option[T]` which is `None` when the message has a null payload.
      *
      * @param processor processor function that takes a map of messages for different tenants
      * @param batchSize the maximum number of messages to process
      */
    def processBatchWithKeysThenCommit(
      processor: Map[String, Seq[(String, Option[KafkaPerson])]] ⇒ scala.util.Try[Map[String, Seq[(String, Option[KafkaPerson])]]],
      batchSize: Int = 1
    ): scala.util.Try[Map[String, Seq[(String, Option[KafkaPerson])]]] =
      doProcess[(String,  Option[KafkaPerson])] { message ⇒
        Some(
          message.key → Option(message.message).map(Json.parse(_).as[KafkaPerson])
        )
      }(processor, batchSize)

    def doProcess[T](
      converter: MessageAndMetadata[String, String] ⇒ Option[T]
    )(
      processor: Map[String, Seq[T]] ⇒ scala.util.Try[Map[String, Seq[T]]],
      batchSize: Int = 1
    ): scala.util.Try[Map[String, Seq[T]]] = {
      @tailrec
      def fetchBatch(remainingInBatch: Int, messages: Map[String, Seq[T]]): scala.util.Try[Map[String, Seq[T]]] ={
        if (remainingInBatch == 0) {
          scala.util.Success(messages)
        } else {
          // FIXME test
          scala.util.Try {
            iterator.next()
          } match {
            case scala.util.Success(message) ⇒
              val newMessages = converter(message) map { entity ⇒
                val topicRegex(tenant) = message.topic
                val newSeq = messages.get(tenant).getOrElse(Seq.empty) :+ entity

                messages + (tenant → newSeq)
              } getOrElse messages

              fetchBatch(remainingInBatch - 1, newMessages)

            case scala.util.Failure(ex) ⇒
              ex match {
                case ex: ConsumerTimeoutException ⇒
                  // Consumer timed out waiting for a message. Ending batch.
                  scala.util.Success(messages)
                case ex ⇒
                  scala.util.Failure(ex)
              }
          }
        }
      }

      fetchBatch(batchSize, Map.empty) match {
        case scala.util.Success(messages) ⇒
          processor(messages) map { allMessages ⇒
            consumer.commitOffsets(true)
            allMessages
          }

        case scala.util.Failure(ex) ⇒
          scala.util.Failure(ex)
      }
    }

    def shutdown() = consumer.shutdown()
  }
}

