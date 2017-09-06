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

/**
 * Calculate a special hash value for <code>HashedOrderlessMatcher</code> methods.
 *
 */
final public class HashValueVisitor extends AbstractVisitorInt {

	public final static HashValueVisitor HASH_VALUE_VISITOR = new HashValueVisitor();

	public HashValueVisitor() {
	}

	/**
	 * Calculate a hash value. Especially if the first argument of the given <code>ast</code> is itself an
	 * <code>AST</code> object.
	 * 
	 * @return the calculated hash value.
	 */
	@Override
	public int visit(IAST ast) {
		final int size = ast.size();
		if (size > 1) {
			if (ast.arg1().isAST()) {
				IAST temp = (IAST) ast.arg1();
				return ast.head().hashCode() + (31 * temp.head().hashCode() + temp.size()) + size;
			}
			return (31 * ast.head().hashCode() + size);
		}
		return (size == 1) ? (17 * ast.head().hashCode()) : 41;
	}

	@Override
	public int visit(IComplex element) {
		return element.hashCode();
	}

	@Override
	public int visit(IComplexNum element) {
		return element.hashCode();
	}

	@Override
	public int visit(IFraction element) {
		return element.hashCode();
	}

	@Override
	public int visit(IInteger element) {
		return element.hashCode();
	}

	@Override
	public int visit(INum element) {
		return element.hashCode();
	}

	@Override
	public int visit(IPattern element) {
		return element.hashCode();
	}

	@Override
	public int visit(IStringX element) {
		return element.hashCode();
	}

	@Override
	public int visit(ISymbol element) {
		return element.hashCode();
	}
}
