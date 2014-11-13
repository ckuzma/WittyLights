import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WitResponse {
	private int[] virtualBoard = {0,0,0,0,0,0,0,0,0,0};
	public String theBoard = "00000000";
	private int continueToggle = 1;
	
	public String updateVirtualLights(String incomingJson) throws IOException{
		int ledNum = 0;
		String onOff = "off";
		JsonParser parser = new JsonParser();
		JsonObject responseObject = (JsonObject) parser.parse(incomingJson);
		// Print out the JSON
		//System.out.println(responseObject);
		try {
			JsonElement outcomes = responseObject.get("outcomes");
			JsonObject outcomesObject = (JsonObject) ((JsonArray) outcomes)
					.get(0);
			JsonElement entities = outcomesObject.get("entities");
			JsonElement entitiesValue = ((JsonObject) entities)
					.get("number");
			JsonElement entitiesCommand = ((JsonObject) entities)
					.get("on_off");
			JsonObject numberObject = (JsonObject) ((JsonArray) entitiesValue)
					.get(0);
			JsonObject commandObject = (JsonObject) ((JsonArray) entitiesCommand)
					.get(0);
			JsonElement number = numberObject.get("value");
			JsonElement power = commandObject.get("value");
			ledNum = number.getAsInt();
			onOff = power.getAsString();
			//debug
			//System.out.println("led " + ledNum + " power " + onOff);
			if (onOff.equals("on")) {
				virtualBoard[ledNum + 1] = 1;
			}
			if (onOff.equals("off")) {
				virtualBoard[ledNum + 1] = 0;
			}
		} catch (Exception e) {
			System.out.println("A misunderstood command was received. Try again.");
			continueToggle = 0;
		}
		// debug
		//printBoard(virtualBoard);
		String virtualBoardString = "";
		for (int x = 0; x < virtualBoard.length; x++) {
			virtualBoardString += virtualBoard[x];
		}
		theBoard = virtualBoardString;
		if(continueToggle == 0){
			return "0";
		}
		else{
			return virtualBoardString;
		}
	}
}