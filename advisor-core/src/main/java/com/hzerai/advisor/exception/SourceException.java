/**
 * 
 */
package com.hzerai.advisor.exception;

import java.io.IOException;

/**
 * @author Habib Zerai
 *
 */
public class SourceException extends Exception {

	/**
	 * @param canRead
	 * @param isEmpty
	 */
	public SourceException(boolean canRead, boolean isEmpty) {
		//treat eachCase
	}

	/**
	 * @param e1
	 */
	public SourceException(IOException e1) {
		super(e1);
	}

}
