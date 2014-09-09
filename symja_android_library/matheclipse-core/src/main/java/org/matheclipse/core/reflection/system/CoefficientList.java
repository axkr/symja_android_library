package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.Polynomial;

/**
 * 
 */
public class CoefficientList extends AbstractFunctionEvaluator {

	public CoefficientList() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IExpr expr = F.evalExpandAll(ast.arg1());
		ISymbol arg2 = Validate.checkSymbolType(ast, 2);
		// try {
		Polynomial poly = new Polynomial(expr, (ISymbol) arg2);
		if (poly.isPolynomial()) {
			if (poly.isZero()) {
				return F.List();
			}
			return poly.coefficientList();
		}
		throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
	}

	/**
	 * Get the coefficient list of a univariate polynomial
	 * 
	 * @param polynomial
	 * @param variable
	 * @return
	 */
	public static double[] coefficientList(IExpr polynomial, final ISymbol variable) throws JASConversionException {
		Polynomial poly = new Polynomial(polynomial, (ISymbol) variable);
		if (!poly.isPolynomial()) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}

		IAST list = poly.coefficientList();
		int degree = list.size() - 2;
		double[] result = new double[degree + 1];
		for (int i = 1; i < list.size(); i++) {
			IExpr temp = list.get(i);
			if (temp.isSignedNumber()) {
				result[i - 1] = ((ISignedNumber) temp).doubleValue();
				continue;
			}
			if (temp.isNumericFunction()) {
				temp = F.eval(temp);
				if (temp.isSignedNumber()) {
					result[i - 1] = ((ISignedNumber) temp).doubleValue();
					continue;
				}
			}
			return null;
		}
		return result;
	}

	public static long univariateCoefficientList(IExpr polynomial, final ISymbol variable, List<IExpr> resultList)
			throws JASConversionException {
		Polynomial poly = new Polynomial(polynomial, (ISymbol) variable);
		if (!poly.isPolynomial()) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
		IAST list = poly.coefficientList();
		int degree = list.size() - 2;
		if (degree >= Short.MAX_VALUE) {
			return degree;
		}
		for (int i = 0; i <= degree; i++) {
			IExpr temp = list.get(i + 1);
			resultList.add(temp);
		}
		return degree;
	}

	/**
	 * 
	 * @param polynomial
	 * @param variable
	 * @param resultList
	 *            the coefficient list of the given univariate polynomial in increasing order
	 * @param resultListDiff
	 *            the coefficient list of the derivative of the given univariate polynomial
	 * @return the degree of the univariate polynomial; if <code>degree >= Short.MAX_VALUE</code>, the result list will be empty.
	 */
	public static long univariateCoefficientList(IExpr polynomial, ISymbol variable, List<IExpr> resultList,
			List<IExpr> resultListDiff) throws JASConversionException {
		Polynomial poly = new Polynomial(polynomial, (ISymbol) variable);
		if (!poly.isPolynomial()) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
		IAST polyExpr = poly.coefficientList();

		int degree = polyExpr.size() - 2;
		if (degree >= Short.MAX_VALUE) {
			return degree;
		}
		for (int i = 0; i <= degree; i++) {
			IExpr temp = polyExpr.get(i + 1);
			resultList.add(temp);
		}
		IAST polyDiff = poly.derivative().coefficientList();
		int degreeDiff = polyDiff.size() - 2;
		for (int i = 0; i <= degreeDiff; i++) {
			IExpr temp = polyDiff.get(i + 1);
			resultListDiff.add(temp);
		}
		return degree;
	}
}