package org.matheclipse.core.interfaces;

import com.duy.lambda.DoubleFunction;

import org.matheclipse.core.expression.F;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class ISymbolImpl extends IExprImpl implements ISymbol {

    public IExpr mapConstantDouble(DoubleFunction<IExpr> function) {
        return F.NIL;
    }
}
