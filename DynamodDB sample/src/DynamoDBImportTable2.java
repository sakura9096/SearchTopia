import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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

public class DynamoDBImportTable2 {
	 static AmazonDynamoDBClient dynamoDB;

	 /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.ProfilesConfigFile
     * @see com.amazonaws.ClientConfiguration
     **/
    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (/Users/fanglinlu/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
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

    public static void main(String[] args) throws Exception {
        init();
  
        writeMultipleItemsBatchWrite();
    }
    
    private static void writeMultipleItemsBatchWrite() {
    		
    		File directory = new File ("/Users/fanglinlu/Desktop/newOutput");
    		File[] fList = directory.listFiles ();
    		
    		for (File file : fList) {
	    		String tableName;
	    		try {
				FileReader fileReader = new FileReader (file.getAbsolutePath());
				BufferedReader br1 = new BufferedReader (fileReader);
				
				
		    		while (true) {
		    		    
			    		ArrayList<ArrayList<Map<String, AttributeValue>>> returningItems = get1000Items(br1);
	
			    		
			    		if (returningItems.get(0).size() == 0 && returningItems.get(1).size() == 0) {
			    			break;
			    		}
			    		for (int k = 0; k < 2; k++) {
			    			ArrayList<Map<String, AttributeValue>> items = returningItems.get(k);
				    		
				    		int i = 0;
				    		while (i < items.size()) {
				    			List<WriteRequest> requests = new ArrayList<WriteRequest>();
				    			HashSet<WordURLWrapper> wuw = new HashSet<WordURLWrapper> ();
				    			for (int j = 0; j < 25 && i < items.size(); j++, i++) {
				    				Map<String, AttributeValue> item = items.get(i);
				 //   				System.out.println("word:" + item.get("word").getS());
				 //   				System.out.println("url:" + item.get("url").getS());
				    				WordURLWrapper currentWrapper = new WordURLWrapper (item.get("word").getS(), item.get("url").getS());
				    				if (wuw.contains(currentWrapper)) {
				    					j--;
				 //   					System.out.println("Contains!");
				    				} else {
				    					wuw.add(currentWrapper);
				    					PutRequest putRequest = new PutRequest (items.get(i));
						    			WriteRequest wr = new WriteRequest (putRequest);
						    			requests.add(wr);
						    			
				    				}
					    		
				    			}
				    			
				    			Map<String, List<WriteRequest>> map = new HashMap <String, List<WriteRequest>>();
				    			if (k == 0) {
				    				tableName = "Fancy-Barrel2";
				    			} else {
				    				tableName = "Normal-Barrel2";
				    			}
				    			
				    			map.put(tableName, requests);
				    			
				    			
				    			BatchWriteItemRequest bwir = new BatchWriteItemRequest(map);
				//	    			TableWriteItems writeItems = new TableWriteItems(tableName).withItemsToPut(items);
				    			BatchWriteItemResult outcome = dynamoDB.batchWriteItem(bwir);
				    		}
			    		}
		    		}
		    		
		    		br1.close();		
				
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
    		}
		

    }
    
    public static ArrayList<ArrayList<Map<String, AttributeValue>>> get1000Items(BufferedReader br1) throws Exception{
    		ArrayList<ArrayList<Map<String, AttributeValue>>> result = new ArrayList<ArrayList<Map<String, AttributeValue>>> ();
    		
//		HashMap<String, PriorityQueue<TFIDFURLWrapper>> hm1 = new HashMap <String, PriorityQueue<TFIDFURLWrapper>>();
//		HashMap<String, PriorityQueue<TFIDFURLWrapper>> hm2 = new HashMap <String, PriorityQueue<TFIDFURLWrapper>>();
		
		ArrayList<Map<String,AttributeValue>> returningItems1 = new ArrayList<Map<String,AttributeValue>>();
		ArrayList<Map<String,AttributeValue>> returningItems2 = new ArrayList<Map<String,AttributeValue>>();
		
		String line = null;
		int i = 0;
		
//		while ((line = br1.readLine()) != null && i < 10000) {
//				i++;
//		}
		while ((line = br1.readLine()) != null && returningItems1.size() <= 1000 && returningItems2.size() <= 1000) {
//			System.out.println("The line is:" + line);
			String[] lineInfo = line.split("\t");
			String word;
			String url;
			String tf;
			String idf;
			String tfIdf;
			
			if (lineInfo.length == 5) {
				word = lineInfo[0];
				url = lineInfo[1];
				tf =lineInfo[2];
				idf = lineInfo[3];
				tfIdf = lineInfo[4];
				
			} else if (lineInfo.length == 6){
				word = lineInfo[0] + " " + lineInfo[1];
				url = lineInfo[2];
				tf = lineInfo[3];
				idf = lineInfo[4];
				tfIdf = lineInfo[5];	
			} else {
				continue;
			}

			String putWord = word.substring(2);

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("word", new AttributeValue (putWord));
			int index = url.indexOf("?");
			if (index != -1) { 
				item.put("url", new AttributeValue (url.substring(0, index)));
				item.put("originalURL", new AttributeValue (url));
			} else {
				item.put("url", new AttributeValue(url));
			}
			item.put("tf", new AttributeValue (tf));
			item.put("idf", new AttributeValue (idf));
			item.put("tfidf", new AttributeValue().withN(tfIdf));
			
			if (word.startsWith("1:")) {
				returningItems1.add(item);
			} else {
				returningItems2.add(item);
			}

		}
		
		result.add(returningItems1);
		result.add(returningItems2);
		return result;
    }
}
