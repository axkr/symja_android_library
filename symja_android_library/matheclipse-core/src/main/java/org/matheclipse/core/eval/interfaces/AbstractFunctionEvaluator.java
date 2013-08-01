package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * 
 */
public abstract class AbstractFunctionEvaluator implements IFunctionEvaluator {

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	/**
	 * Get the predefined rules for this function symbol. If no rules are
	 * available return <code>null</code>
	 * 
	 * @return
	 */
	public String[] getRules() {
		return null;
	}

	public IAST getRuleAST() {
		return null;
	}

	/**
	 * Evaluate built-in rules and define Attributes for a function.
	 * 
	 */
	public void setUp(final ISymbol symbol) throws SyntaxError {
		IAST ruleList;
		final EvalEngine engine = EvalEngine.get();
		if ((ruleList = getRuleAST()) != null) {
			engine.addRules(ruleList);
		}
		String[] rules;
		if ((rules = getRules()) != null) {
			final Parser parser = new Parser();

			boolean oldPackageMode = engine.isPackageMode();
			boolean oldTraceMode = engine.isTraceMode();
			try {
				engine.setPackageMode(true);
				engine.setTraceMode(false);
				// if (session != null) {
				// parser.setFactory(ExpressionFactory.get());
				// }
				if (Config.DEBUG) {
					try {
						setUpRules(rules, parser, engine);
					} catch (final Throwable th) {
						th.printStackTrace();
					}
				} else {
					setUpRules(rules, parser, engine);
				}
			} finally {
				engine.setPackageMode(oldPackageMode);
				engine.setTraceMode(oldTraceMode);
			}
		}
		F.SYMBOL_OBSERVER.createPredefinedSymbol(symbol.toString());
	}

	private void setUpRules(final String[] rules, final Parser parser, final EvalEngine engine) {
		for (int i = 0; i < rules.length; i++) {
			final ASTNode parsedAST = parser.parse(rules[i]);
			final IExpr obj = AST2Expr.CONST.convert(parsedAST);
			// engine.init();
			engine.evaluate(obj);
		}

	}

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
			if (times.get(1).isNumber()) {
				if (((INumber) times.get(1)).complexSign() < 0) {
					return true;
				}
			}
		} else if (expr.isPlus()) {
			IAST plus = (IAST) expr;
			if (plus.get(1).isNumber()) {
				if (((INumber) plus.get(1)).complexSign() < 0) {
					return true;
				}
			}
		}
	
		return false;
	}

}