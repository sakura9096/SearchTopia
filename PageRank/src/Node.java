import java.util.HashSet;

/**
 * @author yuezhang
 *
 */
public class Node {

	private String url;
	private double pageRank;
//	private HashSet<String> outlinks;
	
	
	/**
	 * @param url
	 */
	public Node(String url) {
		this.url = url;
//		outlinks = new HashSet<>();
	}
	
//	public void addOutLinks(String outlink) {
//		outlinks.add(outlink);
//	}
	
//	public HashSet<String> getOutLinks() {
//		return outlinks;
//	}
	
//	public void removeOutLinks(String outlink) {
//		outlinks.remove(outlink);
//	}
//	
//	public String outlinksToString() {
//		StringBuilder sb = new StringBuilder();
//		for (String outlink : outlinks) {
//			sb.append(outlink + "\t");
//		}
//		sb.substring(0, sb.length() - 1);
//		return sb.toString();
//	}

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
