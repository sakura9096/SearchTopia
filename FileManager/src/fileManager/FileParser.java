package fileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Get file from resource and parse them
 */
public class FileParser {
	private Map<String, List<WordOccurence>> normalHitMap;
	private Map<String, List<WordOccurence>> fancyHitMap;
	private List<String> outLinks;
	private File fileToRead;
	private String url;
	private String hostName;
	int position; //record current read position
	int maxNormalHit;
	int maxFanceyHit;
	
	
	public FileParser(File fileToRead) {
		this.normalHitMap = new HashMap<>();
		this.fancyHitMap = new HashMap<>();
		this.outLinks = new ArrayList<>();
		this.fileToRead = fileToRead;
		this.position = 1;
	}
	
	/*
	 * Split a string and parse the word to wordMap
	 */
	public void parseString(String str, int type) {
		if (str == null || str.length() == 0) {
			return;
		}
		String[] words = str.split("[^a-zA-Z0-9']+");
		for (String wordToken : words) {
			addToMap(wordToken, type, position);
		}
	}
	
	
	/*
	 * Check if a word is all letter so that we can use stemmer
	 */
	public boolean isWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLetter(word.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Check if a word is all capital
	 */
	public boolean isCapital(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isUpperCase(word.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Add to word occurence map
	 */
	public void addToMap(String word, int type, int position) {
		if (word == null || word.length() == 0) return;
		word = word.trim();
		boolean isCapital = false;
		if (isCapital(word)) {
			isCapital = true;
		}
		word = word.toLowerCase();
		if (isWord(word)) {
			word = Stemmer.getString(word);
		}
		//if it's stop word, we don't handle it;
		if (StopList.contains(word)) {
			return;
		}
		WordOccurence wordOccurence = new WordOccurence(this.url, type, isCapital, this.position++);
		if (wordOccurence.getImportance() > 4) {
			String key = "1:" + word;
			addToMapHelper(key, wordOccurence, this.fancyHitMap);
		} else {
			String key = "2:" + word;
			addToMapHelper(key, wordOccurence, this.normalHitMap);
		}
		
	}
	
	public void addToMapHelper(String key, WordOccurence wordOccurence, Map<String, List<WordOccurence>> map) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<WordOccurence>());
		}
		map.get(key).add(wordOccurence);
	}
	
	/*
	 * Parse the file
	 */
	public void parse() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileToRead));

		this.url = br.readLine();
		System.out.println(this.url);
		this.hostName = new URL(URLDecoder.decode(url, "UTF-8")).getHost();
		String html = br.readLine();
		Document doc = Jsoup.parse(html, url);
		
		//read from url
		
		
		//read from metadata
		Elements metadata = doc.select("meta[name]");
		if (metadata != null) {
			StringBuilder metasb = new StringBuilder();
			for (Element element : metadata) {
				if (element.attr("name").equals("keyword") ||
						element.attr("name").equals("description")) {
					metasb.append(element.attr("content") + " ");
				}
				parseString(metasb.toString(), 2);
				
			}
		}
		
		//read from title
		String title = doc.title();
		parseString(title, 1);
		
		
		//read from body
		Element body = doc.body();
		if (body != null) {
			parseString(body.text(), 3);
		}
 		
		//then read from possible links and build the anchor file
		Elements links = doc.select("a[href]");
		if (links != null) {
			StringBuilder anchorsb = new StringBuilder();
			for (Element link : links) {
				String outLink = link.attr("abs:href").trim();
				if (outLink.length() == 0) continue;
				if (outLink.endsWith("/")) {
					outLink = outLink.substring(0, outLink.length() - 1);
				}
				String outHost = new URL(URLDecoder.decode(outLink, "UTF-8")).getHost();
				if (!outHost.equals(this.hostName)) {
					outLink = URLDecoder.decode(outLink, "UTF-8");
					this.outLinks.add(outLink);
				}
				
				String anchor = link.text().trim();
				anchorsb.append(anchor + " ");
			}
			parseString(anchorsb.toString(), 0);
		}
		
		for (String mapKeyWord: this.fancyHitMap.keySet()) {
			this.maxFanceyHit = Math.max(this.maxFanceyHit, this.fancyHitMap.get(mapKeyWord).size());
		}
		
		for (String mapKeyWord: this.normalHitMap.keySet()) {
			this.maxNormalHit = Math.max(this.maxNormalHit, this.normalHitMap.get(mapKeyWord).size());
		}
		
	}
	
	
	public String wordOccurenceToString(Map<String, List<WordOccurence>> map, int maxFrequency) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, List<WordOccurence>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<WordOccurence> value = entry.getValue();
			sb.append(key + " ");
			for (WordOccurence wordOccurence : value) {
				wordOccurence.setMaxFrequency(maxFrequency);
				sb.append(wordOccurence + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String outLinksToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.url + " ");
		for (String outLink : outLinks) {
			sb.append(outLink + " ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public String fancyHitMapToString() {
		return this.wordOccurenceToString(this.fancyHitMap, this.maxFanceyHit);
	}
	
	public String normalHitMapToString() {
		return this.wordOccurenceToString(this.normalHitMap, this.maxNormalHit);
	}
	
	
	
	
}
