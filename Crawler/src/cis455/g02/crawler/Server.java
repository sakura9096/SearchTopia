package cis455.g02.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import cis455.g02.storage.StoreWrapper;

import java.io.InputStreamReader;

public class Server implements Runnable {
	private int port;
	private AtomicLong backup;
	private ConcurrentLinkedQueue<String> frontier;
	private StoreWrapper db;
	
	
	public Server(int port, AtomicLong backup, String dbDir, ConcurrentLinkedQueue<String> frontier) {
		this.port = port;
		this.backup = backup;
		this.frontier = frontier;
		this.db = StoreWrapper.getInstance(dbDir);
	}
	
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
//			serverSocket.setSoTimeout(200000);
			while (true) {
				try {
					System.out.println("I am begin to accept new request!");
					Socket clientSocket = serverSocket.accept();
			//		System.out.println("accept client " + clientSocket.getLocalPort());
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String current = bufferReader.readLine();
					System.out.println("receive message: " + current);
					if (current != null) current = current.trim();
					int index = current.indexOf("#");
					if (index != -1) {
						current = current.substring(0, index);
					}
					if (!this.db.urlCrawled(current)) {
						System.out.println("server - current url: " + current);
						this.frontier.offer(current);
						backup.incrementAndGet();
						this.checkAndUpdate();
					}
					
					clientSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("-----------------Server shut down----------------");
					serverSocket.close();
					break;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			
			
		}
	}
	
	
	public void checkAndUpdate() {
		if (this.backup.get() > 5000 && this.backup.get() < 1000000) {
			List<String> origin = db.getAllFrontiers();
			for (String str: origin) {
				db.deleteFrontierURL(str);
			}
			db.addAllFroniters(new ArrayList<String>(this.frontier));
			this.backup.set(0);
		}
	}

}
