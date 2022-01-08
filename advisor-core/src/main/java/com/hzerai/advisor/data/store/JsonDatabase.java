/**
 * 
 */
package com.hzerai.advisor.data.store;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzerai.advisor.data.Database;
import com.hzerai.advisor.data.model.PersistentException;
import com.hzerai.advisor.exception.DataException;

/**
 * @author Habib Zerai
 *
 */
public class JsonDatabase implements Database {

	private final Map<String, PersistentException> exceptions;

	/**
	 * @return
	 */
	public JsonDatabase() {
		try {
			exceptions = new TreeMap<>();

			ObjectMapper mapper = new ObjectMapper();
			List<PersistentException> fromDb = mapper.readValue(
					this.getClass().getClassLoader().getResourceAsStream("db.json"),
					new TypeReference<List<PersistentException>>() {
					});
			for (PersistentException e : fromDb) {
				String name = e.getName();
				if (exceptions.containsKey(name)) {
					exceptions.get(name).getEvents().addAll(e.getEvents());
				} else {
					exceptions.put(name, e);
				}
			}
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	@Override
	public Collection<PersistentException> findAll() {
		return Collections.unmodifiableCollection(exceptions.values());
	}

	@Override
	public PersistentException findByName(String name) {
		return exceptions.get(name);
	}

	@Override
	public void save(PersistentException exception) {
		exceptions.put(exception.getName(), exception);
	}

}
