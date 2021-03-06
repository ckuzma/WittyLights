import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class WitQuery{
	Credentials credentials = new Credentials(); // To prevent me sharing my own keys...
	
	private String authToken = credentials.witAccessKey;
	private String witUrl = "https://api.wit.ai/message?v=20141112&q=";
	
	public String parseQuery(String userCommand) throws IOException{
		String userQuery = URLEncoder.encode(userCommand, "UTF-8");
		String completeUrl = witUrl + userQuery;
		URL url = new URL(completeUrl);
		URLConnection openWitConnection = url.openConnection();
		openWitConnection.setRequestProperty("Authorization", "Bearer " + authToken);
		BufferedReader incoming = new BufferedReader(new InputStreamReader(openWitConnection.getInputStream()));
		String inputLine;
		String returnMessage = "";
		while ((inputLine = incoming.readLine()) != null) {
			returnMessage += inputLine;
		}
		incoming.close();
		return returnMessage;
	}
	
	
}