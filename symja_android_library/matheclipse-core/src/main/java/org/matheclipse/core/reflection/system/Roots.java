package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.evalExpandAll;

import java.util.List;
import java.util.function.IntFunction;

import javax.annotation.Nonnull;

import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.QuarticSolver;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;

/**
 * <pre>
 * Roots(polynomial - equation, var)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * determine the roots of a univariate polynomial equation with respect to the variable <code>var</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Roots(3*x^3-5*x^2+5*x-2==0,x)
 * x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)
 * </pre>
 */
public class Roots extends AbstractFunctionEvaluator {

	public Roots() {
	}

	/**
	 * Determine the roots of a univariate polynomial
	 * 
	 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
	 * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
	 * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isEqual()) {
			IAST equalAST = (IAST) arg1;
			if (equalAST.arg2().isZero()) {
				arg1 = equalAST.arg1();
			} else {
				arg1 = engine.evaluate(F.Subtract(equalAST.arg1(), equalAST.arg2()));
			}
		} else {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal() expression expected!");
		}
		VariablesSet eVar = null;
		if (ast.arg2().isList()) {
			eVar = new VariablesSet(ast.arg2());
		} else {
			eVar = new VariablesSet();
			eVar.add(ast.arg2());
		}
		if (!eVar.isSize(1)) {
			// factorization only possible for univariate polynomials
			throw new WrongArgumentType(ast, ast.arg2(), 2, "Only one variable expected");
		}
		IAST variables = eVar.getVarList();
		IExpr variable = variables.arg1();
		IAST list = roots(arg1, false, variables, engine);
		if (list.isPresent()) {
			IASTAppendable or = F.Or();
			for (int i = 1; i < list.size(); i++) {
				or.append(F.Equal(variable, list.get(i)));
			}
			return or;
		}
		return F.NIL;
	}

	protected static IAST roots(final IExpr arg1, boolean numericSolutions, IAST variables, EvalEngine engine) {

		IExpr expr = evalExpandAll(arg1, engine);

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
		return rootsOfVariable(expr, denom, variables, numericSolutions, engine);
	}

	/**
	 * <p>
	 * Given a set of polynomial coefficients, compute the roots of the polynomial. Depending on the polynomial being
	 * considered the roots may contain complex number. When complex numbers are present they will come in pairs of
	 * complex conjugates.
	 * </p>
	 * 
	 * @param coefficients
	 *            coefficients of the polynomial.
	 * @return the roots of the polynomial
	 */
	@Nonnull
	public static IAST findRoots(double... coefficients) {
		int N = coefficients.length - 1;

		// Construct the companion matrix
		RealMatrix c = new Array2DRowRealMatrix(N, N);

		double a = coefficients[N];
		for (int i = 0; i < N; i++) {
			c.setEntry(i, N - 1, -coefficients[i] / a);
		}
		for (int i = 1; i < N; i++) {
			c.setEntry(i, i - 1, 1);
		}

		try {

			EigenDecomposition ed = new EigenDecomposition(c);

			double[] realValues = ed.getRealEigenvalues();
			double[] imagValues = ed.getImagEigenvalues();

			IASTAppendable roots = F.ListAlloc(N);
			return roots.appendArgs(0, N,
                    new IntFunction<IExpr>() {
                        @Override
                        public IExpr apply(int i) {
                            return F.chopExpr(F.complexNum(realValues[i], imagValues[i]), Config.DEFAULT_ROOTS_CHOP_DELTA);
                        }
                    });
			// for (int i = 0; i < N; i++) {
			// roots.append(F.chopExpr(F.complexNum(realValues[i], imagValues[i]), Config.DEFAULT_ROOTS_CHOP_DELTA));
			// }
			// return roots;
		} catch (Exception ime) {
			throw new WrappedException(ime);
		}

	}

