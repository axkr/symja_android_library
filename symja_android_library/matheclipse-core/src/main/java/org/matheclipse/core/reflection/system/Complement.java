package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Complement extends AbstractFunctionEvaluator {

	public Complement() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (!ast.arg1().isAtom() && !ast.arg2().isAtom()) {

			final IAST arg1 = ((IAST) ast.arg1());
			final IAST arg2 = ((IAST) ast.arg2());
			return complement(arg1, arg2);
		}
		return null;
	}

	public static IExpr complement(final IAST arg1, final IAST arg2) {
		IAST result = F.List();
		Set<IExpr> set2 = arg2.asSet();
		Set<IExpr> set3 = new HashSet<IExpr>();
		for (int i = 1; i < arg1.size(); i++) {
			IExpr temp = arg1.get(i);
			if (!set2.contains(temp)) {
				set3.add(temp);
			}
		}
		for (IExpr expr : set3) {
			result.add(expr);
		}
		EvalAttributes.sort(result);
		return result;
	}
}
