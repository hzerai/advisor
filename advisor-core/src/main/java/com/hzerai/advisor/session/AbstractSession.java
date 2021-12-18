/**
 * 
 */
package com.hzerai.advisor.session;

import com.hzerai.advisor.data.Database;

/**
 * @author Habib Zerai
 *
 */
public abstract class AbstractSession implements Session {

	protected Processor processor;
	protected Database database;

	protected AbstractSession(SessionMode mode) {
		SessionFactory.mode = mode;
	}

	void setProcessor(Processor processor) {
		this.processor = processor;
		processor.process();
	}

	@Override
	public void print() {
		processor.print(database, System.out);
	}

	/**
	 * @param db
	 */
	void setDatabase(Database db) {
		this.database = db;
	}

}