	/**
	 * 
	 * @param expr
	 * @param denominator
	 * @param variables
	 * @param numericSolutions
	 * @param engine
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denominator, final IAST variables,
			boolean numericSolutions, EvalEngine engine) {
		IASTAppendable result = F.NIL;
		// ASTRange r = new ASTRange(variables, 1);
		// List<IExpr> varList = r;
		List<IExpr> varList = variables.copyTo();
		try {
			IExpr temp;
			IAST list = rootsOfQuadraticExprPolynomial(expr, variables);
			if (list.isPresent()) {
				return list;
			}
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numericSolutions);
			// if (polyRat.degree(0) <= 2) {
			result = rootsOfExprPolynomial(expr, variables, false);
			if (result.isPresent()) {
				return result;
			}
			// }
			result = F.ListAlloc(8);
			IAST factorRational = Algebra.factorRational(polyRat, jas, varList, F.List);
			for (int i = 1; i < factorRational.size(); i++) {
				temp = F.evalExpand(factorRational.get(i));
				IAST quarticResultList = QuarticSolver.solve(temp, variables.arg1());
				if (quarticResultList.isPresent()) {
					for (int j = 1; j < quarticResultList.size(); j++) {
						if (numericSolutions) {
							result.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
									Config.DEFAULT_ROOTS_CHOP_DELTA));
						} else {
							result.append(quarticResultList.get(j));
						}
					}
				} else {
					polyRat = jas.expr2JAS(temp, numericSolutions);
					IAST factorComplex = Algebra.factorComplex(polyRat, jas, varList, F.List, true);
					for (int k = 1; k < factorComplex.size(); k++) {
						temp = F.evalExpand(factorComplex.get(k));
						quarticResultList = QuarticSolver.solve(temp, variables.arg1());
						if (quarticResultList.isPresent()) {
							for (int j = 1; j < quarticResultList.size(); j++) {
								if (numericSolutions) {
									result.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
											Config.DEFAULT_ROOTS_CHOP_DELTA));
								} else {
									result.append(quarticResultList.get(j));
								}
							}
						} else {
							double[] coefficients = CoefficientList.coefficientList(temp, (ISymbol) variables.arg1());
							if (coefficients == null) {
								return F.NIL;
							}
							IAST resultList = findRoots(coefficients);
							// IAST resultList = RootIntervals.croots(temp,
							// true);
							if (resultList.size() > 0) {
								result.appendArgs(resultList);
							}
						}
					}
				}
			}
			result = QuarticSolver.createSet(result);
			return result;
		} catch (JASConversionException e) {
			result = rootsOfExprPolynomial(expr, variables, true);
		}
		if (result.isPresent()) {
			if (!denominator.isNumber()) {
				// eliminate roots from the result list, which occur in the
				// denominator
				int i = 1;
				while (i < result.size()) {
					IExpr temp = denominator.replaceAll(F.Rule(variables.arg1(), result.get(i)));
					if (temp.isPresent() && engine.evaluate(temp).isZero()) {
						result.remove(i);
						continue;
					}
					i++;
				}
			}
			return result;
		}
		return F.NIL;
	}

	private static IASTAppendable rootsOfExprPolynomial(final IExpr expr, IAST varList, boolean rootsOfQuartic) {
		IASTAppendable result = F.NIL;
		try {
			// try to generate a common expression polynomial
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
			ExprPolynomial ePoly = ring.create(expr, false, false);
			ePoly = ePoly.multiplyByMinimumNegativeExponents();
			if (ePoly.degree(0) >= Integer.MAX_VALUE) {
				return F.NIL;
			}
			if (ePoly.degree(0) >= 3) {
				result = unitPolynomial((int) ePoly.degree(0), ePoly);
				if (result.isPresent()) {
					result = QuarticSolver.createSet(result);
					return result;
				}
			}
			if (!rootsOfQuartic && ePoly.degree(0) > 2) {
				return F.NIL;
			}
			result = rootsOfQuarticPolynomial(ePoly);
			if (result.isPresent()) {
				if (expr.isNumericMode()) {
					for (int i = 1; i < result.size(); i++) {
						result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
					}
				}
				return result;
			}
		} catch (JASConversionException e2) {
			if (Config.SHOW_STACKTRACE) {
				e2.printStackTrace();
			}
		}
		return F.NIL;
	}

	/**
	 * Solve a polynomial with degree &lt;= 2.
	 * 
	 * @param expr
	 * @param varList
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IAST rootsOfQuadraticExprPolynomial(final IExpr expr, IAST varList) {
		IASTAppendable result = F.NIL;
		try {
			// try to generate a common expression polynomial
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
			ExprPolynomial ePoly = ring.create(expr, false, false);
			ePoly = ePoly.multiplyByMinimumNegativeExponents();
			result = rootsOfQuadraticPolynomial(ePoly);
			if (result.isPresent() && expr.isNumericMode()) {
				for (int i = 1; i < result.size(); i++) {
					result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
				}
			}
		} catch (JASConversionException e2) {
			if (Config.SHOW_STACKTRACE) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Solve a polynomial with degree &lt;= 4.
	 * 
	 * @param polynomial
	 *            the polynomial
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IASTAppendable rootsOfQuarticPolynomial(ExprPolynomial polynomial) {
		long varDegree = polynomial.degree(0);

		if (polynomial.isConstant()) {
			return F.ListAlloc(0);
		}

		IExpr a;
		IExpr b;
		IExpr c;
		IExpr d;
		IExpr e;
		if (varDegree <= 4) {
			// solve quartic equation:
			a = C0;
			b = C0;
			c = C0;
			d = C0;
			e = C0;
			for (ExprMonomial monomial : polynomial) {
				IExpr coeff = monomial.coefficient();
				long lExp = monomial.exponent().getVal(0);
				if (lExp == 4) {
					a = coeff;
				} else if (lExp == 3) {
					b = coeff;
				} else if (lExp == 2) {
					c = coeff;
				} else if (lExp == 1) {
					d = coeff;
				} else if (lExp == 0) {
					e = coeff;
				} else {
					return F.NIL;
				}
			}
			IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
			if (result.isPresent()) {
				return QuarticSolver.createSet(result);
			}
		}

		return F.NIL;
	}

	/**
	 * Solve polynomials of the form <code>a * x^varDegree + b == 0</code>
	 * 
	 * @param varDegree
	 * @param polynomial
	 * @return
	 */
	private static IASTAppendable unitPolynomial(int varDegree, ExprPolynomial polynomial) {
		IExpr a;
		IExpr b;
		a = C0;
		b = C0;
		for (ExprMonomial monomial : polynomial) {
			IExpr coeff = monomial.coefficient();
			long lExp = monomial.exponent().getVal(0);
			if (lExp == varDegree) {
				a = coeff;
			} else if (lExp == 0) {
				b = coeff;
			} else {
				return F.NIL;
			}
		}

		// a * x^varDegree + b
		if (!a.isOne()) {
			a = F.Power(a, F.fraction(-1, varDegree));
		}
		if (!b.isOne()) {
			b = F.Power(b, F.fraction(1, varDegree));
		}
		if ((varDegree & 0x0001) == 0x0001) {
			// odd
			IASTAppendable result = F.ListAlloc(varDegree);
			for (int i = 1; i <= varDegree; i++) {
				result.append(F.Times(F.Power(F.CN1, i - 1), F.Power(F.CN1, F.fraction(i, varDegree)), b, a));
			}
			return result;
		} else {
			// even
			IASTAppendable result = F.ListAlloc(varDegree);
			long size = varDegree / 2;
			int k = 1;
			for (int i = 1; i <= size; i++) {
				result.append(F.Times(F.CN1, F.Power(F.CN1, F.fraction(k, varDegree)), b, a));
				result.append(F.Times(F.Power(F.CN1, F.fraction(k, varDegree)), b, a));
				k += 2;
			}
			return result;
		}

	}

