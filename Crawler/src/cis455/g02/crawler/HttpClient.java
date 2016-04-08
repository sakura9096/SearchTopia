package cis455.g02.crawler;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {
	String url;
	HttpsURLConnection httpsCon;
	URL myurl;
	Boolean isValid;
	
	String contentType;
	int statusCode;
	int contentLength;
	long lastModified;
	
	InputStream in;
	String body;
	
	String host;
	String protocol;
	int portNumber;
	String path;
	
	
	public HttpClient (String url) {
		this.isValid = true;
		this.url = url;
		init(this.url);
		try {
			this.myurl = new URL(this.url);
		} catch (MalformedURLException e) {
			System.out.println("URL protocal not valid: " + url);
			this.isValid = false;
		}
		
	}
	
	public void init(String str) {
		String fileUrl;
		if (str.startsWith("http://")) {
			fileUrl = str.substring(7);
			this.protocol = "http";
		} else if (str.startsWith("https://")) {
			fileUrl = str.substring(8);
			this.protocol = "https";
		} else {
			fileUrl = str;
			this.protocol = "http";
		}
		
		
		int pos = fileUrl.indexOf('/');
		String hostName = fileUrl;
		if (pos == -1) {
			path = "/";
		} else {
			path = fileUrl.substring(pos);
			hostName = fileUrl.substring(0, pos);
			if (hostName.equals("/") || hostName.equals("")) {
				return;
			}
		}
		if (hostName.indexOf(':') != -1) {
			String[] parts = hostName.split(":", 2);
			this.host = parts[0].trim();
			try {
				this.portNumber = Integer.parseInt(parts[1].trim());
			} catch (Exception e) {
				this.portNumber = 80;
			}
		} else {
			this.host = hostName;
			this.portNumber = 80;
		}
	}
	
	
	public void excuteHead() {
		try {
			if (url.startsWith("https")) {
				URL hpsUrl = this.myurl;
				HttpsURLConnection hpsConnection = (HttpsURLConnection)hpsUrl.openConnection();
				hpsConnection.setRequestMethod("HEAD");
				hpsConnection.setRequestProperty("User-Agent", "cis455crawler");
				if (hpsConnection.getContentType() == null) {
					this.statusCode = 500;
					return;
				}
				this.contentType = this.parseContentType(hpsConnection.getContentType());
				this.contentLength = hpsConnection.getContentLength();
				this.statusCode = hpsConnection.getResponseCode();
				
				if (statusCode  >= 400) return;
				
				this.lastModified = hpsConnection.getLastModified();
				hpsConnection.disconnect();
				
			} else if (url.startsWith("http")) {
				URL hpUrl = this.myurl;
				HttpURLConnection hpConnection = (HttpURLConnection)hpUrl.openConnection();
				hpConnection.setRequestMethod("HEAD");
				hpConnection.setRequestProperty("User-Agent", "cis455crawler");
				if (hpConnection.getContentType() == null) {
					this.statusCode = 500;
					return;
				}
				this.contentType = this.parseContentType(hpConnection.getContentType());
				this.contentLength = hpConnection.getContentLength();
				this.statusCode = hpConnection.getResponseCode();
				
				if (statusCode  >= 400) return;
				
				this.lastModified = hpConnection.getLastModified();
				hpConnection.disconnect();
				
			}
		} catch (Exception e) {
			return;
		}
	}
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void executeGet() {
		try {
			if (url.startsWith("https")) {
				URL hpsUrl = this.myurl;
				HttpsURLConnection hpsConnection = (HttpsURLConnection)hpsUrl.openConnection();
				hpsConnection.setRequestMethod("HEAD");
				hpsConnection.setRequestProperty("User-Agent", "cis455crawler");
				in = hpsConnection.getInputStream();
				
			} else if (url.startsWith("http")) {
				URL hpUrl = this.myurl;
				HttpURLConnection hpConnection = (HttpURLConnection)hpUrl.openConnection();
				hpConnection.setRequestMethod("HEAD");
				hpConnection.setRequestProperty("User-Agent", "cis455crawler");
				in = hpConnection.getInputStream();
				
			}
			
			if (contentLength > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < contentLength; i++) {
					sb.append((char)in.read());
				}
				body = sb.toString();
			} else {
				StringBuilder sb = new StringBuilder();
				int ch;
				while ((ch = in.read()) != -1) {
					contentLength++;
					sb.append((char) ch);
				}
				body = sb.toString();
			}
			
		} catch (Exception e) {
			return;
		}
	}
	
	
	public String parseContentType(String type) {
		int end = type.indexOf(";");
		if (end == -1) {
			return type.trim();
		} else {
			return type.split(";")[0].trim();
		}
	}


	public Boolean getIsValid() {
		return isValid;
	}


	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	public int getContentLength() {
		return contentLength;
	}


	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}


	public long getLastModified() {
		return lastModified;
	}


	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}
	
	
	
	
	
}