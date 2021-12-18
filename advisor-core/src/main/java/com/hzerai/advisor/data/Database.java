/**
 * 
 */
package com.hzerai.advisor.data;

import java.util.Collection;

import com.hzerai.advisor.data.model.PersistentException;

/**
 * @author Habib Zerai
 *
 */
public interface Database {
	
	Collection<PersistentException> findAll();
	
	PersistentException findByName(String name);
	
	void save(PersistentException exception);

}
