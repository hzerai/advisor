/**
 * 
 */
package com.hzerai.advisor.session;

/**
 * @author Habib Zerai
 *
 */
public interface Session {

	void print();

	<T> T analyse(Class<?> T);

}
