/**
 * 
 */
package com.hzerai.advisor.data.model;

import java.util.regex.Pattern;

import com.hzerai.advisor.util.regex.RegexUtility;

/**
 * @author Habib Zerai
 *
 */
public class PersistentEvent {

	private String messageRegex;
	private String hint;
	private String todo;
	private String moduleName;
	private String wikiLink;
	private String compiledRegex;
	private boolean debug_source = false;
	private int param_number = 0;
	private String ifExistInStack = null;
	private boolean def = false;
	private String ignorePackage = "java.,org.,sun.";

	public String getIgnorePackage() {
		return ignorePackage;
	}

	public void setIgnorePackage(String ignorePackage) {
		this.ignorePackage = ignorePackage;
	}

	public boolean isDebugSource() {
		return debug_source;
	}

	public String getIfExistInStack() {
		return ifExistInStack;
	}

	public void setIfExistInStack(String ifExistInStack) {
		this.ifExistInStack = ifExistInStack;
	}

	public boolean isDef() {
		return def;
	}

	public void setDef(boolean def) {
		this.def = def;
	}

	public String getMessageRegex() {
		return messageRegex;
	}

	public void setMessageRegex(String messageRegex) {
		if (messageRegex != null) {
			String specialCharacters = "[()*+\\[\\]^|]";
			Pattern p = Pattern.compile(specialCharacters);
			this.compiledRegex = p.matcher(messageRegex).replaceAll(".");
			if (this.compiledRegex.indexOf('{') > -1 && this.compiledRegex.indexOf('}') > 0) {
				Object[] o = RegexUtility.transformParamToRE(new Object[] { this.compiledRegex, 0 }, 0);
				this.compiledRegex = (String) o[0];
				param_number = (int) o[1];
			}
			this.compiledRegex = ".*" + this.compiledRegex + ".*";
		}

		this.messageRegex = this.compiledRegex;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		debug_source = debug_source || RegexUtility.containsKeyWords(hint);
		this.hint = hint;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		debug_source = debug_source || RegexUtility.containsKeyWords(todo);
		this.todo = todo;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getWikiLink() {
		return wikiLink;
	}

	public String getCompiledRegex() {
		return compiledRegex;
	}

	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}

	public int getParamNumber() {
		return param_number;
	}

	@Override
	public String toString() {
		return "Event [messageRegex=" + messageRegex + ", hint=" + hint + ", todo=" + todo + ", moduleName="
				+ moduleName + ", wikiLink=" + wikiLink + "]";
	}

}
