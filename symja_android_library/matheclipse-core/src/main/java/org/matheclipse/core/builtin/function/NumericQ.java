package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns <code>True</code>, if the given expression is a numeric function or
 * value.
 * 
 */
public class NumericQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {

	/**
	 * Constructor for the unary predicate
	 */
	// public final static NumericQ CONST = new NumericQ();

	public NumericQ() {
	}

	@Override
	public boolean test(IExpr arg) {
		return arg.isNumericFunction();
	}

	/**
	 * Returns <code>True</code> if the first argument is a numeric object;
	 * <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = engine.evaluate(ast.arg1());
		return F.bool(test(arg1));
	}

	@Override
	public void setUp(ISymbol newSymbol) {
	}

	/**
	 * Do a numerical evaluation of the given expression, if
	 * <code>arg1.isNumericFunction()</code> is <code>true</code>. Return a
	 * <code>ISignedNumber</code> if the evaluation result is a
	 * <code>ISignedNumber</code> and <code>null</code> in all other cases
	 * 
	 * @param arg1
	 *            an expression
	 * @return a signed number if possible, otherwise return <code>null</code>
	 * 
	 * @deprecated use {@link IExpr#evalSignedNumber()} instead
	 */
	@Deprecated
	public static ISignedNumber getSignedNumberNumericQ(IExpr arg1) {
		if (arg1.isSignedNumber()) {
			return (ISignedNumber) arg1;
		}
		if (arg1.isNumber()) {
			return null;
		}
		if (arg1.isNumericFunction()) {
			IExpr result = F.evaln(arg1);
			if (result.isSignedNumber()) {
				return (ISignedNumber) result;
			}
		}
		return null;
	}

	/**
	 * Do a numerical evaluation of the given expression, if
	 * <code>arg1.isNumericFunction()</code> is <code>true</code>. Return a
	 * <code>INumber</code> if the evaluation result is a <code>INumber</code>
	 * and <code>null</code> in all other cases
	 * 
	 * @param arg1
	 *            an expression
	 * @return a number if possible
	 * @deprecated use {@link IExpr#evalNumber()} instead
	 */
	@Deprecated
	public static INumber getNumberNumericQ(IExpr arg1) {
		if (arg1.isNumber()) {
			return (INumber) arg1;
		}
		if (arg1.isNumericFunction()) {
			IExpr result = F.evaln(arg1);
			if (result.isNumber()) {
				return (INumber) result;
			}
		}
		return null;
	}
}
