/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableReductionAbstract;
import edu.jas.gb.SolvableReductionSeq;
import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordGroebnerBaseSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;


/**
 * Package gbufd utilities.
 * @author Heinz Kredel
 */

public class PolyGBUtil {


    private static final Logger logger = LogManager.getLogger(PolyGBUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Test for resultant.
     * @param A generic polynomial.
     * @param B generic polynomial.
     * @param r generic polynomial.
     * @return true if res(A,B) isContained in ideal(A,B), else false.
     */
    public static <C extends GcdRingElem<C>> boolean isResultant(GenPolynomial<C> A, GenPolynomial<C> B,
                    GenPolynomial<C> r) {
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
     * @param A list of generic polynomials sorted according to appearing main
     *            variables.
     * @return top pseudo remainder of P wrt. A for the appearing variables.
     */
    public static <C extends RingElem<C>> GenPolynomial<C> topPseudoRemainder(List<GenPolynomial<C>> A,
                    GenPolynomial<C> P) {
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
     * Top coefficient pseudo remainder of the leading coefficient of P wrt A in
     * the main variables.
     * @param P generic polynomial in n+1 variables.
     * @param A list of generic polynomials in n variables sorted according to
     *            appearing main variables.
     * @return pseudo remainder of the leading coefficient of P wrt A.
     */
    public static <C extends RingElem<C>> GenPolynomial<C> topCoefficientPseudoRemainder(
                    List<GenPolynomial<C>> A, GenPolynomial<C> P) {
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
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<GenPolynomial<C>>> coefficientPseudoRemainder(
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
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> coefficientPseudoRemainderBase(
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


    /**
     * Intersection. Generators for the intersection of ideals.
     * @param pfac polynomial ring
     * @param A list of polynomials
     * @param B list of polynomials
     * @return generators for (A \cap B)
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> intersect(GenPolynomialRing<C> pfac,
                    List<GenPolynomial<C>> A, List<GenPolynomial<C>> B) {
        if (A == null || A.isEmpty()) { // (0)
            return B;
        }
        if (B == null || B.isEmpty()) { // (0)
            return A;
        }
        int s = A.size() + B.size();
        List<GenPolynomial<C>> c = new ArrayList<GenPolynomial<C>>(s);
        GenPolynomialRing<C> tfac = pfac.extend(1);
        // term order is also adjusted
        for (GenPolynomial<C> p : A) {
            p = p.extend(tfac, 0, 1L); // t*p
            c.add(p);
        }
        for (GenPolynomial<C> p : B) {
            GenPolynomial<C> q = p.extend(tfac, 0, 1L);
            GenPolynomial<C> r = p.extend(tfac, 0, 0L);
            p = r.subtract(q); // (1-t)*p
            c.add(p);
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(tfac.coFac);
        logger.warn("intersect computing GB");
        List<GenPolynomial<C>> G = bb.GB(c);
        if (debug) {
            logger.debug("intersect GB = " + G);
        }
        List<GenPolynomial<C>> I = PolyUtil.<C> intersect(pfac, G);
        return I;
    }


    /**
     * Intersection. Generators for the intersection of ideals.
     * @param pfac solvable polynomial ring
     * @param A list of polynomials
     * @param B list of polynomials
     * @return generators for (A \cap B)
     */
    public static <C extends GcdRingElem<C>> List<GenSolvablePolynomial<C>> intersect(
                    GenSolvablePolynomialRing<C> pfac, List<GenSolvablePolynomial<C>> A,
                    List<GenSolvablePolynomial<C>> B) {
        if (A == null || A.isEmpty()) { // (0)
            return B;
        }
        if (B == null || B.isEmpty()) { // (0)
            return A;
        }
        int s = A.size() + B.size();
        List<GenSolvablePolynomial<C>> c = new ArrayList<GenSolvablePolynomial<C>>(s);
        GenSolvablePolynomialRing<C> tfac = pfac.extend(1);
        // term order is also adjusted
        for (GenSolvablePolynomial<C> p : A) {
            p = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 1L); // t*p
            c.add(p);
        }
        for (GenSolvablePolynomial<C> p : B) {
            GenSolvablePolynomial<C> q = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 1L);
            GenSolvablePolynomial<C> r = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 0L);
            p = (GenSolvablePolynomial<C>) r.subtract(q); // (1-t)*p
            c.add(p);
        }
        SolvableGroebnerBaseAbstract<C> sbb = SGBFactory.<C> getImplementation(tfac.coFac);
        //new SolvableGroebnerBaseSeq<C>();
        logger.warn("intersect computing GB");
        List<GenSolvablePolynomial<C>> g = sbb.leftGB(c);
        //List<GenSolvablePolynomial<C>> g = sbb.twosidedGB(c);
        if (debug) {
            logger.debug("intersect GB = " + g);
        }
        List<GenSolvablePolynomial<C>> I = PolyUtil.<C> intersect(pfac, g);
        return I;
    }


    /**
     * Intersection. Generators for the intersection of word ideals.
     * @param pfac word polynomial ring
     * @param A list of word polynomials
     * @param B list of word polynomials
     * @return generators for (A \cap B) if it exists
     */
    public static <C extends GcdRingElem<C>> List<GenWordPolynomial<C>> intersect(
                    GenWordPolynomialRing<C> pfac, List<GenWordPolynomial<C>> A,
                    List<GenWordPolynomial<C>> B) {
        if (A == null || A.isEmpty()) { // (0)
            return B;
        }
        if (B == null || B.isEmpty()) { // (0)
            return A;
        }
        int s = A.size() + B.size();
        List<GenWordPolynomial<C>> L = new ArrayList<GenWordPolynomial<C>>(s);
        GenWordPolynomialRing<C> tfac = pfac.extend(1);
        List<GenWordPolynomial<C>> gens = tfac.univariateList();
        //System.out.println("gens = " + gens);
        GenWordPolynomial<C> t = gens.get(gens.size() - 1);
        //System.out.println("t = " + t);
        // make t commute with other variables
        for (GenWordPolynomial<C> p : gens) {
            if (t == p) {
                continue;
            }
            GenWordPolynomial<C> c = t.multiply(p).subtract(p.multiply(t)); // t p - p t
            L.add(c);
        }
        for (GenWordPolynomial<C> p : A) {
            p = tfac.valueOf(p).multiply(t); // t p
            L.add(p);
        }
        for (GenWordPolynomial<C> p : B) {
            GenWordPolynomial<C> q = tfac.valueOf(p).multiply(t);
            GenWordPolynomial<C> r = tfac.valueOf(p);
            p = r.subtract(q); // (1-t) p
            L.add(p);
        }
        //System.out.println("L = " + L);
        WordGroebnerBaseAbstract<C> bb = new WordGroebnerBaseSeq<C>();
        logger.warn("intersect computing GB");
        List<GenWordPolynomial<C>> G = bb.GB(L);
        //System.out.println("G = " + G);
        if (debug) {
            logger.debug("intersect GB = " + G);
        }
        List<GenWordPolynomial<C>> I = PolyUtil.<C> intersect(pfac, G);
        return I;
    }


    /**
     * Solvable quotient and remainder via reduction.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return [ n/d, n - (n/d)*d ]
     */
    @SuppressWarnings({"cast","unchecked"})
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] quotientRemainder(
                    GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        GenSolvablePolynomial<C>[] res = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        if (d.isZERO()) {
            throw new RuntimeException("division by zero: " + n + "/" + d);
        }
        if (n.isZERO()) {
            res[0] = n;
            res[1] = n;
            return res;
        }
        GenSolvablePolynomialRing<C> r = n.ring;
        if (d.isONE()) {
            res[0] = n;
            res[1] = r.getZERO();
            return res;
        }
        // divide
        List<GenSolvablePolynomial<C>> Q = new ArrayList<GenSolvablePolynomial<C>>(1);
        Q.add(r.getZERO());
        List<GenSolvablePolynomial<C>> D = new ArrayList<GenSolvablePolynomial<C>>(1);
        D.add(d);
        SolvableReductionAbstract<C> sred = new SolvableReductionSeq<C>();
        res[1] = sred.rightNormalform(Q, D, n); // left
        res[0] = Q.get(0);
        return res;
    }


    /**
     * Subring generators.
     * @param A list of polynomials in n variables.
     * @return a Groebner base of polynomials in m &gt; n variables generating
     *         the subring of K[A].
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> subRing(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        logger.debug("pfac = " + pfac.toScript());
        int n = pfac.nvar;
        List<GenPolynomial<C>> Ap = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> a : A) {
            if (a == null || a.isZERO() || a.isONE()) {
                continue;
            }
            Ap.add(a);
        }
        int k = Ap.size();
        if (k == 0) {
            return Ap;
        }
        GenPolynomialRing<C> rfac = pfac.extendLower(k);
        logger.debug("rfac = " + rfac.toScript());
        assert rfac.nvar == n + k : "rfac.nvar == n+k";
        List<GenPolynomial<C>> sr = new ArrayList<GenPolynomial<C>>();
        int i = 0;
        for (GenPolynomial<C> a : Ap) {
            GenPolynomial<C> b = a.extendLower(rfac, 0, 0L);
            b = b.subtract(pfac.getONE().extendLower(rfac, i++, 1L));
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            sr.add(b);
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(pfac.coFac);
        List<GenPolynomial<C>> srg = bb.GB(sr);
        return srg;
    }


    /**
     * Subring membership.
     * @param A Groebner base of polynomials in m &gt; n variables generating
     *            the subring of elements of K[A].
     * @param g polynomial in n variables.
     * @return true, if g \in K[A], else false.
     */
    public static <C extends GcdRingElem<C>> boolean subRingMember(List<GenPolynomial<C>> A,
                    GenPolynomial<C> g) {
        if (A == null || A.isEmpty()) {
            return true;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        GenPolynomial<C> m = g;
        if (pfac.nvar != g.ring.nvar) {
            m = m.extendLower(pfac, 0, 0L);
        } else {
            throw new IllegalArgumentException("g must be extended: " + pfac.nvar + " == " + g.ring.nvar
                            + " did you mean method subRingAndMember()?");
        }
        //ReductionAbstract<C> red = new ReductionSeq<C>();
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(pfac.coFac);
        GenPolynomial<C> r = bb.red.normalform(A, m);
        //System.out.println("r = " + r);
        GenPolynomialRing<C> cfac = pfac.contract(g.ring.nvar);
        logger.debug("cfac = " + cfac.toScript());
        Map<ExpVector, GenPolynomial<C>> map = r.contract(cfac);
        //System.out.println("map = " + map);
        boolean t = map.size() == 1 && map.keySet().contains(g.ring.evzero);
        if (!t) {
            System.out.println("false: map = " + map);
        }
        return t;
    }


    /**
     * Subring and membership test.
     * @param A list of polynomials in n variables.
     * @param g polynomial in n variables.
     * @return true, if g \in K[A], else false.
     */
    public static <C extends GcdRingElem<C>> boolean subRingAndMember(List<GenPolynomial<C>> A,
                    GenPolynomial<C> g) {
        if (A == null || A.isEmpty()) {
            return true;
        }
        List<GenPolynomial<C>> srg = PolyGBUtil.<C> subRing(A);
        return PolyGBUtil.<C> subRingMember(srg, g);
    }


    /**
     * Chinese remainder theorem.
     * @param F = ( F_i ) list of list of polynomials in n variables.
     * @param A = ( f_i ) list of polynomials in n variables.
     * @return p \in \Cap_i (f_i + ideal(F_i)) if it exists, else null.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> chineseRemainderTheorem(
                    List<List<GenPolynomial<C>>> F, List<GenPolynomial<C>> A) {
        if (F == null || F.isEmpty() || A == null || A.isEmpty()) {
            throw new IllegalArgumentException("F and A may not be empty or null");
        }
        int m = F.size();
        if (m != A.size()) {
            throw new IllegalArgumentException("size(F) and size(A) must be equal");
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        logger.debug("pfac = " + pfac.toScript());
        GenPolynomialRing<C> rfac = pfac.extend(m);
        logger.debug("rfac = " + rfac.toScript());
        GenPolynomial<C> y = rfac.getONE();
        GenPolynomial<C> f = rfac.getZERO();
        int i = 0;
        List<GenPolynomial<C>> Fp = new ArrayList<GenPolynomial<C>>(); //m**2?
        //System.out.println("A = " + A);
        for (List<GenPolynomial<C>> Fi : F) {
            GenPolynomial<C> Yi = pfac.getONE().extend(rfac, i, 1L);
            y = y.subtract(Yi);
            List<GenPolynomial<C>> Fip = new ArrayList<GenPolynomial<C>>(Fi.size());
            //System.out.println("Fi = " + Fi);
            for (GenPolynomial<C> a : Fi) {
                GenPolynomial<C> b = a.extend(rfac, i, 1L);
                Fip.add(b);
            }
            //System.out.println("Fip = " + Fip);
            Fp.addAll(Fip);
            GenPolynomial<C> a = A.get(i);
            //System.out.println("a = " + a);
            f = f.sum(a.extend(rfac, i, 1L));
            i++;
        }
        Fp.add(y);
        //System.out.println("f = " + f);
        //System.out.println("Fp = " + Fp);
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(pfac.coFac);
        List<GenPolynomial<C>> Fpp = bb.GB(Fp);
        //System.out.println("Fpp = " + Fpp);
        GenPolynomial<C> h = bb.red.normalform(Fpp, f);
        //System.out.println("h = " + h);
        ////PseudoReduction<C> pr = new PseudoReductionSeq<C>();
        ////PseudoReductionEntry<C> fz = pr.normalformFactor(Fpp, f);
        ////System.out.println("fz = " + fz);
        List<GenPolynomial<C>> H = new ArrayList<GenPolynomial<C>>();
        H.add(h);
        H = PolyUtil.<C> intersect(pfac, H);
        //System.out.println("H != (): " + (! H.isEmpty()));
        if (H.isEmpty()) {
            return null;
        }
        return H.get(0);
    }


    /**
     * Is Chinese remainder.
     * @param F = ( F_i ) list of list of polynomials in n variables.
     * @param A = ( f_i ) list of polynomials in n variables.
     * @param h polynomial in n variables.
     * @return true if h \in \Cap_i (f_i + ideal(F_i)), else false.
     */
    public static <C extends GcdRingElem<C>> boolean isChineseRemainder(List<List<GenPolynomial<C>>> F,
                    List<GenPolynomial<C>> A, GenPolynomial<C> h) {
        if (h == null) {
            return false;
        }
        if (F == null || F.isEmpty() || A == null || A.isEmpty()) {
            return false;
        }
        if (F.size() != A.size()) {
            return false;
        }
        GenPolynomialRing<C> pfac = h.ring;
        if (!pfac.coFac.isField()) {
            //System.out.println("pfac = " + pfac.toScript());
            logger.error("only for field coefficients: " + pfac.toScript());
            //throw new IllegalArgumentException("only for field coefficients: " + pfac.toScript());
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(pfac.coFac);
        int i = 0;
        for (List<GenPolynomial<C>> Fi : F) {
            List<GenPolynomial<C>> Fp = bb.GB(Fi);
            //System.out.println("Fp = " + Fp);
            GenPolynomial<C> a = A.get(i);
            GenPolynomial<C> fi = bb.red.normalform(Fp, h.subtract(a));
            if (!fi.isZERO()) {
                //System.out.println("Fp = " + Fp + ", Fi = " + Fi);
                //System.out.println("h  = " + h  + ", a  = " + a  + ", fi  = " + fi);
                logger.info("h-a = " + h.subtract(a) + ", fi  = " + fi);
                return false;
            }
            i++;
        }
        return true;
    }


    /**
     * Chinese remainder theorem, interpolation.
     * @param fac polynomial ring over K in n variables.
     * @param E = ( E_i ), E_i = ( e_ij ) list of list of elements of K, the evaluation points.
     * @param V = ( f_i ) list of elements of K, the evaluation values.
     * @return p \in K[X1,...,Xn], with p(E_i) = f_i, if it exists, else null.
     */
    @SuppressWarnings({"cast","unchecked"})
    public static <C extends GcdRingElem<C>> GenPolynomial<C> CRTInterpolation(
                  GenPolynomialRing<C> fac, List<List<C>> E, List<C> V) {
        if (E == null || E.isEmpty() || V == null || V.isEmpty()) {
            throw new IllegalArgumentException("E and V may not be empty or null");
        }
        int m = E.size();
        if (m != V.size()) {
            throw new IllegalArgumentException("size(E) and size(V) must be equal");
        }
        //System.out.println("fac = " + fac.toScript());
        List<List<GenPolynomial<C>>> F = new ArrayList<List<GenPolynomial<C>>>(E.size());
        List<GenPolynomial<C>> A = new ArrayList<GenPolynomial<C>>(V.size());
        List<GenPolynomial<C>> gen = (List<GenPolynomial<C>>) fac.univariateList();
        //System.out.println("gen = " + gen);
        int i = 0;
        for (List<C> Ei : E) {
            List<GenPolynomial<C>> Fi = new ArrayList<GenPolynomial<C>>();
            int j = 0;
            for (C eij : Ei) {
                GenPolynomial<C> ep = gen.get(j);
                ep = ep.subtract( fac.valueOf(eij) );
                Fi.add(ep);
                j++;
            }
            F.add(Fi);
            String ai = " " + V.get(i); // (sic) .toString() not possible
            //System.out.println("ai = " + ai);
            GenPolynomial<C> ap = fac.valueOf(ai);
            A.add(ap);
            i++;
        }
        //System.out.println("F = " + F);
        //System.out.println("A = " + A);
        GenPolynomial<C> p = PolyGBUtil. <C>chineseRemainderTheorem(F,A);
        //System.out.println("p = " + p);
        //System.out.println("t = " + PolyGBUtil. <C>isChineseRemainder(F,A,p));
        return p;
    }

}
