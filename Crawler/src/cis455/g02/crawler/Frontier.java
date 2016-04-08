package cis455.g02.crawler;

import java.util.ArrayList;
import java.util.List;

import cis455.g02.storage.StoreWrapper;

public class Frontier {
	
	private static Frontier instance;
	private List<String> queue;
	private StoreWrapper db;
	
	
	public Frontier() {
		this.queue = new ArrayList<String>();
		this.db = StoreWrapper.getInstance("hard code");
	}
	
	public static Frontier getInstance() {
		if (instance == null) {
			instance = new Frontier();
		} 
		return instance;
	}
	
	public synchronized void setQueue(List<String> q) {
		this.queue = q;
	}
	
	public synchronized List<String> getQueue() {
		return this.queue;
	}
	
	public synchronized boolean contains(String str) {
		return this.queue.contains(str);
	}
	
	public synchronized void enQueue(String url) {
		this.queue.add(url);
		
		if (this.queue.size() > 5000) {
			List<String> origin = db.getAllFrontiers();
			for (String str: origin) {
				db.deleteFrontierURL(str);
			}
			db.addAllFroniters(new ArrayList<String>(this.queue));
		}
	}
	
	
	public synchronized void addAll(List<String> urls) {
		this.queue.addAll(urls);
	}
	
	
	
	
	

}
