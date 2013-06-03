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
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Squarefree factorization algorithms factory. Select appropriate squarefree
 * factorization engine based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>Squarefree</code> interface
 *        use the <code>SquarefreeFactory</code>. It will select an appropriate
 *        implementation based on the types of polynomial coefficients C. To
 *        obtain an implementation use <code>getImplementation()</code>, it
 *        returns an object of a class which extends the
 *        <code>SquarefreeAbstract</code> class which implements the
 *        <code>Squarefree</code> interface.
 * 
 *        <pre>
 * Squarefree&lt;CT&gt; engine;
 * engine = SquarefreeFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.squarefreeFactors(a);
 * </pre>
 * 
 *        For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 *        <pre>
 * BigInteger cofac = new BigInteger();
 * Squarefree&lt;BigInteger&gt; engine;
 * engine = SquarefreeFactory.getImplementation(cofac);
 * Sm = engine.sqaurefreeFactors(poly);
 * </pre>
 * 
 * @see edu.jas.ufd.Squarefree#squarefreeFactors(edu.jas.poly.GenPolynomial P)
 */

public class SquarefreeFactory {


    private static final Logger logger = Logger.getLogger(SquarefreeFactory.class);


    /**
     * Protected factory constructor.
     */
    protected SquarefreeFactory() {
    }


    /**
     * Determine suitable implementation of factorization algorithm, case
     * ModInteger.
     * @param fac ModIntegerRing.
     * @return squarefree factorization algorithm implementation.
     */
    public static SquarefreeAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        return new SquarefreeFiniteFieldCharP<ModInteger>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithm, case
     * ModLong.
     * @param fac ModLongRing.
     * @return squarefree factorization algorithm implementation.
     */
    public static SquarefreeAbstract<ModLong> getImplementation(ModLongRing fac) {
        return new SquarefreeFiniteFieldCharP<ModLong>(fac);
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithm,
     * case BigInteger.
     * @param fac BigInteger.
     * @return squarefree factorization algorithm implementation.
     */
    public static SquarefreeAbstract<BigInteger> getImplementation(BigInteger fac) {
        return new SquarefreeRingChar0<BigInteger>(fac);
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithms,
     * case BigRational.
     * @param fac BigRational.
     * @return squarefree factorization algorithm implementation.
     */
    public static SquarefreeAbstract<BigRational> getImplementation(BigRational fac) {
        return new SquarefreeFieldChar0<BigRational>(fac);
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithms,
     * case AlgebraicNumber&lt;C&gt;.
     * @param fac AlgebraicNumberRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return squarefree factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SquarefreeAbstract<AlgebraicNumber<C>> getImplementation(
                    AlgebraicNumberRing<C> fac) {
        PolyUfdUtil.<C> ensureFieldProperty(fac);
        if (fac.isField()) {
            if (fac.characteristic().signum() == 0) {
                return new SquarefreeFieldChar0<AlgebraicNumber<C>>(fac);
            }
            if (fac.isFinite()) {
                return new SquarefreeFiniteFieldCharP<AlgebraicNumber<C>>(fac);
            }
            return new SquarefreeInfiniteAlgebraicFieldCharP<C>(fac);
        }
        throw new ArithmeticException("eventually no integral domain " + fac.getClass().getName());
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithms,
     * case Quotient&lt;C&gt;.
     * @param fac QuotientRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return squarefree factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SquarefreeAbstract<Quotient<C>> getImplementation(
                    QuotientRing<C> fac) {
        if (fac.characteristic().signum() == 0) {
            return new SquarefreeFieldChar0<Quotient<C>>(fac);
        }
        return new SquarefreeInfiniteFieldCharP<C>(fac);
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithms,
     * case GenPolynomial&lt;C&gt;.
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return squarefree factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SquarefreeAbstract<C> getImplementation(GenPolynomialRing<C> fac) {
        return getImplementationPoly(fac);
    }


    /*
     * Determine suitable implementation of squarefree factorization algorithms,
     * case GenPolynomial&lt;C&gt;.
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return squarefree factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    protected static <C extends GcdRingElem<C>> SquarefreeAbstract<C> getImplementationPoly(
                    GenPolynomialRing<C> fac) {
        if (fac.characteristic().signum() == 0) {
            if (fac.coFac.isField()) {
                return new SquarefreeFieldChar0<C>(fac.coFac);
            }
            return new SquarefreeRingChar0<C>(fac.coFac);
        }
        if (fac.coFac.isFinite()) {
            return new SquarefreeFiniteFieldCharP<C>(fac.coFac);
        }
        Object ocfac = fac.coFac;
        SquarefreeAbstract saq = null;
        if (ocfac instanceof QuotientRing) {
            QuotientRing<C> qf = (QuotientRing<C>) ocfac;
            saq = new SquarefreeInfiniteFieldCharP<C>(qf);
        } else if (ocfac instanceof AlgebraicNumberRing) {
            AlgebraicNumberRing<C> af = (AlgebraicNumberRing<C>) ocfac;
            saq = new SquarefreeInfiniteAlgebraicFieldCharP<C>(af);
        }
        if (saq == null) {
            throw new IllegalArgumentException("no squarefree factorization " + fac.coFac);
        }
        SquarefreeAbstract<C> sa = (SquarefreeAbstract<C>) saq;
        return sa;
    }


    /**
     * Determine suitable implementation of squarefree factorization algorithms,
     * other cases.
     * @param <C> coefficient type
     * @param fac RingFactory&lt;C&gt;.
     * @return squarefree factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> SquarefreeAbstract<C> getImplementation(RingFactory<C> fac) {
        //logger.info("fac = " + fac.getClass().getName());
        //System.out.println("fac_o = " + fac.getClass().getName());
        SquarefreeAbstract/*raw type<C>*/ufd = null;
        AlgebraicNumberRing afac = null;
        QuotientRing qfac = null;
        GenPolynomialRing pfac = null;
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            ufd = new SquarefreeRingChar0<C>(fac);
        } else if (ofac instanceof BigRational) {
            ufd = new SquarefreeFieldChar0<C>(fac);
        } else if (ofac instanceof ModIntegerRing) {
            ufd = new SquarefreeFiniteFieldCharP<C>(fac);
        } else if (ofac instanceof ModLongRing) {
            ufd = new SquarefreeFiniteFieldCharP<C>(fac);
        } else if (ofac instanceof AlgebraicNumberRing) {
            afac = (AlgebraicNumberRing) ofac;
            //ofac = afac.ring.coFac;
            //System.out.println("o_afac = " + ofac);
            ufd = getImplementation(afac);
        } else if (ofac instanceof QuotientRing) {
            qfac = (QuotientRing) ofac;
            ufd = getImplementation(qfac);
        } else if (ofac instanceof GenPolynomialRing) {
            pfac = (GenPolynomialRing) ofac;
            ufd = getImplementationPoly(pfac);
        } else if (fac.isField()) {
            //System.out.println("fac_field = " + fac);
            if (fac.characteristic().signum() == 0) {
                ufd = new SquarefreeFieldChar0<C>(fac);
            } else {
                if (fac.isFinite()) {
                    ufd = new SquarefreeFiniteFieldCharP<C>(fac);
                } else {
                    ufd = new SquarefreeInfiniteFieldCharP/*raw*/(fac);
                }
            }
        } else if (fac.characteristic().signum() == 0) {
            ufd = new SquarefreeRingChar0<C>(fac);
        } else {
            throw new IllegalArgumentException("no squarefree factorization implementation for "
                            + fac.getClass().getName());
        }
        logger.debug("ufd = " + ufd);
        return (SquarefreeAbstract<C>) ufd;
    }

}
