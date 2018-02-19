/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory. Generate real and complex algebraic numbers from root
 * intervals or rectangles.
 *
 * @author Heinz Kredel
 */
public class RootFactory {


    /**
     * Is real algebraic number a root of a polynomial.
     *
     * @param f univariate polynomial.
     * @param r real algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRoot(GenPolynomial<C> f,
                                                                       RealAlgebraicNumber<C> r) {
        RealAlgebraicRing<C> rr = r.factory();
        GenPolynomialRing<RealAlgebraicNumber<C>> rfac = new GenPolynomialRing<RealAlgebraicNumber<C>>(rr,
                f.factory());
        GenPolynomial<RealAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C>convertToRealCoefficients(rfac, f);
        RealAlgebraicNumber<C> a = PolyUtil.<RealAlgebraicNumber<C>>evaluateMain(rr, p, r);
        return a.isZERO();
    }


    /**
     * Real algebraic numbers.
     *
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbers(
            GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C>getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>, Long> SF = engine.squarefreeFactors(f);
        //Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<C>, Long> me : SF.entrySet()) {
            GenPolynomial<C> sp = me.getKey();
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers.
     *
     * @param f   univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbers(
            GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C>getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>, Long> SF = engine.squarefreeFactors(f);
        //Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<C>, Long> me : SF.entrySet()) {
            GenPolynomial<C> sp = me.getKey();
            List<Interval<C>> iv = rr.realRoots(sp, eps);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     *
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(
            GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C>getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>, Long> SF = engine.baseFactors(f);
        //Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<C>, Long> me : SF.entrySet()) {
            GenPolynomial<C> sp = me.getKey();
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     *
     * @param f   univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(
            GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C>getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>, Long> SF = engine.baseFactors(f);
        //Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<C>, Long> me : SF.entrySet()) {
            GenPolynomial<C> sp = me.getKey();
            List<Interval<C>> iv = rr.realRoots(sp, eps);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a irreducible polynomial.
     *
     * @param f univariate irreducible polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(
            GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        List<Interval<C>> iv = rr.realRoots(f);
        for (Interval<C> I : iv) {
            RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(f, I, true);//field
            RealAlgebraicNumber<C> rn = rar.getGenerator();
            list.add(rn);
        }
        return list;
    }


    /**
     * Real algebraic numbers from a irreducible polynomial.
     *
     * @param f   univariate irreducible polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(
            GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        List<Interval<C>> iv = rr.realRoots(f, eps);
        for (Interval<C> I : iv) {
            RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(f, I, true);//field
            rar.setEps(eps);
            RealAlgebraicNumber<C> rn = rar.getGenerator();
            list.add(rn);
        }
        return list;
    }


    /**
     * Is complex algebraic number a root of a polynomial.
     *
     * @param f univariate polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRoot(GenPolynomial<C> f,
                                                                       ComplexAlgebraicNumber<C> r) {
        ComplexAlgebraicRing<C> cr = r.factory();
        GenPolynomialRing<ComplexAlgebraicNumber<C>> cfac = new GenPolynomialRing<ComplexAlgebraicNumber<C>>(
                cr, f.factory());
        GenPolynomial<ComplexAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C>convertToComplexCoefficients(cfac, f);
        ComplexAlgebraicNumber<C> a = PolyUtil.<ComplexAlgebraicNumber<C>>evaluateMain(cr, p, r);
        return a.isZERO();
    }


    /**
     * Is complex algebraic number a root of a complex polynomial.
     *
     * @param f univariate complex polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRootComplex(GenPolynomial<Complex<C>> f,
                                                                              ComplexAlgebraicNumber<C> r) {
        ComplexAlgebraicRing<C> cr = r.factory();
        GenPolynomialRing<ComplexAlgebraicNumber<C>> cfac = new GenPolynomialRing<ComplexAlgebraicNumber<C>>(
                cr, f.factory());
        GenPolynomial<ComplexAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C>convertToComplexCoefficientsFromComplex(cfac, f);
        ComplexAlgebraicNumber<C> a = PolyUtil.<ComplexAlgebraicNumber<C>>evaluateMain(cr, p, r);
        return a.isZERO();
    }


    /**
     * Is complex algebraic number a real root of a polynomial.
     *
     * @param f univariate polynomial.
     * @param c complex algebraic number.
     * @param r real algebraic number.
     * @return true, if f(c) == 0 and c == r, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRealRoot(GenPolynomial<C> f,
                                                                           ComplexAlgebraicNumber<C> c, RealAlgebraicNumber<C> r) {
        boolean t = isRoot(f, c) && isRoot(f, r);
        if (!t) {
            return t;
        }
        Rectangle<C> rc = c.ring.root;
        Interval<C> ivci = new Interval<C>(rc.getSW().getIm(), rc.getNE().getIm());
        t = ivci.contains(f.ring.coFac.getZERO());
        if (!t) {
            return t;
        }
        //System.out.println("imag = 0");
        Interval<C> ivc = new Interval<C>(rc.getSW().getRe(), rc.getNE().getRe());
        Interval<C> ivr = r.ring.root;
        // disjoint intervals
        if (ivc.right.compareTo(ivr.left) < 0 || ivr.right.compareTo(ivc.left) < 0) {
            //System.out.println("disjoint: ivc = " + ivc + ", ivr = " + ivr);
            return false;
        }
        //System.out.println("not disjoint");
        // full containement
        t = ivc.contains(ivr) || ivr.contains(ivc);
        if (t) {
            return t;
        }
        //System.out.println("with overlap");
        // overlap, refine to smaller interval
        C left = ivc.left;
        if (left.compareTo(ivr.left) > 0) {
            left = ivr.left;
        }
        C right = ivc.right;
        if (right.compareTo(ivr.right) < 0) {
            right = ivr.right;
        }
        Interval<C> ref = new Interval<C>(left, right);
        //System.out.println("refined interval " + ref);
        RealRoots<C> reng = r.ring.engine; //new RealRootsSturm<C>(); 
        long z = reng.realRootCount(ref, f);
        if (z != 1) {
            return false;
        }
        ComplexRing<C> cr = rc.getSW().ring;
        Complex<C> sw = new Complex<C>(cr, left, rc.getSW().getIm());
        Complex<C> ne = new Complex<C>(cr, right, rc.getNE().getIm());
        //Complex<C> sw = new Complex<C>(cr, left, cr.ring.getZERO());
        //Complex<C> ne = new Complex<C>(cr, right, cr.ring.getZERO());
        Rectangle<C> rec = new Rectangle<C>(sw, ne);
        //System.out.println("refined rectangle " + rec);
        ComplexRoots<C> ceng = c.ring.engine; //new ComplexRootsSturm<C>(); 
        GenPolynomial<Complex<C>> p = PolyUtilRoot.<C>complexFromAny(f);
        try {
            z = ceng.complexRootCount(rec, p);
        } catch (InvalidBoundaryException e) {
            System.out.println("should not happen, rec = " + rec + ", p = " + p);
            e.printStackTrace();
            z = 0;
        }
        //System.out.println("complexRootCount: " + z);
        if (z != 1) {
            return false;
        }
        return true;
    }


    /**
     * Is complex decimal number a real root of a polynomial.
     *
     * @param f   univariate polynomial.
     * @param c   complex decimal number.
     * @param r   real decimal number.
     * @param eps desired precision.
     * @return true, if f(c) == 0 and c == r, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRealRoot(GenPolynomial<C> f,
                                                                           Complex<BigDecimal> c, BigDecimal r, BigRational eps) {
        BigDecimal e = new BigDecimal(eps);
        if (c.getIm().abs().compareTo(e) <= 0) {
            if (c.getRe().subtract(r).abs().compareTo(e) <= 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * Complex algebraic numbers.
     *
     * @param f univariate polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(
            GenPolynomial<Complex<C>> f) {
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
                .<Complex<C>>getImplementation(f.ring.coFac);
        Map<GenPolynomial<Complex<C>>, Long> SF = engine.squarefreeFactors(f);
        //Set<GenPolynomial<Complex<C>>> S = SF.keySet();
        List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<Complex<C>>, Long> me : SF.entrySet()) {
            GenPolynomial<Complex<C>> sp = me.getKey();
            List<Rectangle<C>> iv = cr.complexRoots(sp);
            for (Rectangle<C> I : iv) {
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, I);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(cn);
                }
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     *
     * @param f   univariate polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(
            GenPolynomial<Complex<C>> f, BigRational eps) {
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
                .<Complex<C>>getImplementation(f.ring.coFac);
        Map<GenPolynomial<Complex<C>>, Long> SF = engine.squarefreeFactors(f);
        //Set<GenPolynomial<Complex<C>>> S = SF.keySet();
        List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (Map.Entry<GenPolynomial<Complex<C>>, Long> me : SF.entrySet()) {
            GenPolynomial<Complex<C>> sp = me.getKey();
            List<Rectangle<C>> iv = cr.complexRoots(sp);
            for (Rectangle<C> I : iv) {
                Rectangle<C> Iv = I;
                try {
                    Iv = cr.complexRootRefinement(I, sp, eps);
                } catch (InvalidBoundaryException e) {
                    e.printStackTrace();
                }
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, Iv);
                car.setEps(eps);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                long mult = me.getValue();
                for (int i = 0; i < mult; i++) {
                    list.add(cn);
                }
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     *
     * @param f univariate (rational) polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(
            GenPolynomial<C> f) {
        GenPolynomial<Complex<C>> fc = PolyUtilRoot.<C>complexFromAny(f);
        return complexAlgebraicNumbersComplex(fc);
    }


    /**
     * Complex algebraic numbers.
     *
     * @param f   univariate (rational) polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(
            GenPolynomial<C> f, BigRational eps) {
        GenPolynomial<Complex<C>> fc = PolyUtilRoot.<C>complexFromAny(f);
        return complexAlgebraicNumbersComplex(fc, eps);
    }


    /**
     * Filter real roots from complex roots.
     *
     * @param f univariate polynomial.
     * @param c list of complex algebraic numbers.
     * @param r list of real algebraic numbers.
     * @return c minus the real roots from r
     */
    public static <C extends GcdRingElem<C> & Rational> List<ComplexAlgebraicNumber<C>> filterOutRealRoots(
            GenPolynomial<C> f, List<ComplexAlgebraicNumber<C>> c, List<RealAlgebraicNumber<C>> r) {
        if (c.isEmpty()) {
            return c;
        }
        if (r.isEmpty()) {
            return c;
        }
        List<ComplexAlgebraicNumber<C>> cmr = new ArrayList<ComplexAlgebraicNumber<C>>();
        if (c.size() == r.size() /*&& r.size() == f.degree()*/) {
            return cmr;
        }
        List<RealAlgebraicNumber<C>> rl = new LinkedList<RealAlgebraicNumber<C>>(r);
        for (ComplexAlgebraicNumber<C> cn : c) {
            RealAlgebraicNumber<C> rm = null; // ~boolean
            for (RealAlgebraicNumber<C> rn : rl) {
                if (isRealRoot(f, cn, rn)) {
                    //System.out.println("filterOutRealRoots, cn = " + cn + ", rn = " + rn);
                    rm = rn;
                    break; // remove from r
                }
            }
            if (rm == null) {
                cmr.add(cn);
            } else {
                rl.remove(rm);
            }
        }
        return cmr;
    }


