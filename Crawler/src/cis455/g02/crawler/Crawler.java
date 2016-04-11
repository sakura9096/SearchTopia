package cis455.g02.crawler;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.cybozu.labs.langdetect.LangDetectException;

import cis455.g02.storage.StoreWrapper;

public class Crawler implements Runnable {
	String startUrl;
	double maxSize;
	Map<Integer, String> workersIP;
	String selfIP;
	int selfID;
	String outputDir;
	Frontier frontier;
	AtomicLong count;
	AtomicLong backup;
	long maxCount;
	StoreWrapper db;
	Map<String, Integer> hostCount;
	Set<String> seedHost;
	//RobotChecker robotChecker;
	String currentUrl;
	Processor processor;
	Map<Integer, BigInteger> rangeMap;
	int allWorkersSize;
	Map<String, Long> crawTimeMap;
	Map<String, Robot> robotMap;

	
	public Crawler(double maxSize, AtomicLong count, AtomicLong backup,
			Frontier frontier, Map<Integer, String> workersIP, 
			String selfIP, int selfID, String outputDir, String dbDir, long maxCount, 
			Set<String> seedHost, Map<String, Integer> hostCount) {

		this.maxSize = maxSize;
		this.workersIP = workersIP;
		this.allWorkersSize = this.workersIP.size() + 1;
		this.selfIP = selfIP;
		this.selfID = selfID;
		this.outputDir = outputDir;
		this.frontier = frontier;
		this.count = count;
		this.backup = backup;
		this.maxCount = maxCount;
		this.db = StoreWrapper.getInstance(dbDir);
		this.hostCount = hostCount;
		this.seedHost = seedHost;
		this.robotMap = new HashMap<String, Robot>();
		this.crawTimeMap = new HashMap<String, Long>();
		this.processor = new Processor(this.db, outputDir, this.maxSize, this.selfID);
		String min = "0000000000000000000000000000000000000000";
		String max = "ffffffffffffffffffffffffffffffffffffffff";
		BigInteger maxBigInt = new BigInteger(max, 16);
		BigInteger minBigInt = new BigInteger(min, 16);
		this.rangeMap = new ConcurrentHashMap<Integer, BigInteger>();
		
		BigInteger interval = maxBigInt.divide(new BigInteger(Integer.toString(this.allWorkersSize)));
		for (int i = 0; i < this.allWorkersSize; i++) {
			this.rangeMap.put(i, minBigInt.add(interval));
			minBigInt = minBigInt.add(interval);
		}
		
	}

	@Override
	public void run() {
		try {
			while (true) {
				synchronized (frontier) {
					while (frontier.size() == 0) {
						System.out.println("frontier is size 0");
						frontier.wait(10000);
						if (count.get() >= maxCount) {
							return;
						}
					}
				}
				
				synchronized (frontier) {
					startUrl = frontier.deQueue();
					frontier.notifyAll();
				}
				System.out.println("dequeue fron frontier: " + startUrl);
				
			
				
				if (db.urlCrawled(startUrl) || startUrl.length() >= 500) {
					System.out.println(startUrl + " is crawled before! or is too long!");
					continue;
				}
				
				
				try {
					HttpClient tempUrl = new HttpClient(startUrl);
					String host = tempUrl.getHost();
					if (hostCount.containsKey(host)) {
						if (hostCount.get(host) > 500 && !seedHost.contains(host)) {
							System.out.println(host + "---this host has too many pages, but not a seed host!");
							continue;
						}
					}
					int who = chooseWorker(host);
					if (who == selfID) {
						System.out.println(startUrl + "---> is for worker ifself " + who);
						this.selfWork(host, startUrl);
					} else {
						System.out.println(startUrl + "---> is for worker " + who);
						sendToOthers(who, startUrl);
					}
					
				} catch (Exception e) {
					continue;
				}
				
				if (count.get() >= maxCount) {
					System.out.println("exceeds the max!!!!");
					return;
				}
			}
			
		} catch (Exception e) {
			
		}
	}
	
	
	
