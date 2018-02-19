/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;

// import java.util.Collections;


/**
 * Groebner Base sequential iterative algorithm. Implements Groebner bases and
 * GB test.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBaseSeqIter<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseSeqIter.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public GroebnerBaseSeqIter() {
        super();
    }


    /**
     * Constructor.
     *
     * @param red Reduction engine
     */
    public GroebnerBaseSeqIter(Reduction<C> red) {
        super(red);
    }


    /**
     * Constructor.
     *
     * @param pl pair selection strategy
     */
    public GroebnerBaseSeqIter(PairList<C> pl) {
        super(pl);
    }


    /**
     * Constructor.
     *
     * @param red Reduction engine
     * @param pl  pair selection strategy
     */
    public GroebnerBaseSeqIter(Reduction<C> red, PairList<C> pl) {
        super(red, pl);
    }


    /**
     * Groebner base using pairlist class, iterative algorithm.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C>monic(G);
        if (G.size() <= 1) {
            return G;
        }
        // sort, no reverse
        G = OrderedPolynomialList.<C>sort(G);
        //no: Collections.reverse(G);
        logger.info("G-sort = " + G);
        List<GenPolynomial<C>> Gp = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> p : G) {
            if (debug) {
                logger.info("p = " + p);
            }
            GenPolynomial<C> pp = red.normalform(Gp, p);
            if (pp.isZERO()) {
                continue;
            }
            Gp = GB(modv, Gp, pp);
            //System.out.println("GB(Gp+p) = " + Gp);
            if (Gp.size() > 0) {
                if (Gp.get(0).isONE()) {
                    return Gp;
                }
            }
        }
        return Gp;
    }


    /**
     * Groebner base using pairlist class.
     *
     * @param modv module variable number.
     * @param G    polynomial list of a Groebner base.
     * @param f    polynomial.
     * @return GB(G, f) a Groebner base of G+(f).
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> G, GenPolynomial<C> f) {
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(G);
        GenPolynomial<C> g = f.monic();
        if (F.isEmpty()) {
            F.add(g);
            return F;
        }
        if (g.isZERO()) {
            return F;
        }
        if (g.isONE()) {
            F.clear();
            F.add(g);
            return F;
        }
        GenPolynomialRing<C> ring = F.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        G = F;
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.setList(G);
        G.add(g);
        pairlist.put(g);
        logger.info("start " + pairlist);

        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //logger.debug("pair = " + pair);
            if (pair == null) {
                continue;
            }
            pi = pair.pi;
            pj = pair.pj;
            if ( /*false &&*/debug) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }

            S = red.SPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = red.normalform(G, S);
            if (debug) {
                //logger.info("pair = " + pair); 
                //logger.info("ht(S) = " + S.monic()); //.leadingExpVector() );
                logger.info("ht(H) = " + H.monic()); //.leadingExpVector() );
            }
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            H = H.monic();
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }

            H = H.monic();
            if (H.isONE()) {
                G.clear();
                G.add(H);
                pairlist.putOne();
                logger.info("end " + pairlist);
                return G; // since no threads are activated
            }
            if (debug) {
                logger.info("H = " + H);
            }
            if (H.length() > 0) {
                //l++;
                G.add(H);
                pairlist.put(H);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("end " + pairlist);
        return G;
    }

}
