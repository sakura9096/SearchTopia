

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class Node {

	private double pageRank = 0.25;
	private String[] adjacentNodes;
	
	public static final char fieldSeparator = '\t';
	
	public double getPageRank() {
		return pageRank;
	}
		
	public Node setPageRank(double pr) {
		this.pageRank = pr;
		return this;
	}


	/**
	 * @return the adjacentNodes
	 */
	public String[] getAdjacentNodes() {
		return adjacentNodes;
	}


	/**
	 * @param adjacentNodes the adjacentNodes to set
	 */
	public Node setAdjacentNodes(String[] adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
		return this;
	}
	
	
	public boolean containsAdjacentNodes() {
		return adjacentNodes != null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pageRank);
		if (getAdjacentNodes() != null) {
			sb.append(fieldSeparator).append(StringUtils.join(getAdjacentNodes(), fieldSeparator));	
		}
		return sb.toString();
	}
	
	
	public static Node fromMR(String value) throws IOException {
		String[] tokens = StringUtils.splitPreserveAllTokens(value, fieldSeparator);
		if (tokens.length < 1) {
			throw new IOException("Expected 1 or more tokens but received " + tokens.length);
		}
		Node node = new Node().setPageRank(Double.valueOf(tokens[0]));
		if (tokens.length > 1) {
			node.setAdjacentNodes(Arrays.copyOfRange(tokens, 1, tokens.length));
		}
		return node;
	}
}



