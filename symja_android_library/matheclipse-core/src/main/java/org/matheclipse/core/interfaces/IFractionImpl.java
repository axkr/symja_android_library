package org.matheclipse.core.interfaces;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class IFractionImpl extends IRationalImpl implements IFraction {

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract IFraction abs();


    @Override
    public abstract IFraction floorFraction();

    /**
     * Return the fractional part of this fraction
     *
     * @return
     */
    public abstract IFraction fractionalPart();

    public abstract IFraction add(IFraction parm1);

    public abstract IFraction div(IFraction other);

    /**
     * Returns a new rational representing the inverse of <code>this</code>.
     *
     * @return Inverse of <code>this</code>.
     */
    @Override
    public abstract IFraction inverse();

    public abstract IFraction mul(IFraction other);

    /**
     * Returns a new rational equal to <code>-this</code>.
     *
     * @return <code>-this</code>.
     */
    @Override
    public abstract IFraction negate();

    /**
     * Returns this number raised at the specified exponent.
     *
     * @param exp the exponent.
     * @return <code>this<sup>exp</sup></code>
     * @throws ArithmeticException if {@code 0^0} is given.
     */
    @Override
    public abstract IFraction pow(final long exp) throws ArithmeticException;

    public abstract IFraction sub(IFraction parm1);
}
