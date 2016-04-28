package cis455.storage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

public class TestDB {
	EnvironmentConfig envConfig;
	Environment dbEnv;
	DatabaseConfig dbconf;
	Database db;
	String dir = "/Users/fanglinlu/Desktop/title";
	//String dir2 = "/Users/Linjie/Desktop/title";
	
	
	public TestDB() {
		envConfig = new EnvironmentConfig();  
	    envConfig.setAllowCreate(true);  
	    dbEnv = new Environment(new File(dir), envConfig);  
	    dbconf = new DatabaseConfig();  
	    dbconf.setAllowCreate(true);  
	    dbconf.setSortedDuplicates(false);//allow update  
	    db = dbEnv.openDatabase(null, "myStore", dbconf);  
	}
	
	
	public String getTitle(String url) {
		Map<String, String> results = new HashMap<String, String>();
		try {
			DatabaseEntry searchEntry = new DatabaseEntry();  
			DatabaseEntry keyValue = new DatabaseEntry(url.getBytes("UTF-8")); 
			db.get(null, keyValue, searchEntry, LockMode.DEFAULT);//retrieving record  
			String foundData = new String(searchEntry.getData(), "UTF-8");  
			return foundData;
		} catch (Exception e) {
			return null;
		}	   
		  
	}
	
	public void close() {
		db.close();  
	    dbEnv.close();  
	}

}
