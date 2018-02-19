/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.jas.gb.ExtendedGB;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * SyzygySeq class. Implements Syzygy computations and tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SyzygySeq<C extends GcdRingElem<C>> extends SyzygyAbstract<C> {


    private static final Logger logger = Logger.getLogger(SyzygySeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Groebner base engine.
     */
    protected GroebnerBaseAbstract<C> bb;


    /*
     * Module Groebner base engine.
    //protected ModGroebnerBaseAbstract<C> mbb;
     */


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public SyzygySeq(RingFactory<C> cf) {
        super();
        bb = GBFactory.getImplementation(cf);
        //mbb = new ModGroebnerBaseSeq<C>(cf);
    }


    /**
     * Resolution of a module. Only with direct GBs.
     *
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public List<ResPart<C>> resolution(ModuleList<C> M) {
        List<ResPart<C>> R = new ArrayList<ResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM;
        ModuleList<C> Z;
        //ModGroebnerBase<C> mbb = new ModGroebnerBaseSeq<C>(M.ring.coFac);
        //assert cf == M.ring.coFac;
        while (true) {
            GM = bb.GB(MM);
            Z = zeroRelations(GM);
            R.add(new ResPart<C>(MM, GM, Z));
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
    public List // <ResPart<C>|ResPolPart<C>>
    resolution(PolynomialList<C> F) {
        List<List<GenPolynomial<C>>> Z;
        ModuleList<C> Zm;
        List<GenPolynomial<C>> G;
        PolynomialList<C> Gl;

        //GroebnerBase<C> gb = GBFactory.getImplementation(F.ring.coFac);
        //assert cf == F.ring.coFac;
        G = bb.GB(F.list);
        Z = zeroRelations(G);
        Gl = new PolynomialList<C>(F.ring, G);
        Zm = new ModuleList<C>(F.ring, Z);

        List R = resolution(Zm); //// <ResPart<C>|ResPolPart<C>>
        R.add(0, new ResPolPart<C>(F, Gl, Zm));
        return R;
    }


    /**
     * Resolution of a polynomial list.
     *
     * @param F a polynomial list of an arbitrary basis.
     * @return a resolution of F.
     */
    @SuppressWarnings("unchecked")
    public List // <ResPart<C>|ResPolPart<C>>
    resolutionArbitrary(PolynomialList<C> F) {
        List<List<GenPolynomial<C>>> Z;
        ModuleList<C> Zm;
        //List<GenPolynomial<C>> G;
        PolynomialList<C> Gl = null;

        //G = bb.GB(F.list);
        Z = zeroRelationsArbitrary(F.list);
        //Gl = new PolynomialList<C>(F.ring, F.list);
        Zm = new ModuleList<C>(F.ring, Z);

        List R = resolutionArbitrary(Zm); //// <ResPart<C>|ResPolPart<C>>
        R.add(0, new ResPolPart<C>(F, Gl, Zm));
        return R;
    }


    /**
     * Resolution of a module.
     *
     * @param M a module list of an arbitrary basis.
     * @return a resolution of M.
     */
    public List<ResPart<C>> resolutionArbitrary(ModuleList<C> M) {
        List<ResPart<C>> R = new ArrayList<ResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM = null;
        ModuleList<C> Z;
        while (true) {
            //GM = bb.GB(MM);
            Z = zeroRelationsArbitrary(MM);
            R.add(new ResPart<C>(MM, GM, Z));
            if (Z == null || Z.list == null || Z.list.size() == 0) {
                break;
            }
            MM = Z;
        }
        return R;
    }


    /**
     * Syzygy module from arbitrary base.
     *
     * @param modv number of module variables.
     * @param F    a polynomial list.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> zeroRelationsArbitrary(int modv, List<GenPolynomial<C>> F) {
        if (F == null) {
            return new ArrayList<List<GenPolynomial<C>>>();
            //return zeroRelations(modv, F);
        }
        if (F.size() <= 1) {
            return zeroRelations(modv, F);
        }
        //GroebnerBase<C> gb = GBFactory.getImplementation(F.get(0).ring.coFac);
        // assert cf == F.get(0).ring.coFac;
        final int lenf = F.size();
        ExtendedGB<C> exgb = bb.extGB(F);
        if (debug) {
            logger.debug("exgb = " + exgb);
            if (!bb.isReductionMatrix(exgb)) {
                logger.error("is reduction matrix ? false");
            }
        }
        List<GenPolynomial<C>> G = exgb.G;
        List<List<GenPolynomial<C>>> G2F = exgb.G2F;
        List<List<GenPolynomial<C>>> F2G = exgb.F2G;

        List<List<GenPolynomial<C>>> sg = zeroRelations(modv, G);
        GenPolynomialRing<C> ring = G.get(0).ring;
        ModuleList<C> S = new ModuleList<C>(ring, sg);
        if (debug) {
            logger.debug("syz = " + S);
            if (!isZeroRelation(sg, G)) {
                logger.error("is syzygy ? false");
            }
        }
        List<List<GenPolynomial<C>>> sf;
        sf = new ArrayList<List<GenPolynomial<C>>>(sg.size());
        //List<GenPolynomial<C>> row;
        for (List<GenPolynomial<C>> r : sg) {
            Iterator<GenPolynomial<C>> it = r.iterator();
            Iterator<List<GenPolynomial<C>>> jt = G2F.iterator();

            List<GenPolynomial<C>> rf;
            rf = new ArrayList<GenPolynomial<C>>(lenf);
            for (int m = 0; m < lenf; m++) {
                rf.add(ring.getZERO());
            }
            while (it.hasNext() && jt.hasNext()) {
                GenPolynomial<C> si = it.next();
                List<GenPolynomial<C>> ai = jt.next();
                //System.out.println("si = " + si);
                //System.out.println("ai = " + ai);
                if (si == null || ai == null) {
                    continue;
                }
                List<GenPolynomial<C>> pi = blas.scalarProduct(si, ai);
                //System.out.println("pi = " + pi);
                rf = blas.vectorAdd(rf, pi);
            }
            if (it.hasNext() || jt.hasNext()) {
                logger.error("zeroRelationsArbitrary wrong sizes");
            }
            //System.out.println("\nrf = " + rf + "\n");
            sf.add(rf);
        }
        List<List<GenPolynomial<C>>> M;
        M = new ArrayList<List<GenPolynomial<C>>>(lenf);
        for (List<GenPolynomial<C>> r : F2G) {
            Iterator<GenPolynomial<C>> it = r.iterator();
            Iterator<List<GenPolynomial<C>>> jt = G2F.iterator();

            List<GenPolynomial<C>> rf;
            rf = new ArrayList<GenPolynomial<C>>(lenf);
            for (int m = 0; m < lenf; m++) {
                rf.add(ring.getZERO());
            }
            while (it.hasNext() && jt.hasNext()) {
                GenPolynomial<C> si = it.next();
                List<GenPolynomial<C>> ai = jt.next();
                //System.out.println("si = " + si);
                //System.out.println("ai = " + ai);
                if (si == null || ai == null) {
                    continue;
                }
                List<GenPolynomial<C>> pi = blas.scalarProduct(ai, si);
                //System.out.println("pi = " + pi);
                rf = blas.vectorAdd(rf, pi);
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
        /* not true in general
        List<GenPolynomial<C>> F2 = new ArrayList<GenPolynomial<C>>( F.size() );
        for ( List<GenPolynomial<C>> rr: M ) {
            GenPolynomial<C> rrg = blas.scalarProduct( F, rr );
            F2.add( rrg );
        }
        PolynomialList<C> pF = new PolynomialList<C>( ring, F );
        PolynomialList<C> pF2 = new PolynomialList<C>( ring, F2 );
        if ( ! pF.equals( pF2 ) ) {
           logger.error("is FAB = F ? false");
        }
        */
        int sflen = sf.size();
        List<List<GenPolynomial<C>>> M2;
        M2 = new ArrayList<List<GenPolynomial<C>>>(lenf);
        int i = 0;
        for (List<GenPolynomial<C>> ri : M) {
            List<GenPolynomial<C>> r2i;
            r2i = new ArrayList<GenPolynomial<C>>(ri.size());
            int j = 0;
            for (GenPolynomial<C> rij : ri) {
                GenPolynomial<C> p = null;
                if (i == j) {
                    p = ring.getONE().subtract(rij);
                } else {
                    if (rij != null) {
                        p = rij.negate();
                    }
                }
                r2i.add(p);
                j++;
            }
            M2.add(r2i);
            if (!blas.isZero(r2i)) {
                sf.add(r2i);
            }
            i++;
        }
        if (debug) {
            ModuleList<C> M2L = new ModuleList<C>(ring, M2);
            logger.debug("syz M2L = " + M2L);
            ModuleList<C> SF = new ModuleList<C>(ring, sf);
            logger.debug("syz sf = " + SF);
            logger.debug("#syz " + sflen + ", " + sf.size());
            if (!isZeroRelation(sf, F)) {
                logger.error("is syz sf ? false");
            }
        }
        return sf;
    }

}