    /**
     * Filter real roots from complex roots.
     *
     * @param f   univariate polynomial.
     * @param c   list of complex decimal numbers.
     * @param r   list of real decimal numbers.
     * @param eps desired precision.
     * @return c minus the real roots from r
     */
    public static <C extends GcdRingElem<C> & Rational> List<Complex<BigDecimal>> filterOutRealRoots(
            GenPolynomial<C> f, List<Complex<BigDecimal>> c, List<BigDecimal> r, BigRational eps) {
        if (c.isEmpty()) {
            return c;
        }
        if (r.isEmpty()) {
            return c;
        }
        List<Complex<BigDecimal>> cmr = new ArrayList<Complex<BigDecimal>>();
        if (c.size() == r.size() /*&& r.size() == f.degree()*/) {
            return cmr;
        }
        List<BigDecimal> rl = new LinkedList<BigDecimal>(r);
        for (Complex<BigDecimal> cn : c) {
            BigDecimal rm = null; // ~boolean
            for (BigDecimal rn : rl) {
                if (isRealRoot(f, cn, rn, eps)) {
                    //System.out.println("filterOutRealRoots, cn = " + cn + ", rn = " + rn);
                    rm = rn;
                    break; // remove from r
                }
            }
            if (rm == null) {
                cmr.add(cn);
            } else {
                rl.remove(rm);
            }
        }
        return cmr;
    }


