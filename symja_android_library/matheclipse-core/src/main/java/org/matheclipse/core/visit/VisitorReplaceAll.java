package org.matheclipse.core.visit;

import org.matheclipse.core.generic.Functors;
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

import com.google.common.base.Function;

/**
 * Replace all occurrences of expressions where the given
 * <code>function.apply()</code> method returns a non <code>null</code> value.
 * The visitors <code>visit()</code> methods return <code>null</code> if no
 * substitution occurred.
 */
public class VisitorReplaceAll extends VisitorExpr {
	final Function<IExpr,IExpr> fFunction;
	final int fOffset;

	public VisitorReplaceAll(Function<IExpr,IExpr>  function) {
		this(function, 0);
	}

	public VisitorReplaceAll(Function<IExpr,IExpr> function, int offset) {
		super();
		this.fFunction = function;
		this.fOffset = offset;
	}

	public VisitorReplaceAll(IAST ast) {
		this(ast, 0);
	}

	public VisitorReplaceAll(IAST ast, int offset) {
		super();
		this.fFunction = Functors.rules(ast);
		this.fOffset = offset;
	}

	public IExpr visit(IInteger element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IFraction element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IComplex element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(INum element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IComplexNum element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(ISymbol element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IPattern element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public IExpr visit(IStringX element) {
		return fFunction.apply(element);
	}

	@Override
	public IExpr visit(IAST ast) {
		IExpr temp = fFunction.apply(ast);
		if (temp != null) {
			return temp;
		}
		return visitAST(ast);
	}

	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		IAST result = null;
		int i = fOffset;
		while (i < ast.size()) {
			temp = ast.get(i).accept(this);
			if (temp != null) {
				// something was evaluated - return a new IAST:
				result = ast.clone();
				result.set(i++, temp);
				break;
			}
			i++;
		}
		while (i < ast.size()) {
			temp = ast.get(i).accept(this);
			if (temp != null) {
				result.set(i, temp);
			}
			i++;
		}
		return result;
	}
}
