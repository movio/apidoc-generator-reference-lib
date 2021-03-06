 /**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.0-SNAPSHOT
 * apidoc:0.11.21 http://dockerhost:9000/movio/apidoc-generator-reference/0.1.0-SNAPSHOT/test_kafka_0_8_tests
 */

package movio.apidoc.generator.reference.v0.kafka

import org.joda.time.LocalDateTime
import org.mockito.Matchers.any
import org.mockito.Matchers.{ eq ⇒ is }

import com.typesafe.config.ConfigFactory

import movio.testtools.MovioSpec
import movio.testtools.kafka.{ KafkaTestKit, KafkaTestKitUtils }


class KafkaPersonTests extends MovioSpec with KafkaTestKit {
  import movio.apidoc.generator.reference.v0.models._

  val kafkaServer = createKafkaServer()
  kafkaServer.startup()

  override def afterAll() = {
    kafkaServer.shutdown()
    zkServer.stop()
  }

  describe("KafkaPerson Producer and Consumer") {
    it("should timeout with no messages") {
      new Fixture {
        awaitCondition("Message should get processed") {
          def processor(messages: Map[String, Seq[KafkaPerson]]): scala.util.Try[Map[String, Seq[KafkaPerson]]] =  scala.util.Success(messages)
          consumer.processBatchThenCommit(processor) shouldBe scala.util.Success(Map.empty)
        }

        consumer.shutdown
      }
    }

    it("should send and receive a message") {
      new Fixture {
        // Produce test message
        producer.sendWrapped(entity1, tenant).get

        // And consume it
        awaitCondition("Message should get processed") {
          def processor(messages: Map[String, Seq[KafkaPerson]]): scala.util.Try[Map[String, Seq[KafkaPerson]]] = {
            println(messages)
            println("do some side effecting stuff here")
            scala.util.Success(messages)
          }
          consumer.processBatchThenCommit(processor).get(tenant) shouldBe Seq(entity1)
        }

        consumer.shutdown
      }
    }

    it("should send and receive a batch of messages") {
      new Fixture {
        val entities = Seq(entity1, entity2)

        // Produce test message
        producer.sendWrapped(entities, tenant).get

        // And consume it
        awaitCondition("Message should get processed") {
          def processor(messages: Map[String, Seq[KafkaPerson]]): scala.util.Try[Map[String, Seq[KafkaPerson]]] =  {
            println(messages)
            println("do some side effecting stuff here")
            scala.util.Success(messages)
          }
          // Use distinct because there are items in the queue from other tests
          consumer.processBatchThenCommit(processor, 100).get(tenant) shouldBe entities
        }

        consumer.shutdown
      }
    }

    it("messages keys should be available to the processor") {
      new Fixture {
        val entities = Seq(entity1, entity2)

        // Produce test message
        producer.sendWrapped(entities, tenant).get

        // And consume it
        awaitCondition("Message should get processed") {
          def processor(messages: Map[String, Seq[(String, Option[KafkaPerson])]]) = {
            println(messages)
            println("do some side effecting stuff here")
            scala.util.Success(messages)
          }

          // Use distinct because there are items in the queue from other tests
          consumer.processBatchWithKeysThenCommit(processor, 100).get(tenant) shouldBe Seq(
            key1 → Some(entity1),
            key2 → Some(entity2)
          )
        }

        consumer.shutdown
      }
    }

    it("consumer ignores null payload messages, to support deletes on topics with compaction") {
      new Fixture {
        val topic = KafkaPersonTopic.topic(topicInstance)(tenant)
        val rawProducer = createKeyedProducer[String, String](topic, kafkaServer)(k ⇒ k, m ⇒ m)

        producer.sendWrapped(entity1, tenant).get
        // Produce null payload message. Need to use the raw producer because the generated producer would
        // throw an exception when trying to convert a null entity to JSON.
        rawProducer.send("anId", null)
        producer.sendWrapped(entity2, tenant).get

        // And consume them
        var consumedEntities = Seq.empty[KafkaPerson]
        awaitCondition("All messages should get processed") {
          def processor(messages: Map[String, Seq[KafkaPerson]]): scala.util.Try[Map[String, Seq[KafkaPerson]]] =  {
            println(messages)
            println("do some side effecting stuff here")
            scala.util.Success(messages)
          }

          // Use distinct because there are items in the queue from other tests
          consumer.processBatchThenCommit(processor, 100).get.get(tenant).foreach { messages =>
            consumedEntities ++= messages
          }

          consumedEntities shouldBe Seq(entity1, entity2)
        }

        consumer.shutdown
      }
    }
  }

  trait Fixture {

    val brokerConnectionString = kafkaServer.config.hostName + ":" + kafkaServer.config.port
    val topicInstance = "test"
    val tenant = KafkaTestKitUtils.tempTopic()

    val testConfig = ConfigFactory.parseString(s"""
      |configuration {
      |  log-on-startup = false
      |}
      |
      |movio.apidoc.generator.reference.kafka {
      |  producer {
      |    broker-connection-string : "$brokerConnectionString"
      |    topic-instance = "$topicInstance"
      |  }
      |}
      |
      |movio.apidoc.generator.reference.kafka {
      |  consumer {
      |    topic-instance = "$topicInstance"
      |    tenants = ["ignore_me", "$tenant"]
      |    offset-storage-type = "kafka"
      |    offset-storage-dual-commit = false
      |    timeout.ms = "100"
      |    zookeeper.connection = "${zkServer.getConnectString}"
      |  }
      |}
      |""".stripMargin)
      .withFallback(ConfigFactory.load())

    val producer = new KafkaPersonProducer(testConfig)
    val consumer = new KafkaPersonConsumer(testConfig, new java.util.Random().nextInt.toString)
    val entity1 = 
    KafkaPerson (
      v0 = 
        Person (
          id = "id1",
          name = "name1",
          lastActiveTime = None,
          dob = None,
          addresses = List.empty,
          gender = Gender.Male
        ),
      utcGeneratedTime = org.joda.time.LocalDateTime.now(org.joda.time.DateTimeZone.UTC)
    )
    val key1 = entity1.generateKey(tenant)

    val entity2 = 
    KafkaPerson (
      v0 = 
        Person (
          id = "id2",
          name = "name2",
          lastActiveTime = None,
          dob = None,
          addresses = List.empty,
          gender = Gender.Male
        ),
      utcGeneratedTime = org.joda.time.LocalDateTime.now(org.joda.time.DateTimeZone.UTC)
    )
    val key2 = entity2.generateKey(tenant)
  }

}
