/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.root.Interval;
import edu.jas.root.RealRootTuple;
import edu.jas.root.AlgebraicRoots;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory.
 * @author Heinz Kredel
 */
public class RootFactoryApp {


    private static final Logger logger = LogManager.getLogger(RootFactoryApp.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Is complex algebraic number a root of a polynomial.
     * @param f univariate polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRootRealCoeff(GenPolynomial<C> f,
                    Complex<RealAlgebraicNumber<C>> r) {
        RingFactory<C> cfac = f.ring.coFac;
        ComplexRing<C> ccfac = new ComplexRing<C>(cfac);
        GenPolynomialRing<Complex<C>> facc = new GenPolynomialRing<Complex<C>>(ccfac, f.ring);
        GenPolynomial<Complex<C>> fc = PolyUtil.<C> complexFromAny(facc, f);
        return isRoot(fc, r);
    }


    /**
     * Is complex algebraic number a root of a polynomial.
     * @param f univariate polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRoot(GenPolynomial<Complex<C>> f,
                    Complex<RealAlgebraicNumber<C>> r) {
        ComplexRing<RealAlgebraicNumber<C>> cr = r.factory();
        GenPolynomialRing<Complex<RealAlgebraicNumber<C>>> cfac = new GenPolynomialRing<Complex<RealAlgebraicNumber<C>>>(
                        cr, f.factory());
        GenPolynomial<Complex<RealAlgebraicNumber<C>>> p;
        p = PolyUtilApp.<C> convertToComplexRealCoefficients(cfac, f);
        // test algebraic part
        Complex<RealAlgebraicNumber<C>> a = PolyUtil.<Complex<RealAlgebraicNumber<C>>> evaluateMain(cr, p, r);
        boolean t = a.isZERO();
        if (!t) {
            logger.info("f(r) = " + a + ", f = " + f + ", r  = " + r);
            return t;
        }
        // test approximation? not working
        return true;
    }


    /**
     * Is complex algebraic number a root of a polynomial.
     * @param f univariate polynomial.
     * @param R list of complex algebraic numbers.
     * @return true, if f(r) == 0 for all r in R, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> boolean isRoot(GenPolynomial<Complex<C>> f,
                    List<Complex<RealAlgebraicNumber<C>>> R) {
        for (Complex<RealAlgebraicNumber<C>> r : R) {
            boolean t = isRoot(f, r);
            if (!t) {
                return false;
            }
        }
        return true;
    }


    /**
     * Complex algebraic number roots.
     * @param f univariate polynomial.
     * @return a list of different complex algebraic numbers, with f(c) == 0 for
     *         c in roots.
     */
    public static <C extends GcdRingElem<C> & Rational> List<Complex<RealAlgebraicNumber<C>>> complexAlgebraicNumbersComplex(
                    GenPolynomial<Complex<C>> f) {
        GenPolynomialRing<Complex<C>> pfac = f.factory();
        if (pfac.nvar != 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        ComplexRing<C> cfac = (ComplexRing<C>) pfac.coFac;
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory.<Complex<C>> getImplementation(cfac);
        Map<GenPolynomial<Complex<C>>, Long> F = engine.squarefreeFactors(f.monic());
        //System.out.println("S = " + F.keySet());
        List<Complex<RealAlgebraicNumber<C>>> list = new ArrayList<Complex<RealAlgebraicNumber<C>>>();
        for (Map.Entry<GenPolynomial<Complex<C>>,Long> me : F.entrySet()) {
            GenPolynomial<Complex<C>> sp = me.getKey();
            if (sp.isConstant() || sp.isZERO()) {
                continue;
            }
            List<Complex<RealAlgebraicNumber<C>>> ls = RootFactoryApp.<C> complexAlgebraicNumbersSquarefree(sp);
            long m = me.getValue();
            for (long i = 0L; i < m; i++) {
                list.addAll(ls);
            }
        }
        return list;
    }


    /**
     * Complex algebraic number roots.
     * @param f univariate squarefree polynomial.
     * @return a list of different complex algebraic numbers, with f(c) == 0 for
     *         c in roots.
     */
    public static <C extends GcdRingElem<C> & Rational> 
      List<Complex<RealAlgebraicNumber<C>>> complexAlgebraicNumbersSquarefree(
                   GenPolynomial<Complex<C>> f) {
        GenPolynomialRing<Complex<C>> pfac = f.factory();
        if (pfac.nvar != 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        ComplexRing<C> cfac = (ComplexRing<C>) pfac.coFac;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<Complex<C>> tfac = new GenPolynomialRing<Complex<C>>(cfac, 2, to); //,new vars); //tord?
        //System.out.println("tfac = " + tfac);
        GenPolynomial<Complex<C>> t = tfac.univariate(1, 1L).sum(
                        tfac.univariate(0, 1L).multiply(cfac.getIMAG()));
        //System.out.println("t = " + t); // t = x + i y
        GenPolynomialRing<C> rfac = new GenPolynomialRing<C>(cfac.ring, tfac); //tord?
        //System.out.println("rfac = " + rfac);
        List<Complex<RealAlgebraicNumber<C>>> list = new ArrayList<Complex<RealAlgebraicNumber<C>>>();
        GenPolynomial<Complex<C>> sp = f;
        if (sp.isConstant() || sp.isZERO()) {
            return list;
        }
        // substitute t = x + i y
        GenPolynomial<Complex<C>> su = PolyUtil.<Complex<C>> substituteUnivariate(sp, t);
        //System.out.println("su = " + su);
        su = su.monic();
        //System.out.println("su = " + su);
        GenPolynomial<C> re = PolyUtil.<C> realPartFromComplex(rfac, su);
        GenPolynomial<C> im = PolyUtil.<C> imaginaryPartFromComplex(rfac, su);
        if (debug) {
            logger.debug("rfac = " + rfac.toScript());
            logger.debug("t  = " + t + ", re = " + re.toScript() + ", im = " + im.toScript());
        }
        List<GenPolynomial<C>> li = new ArrayList<GenPolynomial<C>>(2);
        li.add(re);
        li.add(im);
        Ideal<C> id = new Ideal<C>(rfac, li);
        //System.out.println("id = " + id);
        List<IdealWithUniv<C>> idul = id.zeroDimRootDecomposition();

        IdealWithRealAlgebraicRoots<C> idr;
        for (IdealWithUniv<C> idu : idul) {
            //System.out.println("---idu = " + idu);
            idr = PolyUtilApp.<C> realAlgebraicRoots(idu);
            //System.out.println("---idr = " + idr);
            for (List<edu.jas.root.RealAlgebraicNumber<C>> crr : idr.ran) {
                //System.out.println("crr = " + crr);
                RealRootTuple<C> root = new RealRootTuple<C>(crr);
                //System.out.println("root = " + root);
                RealAlgebraicRing<C> car = new RealAlgebraicRing<C>(idu, root);
                //System.out.println("car = " + car);
                List<RealAlgebraicNumber<C>> gens = car.generators();
                //System.out.println("gens = " + gens);
                int sg = gens.size();
                RealAlgebraicNumber<C> rre = gens.get(sg - 2);
                RealAlgebraicNumber<C> rim = gens.get(sg - 1);
                ComplexRing<RealAlgebraicNumber<C>> cring = new ComplexRing<RealAlgebraicNumber<C>>(car);
                Complex<RealAlgebraicNumber<C>> crn = new Complex<RealAlgebraicNumber<C>>(cring, rre, rim);
                //System.out.println("crn = " + crn + " in " + crn.ring);
                // refine intervals if necessary, not meaningful
                list.add(crn);
            }
        }
        return list;
    }


    /* approximation ?
    List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f, BigRational eps)
    */


    /**
     * Root reduce of real and complex algebraic numbers.
     * @param a container of real and complex algebraic numbers.
     * @param b container of real and complex algebraic numbers.
     * @return container of real and complex algebraic numbers 
     *         of the primitive element of a and b.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           AlgebraicRootsPrimElem<C> rootReduce(AlgebraicRoots<C> a, AlgebraicRoots<C> b) {
        return rootReduce(a.getAlgebraicRing(), b.getAlgebraicRing());
    }


    /**
     * Root reduce of real and complex algebraic numbers.
     * @param a polynomial.
     * @param b polynomial.
     * @return container of real and complex algebraic numbers 
     *         of the primitive element of a and b.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           AlgebraicRootsPrimElem<C> rootReduce(GenPolynomial<C> a, GenPolynomial<C> b) {
        AlgebraicNumberRing<C> anr = new AlgebraicNumberRing<C>(a);
        AlgebraicNumberRing<C> bnr = new AlgebraicNumberRing<C>(b);
        return rootReduce(anr, bnr);
    }


    /**
     * Root reduce of real and complex algebraic numbers.
     * @param a algebraic number ring.
     * @param b algebraic number ring.
     * @return container of real and complex algebraic numbers 
     *         of the primitive element of a and b.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           AlgebraicRootsPrimElem<C> rootReduce(AlgebraicNumberRing<C> a, AlgebraicNumberRing<C> b) {
        PrimitiveElement<C> pe = PolyUtilApp.<C>primitiveElement(a, b);
        AlgebraicRoots<C> ar = edu.jas.root.RootFactory.<C>algebraicRoots(pe.primitiveElem.modul);
        return new AlgebraicRootsPrimElem<C>(ar, pe);
    }


    /**
     * Roots of unity of real and complex algebraic numbers.
     * @param ar container of real and complex algebraic numbers with primitive element.
     * @return container of real and complex algebraic numbers which are roots
     *         of unity.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           AlgebraicRootsPrimElem<C> rootsOfUnity(AlgebraicRootsPrimElem<C> ar) {
        AlgebraicRoots<C> ur = edu.jas.root.RootFactory.rootsOfUnity(ar);
        if (ar.pelem == null) {
            return new AlgebraicRootsPrimElem<C>(ur, ar.pelem);
        }
        List<AlgebraicNumber<C>> al = new ArrayList<AlgebraicNumber<C>>();
        long d = ar.pelem.primitiveElem.modul.degree();
        AlgebraicNumber<C> c = ar.pelem.A;
        AlgebraicNumber<C> m = c.ring.getONE();
        for (long i = 1; i <= d; i++) {
            m = m.multiply(c);
            if (m.isRootOfUnity()) {
                if (!al.contains(m)) {
                    al.add(m);
                }
            }
        }
        c = ar.pelem.B;
        m = c.ring.getONE();
        for (long i = 1; i <= d; i++) {
            m = m.multiply(c);
            if (m.isRootOfUnity()) {
                if (!al.contains(m)) {
                    al.add(m);
                }
            }
        }
        return new AlgebraicRootsPrimElem<C>(ur, ar.pelem, al);
    }

}
