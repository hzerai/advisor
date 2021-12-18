/**
 * 
 */
package com.hzerai.advisor.session;

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
	private boolean isRecursive;
	private boolean isRemote;

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
	}

	public Session build() {
		if (session == null) {
			throw new AdvisorException("Session build failed.");
		}
		try {
			Processor processor = new Processor(SourceFactory.scanPath(sourcePath, isRecursive, isRemote), out,
					fromDate, toDate);
			session.setProcessor(processor);
		} catch (Exception e) {
			throw new AdvisorException("Session build failed.", e);
		}
		SessionFactory.registerGlobalSession(session);
		return session;
	}

	public SessionBuilder database(String url) {
		Database db = DataManager.getDatabase(url);
		session.setDatabase(db);
		return this;
	}

	public SessionBuilder sourcePath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("source path cannot be null.");
		}
		sourcePath = path;
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

	public SessionBuilder toDate(LocalDateTime todate) {
		this.toDate = todate;
		return this;
	}

	public SessionBuilder remote() {
		isRemote = true;
		return this;
	}

}
