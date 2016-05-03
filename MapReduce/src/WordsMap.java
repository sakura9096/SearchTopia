import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

/**
 * This is a mapper class to take a text as input and emit "word-(url-tf)" pair.
 * @author fanglinlu
 *
 */
public class WordsMap extends Mapper<LongWritable, Text, Text, Text> {
    private final static IntWritable one = new IntWritable(1);
    private Text wordInfo = new Text();
    private Text emitKey = new Text();
    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    		String line = value.toString();
    		if (line.length() == 0) {
    			return;
    		}
        List<String> list = Codec.decode(line);

        Iterator<String> iter = list.iterator();
        emitKey.set(iter.next());

        while (iter.hasNext()) {
        		String nextToken = iter.next();
        		if (nextToken.length() > 0) {
                wordInfo.set(nextToken);
                context.write(new Text(emitKey), wordInfo); 
        		}
   
        }
    }
}
