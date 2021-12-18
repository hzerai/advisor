/**
 * 
 */
package com.hzerai.advisor.source.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.source.LogSource;
import com.hzerai.advisor.source.SourceType;

/**
 * @author Habib Zerai
 *
 */
public class FileSource implements LogSource<File, String> {
	protected String path;
	protected boolean canRead = false;
	protected boolean isRemote;
	protected boolean isRecursive;
	private File file;
	private boolean isEmpty = true;

	public FileSource(File file, boolean recursive, boolean remote) throws SourceException {
		this.file = file;
		this.isRecursive = recursive;
		this.isRemote = remote;
		this.canRead = file.canRead();
		this.path = file.getAbsolutePath();
		empty();
	}

	@Override
	public String read() throws NotFoundException, SourceException {
		if (!canRead || isEmpty) {
			throw new SourceException(canRead, isEmpty);
		}
		try (InputStream is = new FileInputStream(file)) {
			String result = new String(is.readAllBytes());
			return result;
		} catch (FileNotFoundException e) {
			throw new NotFoundException(path);
		} catch (IOException e1) {
			throw new SourceException(e1);
		}
	}

	@Override
	public SourceType getType() {
		return SourceType.File;
	}

	@Override
	public boolean empty() throws SourceException {
		if (!canRead) {
			throw new SourceException(canRead, isEmpty);
		}
		try (InputStream is = getStream()) {
			isEmpty = is.available() == 0;
			return isEmpty;
		} catch (IOException e) {
			throw new SourceException(e);
		}

	}

	InputStream getStream() throws SourceException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new SourceException(e);
		}
	}

	@Override
	public boolean isRecursive() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return isRemote;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean canRead() {
		return canRead;
	}

}
