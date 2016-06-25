package org.matheclipse.core.reflection.system;

import java.util.LinkedHashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Tally extends AbstractEvaluator {

	public Tally() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IAST list = Validate.checkListType(ast, 1);

		int size = ast.size();
		java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
		
		if (size == 2) {
			for (int i = 1; i < list.size(); i++) {
				Integer value = map.get(list.get(i));
				if (value == null) {
					map.put(list.get(i), Integer.valueOf(1));
				} else {
					map.put(list.get(i), Integer.valueOf(value + 1));
				}
			}
		}

		if (size == 3) {
			IAST test = F.binary(ast.arg2(), null, null);
			boolean evaledTrue;
			for (int i = 1; i < list.size(); i++) {
				evaledTrue = false;
				for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
					test.set(1, entry.getKey());
					test.set(2, list.get(i));
					if (engine.evalTrue(test)) {
						evaledTrue = true;
						map.put(entry.getKey(), Integer.valueOf(entry.getValue() + 1));
						break;
					}
				}
				if (!evaledTrue) {
					map.put(list.get(i), Integer.valueOf(1));
				}
			}

		}
		
		IAST result = F.List();
		for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
			result.add(F.List(entry.getKey(), F.integer(entry.getValue())));
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
