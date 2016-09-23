package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Catenate extends AbstractEvaluator {

	public Catenate() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isList()) {
			IAST list = (IAST) ast.arg1();
			int size = 1;
			for (int i = 1; i < list.size(); i++) {
				if (!list.get(i).isList()) {
					return F.NIL;
				}
				size += list.size() - 1;
			}
			IAST resultList = F.ast(F.List, size, false);
			for (int i = 1; i < list.size(); i++) {
				resultList.addAll(((IAST) list.get(i)).args());
			}
			return resultList;
		}
		return F.NIL;
	}

}
