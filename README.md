# SparkStreamingProject
Real Time sentiment Analysis using SparkStreaming, Spark-mlib, NLTK, Kafka, ElasticSearch, Kibana


CS6350: BIG DATA ANALYTICS and MANAGEMENT
Spring 2017

Related to: Data Analytics and Stream Framework using Spark

This consists of two parts. Here, we focus on K-means clustering (data analytics), classification, recommendation (collaborative filtering) and steam framework with Spark, Kafka, Elasticsearch, and Kibana.
Part A:
Q1
Using spark machine learning library spark-mlib, use kmeans to cluster the movies using the ratings given by the user, that is, use the item-user matrix from x	 File provided as input to your program.
Dataset description:
Dataset:  Itemusermat File.
The itemusermat file contains the ratings given to each movie by the users in Matrix format. The file contains the ratings by users for 1000 movies.
Each line contains the movies id and the list of ratings given by the users. 
A rating of 0 is used for entries where the user did not rate a movie.
From the sample below, user1 did not rate movie 2, so we use a rating of 0.
A sample Itemusermat file with the item-user matrix is shown below.

user1
user2
movie1
4
3
movies2
0
2


Set the number of clusters (k) to 10
Your Scala/python code should produce the following output:

•	For each cluster, print any 5 movies in the cluster. Your output should contain the movie_id, movie title, genre and the corresponding cluster it belongs to. Note: Use the movies.dat file to obtain the movie title and genre.

          For example
          cluster: 1
         123,Star wars, sci-fi 



Q2 Classification
Using spark MLlib, use the  supervised learning (decision tree and Naive Bayes) algorithms to classify types of glass based on the dataset “glass.data”
The dataset comprises of the following attributes.
Attribute Information:
   1. Id number: 1 to 214
   2. RI: refractive index
   3. Na: Sodium (unit measurement: weight percent in corresponding oxide, as 
                  are attributes 4-10)
   4. Mg: Magnesium
   5. Al: Aluminum
   6. Si: Silicon
   7. K: Potassium
   8. Ca: Calcium
   9. Ba: Barium
  10. Fe: Iron
  11. Type of glass: (class attribute)
      -- 1 building_windows_float_processed
      -- 2 building_windows_non_float_processed
      -- 3 vehicle_windows_float_processed
      -- 4 vehicle_windows_non_float_processed (none in this database)
      -- 5 containers
      -- 6 tableware
      -- 7 headlamps

Please use 60% of the data for training and 40% for testing and give the accuracy of the classifiers.

Q3. Use Collaborative filtering find the accuracy of ALS model accuracy. Use ratings.dat file. It contains 
        User id ::  movie id :: ratings :: timestamp.  Your program should report the accuracy of the model.
       For details follow the link:    http://spark.apache.org/docs/latest/mllib-collaborative-filtering.html  
Please use 60% of the data for training and 40% for testing and report the accuracy of the model.










Part B: Spark Streaming framework with Kafka
Q4. You are required to implement the following framework using Apache Spark Streaming, Kafka, Elasticsearch and Kibana. The framework performs SENTIMENT analysis of particular hash tags in tweetter data in real-time. For example, we want to do the sentiment analysis for all the tweets for #trump, #obama and show them (e.g., positive, neutral, negative, etc. tweets) on a map. When we show tweets on a map, we plot them using their latitude and longitude.  







Figure: Sentiment analysis framework

The above framework has the following components:

1.	Scrapper:
The scrapper will collect all tweets and sends them to Kafka for analytics. Please DONOT USE SPARK STREAMING TWEETTER API for collecting tweets. The scraper will be a standalone program written in JAVA/PYTHON and should perform the followings:
a.	Collecting tweets in real-time with particular hash tags. For example, we will collect all tweets with #trum, #obama.
b.	After getting tweets we will filter them based on their latitude and longitude. If any tweet does not have latitude and longitude, we will discard them.
c.	After filtering, we will send them (tweets with lat/lng) to Kafka.
d.	You should use Kafka API (producer) in your program (https://kafka.apache.org/090/documentation.html#producerapi)
e.	You scrapper program will run infinitely and should take hash tag as input parameter while running.
2.	Kafka
You need to install Kafka and run Kafka Server with Zookeeper. You should create a dedicated channel/topic for data transport.
3.	Sentiment Analyzer
Sentiment Analysis is the process of determining whether a piece of writing is positive, negative or neutral. It's also known as opinion mining, deriving the opinion or attitude of a speaker.
For example,
“President Donald Trump approaches his first big test this week from a position of unusual weakness.”  - has positive sentiment.
“Trump has the lowest standing in public opinion of any new president in modern history.” - has neutral sentiment.
“Trump has displayed little interest in the policy itself, casting it as a thankless chore to be done before getting to tax-cut legislation he values more.” - has negative sentiment.

The above examples are taken from CNBC news:
http://www.cnbc.com/2017/03/22/trumps-first-big-test-comes-as-hes-in-an-unusual-position-of-weakness.html

You can use any third party sentiment analyzer like Stanford CoreNLP (java/scala), nltk(python) for sentiment analyzing. For example, you can add Stanford CoreNLP as an external library using SBT/Maven  in your scala/java project. In python you can import nltk by installing it using pip.

Sentiment analysis using Spark Streaming:
In Spark Streaming, create a Kafka consumer (shown in the class for streaming word count) and periodically collect filtered tweets from scrapper. For each has tag, perform sentiment analysis using Sentiment Analyzing tool (discussed above). Then for each hash tag, send the output to Elasticsearch for visualization.



4.	Visualizer
Install Elasticsearch and Kibana. Create an index for visualization. Create a map to show all kinds of tweets. After that, create a dashboard to show the map. Dashboard requires data to be time stamped. So, when you send data from Spark to Elasticsearch add a time stamp. In the dashboard set a Map refresh time to 2 min as an example.
(OPTIONAL) – Bonus 5-point s will be given if you show tweets for different categories. For example, if we have two hash tags, #Obama and #trump, we will use different color icons for showing them in map. 
You can use small circle with different colors for showing tweets.
ELK tutorial:
https://www.oreilly.com/learning/a-guide-to-elasticsearch-5-and-the-elkelastic-stack
https://www.digitalocean.com/community/tutorials/how-to-use-kibana-dashboards-and-visualizations
 


Clustering BFR algorithm:





