package org.matheclipse.core.visit;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Visit every node of an <code>IExpr</code> expression.
 */
public abstract class VisitorExpr extends AbstractVisitor {

	public VisitorExpr() {
	}

	/**
	 * Visit an <code>IAST</code> with the given head and no arguments (i.e. <code>head[]</code>).
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	public IExpr visit1(IExpr head) {
		return F.NIL;
	}

	/**
	 * Visit an <code>IAST</code> with the given head and one argument (i.e. <code>head[arg1]</code>).
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	public IExpr visit2(IExpr head, IExpr arg1) {
		return F.NIL;
	}

	/**
	 * Visit an <code>IAST</code> with the given head and two arguments (i.e. <code>head[arg1, arg2]</code>).
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
		return F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IASTMutable ast) {
		IExpr temp = F.NIL;
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
		return temp.orElseGet(() -> visitAST(ast));
	}

	/**
	 * 
	 * @return the cloned <code>IAST</code> with changed evaluated subexpressions, or <code>F.NIL</code>, if no
	 *         evaluation is possible
	 */
	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		// IASTAppendable result = F.NIL;
		int i = 1;
		int size = ast.size();
		while (i < size) {
			temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				IASTMutable result = ast.setAtCopy(i++, temp);
				ast.forEach(i, size, (x, j) -> {
					IExpr t = x.accept(this);
					if (t.isPresent()) {
						result.set(j, t);
					}
				});
				return result;
			}
			i++;
		}
		return F.NIL;
	}
}