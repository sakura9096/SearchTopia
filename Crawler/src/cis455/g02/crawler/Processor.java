package cis455.g02.crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import cis455.g02.storage.StoreWrapper;

public class Processor {
	
	private StoreWrapper db;
	private String outputDir;
	private double maxSize;
	private String selfId;
	private Detector detector;

	public Processor(StoreWrapper db, String output, double maxSize, int selfId) {
		this.db = db;
		this.outputDir = output;
		this.maxSize = maxSize;
		this.selfId = selfId + "";
	}
	
	
	/*
	 * Download the page and save to the database
	 */
	public void download(String url) {
		HttpClient client = new HttpClient(url);
		
		client.executeGet();
		String body = client.getBody();
		File file = new File(this.outputDir + this.selfId + "_" + System.currentTimeMillis());
		try {
			file.createNewFile();
			PrintWriter out = new PrintWriter(file);
			out.println(url);
			out.print(body);
			out.flush();
			out.close();
			this.db.putCrawledURL(url, System.currentTimeMillis());
		} catch (IOException e) {
			System.out.println("error in writing!");
		}
		
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
				if (link.length() < 500 && !isTooDeep(link) && link.startsWith("http")) links.add(link);
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
	
	
	public boolean isTooDeep(String url) {
		String[] parts = url.split("/");
		return parts.length > 5;
	}
	
	public boolean isEnglish(String body) {
		try {
			detector = DetectorFactory.create();
			detector.append(body);
			if ("en".equals(detector.detect())) return true;
			else return false;
		} catch (LangDetectException e1) {
			return false;
		}
		
	}
	

}
