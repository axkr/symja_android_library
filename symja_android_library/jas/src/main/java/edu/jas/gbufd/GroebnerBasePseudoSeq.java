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
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;


/**
 * Groebner Base with pseudo reduction sequential algorithm. Implements
 * coefficient fraction free Groebner bases.
 * Coefficients can for example be integers or (commutative) univariate polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBasePseudoSeq<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBasePseudoSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<C> red;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePseudoSeq(RingFactory<C> rf) {
        this(new PseudoReductionSeq<C>(), rf, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoSeq(RingFactory<C> rf, PairList<C> pl) {
        this(new PseudoReductionSeq<C>(), rf, pl);
    }


    /**
     * Constructor.
     * @param red pseudo reduction engine. <b>Note:</b> red must be an instance
     *            of PseudoReductionSeq.
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoSeq(PseudoReduction<C> red, RingFactory<C> rf, PairList<C> pl) {
        super(red, pl);
        this.red = red;
        cofac = rf;
        engine = GCDFactory.<C> getImplementation(rf);
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getProxy( rf );
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = engine.basePrimitivePart(G);
        if (G.size() <= 1) {
            return G;
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(G);

        /*
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        PairList<C> pairlist = null;
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = engine.basePrimitivePart(p); //p.monic();
                p = p.abs();
                if (p.isConstant()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads are activated
                }
                G.add(p);
                if (pairlist == null) {
                    //pairlist = new OrderedPairlist<C>(modv, p.ring);
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

        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, H;
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
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = red.normalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }
            H = engine.basePrimitivePart(H);
            H = H.abs();
            if (H.isConstant()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (logger.isDebugEnabled()) {
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
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Gp) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(Gp);
        /*        
        // remove zero polynomials
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>(Gp.size());
        for (GenPolynomial<C> a : Gp) {
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
        GenPolynomial<C> a;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenPolynomial<C>> ff;
                    ff = new ArrayList<GenPolynomial<C>>(G);
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
            a = red.normalform(G, a);
            a = engine.basePrimitivePart(a); //a.monic(); not possible
            a = a.abs();
            //a = red.normalform( F, a );
            G.add(a); // adds as last
            i++;
        }
        return G;
    }

}
