package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;

/**
 * 
 */
public class CoefficientList extends AbstractFunctionEvaluator {

	public CoefficientList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		IExpr expr = F.evalExpandAll(ast.arg1(), engine);
		ISymbol arg2 = Validate.checkSymbolType(ast, 2);
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(arg2));
			ExprPolynomial poly = ring.create(expr);
			if (poly.isZero()) {
				return F.List();
			}
			return poly.coefficientList();
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
		}
	}

	/**
	 * Get the coefficient list of a univariate polynomial.
	 * 
	 * @param polynomial
	 * @param variable
	 * @return <code>null</code> if the list couldn't be evaluated.
	 */
	public static double[] coefficientList(IExpr polynomial, final ISymbol variable) throws JASConversionException {
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
			ExprPolynomial poly = ring.create(polynomial);
			// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
			// if (!poly.isPolynomial()) {
			// throw new WrongArgumentType(polynomial, "Polynomial expected!");
			// }

			IAST list = poly.coefficientList();
			int degree = list.size() - 2;
			double[] result = new double[degree + 1];
			for (int i = 1; i < list.size(); i++) {
				ISignedNumber temp = list.get(i).evalSignedNumber();
				if (temp != null) {
					result[i - 1] = temp.doubleValue();
				} else {
					return null;
				}
			}
			return result;
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
	}

	public static long univariateCoefficientList(IExpr polynomial, final ISymbol variable, List<IExpr> resultList)
			throws JASConversionException {
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
			ExprPolynomial poly = ring.create(polynomial);
			// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
			// if (!poly.isPolynomial()) {
			// throw new WrongArgumentType(polynomial, "Polynomial expected!");
			// }
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
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
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
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
			ExprPolynomial poly = ring.create(polynomial);
			// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
			// if (!poly.isPolynomial()) {
			// throw new WrongArgumentType(polynomial, "Polynomial expected!");
			// }
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
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
	}
}