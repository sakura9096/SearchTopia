package cis455.queryProcess;

public class ItemWrapper {
	
	String originalUrl;
	String normalizedUrl;
	double tfidf;
	double pageRank;
	
	public ItemWrapper (double tfidf, String url) {
		this.tfidf = tfidf;
		this.normalizedUrl = url;
	}
	
	public ItemWrapper(double tfidf, double pageRank, String originalUrl, String normalizedUrl) {
		this.tfidf = tfidf;
		this.pageRank = pageRank;
		this.originalUrl = originalUrl;
		this.normalizedUrl = normalizedUrl;
	}
	
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}
	
	public String getNormalizedUrl() {
		return normalizedUrl;
	}
}
