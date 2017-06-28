/**
  * Created by kunalkrishna on 4/16/17.
  */
import java.util.HashMap

import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord }
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter._
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.storage.StorageLevel
import Sentiment.Sentiment
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import scala.collection.convert.wrapAll._


object backup {

  val conf = new SparkConf().setMaster("local[6]").setAppName("Spark Streaming - Kafka Producer - PopularHashTags").set("spark.executor.memory", "1g")
  val sc = new SparkContext(conf)

  val props = new Properties()
  props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)


  def main(args: Array[String]) {

    sc.setLogLevel("WARN")

    // Create an array of arguments: zookeeper hostname/ip,consumer group, topicname, num of threads
    val Array(zkQuorum, group, topics, numThreads) = args

    // Set the Spark StreamingContext to create a DStream for every 2 seconds
    val ssc = new StreamingContext(sc, Seconds(2))
    ssc.checkpoint("checkpoint")

    // Map each topic to a thread
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    // Map value from the kafka message (k, v) pair
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    // Filter hashtags


    var result= lines.map(x=> mainSentiment(x))

    result.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def mainSentiment(input: String): Sentiment = Option(input) match {
    case Some(text) if !text.isEmpty => extractSentiment(text)
    case _ => throw new IllegalArgumentException("input can't be null or empty")
  }

  private def extractSentiment(text: String): Sentiment = {
    val (_, sentiment) = extractSentiments(text)
      .maxBy { case (sentence, _) => sentence.length }
    sentiment
  }

  def extractSentiments(text: String): List[(String, Sentiment)] = {
    val annotation: Annotation = pipeline.process(text)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    sentences.map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map { case (sentence, tree) => (sentence.toString,Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree))) }
      .toList
  }


}
