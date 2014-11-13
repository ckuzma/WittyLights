/*
 * WittyLights - by Chris Kuzma
 * 
 * This is a proof-of-concept tweetbot that rides atop the Wit API. (For more info go to
 * http://www.wit.ai) It simulates a series of 8 lights, each of which can be individually
 * turned on or off using natural language. Twitter is the means of interfacing with the
 * bot, controlling its virtual lights.
 * 
 * You will need to modify the access keys defined in the [Credentials.java] file in order
 * to run this yourself. That requires both developer keys from Twitter in addition to
 * a server access token from Wit.
 * 
 * As of 11:27p on November 12th, 2014, this is using the latest version of Wit's open
 * API (version datestamp 20141112).
 * 
 * For debugging purposes, there is an initial variable that can be set to 1 to prevent
 * posting any tweets while modifying and testing the code.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class WittyLights{
	public static void main(String[] args) throws Exception{
		// Debug mode = 1, Normal mode = 0
		int debug = 0;
		
		// Make some objects
		Credentials credentials = new Credentials(); // To prevent me from sharing my own keys...
		WitQuery wit = new WitQuery();
		WitResponse processor = new WitResponse();
		
		// Title screen
    	System.out.print("\nWittyLights v2.0\n---by Chris Kuzma\n");

		
		// Twitter authentication garbage
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true);
	    cb.setOAuthConsumerKey(credentials.twitterConsumerKey);
	    cb.setOAuthConsumerSecret(credentials.twitterConsumerSecret);
	    cb.setOAuthAccessToken(credentials.twitterAccessToken);
	    cb.setOAuthAccessTokenSecret(credentials.twitterAccessSecret);
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	    // End twitter authentication garbage
	    
	    int x = 0;
	    long oldTweetId = 0;
	    while (x < 1) {
			try {
				// Find most recent tweet
				Query query = new Query("@WittyDevices"); //Change this to the Twitter handle of your Twitterbot
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				Status recentTweet = tweets.get(0);
				String userName = recentTweet.getUser().getScreenName();
				String tweetText = recentTweet.getText();
				long newTweetId = recentTweet.getId();
				// End most recent tweet search
				
				// Check for a unique ID, query Wit if unique from last seen tweet
				if (newTweetId != oldTweetId){
					System.out.println("USER: @" + userName + " TWEET: " + tweetText);
					System.out.println("Sending data to Wit...");
					try {
						String dataFromWit = wit.parseQuery(tweetText);
						String virtualBoard = processor.updateVirtualLights(dataFromWit);
						if (!virtualBoard.equals("0")){
						System.out.println("This is the board: " + virtualBoard.substring(2));
						System.out.println("Updating Twitter...");
						String updateTweet = "@" + userName + " Thanks for that command. Board is now " + virtualBoard.substring(2) + ".";
						StatusUpdate confirmTweet = new StatusUpdate(updateTweet);
						confirmTweet.inReplyToStatusId(newTweetId);
						try {
							if (debug == 0){
								twitter.updateStatus(updateTweet);
								System.out.println("Tweeted command confirmation.");
							}
							else{
								System.out.println("DEBUG MODE: Skipped command confirmation tweet.");
							}
						} catch (Exception e1) {
							System.out.println("Unable to tweet.");
						}
						}
						else{
							try {
								StatusUpdate reply = new StatusUpdate("@" + userName + " Sorry, but that command was not understood. Try again!");
								reply.inReplyToStatusId(newTweetId);
								if (debug == 0){
									twitter.updateStatus(reply);
									System.out.println("Tweeted failure message.");
								}
								else{
									System.out.println("DEBUG MODE: Skipped failure message tweet.");
								}
							} catch (Exception e1) {
								System.out.println("Unable to tweet.");
							}
						}
						

						oldTweetId = newTweetId;
					} catch (Exception e) {
						System.out.println("Error. Probably unable to reach Wit.");
						
					}
				}
				// End unique ID check and response
				
			} catch (Exception e) {
				// Just in case we can't get to Twitter / find any new tweets to analyze... let's get some debug info
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
				System.out.println(timeStamp + "- Founds no tweets / Twitter is down.");
			}
			
			Thread.sleep(10000); // Delay in ms
		}
	}
}