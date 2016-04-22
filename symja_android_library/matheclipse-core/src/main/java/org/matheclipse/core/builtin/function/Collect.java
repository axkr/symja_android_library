package org.matheclipse.core.builtin.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.reflection.system.Thread;

/**
 * <code>Collect(expr, variable)</code> - collect subexpressions in expr which
 * belong to the same variable.
 * 
 * <p>
 * See the online Symja function reference: <a href=
 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Collect">
 * Collect</a>
 * </p>
 */
public class Collect extends AbstractCoreFunctionEvaluator {

	public Collect() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);
		try {
			IExpr head = null;
			if (ast.size() == 4) {
				head = engine.evaluate(ast.arg3());
			}
			final IExpr arg1 = F.expandAll(ast.arg1(), true, true);
			IAST temp = Thread.threadLogicEquationOperators(arg1, ast, 1);
			if (temp.isPresent()) {
				return temp;
			}
			final IExpr arg2 = engine.evalPattern(ast.arg2());
			if (!arg2.isList()) {
				return collectSingleVariable(arg1, arg2, F.List(), 1, head, engine);
			}
			IAST list = (IAST) arg2;
			if (list.size() > 1) {
				return collectSingleVariable(arg1, list.arg1(), (IAST) arg2, 2, head, engine);
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}

	public IExpr collectSingleVariable(IExpr arg1, IExpr arg2, final IAST list, final int pos, IExpr head,
			EvalEngine engine) {
		if (arg1.isAST()) {
			Map<IExpr, IAST> map = new HashMap<IExpr, IAST>();
			IAST poly = (IAST) arg1;
			IAST rest = F.Plus();

			IPatternMatcher matcher = new PatternMatcher(arg2);
			collectToMap(poly, matcher, map, rest);
			if (pos < list.size()) {
				// collect next pattern in sub-expressions
				IAST result = F.Plus();
				if (rest.size() > 1) {
					result.add(collectSingleVariable(rest, list.get(pos), list, pos + 1, head, engine));
				}
				for (Entry<IExpr, IAST> entry : map.entrySet()) {
					IExpr temp = collectSingleVariable(entry.getValue().getOneIdentity(F.C0), list.get(pos), list,
							pos + 1, head, engine);
					result.add(F.Times(entry.getKey(), temp));
				}
				return result;
			}

			if (head != null) {
				IAST simplifyAST = F.unaryAST1(head, null);
				IExpr coefficient;
				for (int i = 1; i < rest.size(); i++) {
					simplifyAST.set(1, rest.get(i));
					coefficient = engine.evaluate(simplifyAST);
					rest.set(i, coefficient);
				}
				for (Map.Entry<IExpr, IAST> entry : map.entrySet()) {
					simplifyAST.set(1, entry.getValue());
					coefficient = engine.evaluate(simplifyAST);
					if (coefficient.isPlus()) {
						rest.add(F.Times(entry.getKey()).addOneIdentity((IAST) coefficient));
					} else {
						rest.add(entry.getKey().times(coefficient));
					}
				}
			} else {
				IAST coefficient;
				for (IExpr key : map.keySet()) {
					coefficient = map.get(key);
					rest.add(F.Times(key).addOneIdentity(coefficient));
				}
			}
			return rest.getOneIdentity(F.C0);
		}
		return arg1;
	}

	public void collectToMap(IExpr expr, IPatternMatcher matcher, Map<IExpr, IAST> map, IAST rest) {
		if (expr.isFree(matcher, false)) {
			rest.add(expr);
			return;
		} else if (matcher.test(expr)) {
			addPowerFactor(expr, F.C1, map);
			return;
		} else if (isPowerMatched(expr, matcher)) {
			addPowerFactor(expr, F.C1, map);
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
				rest.addOneIdentity(clone);
			}
			return;
		} else if (expr.isTimes()) {
			IAST timesAST = (IAST) expr;
			for (int i = 1; i < timesAST.size(); i++) {
				if (matcher.test(timesAST.get(i)) || isPowerMatched(timesAST.get(i), matcher)) {
					IAST clone = timesAST.clone();
					clone.remove(i);
					addOneIdentityPowerFactor(timesAST.get(i), clone, map);
					return;
				}
			}
			rest.add(expr);
			return;
		}
		rest.add(expr);
		return;
	}

	public boolean collectToMapPlus(IExpr expr, IPatternMatcher matcher, Map<IExpr, IAST> map) {
		if (expr.isFree(matcher, false)) {
			return false;
		} else if (matcher.test(expr)) {
			addPowerFactor(expr, F.C1, map);
			return true;
		} else if (isPowerMatched(expr, matcher)) {
			addPowerFactor(expr, F.C1, map);
			return true;
		} else if (expr.isTimes()) {
			IAST timesAST = (IAST) expr;
			for (int i = 1; i < timesAST.size(); i++) {
				if (matcher.test(timesAST.get(i)) || isPowerMatched(timesAST.get(i), matcher)) {
					IAST clone = timesAST.removeAtClone(i);
					addOneIdentityPowerFactor(timesAST.get(i), clone, map);
					return true;
				}
			}
		}

		return false;
	}

	public void addOneIdentityPowerFactor(IExpr key, IAST subAST, Map<IExpr, IAST> map) {
		IAST resultList = map.get(key);
		if (resultList == null) {
			resultList = F.Plus();
			map.put(key, resultList);
		}
		resultList.addOneIdentity(subAST);
	}

	public void addPowerFactor(IExpr key, IExpr value, Map<IExpr, IAST> map) {
		IAST resultList = map.get(key);
		if (resultList == null) {
			resultList = F.Plus();
			map.put(key, resultList);
		}
		resultList.add(value);
	}

	public boolean isPowerMatched(IExpr poly, IPatternMatcher matcher) {
		return poly.isPower() && ((IAST) poly).arg2().isNumber() && matcher.test(((IAST) poly).arg1());
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
