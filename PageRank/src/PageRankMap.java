

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * The Mapper for PageRank MapReduce work.
 * The input format is [url	pageRank	url1	url2...]
 * The output format is [url	pageRank	url1	url2...]
 * and  [url1	PR:pageRankSlice]
 * 		[url2	PR:pageRankSlice]
 * 		[url3	PR:pageRankSlice]
 * 		...
 * 
 * @author yuezhang
 *
 */
public class PageRankMap extends Mapper<Text, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue  = new Text();
	
	// the web link graph
	private Graph graph = Graph.getInstance();
	
	@Override
	protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		// format: [sourceURL	initialPR	url1	url2...]
		context.write(key, value);
		
		Node node = graph.getNodes().get(key.toString());
		
		if(node != null && graph.getOutboundLinks(node).size() > 0) {
			String[] tokens = value.toString().split("\t");
			
			double outboundPageRank = Double.valueOf(tokens[0]) /(double)graph.getOutboundLinks(node).size();
			
			// go through all the nodes and propagate PageRank to them
			Iterator<Node> it = graph.getOutboundLinks(node).iterator();
			while (it.hasNext()) {
				Node outlink = it.next();
				outKey.set(outlink.getUrl());
				outValue.set("PR:" + String.valueOf(outboundPageRank));
				// format: [outlink	rankPerOutlink]
				context.write(outKey, outValue);
			}
		} 
		//sink nodes -- spread their PR to every node
		else if (node != null && graph.getOutboundLinks(node).size() == 0) {
			
			String[] tokens = value.toString().split("\t");
			
			double outboundPageRank = Double.valueOf(tokens[0]) /(double)graph.getNumNode();
			
			Iterator<Node> it = graph.getNodes().values().iterator();
			while (it.hasNext()) {
				Node outlink = it.next();
				outKey.set(outlink.getUrl());
				outValue.set("PR:" + String.valueOf(outboundPageRank));
				// format: [outlink	rankPerOutlink]
				context.write(outKey, outValue);
			}
		}
	}
}
