package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">
 * Wikipedia:Ordinary differential equation</a>
 * 
 */
public class DSolve extends AbstractFunctionEvaluator {
	public DSolve() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.DSOLVE) {
			return F.NIL;
		}
		Validate.checkSize(ast, 4);

		IAST listOfEquations = Validate.checkEquations(ast, 1);
		if (ast.arg2().isAST()) {
			IAST uFunction1Arg = (IAST) ast.arg2();
			IExpr xVar = ast.arg3();
			if (uFunction1Arg.size() == 2 && uFunction1Arg.arg1().equals(xVar)) {
				IAST listOfVariables = F.List(uFunction1Arg);
				if (listOfEquations.size() <= 3) {
					IExpr C_1 = F.$(F.CSymbol, F.C1); // constant C(1)
					IExpr equation1 = listOfEquations.arg1();

					IExpr temp = solveSingleODE(equation1, uFunction1Arg, xVar, listOfVariables, C_1, engine);
					if (temp.isPresent()) {
						if (listOfEquations.size() == 3) {
							IExpr equation2 = listOfEquations.arg2();
							if (equation2.isFree(xVar)) {
								IExpr[] boundary = solveSingleBoundary(equation2, uFunction1Arg, xVar, listOfVariables,
										engine);
								if (boundary != null) {
									IExpr res = F.subst(temp, F.List(F.Rule(xVar, boundary[1])));
									IExpr C1 = engine.evaluate(F.Roots(F.Equal(res, boundary[0]), C_1));
									if (C1.isAST(F.Equal, 3, C_1)) {
										res = F.subst(temp, F.List(F.Rule(C_1, ((IAST) C1).arg2())));
										return F.List(F.List(F.Rule(uFunction1Arg, res)));
									}  
								}
							}
							return F.NIL;
						}
						return F.List(F.List(F.Rule(uFunction1Arg, temp)));
					}
				}
			}
		}
		return F.NIL;
	}

	private IExpr solveSingleODE(IExpr equation, IAST uFunction1Arg, IExpr xVar, IAST listOfVariables, IExpr C_1,
			EvalEngine engine) {
		ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, listOfVariables,
				listOfVariables.size() - 1);

		if (equation.isAST()) {
			IAST eq = ((IAST) equation).clone();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			// TODO implement other expressions then Plus(...)
			int j = 1;
			IAST[] deriveExpr = null;
			while (j < eq.size()) {
				IAST[] temp = eq.get(j).isDerivative();
				if (temp != null) {
					if (deriveExpr != null) {
						// TODO manage multiple Derive() functions in one
						// expression
						return F.NIL;
					}
					deriveExpr = temp;
					// eliminate deriveExpr from Plus(...) expression
					eq.remove(j);
					continue;
				}
				j++;
			}
			if (deriveExpr != null) {
				try {
					int order = -1;
					if (deriveExpr.length == 3) {
						if (deriveExpr[0].size() == 2 && deriveExpr[0].arg1().isInteger()) {
							order = ((IInteger) deriveExpr[0].arg1()).toInt();
							// TODO check how and that the uFunction and
							// xVar is used in the deriv expression...
						}
					}

					ExprPolynomial poly = ring.create(eq.getOneIdentity(F.C0), false);
					if (order == 1 && poly.degree() <= 1) {
						IAST coeffs = poly.coefficientList();
						IExpr q = coeffs.get(1); // degree 0
						IExpr p = F.C0;
						if (poly.degree() == 1) {
							p = coeffs.get(2); // degree 1
						}
						return linearODE(p, q, uFunction1Arg, xVar, C_1, engine);
					}
				} catch (RuntimeException re) {

				}
			}
		}
		return F.NIL;
	}

	private IExpr[] solveSingleBoundary(IExpr equation, IAST uFunction1Arg, IExpr xVar, IAST listOfVariables,
			EvalEngine engine) {
		if (equation.isAST()) {
			IAST eq = ((IAST) equation).clone();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			int j = 1;
			IExpr uArg1 = null;
			IExpr head = uFunction1Arg.head();
			while (j < eq.size()) {
				if (eq.get(j).isAST(head, uFunction1Arg.size())) {
					uArg1 = ((IAST) eq.get(j)).arg1();
					eq.remove(j);
					continue;
				}
				j++;
			}
			if (uArg1 != null) {
				IExpr[] result = new IExpr[2];
				result[0] = uArg1;
				result[1] = engine.evaluate(eq.getOneIdentity(F.C0).negate());
				return result;
			}

		}
		return null;
	}

	/**
	 * Solve linear ODE.
	 * 
	 * @param p
	 *            coefficient of degree 1
	 * @param q
	 *            coefficient of degree 0
	 * @param xVar
	 *            variable
	 * @param engine
	 *            the evaluation engine
	 * @return <code>F.NIL</code> if the evaluation was not possible
	 */
	private IExpr linearODE(IExpr p, IExpr q, IExpr uFunction, IExpr xVar, IExpr C_1, EvalEngine engine) {
		// integrate p
		IExpr pInt = engine.evaluate(F.Exp(F.Integrate(p, xVar)));

		if (q.isZero()) {
			return engine.evaluate(F.Divide(C_1, pInt));
		} else {
			IExpr qInt = engine.evaluate(F.Plus(C_1, F.Expand(F.Integrate(F.Times(F.CN1, q, pInt), xVar))));
			return engine.evaluate(F.Expand(F.Divide(qInt, pInt)));
		}
	}

}
