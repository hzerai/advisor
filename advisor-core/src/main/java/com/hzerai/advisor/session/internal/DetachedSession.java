/**
 * 
 */
package com.hzerai.advisor.session.internal;

import com.hzerai.advisor.session.AbstractSession;
import com.hzerai.advisor.session.SessionMode;

/**
 * @author Habib Zerai
 *
 */
@SuppressWarnings("unchecked")
public class DetachedSession extends AbstractSession {

	public DetachedSession() {
		super(SessionMode.Detached);

	}

	@Override
	public <T> T analyse(Class<?> T) {
		if (String.class.isAssignableFrom(T)) {
			return (T) this.processor.map(database);
		}
		return null;
	}

}
