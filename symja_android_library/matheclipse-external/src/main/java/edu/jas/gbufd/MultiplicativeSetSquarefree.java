/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Multiplicative set of squarefree and co-prime polynomials. a, b in M implies
 * a*b in M, 1 in M.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class MultiplicativeSetSquarefree<C extends GcdRingElem<C>> extends MultiplicativeSet<C> {


    private static final Logger logger = LogManager.getLogger(MultiplicativeSetSquarefree.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Squarefree decomposition engine.
     */
    protected final SquarefreeAbstract<C> engine;


    /**
     * MultiplicativeSet constructor. Constructs an empty multiplicative set.
     * @param ring polynomial ring factory for coefficients.
     */
    public MultiplicativeSetSquarefree(GenPolynomialRing<C> ring) {
        super(ring);
        engine = SquarefreeFactory.getImplementation(ring.coFac);
    }


    /**
     * MultiplicativeSet constructor.
     * @param ring polynomial ring factory for coefficients.
     * @param ms a list of non-zero polynomials.
     * @param eng squarefree factorization engine.
     */
    protected MultiplicativeSetSquarefree(GenPolynomialRing<C> ring, List<GenPolynomial<C>> ms,
                    SquarefreeAbstract<C> eng) {
        super(ring, ms);
        engine = eng;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MultiplicativeSetSquarefree" + mset;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof MultiplicativeSetSquarefree)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * Hash code for this multiplicative set.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Add polynomial to mset.
     * @param cc polynomial to be added to mset.
     * @return new multiplicative set.
     */
    @Override
    public MultiplicativeSetSquarefree<C> add(GenPolynomial<C> cc) {
        if (cc == null || cc.isZERO() || cc.isConstant()) {
            return this;
        }
        if (ring.coFac.isField()) {
            cc = cc.monic();
        }
        List<GenPolynomial<C>> list;
        if (mset.size() == 0) {
            list = engine.coPrimeSquarefree(cc, mset);
            if (ring.coFac.isField()) {
                list = PolyUtil.<C> monic(list);
            }
            return new MultiplicativeSetSquarefree<C>(ring, list, engine);
        }
        GenPolynomial<C> c = removeFactors(cc);
        if (c.isConstant()) {
            logger.info("skipped unit or constant = " + c);
            return this;
        }
        logger.info("added to squarefree mset = " + c);
        list = engine.coPrimeSquarefree(c, mset);
        if (ring.coFac.isField()) {
            list = PolyUtil.<C> monic(list);
        }
        return new MultiplicativeSetSquarefree<C>(ring, list, engine);
    }


    /**
     * Replace polynomial list of mset.
     * @param L polynomial list to replace mset.
     * @return new multiplicative set.
     */
    @Override
    public MultiplicativeSetSquarefree<C> replace(List<GenPolynomial<C>> L) {
        MultiplicativeSetSquarefree<C> ms = new MultiplicativeSetSquarefree<C>(ring);
        if (L == null || L.size() == 0) {
            return ms;
        }
        for (GenPolynomial<C> p : L) {
            ms = ms.add(p);
        }
        return ms;
    }

}
