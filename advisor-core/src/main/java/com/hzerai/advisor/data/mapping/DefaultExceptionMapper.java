/**
 * 
 */
package com.hzerai.advisor.data.mapping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.MatchResult;
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
		if (er == null) {
			return null;
		}
		PersistentException ec = db.findByName(er.getName());
		if (ec == null) {
			return findEVFromCB(er);
		}
		Map<String, Object> result = getMappedException(er, ec);
		if (result.containsKey("event") && result.containsKey("matcher")) {
			return new ExceptionEvaluator(er, (PersistentEvent) result.get("event"),
					(MatchResult) result.get("matcher"));
		} else if (result.containsKey("event")) {
			return new ExceptionEvaluator(er, (PersistentEvent) result.get("event"), null);
		} else {
			return findEVFromCB(er);
		}
	}

	private Map<String, Object> getMappedException(TransientException er, PersistentException ec) {
		Map<String, Object> result = new HashMap<>();
		Pattern pattern;
		Matcher matcher;
		PersistentEvent def = null;
		for (PersistentEvent event : ec.getEvents()) {
			if(event.isDef()) {
				def = event;
			}
			String eventRegex = event.getCompiledRegex();
			eventRegex = eventRegex == null ? event.getMessageRegex() : eventRegex;
			if (eventRegex != null) {
				pattern = Pattern.compile(eventRegex);
				matcher = pattern.matcher(er.getMessage());
				if (matcher.matches()) {
					result.put("event", event);
					result.put("matcher", matcher.toMatchResult());
					return result;
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
					result.put("event", event);
					return result;
				}
			}
		}
		if(def != null) {
			result.put("event", def);
		}
		return result;
	}

	private ExceptionEvaluator findEVFromCB(TransientException er) {
		PersistentException ec = null;
		Iterator<String> it = er.getCausedBy().iterator();
		String cb;
		String msg = er.getMessage();
		while (it.hasNext()) {
			cb = it.next().trim();
			cb = cb.replace("Caused by: ", "");
			if (db.findByName(cb.trim()) == null && cb.contains("Exception")) {
				msg = cb.substring(cb.indexOf("Exception") + (cb.contains("Exception:") ? 10 : 9)).trim();
				cb = cb.substring(0, cb.indexOf("Exception") + 9);
			} else if (cb.contains("Error")) {
				msg = cb.substring(cb.indexOf("Error") + (cb.contains("Error:") ? 6 : 5)).trim();
				cb = cb.substring(0, cb.indexOf("Error") + 5);
			}
			ec = db.findByName(cb);
			er.setMessage(msg);
			er.setName(cb);
			if (ec != null) {
				Map<String, Object> result = getMappedException(er, ec);
				if (result.containsKey("event") && result.containsKey("matcher")) {
					return new ExceptionEvaluator(er, (PersistentEvent) result.get("event"),
							(MatchResult) result.get("matcher"));
				} else if (result.containsKey("event")) {
					return new ExceptionEvaluator(er, (PersistentEvent) result.get("event"), null);
				} else {
					continue;
				}
			}
		}
		return new ExceptionEvaluator(er, null, null);
	}

}
