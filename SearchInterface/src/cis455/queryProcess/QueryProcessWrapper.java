package cis455.queryProcess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryProcessWrapper {
	
	public List<String> getQueryResult (String query) {
		
		String[] queryList = query.split("\\s+");
		List<String> finalQueryList = new ArrayList<String>();
		for (int i = 0; i < queryList.length; i++) {
			String word = queryList[i].toLowerCase().trim();
			if (!StopList.contains(word)) {
				if (isWord(word)) {
					word = Stemmer.getString(word);
				}
				finalQueryList.add(word);
			}
		}
		
		if (finalQueryList.size() == 1) {
			return this.serachOneWord(finalQueryList.get(0));
		} else if (finalQueryList.size() == 2) {
			return this.searchTwoWords(finalQueryList.get(0), finalQueryList.get(1));
		} else if (finalQueryList.size() >= 3) {
			return this.searchThreeWords(finalQueryList.get(0), finalQueryList.get(1), finalQueryList.get(2));
		}
		
		
		return finalQueryList;
	}
	
	
	/*
	 * Check if a word is all letter so that we can use stemmer
	 */
	public boolean isWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isLetter(word.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	
	public List<String> serachOneWord(String word1) {
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<TFIDFURLWrapper> fancyReturns = database.getURLsFromFacnyBarrel(word1);
		List<String> result = new ArrayList<String>();
		if (fancyReturns.size() >= 100) {
			for (int i = 0; i < 100; i++) {
				result.add(fancyReturns.get(i).url);
			}
			return result;
		} else {
			for (int i = 0; i < fancyReturns.size(); i++) {
				result.add(fancyReturns.get(i).url);
			}
			List<TFIDFURLWrapper> normalReturns = database.getURLsFromNormalBarrel(word1);
			int min = Math.min(100 - fancyReturns.size(), normalReturns.size());
			for (int i = 0; i < min; i++) {
				result.add(normalReturns.get(i).url);
			}
			return result;
		}
	}
	
	public List<String> searchTwoWords(String word1, String word2) {
		String conbine = word1 + " " + word2;
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<TFIDFURLWrapper> fancyReturns = database.getURLsFromFacnyBarrel(conbine);
		List<String> result = new ArrayList<String>();
		if (fancyReturns.size() >= 100) {
			for (int i = 0; i < 100; i++) {
				result.add(fancyReturns.get(i).url);
			}
			return result;
		} else {
			for (int i = 0; i < fancyReturns.size(); i++) {
				result.add(fancyReturns.get(i).url);
			}
			List<TFIDFURLWrapper> normalReturns = database.getURLsFromNormalBarrel(conbine);
			
			if (normalReturns.size() >= 100 - fancyReturns.size()) {
				int leave = 100 - fancyReturns.size();
				for (int i = 0; i < leave; i++) {
					result.add(normalReturns.get(i).url);
				}
				return result;
			} else {
				for (int i = 0; i < normalReturns.size(); i++) {
					result.add(normalReturns.get(i).url);
				}
				List<String> list1 = this.serachOneWord(word1);
				List<String> list2 = this.serachOneWord(word2);
				List<String> list12 = new ArrayList<String>();
				list12.addAll(list1);
				list12.addAll(list2);
				int min = Math.min(100 - fancyReturns.size() - normalReturns.size(), list12.size());
				for (int i = 0; i < min; i++) {
					result.add(list12.get(i));
				}
				return result;
			}
			
		}
	}
	
	public List<String> searchThreeWords(String word1, String word2, String word3) {
		String combine1 = word1 + " " + word2;
		String combine2 = word2 + " " + word3;
		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		Set<TFIDFURLWrapper> topSet11 = new HashSet<TFIDFURLWrapper>(database.getURLsFromFacnyBarrel(combine1));
		Set<TFIDFURLWrapper> topSet12 = new HashSet<TFIDFURLWrapper>(database.getURLsFromFacnyBarrel(word3));
		topSet11.retainAll(topSet12);
		Set<TFIDFURLWrapper> topSet21 = new HashSet<TFIDFURLWrapper>(database.getURLsFromFacnyBarrel(combine2));
		Set<TFIDFURLWrapper> topSet22 = new HashSet<TFIDFURLWrapper>(database.getURLsFromFacnyBarrel(word1));
		topSet21.retainAll(topSet22);
		Set<TFIDFURLWrapper> topSet1 = new HashSet<TFIDFURLWrapper>(database.getURLsFromFacnyBarrel(word2));
		topSet1.retainAll(topSet12);
		topSet1.retainAll(topSet22);
		Set<TFIDFURLWrapper> topSet = new HashSet<TFIDFURLWrapper>();
		topSet.addAll(topSet11);
		topSet.addAll(topSet21);
		topSet.addAll(topSet1);
		List<String> result = new ArrayList<String>();
		if (topSet.size() >= 100) {
			for (TFIDFURLWrapper w: topSet) {
				result.add(w.url);
			}
			return result;
		} else {
			for (TFIDFURLWrapper w: topSet) {
				result.add(w.url);
			}
			List<String> l1 = this.searchTwoWords(word1, word2);
			List<String> l2 = this.searchTwoWords(word2, word3);
			l1.addAll(l2);
			int min = Math.min(100 - topSet.size(), l1.size());
			for (int i = 0; i < min; i++) {
				result.add(l1.get(i));
			}
			return result;
		}
		
	}
	
}
