import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
/**
 * This is the reduce class that takes the "word, url and tf" as input and output "word, url and tfidf".
 * @author fanglinlu
 *
 */
public class WordsReduce extends Reducer<Text, Text, Text, Text> {
    
    public void reduce(Text key, Iterable<Text> values, Context context)
    throws IOException, InterruptedException {

    		HashMap <String, HitsWrapper> result = new HashMap <String, HitsWrapper> ();

    		for (Text hit: values) {
    			String hitString = hit.toString ();
    			StringTokenizer tokenizer = new StringTokenizer(hitString, "\t");
    			try {
	    			String currentURL = tokenizer.nextToken();
	    			String secondToken = tokenizer.nextToken();
	    			
	    			if (!result.containsKey(currentURL)) {
	    				int wordURLFreq = 1;
    				
	    				String maxFrequency = secondToken;
	    				
	    				HitsWrapper hitsWrapper = new HitsWrapper (wordURLFreq, maxFrequency);   				
	    				result.put(currentURL, hitsWrapper);
	    				
	    			} else {
	    				HitsWrapper currentHitsWrapper = result.get(currentURL);
	    				currentHitsWrapper.increaseWordFrequency();
	    			}
    			} catch (Exception e) {
    				e.printStackTrace();
    				continue;
    			}
    			
    		}
    		
    		for (String url: result.keySet()) {
    			String wordURL = key.toString() + "\t" + url;
    			Text reducedKey = new Text ();
    			reducedKey.set(wordURL);
    			HitsWrapper hitsWrapper = result.get (url);
    			
    			double idf = Math.log10 (1017446/(double) result.keySet().size());
    			double tf = 0.5 + 0.5 * hitsWrapper.getWordFrequency() / Integer.parseInt(hitsWrapper.getMaxFrequency());
    			
    			double tfidf = idf * tf;
    			String reducedValue = tfidf + "";
    			
    			context.write(new Text(wordURL), new Text (reducedValue));
    		}

    }
}
