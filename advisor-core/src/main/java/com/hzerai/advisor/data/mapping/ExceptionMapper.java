/**
 * 
 */
package com.hzerai.advisor.data.mapping;

import com.hzerai.advisor.data.TransientException;

/**
 * @author Habib Zerai
 *
 */
public interface ExceptionMapper {

	ExceptionEvaluator map(TransientException e);

}
