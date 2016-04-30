package cis455.queryProcess;

import java.util.ArrayList;
import java.util.List;

public class DyanamoDBDatabaseTester {
	public static void main (String[] args) {
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<ItemWrapper2> returnedItems = database.getURLsFromFacnyBarrel("228");
		
		for (ItemWrapper2 item: returnedItems) {
			System.out.println(item.originalUrl + "\t" + item.tfidf + "\n");
		}
	}
 }
