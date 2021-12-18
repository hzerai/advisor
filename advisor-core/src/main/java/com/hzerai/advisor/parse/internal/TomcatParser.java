/**
 * 
 */
package com.hzerai.advisor.parse.internal;

import com.hzerai.advisor.data.AnalysisOutput;
import com.hzerai.advisor.parse.LogParser;
import com.hzerai.advisor.parse.internal.stacktrace.StackTraceTokenizer;

/**
 * @author Habib Zerai
 *
 */
public class TomcatParser implements LogParser {

	private final StackTraceTokenizer lines;

	public TomcatParser(StackTraceTokenizer lines) {
		lines.stepBack();
		this.lines = lines;
	}

	@Override
	public AnalysisOutput parse() {
		return null;
	}

	public static boolean matches(String line) {
		return false;
	}

}
