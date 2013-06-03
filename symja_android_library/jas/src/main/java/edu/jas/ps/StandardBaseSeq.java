/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.poly.ExpVector;


/**
 * Standard Base sequential algorithm. Implements Standard bases and GB test.
 * <b>Note: </b> Currently the term order is fixed to the order defined by the
 * iterator over exponent vectors <code>ExpVectorIterator</code>.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class StandardBaseSeq<C extends RingElem<C>>
    /*todo: extends StandardBaseAbstract<C>*/{


    private static final Logger logger = Logger.getLogger(StandardBaseSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    public final ReductionSeq<C> red;


    /**
     * Constructor.
     */
    public StandardBaseSeq() {
        //super();
        this(new ReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public StandardBaseSeq(ReductionSeq<C> red) {
        this.red = red; //super(red);
    }


    /**
     * Standard base test.
     * @param F power series list.
     * @return true, if F is a Standard base, else false.
     */
    public boolean isSTD(List<MultiVarPowerSeries<C>> F) {
        return isSTD(0, F);
    }


    /**
     * Standard base test.
     * @param modv module variable number.
     * @param F power series list.
     * @return true, if F is a Standard base, else false.
     */
    public boolean isSTD(int modv, List<MultiVarPowerSeries<C>> F) {
        if (F == null) {
            return true;
        }
        MultiVarPowerSeries<C> pi, pj, s, h;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                //                 if ( ! red.criterion4( pi, pj ) ) { 
                //                    continue;
                //                 }
                s = red.SPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                h = red.normalform(F, s);
                if (!h.isZERO()) {
                    System.out.println("pi = " + pi + ", pj = " + pj);
                    System.out.println("s  = " + s + ", h = " + h);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Standard base using pairlist class.
     * @param F power series list.
     * @return STD(F) a Standard base of F.
     */
    public List<MultiVarPowerSeries<C>> STD(List<MultiVarPowerSeries<C>> F) {
        return STD(0, F);
    }


    /**
     * Standard base using pairlist class.
     * @param modv module variable number.
     * @param F power series list.
     * @return STD(F) a Standard base of F.
     */
    public List<MultiVarPowerSeries<C>> STD(int modv, List<MultiVarPowerSeries<C>> F) {
        MultiVarPowerSeries<C> p = null;
        List<MultiVarPowerSeries<C>> G = new ArrayList<MultiVarPowerSeries<C>>();
        OrderedPairlist<C> pairlist = null;
        int l = F.size();
        ListIterator<MultiVarPowerSeries<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (!p.isZERO()) {
                //p = p.monic();
                if (p.isUnit()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads are activated
                }
                G.add(p);
                if (pairlist == null) {
                    pairlist = new OrderedPairlist<C>(modv, p.ring);
                    if (!p.ring.coFac.isField()) {
                        throw new IllegalArgumentException("coefficients not from a field");
                    }
                }
                // putOne not required
                pairlist.put(p);
            } else {
                l--;
            }
        }
        if (l <= 1) {
            return G; // since no threads are activated
        }

        Pair<C> pair;
        MultiVarPowerSeries<C> pi;
        MultiVarPowerSeries<C> pj;
        MultiVarPowerSeries<C> S;
        MultiVarPowerSeries<C> H;
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
            //S.setTruncate(p.ring.truncate()); // ??
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (logger.isInfoEnabled()) {
                ExpVector es = S.orderExpVector();
                logger.info("ht(S) = " + es.toString(S.ring.vars) + ", " + es); // + ", S = " + S);
            }

            //long t = System.currentTimeMillis();
            H = red.normalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            //t = System.currentTimeMillis() - t;
            //System.out.println("time = " + t);
            if (logger.isInfoEnabled()) {
                ExpVector eh = H.orderExpVector();
                logger.info("ht(H) = " + eh.toString(S.ring.vars) + ", " + eh); // + ", coeff(HT(H)) = " + H.coefficient(eh));
            }

            //H = H.monic();
            if (H.isUnit()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (logger.isDebugEnabled()) {
                logger.info("H = " + H);
            }
            //if (!H.isZERO()) {
                l++;
                G.add(H);
                pairlist.put(H);
            //}
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalSTD(G);
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered Standard basis.
     * @param Gp a Standard base.
     * @return a minimal Standard base of Gp, not auto reduced.
     */
    public List<MultiVarPowerSeries<C>> minimalSTD(List<MultiVarPowerSeries<C>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero power series
        List<MultiVarPowerSeries<C>> G = new ArrayList<MultiVarPowerSeries<C>>(Gp.size());
        for (MultiVarPowerSeries<C> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // make positive a = a.abs(); ?
                a = a.monic();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible power series
        MultiVarPowerSeries<C> a;
        List<MultiVarPowerSeries<C>> F = new ArrayList<MultiVarPowerSeries<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop power series 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<MultiVarPowerSeries<C>> ff = new ArrayList<MultiVarPowerSeries<C>>(G);
                    ff.addAll(F);
                    a = red.normalform(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("error, nf(a) " + a);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        // power series not reduced
        return G;
    }

}
