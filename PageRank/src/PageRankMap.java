import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

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
 */
public class PageRankMap extends Mapper<Text, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue  = new Text();

	private HashSet<String> srcURL;

	@Override
	protected void setup(Context context) throws FileNotFoundException, IOException, InterruptedException {
		srcURL = new HashSet<String>();
		try {
			String filename = "s3://new-anchor-file/intermediate/sourceURL.txt";
			Scanner in = new Scanner(new File(filename));

			while (in.hasNextLine()) {
				srcURL.add(in.nextLine());
			}
//			System.out.println("== Size of SourceURL:" +  srcURL.size());
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		// format: [sourceURL	initialPR	url1	url2...]
		//			getSrcURL();

		context.write(key, value);

		//		Node node = graph.getNodes().get(key.toString());
		//		
		//		if(node != null && graph.getOutboundLinks(node).size() > 0) {
		//			String[] tokens = value.toString().split("\t");
		//			
		//			double outboundPageRank = Double.valueOf(tokens[0]) /(double)graph.getOutboundLinks(node).size();
		//			
		//			// go through all the nodes and propagate PageRank to them
		//			Iterator<Node> it = graph.getOutboundLinks(node).iterator();
		//			while (it.hasNext()) {
		//				Node outlink = it.next();
		//				outKey.set(outlink.getUrl());
		//				outValue.set("PR:" + String.valueOf(outboundPageRank));
		//				// format: [outlink	rankPerOutlink]
		//				context.write(outKey, outValue);
		//			}
		//		} 
		//		//sink nodes -- spread their PR to every node
		//		else if (node != null && graph.getOutboundLinks(node).size() == 0) {
		//			
		//			String[] tokens = value.toString().split("\t");
		//			
		//			double outboundPageRank = Double.valueOf(tokens[0]) /(double)graph.getNumNode();
		//			
		//			Iterator<Node> it = graph.getNodes().values().iterator();
		//			while (it.hasNext()) {
		//				Node outlink = it.next();
		//				outKey.set(outlink.getUrl());
		//				outValue.set("PR:" + String.valueOf(outboundPageRank));
		//				// format: [outlink	rankPerOutlink]
		//				context.write(outKey, outValue);
		//			}
		//		}

		String val = value.toString().trim();
		if (val != null && !val.equals("")) {
			String[] tokens = val.split("\t");
			if (tokens.length > 1) {
				double outboundPageRank = Double.valueOf(tokens[0]) / (double)(tokens.length - 1);
				for (int i = 1; i < tokens.length; i++) {
					outKey.set(tokens[i]);
					outValue.set("PR:" + String.valueOf(outboundPageRank));
					context.write(outKey, outValue);
				}
			} else if (tokens.length == 1) { // sink nodes
				double outboundPageRank = Double.valueOf(tokens[0]) / (double)srcURL.size();
				Iterator<String> it = srcURL.iterator();
				while (it.hasNext()) {
					outKey.set(it.next());
					outValue.set("PR:" + String.valueOf(outboundPageRank));
					context.write(outKey, outValue);
				}
			}
		}			
	}
}
