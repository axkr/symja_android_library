/*
 * $Id$
 */

package edu.jas.application;


import org.apache.log4j.Logger;

import java.io.Serializable;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gb.GBOptimized;
import edu.jas.gb.GBProxy;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseArriSigSeqIter;
import edu.jas.gb.GroebnerBaseF5zSigSeqIter;
import edu.jas.gb.GroebnerBaseGGVSigSeqIter;
import edu.jas.gb.GroebnerBaseParIter;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gb.GroebnerBaseSeqIter;
import edu.jas.gb.OrderedMinPairlist;
import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.OrderedSyzPairlist;
import edu.jas.gb.PairList;
import edu.jas.gbufd.GBFactory;
import edu.jas.gbufd.GroebnerBaseFGLM;
import edu.jas.gbufd.GroebnerBasePseudoParallel;
import edu.jas.gbufd.GroebnerBaseQuotient;
import edu.jas.gbufd.GroebnerBaseRational;
import edu.jas.gbufd.GroebnerBaseWalk;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Builder for commutative Gr&ouml;bner bases algorithm implementations.
 *
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>GroebnerBase</code>
 * interface one can use the <code>GBFactory</code> or this
 * <code>GBAlgorithmBuilder</code>. This class will select and compose an
 * appropriate implementation based on the types of polynomial
 * coefficients C and the desired properties. To build an implementation
 * start with the static method <code>polynomialRing()</code> to define
 * the polynomial ring. Then continue to construct the algorithm with the
 * methods
 * <ul>
 * <li><code>optimize()</code> or <code>optimize(boolean)</code> for term
 * order (variable order) optimization (true for return of permuted
 * polynomials),</li>
 * <li><code>normalPairlist()</code> (default),
 * <code>syzygyPairlist()</code> or <code>simplePairlist()</code> for
 * pair-list selection strategies,</li>
 * <li><code>fractionFree()</code> for clearing denominators and
 * computing with pseudo reduction,</li>
 * <li><code>graded()</code> for using the FGLM algorithm to first
 * compute a Gr&ouml;bner base with respect to a graded term order and
 * then constructing a Gr&ouml;bner base wrt. a lexicographical term
 * order,</li>
 * <li><code>walk()</code> for using the Gr&ouml;bner walk algorithm to first
 * compute a Gr&ouml;bner base with respect to a graded term order and
 * then constructing a Gr&ouml;bner base wrt. a lexicographical term
 * order,</li>
 * <li><code>iterated()</code> for using the iterative GB algorithm to
 * compute a Gr&ouml;bner base adding one polynomial after another,</li>
 * <li><code>F5()</code>, <code>GGV()</code> and <code>Arri()</code> for using
 * the respective iterative signature based GB algorithm (over field coefficients) to
 * compute a Gr&ouml;bner base adding one polynomial after another,</li>
 * <li><code>parallel()</code> additionaly compute a Gr&ouml;bner base
 * over a field or integral domain in parallel,</li>
 * <li><code>euclideanDomain()</code> for computing a e-Gr&ouml;bner
 * base,</li>
 * <li><code>domainAlgorithm(Algo)</code> for computing a d- or
 * e-Gr&ouml;bner base,</li>
 * </ul>
 * Finally call the method <code>build()</code> to obtain an
 * implementaton of class <code>GroebnerBaseAbstract</code>. For example
 * <p>
 * <pre>
 *
 * GenPolynomialRing&lt;C&gt; pf = new GenPolynomialRing&lt;C&gt;(cofac, vars);
 * GroebnerBaseAbstract&lt;C&gt; engine;
 * engine = GBAlgorithmBuilder.&lt;C&gt; polynomialRing(pf).fractionFree().parallel().optimize().build();
 * c = engine.GB(A);
 * </pre>
 * <p>
 * For example, if the coefficient type is BigRational, the usage looks
 * like
 * <p>
 * <pre>
 *
 * GenPolynomialRing&lt;BigRational&gt; pf = new GenPolynomialRing&lt;BigRational&gt;(cofac, vars);
 * GroebnerBaseAbstract&lt;BigRational&gt; engine;
 * engine = GBAlgorithmBuilder.&lt;BigRational&gt; polynomialRing(pf).fractionFree().parallel().optimize().build();
 * c = engine.GB(A);
 * </pre>
 * <p>
 * <b>Note:</b> Not all combinations are meanigful
 * @see edu.jas.gb.GroebnerBase
 * @see GBFactory
 */

