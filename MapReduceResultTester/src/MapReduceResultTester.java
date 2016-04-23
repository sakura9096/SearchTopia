import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//import org.apache.hadoop.io.Text;

public class MapReduceResultTester {

	public static void main(String[] args) {
		try {
//			FileReader fileReader = new FileReader ("/Users/fanglinlu/Documents/classes/CIS555/hadoop-2.7.2/input/test-input");
			FileReader fileReader = new FileReader ("newOutput");
			BufferedReader br1 = new BufferedReader (fileReader);
			
//			FileWriter fileWriter = new FileWriter ("outputTestResult", true);
//			BufferedWriter bw = new BufferedWriter (fileWriter);
			
			HashMap<String, TFIDFURLWrapper> hm = new HashMap <String, TFIDFURLWrapper>();
//			bw.write(fileName + "\n");
//			bw.flush();
//			bw.close();
			
			String line = null;
			int i = 0;
			
			while ((line = br1.readLine()) != null && i < 100) {
//				String[] lineInfo = line.split("\t");
//				String word = lineInfo[0];
//				String url = lineInfo[1];
//				String tfIdf =lineInfo[2];
//				double tfIdfValue = Double.parseDouble(tfIdf);
				
				List<String> list = Codec.decode(line);
//		        String [] lineInfo = line.split("\t");
//		        if (Integer.parseInt(lineInfo[3]) >= 4) {
//		        		emitKey.set(tokenizer.nextToken() + ":1");
//		        } else {
//		        		emitKey.set(tokenizer.nextToken() + ":2");
//		        }
		        Iterator<String> iter = list.iterator();
		        System.out.println(iter.next());
//		        String lowerCaseKey = emitKey.toString().toLowerCase();
		        while (iter.hasNext()) {
		        		String nextToken = iter.next();
		        		if (nextToken.length() > 0) {
//		                word.set(nextToken);
		        			System.out.println(nextToken);
//		                  String lowerCaseKey = word.toString().toLowerCase();
		    //          if (stringWord.matches(pattern)){
//		                context.write(new Text(emitKey), word); 
		        		}
		   
		        }
//				bw.write(line);
//				i++;
			}
			
			br1.close();
//			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
