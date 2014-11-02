import java.util.ArrayList;
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
		
		// Make some objects
		ConnectionToWit wit = new ConnectionToWit();
		ProcessWitJson processor = new ProcessWitJson();
		ConnectionToArduino arduino = new ConnectionToArduino();
		
		// Ask about connecting to Arduino
		int arduinoBinary = 0;
    	System.out.print("\nWittyLights v2.0\n---by Chris Kuzma\n\nDo you want to connect to an Arduino? Enter Y or N: ");
    	Scanner input = new Scanner(System.in);
    	String arduinoOrNot = input.nextLine();
    	if(arduinoOrNot.equals("y") || arduinoOrNot.equals("Y")){
			arduinoBinary = 1;
			try {
				arduino.initialize();
			} catch (Exception e) {
				System.out.println("Unable to connect to Arduino.");
			}
		}
		if(arduinoOrNot.equals("n") || arduinoOrNot.equals("N")){
			arduinoBinary = 0;
		}
		
		// Twitter authentication garbage
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	          .setOAuthConsumerKey("BJdtbS7GkjmoXKhsTZmSDAnj6")
	          .setOAuthConsumerSecret("hH7AWbesIwHbdprzXlzQ09QAaP7kZ9Jhi7ZiLIfRZo57lCgMH1")
	          .setOAuthAccessToken("2835411379-1pOxY5y9kmlrpBlVn2CwLvdMiBC8ImFy0WPCQUB")
	          .setOAuthAccessTokenSecret("dduwhk21tyeAhYgvyNAhqJDI36wLmIiUXcHrqs8Mdk1nP");
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	    // End twitter authentication garbage
	    int x = 0;
	    long oldTweetId = 0;
	    while (x < 1) {
			try {
				// Find most recent tweet
				Query query = new Query("@WittyDevices");
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				Status recentTweet = tweets.get(0);
				String userName = recentTweet.getUser().getScreenName();
				String tweetText = recentTweet.getText();
				long newTweetId = recentTweet.getId();
				// End most recent tweet search
				// Check for a unique ID, query Wit
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
							twitter.updateStatus(confirmTweet);
							System.out.println("Tweeted successfully.");
						} catch (Exception e1) {
							System.out.println("Unable to tweet.");
						}
						}
						else{
							try {
								StatusUpdate reply = new StatusUpdate("@" + userName + " Sorry, but that command was not understood. Try again!");
								reply.inReplyToStatusId(newTweetId);
								twitter.updateStatus(reply);
								
								System.out.println("Tweeted failure message successfully.");
							} catch (Exception e1) {
								System.out.println("Unable to tweet.");
							}
						}
						
						if (arduinoBinary == 1){
							try {
								arduino.sendData(virtualBoard);
							} catch (Exception e) {
								System.out.println("Unable to send data to Arduino.");
							}
						}
						oldTweetId = newTweetId;
					} catch (Exception e) {
						System.out.println("Error. Probably unable to reach Wit.");
						
					}
				}
				// End ID check
				
			} catch (Exception e) {
				System.out.println("Founds no tweets / Twitter is down.");
			}
			// Delay
			Thread.sleep(5000);
		}
	}
}