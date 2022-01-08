/**
 * 
 */
package com.hzerai.advisor.data;

import com.hzerai.advisor.data.store.JsonDatabase;
import com.hzerai.advisor.session.SessionFactory;

/**
 * @author Habib Zerai
 *
 */
public class DataManager {

	public static Database getDatabase() {
		/**
		 * some thing like this
		 */
		switch (SessionFactory.mode) {
		case Detached:
			return new JsonDatabase();
		case Attached:
			return new JsonDatabase();
		case Standalone:
			return new JsonDatabase();
		}
		throw new IllegalArgumentException("session mode not specified.");
	}

}