    /**
     * Roots as real and complex algebraic numbers.
     *
     * @param f univariate polynomial.
     * @return container of real and complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> AlgebraicRoots<C> algebraicRoots(GenPolynomial<C> f) {
        List<RealAlgebraicNumber<C>> rl = realAlgebraicNumbers(f);
        List<ComplexAlgebraicNumber<C>> cl = complexAlgebraicNumbers(f);
        GenPolynomial<Complex<C>> cf = null;
        if (!cl.isEmpty()) {
            cf = cl.get(0).ring.algebraic.modul;
        }
        cl = filterOutRealRoots(f, cl, rl);
        AlgebraicRoots<C> ar = new AlgebraicRoots<C>(f, cf, rl, cl);
        return ar;
    }


    /**
     * Roots of unity of real and complex algebraic numbers.
     *
     * @param ar container of real and complex algebraic numbers.
     * @return container of real and complex algebraic numbers which are roots
     * of unity.
     */
    public static <C extends GcdRingElem<C> & Rational> AlgebraicRoots<C> rootsOfUnity(AlgebraicRoots<C> ar) {
        List<RealAlgebraicNumber<C>> rl = new ArrayList<RealAlgebraicNumber<C>>();
        for (RealAlgebraicNumber<C> r : ar.real) {
            if (r.isRootOfUnity()) {
                rl.add(r);
            }
        }
        List<ComplexAlgebraicNumber<C>> cl = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (ComplexAlgebraicNumber<C> c : ar.complex) {
            if (c.isRootOfUnity()) {
                cl.add(c);
            }
        }
        AlgebraicRoots<C> ur = new AlgebraicRoots<C>(ar.p, ar.cp, rl, cl);
        return ur;
    }


