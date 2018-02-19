/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.jas.gb.SolvableExtendedGB;
import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Syzygy sequential class for solvable polynomials. Implements Syzygy
 * computations and tests with Groebner bases.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableSyzygySeq<C extends GcdRingElem<C>> extends SolvableSyzygyAbstract<C> {


    private static final Logger logger = Logger.getLogger(SolvableSyzygySeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    private static boolean assertEnabled = false;


    static {
        assert assertEnabled = true; // official hack to check assertions enabled
    }


    /**
     * Groebner basis engine.
     */
    protected SolvableGroebnerBase<C> sbb;


    /*
     * Module Groebner basis engine.
    //protected ModSolvableGroebnerBase<C> msbb;
     */


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public SolvableSyzygySeq(RingFactory<C> cf) {
        sbb = SGBFactory.getImplementation(cf);
        //msbb = new ModSolvableGroebnerBaseSeq<C>(cf);
    }


    /**
     * Resolution of a module. Only with direct GBs.
     *
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> resolution(ModuleList<C> M) {
        List<SolvResPart<C>> R = new ArrayList<SolvResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM;
        ModuleList<C> Z;
        while (true) {
            GM = sbb.leftGB(MM);
            Z = leftZeroRelations(GM);
            R.add(new SolvResPart<C>(MM, GM, Z));
            if (Z == null || Z.list == null || Z.list.size() == 0) {
                break;
            }
            MM = Z;
        }
        return R;
    }


    /**
     * Resolution of a polynomial list. Only with direct GBs.
     *
     * @param F a polynomial list of a Groebner basis.
     * @return a resolution of F.
     */
    @SuppressWarnings("unchecked")
    public List/*<SolvResPart<C>|SolvResPolPart<C>>*/resolution(PolynomialList<C> F) {
        List<List<GenSolvablePolynomial<C>>> Z;
        ModuleList<C> Zm;
        List<GenSolvablePolynomial<C>> G;
        PolynomialList<C> Gl;

        G = sbb.leftGB(F.castToSolvableList());
        Z = leftZeroRelations(G);
        Gl = new PolynomialList<C>((GenSolvablePolynomialRing<C>) F.ring, G);
        Zm = new ModuleList<C>((GenSolvablePolynomialRing<C>) F.ring, Z);

        List R = resolution(Zm);
        R.add(0, new SolvResPolPart<C>(F, Gl, Zm));
        return R;
    }


    /**
     * Resolution of a module.
     *
     * @param M a module list of an arbitrary basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> resolutionArbitrary(ModuleList<C> M) {
        List<SolvResPart<C>> R = new ArrayList<SolvResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM = null;
        ModuleList<C> Z;
        while (true) {
            //GM = sbb.leftGB(MM);
            Z = leftZeroRelationsArbitrary(MM);
            R.add(new SolvResPart<C>(MM, GM, Z));
            if (Z == null || Z.list == null || Z.list.size() == 0) {
                break;
            }
            MM = Z;
        }
        return R;
    }


    /**
     * Resolution of a polynomial list.
     *
     * @param F a polynomial list of an arbitrary basis.
     * @return a resolution of F.
     */
    @SuppressWarnings("unchecked")
    public List/*<SolvResPart<C>|SolvResPolPart<C>>*/resolutionArbitrary(PolynomialList<C> F) {
        List<List<GenSolvablePolynomial<C>>> Z;
        ModuleList<C> Zm;
        PolynomialList<C> Gl = null;
        //List<GenSolvablePolynomial<C>> G = sbb.leftGB( F.castToSolvableList() );
        //Gl = new PolynomialList<C>((GenSolvablePolynomialRing<C>)F.ring, G);
        Z = leftZeroRelationsArbitrary(F.castToSolvableList());
        Zm = new ModuleList<C>((GenSolvablePolynomialRing<C>) F.ring, Z);

        List R = resolutionArbitrary(Zm);
        R.add(0, new SolvResPolPart<C>(F, Gl, Zm));
        return R;
    }


    /**
     * Left syzygy module from arbitrary base.
     *
     * @param modv number of module variables.
     * @param F    a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    @SuppressWarnings("unchecked")
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelationsArbitrary(int modv,
                                                                           List<GenSolvablePolynomial<C>> F) {
        if (F == null) {
            return null; //leftZeroRelations( modv, F );
        }
        if (F.size() <= 1) {
            return leftZeroRelations(modv, F);
        }
        final int lenf = F.size();
        SolvableExtendedGB<C> exgb = sbb.extLeftGB(modv, F);
        if (debug) {
            logger.info("exgb = " + exgb);
        }
        if (assertEnabled) {
            logger.info("check1 exgb start");
            if (!sbb.isLeftReductionMatrix(exgb)) {
                logger.error("is reduction matrix ? false");
            }
            logger.info("check1 exgb end");
        }
        List<GenSolvablePolynomial<C>> G = exgb.G;
        List<List<GenSolvablePolynomial<C>>> G2F = exgb.G2F;
        List<List<GenSolvablePolynomial<C>>> F2G = exgb.F2G;

        List<List<GenSolvablePolynomial<C>>> sg = leftZeroRelations(modv, G);
        GenSolvablePolynomialRing<C> ring = G.get(0).ring;
        ModuleList<C> S = new ModuleList<C>(ring, sg);
        if (debug) {
            logger.info("syz = " + S);
        }
        if (assertEnabled) {
            logger.info("check2 left syz start");
            if (!isLeftZeroRelation(sg, G)) {
                logger.error("is syzygy ? false");
            }
            logger.info("check2 left syz end");
        }

        List<List<GenSolvablePolynomial<C>>> sf;
        sf = new ArrayList<List<GenSolvablePolynomial<C>>>(sg.size());
        //List<GenPolynomial<C>> row;

        for (List<GenSolvablePolynomial<C>> r : sg) {
            Iterator<GenSolvablePolynomial<C>> it = r.iterator();
            Iterator<List<GenSolvablePolynomial<C>>> jt = G2F.iterator();

            List<GenSolvablePolynomial<C>> rf;
            rf = new ArrayList<GenSolvablePolynomial<C>>(lenf);
            for (int m = 0; m < lenf; m++) {
                rf.add(ring.getZERO());
            }
            while (it.hasNext() && jt.hasNext()) {
                GenSolvablePolynomial<C> si = it.next();
                List<GenSolvablePolynomial<C>> ai = jt.next();
                //System.out.println("si = " + si);
                //System.out.println("ai = " + ai);
                if (si == null || ai == null) {
                    continue;
                }
                // pi has wrong type:
                List<GenPolynomial<C>> pi = blas.scalarProduct(si, PolynomialList.<C>castToList(ai));
                //System.out.println("pi = " + pi);
                rf = PolynomialList.<C>castToSolvableList(blas.vectorAdd(PolynomialList.<C>castToList(rf),
                        pi));
            }
            if (it.hasNext() || jt.hasNext()) {
                logger.error("leftZeroRelationsArbitrary wrong sizes");
            }
            //System.out.println("\nrf = " + rf + "\n");
            sf.add(rf);
        }
        if (assertEnabled) {
            logger.info("check3 left syz start");
            if (!isLeftZeroRelation(sf, F)) {
                logger.error("is partial syz sf ? false");
            }
            logger.info("check3 left syz end");
        }

        List<List<GenSolvablePolynomial<C>>> M;
        M = new ArrayList<List<GenSolvablePolynomial<C>>>(lenf);
        for (List<GenSolvablePolynomial<C>> r : F2G) {
            Iterator<GenSolvablePolynomial<C>> it = r.iterator();
            Iterator<List<GenSolvablePolynomial<C>>> jt = G2F.iterator();

            List<GenSolvablePolynomial<C>> rf;
            rf = new ArrayList<GenSolvablePolynomial<C>>(lenf);
            for (int m = 0; m < lenf; m++) {
                rf.add(ring.getZERO());
            }
            while (it.hasNext() && jt.hasNext()) {
                GenSolvablePolynomial<C> si = it.next();
                List<GenSolvablePolynomial<C>> ai = jt.next();
                //System.out.println("si = " + si);
                //System.out.println("ai = " + ai);
                if (si == null || ai == null) {
                    continue;
                }
                //pi has wrong type, should be: List<GenSolvablePolynomial<C>>
                List<GenPolynomial<C>> pi = blas.scalarProduct(si, PolynomialList.<C>castToList(ai));
                //System.out.println("pi = " + pi);
                rf = PolynomialList.<C>castToSolvableList(blas.vectorAdd(PolynomialList.<C>castToList(rf),
                        pi));
            }
            if (it.hasNext() || jt.hasNext()) {
                logger.error("zeroRelationsArbitrary wrong sizes");
            }
            //System.out.println("\nMg Mf = " + rf + "\n");
            M.add(rf);
        }
        //ModuleList<C> ML = new ModuleList<C>( ring, M );
        //System.out.println("syz ML = " + ML);
        // debug only:
        //List<GenSolvablePolynomial<C>> F2 = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        /* not true in general
        List<GenPolynomial<C>> Fp = PolynomialList.<C>castToList(F);
        for ( List<GenSolvablePolynomial<C>> rr: M ) {
            GenSolvablePolynomial<C> rrg = PolynomialList.<C>castToSolvableList(blas.scalarProduct(Fp,PolynomialList.<C>castToList(rr)));
            F2.add( rrg );
        }
        PolynomialList<C> pF = new PolynomialList<C>( ring, F );
        PolynomialList<C> pF2 = new PolynomialList<C>( ring, F2 );
        if ( ! pF.equals( pF2 ) ) {
           logger.error("is FAB = F ? false");
           //System.out.println("pF  = " + pF.list.size());
           //System.out.println("pF2 = " + pF2.list.size());
        }
        */
        int sflen = sf.size();
        List<List<GenSolvablePolynomial<C>>> M2;
        M2 = new ArrayList<List<GenSolvablePolynomial<C>>>(lenf);
        int i = 0;
        for (List<GenSolvablePolynomial<C>> ri : M) {
            List<GenSolvablePolynomial<C>> r2i;
            r2i = new ArrayList<GenSolvablePolynomial<C>>(ri.size());
            int j = 0;
            for (GenSolvablePolynomial<C> rij : ri) {
                GenSolvablePolynomial<C> p = null;
                if (i == j) {
                    p = (GenSolvablePolynomial<C>) ring.getONE().subtract(rij);
                } else {
                    if (rij != null) {
                        p = (GenSolvablePolynomial<C>) rij.negate();
                    }
                }
                r2i.add(p);
                j++;
            }
            M2.add(r2i);
            if (!blas.isZero(PolynomialList.<C>castToList(r2i))) {
                sf.add(r2i);
            }
            i++;
        }
        ModuleList<C> M2L = new ModuleList<C>(ring, M2);
        if (debug) {
            logger.debug("syz M2L = " + M2L);
        }

        if (debug) {
            ModuleList<C> SF = new ModuleList<C>(ring, sf);
            logger.debug("syz sf = " + SF);
            logger.debug("#syz " + sflen + ", " + sf.size());
        }
        if (assertEnabled) {
            logger.info("check4 left syz start");
            if (!isLeftZeroRelation(sf, F)) {
                logger.error("is syz sf ? false");
            }
            logger.info("check4 left syz end");
        }
        return sf;
    }


    /**
     * Left Ore condition. Generators for the left Ore condition of two solvable
     * polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with p*a = q*b
     */
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C>[] leftOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        GenSolvablePolynomialRing<C> pfac = a.ring;
        GenSolvablePolynomial<C>[] oc = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        if (a.equals(b)) {
            oc[0] = pfac.getONE();
            oc[1] = pfac.getONE();
            return oc;
        }
        if (a.isConstant()) {
            if (pfac.coFac.isCommutative()) { // ??
                oc[0] = b;
                oc[1] = a;
                return oc;
            }
            oc[1] = pfac.getONE();
            C c = a.leadingBaseCoefficient().inverse();
            oc[0] = b.multiply(c);
            return oc;
        }
        if (b.isConstant()) {
            if (pfac.coFac.isCommutative()) { // ??
                oc[0] = b;
                oc[1] = a;
                return oc;
            }
            oc[0] = pfac.getONE();
            C c = b.leadingBaseCoefficient().inverse();
            oc[1] = a.multiply(c);
            return oc;
        }
        logger.info("computing left Ore condition: " + a + ", " + b);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(2);
        F.add(a);
        F.add(b);
        List<List<GenSolvablePolynomial<C>>> Gz = leftZeroRelationsArbitrary(F);
        /*
        if (Gz.size() < 0) { // always false
            //System.out.println("Gz = " + Gz);
            ModuleList<C> M = new ModuleList<C>(pfac, Gz);
            ModuleList<C> GM = sbb.leftGB(M);
            //System.out.println("GM = " + GM);
            Gz = GM.castToSolvableList();
        }
        */
        List<GenSolvablePolynomial<C>> G1 = null;
        GenSolvablePolynomial<C> g1 = null;
        for (List<GenSolvablePolynomial<C>> Gi : Gz) {
            //System.out.println("Gi = " + Gi);
            if (Gi.get(0).isZERO()) {
                continue;
            }
            if (G1 == null) {
                G1 = Gi;
            }
            if (g1 == null) {
                g1 = G1.get(0);
            } else if (g1.compareTo(Gi.get(0)) > 0) { //g1.degree() > Gi.get(0).degree() 
                G1 = Gi;
                g1 = G1.get(0);
            }
        }
        oc[0] = g1; //G1.get(0);
        oc[1] = (GenSolvablePolynomial<C>) G1.get(1).negate();
        //logger.info("Ore multiple: " + oc[0].multiply(a) + ", " + Arrays.toString(oc));
        return oc;
    }


    /**
     * Right Ore condition. Generators for the right Ore condition of two
     * solvable polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with a*p = b*q
     */
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C>[] rightOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        GenSolvablePolynomialRing<C> pfac = a.ring;
        GenSolvablePolynomial<C>[] oc = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        if (a.equals(b)) {
            oc[0] = pfac.getONE();
            oc[1] = pfac.getONE();
            return oc;
        }
        if (a.isConstant()) {
            if (pfac.coFac.isCommutative()) { // ??
                oc[0] = b;
                oc[1] = a;
                return oc;
            }
            oc[1] = pfac.getONE();
            C c = a.leadingBaseCoefficient().inverse();
            oc[0] = b.multiply(c);
            return oc;
        }
        if (b.isConstant()) {
            if (pfac.coFac.isCommutative()) { // ??
                oc[0] = b;
                oc[1] = a;
                return oc;
            }
            oc[0] = pfac.getONE();
            C c = b.leadingBaseCoefficient().inverse();
            oc[1] = a.multiply(c);
            return oc;
        }
        logger.info("computing right Ore condition: " + a + ", " + b);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(2);
        F.add(a);
        F.add(b);
        List<List<GenSolvablePolynomial<C>>> Gz = rightZeroRelationsArbitrary(F);
        List<GenSolvablePolynomial<C>> G1 = null;
        GenSolvablePolynomial<C> g1 = null;
        for (List<GenSolvablePolynomial<C>> Gi : Gz) {
            if (Gi.get(0).isZERO()) {
                continue;
            }
            if (G1 == null) {
                G1 = Gi;
            }
            if (g1 == null) {
                g1 = G1.get(0);
            } else if (g1.compareTo(Gi.get(0)) > 0) {
                G1 = Gi;
                g1 = G1.get(0);
            }
        }
        oc[0] = G1.get(0);
        oc[1] = (GenSolvablePolynomial<C>) G1.get(1).negate();
        //logger.info("Ore multiple: " + a.multiply(oc[0]) + ", " + Arrays.toString(oc));
        return oc;
    }


    /**
     * Left simplifier. Method of Apel &amp; Lassner (1987).
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with a/b = p/q and q is minimal and monic
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C>[] leftSimplifier(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        GenSolvablePolynomial<C>[] oc = null;
        if (a.isConstant() || b.isConstant()) {
            oc = new GenSolvablePolynomial[]{a, b};
            return oc;
        }
        if (a.totalDegree() > 3 || b.totalDegree() > 3) { // how avoid too long running GBs ?
            //if (a.totalDegree() + b.totalDegree() > 6) { 
            // && a.length() < 10 && b.length() < 10
            logger.warn("skipping simplifier GB computation: degs = " + a.totalDegree() + ", " + b.totalDegree());
            oc = new GenSolvablePolynomial[]{a, b};
            return oc;
        }
        //GenSolvablePolynomialRing<C> pfac = a.ring;
        oc = rightOreCond(a, b);
        logger.info("oc = " + Arrays.toString(oc)); // + ", a = " + a + ", b = " + b);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(oc.length);
        // opposite order and undo negation
        F.add((GenSolvablePolynomial<C>) oc[1].negate());
        F.add(oc[0]);
        //logger.info("F = " + F);
        List<List<GenSolvablePolynomial<C>>> Gz = leftZeroRelationsArbitrary(F);
        //logger.info("Gz: " + Gz);
        List<GenSolvablePolynomial<C>> G1 = new ArrayList<GenSolvablePolynomial<C>>(Gz.size());
        List<GenSolvablePolynomial<C>> G2 = new ArrayList<GenSolvablePolynomial<C>>(Gz.size());
        for (List<GenSolvablePolynomial<C>> ll : Gz) {
            if (!ll.get(0).isZERO()) { // && !ll.get(1).isZERO()
                G1.add(ll.get(0)); // denominators
                G2.add(ll.get(1)); // numerators
            }
        }
        logger.info("G1(den): " + G1 + ", G2(num): " + G2);
        SolvableExtendedGB<C> exgb = sbb.extLeftGB(G1);
        logger.info("exgb.F: " + exgb.F + ", exgb.G: " + exgb.G);
        List<GenSolvablePolynomial<C>> G = exgb.G;
        int m = 0;
        GenSolvablePolynomial<C> min = null;
        for (int i = 0; i < G.size(); i++) {
            if (min == null) {
                min = G.get(i);
                m = i;
            } else if (min.compareTo(G.get(i)) > 0) {
                min = G.get(i);
                m = i;
            }
        }
        //wrong: blas.scalarProduct(G2,exgb.G2F.get(m));
        GenSolvablePolynomial<C> min2 = (GenSolvablePolynomial<C>) blas.scalarProduct(
                PolynomialList.<C>castToList(exgb.G2F.get(m)), PolynomialList.<C>castToList(G2));
        logger.info("min(den): " + min + ", min(num): " + min2 + ", m = " + m + ", " + exgb.G2F.get(m));
        // opposite order
        GenSolvablePolynomial<C> n = min2; // nominator
        GenSolvablePolynomial<C> d = min; // denominator
        // normalize
        if (d.signum() < 0) {
            n = (GenSolvablePolynomial<C>) n.negate();
            d = (GenSolvablePolynomial<C>) d.negate();
        }
        C lc = d.leadingBaseCoefficient();
        if (!lc.isONE() && lc.isUnit()) {
            lc = lc.inverse();
            n = n.multiplyLeft(lc);
            d = d.multiplyLeft(lc);
        }
        if (debug) {
            int t = compare(a, b, n, d);
            if (t != 0) {
                oc[0] = a; // nominator
                oc[1] = b; // denominator
                throw new RuntimeException("simp wrong, giving up: t = " + t);
                //logger.error("simp wrong, giving up: t = " + t);
                //return oc;
            }
        }
        oc[0] = n; // nominator
        oc[1] = d; // denominator
        return oc;
    }

}
