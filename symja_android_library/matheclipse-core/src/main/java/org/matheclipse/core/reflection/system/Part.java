package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Part implements IFunctionEvaluator {
	public Part() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() >= 3) {
//			if (ast.get(1) == F.Null) {
//				System.out.println("Part argument is null");
//			}
			if (ast.get(1).isAST()) {
				return getPart(ast.getAST(1), ast, 2);
			}
		}
		return null;
	}

	private IExpr getPart(final IExpr expr1, final IAST ast, int pos) {
		
		if (!expr1.isAST()) {
			throw new WrongArgumentType(ast, expr1, pos, "Wrong argument for Part[] function. Function or list expected.");
		}
		IAST arg1 = (IAST) expr1;
		final IExpr arg2 = ast.get(pos);
		int p1 = pos + 1;
		IExpr temp = null;
		if (arg2.isSignedNumber()) {
			final int indx = Validate.checkIntType(ast, pos, Integer.MIN_VALUE);
			IExpr ires = null;
			ires = getIndex(arg1, indx);

			if (p1 < ast.size()) {
				return getPart(ires, ast, p1);
			}
			return ires;
		} else if (arg2.isList()) {
			final IAST lst = (IAST) arg2;
			final IAST result = F.List();

			for (int i = 1; i < lst.size(); i++) {
				final IExpr expr = lst.get(i);
				if (expr.isInteger()) {
					IExpr ires = null;

					final int indx = Validate.checkIntType(lst, i, Integer.MIN_VALUE);
					ires = getIndex(arg1, indx);
					if (ires == null) {
						return null;
					}
					if (p1 < ast.size()) {
						temp = getPart(ires, ast, p1);
						result.add(temp);
					} else {
						result.add(ires);
					}
				}
			}
			return result;
		}
		throw new WrongArgumentType(ast, arg2, pos, "Wrong argument for Part[] function");
	}

	/**
	 * Get the element stored at the given <code>position</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	IExpr getIndex(IAST ast, int position) {
		if (position < 0) {
			position = ast.size() + position;
		}
		if ((position < 0) || (position >= ast.size())) {
			throw new WrappedException(new IndexOutOfBoundsException("Part[] index " + position + " of " + ast.toString()
					+ " is out of bounds."));
		}
		return ast.get(position);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDREST);
	}
}
