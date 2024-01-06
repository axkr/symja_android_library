/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * D-Groebner Base sequential algorithm. Implements D-Groebner bases and GB
 * test. <b>Note:</b> Minimal reduced GBs are not unique. see BWK, section 10.1.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class DGroebnerBaseSeq<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = LogManager.getLogger(DGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected DReduction<C> dred; // shadow super.red ??


    /**
     * Constructor.
     */
    public DGroebnerBaseSeq() {
        this(new DReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param dred D-Reduction engine
     */
    public DGroebnerBaseSeq(DReduction<C> dred) {
        super(dred);
        this.dred = dred;
        assert this.dred == super.red;
    }


    /**
     * D-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {
        GenPolynomial<C> pi, pj, s, d;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!dred.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                d = dred.GPolynomial(pi, pj);
                if (!d.isZERO()) {
                    // better check for top reduction only
                    d = dred.normalform(F, d);
                }
                if (!d.isZERO()) {
                    System.out.println("d-pol(" + pi + "," + pj + ") != 0: " + d);
                    return false;
                }
                // works ok
                if (!dred.criterion4(pi, pj)) {
                    continue;
                }
                s = dred.SPolynomial(pi, pj);
                if (!s.isZERO()) {
                    s = dred.normalform(F, s);
                }
                if (!s.isZERO()) {
                    System.out.println("s-pol(" + i + "," + j + ") != 0: " + s);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * D-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a D-Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        if (G.size() <= 1) {
            return G;
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        OrderedDPairlist<C> pairlist = new OrderedDPairlist<C>(modv, ring);
        pairlist.put(G);

        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, D, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //System.out.println("pair = " + pair);
            if (pair == null)
                continue;
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.debug("pi    = {}", pi);
                logger.debug("pj    = {}", pj);
            }
            H = null;

            // D-polynomial case ----------------------
            D = dred.GPolynomial(pi, pj);
            //System.out.println("D_d = " + D);
            if (!D.isZERO() && !dred.isTopReducible(G, D)) {
                H = dred.normalform(G, D);
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                if (!H.isZERO()) {
                    logger.info("Dred = {}", H);
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
            }

            // S-polynomial case -----------------------
            if (pair.getUseCriterion3() && pair.getUseCriterion4()) {
                S = dred.SPolynomial(pi, pj);
                //System.out.println("S_d = " + S);
                if (S.isZERO()) {
                    pair.setZero();
                    //continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ht(S) = {}", S.leadingExpVector());
                }
                H = dred.normalform(G, S);
                if (H.isZERO()) {
                    pair.setZero();
                    //continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ht(H) = {}", H.leadingExpVector());
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                logger.debug("H = {}", H);
                if (!H.isZERO()) {
                    logger.info("Sred = {}", H);
                    //len = G.size();
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
            }
        }
        logger.debug("#sequential list = {}", G.size());
        G = minimalGB(G);
        logger.info("{}", pairlist);
        return G;
    }


    /**
     * Extended Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    @Override
    public ExtendedGB<C> extGB(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            throw new IllegalArgumentException("null or empty F not allowed");
        }
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<List<GenPolynomial<C>>> F2G = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> G2F = new ArrayList<List<GenPolynomial<C>>>();
        OrderedDPairlist<C> pairlist = null;
        boolean oneInGB = false;
        int len = F.size();

        List<GenPolynomial<C>> row = null;
        List<GenPolynomial<C>> rows = null;
        List<GenPolynomial<C>> rowh = null;
        GenPolynomialRing<C> ring = null;
        GenPolynomial<C> H;
        GenPolynomial<C> p;

        int nzlen = 0;
        for (GenPolynomial<C> f : F) {
            if (f.length() > 0) {
                nzlen++;
            }
            if (ring == null) {
                ring = f.ring;
            }
        }
        GenPolynomial<C> one = ring.getONE();
        int k = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                row = blas.genVector(nzlen, null);
                row.set(k, one);
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
                //logger.info("p row = {}", row);
                G2F.add(row);
                if (pairlist == null) {
                    //pairlist = strategy.create(modv, p.ring);
                    pairlist = new OrderedDPairlist<C>(modv, p.ring);
                    if (p.ring.coFac.isField()) {
                        //throw new RuntimeException("coefficients from a field");
                        logger.warn("coefficients from a field " + p.ring.coFac);
                    }
                }
                // putOne not required
                pairlist.put(p);
            } else {
                len--;
            }
        }
        ExtendedGB<C> exgb;
        if (len <= 1 || oneInGB) {
            // adjust F2G
            for (GenPolynomial<C> f : F) {
                row = blas.genVector(G.size(), null);
                H = dred.normalform(row, G, f);
                if (!H.isZERO()) {
                    logger.error("nonzero H = {}", H);
                }
                logger.info("f row = {}", row);
                F2G.add(row);
            }
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            //System.out.println("exgb #1 = " + exgb);
            return exgb;
        }

        Pair<C> pair;
        int i, j;
        GenPolynomial<C> pi, pj;
        GenPolynomial<C> S, D;
        k = 0;
        while (pairlist.hasNext() && !oneInGB) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }
            i = pair.i;
            j = pair.j;
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.info("i, pi    = {}, {}", i, pi);
                logger.info("j, pj    = {}, {}", j, pj);
            }
            H = null;

            // D-polynomial case ----------------------
            rows = blas.genVector(G.size() + 1, null);
            D = dred.GPolynomial(rows, i, pi, j, pj);
            logger.info("Gpol = {}", D);
            if (debug) {
                logger.info("is reduction D = " + dred.isReductionNF(rows, G, D, ring.getZERO()));
            }
            if (!D.isZERO()) { //&& !dred.isTopReducible(G, D)
                //continue;
                rowh = blas.genVector(G.size() + 1, null);
                H = dred.normalform(rowh, G, D);
                if (debug) {
                    logger.info("is reduction H = " + dred.isReductionNF(rowh, G, D, H));
                }
                //H = H.monic();
                int s = H.leadingBaseCoefficient().signum();
                if (s < 0) {
                    logger.info("negate: H_D rowd, rowh = {}, {}", rows, rowh);
                    H = H.negate();
                    rows = blas.vectorNegate(rows);
                    rowh = blas.vectorNegate(rowh);
                }
                if (H.isONE()) {
                    // G.clear();
                    G.add(H);
                    oneInGB = true;
                } else if (!H.isZERO()) {
                    logger.info("H_G red = {}", H);
                    G.add(H);
                    pairlist.put(H);
                }
                //System.out.println("rowd = " + rows);
                //System.out.println("rowh = " + rowh);
                row = blas.vectorCombineRep(rows, rowh);
                logger.debug("H_G row = {}", row);
                if (!H.isZERO()) {
                    G2F.add(row);
                }
                if (debug) {
                    logger.debug("ht(H) = {}", H.leadingExpVector());
                    logger.info("is reduction D,H = " + dred.isReductionNF(row, G, H, ring.getZERO()));
                }
            }

            // S-polynomial case ----------------------
            rows = blas.genVector(G.size() + 1, null);
            S = dred.SPolynomial(rows, i, pi, j, pj);
            logger.info("Spol = {}", S);
            if (debug) {
                logger.info("is reduction S = " + dred.isReductionNF(rows, G, S, ring.getZERO()));
            }
            rowh = blas.genVector(G.size() + 1, null);
            if (!S.isZERO()) { //&& !dred.isTopReducible(G, S)
                //continue;
                H = dred.normalform(rowh, G, S);
                if (debug) {
                    logger.info("Spol_red = {}", H);
                    logger.info("is reduction H = " + dred.isReductionNF(rowh, G, S, H));
                }
                //H = H.monic();
                int s = H.leadingBaseCoefficient().signum();
                if (s < 0) {
                    logger.info("negate: H_S rows, rowh = {}, {}", rows, rowh);
                    H = H.negate();
                    //rowh = rowh.negate(); //rowh.set(G.size(), one.negate());
                    rows = blas.vectorNegate(rows);
                    rowh = blas.vectorNegate(rowh);
                }
                //logger.info("Spol_red_norm = {}", H);
                if (H.isONE()) {
                    //G.clear();
                    G.add(H);
                    oneInGB = true;
                } else if (!H.isZERO()) {
                    logger.info("H_S red = {}", H);
                    G.add(H);
                    pairlist.put(H);
                }
                //System.out.println("rows = " + rows);
                //System.out.println("rowh = " + rowh);
                row = blas.vectorCombineRep(rows, rowh);
                logger.debug("H_S row = {}", row);
                if (!H.isZERO()) {
                    G2F.add(row);
                }
                if (debug) {
                    logger.debug("ht(H) = {}", H.leadingExpVector());
                    logger.info("is reduction S,H = " + dred.isReductionNF(row, G, H, ring.getZERO()));
                }
            }
        }
        if (true || debug) {
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            boolean t = isReductionMatrix(exgb);
            if (!t) {
                logger.info("exgb unnorm = {}", exgb);
                logger.info("exgb t_1 = {}", t);
            }
        }
        G2F = normalizeMatrix(F.size(), G2F);
        if (debug) {
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            boolean t = isReductionMatrix(exgb);
            if (!t) {
                logger.info("exgb norm nonmin = {}", exgb);
                logger.info("exgb t_2 = {}", t);
            }
        }
        exgb = minimalExtendedGB(F.size(), G, G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        if (true || debug) {
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            boolean t = isMinReductionMatrix(exgb);
            if (!t) {
                logger.info("exgb minGB = {}", exgb);
                logger.info("exgb t_3 = {}", t);
            }
        }
        exgb = new ExtendedGB<C>(F, G, F2G, G2F);
        // setup matrices F and F2G
        for (GenPolynomial<C> f : F) {
            row = blas.genVector(G.size() + 1, null);
            H = dred.normalform(row, G, f);
            if (!H.isZERO()) {
                logger.error("nonzero H, G = {}, {}", H, G);
                throw new RuntimeException("H != 0");
            }
            F2G.add(row);
        }
        exgb = new ExtendedGB<C>(F, G, F2G, G2F);
        if (debug) {
            boolean t = isMinReductionMatrix(exgb);
            if (!t) {
                logger.info("exgb +F+F2G = {}", exgb);
                logger.info("exgb t_4 = {}", t);
            }
        }
        return exgb;
    }


    /**
     * Minimal extended groebner basis.
     * @param flen length of rows.
     * @param Gp a Groebner base.
     * @param M a reduction matrix.
     * @return a (partially) reduced Groebner base of Gp in a (fake) container.
     */
    @Override
    public ExtendedGB<C> minimalExtendedGB(int flen, List<GenPolynomial<C>> Gp,
                    List<List<GenPolynomial<C>>> M) {
        if (Gp == null) {
            return null; //new ExtendedGB<C>(null,Gp,null,M);
        }
        List<GenPolynomial<C>> G, F, T;
        G = new ArrayList<GenPolynomial<C>>(Gp);
        F = new ArrayList<GenPolynomial<C>>(Gp.size());

        List<List<GenPolynomial<C>>> Mg, Mf;
        Mg = new ArrayList<List<GenPolynomial<C>>>(M.size());
        Mf = new ArrayList<List<GenPolynomial<C>>>(M.size());
        for (List<GenPolynomial<C>> r : M) {
            // must be copied also
            List<GenPolynomial<C>> row = new ArrayList<GenPolynomial<C>>(r);
            Mg.add(row);
        }

        boolean mt;
        ListIterator<GenPolynomial<C>> it;
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> jx = new ArrayList<Integer>();
        int k = 0;
        //System.out.println("flen, Gp, M = " + flen + ", " + Gp.size() + ", " + M.size() );
        GenPolynomialRing<C> pfac = null;
        GenPolynomial<C> pi, pj;
        ExpVector ei, ej;
        C ai, aj, r;
        while (G.size() > 0) {
            pi = G.remove(0);
            ei = pi.leadingExpVector();
            ai = pi.leadingBaseCoefficient();
            if (pfac == null) {
                pfac = pi.ring;
            }

            it = G.listIterator();
            mt = false;
            while (it.hasNext() && !mt) {
                pj = it.next();
                ej = pj.leadingExpVector();
                mt = ei.multipleOf(ej);
                if (mt) {
                    aj = pj.leadingBaseCoefficient();
                    r = ai.remainder(aj);
                    mt = r.isZERO(); // && mt
                }
            }
            it = F.listIterator();
            while (it.hasNext() && !mt) {
                pj = it.next();
                ej = pj.leadingExpVector();
                mt = ei.multipleOf(ej);
                if (mt) {
                    aj = pj.leadingBaseCoefficient();
                    r = ai.remainder(aj);
                    mt = r.isZERO(); // && mt
                }
            }
            T = new ArrayList<GenPolynomial<C>>(G);
            T.addAll(F);
            //GenPolynomial<C> t = dred.normalform(T, pi);
            //System.out.println("t, mt, t==0: " + t + ", " + mt + ", " + t.isZERO());
            if (!mt) {
                F.add(pi);
                ix.add(k);
                //System.out.println("ix: " + ix);
            } else { // drop polynomial and corresponding row and column ??
                jx.add(k);
                //System.out.println("jx: " + jx);
            }
            k++;
        }
        logger.info("ix, jx, #M = {}, {}, {}", ix, jx, Mg.size());
        int fix = -1; // copied polys
        // copy Mg to Mf as indicated by ix
        for (int i = 0; i < ix.size(); i++) {
            int u = ix.get(i);
            if (u >= flen && fix == -1) {
                fix = Mf.size();
            }
            //System.out.println("copy u_ix, fix = " + u + ", " + fix);
            if (u >= 0) {
                List<GenPolynomial<C>> row = Mg.get(u);
                Mf.add(row);
            }
        }
        // for (int i = 0; i < jx.size(); i++) {
        //     int u = jx.get(i);
        //     //System.out.println("copy u_jx = " + u);
        //     if (u >= 0) {
        //         List<GenPolynomial<C>> row = blas.genVector(flen,null);
        //         Mf.add(u, row);
        //         F.add(u, pfac.getZERO());
        //     }
        // }
        if (F.size() <= 1 || fix == -1) {
            return new ExtendedGB<C>(null, F, null, Mf);
        }
        // must return, since extended normalform has not correct order of polys
        return new ExtendedGB<C>(null, F, null, Mf);
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
    }


    /**
     * Inverse for element modulo ideal.
     * @param h polynomial
     * @param F polynomial list
     * @return inverse of h with respect to ideal(F), if defined
     */
    public GenPolynomial<C> inverse(GenPolynomial<C> h, List<GenPolynomial<C>> F) {
        if (h == null || h.isZERO()) {
            throw new NotInvertibleException("zero not invertible");
        }
        if (F == null || F.size() == 0) {
            throw new NotInvertibleException("zero ideal");
        }
        if (h.isUnit()) {
            return h.inverse();
        }
        // prepare by GB precomputation
        List<GenPolynomial<C>> G = GB(F);
        List<GenPolynomial<C>> I = new ArrayList<GenPolynomial<C>>(1 + G.size());
        I.add(h);
        I.addAll(G);
        // now compute extended gcd(h,G)
        ExtendedGB<C> X = extGB(I);
        List<GenPolynomial<C>> hG = X.G;
        //System.out.println("hG = " + hG);
        GenPolynomial<C> one = null;
        int i = -1;
        for (GenPolynomial<C> p : hG) {
            i++;
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                one = p;
                break;
            }
        }
        if (one == null) {
            throw new NotInvertibleException("h = " + h);
        }
        List<GenPolynomial<C>> row = X.G2F.get(i); // != -1
        GenPolynomial<C> g = row.get(0);
        if (g == null || g.isZERO()) {
            throw new NotInvertibleException("h = " + h);
        }
        // adjust g to get g*h == 1 mod ideal(G)
        GenPolynomial<C> f = g.multiply(h);
        GenPolynomial<C> k = red.normalform(G, f);
        //System.out.println("g = " + g + ", h = " + h + ", f = " + f + ", k = " + k);
        if (k.signum() < 0) { // then is -1 or inv-G(0)
            //System.out.println("k < 0: " + G);
            g = g.sum(G.get(0)); //.negate();
        }
        return g;
    }

}
