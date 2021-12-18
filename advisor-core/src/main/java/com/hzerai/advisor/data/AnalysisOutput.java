/**
 * 
 */
package com.hzerai.advisor.data;

import static com.hzerai.advisor.data.TransientException.STRING_SPERATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Habib Zerai
 *
 */
public class AnalysisOutput {

	private List<TransientException> exceptions = new ArrayList<>();
	private String logFile;

	public List<TransientException> getResult() {
		return exceptions;
	}

	public void addResult(TransientException ex) {
		exceptions.add(ex);
	}

	public void setLogfile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFile() {
		return this.logFile;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String seperator = padRightSpaces("", 500, '-');
		sb.append(seperator);
		sb.append(System.lineSeparator());
		sb.append("| Logging File input =====> ").append(logFile);
		sb.append(System.lineSeparator());
		sb.append(seperator);
		sb.append(System.lineSeparator());
		sb.append(padRightSpaces("|  Exception date", 25, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Logged by", 70, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Thread name", 50, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Log level", 15, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Exception name", 70, ' '));
		sb.append(STRING_SPERATOR);
		sb.append("Message of the exception");
		sb.append(System.lineSeparator());
		sb.append(seperator);
		this.exceptions.forEach(ex -> {
			sb.append(System.lineSeparator());
			sb.append("| ");
			sb.append(ex);
			sb.append(System.lineSeparator());
			sb.append(seperator);
		});
		sb.append(System.lineSeparator() + System.lineSeparator() + seperator + System.lineSeparator()
				+ System.lineSeparator());
		return sb.toString();
	}

	public String padRightSpaces(Object input, int padSize, char ch) {
		String inputString = String.valueOf(input);
		if (inputString.length() >= padSize) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(inputString);
		while (sb.length() < padSize) {
			sb.append(ch);
		}
		return sb.toString();
	}

}