public class GBAlgorithmBuilder<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(GBAlgorithmBuilder.class);
    /**
     * The current polynomial ring.
     */
    public final GenPolynomialRing<C> ring;
    /**
     * Requested pairlist strategy.
     */
    public final PairList<C> strategy;
    /**
     * The current GB algorithm implementation.
     */
    private GroebnerBaseAbstract<C> algo;


    /**
     * Constructor not for use.
     */
    protected GBAlgorithmBuilder() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     *
     * @param ring the polynomial ring.
     */
    public GBAlgorithmBuilder(GenPolynomialRing<C> ring) {
        this(ring, null);
    }


    /**
     * Constructor.
     *
     * @param ring the polynomial ring.
     * @param algo already determined algorithm.
     */
    public GBAlgorithmBuilder(GenPolynomialRing<C> ring, GroebnerBaseAbstract<C> algo) {
        this(ring, algo, null);
    }


    /**
     * Constructor.
     *
     * @param ring     the polynomial ring.
     * @param algo     already determined algorithm.
     * @param strategy pairlist strategy.
     */
    public GBAlgorithmBuilder(GenPolynomialRing<C> ring, GroebnerBaseAbstract<C> algo, PairList<C> strategy) {
        if (ring == null) {
            throw new IllegalArgumentException("ring may not be null");
        }
        this.ring = ring;
        if (strategy == null) {
            strategy = new OrderedPairlist<C>();
        } else {
            if (algo == null) { // or overwrite?
                algo = GBFactory.<C>getImplementation(ring.coFac, strategy);
            }
        }
        this.algo = algo; // null accepted
        this.strategy = strategy;
    }

    /**
     * Define polynomial ring.
     *
     * @param fac the commutative polynomial ring.
     * @return GBAlgorithmBuilder object.
     */
    public static <C extends GcdRingElem<C>> GBAlgorithmBuilder<C> polynomialRing(GenPolynomialRing<C> fac) {
        return new GBAlgorithmBuilder<C>(fac);
    }

    /**
     * Build the GB algorithm implementaton.
     *
     * @return GB algorithm implementaton as GroebnerBaseAbstract object.
     */
    public GroebnerBaseAbstract<C> build() {
        if (algo == null) {
            if (strategy == null) { // should not happen
                algo = GBFactory.<C>getImplementation(ring.coFac);
            } else {
                algo = GBFactory.<C>getImplementation(ring.coFac, strategy);
            }
        }
        return algo;
    }

    /**
     * Select syzygy critical pair-list strategy. Gebauer and M&ouml;ller
     * algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> syzygyPairlist() {
        return new GBAlgorithmBuilder<C>(ring, algo, new OrderedSyzPairlist<C>());
    }


    /**
     * Select normal critical pair-list strategy. Buchberger, Winkler and Kredel
     * algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> normalPairlist() {
        return new GBAlgorithmBuilder<C>(ring, algo, new OrderedPairlist<C>());
    }


    /**
     * Select simple critical pair-list strategy. Original Buchberger algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> simplePairlist() {
        return new GBAlgorithmBuilder<C>(ring, algo, new OrderedMinPairlist<C>());
    }


    /**
     * Request term order optimization. Call optimize(true) for return of
     * permuted polynomials.
     *
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> optimize() {
        return optimize(true);
    }


    /**
     * Request term order optimization.
     *
     * @param rP true for return of permuted polynomials, false for inverse
     *           permuted polynomials and new GB computation.
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> optimize(boolean rP) {
        if (algo == null) {
            algo = GBFactory.<C>getImplementation(ring.coFac, strategy);
        }
        GroebnerBaseAbstract<C> bb = new GBOptimized<C>(algo, rP);
        return new GBAlgorithmBuilder<C>(ring, bb, strategy);
    }


    /**
     * Request fraction free algorithm. For BigRational and Quotient
     * coefficients denominators are cleared and pseudo reduction is used.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public GBAlgorithmBuilder<C> fractionFree() {
        if (algo != null) {
            logger.warn("selected algorithm ignored: " + algo + ", use fractionFree before");
        }
        if (((Object) ring.coFac) instanceof BigRational) {
            BigRational cf = (BigRational) (Object) ring.coFac;
            PairList<BigRational> sty = (PairList) strategy;
            GroebnerBaseAbstract<BigRational> bb = GBFactory.getImplementation(cf, GBFactory.Algo.ffgb, sty);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) (GroebnerBaseAbstract) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb, strategy);
        }
        if (((Object) ring.coFac) instanceof QuotientRing) {
            QuotientRing<C> cf = (QuotientRing<C>) (Object) ring.coFac;
            PairList<Quotient<C>> sty = (PairList) strategy;
            GroebnerBaseAbstract<Quotient<C>> bb = GBFactory.<C>getImplementation(cf, GBFactory.Algo.ffgb,
                    sty);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) (GroebnerBaseAbstract) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb, strategy);
        }
        logger.warn("no fraction free algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request e-GB algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> euclideanDomain() {
        return domainAlgorithm(GBFactory.Algo.egb);
    }


    /**
     * Request d-, e- or i-GB algorithm.
     *
     * @param a algorithm from GBFactory.Algo.
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public GBAlgorithmBuilder<C> domainAlgorithm(GBFactory.Algo a) {
        if (strategy != null) {
            logger.warn("strategie " + strategy + " ignored");
        }
        if (((Object) ring.coFac) instanceof BigInteger) {
            BigInteger cf = (BigInteger) (Object) ring.coFac;
            GroebnerBaseAbstract<BigInteger> bb = GBFactory.getImplementation(cf, a);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) (GroebnerBaseAbstract) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb);
        }
        if (((Object) ring.coFac) instanceof GenPolynomial) {
            GenPolynomialRing<C> cf = (GenPolynomialRing) (Object) ring.coFac;
            GroebnerBaseAbstract<GenPolynomial<C>> bb = GBFactory.<C>getImplementation(cf, a);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) (GroebnerBaseAbstract) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb);
        }
        logger.warn("no domain algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request parallel algorithm. Additionaly run a parallel algorithm via
     * GBProxy.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> parallel() {
        return parallel(ComputerThreads.N_CPUS);
    }


    /**
     * Request parallel algorithm. Additionaly run a parallel algorithm via
     * GBProxy.
     *
     * @param threads number of threads requested.
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public GBAlgorithmBuilder<C> parallel(int threads) {
        if (ComputerThreads.NO_THREADS) {
            logger.warn("parallel algorithms disabled");
            return this;
        }
        if (algo == null) {
            algo = GBFactory.<C>getImplementation(ring.coFac, strategy);
        }
        if (algo instanceof GroebnerBaseSeqIter) { // iterative requested
            GroebnerBaseAbstract<C> bb;
            bb = (GroebnerBaseAbstract) new GroebnerBaseParIter<C>(threads, strategy);
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb, strategy);
        } else if (((RingFactory) ring.coFac) instanceof BigRational) {
            GroebnerBaseAbstract<C> bb;
            if (algo instanceof GroebnerBaseRational) { // fraction free requested
                PairList<BigInteger> pli;
                if (strategy instanceof OrderedMinPairlist) {
                    pli = new OrderedMinPairlist<BigInteger>();
                } else if (strategy instanceof OrderedSyzPairlist) {
                    pli = new OrderedSyzPairlist<BigInteger>();
                } else {
                    pli = new OrderedPairlist<BigInteger>();
                }
                bb = (GroebnerBaseAbstract) new GroebnerBaseRational<BigRational>(threads, pli);
            } else {
                bb = (GroebnerBaseAbstract) new GroebnerBaseParallel<C>(threads, strategy);
            }
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb, strategy);
        } else if (((RingFactory) ring.coFac) instanceof QuotientRing) {
            GroebnerBaseAbstract<C> bb;
            if (algo instanceof GroebnerBaseQuotient) { // fraction free requested
                PairList<GenPolynomial<C>> pli;
                if (strategy instanceof OrderedMinPairlist) {
                    pli = new OrderedMinPairlist<GenPolynomial<C>>();
                } else if (strategy instanceof OrderedSyzPairlist) {
                    pli = new OrderedSyzPairlist<GenPolynomial<C>>();
                } else {
                    pli = new OrderedPairlist<GenPolynomial<C>>();
                }
                QuotientRing<C> fac = (QuotientRing) ring.coFac;
                bb = (GroebnerBaseAbstract) new GroebnerBaseQuotient<C>(threads, fac, pli); // pl not possible
            } else {
                bb = (GroebnerBaseAbstract) new GroebnerBaseParallel<C>(threads, strategy);
            }
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb);
        } else if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb = new GroebnerBaseParallel<C>(threads, strategy);
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb, strategy);
        } else if (ring.coFac.getONE() instanceof GcdRingElem) {
            GroebnerBaseAbstract<C> bb = new GroebnerBasePseudoParallel<C>(threads, ring.coFac, strategy);
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb, strategy);
        }
        logger.warn("no parallel algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request FGLM algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> graded() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            if (algo == null) {
                bb = new GroebnerBaseFGLM<C>();
            } else {
                bb = new GroebnerBaseFGLM<C>(algo);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no FGLM algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request Groebner walk algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> walk() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            if (algo == null) {
                bb = new GroebnerBaseWalk<C>();
            } else {
                bb = new GroebnerBaseWalk<C>(algo);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no Groebner walk algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request iterated GB algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> iterated() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            bb = new GroebnerBaseSeqIter<C>(strategy);
            // if (algo instanceof GBProxy) ... assemble parallel todo
            if (algo != null) {
                logger.warn("algo " + algo + " ignored for " + bb);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no iterated GB algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request iterated F5 signature based GB algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> F5() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            bb = new GroebnerBaseF5zSigSeqIter<C>();
            // if (algo instanceof GBProxy) ... assemble parallel todo
            if (algo != null) {
                logger.warn("algo " + algo + " ignored for " + bb);
            }
            if (strategy != null) {
                logger.warn("strategy " + strategy + " ignored for " + bb);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no iterated F5 GB algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request iterated GGV signature based GB algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> GGV() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            bb = new GroebnerBaseGGVSigSeqIter<C>();
            // if (algo instanceof GBProxy) ... assemble parallel todo
            if (algo != null) {
                logger.warn("algo " + algo + " ignored for " + bb);
            }
            if (strategy != null) {
                logger.warn("strategy " + strategy + " ignored for " + bb);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no iterated GGV GB algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request iterated Arri signature based GB algorithm.
     *
     * @return GBAlgorithmBuilder object.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> Arri() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            bb = new GroebnerBaseArriSigSeqIter<C>();
            // if (algo instanceof GBProxy) ... assemble parallel todo
            if (algo != null) {
                logger.warn("algo " + algo + " ignored for " + bb);
            }
            if (strategy != null) {
                logger.warn("strategy " + strategy + " ignored for " + bb);
            }
            return new GBAlgorithmBuilder<C>(ring, bb, strategy);
        }
        logger.warn("no iterated Arri GB algorithm implemented for " + ring);
        return this;
    }


    /**
     * String representation of the GB algorithm implementation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(" ");
        if (algo != null) {
            s.append(algo.toString());
            s.append(" for ");
        }
        s.append(ring.toString());
        if (strategy != null) {
            s.append(" strategy=");
            s.append(strategy.toString());
        }
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer(" ");
        if (algo != null) {
            s.append(algo.toString()); // nonsense
            s.append(" ");
        }
        s.append(ring.toScript());
        if (strategy != null) {
            s.append(",strategy=");
            s.append(strategy.toString());
        }
        return s.toString();
    }

}
