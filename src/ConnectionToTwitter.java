import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ConnectionToTwitter{

public static String search(String searchQuery) {
	String searchResults = "";
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
          .setOAuthConsumerKey("")
          .setOAuthConsumerSecret("")
          .setOAuthAccessToken("")
          .setOAuthAccessTokenSecret("");
    TwitterFactory tf = new TwitterFactory(cb.build());
    Twitter twitter = tf.getInstance();
    List<String> tweetList = new ArrayList<String>();
        try {
            Query query = new Query(searchQuery);
            QueryResult result;
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            Status recentTweet = tweets.get(tweets.size() - 1);
            for (Status tweet : tweets) {
            	// These are just some examples of what can be done:
            	
                System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                System.out.println(tweet.getCreatedAt());
                System.out.println("This is the tweet ID");
                System.out.println(tweet.getId());
                //twitter.updateStatus("@" + tweet.getUser().getScreenName() + " is my developer.");

            	tweetList.add(tweet.getText());

            }

            

            //System.exit(-1);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            //System.exit(-1);
        }
        return tweetList.get(0);
}
}