/*
 * $Id$
 */

package edu.jas.application;


import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealAlgebraicRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorAlgebraic;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorQuotient;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufdroot.FactorRealAlgebraic;


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
 * @see edu.jas.ufd.FactorFactory#getImplementation(edu.jas.structure.RingFactory
 *      P)
 */

public class FactorFactory extends edu.jas.ufd.FactorFactory {


    private static final Logger logger = Logger.getLogger(FactorFactory.class);


    /**
     * Protected factory constructor.
     */
    protected FactorFactory() {
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
        return new FactorAlgebraic<C>(fac, FactorFactory.<C> getImplementation(fac.ring.coFac));
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
        return new FactorQuotient<C>(fac, FactorFactory.<C> getImplementation(fac.ring.coFac));
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
     * Determine suitable implementation of factorization algorithms, case
     * RealAlgebraicNumber&lt;C&gt;.
     * @param fac RealAlgebraicRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C> & Rational> FactorAbstract<RealAlgebraicNumber<C>> getImplementation(
                    RealAlgebraicRing<C> fac) {
        return new FactorRealAlgebraic<C>(fac,
                        FactorFactory.<AlgebraicNumber<C>> getImplementation(fac.algebraic));
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * RealAlgebraicNumber&lt;C&gt;.
     * @param fac RealAlgebraicRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("cast")
    public static <C extends GcdRingElem<C> & Rational> FactorAbstract<edu.jas.application.RealAlgebraicNumber<C>> getImplementation(
                    edu.jas.application.RealAlgebraicRing<C> fac) {
        edu.jas.root.RealAlgebraicRing<C> rar = (edu.jas.root.RealAlgebraicRing<C>) (Object) fac.realRing;
        return new FactorRealReal<C>(fac,
                        FactorFactory.<edu.jas.root.RealAlgebraicNumber<C>> getImplementation(rar));
    }


    /**
     * Determine suitable implementation of factorization algorithms, other
     * cases.
     * @param <C> coefficient type
     * @param fac RingFactory&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("cast")
    public static <C extends GcdRingElem<C>> FactorAbstract<C> getImplementation(RingFactory<C> fac) {
        logger.info("app factor factory = " + fac.getClass().getName());
        //System.out.println("fac_o = " + fac.getClass().getName());
        FactorAbstract/*raw type<C>*/ufd = null;
        edu.jas.application.RealAlgebraicRing rrfac = null;
        RealAlgebraicRing rfac = null;
        AlgebraicNumberRing afac = null;
        ComplexRing cfac = null;
        QuotientRing qfac = null;
        GenPolynomialRing pfac = null;
        Object ofac = fac;
        if (ofac instanceof edu.jas.application.RealAlgebraicRing) {
            //System.out.println("rrfac_o = " + ofac);
            rrfac = (edu.jas.application.RealAlgebraicRing) ofac;
            //ofac = rrfac.realRing;
            ufd = new FactorRealReal/*raw <C>*/(
                            rrfac,
                            FactorFactory.<edu.jas.root.RealAlgebraicNumber> getImplementation(rrfac.realRing));
        } else if (ofac instanceof edu.jas.root.RealAlgebraicRing) {
            //System.out.println("rfac_o = " + ofac);
            rfac = (edu.jas.root.RealAlgebraicRing) ofac;
            //ofac = rfac.algebraic;
            ufd = new FactorRealAlgebraic/*raw <C>*/(rfac,
                            FactorFactory.<AlgebraicNumber<C>> getImplementation(rfac.algebraic));
        } else if (ofac instanceof ComplexRing) {
            cfac = (ComplexRing<C>) ofac;
            afac = cfac.algebraicRing();
            ufd = new FactorComplex(cfac, FactorFactory.<C> getImplementation(afac));
        } else if (ofac instanceof AlgebraicNumberRing) {
            //System.out.println("afac_o = " + ofac);
            afac = (AlgebraicNumberRing) ofac;
            //ofac = afac.ring.coFac;
            ufd = new FactorAlgebraic/*raw <C>*/(afac, FactorFactory.<C> getImplementation(afac.ring.coFac));
        } else if (ofac instanceof QuotientRing) {
            //System.out.println("qfac_o = " + ofac);
            qfac = (QuotientRing) ofac;
            ufd = new FactorQuotient/*raw <C>*/(qfac, FactorFactory.<C> getImplementation(qfac.ring.coFac));
        } else if (ofac instanceof GenPolynomialRing) {
            //System.out.println("qfac_o = " + ofac);
            pfac = (GenPolynomialRing) ofac;
            ufd = getImplementation(pfac.coFac);
        } else {
            //System.out.println("no fac = " + fac.getClass().getName());
            ufd = edu.jas.ufd.FactorFactory.getImplementation(fac);
            //return (FactorAbstract<C>) ufd;
        }
        //logger.info("implementation = " + ufd);
        return (FactorAbstract<C>) ufd;
    }

}
