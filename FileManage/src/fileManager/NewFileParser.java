package fileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewFileParser {
	private Map<String, Double> normalHitMap;
	private Map<String, Double> fancyHitMap;
	private Map<String, Double> normalPhraseHitMap;
	private Map<String, Double> fancyPhraseHitMap;
	private List<String> outLinks;
	
	private File fileToRead;
	private String url;
	private String hostName;
	
	public NewFileParser(File fileToRead) {
		this.normalHitMap = new HashMap<>();
		this.fancyHitMap = new HashMap<>();
		this.normalPhraseHitMap = new HashMap<>();
		this.fancyPhraseHitMap = new HashMap<>();
		this.outLinks = new ArrayList<>();
		this.fileToRead = fileToRead;
	}
	
	public boolean isASCII(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) > 128) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isAllASCII(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) > 128) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * check if the word is valid or not
	 */
	public String isValidWord(String word) {
		if (word == null) return null;
		word = word.trim();
		word = this.removeQuote(word);
		word = word.trim();
		if (word.length() == 0 || !isASCII(word)) return null;
		word = word.toLowerCase();
		if (isWord(word)) {
			word = Stemmer.getString(word);
		}
		if (StopList.contains(word)) {
			return null;
		}
		return word;
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
	
	/*
	 * remove potential quote
	 */
	public String removeQuote(String word) {
		 int start = 0;
		 int end = word.length() - 1;
		 while (start < word.length() && word.charAt(start) == '\'') {
			 start++;
		 }
		 while (end >= 0 && word.charAt(end) == '\'') {
			 end--;
		 }
		 return start > end ? "" : word.substring(start, end + 1);
	}
	
	public void parseString(String str, int type) {
		if (str == null || str.length() == 0) {
			return;
		}
		String[] words = str.split("[^a-zA-Z0-9']+");
		String prev = null;//record the prev word
		for (int i = 0; i < words.length; i++) {
			String curr = isValidWord(words[i]);
			if (curr == null) {
				continue;
			}
			addToMap(curr, type, false);
			if (prev != null) {
				addToMap(prev + "\t" + curr, type, true);
			}
			prev = curr;
		}
	}
	
	public void addToMap(String word, int type, boolean isPhrase) {
		if (!isPhrase) {
			if (type == 0) {
				String key = "1:" + word;
				addToMapHelper(key, this.fancyHitMap);
			} else {
				String key = "2:" + word;
				addToMapHelper(key, this.normalHitMap);
			}
		} else {
			if (type == 0) {
				String key = "1:" + word;
				addToMapHelper(key, this.fancyPhraseHitMap);
			} else {
				String key = "2:" + word;
				addToMapHelper(key, this.normalPhraseHitMap);
			}
		}
	}
	
	/*
	 * Add to map helper
	 */
	public void addToMapHelper(String word, Map<String, Double> map) {
		if (map.containsKey(word)) {
			map.put(word, map.get(word) + 1);
		} else {
			map.put(word, 1.0);
		}
	}
	
	/*
	 * Parse the file
	 */
	public void parse() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileToRead));

		this.url = br.readLine();
		int index = url.indexOf("?");
		if (index != -1) {
			url = url.substring(0, index);
		}

		this.hostName = new URL(URLDecoder.decode(url, "UTF-8")).getHost();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String html = sb.toString();
		Document doc = Jsoup.parse(html, url);
		
		Elements metadata = doc.select("meta[name]");
		if (metadata != null) {
			StringBuilder metasb = new StringBuilder();
			for (Element element : metadata) {
				if (element.attr("name").equals("keyword") ||
						element.attr("name").equals("description")) {
					metasb.append(element.attr("content") + " ");
				}
			}
			if (!this.isAllASCII(metasb.toString())) {
				throw new IOException();
			}
			parseString(metasb.toString(), 0);
		}
		
		String title = doc.title();
		//System.out.println(title);
		if (!this.isAllASCII(title)) {
			throw new IOException();
		}
		parseString(title, 0);
		
		Element body = doc.body();
		if (body != null) {
			parseString(body.text(), 1);
		}
		
//		Elements links = doc.select("a[href]");
//		if (links != null) {
//			StringBuilder anchorsb = new StringBuilder();
//			for (Element link : links) {
//				String outLink = link.attr("abs:href").trim();
//				if (outLink.length() == 0) continue;
//				if (outLink.endsWith("/")) {
//					outLink = outLink.substring(0, outLink.length() - 1);
//				}
//				try {
//					outLink = URLDecoder.decode(outLink, "UTF-8");
//					index = outLink.indexOf("?");
//					if (index != -1) {
//						outLink = outLink.substring(0, index);
//					}
//				} catch (Exception e) {
//			
//				}
//				
//				this.outLinks.add(outLink);
//			}
//			System.out.println(anchorsb.toString());
//			parseString(anchorsb.toString(), 0);
//		}
		
		//remove phrase that appears only once
		removeFromPhraseMap();
		
		normalizeMap();
	}
	
	public String outLinksToString() {
		List<String> temp = new ArrayList<>();
		temp.add(this.url);
		for (String outLink : outLinks) {
			temp.add(outLink);
		}
		StringBuilder sb = new StringBuilder(Codec.encode(temp));
		sb.append('\n');
		return sb.toString();
	}
	
	/*
	 * for normalization
	 */
	public void normalizeMap() {
		normalizeMap(this.fancyHitMap);
		normalizeMap(this.fancyPhraseHitMap);
		normalizeMap(this.normalHitMap);
		normalizeMap(this.normalPhraseHitMap);
	}
	
	public void normalizeMap(Map<String, Double> map) {
		Double maxValue = Double.MIN_VALUE;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			maxValue = Math.max(maxValue, entry.getValue());
		}
		//System.out.println(maxValue);
		
		Double square_tf = 0.0;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			double value = entry.getValue();
			double newValue = 0.5 + 0.5 * (value / maxValue);
			//System.out.println(newValue);
			entry.setValue(newValue);
			square_tf += Math.pow(newValue, 2);
		}
		
		square_tf = Math.pow(square_tf, 0.5);
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			double value = entry.getValue();
			double newValue = value / square_tf;
			entry.setValue(newValue);
		}
	}
	
	public void write(FileWriter outWriter) throws IOException {
		this.writeToString(this.fancyHitMap, outWriter);
		this.writeToString(this.fancyPhraseHitMap, outWriter);
		this.writeToString(this.normalHitMap, outWriter);
		this.writeToString(this.normalPhraseHitMap, outWriter);
	}
	
	public void writeToString(Map<String, Double> map, FileWriter outWriter) throws IOException {
		List<String> temp = new ArrayList<>();
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			temp.clear();
			temp.addAll(Arrays.asList(entry.getKey(), this.url, String.valueOf(entry.getValue())));
			StringBuilder sb = new StringBuilder(Codec.encode(temp));
			sb.append("\n");
			outWriter.write(sb.toString());
			outWriter.flush();
		}
	}
		
		
	
	public void removeFromPhraseMap() {
		for (Iterator<Map.Entry<String, Double>> it = this.normalPhraseHitMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			if (entry.getValue().compareTo(1.0) == 0) {
				it.remove();
			}
		}
	
	}
		
		
	
	
	
	
}
