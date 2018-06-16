package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates to the
 * <code>evaluate()</code>
 * 
 */
public class PredicateEvaluator extends AbstractCorePredicateEvaluator {
	Predicate<IExpr> predicate;

	public PredicateEvaluator(Predicate<IExpr> predicate) {
		this.predicate = predicate;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 2) {
			return predicate.test(engine.evaluate(ast.arg1())) ? F.True : F.False;
		}
		Validate.checkSize(ast, 2);
		return F.NIL;
	}

	@Override
	public boolean evalArg1Boole(IExpr arg1, EvalEngine engine) {
		return predicate.test(engine.evaluate(arg1));
	}

}