/**
  * Created by kunalkrishna on 4/11/17.
  */
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.{ SparkContext, SparkConf }


import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import scala.collection.convert.wrapAll._
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark
import Sentiment.Sentiment


object SentimentAnalysis {

  val conf = new SparkConf().setMaster("local[*]").setAppName("SentimentAnalysis").set("spark.executor.memory", "1g")
  //  val conf = new SparkConf().setMaster("local[2]").setAppName("SentimentAnalysis")

//  System.setProperty("hadoop.home.dir", "D:\\spark");
//  System.setProperty("hadoop.home.dir", "/")
  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(10))

  //Related to sentiment
  val props = new Properties()
  props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)


  def main(args: Array[String]) {

    conf.set("es.resource","twitter2/tweet")
    //    sc.setLogLevel("WARN")
    conf.set("es.index.auto.create", "true")
    conf.set("es.nodes","localhost")
    conf.set("es.port","9200")
    conf.set("es.nodes.client.only","true")


    //setting related to twitter sentiment analysis

    // Create an array of arguments: zookeeper hostname/ip,consumer group, topicname, num of threads
    //val Array(zkQuorum, group, topics, numThreads) = Array("localhost", "localhost","tweets", "4")
    val (zkQuorum, group, topics, numThreads) = ("localhost:2181", "spark-streaming-consumer-group", "tweets", "4")


    // Set the Spark StreamingContext to create a DStream for every 2 seconds
    ssc.checkpoint("checkpoint")

    // Map each topic to a thread
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    // Map value from the kafka message (k, v) pair
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)
    //    val lines = KafkaUtils.createStream(ssc, zkQuorum,topics)


        lines.foreachRDD{(rdd, time) =>
          rdd.map(l => {
            Map(
              "Sentiment"  -> mainSentiment(l._2.split("::")(0)).toString(),
              //"GeoLocation"-> l._1,
              "content" -> l._2.split("::")(0).toString()

            )
          }).saveToEs("twitter2/tweet")
        }

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
    sentences
      .map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map { case (sentence, tree) => (sentence.toString,Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree))) }
      .toList
  }


}

