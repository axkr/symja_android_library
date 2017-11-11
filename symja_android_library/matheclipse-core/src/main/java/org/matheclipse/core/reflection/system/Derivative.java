package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.DerivativeRules;

/**
 * <pre>
 * Derivative(n)[f]
 * </pre>
 * 
 * <blockquote>
 * <p>
 * represents the <code>n</code>-th derivative of the function <code>f</code>.<br />
 * </p>
 * </blockquote>
 * 
 * <pre>
 * Derivative(n1, n2, n3,...)[f]
 * </pre>
 * 
 * <blockquote>
 * <p>
 * represents a multivariate derivative.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Derivative(1)[Sin]    
 * Cos(#1)&amp;    
 * 
 * &gt;&gt; Derivative(3)[Sin]    
 * -Cos(#1)&amp;   
 * 
 * &gt;&gt; Derivative(2)[# ^ 3&amp;]    
 * 6*(#1&amp;)
 * </pre>
 * <p>
 * <code>Derivative</code> can be entered using <code>'</code>:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; Sin'(x)    
 * Cos(x)    
 * 
 * &gt;&gt; (# ^ 4&amp;)''    
 * 12*(#1^2&amp;)   
 * 
 * &gt;&gt; f'(x) // FullForm    
 * "Derivative(1)[f][x]"
 * </pre>
 * <p>
 * The <code>0</code>th derivative of any expression is the expression itself:
 * </p>
 * 
 * <pre>
 * &gt;&gt; Derivative(0,0,0)[a+b+c]    
 * a+b+c
 * </pre>
 * <p>
 * Unknown derivatives:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; Derivative(2, 1)[h]    
 * Derivative(2,1)[h]   
 * 
 * &gt;&gt; Derivative(2, 0, 1, 0)[h(g)]    
 * Derivative(2,0,1,0)[h(g)]
 * </pre>
 */
public class Derivative extends AbstractFunctionEvaluator implements DerivativeRules {

	/**
	 * Mapped symbol to value for Derivative[1][&lt;symbol&gt;]
	 */
	// private static Map<ISymbol, IExpr> DERIVATIVE_1_MAP = new IdentityHashMap<ISymbol, IExpr>(197);

	/**
	 * Mapped symbol to value for Derivative[2][&lt;symbol&gt;]
	 */
	// private static Map<ISymbol, IExpr> DERIVATIVE_2_MAP = new IdentityHashMap<ISymbol, IExpr>(97);

	/**
	 * Mapped symbol to value for Derivative[&lt;n&gt;][&lt;symbol&gt;]
	 */
	// private static Map<ISymbol, IExpr> DERIVATIVE_N_MAP = new IdentityHashMap<ISymbol, IExpr>(197);

	/**
	 * Mapped symbol to value for Derivative[&lt;n&gt;, &lt;m&gt;][&lt;symbol&gt;]
	 */
	private static Map<IAST, IExpr> DERIVATIVE_N_M_MAP = new HashMap<IAST, IExpr>(197);

	static {
		// for (int i = 1; i < RULES1.size(); i++) {
		// IAST rule = (IAST) RULES1.get(i);
		// // Derivative[1][symbol]
		// DERIVATIVE_1_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		// }
		// for (int i = 1; i < RULES2.size(); i++) {
		// IAST rule = (IAST) RULES2.get(i);
		// // Derivative[2][symbol]
		// DERIVATIVE_2_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		// }
		// for (int i = 1; i < RULES3.size(); i++) {
		// IAST rule = (IAST) RULES3.get(i);
		// // Derivative[n][symbol]
		// DERIVATIVE_N_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		// }
		for (int i = 1; i < RULES4.size(); i++) {
			IAST rule = (IAST) RULES4.get(i);
			// Derivative[n][symbol]
			DERIVATIVE_N_M_MAP.put((IAST) rule.arg1(), rule.arg2());
		}
	}

