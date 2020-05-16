package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Interface for &quot;core functions&quot; which can specially evaluated like <code>Attributes, List, Part</code>
 * </p>
 * 
 */
public interface ISetEvaluator extends IFunctionEvaluator {
	
	default IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, final EvalEngine engine) {
		return F.NIL;
	}
}
