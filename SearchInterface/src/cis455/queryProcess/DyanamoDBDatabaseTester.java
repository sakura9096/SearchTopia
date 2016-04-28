package cis455.queryProcess;

import java.util.ArrayList;

public class DyanamoDBDatabaseTester {
	public static void main (String[] args) {
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		ArrayList<TFIDFURLWrapper> returnedItems = database.getURLsFromWord("adhes");
		for (TFIDFURLWrapper item: returnedItems) {
			System.out.println(item.url + "\t" + item.tfidf + "\n");
		}
	}
 }
