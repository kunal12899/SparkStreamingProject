/**
 * Created by kunalkrishna on 4/11/17.
 */

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import twitter4j.*;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import kafka.producer.KeyedMessage;

public class KafkaProducerTweet {
    public static void main(String[] args) throws Exception {
        final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);


        String consumerKey = "vNu3DgMteZJewvx9RpT9U7XKJ";
        String consumerSecret = "yh4bJIEfzWB2fd7y3Zolatg9X7rEL08fLBW049vkvzisltqwg8";
        String accessToken = "86056153-R8j8RPgMZSKFrfBdWGJIgBgDCyMPdvfSY2TebxJSU";
        String accessTokenSecret = "vI0az6wdtCh0Lur3Pzn1mSM1a5mVpSLZrE9C0Y72bctgp";
        String topicName = "tweets";
        String[] arguments = args.clone();
        //String[] keyWords = Arrays.copyOfRange(arguments, 5, arguments.length);
        String[] keyWords = {"#Obama", "#trump"};

        // Set twitter oAuth tokens in the configuration
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

        // Create twitterstream using the configuration
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                queue.offer(status);

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);

        // Filter keywords
        FilterQuery query = new FilterQuery().track(keyWords);
        twitterStream.filter(query);

        // Thread.sleep(5000);

        // Add Kafka producer config settings
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        // poll for new tweets in the queue. If new tweets are added, send them
        // to the topic
        while (true) {
            Status ret = queue.poll();

            if (ret == null) {
                Thread.sleep(10);
            } else {
                System.out.println("Tweet text: " + ret.getText() + " latlng " + ret.getUser().getLocation());

                producer.send(new ProducerRecord<String, String>(topicName, ret.getText() + "::" + ret.getUser().getLocation()));
                //producer.send(new ProducerRecord<String, String>(topicName,hashtage.getText().toString(), ret.getText().toString()));
            }
        }
    }

}
