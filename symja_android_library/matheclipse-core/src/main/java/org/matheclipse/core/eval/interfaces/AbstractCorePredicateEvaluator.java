package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract interface for built-in Symja functions. The
 * <code>numericEval()</code> method delegates to the <code>evaluate()</code>
 * 
 */
public abstract class AbstractCorePredicateEvaluator extends AbstractCoreFunctionEvaluator {

	abstract public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine);

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 2) {
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isList()) {
				// thread over list?
				if ((ast.topHead().getAttributes() & ISymbol.LISTABLE) == ISymbol.LISTABLE) {
					return ((IAST) arg1).mapAt(F.IntegerQ(null), 1);
				}
			}
			return F.bool(evalArg1Boole(arg1, engine));
		}
		Validate.checkSize(ast, 2);
		return F.NIL;
	}

}