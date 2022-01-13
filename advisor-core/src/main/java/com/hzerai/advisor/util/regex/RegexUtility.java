/**
 * 
 */
package com.hzerai.advisor.util.regex;

/**
 * @author Habib Zerai
 *
 */
public class RegexUtility {

	public static boolean containsKeyWords(String text) {
		if (text != null) {
			return text.contains("{class}") || text.contains("{method}") || text.contains("{line}");
		}
		return false;
	}

	public static Object[] transformParamToRE(Object[] result, int start) {
		String text = (String) result[0];
		if (text != null && !text.isEmpty() && text.indexOf('{') > -1 && text.indexOf('}') > 0) {
			start = text.indexOf('{', start);
			int end = text.indexOf('}', start + 1) + 1;
			String tbr = text.substring(start, end);
			if (isNumber(tbr.substring(1, tbr.length() - 1))) {
				text = text.replace(tbr, "(.*)");
			}
			result[0] = text;
			result[1] = ((int) result[1]) + 1;
			return transformParamToRE(result, end + 1);
		}

		return result;
	}

	public static boolean isNumber(String text) {
		if (text == null || text.isEmpty()) {
			return false;
		}
		for (char c : text.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

}
