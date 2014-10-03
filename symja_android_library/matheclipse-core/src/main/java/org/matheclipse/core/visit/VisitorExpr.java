package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Visit every node of an <code>IExpr</code> expression.
 */
public class VisitorExpr extends AbstractVisitor<IExpr> {

	public VisitorExpr() {
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IInteger element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IFraction element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IComplex element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(INum element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IComplexNum element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(ISymbol element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IPattern element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IStringX element) {
		return null;
	}

	/**
	 * Visit an <code>IAST</code> with the given head and no arguments (i.e. <code>head[]</code>).
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit1(IExpr head) {
		return null;
	}

	/**
	 * Visit an <code>IAST</code> with the given head and one argument (i.e. <code>head[arg1]</code>).
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit2(IExpr head, IExpr arg1) {
		return null;
	}

	/**
	 * Visit an <code>IAST</code> with the given head and two arguments (i.e. <code>head[arg1, arg2]</code>).
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IAST ast) {
		IExpr temp = null;
		switch (ast.size()) {
		case 1:
			temp = visit1(ast.head());
			break;
		case 2:
			temp = visit2(ast.head(), ast.arg1());
			break;
		case 3:
			temp = visit3(ast.head(), ast.arg1(), ast.arg2());
			break;
		}
		if (temp != null) {
			return temp;
		}
		return visitAST(ast);
	}

	/**
	 * 
	 * @return the cloned <code>IAST</code> with changed evaluated subexpressions, or <code>null</code>, if no evaluation is
	 *         possible
	 */
	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		IAST result = null;
		int i = 1;
		while (i < ast.size()) {
			temp = ast.get(i).accept(this);
			if (temp != null) {
				// something was evaluated - return a new IAST:
				result = ast.clone();
				for (int j = 1; j < i; j++) {
					result.set(j, ast.get(j));
				}
				result.set(i++, temp);
				break;
			}
			i++;
		}
		if (result != null) {
			while (i < ast.size()) {
				temp = ast.get(i).accept(this);
				if (temp != null) {
					result.set(i, temp);
				} else {
					result.set(i, ast.get(i));
				}
				i++;
			}
		}
		return result;
	}
}