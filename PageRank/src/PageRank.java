import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * The PageRank calculation.
 *
 */
public class PageRank {


	public static void main(String[] args) {

		if (args.length < 3) {
			System.out.println("Usage: PageRank <input path> <output path> <sourceURL path>") ;
			return;
		}

		String inputFile = args[0];
		String outputDir = args[1];
		String dbDir = args[2];

		//		SourceURLWrapper sourceURL = SourceURLWrapper.getInstance(dbDir);

		try {
			runPageRank(inputFile, outputDir, dbDir);

		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	private static void runPageRank(String input, String outputDir, String dbDir) throws Exception {

		Configuration conf = new Configuration();

		Path outputPath = new Path(outputDir);
		outputPath.getFileSystem(conf).delete(outputPath, true);
		outputPath.getFileSystem(conf).mkdirs(outputPath);

		Path inputPath = new Path(outputPath, "input.txt");

		Path sourceURLFile = new Path(dbDir, "sourceURL.txt");
		int numNode = preprocessInputFile(new Path(input), inputPath, sourceURLFile);
		System.out.println("Num Node: " + numNode);
		//		int numNode = 1017446;
		int iteration = 1;
		//		double convergenceGoal = 0.01;


		while (iteration < 30) {

			Path jobOutPath = new Path(outputPath, String.valueOf(iteration));

			System.out.println("======================================");
			System.out.println("Iteration:    " + iteration);
			System.out.println("Input path:   " + inputPath);
			System.out.println("Output path:  " + jobOutPath);
			System.out.println("======================================");

			//			if (pageRankDrive(inputPath, jobOutPath, numNode, iteration) < convergenceGoal) {
			//				System.out.println("Reached convergence goal: " + convergenceGoal + ". Done.");
			//				break;
			//			}
			pageRankDrive(inputPath, jobOutPath, numNode, iteration);

			// update the input directory to be the output of last iteration
			inputPath = jobOutPath;
			iteration++;
		}

	}

	private static int preprocessInputFile(Path input, Path intermediate, Path dbDir) throws IOException {

		Set<String> srcURL = new HashSet<>();

		Configuration conf = new Configuration();
		FileSystem fs = input.getFileSystem(conf);

		OutputStream os = fs.create(intermediate);
		OutputStream outstream = fs.create(dbDir);

		FileStatus[] status = fs.listStatus(input);
		for (int i = 0; i < status.length; i++){
			LineIterator it = IOUtils.lineIterator(fs.open(status[i].getPath()), "UTF8");
			// go through the anchor file and extract all the source urls
			while (it.hasNext()) {
				String line = it.nextLine();
				List<String> str = Codec.decode(line);
				if (str.size() < 1) {
					continue;
				}			
				String source = str.get(0).trim();
				if (!source.equals("") && source.startsWith("http") && !srcURL.contains(source)) {
					srcURL.add(source);
					//					System.out.println(source);
					IOUtils.write(source + "\n", outstream);
				}		
			}
		}

		// give an initial pageRank to each url
		double initialPageRank = 1.0;

		for (int i = 0; i < status.length; i++){
			LineIterator it = IOUtils.lineIterator(fs.open(status[i].getPath()), "UTF8");
			// the input file format: [url	outlink1	outlink2	outlink3...]
			// the resulted file format: [url	pageRank	outlink1	outlink2 ...]
			while (it.hasNext()) {
				String line = it.nextLine();
				List<String> str = Codec.decode(line);
				if (str.size() < 1) {
					continue;
				}

				String source = str.get(0).trim();
				if (!source.startsWith("http")) {
					continue;
				}
				if (str.size() == 1) {				
					IOUtils.write(source + "\t" + initialPageRank + "\n", os);
					continue;
				}

				HashSet<String> outlinks = new HashSet<>();
				StringBuilder sb = new StringBuilder();
				for (int j = 1; j < str.size(); j++) {
					String token = str.get(j).trim();
					if (!token.equals("") && !outlinks.contains(token) 
							&& srcURL.contains(token) && !token.equals(source)) {
						sb.append(token + "\t");
					}
					outlinks.add(token);
				}
				IOUtils.write(source + "\t" + initialPageRank + "\t" + sb.toString() + "\n", os);		
			}
		}

		os.close();
		outstream.close();
		
		return srcURL.size();
	}


	private static double pageRankDrive(Path input, Path output, int numNode, int iteration) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {

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

		//		job.addCacheFile(new URI(sourceURLFile + "#sourceURL"));

		if (!job.waitForCompletion(true)) {
			System.out.println("Job " + iteration + " failed.");
		}

		// use Counter to measure the progress of job
		long convSum = job.getCounters().findCounter(PageRankReduce.Counter.PROGRESS).getValue();

		double convergence = ((double) convSum / PageRankReduce.SCALE) / (double) numNode;
		System.out.println("Convergence for iteration " + iteration + ": " + convergence);

		System.out.println("======================================");
		System.out.println("Num nodes:           " + numNode);
		System.out.println("Summed convergence:  " + convSum);
		System.out.println("Convergence:         " + convergence);
		System.out.println("======================================");

		return convergence;
	}


	/**
	 * A helper class for encoding and decoding the url.
	 */
	static class Codec {
		// Encodes a list of strings to a single string.
		public String encode(List<String> strs) {
			StringBuilder sb = new StringBuilder();
			for (String str : strs) {
				sb.append(str.replace("/", "//").replace("*", "/*"));
				sb.append('*');
			}
			return sb.toString();
		}

		// Decodes a single string to a list of strings.
		public static List<String> decode(String s) {
			StringBuilder sb = new StringBuilder();
			List<String> res = new ArrayList<String>();
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '/') {
					i++;
					sb.append(s.charAt(i));
				} else if (s.charAt(i) == '*') {
					res.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(s.charAt(i));
				}
			}
			return res;
		}
	}

}
