import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
  * Created by kunalkrishna on 4/15/17.
  */
object Question2_NB {
  def main(args: Array[String]){

    val conf = new SparkConf().setAppName("Q2NB").setMaster("local")

    val sc = new SparkContext(conf)

    val glassData = sc.textFile("glass.data")
    val parsedData = glassData.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(10).toDouble, Vectors.dense(parts(0).toDouble,parts(1).toDouble,parts(2).toDouble,parts(3).toDouble,parts(4).toDouble,parts(5).toDouble,parts(6).toDouble,parts(7).toDouble,parts(8).toDouble,parts(9).toDouble))
    }

    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val trainingData = splits(0)
    val test = splits(1)

    val model = NaiveBayes.train(trainingData, lambda = 1.0)

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    println("Accuracy for Naive Bayes Algorithm is : " + (accuracy*100)+"%")


  }
}
