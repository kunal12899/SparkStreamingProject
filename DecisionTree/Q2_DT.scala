/**
  * Created by kunalkrishna on 4/16/17.
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.configuration.Algo._
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.mllib.util.MLUtils

object Q2_DT {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Q1").setMaster("local")

    val sc = new SparkContext(conf)

    val glassData = sc.textFile("glass.data")
    val parsedData = glassData.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(10).toDouble, Vectors.dense(parts(0).toDouble, parts(1).toDouble, parts(2).toDouble, parts(3).toDouble, parts(4).toDouble, parts(5).toDouble, parts(6).toDouble, parts(7).toDouble, parts(8).toDouble, parts(9).toDouble))
    }

    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0)
    val test = splits(1)


    val numOfClasses = 8
    val categoricalFeatures = Map[Int, Int]()
    val impurity = "gini"
    val mDepth = 5
    val mBins = 32

    val model = DecisionTree.trainClassifier(training, numOfClasses, categoricalFeatures,
      impurity, mDepth, mBins)

    val labelAndPreds = test.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val accuracy = 1.0 * labelAndPreds.filter(r => r._1 == r._2).count.toDouble / test.count

    println("Accuracy for Decision Tree Algorithm is : " + (accuracy * 100) + "%")

  }
}