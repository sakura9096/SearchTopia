package cis455.queryProcess;

public class ItemWrapper2 implements Comparable<ItemWrapper2>{
	String originalUrl;
	String normalizedUrl;
	double tfidf;
	double pageRank;
	double totalScore;
	
	public ItemWrapper2 (double tfidf, String url) {
		this.tfidf = tfidf;
		this.normalizedUrl = url;
	}
	
	public ItemWrapper2 (double tfidf, double pageRank, String originalUrl, double totalScore) {
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
	
	public String getOriginalUrl() {
		return originalUrl;
	}
	
	public void setTotalScore(double score) {
		this.totalScore = score;
	}
	
	public double getTotalScore() {
		return totalScore;
	}

	@Override
	public int compareTo(ItemWrapper2 o) {
		if (o.getTotalScore() < totalScore) {
			return 1;
		} else if (o.getTotalScore() > totalScore) {
			return -1;
		} else {
			return 0;
		}
	}

}
