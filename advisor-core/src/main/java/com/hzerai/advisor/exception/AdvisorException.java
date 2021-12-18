/**
 * 
 */
package com.hzerai.advisor.exception;

/**
 * @author Habib Zerai
 *
 */
public class AdvisorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdvisorException(Throwable e) {
		super(e);
	}

	public AdvisorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param string
	 */
	public AdvisorException(String message) {
		super(message);
	}
}
