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
public class GenericParser implements LogParser {
	protected StackTraceTokenizer lines;

	public GenericParser(StackTraceTokenizer lines) {
		lines.stepBack();
		this.lines = lines;
	}

	@Override
	public AnalysisOutput parse() {
		return new AnalysisOutput();
	}

	public static boolean matches(String line) {
		return false;
	}

}
