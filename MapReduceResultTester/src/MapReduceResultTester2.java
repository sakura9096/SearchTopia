import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class MapReduceResultTester2 {
	
	private static Comparator <TFIDFURLWrapper> tfidfComparator = new Comparator <TFIDFURLWrapper> () {
		public int compare (TFIDFURLWrapper w1, TFIDFURLWrapper w2) {

			if (w1.tfidf - w2.tfidf < 0) {
				return -1;
			} else if (w1.tfidf - w2.tfidf > 0) {
				return 1;
			} else {
				return 0;
			}
			
		}
	};
	public static void main(String[] args) {
		try {
//			FileReader fileReader = new FileReader ("/Users/fanglinlu/Documents/classes/CIS555/hadoop-2.7.2/input/test-input");
//			FileReader fileReader = new FileReader ("/Users/fanglinlu/Documents/workspace/S3Test/output/Output");
			FileReader fileReader = new FileReader ("");
			BufferedReader br1 = new BufferedReader (fileReader);
			
			FileWriter fileWriter = new FileWriter ("outputTestResult4", true);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			
			HashMap<String, PriorityQueue<TFIDFURLWrapper>> hm = new HashMap <String, PriorityQueue<TFIDFURLWrapper>>();
//			bw.write(fileName + "\n");
//			bw.flush();
//			bw.close();
			
			String line = null;
			int i = 0;
//			while ((line = br1.readLine()) != null && i < 40000000) {
//				i++;
//			}
			while ((line = br1.readLine()) != null && i < 800000) {
				String[] lineInfo = line.split("\t");
				String word;
				String url;
				String tfIdf;
				if (lineInfo.length < 4) {
					word = lineInfo[0];
					url = lineInfo[1];
					tfIdf =lineInfo[2];
					
				} else {
					word = lineInfo[0] + "\t" + lineInfo[1];
					url = lineInfo[2];
					tfIdf = lineInfo[3];
				}
				
				double tfIdfValue = Double.parseDouble(tfIdf);
				
				TFIDFURLWrapper tuw = new TFIDFURLWrapper (tfIdfValue, url);
				if (hm.keySet().contains(word)) {
					hm.get(word).add(tuw);
				} else {
					PriorityQueue<TFIDFURLWrapper> pq = new PriorityQueue <TFIDFURLWrapper> (10, tfidfComparator);
					pq.add(tuw);
					hm.put(word, pq);
				}
	//			List<String> list = Codec.decode(line);
//		        String [] lineInfo = line.split("\t");
//		        if (Integer.parseInt(lineInfo[3]) >= 4) {
//		        		emitKey.set(tokenizer.nextToken() + ":1");
//		        } else {
//		        		emitKey.set(tokenizer.nextToken() + ":2");
//		        }
//		        Iterator<String> iter = list.iterator();
//		        System.out.println(iter.next());
////		        String lowerCaseKey = emitKey.toString().toLowerCase();
//		        while (iter.hasNext()) {
//		        		String nextToken = iter.next();
//		        		if (nextToken.length() > 0) {
////		                word.set(nextToken);
//		        			System.out.println(nextToken);
////		                  String lowerCaseKey = word.toString().toLowerCase();
//		    //          if (stringWord.matches(pattern)){
////		                context.write(new Text(emitKey), word); 
//		        		}
//		   
//		        }
	//			bw.write(line);
				i++;
			}
			
			for (String keyWord: hm.keySet()) {
				PriorityQueue<TFIDFURLWrapper> pq = hm.get(keyWord);
				while (!pq.isEmpty()) {
					TFIDFURLWrapper tfw = pq.poll();
					bw.write(keyWord + "\t" +  tfw.url + "\t" + tfw.tfidf + "\n");
				}
			}
			br1.close();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
}
