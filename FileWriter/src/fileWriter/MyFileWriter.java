package fileWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {
	public static void main(String[] args) throws IOException {
		String inputDirectory = args[0];
		String outputDirectory = args[1];
		
		File toRead = new File(inputDirectory);
		BufferedReader inputReader = new BufferedReader(new FileReader(toRead));
		
		FileWriter outWriter = new FileWriter(outputDirectory, true);
		
		String line = null;
		String currWord = null;
		PriorityQueue<WordHit> queue = new PriorityQueue<>(10, new MyComparator());
		while ((line = inputReader.readLine()) != null) {
			
		}
		
		
		
	}
	
	
	class MyComparator implements Comparator<WordHit> {
		public int compare(WordHit one, WordHit two) {
			if (one.tf_idf == two.tf_idf) return 0;
			return one.tf_idf > two.tf_idf ? -1 : 1;
		}
	}
}
