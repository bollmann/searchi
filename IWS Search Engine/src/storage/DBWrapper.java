/*
 * Written by Shreejit Gangadharan
 */
package storage;

import java.io.File;

import org.apache.log4j.Logger;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;

// TODO: Auto-generated Javadoc
/**
 * The Class DBWrapper.
 */
public class DBWrapper {
	private static Logger logger = Logger.getLogger(DBWrapper.class);

	/** The env directory. */
	private static String envDirectory = null;

	/** The my env. */
	private static Environment myEnv;

	/** The store. */
	private static EntityStore store;

	public static EntityStore getStore() {
		return store;
	}

	public static void setStore(EntityStore store) {
		DBWrapper.store = store;
	}

	/* TODO: write object store wrapper for BerkeleyDB */
	public static synchronized void initialize(String dbStoreLocation) {
		EnvironmentConfig myEnvConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		myEnvConfig.setAllowCreate(true);
		storeConfig.setAllowCreate(true);
		
//		myEnvConfig.setTransactional(true);
//		storeConfig.setTransactional(true);
		// Open the environment and entity store
		File dbFile = new File(dbStoreLocation);
		logger.info("Looking for persistent store at:"
				+ dbFile.getAbsolutePath());
		if (!dbFile.exists()) {
			dbFile.mkdir();
		}
		logger.info("Attempting to access dbStoreLocation:"
				+ dbFile.getAbsolutePath());
		envDirectory = dbStoreLocation;
		myEnv = new Environment(new File(dbStoreLocation), myEnvConfig);
		store = new EntityStore(myEnv, "EntityStore", storeConfig);
	}

	public static synchronized void close() {
		store.close();
		myEnv.cleanLog();
		myEnv.close();
	}

	public static void main(String[] args) {
		DBWrapper.initialize("resources/BDBStore");
		UserInfoAccessor uiAccessor = new UserInfoAccessor(store);
		UserInfo info = new UserInfo();
		info.setPassword("qwe");
		info.setUserName("user5");
		uiAccessor.pIndex.put(info);

		System.out.println("All users");
		EntityCursor<UserInfo> uCursor = uiAccessor.pIndex.entities();
		for (UserInfo user : uCursor) {
			System.out.println(user);
		}
		uCursor.close();
		DBWrapper.close();
	}

}
