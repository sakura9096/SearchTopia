package cis455.queryProcess;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable (tableName = "Fancy-Barrel")

public class FancyBarrel {
	private String word;
	private String url;
	private double tfidf;
	
	@DynamoDBHashKey (attributeName = "word")
	public String getWord() {
		return word;
	}
	
	public void setWord (String word) {
		this.word = word;
	}
	
	@DynamoDBRangeKey (attributeName = "url")
	public String getURL () {
		return url;
	}
	
	public void setURL (String url) {
		this.url = url;
	}
	
	@DynamoDBAttribute (attributeName = "tfidf")
	public double getTFIDF () {
		return tfidf;
	}
	
	public void setTFIDF (double tfidf) {
		this.tfidf = tfidf;
	}
	
	
	
}