	public int chooseWorker(String host) {
		try {
			String hashValue = this.byteToHexString(this.getHashValue(host));
			BigInteger hashBigInt = new BigInteger(hashValue, 16);
			for (int i = 0; i < this.allWorkersSize; i++) {
				if (hashBigInt.compareTo(this.rangeMap.get(i)) < 0) {
					return i;
				}
			}
		} catch (Exception e) {
			return this.selfID;
		}
		return this.selfID;
	}
	
	public void selfWork(String host, String url) {
		if (processor == null) return;
		System.out.println("Do the selfwork: " + url);
		this.currentUrl = url;
		
		HttpClient client = new HttpClient(currentUrl);
		client.excuteHead();
		if (client.getStatusCode() == -1 || client.getStatusCode() >= 400) return;
		
	//	long lastChecked = System.currentTimeMillis();
		String type = client.getContentType();
		int contentLength = client.getContentLength();
		if (!"text/html".equals(type) || contentLength > this.maxSize * 1024 * 1024) return;
	    
		Long last = this.crawTimeMap.get(host);
		long lastC;
		if (last == null) {
			lastC = 0;
		} else {
			lastC = last;
		}
		if (!this.checkValid(url, lastC)) return;
		
		this.processor.download(currentUrl);
	    System.out.println(currentUrl + ":Downloading");
	    count.incrementAndGet();
	   
	    if (hostCount.containsKey(host)) {
	    	hostCount.put(host, hostCount.get(host) + 1);
	    } else {
	    	hostCount.put(host, 1);
	    }
	    List<String> links = this.processor.extract(currentUrl);
	    System.out.println(currentUrl + ":Extracting links");
	    if (frontier.size() < 1000000) {
	    	 for (String link: links) {
	  		   System.out.println(link + ":Extracting link");
	  		   synchronized (frontier) {	  			  
	  			   frontier.enQueue(link);
	  			   System.out.println(link + ":Pushing to the frontier");
	  			   frontier.notifyAll();
	  			   backup.incrementAndGet();
	  			  
	  		   }
	  	    }
	    }
	   
	}
	
	public void sendToOthers(int who, String url) {
		try {
			String worker = this.workersIP.get(who);
			String[] parts = worker.split(":", 2);
			if (parts.length != 2) return;
			String otherip = parts[0].trim();
			int otherport = Integer.parseInt(parts[1].trim());
			Socket s = new Socket(otherip, otherport);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.println(url);
			pw.flush();
			pw.close();
			s.close();
		} catch (Exception e) {
			
		}
	}
	
	
	/*
	 * Get the SHA-1 hash value given the key
	 */
	public byte[] getHashValue(String key) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(key.getBytes());
		byte[] result = md.digest();
		return result;
		
	}
	
	// reference: rgagnon.com/javadetails/java-0596.html
	public String byteToHexString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
	
	
	public boolean checkValid(String url, long lastCrawled) {
		Robot robot = getRobot(url);
		if (robot == null) return true;
		long currentTime = System.currentTimeMillis();
		int delay = robot.getDelay() * 1000;
		if (currentTime - lastCrawled < delay) {
			synchronized (frontier) {
				frontier.enQueue(url);;
			}
			return false;
		} 
		HttpClient urlInfo = new HttpClient(url);
		String path = urlInfo.getPath();
		Set<String> allows = robot.getAllows();
		Set<String> disallows = robot.getDisallows();
		for (String allow: allows) {
			if (path.startsWith(allow)) {
				if (allow.endsWith("/")) {
					return true;
				}
				if (allow.length() == path.length()) {
					return true;
				}
			}
		}
		for (String disallow: disallows) {
			if (path.startsWith(disallow)) {
				if (disallow.endsWith("/")) {
					return false;
				}
				if (disallow.length() == path.length()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Robot getRobot(String url) {
		HttpClient urlInfo = new HttpClient(url);
		String host = urlInfo.getHost();
		String protocol = urlInfo.getProtocol();
		Robot robot = this.robotMap.get(host);
		if (robot != null) return robot;
		Robot newRobot = null;
		try {
			newRobot = new Robot(host, protocol);
		} catch (Exception e) {
			newRobot = null;
		}
		return newRobot;
	}
		
		

}
