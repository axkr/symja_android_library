package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Part extends AbstractCoreFunctionEvaluator {
	public Part() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() >= 3) {
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				IAST evaledAST = F.NIL;

				boolean numericMode = engine.isNumericMode();
				IExpr temp;
				try {
					int astSize = ast.size();
					for (int i = 2; i < astSize; i++) {
						temp = engine.evalLoop(ast.get(i));
						if (temp.isPresent()) {
							if (evaledAST.isPresent()) {
								evaledAST.set(i, temp);
							} else {
								evaledAST = ast.copy();
								evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
								evaledAST.set(i, temp);
							}
						}
					}
				} finally {
					engine.setNumericMode(numericMode);
				}
				if (evaledAST.isPresent()) {
					return getPart((IAST) arg1, evaledAST, 2, engine);
				}
				return getPart((IAST) arg1, ast, 2, engine);
			}
		}
		return F.NIL;
	}

	private IExpr getPart(final IAST arg1, final IAST ast, int pos, EvalEngine engine) {
		final IExpr arg2 = ast.get(pos);
		int p1 = pos + 1;
		if (arg2.isSignedNumber()) {
			final int indx = Validate.checkIntType(ast, pos, Integer.MIN_VALUE);
			IExpr ires = null;
			ires = getIndex(arg1, indx);
			if (p1 < ast.size()) {
				if (ires.isAST()) {
					return getPart((IAST) ires, ast, p1, engine);
				} else {
					throw new WrongArgumentType(ast, arg1, pos,
							"Wrong argument for Part[] function. Function or list expected.");
				}
			}
			return ires;
		} else if (arg2.isSpan()) {
			IAST span = (IAST) arg2;
			if (span.isAST2()) {
				final int indx1 = Validate.checkIntType(span, 1, Integer.MIN_VALUE);
				final int indx2;
				if (span.arg2().equals(F.All)) {
					indx2 = arg1.size() - 1;
				} else {
					indx2 = Validate.checkIntType(span, 2, Integer.MIN_VALUE);
				}

				IAST result = arg1.copyHead();
				int last = indx2;
				if (indx2 < 0) {
					last = arg1.size() + indx2;
				}
				for (int i = indx1; i <= last; i++) {
					result.add(arg1.get(i));
				}
				return result;
			}
		} else if (arg2.isList()) {
			IExpr temp = null;
			final IAST lst = (IAST) arg2;
			final IAST result = F.ListC(lst.size());

			for (int i = 1; i < lst.size(); i++) {
				final IExpr expr = lst.get(i);
				if (expr.isInteger()) {
					IExpr ires = null;

					final int indx = Validate.checkIntType(lst, i, Integer.MIN_VALUE);
					ires = getIndex(arg1, indx);
					if (ires == null) {
						return F.NIL;
					}
					if (p1 < ast.size()) {
						if (ires.isAST()) {
							temp = getPart((IAST) ires, ast, p1, engine);
							result.add(temp);
						} else {
							throw new WrongArgumentType(ast, arg1, pos,
									"Wrong argument for Part[] function. Function or list expected.");
						}
					} else {
						result.add(ires);
					}
				}
			}
			return result;
		}
		engine.printMessage("Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
		return F.NIL;
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
			throw new WrappedException(new IndexOutOfBoundsException(
					"Part[] index " + position + " of " + ast.toString() + " is out of bounds."));
		}
		return ast.get(position);
	}
}
