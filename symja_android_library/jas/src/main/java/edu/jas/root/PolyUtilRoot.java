/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;


/**
 * Polynomial utilities related to real and complex roots.
 * @author Heinz Kredel
 */

public class PolyUtilRoot {


    private static final Logger logger = Logger.getLogger(PolyUtilRoot.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients, C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<RealAlgebraicNumber<C>> convertToAlgebraicCoefficients(
                    GenPolynomialRing<RealAlgebraicNumber<C>> pfac, GenPolynomial<C> A) {
        RealAlgebraicRing<C> afac = (RealAlgebraicRing<C>) pfac.coFac;
        if (debug) {
            logger.info("afac = " + afac);
        }
        return PolyUtil.<C, RealAlgebraicNumber<C>> map(pfac, A, new CoeffToReAlg<C>(afac));
    }


    /**
     * Convert to recursive RealAlgebraicNumber coefficients. Represent as
     * polynomial with recursive RealAlgebraicNumber<C> coefficients, C is e.g.
     * ModInteger or BigRational.
     * @param depth recursion depth of RealAlgebraicNumber coefficients.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<RealAlgebraicNumber<C>> convertToRecAlgebraicCoefficients(
                    int depth, GenPolynomialRing<RealAlgebraicNumber<C>> pfac, GenPolynomial<C> A) {
        RealAlgebraicRing<C> afac = (RealAlgebraicRing<C>) pfac.coFac;
        return PolyUtil.<C, RealAlgebraicNumber<C>> map(pfac, A, new CoeffToRecReAlg<C>(depth, afac));
    }


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients, C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A recursive polynomial with GenPolynomial&lt;BigInteger&gt;
     *            coefficients to be converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<RealAlgebraicNumber<C>> convertRecursiveToAlgebraicCoefficients(
                    GenPolynomialRing<RealAlgebraicNumber<C>> pfac, GenPolynomial<GenPolynomial<C>> A) {
        RealAlgebraicRing<C> afac = (RealAlgebraicRing<C>) pfac.coFac;
        return PolyUtil.<GenPolynomial<C>, RealAlgebraicNumber<C>> map(pfac, A, new PolyToReAlg<C>(afac));
    }


    /**
     * Convert to AlgebraicNumber coefficients. Represent as polynomial with
     * AlgebraicNumber<C> coefficients.
     * @param afac result polynomial factory.
     * @param A polynomial with RealAlgebraicNumber&lt;C&gt; coefficients to be
     *            converted.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<AlgebraicNumber<C>> algebraicFromRealCoefficients(
                    GenPolynomialRing<AlgebraicNumber<C>> afac, GenPolynomial<RealAlgebraicNumber<C>> A) {
        AlgebraicNumberRing<C> cfac = (AlgebraicNumberRing<C>) afac.coFac;
        return PolyUtil.<RealAlgebraicNumber<C>, AlgebraicNumber<C>> map(afac, A, new AlgFromRealCoeff<C>(
                        cfac));
    }


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients.
     * @param rfac result polynomial factory.
     * @param A polynomial with AlgebraicNumber&lt;C&gt; coefficients to be
     *            converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<RealAlgebraicNumber<C>> realFromAlgebraicCoefficients(
                    GenPolynomialRing<RealAlgebraicNumber<C>> rfac, GenPolynomial<AlgebraicNumber<C>> A) {
        RealAlgebraicRing<C> cfac = (RealAlgebraicRing<C>) rfac.coFac;
        return PolyUtil.<AlgebraicNumber<C>, RealAlgebraicNumber<C>> map(rfac, A, new RealFromAlgCoeff<C>(
                        cfac));
    }


    /**
     * Convert to RealAlgebraicNumber coefficients. Represent as polynomial with
     * RealAlgebraicNumber<C> coefficients, C is e.g. BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with RealAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<RealAlgebraicNumber<C>> convertToRealCoefficients(
                    GenPolynomialRing<RealAlgebraicNumber<C>> pfac, GenPolynomial<C> A) {
        RealAlgebraicRing<C> afac = (RealAlgebraicRing<C>) pfac.coFac;
        return PolyUtil.<C, RealAlgebraicNumber<C>> map(pfac, A, new CoeffToReal<C>(afac));
    }


    /**
     * Convert to ComplexAlgebraicNumber coefficients. Represent as polynomial
     * with ComplexAlgebraicNumber<C> coefficients, C is e.g. BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with ComplexAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<ComplexAlgebraicNumber<C>> convertToComplexCoefficients(
                    GenPolynomialRing<ComplexAlgebraicNumber<C>> pfac, GenPolynomial<C> A) {
        ComplexAlgebraicRing<C> afac = (ComplexAlgebraicRing<C>) pfac.coFac;
        return PolyUtil.<C, ComplexAlgebraicNumber<C>> map(pfac, A, new CoeffToComplex<C>(afac));
    }


    /**
     * Convert to ComplexAlgebraicNumber coefficients. Represent as polynomial
     * with ComplexAlgebraicNumber<C> coefficients, C is e.g. BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with ComplexAlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C> & Rational> GenPolynomial<ComplexAlgebraicNumber<C>> convertToComplexCoefficientsFromComplex(
                    GenPolynomialRing<ComplexAlgebraicNumber<C>> pfac, GenPolynomial<Complex<C>> A) {
        ComplexAlgebraicRing<C> afac = (ComplexAlgebraicRing<C>) pfac.coFac;
        return PolyUtil.<Complex<C>, ComplexAlgebraicNumber<C>> map(pfac, A,
                        new CoeffToComplexFromComplex<C>(afac));
    }

}


/**
 * Polynomial to algebraic functor.
 */
class PolyToReAlg<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<GenPolynomial<C>, RealAlgebraicNumber<C>> {


    final protected RealAlgebraicRing<C> afac;


    public PolyToReAlg(RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
    }


    public RealAlgebraicNumber<C> eval(GenPolynomial<C> c) {
        if (c == null) {
            return afac.getZERO();
        }
        return new RealAlgebraicNumber<C>(afac, c);
    }
}


/**
 * Coefficient to algebraic functor.
 */
class CoeffToReAlg<C extends GcdRingElem<C> & Rational> implements UnaryFunctor<C, RealAlgebraicNumber<C>> {


    final protected RealAlgebraicRing<C> afac;


    final protected GenPolynomial<C> zero;


    public CoeffToReAlg(RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
        GenPolynomialRing<C> pfac = afac.algebraic.ring;
        zero = pfac.getZERO();
    }


    public RealAlgebraicNumber<C> eval(C c) {
        if (c == null) {
            return afac.getZERO();
        }
        return new RealAlgebraicNumber<C>(afac, zero.sum(c));
    }
}


/**
 * Coefficient to recursive algebraic functor.
 */
class CoeffToRecReAlg<C extends GcdRingElem<C> & Rational> implements UnaryFunctor<C, RealAlgebraicNumber<C>> {


    final protected List<RealAlgebraicRing<C>> lfac;


    final int depth;


    @SuppressWarnings("cast")
    public CoeffToRecReAlg(int depth, RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        RealAlgebraicRing<C> afac = fac;
        this.depth = depth;
        lfac = new ArrayList<RealAlgebraicRing<C>>(this.depth);
        lfac.add(fac);
        for (int i = 1; i < this.depth; i++) {
            RingFactory<C> rf = afac.algebraic.ring.coFac;
            if (!(rf instanceof RealAlgebraicRing)) {
                throw new IllegalArgumentException("fac depth to low");
            }
            afac = (RealAlgebraicRing<C>) (Object) rf;
            lfac.add(afac);
        }
    }


    @SuppressWarnings("cast")
    public RealAlgebraicNumber<C> eval(C c) {
        if (c == null) {
            return lfac.get(0).getZERO();
        }
        C ac = c;
        RealAlgebraicRing<C> af = lfac.get(lfac.size() - 1);
        GenPolynomial<C> zero = af.algebraic.ring.getZERO();
        RealAlgebraicNumber<C> an = new RealAlgebraicNumber<C>(af, zero.sum(ac));
        for (int i = lfac.size() - 2; i >= 0; i--) {
            af = lfac.get(i);
            zero = af.algebraic.ring.getZERO();
            ac = (C) (Object) an;
            an = new RealAlgebraicNumber<C>(af, zero.sum(ac));
        }
        return an;
    }
}


/**
 * Coefficient to algebraic from real algebraic functor.
 */
class AlgFromRealCoeff<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<RealAlgebraicNumber<C>, AlgebraicNumber<C>> {


    final protected AlgebraicNumberRing<C> afac;


    public AlgFromRealCoeff(AlgebraicNumberRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
    }


    public AlgebraicNumber<C> eval(RealAlgebraicNumber<C> c) {
        if (c == null) {
            return afac.getZERO();
        }
        return c.number;
    }
}


/**
 * Coefficient to real algebriac from algebraic functor.
 */
class RealFromAlgCoeff<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<AlgebraicNumber<C>, RealAlgebraicNumber<C>> {


    final protected RealAlgebraicRing<C> rfac;


    public RealFromAlgCoeff(RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        rfac = fac;
    }


    public RealAlgebraicNumber<C> eval(AlgebraicNumber<C> c) {
        if (c == null) {
            return rfac.getZERO();
        }
        return new RealAlgebraicNumber<C>(rfac, c);
    }
}


/**
 * Coefficient to real algebraic functor.
 */
class CoeffToReal<C extends GcdRingElem<C> & Rational> implements UnaryFunctor<C, RealAlgebraicNumber<C>> {


    final protected RealAlgebraicRing<C> rfac;


    final protected AlgebraicNumber<C> zero;


    public CoeffToReal(RealAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        rfac = fac;
        AlgebraicNumberRing<C> afac = rfac.algebraic;
        zero = afac.getZERO();
    }


    public RealAlgebraicNumber<C> eval(C c) {
        if (c == null) {
            return rfac.getZERO();
        }
        return new RealAlgebraicNumber<C>(rfac, zero.sum(c));
    }
}


/**
 * Coefficient to complex algebraic functor.
 */
class CoeffToComplex<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<C, ComplexAlgebraicNumber<C>> {


    final protected ComplexAlgebraicRing<C> cfac;


    final protected AlgebraicNumber<Complex<C>> zero;


    final protected ComplexRing<C> cr;


    public CoeffToComplex(ComplexAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        cfac = fac;
        AlgebraicNumberRing<Complex<C>> afac = cfac.algebraic;
        zero = afac.getZERO();
        cr = (ComplexRing<C>) afac.ring.coFac;
    }


    public ComplexAlgebraicNumber<C> eval(C c) {
        if (c == null) {
            return cfac.getZERO();
        }
        return new ComplexAlgebraicNumber<C>(cfac, zero.sum(new Complex<C>(cr, c)));
    }
}


/**
 * Coefficient to complex algebraic from complex functor.
 */
class CoeffToComplexFromComplex<C extends GcdRingElem<C> & Rational> implements
                UnaryFunctor<Complex<C>, ComplexAlgebraicNumber<C>> {


    final protected ComplexAlgebraicRing<C> cfac;


    final protected AlgebraicNumber<Complex<C>> zero;


    //final protected ComplexRing<C> cr;


    public CoeffToComplexFromComplex(ComplexAlgebraicRing<C> fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac must not be null");
        }
        cfac = fac;
        AlgebraicNumberRing<Complex<C>> afac = cfac.algebraic;
        zero = afac.getZERO();
        //cr = (ComplexRing<C>) afac.ring.coFac;
    }


    public ComplexAlgebraicNumber<C> eval(Complex<C> c) {
        if (c == null) {
            return cfac.getZERO();
        }
        return new ComplexAlgebraicNumber<C>(cfac, zero.sum(c));
    }
}
