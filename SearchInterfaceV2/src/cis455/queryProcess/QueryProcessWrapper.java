package cis455.queryProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryProcessWrapper {
	
	private DynamoDBDatabase database;
	public QueryProcessWrapper () {
		database = DynamoDBDatabase.getInstance();
	}
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
		System.out.println("size" + finalQueryList.size());
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
//		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<ItemWrapper2> fancyReturns = database.getURLsFromFacnyBarrel(word1);
		Collections.sort(fancyReturns, Collections.reverseOrder());
		
		List<String> result = new ArrayList<String>();
		
		if (fancyReturns.size() >= 100) {
			
			for (int i = 0; i < 100; i++) {
				result.add(fancyReturns.get(i).originalUrl);
			}
			return result;
		} else {
			
			for (int i = 0; i < fancyReturns.size(); i++) {
				result.add(fancyReturns.get(i).originalUrl);
			}
			
			List<ItemWrapper2> normalReturns = database.getURLsFromNormalBarrel(word1);
		
			Collections.sort(normalReturns, Collections.reverseOrder());
			int min = Math.min(100 - fancyReturns.size(), normalReturns.size());
			for (int i = 0; i < min; i++) {
				result.add(normalReturns.get(i).originalUrl);
			}
			return result;
		}
	}
	
	public List<String> searchTwoWords(String word1, String word2) {
		String conbine = word1 + " " + word2;
//		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		List<ItemWrapper2> fancyReturns = database.getURLsFromFacnyBarrel(conbine);
		Collections.sort(fancyReturns, Collections.reverseOrder());
		List<String> result = new ArrayList<String>();
		
		if (fancyReturns.size() >= 100) {
			
			for (int i = 0; i < 100; i++) {
				result.add(fancyReturns.get(i).originalUrl);
				
			}
			
			return result;
		} else {
			
			for (int i = 0; i < fancyReturns.size(); i++) {
				result.add(fancyReturns.get(i).originalUrl);
			}
			List<ItemWrapper2> normalReturns = database.getURLsFromNormalBarrel(conbine);
			Collections.sort(normalReturns, Collections.reverseOrder());
			if (normalReturns.size() >= 100 - fancyReturns.size()) {
				
				int leave = 100 - fancyReturns.size();
				for (int i = 0; i < leave; i++) {
					result.add(normalReturns.get(i).originalUrl);
				}
				return result;
			} else {
				
				for (int i = 0; i < normalReturns.size(); i++) {
					result.add(normalReturns.get(i).originalUrl);
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
//		DynamoDBDatabase database = DynamoDBDatabase.getInstance();
		Set<ItemWrapper2> topSet11 = new HashSet<ItemWrapper2>(database.getURLsFromFacnyBarrel(combine1));
		Set<ItemWrapper2> topSet12 = new HashSet<ItemWrapper2>(database.getURLsFromFacnyBarrel(word3));
		topSet11.retainAll(topSet12);
		Set<ItemWrapper2> topSet21 = new HashSet<ItemWrapper2>(database.getURLsFromFacnyBarrel(combine2));
		Set<ItemWrapper2> topSet22 = new HashSet<ItemWrapper2>(database.getURLsFromFacnyBarrel(word1));
		topSet21.retainAll(topSet22);
		Set<ItemWrapper2> topSet1 = new HashSet<ItemWrapper2>(database.getURLsFromFacnyBarrel(word2));
		topSet1.retainAll(topSet12);
		topSet1.retainAll(topSet22);
		Set<ItemWrapper2> topSet = new HashSet<ItemWrapper2>();
		topSet.addAll(topSet11);
		topSet.addAll(topSet21);
		topSet.addAll(topSet1);
		List<ItemWrapper2> topList = new ArrayList<ItemWrapper2>(topSet);
		Collections.sort(topList, Collections.reverseOrder());
		
		List<String> result = new ArrayList<String>();
		if (topSet.size() >= 100) {
			for (ItemWrapper2 w: topList) {
				result.add(w.originalUrl);
			}
			return result;
		} else {
			for (ItemWrapper2 w: topList) {
				result.add(w.originalUrl);
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
