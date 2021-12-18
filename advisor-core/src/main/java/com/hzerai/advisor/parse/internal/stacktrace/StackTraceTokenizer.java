/**
 * 
 */
package com.hzerai.advisor.parse.internal.stacktrace;

/**
 * @author Habib Zerai
 *
 */
public class StackTraceTokenizer {

	/**
	 * 
	 */
	public static final String WINDOWS_LINE_SEPERATOR = "\n";
	/**
	 * 
	 */
	public static final String UNIX_LINE_SEPERATOR = "\r\n";
	private String[] stackTrace;
	private String currentLine = null;
	private int index = -1;
	private int mark = -2;
	private final int size;

	public StackTraceTokenizer(String input) {
		if (input.contains(UNIX_LINE_SEPERATOR)) {
			this.stackTrace = input.split(UNIX_LINE_SEPERATOR);
		} else if (input.contains(WINDOWS_LINE_SEPERATOR)) {
			this.stackTrace = input.split(WINDOWS_LINE_SEPERATOR);
		}
		this.size = this.stackTrace.length;
	}

	public void mark() {
		this.mark = index;
	}
	
	public void stepBack() {
		if(index >= 0) {
			index--;
		}
	}

	public boolean marked() {
		return mark > 0;
	}

	public void reset() {
		if (mark >= 0) {
			index = mark;
			mark = -2;
		}
		index = 0;
	}

	public String nextLine() {
		if ((index + 1) < size) {
			index++;
			currentLine = stackTrace[index];
			return currentLine;
		}
		return null;
	}

	public String currentLine() {
		return currentLine;
	}

	public boolean hasNext() {
		return (index + 1) < size;
	}

}
