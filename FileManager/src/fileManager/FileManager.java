package fileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Get all files from file and send them to file parser for parsing
 */
public class FileManager {
	public static void main(String[] args) throws IOException {
		String inputDirectory = args[0];
		String outputDirectory = args[1];
		
		File filesToRead = new File(inputDirectory);
		if (!filesToRead.isDirectory()) {
			System.out.println("Invalid directory");
			return;
		}
		
		FileWriter writer = new FileWriter(outputDirectory, true);
		
		for (File fileToRead : filesToRead.listFiles()) {
			FileParser fileParser = new FileParser(fileToRead);
			fileParser.parse();
			writer.write(fileParser.wordOccurenceToString());
			writer.write(fileParser.outLinksToString());
		}
		
	}

}
