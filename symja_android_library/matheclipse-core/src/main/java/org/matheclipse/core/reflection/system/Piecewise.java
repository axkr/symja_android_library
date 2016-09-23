package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A piecewise-defined function (also called a piecewise function or a hybrid function) is a function which is defined by multiple
 * subfunctions, each subfunction applying to a certain interval of the main function's domain.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Piecewise">Wikipedia:Piecewise</a>
 * 
 */
public class Piecewise extends AbstractFunctionEvaluator {
	public Piecewise() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		int[] dim = ast.arg1().isMatrix();
		if (dim == null || dim[0] <= 0 || dim[1] != 2) {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "Matrix with row-dimension > 0 and column-dimension == 2 expected!");
		}
		IAST matrix = (IAST) ast.arg1();
		IExpr defaultValue = F.C0;
		if (ast.isAST2()) {
			defaultValue = ast.arg2();
		}
		IExpr cond;
		IAST row;
		IAST result = F.List();
		IAST pw = F.ast(F.Piecewise);
		pw.append(result);
		boolean evaluated = false;
		boolean noBoolean = false;
		for (int i = 1; i < matrix.size(); i++) {
			row = matrix.getAST(i);
			cond = row.arg2();
			if (cond.isTrue()) {
				if (!evaluated && i == matrix.size() - 1) {
					return F.NIL;
				}
				if (noBoolean) {
					result.append(F.List(row.arg1(), F.True));
					return pw;
				}
				return row.arg1();
			} else if (cond.isFalse()) {
				evaluated = true;
				continue;
			}
			cond = engine.evaluateNull(cond);
			if (!cond.isPresent()) {
				noBoolean = true;
				result.append(F.List(row.arg1(), row.arg2()));
				continue;
			} else if (cond.isTrue()) {
				evaluated = true;
				if (noBoolean) {
					result.append(F.List(row.arg1(), F.True));
					return pw;
				}
				return row.arg1();
			} else if (cond.isFalse()) {
				evaluated = true;
				continue;
			} else {
				result.append(F.List(row.arg1(), cond));
				noBoolean = true;
				continue;
			}
		}
		if (!noBoolean) {
			return defaultValue;
		} else {
			if (evaluated) {
				pw.append(defaultValue);
				return pw;
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol)  {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
