import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class WordsMap extends Mapper<LongWritable, Text, Text, Text> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    private Text emitKey = new Text();
//    private String pattern= "^[a-z][a-z0-9]*$";
    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        String [] lineInfo = line.split("\t");
//        if (Integer.parseInt(lineInfo[3]) >= 4) {
//        		emitKey.set(tokenizer.nextToken() + ":1");
//        } else {
//        		emitKey.set(tokenizer.nextToken() + ":2");
//        }
        emitKey.set(tokenizer.nextToken());
//        String lowerCaseKey = emitKey.toString().toLowerCase();
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
//            String lowerCaseKey = word.toString().toLowerCase();
  //          if (stringWord.matches(pattern)){
             context.write(new Text(emitKey), word);    
        }
    }
}
