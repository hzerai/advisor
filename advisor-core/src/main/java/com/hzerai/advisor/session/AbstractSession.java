/**
 * 
 */
package com.hzerai.advisor.session;

import com.hzerai.advisor.data.Database;
import com.hzerai.advisor.data.mapping.ExceptionEvaluator;

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
	public <T> T analyse(Class<?> T) {
		if (String.class.isAssignableFrom(T)) {
			return (T) this.processor.map(database);
		} else if (ExceptionEvaluator.class.isAssignableFrom(T)) {
			return (T) this.processor.mapToList(database);
		}
		return null;
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
