package cis455.queryProcess;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable (tableName = "PageRank")
public class PageRankTable {

	private String normalizedUrl;
	private double pageRank;

	@DynamoDBHashKey (attributeName = "url")
	public String getUrl() {
		return normalizedUrl;
	}
	
	public void setUrl(String url) {
		this.normalizedUrl = url;
	}
	
	@DynamoDBRangeKey (attributeName = "PageRank")
	public double getPageRank () {
		return pageRank;
	}
	
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}
	
	
}