    /**
     * Root refinement of real and complex algebraic numbers.
     *
     * @param a   container of real and complex algebraic numbers.
     * @param eps desired precision for root intervals and rectangles.
     */
    public static <C extends GcdRingElem<C> & Rational> void rootRefine(AlgebraicRoots<C> a,
                                                                        BigRational eps) {
        for (RealAlgebraicNumber<C> r : a.real) {
            r.ring.refineRoot(eps);
        }
        for (ComplexAlgebraicNumber<C> c : a.complex) {
            c.ring.refineRoot(eps);
        }
        return; // a or void?
    }


    /**
     * Roots as real and complex decimal numbers.
     *
     * @param f   univariate polynomial.
     * @param eps desired precision.
     * @return container of real and complex decimal numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> DecimalRoots<C> decimalRoots(GenPolynomial<C> f, BigRational eps) {
        RealRootsAbstract<C> rengine = new RealRootsSturm<C>();
        List<BigDecimal> rl = rengine.approximateRoots(f, eps);

        GenPolynomial<Complex<C>> fc = PolyUtilRoot.<C>complexFromAny(f);
        ComplexRootsAbstract<C> cengine = new ComplexRootsSturm<C>(fc.ring.coFac);
        List<Complex<BigDecimal>> cl = cengine.approximateRoots(fc, eps);

        cl = filterOutRealRoots(f, cl, rl, eps);
        DecimalRoots<C> ar = new DecimalRoots<C>(f, fc, rl, cl);
        return ar;
    }

    /**
     * Roots as real and complex decimal numbers.
     *
     * @param ar  container for real and complex algebraic roots.
     * @param eps desired precision.
     * @return container of real and complex decimal numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> DecimalRoots<C> decimalRoots(AlgebraicRoots<C> ar, BigRational eps) {
        //no: rootRefine(ar, eps);
        RealRootsAbstract<C> rengine = new RealRootsSturm<C>();
        List<BigDecimal> rl = new ArrayList<BigDecimal>(ar.real.size());
        for (RealAlgebraicNumber<C> r : ar.real) {
            try {
                BigDecimal d = rengine.approximateRoot(r.ring.root, ar.p, eps);
                rl.add(d);
            } catch (NoConvergenceException e) {
                System.out.println("should not happen: " + e);
            }
        }
        ComplexRootsAbstract<C> cengine = new ComplexRootsSturm<C>(ar.cp.ring.coFac);
        List<Complex<BigDecimal>> cl = new ArrayList<Complex<BigDecimal>>(ar.complex.size());
        for (ComplexAlgebraicNumber<C> c : ar.complex) {
            try {
                Complex<BigDecimal> d = cengine.approximateRoot(c.ring.root, ar.cp, eps);
                cl.add(d);
            } catch (NoConvergenceException e) {
                System.out.println("should not happen: " + e);
            }
        }
        DecimalRoots<C> dr = new DecimalRoots<C>(ar.p, ar.cp, rl, cl);
        return dr;
    }

}
