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
import edu.jas.gb.SolvableReduction;
import edu.jas.gb.SolvableReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.vector.BasicLinAlg;


/**
 * Syzygy abstract class for solvable polynomials. Implements Syzygy
 * computations and tests.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class SolvableSyzygyAbstract<C extends GcdRingElem<C>> implements SolvableSyzygy<C> {


    private static final Logger logger = Logger.getLogger(SolvableSyzygyAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Solvable reduction engine.
     */
    public final SolvableReduction<C> sred;


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
    public SolvableSyzygyAbstract() {
        red = new ReductionSeq<C>();
        sred = new SolvableReductionSeq<C>();
        blas = new BasicLinAlg<GenPolynomial<C>>();
    }


    /**
     * Left syzygy for left Groebner base.
     *
     * @param F a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelations(List<GenSolvablePolynomial<C>> F) {
        return leftZeroRelations(0, F);
    }


    /**
     * Left syzygy for left Groebner base.
     *
     * @param modv number of module variables.
     * @param F    a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelations(int modv, List<GenSolvablePolynomial<C>> F) {
        List<List<GenSolvablePolynomial<C>>> Z = new ArrayList<List<GenSolvablePolynomial<C>>>();
        ArrayList<GenSolvablePolynomial<C>> S = new ArrayList<GenSolvablePolynomial<C>>(F.size());
        for (int i = 0; i < F.size(); i++) {
            S.add(null);
        }
        GenSolvablePolynomial<C> pi, pj, s, h, zero;
        zero = null;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            if (zero == null) {
                zero = pi.ring.getZERO();
            }
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) continue;
                List<GenSolvablePolynomial<C>> row = new ArrayList<GenSolvablePolynomial<C>>(S);
                s = sred.leftSPolynomial(row, i, pi, j, pj);
                //logger.info("row = " + row);
                if (s.isZERO()) {
                    Z.add(row);
                    continue;
                }

                h = sred.leftNormalform(row, F, s);
                if (!h.isZERO()) {
                    throw new ArithmeticException("Syzygy no leftGB");
                }
                if (logger.isDebugEnabled()) {
                    logger.info("row = " + row);
                }
                Z.add(row);
            }
        }
        // set null to zero
        for (List<GenSolvablePolynomial<C>> vr : Z) {
            for (int j = 0; j < vr.size(); j++) {
                if (vr.get(j) == null) {
                    vr.set(j, zero);
                }
            }
        }
        return Z;
    }


    /**
     * Left syzygy for left module Groebner base.
     *
     * @param M a Groebner base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> leftZeroRelations(ModuleList<C> M) {
        ModuleList<C> N = null;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenSolvablePolynomial<C> zero = (GenSolvablePolynomial<C>) M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G = leftZeroRelations(modv, F.castToSolvableList());
        //if (G == null) {
        //    return N;
        //}
        List<List<GenSolvablePolynomial<C>>> Z = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            //System.out.println("\nG("+i+") = " + G.get(i));
            for (int j = 0; j < Gi.size(); j++) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                    if (r.size() == 0) {
                        Zi.add(zero);
                    } else if (r.size() == 1) {
                        GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>) (r.values().toArray())[0];
                        Zi.add(vi);
                    } else { // will not happen
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>) M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Right syzygy module from Groebner base.
     *
     * @param F a solvable polynomial list, a Groebner base.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelations(List<GenSolvablePolynomial<C>> F) {
        return rightZeroRelations(0, F);
    }


    /**
     * Right syzygy module from Groebner base.
     *
     * @param modv number of module variables.
     * @param F    a solvable polynomial list, a Groebner base.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    @SuppressWarnings("unchecked")
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelations(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomialRing<C> ring = null;
        for (GenSolvablePolynomial<C> p : F) {
            if (p != null) {
                ring = p.ring;
                break;
            }
        }
        List<List<GenSolvablePolynomial<C>>> Z;
        if (ring == null) { // all null
            Z = new ArrayList<List<GenSolvablePolynomial<C>>>(1);
            Z.add(F);
            return Z;
        }
        //System.out.println("ring to reverse = " + ring.toScript());
        GenSolvablePolynomialRing<C> rring = ring.reverse(true);
        GenSolvablePolynomial<C> q;
        List<GenSolvablePolynomial<C>> rF;
        rF = new ArrayList<GenSolvablePolynomial<C>>(F.size());
        for (GenSolvablePolynomial<C> p : F) {
            if (p != null) {
                q = (GenSolvablePolynomial<C>) p.reverse(rring);
                rF.add(q);
            }
        }
        if (debug) {
            PolynomialList<C> pl = new PolynomialList<C>(rring, rF);
            logger.info("reversed problem = " + pl.toScript());
        }
        List<List<GenSolvablePolynomial<C>>> rZ = leftZeroRelations(modv, rF);
        if (debug) {
            boolean isit = isLeftZeroRelation(rZ, rF);
            logger.debug("isLeftZeroRelation = " + isit);
        }
        GenSolvablePolynomialRing<C> oring = rring.reverse(true);
        if (debug) {
            logger.debug("ring == oring: " + ring.equals(oring));
        }
        ring = oring;
        Z = new ArrayList<List<GenSolvablePolynomial<C>>>(rZ.size());
        for (List<GenSolvablePolynomial<C>> z : rZ) {
            if (z == null) {
                continue;
            }
            List<GenSolvablePolynomial<C>> s;
            s = new ArrayList<GenSolvablePolynomial<C>>(z.size());
            for (GenSolvablePolynomial<C> p : z) {
                if (p != null) {
                    q = (GenSolvablePolynomial<C>) p.reverse(ring);
                    s.add(q);
                }
            }
            Z.add(s);
        }
        return Z;
    }


    /**
     * Right syzygy for right module Groebner base.
     *
     * @param M a Groebner base.
     * @return rightSyz(M), a basis for the right module of syzygies for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> rightZeroRelations(ModuleList<C> M) {
        ModuleList<C> N = null;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenSolvablePolynomial<C> zero = (GenSolvablePolynomial<C>) M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G = rightZeroRelations(modv, F.castToSolvableList());
        //if (G == null) {
        //    return N;
        //}
        List<List<GenSolvablePolynomial<C>>> Z = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            //System.out.println("\nG("+i+") = " + G.get(i));
            for (int j = 0; j < Gi.size(); j++) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                    if (r.size() == 0) {
                        Zi.add(zero);
                    } else if (r.size() == 1) {
                        GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>) (r.values().toArray())[0];
                        Zi.add(vi);
                    } else { // will not happen
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>) M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Test if left syzygy.
     *
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean isLeftZeroRelation(List<List<GenSolvablePolynomial<C>>> Z, List<GenSolvablePolynomial<C>> F) {
        List<GenPolynomial<C>> Fp = PolynomialList.<C>castToList(F);
        for (List<GenSolvablePolynomial<C>> row : Z) {
            // p has wrong type:
            GenPolynomial<C> p = blas.scalarProduct(PolynomialList.<C>castToList(row), Fp);
            if (p == null) {
                continue;
            }
            if (!p.isZERO()) {
                logger.info("is not ZeroRelation = " + p);
                return false;
            }
        }
        return true;
    }


    /**
     * Test if right syzygy.
     *
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean isRightZeroRelation(List<List<GenSolvablePolynomial<C>>> Z,
                                       List<GenSolvablePolynomial<C>> F) {
        List<GenPolynomial<C>> Fp = PolynomialList.<C>castToList(F);
        for (List<GenSolvablePolynomial<C>> row : Z) {
            List<GenPolynomial<C>> yrow = PolynomialList.<C>castToList(row);
            // p has wrong type:
            GenPolynomial<C> p = blas.scalarProduct(Fp, yrow); // param order
            if (p == null) {
                continue;
            }
            if (!p.isZERO()) {
                logger.info("is not ZeroRelation = " + p);
                return false;
            }
        }
        return true;
    }


    /**
     * Test if left sysygy of modules
     *
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean isLeftZeroRelation(ModuleList<C> Z, ModuleList<C> F) {
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
     * Test if right sysygy of modules
     *
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean isRightZeroRelation(ModuleList<C> Z, ModuleList<C> F) {
        if (Z == null || Z.list == null) {
            return true;
        }
        for (List<GenPolynomial<C>> row : Z.list) {
            List<GenPolynomial<C>> zr = blas.rightScalarProduct(row, F.list);
            //List<GenPolynomial<C>> zr = blas.scalarProduct(row,F.list);
            if (!blas.isZero(zr)) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * Left syzygy module from arbitrary base.
     *
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> leftZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F) {
        return leftZeroRelationsArbitrary(0, F);
    }


    /**
     * Left syzygy for arbitrary left module base.
     *
     * @param M an arbitrary base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> leftZeroRelationsArbitrary(ModuleList<C> M) {
        ModuleList<C> N = null;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenSolvablePolynomial<C> zero = (GenSolvablePolynomial<C>) M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G = leftZeroRelationsArbitrary(modv, F.castToSolvableList());
        if (G == null) {
            return N;
        }
        List<List<GenSolvablePolynomial<C>>> Z = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            //System.out.println("\nG("+i+") = " + G.get(i));
            for (int j = 0; j < Gi.size(); j++) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                    if (r.size() == 0) {
                        Zi.add(zero);
                    } else if (r.size() == 1) {
                        GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>) (r.values().toArray())[0];
                        Zi.add(vi);
                    } else { // will not happen
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>) M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Right syzygy module from arbitrary base.
     *
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F) {
        return rightZeroRelationsArbitrary(0, F);
    }


    /**
     * Right syzygy module from arbitrary base.
     *
     * @param modv number of module variables.
     * @param F    a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    @SuppressWarnings("unchecked")
    public List<List<GenSolvablePolynomial<C>>> rightZeroRelationsArbitrary(int modv,
                                                                            List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomialRing<C> ring = null;
        for (GenSolvablePolynomial<C> p : F) {
            if (p != null) {
                ring = p.ring;
                break;
            }
        }
        List<List<GenSolvablePolynomial<C>>> Z;
        if (ring == null) { // all null
            Z = new ArrayList<List<GenSolvablePolynomial<C>>>(1);
            Z.add(F);
            return Z;
        }
        GenSolvablePolynomialRing<C> rring = ring.reverse(true);
        GenSolvablePolynomial<C> q;
        List<GenSolvablePolynomial<C>> rF;
        rF = new ArrayList<GenSolvablePolynomial<C>>(F.size());
        for (GenSolvablePolynomial<C> p : F) {
            if (p != null) {
                q = (GenSolvablePolynomial<C>) p.reverse(rring);
                rF.add(q);
            }
        }
        if (debug) {
            PolynomialList<C> pl = new PolynomialList<C>(rring, rF);
            logger.info("reversed problem = " + pl.toScript());
        }
        List<List<GenSolvablePolynomial<C>>> rZ = leftZeroRelationsArbitrary(modv, rF);
        if (debug) {
            ModuleList<C> pl = new ModuleList<C>(rring, rZ);
            logger.info("reversed syzygies = " + pl.toScript());
            boolean isit = isLeftZeroRelation(rZ, rF);
            logger.info("isLeftZeroRelation = " + isit);
        }
        GenSolvablePolynomialRing<C> oring = rring.reverse(true);
        if (debug) {
            logger.info("ring == oring: " + ring.equals(oring));
        }
        ring = oring;
        Z = new ArrayList<List<GenSolvablePolynomial<C>>>(rZ.size());
        for (List<GenSolvablePolynomial<C>> z : rZ) {
            if (z == null) {
                continue;
            }
            List<GenSolvablePolynomial<C>> s;
            s = new ArrayList<GenSolvablePolynomial<C>>(z.size());
            for (GenSolvablePolynomial<C> p : z) {
                if (p != null) {
                    q = (GenSolvablePolynomial<C>) p.reverse(ring);
                    s.add(q);
                    //System.out.println("p = " + p + "\nreverse(p) = " + q);
                }
            }
            Z.add(s);
        }
        return Z;
    }


    /**
     * Right syzygy for arbitrary base.
     *
     * @param M an arbitray base.
     * @return rightSyz(M), a basis for the right module of syzygies for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> rightZeroRelationsArbitrary(ModuleList<C> M) {
        ModuleList<C> N = null;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        GenSolvablePolynomial<C> zero = (GenSolvablePolynomial<C>) M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G = rightZeroRelationsArbitrary(modv, F.castToSolvableList());
        //if (G == null) {
        //    return N;
        //}
        List<List<GenSolvablePolynomial<C>>> Z = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for (int i = 0; i < G.size(); i++) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            //System.out.println("G("+i+") = " + Gi);
            for (int j = 0; j < Gi.size(); j++) {
                GenSolvablePolynomial<C> p = Gi.get(j);
                //System.out.println("G("+i+","+j+") = " + p);
                if (p != null) {
                    Map<ExpVector, GenPolynomial<C>> r = p.contract(M.ring);
                    //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                    if (r.size() == 0) {
                        Zi.add(zero);
                    } else if (r.size() == 1) {
                        GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>) (r.values().toArray())[0];
                        Zi.add(vi);
                    } else { // will not happen
                        logger.error("p = " + p + ", r = " + r);
                        throw new RuntimeException("Map.size() > 1 = " + r.size());
                    }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add(Zi);
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>) M.ring, Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Test left Ore condition.
     *
     * @param a  solvable polynomial
     * @param b  solvable polynomial
     * @param oc = [p,q] two solvable polynomials
     * @return true if p*a = q*b, else false
     */
    public boolean isLeftOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b,
                                 GenSolvablePolynomial<C>[] oc) {
        GenSolvablePolynomial<C> c = oc[0].multiply(a);
        GenSolvablePolynomial<C> d = oc[1].multiply(b);
        return c.equals(d);
    }


    /**
     * Test right Ore condition.
     *
     * @param a  solvable polynomial
     * @param b  solvable polynomial
     * @param oc = [p,q] two solvable polynomials
     * @return true if a*p = b*q, else false
     */
    public boolean isRightOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b,
                                  GenSolvablePolynomial<C>[] oc) {
        GenSolvablePolynomial<C> c = a.multiply(oc[0]);
        GenSolvablePolynomial<C> d = b.multiply(oc[1]);
        return c.equals(d);
    }


    /**
     * Left simplifier. Method of Apel &amp; Lassner (1987).
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with a/b = p/q and q is minimal and monic
     */
    public abstract GenSolvablePolynomial<C>[] leftSimplifier(GenSolvablePolynomial<C> a,
                                                              GenSolvablePolynomial<C> b);


    /**
     * Comparison like SolvableLocal or SolvableQuotient.
     *
     * @param num SolvablePolynomial.
     * @param den SolvablePolynomial.
     * @param n   SolvablePolynomial.
     * @param d   SolvablePolynomial.
     * @return sign((num/den)-(n/d)).
     */
    public int compare(GenSolvablePolynomial<C> num, GenSolvablePolynomial<C> den,
                       GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        if (n == null || n.isZERO()) {
            return num.signum();
        }
        if (num.isZERO()) {
            return -n.signum();
        }
        // assume sign(den,b.den) > 0
        int s1 = num.signum();
        int s2 = n.signum();
        int t = (s1 - s2) / 2;
        if (t != 0) {
            System.out.println("compareTo: t = " + t);
            //return t;
        }
        if (den.compareTo(d) == 0) {
            return num.compareTo(n);
        }
        GenSolvablePolynomial<C> r, s;
        // if (den.isONE()) { }
        // if (b.den.isONE()) { }
        GenSolvablePolynomial<C>[] oc = leftOreCond(den, d);
        if (debug) {
            System.out.println("oc[0] den =<>= oc[1] d: (" + oc[0] + ") (" + den + ") = (" + oc[1] + ") ("
                    + d + ")");
        }
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        r = oc[0].multiply(num);
        s = oc[1].multiply(n);
        logger.info("compare: r = " + r + ", s = " + s);
        return r.compareTo(s);
    }

}


