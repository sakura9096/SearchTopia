import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author yuezhang
 *
 */
public class PageRank {

	// the link graph of our corpus
	private static Graph graph;

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Usage: PageRank <input path> <output path>") ;
			return;
		}

		String inputFile = args[0];
		String outputDir = args[1];

		graph = Graph.getInstance();

		try {
			runPageRank(inputFile, outputDir, graph);

		} catch (Exception e) {			
			e.printStackTrace();
		}

	}

	private static void runPageRank(String input, String outputDir, Graph graph) throws Exception {

		Configuration conf = new Configuration();

		Path outputPath = new Path(outputDir);
		outputPath.getFileSystem(conf).delete(outputPath, true);
		outputPath.getFileSystem(conf).mkdirs(outputPath);

		Path inputPath = new Path(outputPath, "input.txt");

		int numNode = buildGraphAndInputFile(new Path(input), inputPath, graph);

		int iteration = 1;
		double convergenceGoal = 0.01;

		while (true) {

			Path jobOutPath = new Path(outputPath, String.valueOf(iteration));
			
			System.out.println("======================================");
			System.out.println("=  Iteration:    " + iteration);
			System.out.println("=  Input path:   " + inputPath);
			System.out.println("=  Output path:  " + jobOutPath);
			System.out.println("======================================");
			
			if (pageRankDrive(inputPath, jobOutPath, numNode, iteration) < convergenceGoal) {
				System.out.println("Reached convergence goal: " + convergenceGoal + ". Done.");
				break;
			}
			// update the input directory to be the output of last iteration
			inputPath = jobOutPath;
			iteration++;
		}

	}

	private static int buildGraphAndInputFile(Path input, Path intermediate, Graph graph) throws IOException {

		long startTime = System.currentTimeMillis();

		Configuration conf = new Configuration();
		FileSystem fs = input.getFileSystem(conf);
		
		int numNode = getNumNodes(input);
		// give an initial pageRank to each url
		double initialPageRank = 1.0 / (double) numNode;

		OutputStream os = fs.create(intermediate);
		LineIterator it = IOUtils.lineIterator(fs.open(input), "UTF8");

		// parse the input file one line at a time, generate the link graph
		// the input file format: [url	outlink1	outlink2	outlink3...]
		// the resulted file format: [url	pageRank	outlink1	outlink2 ...]
		while (it.hasNext()) {
			String line = it.nextLine();
			String[] tokens = line.split("\\s+");
			if (tokens.length < 1) {
				continue;
			}
			Node from = new Node(tokens[0]); // the source url
			from.setPageRank(initialPageRank);
			graph.addNode(from);
			// sink node
			if (tokens.length == 1) {
				IOUtils.write(from.getUrl() + "\t" + initialPageRank + "\n", os);
				continue;
			}
			HashSet<String> outlinks = new HashSet<>();
			for (int i = 1; i < tokens.length; i++) {
				String token = tokens[i];
				// ignore links pointing to the same outbound url
				if (!outlinks.contains(token)) {
					Node to = new Node(token);
					// remove self-links 
					if (!to.equals(from)) {
						graph.addLink(from, to);
					}
					outlinks.add(token);
				}	
			}
			IOUtils.write(from.getUrl() + "\t" + initialPageRank + "\t" + 
					graph.outboundLinksToString(from) + "\n", os);
		}
		os.close();
		System.out.println(String.format("Loaded %d nodes and %d links in %d ms", 
				graph.getNumNode(), graph.getNumLinks(), (System.currentTimeMillis()-startTime)));
		
		return graph.getNumNode();
	}


	private static double pageRankDrive(Path input, Path output, int numNode, int iteration) throws IOException, ClassNotFoundException, InterruptedException {

		Configuration conf = new Configuration();

		conf.setInt(PageRankReduce.NUM_NODES, numNode);
		
		Job job = Job.getInstance(conf, "PageRankJob" + iteration);
		
		job.setJarByClass(PageRank.class);
		job.setMapperClass(PageRankMap.class);
		job.setReducerClass(PageRankReduce.class);

		job.setInputFormatClass(KeyValueTextInputFormat.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		FileInputFormat.setInputPaths(job, input);
		FileOutputFormat.setOutputPath(job, output);

		if (!job.waitForCompletion(true)) {
			System.out.println("Job " + iteration + " failed.");
		}
		
		// use Counter to measure the progress of job
		long convSum = job.getCounters().findCounter(PageRankReduce.Counter.PROGRESS).getValue();
		
		double convergence = ((double) convSum / PageRankReduce.SCALE) / (double) numNode;
		System.out.println("Convergence for iteration " + iteration + ": " + convergence);
		
		System.out.println("======================================");
		System.out.println("=  Num nodes:           " + numNode);
		System.out.println("=  Summed convergence:  " + convSum);
		System.out.println("=  Convergence:         " + convergence);
		System.out.println("======================================");
		
		return convergence;
	}
	
	public static int getNumNodes(Path file) throws IOException {
		Configuration conf = new Configuration();

		FileSystem fs = file.getFileSystem(conf);

		return IOUtils.readLines(fs.open(file), "UTF8").size();
	}
	
}
