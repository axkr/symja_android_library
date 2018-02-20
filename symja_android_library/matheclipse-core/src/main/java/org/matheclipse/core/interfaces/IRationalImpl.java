package org.matheclipse.core.interfaces;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class IRationalImpl extends ISignedNumberImpl implements IRational {
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract IRational abs();

    public abstract IRational add(IRational parm1);

    public abstract IRational divideBy(IRational parm1);

    /**
     * Returns the numerator of this fraction.
     *
     * @return denominator
     */
    public abstract IInteger getNumerator();

    public abstract IRational multiply(IRational parm1);

    @Override
    public abstract IRational negate();

    /**
     * Returns this number raised at the specified exponent.
     *
     * @param exp the exponent.
     * @return <code>this<sup>exp</sup></code>
     * @throws ArithmeticException if {@code 0^0} is given.
     */
    public abstract IRational pow(final long exp) throws ArithmeticException;

    public abstract IRational subtract(IRational parm1);

}
