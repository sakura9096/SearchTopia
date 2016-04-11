package fileManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * Class for storing the stop list
 */
public class StopList {
	public final static String[] stopList = {"a"};
	
	public final static Set<String> stopSet = new HashSet<>(Arrays.asList(stopList));
	
	public static boolean contains(String word) {
		return stopSet.contains(word);
	}
	
	
	
}
