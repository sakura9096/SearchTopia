package pageRankStorage;
import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

/**
 * This is a wrapper class for storing the PageRank info.
 * @author yuezhang
 *
 */
public class PageRankWrapper {

	private static Environment myEnv;
	private static EntityStore store;

	// data accessors for user, channel, webpage and url
	private PrimaryIndex<String, PageRankEntity> pageRanks;

	/**
	 * The constructor.
	 * @param envDir
	 * @param readOnly
	 */
	public PageRankWrapper(String envDir, boolean readOnly) {

		File envHome = new File(envDir);
		if (!envHome.exists()) {
			envHome.mkdir();
		}
		//setup the environment and entityStore
		setup(envHome, readOnly);
		//open the indices
		pageRanks = store.getPrimaryIndex(String.class, PageRankEntity.class);
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
	public void addPageRank(String url, double pageRank) {
		PageRankEntity page = pageRanks.get(url);
		if (page == null) {
			page = new PageRankEntity();
		}
		page.setPageRank(pageRank);
		pageRanks.put(page);
	}

}
