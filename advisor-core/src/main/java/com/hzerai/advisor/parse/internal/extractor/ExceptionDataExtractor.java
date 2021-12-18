/**
 * 
 */
package com.hzerai.advisor.parse.internal.extractor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.MatchResult;

import com.hzerai.advisor.data.TransientException;
import com.hzerai.advisor.exception.DataParseException;
import com.hzerai.advisor.data.LogLevel;

/**
 * @author Habib Zerai
 *
 */
public class ExceptionDataExtractor {

	public static TransientException extract(Map<String, String> parserProperties, MatchResult mr,
			Map<String, Integer> mapper) {
		TransientException result = new TransientException();
		if(mapper.containsKey("name")) {
			result.setName(mr.group(mapper.get("name")));
		}
		result.setDate(extractDate(mr.group(mapper.get("date")), parserProperties.get("date_format")));
		result.setLevel(extractLevel(mr.group(mapper.get("level"))));
		if(mapper.containsKey("name")) {
			result.setMessage(mr.group(mapper.get("message")));
		}
		result.setThread(mr.group(mapper.get("thread")));
		result.setLogger(mr.group(mapper.get("logger")));
		return result;
	}

	private static LocalDateTime extractDate(String dateString, String dateFormat) {
		if (dateString == null) {
			return null;
		}
		try {
			return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
		} catch (DateTimeParseException e) {
			throw new DataParseException(e);
		}
	}

	private static LogLevel extractLevel(String level) {

		return LogLevel.ERROR;
	}

}
