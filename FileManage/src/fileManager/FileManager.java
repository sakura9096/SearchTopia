package fileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
 * Get all files from file and send them to file parser for parsing
 */
public class FileManager {
	public static void main(String[] args) throws IOException {
		String inputDirectory = args[0];
		String outputDirectory = args[1];
		String anchorDirectory = args[2];
		
//		File fileToRead = new File(inputDirectory);
//		
//		FileParser parser = new FileParser(fileToRead);
//		parser.parse();
//		System.out.println(parser.fancyHitMapToString());
//		System.out.println(parser.normalHitMapToString());
//		System.out.println(parser.outLinksToString());
		
		File filesToRead = new File(inputDirectory);
		if (!filesToRead.isDirectory()) {
			System.out.println("Invalid directory");
			return;
		}
		
		
		FileWriter outWriter = new FileWriter(outputDirectory, true);
		FileWriter anchorWriter = new FileWriter(anchorDirectory, true);
		
		File[] fileToReadArray = filesToRead.listFiles();
		//File[] fileToReadArray = new File[]{filesToRead};
		Arrays.sort(fileToReadArray);
		
		for (int i = 0; i < fileToReadArray.length; i++) {
			FileParser fileParser = new FileParser(fileToReadArray[i]);
			try {
				fileParser.parse();
			} catch (Exception e) {
				 //TODO Auto-generated catch block
				continue;
			}
			System.out.println(fileToReadArray[i].getName());
//			fileParser.fancyHitMapToString(outWriter);
//			fileParser.fancyPhraseHitMapToString(outWriter);
//			fileParser.normalHitMapToString(outWriter);
//			fileParser.normalPhraseHitMapToString(outWriter);
			anchorWriter.write(fileParser.outLinksToString());
			anchorWriter.flush();
			
		}
		
		outWriter.close();
		anchorWriter.close();
	}

}
