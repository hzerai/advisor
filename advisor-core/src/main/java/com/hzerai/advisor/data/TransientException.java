/**
 * 
 */
package com.hzerai.advisor.data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Habib Zerai
 *
 */
public class TransientException {

	private static final String TWO_DOTS = ":";
	/**
	 * 
	 */
	protected static final String STRING_SPERATOR = "   |   ";
	private String name;
	private String message;
	private LogLevel level;
	private String logger;
	private String thread;
	private LocalDateTime date;
	private String stackTrace;
	private String logFile;
	private Set<String> causedBy = new TreeSet<>();

	public String getName() {
		if ((name == null || name.isEmpty() || name.trim().equals("null")) && !causedBy.isEmpty()) {
			String tmpName = causedBy.iterator().next().replace("Caused by: ", "");
			int hasDots = tmpName.indexOf(TWO_DOTS) + 1;
			if (hasDots > 0) {
				tmpName = tmpName.substring(0, hasDots - 1);
				message = tmpName.substring(hasDots - 1);
			}
			name = tmpName;
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		this.level = level;
	}

	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		if (stackTrace != null) {
			int to = stackTrace.indexOf(System.lineSeparator(), 0);
			int more = stackTrace.indexOf(" more");
			int causedBy = stackTrace.lastIndexOf("Caused by");
			int end = more > 0 ? more : (stackTrace.length() > 6000 ? 6000 : stackTrace.length());
			stackTrace = stackTrace.substring(causedBy > 0 ? causedBy : 0, end);
		}
		this.stackTrace = stackTrace;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public Set<String> getCausedBy() {
		return causedBy;
	}

	public void addCausedBy(String causedBy) {
		this.causedBy.add(causedBy);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(padRightSpaces(date, 23));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces(logger, 70));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces(thread, 50));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces(level, 15));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces(getName(), 70));
		sb.append(STRING_SPERATOR);
		sb.append(message);
		if (!causedBy.isEmpty()) {
			StringBuilder treeBuilder = new StringBuilder(padRightSpaces("", 180));
			treeBuilder.append("|______  ");
			for (String oneCB : causedBy) {
				treeBuilder.insert(0, "      ");
				sb.append(System.lineSeparator());
				sb.append(treeBuilder.toString());
				sb.append(oneCB.trim());
			}
		}
		return sb.toString();
	}

	public String padRightSpaces(Object input, int padSize) {
		String inputString = String.valueOf(input);
		if (inputString.length() >= padSize) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(inputString);
		while (sb.length() < padSize) {
			sb.append(' ');
		}
		return sb.toString();
	}

}
