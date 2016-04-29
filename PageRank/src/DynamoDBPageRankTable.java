import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

public class DynamoDBPageRankTable {

	static AmazonDynamoDBClient dynamoDB;

	public static void main(String[] args) throws Exception {
		init();

		writeMultipleItemsBatchWrite();
	}

	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (/Users/fanglinlu/.aws/credentials).
		 */
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
			System.out.println(credentials);
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (/Users/fanglinlu/.aws/credentials), and is in valid format.",
							e);
		}
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usEast = Region.getRegion(Regions.US_EAST_1);
		dynamoDB.setRegion(usEast);
	}

	private static void writeMultipleItemsBatchWrite() {

		String tableName = "PageRank";

		ArrayList<Map<String, AttributeValue>> items = getItemFromFile();
		try {
			int i = 0;
			while (i < items.size()) {

				List<WriteRequest> requests = new ArrayList<WriteRequest>();

				for (int j = 0; j < 25 && i < items.size(); j++, i++) {
					PutRequest putRequest = new PutRequest (items.get(i));
					WriteRequest wr = new WriteRequest (putRequest);
					requests.add(wr);
				}

				Map<String, List<WriteRequest>> map = new HashMap<String, List<WriteRequest>>();

				map.put(tableName, requests);
				BatchWriteItemRequest bwir = new BatchWriteItemRequest(map);
				BatchWriteItemResult outcome = dynamoDB.batchWriteItem(bwir);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private static ArrayList<Map<String, AttributeValue>> getItemFromFile() {

		ArrayList<Map<String, AttributeValue>> result = new ArrayList<Map<String, AttributeValue>>();

		FileReader fileReader = null;
		BufferedReader br1 = null;
		try {
			fileReader = new FileReader ("/Users/yuezhang/Downloads/output/part-r-00010");
			br1 = new BufferedReader (fileReader);

			String line;
			while ((line = br1.readLine()) != null) {
				String[] token = line.split("\t");
				String url = token[0];
				double pageRank = Double.parseDouble(token[1]);
				System.out.println("URL: " + url + " pageRank: " + pageRank);

				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("url", new AttributeValue (url));
				item.put("PageRank", new AttributeValue().withN(pageRank + ""));
				result.add(item);		
			}
		} catch (IOException e) {

			System.err.println("Error reading the PageRank file.");

		} finally {			
			try {
				fileReader.close();
				br1.close();			
			} catch (IOException e) {			
				System.err.println("Error closing the FileReader/BufferedReader.");
			}
		}

		return result;
	}
}
