/**
 * 
 */
package com.hzerai.advisor.source;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;

/**
 * @author Habib Zerai
 *
 */
public interface LogSource <I,O> {

	O read() throws NotFoundException, SourceException;

	boolean isRecursive();

	boolean isRemote();

	SourceType getType();

	String getPath();

	boolean canRead();

	boolean empty() throws SourceException;

}
