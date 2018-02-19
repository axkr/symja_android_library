/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.gb.OrderedMinPairlist;
import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.OrderedSyzPairlist;
import edu.jas.gb.PairList;
import edu.jas.gb.SGBProxy;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseParallel;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.gb.SolvableReductionSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPairFactory;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ValueFactory;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


// import edu.jas.application.SolvableResidueRing; // package cycle


/**
 * Solvable Groebner bases algorithms factory. Select appropriate Solvable
 * Groebner bases engine based on the coefficient types.
 *
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>SolvableGroebnerBase</code>
 * interface use the <code>SGBFactory</code>. It will select an
 * appropriate implementation based on the types of polynomial
 * coefficients C. The method to obtain an implementation is
 * <code>getImplementation()</code>. It returns an object of a class
 * which implements the <code>SolvableGroebnerBase</code> interface, more
 * precisely an object of abstract class
 * <code>SolvableGroebnerBaseAbstract</code>.
 * <p>
 * <pre>
 *
 * SolvableGroebnerBase&lt;CT&gt; engine;
 * engine = SGBFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.GB(A);
 * </pre>
 * <p>
 * For example, if the coefficient type is BigInteger, the usage looks
 * like
 * <p>
 * <pre>
 *
 * BigInteger cofac = new BigInteger();
 * SolvableGroebnerBase&lt;BigInteger&gt; engine;
 * engine = SGBFactory.getImplementation(cofac);
 * c = engine.GB(A);
 * </pre>
 * @see edu.jas.gb.GroebnerBase
 * @see edu.jas.gb.SolvableGroebnerBase
 * @see edu.jas.application.GBAlgorithmBuilder
 */

public class SGBFactory {


