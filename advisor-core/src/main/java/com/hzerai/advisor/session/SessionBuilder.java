/**
 * 
 */
package com.hzerai.advisor.session;

import java.io.InputStream;
import java.time.LocalDateTime;

import com.hzerai.advisor.data.DataManager;
import com.hzerai.advisor.data.Database;
import com.hzerai.advisor.exception.AdvisorException;
import com.hzerai.advisor.session.internal.AttachedSession;
import com.hzerai.advisor.session.internal.DetachedSession;
import com.hzerai.advisor.session.internal.StandaloneSession;
import com.hzerai.advisor.source.SourceFactory;

/**
 * @author Habib Zerai
 *
 */
public class SessionBuilder {

	private AbstractSession session;
	private String sourcePath;
	private String out;
	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	private String exceptionName;
	private boolean isRecursive;
	private boolean isRemote;
	private InputStream is;

	public static SessionBuilder mode(SessionMode mode) {
		return new SessionBuilder(mode);
	}

	private SessionBuilder(SessionMode mode) {
		switch (mode) {
		case Detached:
			this.session = new DetachedSession();
			break;
		case Attached:
			this.session = new AttachedSession();
			break;
		case Standalone:
			this.session = new StandaloneSession();
			break;
		default:
			throw new IllegalArgumentException("session mode not specified.");
		}
		Database db = DataManager.getDatabase();
		session.setDatabase(db);
	}

	public Session build() {
		if (session == null) {
			throw new AdvisorException("Session build failed.");
		}
		try {
			Processor processor = null;
			if (session instanceof DetachedSession) {
				processor = new Processor(SourceFactory.scanPath(sourcePath, isRecursive, isRemote), out, fromDate,
						toDate);
				SessionFactory.registerGlobalSession(session);
			} else if (session instanceof StandaloneSession) {
				processor = new Processor(is, fromDate, toDate, exceptionName);
			}
			session.setProcessor(processor);
		} catch (Exception e) {
			throw new AdvisorException("Session build failed.", e);
		}
		return session;
	}

	public SessionBuilder sourcePath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("source path cannot be null.");
		}
		sourcePath = path;
		return this;
	}

	public SessionBuilder inputStream(InputStream is) {
		if (is == null) {
			throw new IllegalArgumentException("inputStream cannot be null.");
		}
		this.is = is;
		return this;
	}

	public SessionBuilder outputFile(String out) {
		if (out == null) {
			throw new IllegalArgumentException("output path cannot be null.");
		}
		this.out = out;
		return this;
	}

	public SessionBuilder recursive() {
		isRecursive = true;
		return this;
	}

	public SessionBuilder fromDate(LocalDateTime fromdate) {
		this.fromDate = fromdate;
		return this;
	}
	
	public SessionBuilder exceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
		return this;
	}

	public SessionBuilder toDate(LocalDateTime todate) {
		this.toDate = todate;
		return this;
	}

	public SessionBuilder remote() {
		isRemote = true;
		return this;
	}

}
