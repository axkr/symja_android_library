package org.matheclipse.core.interfaces;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class INumberImpl extends IExprImpl implements INumber {
    @Override
    public abstract INumber conjugate();

    /**
     * Returns the imaginary part of a complex number
     *
     * @return real part
     */
    @Override
    public abstract ISignedNumber im();

    @Override
    public abstract INumber opposite();

    /**
     * Returns the real part of a complex number
     *
     * @return real part
     */
    @Override
    public abstract ISignedNumber re();

    /**
     * Get the absolute value for a given number
     *
     * @return
     * @deprecated use abs()
     */
    @Deprecated
    public IExpr eabs() {
        return abs();
    }
}
