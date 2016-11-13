package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

public class Nearest extends AbstractFunctionEvaluator {

	public Nearest() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 4);

		if (ast.arg1().isAST()) {
			if (ast.size() == 3 && ast.arg2().isNumber()) {
				IAST listArg1 = (IAST) ast.arg1();
				if (listArg1.size() > 1) {
					INumber arg2 = (INumber) ast.arg2();
					// Norm() is the default distance function for numeric data
					IExpr distanceFunction = F.Function(F.Norm(F.Subtract(F.Slot1, F.Slot2)));
					return numericalNearest(listArg1, arg2, distanceFunction, engine);
				}
			}
		}

		return F.NIL;
	}

	/**
	 * Gives the list of elements from <code>inputList</code> to which x is
	 * nearest.
	 * 
	 * @param inputList
	 * @param x
	 * @param engine
	 * @return the list of elements from <code>inputList</code> to which x is
	 *         nearest
	 */
	private static IAST numericalNearest(IAST inputList, INumber x, IExpr distanceFunction, EvalEngine engine) {
		try {
			IAST nearest = null;
			IExpr distance = F.NIL;
			IAST temp;
			for (int i = 1; i < inputList.size(); i++) {
				temp = F.ast(distanceFunction);
				temp.append(x);
				temp.append(inputList.get(i));
				if (nearest == null) {
					nearest = F.List();
					nearest.append(inputList.get(i));
					distance = temp;
				} else {
					IExpr comparisonResult = engine.evaluate(F.Greater(distance, temp));
					if (comparisonResult.isTrue()) {
						nearest = F.List();
						nearest.append(inputList.get(i));
						distance = temp;
					} else if (comparisonResult.isFalse()) {
						if (engine.evalTrue(F.Equal(distance, temp))) {
							nearest.append(inputList.get(i));
						}
						continue;
					} else {
						// undefined
						return F.NIL;
					}
				}
			}
			return nearest;
		} catch (ClassCastException cce) {
		} catch (RuntimeException rex) {
		}
		return F.NIL;
	}
}
