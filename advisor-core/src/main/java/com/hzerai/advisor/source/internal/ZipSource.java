/**
 * 
 */
package com.hzerai.advisor.source.internal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.source.LogSource;
import com.hzerai.advisor.source.SourceType;

/**
 * @author Habib Zerai
 *
 */
public class ZipSource implements LogSource<ZipFile, Enumeration<? extends ZipEntry>> {

	protected ZipFile zipFile;
	protected String path;
	protected boolean canRead;
	protected boolean isRemote;
	protected boolean isRecursive;

	/**
	 * @param file
	 * @param recursive
	 * @param remote
	 * @throws SourceException
	 */
	public ZipSource(ZipFile zipFile, boolean recursive, boolean remote) throws SourceException {
		this.path = zipFile.getName();
		this.zipFile = zipFile;
		this.isRecursive = recursive;
		this.isRemote = remote;
		this.canRead = true;
	}

	public ZipSource(String zipFilePath, String path, boolean recursive, boolean remote) throws SourceException {
		this(createZip(zipFilePath), recursive, remote);
	}

	private static ZipFile createZip(String zipFilePath) throws SourceException {
		try {
			return new ZipFile(zipFilePath);
		} catch (IOException e) {
			throw new SourceException(e);
		}
	}

	@Override
	public SourceType getType() {
		return SourceType.Zip;
	}

	@Override
	public Enumeration<? extends ZipEntry> read() throws NotFoundException, SourceException {
		return zipFile.entries();
	}

	public ZipFile getUnderlyingFile() {
		return zipFile;
	}

	@Override
	public boolean empty() throws SourceException {
		return false;
	}

	@Override
	public boolean isRecursive() {
		return isRecursive;
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
