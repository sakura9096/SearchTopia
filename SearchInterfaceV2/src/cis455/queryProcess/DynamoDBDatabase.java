	package cis455.queryProcess;

import java.awt.print.Book;
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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.Attribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class DynamoDBDatabase {
	
	private static DynamoDBDatabase database = null;
	static AmazonDynamoDBClient dynamoDB;
	
	private DynamoDBDatabase() {
		
	}
	
	public static DynamoDBDatabase getInstance () {
		if (database == null) {
			database = new DynamoDBDatabase();
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
	 
	 public List<ItemWrapper2> getURLsFromFacnyBarrel (String word) {
//		TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes ("Fancy-Bareel");
//		tableKeysAndAttributes.addHashOnlyPrimaryKeys( , , );
		 List<ItemWrapper2> result = new ArrayList<ItemWrapper2> ();
		 
		 DynamoDBMapper mapper = new DynamoDBMapper (dynamoDB);
		 
		 //FancyBarrel fb = mapper.load(FancyBarrel.class, word);
		 
		 Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		 
		 eav.put(":val1", new AttributeValue().withS(word));
		 
		 DynamoDBQueryExpression<FancyBarrel> queryExpression = new DynamoDBQueryExpression<FancyBarrel>()
		            .withKeyConditionExpression("word = :val1")
		            .withExpressionAttributeValues(eav);
		 
		 List<FancyBarrel> queryResult = mapper.query(FancyBarrel.class, queryExpression);
		 System.out.println("The size of fancybarrel is:" + queryResult.size());
		 
		 for (FancyBarrel itemsValue: queryResult) {
			 double tfidf =itemsValue.getTFIDF();
			 String normalizedUrl = itemsValue.getNormalizedURL();
			 
			 Map<String, AttributeValue> eav1 = new HashMap<String, AttributeValue>();
			 
			 eav1.put(":val2", new AttributeValue().withS(normalizedUrl));
			 
			 DynamoDBQueryExpression<PageRankTable> queryExpression1 = new DynamoDBQueryExpression<PageRankTable>()
			            .withKeyConditionExpression("normalizedUrl = :val2")
			            .withExpressionAttributeValues(eav1);
			 
			 List<PageRankTable> queryResult1 = mapper.query (PageRankTable.class, queryExpression1);
			 double pageRank = queryResult1.get(0).getPageRank();
			 double totalScore = tfidf * pageRank;
			 ItemWrapper2 item = new ItemWrapper2(tfidf, pageRank, itemsValue.getOriginalURL(), totalScore);
			 
			 result.add(item);
		 
		 }
//		 
		 System.out.println("result size is:" + result.size());
		 return result;
		 
		
	 }
	 
	 public List<ItemWrapper2> getURLsFromNormalBarrel (String word) {
//			TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes ("Fancy-Bareel");
//			tableKeysAndAttributes.addHashOnlyPrimaryKeys( , , );
			 List<ItemWrapper2> result = new ArrayList<ItemWrapper2> ();
			 
			 DynamoDBMapper mapper = new DynamoDBMapper (dynamoDB);
			 
			 //FancyBarrel fb = mapper.load(FancyBarrel.class, word);
			 
			 Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
			 
			 eav.put(":val1", new AttributeValue().withS(word));
			 
			 DynamoDBQueryExpression<NormalBarrel> queryExpression = new DynamoDBQueryExpression<NormalBarrel>()
			            .withKeyConditionExpression("word = :val1")
			            .withExpressionAttributeValues(eav);
			 
			 List<NormalBarrel> queryResult = mapper.query (NormalBarrel.class, queryExpression);
			 
			 for (NormalBarrel itemsValue: queryResult) {
				 double tfidf =itemsValue.getTFIDF();
				 String normalizedUrl = itemsValue.getNormalizedURL();
				 
				 Map<String, AttributeValue> eav1 = new HashMap<String, AttributeValue>();
				 
				 eav1.put(":val2", new AttributeValue().withS(normalizedUrl));
				 
				 DynamoDBQueryExpression<PageRankTable> queryExpression1 = new DynamoDBQueryExpression<PageRankTable>()
				            .withKeyConditionExpression("normalizedUrl = :val2")
				            .withExpressionAttributeValues(eav1);
				 
				 List<PageRankTable> queryResult1 = mapper.query (PageRankTable.class, queryExpression1);
				 double pageRank = queryResult1.get(0).getPageRank();
				 double totalScore = tfidf * pageRank;
				 ItemWrapper2 item = new ItemWrapper2(tfidf, pageRank, itemsValue.getOriginalURL(), totalScore);
				 
				 result.add(item);
			 
			 }
			 
			 
//			 QueryRequest qRequest = new QueryRequest ("Fancy-Barrel");
//			 qRequest.setKeyConditionExpression ("word=:" + word);
//			 
//			 QueryResult qr = dynamoDB.query (qRequest);
			 
//			 for (Map<String, AttributeValue> itemsValue: qr.getItems()) {
//				 AttributeValue tfidfAttribute = itemsValue.get("tfidf");
//				 String tfidfAttributeStr = tfidfAttribute.getN();
//				 Double tfidf = Double.parseDouble (tfidfAttributeStr);
//				 
//				 
//				 TFIDFURLWrapper tfuw = new TFIDFURLWrapper(tfidf, itemsValue.get("url").getS());
//				 result.add(tfuw);
//				 
//			 }
//			 
			 return result;
			 
			
		 }
	 
	 

	 public ArrayList<ItemWrapper> addPageRank (ArrayList<ItemWrapper> list) {

			DynamoDBMapper mapper = new DynamoDBMapper (dynamoDB);

			for (ItemWrapper item : list) {
				String url = item.getNormalizedUrl();
				
				Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();

				eav.put(":val1", new AttributeValue().withS(url));

				DynamoDBQueryExpression<PageRankTable> queryExpression = new DynamoDBQueryExpression<PageRankTable>()
						.withKeyConditionExpression("url = :val1")
						.withExpressionAttributeValues(eav);

				List<PageRankTable> queryResult = mapper.query(PageRankTable.class, queryExpression);
				double rankVal = queryResult.get(0).getPageRank();
				item.setPageRank(rankVal);
			}

			return list;
		}
		
	  
}
