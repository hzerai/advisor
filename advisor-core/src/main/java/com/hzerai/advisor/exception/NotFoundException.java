/**
 * 
 */
package com.hzerai.advisor.exception;

/**
 * @author Habib Zerai
 *
 */
public class NotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String fileName) {
		super(fileName);
	}
}
