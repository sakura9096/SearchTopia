package cis455.g02.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.InputStreamReader;

public class Server implements Runnable {
	private int port;
	private Frontier frontier;
	
	
	public Server(int port) {
		this.port = port;
		this.frontier = Frontier.getInstance();
	}
	
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(200000);
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String current = bufferReader.readLine();
					if (current != null) current = current.trim();
					if (!this.frontier.contains(current)) {
						System.out.println("server - current url: " + current);
						this.frontier.enQueue(current);
					}
					clientSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("-----------------Server shut down----------------");
					serverSocket.close();
					break;
				} catch (Exception e) {
					
				}
			}
		} catch (IOException e) {
			
			
		}
	}

}
