package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Limit of a function. See <a
 * href="http://en.wikipedia.org/wiki/List_of_limits">List of Limits</a>
 */
public class Limit extends AbstractFunctionEvaluator {
	/**
	 * Try L'hospitales rule. See <a
	 * href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">Wikipedia
	 * L'Hôpital's rule</a>
	 * 
	 * @param numerator
	 * @param denominator
	 * @param sym
	 * @param rule
	 * @return
	 */
	private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, ISymbol sym, IExpr lim, IAST rule) {
		EvalEngine engine = EvalEngine.get();
		int limit = engine.getRecursionLimit();
		if (limit > 0) {
			IExpr expr = F.eval(F.Times(F.D(numerator, sym), F.Power(F.D(denominator, sym), F.CN1)));
			return limit(expr, sym, lim, rule, false);
		}
		try {
			if (limit <= 0) {
				// set recursion limit for using l'Hospitales rule
				engine.setRecursionLimit(128);
			}
			IExpr expr = F.eval(F.Times(F.D(numerator, sym), F.Power(F.D(denominator, sym), F.CN1)));
			return limit(expr, sym, lim, rule, false);
		} catch (RecursionLimitExceeded rle) {
			engine.setRecursionLimit(limit);
		} finally {
			engine.setRecursionLimit(limit);
		}
		return null;
	}

	public static IExpr limit(final IExpr expr, ISymbol sym, IExpr lim, IAST rule, boolean evalExpr) {
		IExpr expression = expr;
		if (evalExpr) {
			IExpr result = F.eval(expression);
			if (result.isNumericFunction()) {
				return result;
			}
			if (!result.equals(F.Indeterminate)) {
				expression = result;
			}

			if (lim.isNumericFunction()) {
				result = expression.replaceAll(rule);
				if (result != null) {
					result = F.eval(result);
					if (result.isNumericFunction()) {
						return result;
					}
				}
			}
		}
		if (expression.isFree(sym, true)) {
			// Limit[a_,sym->lim] -> a
			return expression;
		}
		if (expression.equals(sym)) {
			// Limit[x_,x_->lim] -> lim
			return lim;
		}
		if (expression.isAST()) {
			final IAST arg1 = (IAST) expression;
			final IExpr header = arg1.head();
			if (arg1.size() == 2) {
				if (header.equals(F.Sin) || header.equals(F.Cos)) {
					return F.$(header, F.Limit(arg1.get(1), rule));
				}
			}
			if (header == F.Plus) {
				// Limit[a_+b_+c_,sym->lim] ->
				// Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
				return mapLimit(arg1, rule);
			} else if (header == F.Times) {
				IExpr[] parts = Apart.getFractionalPartsTimes(arg1, false);
				if (parts != null) {
					IAST plusResult = Apart.apart(parts, F.List(sym));
					if (plusResult != null) {
						// OneIdentity if plusResult.size() == 2
						if (plusResult.size() > 2) {
							return mapLimit(plusResult, rule);
						}
					}
				}
				IExpr numerator = parts[0];
				IExpr denominator = parts[1];
				IExpr temp = timesLimit(numerator, denominator, sym, lim, rule);
				if (temp != null) {
					return temp;
				}
				return mapLimit(arg1, rule);
			} else if (arg1.isAST(F.Power, 3)) {
				if (arg1.get(2).isInteger()) {
					// Limit[a_^n_,sym->lim] -> Limit[a,sym->lim]^n
					IInteger n = (IInteger) arg1.get(2);
					IExpr temp = F.eval(F.Limit(arg1.get(1), rule));
					if (temp.isInfinity()) {
						if (n.isPositive()) {
							return temp;
						} else if (n.isNegative()) {
							return F.C0;
						}
						return null;
					} else if (temp.isNegativeInfinity()) {
						if (n.isPositive()) {
							if (n.isEven()) {
								return F.CInfinity;
							} else {
								return F.CNInfinity;
							}
						} else if (n.isNegative()) {
							return F.C0;
						}
						return null;
					} else if (temp.equals(F.Indeterminate) || temp.isAST(F.Limit)) {
						return null;
					}
					if (n.isPositive()) {
						return F.Power(temp, n);
					} else if (n.isNegative() && n.isEven()) {
						return F.Power(temp, n);
					}

				}
			}

		}

		return null;
	}

	private static IExpr mapLimit(final IAST expr, IAST rule) {
		final IAST resultList = expr.clone();
		for (int i = 1; i < resultList.size(); i++) {
			resultList.set(i, F.Limit(resultList.get(i), rule));
		}
		return resultList;
	}

	/**
	 * 
	 * @param numerator
	 * @param denominator
	 * @param sym
	 * @param lim
	 * @param rule
	 * @return <code>null</code> if no limit found
	 */
	private static IExpr timesLimit(final IExpr numerator, final IExpr denominator, ISymbol sym, IExpr lim, IAST rule) {
		IExpr numValue;
		IExpr denValue;
		if (denominator.isOne() && numerator.isTimes()) {
			// Limit[a_*b_*c_,sym->lim] ->
			// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
			return mapLimit((IAST) numerator, rule);
		}
		if (!denominator.isNumber() || denominator.isZero()) {
			denValue = F.evalBlock(denominator, sym, lim);
			if (denValue.equals(F.Indeterminate)) {
				return null;
			} else if (denValue.isZero()) {
				numValue = F.evalBlock(numerator, sym, lim);
				if (numValue.isZero()) {
					return lHospitalesRule(numerator, denominator, sym, lim, rule);
				}
				return null;
			} else if (F.CInfinity.equals(denValue)) {
				numValue = F.evalBlock(numerator, sym, lim);
				if (F.CInfinity.equals(numValue)) {
					return lHospitalesRule(numerator, denominator, sym, lim, rule);
				}
				return null;
			} else if (denValue.isNegativeInfinity()) {
				numValue = F.evalBlock(numerator, sym, lim);
				if (numValue.isNegativeInfinity()) {
					return lHospitalesRule(numerator, denominator, sym, lim, rule);
				}
				return null;
			}
		}
		return F.Times(F.Limit(numerator, rule), F.Power(F.Limit(denominator, rule), F.CN1));
	}

	private String[] RULES = { "Limit[x_^n_IntegerQ, x_Symbol->Infinity]:= 0 /; Negative[n]",
			"Limit[x_^n_IntegerQ, x_Symbol->DirectedInfinity[-1]]:= 0 /; Negative[n]",
			"Limit[(1+x_^(-1))^x_, x_Symbol->Infinity]=E", "Limit[(1-x_^(-1))^x_, x_Symbol->Infinity]=E^(-1)", };

	public Limit() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (!ast.get(2).isRuleAST()) {
			return null;
		}
		IAST rule = (IAST) ast.get(2);
		if (!(rule.get(1).isSymbol())) {
			return null;
		}
		ISymbol sym = (ISymbol) rule.get(1);
		IExpr lim = null;
		if (rule.get(2).isFree(sym, true)) {
			lim = rule.get(2);
		} else {
			return null;
		}
		return limit(ast.get(1), sym, lim, rule, true);
	}

	@Override
	public String[] getRules() {
		return RULES;
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
		super.setUp(symbol);
	}
}