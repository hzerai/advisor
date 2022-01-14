/**
 * 
 */
package com.hzerai.advisor.parse.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hzerai.advisor.data.AnalysisOutput;
import com.hzerai.advisor.data.TransientException;
import com.hzerai.advisor.parse.LogParser;
import com.hzerai.advisor.parse.internal.extractor.ExceptionDataExtractor;
import com.hzerai.advisor.parse.internal.stacktrace.StackTraceTokenizer;

/**
 * @author Habib Zerai
 *
 */
public class PalmyraParser implements LogParser {

	private static final String palmyraRegex = ".*([A-Z][a-z]{2} \\d{2}, \\d{4} \\d{2}:\\d{2}:\\d{2}:\\d{3}) .* (ERROR|SEVERE|FATAL) \\|([a-zA-Z]*)-\\[(.{1,50})]:.*";
	private static final String palmyraRealException = "(.*Exception)[:| ]\\s*(.*)";
	private final StackTraceTokenizer lines;
	private static final Pattern pattern = Pattern.compile(palmyraRegex);
	private final Matcher matcher = pattern.matcher("");
	private static final Pattern palmyraRealExceptionpattern = Pattern.compile(palmyraRealException);
	private final Matcher palmyraRealExceptionmatcher = palmyraRealExceptionpattern.matcher("");
	private static final Map<String, Integer> mapper = new HashMap<>();
	private static final Map<String, String> properties = new HashMap<>();

	{
		properties.put("date_format", "MMM dd, yyyy HH:mm:ss:SSS");
		mapper.put("date", 1);
		mapper.put("level", 2);
		mapper.put("logger", 3);
		mapper.put("thread", 4);
	}

	public PalmyraParser(StackTraceTokenizer lines) {
		lines.stepBack();
		this.lines = lines;
	}

	@Override
	public AnalysisOutput parse() {
		String inputLine = null;
		StringBuilder stack = new StringBuilder();
		AnalysisOutput output = new AnalysisOutput();
		boolean hasCB = false;
		while ((inputLine = lines.nextLine()) != null) {
			if(inputLine.contains("security.securityModule.ignoreException")) {
				continue;
			}
			matcher.reset(inputLine);
			stack.append(inputLine).append(System.lineSeparator());
			if (inputLine.contains("Caused by:") && !output.getResult().isEmpty()) {
				output.getResult().get(output.getResult().size() - 1)
						.addCausedBy(inputLine.trim().substring(inputLine.indexOf("Caused by:")));
				hasCB = true;
			} else if (matcher.find()) {
				TransientException ex = ExceptionDataExtractor.extract(properties, matcher.toMatchResult(), mapper);
				if (output.getResult().isEmpty()) {
					stack = new StringBuilder();
					hasCB = false;
				} else {
					if (hasCB) {
						output.getResult().get(output.getResult().size() - 1)
								.setStackTrace(stack.toString().substring(stack.lastIndexOf("Caused by:")));
					} else {
						output.getResult().get(output.getResult().size() - 1).setStackTrace(stack.toString());
					}
					stack = new StringBuilder();
					hasCB = false;
				}
				inputLine = lines.nextLine();
				if (inputLine != null) {
					palmyraRealExceptionmatcher.reset(inputLine);
					if (palmyraRealExceptionmatcher.find()) {
						String name = palmyraRealExceptionmatcher.group(1);
						String message = palmyraRealExceptionmatcher.group(2);
						ex.setName(name);
						ex.setMessage(message);
					}
				}
				output.addResult(ex);
			}
		}
		if (!output.getResult().isEmpty()) {
			if (hasCB) {
				output.getResult().get(output.getResult().size() - 1)
						.setStackTrace(stack.toString().substring(stack.lastIndexOf("Caused by:")));
			} else {
				output.getResult().get(output.getResult().size() - 1).setStackTrace(stack.toString());
			}
		}
		return output;
	}

	public static boolean matches(String line) {
		return pattern.matcher(line).matches();
	}

}
