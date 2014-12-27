package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.VisitorCollectionBoolean;

import com.google.common.base.Predicate;

/**
 * Determine the variable symbols from a Symja expression.
 * 
 */
public class ExprVariables {

	public static class VariablesVisitor extends VisitorCollectionBoolean {
		public VariablesVisitor(Collection<IExpr> collection) {
			super(collection);
		}

		public boolean visit(ISymbol symbol) {
			if (symbol.isVariable()) {
				fCollection.add(symbol);
				return true;
			}
			return false;
		}
	}

	public class IsMemberVisitor extends AbstractVisitorBoolean {
		public IsMemberVisitor() {
			super();
		}

		public boolean visit(IAST list) {
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).accept(this)) {
					return true;
				}
			}
			return false;
		}

		public boolean visit(ISymbol symbol) {
			if (symbol.isVariable()) {
				return set.contains(symbol);
			}
			return false;
		}
	}

	private final Set<IExpr> set = new TreeSet<IExpr>();

	/**
	 * Create an empty instance
	 */
	public ExprVariables() {
		super();
	}

	/**
	 * Determine the variable symbols from a Symja expression.
	 */
	public ExprVariables(final IExpr expression) {
		super();
		expression.accept(new VariablesVisitor(set));
	}

	/**
	 * Add the symbol to the set of variables.
	 * 
	 * @param symbol
	 * @return <tt>true</tt> if the underlying set did not already contain the symbol
	 */
	public boolean add(final ISymbol symbol) {
		return set.add(symbol);
	}

	/**
	 * Add the variables of the given expression
	 * 
	 * @param expression
	 */
	public void addVarList(final IExpr expression) {
		expression.accept(new VariablesVisitor(set));
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(IExpr o) {
		return set.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<? extends IExpr> c) {
		return set.containsAll(c);
	}

	/**
	 * @return the varList
	 */
	public IAST getVarList() {
		final Iterator<IExpr> iter = set.iterator();
		final IAST list = List();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	public List<IExpr> getArrayList() {
		final Iterator<IExpr> iter = set.iterator();
		final List<IExpr> list = new ArrayList<IExpr>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	public List<IExpr> appendToList(final List<IExpr> list) {
		final Iterator<IExpr> iter = set.iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	public String[] getVarListAsString() {
		String[] result = new String[set.size()];
		final Iterator<IExpr> iter = set.iterator();
		int i = 0;
		while (iter.hasNext()) {
			result[i++] = iter.next().toString();
		}
		return result;
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	public static Predicate<IExpr> isFree(final ExprVariables exprVar) {
		return new Predicate<IExpr>() {
			final IsMemberVisitor visitor = exprVar.new IsMemberVisitor();
			
			@Override
			public boolean apply(IExpr input) {
				return !input.accept(visitor);
			}
		};
	}

	/**
	 * Check if the expression contains the given number of variables.
	 * 
	 * @param expr
	 * @return <code>true</code> if the expr contains the given number of variables.
	 */
	public boolean isSize(int size) {
		return set.size() == size;
	}

	/**
	 * The number of determined variables.
	 * 
	 * @return
	 */
	public int size() {
		return set.size();
	}

	/**
	 * @param a
	 * @return
	 * @see java.util.Set#toArray(T[])
	 */
	public IExpr[] toArray(IExpr[] a) {
		return set.toArray(a);
	}
}
