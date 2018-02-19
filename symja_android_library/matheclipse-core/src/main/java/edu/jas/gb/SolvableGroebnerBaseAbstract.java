/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.vector.BasicLinAlg;


/**
 * Solvable Groebner Bases abstract class. Implements common left, right and
 * twosided Groebner bases and left, right and twosided GB tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class SolvableGroebnerBaseAbstract<C extends RingElem<C>> implements SolvableGroebnerBase<C> {


    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();
    /**
     * Reduction engine.
     */
    public final Reduction<C> red;
    /**
     * Strategy for pair selection.
     */
    public final PairList<C> strategy;
    /**
     * Commutative Groebner bases engine.
     */
    public final GroebnerBaseAbstract<C> cbb;
    /**
     * Linear algebra engine.
     */
    protected final BasicLinAlg<GenPolynomial<C>> blas;
    /**
     * Solvable reduction engine.
     */
    public SolvableReduction<C> sred;


    /**
     * Constructor.
     */
    public SolvableGroebnerBaseAbstract() {
        this(new SolvableReductionSeq<C>());
    }


    /**
     * Constructor.
     *
     * @param sred Solvable reduction engine
     */
    public SolvableGroebnerBaseAbstract(SolvableReduction<C> sred) {
        this(sred, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     *
     * @param pl pair selection strategy
     */
    public SolvableGroebnerBaseAbstract(PairList<C> pl) {
        this(new SolvableReductionSeq<C>(), pl);
    }


    /**
     * Constructor.
     *
     * @param sred Solvable reduction engine
     * @param pl   pair selection strategy
     */
    public SolvableGroebnerBaseAbstract(SolvableReduction<C> sred, PairList<C> pl) {
        this.red = new ReductionSeq<C>();
        this.sred = sred;
        this.strategy = pl;
        blas = new BasicLinAlg<GenPolynomial<C>>();
        cbb = new GroebnerBaseSeq<C>();
    }


    /**
     * Normalize polynomial list.
     *
     * @param A list of polynomials.
     * @return list of polynomials with zeros removed and ones/units reduced.
     */
    public List<GenSolvablePolynomial<C>> normalizeZerosOnes(List<GenSolvablePolynomial<C>> A) {
        //List<GenPolynomial<C>> a = PolynomialList.<C> castToList(A);
        //List<GenPolynomial<C>> n = cbb.normalizeZeroOnes(a);
        //List<GenSolvablePolynomial<C>> N = PolynomialList.<C> castToSolvableList(n);
        if (A == null) {
            return A;
        }
        List<GenSolvablePolynomial<C>> N = new ArrayList<GenSolvablePolynomial<C>>(A.size());
        if (A.isEmpty()) {
            return N;
        }
        for (GenSolvablePolynomial<C> p : A) {
            if (p == null || p.isZERO()) {
                continue;
            }
            if (p.isUnit()) {
                N.clear();
                N.add(p.ring.getONE());
                return N;
            }
            N.add((GenSolvablePolynomial<C>) p.abs());
        }
        //N.trimToSize();
        return N;
    }


    /**
     * Left Groebner base test.
     *
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F) {
        return isLeftGB(0, F, true);
    }


    /**
     * Left Groebner base test.
     *
     * @param F solvable polynomial list.
     * @param b true for simple test, false for GB test.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F, boolean b) {
        return isLeftGB(0, F, b);
    }


    /**
     * Left Groebner base test.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return isLeftGB(modv, F, true);
    }


    /**
     * Left Groebner base test.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @param b    true for simple test, false for GB test.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F, boolean b) {
        if (b) {
            return isLeftGBsimple(modv, F);
        }
        return isLeftGBidem(modv, F);
    }


    /**
     * Left Groebner base test.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGBsimple(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> pi, pj, s, h;
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
                h = sred.leftNormalform(F, s);
                if (!h.isZERO()) {
                    logger.info("no left GB: pi = " + pi + ", pj = " + pj);
                    logger.info("s  = " + s + ", h = " + h);
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
    public boolean isLeftGBidem(int modv, List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenSolvablePolynomialRing<C> pring = F.get(0).ring;
        List<GenSolvablePolynomial<C>> G = leftGB(modv, F);
        PolynomialList<C> Fp = new PolynomialList<C>(pring, F);
        PolynomialList<C> Gp = new PolynomialList<C>(pring, G);
        return Fp.compareTo(Gp) == 0;
    }


    /**
     * Twosided Groebner base test.
     *
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp) {
        return isTwosidedGB(0, Fp);
    }


    /**
     * Twosided Groebner base test.
     *
     * @param modv number of module variables.
     * @param Fp   solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {
        if (Fp == null || Fp.size() == 0) { // 0 not 1
            return true;
        }
        if (Fp.size() == 1 && Fp.get(0).isONE()) {
            return true;
        }
        GenSolvablePolynomialRing<C> ring = Fp.get(0).ring; // assert != null
        // add also coefficient generators
        List<GenSolvablePolynomial<C>> X;
        X = PolynomialList.castToSolvableList(ring.generators(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(Fp.size() * (1 + X.size()));
        F.addAll(Fp);
        GenSolvablePolynomial<C> p, q, x, pi, pj, s, h;
        for (int i = 0; i < Fp.size(); i++) {
            p = Fp.get(i);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                q = p.multiply(x);
                q = sred.leftNormalform(F, q);
                //System.out.println("is: q generated = " + q + ", p = " + p + ", x = " + x);
                if (!q.isZERO()) {
                    return false;
                    //F.add(q);
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
                h = sred.leftNormalform(F, s);
                if (!h.isZERO()) {
                    logger.info("is not TwosidedGB: " + h);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Twosided Groebner base idempotence test.
     *
     * @param F solvable polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    public boolean isTwosidedGBidem(List<GenSolvablePolynomial<C>> F) {
        return isTwosidedGBidem(0, F);
    }


    /**
     * Twosided Groebner base idempotence test.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    public boolean isTwosidedGBidem(int modv, List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenSolvablePolynomialRing<C> pring = F.get(0).ring;
        List<GenSolvablePolynomial<C>> G = twosidedGB(modv, F);
        PolynomialList<C> Fp = new PolynomialList<C>(pring, F);
        PolynomialList<C> Gp = new PolynomialList<C>(pring, G);
        return Fp.compareTo(Gp) == 0;
    }


    /**
     * Right Groebner base test.
     *
     * @param F solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(List<GenSolvablePolynomial<C>> F) {
        return isRightGB(0, F);
    }


    /**
     * Right Groebner base test.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> pi, pj, s, h;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            //System.out.println("pi right = " + pi);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                //System.out.println("pj right = " + pj);
                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.rightSPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                //System.out.println("s right = " + s);
                h = sred.rightNormalform(F, s);
                if (!h.isZERO()) {
                    logger.info("isRightGB non zero h = " + h + " :: " + h.ring);
                    logger.info("p" + i + " = " + pi + ", p" + j + " = " + pj);
                    return false;
                    //} else {
                    //logger.info("isRightGB zero h = " + h);
                }
            }
        }
        return true;
    }


    /**
     * Right Groebner base idempotence test.
     *
     * @param F solvable polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    public boolean isRightGBidem(List<GenSolvablePolynomial<C>> F) {
        return isRightGBidem(0, F);
    }


    /**
     * Right Groebner base idempotence test.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    public boolean isRightGBidem(int modv, List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenSolvablePolynomialRing<C> pring = F.get(0).ring;
        List<GenSolvablePolynomial<C>> G = rightGB(modv, F);
        PolynomialList<C> Fp = new PolynomialList<C>(pring, F);
        PolynomialList<C> Gp = new PolynomialList<C>(pring, G);
        return Fp.compareTo(Gp) == 0;
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> leftGB(List<GenSolvablePolynomial<C>> F) {
        return leftGB(0, F);
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     *
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C> extLeftGB(List<GenSolvablePolynomial<C>> F) {
        return extLeftGB(0, F);
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return a container for an extended left Groebner base G of F together
     * with back-and-forth transformations.
     */
    public SolvableExtendedGB<C> extLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        throw new UnsupportedOperationException("extLeftGB not implemented in "
                + this.getClass().getSimpleName());
    }


    /**
     * Left minimal ordered groebner basis.
     *
     * @param Gp a left Groebner base.
     * @return leftGBmi(F) a minimal left Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> leftMinimalGB(List<GenSolvablePolynomial<C>> Gp) {
        ArrayList<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        ListIterator<GenSolvablePolynomial<C>> it = Gp.listIterator();
        for (GenSolvablePolynomial<C> a : Gp) {
            // a = (SolvablePolynomial) it.next();
            if (a.length() != 0) { // always true
                // already monic a = a.monic();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }

        ExpVector e;
        ExpVector f;
        GenSolvablePolynomial<C> a, p;
        ArrayList<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>();
        boolean mt;

        while (G.size() > 0) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            it = F.listIterator();
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            if (!mt) {
                F.add(a);
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }

        F = new ArrayList<GenSolvablePolynomial<C>>();
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            a = sred.leftNormalform(G, a);
            a = sred.leftNormalform(F, a);
            F.add(a);
        }
        return F;
    }


    /**
     * Right minimal ordered groebner basis.
     *
     * @param Gp a right Groebner base.
     * @return rightGBmi(F) a minimal right Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> rightMinimalGB(List<GenSolvablePolynomial<C>> Gp) {
        ArrayList<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        ListIterator<GenSolvablePolynomial<C>> it = Gp.listIterator();
        for (GenSolvablePolynomial<C> a : Gp) {
            if (a.length() != 0) { // always true
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }

        ExpVector e;
        ExpVector f;
        GenSolvablePolynomial<C> a, p;
        ArrayList<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>();
        boolean mt;

        while (G.size() > 0) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            it = F.listIterator();
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            if (!mt) {
                F.add(a);
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }

        F = new ArrayList<GenSolvablePolynomial<C>>();
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            a = sred.rightNormalform(G, a);
            a = sred.rightNormalform(F, a);
            F.add(a);
        }
        return F;
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> twosidedGB(List<GenSolvablePolynomial<C>> Fp) {
        return twosidedGB(0, Fp);
    }


    /**
     * Right Groebner base using opposite ring left GB.
     *
     * @param F solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> rightGB(List<GenSolvablePolynomial<C>> F) {
        return rightGB(0, F);
    }


    /**
     * Right Groebner base using opposite ring left GB.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial<C>> rightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(F);
        if (G.size() <= 1) {
            return G;
        }
        GenSolvablePolynomialRing<C> ring = G.get(0).ring; // assert != null
        GenSolvablePolynomialRing<C> rring = ring.reverse(true); //true
        //System.out.println("reversed ring = " + rring);
        GenSolvablePolynomial<C> q;
        List<GenSolvablePolynomial<C>> rF;
        rF = new ArrayList<GenSolvablePolynomial<C>>(F.size());
        for (GenSolvablePolynomial<C> p : F) {
            if (p != null) {
                q = (GenSolvablePolynomial<C>) p.reverse(rring);
                rF.add(q);
            }
        }
        if (logger.isInfoEnabled()) {
            PolynomialList<C> pl = new PolynomialList<C>(rring, rF);
            logger.info("reversed problem = " + pl.toScript());
        }
        List<GenSolvablePolynomial<C>> rG = leftGB(modv, rF);
        if (debug) {
            //PolynomialList<C> pl = new PolynomialList<C>(rring,rG);
            //logger.info("reversed GB = " + pl);
            long t = System.currentTimeMillis();
            boolean isit = isLeftGB(rG);
            t = System.currentTimeMillis() - t;
            logger.info("is left GB = " + isit + ", in " + t + " milliseconds");
        }
        //System.out.println("reversed left GB = " + rG);
        ring = rring.reverse(true); // true
        G = new ArrayList<GenSolvablePolynomial<C>>(rG.size());
        for (GenSolvablePolynomial<C> p : rG) {
            if (p != null) {
                q = (GenSolvablePolynomial<C>) p.reverse(ring);
                G.add(q);
            }
        }
        if (debug) {
            //PolynomialList<C> pl = new PolynomialList<C>(ring,G);
            //logger.info("GB = " + pl);
            long t = System.currentTimeMillis();
            boolean isit = isRightGB(G);
            t = System.currentTimeMillis() - t;
            logger.info("is right GB = " + isit + ", in " + t + " milliseconds");
        }
        return G;
    }


    /**
     * Module left Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public boolean isLeftGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return isLeftGB(modv, F.castToSolvableList());
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> leftGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        if (debug) {
            logger.info("F left +++++++++++++++++++ \n" + F);
        }
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G = leftGB(modv, F.castToSolvableList());
        F = new PolynomialList<C>(sring, G);
        if (debug) {
            logger.info("G left +++++++++++++++++++ \n" + F);
        }
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module twosided Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public boolean isTwosidedGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return isTwosidedGB(modv, F.castToSolvableList());
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> twosidedGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G = twosidedGB(modv, F.castToSolvableList());
        F = new PolynomialList<C>(sring, G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module right Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a right Groebner base, else false.
     */
    public boolean isRightGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        //System.out.println("F test = " + F);
        return isRightGB(modv, F.castToSolvableList());
    }


    /**
     * Right Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return rightGB(M) a right Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> rightGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        if (debug) {
            logger.info("M ====================== \n" + M);
        }
        TermOrder to = M.ring.tord;
        if (to.getSplit() <= M.ring.nvar) {
            throw new IllegalArgumentException("extended TermOrders not supported for rightGBs: " + to);
        }
        List<List<GenSolvablePolynomial<C>>> mlist = M.castToSolvableList();
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) M.ring;
        GenSolvablePolynomialRing<C> rring = sring.reverse(true); //true
        sring = rring.reverse(true); // true

        List<List<GenSolvablePolynomial<C>>> nlist = new ArrayList<List<GenSolvablePolynomial<C>>>(M.rows);
        for (List<GenSolvablePolynomial<C>> row : mlist) {
            List<GenSolvablePolynomial<C>> nrow = new ArrayList<GenSolvablePolynomial<C>>(row.size());
            for (GenSolvablePolynomial<C> elem : row) {
                GenSolvablePolynomial<C> nelem = (GenSolvablePolynomial<C>) elem.reverse(rring);
                nrow.add(nelem);
            }
            nlist.add(nrow);
        }
        ModuleList<C> rM = new ModuleList<C>(rring, nlist);
        if (debug) {
            logger.info("rM -------------------- \n" + rM);
        }
        ModuleList<C> rMg = leftGB(rM);
        if (debug) {
            logger.info("rMg -------------------- \n" + rMg);
            logger.info("isLeftGB(rMg) ---------- " + isLeftGB(rMg));
        }
        mlist = rMg.castToSolvableList();
        nlist = new ArrayList<List<GenSolvablePolynomial<C>>>(rMg.rows);
        for (List<GenSolvablePolynomial<C>> row : mlist) {
            List<GenSolvablePolynomial<C>> nrow = new ArrayList<GenSolvablePolynomial<C>>(row.size());
            for (GenSolvablePolynomial<C> elem : row) {
                GenSolvablePolynomial<C> nelem = (GenSolvablePolynomial<C>) elem.reverse(sring);
                nrow.add(nelem);
            }
            nlist.add(nrow);
        }
        ModuleList<C> Mg = new ModuleList<C>(sring, nlist);
        if (debug) {
            logger.info("Mg -------------------- \n" + Mg);
            logger.info("isRightGB(Mg) --------- " + isRightGB(Mg));
        }
        return Mg;
    }


    /**
     * Test if left reduction matrix.
     *
     * @param exgb an SolvableExtendedGB container.
     * @return true, if exgb contains a left reduction matrix, else false.
     */
    public boolean isLeftReductionMatrix(SolvableExtendedGB<C> exgb) {
        if (exgb == null) {
            return true;
        }
        return isLeftReductionMatrix(exgb.F, exgb.G, exgb.F2G, exgb.G2F);
    }


    /**
     * Test if left reduction matrix.
     *
     * @param F  a solvable polynomial list.
     * @param G  a left Groebner base.
     * @param Mf a possible left reduction matrix.
     * @param Mg a possible left reduction matrix.
     * @return true, if Mg and Mf are left reduction matrices, else false.
     */
    public boolean isLeftReductionMatrix(List<GenSolvablePolynomial<C>> F, List<GenSolvablePolynomial<C>> G,
                                         List<List<GenSolvablePolynomial<C>>> Mf, List<List<GenSolvablePolynomial<C>>> Mg) {
        // no more check G and Mg: G * Mg[i] == 0
        // check F and Mg: F * Mg[i] == G[i]
        int k = 0;
        for (List<GenSolvablePolynomial<C>> row : Mg) {
            boolean t = sred.isLeftReductionNF(row, F, G.get(k), null);
            if (!t) {
                System.out.println("row = " + row);
                System.out.println("F   = " + F);
                System.out.println("Gk  = " + G.get(k));
                logger.info("F isLeftReductionMatrix s, k = " + F.size() + ", " + k);
                return false;
            }
            k++;
        }
        // check G and Mf: G * Mf[i] == F[i]
        k = 0;
        for (List<GenSolvablePolynomial<C>> row : Mf) {
            boolean t = sred.isLeftReductionNF(row, G, F.get(k), null);
            if (!t) {
                logger.error("G isLeftReductionMatrix s, k = " + G.size() + ", " + k);
                return false;
            }
            k++;
        }
        return true;
    }


    /**
     * Ideal common zero test.
     *
     * @return -1, 0 or 1 if dimension(this) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest(List<GenSolvablePolynomial<C>> A) {
        List<GenPolynomial<C>> cA = PolynomialList.<C>castToList(A);
        return cbb.commonZeroTest(cA);
    }


    /**
     * Univariate head term degrees.
     *
     * @param A list of solvable polynomials.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees(List<GenSolvablePolynomial<C>> A) {
        List<GenPolynomial<C>> cA = PolynomialList.<C>castToList(A);
        return cbb.univariateDegrees(cA);
    }


    /**
     * Construct univariate solvable polynomial of minimal degree in variable i
     * of a zero dimensional ideal(G).
     *
     * @param i variable index.
     * @param G list of solvable polynomials, a monic reduced left Gr&ouml;bner
     *          base of a zero dimensional ideal.
     * @return univariate solvable polynomial of minimal degree in variable i in
     * ideal_left(G)
     */
    public GenSolvablePolynomial<C> constructUnivariate(int i, List<GenSolvablePolynomial<C>> G) {
        if (G == null || G.size() == 0) {
            throw new IllegalArgumentException("G may not be null or empty");
        }
        List<Long> ud = univariateDegrees(G);
        if (ud.size() <= i) {
            //logger.info("univ pol, ud = " + ud);
            throw new IllegalArgumentException("ideal(G) not zero dimensional " + ud);
        }
        int ll = 0;
        Long di = ud.get(i);
        if (di != null) {
            ll = (int) (long) di;
        } else {
            throw new IllegalArgumentException("ideal(G) not zero dimensional");
        }
        long vsdim = 1;
        for (Long d : ud) {
            if (d != null) {
                vsdim *= d;
            }
        }
        logger.info("univariate construction, deg = " + ll + ", vsdim = " + vsdim);
        GenSolvablePolynomialRing<C> pfac = G.get(0).ring;
        RingFactory<C> cfac = pfac.coFac;

        GenPolynomialRing<C> cpfac = new GenPolynomialRing<C>(cfac, ll, new TermOrder(TermOrder.INVLEX));
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = new GenSolvablePolynomialRing<GenPolynomial<C>>(
                cpfac, pfac); // relations
        GenSolvablePolynomial<GenPolynomial<C>> P = rfac.getZERO();
        for (int k = 0; k < ll; k++) {
            GenSolvablePolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, k);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - k);
            Pp = Pp.multiply(cp);
            P = (GenSolvablePolynomial<GenPolynomial<C>>) P.sum(Pp);
        }
        if (debug) {
            logger.info("univariate construction, P = " + P);
            logger.info("univariate construction, deg_*(G) = " + ud);
            //throw new RuntimeException("check");
        }
        GroebnerBaseAbstract<C> bbc = new GroebnerBaseSeq<C>();
        GenSolvablePolynomial<C> X;
        GenSolvablePolynomial<C> XP;
        // solve system of linear equations for the coefficients of the univariate polynomial
        List<GenPolynomial<C>> ls;
        int z = -1;
        do {
            //System.out.println("ll  = " + ll);
            GenSolvablePolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, ll);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - ll);
            Pp = Pp.multiply(cp);
            P = (GenSolvablePolynomial<GenPolynomial<C>>) P.sum(Pp);
            X = pfac.univariate(i, ll);
            XP = sred.leftNormalform(G, X);
            //System.out.println("XP = " + XP);
            GenSolvablePolynomial<GenPolynomial<C>> XPp = PolyUtil.<C>toRecursive(rfac, XP);
            GenSolvablePolynomial<GenPolynomial<C>> XPs = (GenSolvablePolynomial<GenPolynomial<C>>) XPp
                    .sum(P);
            ls = new ArrayList<GenPolynomial<C>>(XPs.getMap().values());
            //System.out.println("ls,1 = " + ls);
            ls = red.irreducibleSet(ls);
            z = bbc.commonZeroTest(ls);
            if (z != 0) {
                ll++;
                if (ll > vsdim) {
                    logger.info("univariate construction, P = " + P);
                    logger.info("univariate construction, nf(P) = " + XP);
                    logger.info("G = " + G);
                    throw new ArithmeticException(
                            "univariate polynomial degree greater than vector space dimansion");
                }
                cpfac = cpfac.extend(1);
                rfac = new GenSolvablePolynomialRing<GenPolynomial<C>>(cpfac, pfac);
                P = PolyUtil.<C>extendCoefficients(rfac, P, 0, 0L);
                XPp = PolyUtil.<C>extendCoefficients(rfac, XPp, 0, 1L);
                P = (GenSolvablePolynomial<GenPolynomial<C>>) P.sum(XPp);
            }
        } while (z != 0); // && ll <= 5 && !XP.isZERO()
        // construct result polynomial
        String var = pfac.getVars()[pfac.nvar - 1 - i];
        GenSolvablePolynomialRing<C> ufac = new GenSolvablePolynomialRing<C>(cfac, 1, new TermOrder(
                TermOrder.INVLEX), new String[]{var});
        GenSolvablePolynomial<C> pol = ufac.univariate(0, ll);
        for (GenPolynomial<C> pc : ls) {
            ExpVector e = pc.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] v = e.dependencyOnVariables();
            if (v == null || v.length == 0) {
                continue;
            }
            int vi = v[0];
            C tc = pc.trailingBaseCoefficient();
            tc = tc.negate();
            GenSolvablePolynomial<C> pi = ufac.univariate(0, ll - 1 - vi);
            pi = pi.multiply(tc);
            pol = (GenSolvablePolynomial<C>) pol.sum(pi);
        }
        if (logger.isInfoEnabled()) {
            logger.info("univariate construction, pol = " + pol);
        }
        return pol;
    }


    /**
     * Construct univariate solvable polynomials of minimal degree in all
     * variables in zero dimensional left ideal(G).
     *
     * @return list of univariate polynomial of minimal degree in each variable
     * in ideal_left(G)
     */
    public List<GenSolvablePolynomial<C>> constructUnivariate(List<GenSolvablePolynomial<C>> G) {
        List<GenSolvablePolynomial<C>> univs = new ArrayList<GenSolvablePolynomial<C>>();
        if (G == null || G.isEmpty()) {
            return univs;
        }
        for (int i = G.get(0).ring.nvar - 1; i >= 0; i--) {
            GenSolvablePolynomial<C> u = constructUnivariate(i, G);
            univs.add(u);
        }
        return univs;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        logger.info("terminate not implemented");
        //throw new RuntimeException("get a stack trace");
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        logger.info("cancel not implemented");
        return 0;
    }

}
