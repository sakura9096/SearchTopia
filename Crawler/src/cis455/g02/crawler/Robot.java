package cis455.g02.crawler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Robot, given a robot url, to retrieve the robots.txt, and put all the infomation into 
 * corresponding fields
 * @author Linjie Peng
 *
 */
public class Robot {
	private Set<String> disallowList;
	private Set<String> allowList;
	private HttpClient client;
	private int delay = 0;
	
	private boolean allowFlag;
	private boolean selfFlag;
	private boolean errorFlag;
	
	
	public Robot(String name) {
		String robotUrl = generateUrl(name);
		this.client = new HttpClient(robotUrl);
		client.executeGet();
		if (client == null || client.getStatusCode() >= 400) {
			this.errorFlag = true;
			return;
		}
		
		this.disallowList = new HashSet<String>();
		this.allowList = new HashSet<String>();
		
		if (client.getBody() == null) return;
		BufferedReader bf = new BufferedReader(new StringReader(client.getBody()));
		String line;
		
		try {
			while ((line = bf.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					if (this.selfFlag) break;
					this.allowFlag = false;
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				String[] parts = line.split(":", 2);
				if (parts.length > 1) {
					String key = parts[0].trim().toLowerCase();
					String value = parts[1].trim();
					switch (key) {
					case "user-agent":
						if (value.equals("cis455crawler")) {
							this.selfFlag = true;
							this.allowFlag = true;
							this.disallowList.clear();
							this.allowList.clear();
						} else if (value.equals("*")) {
							if (!this.selfFlag) {
								this.allowFlag = true;
							}
						}
						break;
					case "disallow":
						if (this.allowFlag) {
							this.disallowList.add(value);
						}
						break;
					case "allow":
						if (this.allowFlag) {
							this.allowList.add(value);
						}
						break;
					case "crawl-delay": 
						if (this.allowFlag) {
							this.delay = Integer.parseInt(value);
						}
						break;
					}
				}	
			}
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing robot.txt!");
			this.errorFlag = true;
			return;
		}
		
		
	}
	
	/*
	 * Generate the robot.txt url
	 */
	public String generateUrl(String name) {
		
		return "http://" + name + "/robots.txt";
			
	}
	
	/*
	 * Check if this robots.txt exsits and no error when parsing it
	 */
	public boolean isValidFile() {
		return !this.errorFlag;
	}
	
	/*
	 * In seconds (default 0);
	 */
	public int getDelay() {
		return this.delay;
	}
	
	/*
	 * Get the set of disallowed URLs
	 */
	public Set<String> getDisallows() {
		return this.disallowList;
	}
	
	/*
	 * Get the set of allowed URLs
	 */
	public Set<String> getAllows() {
		return this.allowList;
	}
}
