package org.matheclipse.core.builtin.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;

/**
 * <pre>Collect(expr, variable)
 * </pre>
 * <blockquote><p>collect subexpressions in <code>expr</code> which belong to the same <code>variable</code>.</p>
 * </blockquote>
 */
public class Collect extends AbstractCoreFunctionEvaluator {

	public Collect() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);
		try {
			IExpr head = null;
			if (ast.isAST3()) {
				head = engine.evaluate(ast.arg3());
			}
			final IExpr arg1 = F.expandAll(ast.arg1(), true, true);
			IAST temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (temp.isPresent()) {
				return temp;
			}
			final IExpr arg2 = engine.evalPattern(ast.arg2());
			if (!arg2.isList()) {
				return collectSingleVariable(arg1, arg2, null, 1, head, engine);
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

	/**
	 * Collect terms in <code>expr</code> containing the same power expressions
	 * of <code>x</code>.
	 * 
	 * @param expr
	 * @param x
	 *            the current variable from the list of variables which should
	 *            be collected
	 * @param listOfVariables
	 *            list of variables which should be collected or
	 *            <code>null</code> if no list is available
	 * @param listPosition
	 *            position of the next variable in the list after <code>x</code>
	 *            which should be collected recursively
	 * @param head
	 *            the head which should be applied to each coefficient or
	 *            <code>null</code> if no head should be applied
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	private IExpr collectSingleVariable(IExpr expr, IExpr x, final IAST listOfVariables, final int listPosition,
			IExpr head, EvalEngine engine) {
		if (expr.isAST()) {
			Map<IExpr, IAST> map = new HashMap<IExpr, IAST>();
			IAST poly = (IAST) expr;
			IAST rest = F.PlusAlloc(poly.size());

			IPatternMatcher matcher = new PatternMatcherEvalEngine(x, engine);
			collectToMap(poly, matcher, map, rest);
			if (listOfVariables != null && listPosition < listOfVariables.size()) {
				// collect next pattern in sub-expressions
				IAST result = F.PlusAlloc(map.size()+1);
				if (rest.size() > 1) {
					result.append(collectSingleVariable(rest, listOfVariables.get(listPosition), listOfVariables,
							listPosition + 1, head, engine));
				}
				for (Entry<IExpr, IAST> entry : map.entrySet()) {
					IExpr temp = collectSingleVariable(entry.getValue().getOneIdentity(F.C0),
							listOfVariables.get(listPosition), listOfVariables, listPosition + 1, head, engine);
					result.append(F.Times(entry.getKey(), temp));
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
						rest.append(F.Times(entry.getKey()).appendOneIdentity((IAST) coefficient));
					} else {
						rest.append(entry.getKey().times(coefficient));
					}
				}
			} else {
				IAST coefficient;
				for (IExpr key : map.keySet()) {
					coefficient = map.get(key);
					IAST times = F.TimesAlloc(2);
					times.append(key);
					times.appendOneIdentity(coefficient);
					rest.append(times);
				}
			}
			return rest.getOneIdentity(F.C0);
		}
		return expr;
	}

	public void collectToMap(IExpr expr, IPatternMatcher matcher, Map<IExpr, IAST> map, IAST rest) {
		if (expr.isFree(matcher, false)) {
			rest.append(expr);
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
				rest.appendOneIdentity(clone);
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
			rest.append(expr);
			return;
		}
		rest.append(expr);
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
		resultList.appendOneIdentity(subAST);
	}

	public void addPowerFactor(IExpr key, IExpr value, Map<IExpr, IAST> map) {
		IAST resultList = map.get(key);
		if (resultList == null) {
			resultList = F.Plus();
			map.put(key, resultList);
		}
		resultList.append(value);
	}

	public boolean isPowerMatched(IExpr poly, IPatternMatcher matcher) {
		return poly.isPower() && ((IAST) poly).arg2().isNumber() && matcher.test(((IAST) poly).arg1());
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
