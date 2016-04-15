import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class WordsReduce extends Reducer<Text, Text, Text, Text> {
    
    public void reduce(Text key, Iterable<Text> values, Context context)
    throws IOException, InterruptedException {
//      int sum = 0;
//        for (IntWritable val : values) {
//            sum += val.get();
//        }
//    		HashMap <WordURLWrapper, ArrayList<HitInfo>> result = new HashMap <WordURLWrapper, ArrayList<HitInfo>> ();
//   		HashMap <WordURLWrapper, HitsWrapper> result = new HashMap <WordURLWrapper, HitsWrapper> ();
    	//	System.out.println("The key is:" +key.toString());
    		HashMap <String, HitsWrapper> result = new HashMap <String, HitsWrapper> ();
    		int numOfDocContainsKey = 0;
    		String oldURL = "";
    		int wordURLFreq = 0;
    		
    		for (Text hit: values) {
    			String hitString = hit.toString ();
    			StringTokenizer tokenizer = new StringTokenizer(hitString, "\t");
    			String currentURL = tokenizer.nextToken();
    	//		System.out.println("The next token is:" + currentURL);
    			String secondToken = tokenizer.nextToken();
    	//		System.out.println("Second token is:" + secondToken);
    			HitInfo currentHitInfo = new HitInfo (tokenizer.nextToken(), tokenizer.nextToken());
 //   			WordURLWrapper wuw = new WordURLWrapper (key.toString(), currentURL, tokenizer.nextToken());
    			
    			if (!oldURL.equals(currentURL)) {
    				wordURLFreq = 1;
    				numOfDocContainsKey ++;
    				oldURL = currentURL;
    				
    				ArrayList <HitInfo> currList = new ArrayList<HitInfo> ();
    				currList.add(currentHitInfo);
    				
    				String maxFrequency = tokenizer.nextToken();
//    				System.out.println("maxFrequency token is:" + maxFrequency);
    				HitsWrapper hitsWrapper = new HitsWrapper (wordURLFreq, currList, maxFrequency);   				
    				result.put(currentURL, hitsWrapper);
    				
    			} else {
    				wordURLFreq ++;
    				result.get(currentURL).addHitsInfo(currentHitInfo);
  //  				result.get().setWordFrequency(wordURLFreq);
    			}
    		}
    		
    		for (String url: result.keySet()) {
  //  			String wordURL = wuwrapper.getWord() + "\t" + wuwrapper.getUrl();
    			String wordURL = key.toString() + "\t" + url;
    			Text reducedKey = new Text ();
    			reducedKey.set(wordURL);
    			HitsWrapper hitsWrapper = result.get (url);
    			
    			double idf = Math.log10 (1000000/(double) numOfDocContainsKey);
    			double tf = 0.5 + 0.5 * hitsWrapper.getWordFrequency() / Integer.parseInt(hitsWrapper.getMaxFrequency());
    			
    			double tfidf = idf * tf;
    			String reducedValue = tfidf + "\t";
    			for (HitInfo hitInfo: hitsWrapper.getHitsInfo()) {
    				reducedValue += hitInfo.getPosition() + "\t";
    			}
    			
    			context.write(new Text(wordURL), new Text (reducedValue));
    		}
    		
    		
//       context.write(key, new IntWritable(sum));
    }
}
