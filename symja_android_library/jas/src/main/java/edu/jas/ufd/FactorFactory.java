/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Factorization algorithms factory. Select appropriate factorization engine
 * based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>Factorization</code>
 *        interface use the <code>FactorFactory</code>. It will select an
 *        appropriate implementation based on the types of polynomial
 *        coefficients C. To obtain an implementation use
 *        <code>getImplementation()</code>, it returns an object of a class
 *        which extends the <code>FactorAbstract</code> class which implements
 *        the <code>Factorization</code> interface.
 * 
 *        <pre>
 * Factorization&lt;CT&gt; engine;
 * engine = FactorFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.factors(a);
 * </pre>
 * 
 *        For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 *        <pre>
 * BigInteger cofac = new BigInteger();
 * Factorization&lt;BigInteger&gt; engine;
 * engine = FactorFactory.getImplementation(cofac);
 * Sm = engine.factors(poly);
 * </pre>
 * 
 * @see edu.jas.ufd.Factorization#factors(edu.jas.poly.GenPolynomial P)
 */

public class FactorFactory {


    private static final Logger logger = Logger.getLogger(FactorFactory.class);


    /**
     * Protected factory constructor.
     */
    protected FactorFactory() {
    }


    /**
     * Determine suitable implementation of factorization algorithm, case
     * ModInteger.
     * @param fac ModIntegerRing.
     * @return factorization algorithm implementation.
     */
    public static FactorAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        return new FactorModular<ModInteger>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithm, case
     * ModInteger.
     * @param fac ModIntegerRing.
     * @return factorization algorithm implementation.
     */
    public static FactorAbstract<ModLong> getImplementation(ModLongRing fac) {
        return new FactorModular<ModLong>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithm, case
     * BigInteger.
     * @param fac BigInteger.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unused")
    public static FactorAbstract<BigInteger> getImplementation(BigInteger fac) {
        return new FactorInteger<ModLong>();
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * BigRational.
     * @param fac BigRational.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unused")
    public static FactorAbstract<BigRational> getImplementation(BigRational fac) {
        return new FactorRational();
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * AlgebraicNumber&lt;C&gt;.
     * @param fac AlgebraicNumberRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> FactorAbstract<AlgebraicNumber<C>> getImplementation(
                    AlgebraicNumberRing<C> fac) {
        return new FactorAlgebraic<C>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * Complex&lt;C&gt;.
     * @param fac ComplexRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> FactorAbstract<Complex<C>> getImplementation(ComplexRing<C> fac) {
        return new FactorComplex<C>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * Quotient&lt;C&gt;.
     * @param fac QuotientRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> FactorAbstract<Quotient<C>> getImplementation(QuotientRing<C> fac) {
        return new FactorQuotient<C>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * recursive GenPolynomial&lt;C&gt;. Use <code>recursiveFactors()</code>.
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> FactorAbstract<C> getImplementation(GenPolynomialRing<C> fac) {
        return getImplementation(fac.coFac);
    }


    /**
     * Determine suitable implementation of factorization algorithms, other
     * cases.
     * @param <C> coefficient type
     * @param fac RingFactory&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> FactorAbstract<C> getImplementation(RingFactory<C> fac) {
        logger.info("factor factory = " + fac.getClass().getName());
        //System.out.println("fac_o_ufd = " + fac.getClass().getName());
        FactorAbstract/*raw type<C>*/ufd = null;
        AlgebraicNumberRing afac = null;
        ComplexRing cfac = null;
        QuotientRing qfac = null;
        GenPolynomialRing pfac = null;
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            ufd = new FactorInteger();
        } else if (ofac instanceof BigRational) {
            ufd = new FactorRational();
        } else if (ofac instanceof ModIntegerRing) {
            ufd = new FactorModular(fac);
        } else if (ofac instanceof ModLongRing) {
            ufd = new FactorModular(fac);
        } else if (ofac instanceof ComplexRing) {
            cfac = (ComplexRing<C>) ofac;
            ufd = new FactorComplex(cfac);
        } else if (ofac instanceof AlgebraicNumberRing) {
            //System.out.println("afac_o = " + ofac);
            afac = (AlgebraicNumberRing) ofac;
            //ofac = afac.ring.coFac;
            ufd = new FactorAlgebraic/*raw <C>*/(afac);
        } else if (ofac instanceof QuotientRing) {
            //System.out.println("qfac_o = " + ofac);
            qfac = (QuotientRing) ofac;
            ufd = new FactorQuotient/*raw <C>*/(qfac);
        } else if (ofac instanceof GenPolynomialRing) {
            //System.out.println("qfac_o = " + ofac);
            pfac = (GenPolynomialRing) ofac;
            ufd = getImplementation(pfac.coFac);
        } else {
            throw new IllegalArgumentException("no factorization implementation for "
                            + fac.getClass().getName());
        }
        //logger.info("implementation = " + ufd);
        return (FactorAbstract<C>) ufd;
    }

}
