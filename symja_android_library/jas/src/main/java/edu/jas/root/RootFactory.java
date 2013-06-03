/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import edu.jas.arith.Rational;
import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory.
 * @author Heinz Kredel
 */
public class RootFactory {


    /**
     * Is real algebraic number a root of a polynomial.
     * @param f univariate polynomial.
     * @param r real algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> 
           boolean isRoot(GenPolynomial<C> f, RealAlgebraicNumber<C> r) {
        RealAlgebraicRing<C> rr = r.factory(); 
        GenPolynomialRing<RealAlgebraicNumber<C>> rfac 
           = new GenPolynomialRing<RealAlgebraicNumber<C>>(rr,f.factory());
        GenPolynomial<RealAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C> convertToRealCoefficients(rfac,f);
        RealAlgebraicNumber<C> a = PolyUtil.<RealAlgebraicNumber<C>> evaluateMain(rr,p,r);
        return a.isZERO();
    }


    /**
     * Real algebraic numbers.
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbers(GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C> getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>,Long> SF = engine.squarefreeFactors(f);
        Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbers(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C> getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>,Long> SF = engine.squarefreeFactors(f);
        Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp,eps);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C> getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>,Long> SF = engine.baseFactors(f);
        Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C> getImplementation(f.ring.coFac);
        Map<GenPolynomial<C>,Long> SF = engine.baseFactors(f);
        Set<GenPolynomial<C>> S = SF.keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp,eps);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(rn);
                }
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a irreducible polynomial.
     * @param f univariate irreducible polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(GenPolynomial<C> f) {
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
     * @param f univariate irreducible polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        List<Interval<C>> iv = rr.realRoots(f,eps);
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
     * @param f univariate polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> 
           boolean isRoot(GenPolynomial<C> f, ComplexAlgebraicNumber<C> r) {
        ComplexAlgebraicRing<C> cr = r.factory(); 
        GenPolynomialRing<ComplexAlgebraicNumber<C>> cfac 
           = new GenPolynomialRing<ComplexAlgebraicNumber<C>>(cr,f.factory());
        GenPolynomial<ComplexAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C> convertToComplexCoefficients(cfac,f);
        ComplexAlgebraicNumber<C> a = PolyUtil.<ComplexAlgebraicNumber<C>> evaluateMain(cr,p,r);
        return a.isZERO();
    }


    /**
     * Is complex algebraic number a root of a complex polynomial.
     * @param f univariate complex polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> 
           boolean isRootComplex(GenPolynomial<Complex<C>> f, ComplexAlgebraicNumber<C> r) {
        ComplexAlgebraicRing<C> cr = r.factory(); 
        GenPolynomialRing<ComplexAlgebraicNumber<C>> cfac 
           = new GenPolynomialRing<ComplexAlgebraicNumber<C>>(cr,f.factory());
        GenPolynomial<ComplexAlgebraicNumber<C>> p;
        p = PolyUtilRoot.<C> convertToComplexCoefficientsFromComplex(cfac,f);
        ComplexAlgebraicNumber<C> a = PolyUtil.<ComplexAlgebraicNumber<C>> evaluateMain(cr,p,r);
        return a.isZERO();
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f) {
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
                .<Complex<C>> getImplementation(f.ring.coFac);
        Map<GenPolynomial<Complex<C>>,Long> SF = engine.squarefreeFactors(f);
        Set<GenPolynomial<Complex<C>>> S = SF.keySet();
        List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (GenPolynomial<Complex<C>> sp : S) {
            List<Rectangle<C>> iv = cr.complexRoots(sp);
            for (Rectangle<C> I : iv) {
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, I);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(cn);
                }
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f, BigRational eps) {
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
                .<Complex<C>> getImplementation(f.ring.coFac);
        Map<GenPolynomial<Complex<C>>,Long> SF = engine.squarefreeFactors(f);
        Set<GenPolynomial<Complex<C>>> S = SF.keySet();
        List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (GenPolynomial<Complex<C>> sp : S) {
            List<Rectangle<C>> iv = cr.complexRoots(sp);
            for (Rectangle<C> I : iv) {
                Rectangle<C> Iv = I;
                try {
                    Iv = cr.complexRootRefinement(I,sp,eps);
                } catch (InvalidBoundaryException e) {
                    e.printStackTrace();
                }
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, Iv);
                car.setEps(eps);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                long mult = SF.get(sp);
                for ( int i = 0; i < mult; i++ ) {
                     list.add(cn);
                }
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate (rational) polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(GenPolynomial<C> f) {
        if ( f.ring.coFac instanceof Complex ) {
            throw new IllegalArgumentException("f already has Complex coefficients " + f.ring);
        }
        if ( f.ring.coFac instanceof ComplexAlgebraicRing ) {
            throw new UnsupportedOperationException("unsupported ComplexAlgebraicRing coefficients " + f.ring);
        }
        ComplexRing<C> cr = new ComplexRing<C>( f.ring.coFac );
        GenPolynomialRing<Complex<C>> fac = new GenPolynomialRing<Complex<C>>(cr,f.ring);
        GenPolynomial<Complex<C>> fc = PolyUtil.<C>complexFromAny(fac,f); 
        return complexAlgebraicNumbersComplex(fc);
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate (rational) polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
                             List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(GenPolynomial<C> f, BigRational eps) {
        if ( f.ring.coFac instanceof Complex ) {
            throw new IllegalArgumentException("f already has Complex coefficients " + f.ring);
        }
        if ( f.ring.coFac instanceof ComplexAlgebraicRing ) {
            throw new UnsupportedOperationException("unsupported ComplexAlgebraicRing coefficients " + f.ring);
        }
        ComplexRing<C> cr = new ComplexRing<C>( f.ring.coFac );
        GenPolynomialRing<Complex<C>> fac = new GenPolynomialRing<Complex<C>>(cr,f.ring);
        GenPolynomial<Complex<C>> fc = PolyUtil.<C>complexFromAny(fac,f); 
        return complexAlgebraicNumbersComplex(fc,eps);
    }

}
