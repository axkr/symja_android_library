package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.polynomials.ExprPolynomial;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class IEvalStepListenerImpl implements IEvalStepListener {
    /**
     * Solve a polynomial with degree &lt;= 2.
     *
     * @param polynomial the polynomial
     * @return <code>F.NIL</code> if no evaluation was possible, or if this
     * method isn't interesting for listening
     */
    public IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial) {
        return F.NIL;
    }
}
