/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.vector.BasicLinAlg;


/**
 * Groebner Bases abstract class. Implements common Groebner bases and GB test
 * methods.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public abstract class GroebnerBaseAbstract<C extends RingElem<C>> implements GroebnerBase<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    public final Reduction<C> red;


    /**
     * Strategy for pair selection.
     */
    public final PairList<C> strategy;


    /**
     * linear algebra engine.
     */
    public final BasicLinAlg<GenPolynomial<C>> blas;


    /**
     * Constructor.
     */
    public GroebnerBaseAbstract() {
        this(new ReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseAbstract(Reduction<C> red) {
        this(red, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param pl pair selection strategy
     */
    public GroebnerBaseAbstract(PairList<C> pl) {
        this(new ReductionSeq<C>(), pl);
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public GroebnerBaseAbstract(Reduction<C> red, PairList<C> pl) {
        if (red == null) {
            red = new ReductionSeq<C>();
        }
        this.red = red;
        if (pl == null) {
            pl = new OrderedPairlist<C>();
        }
        this.strategy = pl;
        blas = new BasicLinAlg<GenPolynomial<C>>();
    }


    /**
     * Get the String representation with GB engines.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


    /**
     * Groebner base test.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F) {
        return isGB(0, F);
    }


    /**
     * Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {
        return isGB(modv, F, true);
    }


    /**
     * Groebner base test.
     * @param F polynomial list.
     * @param b true for simple test, false for GB test.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F, boolean b) {
        return isGB(0, F, b);
    }


    /**
     * Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @param b true for simple test, false for GB test.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F, boolean b) {
        if (b) {
            return isGBsimple(modv, F);
        }
        return isGBidem(modv, F);
    }


    /**
     * Groebner base simple test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGBsimple(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenPolynomial<C> pi, pj, s, h;
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
                if (!criterion3(i, j, eij, F)) {
                    continue;
                }
                s = red.SPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                //System.out.println("i, j = " + i + ", " + j); 
                h = red.normalform(F, s);
                if (!h.isZERO()) {
                    logger.info("no GB: pi = " + pi + ", pj = " + pj);
                    logger.info("s  = " + s + ", h = " + h);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    boolean criterion3(int i, int j, ExpVector eij, List<GenPolynomial<C>> P) {
        assert i < j;
        //for ( int k = 0; k < P.size(); k++ ) {
        // not of much use
        for (int k = 0; k < i; k++) {
            GenPolynomial<C> A = P.get(k);
            ExpVector ek = A.leadingExpVector();
            if (eij.multipleOf(ek)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Groebner base idempotence test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is equal to GB(F), else false.
     */
    public boolean isGBidem(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenPolynomialRing<C> pring = F.get(0).ring;
        List<GenPolynomial<C>> G = GB(modv, F);
        PolynomialList<C> Fp = new PolynomialList<C>(pring, F);
        PolynomialList<C> Gp = new PolynomialList<C>(pring, G);
        return Fp.compareTo(Gp) == 0;
    }


    /**
     * Common zero test.
     * @param F polynomial list.
     * @return -1, 0 or 1 if dimension(ideal(F)) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest(List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return 1;
        }
        GenPolynomialRing<C> pfac = F.get(0).ring;
        if (pfac.nvar <= 0) {
            return -1;
        }
        //int uht = 0;
        Set<Integer> v = new HashSet<Integer>(); // for non reduced GBs
        for (GenPolynomial<C> p : F) {
            if (p.isZERO()) {
                continue;
            }
            if (p.isConstant()) { // for non-monic lists
                return -1;
            }
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.length == 1) {
                //uht++;
                v.add(u[0]);
            }
        }
        if (pfac.nvar == v.size()) {
            return 0;
        }
        return 1;
    }


    /**
     * Groebner base using pairlist class.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(List<GenPolynomial<C>> F) {
        return GB(0, F);
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param F polynomial list.
     * @return a container for a Groebner base G of F together with
     *         back-and-forth transformations.
     */
    public ExtendedGB<C> extGB(List<GenPolynomial<C>> F) {
        return extGB(0, F);
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for a Groebner base G of F together with
     *         back-and-forth transformations.
     */
    public ExtendedGB<C> extGB(int modv, List<GenPolynomial<C>> F) {
        throw new UnsupportedOperationException("extGB not implemented in " + this.getClass().getSimpleName());
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>(Gp.size());
        for (GenPolynomial<C> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
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
        // reduce remaining polynomials
        Collections.reverse(G); // important for lex GB
        int len = G.size();
        if (debug) {
            System.out.println("#G " + len);
            for (GenPolynomial<C> aa : G) {
                System.out.println("aa = " + aa.length() + ", lt = " + aa.getMap().keySet());
            }
        }
        int i = 0;
        while (i < len) {
            a = G.remove(0);
            if (debug) {
                System.out.println("doing " + a.length() + ", lt = " + a.leadingExpVector());
            }
            a = red.normalform(G, a);
            G.add(a); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Test for minimal ordered Groebner basis.
     * @param Gp an ideal base.
     * @return true, if Gp is a reduced minimal Groebner base.
     */
    public boolean isMinimalGB(List<GenPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() == 0) {
            return true;
        }
        // test for zero polynomials
        for (GenPolynomial<C> a : Gp) {
            if (a == null || a.isZERO()) {
                if (debug) {
                    logger.debug("zero polynomial " + a);
                }
                return false;
            }
        }
        // test for top reducible polynomials
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>(Gp);
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            GenPolynomial<C> a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                if (debug) {
                    logger.debug("top reducible polynomial " + a);
                }
                return false;
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return true;
        }
        // test reducibility of polynomials
        int len = G.size();
        int i = 0;
        while (i < len) {
            GenPolynomial<C> a = G.remove(0);
            if (!red.isNormalform(G, a)) {
                if (debug) {
                    logger.debug("reducible polynomial " + a);
                }
                return false;
            }
            G.add(a); // re-adds as last
            i++;
        }
        return true;
    }


    /**
     * Test if reduction matrix.
     * @param exgb an ExtendedGB container.
     * @return true, if exgb contains a reduction matrix, else false.
     */
    public boolean isReductionMatrix(ExtendedGB<C> exgb) {
        if (exgb == null) {
            return true;
        }
        return isReductionMatrix(exgb.F, exgb.G, exgb.F2G, exgb.G2F);
    }


    /**
     * Test if reduction matrix.
     * @param F a polynomial list.
     * @param G a Groebner base.
     * @param Mf a possible reduction matrix.
     * @param Mg a possible reduction matrix.
     * @return true, if Mg and Mf are reduction matrices, else false.
     */
    public boolean isReductionMatrix(List<GenPolynomial<C>> F, List<GenPolynomial<C>> G,
                    List<List<GenPolynomial<C>>> Mf, List<List<GenPolynomial<C>>> Mg) {
        // no more check G and Mg: G * Mg[i] == 0
        // check F and Mg: F * Mg[i] == G[i]
        int k = 0;
        for (List<GenPolynomial<C>> row : Mg) {
            boolean t = red.isReductionNF(row, F, G.get(k), null);
            if (!t) {
                logger.error("F isReductionMatrix s, k = " + F.size() + ", " + k);
                return false;
            }
            k++;
        }
        // check G and Mf: G * Mf[i] == F[i]
        k = 0;
        for (List<GenPolynomial<C>> row : Mf) {
            boolean t = red.isReductionNF(row, G, F.get(k), null);
            if (!t) {
                logger.error("G isReductionMatrix s, k = " + G.size() + ", " + k);
                return false;
            }
            k++;
        }
        return true;
    }


    /**
     * Normalize M. Make all rows the same size and make certain column elements
     * zero.
     * @param M a reduction matrix.
     * @return normalized M.
     */
    public List<List<GenPolynomial<C>>> normalizeMatrix(int flen, List<List<GenPolynomial<C>>> M) {
        if (M == null) {
            return M;
        }
        if (M.size() == 0) {
            return M;
        }
        List<List<GenPolynomial<C>>> N = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> K = new ArrayList<List<GenPolynomial<C>>>();
        int len = M.get(M.size() - 1).size(); // longest row
        // pad / extend rows
        for (List<GenPolynomial<C>> row : M) {
            List<GenPolynomial<C>> nrow = new ArrayList<GenPolynomial<C>>(row);
            for (int i = row.size(); i < len; i++) {
                nrow.add(null);
            }
            N.add(nrow);
        }
        // System.out.println("norm N fill = " + N);
        // make zero columns
        int k = flen;
        for (int i = 0; i < N.size(); i++) { // 0
            List<GenPolynomial<C>> row = N.get(i);
            if (debug) {
                logger.info("row = " + row);
            }
            K.add(row);
            if (i < flen) { // skip identity part
                continue;
            }
            List<GenPolynomial<C>> xrow;
            GenPolynomial<C> a;
            //System.out.println("norm i = " + i);
            for (int j = i + 1; j < N.size(); j++) {
                List<GenPolynomial<C>> nrow = N.get(j);
                //System.out.println("nrow j = " +j + ", " + nrow);
                if (k < nrow.size()) { // always true
                    a = nrow.get(k);
                    //System.out.println("k, a = " + k + ", " + a);
                    if (a != null && !a.isZERO()) {
                        xrow = blas.scalarProduct(a, row);
                        xrow = blas.vectorAdd(xrow, nrow);
                        //System.out.println("xrow = " + xrow);
                        N.set(j, xrow);
                    }
                }
            }
            k++;
        }
        //System.out.println("norm K reduc = " + K);
        // truncate 
        N.clear();
        for (List<GenPolynomial<C>> row : K) {
            List<GenPolynomial<C>> tr = new ArrayList<GenPolynomial<C>>();
            for (int i = 0; i < flen; i++) {
                tr.add(row.get(i));
            }
            N.add(tr);
        }
        K = N;
        //System.out.println("norm K trunc = " + K);
        return K;
    }


    /**
     * Minimal extended groebner basis.
     * @param Gp a Groebner base.
     * @param M a reduction matrix, is modified.
     * @return a (partially) reduced Groebner base of Gp in a container.
     */
    public ExtendedGB<C> minimalExtendedGB(int flen, List<GenPolynomial<C>> Gp, List<List<GenPolynomial<C>>> M) {
        if (Gp == null) {
            return null; //new ExtendedGB<C>(null,Gp,null,M);
        }
        if (Gp.size() <= 1) {
            return new ExtendedGB<C>(null, Gp, null, M);
        }
        List<GenPolynomial<C>> G;
        List<GenPolynomial<C>> F;
        G = new ArrayList<GenPolynomial<C>>(Gp);
        F = new ArrayList<GenPolynomial<C>>(Gp.size());

        List<List<GenPolynomial<C>>> Mg;
        List<List<GenPolynomial<C>>> Mf;
        Mg = new ArrayList<List<GenPolynomial<C>>>(M.size());
        Mf = new ArrayList<List<GenPolynomial<C>>>(M.size());
        List<GenPolynomial<C>> row;
        for (List<GenPolynomial<C>> r : M) {
            // must be copied also
            row = new ArrayList<GenPolynomial<C>>(r);
            Mg.add(row);
        }
        row = null;

        GenPolynomial<C> a;
        ExpVector e;
        ExpVector f;
        GenPolynomial<C> p;
        boolean mt;
        ListIterator<GenPolynomial<C>> it;
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> jx = new ArrayList<Integer>();
        int k = 0;
        //System.out.println("flen, Gp, M = " + flen + ", " + Gp.size() + ", " + M.size() );
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
            //System.out.println("k, mt = " + k + ", " + mt);
            if (!mt) {
                F.add(a);
                ix.add(k);
            } else { // drop polynomial and corresponding row and column
                // F.add( a.ring.getZERO() );
                jx.add(k);
            }
            k++;
        }
        if (debug) {
            logger.debug("ix, #M, jx = " + ix + ", " + Mg.size() + ", " + jx);
        }
        int fix = -1; // copied polys
        // copy Mg to Mf as indicated by ix
        for (int i = 0; i < ix.size(); i++) {
            int u = ix.get(i);
            if (u >= flen && fix == -1) {
                fix = Mf.size();
            }
            //System.out.println("copy u, fix = " + u + ", " + fix);
            if (u >= 0) {
                row = Mg.get(u);
                Mf.add(row);
            }
        }
        if (F.size() <= 1 || fix == -1) {
            return new ExtendedGB<C>(null, F, null, Mf);
        }
        // must return, since extended normalform has not correct order of polys
        /*
        G = F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        List<GenPolynomial<C>> temp;
        k = 0;
        final int len = G.size();
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( k >= fix ) { // dont touch copied polys
               row = Mf.get( k );
               //System.out.println("doing k = " + k + ", " + a);
               // must keep order, but removed polys missing
               temp = new ArrayList<GenPolynomial<C>>( len );
               temp.addAll( F );
               temp.add( a.ring.getZERO() ); // ??
               temp.addAll( G );
               //System.out.println("row before = " + row);
               a = red.normalform( row, temp, a );
               //System.out.println("row after  = " + row);
            }
            F.add( a );
            k++;
        }
        // does Mf need renormalization?
        */
        return new ExtendedGB<C>(null, F, null, Mf);
    }


    /**
     * Univariate head term degrees.
     * @param A list of polynomials.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees(List<GenPolynomial<C>> A) {
        List<Long> ud = new ArrayList<Long>();
        if (A == null || A.size() == 0) {
            return ud;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if (pfac.nvar <= 0) {
            return ud;
        }
        //int uht = 0;
        Map<Integer, Long> v = new TreeMap<Integer, Long>(); // for non reduced GBs
        for (GenPolynomial<C> p : A) {
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.length == 1) {
                //uht++;
                Long d = v.get(u[0]);
                if (d == null) {
                    v.put(u[0], e.getVal(u[0]));
                }
            }
        }
        for (int i = 0; i < pfac.nvar; i++) {
            Long d = v.get(i);
            ud.add(d);
        }
        //Collections.reverse(ud);
        return ud;
    }


    /**
     * Construct univariate polynomial of minimal degree in variable i of a zero
     * dimensional ideal(G).
     * @param i variable index.
     * @param G list of polynomials, a monic reduced Gr&ouml;bner base of a zero
     *            dimensional ideal.
     * @return univariate polynomial of minimal degree in variable i in ideal(G)
     */
    public GenPolynomial<C> constructUnivariate(int i, List<GenPolynomial<C>> G) {
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
        GenPolynomialRing<C> pfac = G.get(0).ring;
        RingFactory<C> cfac = pfac.coFac;
        String var = pfac.getVars()[pfac.nvar - 1 - i];
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac, 1, new TermOrder(TermOrder.INVLEX),
                        new String[] { var });

        GenPolynomialRing<C> cpfac = new GenPolynomialRing<C>(cfac, ll, new TermOrder(TermOrder.INVLEX));
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, pfac);
        GenPolynomial<GenPolynomial<C>> P = rfac.getZERO();
        for (int k = 0; k < ll; k++) {
            GenPolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, k);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - k);
            Pp = Pp.multiply(cp);
            P = P.sum(Pp);
        }
        if (debug) {
            logger.info("univariate construction, P = " + P);
            logger.info("univariate construction, deg_*(G) = " + ud);
            //throw new RuntimeException("check");
        }
        GenPolynomial<C> X;
        GenPolynomial<C> XP;
        // solve system of linear equations for the coefficients of the univariate polynomial
        List<GenPolynomial<C>> ls;
        int z = -1;
        do {
            //System.out.println("ll  = " + ll);
            GenPolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, ll);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - ll);
            Pp = Pp.multiply(cp);
            P = P.sum(Pp);
            X = pfac.univariate(i, ll);
            XP = red.normalform(G, X);
            //System.out.println("XP = " + XP);
            GenPolynomial<GenPolynomial<C>> XPp = PolyUtil.<C> toRecursive(rfac, XP);
            GenPolynomial<GenPolynomial<C>> XPs = XPp.sum(P);
            ls = new ArrayList<GenPolynomial<C>>(XPs.getMap().values());
            //System.out.println("ls,1 = " + ls);
            ls = red.irreducibleSet(ls);
            z = commonZeroTest(ls);
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
                rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, pfac);
                P = PolyUtil.<C> extendCoefficients(rfac, P, 0, 0L);
                XPp = PolyUtil.<C> extendCoefficients(rfac, XPp, 0, 1L);
                P = P.sum(XPp);
            }
        } while (z != 0); // && ll <= 5 && !XP.isZERO()
        // construct result polynomial
        GenPolynomial<C> pol = ufac.univariate(0, ll);
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
            GenPolynomial<C> pi = ufac.univariate(0, ll - 1 - vi);
            pi = pi.multiply(tc);
            pol = pol.sum(pi);
        }
        if (logger.isInfoEnabled()) {
            logger.info("univariate construction, pol = " + pol);
        }
        return pol;
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
