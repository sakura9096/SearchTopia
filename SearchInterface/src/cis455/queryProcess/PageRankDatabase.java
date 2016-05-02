
public class PageRankDatabase {

	private static PageRankDatabase database = null;
	static AmazonDynamoDBClient dynamoDB;

	private PageRankDatabase() {

	}

	public static PageRankDatabase getInstance () {
		if (database == null) {
			database = new PageRankDatabase();
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


