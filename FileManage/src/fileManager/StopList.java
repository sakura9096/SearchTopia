package fileManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * Class for storing the stop list
 */
public class StopList {
	public final static String[] stopList = { "0", "a", "about", "above", "after", "again",
			"against", "all", "am", "an", "and", "any", "are", "aren't", "as",
			"at", "be", "because", "been", "before", "being", "below",
			"between", "both", "but", "by", "can't", "cannot", "could",
			"couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
			"don't", "down", "during", "each", "few", "for", "from", "further",
			"had", "hadn't", "has", "hasn't", "have", "haven't", "having",
			"he", "he'd", "he'll", "he's", "her", "here", "here's", "hers",
			"herself", "him", "himself", "his", "how", "how's", "i", "i'd",
			"i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it",
			"it's", "its", "itself", "let's", "me", "more", "most", "mustn't",
			"my", "myself", "no", "nor", "not", "of", "off", "on", "once",
			"only", "or", "other", "ought", "our", "ours", "ourselves", "out",
			"over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
			"should", "shouldn't", "so", "some", "such", "than", "that",
			"that's", "the", "their", "theirs", "them", "themselves", "then",
			"there", "there's", "these", "they", "they'd", "they'll",
			"they're", "this", "those", "through", "to", "too", "under",
			"until", "up", "very", "was", "wasn't", "we", "we'd", "we'll",
			"we're", "we've", "were", "weren't", "what's", "when", "when's",
			"where's", "which", "while", "who", "who's", "whom", "why",
			"why's", "with", "won't", "would", "wouldn't", "you", "you'd",
			"you'll", "you're", "you've", "your", "your", "yourself",
			"yourselves" };
	
	public final static Set<String> stopSet = new HashSet<>(Arrays.asList(stopList));
	
	public static boolean contains(String word) {
		return stopSet.contains(word);
	}
	
	
	
}
