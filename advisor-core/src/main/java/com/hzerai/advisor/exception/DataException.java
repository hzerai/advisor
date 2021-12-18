/**
 * 
 */
package com.hzerai.advisor.exception;

/**
 * @author Habib Zerai
 *
 */
public class DataException extends AdvisorException {

	private static final long serialVersionUID = 1L;

	public DataException(Throwable e) {
		super(e);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(String message) {
		super(message);
	}

}
