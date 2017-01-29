package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * <code>Break()</code> leaves a <code>Do</code>, <code>For</code> or <code>While</code> loop.
 * </p>
 * <p>
 * See the online Symja function reference: <a
 * href="https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Break">Break</a>
 * </p>
 *
 */
public class Break extends AbstractCoreFunctionEvaluator {

	public Break() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			throw BreakException.CONST;
		}
		Validate.checkSize(ast, 1);

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
