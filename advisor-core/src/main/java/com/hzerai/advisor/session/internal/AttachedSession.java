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
public class AttachedSession extends AbstractSession {

	public AttachedSession() {
		super(SessionMode.Attached);
	}

	@Override
	public <T> T analyse(Class<?> T) {
		return null;
	}

}
