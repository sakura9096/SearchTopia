package cis455.queryProcess;

import java.util.ArrayList;
import java.util.List;

public class DyanamoDBDatabaseTester {
	public static void main (String[] args) {
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<TFIDFURLWrapper> returnedItems = database.getURLsFromFacnyBarrel("job near");
		for (TFIDFURLWrapper item: returnedItems) {
			System.out.println(item.url + "\t" + item.tfidf + "\n");
		}
	}
 }
