package org.matheclipse.generic.combinatoric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.generic.nested.INestedList;
import org.matheclipse.generic.nested.INestedListElement;

/**
 * Cartesian product iterable.
 * 
 * <br/>
 * See <a
 * href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
 * 
 * @author Heinz Kredel
 * @author Axel Kramer (Modifications for MathEclipse)
 */
public class CartesianProductList<T extends INestedListElement, L extends List<T> & INestedListElement> implements Iterable<L> {

	/**
	 * data structure.
	 */
	public final List<L> comps;

	private final L fEmptyResultList;

	private final INestedList<T, L> fCopier;

	/**
	 * CartesianProduct constructor.
	 * 
	 * @param comps
	 *          components of the cartesian product.
	 */
	public CartesianProductList(List<L> comps, L emptyResultList, INestedList<T, L> copier) {
		if (comps == null) {
			throw new IllegalArgumentException("null components not allowed");
		}
		this.comps = comps;
		this.fEmptyResultList = emptyResultList;
		this.fCopier = copier;
	}

	/**
	 * Get an iterator over subsets.
	 * 
	 * @return an iterator.
	 */
	public Iterator<L> iterator() {
		return new CartesianProductIterator<T, L>(comps, fEmptyResultList, fCopier);
	}

}

/**
 * Cartesian product iterator.
 * 
 * @author Heinz Kredel
 */
class CartesianProductIterator<T extends INestedListElement, L extends List<T> & INestedListElement> implements Iterator<L> {

	/**
	 * data structure.
	 */
	final List<L> comps;

	final List<Iterator<T>> compit;

	L current;

	boolean empty;

	final private INestedList<T, L> fCopier;

	/**
	 * CartesianProduct iterator constructor.
	 * 
	 * @param comps
	 *          components of the cartesian product.
	 */
	public CartesianProductIterator(List<L> comps, L emptyResultList, INestedList<T, L> copier) {
		if (comps == null) {
			throw new IllegalArgumentException("null comps not allowed");
		}
		this.fCopier = copier;
		this.comps = comps;
		current = emptyResultList;
		compit = new ArrayList<Iterator<T>>(comps.size());
		empty = false;
		for (L ci : comps) {
			Iterator<T> it = ci.iterator();
			if (!it.hasNext()) {
				empty = true;
				current.clear();
				break;
			}
			current.add(it.next());
			compit.add(it);
		}
		// System.out.println("current = " + current);
	}

	/**
	 * Test for availability of a next tuple.
	 * 
	 * @return true if the iteration has more tuples, else false.
	 */
	public synchronized boolean hasNext() {
		return !empty;
	}

	/**
	 * Get next tuple.
	 * 
	 * @return next tuple.
	 */
	public synchronized L next() {
		if (empty) {
			throw new RuntimeException("invalid call of next()");
		}
		// IAST res = (IAST) current.clone();
		L res = fCopier.clone(current);
		// search iterator which hasNext
		int i = compit.size() - 1;
		for (; i >= 0; i--) {
			Iterator<T> iter = compit.get(i);
			if (iter.hasNext()) {
				break;
			}
		}
		if (i < 0) {
			empty = true;
			return res;
		}
		// update iterators
		for (int j = i + 1; j < compit.size(); j++) {
			Iterator<T> iter = comps.get(j).iterator();
			compit.set(j, iter);
		}
		// update current
		for (int j = i; j < compit.size(); j++) {
			Iterator<T> iter = compit.get(j);
			T el = iter.next();
			current.set(j + 1, el);
		}
		return res;
	}

	/**
	 * Remove a tuple if allowed.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException("cannnot remove tuples");
	}

}
