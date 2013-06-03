/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.structure.GcdRingElem;


/**
 * Groebner bases via optimized variable and term order.
 * @author Heinz Kredel
 */

public class GBOptimized<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GBOptimized.class);


    private final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * GB engine.
     */
    public final GroebnerBaseAbstract<C> e1;


    /**
     * Indicator for return of permuted polynomials.
     */
    public final boolean retPermuted;


    /**
     * GBOptimized constructor.
     * @param e1 Groebner base engine.
     */
    public GBOptimized(GroebnerBaseAbstract<C> e1) {
        this(e1, false); // true ??
    }


    /**
     * GBOptimized constructor.
     * @param e1 Groebner base engine.
     * @param rP true for return of permuted polynomials, false for inverse
     *            permuted polynomials and new GB computation.
     */
    public GBOptimized(GroebnerBaseAbstract<C> e1, boolean rP) {
        this.e1 = e1;
        this.retPermuted = rP;
    }


    /**
     * Get the String representation with GB engine.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GBOptimized[ " + e1.toString() + " ]";
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        e1.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        int s = e1.cancel();
        return s;
    }


    /**
     * Groebner base.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    //JAVA6only: @Override
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        if (modv != 0) {
            throw new UnsupportedOperationException("implemented only for modv = 0, not " + modv);
        }
        GenPolynomialRing<C> pfac = F.get(0).ring;
        OptimizedPolynomialList<C> opt = TermOrderOptimization.<C> optimizeTermOrder(pfac, F);
        List<GenPolynomial<C>> P = opt.list;
        if (debug) {
            logger.info("optimized polynomials: " + P);
        }
        List<Integer> iperm = TermOrderOptimization.inversePermutation(opt.perm);
        logger.info("optimize perm: " + opt.perm + ", de-optimize perm: " + iperm);

        // compute GB with backing engine
        List<GenPolynomial<C>> G = e1.GB(modv, P);
        if (retPermuted || G.isEmpty()) {
            return G;
        }
        List<GenPolynomial<C>> iopt = TermOrderOptimization.<C> permutation(iperm, pfac, G);
        if (debug) {
            logger.info("de-optimized polynomials: " + iopt);
        }
        if (iopt.size() == 1) {
            return iopt;
        }
        logger.warn("recomputing GB");
        G = e1.GB(modv, iopt);
        return G;
    }

}
