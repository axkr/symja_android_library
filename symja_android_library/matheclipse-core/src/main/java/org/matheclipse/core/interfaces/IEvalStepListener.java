package org.matheclipse.core.interfaces;

import org.matheclipse.core.polynomials.ExprPolynomial;

/**
 * A listener which could listen to the <code>EvalEngine#evalLoop()</code>
 * steps, to implement an evaluation trace or a step by step evaluation.
 */
public interface IEvalStepListener {

    /**
     * Get the current &quot;evaluation step hint&quot; for the evaluation step
     * listener.
     *
     * @return the current hint or <code>null</code> if no hint was available.
     */
    String getHint();

    /**
     * Set an &quot;evaluation step hint&quot; for the evaluation step listener.
     * If defined, this hint could be used in the <code>add()</code> method
     * instead of the <code>add's</code> hint parameter.
     *
     * @param hint
     */
    void setHint(String hint);

    /**
     * Sets up the next evaluation step.
     *
     * @param expr           the inpput expression which should currently be evaluated
     * @param recursionDepth the current recursion depth of this evaluation step
     */
    void setUp(IExpr expr, int recursionDepth);

    /**
     * Tear down this evaluation step (called finally at the evaluation loop).
     *
     * @param recursionDepth the current recursion depth of this evaluation step
     */
    void tearDown(int recursionDepth);

    /**
     * Add a new step in which the <code>inputExpr</code> was evaluated to the
     * new <code>resultExpr</code>.
     *
     * @param inputExpr        the input expression
     * @param resultExpr       the evaluated result expression
     * @param recursionDepth   the current recursion depth
     * @param iterationCounter the current iteration counter
     * @param hint             this hint will be used in the listener, if no other hint was
     *                         set with the <code>setHint()</code> method.
     * @see IEvalStepListener#setHint(String)
     */
    void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter, String hint);

    /**
     * Solve a polynomial with degree &lt;= 2.
     *
     * @param polynomial the polynomial
     * @return <code>F.NIL</code> if no evaluation was possible, or if this
     * method isn't interesting for listening
     */
    IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial);
}