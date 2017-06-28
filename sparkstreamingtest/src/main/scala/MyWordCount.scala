/**
  * Created by kunalkrishna on 4/10/17.
  */
  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
  import org.apache.spark.SparkConf
  import org.apache.spark.streaming._
  import org.apache.spark.streaming.kafka.KafkaUtils



  object MyWordCount {

    def main(args: Array[String]) {
      val (zkQuorum, group, topics, numThreads) = ("localhost", "localhost", "test", "20")
      val sparkConf = new SparkConf().setMaster("local[*]").setSparkHome("/usr/local/spark").setAppName("MyWordCount")
      val ssc = new StreamingContext(sparkConf, Seconds(2))
      ssc.checkpoint("checkpoint")

      //val topics = "test"
      val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
      val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
      val words = lines.flatMap(_.split(" "))
      val wordCounts = words.map(x => (x.toLowerCase, 1L))
        .reduceByKey(_ + _)

      wordCounts.print()

      ssc.start()
      ssc.awaitTermination()
    }
  }

