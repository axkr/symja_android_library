package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 */
public class AbstractNonOrderlessArgMultiple extends AbstractArg2 {

	public IExpr evaluateArg0(final IAST ast, EvalEngine engine) {
		return F.NIL;
	}

	public IExpr evaluateArg1(final IAST ast, EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size == 3) {
			return binaryOperator(ast.arg1(), ast.arg2());
		} else if (size == 1) {
			return evaluateArg1(ast, engine);
		} else if (size == 0) {
			return evaluateArg0(ast, engine);
		}

		if (size > 3) {
			final ISymbol sym = ast.topHead();
			final IASTAppendable result = F.ast(sym);
			IExpr tres;
			IExpr temp = ast.arg1();
			boolean evaled = false;
			int i = 2;

			while (i < size) {
				tres = binaryOperator(temp, ast.get(i));
				if (!tres.isPresent()) {
					result.append(temp);
					if (i == size - 1) {
						result.append(ast.get(i));
					} else {
						temp = ast.get(i);
					}
					i++;
				} else {
					evaled = true;
					temp = tres;
					if (i == (size - 1)) {
						result.append(temp);
					}
					i++;
				}
			}

			if (evaled) {
				if ((result.isAST1()) && ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)) {
					return result.arg1();
				}
				return result;
			}
		}

		return F.NIL;
	}

}