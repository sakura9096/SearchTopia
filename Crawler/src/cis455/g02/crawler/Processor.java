package cis455.g02.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cis455.g02.storage.StoreWrapper;

public class Processor {
	
	private StoreWrapper db;
	private String outputDir;

	public Processor(StoreWrapper db, String output) {
		this.db = db;
		this.outputDir = output;
	}
	
	
	/*
	 * Download the page and save to the database
	 */
	public void download(String url) {
		HttpClient client = new HttpClient(url);
		client.executeGet();
		long lastChecked = System.currentTimeMillis();
		String type = client.getContentType();
		if (type.equals("text/html")) return;
		String body = client.getBody();
		this.db.putCrawledURL(url, lastChecked);
	}
	
	
	/*
	 * Extract all the links in the page 
	 */
	public List<String> extract(String url) {
		List<String> links = new ArrayList<String>();
		Document doc = null;
		try {
			url = normalize(url);
			doc = Jsoup.connect(url).get();
			Elements eles = doc.select("a[href]");
			for (Element e: eles) {
				String link = e.attr("abs:href");
				links.add(link);
			}
			return links;
		} catch (IOException e) {
			
		}
		return links;
	}
	
	public String normalize(String url) {
		if (!url.toLowerCase().startsWith("http") && !url.toLowerCase().startsWith("https")) {
			return "http://" + url;
		} else {
			return url;
		}
	}
	

}
