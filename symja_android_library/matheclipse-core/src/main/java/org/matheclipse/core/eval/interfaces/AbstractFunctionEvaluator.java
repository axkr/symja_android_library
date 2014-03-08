package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates to the <code>evaluate()</code>
 * 
 */
public abstract class AbstractFunctionEvaluator implements IFunctionEvaluator {

	/** {@inheritDoc} */
	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	/**
	 * Get the predefined rules for this function symbol. If no rules are available return <code>null</code>.
	 * 
	 * @return
	 */
	// public String[] getRules() {
	// return null;
	// }

	public IAST getRuleAST() {
		return null;
	}

	/**
	 * Evaluate built-in rules and define Attributes for a function.
	 * 
	 */
	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		IAST ruleList;
		final EvalEngine engine = EvalEngine.get();
		if ((ruleList = getRuleAST()) != null) {
			engine.addRules(ruleList);
		}

		// String[] rules;
		// if ((rules = getRules()) != null) {
		// final Parser parser = new Parser();
		//
		// boolean oldPackageMode = engine.isPackageMode();
		// boolean oldTraceMode = engine.isTraceMode();
		// try {
		// engine.setPackageMode(true);
		// engine.setTraceMode(false);
		// // if (session != null) {
		// // parser.setFactory(ExpressionFactory.get());
		// // }
		// if (Config.DEBUG) {
		// try {
		// setUpRules(rules, parser, engine);
		// } catch (final Throwable th) {
		// th.printStackTrace();
		// }
		// } else {
		// setUpRules(rules, parser, engine);
		// }
		// } finally {
		// engine.setPackageMode(oldPackageMode);
		// engine.setTraceMode(oldTraceMode);
		// }
		// }
		F.SYMBOL_OBSERVER.createPredefinedSymbol(symbol.toString());
	}

	// private void setUpRules(final String[] rules, final Parser parser, final EvalEngine engine) {
	// for (int i = 0; i < rules.length; i++) {
	// final ASTNode parsedAST = parser.parse(rules[i]);
	// final IExpr obj = AST2Expr.CONST.convert(parsedAST);
	// // engine.init();
	// engine.evaluate(obj);
	// }
	//
	// }

	/** {@inheritDoc} */
	@Override
	abstract public IExpr evaluate(final IAST ast);

	/**
	 * Create a rule which invokes the method name in this class instance.
	 * 
	 * @param symbol
	 * @param patternString
	 * @param methodName
	 */
	public void createRuleFromMethod(ISymbol symbol, String patternString, String methodName) {
		PatternMatcherAndInvoker pm = new PatternMatcherAndInvoker(patternString, this, methodName);
		symbol.putDownRule(pm);
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @return <code>true</code> if the first argument is canonical negative
	 */
	public static boolean isNegativeExpression(final IExpr expr) {
		if (expr.isNumber()) {
			if (((INumber) expr).complexSign() < 0) {
				return true;
			}
		} else if (expr.isTimes()) {
			IAST times = (IAST) expr;
			if (times.arg1().isNumber()) {
				if (((INumber) times.arg1()).complexSign() < 0) {
					return true;
				}
			}
		} else if (expr.isPlus()) {
			IAST plus = (IAST) expr;
			if (plus.arg1().isNumber()) {
				if (((INumber) plus.arg1()).complexSign() < 0) {
					return true;
				}
				// } else {
				// IAST times = null;
				// for (int i = 1; i < plus.size(); i++) {
				// if (plus.get(i).isAST()) {
				// if (times != null) {
				// return false;
				// }
				// if (plus.get(i).isTimes()) {
				// times = (IAST) plus.get(i);
				// if (times.arg1().isNumber()) {
				// if (((INumber) times.arg1()).complexSign() < 0) {
				// continue;
				// }
				// }
				// }
				// return false;
				// }
				// }
				// return true;
			}
		}

		return false;
	}

	/**
	 * Check if <code>expr</code> is a pure imaginary number without a real part.
	 * 
	 * @param expr
	 * @return <code>null</code>, if <code>expr</code> is not prue imaginary number.
	 */
	public static IExpr getPureImaginaryPart(final IExpr expr) {
		if (expr.isComplex() && ((IComplex) expr).getRe().isZero()) {
			IComplex compl = (IComplex) expr;
			return compl.getIm();
		}
		if (expr.isTimes()) {
			IAST times = ((IAST) expr);
			IExpr arg1 = times.arg1();
			if (arg1.isComplex() && ((IComplex) arg1).getRe().isZero()) {
				times = times.clone();
				times.set(1, ((IComplex) arg1).getIm());
				return times;
			}
		}
		return null;
	}

	public static IExpr[] getPeriodicParts(final IExpr expr) {
		if (expr.isPlus()) {
			IAST plus = (IAST) expr;
			for (int i = 0; i < plus.size(); i++) {
				if (plus.get(i).isTimes()) {
					IAST times = (IAST) plus.get(i);
					if (times.size() == 3 && times.arg2().equals(F.Pi)) {
						if (times.arg1().isRational()) {
							IExpr[] result = new IExpr[2];
							IAST cloned = plus.clone();
							cloned.remove(i);
							result[0] = cloned;
							result[1] = times.arg1();
							return result;
						}
					}
				}
			}

		}
		return null;
	}

}