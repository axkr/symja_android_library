/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.root.Interval;
import edu.jas.root.RealRootTuple;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory.
 * @author Heinz Kredel
 */
public class RootFactory {


    private static final Logger logger = Logger.getLogger(RootFactory.class);


    private static boolean debug = logger.isDebugEnabled();


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
        // test approximation, not working
        RealAlgebraicRing<C> rring = (RealAlgebraicRing<C>) cr.ring;
        RealRootTuple<C> rroot = rring.getRoot();
        List<edu.jas.root.RealAlgebraicNumber<C>> rlist = rroot.tuple;
        //System.out.println("rlist = " + rlist);
        Interval<C> vr = rlist.get(0).ring.getRoot();
        Interval<C> vi = rlist.get(1).ring.getRoot();
        ComplexRing<C> ccfac = new ComplexRing<C>((RingFactory<C>) vr.left.factory());
        Complex<C> sw = new Complex<C>(ccfac, vr.left, vi.left);
        Complex<C> ne = new Complex<C>(ccfac, vr.right, vi.right);
        Complex<C> epsw = PolyUtil.<Complex<C>> evaluateMain(ccfac, f, sw);
        Complex<C> epne = PolyUtil.<Complex<C>> evaluateMain(ccfac, f, ne);
        int rootre = (epsw.getRe().signum() * epne.getRe().signum());
        int rootim = (epsw.getIm().signum() * epne.getIm().signum());
        t = (rootre <= 0 && rootim <= 0);
        //logger.info("p(root): p = " + f + ", r = " + r.getRe() + " i " + r.getIm());
        //logger.info("r = " + new BigDecimal(r.getRe().magnitude()) + " i " + new BigDecimal(r.getIm().magnitude()));
        //logger.info("vr = " + vr + ", vi = " + vi);
        //logger.info("sw   = " + sw   + ", ne   = " + ne);
        //logger.info("root(re) = " + rootre + ", root(im) = " + rootim);
        //logger.info("epsw   = " + new BigDecimal(epsw.getRe().getRational()) + " i " + new BigDecimal(epsw.getIm().getRational()));
        //logger.info("epne   = " + new BigDecimal(epne.getRe().getRational()) + " i " + new BigDecimal(epne.getIm().getRational()));
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
        Set<GenPolynomial<Complex<C>>> S = F.keySet();
        //System.out.println("S = " + S);
        List<Complex<RealAlgebraicNumber<C>>> list = new ArrayList<Complex<RealAlgebraicNumber<C>>>();
        for (GenPolynomial<Complex<C>> sp : S) {
            if (sp.isConstant() || sp.isZERO()) {
                continue;
            }
            List<Complex<RealAlgebraicNumber<C>>> ls = RootFactory.<C> complexAlgebraicNumbersSquarefree(sp);
            long m = F.get(sp);
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
        GenPolynomialRing<Complex<C>> tfac = new GenPolynomialRing<Complex<C>>(cfac, 2, to); //,vars); //tord?
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

                //boolean it;
                //int count = 0;
                //do { // refine intervals if necessary, not meaningful
                //    Interval<C> vr = crr.get(0).ring.getRoot();
                //    Interval<C> vi = crr.get(1).ring.getRoot();
                //    ComplexRing<C> ccfac = new ComplexRing<C>((RingFactory<C>) vr.left.factory());
                //    Complex<C> sw = new Complex<C>(ccfac, vr.left, vi.left);
                //    Complex<C> ne = new Complex<C>(ccfac, vr.right, vi.right);
                //    Complex<C> epsw = PolyUtil.<Complex<C>> evaluateMain(ccfac, f, sw);
                //    Complex<C> epne = PolyUtil.<Complex<C>> evaluateMain(ccfac, f, ne);
                //    int rootre = (epsw.getRe().signum() * epne.getRe().signum());
                //    int rootim = (epsw.getIm().signum() * epne.getIm().signum());
                //    it = true || (rootre <= 0 && rootim <= 0); // TODO
                //    if (!it) {
                //        logger.info("refine intervals: vr = " + vr + ", vi = " + vi); // + ", crn = " + crn.ring);
                //        crn.getRe().ring.realRing.halfInterval();
                //        //System.out.println("crn.re = " + crn.getRe().ring.realRing);
                //        crn.getIm().ring.realRing.halfInterval();
                //        //System.out.println("crn.im = " + crn.getIm().ring.realRing);
                //        if (count++ > 2) {
                //            //throw new RuntimeException("no roots of " + f);
                //            logger.info("break in root refinement of " + crn + " in " + crn.ring);
                //            it = true;
                //        }
                //    }
                //} while (!it);
                list.add(crn);
            }
        }
        return list;
    }


    /* todo
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     public static <C extends GcdRingElem<C> & Rational> 
     List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f, BigRational eps) {
     }
    */

}
