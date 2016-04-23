package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;


public class Main {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dbDir = args[0];
		String intputDir = args[1];
		
		EnvironmentConfig envConfig = new EnvironmentConfig();  
        envConfig.setAllowCreate(true);  
        File file = new File(dbDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
        Environment dbEnv = new Environment(new File(dbDir), envConfig);  
        DatabaseConfig dbconf = new DatabaseConfig();  
        dbconf.setAllowCreate(true); 
        
	    dbconf.setSortedDuplicates(false);//allow update  
 
        Database db = dbEnv.openDatabase(null, "myStore", dbconf);  
      
   
		try {
			
			File input = new File(intputDir);
			if (!input.isDirectory()) return;
			File[] files = input.listFiles();
			for (int i = 0; i < files.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				String url = br.readLine();
				if (url == null) continue;
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String html = sb.toString();
				br.close();
				Document doc = Jsoup.parse(html, url);
				String title = doc.title();
				if (title != null) {
					DatabaseEntry dataValue = new DatabaseEntry(title.getBytes("UTF-8"));   
			        DatabaseEntry keyValue = new DatabaseEntry(url.getBytes("UTF-8"));  
			        db.put(null, keyValue, dataValue);//inserting an entry  }  
					System.out.println("store url: " + url);
					System.out.println("store title: " + title);
				}
			
		}
		
	   db.close();  
	   dbEnv.close(); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			db.close();  
		    dbEnv.close(); 
			return;
		}
		
	}

}
