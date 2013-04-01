package org.matheclipse.generic.util;

import java.util.ArrayList;

import org.matheclipse.generic.nested.INestedListElement;
import org.matheclipse.generic.nested.NestedAlgorithms;

/**
 * Class for generating (nested) <code>java.util.ArrayList</code>s. <br/>
 * I.e. in the nested case, for an <code>java.util.ArrayList</code>, which
 * contains elements of type <code>T</code> and of type
 * <code>java.util.ArrayList&lt;T&gt;</code>
 */
public class ArrayListAlgorithms<T extends INestedListElement, L extends ArrayList<T> & INestedListElement> extends
		NestedAlgorithms<T, L> {

	final Class<L> fType;

	public ArrayListAlgorithms(Class<L> type) {
		fType = type;
	}

	/**
	 * Returns a shallow copy of the <code>list</code> instance.
	 * 
	 * @return a clone of this <code>list</code> instance.
	 */
	@SuppressWarnings("unchecked")
	public L clone(L list) {
		return (L) list.clone();
	}

	/**
	 * Create a copy of the given list, which only contains the head element of
	 * the list (i.e. the element with index 0).
	 */
	@SuppressWarnings("unchecked")
	public L newInstance(L list) {
		// same as clone for ArrayLists
		return (L) new ArrayList<T>();
	}

	public boolean isInstance(INestedListElement object) {
		return fType.isInstance(object);
	}

}
