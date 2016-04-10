package cis455.g02.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import cis455.g02.storage.StoreWrapper;

public class Frontier {
	
	private static Frontier instance;
	private List<String> queue;
	private StoreWrapper db;
	private AtomicLong backup;
	
	
	public Frontier(AtomicLong backup, String dbDir) {
		this.queue = new ArrayList<String>();
		this.db = StoreWrapper.getInstance(dbDir);
		this.backup = backup;
	}
	
	public static Frontier getInstance(AtomicLong backup, String dbDir) {
		if (instance == null) {
			instance = new Frontier(backup, dbDir);
		} 
		return instance;
	}
	
	public void setQueue(List<String> q) {
		this.queue = q;
	}
	
	public  List<String> getQueue() {
		return this.queue;
	}
	
	public boolean contains(String str) {
		return this.queue.contains(str);
	}
	
	public String deQueue() {
		return this.queue.remove(0);
	}
	
	public void enQueue(String url) {
		this.queue.add(url);
		
		if (this.backup.get() > 10000 && this.backup.get() < 1000000) {
			List<String> origin = db.getAllFrontiers();
			for (String str: origin) {
				db.deleteFrontierURL(str);
			}
			db.addAllFroniters(new ArrayList<String>(this.queue));
			this.backup.set(0);
		}
	}
	
	
	public void addAll(List<String> urls) {
		this.queue.addAll(urls);
	}
	
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}
	
	public int size() {
		return this.queue.size();
	}
	
	
	
	
	

}
