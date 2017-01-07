package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AllTrue extends AbstractFunctionEvaluator {

	public AllTrue() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			IExpr head = ast.arg2();
			return allTrue(list, head, engine);
		}
		return F.NIL;
	}

	/**
	 * If all expressions evaluates to <code>true</code> for a given unary
	 * predicate function return <code>True</code>, if any expression evaluates
	 * to <code>false</code> return <code>False</code>, else return an
	 * <code>And(...)</code> expression of the result expressions.
	 * 
	 * @param list
	 *            list of expressions
	 * @param head
	 *            the head of a unary predicate function
	 * @param engine
	 * @return
	 */
	public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
		IAST logicalAnd = F.And();
		int size = list.size();
		for (int i = 1; i < size; i++) {
			IExpr temp = engine.evaluate(F.unary(head, list.get(i)));
			if (temp.isTrue()) {
				continue;
			} else if (temp.isFalse()) {
				return F.False;
			}
			logicalAnd.append(temp);
		}
		if (logicalAnd.size() > 1) {
			return logicalAnd;
		}
		return F.True;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
