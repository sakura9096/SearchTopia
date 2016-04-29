package pageRankStorage;
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
 * This is a wrapper class for storing the PageRank info.
 * @author yuezhang
 *
 */
public class SourceURLWrapper {

	private static SourceURLWrapper instance = null;
	
	private static Environment myEnv;
	private static EntityStore store;

	// data accessors for user, channel, webpage and url
	private PrimaryIndex<String, SourceURL> sourceURLs;

	/**
	 * The constructor.
	 * @param envDir
	 * @param readOnly
	 */
	
	
	public static SourceURLWrapper getInstance(String envDir) {
		if (instance == null) {
			synchronized (SourceURLWrapper.class) {
				if (instance == null) {
					instance = new SourceURLWrapper(envDir, false);
				}
			}
		}
		return instance;
	}
	
	private SourceURLWrapper(String envDir, boolean readOnly) {

		File envHome = new File(envDir);
		if (!envHome.exists()) {
			envHome.mkdir();
		}
		//setup the environment and entityStore
		setup(envHome, readOnly);
		//open the indices
		sourceURLs = store.getPrimaryIndex(String.class, SourceURL.class);
	}

	/**
	 * @param envHome
	 * @param readOnly
	 */
	public static void setup(File envHome, boolean readOnly) {

		try {
			EnvironmentConfig myEnvConfig = new EnvironmentConfig();
			StoreConfig storeConfig = new StoreConfig();

			//if the environment is opened for write, 
			//create the environment and entity store if they do not exist
			myEnvConfig.setAllowCreate(!readOnly);
			storeConfig.setAllowCreate(!readOnly);

			myEnvConfig.setTransactional(true);

			// open the environment and entity store
			myEnv = new Environment(envHome, myEnvConfig);
			store = new EntityStore(myEnv, "EntityStore", storeConfig);
		} catch(DatabaseException dbe) {
			System.err.println("Error opening myEnv and store: " + dbe.toString());
			System.exit(-1);
		}
	}

	/**
	 * Return the entity store.
	 * @return The entity store
	 */
	public EntityStore getStore() {
		return store;
	}

	/**
	 * Return the environment.
	 * @return The environment
	 */
	public Environment getEnv() {
		return myEnv;
	}

	/**
	 * Close the store and environment.
	 */
	public void close() {
		myEnv.sync();
		if (store != null) {
			try {
				store.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing store: " + dbe.toString());
				System.exit(-1);
			}
		}
		if (myEnv != null) {
			try {
				myEnv.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing myEnv: " + dbe.toString());
				System.exit(-1);
			}
		}
	}

	/**
	 * @param url
	 * @param pageRank
	 */
	public void addSourceURL(String url) {
		SourceURL page = new SourceURL();
		page.setUrl(url);
		sourceURLs.put(page);
		
		myEnv.sync();
		store.sync();
	}
	
	public boolean containsURL(String url) {
		return sourceURLs.contains(url);
	}
	
	/**
	 * @return
	 */
	public List<String> getAllSourceURL() {
		List<String> urls = new ArrayList<>();
		EntityCursor<String> urlStr = sourceURLs.keys();
		try {
			for (String key: urlStr) {
				urls.add(key);
			}
		} finally {
			urlStr.close();
		}
		return urls;
	}
	
}