	public Derivative() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		IAST[] derivativeAST = ast.isDerivative();
		if (derivativeAST != null) {
			IAST derivativeHead = derivativeAST[0];
			IAST functions = derivativeAST[1];
			if (functions.size() == 2 && functions.arg1().isNumber()) {
				return F.Function(F.C0);
			}
			boolean isZero = true;
			for (int i = 1; i < derivativeHead.size(); i++) {
				if (!derivativeHead.get(i).isZero()) {
					isZero = false;
					break;
				}
			}
			if (isZero) {
				if (derivativeAST[2] == null) {
					return derivativeAST[1].arg1();
				}
			}

			if (derivativeHead.size() == 2) {
				IExpr head = derivativeHead.arg1();
				if (head.isInteger()) {
					// IAST functions = derivativeAST[1];
					if (functions.size() == 2) {
						try {
							int n = ((IInteger) head).toInt();
							if (n >= 1) {
								IAST fullDerivative = derivativeAST[2];
								return evaluateDArg1IfPossible(n, derivativeHead, functions, fullDerivative, engine);
							}
						} catch (ArithmeticException ae) {
							// toInt() may throw ArithmeticException
						}
						return F.NIL;
					}
				}
			}
			if (ast.head().isAST(F.Derivative, 2)) {
				// Derivative(n)
				IAST head = (IAST) ast.head();
				if (head.arg1().isInteger()) {
					try {
						int n = ((IInteger) head.arg1()).toInt();
						IExpr arg1 = ast.arg1();
						if (n >= 0) {
							if (arg1.isSymbol()) {
								ISymbol symbol = (ISymbol) arg1;
								// return derivative(n, symbol, engine);
							} else {
								if (arg1.isFunction()) {
									return derivative(n, (IAST) arg1, engine);
								}
							}
						}
					} catch (ArithmeticException ae) {

					}
				}
				return F.NIL;
			}
			if (ast.head().isAST(F.Derivative, 3)) {
				// Derivative(n, m)
				IAST head = (IAST) ast.head();
				if (head.arg1().isInteger() && head.arg2().isInteger()) {
					try {
						int n = ((IInteger) head.arg1()).toInt();
						int m = ((IInteger) head.arg2()).toInt();
						IExpr arg1 = ast.arg1();
						if (n >= 0 && m >= 0) {
							if (arg1.isSymbol()) {
								ISymbol symbol = (ISymbol) arg1;
								return derivative(n, m, symbol, engine);
							}
						}
					} catch (ArithmeticException ae) {

					}
				}
				return F.NIL;
			}
		}
		return F.NIL;
	}

	/**
	 * Evaluate a <code>Derivative(1)[f]</code> or <code>Derivative(1)[f][x]</code> expression.
	 * 
	 * @param n
	 *            the number of derivative depth
	 * @param head
	 * @param headDerivative
	 * @param fullDerivative
	 * @param engine
	 * @return
	 */
	private IExpr evaluateDArg1IfPossible(int n, IAST head, IAST headDerivative, IAST fullDerivative,
			EvalEngine engine) {
		IExpr newFunction;
		IExpr symbol = F.Slot1;
		if (fullDerivative != null) {
			if (fullDerivative.size() != 2) {
				return F.NIL;
			}
			symbol = fullDerivative.arg1();
		}
		newFunction = engine.evaluate(F.unaryAST1(headDerivative.arg1(), symbol));

		IAST dExpr;
		if (n == 1) {
			dExpr = F.D(newFunction, symbol);
		} else {
			dExpr = F.D(newFunction, F.List(symbol, F.ZZ(n)));
		}
		dExpr.setEvalFlags(IAST.IS_DERIVATIVE_EVALED);

		IExpr dResult = engine.evalRules(F.D, dExpr);

		if (dResult.isPresent()) {
			dResult = engine.evaluate(dResult);
			return F.Function(dResult);
		}
		if (n > 1) {
			for (int i = 0; i < n; i++) {
				dExpr = F.D(newFunction, symbol);
				dExpr.setEvalFlags(IAST.IS_DERIVATIVE_EVALED);
				dResult = engine.evalRules(F.D, dExpr);
				if (!dResult.isPresent()) {
					return F.NIL;
				} else {
					newFunction = engine.evaluate(dResult);
				}
			}
			return F.Function(newFunction);
		}
		return F.NIL;
	}

	/**
	 * Get the n-th derivative (<code>Derivative[n][symbol]</code>) if possible. Otherwise return <code>null</code>
	 * 
	 * @param n
	 *            differentiating <code>n</code> times with respect to the 1. argument
	 * @param symbol
	 *            the function symbol which should be searched in the look-up table.
	 * @return <code>null</code> if no entry was found
	 */
	// public static IExpr derivative(int n, ISymbol symbol, EvalEngine engine) {
	// if (n == 1) {
	// // Derivative[1][symbol]
	// IExpr result = DERIVATIVE_1_MAP.get(symbol);
	// if (result != null) {
	// return F.unaryAST1(F.Function, engine.evaluate(result));
	// }
	// }
	// if (n == 2) {
	// // Derivative[2][symbol]
	// IExpr result = DERIVATIVE_2_MAP.get(symbol);
	// if (result != null) {
	// return F.unaryAST1(F.Function, engine.evaluate(result));
	// }
	// }
	// if (n > 0) {
	// // Derivative[n][symbol]
	// IExpr result = DERIVATIVE_N_MAP.get(symbol);
	// if (result != null) {
	// // replace Slot[2] with the integer number
	// IAST slotsList = F.List(F.NIL, F.integer(n));
	// return F.unaryAST1(F.Function, engine.evaluate(Lambda.replaceSlotsOrElse(result, slotsList, result)));
	// }
	// }
	// return F.NIL;
	// }

	public static IExpr derivative(int n, IAST function, EvalEngine engine) {
		if (n == 0) {
			return function;
		}
		if (n >= 1) {
			if (function.isAST1()) {
				// Derivative[1][(...)&]
				IExpr arg1 = function.arg1();
				if (arg1.isPower()) {
					IAST power = (IAST) arg1;
					if (power.arg1().equals(F.Slot1) && power.arg2().isFree(F.Slot1)) {
						return F.Times(power.arg2(), createDerivative(n - 1,
								F.unaryAST1(F.Function, engine.evaluate(F.Power(F.Slot1, power.arg2().dec())))));

					}
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Get the (n, m)-th derivative (<code>Derivative[n, m][symbol]</code>) if possible. Otherwise return
	 * <code>null</code>
	 * 
	 * @param n
	 *            differentiating <code>n</code> times with respect to the 1. argument
	 * @param m
	 *            differentiating <code>m</code> times with respect to the 2. argument
	 * @param symbol
	 *            the function symbol which should be searched in the look-up table.
	 * @return <code>F.NIL</code> if no entry was found
	 */
	private static IExpr derivative(int n, int m, ISymbol symbol, EvalEngine engine) {
		IAST listKey = F.List(symbol, F.integer(n), F.integer(m));
		IExpr result = DERIVATIVE_N_M_MAP.get(listKey);
		if (result != null) {
			return F.unaryAST1(F.Function, engine.evaluate(result));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDALL);
		super.setUp(newSymbol);
	}

	/**
	 * Create <code>Derivative[n][header][arg1]</code>
	 * 
	 * @param n
	 * @param header
	 * @param arg1
	 * @return
	 */
	public static IAST createDerivative(final int n, final IExpr header, final IExpr arg1) {
		IASTAppendable deriv = F.Derivative(F.integer(n));
		IASTAppendable fDeriv = F.ast(deriv);
		fDeriv.append(header);
		IASTAppendable fDerivParam = F.ast(fDeriv);
		fDerivParam.append(arg1);
		return fDerivParam;
	}

	/**
	 * Create <code>Derivative[n][header][arg1]</code>
	 * 
	 * @param n
	 * @param header
	 * @param arg1
	 * @return
	 */
	public static IAST createDerivative(final int n, final IExpr header) {
		IAST deriv = F.Derivative(F.integer(n));
		IASTAppendable fDeriv = F.ast(deriv);
		fDeriv.append(header);
		return fDeriv;
	}
}
