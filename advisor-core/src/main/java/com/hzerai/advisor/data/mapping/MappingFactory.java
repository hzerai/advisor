/**
 * 
 */
package com.hzerai.advisor.data.mapping;

import com.hzerai.advisor.data.Database;

/**
 * @author Habib Zerai
 *
 */
public class MappingFactory {

	public static ExceptionMapper getMapper(Database db) {
		return new DefaultExceptionMapper(db);
	}

}
