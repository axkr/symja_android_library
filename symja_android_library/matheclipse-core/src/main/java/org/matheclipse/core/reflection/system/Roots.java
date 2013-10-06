package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.evalExpandAll;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.QuarticSolver;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;

/**
 * Determine the roots of a univariate polynomial
 * 
 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>, <a
 * href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and <a
 * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
 */
public class Roots extends AbstractFunctionEvaluator {

	public Roots() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			return null;
		}
		return roots(ast, false);
	}

	protected static IAST roots(final IAST ast, boolean numericSolutions) {
		ExprVariables eVar = new ExprVariables(ast.get(1));
		if (!eVar.isSize(1)) {
			// factor only possible for univariate polynomials
			return null;
		}
		IExpr expr = evalExpandAll(ast.get(1));
		IAST variables = eVar.getVarList();
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
		return rootsOfVariable(expr, denom, variables, numericSolutions);
	}

	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denom, final IAST variables, boolean numericSolutions) {

		if (numericSolutions) {
			IAST result = List();
			IAST resultList = RootIntervals.croots(expr, true);
			if (resultList == null) {
				return null;
			}
			if (resultList.size() > 0) {
				result.addAll(resultList);
			}
			return result;
		}

		IAST result = null;
		ASTRange r = new ASTRange(variables, 1);
		List<IExpr> varList = r.toList();
		try {
			result = F.List();
			IAST factors = Factor.factorComplex(expr, varList, F.List, true);
			for (int i = 1; i < factors.size(); i++) {
				IAST quarticResultList = QuarticSolver.solve(F.evalExpand(factors.get(i)), variables.get(1));
				if (quarticResultList != null) {
					for (int j = 1; j < quarticResultList.size(); j++) {
						result.add(quarticResultList.get(j));
					}
				}
			}
			result = QuarticSolver.createSet(result);
			return result;
		} catch (JASConversionException e) {
			try {
				// try to generate a common expression polynomial
				JASConvert<IExpr> eJas = new JASConvert<IExpr>(varList, new ExprRingFactory());
				GenPolynomial<IExpr> ePoly = eJas.expr2IExprJAS(expr);
				result = rootsOfPolynomial(ePoly);
			} catch (JASConversionException e2) {
				if (Config.SHOW_STACKTRACE) {
					e2.printStackTrace();
				}
			}
		}
		if (result != null) {
			if (!denom.isNumber()) {
				// eliminate roots from the result list, which occur in the denominator
				int i = 1;
				while (i < result.size()) {
					IExpr temp = denom.replaceAll(F.Rule(variables.get(1), result.get(i)));
					if (temp != null && F.eval(temp).isZero()) {
						result.remove(i);
						continue;
					}
					i++;
				}
			}
			return result;
		}
		return null;
	}

	private static IAST rootsOfPolynomial(GenPolynomial<IExpr> ePoly) {
		long varDegree = ePoly.degree(0);
		IAST result = List();
		if (ePoly.isConstant()) {
			return result;
		}
		IExpr a;
		IExpr b;
		IExpr c;
		IExpr d;
		IExpr e;
		if (varDegree <= 4) {
			// solve quartic equation: a*x^2 + b*x + c = 0
			a = C0;
			b = C0;
			c = C0;
			d = C0;
			e = C0;
			for (Monomial<IExpr> monomial : ePoly) {
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
			result = QuarticSolver.quarticSolve(a, b, c, d, e);
			if (result != null) {
				result = QuarticSolver.createSet(result);
				return result;
			}

		}

		return null;
	}

}