	/**
	 * Solve a polynomial with degree &lt;= 2.
	 * 
	 * @param polynomial
	 *            the polynomial
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial) {
		long varDegree = polynomial.degree(0);

		if (polynomial.isConstant()) {
			return F.ListAlloc(1);
		}
		IExpr a;
		IExpr b;
		IExpr c;
		IExpr d;
		IExpr e;
		if (varDegree <= 2) {
			IEvalStepListener listener = EvalEngine.get().getStepListener();
			if (listener != null) {
				IASTAppendable temp = listener.rootsOfQuadraticPolynomial(polynomial);
				if (temp.isPresent()) {
					return temp;
				}
			}
			// solve quadratic equation:
			a = C0;
			b = C0;
			c = C0;
			d = C0;
			e = C0;
			for (ExprMonomial monomial : polynomial) {
				IExpr coeff = monomial.coefficient();
				long lExp = monomial.exponent().getVal(0);
				if (lExp == 4) {
					a = coeff;
				} else if (lExp == 3) {
					b = coeff;
				} else if (lExp == 2) {
					c = coeff;
				} else if (lExp == 1) {
					d = coeff;
				} else if (lExp == 0) {
					e = coeff;
				} else {
					throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
				}
			}
			IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
			if (result.isPresent()) {
				result = QuarticSolver.createSet(result);
				return result;
			}

		}

		return F.NIL;
	}
}