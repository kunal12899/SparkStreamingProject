name := "sparkstreamingtest"

version := "1.0"

scalaVersion := "2.10.5"



libraryDependencies ++= Seq("org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" %% "spark-mllib" % "1.6.1",
  "org.apache.spark" % "spark-streaming_2.10" % "1.6.1",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1",
  "org.apache.spark" % "spark-streaming-twitter_2.11" % "1.5.2",
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  "org.elasticsearch" % "elasticsearch-spark_2.10" % "2.2.0-m1" % "compile",
  "com.google.code.gson" % "gson" % "2.2.4",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")))
    