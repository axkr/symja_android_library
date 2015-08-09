package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.evalExpandAll;

import org.apache.commons.math4.analysis.solvers.LaguerreSolver;
import org.apache.commons.math4.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Determine the numerical roots of a univariate polynomial
 * 
 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>, <a
 * href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and <a
 * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
 * 
 * @see Roots
 */
public class NRoots extends AbstractFunctionEvaluator {

	public NRoots() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			return null;
		}
		IExpr temp = roots(ast);
		if (temp == null || !temp.isList()) {
			return null;
		}
		IAST list = (IAST) temp;
		IAST result = F.List();
		for (int i = 1; i < list.size(); i++) {
			result.add(F.evaln(list.get(i)));
		}
		return result;
	}

	protected static IAST roots(final IAST ast) {
		VariablesSet eVar = new VariablesSet(ast.arg1());
		if (!eVar.isSize(1)) {
			// factor only possible for univariate polynomials
			return null;
		}
		IExpr expr = evalExpandAll(ast.arg1());
		IAST variables = eVar.getVarList();
		ISymbol sym = (ISymbol) variables.arg1();
		double[] coefficients = Expr2Object.toPolynomial(expr, sym);

		if (coefficients != null) {
			// IAST result = rootsUp2Degree3(coefficients);
			// if (result != null) {
			// return result;
			// }
			LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
			// System.out.println(expr);
			Complex[] roots = solver.solveAllComplex(coefficients, 0);
			return Object2Expr.convertComplex(roots);
		}
		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Together.together((IAST) expr);

			// split expr into numerator and denominator
			denom = F.eval(F.Denominator(expr));
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = F.eval(F.Numerator(expr));
			}
		}
		return rootsOfVariable(expr, denom);
	}

	/**
	 * 
	 * @param coefficients
	 * @return <code>null</code> if the result couldn't be evaluated
	 */
	public static IAST rootsUp2Degree3(double[] coefficients) {
		IAST result = null;
		if (coefficients.length == 0) {
			return null;
		}
		if (coefficients.length == 1) {
			return quadratic(0.0, 0.0, coefficients[0]);
		}
		if (coefficients.length == 2) {
			return quadratic(0.0, coefficients[1], coefficients[0]);
		}
		if (coefficients.length == 3) {
			return quadratic(coefficients[2], coefficients[1], coefficients[0]);
		}
		if (coefficients.length == 4) {
			result = cubic(coefficients[3], coefficients[2], coefficients[1], coefficients[0]);
		}
		return result;
	}

	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denom) {
		IAST result = List();
		IAST resultList = RootIntervals.croots(expr, true);
		if (resultList != null) {
			if (resultList.size() > 0) {
				result.addAll(resultList);
			}
			return result;
		}
		return null;
	}

	private static IAST quadratic(double a, double b, double c) {
		IAST result = F.List();
		double discriminant = (b * b - (4 * a * c));
		if (F.isZero(discriminant)) {
			double bothEqual = ((-b / (2.0 * a)));
			result.add(F.num(bothEqual));
			result.add(F.num(bothEqual));
		} else if (discriminant < 0.0) {
			// two complex roots
			double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
			double realPart = (-b / (2.0 * a));
			result.add(F.complex(realPart, imaginaryPart));
			result.add(F.complex(realPart, -imaginaryPart));
		} else {
			// two real roots
			double real1 = ((-b + Math.sqrt(discriminant)) / (2.0 * a));
			double real2 = ((-b - Math.sqrt(discriminant)) / (2.0 * a));
			result.add(F.num(real1));
			result.add(F.num(real2));
		}
		return result;
	}

	/**
	 * See <a href= "http://stackoverflow.com/questions/13328676/c-solving-cubic-equations" >http
	 * ://stackoverflow.com/questions/13328676/c-solving-cubic-equations</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	private static IAST cubic(double a, double b, double c, double d) {
		if (F.isZero(a)) {
			return null;
		}
		if (F.isZero(d)) {
			return null;
		}
		IAST result = F.List();
		b /= a;
		c /= a;
		d /= a;

		double q = (3.0 * c - (b * b)) / 9.0;
		double r = -(27.0 * d) + b * (9.0 * c - 2.0 * (b * b));
		r /= 54.0;
		double discriminant = q * q * q + r * r;

		double term1 = (b / 3.0);
		if (discriminant > 0) {
			// one root real, two are complex
			double s = r + Math.sqrt(discriminant);
			s = ((s < 0) ? -Math.pow(-s, (1.0 / 3.0)) : Math.pow(s, (1.0 / 3.0)));
			double t = r - Math.sqrt(discriminant);
			t = ((t < 0) ? -Math.pow(-t, (1.0 / 3.0)) : Math.pow(t, (1.0 / 3.0)));
			result.add(F.num(-term1 + s + t));
			term1 += (s + t) / 2.0;
			double realPart = -term1;
			term1 = Math.sqrt(3.0) * (-t + s) / 2;
			result.add(F.complex(realPart, term1));
			result.add(F.complex(realPart, -term1));
			return result;
		}

		// The remaining options are all real
		double r13;
		if (F.isZero(discriminant)) {
			// All roots real, at least two are equal.
			r13 = ((r < 0) ? -Math.pow(-r, (1.0 / 3.0)) : Math.pow(r, (1.0 / 3.0)));
			result.add(F.num(-term1 + 2.0 * r13));
			result.add(F.num(-(r13 + term1)));
			result.add(F.num(-(r13 + term1)));
			return result;
		}

		// Only option left is that all roots are real and unequal (to get here,
		// q < 0)
		q = -q;
		double dum1 = q * q * q;
		dum1 = Math.acos(r / Math.sqrt(dum1));
		r13 = 2.0 * Math.sqrt(q);
		result.add(F.num(-term1 + r13 * Math.cos(dum1 / 3.0)));
		result.add(F.num(-term1 + r13 * Math.cos((dum1 + 2.0 * Math.PI) / 3.0)));
		result.add(F.num(-term1 + r13 * Math.cos((dum1 + 4.0 * Math.PI) / 3.0)));
		return result;
	}
}