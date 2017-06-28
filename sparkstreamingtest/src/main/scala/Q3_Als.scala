/**
  * Created by kunalkrishna on 4/16/17.
  */
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

object Q3_Als {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Q3ALS").setMaster("local")

    val sc = new SparkContext(conf)

    val data = sc.textFile("ratings.dat")
    val ratings = data.map(_.split("::") match { case Array(user, item, rate, timestamp) => Rating(user.toInt, item.toInt, rate.toInt) })

    val rank = 10
    val numIterations = 10

    val splits = ratings.randomSplit(Array(0.6, 0.4), seed = 11L)
    val trainingData = splits(0)
    val test = splits(1)

    val model = ALS.train(trainingData, rank, numIterations, 0.01)


    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }

    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }

    val ratesAndPreds = test.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)

    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()

    println("MSE= " + MSE)
    println("Accuracy: " + (MSE * 100) + "%")

  }
}