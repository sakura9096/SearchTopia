import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class DynamoDBIndexerTable {
	
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
     */
	 private static Comparator <TFIDFURLWrapper> tfidfComparator = new Comparator <TFIDFURLWrapper> () {
			public int compare (TFIDFURLWrapper w1, TFIDFURLWrapper w2) {

				if (w1.tfidf - w2.tfidf < 0) {
					return 1;
				} else if (w1.tfidf - w2.tfidf > 0) {
					return -1;
				} else {
					return 0;
				}
				
			}
	 };
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
//    		FileReader fileReader = new FileReader ("/Users/fanglinlu/Documents/workspace/S3Test/output/Output");
    		String tableName;
    		try {
			FileReader fileReader = new FileReader ("/Users/fanglinlu/Documents/classes/CIS555/project/output3");
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line;
//			int l = 0;
//			while ((line = br1.readLine()) != null && l < 782300) {
//				l++;
//			}
			
	    		while (true) {
	    		    
		    		ArrayList<ArrayList<Map<String, AttributeValue>>> returningItems = get1000Items(br1);
//		    		System.out.println("The items size are:" + items.size());
		    		
//    				ArrayList<Map<String, AttributeValue>> items = new ArrayList <Map<String, AttributeValue>> ();
//    				
//    				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
//    				item.put("word", new AttributeValue ("000"));
//    				item.put("url", new AttributeValue ("url"));
//    				item.put("tfidf", new AttributeValue ().withN("2.344"));
//    				items.add(item);
		    		
		    		if (returningItems.get(0).size() == 0 && returningItems.get(1).size() == 0) {
		    			return;
		    		}
		    		for (int k = 0; k < 2; k++) {
		    			ArrayList<Map<String, AttributeValue>> items = returningItems.get(k);
//			    		if (items.size() == 0) return;
			    		
			    		int i = 0;
			    		while (i < items.size()) {
			    			List<WriteRequest> requests = new ArrayList<WriteRequest>();
			    			
			    			for (int j = 0; j < 25 && i < items.size(); j++, i++) {
				    			PutRequest putRequest = new PutRequest (items.get(i));
				    			WriteRequest wr = new WriteRequest (putRequest);
				    			requests.add(wr);
			    			}
			    			
			    			Map<String, List<WriteRequest>> map = new HashMap <String, List<WriteRequest>>();
			    			if (k == 0) {
			    				tableName = "Fancy-Barrel";
			    			} else {
			    				tableName = "Normal-Barrel";
			    			}
			    			
			    			map.put(tableName, requests);
			    			
			    			
			    			BatchWriteItemRequest bwir = new BatchWriteItemRequest(map);
			//	    			TableWriteItems writeItems = new TableWriteItems(tableName).withItemsToPut(items);
			    			BatchWriteItemResult outcome = dynamoDB.batchWriteItem(bwir);
			    		}
		    		}
	    		}
			
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
		

    }
    
    public static ArrayList<ArrayList<Map<String, AttributeValue>>> get1000Items(BufferedReader br1) throws Exception{
    		ArrayList<ArrayList<Map<String, AttributeValue>>> result = new ArrayList<ArrayList<Map<String, AttributeValue>>> ();
    		
		HashMap<String, PriorityQueue<TFIDFURLWrapper>> hm1 = new HashMap <String, PriorityQueue<TFIDFURLWrapper>>();
		HashMap<String, PriorityQueue<TFIDFURLWrapper>> hm2 = new HashMap <String, PriorityQueue<TFIDFURLWrapper>>();
		
		ArrayList<Map<String,AttributeValue>> returningItems1 = new ArrayList<Map<String,AttributeValue>>();
		ArrayList<Map<String,AttributeValue>> returningItems2 = new ArrayList<Map<String,AttributeValue>>();
		
		String line = null;
		int i = 0;
			while ((line = br1.readLine()) != null && i < 10000) {
				i++;
		}
		while ((line = br1.readLine()) != null && hm1.keySet().size() <= 1000 && hm2.keySet().size() <= 1000) {
			
			String[] lineInfo = line.split("\t");
			String word;
			String url;
			String tfIdf;
			if (lineInfo.length < 4) {
				word = lineInfo[0];
				url = lineInfo[1];
				tfIdf =lineInfo[2];
				
			} else {
				word = lineInfo[0] + " " + lineInfo[1];
				url = lineInfo[2];
				tfIdf = lineInfo[3];
			}
			
			double tfIdfValue = Double.parseDouble(tfIdf);
			DecimalFormat df = new DecimalFormat ("#.##");
//			System.out.println(df.format(tfIdfValue));
			TFIDFURLWrapper tuw = new TFIDFURLWrapper (Double.parseDouble (df.format(tfIdfValue)), url);
			
			if (word.startsWith("1:")) {
				String putWord = word.substring(2);
				if (hm1.keySet().contains(putWord)) {
					PriorityQueue<TFIDFURLWrapper> pq = hm1.get(putWord);
					pq.add(tuw);
				
				} else {
					PriorityQueue<TFIDFURLWrapper> pq = new PriorityQueue <TFIDFURLWrapper> (10, tfidfComparator);
					pq.add(tuw);
					hm1.put(putWord, pq);
				}
			} else {
				String putWord = word.substring(2);
				if (hm2.keySet().contains(putWord)) {
					PriorityQueue<TFIDFURLWrapper> pq = hm2.get(putWord);
					pq.add(tuw);
				
				} else {
					PriorityQueue<TFIDFURLWrapper> pq = new PriorityQueue <TFIDFURLWrapper> (10, tfidfComparator);
					pq.add(tuw);
					hm2.put(putWord, pq);
				}
			}

		}
				
		for (String keyWord: hm1.keySet()) {
			PriorityQueue<TFIDFURLWrapper> pq = hm1.get(keyWord);
			int m = 0;
			while (!pq.isEmpty() && m < 100) {
				TFIDFURLWrapper tfw = pq.poll();
//					bw.write(keyWord + "\t" +  tfw.url + "\t" + tfw.tfidf + "\n");
				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("word", new AttributeValue (keyWord));
				item.put("url", new AttributeValue (tfw.url));
				item.put("tfidf", new AttributeValue().withN(tfw.tfidf + ""));
				returningItems1.add(item);
				m ++;
			}
		}
		
		for (String keyWord: hm2.keySet()) {
			PriorityQueue<TFIDFURLWrapper> pq = hm2.get(keyWord);
			int m = 0;
			while (!pq.isEmpty() && m < 100) {
				TFIDFURLWrapper tfw = pq.poll();
//					bw.write(keyWord + "\t" +  tfw.url + "\t" + tfw.tfidf + "\n");
				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("word", new AttributeValue (keyWord));
				item.put("url", new AttributeValue (tfw.url));
				item.put("tfidf", new AttributeValue ().withN(tfw.tfidf + ""));
				returningItems2.add(item);
				m ++;
			}
		}
		
		
		result.add(returningItems1);
		result.add(returningItems2);
		return result;

    }

}
