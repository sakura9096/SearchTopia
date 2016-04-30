import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

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

public class DynamoDBImportTable3 {
	
	private static Comparator <NewTFIDFURLWrapper> tfidfComparator = new Comparator <NewTFIDFURLWrapper> () {
		public int compare (NewTFIDFURLWrapper w1, NewTFIDFURLWrapper w2) {
			if (w1.tfidf == w2.tfidf) return 0;
			return w1.tfidf < w2.tfidf ? -1 : 1;
		}
	};
	
	 static AmazonDynamoDBClient dynamoDB;
	 static String prevString = null;
	 static boolean isFancy = false;
	 static Set<String> set = new HashSet<>();
	 
	 static int heapLimit = 300;
	 static Queue<NewTFIDFURLWrapper> fancyWriteQueue = new LinkedList<>();
	 static Queue<NewTFIDFURLWrapper> normalWriteQueue = new LinkedList<>();
	 static int queueLimit = 25;
	 static String fancyTable = "Fancy";
	 static String normalTable = "Normal";
	 
	 static Queue<NewTFIDFURLWrapper> heap = new PriorityQueue<>(heapLimit, tfidfComparator);

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
        
        File directory = new File ("/Users/yilunfu/Documents/workspace/Group02/S3Test/output");
		File[] fList = directory.listFiles ();
		
		for (File file : fList) {
    		String tableName;
    		try {
			FileReader fileReader = new FileReader (file.getAbsolutePath());
			BufferedReader br1 = new BufferedReader (fileReader);
			
			getItems(br1);
	    	br1.close();	
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		
		}
    }
    
    /*
     * Batch write to Queue
     */
    private static void batchWriteQueue(Queue<NewTFIDFURLWrapper> queue, String tableName) throws Exception {
    	Map<String, List<WriteRequest>> map = new HashMap <String, List<WriteRequest>>();
    	List<WriteRequest> requests = new ArrayList<WriteRequest>();
    	while (!queue.isEmpty()) {
    		NewTFIDFURLWrapper newItem = queue.poll();
    		Map<String, AttributeValue> item = new HashMap<>();
    		if (newItem.url.indexOf('?') != -1) {
    			throw new Exception("url contains ?");
    		}
//    		System.out.println(newItem.word);
//    		System.out.println(newItem.url);
//    		System.out.println(newItem.tfidf);
    		item.put("word", new AttributeValue(newItem.word));
    		item.put("url", new AttributeValue(newItem.url));
    		item.put("originalurl", new AttributeValue(newItem.originURL));
    		item.put("tfidf", new AttributeValue(String.valueOf(newItem.tfidf)));
    		
    		PutRequest putRequest = new PutRequest(item);
    		WriteRequest wr = new WriteRequest (putRequest);
    		requests.add(wr);
    	}
    	
    	map.put(tableName, requests);
    	BatchWriteItemRequest bwir = new BatchWriteItemRequest(map);
    	BatchWriteItemResult outcome = dynamoDB.batchWriteItem(bwir);
    }
    
    private static void batchWriteFancyQueue() throws Exception {
    	batchWriteQueue(fancyWriteQueue, fancyTable);
    }
    
    private static void batchWriteNormalQueue() throws Exception {
    	batchWriteQueue(normalWriteQueue, normalTable);
    }
    
    public static void addToFancyQueue(NewTFIDFURLWrapper wrapper) throws Exception {
    	fancyWriteQueue.offer(wrapper);
    	if (fancyWriteQueue.size() == queueLimit) {
    		batchWriteFancyQueue();
    	}
    }
    
    public static void addToNormalQueue(NewTFIDFURLWrapper wrapper) throws Exception {
    	normalWriteQueue.offer(wrapper);
    	if (normalWriteQueue.size() == queueLimit) {
    		batchWriteNormalQueue();
    	}
    }
    
    public static void clearHeap() throws Exception {
    	if (prevString == null) {
    		return;
    	}
    	while (!heap.isEmpty()) {
    		if (isFancy) {
    			addToFancyQueue(heap.poll());
    		} else {
    			addToNormalQueue(heap.poll());
    		}
    	}
    	
    }
    
    public static void getItems(BufferedReader br1) throws Exception {
    	String line =  null;
		while ((line = br1.readLine()) != null) {
			String[] lineInfo = line.split("\t");
			String word;
			String url;
			String originUrl;
			String tf;
			String idf;
			String tfIdf;
			
			if (lineInfo.length == 5) {
				word = lineInfo[0];
				originUrl = lineInfo[1];
				url = getOriginUrl(originUrl);
				tf =lineInfo[2];
				idf = lineInfo[3];
				tfIdf = lineInfo[4];
				
			} else if (lineInfo.length == 6){
				word = lineInfo[0] + " " + lineInfo[1];
				originUrl = lineInfo[2];
				url = getOriginUrl(originUrl);
				tf = lineInfo[3];
				idf = lineInfo[4];
				tfIdf = lineInfo[5];	
			} else {
				continue;
			}
			
			String check = word.substring(0, 2);
			if (check.equals("2:")) {
				continue;
			}
			word = word.substring(2);
			
			if (prevString != null && prevString.equals(word)) {
				double tfIdfValue = Double.parseDouble(tfIdf);
				DecimalFormat df = new DecimalFormat ("#.##");
				NewTFIDFURLWrapper wrapper = new NewTFIDFURLWrapper(word, Double.parseDouble (df.format(tfIdfValue)), url, originUrl);
				if (heap.size() < heapLimit) {
					if (!set.contains(url)) {
						set.add(url);
						heap.offer(wrapper);
					} 
				} else if (heap.peek().tfidf < wrapper.tfidf) {
					NewTFIDFURLWrapper temp = heap.poll();
					set.remove(temp.url);
					if (set.contains(url)) {
						set.add(temp.url);
						heap.offer(temp);
					} else {
						set.add(url);
						heap.offer(wrapper);
					}
				}
			} else {
				clearHeap();
				set.clear();
				if (check.equals("1:")) {
					isFancy = true;
				} else {
					isFancy = false;
				}
				prevString = word;
				double tfIdfValue = Double.parseDouble(tfIdf);
				DecimalFormat df = new DecimalFormat ("#.##");
				NewTFIDFURLWrapper wrapper = new NewTFIDFURLWrapper(word, Double.parseDouble (df.format(tfIdfValue)), url, originUrl);
				set.add(url);
				heap.offer(wrapper);
			}
		}
		clearHeap();
    }
    
    public static String getOriginUrl(String url) {
    	int index = url.indexOf('?');
    	if (index == -1) {
    		return url;
    	} else {
    		return url.substring(0, index);
    	}
    }
    
    
}
