/**
 * 
 */
package com.hzerai.advisor.data.mapping;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hzerai.advisor.data.Database;
import com.hzerai.advisor.data.TransientException;
import com.hzerai.advisor.data.model.PersistentEvent;
import com.hzerai.advisor.data.model.PersistentException;

/**
 * @author Habib Zerai
 *
 */
public class DefaultExceptionMapper implements ExceptionMapper {

	private final Database db;

	public DefaultExceptionMapper(Database db) {
		this.db = db;
	}

	@Override
	public ExceptionEvaluator map(TransientException er) {
		Pattern pattern;
		Matcher matcher;
		if (er == null) {
			return null;
		}
		PersistentException ec = db.findByName(er.getName());
		if (ec == null) {
			Iterator<String> it = er.getCausedBy().iterator();
			String cb;
			String msg = "";
			while (it.hasNext()) {
				cb = it.next().trim();
				cb = cb.replace("Caused by: ", "");
				if (db.findByName(cb.trim()) == null && cb.contains("Exception")) {
					msg = cb.substring(cb.indexOf("Exception") + (cb.contains("Exception:") ? 10 : 9)).trim();
					cb = cb.substring(0, cb.indexOf("Exception") + 9);
				}
				ec = db.findByName(cb);
				if (ec != null) {
					er.setMessage(msg);
					er.setName(cb);
					break;
				}
			}
		}
		if (ec != null) {
			PersistentEvent def = null;
			for (PersistentEvent event : ec.getEvents()) {
				if (event.isDef()) {
					def = event;
				}
				String eventRegex = event.getCompiledRegex();
				eventRegex = eventRegex == null ? event.getMessageRegex() : eventRegex;
				if (eventRegex != null) {
					pattern = Pattern.compile(eventRegex);
					matcher = pattern.matcher(er.getMessage());
					if (matcher.matches()) {
						return new ExceptionEvaluator(er, event, matcher.toMatchResult());
					}
				} else if (event.getIfExistInStack() != null && er.getStackTrace() != null) {
					boolean matches = true;
					for (String db : event.getIfExistInStack().split(",")) {
						matches = matches && er.getStackTrace().contains(db);
						if (!matches) {
							break;
						}
					}
					if (matches) {
						return new ExceptionEvaluator(er, event, null);
					}
				}
			}
			return new ExceptionEvaluator(er, def, null);
		}
		return new ExceptionEvaluator(er, null, null);
	}

}
