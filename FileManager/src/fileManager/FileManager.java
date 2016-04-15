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
		String anchorDirectory = args[2];
		
		File filesToRead = new File(inputDirectory);
		if (!filesToRead.isDirectory()) {
			System.out.println("Invalid directory");
			return;
		}
		
		FileWriter outWriter = new FileWriter(outputDirectory, true);
		FileWriter anchorWriter = new FileWriter(anchorDirectory, true);
		
		
		for (File fileToRead : filesToRead.listFiles()) {
			FileParser fileParser = new FileParser(fileToRead);
			fileParser.parse();
			outWriter.write(fileParser.fancyHitMapToString());
			outWriter.write(fileParser.fancyPhraseHitMapToString());
			outWriter.write(fileParser.normalHitMapToString());
			outWriter.write(fileParser.normalPhraseHitMapToString());
			anchorWriter.write(fileParser.outLinksToString());
		}
		outWriter.flush();
		anchorWriter.flush();
		
		outWriter.close();
		anchorWriter.close();
		
	}

}
