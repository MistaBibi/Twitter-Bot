package main;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class TwitterBot {
    private Properties properties;
    private InputStream input;
    private ConfigurationBuilder cb;
    private TwitterFactory tf;
    private Twitter twitter;

    TwitterBot() {
        properties = new Properties();
        try {
            input = new FileInputStream("util/twitter.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
                .setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
                .setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
                .setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
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

    public static void main(String[] args) {
        TwitterBot tb = new TwitterBot();
        tb.retweetUser("realDonaldTrump");
    }
}
