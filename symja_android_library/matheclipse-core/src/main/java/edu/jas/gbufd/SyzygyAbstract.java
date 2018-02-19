/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.vector.BasicLinAlg;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenVectorModul;


/**
 * SyzygyAbstract class. Implements Syzygy computations and tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class SyzygyAbstract<C extends GcdRingElem<C>> implements Syzygy<C> {


    private static final Logger logger = Logger.getLogger(SyzygyAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected Reduction<C> red;


    /**
     * Linear algebra engine.
     */
    protected BasicLinAlg<GenPolynomial<C>> blas;


    /**
     * Constructor.
     */
    public SyzygyAbstract() {
        red = new ReductionSeq<C>();
        blas = new BasicLinAlg<GenPolynomial<C>>();
    }


    /**
     * Syzygy module from Groebner base. F must be a Groebner base.
     *
     * @param F a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> zeroRelations(List<GenPolynomial<C>> F) {
        return zeroRelations(0, F);
    }


    /**
     * Syzygy module from Groebner base. F must be a Groebner base.
     *
     * @param modv number of module variables.
     * @param F    a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> zeroRelations(int modv, List<GenPolynomial<C>> F) {
        List<List<GenPolynomial<C>>> Z = new ArrayList<List<GenPolynomial<C>>>();
        if (F == null) {
            return Z;
        }
        GenVectorModul<GenPolynomial<C>> mfac = null;
        int i = 0;
        while (mfac == null && i < F.size()) {
            GenPolynomial<C> p = F.get(i);
            if (p != null) {
                mfac = new GenVectorModul<GenPolynomial<C>>(p.ring, F.size());
            }
        }
        if (mfac == null) {
            return Z;
        }
        GenVector<GenPolynomial<C>> v = mfac.fromList(F);
        //System.out.println("F = " + F + " v = " + v);
        return zeroRelations(modv, v);
    }


    /**
     * Syzygy module from Groebner base. v must be a Groebner base.
     *
     * @param modv number of module variables.
     * @param v    a Groebner base.
     * @return syz(v), a basis for the module of syzygies for v.
     */
    public List<List<GenPolynomial<C>>> zeroRelations(int modv, GenVector<GenPolynomial<C>> v) {
        List<List<GenPolynomial<C>>> Z = new ArrayList<List<GenPolynomial<C>>>();
        GenVectorModul<GenPolynomial<C>> mfac = v.modul;
        List<GenPolynomial<C>> F = v.val;
        GenVector<GenPolynomial<C>> S = mfac.getZERO();
        GenPolynomial<C> pi, pj, s, h;
        //zero = mfac.coFac.getZERO();
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                List<GenPolynomial<C>> row = S.copy().val;

                s = red.SPolynomial(row, i, pi, j, pj);
                if (s.isZERO()) {
                    Z.add(row);
                    continue;
                }

                h = red.normalform(row, F, s);
                if (!h.isZERO()) {
                    throw new RuntimeException("Syzygy no GB");
                }
                if (debug) {
                    logger.info("row = " + row.size());
                }
                Z.add(row);
            }
        }
        return Z;
    }


    /**
     * Syzygy module from module Groebner base. M must be a module Groebner
     * base.
     *
     * @param M a module Groebner base.
     * @return syz(M), a basis for the module of syzygies for M.
     */
    public ModuleList<C> zeroRelations(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenPolynomial<C> zero = M.ring.getZERO();
        //System.out.println("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        //System.out.println("modv = " + modv);
        List<List<GenPolynomial<C>>> G = zeroRelations(modv, F.list);
        List<List<GenPolynomial<C>>> Z = new ArrayList<List<GenPolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            //F = new PolynomialList(F.ring,(List)G.get(i));
            List<GenPolynomial<C>> Gi = G.get(i);
            List<GenPolynomial<C>> Zi = new ArrayList<GenPolynomial<C>>();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for (int j = 0; j < Gi.size(); j++) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenPolynomial<C> p = Gi.get(j);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    int s = 0;
                    for (GenPolynomial<C> vi : r.values()) {
                        Zi.add(vi);
                        s++;
                    }
                    if (s == 0) {
                        Zi.add(zero);
                    } else if (s > 1) { // will not happen
                        System.out.println("p = " + p);
                        System.out.println("map(" + i + "," + j + ") = " + r + ", size = " + r.size());
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>(M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Test if sysygy.
     *
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of syzygies for F, else false.
     */

    public boolean isZeroRelation(List<List<GenPolynomial<C>>> Z, List<GenPolynomial<C>> F) {
        for (List<GenPolynomial<C>> row : Z) {
            GenPolynomial<C> p = blas.scalarProduct(row, F);
            if (p == null) {
                continue;
            }
            if (!p.isZERO()) {
                logger.info("is not ZeroRelation = " + p.toString(p.ring.getVars()));
                logger.info("row = " + row);
                //logger.info("F = " + F);
                return false;
            }
        }
        return true;
    }


    /**
     * Test if sysygy of modules.
     *
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of syzygies for F, else false.
     */

    public boolean isZeroRelation(ModuleList<C> Z, ModuleList<C> F) {
        if (Z == null || Z.list == null) {
            return true;
        }
        for (List<GenPolynomial<C>> row : Z.list) {
            List<GenPolynomial<C>> zr = blas.leftScalarProduct(row, F.list);
            if (!blas.isZero(zr)) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * Syzygy module from arbitrary base.
     *
     * @param F a polynomial list.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> zeroRelationsArbitrary(List<GenPolynomial<C>> F) {
        return zeroRelationsArbitrary(0, F);
    }


    /**
     * Syzygy module from arbitrary module base.
     *
     * @param M an arbitrary module base.
     * @return syz(M), a basis for the module of syzygies for M.
     */
    public ModuleList<C> zeroRelationsArbitrary(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenPolynomial<C> zero = M.ring.getZERO();
        //System.out.println("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        //System.out.println("modv = " + modv);
        List<List<GenPolynomial<C>>> G = zeroRelationsArbitrary(modv, F.list);
        List<List<GenPolynomial<C>>> Z = new ArrayList<List<GenPolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            //F = new PolynomialList(F.ring,(List)G.get(i));
            List<GenPolynomial<C>> Gi = G.get(i);
            List<GenPolynomial<C>> Zi = new ArrayList<GenPolynomial<C>>();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for (int j = 0; j < Gi.size(); j++) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenPolynomial<C> p = Gi.get(j);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    int s = 0;
                    for (GenPolynomial<C> vi : r.values()) {
                        Zi.add(vi);
                        s++;
                    }
                    if (s == 0) {
                        Zi.add(zero);
                    } else if (s > 1) { // will not happen
                        System.out.println("p = " + p);
                        System.out.println("map(" + i + "," + j + ") = " + r + ", size = " + r.size());
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>(M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }

}


/**
 * Container for module resolution components.
 *
 * @param <C> coefficient type
 */
class ResPart<C extends RingElem<C>> implements Serializable {


    public final ModuleList<C> module;


    public final ModuleList<C> GB;


    public final ModuleList<C> syzygy;


    /**
     * ResPart.
     *
     * @param m a module list.
     * @param g a module list GB.
     * @param z a syzygy module list.
     */
    public ResPart(ModuleList<C> m, ModuleList<C> g, ModuleList<C> z) {
        module = m;
        GB = g;
        syzygy = z;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ResPart(\n");
        s.append("module = " + module);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }
}


/**
 * Container for polynomial resolution components.
 */
class ResPolPart<C extends RingElem<C>> implements Serializable {


    public final PolynomialList<C> ideal;


    public final PolynomialList<C> GB;


    public final ModuleList<C> syzygy;


    /**
     * ResPolPart.
     *
     * @param m a polynomial list.
     * @param g a polynomial list GB.
     * @param z a syzygy module list.
     */
    public ResPolPart(PolynomialList<C> m, PolynomialList<C> g, ModuleList<C> z) {
        ideal = m;
        GB = g;
        syzygy = z;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ResPolPart(\n");
        s.append("ideal = " + ideal);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }

}
