/**
 * 
 */
package com.hzerai.advisor.source.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.source.LogSource;
import com.hzerai.advisor.source.SourceType;

/**
 * @author Habib Zerai
 *
 */
@SuppressWarnings("rawtypes")
public class FolderSource implements LogSource<File, Set<LogSource>> {
	protected String path;
	protected boolean canRead = false;
	protected boolean isRemote;
	protected boolean isRecursive;
	private File file;
	private boolean isEmpty = true;
	private Set<LogSource> content = new HashSet<>();

	/**
	 * @param file
	 * @param recursive
	 * @param remote
	 * @throws SourceException
	 */
	public FolderSource(File file, boolean recursive, boolean remote) throws SourceException {
		this.file = file;
		this.path = file.getAbsolutePath();
		this.canRead = file.canRead();
		if (canRead) {
			this.isEmpty = file.listFiles().length == 0;
		}
	}

	@Override
	public SourceType getType() {
		return SourceType.Folder;
	}

	@Override
	public Set<LogSource> read() throws NotFoundException, SourceException {
		if (!canRead || isEmpty) {
			throw new SourceException(canRead, isEmpty);
		}
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				Logger.getLogger("Advisor").log(Level.INFO, "Found folder : " + f.getAbsolutePath());
				content.add(new FolderSource(f, isRecursive, isRemote));
			} else if (f.isFile()) {
				if (f.getAbsolutePath().endsWith("zip")) {
					Logger.getLogger("Advisor").log(Level.INFO, "Found zip file : " + f.getAbsolutePath());
					ZipFile zipFile;
					try {
						zipFile = new ZipFile(f.getAbsolutePath());
					} catch (IOException e) {
						throw new SourceException(e);
					}
					content.add(new ZipSource(zipFile, isRecursive, isRemote));
				} else {
					Logger.getLogger("Advisor").log(Level.INFO, "Found file : " + f.getAbsolutePath());
					content.add(new FileSource(f, isRecursive, isRemote));
				}
			}
		}
		return content;
	}

	@Override
	public boolean empty() {
		return isEmpty;
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
