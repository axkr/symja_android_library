package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;

/**
 * Created by Duy on 2/20/2018.
 */
public abstract class ISignedNumberImpl extends INumberImpl implements ISignedNumber {
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ISignedNumber abs();

    @Override
    public abstract ISignedNumber opposite();

    /**
     * Returns (-1) * this
     *
     * @return
     */
    @Override
    public abstract ISignedNumber negate();

    /**
     * Divide <code>this</code> signed number by <code>that</code> signed number.
     *
     * @param that a signed number
     * @return
     */
    public abstract ISignedNumber divideBy(ISignedNumber that);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ISignedNumber inverse();

    /**
     * Test if <code>this</code> signed number is greater <code>than</code> that signed number..
     *
     * @return <code>this > that</code>
     */
    public abstract boolean isGreaterThan(ISignedNumber that);

    /**
     * Test if <code>this</code> signed number is less <code>than</code> that signed number..
     *
     * @return <code>this < that</code>
     */
    public abstract boolean isLessThan(ISignedNumber that);

    /**
     * {@inheritDoc}
     */
    @Override
    public IExpr complexArg() {
        if (sign() < 0) {
            return F.Pi;
        }
        return F.C0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INumber conjugate() {
        return this;
    }

    /**
     * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>lower</code> value. If this is a
     * <code>ISignedNUmber</code> expression return <code>this</code>.
     *
     * @return <code>F.NIL</code> if this expression is no interval and no signed number.
     */
    @Override
    public IExpr lower() {
        return this;
    }

    /**
     * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>upper</code> value. If this is a
     * <code>ISignedNUmber</code> expression return <code>this</code>.
     *
     * @return <code>F.NIL</code> if this expression is no interval and no signed number.
     */
    @Override
    public IExpr upper() {
        return this;
    }
}
