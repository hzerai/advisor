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
public class Log4jParser implements LogParser {

	private static final String log4gRegex = ".*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) *(ERROR|SEVERE|FATAL|INFO) *\\[(.*)\\] *\\((.{1,50})\\) *.*\\s(.*Exception)[:| ]\\s*(.*)";
	private static final Pattern pattern = Pattern.compile(log4gRegex);
	private final Matcher matcher = pattern.matcher("");
	private static final Map<String, Integer> mapper = new HashMap<>();
	private static final Map<String, String> properties = new HashMap<>();
	private final StackTraceTokenizer lines;

	{
		properties.put("date_format", "yyyy-MM-dd HH:mm:ss,SSS");
		mapper.put("name", 5);
		mapper.put("message", 6);
		mapper.put("date", 1);
		mapper.put("level", 2);
		mapper.put("logger", 3);
		mapper.put("thread", 4);
	}

	public Log4jParser(StackTraceTokenizer lines) {
		lines.stepBack();
		this.lines = lines;
	}

	@Override
	public AnalysisOutput parse() {
		StringBuilder stack = new StringBuilder();
		String inputLine = null;
		boolean hasCB = false;
		AnalysisOutput output = new AnalysisOutput();
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
				output.addResult(ex);
			}
		}
		if (hasCB) {
			output.getResult().get(output.getResult().size() - 1)
					.setStackTrace(stack.toString().substring(stack.lastIndexOf("Caused by:")));
		} else {
			output.getResult().get(output.getResult().size() - 1).setStackTrace(stack.toString());
		}
		return output;
	}

	public static boolean matches(String line) {
		return pattern.matcher(line).matches();
	}

}
