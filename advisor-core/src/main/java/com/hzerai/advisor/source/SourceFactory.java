/**
 * 
 */
package com.hzerai.advisor.source;

import java.io.File;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.source.internal.FileSource;
import com.hzerai.advisor.source.internal.FolderSource;
import com.hzerai.advisor.source.internal.ZipSource;

/**
 * @author Habib Zerai
 *
 */
public class SourceFactory {

	public static LogSource scanPath(String filePath, boolean isRecursive, boolean isRemote)
			throws NotFoundException, SourceException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new NotFoundException(filePath);
		}
		if (filePath.endsWith("zip")) {
			return new ZipSource(filePath, filePath, isRecursive, isRemote);
		} else if (file.isDirectory()) {
			return new FolderSource(file, isRecursive, isRemote);
		} else if (file.isFile()) {
			return new FileSource(file, isRecursive, isRemote);
		}
		return null;
	}
}
