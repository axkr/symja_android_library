package org.matheclipse.core.interfaces;

/**
 * An abstract listener which could listen to the <code>EvalEngine#evalLoop()</code> steps, to implement an evaluation trace or a
 * step by step evaluation.
 */
public abstract class AbstractEvalStepListener extends IEvalStepListenerImpl implements IEvalStepListener {

    protected String fHint = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter, String hint);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHint() {
        return fHint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHint(String hint) {
        this.fHint = hint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(IExpr expr, int recursionDepth) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown(int recursionDepth) {

    }

}