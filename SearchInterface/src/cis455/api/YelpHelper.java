package cis455.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class YelpHelper {

	final static String CONSUMER_KEY = "Ctz6UJHCjdZSVriRH3eTlg";
	final static String CONSUMER_SECRET = "TbeT6E8oP-W64eD872iiUX2hWoo";
	final static String TOKEN = "mDOlzmSR0pnKcHTJmsR_90lAnJwWRflr";
	final static String TOKEN_SECRET = "fugvNJGTmSOZrXo5jULzVTeSvKY";
	JSONArray array;
	
	public YelpHelper(String query) {
		YelpAPI yelpAPI = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
		array = yelpAPI.queryAPI(yelpAPI, query, "Philadelphia, PA");
	}
	
	public String excute() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"container\">"
				+ "<table class=\"table table-hover\" style='width:28%'>"
				+ "<thead>"
				+ "<tr>"
				+ "<th>Name</th>"
				+ "<th>Phone</th>"
				+ "<th>Rating</th>"
				+ "</tr>"
				+ "</thead><tbody>");
		for (int i = 0; i < array.size(); ++i) {
		    JSONObject rec = (JSONObject)array.get(i);
		    String name = rec.get("name").toString();
		    String url = rec.get("url").toString();
		    String rating = rec.get("rating").toString();
		    String phone = rec.get("phone").toString();
		    sb.append("<tr><td align='left'><a href=\"" + url + "\">" + name + "</a></td><td align='left'><img src=\"" + rating + "\"></td>"
		    		+ "<td align='left'>" + phone + "</td></tr>");
		}
		sb.append("</tbody></table></div>");	
		return sb.toString();
	}
}
 