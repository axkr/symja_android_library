package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.parser.client.SyntaxError;

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
		if ((ruleList = getRuleAST()) != null) {
			EvalEngine.get().addRules(ruleList);
		}

		F.SYMBOL_OBSERVER.createPredefinedSymbol(symbol.toString());
	}

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
			IExpr arg1 = ((IAST) expr).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (expr.isPlus()) {
			IExpr arg1 = ((IAST) expr).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (expr.isNegativeInfinity()) {
			return true;
		}

		return false;
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @return <code>true</code> if the first argument is canonical negative
	 */
	public static IExpr getNormalizedNegativeExpression(final IExpr expr) {
		IAST result;
		if (expr.isNumber()) {
			if (((INumber) expr).complexSign() < 0) {
				return ((INumber) expr).negate();
			}
		}
		if (expr.isAST()) {
			if (expr.isTimes()) {
				IAST timesAST = ((IAST) expr);
				IExpr arg1 = timesAST.arg1();
				if (arg1.isNumber()) {
					if (((INumber) arg1).complexSign() < 0) {
						result = timesAST.clone();
						result.set(1, ((INumber) arg1).negate());
						return result;
					}
				} else if (arg1.isNegativeInfinity()) {
					result = timesAST.clone();
					result.set(1, F.CInfinity);
					return result;
//				} else {
//					IExpr arg1Negated = getNormalizedNegativeExpression(arg1);
//					if (arg1Negated != null) {
//						for (int i = 2; i < timesAST.size(); i++) {
//							IExpr temp = timesAST.get(i);
//							if (temp.isPlus()||temp.isTimes()) {
//								return null;
//							}
//						}
//						result = timesAST.clone();
//						result.set(1, arg1Negated);
//						return result;
//					}
				}
			} else if (expr.isPlus()) {
				IAST plusAST = ((IAST) expr);
				IExpr arg1 = plusAST.arg1();
				if (arg1.isNumber()) {
					if (((INumber) arg1).complexSign() < 0) {
						result = plusAST.clone();
						result.set(1, arg1.negate());
						for (int i = 2; i < plusAST.size(); i++) {
							result.set(i, plusAST.get(i).negate());
						}
						return result;
					}
				} else if (arg1.isNegativeInfinity()) {
					result = plusAST.clone();
					result.set(1, F.CInfinity);
					for (int i = 2; i < plusAST.size(); i++) {
						result.set(i, plusAST.get(i).negate());
					}
					return result;
				} else {
					if (arg1.isTimes()) {
						IExpr arg1Negated = getNormalizedNegativeExpression(arg1);
						if (arg1Negated != null) {
							int positiveElementsCounter = 0;
							result = plusAST.clone();
							result.set(1, arg1Negated);
							for (int i = 2; i < plusAST.size(); i++) {
								IExpr temp = plusAST.get(i);
								if (!temp.isTimes() && !temp.isPower()) {
									return null;
								}
								arg1Negated = getNormalizedNegativeExpression(temp);
								if (arg1Negated != null) {
									result.set(i, arg1Negated);
								} else {
									positiveElementsCounter++;
									if (positiveElementsCounter * 2 >= plusAST.size()-1) {
										// number of positive elements is greater than number of negative elements
										return null;
									}
									result.set(i, temp.negate());
								}
							}
							return result;
						}
					}
				}
			} else if (expr.isNegativeInfinity()) {
				return F.CInfinity;
			}
		}
		return null;
	}

	/**
	 * Check if <code>expr</code> is a pure imaginary number without a real part.
	 * 
	 * @param expr
	 * @return <code>null</code>, if <code>expr</code> is not a pure imaginary number.
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