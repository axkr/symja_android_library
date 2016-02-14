package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cartesian product for multiple lists.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
 */
public class CartesianProduct extends AbstractFunctionEvaluator {

	/**
	 * Cartesian product iterator.
	 * 
	 * @author Heinz Kredel
	 */
	static class CartesianProductIterator implements Iterator<IAST> {

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
		}

		/**
		 * Test for availability of a next tuple.
		 * 
		 * @return true if the iteration has more tuples, else false.
		 */
		@Override
		public synchronized boolean hasNext() {
			return !empty;
		}

		/**
		 * Get next tuple.
		 * 
		 * @return next tuple.
		 */
		@Override
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
		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannnot remove tuples");
		}

	}

	/**
	 * Cartesian product iterable.
	 * 
	 * <br/>
	 * See <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
	 * 
	 * @author Heinz Kredel
	 * @author Axel Kramer (Modifications for Symja)
	 */
	final static class CartesianProductList implements Iterable<IAST> {

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
		@Override
		public Iterator<IAST> iterator() {
			return new CartesianProductIterator(comps, fEmptyResultList);
		}

	}

	public CartesianProduct() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);
		List<IAST> la = new ArrayList<IAST>(ast.size() - 1);
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isList()) {
				la.add((IAST) ast.get(i));
			} else {
				return F.NIL;
			}
		}
		CartesianProductList cpi = new CartesianProductList(la, F.List());
		IAST result = F.List();
		for (IAST iast : cpi) {
			result.add(iast);
		}
		return result;
	}
}