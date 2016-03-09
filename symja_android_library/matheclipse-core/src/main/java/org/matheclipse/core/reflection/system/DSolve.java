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
		IExpr uFunction = ast.arg2();
		IExpr xVar = ast.arg3();
		IAST listOfVariables = F.List(uFunction);
		if (listOfEquations.size() == 2) {
			IExpr equation = F.evalExpandAll(listOfEquations.arg1());

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
							return linearODE(p, q, uFunction, xVar, engine);
						}
					} catch (RuntimeException re) {

					}
				}
			}

		}
		return F.NIL;
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
	private IExpr linearODE(IExpr p, IExpr q, IExpr uFunction, IExpr xVar, EvalEngine engine) {
		// integrate p
		IExpr pInt = engine.evaluate(F.Integrate(p, xVar));
		IExpr C_1 = F.$(F.CSymbol, F.C1); // constant C(1)
		IAST result = F.List();
		if (q.isZero()) {
			// homogenous linear ODE
			result.add(F.List(F.Rule(uFunction, F.Times(C_1, F.Exp(F.Times(F.CN1, pInt))))));
			return result;
		} else {
			// TODO implement inhomogenous linear ODE
		}
		return F.NIL;
	}

}
