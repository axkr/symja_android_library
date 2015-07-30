package org.matheclipse.core.reflection.system;

import java.util.Map;
import java.util.HashMap;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.DerivativeRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Determine the derivative of a given function symbol.
 */
public class Derivative extends AbstractFunctionEvaluator implements DerivativeRules {

	/**
	 * Mapped symbol to value for Derivative[1][&lt;symbol&gt;]
	 */
	private static Map<ISymbol, IExpr> DERIVATIVE_1_MAP = new HashMap<ISymbol, IExpr>(197);

	/**
	 * Mapped symbol to value for Derivative[2][&lt;symbol&gt;]
	 */
	private static Map<ISymbol, IExpr> DERIVATIVE_2_MAP = new HashMap<ISymbol, IExpr>(97);

	/**
	 * Mapped symbol to value for Derivative[&lt;n&gt;][&lt;symbol&gt;]
	 */
	private static Map<ISymbol, IExpr> DERIVATIVE_N_MAP = new HashMap<ISymbol, IExpr>(197);

	/**
	 * Mapped symbol to value for Derivative[&lt;n&gt;, &lt;m&gt;][&lt;symbol&gt;]
	 */
	private static Map<IAST, IExpr> DERIVATIVE_N_M_MAP = new HashMap<IAST, IExpr>(197);

	static {
		for (int i = 1; i < RULES1.size(); i++) {
			IAST rule = (IAST) RULES1.get(i);
			// Derivative[1][symbol]
			DERIVATIVE_1_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		}
		for (int i = 1; i < RULES2.size(); i++) {
			IAST rule = (IAST) RULES2.get(i);
			// Derivative[2][symbol]
			DERIVATIVE_2_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		}
		for (int i = 1; i < RULES3.size(); i++) {
			IAST rule = (IAST) RULES3.get(i);
			// Derivative[n][symbol]
			DERIVATIVE_N_MAP.put((ISymbol) rule.arg1(), rule.arg2());
		}
		for (int i = 1; i < RULES4.size(); i++) {
			IAST rule = (IAST) RULES4.get(i);
			// Derivative[n][symbol]
			DERIVATIVE_N_M_MAP.put((IAST) rule.arg1(), rule.arg2());
		}
	}

	public Derivative() {
	}

	@Override
	public IExpr evaluate(IAST ast) {
		if (ast.size() == 2) {
			if (ast.head().isAST(F.Derivative, 2)) {
				// Derivative(n)
				IAST head = (IAST) ast.head();
				if (head.arg1().isInteger()) {
					try {
						int n = ((IInteger) head.arg1()).toInt();
						IExpr arg1 = ast.arg1();
						if (n > 0) {
							if (arg1.isSymbol()) {
								ISymbol symbol = (ISymbol) arg1;
								return derivative(n, symbol);
							}
						}
					} catch (ArithmeticException ae) {

					}
				}
				return null;
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
								return derivative(n, m, symbol);
							}
						}
					} catch (ArithmeticException ae) {

					}
				}
				return null;
			}
		}
		return null;
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
	public static IExpr derivative(int n, ISymbol symbol) {
		if (n == 1) {
			// Derivative[1][symbol]
			IExpr result = DERIVATIVE_1_MAP.get(symbol);
			if (result != null) {
				return result;
			}
		}
		if (n == 2) {
			// Derivative[2][symbol]
			IExpr result = DERIVATIVE_2_MAP.get(symbol);
			if (result != null) {
				return result;
			}
		}
		if (n > 0) {
			// Derivative[n][symbol]
			IExpr result = DERIVATIVE_N_MAP.get(symbol);
			if (result != null) {
				// replace Slot[2] with the integer number
				IAST slotsList = F.List(null, F.integer(n));
				result = result.replaceSlots(slotsList);
				return result;
			}
		}
		return null;
	}

	/**
	 * Get the (n, m)-th derivative (<code>Derivative[n, m][symbol]</code>) if possible. Otherwise return <code>null</code>
	 * 
	 * @param n
	 *            differentiating <code>n</code> times with respect to the 1. argument
	 * @param m
	 *            differentiating <code>m</code> times with respect to the 2. argument
	 * @param symbol
	 *            the function symbol which should be searched in the look-up table.
	 * @return <code>null</code> if no entry was found
	 */
	public static IExpr derivative(int n, int m, ISymbol symbol) {
		IAST listKey = F.List(symbol, F.integer(n), F.integer(m));
		return DERIVATIVE_N_M_MAP.get(listKey);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.NHOLDALL);
		super.setUp(symbol);
	}

}
