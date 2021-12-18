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
public class StandaloneSession extends AbstractSession {

	public StandaloneSession() {
		super(SessionMode.Standalone);
	}

	@Override
	public <T> T analyse(Class<?> T) {
		return null;
	}

}
