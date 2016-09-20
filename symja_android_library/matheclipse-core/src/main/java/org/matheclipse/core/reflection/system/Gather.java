package org.matheclipse.core.reflection.system;

import java.util.Map;
import java.util.TreeMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Gather extends AbstractEvaluator {
	public Gather() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		int size = ast.size();
		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			java.util.Map<IExpr, IAST> map;
			if (size > 2) {
				IExpr arg2 = ast.arg2();
				map = new TreeMap<IExpr, IAST>(new Comparators.BinaryHeadComparator(arg2));
			} else {
				map = new TreeMap<IExpr, IAST>();
			}
			for (int i = 1; i < arg1.size(); i++) {
				IAST list = map.get(arg1.get(i));
				if (list == null) {
					list = F.List();
					list.add(arg1.get(i));
					map.put(arg1.get(i), list);
				} else {
					list.add(arg1.get(i));
				}
			}

			IAST result = F.List();
			for (Map.Entry<IExpr, IAST> entry : map.entrySet()) {
				result.add(entry.getValue());
			}
			return result;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
