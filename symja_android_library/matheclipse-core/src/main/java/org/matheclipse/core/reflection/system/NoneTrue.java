package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NoneTrue extends AbstractFunctionEvaluator {

	public NoneTrue() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			IExpr head = ast.arg2();
			return noneTrue(list, head, engine);
		}
		return F.NIL;
	}

	/**
	 * If any expression evaluates to <code>true</code> for a given unary
	 * predicate function return <code>False</code>, if all are
	 * <code>false</code> return <code>True</code>, else return an
	 * <code>Nor(...)</code> expression of the result expressions.
	 * 
	 * @param list
	 *            list of expressions
	 * @param head
	 *            the head of a unary predicate function
	 * @param engine
	 * @return
	 */
	public IExpr noneTrue(IAST list, IExpr head, EvalEngine engine) {
		IAST logicalNor = F.ast(F.Nor);
		int size = list.size();
		for (int i = 1; i < size; i++) {
			IExpr temp = engine.evaluate(F.unary(head, list.get(i)));
			if (temp.isTrue()) {
				return F.False;
			} else if (temp.isFalse()) {
				continue;
			}
			logicalNor.append(temp);
		}
		if (logicalNor.size() > 1) {
			return logicalNor;
		}
		return F.True;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