    private static final Logger logger = Logger.getLogger(SGBFactory.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Protected factory constructor.
     */
    protected SGBFactory() {
    }


    /**
     * Determine suitable implementation of GB algorithms, no factory case.
     *
     * @return GB algorithm implementation for field coefficients.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<C> getImplementation() {
        logger.warn("no coefficent factory given, assuming field coeffcients");
        SolvableGroebnerBaseAbstract<C> bba = new SolvableGroebnerBaseSeq<C>();
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModLong.
     *
     * @param fac ModLongRing.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<ModLong> getImplementation(ModLongRing fac) {
        return getImplementation(fac, new OrderedPairlist<ModLong>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModLong.
     *
     * @param fac ModLongRing.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<ModLong> getImplementation(ModLongRing fac,
                                                                          PairList<ModLong> pl) {
        SolvableGroebnerBaseAbstract<ModLong> bba;
        if (fac.isField()) {
            bba = new SolvableGroebnerBaseSeq<ModLong>(pl);
        } else {
            bba = new SolvableGroebnerBasePseudoSeq<ModLong>(fac, pl);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModInteger.
     *
     * @param fac ModIntegerRing.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        return getImplementation(fac, new OrderedPairlist<ModInteger>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModInteger.
     *
     * @param fac ModIntegerRing.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<ModInteger> getImplementation(ModIntegerRing fac,
                                                                             PairList<ModInteger> pl) {
        SolvableGroebnerBaseAbstract<ModInteger> bba;
        if (fac.isField()) {
            bba = new SolvableGroebnerBaseSeq<ModInteger>(pl);
        } else {
            bba = new SolvableGroebnerBasePseudoSeq<ModInteger>(fac, pl);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     *
     * @param fac BigInteger.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigInteger> getImplementation(BigInteger fac) {
        return getImplementation(fac, GBFactory.Algo.igb);
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     *
     * @param fac BigInteger.
     * @param a   algorithm, a = igb, egb, dgb.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigInteger> getImplementation(BigInteger fac, GBFactory.Algo a) {
        return getImplementation(fac, a, new OrderedPairlist<BigInteger>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     *
     * @param fac BigInteger.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigInteger> getImplementation(BigInteger fac,
                                                                             PairList<BigInteger> pl) {
        return getImplementation(fac, GBFactory.Algo.igb, pl);
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     *
     * @param fac BigInteger.
     * @param a   algorithm, a = igb, egb, dgb.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigInteger> getImplementation(BigInteger fac,
                                                                             GBFactory.Algo a, PairList<BigInteger> pl) {
        SolvableGroebnerBaseAbstract<BigInteger> bba;
        switch (a) {
            case igb:
                bba = new SolvableGroebnerBasePseudoSeq<BigInteger>(fac, pl);
                break;
            case egb:
                throw new UnsupportedOperationException("egb algorithm not available for BigInteger " + a);
            case dgb:
                throw new UnsupportedOperationException("dgb algorithm not available for BigInteger " + a);
            default:
                throw new IllegalArgumentException("algorithm not available for BigInteger " + a);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigRational.
     *
     * @param fac BigRational.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigRational> getImplementation(BigRational fac) {
        return getImplementation(fac, GBFactory.Algo.qgb);
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigRational.
     *
     * @param fac BigRational.
     * @param a   algorithm, a = qgb, ffgb.
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigRational> getImplementation(BigRational fac,
                                                                              GBFactory.Algo a) {
        return getImplementation(fac, a, new OrderedPairlist<BigRational>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigRational.
     *
     * @param fac BigRational.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigRational> getImplementation(BigRational fac,
                                                                              PairList<BigRational> pl) {
        return getImplementation(fac, GBFactory.Algo.qgb, pl);
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigRational.
     *
     * @param fac BigRational.
     * @param a   algorithm, a = qgb, ffgb.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static SolvableGroebnerBaseAbstract<BigRational> getImplementation(BigRational fac,
                                                                              GBFactory.Algo a, PairList<BigRational> pl) {
        SolvableGroebnerBaseAbstract<BigRational> bba;
        switch (a) {
            case qgb:
                bba = new SolvableGroebnerBaseSeq<BigRational>(pl);
                break;
            case ffgb:
                throw new UnsupportedOperationException("ffgb algorithm not available for BigRational " + a);
                //PairList<BigInteger> pli;
                //if (pl instanceof OrderedMinPairlist) {
                //    pli = new OrderedMinPairlist<BigInteger>();
                //} else if (pl instanceof OrderedSyzPairlist) {
                //    pli = new OrderedSyzPairlist<BigInteger>();
                //} else {
                //    pli = new OrderedPairlist<BigInteger>();
                //}
                //bba = new SolvableGroebnerBaseRational<BigRational>(pli); // pl not possible
                //break;
            default:
                throw new IllegalArgumentException("algorithm not available for " + fac.toScriptFactory()
                        + ", Algo = " + a);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case Quotient
     * coefficients.
     *
     * @param fac QuotientRing.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<Quotient<C>> getImplementation(
            QuotientRing<C> fac) {
        return getImplementation(fac, GBFactory.Algo.qgb);
    }


    /**
     * Determine suitable implementation of GB algorithms, case Quotient
     * coefficients.
     *
     * @param fac QuotientRing.
     * @param a   algorithm, a = qgb, ffgb.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<Quotient<C>> getImplementation(
            QuotientRing<C> fac, GBFactory.Algo a) {
        return getImplementation(fac, a, new OrderedPairlist<Quotient<C>>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case Quotient
     * coefficients.
     *
     * @param fac QuotientRing.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<Quotient<C>> getImplementation(
            QuotientRing<C> fac, PairList<Quotient<C>> pl) {
        return getImplementation(fac, GBFactory.Algo.qgb, pl);
    }


    /**
     * Determine suitable implementation of GB algorithms, case Quotient
     * coefficients.
     *
     * @param fac QuotientRing.
     * @param a   algorithm, a = qgb, ffgb.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<Quotient<C>> getImplementation(
            QuotientRing<C> fac, GBFactory.Algo a, PairList<Quotient<C>> pl) {
        SolvableGroebnerBaseAbstract<Quotient<C>> bba;
        if (logger.isInfoEnabled()) {
            logger.info("QuotientRing, fac = " + fac);
        }
        switch (a) {
            case qgb:
                bba = new SolvableGroebnerBaseSeq<Quotient<C>>(new SolvableReductionSeq<Quotient<C>>(), pl);
                break;
            case ffgb:
                throw new UnsupportedOperationException("ffgb algorithm not available for " + a);
                //PairList<GenPolynomial<C>> pli;
                //if (pl instanceof OrderedMinPairlist) {
                //    pli = new OrderedMinPairlist<GenPolynomial<C>>();
                //} else if (pl instanceof OrderedSyzPairlist) {
                //    pli = new OrderedSyzPairlist<GenPolynomial<C>>();
                //} else {
                //    pli = new OrderedPairlist<GenPolynomial<C>>();
                //}
                //bba = new SolvableGroebnerBaseQuotient<C>(fac, pli); // pl not possible
                //break;
            default:
                throw new IllegalArgumentException("algorithm not available for Quotient " + a);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case (recursive)
     * polynomial.
     *
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<GenPolynomial<C>> getImplementation(
            GenPolynomialRing<C> fac) {
        return getImplementation(fac, GBFactory.Algo.igb);
    }


    /**
     * Determine suitable implementation of GB algorithms, case (recursive)
     * polynomial.
     *
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param a   algorithm, a = igb or egb, dgb if fac is univariate over a
     *            field.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<GenPolynomial<C>> getImplementation(
            GenPolynomialRing<C> fac, GBFactory.Algo a) {
        return getImplementation(fac, a, new OrderedPairlist<GenPolynomial<C>>());
    }


    /**
     * Determine suitable implementation of GB algorithms, case (recursive)
     * polynomial.
     *
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<GenPolynomial<C>> getImplementation(
            GenPolynomialRing<C> fac, PairList<GenPolynomial<C>> pl) {
        return getImplementation(fac, GBFactory.Algo.igb, pl);
    }


    /**
     * Determine suitable implementation of GB algorithms, case (recursive)
     * polynomial.
     *
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @param a   algorithm, a = igb or egb, dgb if fac is univariate over a
     *            field.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> SolvableGroebnerBaseAbstract<GenPolynomial<C>> getImplementation(
            GenPolynomialRing<C> fac, GBFactory.Algo a, PairList<GenPolynomial<C>> pl) {
        SolvableGroebnerBaseAbstract<GenPolynomial<C>> bba;
        switch (a) {
            case igb:
                bba = new SolvableGroebnerBasePseudoRecSeq<C>(fac, pl);
                break;
            case egb:
                throw new UnsupportedOperationException("egb algorithm not available for " + a);
                //if (fac.nvar > 1 || !fac.coFac.isField()) {
                //    throw new IllegalArgumentException("coefficients not univariate or not over a field" + fac);
                //}
                //bba = new ESolvableGroebnerBaseSeq<GenPolynomial<C>>(); // pl not suitable
                //break;
            case dgb:
                throw new UnsupportedOperationException("dgb algorithm not available for " + a);
                //if (fac.nvar > 1 || !fac.coFac.isField()) {
                //    throw new IllegalArgumentException("coefficients not univariate or not over a field" + fac);
                //}
                //bba = new DSolvableGroebnerBaseSeq<GenPolynomial<C>>(); // pl not suitable
                //break;
            default:
                throw new IllegalArgumentException("algorithm not available for GenPolynomial<C> " + a);
        }
        return bba;
    }


    /*
     * Determine suitable implementation of GB algorithms, case regular rings.
     * @param fac RegularRing.
     * @return GB algorithm implementation.
    public static <C extends RingElem<C>> SolvableGroebnerBaseAbstract<Product<C>> getImplementation(
                    ProductRing<C> fac) {
        SolvableGroebnerBaseAbstract<Product<C>> bba;
        if (fac.onlyFields()) {
            bba = new RSolvableGroebnerBaseSeq<Product<C>>();
        } else {
            bba = new RSolvableGroebnerBasePseudoSeq<Product<C>>(fac);
        }
        return bba;
    }
     */


    /**
     * Determine suitable implementation of GB algorithms, other cases.
     *
     * @param fac RingFactory&lt;C&gt;.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> // interface RingElem not sufficient 
    SolvableGroebnerBaseAbstract<C> getImplementation(RingFactory<C> fac) {
        return getImplementation(fac, new OrderedPairlist<C>());
    }


    /**
     * Determine suitable implementation of GB algorithms, other cases.
     *
     * @param fac RingFactory&lt;C&gt;.
     * @param pl  pair selection strategy
     * @return GB algorithm implementation.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static <C extends GcdRingElem<C>> // interface RingElem not sufficient 
    SolvableGroebnerBaseAbstract<C> getImplementation(RingFactory<C> fac, PairList<C> pl) {
        if (debug) {
            logger.debug("fac = " + fac.getClass().getName()); // + ", fac = " + fac.toScript());
        }
        if (fac.isField()) {
            return new SolvableGroebnerBaseSeq<C>(pl);
        }
        if (fac instanceof ValueFactory) {
            return new SolvableGroebnerBasePseudoSeq<C>(fac, pl);
        }
        if (fac instanceof QuotPairFactory) {
            return new SolvableGroebnerBaseSeq<C>(pl);
        }
        SolvableGroebnerBaseAbstract bba = null;
        Object ofac = fac;
        if (ofac instanceof GenPolynomialRing) {
            PairList<GenPolynomial<C>> pli;
            if (pl instanceof OrderedMinPairlist) {
                pli = new OrderedMinPairlist<GenPolynomial<C>>();
            } else if (pl instanceof OrderedSyzPairlist) {
                pli = new OrderedSyzPairlist<GenPolynomial<C>>();
            } else {
                pli = new OrderedPairlist<GenPolynomial<C>>();
            }
            GenPolynomialRing<C> rofac = (GenPolynomialRing<C>) ofac;
            SolvableGroebnerBaseAbstract<GenPolynomial<C>> bbr = new SolvableGroebnerBasePseudoRecSeq<C>(
                    rofac, pli); // not pl
            bba = (SolvableGroebnerBaseAbstract) bbr;
            //} else if (ofac instanceof ProductRing) {
            //    ProductRing pfac = (ProductRing) ofac;
            //    if (pfac.onlyFields()) {
            //        bba = new RSolvableGroebnerBaseSeq<Product<C>>();
            //    } else {
            //        bba = new RSolvableGroebnerBasePseudoSeq<Product<C>>(pfac);
            //    }
        } else {
            bba = new SolvableGroebnerBasePseudoSeq<C>(fac, pl);
        }
        logger.info("bba = " + bba.getClass().getName());
        return bba;
    }


    /**
     * Determine suitable parallel/concurrent implementation of GB algorithms if
     * possible.
     *
     * @param fac RingFactory&lt;C&gt;.
     * @return GB proxy algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> // interface RingElem not sufficient 
    SolvableGroebnerBaseAbstract<C> getProxy(RingFactory<C> fac) {
        return getProxy(fac, new OrderedPairlist<C>());
    }


    /**
     * Determine suitable parallel/concurrent implementation of GB algorithms if
     * possible.
     *
     * @param fac RingFactory&lt;C&gt;.
     * @param pl  pair selection strategy
     * @return GB proxy algorithm implementation.
     */
    @SuppressWarnings({"unchecked"})
    public static <C extends GcdRingElem<C>> // interface RingElem not sufficient 
    SolvableGroebnerBaseAbstract<C> getProxy(RingFactory<C> fac, PairList<C> pl) {
        if (ComputerThreads.NO_THREADS) {
            return SGBFactory.<C>getImplementation(fac, pl);
        }
        if (debug) {
            logger.debug("proxy fac = " + fac.getClass().getName());
        }
        int th = (ComputerThreads.N_CPUS > 2 ? ComputerThreads.N_CPUS - 1 : 2);
        if (fac.isField()) {
            SolvableGroebnerBaseAbstract<C> e1 = new SolvableGroebnerBaseSeq<C>(pl);
            SolvableGroebnerBaseAbstract<C> e2 = new SolvableGroebnerBaseParallel<C>(th, pl);
            return new SGBProxy<C>(e1, e2);
        } else if (fac.characteristic().signum() == 0) {
            if (fac instanceof GenPolynomialRing) {
                GenPolynomialRing pfac = (GenPolynomialRing) fac;
                OrderedPairlist ppl = new OrderedPairlist<GenPolynomial<C>>();
                SolvableGroebnerBaseAbstract e1 = new SolvableGroebnerBasePseudoRecSeq<C>(pfac, ppl);
                logger.warn("no parallel version available, returning sequential version");
                return e1;
                //SolvableGroebnerBaseAbstract e2 = new SolvableGroebnerBasePseudoRecParallel<C>(th, pfac, ppl);
                //return new SGBProxy<C>(e1, e2);
            }
            SolvableGroebnerBaseAbstract<C> e1 = new SolvableGroebnerBasePseudoSeq<C>(fac, pl);
            logger.warn("no parallel version available, returning sequential version");
            return e1;
            //SolvableGroebnerBaseAbstract<C> e2 = new SolvableGroebnerBasePseudoParallel<C>(th, fac, pl);
            //return new SGBProxy<C>(e1, e2);
        }
        return getImplementation(fac, pl);
    }


    /**
     * Determine suitable parallel/concurrent implementation of GB algorithms if
     * possible.
     *
     * @param fac RingFactory&lt;C&gt;.
     * @return GB proxy algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> // interface RingElem not sufficient 
    SolvableGroebnerBaseAbstract<GenPolynomial<C>> getProxy(GenPolynomialRing<C> fac) {
        if (ComputerThreads.NO_THREADS) {
            //return SGBFactory.<GenPolynomial<C>> getImplementation(fac);
            return SGBFactory.getImplementation(fac);
        }
        if (debug) {
            logger.debug("fac = " + fac.getClass().getName());
        }
        //int th = (ComputerThreads.N_CPUS > 2 ? ComputerThreads.N_CPUS - 1 : 2);
        OrderedPairlist<GenPolynomial<C>> ppl = new OrderedPairlist<GenPolynomial<C>>();
        SolvableGroebnerBaseAbstract<GenPolynomial<C>> e1 = new SolvableGroebnerBasePseudoRecSeq<C>(fac, ppl);
        logger.warn("no parallel version available, returning sequential version");
        return e1;
        //SolvableGroebnerBaseAbstract<GenPolynomial<C>> e2 = new SolvableGroebnerBasePseudoRecParallel<C>(th, fac, ppl);
        //return new SGBProxy<GenPolynomial<C>>(e1, e2);
        //return new SGBProxy(e1, e2);
    }

}
