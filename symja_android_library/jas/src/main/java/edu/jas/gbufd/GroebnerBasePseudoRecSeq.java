/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.Pair;
import edu.jas.gb.PairList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;


/**
 * Groebner Base with pseudo reduction sequential algorithm for integral
 * function coefficients. Implements polynomial fraction free coefficients
 * Groebner bases. Coefficients can for example be 
 * (commutative) multivariate polynomials.
 * @param <C> base coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBasePseudoRecSeq<C extends GcdRingElem<C>> extends
                GroebnerBaseAbstract<GenPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(GroebnerBasePseudoRecSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<C> redRec;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<GenPolynomial<C>> red;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<GenPolynomial<C>> cofac;


    /**
     * Base coefficient ring factory.
     */
    protected final RingFactory<C> baseCofac;


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf) {
        this(new PseudoReductionSeq<GenPolynomial<C>>(), rf, new OrderedPairlist<GenPolynomial<C>>(
                        new GenPolynomialRing<GenPolynomial<C>>(rf, 1))); // 1=hack
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf, PairList<GenPolynomial<C>> pl) {
        this(new PseudoReductionSeq<GenPolynomial<C>>(), rf, pl);
    }


    /**
     * Constructor.
     * @param red pseudo reduction engine. <b>Note:</b> red must be an instance
     *            of PseudoReductionSeq.
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    @SuppressWarnings("cast")
    public GroebnerBasePseudoRecSeq(PseudoReduction<GenPolynomial<C>> red, RingFactory<GenPolynomial<C>> rf,
                    PairList<GenPolynomial<C>> pl) {
        super(red, pl);
        this.red = red;
        this.redRec = (PseudoReduction<C>) (PseudoReduction) red;
        cofac = rf;
        GenPolynomialRing<C> rp = (GenPolynomialRing<C>) cofac;
        baseCofac = rp.coFac;
        //engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( baseCofac );
        //not used: 
        engine = GCDFactory.<C> getProxy(baseCofac);
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    //@Override
    public List<GenPolynomial<GenPolynomial<C>>> GB(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        List<GenPolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(F);
        G = engine.recursivePrimitivePart(G);
        if (G.size() <= 1) {
            return G;
        }
        GenPolynomialRing<GenPolynomial<C>> ring = G.get(0).ring;
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<GenPolynomial<C>> pairlist = strategy.create(modv, ring);
        pairlist.put(G);

        /*
        GenPolynomial<GenPolynomial<C>> p;
        List<GenPolynomial<GenPolynomial<C>>> G = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        PairList<GenPolynomial<C>> pairlist = null;
        int l = F.size();
        ListIterator<GenPolynomial<GenPolynomial<C>>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = engine.recursivePrimitivePart(p); //p.monic();
                p = p.abs();
                if (p.isConstant()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads are activated
                }
                G.add(p);
                if (pairlist == null) {
                    //pairlist = new OrderedPairlist<GenPolynomial<C>>(modv, p.ring);
                    pairlist = strategy.create(modv, p.ring);
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
        */

        Pair<GenPolynomial<C>> pair;
        GenPolynomial<GenPolynomial<C>> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null)
                continue;

            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }

            S = red.SPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(S) = " + S.leadingExpVector());
            }

            H = redRec.normalformRecursive(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }
            H = engine.recursivePrimitivePart(H);
            H = H.abs();
            if (H.isConstant()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (debug) {
                logger.debug("H = " + H);
            }
            if (H.length() > 0) {
                //l++;
                G.add(H);
                pairlist.put(H);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    @Override
    public List<GenPolynomial<GenPolynomial<C>>> minimalGB(List<GenPolynomial<GenPolynomial<C>>> Gp) {
        List<GenPolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(Gp);
        /*
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<GenPolynomial<C>>> G = new ArrayList<GenPolynomial<GenPolynomial<C>>>(Gp.size());
        for (GenPolynomial<GenPolynomial<C>> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        */
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenPolynomial<GenPolynomial<C>> a;
        List<GenPolynomial<GenPolynomial<C>>> F;
        F = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenPolynomial<GenPolynomial<C>>> ff;
                    ff = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G);
                    ff.addAll(F);
                    a = redRec.normalformRecursive(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("error, nf(a) " + a);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }
        Collections.reverse(G); // important for lex GB
        // reduce remaining polynomials
        int len = G.size();
        int i = 0;
        while (i < len) {
            a = G.remove(0);
            //System.out.println("doing " + a.length());
            a = redRec.normalformRecursive(G, a);
            a = engine.recursivePrimitivePart(a); //a.monic(); was not required
            a = a.abs();
            //a = redRec.normalformRecursive( F, a );
            G.add(a); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Groebner base simple test.
     * @param modv module variable number.
     * @param F recursive polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    @Override
    public boolean isGBsimple(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> pi, pj, s, h;
        ExpVector ei, ej, eij;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            ei = pi.leadingExpVector();
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                ej = pj.leadingExpVector();
                if (!red.moduleCriterion(modv, ei, ej)) {
                    continue;
                }
                eij = ei.lcm(ej);
                if (!red.criterion4(ei, ej, eij)) {
                    continue;
                }
                //if (!criterion3(i, j, eij, F)) {
                //    continue;
                //}
                s = red.SPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                //System.out.println("i, j = " + i + ", " + j); 
                h = redRec.normalformRecursive(F, s);
                if (!h.isZERO()) {
                    logger.info("no GB: pi = " + pi + ", pj = " + pj);
                    logger.info("s  = " + s + ", h = " + h);
                    return false;
                }
            }
        }
        return true;
    }

}
