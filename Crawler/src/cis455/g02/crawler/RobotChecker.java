package cis455.g02.crawler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RobotChecker
 * Provide methods to check if the crawling behavior satisfies the requirements of robots.txt
 * @author Linjie Peng
 *
 */
public class RobotChecker {
	private Map<String, Robot> robotMap;
	private Map<String, Long> crawTimeMap;
	
	
	public RobotChecker() {
		this.robotMap = new ConcurrentHashMap<String, Robot>();
		this.crawTimeMap = new ConcurrentHashMap<String, Long>();
	}
	
	/*
	 * Get the robot by host name, if no such a host name, add a host name and it's robot rules to the robot map
	 */
	public Robot getRobot(String url) {
	
		try {

			HttpClient urlInfo = new HttpClient(url);
			String host = urlInfo.getHost();
			
			Robot robot = null;
			if (this.robotMap.containsKey(host)) {
				robot = this.robotMap.get(host);
			} else {
				robot = new Robot(host);
				this.robotMap.put(host, robot);
				this.crawTimeMap.put(host, 0L);
			}

			if (robot.isValidFile()) {

				return robot;
			} else {

				return null;
			}
			 
			
		} catch (Exception e) {
			System.out.println(url);
			e.printStackTrace();
			
			System.out.println("Error in parsing the url");
			return null;
		}
	}
	
	/*
	 * Check if it meets the delay time specified in the robots.txt
	 */
	public boolean checkDelay(String url) {
		try {
			HttpClient urlInfo = new HttpClient(url);
			String host = urlInfo.getHost();
			if (this.robotMap.containsKey(host)) {
				Robot robot = this.robotMap.get(host);
				int delay = robot.getDelay() * 1000;
				if (delay == 0) return true;
				long lastTime = this.crawTimeMap.get(host);
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastTime >= delay) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();;
			System.out.println("Error in parsing the url");
			return false;
		}
	}
	
	/*
	 * Update the check time
	 */
	public void updateCurrentTime(String url) {
		try {
			HttpClient urlInfo = new HttpClient(url);
			String host = urlInfo.getHost();
			this.crawTimeMap.put(host, System.currentTimeMillis());
		} catch (Exception e) {
			
		}
	}
	
	/*
	 * Check if the url meets all the requirements in the responding robot.txt
	 */
	public boolean checkValid(String url) {
		try {
			HttpClient urlInfo = new HttpClient(url);
			String host = urlInfo.getHost();
			String path = urlInfo.getPath();
			Robot robot = getRobot(host);
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
		} catch (Exception e) {
			System.out.println("Error in parsing the url");
			return false;
		}
	}

}
