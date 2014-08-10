/*
 * Created on 08.08.2003
 *
*/
package org.matheclipse.core.expression;

/**
 * @deprecated
 */
public class ListProperties {
	final public static int EVAL_FLAGS = 0;
	final public static int PATTERN_MATCHING_FLAGS = 1;

	final private static int MAX_ENTRIES = PATTERN_MATCHING_FLAGS + 1;
	private Object[] array;

	public ListProperties() {
		array = new Object[MAX_ENTRIES];
	}

	/**
	 * @param index
	 * @return
	 */
	public Object get(final int index) {
		return array[index];
	}

	/**
	 * @param object
	 * @param index
	 */
	public void set(final int index, final Object object) {
		array[index] = object;
	}

	/**
	 * 
	 * @param index
	 * @param flags
	 */
	public void addIntObjectFlags(final int index, final int flags) {
		final IntObject iObj = (IntObject) array[index];
		iObj.value |= flags;
	}

	public void clearIntObjectFlags(final int index) {
		final IntObject iObj = (IntObject) array[index];
		iObj.value = 0;
	}
}
