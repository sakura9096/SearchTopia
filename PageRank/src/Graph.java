import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author yuezhang
 *
 */
public class Graph {

	private static Graph instance = null;
	
	private HashMap<String, Node> nodes;
	private HashMap<Node, HashSet<Node>> outboundLinks;
	private HashMap<Node, HashSet<Node>> inboundLinks;
	private int numLinks = 0;
	
	/**
	 * The private constructor.
	 */
	private Graph() {
		nodes = new HashMap<>();
		outboundLinks = new HashMap<>();
		inboundLinks = new HashMap<>();
	}
	
	/**
	 * @return The graph instance.
	 */
	public static Graph getInstance() {
		if (instance == null) {
			synchronized(Graph.class) {
				if (instance == null) {
					instance = new Graph();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Add a link to the link graph.
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean addLink(Node from, Node to) {
		if (from.equals(to)) {
			return false;
		}
		addNode(from);
		addNode(to);
		
		if (outboundLinks.get(from).contains(to)) {
			return false;
		}	
		outboundLinks.get(from).add(to);
		inboundLinks.get(to).add(from);
		numLinks++;
		return true;
	}
	
	/**
	 * Add a node to the graph.
	 * @param node
	 * @return
	 */
	public boolean addNode(Node node) {
		if (nodes.containsValue(node)) {
			return false;
		}
		nodes.put(node.getUrl(), node);
		if (!outboundLinks.containsKey(node)) {
			outboundLinks.put(node, new HashSet<Node>());
		}
		if (!inboundLinks.containsKey(node)) {
			inboundLinks.put(node, new HashSet<Node>());
		}
		return true;
	}
	
	/**
	 * @return the nodes
	 */
	public HashMap<String, Node> getNodes() {
		return nodes;
	}

	/**
	 * @return the outboundLinks
	 */
	public HashSet<Node> getOutboundLinks(Node node) {
		return outboundLinks.get(node);
	}
	
	
	/**
	 * Return a String representation of the node's outbound links.
	 * @param node
	 * @return
	 */
	public String outboundLinksToString(Node node) {
		StringBuilder sb = new StringBuilder();
		Iterator<Node> it = getOutboundLinks(node).iterator();
		while (it.hasNext()) {
			sb.append(it.next().getUrl() + "\t");
		}
		sb.substring(0, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * @return the inboundLinks
	 */
	public HashSet<Node> getInboundLinks(Node node) {
		return inboundLinks.get(node);
	}

	/**
	 * @return the numLinks
	 */
	public int getNumLinks() {
		return numLinks;
	}

	/**
	 * @return the numNode
	 */
	public int getNumNode() {
		return nodes.size();
	}

}
