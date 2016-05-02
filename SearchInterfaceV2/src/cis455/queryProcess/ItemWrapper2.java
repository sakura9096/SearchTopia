package cis455.queryProcess;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	
	public ItemWrapper2 (double tfidf, double pageRank, String originalUrl, String normalizedUrl, double totalScore) {
		this.tfidf = tfidf;
		this.pageRank = pageRank;
		this.originalUrl = originalUrl;
		this.normalizedUrl = normalizedUrl;
		this.totalScore = totalScore;
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
	

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(this.normalizedUrl).
            append(tfidf).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof ItemWrapper2))
            return false;
        if (obj == this)
            return true;

       ItemWrapper2 rhs = (ItemWrapper2) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(this.normalizedUrl, rhs.normalizedUrl).
            isEquals();
    }


}
