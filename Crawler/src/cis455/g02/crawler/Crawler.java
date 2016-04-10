package cis455.g02.crawler;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
	RobotChecker robotChecker;
	String currentUrl;
	Processor processor;
	Map<Integer, BigInteger> rangeMap;
	int allWorkersSize;

	
	public Crawler(double maxSize, AtomicLong count, AtomicLong backup,
			Frontier frontier, Map<Integer, String> workersIP, 
			String selfIP, int selfID, String outputDir, String dbDir, long maxCount, 
			Set<String> seedHost, Map<String, Integer> hostCount, RobotChecker checker) {

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
		this.robotChecker = checker;
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
				
				while (!this.robotChecker.checkDelay(startUrl)) {
					synchronized (frontier) {
						frontier.enQueue(currentUrl);
						continue;
					}
				}
				
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
		System.out.println("Do the selfwork: " + url);
		this.currentUrl = url;
		this.robotChecker.getRobot(currentUrl);
	    this.robotChecker.updateCurrentTime(currentUrl);
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
	  			   if (this.robotChecker.checkValid(link)) {	
	  				   frontier.enQueue(link);
	  				   frontier.notifyAll();
	  				   backup.incrementAndGet();
	  			   }
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
		
		

}
