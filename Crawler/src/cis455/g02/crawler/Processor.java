package cis455.g02.crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
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

/**
 * Processor is responsible for downloading pages and extract links
 * @author Linjie
 *
 */
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
		String fileDir = this.outputDir + this.hashToString(url);
	//	System.out.println(fileDir);
		File file = new File(fileDir);
	//	System.out.println(this.outputDir + this.selfId + "_" + System.currentTimeMillis());
		try {
			if (file.exists() && file.isFile()) {
				this.db.putCrawledURL(url, System.currentTimeMillis());
				return;
			}
			file.createNewFile();
			PrintWriter out = new PrintWriter(file);
			out.println(url);
			out.print(body);
			out.flush();
			out.close();
			this.db.putCrawledURL(url, System.currentTimeMillis());
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
				int index = link.indexOf("#");
		  		if (index != -1) {
		  			link = link.substring(0, index);
		  		}
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
	
	private String hashToString(String s){

		try{
			MessageDigest m = MessageDigest.getInstance("SHA-1");
			m.update(s.getBytes());
			byte byteData[] = m.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		}catch(Exception e){
			return System.currentTimeMillis() + "";
		}
	}
	

}
