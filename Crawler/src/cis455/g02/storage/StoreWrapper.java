package cis455.g02.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
/**
 * StoreWrapper class as a drive to get access from and write into the berkeleydb
 * @author Linjie
 *
 */
public class StoreWrapper {
	private Environment myEnv;
	private EntityStore myStore;
	private PrimaryIndex <String, Frontier> frontierByURL;
	private PrimaryIndex <String, Crawled> crawledByURL;

	private static StoreWrapper myStoreWrapper  = null;
	
	private StoreWrapper (String envHome) {
		setup (envHome);
	}
	
	public static StoreWrapper getInstance (String envHome) {
		if (myStoreWrapper == null) {
			File file = new File(envHome);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdir();
			}
			myStoreWrapper = new StoreWrapper (envHome);
			
		}
		
		return myStoreWrapper;
	}
	
	 public void setup (String envHome) {
		 try {
			 EnvironmentConfig myEnvConfig = new EnvironmentConfig();
			 StoreConfig myStoreConfig = new StoreConfig();
			
			 myEnvConfig.setAllowCreate(true);
			 myStoreConfig.setAllowCreate(true);
			 
			 File envHomeFileName = new File (envHome);
			 
			 myEnv = new Environment (envHomeFileName, myEnvConfig);
			 myStore = new EntityStore (myEnv, "myStore", myStoreConfig);
			 
			 frontierByURL = myStore.getPrimaryIndex(String.class, Frontier.class);
			 crawledByURL = myStore.getPrimaryIndex(String.class, Crawled.class);
			
		 } catch (DatabaseException e) {
			 System.out.println(e.getMessage());
		 }
		 
	 }
	 
	 public void close() {
		 try {
			 if (this.myStore != null) this.myStore.close();
			 if (this.myEnv != null) this.myEnv.close();
		 } catch (DatabaseException e) {
			 System.out.println(e.getMessage());
		 }
	 }
	 
	 public void putFrontierURL (String url) {
		 Frontier frontier = new Frontier();
		 frontier.setURL(url);
		 frontierByURL.put(frontier);
		 myEnv.sync();
		 myStore.sync();
	 }
	 
	 public void deleteFrontierURL (String url) {
		 frontierByURL.delete(url);
	 }
	 
	 
	 public void putCrawledURL (String crawledURL, long lastModified) {
		 Crawled crawled = new Crawled ();
		 crawled.setUrl(crawledURL);
		
		 crawledByURL.put(crawled);
		 
		 myEnv.sync();
		 myStore.sync();
	 }
	 

	 
	 public List<String> getAllFrontiers () {
		 
		 EntityCursor<Frontier> allFrontiers = frontierByURL.entities();
		 List<String> result = new ArrayList<String>();
		 try {
			 for (Frontier frontier: allFrontiers) {
				 result.add(frontier.getURL());
			 }
		 } finally {
			 allFrontiers.close();
		 }
		 
		 return result;
	 }
	 
	
	 
	 public boolean urlCrawled (String url) {
		 return crawledByURL.contains(url);
	 }
	 
	 
	 public void addAllFroniters(List<String> list) {
		 for (String lis: list) {
			 this.putFrontierURL(lis);
		 }
	 }
	 
	 
	 
}
