/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;
import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.root.ComplexRoots;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.Interval;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealAlgebraicRing;
import edu.jas.root.RealRootTuple;
import edu.jas.root.RealRootsAbstract;
import edu.jas.root.RealRootsSturm;
import edu.jas.root.Rectangle;
import edu.jas.root.RootFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.ListUtil;


/**
 * Polynomial utilities for applications, for example conversion ExpVector to
 * Product or zero dimensional ideal root computation.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class PolyUtilApp<C extends RingElem<C>> {


    private static final Logger logger = Logger.getLogger(PolyUtilApp.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<Product<Residue<C>>>> toProductRes(
                    GenPolynomialRing<Product<Residue<C>>> pfac, List<GenPolynomial<GenPolynomial<C>>> L) {

        List<GenPolynomial<Product<Residue<C>>>> list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if (L == null || L.size() == 0) {
            return list;
        }
        GenPolynomial<Product<Residue<C>>> b;
        for (GenPolynomial<GenPolynomial<C>> a : L) {
            b = toProductRes(pfac, a);
            list.add(b);
        }
        return list;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<Product<Residue<C>>> toProductRes(
                    GenPolynomialRing<Product<Residue<C>>> pfac, GenPolynomial<GenPolynomial<C>> A) {

        GenPolynomial<Product<Residue<C>>> P = pfac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return P;
        }
        RingFactory<Product<Residue<C>>> rpfac = pfac.coFac;
        ProductRing<Residue<C>> fac = (ProductRing<Residue<C>>) rpfac;
        Product<Residue<C>> p;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = toProductRes(fac, a);
            if (!p.isZERO()) {
                P.doPutToMap(e, p);
            }
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>> Product<Residue<C>> toProductRes(ProductRing<Residue<C>> pfac,
                    GenPolynomial<C> c) {

        SortedMap<Integer, Residue<C>> elem = new TreeMap<Integer, Residue<C>>();
        for (int i = 0; i < pfac.length(); i++) {
            RingFactory<Residue<C>> rfac = pfac.getFactory(i);
            ResidueRing<C> fac = (ResidueRing<C>) rfac;
            Residue<C> u = new Residue<C>(fac, c);
            //fac.fromInteger( c.getVal() );
            if (!u.isZERO()) {
                elem.put(i, u);
            }
        }
        return new Product<Residue<C>>(pfac, elem);
    }


    /**
     * Product residue representation.
     * @param <C> coefficient type.
     * @param CS list of ColoredSystems from comprehensive GB system.
     * @return Product residue represenation of CS.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<Product<Residue<C>>>> toProductRes(
                    List<ColoredSystem<C>> CS) {

        List<GenPolynomial<Product<Residue<C>>>> list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if (CS == null || CS.size() == 0) {
            return list;
        }
        GenPolynomialRing<GenPolynomial<C>> pr = null;
        List<RingFactory<Residue<C>>> rrl = new ArrayList<RingFactory<Residue<C>>>(CS.size());
        for (ColoredSystem<C> cs : CS) {
            Ideal<C> id = cs.condition.zero;
            ResidueRing<C> r = new ResidueRing<C>(id);
            if (!rrl.contains(r)) {
                rrl.add(r);
            }
            if (pr == null) {
                if (cs.list.size() > 0) {
                    pr = cs.list.get(0).green.ring;
                }
            }
        }
        ProductRing<Residue<C>> pfac;
        pfac = new ProductRing<Residue<C>>(rrl);
        //System.out.println("pfac = " + pfac);
        GenPolynomialRing<Product<Residue<C>>> rf = new GenPolynomialRing<Product<Residue<C>>>(pfac, pr.nvar,
                        pr.tord, pr.getVars());
        GroebnerSystem<C> gs = new GroebnerSystem<C>(CS);
        List<GenPolynomial<GenPolynomial<C>>> F = gs.getCGB();
        list = PolyUtilApp.<C> toProductRes(rf, F);
        return list;
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<Residue<C>>> toResidue(
                    GenPolynomialRing<Residue<C>> pfac, List<GenPolynomial<GenPolynomial<C>>> L) {
        List<GenPolynomial<Residue<C>>> list = new ArrayList<GenPolynomial<Residue<C>>>();
        if (L == null || L.size() == 0) {
            return list;
        }
        GenPolynomial<Residue<C>> b;
        for (GenPolynomial<GenPolynomial<C>> a : L) {
            b = toResidue(pfac, a);
            if (!b.isZERO()) {
                list.add(b);
            }
        }
        return list;
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<Residue<C>> toResidue(
                    GenPolynomialRing<Residue<C>> pfac, GenPolynomial<GenPolynomial<C>> A) {
        GenPolynomial<Residue<C>> P = pfac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return P;
        }
        RingFactory<Residue<C>> rpfac = pfac.coFac;
        ResidueRing<C> fac = (ResidueRing<C>) rpfac;
        Residue<C> p;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = new Residue<C>(fac, a);
            if (!p.isZERO()) {
                P.doPutToMap(e, p);
            }
        }
        return P;
    }


    /**
     * Product slice.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return Slices represenation of L.
     */
    public static <C extends GcdRingElem<C>> Map<Ideal<C>, PolynomialList<GenPolynomial<C>>> productSlice(
                    PolynomialList<Product<Residue<C>>> L) {

        Map<Ideal<C>, PolynomialList<GenPolynomial<C>>> map;
        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>) fpr;
        int s = pr.length();
        map = new TreeMap<Ideal<C>, PolynomialList<GenPolynomial<C>>>();
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        for (int i = 0; i < s; i++) {
            RingFactory<Residue<C>> r = pr.getFactory(i);
            ResidueRing<C> rr = (ResidueRing<C>) r;
            Ideal<C> id = rr.ideal;
            GenPolynomialRing<C> cof = rr.ring;
            GenPolynomialRing<GenPolynomial<C>> pfc;
            pfc = new GenPolynomialRing<GenPolynomial<C>>(cof, L.ring);
            slist = fromProduct(pfc, plist, i);
            spl = new PolynomialList<GenPolynomial<C>>(pfc, slist);
            PolynomialList<GenPolynomial<C>> d = map.get(id);
            if (d != null) {
                throw new RuntimeException("ideal exists twice " + id);
            }
            map.put(id, spl);
        }
        return map;
    }


    /**
     * Product slice at i.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coeffients.
     * @param i index of slice.
     * @return Slice of of L at i.
     */
    public static <C extends GcdRingElem<C>> PolynomialList<GenPolynomial<C>> productSlice(
                    PolynomialList<Product<Residue<C>>> L, int i) {

        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>) fpr;
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        RingFactory<Residue<C>> r = pr.getFactory(i);
        ResidueRing<C> rr = (ResidueRing<C>) r;
        GenPolynomialRing<C> cof = rr.ring;
        GenPolynomialRing<GenPolynomial<C>> pfc;
        pfc = new GenPolynomialRing<GenPolynomial<C>>(cof, L.ring);
        slist = fromProduct(pfc, plist, i);
        spl = new PolynomialList<GenPolynomial<C>>(pfc, slist);
        return spl;
    }


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<GenPolynomial<C>>> fromProduct(
                    GenPolynomialRing<GenPolynomial<C>> pfac, List<GenPolynomial<Product<Residue<C>>>> L,
                    int i) {

        List<GenPolynomial<GenPolynomial<C>>> list = new ArrayList<GenPolynomial<GenPolynomial<C>>>();

        if (L == null || L.size() == 0) {
            return list;
        }
        GenPolynomial<GenPolynomial<C>> b;
        for (GenPolynomial<Product<Residue<C>>> a : L) {
            b = fromProduct(pfac, a, i);
            if (!b.isZERO()) {
                b = b.abs();
                if (!list.contains(b)) {
                    list.add(b);
                }
            }
        }
        return list;
    }


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param P polynomial to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of P in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> fromProduct(
                    GenPolynomialRing<GenPolynomial<C>> pfac, GenPolynomial<Product<Residue<C>>> P, int i) {

        GenPolynomial<GenPolynomial<C>> b = pfac.getZERO().copy();
        if (P == null || P.isZERO()) {
            return b;
        }

        for (Map.Entry<ExpVector, Product<Residue<C>>> y : P.getMap().entrySet()) {
            ExpVector e = y.getKey();
            Product<Residue<C>> a = y.getValue();
            Residue<C> r = a.get(i);
            if (r != null && !r.isZERO()) {
                GenPolynomial<C> p = r.val;
                if (!p.isZERO()) {
                    b.doPutToMap(e, p);
                }
            }
        }
        return b;
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>> String productSliceToString(
                    Map<Ideal<C>, PolynomialList<GenPolynomial<C>>> L) {
        Set<GenPolynomial<GenPolynomial<C>>> sl = new TreeSet<GenPolynomial<GenPolynomial<C>>>();
        PolynomialList<GenPolynomial<C>> pl = null;
        StringBuffer sb = new StringBuffer(); //"\nproductSlice ----------------- begin");
        for (Map.Entry<Ideal<C>, PolynomialList<GenPolynomial<C>>> en : L.entrySet()) {
            sb.append("\n\ncondition == 0:\n");
            sb.append(en.getKey().list.toScript());
            pl = en.getValue(); //L.get(id);
            sl.addAll(pl.list);
            sb.append("\ncorresponding ideal:\n");
            sb.append(pl.toScript());
        }
        //List<GenPolynomial<GenPolynomial<C>>> sll 
        //   = new ArrayList<GenPolynomial<GenPolynomial<C>>>( sl );
        //pl = new PolynomialList<GenPolynomial<C>>(pl.ring,sll);
        // sb.append("\nunion = " + pl.toString());
        //sb.append("\nproductSlice ------------------------- end\n");
        return sb.toString();
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return string represenation of slices of L.
     */
    public static <C extends GcdRingElem<C>> String productToString(PolynomialList<Product<Residue<C>>> L) {
        Map<Ideal<C>, PolynomialList<GenPolynomial<C>>> M;
        M = productSlice(L);
        String s = productSliceToString(M);
        return s;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<Complex<BigDecimal>>> complexRootTuples(
                    Ideal<D> I, D eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if (logger.isInfoEnabled()) {
            logger.info("univs = " + univs);
        }
        return complexRoots(I, univs, eps);
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<Complex<BigDecimal>>> complexRoots(
                    Ideal<D> I, List<GenPolynomial<D>> univs, D eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
        RingFactory<D> cf = (RingFactory<D>) I.list.ring.coFac;
        ComplexRing<D> cr = new ComplexRing<D>(cf);
        ComplexRootsAbstract<D> cra = new ComplexRootsSturm<D>(cr);
        List<GenPolynomial<Complex<D>>> cunivs = new ArrayList<GenPolynomial<Complex<D>>>();
        for (GenPolynomial<D> p : univs) {
            GenPolynomialRing<Complex<D>> pfac = new GenPolynomialRing<Complex<D>>(cr, p.ring);
            //System.out.println("pfac = " + pfac.toScript());
            GenPolynomial<Complex<D>> cp = PolyUtil.<D> toComplex(pfac, p);
            cunivs.add(cp);
            //System.out.println("cp = " + cp);
        }
        for (int i = 0; i < I.list.ring.nvar; i++) {
            List<Complex<BigDecimal>> cri = cra.approximateRoots(cunivs.get(i), eps);
            //System.out.println("cri = " + cri);
            croots.add(cri);
        }
        croots = ListUtil.<Complex<BigDecimal>> tupleFromList(croots);
        return croots;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(cap_i(G_i))
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<Complex<BigDecimal>>> complexRootTuples(
                    List<IdealWithUniv<D>> Il, D eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
        for (IdealWithUniv<D> I : Il) {
            List<List<Complex<BigDecimal>>> cr = complexRoots(I.ideal, I.upolys, eps);
            croots.addAll(cr);
        }
        return croots;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of ideals with coordinates of complex roots for
     *         ideal(cap_i(G_i))
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithComplexRoots<D>> complexRoots(
                    List<IdealWithUniv<D>> Il, D eps) {
        List<IdealWithComplexRoots<D>> Ic = new ArrayList<IdealWithComplexRoots<D>>(Il.size());
        for (IdealWithUniv<D> I : Il) {
            List<List<Complex<BigDecimal>>> cr = complexRoots(I.ideal, I.upolys, eps);
            IdealWithComplexRoots<D> ic = new IdealWithComplexRoots<D>(I, cr);
            Ic.add(ic);
        }
        return Ic;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param G list of polynomials of a of zero dimensional ideal.
     * @param eps desired precision.
     * @return list of ideals with coordinates of complex roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithComplexRoots<D>> complexRoots(
                    Ideal<D> G, D eps) {
        List<IdealWithUniv<D>> Il = G.zeroDimDecomposition();
        return complexRoots(Il, eps);
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<BigDecimal>> realRootTuples(Ideal<D> I,
                    D eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if (logger.isInfoEnabled()) {
            logger.info("univs = " + univs);
        }
        return realRoots(I, univs, eps);
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<BigDecimal>> realRoots(Ideal<D> I,
                    List<GenPolynomial<D>> univs, D eps) {
        List<List<BigDecimal>> roots = new ArrayList<List<BigDecimal>>();
        //RingFactory<D> cf = (RingFactory<D>) I.list.ring.coFac;
        RealRootsAbstract<D> rra = new RealRootsSturm<D>();
        for (int i = 0; i < I.list.ring.nvar; i++) {
            List<BigDecimal> rri = rra.approximateRoots(univs.get(i), eps);
            //System.out.println("rri = " + rri);
            roots.add(rri);
        }
        //System.out.println("roots-1 = " + roots);
        roots = ListUtil.<BigDecimal> tupleFromList(roots);
        //System.out.println("roots-2 = " + roots);
        return roots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(cap_i(G_i))
     */
    public static <D extends GcdRingElem<D> & Rational> List<List<BigDecimal>> realRootTuples(
                    List<IdealWithUniv<D>> Il, D eps) {
        List<List<BigDecimal>> rroots = new ArrayList<List<BigDecimal>>();
        for (IdealWithUniv<D> I : Il) {
            List<List<BigDecimal>> rr = realRoots(I.ideal, I.upolys, eps);
            rroots.addAll(rr);
        }
        return rroots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of ideals with coordinates of real roots for
     *         ideal(cap_i(G_i))
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithRealRoots<D>> realRoots(
                    List<IdealWithUniv<D>> Il, D eps) {
        List<IdealWithRealRoots<D>> Ir = new ArrayList<IdealWithRealRoots<D>>(Il.size());
        for (IdealWithUniv<D> I : Il) {
            List<List<BigDecimal>> rr = realRoots(I.ideal, I.upolys, eps);
            IdealWithRealRoots<D> ir = new IdealWithRealRoots<D>(I, rr);
            Ir.add(ir);
        }
        return Ir;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param G list of polynomials of a of zero dimensional ideal.
     * @param eps desired precision.
     * @return list of ideals with coordinates of real roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithRealRoots<D>> realRoots(Ideal<D> G,
                    D eps) {
        List<IdealWithUniv<D>> Il = G.zeroDimDecomposition();
        return realRoots(Il, eps);
    }


    /**
     * Test for real roots of zero dimensional ideal(L).
     * @param L list of polynomials.
     * @param roots list of real roots for ideal(G).
     * @param eps desired precision.
     * @return true if root is a list of coordinates of real roots for ideal(L)
     */
    public static boolean isRealRoots(List<GenPolynomial<BigDecimal>> L, List<List<BigDecimal>> roots,
                    BigDecimal eps) {
        if (L == null || L.size() == 0) {
            return true;
        }
        // polynomials with decimal coefficients
        BigDecimal dc = BigDecimal.ONE;
        GenPolynomialRing<BigDecimal> dfac = L.get(0).ring;
        //System.out.println("dfac = " + dfac);
        for (GenPolynomial<BigDecimal> dp : L) {
            //System.out.println("dp = " + dp);
            for (List<BigDecimal> r : roots) {
                //System.out.println("r = " + r);
                BigDecimal ev = PolyUtil.<BigDecimal> evaluateAll(dc, dfac, dp, r);
                if (ev.abs().compareTo(eps) > 0) {
                    System.out.println("ev = " + ev);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Test for complex roots of zero dimensional ideal(L).
     * @param L list of polynomials.
     * @param roots list of real roots for ideal(G).
     * @param eps desired precision.
     * @return true if root is a list of coordinates of complex roots for
     *         ideal(L)
     */
    public static boolean isComplexRoots(List<GenPolynomial<Complex<BigDecimal>>> L,
                    List<List<Complex<BigDecimal>>> roots, BigDecimal eps) {
        if (L == null || L.size() == 0) {
            return true;
        }
        // polynomials with decimal coefficients
        BigDecimal dc = BigDecimal.ONE;
        ComplexRing<BigDecimal> dcc = new ComplexRing<BigDecimal>(dc);
        GenPolynomialRing<Complex<BigDecimal>> dfac = L.get(0).ring;
        //System.out.println("dfac = " + dfac);
        for (GenPolynomial<Complex<BigDecimal>> dp : L) {
            //System.out.println("dp = " + dp);
            for (List<Complex<BigDecimal>> r : roots) {
                //System.out.println("r = " + r);
                Complex<BigDecimal> ev = PolyUtil.<Complex<BigDecimal>> evaluateAll(dcc, dfac, dp, r);
                if (ev.norm().getRe().compareTo(eps) > 0) {
                    System.out.println("ev = " + ev);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Construct real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal with univariate irreducible polynomials
     *            and bi-variate polynomials.
     * @return real algebraic roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> IdealWithRealAlgebraicRoots<D> realAlgebraicRoots(
                    IdealWithUniv<D> I) {
        List<List<RealAlgebraicNumber<D>>> ran = new ArrayList<List<RealAlgebraicNumber<D>>>();
        if (I == null) {
            throw new IllegalArgumentException("null ideal not permitted");
        }
        if (I.ideal == null || I.upolys == null) {
            throw new IllegalArgumentException("null ideal components not permitted " + I);
        }
        if (I.ideal.isZERO() || I.upolys.size() == 0) {
            return new IdealWithRealAlgebraicRoots<D>(I, ran);
        }
        GenPolynomialRing<D> fac = I.ideal.list.ring;
        // case i == 0:
        GenPolynomial<D> p0 = I.upolys.get(0);
        GenPolynomial<D> p0p = PolyUtil.<D> selectWithVariable(I.ideal.list.list, fac.nvar - 1);
        if (p0p == null) {
            throw new RuntimeException("no polynomial found in " + (fac.nvar - 1) + " of  " + I.ideal);
        }
        //System.out.println("p0  = " + p0);
        if (logger.isInfoEnabled()) {
            logger.info("p0p = " + p0p);
        }
        int[] dep0 = p0p.degreeVector().dependencyOnVariables();
        //System.out.println("dep0 = " + Arrays.toString(dep0));
        if (dep0.length != 1) {
            throw new RuntimeException("wrong number of variables " + Arrays.toString(dep0));
        }
        List<RealAlgebraicNumber<D>> rra = RootFactory.<D> realAlgebraicNumbersIrred(p0);
        if (logger.isInfoEnabled()) {
            List<Interval<D>> il = new ArrayList<Interval<D>>();
            for (RealAlgebraicNumber<D> rr : rra) {
                il.add(rr.ring.getRoot());
            }
            logger.info("roots(p0) = " + il);
        }
        for (RealAlgebraicNumber<D> rr : rra) {
            List<RealAlgebraicNumber<D>> rl = new ArrayList<RealAlgebraicNumber<D>>();
            rl.add(rr);
            ran.add(rl);
        }
        // case i > 0:
        for (int i = 1; i < I.upolys.size(); i++) {
            List<List<RealAlgebraicNumber<D>>> rn = new ArrayList<List<RealAlgebraicNumber<D>>>();
            GenPolynomial<D> pi = I.upolys.get(i);
            GenPolynomial<D> pip = PolyUtil.selectWithVariable(I.ideal.list.list, fac.nvar - 1 - i);
            if (pip == null) {
                throw new RuntimeException("no polynomial found in " + (fac.nvar - 1 - i) + " of  " + I.ideal);
            }
            //System.out.println("i   = " + i);
            //System.out.println("pi  = " + pi);
            if (logger.isInfoEnabled()) {
                logger.info("pi  = " + pi);
                logger.info("pip = " + pip);
            }
            int[] depi = pip.degreeVector().dependencyOnVariables();
            //System.out.println("depi = " + Arrays.toString(depi));
            if (depi.length < 1 || depi.length > 2) {
                throw new RuntimeException("wrong number of variables " + Arrays.toString(depi));
            }
            rra = RootFactory.<D> realAlgebraicNumbersIrred(pi);
            if (logger.isInfoEnabled()) {
                List<Interval<D>> il = new ArrayList<Interval<D>>();
                for (RealAlgebraicNumber<D> rr : rra) {
                    il.add(rr.ring.getRoot());
                }
                logger.info("roots(pi) = " + il);
            }
            if (depi.length == 1) {
                // all combinations are roots of the ideal I
                for (RealAlgebraicNumber<D> rr : rra) {
                    //System.out.println("rr.ring = " + rr.ring);
                    for (List<RealAlgebraicNumber<D>> rx : ran) {
                        //System.out.println("rx = " + rx);
                        List<RealAlgebraicNumber<D>> ry = new ArrayList<RealAlgebraicNumber<D>>();
                        ry.addAll(rx);
                        ry.add(rr);
                        rn.add(ry);
                    }
                }
            } else { // depi.length == 2
                // select roots of the ideal I
                GenPolynomial<D> pip2 = PolyUtil.<D> removeUnusedUpperVariables(pip);
                //System.out.println("pip2 = " + pip2.ring);
                GenPolynomialRing<D> ufac = pip2.ring.contract(1);
                TermOrder to = new TermOrder(TermOrder.INVLEX);
                GenPolynomialRing<GenPolynomial<D>> rfac = new GenPolynomialRing<GenPolynomial<D>>(ufac, 1,
                                to);
                GenPolynomial<GenPolynomial<D>> pip2r = PolyUtil.<D> recursive(rfac, pip2);
                int ix = fac.nvar - 1 - depi[depi.length - 1];
                //System.out.println("ix = " + ix);
                for (RealAlgebraicNumber<D> rr : rra) {
                    //System.out.println("rr.ring = " + rr.ring);
                    Interval<D> rroot = rr.ring.getRoot();
                    GenPolynomial<D> pip2el = PolyUtil.<D> evaluateMainRecursive(ufac, pip2r, rroot.left);
                    GenPolynomial<D> pip2er = PolyUtil.<D> evaluateMainRecursive(ufac, pip2r, rroot.right);
                    GenPolynomialRing<D> upfac = I.upolys.get(ix).ring;
                    GenPolynomial<D> pip2elc = convert(upfac, pip2el);
                    GenPolynomial<D> pip2erc = convert(upfac, pip2er);
                    //System.out.println("pip2elc = " + pip2elc);
                    //System.out.println("pip2erc = " + pip2erc);
                    for (List<RealAlgebraicNumber<D>> rx : ran) {
                        //System.out.println("rx = " + rx);
                        RealAlgebraicRing<D> rar = rx.get(ix).ring;
                        //System.out.println("rar = " + rar.toScript());
                        RealAlgebraicNumber<D> rel = new RealAlgebraicNumber<D>(rar, pip2elc);
                        RealAlgebraicNumber<D> rer = new RealAlgebraicNumber<D>(rar, pip2erc);
                        int sl = rel.signum();
                        int sr = rer.signum();
                        //System.out.println("sl = " + sl + ", sr = " + sr + ", sl*sr = " + (sl*sr));
                        if (sl * sr <= 0) {
                            //System.out.println("sl * sr <= 0: rar = " + rar.toScript());
                            List<RealAlgebraicNumber<D>> ry = new ArrayList<RealAlgebraicNumber<D>>();
                            ry.addAll(rx);
                            ry.add(rr);
                            rn.add(ry);
                        }
                    }
                }
            }
            ran = rn;
        }
        if (logger.isInfoEnabled()) {
            for (List<RealAlgebraicNumber<D>> rz : ran) {
                List<Interval<D>> il = new ArrayList<Interval<D>>();
                for (RealAlgebraicNumber<D> rr : rz) {
                    il.add(rr.ring.getRoot());
                }
                logger.info("root-tuple = " + il);
            }
        }
        IdealWithRealAlgebraicRoots<D> Ir = new IdealWithRealAlgebraicRoots<D>(I, ran);
        return Ir;
    }


    /**
     * Construct real roots for zero dimensional ideal(G).
     * @param I list of zero dimensional ideal with univariate irreducible
     *            polynomials and bi-variate polynomials.
     * @return list of real algebraic roots for all ideal(I_i)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithRealAlgebraicRoots<D>> realAlgebraicRoots(
                    List<IdealWithUniv<D>> I) {
        List<IdealWithRealAlgebraicRoots<D>> lir = new ArrayList<IdealWithRealAlgebraicRoots<D>>(I.size());
        for (IdealWithUniv<D> iu : I) {
            IdealWithRealAlgebraicRoots<D> iur = PolyUtilApp.<D> realAlgebraicRoots(iu);
            //System.out.println("iur = " + iur);
            lir.add(iur);
        }
        return lir;
    }


    /**
     * Construct complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal with univariate irreducible polynomials
     *            and bi-variate polynomials.
     * @return complex algebraic roots for ideal(G) <b>Note:</b> implementation
     *         contains errors, do not use.
     */
    public static <D extends GcdRingElem<D> & Rational> IdealWithComplexAlgebraicRoots<D> complexAlgebraicRootsWrong( // Wrong
                    IdealWithUniv<D> I) {
        List<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>> can;
        can = new ArrayList<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>>();
        if (I == null) {
            throw new IllegalArgumentException("null ideal not permitted");
        }
        if (I.ideal == null || I.upolys == null) {
            throw new IllegalArgumentException("null ideal components not permitted " + I);
        }
        if (I.ideal.isZERO() || I.upolys.size() == 0) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        GenPolynomialRing<D> fac = I.ideal.list.ring;
        if (fac.nvar == 0) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        if (fac.nvar != I.upolys.size()) {
            throw new IllegalArgumentException("ideal not zero dimnsional: " + I);
        }
        // case i == 0:
        GenPolynomial<D> p0 = I.upolys.get(0);
        GenPolynomial<D> p0p = PolyUtil.<D> selectWithVariable(I.ideal.list.list, fac.nvar - 1);
        if (p0p == null) {
            throw new RuntimeException("no polynomial found in " + (fac.nvar - 1) + " of  " + I.ideal);
        }
        if (logger.isInfoEnabled()) {
            logger.info("p0  = " + p0);
            logger.info("p0p = " + p0p);
        }
        int[] dep0 = p0p.degreeVector().dependencyOnVariables();
        //System.out.println("dep0 = " + Arrays.toString(dep0));
        if (dep0.length != 1) {
            throw new RuntimeException("wrong number of variables " + Arrays.toString(dep0));
        }
        RingFactory<D> cfac = p0.ring.coFac;
        ComplexRing<D> ccfac = new ComplexRing<D>(cfac);
        GenPolynomialRing<Complex<D>> facc = new GenPolynomialRing<Complex<D>>(ccfac, p0.ring);
        GenPolynomial<Complex<D>> p0c = PolyUtil.<D> complexFromAny(facc, p0);
        List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cra;
        cra = edu.jas.application.RootFactory.<D> complexAlgebraicNumbersSquarefree(p0c);
        logger.info("#roots(p0c) = " + cra.size());
        if (debug) {
            boolean t = edu.jas.application.RootFactory.<D> isRoot(p0c, cra);
            if (!t) {
                throw new RuntimeException("no roots of " + p0c);
            }
        }
        for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
            List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cl;
            cl = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
            cl.add(cr);
            can.add(cl);
        }
        if (fac.nvar == 1) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        // case i > 0:
        for (int i = 1; i < I.upolys.size(); i++) {
            List<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>> cn;
            cn = new ArrayList<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>>();
            GenPolynomial<D> pi = I.upolys.get(i);
            GenPolynomial<D> pip = PolyUtil.selectWithVariable(I.ideal.list.list, fac.nvar - 1 - i);
            if (pip == null) {
                throw new RuntimeException("no polynomial found in " + (fac.nvar - 1 - i) + " of  " + I.ideal);
            }
            if (logger.isInfoEnabled()) {
                logger.info("pi(" + i + ") = " + pi);
                logger.info("pip  = " + pip);
            }
            facc = new GenPolynomialRing<Complex<D>>(ccfac, pi.ring);
            GenPolynomial<Complex<D>> pic = PolyUtil.<D> complexFromAny(facc, pi);
            int[] depi = pip.degreeVector().dependencyOnVariables();
            //System.out.println("depi = " + Arrays.toString(depi));
            if (depi.length < 1 || depi.length > 2) {
                throw new RuntimeException("wrong number of variables " + Arrays.toString(depi) + " for "
                                + pip);
            }
            cra = edu.jas.application.RootFactory.<D> complexAlgebraicNumbersSquarefree(pic);
            logger.info("#roots(pic) = " + cra.size());
            if (debug) {
                boolean t = edu.jas.application.RootFactory.<D> isRoot(pic, cra);
                if (!t) {
                    throw new RuntimeException("no roots of " + pic);
                }
            }
            if (depi.length == 1) {
                // all combinations are roots of the ideal I
                for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
                    //System.out.println("cr.ring = " + cr.ring);
                    for (List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cx : can) {
                        //System.out.println("cx = " + cx);
                        List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cy;
                        cy = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
                        cy.addAll(cx);
                        cy.add(cr);
                        cn.add(cy);
                    }
                }
            } else { // depi.length == 2
                // select roots of the ideal I
                GenPolynomial<D> pip2 = PolyUtil.<D> removeUnusedUpperVariables(pip);
                GenPolynomialRing<GenPolynomial<D>> rfac = pip2.ring.recursive(1);
                GenPolynomialRing<D> ufac = pip2.ring.contract(1);
                GenPolynomialRing<Complex<D>> ucfac = new GenPolynomialRing<Complex<D>>(ccfac, ufac);
                GenPolynomialRing<Complex<D>> c2fac = new GenPolynomialRing<Complex<D>>(ccfac, pip2.ring);
                GenPolynomial<Complex<D>> pip2c = PolyUtil.<D> complexFromAny(c2fac, pip2);
                //System.out.println("pip2c = " + pip2c);
                GenPolynomialRing<GenPolynomial<Complex<D>>> rcfac;
                rcfac = new GenPolynomialRing<GenPolynomial<Complex<D>>>(ucfac, rfac);
                GenPolynomial<GenPolynomial<Complex<D>>> pip2cr = PolyUtil.<Complex<D>> recursive(rcfac,
                                pip2c);
                //System.out.println("pip2cr = " + pip2cr);

                int ix = fac.nvar - 1 - depi[depi.length - 1];
                //System.out.println("ix = " + ix);
                for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
                    System.out.println("cr = " + toString(cr)); // <----------------------------------
                    edu.jas.application.RealAlgebraicRing<D> cring = (edu.jas.application.RealAlgebraicRing<D>) cr.ring.ring;
                    RealRootTuple<D> rroot = cring.getRoot();
                    List<RealAlgebraicNumber<D>> rlist = rroot.tuple;
                    Interval<D> vr = rlist.get(0).ring.getRoot();
                    Interval<D> vi = rlist.get(1).ring.getRoot();
                    logger.info("vr = " + vr + ", vi = " + vi);
                    if (vr.length().isZERO()) {
                        D e = vr.left.factory().parse("1/2");
                        D m = vr.left; //middle();
                        vr = new Interval<D>(m.subtract(e), m.sum(e));
                        logger.info("|vr| == 0: " + vr);
                    }
                    if (vi.length().isZERO()) {
                        D e = vi.left.factory().parse("1/2");
                        D m = vi.left; //middle();
                        vi = new Interval<D>(m.subtract(e), m.sum(e));
                        logger.info("|vi| == 0: " + vi);
                    }
                    Complex<D> sw = new Complex<D>(ccfac, vr.left, vi.left);
                    Complex<D> ne = new Complex<D>(ccfac, vr.right, vi.right);
                    logger.info("sw   = " + toString1(sw) + ", ne   = " + toString1(ne));
                    GenPolynomial<Complex<D>> pip2cesw, pip2cene;
                    pip2cesw = PolyUtil.<Complex<D>> evaluateMainRecursive(ucfac, pip2cr, sw);
                    pip2cene = PolyUtil.<Complex<D>> evaluateMainRecursive(ucfac, pip2cr, ne);
                    GenPolynomialRing<D> upfac = I.upolys.get(ix).ring;
                    GenPolynomialRing<Complex<D>> upcfac = new GenPolynomialRing<Complex<D>>(ccfac, upfac);
                    //System.out.println("upfac = " + upfac);
                    //System.out.println("upcfac = " + upcfac);
                    GenPolynomial<Complex<D>> pip2eswc = convertComplexComplex(upcfac, pip2cesw);
                    GenPolynomial<Complex<D>> pip2enec = convertComplexComplex(upcfac, pip2cene);
                    //System.out.println("pip2eswc = " + pip2eswc);
                    //System.out.println("pip2enec = " + pip2enec);
                    for (List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cx : can) {
                        //System.out.println("cxi = " + toString(cx.get(ix)));
                        Complex<edu.jas.application.RealAlgebraicNumber<D>> cax = cx.get(ix);
                        ComplexRing<edu.jas.application.RealAlgebraicNumber<D>> car = cax.ring;
                        edu.jas.application.RealAlgebraicRing<D> rar = (edu.jas.application.RealAlgebraicRing<D>) car.ring;
                        //System.out.println("car = " + car);
                        //System.out.println("rar = " + rar);
                        TermOrder to = new TermOrder(TermOrder.INVLEX);
                        String vvr = rar.algebraic.ring.getVars()[0];
                        String vvi = rar.algebraic.ring.getVars()[1];
                        String[] vars = new String[] { vvr, vvi };
                        GenPolynomialRing<Complex<D>> tfac = new GenPolynomialRing<Complex<D>>(ccfac, to,
                                        vars);
                        GenPolynomial<Complex<D>> t = tfac.univariate(1, 1L).sum(
                                        tfac.univariate(0, 1L).multiply(ccfac.getIMAG()));
                        //System.out.println("t  = " + t); // t = x + i y
                        GenPolynomialRing<D> rtfac = new GenPolynomialRing<D>(cfac, tfac);
                        GenPolynomial<Complex<D>> su;
                        GenPolynomial<D> re, im;
                        su = PolyUtil.<Complex<D>> substituteUnivariate(pip2eswc, t);
                        //su = su.monic(); not here
                        re = PolyUtil.<D> realPartFromComplex(rtfac, su);
                        im = PolyUtil.<D> imaginaryPartFromComplex(rtfac, su);
                        //System.out.println("re = " + re);
                        //System.out.println("im = " + im);
                        edu.jas.application.RealAlgebraicNumber<D> resw, imsw, rene, imne;
                        resw = new edu.jas.application.RealAlgebraicNumber<D>(rar, re);
                        //System.out.println("resw = " + resw);
                        int sswr = resw.signum();
                        imsw = new edu.jas.application.RealAlgebraicNumber<D>(rar, im);
                        //System.out.println("imsw = " + imsw);
                        int sswi = imsw.signum();
                        su = PolyUtil.<Complex<D>> substituteUnivariate(pip2enec, t);
                        //su = su.monic(); not here
                        re = PolyUtil.<D> realPartFromComplex(rtfac, su);
                        im = PolyUtil.<D> imaginaryPartFromComplex(rtfac, su);
                        //System.out.println("re = " + re);
                        //System.out.println("im = " + im);
                        rene = new edu.jas.application.RealAlgebraicNumber<D>(rar, re);
                        //System.out.println("rene = " + rene);
                        int sner = rene.signum();
                        imne = new edu.jas.application.RealAlgebraicNumber<D>(rar, im);
                        //System.out.println("imne = " + imne);
                        int snei = imne.signum();
                        //System.out.println("sswr = " + sswr + ", sswi = " + sswi);
                        //System.out.println("sner = " + sner + ", snei = " + snei);
                        if ((sswr * sner <= 0 && sswi * snei <= 0)) { // wrong !
                            logger.info("   hit, cxi = " + toString(cx.get(ix)) + ", cr = " + toString(cr));
                            List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cy;
                            cy = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
                            cy.addAll(cx);
                            cy.add(cr);
                            cn.add(cy);
                        } else {
                            logger.info("no hit, cxi = " + toString(cx.get(ix)) + ", cr = " + toString(cr));
                        }
                    }
                }
            }
            can = cn;
        }
        IdealWithComplexAlgebraicRoots<D> Ic = new IdealWithComplexAlgebraicRoots<D>(I, can);
        return Ic;
    }


    /**
     * Construct complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal with univariate irreducible polynomials
     *            and bi-variate polynomials.
     * @return complex algebraic roots for ideal(G) <b>Note:</b> not jet
     *         completed oin all cases.
     */
    public static <D extends GcdRingElem<D> & Rational> IdealWithComplexAlgebraicRoots<D> complexAlgebraicRoots(
                    IdealWithUniv<D> I) {
        List<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>> can;
        can = new ArrayList<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>>();
        if (I == null) {
            throw new IllegalArgumentException("null ideal not permitted");
        }
        if (I.ideal == null || I.upolys == null) {
            throw new IllegalArgumentException("null ideal components not permitted " + I);
        }
        if (I.ideal.isZERO() || I.upolys.size() == 0) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        GenPolynomialRing<D> fac = I.ideal.list.ring;
        if (fac.nvar == 0) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        if (fac.nvar != I.upolys.size()) {
            throw new IllegalArgumentException("ideal not zero dimnsional: " + I);
        }
        // case i == 0:
        GenPolynomial<D> p0 = I.upolys.get(0);
        GenPolynomial<D> p0p = PolyUtil.<D> selectWithVariable(I.ideal.list.list, fac.nvar - 1);
        if (p0p == null) {
            throw new RuntimeException("no polynomial found in " + (fac.nvar - 1) + " of  " + I.ideal);
        }
        if (logger.isInfoEnabled()) {
            logger.info("p0  = " + p0);
            logger.info("p0p = " + p0p);
        }
        int[] dep0 = p0p.degreeVector().dependencyOnVariables();
        //System.out.println("dep0 = " + Arrays.toString(dep0));
        if (dep0.length != 1) {
            throw new RuntimeException("wrong number of variables " + Arrays.toString(dep0));
        }
        RingFactory<D> cfac = p0.ring.coFac;
        ComplexRing<D> ccfac = new ComplexRing<D>(cfac);
        GenPolynomialRing<Complex<D>> facc = new GenPolynomialRing<Complex<D>>(ccfac, p0.ring);
        GenPolynomial<Complex<D>> p0c = PolyUtil.<D> complexFromAny(facc, p0);
        List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cra;
        cra = edu.jas.application.RootFactory.<D> complexAlgebraicNumbersSquarefree(p0c);
        logger.info("#roots(p0c) = " + cra.size());
        for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
            List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cl;
            cl = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
            cl.add(cr);
            can.add(cl);
        }
        if (fac.nvar == 1) {
            return new IdealWithComplexAlgebraicRoots<D>(I, can);
        }
        // case i > 0:
        for (int i = 1; i < I.upolys.size(); i++) {
            List<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>> cn;
            cn = new ArrayList<List<Complex<edu.jas.application.RealAlgebraicNumber<D>>>>();
            GenPolynomial<D> pi = I.upolys.get(i);
            GenPolynomial<D> pip = PolyUtil.selectWithVariable(I.ideal.list.list, fac.nvar - 1 - i);
            if (pip == null) {
                throw new RuntimeException("no polynomial found in " + (fac.nvar - 1 - i) + " of  " + I.ideal);
            }
            if (logger.isInfoEnabled()) {
                logger.info("pi(" + i + ") = " + pi);
                logger.info("pip  = " + pip);
            }
            facc = new GenPolynomialRing<Complex<D>>(ccfac, pi.ring);
            GenPolynomial<Complex<D>> pic = PolyUtil.<D> complexFromAny(facc, pi);
            int[] depi = pip.degreeVector().dependencyOnVariables();
            //System.out.println("depi = " + Arrays.toString(depi));
            if (depi.length < 1 || depi.length > 2) {
                throw new RuntimeException("wrong number of variables " + Arrays.toString(depi) + " for "
                                + pip);
            }
            cra = edu.jas.application.RootFactory.<D> complexAlgebraicNumbersSquarefree(pic);
            logger.info("#roots(pic) = " + cra.size());
            if (depi.length == 1) {
                // all combinations are roots of the ideal I
                for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
                    //System.out.println("cr.ring = " + cr.ring);
                    for (List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cx : can) {
                        //System.out.println("cx = " + cx);
                        List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cy;
                        cy = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
                        cy.addAll(cx);
                        cy.add(cr);
                        cn.add(cy);
                    }
                }
            } else { // depi.length == 2
                // select roots of the ideal I
                GenPolynomial<D> pip2 = PolyUtil.<D> removeUnusedUpperVariables(pip);
                pip2 = PolyUtil.<D> removeUnusedLowerVariables(pip2);
                pip2 = PolyUtil.<D> removeUnusedMiddleVariables(pip2);
                GenPolynomialRing<GenPolynomial<D>> rfac = pip2.ring.recursive(1);
                GenPolynomialRing<D> ufac = pip2.ring.contract(1);
                GenPolynomialRing<Complex<D>> ucfac = new GenPolynomialRing<Complex<D>>(ccfac, ufac);
                GenPolynomialRing<Complex<D>> c2fac = new GenPolynomialRing<Complex<D>>(ccfac, pip2.ring);
                GenPolynomial<Complex<D>> pip2c = PolyUtil.<D> complexFromAny(c2fac, pip2);
                GenPolynomialRing<GenPolynomial<Complex<D>>> rcfac;
                rcfac = new GenPolynomialRing<GenPolynomial<Complex<D>>>(ucfac, rfac);
                GenPolynomial<GenPolynomial<Complex<D>>> pip2cr = PolyUtil.<Complex<D>> recursive(rcfac,
                                pip2c);
                //System.out.println("pip2cr = " + pip2cr);
                int ix = fac.nvar - 1 - depi[depi.length - 1];
                //System.out.println("ix = " + ix);
                for (Complex<edu.jas.application.RealAlgebraicNumber<D>> cr : cra) {
                    //System.out.println("cr = " + toString(cr)); 
                    edu.jas.application.RealAlgebraicRing<D> cring = (edu.jas.application.RealAlgebraicRing<D>) cr.ring.ring;
                    RealRootTuple<D> rroot = cring.getRoot();
                    List<RealAlgebraicNumber<D>> rlist = rroot.tuple;
                    //System.out.println("rlist = " + rlist);
                    Interval<D> vr = rlist.get(0).ring.getRoot();
                    Interval<D> vi = rlist.get(1).ring.getRoot();
                    //logger.info("vr = " + vr + ", vi = " + vi);
                    edu.jas.application.RealAlgebraicNumber<D> vrl, vil, vrr, vir;
                    vrl = new edu.jas.application.RealAlgebraicNumber<D>(cring, vr.left);
                    vil = new edu.jas.application.RealAlgebraicNumber<D>(cring, vi.left);
                    vrr = new edu.jas.application.RealAlgebraicNumber<D>(cring, vr.right);
                    vir = new edu.jas.application.RealAlgebraicNumber<D>(cring, vi.right);
                    ComplexRing<edu.jas.application.RealAlgebraicNumber<D>> crr;
                    crr = new ComplexRing<edu.jas.application.RealAlgebraicNumber<D>>(cring);
                    Complex<edu.jas.application.RealAlgebraicNumber<D>> csw, cne;
                    csw = new Complex<edu.jas.application.RealAlgebraicNumber<D>>(crr, vrl, vil);
                    cne = new Complex<edu.jas.application.RealAlgebraicNumber<D>>(crr, vrr, vir);
                    //logger.info("csw  = " + toString(csw)   + ", cne  = " + toString(cne));
                    Rectangle<edu.jas.application.RealAlgebraicNumber<D>> rec;
                    rec = new Rectangle<edu.jas.application.RealAlgebraicNumber<D>>(csw, cne);
                    //System.out.println("rec = " + rec);
                    for (List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cx : can) {
                        Complex<edu.jas.application.RealAlgebraicNumber<D>> cax = cx.get(ix);
                        //System.out.println("cax = " + toString(cax));
                        ComplexRing<edu.jas.application.RealAlgebraicNumber<D>> car = cax.ring;
                        //System.out.println("car = " + car);
                        GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<D>>> pcrfac;
                        pcrfac = new GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<D>>>(
                                        car, rcfac);
                        GenPolynomial<Complex<edu.jas.application.RealAlgebraicNumber<D>>> pcr;
                        pcr = evaluateToComplexRealCoefficients(pcrfac, pip2cr, cax);
                        //System.out.println("pcr = " + pcr);
                        ComplexRoots<edu.jas.application.RealAlgebraicNumber<D>> rengine;
                        rengine = new ComplexRootsSturm<edu.jas.application.RealAlgebraicNumber<D>>(car);
                        long nr = 0;
                        try {
                            nr = rengine.complexRootCount(rec, pcr);
                            //logger.info("rootCount = " + nr);
                        } catch (InvalidBoundaryException e) {
                            e.printStackTrace();
                        }
                        if (nr == 1) { // one root
                            logger.info("   hit, cxi = " + toString(cx.get(ix)) + ", cr = " + toString(cr));
                            List<Complex<edu.jas.application.RealAlgebraicNumber<D>>> cy;
                            cy = new ArrayList<Complex<edu.jas.application.RealAlgebraicNumber<D>>>();
                            cy.addAll(cx);
                            cy.add(cr);
                            cn.add(cy);
                        } else if (nr > 1) {
                            logger.error("to many roots, cxi = " + toString(cx.get(ix)) + ", cr = "
                                            + toString(cr));
                        } else { // no root
                            logger.info("no hit, cxi = " + toString(cx.get(ix)) + ", cr = " + toString(cr));
                        }
                    }
                }
            }
            can = cn;
        }
        IdealWithComplexAlgebraicRoots<D> Ic = new IdealWithComplexAlgebraicRoots<D>(I, can);
        return Ic;
    }


    /**
     * String representation of a deximal approximation of a complex number.
     * @param c compelx number.
     * @return String representation of c
     */
    public static <D extends GcdRingElem<D> & Rational> String toString(
                    Complex<edu.jas.application.RealAlgebraicNumber<D>> c) {
        edu.jas.application.RealAlgebraicNumber<D> re = c.getRe();
        edu.jas.application.RealAlgebraicNumber<D> im = c.getIm();
        String s = re.decimalMagnitude().toString();
        if (!im.isZERO()) {
            s = s + "i" + im.decimalMagnitude();
        }
        return s;
    }


    /**
     * String representation of a deximal approximation of a complex number.
     * @param c compelx number.
     * @return String representation of c
     */
    public static <D extends GcdRingElem<D> & Rational> String toString1(Complex<D> c) {
        D re = c.getRe();
        D im = c.getIm();
        String s = new BigDecimal(re.getRational()).toString();
        if (!im.isZERO()) {
            s = s + "i" + new BigDecimal(im.getRational());
        }
        return s;
    }


    /**
     * Construct complex roots for zero dimensional ideal(G).
     * @param I list of zero dimensional ideal with univariate irreducible
     *            polynomials and bi-variate polynomials.
     * @return list of complex algebraic roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithComplexAlgebraicRoots<D>> complexAlgebraicRoots(
                    List<IdealWithUniv<D>> I) {
        List<IdealWithComplexAlgebraicRoots<D>> lic = new ArrayList<IdealWithComplexAlgebraicRoots<D>>();
        for (IdealWithUniv<D> iu : I) {
            IdealWithComplexAlgebraicRoots<D> iuc = PolyUtilApp.<D> complexAlgebraicRoots(iu);
            //System.out.println("iuc = " + iuc);
            lic.add(iuc);
        }
        return lic;
    }


    /**
     * Construct exact set of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithComplexAlgebraicRoots<D>> complexAlgebraicRoots(
                    Ideal<D> I) {
        List<IdealWithUniv<D>> Ir = I.zeroDimRootDecomposition();
        //System.out.println("Ir = " + Ir);
        List<IdealWithComplexAlgebraicRoots<D>> roots = PolyUtilApp.<D> complexAlgebraicRoots(Ir);
        return roots;
    }


    /*
     * Convert to a polynomial in given ring.
     * @param fac result polynomial ring.
     * @param p polynomial.
     * @return polynomial in ring fac <b>Note: </b> if p can not be represented
     *         in fac then the results are unpredictable.
     */
    static <C extends RingElem<C>> GenPolynomial<C> convert(GenPolynomialRing<C> fac, GenPolynomial<C> p) {
        if (fac.equals(p.factory())) {
            return p;
        }
        GenPolynomial<C> q = fac.parse(p.toString());
        if (!q.toString().equals(p.toString())) {
            throw new RuntimeException("convert(" + p + ") = " + q);
        }
        return q;
    }


    /*
     * Convert to a polynomial in given ring.
     * @param fac result polynomial ring.
     * @param p polynomial.
     * @return polynomial in ring fac <b>Note: </b> if p can not be represented
     *         in fac then the results are unpredictable.
     */
    static <C extends RingElem<C>> GenPolynomial<Complex<C>> convertComplex(
                    GenPolynomialRing<Complex<C>> fac, GenPolynomial<C> p) {
        GenPolynomial<Complex<C>> q = fac.parse(p.toString());
        if (!q.toString().equals(p.toString())) {
            throw new RuntimeException("convert(" + p + ") = " + q);
        }
        return q;
    }


    /*
     * Convert to a polynomial in given ring.
     * @param fac result polynomial ring.
     * @param p complex polynomial.
     * @return polynomial in ring fac <b>Note: </b> if p can not be represented
     *         in fac then the results are unpredictable.
     */
    static <C extends RingElem<C>> GenPolynomial<Complex<C>> convertComplexComplex(
                    GenPolynomialRing<Complex<C>> fac, GenPolynomial<Complex<C>> p) {
        if (fac.equals(p.factory())) {
            return p;
        }
        GenPolynomial<Complex<C>> q = fac.parse(p.toString());
        if (!q.toString().equals(p.toString())) {
            throw new RuntimeException("convert(" + p + ") = " + q);
        }
        return q;
    }


    /**
     * Construct exact set of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <D extends GcdRingElem<D> & Rational> List<IdealWithRealAlgebraicRoots<D>> realAlgebraicRoots(
                    Ideal<D> I) {
        List<IdealWithUniv<D>> Ir = I.zeroDimRootDecomposition();
        //System.out.println("Ir = " + Ir);
        List<IdealWithRealAlgebraicRoots<D>> roots = PolyUtilApp.<D> realAlgebraicRoots(Ir);
        return roots;
    }


    /**
     * Construct primitive element for double field extension.
     * @param a algebraic number ring with squarefree monic minimal polynomial
     * @param b algebraic number ring with squarefree monic minimal polynomial
     * @return primitive element container with algebraic number ring c, with
     *         Q(c) = Q(a,b)
     */
    public static <C extends GcdRingElem<C>> PrimitiveElement<C> primitiveElement(AlgebraicNumberRing<C> a,
                    AlgebraicNumberRing<C> b) {
        GenPolynomial<C> ap = a.modul;
        GenPolynomial<C> bp = b.modul;

        // setup bivariate polynomial ring
        String[] cv = new String[2];
        cv[0] = ap.ring.getVars()[0];
        cv[1] = bp.ring.getVars()[0];
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(ap.ring.coFac, 2, to, cv);
        GenPolynomial<C> as = ap.extendUnivariate(cfac, 0);
        GenPolynomial<C> bs = bp.extendUnivariate(cfac, 1);
        List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>(2);
        L.add(as);
        L.add(bs);
        List<GenPolynomial<C>> Op = new ArrayList<GenPolynomial<C>>();

        Ideal<C> id = new Ideal<C>(cfac, L);
        //System.out.println("id = " + id);
        IdealWithUniv<C> iu = id.normalPositionFor(0, 1, Op);
        //System.out.println("iu = " + iu);

        // extract result polynomials
        List<GenPolynomial<C>> Np = iu.ideal.getList();
        //System.out.println("Np = " + Np);
        as = PolyUtil.<C> selectWithVariable(Np, 1);
        bs = PolyUtil.<C> selectWithVariable(Np, 0);
        GenPolynomial<C> cs = PolyUtil.<C> selectWithVariable(Np, 2);
        //System.out.println("as = " + as);
        //System.out.println("bs = " + bs);
        //System.out.println("cs = " + cs);
        String[] ev = new String[] { cs.ring.getVars()[0] };
        GenPolynomialRing<C> efac = new GenPolynomialRing<C>(ap.ring.coFac, 1, to, ev);
        //System.out.println("efac = " + efac);
        cs = cs.contractCoeff(efac);
        //System.out.println("cs = " + cs);
        as = as.reductum().contractCoeff(efac);
        as = as.negate();
        //System.out.println("as = " + as);
        bs = bs.reductum().contractCoeff(efac);
        bs = bs.negate();
        //System.out.println("bs = " + bs);
        AlgebraicNumberRing<C> c = new AlgebraicNumberRing<C>(cs);
        AlgebraicNumber<C> ab = new AlgebraicNumber<C>(c, as);
        AlgebraicNumber<C> bb = new AlgebraicNumber<C>(c, bs);
        PrimitiveElement<C> pe = new PrimitiveElement<C>(c, ab, bb, a, b);
        if (logger.isInfoEnabled()) {
            logger.info("primitive element = " + c);
        }
        return pe;
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the
     *            new ring.
     * @param a algebraic number to convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> AlgebraicNumber<C> convertToPrimitiveElem(
                    AlgebraicNumberRing<C> cfac, AlgebraicNumber<C> A, AlgebraicNumber<C> a) {
        GenPolynomialRing<C> aufac = a.ring.ring;
        GenPolynomialRing<AlgebraicNumber<C>> ar = new GenPolynomialRing<AlgebraicNumber<C>>(cfac, aufac);
        GenPolynomial<AlgebraicNumber<C>> aps = PolyUtil.<C> convertToAlgebraicCoefficients(ar, a.val);
        AlgebraicNumber<C> ac = PolyUtil.<AlgebraicNumber<C>> evaluateMain(cfac, aps, A);
        return ac;
    }


    /**
     * Convert coefficients to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the
     *            new ring.
     * @param a polynomial with coefficients algebraic number to convert.
     * @return a with coefficients converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<AlgebraicNumber<C>> convertToPrimitiveElem(
                    AlgebraicNumberRing<C> cfac, AlgebraicNumber<C> A, GenPolynomial<AlgebraicNumber<C>> a) {
        GenPolynomialRing<AlgebraicNumber<C>> cr = new GenPolynomialRing<AlgebraicNumber<C>>(cfac, a.ring);
        return PolyUtil.<AlgebraicNumber<C>, AlgebraicNumber<C>> map(cr, a, new CoeffConvertAlg<C>(cfac, A));
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the
     *            new ring.
     * @param a recursive algebraic number to convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> AlgebraicNumber<C> convertToPrimitiveElem(
                    AlgebraicNumberRing<C> cfac, AlgebraicNumber<C> A, AlgebraicNumber<C> B,
                    AlgebraicNumber<AlgebraicNumber<C>> a) {
        GenPolynomial<AlgebraicNumber<C>> aps = PolyUtilApp.<C> convertToPrimitiveElem(cfac, A, a.val);
        AlgebraicNumber<C> ac = PolyUtil.<AlgebraicNumber<C>> evaluateMain(cfac, aps, B);
        return ac;
    }


    /**
     * Construct primitive element for double field extension.
     * @param b algebraic number ring with squarefree monic minimal polynomial
     *            over Q(a)
     * @return primitive element container with algebraic number ring c, with
     *         Q(c) = Q(a)(b)
     */
    public static <C extends GcdRingElem<C>> PrimitiveElement<C> primitiveElement(
                    AlgebraicNumberRing<AlgebraicNumber<C>> b) {
        GenPolynomial<AlgebraicNumber<C>> bp = b.modul;
        AlgebraicNumberRing<C> a = (AlgebraicNumberRing<C>) b.ring.coFac;
        GenPolynomial<C> ap = a.modul;

        // setup bivariate polynomial ring
        String[] cv = new String[2];
        cv[0] = ap.ring.getVars()[0];
        cv[1] = bp.ring.getVars()[0];
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(ap.ring.coFac, 2, to, cv);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(a.ring, 1,
                        bp.ring.getVars());
        GenPolynomial<C> as = ap.extendUnivariate(cfac, 0);
        GenPolynomial<GenPolynomial<C>> bss = PolyUtil.<C> fromAlgebraicCoefficients(rfac, bp);
        GenPolynomial<C> bs = PolyUtil.<C> distribute(cfac, bss);
        List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>(2);
        L.add(as);
        L.add(bs);
        List<GenPolynomial<C>> Op = new ArrayList<GenPolynomial<C>>();

        Ideal<C> id = new Ideal<C>(cfac, L);
        //System.out.println("id = " + id);
        IdealWithUniv<C> iu = id.normalPositionFor(0, 1, Op);
        //System.out.println("iu = " + iu);

        // extract result polynomials
        List<GenPolynomial<C>> Np = iu.ideal.getList();
        as = PolyUtil.<C> selectWithVariable(Np, 1);
        bs = PolyUtil.<C> selectWithVariable(Np, 0);
        GenPolynomial<C> cs = PolyUtil.<C> selectWithVariable(Np, 2);
        //System.out.println("as = " + as);
        //System.out.println("bs = " + bs);
        //System.out.println("cs = " + cs);
        String[] ev = new String[] { cs.ring.getVars()[0] };
        GenPolynomialRing<C> efac = new GenPolynomialRing<C>(ap.ring.coFac, 1, to, ev);
        // System.out.println("efac = " + efac);
        cs = cs.contractCoeff(efac);
        // System.out.println("cs = " + cs);
        as = as.reductum().contractCoeff(efac);
        as = as.negate();
        // System.out.println("as = " + as);
        bs = bs.reductum().contractCoeff(efac);
        bs = bs.negate();
        //System.out.println("bs = " + bs);
        AlgebraicNumberRing<C> c = new AlgebraicNumberRing<C>(cs);
        AlgebraicNumber<C> ab = new AlgebraicNumber<C>(c, as);
        AlgebraicNumber<C> bb = new AlgebraicNumber<C>(c, bs);
        PrimitiveElement<C> pe = new PrimitiveElement<C>(c, ab, bb); // missing ,a,b);
        if (logger.isInfoEnabled()) {
            logger.info("primitive element = " + pe);
        }
        return pe;
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the
     *            new ring.
     * @param a polynomial with recursive algebraic number coefficients to
     *            convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<AlgebraicNumber<C>> convertToPrimitiveElem(
                    AlgebraicNumberRing<C> cfac, AlgebraicNumber<C> A, AlgebraicNumber<C> B,
                    GenPolynomial<AlgebraicNumber<AlgebraicNumber<C>>> a) {
        GenPolynomialRing<AlgebraicNumber<C>> cr = new GenPolynomialRing<AlgebraicNumber<C>>(cfac, a.ring);
        return PolyUtil.<AlgebraicNumber<AlgebraicNumber<C>>, AlgebraicNumber<C>> map(cr, a,
                        new CoeffRecConvertAlg<C>(cfac, A, B));
    }


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients from package
     * 
     * <pre>
     * edu.jas.root
     * </pre>
     * 
     * .
     * @param afac result polynomial factory.
     * @param A polynomial with RealAlgebraicNumber&lt;C&gt; coefficients to be
     *            converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>> realAlgFromRealCoefficients(
                    GenPolynomialRing<edu.jas.root.RealAlgebraicNumber<C>> afac,
                    GenPolynomial<edu.jas.application.RealAlgebraicNumber<C>> A) {
        edu.jas.root.RealAlgebraicRing<C> cfac = (edu.jas.root.RealAlgebraicRing<C>) afac.coFac;
        return PolyUtil.<edu.jas.application.RealAlgebraicNumber<C>, edu.jas.root.RealAlgebraicNumber<C>> map(
                        afac, A, new ReAlgFromRealCoeff<C>(cfac));
    }


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients from package
     * 
     * <pre>
     * edu.jas.application
     * </pre>
     * 
     * .
     * @param rfac result polynomial factory.
     * @param A polynomial with RealAlgebraicNumber&lt;C&gt; coefficients to be
     *            converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<edu.jas.application.RealAlgebraicNumber<C>> realFromRealAlgCoefficients(
                    GenPolynomialRing<edu.jas.application.RealAlgebraicNumber<C>> rfac,
                    GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>> A) {
        edu.jas.application.RealAlgebraicRing<C> cfac = (edu.jas.application.RealAlgebraicRing<C>) rfac.coFac;
        return PolyUtil.<edu.jas.root.RealAlgebraicNumber<C>, edu.jas.application.RealAlgebraicNumber<C>> map(
                        rfac, A, new RealFromReAlgCoeff<C>(cfac));
    }


    /**
     * Convert to Complex&lt;RealAlgebraicNumber&gt; coefficients. Represent as
     * polynomial with Complex&lt;RealAlgebraicNumber&gt; coefficients, C is
     * e.g. BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with Complex coefficients to be converted.
     * @return polynomial with Complex&lt;RealAlgebraicNumber&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<Complex<edu.jas.application.RealAlgebraicNumber<C>>> convertToComplexRealCoefficients(
                    GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<C>>> pfac,
                    GenPolynomial<Complex<C>> A) {
        ComplexRing<edu.jas.application.RealAlgebraicNumber<C>> afac;
        afac = (ComplexRing<edu.jas.application.RealAlgebraicNumber<C>>) pfac.coFac;
        return PolyUtil.<Complex<C>, Complex<edu.jas.application.RealAlgebraicNumber<C>>> map(pfac, A,
                        new CoeffToComplexReal<C>(afac));
    }


    /**
     * Evaluate to Complex&lt;RealAlgebraicNumber&gt; coefficients. Represent as
     * polynomial with Complex&lt;RealAlgebraicNumber&gt; coefficients, C is
     * e.g. BigRational.
     * @param pfac result polynomial factory.
     * @param A = A(x,Y) a recursive polynomial with
     *            GenPolynomial&lt;Complex&gt; coefficients to be converted.
     * @param r Complex&lt;RealAlgebraicNumber&gt; to be evaluated at.
     * @return A(r,Y), a polynomial with Complex&lt;RealAlgebraicNumber&gt;
     *         coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<Complex<edu.jas.application.RealAlgebraicNumber<C>>> evaluateToComplexRealCoefficients(
                    GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<C>>> pfac,
                    GenPolynomial<GenPolynomial<Complex<C>>> A,
                    Complex<edu.jas.application.RealAlgebraicNumber<C>> r) {
        return PolyUtil.<GenPolynomial<Complex<C>>, Complex<edu.jas.application.RealAlgebraicNumber<C>>> map(
                        pfac, A, new EvaluateToComplexReal<C>(pfac, r));
    }


}


/**
 * Coefficient to convert algebriac functor.
 */
class CoeffConvertAlg<C extends GcdRingElem<C>> implements
                UnaryFunctor<AlgebraicNumber<C>, AlgebraicNumber<C>> {


    final protected AlgebraicNumberRing<C> afac;


    final protected AlgebraicNumber<C> A;


    public CoeffConvertAlg(AlgebraicNumberRing<C> fac, AlgebraicNumber<C> a) {
        if (fac == null || a == null) {
            throw new IllegalArgumentException("fac and a must not be null");
        }
        afac = fac;
        A = a;
    }


    public AlgebraicNumber<C> eval(AlgebraicNumber<C> c) {
        if (c == null) {
            return afac.getZERO();
        }
        return PolyUtilApp.<C> convertToPrimitiveElem(afac, A, c);
    }
}


/**
 * Coefficient recursive to convert algebriac functor.
 */
class CoeffRecConvertAlg<C extends GcdRingElem<C>> implements
                UnaryFunctor<AlgebraicNumber<AlgebraicNumber<C>>, AlgebraicNumber<C>> {


    final protected AlgebraicNumberRing<C> afac;


    final protected AlgebraicNumber<C> A;


    final protected AlgebraicNumber<C> B;


    public CoeffRecConvertAlg(AlgebraicNumberRing<C> fac, AlgebraicNumber<C> a, AlgebraicNumber<C> b) {
        if (fac == null || a == null || b == null) {
            throw new IllegalArgumentException("fac, a and b must not be null");
        }
        afac = fac;
        A = a;
        B = b;
    }


    public AlgebraicNumber<C> eval(AlgebraicNumber<AlgebraicNumber<C>> c) {
        if (c == null) {
            return afac.getZERO();
        }
        return PolyUtilApp.<C> convertToPrimitiveElem(afac, A, B, c);
    }
}


/**
 * Coefficient to real algebriac from real algebraic functor.
 */
class ReAlgFromRealCoeff<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<edu.jas.application.RealAlgebraicNumber<C>, edu.jas.root.RealAlgebraicNumber<C>> {


    final protected edu.jas.root.RealAlgebraicRing<C> afac;


    public ReAlgFromRealCoeff(edu.jas.root.RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
    }


    public edu.jas.root.RealAlgebraicNumber<C> eval(edu.jas.application.RealAlgebraicNumber<C> c) {
        if (c == null) {
            return afac.getZERO();
        }
        return (edu.jas.root.RealAlgebraicNumber<C>) (Object) c.number; // force ignore recursion
    }
}


/**
 * Coefficient to real algebriac from algebraic functor.
 */
class RealFromReAlgCoeff<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<edu.jas.root.RealAlgebraicNumber<C>, edu.jas.application.RealAlgebraicNumber<C>> {


    final protected edu.jas.application.RealAlgebraicRing<C> rfac;


    public RealFromReAlgCoeff(edu.jas.application.RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        rfac = fac;
    }


    public edu.jas.application.RealAlgebraicNumber<C> eval(edu.jas.root.RealAlgebraicNumber<C> c) {
        if (c == null) {
            return rfac.getZERO();
        }
        edu.jas.root.RealAlgebraicNumber<edu.jas.root.RealAlgebraicNumber<C>> rrc = (edu.jas.root.RealAlgebraicNumber<edu.jas.root.RealAlgebraicNumber<C>>) (Object) c; // force resurrect recursion
        return new edu.jas.application.RealAlgebraicNumber<C>(rfac, rrc);
    }
}


/**
 * Coefficient to complex real algebriac functor.
 */
class CoeffToComplexReal<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<Complex<C>, Complex<edu.jas.application.RealAlgebraicNumber<C>>> {


    final protected ComplexRing<edu.jas.application.RealAlgebraicNumber<C>> cfac;


    final edu.jas.application.RealAlgebraicRing<C> afac;


    final GenPolynomialRing<C> pfac;


    public CoeffToComplexReal(ComplexRing<edu.jas.application.RealAlgebraicNumber<C>> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        cfac = fac;
        afac = (edu.jas.application.RealAlgebraicRing<C>) cfac.ring;
        pfac = afac.univs.ideal.getRing();
    }


    public Complex<edu.jas.application.RealAlgebraicNumber<C>> eval(Complex<C> c) {
        if (c == null) {
            return cfac.getZERO();
        }
        GenPolynomial<C> pr, pi;
        pr = new GenPolynomial<C>(pfac, c.getRe());
        pi = new GenPolynomial<C>(pfac, c.getIm());
        //System.out.println("pr = " + pr);
        //System.out.println("pi = " + pi);
        edu.jas.application.RealAlgebraicNumber<C> re, im;
        re = new edu.jas.application.RealAlgebraicNumber<C>(afac, pr);
        im = new edu.jas.application.RealAlgebraicNumber<C>(afac, pi);
        //System.out.println("re = " + re);
        //System.out.println("im = " + im);
        return new Complex<edu.jas.application.RealAlgebraicNumber<C>>(cfac, re, im);
    }
}


/**
 * Polynomial coefficient to complex real algebriac evaluation functor.
 */
class EvaluateToComplexReal<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<GenPolynomial<Complex<C>>, Complex<edu.jas.application.RealAlgebraicNumber<C>>> {


    final protected GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<C>>> pfac;


    final protected ComplexRing<edu.jas.application.RealAlgebraicNumber<C>> cfac;


    final protected Complex<edu.jas.application.RealAlgebraicNumber<C>> root;


    public EvaluateToComplexReal(GenPolynomialRing<Complex<edu.jas.application.RealAlgebraicNumber<C>>> fac,
                    Complex<edu.jas.application.RealAlgebraicNumber<C>> r) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        pfac = fac;
        cfac = (ComplexRing<edu.jas.application.RealAlgebraicNumber<C>>) fac.coFac;
        root = r;
        //System.out.println("cfac  = " + cfac);
        //System.out.println("root  = " + root);
    }


    public Complex<edu.jas.application.RealAlgebraicNumber<C>> eval(GenPolynomial<Complex<C>> c) {
        if (c == null) {
            return cfac.getZERO();
        }
        //System.out.println("c  = " + c);
        GenPolynomial<Complex<edu.jas.application.RealAlgebraicNumber<C>>> cp;
        cp = PolyUtilApp.<C> convertToComplexRealCoefficients(pfac, c);
        Complex<edu.jas.application.RealAlgebraicNumber<C>> cr;
        cr = PolyUtil.<Complex<edu.jas.application.RealAlgebraicNumber<C>>> evaluateMain(cfac, cp, root);
        return cr;
    }
}
