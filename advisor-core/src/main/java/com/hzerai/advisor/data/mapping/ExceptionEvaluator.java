/**
 * 
 */
package com.hzerai.advisor.data.mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.MatchResult;

import com.hzerai.advisor.data.TransientException;
import com.hzerai.advisor.data.model.PersistentEvent;
import com.hzerai.advisor.parse.internal.stacktrace.StackTraceTokenizer;

/**
 * @author Habib Zerai
 *
 */
public class ExceptionEvaluator {
	protected static final String STRING_SPERATOR = "   |   ";
	public static final String seperator = padRightSpaces(" ", 156, '=');
	private final PersistentEvent event;
	private final TransientException exception;
	private final MatchResult matchResult;
	private ExceptionEvaluator child = null;

	private String hint = null;
	private String todo = null;

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
	}

	public boolean isMapped() {
		return event != null;
	}

	public ExceptionEvaluator getChild() {
		return child;
	}

	public void setChild(ExceptionEvaluator child) {
		this.child = child;
	}

	public PersistentEvent getEvent() {
		return event;
	}

	public TransientException getException() {
		return exception;
	}

	private String output = null;

	public ExceptionEvaluator(TransientException exception, PersistentEvent event, MatchResult matchResult) {
		this.exception = exception;
		this.event = event;
		this.matchResult = matchResult;
		prepareOutput();
	}

	public String getKey() {
		if (output != null && output.matches(".*:(\\d*|\\(confiscated\\))")) {
			return output.substring(0, output.lastIndexOf(':'));
		}
		return output == null ? exception.toString().substring(180) : output;
	}

	private void prepareOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append(padRightSpaces(exception.getName(), 70));
		sb.append(STRING_SPERATOR);

		if (matchResult != null) {
			String todo = event.getTodo();
			String hint = event.getHint();
			int paramsNumber = event.getParamNumber();
			if (hint == null) {
				hint = matchResult.group(0);
			} else {
				for (int i = 1; i <= paramsNumber; i++) {
					hint = hint.replace("{" + i + "}", matchResult.group(i));
				}
			}
			if (todo != null) {
				for (int i = 1; i <= paramsNumber; i++) {
					todo = todo.replace("{" + i + "}", matchResult.group(i));
				}
			}
			this.hint = hint;
			this.todo = todo;
			sb.append(hint);
		} else if (event != null) {
			sb.append(scanStacktrace(event));
		} else {
			sb.append(exception.getMessage());
			this.hint = " No idea!";
			this.todo = " No idea either!";
		}
		output = sb.toString();
	}

	private String prettyOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append("Advisor found a ").append(exception.getName()).append(" that occured ");
		LocalDateTime last = exception.getDate();
		LocalDateTime first = null;
		int chN = 1;
		ExceptionEvaluator ch = child;
		String message = exception.getMessage();
		while (ch != null) {
			first = ch.exception.getDate();
			String chMsg = ch.exception.getMessage();
			if (chMsg != null && !chMsg.trim().isEmpty() && !"null".equals(chMsg.trim())) {
				message = chMsg;
			}
			ch = ch.child;
			chN++;
		}
		if (chN > 1) {
			sb.append(chN).append(" times between ")
					.append(first.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append(" and ")
					.append(last.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		} else {
			sb.append("one time at ").append(last.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		}
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		String localMsg = exception.getMessage();
		if (message != null && localMsg == null || localMsg.trim().isEmpty() || "null".equals(localMsg.trim())) {
			localMsg = message;
		}
		if (localMsg == null || localMsg.isEmpty() || "null".equals(localMsg)) {
			localMsg = "empty";
		}
		sb.append("The exception message was : ").append(localMsg);
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append("What we think happened is : ").append(this.hint);
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append("What you should do is : ").append(this.todo);
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append("Printing occurences : ");
		sb.append(System.lineSeparator()).append("\t");
		sb.append(seperator.substring(44));
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append(padRightSpaces("Occurence date", 19, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Logged by", 30, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Thread name", 30, ' '));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces("Level", 7, ' '));
		sb.append(" ||");
		sb.append(System.lineSeparator()).append("\t");
		sb.append(seperator.substring(44));
		return sb.toString();
	}

	private String scanStacktrace(PersistentEvent event) {
		if (exception.getStackTrace() == null) {
			return event.getHint();
		}
		String[] lines = exception.getStackTrace().split(System.lineSeparator());
		String business_stack = null;
		boolean confiscated = false;
		for (int i = 0; i < lines.length; i++) {
			String l = lines[i];
			int atIndex = l.indexOf("at ");
			if (atIndex < 0) {
				continue;
			}
			l = l.substring(atIndex + 2).trim();
			if(l.contains("//")) {
				l = l.substring(l.indexOf("//")+2);
			}
			if (!l.startsWith("java.") && !l.startsWith("org.") && !l.startsWith("sun.")) {
				if (l.contains("[") && l.contains("]")) {
					business_stack = l.substring(0, l.indexOf("]") + 1).trim();
				} else {
					business_stack = l.substring(0, l.indexOf(")") + 1).trim();
				}
				confiscated = business_stack.contains("Unknown Source");
				break;
			}
		}
		if (business_stack == null && lines.length > 1) {
			business_stack = "";
			String l = lines[1];
			if (l.contains("[") && l.contains("]")) {
				business_stack = l.substring(0, l.indexOf("]") + 1).trim();
			} else {
				business_stack = l.substring(0, l.indexOf(")") + 1).trim();
			}
			confiscated = business_stack.contains("Unknown Source");
		}
		String class_method = "class";
		String business_class = "class";
		String business_method = "method";
		String line = "-1";
		if (business_stack.contains("(")) {
			class_method = business_stack.substring(0, business_stack.indexOf("("));
			business_class = class_method.substring(0, class_method.lastIndexOf("."));
			business_method = class_method.replace(business_class, "").substring(1);
			business_method = "<init>".equals(business_method) ? "Constructor" : business_method;
			line = confiscated ? "(confiscated)"
					: business_stack.substring(business_stack.indexOf(".java:"), business_stack.indexOf(")"))
							.replace(".java:", "");
		}
		String hint = event.getHint().replace("{class}", business_class);
		hint = hint.replace("{method}", business_method);
		hint = hint.replace("{line}", line);
		hint = hint.replace("{message}", this.exception.getMessage());
		String todo = event.getTodo().replace("{class}", business_class);
		todo = todo.replace("{method}", business_method);
		todo = todo.replace("{line}", line);
		todo = todo.replace("{message}", this.exception.getMessage());
		if (hint.contains("{stack}") || todo.contains("{stack}")) {
			StackTraceTokenizer tokenizer = new StackTraceTokenizer(this.exception.getStackTrace());
			String stack = "|| " + System.lineSeparator();
			String inputLine = null;
			int i = 0;
			tokenizer.nextLine();
			while ((inputLine = tokenizer.nextLine()) != null && i < 25) {
				i++;
				stack = stack + "|| " + inputLine + System.lineSeparator();
			}
			stack = stack + "|| ";
			hint = hint.replace("{stack}", stack);
			todo = todo.replace("{stack}", stack);
		}

		if (hint.matches(".*:(\\d*|\\(confiscated\\))")) {
			hint = hint.substring(0, hint.lastIndexOf(':'));
		}
		if (todo.matches(".*:(\\d*|\\(confiscated\\))")) {
			todo = todo.substring(0, todo.lastIndexOf(':'));
		}
		this.hint = hint;
		this.todo = todo;
		return hint + todo;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(seperator.substring(44));
		sb.append(System.lineSeparator()).append("\t");
		sb.append(System.lineSeparator()).append("\t");
		sb.append(System.lineSeparator()).append("\t");
		sb.append(seperator.substring(44));
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append(prettyOutput());
		ExceptionEvaluator ch = this;
		int pos = sb.length();
		while (ch != null) {
			sb.insert(pos, appendChild(ch));
			ch = ch.child;
		}
		return sb.toString();
	}

	private String appendChild(ExceptionEvaluator ch) {
		TransientException e = ch.getException();
		StringBuilder sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("\t");
		sb.append("|| ");
		sb.append(e.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sb.append(STRING_SPERATOR);
		String logger = e.getLogger();
		if (logger != null && logger.length() > 30) {
			logger = "..." + logger.substring(logger.length() - 27);
		}
		sb.append(padRightSpaces(logger, 30));
		sb.append(STRING_SPERATOR);
		String thread = e.getThread();
		if (thread != null && thread.length() > 30) {
			thread = "..." + thread.substring(thread.length() - 27);
		}
		sb.append(padRightSpaces(thread, 30));
		sb.append(STRING_SPERATOR);
		sb.append(padRightSpaces(e.getLevel(), 7));
		sb.append(" ||");
		return sb.toString();
	}

	public String padRightSpaces(Object input, int padSize) {
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
