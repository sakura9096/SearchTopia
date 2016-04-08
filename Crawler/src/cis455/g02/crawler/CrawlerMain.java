package cis455.g02.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cis455.g02.storage.StoreWrapper;

public class CrawlerMain {
	String selfIP; 
	List<String> workersIP;
	int numThread;
	int maxSize;
	String dbDir;
	StoreWrapper db;
	
	public CrawlerMain(String selfIP, List<String> workersIP, int numThread, int maxSize, String dbDir) {
		this.selfIP = selfIP;
		this.workersIP = new ArrayList<String>();
		this.maxSize = maxSize;
		this.dbDir = dbDir;
		this.db = StoreWrapper.getInstance(dbDir);
	}
	
	public void init() throws InterruptedException {
		List<String> frontiersDB = db.getAllFrontiers();
		Frontier frontier = Frontier.getInstance();
		if (frontiersDB != null && !frontiersDB.isEmpty()) {
			frontier.addAll(frontiersDB);
		}
		System.out.println("before crawling, set up frontier");
		Thread.sleep(50000);
	}
	

}
