import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * The Reducer for PageRank MapReduce work.
 * The input is the intermediate key-value pairs from PageRankMap,
 * and the output format is: [url	pageRank	url1	url2...]
 *
 */
public class PageRankReduce extends Reducer<Text, Text, Text, Text> {

	public static final double DAMPING_FACTOR = 0.85;
	public static final double SCALE = 1000.0;
	public static String NUM_NODES = "pagerank.numnodes";
	private int numNodes;

	
	public static enum Counter {
		PROGRESS
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		numNodes = context.getConfiguration().getInt(NUM_NODES, 0);
	}

	private Text outValue = new Text();

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		double pageRankSum = 0;	
		String sourceNode = null;
		double originPageRank = 0;
		String outlinks = null;
		
		for (Text textValue : values) {
			String val = textValue.toString().trim();
			if (val != null && !val.equals("")) {
				if (!val.startsWith("PR:")) {
//					sourceNode = graph.getNodes().get(key.toString());
					sourceNode = key.toString();
					String pr = val.split("\t")[0];
					originPageRank = Double.valueOf(pr);
					outlinks = val.substring(pr.length(), val.length()).trim();
				} else if (val.startsWith("PR:")){
					String[] vals = val.split("\t");
					pageRankSum += Double.valueOf(vals[0].substring(3, vals[0].length()));
				}
			}
			
		}
//		System.out.println("PageRankSum: " + pageRankSum);
		if (sourceNode != null) {
			
			double dampingTerm = ((1.0 - DAMPING_FACTOR) / (double) numNodes);
			double newPageRank = dampingTerm + (DAMPING_FACTOR * pageRankSum);
//			double delta = sourceNode.getPageRank() - newPageRank;
			double delta = originPageRank - newPageRank;

//			sourceNode.setPageRank(newPageRank);
//			if (graph.getOutboundLinks(sourceNode).size() > 0) {
//				outValue.set(newPageRank + "\t" + graph.outboundLinksToString(sourceNode));
//			} else {
//				outValue.set(newPageRank + "");
//			}
//			context.write(new Text(sourceNode.getUrl()), outValue);
//			
			
			outValue.set(newPageRank + "\t" + outlinks);
			context.write(new Text(sourceNode), outValue);
			
			// to check the convergence 
			int scaledDelta = Math.abs((int) (delta * SCALE));
			context.getCounter(Counter.PROGRESS).increment(scaledDelta);
		}
		
	}
}