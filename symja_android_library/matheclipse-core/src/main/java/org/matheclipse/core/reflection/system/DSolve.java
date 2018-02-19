package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;

import com.duy.lambda.Consumer;

/**
 * <pre>
 * DSolve(equation, f(var), var)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * attempts to solve a linear differential <code>equation</code> for the function <code>f(var)</code> and variable
 * <code>var</code>.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">Wikipedia - Ordinary differential
 * equation</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; DSolve({y'(x)==y(x)+2},y(x), x)
 * {{y(x)-&gt;-2+E^x*C(1)}}
 * 
 * &gt;&gt;&gt; DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)
 * {{y(x)-&gt;-2+3*E^x}}
 * </pre>
 * 
 * <h3>Related terms</h3>
 * <p>
 * <a href="Factor.md">Factor</a>, <a href="FindRoot.md">FindRoot</a>,
 * <a href="NRoots.md">NRoots</a>,<a href="Solve.md">Solve</a>
 * </p>
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

		if (ast.arg2().isAST()) {
			IAST uFunction1Arg = (IAST) ast.arg2();
			IExpr xVar = ast.arg3();
			IASTAppendable listOfEquations = Validate.checkEquations(ast, 1).copyAppendable();
			IExpr[] boundaryCondition = null;
			int i = 1;
			while (i < listOfEquations.size()) {
				IExpr equation = listOfEquations.get(i);
				if (equation.isFree(xVar)) {
					boundaryCondition = solveSingleBoundary(equation, uFunction1Arg, xVar, engine);
					if (boundaryCondition != null) {
						listOfEquations.remove(i);
						break;
					}
				}
				i++;
			}

			if (uFunction1Arg.isAST1() && uFunction1Arg.arg1().equals(xVar)) {
				IAST listOfVariables = F.List(uFunction1Arg);
				if (listOfEquations.size() <= 2) {
					IExpr C_1 = F.$(F.CSymbol, F.C1); // constant C(1)
					IExpr equation = listOfEquations.arg1();
					IExpr temp = solveSingleODE(equation, uFunction1Arg, xVar, listOfVariables, C_1, engine);
					if (!temp.isPresent()) {
						temp = odeSolve(engine, equation, xVar, uFunction1Arg, C_1);
					}
					if (temp.isPresent()) {
						if (boundaryCondition != null) {
							IExpr res = F.subst(temp, F.List(F.Rule(xVar, boundaryCondition[0])));
							IExpr C1 = engine.evaluate(F.Roots(F.Equal(res, boundaryCondition[1]), C_1));
							if (C1.isAST(F.Equal, 3, C_1)) {
								res = F.subst(temp, F.List(F.Rule(C_1, ((IAST) C1).arg2())));
								return F.List(F.List(F.Rule(uFunction1Arg, res)));
							}
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
			IASTAppendable eq = ((IAST) equation).copyAppendable();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			int j = 1;
			IAST[] deriveExpr = null;
			while (j < eq.size()) {
				IAST[] temp = eq.get(j).isDerivativeAST1();
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

				int order = derivativeOrder(deriveExpr);
				if (order < 0) {
					return F.NIL;
				}
				try {
					ExprPolynomial poly = ring.create(eq.getOneIdentity(F.C0), false, true);
					if (order == 1 && poly.degree() <= 1) {
						IAST coeffs = poly.coefficientList();
						IExpr q = coeffs.get(1); // degree 0
						IExpr p = F.C0;
						if (poly.degree() == 1) {
							p = coeffs.get(2); // degree 1
						}
						return linearODE(p, q, uFunction1Arg, xVar, C_1, engine);
					}
				} catch (RuntimeException rex) {
					if (Config.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
			}
		}
		return F.NIL;
	}

	public static int derivativeOrder(IAST[] deriveExpr) {
		int order = -1;
		try {
			if (deriveExpr.length == 3) {
				if (deriveExpr[0].isAST1() && deriveExpr[0].arg1().isInteger()) {
					order = ((IInteger) deriveExpr[0].arg1()).toInt();
					// TODO check how and that the uFunction and
					// xVar is used in the derive expression...
				}
			}
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		}
		return order;
	}

	/**
	 * Equation <code>-1+y(0)</code> gives <code>[0, 1]</code> (representing the boundary equation y(0)==1)
	 * 
	 * @param equation
	 *            the equation
	 * @param uFunction1Arg
	 *            function name <code>y(x)</code>
	 * @param xVar
	 *            variable <code>x</code>
	 * @param engine
	 * @return
	 */
	private IExpr[] solveSingleBoundary(IExpr equation, IAST uFunction1Arg, IExpr xVar, EvalEngine engine) {
		if (equation.isAST()) {
			IASTAppendable eq = ((IAST) equation).copyAppendable();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			int j = 1;
			IExpr uArg1 = null;
			IExpr head = uFunction1Arg.head();
			while (j < eq.size()) {
				// TODO check for negative expression (i.e. Times[-1, eq.get(j)]
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

	private static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1) {
		IExpr[] p = odeTransform(engine, w, x, y);
		if (p != null) {
			IExpr m = p[0];
			IExpr n = p[1];
			IExpr f = odeSeparable(engine, m, n, x, y, C_1);
			if (f.isPresent()) {
				return f;
			}
			// return exactSolve(engine, m, n, x, y);
		}
		return F.NIL;
	}

	private static IExpr[] odeTransform(EvalEngine engine, IExpr w, IExpr x, IExpr y) {
		IExpr v = engine.evaluate(F.Together(w));
		IExpr numerator = engine.evaluate(F.Numerator(v));
		IExpr dyx = engine.evaluate(F.D(y, x));
		IExpr m = engine.evaluate(F.Coefficient(numerator, dyx, F.C0));
		IExpr n = engine.evaluate(F.Coefficient(numerator, dyx, F.C1));
		return new IExpr[] { m, n };
	}

	private static IExpr odeSeparable(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1) {
		if (n.isOne()) {
			IExpr fxExpr = F.NIL;
			IExpr gyExpr = F.NIL;
			if (m.isFree(y)) {
				gyExpr = F.C1;
				fxExpr = m;
			} else if (m.isTimes()) {
				IAST timesAST = (IAST) m;
				IASTAppendable fx = F.TimesAlloc(timesAST.size());
				IASTAppendable gy = F.TimesAlloc(timesAST.size());
				timesAST.forEach(new Consumer<IExpr>() {
                    @Override
                    public void accept(IExpr expr) {
                        if (expr.isFree(y)) {
                            fx.append(expr);
                        } else {
                            gy.append(expr);
                        }
                    }
                });
				fxExpr = engine.evaluate(fx);
				gyExpr = engine.evaluate(gy);
			}
			if (fxExpr.isPresent() && gyExpr.isPresent()) {
				gyExpr = engine.evaluate(F.Integrate(F.Divide(F.C1, gyExpr), y));
				fxExpr = engine.evaluate(F.Plus(F.Integrate(F.Times(F.CN1, fxExpr), x), C_1));
				IExpr yEquation = engine.evaluate(F.Subtract(gyExpr, fxExpr));
				IExpr result = Eliminate.extractVariable(yEquation, y);
				if (result.isPresent()) {
					return result;
				}
			}

		}
		return F.NIL;
	}

	/**
	 * An implicit solution to the differential equation <code>m + n*(dy/dx) == 0</code> or <code>F.NIL</code>.
	 * 
	 * @param m
	 *            algebraic expression
	 * @param n
	 *            algebraic expression
	 * @param x
	 *            symbol
	 * @param y
	 *            symbol
	 * @return
	 */
	private static IExpr exactSolve(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y) {

		if (n.isZero()) {
			return F.NIL;
		}
		if (m.isZero()) {
			return F.Equal(y, F.CSymbol);
		}
		IExpr my = engine.evaluate(F.D(m, y));
		IExpr nx = engine.evaluate(F.D(n, x));

		IExpr d = engine.evaluate(F.Subtract(my, nx));

		IExpr u = F.NIL;
		if (d.isZero()) {
			u = F.C1;
		} else {
			IExpr f = engine.evaluate(F.Together(F.Divide(d, n)));
			if (f.isFree(y)) {
				u = engine.evaluate(F.Exp(F.Integrate(f, x)));
				d = F.C0;
			} else {
				IExpr g = engine.evaluate(F.Simplify(F.Divide(d.negate(), m)));
				if (g.isFree(x)) {
					u = engine.evaluate(F.Exp(F.Integrate(g, y)));
					d = F.C0;
				}
			}
		}

		if (d.isZero()) {
			IExpr g = engine.evaluate(F.Integrate(F.Times(u, m), x));
			IExpr hp = engine.evaluate(F.Subtract(F.Times(u, n), F.D(g, y)));
			IExpr h = engine.evaluate(F.Integrate(hp, y));
			return F.Equal(engine.evaluate(F.Plus(g, h)), F.CSymbol);
		}

		return F.NIL;
	}

	// public static void main(String[] args) {
	// Config.SERVER_MODE = false;
	// F.initSymbols();
	// F.join();
	// EvalEngine engine = new EvalEngine(true);
	// EvalEngine.set(engine);
	//
	// IExpr fy = F.y;
	// IExpr C_1 = F.$(F.CSymbol, F.C1); // constant C(1)
	//
	// // IExpr result = exactSolve(engine, F.Power(fy, F.CN2), F.Times(F.C2, F.x), fy, F.x);
	// IExpr result = odeSolve(engine, F.Equal(F.Times(F.C2, F.Power(fy, F.C2), F.x), F.C0), fy, F.x, C_1);
	//
	// result = engine.evaluate(F.Solve(result, F.y));
	// System.out.println(result.toString());
	// }

}
