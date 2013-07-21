package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class NestWhileList implements ICoreFunctionEvaluator {

	public NestWhileList() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return nestList(ast.get(2), F.eval(ast.get(3)), Functors.append(F.ast(ast.get(1))), List());
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public static IAST nestList(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn, final IAST resultList) {
		IExpr temp = expr;
		Predicate<IExpr> predicate = Predicates.isTrue(test);

		while (predicate.apply(temp)) {
			resultList.add(temp);
			temp = F.eval(fn.apply(temp));
		}
		resultList.add(temp);
		return resultList;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
