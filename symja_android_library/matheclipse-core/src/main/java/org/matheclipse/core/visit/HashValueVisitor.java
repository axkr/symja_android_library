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

	public HashValueVisitor() {
	}

	public void setUp() {
		// fCurrentDepth = 0;
	}

	public int visit(IAST list) {
		int hash = 0;
		if (list.size() > 1) {
			if (list.arg1().isAST()) {
				IAST temp = (IAST) list.arg1();
				hash = list.head().hashCode() + (31 * temp.head().hashCode() + temp.size()) + list.size();
			} else {
				hash = (31 * list.head().hashCode() + list.size());
			}
		} else {
			if (list.size() == 1) {
				hash = (17 * list.head().hashCode());
			} else {
				// this case shouldn't happen
				hash = 41;
			}
		}
		return hash;
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
