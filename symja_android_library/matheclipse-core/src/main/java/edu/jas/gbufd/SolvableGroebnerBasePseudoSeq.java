/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.Pair;
import edu.jas.gb.PairList;
import edu.jas.gb.SolvableExtendedGB;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorFake;

// import edu.jas.poly.GenPolynomialRing;


/**
 * Solvable Groebner Base with pseudo reduction sequential algorithm. Implements
 * coefficient fraction free Groebner bases. Coefficients can for example be
 * integers or (commutative) univariate polynomials.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see GBFactory
 */

public class SolvableGroebnerBasePseudoSeq<C extends GcdRingElem<C>> extends SolvableGroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(SolvableGroebnerBasePseudoSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final SolvablePseudoReduction<C> sred;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     *
     * @param rf coefficient ring factory.
     */
    public SolvableGroebnerBasePseudoSeq(RingFactory<C> rf) {
        this(new SolvablePseudoReductionSeq<C>(), rf, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     *
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    public SolvableGroebnerBasePseudoSeq(RingFactory<C> rf, PairList<C> pl) {
        this(new SolvablePseudoReductionSeq<C>(), rf, pl);
    }


    /**
     * Constructor.
     *
     * @param red pseudo reduction engine. <b>Note:</b> red must be an instance
     *            of PseudoReductionSeq.
     * @param rf  coefficient ring factory.
     * @param pl  pair selection strategy
     */
    public SolvableGroebnerBasePseudoSeq(SolvablePseudoReduction<C> red, RingFactory<C> rf, PairList<C> pl) {
        super(red, pl);
        this.sred = red;
        cofac = rf;
        if (!cofac.isCommutative()) {
            logger.warn("right reduction not correct for " + cofac.toScript());
            engine = new GreatestCommonDivisorFake<C>(); // only for Ore conditions
            // TODO check that also coeffTable is empty for recursive solvable poly ring
            //System.out.println("stack trace = "); 
            //Exception e = new RuntimeException("get stack trace");
            //e.printStackTrace();
        } else {
            //engine = GCDFactory.<C> getImplementation(rf);
            engine = GCDFactory.<C>getProxy(rf);
        }
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    @Override
    public List<GenSolvablePolynomial<C>> leftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(F);
        G = PolynomialList.<C>castToSolvableList(engine.basePrimitivePart(PolynomialList.<C>castToList(G)));
        if (G.size() <= 1) {
            return G;
        }
        GenSolvablePolynomialRing<C> ring = G.get(0).ring;
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.<C>castToList(G));

        Pair<C> pair;
        GenSolvablePolynomial<C> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null)
                continue;

            pi = (GenSolvablePolynomial<C>) pair.pi;
            pj = (GenSolvablePolynomial<C>) pair.pj;
            if (debug) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }

            S = sred.leftSPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = sred.leftNormalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }
            H = (GenSolvablePolynomial<C>) engine.basePrimitivePart(H);
            H = (GenSolvablePolynomial<C>) H.abs();
            if (H.isConstant()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (logger.isDebugEnabled()) {
                logger.debug("H = " + H);
            }
            if (H.length() > 0) {
                G.add(H);
                pairlist.put(H);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = leftMinimalGB(G);
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered Solvable Groebner basis.
     *
     * @param Gp a Solvable Groebner base.
     * @return a reduced Solvable Groebner base of Gp.
     */
    @Override
    public List<GenSolvablePolynomial<C>> leftMinimalGB(List<GenSolvablePolynomial<C>> Gp) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(Gp);
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenSolvablePolynomial<C> a;
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (sred.isTopReducible(G, a) || sred.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenSolvablePolynomial<C>> ff;
                    ff = new ArrayList<GenSolvablePolynomial<C>>(G);
                    ff.addAll(F);
                    a = sred.leftNormalform(ff, a);
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
            a = sred.leftNormalform(G, a);
            a = (GenSolvablePolynomial<C>) engine.basePrimitivePart(a); //a.monic(); not possible
            a = (GenSolvablePolynomial<C>) a.abs();
            //a = sred.normalform( F, a );
            G.add(a); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Twosided Solvable Groebner base using pairlist class.
     *
     * @param modv number of module variables.
     * @param Fp   solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    @Override
    public List<GenSolvablePolynomial<C>> twosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(Fp);
        G = PolynomialList.<C>castToSolvableList(engine.basePrimitivePart(PolynomialList.<C>castToList(G)));
        if (G.size() < 1) { // two-sided!
            return G;
        }
        //System.out.println("G = " + G);
        GenSolvablePolynomialRing<C> ring = G.get(0).ring; // assert != null
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        // add also coefficient generators
        List<GenSolvablePolynomial<C>> X;
        X = PolynomialList.castToSolvableList(ring.generators(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(G.size() * (1 + X.size()));
        F.addAll(G);
        logger.info("right multipy: F = " + F);
        GenSolvablePolynomial<C> p, x, q;
        for (int i = 0; i < F.size(); i++) { // F changes
            p = F.get(i);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                q = p.multiply(x);
                logger.info("right multipy: p = " + p + ", x = " + x + ", q = " + q);
                q = sred.leftNormalform(F, q);
                if (!q.isZERO()) {
                    q = (GenSolvablePolynomial<C>) engine.basePrimitivePart(q);
                    q = (GenSolvablePolynomial<C>) q.abs();
                    logger.info("right multipy: red(q) = " + q);
                    F.add(q);
                }
            }
        }
        G = F;
        //System.out.println("G generated = " + G);
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.<C>castToList(G));

        Pair<C> pair;
        GenSolvablePolynomial<C> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }

            pi = (GenSolvablePolynomial<C>) pair.pi;
            pj = (GenSolvablePolynomial<C>) pair.pj;
            if (debug) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }

            S = sred.leftSPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = sred.leftNormalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }

            H = (GenSolvablePolynomial<C>) engine.basePrimitivePart(H);
            H = (GenSolvablePolynomial<C>) H.abs();
            if (H.isONE()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (debug) {
                logger.debug("H = " + H);
            }
            if (H.length() > 0) {
                G.add(H);
                pairlist.put(H);
                for (int j = 0; j < X.size(); j++) {
                    x = X.get(j);
                    p = H.multiply(x);
                    p = sred.leftNormalform(G, p);
                    if (!p.isZERO()) {
                        p = (GenSolvablePolynomial<C>) engine.basePrimitivePart(p);
                        p = (GenSolvablePolynomial<C>) p.abs();
                        if (p.isONE()) {
                            G.clear();
                            G.add(p);
                            return G; // since no threads are activated
                        }
                        G.add(p);
                        pairlist.put(p);
                    }
                }
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = leftMinimalGB(G);
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return a container for an extended left Groebner base of F. <b>Note:
     * </b> not implemented;
     */
    //@SuppressWarnings("unchecked")
    @Override
    public SolvableExtendedGB<C> extLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }

}
