import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynamoDBTest {
	
	public static void main (String[] args) {
		try {
			FileReader fileReader = new FileReader ("dynamodbTest");
			BufferedReader br1 = new BufferedReader (fileReader);
			DynamoDBIndexerTable dit = new DynamoDBIndexerTable();
			ArrayList<ArrayList<Map<String, AttributeValue>>> items = dit.get1000Items(br1);
			
			for (int i = 0; i < items.size(); i++) {
				for (int j = 0; j < items.get(i).size(); j++) {
					System.out.println(items.get(i).get(j).get("word") + "\t" + items.get(i).get(j).get("url") + "\t" + items.get(i).get(j).get("tfidf"));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
}
