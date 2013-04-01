package org.matheclipse.generic.nested;

import java.util.List;


/**
 * Interface for nested lists. I.e. for lists which contain objects of interface
 * type <code>java.util.List<T></code> and <code>T</code>.
 * 
 * The derived instances have to define the clone and copy semantics.
 */
public interface INestedList<T extends INestedListElement, L extends List<T> & INestedListElement> {

	/**
	 * Cast the <code>object</code> to the list type <code>L</code>.
	 * 
	 * @param object
	 * @return
	 */
	public abstract L cast(T obj);

	/**
	 * Cast the <code>list</code> to type <code>T</code>.
	 * 
	 * @param list
	 * @return
	 */
	public abstract T castList(L list);

	/**
	 * Return a shallow copy of the <code>list</code> instance.
	 * 
	 * @return a clone of this <code>list</code> instance.
	 */
	public abstract L clone(L list);

	/**
	 * Is the <code>object</code> an instance of type <code>L</code>?
	 * 
	 * @param obj
	 * @return
	 */
	public abstract boolean isInstance(T object);

	/**
	 * Create a new empty copy of the <code>list</code>. <br/>
	 * <br/>
	 * The new list can contain some shallow copied elements of the list (i.e. the
	 * element with index 0, which could for example be interpreted as a special
	 * header element). The derived <code>NestedAlgorithms</code> implementation
	 * decides how to copy the list elements exactly.
	 */
	public abstract L newInstance(L list);

}