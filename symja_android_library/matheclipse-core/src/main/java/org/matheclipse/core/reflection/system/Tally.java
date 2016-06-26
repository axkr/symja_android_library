package org.matheclipse.core.reflection.system;

import java.util.LinkedHashMap;
import java.util.function.BiPredicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Tally extends AbstractEvaluator {

	public Tally() {
		// default ctor
	}

	private static IAST createResultList(java.util.Map<IExpr, Integer> map) {
		IAST result = F.List();
		for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
			result.add(F.List(entry.getKey(), F.integer(entry.getValue())));
		}
		return result;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IAST list = Validate.checkListType(ast, 1);

		int size = ast.size();

		if (size == 2) {
			return tally1Arg(list);
		} else if (size == 3) {
			BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(ast.arg2());
			return tally2Args(list, biPredicate);
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

	public static IAST tally1Arg(IAST list) {
		java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
		for (int i = 1; i < list.size(); i++) {
			Integer value = map.get(list.get(i));
			if (value == null) {
				map.put(list.get(i), Integer.valueOf(1));
			} else {
				map.put(list.get(i), Integer.valueOf(value + 1));
			}
		}
		return createResultList(map);
	}

	private static IAST tally2Args(IAST list, BiPredicate<IExpr, IExpr> biPredicate) {
		java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
		boolean evaledTrue;
		for (int i = 1; i < list.size(); i++) {
			evaledTrue = false;
			for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
				if (biPredicate.test(entry.getKey(), list.get(i))) {
					evaledTrue = true;
					map.put(entry.getKey(), Integer.valueOf(entry.getValue() + 1));
					break;
				}
			}
			if (!evaledTrue) {
				map.put(list.get(i), Integer.valueOf(1));
			}
		}
		return createResultList(map);
	}

}
