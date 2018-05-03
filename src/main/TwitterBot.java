package main;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TwitterBot {
    private Configuration configuration;
    private TwitterFactory tf;
    private Twitter twitter;

    public TwitterBot() {
        configuration = createConfiguration();
        tf = new TwitterFactory(configuration);
        twitter = tf.getInstance();
    }

    private Configuration createConfiguration() {
        Properties properties = new Properties();
        ConfigurationBuilder cb;
        try {
            properties.load(new FileInputStream("util/twitter.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
                .setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
                .setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
                .setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        return cb.build();
    }

    public void setStatus(String latestStatus) {
        Status status = null;
        try {
            status = twitter.updateStatus(latestStatus);
            System.out.println("Successfully updated the status to [" + status.getText() + "].");
        } catch (TwitterException e) {
            System.out.println("Error has occurred when trying to update status\n");
            e.printStackTrace();
        } finally {
            System.out.println("Status update terminated.");
        }
    }

    public void retweetUser(String user) {
        try {
            List<Status> statuses = twitter.getUserTimeline(user);
            System.out.println("Showing @" + user + "'s user timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                setStatus(status.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }

    public void streamListener() {
        TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
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
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
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
        twitterStream.sample();
    }

    public static void main(String[] args) {
        TwitterBot tb = new TwitterBot();
//        tb.retweetUser("realDonaldTrump");
        tb.streamListener();
    }
}