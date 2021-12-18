/**
 * 
 */
package com.hzerai.advisor.session;

/**
 * @author Habib Zerai
 *
 */
public class SessionFactory {

	private static Session currentGlobalSession;
	public static SessionMode mode = SessionMode.Detached;

	public static Session currentSession() {
		if (SessionMode.Detached.equals(mode)) {
			return currentGlobalSession;
		}
		return null;
	}

	static void registerGlobalSession(Session session) {
		currentGlobalSession = session;
	}

}
