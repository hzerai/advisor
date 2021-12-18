/**
 * 
 */
package com.hzerai.advisor.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Habib Zerai
 *
 */
public class PersistentException {

	private String name;
	private List<PersistentEvent> events = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersistentEvent> getEvents() {
		return events;
	}

	public void setEvents(List<PersistentEvent> events) {
		this.events = events;
	}

	public void addEvent(PersistentEvent event) {
		this.events.add(event);
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "ExceptionConfig [name=" + name + ", events=" + events + "]";

		}
	}

}
