	package cis455.queryProcess;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;


public class DynamoDBDatabase {
	
	private static DynamoDBDatabase database = null;
	static AmazonDynamoDBClient dynamoDB;
	//private static final Logger logger = LogManager.getLogger(DynamoDBDatabase.class);
	
	private DynamoDBDatabase() {
		
	}
	
	public static DynamoDBDatabase getInstance () {
		if (database == null) {
			database = new DynamoDBDatabase();
			//logger.debug("enter database");
			try {
				init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		
		return database;
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
            
        } catch (Exception e) {
//            logger.debug(
//                    "Cannot load the credentials from the credential profiles file. " +
//                    "Please make sure that your credentials file is at the correct " +
//                    "location (/Users/fanglinlu/.aws/credentials), and is in valid format.",
//                    e);
        }
        dynamoDB = new AmazonDynamoDBClient(credentials);
        if (dynamoDB == null) {
        	//logger.debug("dynamoDB is null");
        }
        Region usEast = Region.getRegion(Regions.US_EAST_1);
        dynamoDB.setRegion(usEast);
	 }
	 
	 public List<ItemWrapper2> getURLsFromFacnyBarrel (String word) {
//		TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes ("Fancy-Bareel");
//		tableKeysAndAttributes.addHashOnlyPrimaryKeys( , , );
		 List<ItemWrapper2> result = new ArrayList<ItemWrapper2> ();
		 
		 DynamoDBMapper mapper = new DynamoDBMapper (dynamoDB);
		 

		 if (dynamoDB == null) {
			 throw new NullPointerException("DynamoDB is Null");
		 }
		 
		 //FancyBarrel fb = mapper.load(FancyBarrel.class, word);
		 
		 Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		 
		 
		 eav.put(":val1", new AttributeValue().withS(word));
		 
		 DynamoDBQueryExpression<FancyBarrel> queryExpression = new DynamoDBQueryExpression<FancyBarrel>()
		            .withKeyConditionExpression("word = :val1")
		            .withExpressionAttributeValues(eav);
		 
		 List<FancyBarrel> queryResult = mapper.query(FancyBarrel.class, queryExpression);
		 System.out.println("The size of fancybarrel is:" + queryResult.size());
		 
		 Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		 expressionAttributeNames.put("#u", "url");
		 
		 for (FancyBarrel itemsValue: queryResult) {
			 double tfidf =itemsValue.getTFIDF();
			 String normalizedUrl = itemsValue.getUrl();
			// System.out.println("normalized: " + normalizedUrl);
			 Map<String, AttributeValue> eav1 = new HashMap<String, AttributeValue>();
			 
			 eav1.put(":val2", new AttributeValue().withS(normalizedUrl));
			 
			 DynamoDBQueryExpression<PageRankTable> queryExpression1 = new DynamoDBQueryExpression<PageRankTable>()
			            .withKeyConditionExpression("#u = :val2")
			            .withExpressionAttributeNames(expressionAttributeNames)
			            .withExpressionAttributeValues(eav1);
			 
			 List<PageRankTable> queryResult1 = mapper.query (PageRankTable.class, queryExpression1);
			 double pageRank;
			 double totalScore;
			 if (queryResult1 == null || queryResult1.size() == 0) {
				 pageRank = 0;
				 totalScore = 2 * tfidf * pageRank / (tfidf + pageRank);
			 } else {
				pageRank = queryResult1.get(0).getPageRank();
				totalScore = 2 * tfidf * pageRank / (tfidf + pageRank);
			 }
			 
			 ItemWrapper2 item = new ItemWrapper2(tfidf, pageRank, itemsValue.getOriginalURL(), normalizedUrl, totalScore);
           
			 result.add(item);
		 
		 }
//		 
		 System.out.println("result size is:" + result.size());
		 return result;
		 
		
	 }
	 
	 public List<ItemWrapper2> getURLsFromNormalBarrel (String word) {

			 List<ItemWrapper2> result = new ArrayList<ItemWrapper2> ();
			 
			 DynamoDBMapper mapper = new DynamoDBMapper (dynamoDB);
			 
			 //FancyBarrel fb = mapper.load(FancyBarrel.class, word);
			 
			 Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
			 
			 eav.put(":val1", new AttributeValue().withS(word));
			 
			 DynamoDBQueryExpression<NormalBarrel> queryExpression = new DynamoDBQueryExpression<NormalBarrel>()
			            .withKeyConditionExpression("word = :val1")
			            .withExpressionAttributeValues(eav);
			 
			 List<NormalBarrel> queryResult = mapper.query (NormalBarrel.class, queryExpression);
			 
			 Map<String, String> expressionAttributeNames = new HashMap<String, String>();
			 expressionAttributeNames.put("#u", "url");
			 
			 for (NormalBarrel itemsValue: queryResult) {
				 double tfidf =itemsValue.getTFIDF();
				 String normalizedUrl = itemsValue.getNormalizedURL();
				 
				 Map<String, AttributeValue> eav1 = new HashMap<String, AttributeValue>();
				 
				 eav1.put(":val2", new AttributeValue().withS(normalizedUrl));
				 
				 DynamoDBQueryExpression<PageRankTable> queryExpression1 = new DynamoDBQueryExpression<PageRankTable>()
				            .withKeyConditionExpression("#u = :val2")
				            .withExpressionAttributeNames(expressionAttributeNames)
				            .withExpressionAttributeValues(eav1);
				 
				 List<PageRankTable> queryResult1 = mapper.query (PageRankTable.class, queryExpression1);
				
				 double pageRank;
				 double totalScore;
//				 double tf_idf = Double.parseDouble(tfidf);
				 if (queryResult1 == null || queryResult1.size() == 0) {
					 pageRank = 0;
					 totalScore = 2 * tfidf * pageRank / (tfidf + pageRank);
				 } else {
					pageRank = queryResult1.get(0).getPageRank();
					totalScore = 2 * tfidf * pageRank / (tfidf + pageRank);
				 }
				 
				 ItemWrapper2 item = new ItemWrapper2(tfidf, pageRank, itemsValue.getOriginalURL(), normalizedUrl, totalScore);
				 
				 result.add(item);
			 
			 }
			 
			 return result;
			 
			
		 }

	  
}
