package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Collect expressions.
 */
public class Collect extends AbstractFunctionEvaluator {

	public Collect() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		try {
			if (!ast.get(2).isList()) {
				IExpr arg1 = F.evalExpandAll(ast.get(1));
				if (arg1.isAST()) {
					Map<IExpr, IAST> map = new HashMap<IExpr, IAST>();
					IAST poly = (IAST) arg1;
					IAST rest = F.Plus();
					IExpr arg2 = ast.get(2);
					PatternMatcher matcher = new PatternMatcher(arg2);
					collectToMap(poly, matcher, map, rest);
					for (IExpr key : map.keySet()) {
						IAST value = map.get(key);
						if (value.size() == 2) {
							rest.add(F.Times(key, value.get(1)));
						} else {
							rest.add(F.Times(key, value));
						}
					}
					if (rest.size() == 2) {
						return rest.get(1);
					}
					return rest;
				}
				return arg1;
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void collectToMap(IExpr expr, PatternMatcher matcher, Map<IExpr, IAST> map, IAST rest) {
		if (expr.isFree(matcher, false)) {
			rest.add(expr);
			return;
		} else if (matcher.apply(expr)) {
			addPowerFactor(expr, F.C1, map);
			return;
		} else if (isPowerMatched(expr, matcher)) {
			addPowerFactor((IAST) expr, F.C1, map);
			return;
		} else if (expr.isPlus()) {
			IAST plusAST = (IAST) expr;
			IAST clone = plusAST.clone();
			int i = 1;
			while (i < clone.size()) {
				if (collectToMapPlus(clone.get(i), matcher, map)) {
					clone.remove(i);
				} else {
					i++;
				}
			}
			if (clone.size() > 1) {
				if (clone.size() == 2) {
					rest.add(clone.get(1));
				} else {
					rest.add(clone);
				}
			}
			return;
		} else if (expr.isTimes()) {
			IAST timesAST = (IAST) expr;
			for (int i = 1; i < timesAST.size(); i++) {
				if (matcher.apply(timesAST.get(i))) {
					IAST clone = timesAST.clone();
					clone.remove(i);
					if (clone.size() == 2) {
						addPowerFactor(timesAST.get(i), clone.get(1), map);
					} else {
						addPowerFactor(timesAST.get(i), clone, map);
					}
					return;
				} else if (isPowerMatched(timesAST.get(i), matcher)) {
					IAST clone = timesAST.clone();
					clone.remove(i);
					if (clone.size() == 2) {
						addPowerFactor(timesAST.get(i), clone.get(1), map);
					} else {
						addPowerFactor(timesAST.get(i), clone, map);
					}
					return;
				}
			}
			rest.add(expr);
			return;
		}
		rest.add(expr);
		return;
	}

	public boolean collectToMapPlus(IExpr expr, PatternMatcher matcher, Map<IExpr, IAST> map) {
		if (expr.isFree(matcher, false)) {
			return false;
		} else if (matcher.apply(expr)) {
			addPowerFactor(expr, F.C1, map);
			return true;
		} else if (isPowerMatched(expr, matcher)) {
			addPowerFactor(expr, F.C1, map);
			return true;
		} else if (expr.isTimes()) {
			IAST timesAST = (IAST) expr;
			for (int i = 1; i < timesAST.size(); i++) {
				if (matcher.apply(timesAST.get(i))) {
					IAST clone = timesAST.clone();
					clone.remove(i);
					if (clone.size() == 2) {
						addPowerFactor(timesAST.get(i), clone.get(1), map);
					} else {
						addPowerFactor(timesAST.get(i), clone, map);
					}
					return true;
				} else if (isPowerMatched(timesAST.get(i), matcher)) {
					IAST clone = timesAST.clone();
					clone.remove(i);
					if (clone.size() == 2) {
						addPowerFactor(timesAST.get(i), clone.get(1), map);
					} else {
						addPowerFactor(timesAST.get(i), clone, map);
					}
					return true;
				}
			}
		}

		return false;
	}

	public void addPowerFactor(IExpr key, IExpr value, Map<IExpr, IAST> map) {
		IAST resultList = map.get(key);
		if (resultList == null) {
			resultList = F.Plus();
			resultList.add(value);
			map.put(key, resultList);
		} else {
			resultList.add(value);
		}
	}

	public boolean isPowerMatched(IExpr poly, PatternMatcher matcher) {
		return poly.isPower() && ((IAST) poly).get(2).isNumber() && matcher.apply(((IAST) poly).get(1));
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
