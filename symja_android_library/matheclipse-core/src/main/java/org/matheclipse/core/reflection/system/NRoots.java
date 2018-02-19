package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.duy.lambda.IntFunction;

import static org.matheclipse.core.expression.F.evalExpandAll;

/**
 * <pre>
 * NRoots(poly)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the numerical roots of polynomial <code>poly</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; NRoots(x^3-4*x^2+x+6)
 * {2.9999999999999996,-1.0000000000000002,1.9999999999999998}
 * </pre>
 * 
 * <h3>Related terms</h3>
 * <p>
 * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>, <a href="GroebnerBasis.md">GroebnerBasis</a>,
 * <a href="FindRoot.md">FindRoot</a>, <a href="Solve.md">Solve</a>
 * </p>
 */
public class NRoots extends AbstractFunctionEvaluator {

	public NRoots() {
	}

	/**
	 * Determine the numerical roots of a univariate polynomial
	 * 
	 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
	 * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
	 * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
	 * 
	 * @see Roots
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IAST variables;
		if (ast.size() == 2) {
			VariablesSet eVar = new VariablesSet(ast.arg1());
			if (!eVar.isSize(1)) {
				// factor only possible for univariate polynomials
				engine.printMessage("NRoots: factorization only possible for univariate polynomials");
				return F.NIL;
			}
			variables = eVar.getVarList();
		} else {
			if (ast.arg2().isList()) {
				variables = (IAST) ast.arg2();
			} else {
				variables = F.List(ast.arg2());
			}
		}
		IExpr temp = roots(ast.arg1(), variables, engine);
		if (!temp.isList()) {
			return F.NIL;
		}
		IAST list = (IAST) temp;
		int size = list.size();
		IASTAppendable result = F.ListAlloc(size);
		return result.appendArgs(size, new IntFunction<IExpr>() {
            @Override
            public IExpr apply(int i) {
                return engine.evalN(list.get(i));
            }
        });
		// for (int i = 1; i < size; i++) {
		// result.append(engine.evalN(list.get(i)));
		// }
		// return result;
	}

	public static IAST roots(final IExpr arg1, IAST variables, EvalEngine engine) {
		if (variables.size() != 2) {
			// factor only possible for univariate polynomials
			engine.printMessage("NRoots: factorization only possible for univariate polynomials");
			return F.NIL;
		}
		IExpr expr = evalExpandAll(arg1, engine);

		ISymbol sym = (ISymbol) variables.arg1();
		double[] coefficients = Expr2Object.toPolynomial(expr, sym);

		if (coefficients != null) {
			LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
			Complex[] roots = solver.solveAllComplex(coefficients, 0);
			return Object2Expr.convertComplex(true, roots);
		}
		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Algebra.together((IAST) expr, engine);

			// split expr into numerator and denominator
			denom = engine.evaluate(F.Denominator(expr));
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = engine.evaluate(F.Numerator(expr));
			}
		}
		return rootsOfVariable(expr, denom);
	}

	/**
	 * 
	 * @param coefficients
	 * @return <code>F.NIL</code> if the result couldn't be evaluated
	 */
	public static IAST rootsUp2Degree3(double[] coefficients) {
		if (coefficients.length == 0) {
			return F.NIL;
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
		IAST result = F.NIL;
		if (coefficients.length == 4) {
			result = cubic(coefficients[3], coefficients[2], coefficients[1], coefficients[0]);
		}
		return result;
	}

	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denom) {

		IASTAppendable resultList = RootIntervals.croots(expr, true);
		if (resultList.isPresent()) {
			// IAST result = F.List();
			// if (resultList.size() > 0) {
			// result.appendArgs(resultList);
			// }
			// return result;
			return resultList;
		}
		return F.NIL;
	}

	private static IAST quadratic(double a, double b, double c) {
		IASTAppendable result = F.ListAlloc(2);
		double discriminant = (b * b - (4 * a * c));
		if (F.isZero(discriminant)) {
			double bothEqual = ((-b / (2.0 * a)));
			result.append(F.num(bothEqual));
			result.append(F.num(bothEqual));
		} else if (discriminant < 0.0) {
			// two complex roots
			double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
			double realPart = (-b / (2.0 * a));
			result.append(F.complex(realPart, imaginaryPart));
			result.append(F.complex(realPart, -imaginaryPart));
		} else {
			// two real roots
			double real1 = ((-b + Math.sqrt(discriminant)) / (2.0 * a));
			double real2 = ((-b - Math.sqrt(discriminant)) / (2.0 * a));
			result.append(F.num(real1));
			result.append(F.num(real2));
		}
		return result;
	}

	/**
	 * See <a href= "http://stackoverflow.com/questions/13328676/c-solving-cubic-equations" > http
	 * ://stackoverflow.com/questions/13328676/c-solving-cubic-equations</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	private static IAST cubic(double a, double b, double c, double d) {
		if (F.isZero(a)) {
			return F.NIL;
		}
		if (F.isZero(d)) {
			return F.NIL;
		}
		IASTAppendable result = F.ListAlloc(3);
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
			result.append(F.num(-term1 + s + t));
			term1 += (s + t) / 2.0;
			double realPart = -term1;
			term1 = Math.sqrt(3.0) * (-t + s) / 2;
			result.append(F.complex(realPart, term1));
			result.append(F.complex(realPart, -term1));
			return result;
		}

		// The remaining options are all real
		double r13;
		if (F.isZero(discriminant)) {
			// All roots real, at least two are equal.
			r13 = ((r < 0) ? -Math.pow(-r, (1.0 / 3.0)) : Math.pow(r, (1.0 / 3.0)));
			result.append(F.num(-term1 + 2.0 * r13));
			result.append(F.num(-(r13 + term1)));
			result.append(F.num(-(r13 + term1)));
			return result;
		}

		// Only option left is that all roots are real and unequal (to get here,
		// q < 0)
		q = -q;
		double dum1 = q * q * q;
		dum1 = Math.acos(r / Math.sqrt(dum1));
		r13 = 2.0 * Math.sqrt(q);
		result.append(F.num(-term1 + r13 * Math.cos(dum1 / 3.0)));
		result.append(F.num(-term1 + r13 * Math.cos((dum1 + 2.0 * Math.PI) / 3.0)));
		result.append(F.num(-term1 + r13 * Math.cos((dum1 + 4.0 * Math.PI) / 3.0)));
		return result;
	}
}