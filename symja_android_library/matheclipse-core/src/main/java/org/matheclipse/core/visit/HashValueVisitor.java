package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class HashValueVisitor extends AbstractVisitorInt {

	// @Override
	// public int visit(IAST list) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	final int fMaxDepth;

	int currentDepth = 0;
	
	public HashValueVisitor() {
		this(1);
	}
	
	public HashValueVisitor(int maxDepth) {
		fMaxDepth = maxDepth;
	}

	public void setUp() {
		currentDepth = 0;
	}

	/**
	 * @see org.matheclipse.core.expression.AST#hashCode()
	 */
	public int visit(IAST list) {
		try {
			++currentDepth;
			if (currentDepth <= fMaxDepth) {
				int hash = 0;
				if (list.size() > 1) {
					hash = (31 * list.get(0).hashCode() + list.get(1).accept(this) + list.size());
				} else {
					if (list.size() == 1) {
						hash = (17 * list.get(0).hashCode());
					} else {
						// this case shouldn't happen
						hash = 41;
					}
				}
				return hash;
			} else {
				return (31 * list.head().hashCode() + list.size());
			}
		} finally {
			--currentDepth;
		}
	}

	public int visit(IComplex element) {
		return element.hashCode();
	}

	public int visit(IComplexNum element) {
		return element.hashCode();
	}

	public int visit(IFraction element) {
		return element.hashCode();
	}

	public int visit(IInteger element) {
		return element.hashCode();
	}

	public int visit(INum element) {
		return element.hashCode();
	}

	public int visit(IPattern element) {
		return element.hashCode();
	}

	public int visit(IStringX element) {
		return element.hashCode();
	}

	public int visit(ISymbol element) {
		return element.hashCode();
	}
}
