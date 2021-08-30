/*
 * $Id$
 */

package edu.jas.root;


import java.io.Serializable;

import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;


/**
 * Boundary determined by a rectangle and a polynomial.
 * 
 * For a given complex polynomial A a closed path throught the corners of the
 * given rectangle is constructed. The path is represented by four polynomials,
 * one for each side of the rectangle. For a real t in [0,1] the i-th polynomial
 * describes the path of A from corner[i] to corner[i+1]. In particular
 * polys[i](0) = A(corner[i]) and polys[i](1) = A(corner[i+1]), with corner[4] =
 * corner[0]. If A would be zero on a point of the path, an
 * InvalidBoundaryException is thrown.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class Boundary<C extends RingElem<C> & Rational> implements Serializable {


    /**
     * Rectangle.
     */
    public final Rectangle<C> rect;


    /**
     * Polynomial.
     */
    public final GenPolynomial<Complex<C>> A;


    /**
     * Boundary polynomials.
     */
    public final GenPolynomial<Complex<C>>[] polys;


    /**
     * Factory for real polynomials.
     */
    GenPolynomialRing<C> rfac;


    /**
     * Constructor.
     * @param r rectangle of of corners.
     * @param p non constant polynomial.
     */
    @SuppressWarnings("unchecked")
    public Boundary(Rectangle<C> r, GenPolynomial<Complex<C>> p) throws InvalidBoundaryException {
        if (p.isConstant() || p.isZERO()) {
            throw new InvalidBoundaryException("p is constant or 0 " + p);
        }
        rect = r;
        A = p;
        GreatestCommonDivisor<Complex<C>> ufd = GCDFactory.<Complex<C>> getImplementation(A.ring.coFac);
        polys = (GenPolynomial<Complex<C>>[]) new GenPolynomial[5];

        Complex<C>[] corner = rect.corners;
        for (int i = 0; i < 4; i++) {
            Complex<C> t = corner[i + 1].subtract(corner[i]);
            GenPolynomial<Complex<C>> tp = A.ring.univariate(0, 1L).multiply(t);
            //System.out.println("t = " + t);
            GenPolynomial<Complex<C>> pc = PolyUtil.<Complex<C>> seriesOfTaylor(A, corner[i]);
            pc = PolyUtil.<Complex<C>> substituteUnivariate(pc, tp);
            GenPolynomial<Complex<C>> gcd = ufd.gcd(A, pc);
            if (!gcd.isONE()) {
                //System.out.println("A = " + A);
                //System.out.println("PC["+i+"] = " + pc);
                //System.out.println("gcd = " + gcd);
                throw new InvalidBoundaryException("A has a zero on rectangle " + rect + ", A = " + A);
            }
            polys[i] = pc;
        }
        polys[4] = polys[0];

        // setup factory for real and imaginary parts
        ComplexRing<C> cr = (ComplexRing<C>) A.ring.coFac;
        RingFactory<C> cf = cr.ring;
        rfac = new GenPolynomialRing<C>(cf, A.ring);
    }


    /**
     * Constructor.
     * @param r rectangle of of corners.
     * @param p polynomial.
     * @param b boundary polynomials.
     */
    protected Boundary(Rectangle<C> r, GenPolynomial<Complex<C>> p, GenPolynomial<Complex<C>>[] b) {
        rect = r;
        A = p;
        polys = b;
        // setup factory for real and imaginary parts
        ComplexRing<C> cr = (ComplexRing<C>) A.ring.coFac;
        RingFactory<C> cf = cr.ring;
        rfac = new GenPolynomialRing<C>(cf, A.ring);
    }


    /**
     * String representation of Boundary.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return rect.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Boundary.
     */
    public String toScript() {
        // Python case
        return rect.toScript();
    }


    /**
     * Get real part for polynomial i.
     * @param i index of polynomial.
     * @return real part for polynomial i.
     */
    public GenPolynomial<C> getRealPart(int i) {
        GenPolynomial<C> f = PolyUtil.<C> realPartFromComplex(rfac, polys[i]);
        return f;
    }


    /**
     * Get imaginary part for polynomial i.
     * @param i index of polynomial.
     * @return imaginary part for polynomial i.
     */
    public GenPolynomial<C> getImagPart(int i) {
        GenPolynomial<C> g = PolyUtil.<C> imaginaryPartFromComplex(rfac, polys[i]);
        return g;
    }


    /**
     * Copy this.
     * @return a copy of this.
     */
    public Boundary<C> copy() {
        return new Boundary<C>(rect, A, polys);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        Boundary<C> a = null;
        try {
            a = (Boundary<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return rect.equals(a.rect) && A.equals(a.A);
    }


    /**
     * Hash code for this Rectangle.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hc = 0;
        hc += 37 * rect.hashCode();
        return 37 * hc + A.hashCode();
    }

}
