/**
 * 
 */
package com.hzerai.advisor.exception;

/**
 * @author Habib Zerai
 *
 */
public class ProcessException extends AdvisorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessException(Throwable e) {
		super(e);
	}

	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param string
	 */
	public ProcessException(String message) {
		super(message);
	}
}