/**
 * Container for module resolution components.
 *
 * @param <C> coefficient type
 */
class SolvResPart<C extends RingElem<C>> implements Serializable {


    public final ModuleList<C> module;


    public final ModuleList<C> GB;


    public final ModuleList<C> syzygy;


    /**
     * SolvResPart.
     *
     * @param m a module list.
     * @param g a module list GB.
     * @param z a syzygy module list.
     */
    public SolvResPart(ModuleList<C> m, ModuleList<C> g, ModuleList<C> z) {
        module = m;
        GB = g;
        syzygy = z;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPart(\n");
        s.append("module = " + module);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }
}


/**
 * Container for polynomial resolution components.
 *
 * @param <C> coefficient type
 */
class SolvResPolPart<C extends RingElem<C>> implements Serializable {


    public final PolynomialList<C> ideal;


    public final PolynomialList<C> GB;


    public final ModuleList<C> syzygy;


    /**
     * SolvResPolPart.
     *
     * @param m a polynomial list.
     * @param g a polynomial list GB.
     * @param z a syzygy module list.
     */
    public SolvResPolPart(PolynomialList<C> m, PolynomialList<C> g, ModuleList<C> z) {
        ideal = m;
        GB = g;
        syzygy = z;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPolPart(\n");
        s.append("ideal = " + ideal);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }

}
