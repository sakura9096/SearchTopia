package cis455.g02.crawler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import cis455.g02.storage.StoreWrapper;
/**
 * CrawlerMain is responsible for start the server, and start each thread crawler and join all the crawlers when 
 * finish crawling
 * @author Linjie
 *
 */
public class CrawlerMain {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		if (args.length != 9) {
			System.err.println("need 9 arguments: maxSize, numThread, selfFileDir, workersFileDir, output, countMax, dbDir, seedsFileDir, profileDir");
			return;
		}
		try {
			DetectorFactory.loadProfile(args[8]);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			System.out.println("error in loading profiles");
			return;
		}
		AtomicLong backup = new AtomicLong();
		backup.set(0);
		String dbDir = args[6];
		ConcurrentLinkedQueue<String> frontier = new ConcurrentLinkedQueue<String>();
		double maxSize = Double.parseDouble(args[0]);
		int threadsSize = Integer.parseInt(args[1]);
		long maxCount = Long.parseLong(args[5]);
		
		StoreWrapper db = StoreWrapper.getInstance(dbDir);
		String outputDir = args[4];
		if (!outputDir.endsWith("/")) outputDir = outputDir + "/";
		boolean fromZero = true;
		
		//get worker's ID and IP
		Map<Integer, String> workerIP = new ConcurrentHashMap<Integer, String>();
		File file = new File(args[3]);
		Scanner in = new Scanner(file);
		String line = "";
		String[] parts = null;
		while(in.hasNextLine()){
			line = in.nextLine();
			parts = line.split("\\s+", 2);
			if (parts.length != 2) continue;
			workerIP.put(Integer.parseInt(parts[0].trim()), parts[1].trim());
		}
		in.close();
		System.out.println("get worker's ID and IP: " + workerIP.size());

		// get self ID and IP
		file = new File(args[2]);
		in = new Scanner(file);
		line = in.nextLine();
		parts = line.split("\\s+", 2);
	
		int selfID = Integer.parseInt(parts[0].trim());;
		String selfIP = parts[1].trim();
		in.close();
		int port = Integer.parseInt(selfIP.split(":")[1]);
		
		System.out.println("get self ID: " + selfID);
		System.out.println("get self IP: " + selfIP);
		System.out.println("get self port: " + port);
		
		
		Server server = new Server(port, backup, dbDir, frontier);
		Thread thread = new Thread(server);
		thread.start();
		System.out.println("start the server to listen: ");

		//get frontier from database
		System.out.println("---------------get frontier from database------------");
		List<String> frontierOfDB = db.getAllFrontiers();
			
		if(frontierOfDB !=null && !frontierOfDB.isEmpty()){
			System.out.println("There is something in the database frontier!!");
			fromZero = false;
			frontier.addAll(frontierOfDB);
		}
		
		//get seedHost
	   Set<String> seedHost = new ConcurrentSkipListSet<String>();
	   file = new File(args[7]);
	   in = new Scanner(file);
	   while(in.hasNextLine()){
		 line = in.nextLine();
		 parts = line.split("\\s+", 2);
		 if (parts.length != 2) continue;
		 seedHost.add(parts[0].trim());
		 frontier.offer(parts[1].trim());
		}
	   in.close();
	   System.out.println("get seedHost: " + seedHost.size());
	   Map<String,Integer> hostCount = new ConcurrentHashMap<String,Integer>();
	   Thread.sleep(30000);
	
	 
	   AtomicLong count = new AtomicLong();
	   
	   count.set(0);
	   
	//   RobotChecker checker = new RobotChecker();
		
	   // start the crawlers
	  
	   Thread[] threadsCrawler = new Thread[threadsSize];
	   for(int i = 0; i < threadsSize;i++){
		  threadsCrawler[i] = new Thread(new Crawler(maxSize, count, backup, frontier, workerIP, selfIP, selfID, outputDir, dbDir, maxCount, seedHost, hostCount), selfID + "-Crawler" + i);
		  threadsCrawler[i].start();
	   }
	   System.out.println("start all crawlers-----------------");
	   for(Thread t: threadsCrawler){
			// join the crawler
			t.join();
			
	   }
	   System.out.println("join all crawlers-----------------");
	   thread.join();
	   System.out.println("----------------------------------The end-----------------------------------");
	   db.close();
	   return;
	}
	

}
