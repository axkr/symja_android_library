/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;


/**
 * Package gbufd utilities.
 * @author Heinz Kredel
 */

public class PolyGBUtil {


    private static final Logger logger = Logger.getLogger(PolyGBUtil.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Test for resultant.
     * @param A generic polynomial.
     * @param B generic polynomial.
     * @param r generic polynomial.
     * @return true if res(A,B) isContained in ideal(A,B), else false.
     */
    public static <C extends GcdRingElem<C>> 
      boolean isResultant(GenPolynomial<C> A, GenPolynomial<C> B, GenPolynomial<C> r) {
        if (r == null || r.isZERO()) {
            return true;
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(r.ring.coFac);
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(2);
        F.add(A);
        F.add(B);
        List<GenPolynomial<C>> G = bb.GB(F);
        //System.out.println("G = " + G);
        GenPolynomial<C> n = bb.red.normalform(G, r);
        //System.out.println("n = " + n);
        return n.isZERO();
    }


    /**
     * Top pseudo reduction wrt the main variables.
     * @param P generic polynomial.
     * @param A list of generic polynomials sorted according to appearing main variables.
     * @return top pseudo remainder of P wrt. A for the appearing variables.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> topPseudoRemainder(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if (A == null || A.isEmpty()) {
            return P.monic();
        }
        if (P.isZERO()) {
            return P;
        }
        //System.out.println("remainder, P = " + P);
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if (pfac.nvar <= 1) { // recursion base 
            GenPolynomial<C> R = PolyUtil.<C> baseSparsePseudoRemainder(P, A.get(0));
            return R.monic();
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<C> Q = A.get(0); // wrong, must eventually search polynomial
        GenPolynomial<GenPolynomial<C>> qr = PolyUtil.<C> recursive(rfac, Q);
        GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> rr;
        if (qr.isONE()) {
            return P.ring.getZERO();
        }
        if (qr.degree(0) > 0) {
            rr = PolyUtil.<C> recursiveSparsePseudoRemainder(pr, qr);
            //System.out.println("remainder, pr = " + pr);
            //System.out.println("remainder, qr = " + qr);
            //System.out.println("remainder, rr = " + rr);
        } else {
            rr = pr;
        }
        if (rr.degree(0) > 0) {
            GenPolynomial<C> R = PolyUtil.<C> distribute(pfac, rr);
            return R.monic();
            // not further reduced wrt. other variables = top-reduction only
        }
        List<GenPolynomial<C>> zeroDeg = zeroDegrees(A);
        GenPolynomial<C> R = topPseudoRemainder(zeroDeg, rr.leadingBaseCoefficient());
        R = R.extend(pfac, 0, 0L);
        return R.monic();
    }


    /**
     * Top coefficient pseudo remainder of the leading coefficient of P wrt A in the main variables.
     * @param P generic polynomial in n+1 variables.
     * @param A list of generic polynomials in n variables sorted according to appearing main variables.
     * @return pseudo remainder of the leading coefficient of P wrt A.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> topCoefficientPseudoRemainder(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if (A == null || A.isEmpty()) {
            return P.monic();
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        GenPolynomialRing<C> pfac1 = A.get(0).ring;
        if (pfac1.nvar <= 1) { // recursion base 
            GenPolynomial<C> a = A.get(0);
            GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(pfac.nvar - 1);
            GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac, P);
            // ldcf(P,x_m) = q a + r 
            GenPolynomial<GenPolynomial<C>> rr = PolyGBUtil.<C> coefficientPseudoRemainderBase(pr, a);
            GenPolynomial<C> R = PolyUtil.<C> distribute(pfac, rr);
            return R.monic();
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac1 = pfac1.recursive(1);
        int nv = pfac.nvar - pfac1.nvar;
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1 + nv);
        GenPolynomialRing<GenPolynomial<GenPolynomial<C>>> rfac2 = rfac.recursive(nv);
        if (debug) {
            logger.info("rfac =" + rfac);
        }
        GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> pr2 = PolyUtil.<GenPolynomial<C>> recursive(rfac2, pr);
        //System.out.println("recursion, pr2 = " + pr2);
        GenPolynomial<C> Q = A.get(0);
        GenPolynomial<GenPolynomial<C>> qr = PolyUtil.<C> recursive(rfac1, Q);
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> rr;
        if (qr.isONE()) {
            return P.ring.getZERO();
        }
        if (qr.degree(0) > 0) {
            // pseudo remainder:  ldcf(P,x_m) = a q + r 
            rr = PolyGBUtil.<C> coefficientPseudoRemainder(pr2, qr);
            //System.out.println("recursion, qr  = " + qr);
            //System.out.println("recursion, pr  = " + pr2);
            //System.out.println("recursion, rr  = " + rr);
        } else {
            rr = pr2;
        }
        // reduction wrt. the other variables
        List<GenPolynomial<C>> zeroDeg = zeroDegrees(A);
        GenPolynomial<GenPolynomial<C>> Rr = PolyUtil.<GenPolynomial<C>> distribute(rfac, rr);
        GenPolynomial<C> R = PolyUtil.<C> distribute(pfac, Rr);
        R = topCoefficientPseudoRemainder(zeroDeg, R);
        return R.monic();
    }


    /**
     * Polynomial leading coefficient pseudo remainder.
     * @param P generic polynomial in n+1 variables.
     * @param A generic polynomial in n variables.
     * @return pseudo remainder of the leading coefficient of P wrt A, with
     *         ldcf(A)<sup>m'</sup> P = quotient * A + remainder.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<GenPolynomial<GenPolynomial<C>>> coefficientPseudoRemainder(
                    GenPolynomial<GenPolynomial<GenPolynomial<C>>> P, GenPolynomial<GenPolynomial<C>> A) {
        if (A == null || A.isZERO()) { // findbugs
            throw new ArithmeticException(P + " division by zero " + A);
        }
        if (A.isONE()) {
            return P.ring.getZERO();
        }
        if (P.isZERO() || P.isONE()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<GenPolynomial<C>>> pfac = P.ring;
        GenPolynomialRing<GenPolynomial<C>> afac = A.ring; // == pfac.coFac
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> r = P;
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> hr;
        GenPolynomial<GenPolynomial<C>> ldcf = P.leadingBaseCoefficient();
        long m = ldcf.degree(0);
        long n = A.degree(0);
        GenPolynomial<C> c = A.leadingBaseCoefficient();
        GenPolynomial<GenPolynomial<C>> cc = afac.getZERO().sum(c);
        //System.out.println("cc = " + cc);
        ExpVector e = A.leadingExpVector();
        for (long i = m; i >= n; i--) {
            if (r.isZERO()) {
                return r;
            }
            GenPolynomial<GenPolynomial<C>> p = r.leadingBaseCoefficient();
            ExpVector g = r.leadingExpVector();
            long k = p.degree(0);
            if (i == k) {
                GenPolynomial<C> pl = p.leadingBaseCoefficient();
                ExpVector f = p.leadingExpVector();
                f = f.subtract(e);
                r = r.multiply(cc); // coeff cc
                h = A.multiply(pl, f); // coeff ac
                hr = new GenPolynomial<GenPolynomial<GenPolynomial<C>>>(pfac, h, g);
                r = r.subtract(hr);
            } else {
                r = r.multiply(cc);
            }
            //System.out.println("r = " + r);
        }
        if (r.degree(0) < P.degree(0)) { // recursion for degree
            r = coefficientPseudoRemainder(r, A);
        }
        return r;
    }


    /**
     * Polynomial leading coefficient pseudo remainder, base case.
     * @param P generic polynomial in 1+1 variables.
     * @param A generic polynomial in 1 variable.
     * @return pseudo remainder of the leading coefficient of P wrt. A, with
     *         ldcf(A)<sup>m'</sup> P = quotient * A + remainder.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<GenPolynomial<C>> coefficientPseudoRemainderBase(
                    GenPolynomial<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (A == null || A.isZERO()) { // findbugs
            throw new ArithmeticException(P + " division by zero " + A);
        }
        if (A.isONE()) {
            return P.ring.getZERO();
        }
        if (P.isZERO() || P.isONE()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        GenPolynomialRing<C> afac = A.ring; // == pfac.coFac
        GenPolynomial<GenPolynomial<C>> r = P;
        GenPolynomial<C> h;
        GenPolynomial<GenPolynomial<C>> hr;
        GenPolynomial<C> ldcf = P.leadingBaseCoefficient();
        long m = ldcf.degree(0);
        long n = A.degree(0);
        C c = A.leadingBaseCoefficient();
        GenPolynomial<C> cc = afac.getZERO().sum(c);
        //System.out.println("cc = " + cc);
        ExpVector e = A.leadingExpVector();
        for (long i = m; i >= n; i--) {
            if (r.isZERO()) {
                return r;
            }
            GenPolynomial<C> p = r.leadingBaseCoefficient();
            ExpVector g = r.leadingExpVector();
            long k = p.degree(0);
            if (i == k) {
                C pl = p.leadingBaseCoefficient();
                ExpVector f = p.leadingExpVector();
                f = f.subtract(e);
                r = r.multiply(cc); // coeff cc
                h = A.multiply(pl, f); // coeff ac
                hr = new GenPolynomial<GenPolynomial<C>>(pfac, h, g);
                r = r.subtract(hr);
            } else {
                r = r.multiply(cc);
            }
            //System.out.println("r = " + r);
        }
        if (r.degree(0) < P.degree(0)) { // recursion for degree
            r = coefficientPseudoRemainderBase(r, A);
        }
        return r;
    }


    /**
     * Extract polynomials with degree zero in the main variable.
     * @param A list of generic polynomials in n variables.
     * @return Z = [a_i] with deg(a_i,x_n) = 0 and in n-1 variables.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<C>> zeroDegrees(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>(A.size());
        for (int i = 0; i < A.size(); i++) {
            GenPolynomial<C> q = A.get(i);
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac, q);
            if (fr.degree(0) == 0) {
                zeroDeg.add(fr.leadingBaseCoefficient());
            }
        }
        return zeroDeg;
    }

}
