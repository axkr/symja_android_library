package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 */
public abstract class AbstractSymbolEvaluator extends ISymbolEvaluatorImpl implements ISymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol) {
        return F.NIL;
    }

    @Override
    public IExpr numericEval(final ISymbol symbol) {
        return evaluate(symbol);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
        return numericEval(symbol);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
        // do nothing
    }
}
