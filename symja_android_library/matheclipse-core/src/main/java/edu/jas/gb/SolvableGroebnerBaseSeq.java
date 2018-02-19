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
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;


/**
 * Solvable Groebner bases sequential algorithms. Implements common left, right
 * and twosided Groebner bases and left, right and twosided GB tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableGroebnerBaseSeq<C extends RingElem<C>> extends SolvableGroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SolvableGroebnerBaseSeq() {
        super();
    }


    /**
     * Constructor.
     *
     * @param sred Solvable reduction engine
     */
    public SolvableGroebnerBaseSeq(SolvableReduction<C> sred) {
        super(sred);
    }


    /**
     * Constructor.
     *
     * @param pl pair selection strategy
     */
    public SolvableGroebnerBaseSeq(PairList<C> pl) {
        super(pl);
    }


    /**
     * Constructor.
     *
     * @param sred Solvable reduction engine
     * @param pl   pair selection strategy
     */
    public SolvableGroebnerBaseSeq(SolvableReduction<C> sred, PairList<C> pl) {
        super(sred, pl);
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial<C>> leftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(F);
        G = PolynomialList.castToSolvableList(PolyUtil.<C>monic(PolynomialList.castToList(G)));
        if (G.size() <= 1) {
            return G;
        }
        GenSolvablePolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isField() && ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficients not from a field: " + ring.coFac.toScript());
        }
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.castToList(G));
        logger.info("start " + pairlist);

        GenSolvablePolynomial<C> pi, pj, S, H;
        Pair<C> pair;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }
            pi = (GenSolvablePolynomial<C>) pair.pi;
            pj = (GenSolvablePolynomial<C>) pair.pj;
            if (debug) {
                logger.info("pi    = " + pi.leadingExpVector());
                logger.info("pj    = " + pj.leadingExpVector());
            }

            S = sred.leftSPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(S) = " + S.leadingExpVector());
            }

            H = sred.leftNormalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector());
                //logger.info("ht(H) = " + H.leadingExpVector() + ", lc(H) = " + H.leadingBaseCoefficient().toScript());
            }

            H = H.monic();
            if (H.isONE()) {
                G.clear();
                G.add(H);
                return G; // since no threads are activated
            }
            if (debug) {
                // logger.info("H = " + H);
                logger.info("#monic(H) = " + H.length());
            }
            if (H.length() > 0) {
                //l++;
                G.add(H);
                pairlist.put(H);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = leftMinimalGB(G);
        logger.info("end " + pairlist);
        return G;
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    @Override
    @SuppressWarnings("unchecked")
    public SolvableExtendedGB<C> extLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            throw new IllegalArgumentException("null or empty F not allowed");
        }
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        List<List<GenSolvablePolynomial<C>>> F2G = new ArrayList<List<GenSolvablePolynomial<C>>>();
        List<List<GenSolvablePolynomial<C>>> G2F = new ArrayList<List<GenSolvablePolynomial<C>>>();
        PairList<C> pairlist = null;
        boolean oneInGB = false;
        int len = F.size();

        List<GenSolvablePolynomial<C>> row = null;
        List<GenSolvablePolynomial<C>> rows = null;
        List<GenSolvablePolynomial<C>> rowh = null;
        GenSolvablePolynomialRing<C> ring = null;
        GenSolvablePolynomial<C> p, H;

        int nzlen = 0;
        for (GenSolvablePolynomial<C> f : F) {
            if (f.length() > 0) {
                nzlen++;
            }
            if (ring == null) {
                ring = f.ring;
            }
        }
        GenSolvablePolynomial<C> mone = ring.getONE(); //.negate();
        int k = 0;
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                row = new ArrayList<GenSolvablePolynomial<C>>(nzlen);
                for (int j = 0; j < nzlen; j++) {
                    row.add(null);
                }
                //C c = p.leadingBaseCoefficient();
                //c = c.inverse();
                //p = p.multiply( c );
                row.set(k, mone); //.multiply(c) );
                k++;
                if (p.isUnit()) {
                    G.clear();
                    G.add(p);
                    G2F.clear();
                    G2F.add(row);
                    oneInGB = true;
                    break;
                }
                G.add(p);
                G2F.add(row);
                if (pairlist == null) {
                    //pairlist = new CriticalPairList<C>( modv, p.ring );
                    pairlist = strategy.create(modv, p.ring);
                }
                // putOne not required
                pairlist.put(p);
            } else {
                len--;
            }
        }
        SolvableExtendedGB<C> exgb;
        if (len <= 1 || oneInGB) {
            // adjust F2G
            for (GenSolvablePolynomial<C> f : F) {
                row = new ArrayList<GenSolvablePolynomial<C>>(G.size());
                for (int j = 0; j < G.size(); j++) {
                    row.add(null);
                }
                H = sred.leftNormalform(row, G, f);
                if (!H.isZERO()) {
                    logger.error("nonzero H = " + H);
                }
                F2G.add(row);
            }
            exgb = new SolvableExtendedGB<C>(F, G, F2G, G2F);
            //System.out.println("exgb 1 = " + exgb);
            return exgb;
        }
        logger.info("start " + pairlist);

        Pair<C> pair;
        int i, j;
        GenSolvablePolynomial<C> pi, pj, S, x, y;
        //GenPolynomial<C> z;
        while (pairlist.hasNext() && !oneInGB) {
            pair = pairlist.removeNext();
            if (pair == null) {
                //pairlist.update(); // ?
                continue;
            }
            i = pair.i;
            j = pair.j;
            pi = (GenSolvablePolynomial<C>) pair.pi;
            pj = (GenSolvablePolynomial<C>) pair.pj;
            if (debug) {
                logger.info("i, pi    = " + i + ", " + pi);
                logger.info("j, pj    = " + j + ", " + pj);
            }

            rows = new ArrayList<GenSolvablePolynomial<C>>(G.size());
            for (int m = 0; m < G.size(); m++) {
                rows.add(null);
            }
            S = sred.leftSPolynomial(rows, i, pi, j, pj);
            if (debug) {
                logger.debug("is reduction S = " + sred.isLeftReductionNF(rows, G, ring.getZERO(), S));
            }
            if (S.isZERO()) {
                pair.setZero();
                //pairlist.update( pair, S );
                // do not add to G2F
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            rowh = new ArrayList<GenSolvablePolynomial<C>>(G.size());
            for (int m = 0; m < G.size(); m++) {
                rowh.add(null);
            }
            H = sred.leftNormalform(rowh, G, S);
            if (debug) {
                //System.out.println("H = " + H);
                logger.debug("is reduction H = " + sred.isLeftReductionNF(rowh, G, S, H));
            }
            if (H.isZERO()) {
                pair.setZero();
                //pairlist.update( pair, H );
                // do not add to G2F
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }

            row = new ArrayList<GenSolvablePolynomial<C>>(G.size() + 1);
            for (int m = 0; m < G.size(); m++) {
                x = rows.get(m);
                if (x != null) {
                    //System.out.println("ms = " + m + " " + x);
                    x = (GenSolvablePolynomial<C>) x.negate();
                }
                y = rowh.get(m);
                if (y != null) {
                    y = (GenSolvablePolynomial<C>) y.negate();
                    //System.out.println("mh = " + m + " " + y);
                }
                if (x == null) {
                    x = y;
                } else {
                    x = (GenSolvablePolynomial<C>) x.sum(y);
                }
                //System.out.println("mx = " + m + " " + x);
                row.add(x);
            }
            if (debug) {
                logger.debug("is reduction 0+sum(row,G) == H : "
                        + sred.isLeftReductionNF(row, G, H, ring.getZERO()));
            }
            row.add(null);

            //  H = H.monic();
            C c = H.leadingBaseCoefficient();
            c = c.inverse();
            H = H.multiply(c);
            // 1*c*row, leads to wrong method dispatch:
            row = PolynomialList.<C>castToSolvableList(blas.scalarProduct(mone.multiply(c),
                    PolynomialList.<C>castToList(row)));
            row.set(G.size(), mone);
            if (H.isONE()) {
                // pairlist.record( pair, H );
                // G.clear(); 
                G.add(H);
                G2F.add(row);
                oneInGB = true;
                break;
            }
            if (debug) {
                logger.debug("H = " + H);
            }
            G.add(H);
            //pairlist.update( pair, H );
            pairlist.put(H);
            G2F.add(row);
        }
        if (debug) {
            exgb = new SolvableExtendedGB<C>(F, G, F2G, G2F);
            logger.info("exgb unnorm = " + exgb);
        }
        G2F = normalizeMatrix(F.size(), G2F);
        if (debug) {
            exgb = new SolvableExtendedGB<C>(F, G, F2G, G2F);
            logger.info("exgb nonmin = " + exgb);
            boolean t2 = isLeftReductionMatrix(exgb);
            logger.debug("exgb t2 = " + t2);
        }
        exgb = minimalSolvableExtendedGB(F.size(), G, G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        logger.debug("#sequential list = " + G.size());
        logger.info("end " + pairlist);
        // setup matrices F and F2G
        for (GenSolvablePolynomial<C> f : F) {
            row = new ArrayList<GenSolvablePolynomial<C>>(G.size());
            for (int m = 0; m < G.size(); m++) {
                row.add(null);
            }
            H = sred.leftNormalform(row, G, f);
            if (!H.isZERO()) {
                logger.error("nonzero H = " + H);
            }
            F2G.add(row);
        }
        logger.info("extGB end");
        return new SolvableExtendedGB<C>(F, G, F2G, G2F);
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param modv number of module variables.
     * @param Fp   solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial<C>> twosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {
        List<GenSolvablePolynomial<C>> F = normalizeZerosOnes(Fp);
        F = PolynomialList.castToSolvableList(PolyUtil.<C>monic(PolynomialList.castToList(F)));
        if (F.size() < 1) { // 0 not 1
            return F;
        }
        if (F.size() == 1 && F.get(0).isONE()) {
            return F;
        }
        GenSolvablePolynomialRing<C> ring = F.get(0).ring;
        if (!ring.coFac.isField() && ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        // add also coefficient generators
        List<GenSolvablePolynomial<C>> X;
        X = PolynomialList.castToSolvableList(ring.generators(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>(F.size() * (1 + X.size()));
        G.addAll(F);
        logger.info("right multipy: G = " + G);
        GenSolvablePolynomial<C> p, q;
        for (int i = 0; i < G.size(); i++) { // G changes
            p = G.get(i);
            for (GenSolvablePolynomial<C> x : X) {
                //x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                q = p.multiply(x);
                logger.info("right multipy: p = " + p + ", x = " + x + ", q = " + q);
                q = sred.leftNormalform(G, q);
                q = q.monic();
                logger.info("right multipy: red(q) = " + q);
                if (!q.isZERO()) {
                    //System.out.println("q generating: = " + q + ", p = " + p + ", x = " + x);
                    if (q.isONE()) {
                        //System.out.println("G generated so far: " + G);
                        G.clear();
                        G.add(q);
                        return G; // since no threads are activated
                    }
                    if (!G.contains(q)) { // why?
                        G.add(q);
                    } else {
                        logger.info("right multipy contained: q = " + q);
                    }
                }
            }
        }
        if (G.size() <= 1) { // 1 ok
            return G; // since no threads are activated
        }
        //System.out.println("G generated = " + G);
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.castToList(G));
        logger.info("twosided start " + pairlist);

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

            H = H.monic();
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
                //System.out.println("H generated = " + H);
                for (GenSolvablePolynomial<C> x : X) {
                    //x = X.get(j);
                    if (x.isONE()) {
                        continue;
                    }
                    q = H.multiply(x);
                    p = sred.leftNormalform(G, q);
                    if (!p.isZERO()) {
                        //System.out.println("p generated = " + p + ", x = " + x);
                        p = p.monic();
                        if (p.isONE()) {
                            G.clear();
                            G.add(p);
                            return G; // since no threads are activated
                        }
                        G.add(p);
                        pairlist.put(p);
                    }
                }
                //System.out.println("G generated = " + G);
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = leftMinimalGB(G);
        logger.info("twosided end " + pairlist);
        return G;
    }


    /**
     * Normalize M. Make all rows the same size and make certain column elements
     * zero.
     *
     * @param M a reduction matrix.
     * @return normalized M.
     */
    public List<List<GenSolvablePolynomial<C>>> normalizeMatrix(int flen,
                                                                List<List<GenSolvablePolynomial<C>>> M) {
        if (M == null) {
            return M;
        }
        if (M.size() == 0) {
            return M;
        }
        List<List<GenSolvablePolynomial<C>>> N = new ArrayList<List<GenSolvablePolynomial<C>>>();
        List<List<GenSolvablePolynomial<C>>> K = new ArrayList<List<GenSolvablePolynomial<C>>>();
        int len = M.get(M.size() - 1).size(); // longest row
        // pad / extend rows
        for (List<GenSolvablePolynomial<C>> row : M) {
            List<GenSolvablePolynomial<C>> nrow = new ArrayList<GenSolvablePolynomial<C>>(row);
            for (int i = row.size(); i < len; i++) {
                nrow.add(null);
            }
            N.add(nrow);
        }
        // System.out.println("norm N fill = " + N);
        // make zero columns
        int k = flen;
        for (int i = 0; i < N.size(); i++) { // 0
            List<GenSolvablePolynomial<C>> row = N.get(i);
            if (debug) {
                logger.info("row = " + row);
            }
            K.add(row);
            if (i < flen) { // skip identity part
                continue;
            }
            List<GenSolvablePolynomial<C>> xrow;
            GenSolvablePolynomial<C> a;
            //System.out.println("norm i = " + i);
            for (int j = i + 1; j < N.size(); j++) {
                List<GenSolvablePolynomial<C>> nrow = N.get(j);
                //System.out.println("nrow j = " +j + ", " + nrow);
                if (k < nrow.size()) { // always true
                    a = nrow.get(k);
                    //System.out.println("k, a = " + k + ", " + a);
                    if (a != null && !a.isZERO()) { // a*row + nrow, leads to wrong method dispatch
                        List<GenPolynomial<C>> yrow = blas.scalarProduct(a,
                                PolynomialList.<C>castToList(row));
                        yrow = blas.vectorAdd(yrow, PolynomialList.<C>castToList(nrow));
                        xrow = PolynomialList.<C>castToSolvableList(yrow);
                        N.set(j, xrow);
                    }
                }
            }
            k++;
        }
        //System.out.println("norm K reduc = " + K);
        // truncate 
        N.clear();
        for (List<GenSolvablePolynomial<C>> row : K) {
            List<GenSolvablePolynomial<C>> tr = new ArrayList<GenSolvablePolynomial<C>>();
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
     * Test if M is a left reduction matrix.
     *
     * @param exgb an SolvableExtendedGB container.
     * @return true, if exgb contains a left reduction matrix, else false.
     */
    @Override
    public boolean isLeftReductionMatrix(SolvableExtendedGB<C> exgb) {
        if (exgb == null) {
            return true;
        }
        return isLeftReductionMatrix(exgb.F, exgb.G, exgb.F2G, exgb.G2F);
    }


    /**
     * Minimal solvable extended groebner basis.
     *
     * @param Gp a left Groebner base.
     * @param M  a left reduction matrix, is modified.
     * @return a (partially) reduced left Groebner base of Gp in a container.
     */
    public SolvableExtendedGB<C> minimalSolvableExtendedGB(int flen, List<GenSolvablePolynomial<C>> Gp,
                                                           List<List<GenSolvablePolynomial<C>>> M) {
        if (Gp == null) {
            return null; //new SolvableExtendedGB<C>(null,Gp,null,M);
        }
        if (Gp.size() <= 1) {
            return new SolvableExtendedGB<C>(null, Gp, null, M);
        }
        List<GenSolvablePolynomial<C>> G;
        List<GenSolvablePolynomial<C>> F;
        G = new ArrayList<GenSolvablePolynomial<C>>(Gp);
        F = new ArrayList<GenSolvablePolynomial<C>>(Gp.size());

        List<List<GenSolvablePolynomial<C>>> Mg;
        List<List<GenSolvablePolynomial<C>>> Mf;
        Mg = new ArrayList<List<GenSolvablePolynomial<C>>>(M.size());
        Mf = new ArrayList<List<GenSolvablePolynomial<C>>>(M.size());
        List<GenSolvablePolynomial<C>> row;
        for (List<GenSolvablePolynomial<C>> r : M) {
            // must be copied also
            row = new ArrayList<GenSolvablePolynomial<C>>(r);
            Mg.add(row);
        }
        row = null;

        GenSolvablePolynomial<C> a;
        ExpVector e;
        ExpVector f;
        GenSolvablePolynomial<C> p;
        boolean mt;
        ListIterator<GenSolvablePolynomial<C>> it;
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
            return new SolvableExtendedGB<C>(null, F, null, Mf);
        }
        // must return, since extended normalform has not correct order of polys
        /*
        G = F;
        F = new ArrayList<GenSolvablePolynomial<C>>( G.size() );
        List<GenSolvablePolynomial<C>> temp;
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
               a = sred.leftNormalform( row, temp, a );
               //System.out.println("row after  = " + row);
            }
            F.add( a );
            k++;
        }
        // does Mf need renormalization?
        */
        return new SolvableExtendedGB<C>(null, F, null, Mf);
    }


    /**
     * Right Groebner base via right reduction using pairlist
     * class. Overides rightGB() via opposite ring.
     *
     * @param modv number of module variables.
     * @param F    solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial<C>> rightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        List<GenSolvablePolynomial<C>> G = normalizeZerosOnes(F);
        G = PolynomialList.castToSolvableList(PolyUtil.<C>monic(PolynomialList.castToList(G)));
        if (G.size() <= 1) {
            return G;
        }
        GenSolvablePolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isField() && ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(PolynomialList.castToList(G));
        logger.info("start " + pairlist);

        GenSolvablePolynomial<C> pi, pj, S, H;
        Pair<C> pair;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }
            pi = (GenSolvablePolynomial<C>) pair.pi;
            pj = (GenSolvablePolynomial<C>) pair.pj;
            if (debug) {
                logger.info("pi    = " + pi);
                logger.info("pj    = " + pj);
            }

            S = sred.rightSPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(S) = " + S.leadingExpVector());
            }

            H = sred.rightNormalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (debug) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }

            H = H.monic();
            if (H.isONE()) {
                G.clear();
                G.add(H);
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
        G = rightMinimalGB(G);
        logger.info("end " + pairlist);
        return G;
    }

}
