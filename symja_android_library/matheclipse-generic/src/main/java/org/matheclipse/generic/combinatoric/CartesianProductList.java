package org.matheclipse.generic.combinatoric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cartesian product iterable.
 * 
 * <br/>
 * See <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
 * 
 * @author Heinz Kredel
 * @author Axel Kramer (Modifications for MathEclipse)
 */
public class CartesianProductList implements Iterable<IAST> {

	/**
	 * data structure.
	 */
	public final List<IAST> comps;

	private final IAST fEmptyResultList;

	/**
	 * CartesianProduct constructor.
	 * 
	 * @param comps
	 *            components of the cartesian product.
	 */
	public CartesianProductList(List<IAST> comps, IAST emptyResultList) {
		if (comps == null) {
			throw new IllegalArgumentException("null components not allowed");
		}
		this.comps = comps;
		this.fEmptyResultList = emptyResultList;
	}

	/**
	 * Get an iterator over subsets.
	 * 
	 * @return an iterator.
	 */
	public Iterator<IAST> iterator() {
		return new CartesianProductIterator(comps, fEmptyResultList);
	}

}

/**
 * Cartesian product iterator.
 * 
 * @author Heinz Kredel
 */
class CartesianProductIterator implements Iterator<IAST> {

	/**
	 * data structure.
	 */
	final List<IAST> comps;

	final List<Iterator<IExpr>> compit;

	IAST current;

	boolean empty;

	/**
	 * CartesianProduct iterator constructor.
	 * 
	 * @param comps
	 *            components of the cartesian product.
	 */
	public CartesianProductIterator(List<IAST> comps, IAST emptyResultList) {
		if (comps == null) {
			throw new IllegalArgumentException("null comps not allowed");
		}
		this.comps = comps;
		current = emptyResultList;
		compit = new ArrayList<Iterator<IExpr>>(comps.size());
		empty = false;
		for (IAST ci : comps) {
			Iterator<IExpr> it = ci.iterator();
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
	public synchronized IAST next() {
		if (empty) {
			throw new RuntimeException("invalid call of next()");
		}
		// IAST res = (IAST) current.clone();
		IAST res = current.clone();
		// search iterator which hasNext
		int i = compit.size() - 1;
		for (; i >= 0; i--) {
			Iterator<IExpr> iter = compit.get(i);
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
			Iterator<IExpr> iter = comps.get(j).iterator();
			compit.set(j, iter);
		}
		// update current
		for (int j = i; j < compit.size(); j++) {
			Iterator<IExpr> iter = compit.get(j);
			IExpr el = iter.next();
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
