/**
 * 
 */
package com.hzerai.advisor.parse;

import com.hzerai.advisor.parse.internal.GenericParser;
import com.hzerai.advisor.parse.internal.Log4jParser;
import com.hzerai.advisor.parse.internal.PalmyraParser;
import com.hzerai.advisor.parse.internal.TomcatParser;
import com.hzerai.advisor.parse.internal.stacktrace.StackTraceTokenizer;

/**
 * @author Habib Zerai
 *
 */
public class ParserFactory {

	public static LogParser getParser(String input) {
		StackTraceTokenizer tokenizer = new StackTraceTokenizer(input);
		String inputLine = null;
		while ((inputLine = tokenizer.nextLine()) != null) {
			if (Log4jParser.matches(inputLine)) {
				return new Log4jParser(tokenizer);
			} else if (PalmyraParser.matches(inputLine)) {
				return new PalmyraParser(tokenizer);
			} else if (TomcatParser.matches(inputLine)) {
				return new TomcatParser(tokenizer);
			}
		}
		return new GenericParser(tokenizer);
	}
}
