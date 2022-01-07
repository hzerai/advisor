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

	public static Database getDatabase(String url) {
		/**
		 * some thing like this
		 */
		switch (SessionFactory.mode) {
		case Detached:
			return new JsonDatabase(url);
		case Attached:
			return new JsonDatabase(url);
		case Standalone:
			return new JsonDatabase(url);
		}
		throw new IllegalArgumentException("session mode not specified.");
	}

}
