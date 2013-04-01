package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Base class for functions with 1 argument (i.e. Sin, Cos...) with Attributes
 * <i>Listable</i> and <i>NumericFunction</i>
 * 
 */
public abstract class AbstractTrigArg1 extends AbstractFunctionEvaluator {

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return evaluateArg1(ast.get(1));
	}

	public IExpr evaluateArg1(final IExpr arg1) {
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		Validate.checkSize(functionList, 2);
		if (functionList.get(1) instanceof Num) {
			return numericEvalD1((Num) functionList.get(1));
		}
		if (functionList.get(1) instanceof ComplexNum) {
			return numericEvalDC1((ComplexNum) functionList.get(1));
		}
		return numericEvalArg1(functionList.get(1));
	}

	/**
	 * Evaluate this function for one double argument
	 * 
	 * @param arg1
	 *          a double number
	 * 
	 * @return
	 */
	public IExpr numericEvalD1(final Num arg1) {
		return null;
	}

	/**
	 * Evaluate this function for one double complex argument
	 * 
	 * @param arg1
	 *          a double complex number
	 * 
	 * @return
	 */
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return null;
	}

	public IExpr numericEvalArg1(final IExpr arg1) {
		return null;
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @return <code>true</code> if the first argument is canonical negative
	 */
	public static boolean isNegativeExpression(final IExpr expr) {
		if (expr.isNumber()) {
			if (((INumber) expr).complexSign() < 0) {
				return true;
			}
		} else if (expr.isTimes()) {
			IAST times = (IAST) expr;
			if (times.get(1).isNumber()) {
				if (((INumber) times.get(1)).complexSign() < 0) {
					return true;
				}
			}
		} else if (expr.isPlus()) {
			IAST plus = (IAST) expr;
			if (plus.get(1).isNumber()) {
				if (((INumber) plus.get(1)).complexSign() < 0) {
					return true;
				}
			}
		}

		return false;
	}
}
