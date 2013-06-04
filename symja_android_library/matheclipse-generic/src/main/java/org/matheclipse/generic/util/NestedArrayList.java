package org.matheclipse.generic.util;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Resizable-array implementation (a wrapper for java.util.ArrayList).<br/>
 * Adds a special <code>head</code> object to the ArrayList implementation
 * 
 * @see Head
 * @see java.util.ArrayList
 */
public class NestedArrayList<E extends IElement> extends ArrayList<E> implements IElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9129782518377195860L;

	/**
	 * Creates an empty NestedArrayList
	 * 
	 */
	public NestedArrayList() {
		super();
	}

	public NestedArrayList(final int initialCapacity) {
		super(initialCapacity);
	}

	public NestedArrayList(Collection<? extends E> c) {
		super(c);
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator and sets the given
	 * header.
	 * 
	 * @param c
	 *          the collection whose elements are to be placed into this list.
	 * @throws NullPointerException
	 *           if the specified collection is null.
	 */
	public NestedArrayList(final Collection<E> c, final E head) {
		super(c.size() + 1);
		add(head);
		addAll(c);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof NestedArrayList) {
			return super.equals((NestedArrayList)obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode()*97;
	}
	
	public NestedArrayList<E> copyHead() {
		final NestedArrayList<E> list = new NestedArrayList<E>(size());
		list.set(0, get(0));
		return list;
	}

}
