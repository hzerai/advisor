/**
 * 
 */
package com.hzerai.advisor.source.internal;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.source.LogSource;
import com.hzerai.advisor.source.SourceType;

/**
 * @author Habib Zerai
 *
 */
public class StacktraceSource implements LogSource<StackTraceElement[], String>{

	@Override
	public String read() throws NotFoundException, SourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRecursive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRemote() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SourceType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canRead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean empty() throws SourceException {
		// TODO Auto-generated method stub
		return false;
	}


	
}
