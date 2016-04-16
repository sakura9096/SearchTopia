
/**
 * @author yuezhang
 *
 */
public class Node {

	private String url;
	private double pageRank;
	
	
	/**
	 * @param url
	 */
	public Node(String url) {
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the pageRank
	 */
	public double getPageRank() {
		return pageRank;
	}

	/**
	 * @param pageRank the pageRank to set
	 */
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}

	/**
	 * Override equals().
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {	
		return url.equals(((Node)obj).getUrl());
	}
	
	/**
	 * Override hashCode(). 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {		
		return url.hashCode();
	}
	
}
