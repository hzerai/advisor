/**
 * 
 */
package com.hzerai.advisor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;

import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.session.Processor;
import com.hzerai.advisor.session.Session;
import com.hzerai.advisor.session.SessionBuilder;
import com.hzerai.advisor.session.SessionMode;

/**
 * @author Habib Zerai
 *
 */
public class Main {

	public static void main(String... args) throws IOException, NotFoundException {

		String base_dir = System.getProperty("user.dir");
		if (base_dir.endsWith(File.separator + "bin")) {
			base_dir = base_dir.substring(0, base_dir.length() - 3);
		} else {
			base_dir = base_dir + File.separator;
		}
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(
				base_dir + File.separator + "conf" + File.separator + "config.properties")) {
			props.load(fis);
			String sep = Processor.seperator.substring(70);
			System.out.println();
			System.out.println("\t  " + sep);
			System.out.println("\t " + padRightSpaces("|| ", 86) + "||");
			System.out.println("\t " + padRightSpaces("|| Printing configuration : ", 86) + "||");
			System.out.println("\t " + padRightSpaces("|| ", 86) + "||");
			props.forEach((p, v) -> System.out.println("\t " + padRightSpaces("|| " + p + " : " + v, 86) + "||"));
			System.out.println("\t " + padRightSpaces("|| ", 86) + "||");
			System.out.println("\t  " + sep);
			System.out.println();
		}
		LocalDateTime fromDate = null;
		String fromDateString = props.getProperty("fromdate");
		if (fromDateString != null) {
			try {
				fromDate = LocalDateTime.parse(fromDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (DateTimeParseException e) {
				System.err.println(
						"Date input should have the format yyyy-MM-dd HH:mm:ss, your input was " + fromDateString);
			}
		}

		LocalDateTime toDate = null;
		String toDateString = props.getProperty("todate");
		if (toDateString != null) {
			try {
				toDate = LocalDateTime.parse(toDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (DateTimeParseException e) {
				System.err.println(
						"Date input should have the format yyyy-MM-dd HH:mm:ss, your input was " + toDateString);
			}
		}
		Session session = SessionBuilder.mode(SessionMode.Detached).database(props.getProperty("database"))
				.sourcePath(props.getProperty("source")).outputFile(props.getProperty("outputfile")).toDate(toDate)
				.fromDate(fromDate).recursive().build();
		session.print();
	}

	public static String padRightSpaces(Object input, int padSize) {
		return padRightSpaces(input, padSize, ' ');
	}

	public static String padRightSpaces(Object input, int padSize, char ch) {
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
