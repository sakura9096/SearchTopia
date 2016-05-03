

@DynamoDBTable (tableName = "PageRank")
public class PageRankTable {

	private String url;
	private double pageRank;

	@DynamoDBHashKey (attributeName = "url")
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@DynamoDBRangeKey (attributeName = "PageRank")
	public String getPageRank () {
		return pageRank;
	}
	
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}
	
	
}
