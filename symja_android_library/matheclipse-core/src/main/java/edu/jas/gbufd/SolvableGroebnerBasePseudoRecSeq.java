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
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorFake;


/**
 * Solvable Groebner Base with pseudo reduction sequential algorithm. Implements
 * coefficient fraction free Groebner bases. Coefficients can for example be
 * (commutative) multivariate polynomials.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see GBFactory
 */

public class SolvableGroebnerBasePseudoRecSeq<C extends GcdRingElem<C>> extends
        SolvableGroebnerBaseAbstract<GenPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(SolvableGroebnerBasePseudoRecSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final SolvablePseudoReduction<C> sredRec;


    /**
     * Pseudo reduction engine.
     */
    protected final SolvablePseudoReduction<GenPolynomial<C>> sred;


    /**
     * Coefficient ring factory.
     */
    //protected final RingFactory<C> cofac;
    protected final GenPolynomialRing<C> cofac;


    /**
     * Constructor.
     *
     * @param rf coefficient ring factory.
     */
    public SolvableGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf) {
        this(rf, new SolvablePseudoReductionSeq<C>());
    }


    /**
     * Constructor.
     *
     * @param rf coefficient ring factory.
     * @param pl pair selection strategy
     */
    public SolvableGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf, PairList<GenPolynomial<C>> pl) {
        this(rf, new SolvablePseudoReductionSeq<C>(), pl);
    }


    /**
     * Constructor.
     *
     * @param rf  coefficient ring factory.
     * @param red pseudo reduction engine. <b>Note:</b> red must be an instance
     *            of PseudoReductionSeq.
     */
    public SolvableGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf, SolvablePseudoReduction<C> red) {
        this(rf, red, new OrderedPairlist<GenPolynomial<C>>());
    }


    /**
     * Constructor.
     *
     * @param rf  coefficient ring factory.
     * @param red pseudo reduction engine. <b>Note:</b> red must be an instance
     *            of PseudoReductionSeq.
     * @param pl  pair selection strategy
     */
    @SuppressWarnings({"cast", "unchecked"})
    public SolvableGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf, SolvablePseudoReduction<C> red,
                                            PairList<GenPolynomial<C>> pl) {
        super((SolvablePseudoReduction<GenPolynomial<C>>) (SolvablePseudoReduction) red, pl);
        this.sred = (SolvablePseudoReduction<GenPolynomial<C>>) (SolvablePseudoReduction) red;
        sredRec = red;
        //this.red = sred;
        cofac = (GenPolynomialRing<C>) rf;
        if (!cofac.isCommutative()) {
            logger.warn("right reduction not correct for " + cofac.toScript());
            engine = new GreatestCommonDivisorFake<C>();
            // TODO check that also coeffTable is empty for recursive solvable poly ring
        } else {
            //engine = GCDFactory.<C> getImplementation(cofac.coFac);
            //
            engine = GCDFactory.<C>getProxy(cofac.coFac);
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
    public List<GenSolvablePolynomial<GenPolynomial<C>>> leftGB(int modv,
                                                                List<GenSolvablePolynomial<GenPolynomial<C>>> F) {
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(F);
        G = PolynomialList.<GenPolynomial<C>>castToSolvableList(PolyUtil.<C>monicRec(engine
                .recursivePrimitivePart(PolynomialList.<GenPolynomial<C>>castToList(G))));
        if (G.size() <= 1) {
            return G;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> ring = G.get(0).ring;
        if (ring.coFac.isField()) { // TODO remove 
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<GenPolynomial<C>> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.<GenPolynomial<C>>castToList(G));
        logger.info("leftGB start " + pairlist);

        Pair<GenPolynomial<C>> pair;
        GenSolvablePolynomial<GenPolynomial<C>> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null)
                continue;

            pi = (GenSolvablePolynomial<GenPolynomial<C>>) pair.pi;
            pj = (GenSolvablePolynomial<GenPolynomial<C>>) pair.pj;
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
                logger.info("ht(S) = " + S.leadingExpVector());
            }

            H = sredRec.leftNormalformRecursive(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector() + ", #(H) = " + H.length());
            }
            H = (GenSolvablePolynomial<GenPolynomial<C>>) engine.recursivePrimitivePart(H);
            H = PolyUtil.<C>monic(H);
            if (H.isConstant()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (debug) {
                logger.info("lc(pp(H)) = " + H.leadingBaseCoefficient().toScript());
            }
            if (H.length() > 0) {
                G.add(H);
                pairlist.put(H);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = leftMinimalGB(G);
        logger.info("leftGB end  " + pairlist);
        return G;
    }


    /**
     * Minimal ordered Solvable Groebner basis.
     *
     * @param Gp a Solvable Groebner base.
     * @return a reduced Solvable Groebner base of Gp.
     */
    @Override
    public List<GenSolvablePolynomial<GenPolynomial<C>>> leftMinimalGB(
            List<GenSolvablePolynomial<GenPolynomial<C>>> Gp) {
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(Gp);
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenSolvablePolynomial<GenPolynomial<C>> a;
        List<GenSolvablePolynomial<GenPolynomial<C>>> F = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (sred.isTopReducible(G, a) || sred.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenSolvablePolynomial<GenPolynomial<C>>> ff;
                    ff = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(G);
                    ff.addAll(F);
                    a = sredRec.leftNormalformRecursive(ff, a);
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
            a = sredRec.leftNormalformRecursive(G, a);
            a = (GenSolvablePolynomial<GenPolynomial<C>>) engine.recursivePrimitivePart(a); //a.monic(); not possible
            a = PolyUtil.<C>monic(a);
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
    public List<GenSolvablePolynomial<GenPolynomial<C>>> twosidedGB(int modv,
                                                                    List<GenSolvablePolynomial<GenPolynomial<C>>> Fp) {
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(Fp);
        G = PolynomialList.<GenPolynomial<C>>castToSolvableList(PolyUtil.<C>monicRec(engine
                .recursivePrimitivePart(PolynomialList.<GenPolynomial<C>>castToList(G))));
        if (G.size() < 1) { // two-sided!
            return G;
        }
        //System.out.println("G = " + G);
        GenSolvablePolynomialRing<GenPolynomial<C>> ring = G.get(0).ring; // assert != null
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        // add also coefficient generators
        List<GenSolvablePolynomial<GenPolynomial<C>>> X;
        X = PolynomialList.castToSolvableList(ring.generators(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<GenPolynomial<C>>> F = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                G.size() * (1 + X.size()));
        F.addAll(G);
        GenSolvablePolynomial<GenPolynomial<C>> p, x, q;
        for (int i = 0; i < F.size(); i++) { // F changes
            p = F.get(i);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                q = p.multiply(x);
                q = sredRec.leftNormalformRecursive(F, q);
                if (!q.isZERO()) {
                    q = (GenSolvablePolynomial<GenPolynomial<C>>) engine.recursivePrimitivePart(q);
                    q = PolyUtil.<C>monic(q);
                    F.add(q);
                }
            }
        }
        G = F;
        //System.out.println("G generated = " + G);
        PairList<GenPolynomial<C>> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.<GenPolynomial<C>>castToList(G));
        logger.info("twosidedGB start " + pairlist);

        Pair<GenPolynomial<C>> pair;
        GenSolvablePolynomial<GenPolynomial<C>> pi, pj, S, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }

            pi = (GenSolvablePolynomial<GenPolynomial<C>>) pair.pi;
            pj = (GenSolvablePolynomial<GenPolynomial<C>>) pair.pj;
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
                logger.info("ht(S) = " + S.leadingExpVector());
            }

            H = sredRec.leftNormalformRecursive(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }

            H = (GenSolvablePolynomial<GenPolynomial<C>>) engine.recursivePrimitivePart(H);
            H = PolyUtil.<C>monic(H);
            if (H.isONE()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (debug) {
                logger.info("lc(pp(H)) = " + H.leadingBaseCoefficient());
            }
            if (H.length() > 0) {
                G.add(H);
                pairlist.put(H);
                for (int j = 0; j < X.size(); j++) {
                    x = X.get(j);
                    if (x.isONE()) {
                        continue;
                    }
                    p = H.multiply(x);
                    p = sredRec.leftNormalformRecursive(G, p);
                    if (!p.isZERO()) {
                        p = (GenSolvablePolynomial<GenPolynomial<C>>) engine.recursivePrimitivePart(p);
                        p = PolyUtil.<C>monic(p);
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
        logger.info("twosidedGB end  " + pairlist);
        return G;
    }


    /**
     * Left Groebner base test.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    @Override
    public boolean isLeftGBsimple(int modv, List<GenSolvablePolynomial<GenPolynomial<C>>> F) {
        //System.out.println("F to left check = " + F);
        GenSolvablePolynomial<GenPolynomial<C>> pi, pj, s, h;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                h = sredRec.leftNormalformRecursive(F, s);
                if (!h.isZERO()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Left Groebner base idempotence test.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    @Override
    public boolean isLeftGBidem(int modv, List<GenSolvablePolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> pring = F.get(0).ring;
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = leftGB(modv, F);
        PolynomialList<GenPolynomial<C>> Fp = new PolynomialList<GenPolynomial<C>>(pring, F);
        PolynomialList<GenPolynomial<C>> Gp = new PolynomialList<GenPolynomial<C>>(pring, G);
        return Fp.compareTo(Gp) == 0;
    }


    /**
     * Twosided Groebner base test.
     *
     * @param modv number of module variables.
     * @param Fp   solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    @Override
    public boolean isTwosidedGB(int modv, List<GenSolvablePolynomial<GenPolynomial<C>>> Fp) {
        //System.out.println("F to twosided check = " + Fp);
        if (Fp == null || Fp.size() == 0) { // 0 not 1
            return true;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> ring = Fp.get(0).ring; // assert != null
        //List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        List<GenSolvablePolynomial<GenPolynomial<C>>> X, Y;
        X = PolynomialList.castToSolvableList(ring.generators()); // todo use? modv
        Y = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>();
        for (GenSolvablePolynomial<GenPolynomial<C>> x : X) {
            if (x.isConstant()) {
                Y.add(x);
            }
        }
        X = Y;
        X.addAll(ring.univariateList(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<GenPolynomial<C>>> F = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                Fp.size() * (1 + X.size()));
        F.addAll(Fp);
        GenSolvablePolynomial<GenPolynomial<C>> p, x, pi, pj, s, h;
        for (int i = 0; i < Fp.size(); i++) {
            p = Fp.get(i);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                p = p.multiply(x);
                p = sredRec.leftNormalformRecursive(F, p);
                if (!p.isZERO()) {
                    return false;
                    //F.add(p);
                }
            }
        }
        //System.out.println("F to check = " + F);
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                h = sredRec.leftNormalformRecursive(F, s);
                if (!h.isZERO()) {
                    logger.info("is not TwosidedGB: " + h);
                    return false;
                }
            }
        }
        return true;
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
    public SolvableExtendedGB<GenPolynomial<C>> extLeftGB(int modv,
                                                          List<GenSolvablePolynomial<GenPolynomial<C>>> F) {
        throw new UnsupportedOperationException(); // TODO
    }

}
