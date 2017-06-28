/**
  * Created by kunalkrishna on 4/12/17.
  */

import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.mllib.clustering.KMeans

import org.apache.spark.mllib.linalg.Vectors

object KmeansQ1 {

  // Load and parse the data
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Q1").setMaster("local")

    val sc = new SparkContext(conf)

    val data = sc.textFile("itemusermat")

    // Cluster the data into two classes using KMeans
    val parsedData = data.map(s => Vectors.dense(s.split(' ').drop(1).map(_.toDouble)))


// number of cluster is 10 and number of iteration is 20
    val numClusters = 10
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    val prediction = data.map{ line =>	val parts = line.split(' ')
      (parts(0),clusters.predict(Vectors.dense(parts.tail.map(_.toDouble))))
    }

    val movie = sc.textFile("movies.dat")
    val  moviesData = movie.map{ line=>val parts = line.split("::")
      (parts(0),(parts(1) + " , " +parts(2)))}

    val joinedData = prediction.join(moviesData)
    val shuffledData= joinedData.map(p=>(p._2._1,(p._1,p._2._2)))

    val gData = shuffledData.groupByKey()

    val ans = gData.map(p=>(p._1,p._2.toList))
    val finalAnswer = ans.map(p=>(p._1,p._2.take(5)))
    finalAnswer.collect()
    finalAnswer.foreach(p=>println("Cluster id - "+ p._1+" --> "+p._2.mkString(":::")))

  }

}
