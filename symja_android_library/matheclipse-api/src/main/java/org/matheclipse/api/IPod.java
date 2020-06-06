package org.matheclipse.api;

import org.matheclipse.core.eval.EvalEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface IPod {
	public final static short DOCUMENTATION = 1;
	public final static short ELEMENT_DATA = 2;
	public final static short CONSTANT_DATA = 3;
	public final static short LIST_DATA = 4;
	/**
	 * Get the type of this pod
	 * 
	 * @return
	 */
	public short podType();

	/**
	 * Get the key word of this pod
	 * 
	 * @return
	 */
	public String keyWord();

	/**
	 * Create and add JSON output of this object.
	 * 
	 * @param mapper
	 * @param podsArray
	 * @return the number of pods; <code>0</code> if no pod was appended
	 */
	public int addJSON(ObjectMapper mapper, ArrayNode podsArray,int formats, EvalEngine engine);
}
