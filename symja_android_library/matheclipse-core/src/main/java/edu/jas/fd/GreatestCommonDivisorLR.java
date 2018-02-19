/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import edu.jas.gbufd.SolvableSyzygyAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with monic polynomial remainder sequence. Fake implementation always returns
 * 1 for any gcds.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorLR<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorLR.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorLR(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     * @param s  algorithm for SolvableSyzygy computation.
     */
    public GreatestCommonDivisorLR(RingFactory<C> cf, SolvableSyzygyAbstract<C> s) {
        super(cf, s);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return [P, S, coP, coS, left, right] with left * coP * right = P and left *
     * coS * right = S.
     */
    public GCDcoFactors<C> leftRightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || P == null) {
            throw new IllegalArgumentException("null polynomials not allowed");
        }
        GenSolvablePolynomialRing<C> ring = P.ring;
        if (ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        GCDcoFactors<C> ret;
        if (P.isZERO() || S.isZERO()) {
            ret = new GCDcoFactors<C>(this, P, S, P, S, ring.getONE(), ring.getONE());
            return ret;
        }
        // compute on coefficients
        C contP = leftBaseContent(P);
        C contS = leftBaseContent(S);
        C contPS = contP.leftGcd(contS);
        if (contPS.signum() < 0) {
            contPS = contPS.negate();
        }
        if (debug) {
            //System.out.println("contP = " + contP + ", contS = " + contS + ", leftGcd(contP,contS) = " + contPS);
            C r1 = contP.leftDivide(contPS);
            boolean t = contPS.multiply(r1).equals(contP);
            if (!t) {
                System.out.println("r1: " + r1 + " * " + contPS + " != " + contP + ", r1*cP="
                        + contPS.multiply(r1));
            }
            C r2 = contS.leftDivide(contPS);
            t = contPS.multiply(r2).equals(contS);
            if (!t) {
                System.out.println("r2: " + r2 + " * " + contPS + " != " + contS + ", r2*cS="
                        + contPS.multiply(r2));
            }
            //System.out.println("leftGcd(contP,contS) = " + contPS);
        }
        GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) P.leftDivideCoeff(contP);
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) S.leftDivideCoeff(contS);
        if (debug) {
            boolean t = p.multiplyLeft(contP).equals(P);
            if (!t) {
                System.out.println(
                        "p: " + p + " * " + contP + " != " + P + ", p*cP=" + p.multiplyLeft(contP));
            }
            t = s.multiplyLeft(contS).equals(S);
            if (!t) {
                System.out.println(
                        "s: " + s + " * " + contS + " != " + S + ", s*cS=" + s.multiplyLeft(contS));
            }
        }
        // compute on main variable
        if (p.isONE()) {
            ret = new GCDcoFactors<C>(this, P, S, p, s, ring.valueOf(contPS), ring.getONE());
            return ret;
        }
        if (s.isONE()) {
            ret = new GCDcoFactors<C>(this, P, S, p, s, ring.valueOf(contPS), ring.getONE());
            return ret;
        }
        boolean field = ring.coFac.isField();
        GenSolvablePolynomial<C> r = p;
        GenSolvablePolynomial<C> q = s;
        GenSolvablePolynomial<C> x;
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C>leftBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
                //System.out.println("baseGCD: lc(q) = " + q.leadingBaseCoefficient() + ", lc(r) = " + r.leadingBaseCoefficient());
            } else {
                r = x;
            }
            //System.out.println("baseGCD: q = " + q + ", r = " + r);
        }
        q = leftBasePrimitivePart(q);
        q = (GenSolvablePolynomial<C>) q.abs();
        // not meaningful:
        // adjust signum of contPS
        //C qc = leftBaseContent(q);
        //q = (GenSolvablePolynomial<C>) q.leftDivideCoeff(qc); 
        //contPS = qc.multiply(contPS);
        //System.out.println("leftGcd()*qc = " + contPS);
        //q = rightBasePrimitivePart(q);
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        p = (GenSolvablePolynomial<C>) P.leftDivideCoeff(contPS); // not contP here
        s = (GenSolvablePolynomial<C>) S.leftDivideCoeff(contPS); // not contS here
        GenSolvablePolynomial<C> p1 = FDUtil.<C>leftBasePseudoQuotient(p, q); // TODO
        GenSolvablePolynomial<C> s1 = FDUtil.<C>leftBasePseudoQuotient(s, q); // TODO
        //System.out.println("p1 = " + p1 + ", s1 = " + s1);
        if (debug) {
            boolean t = p1.multiply(q).equals(p);
            if (!t) {
                GenSolvablePolynomial<C> p1q = (GenSolvablePolynomial<C>) leftBasePrimitivePart(
                        p1.multiply(q)).abs();
                GenSolvablePolynomial<C> pp = (GenSolvablePolynomial<C>) leftBasePrimitivePart(p).abs();
                if (!p1q.equals(pp)) {
                    System.out.println("p1: " + p1 + " * " + q + " != " + p);
                    System.out.println("pp(p1*q): " + p1q + " != " + pp);
                }
                p1q = p1.multiply(q);
                pp = p;
                C c1 = p1q.leadingBaseCoefficient();
                C c2 = pp.leadingBaseCoefficient();
                C[] oc = leftOreCond(c1, c2);
                p1q = p1q.multiplyLeft(oc[0]);
                pp = pp.multiplyLeft(oc[1]);
                if (!p1q.equals(pp)) {
                    System.out.println("p1q: " + p1q + " != " + pp);
                }
            }
            t = s1.multiply(q).equals(s);
            if (!t) {
                GenSolvablePolynomial<C> s1q = (GenSolvablePolynomial<C>) leftBasePrimitivePart(
                        s1.multiply(q)).abs();
                GenSolvablePolynomial<C> sp = (GenSolvablePolynomial<C>) leftBasePrimitivePart(s).abs();
                if (!s1q.equals(sp)) {
                    System.out.println("s1: " + s1 + " * " + q + " != " + s);
                    System.out.println("pp(s1*q): " + s1q + " != " + sp);
                }
                s1q = s1.multiply(q);
                sp = s;
                C c1 = s1q.leadingBaseCoefficient();
                C c2 = sp.leadingBaseCoefficient();
                C[] oc = leftOreCond(c1, c2);
                s1q = s1q.multiplyLeft(oc[0]);
                sp = sp.multiplyLeft(oc[1]);
                if (!s1q.equals(sp)) {
                    System.out.println("s1q: " + s1q + " != " + sp);
                }
            }
            t = p.multiplyLeft(contPS).equals(P); // contPS q p1 == P
            if (!t) {
                System.out.println("p1P: " + contPS + " * " + p + " != " + P);
            }
            t = s.multiplyLeft(contPS).equals(S);
            if (!t) {
                System.out.println("s1S: " + contPS + " * " + s + " != " + S);
            }
            //System.out.println("isField: " + field);
        }
        ret = new GCDcoFactors<C>(this, P, S, p1, s1, ring.valueOf(contPS), q);
        return ret;
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return [P, S, coP, coS, left, right] with left * coP * right = P and left *
     * coS * right = S.
     */
    public GCDcoFactors<C> rightLeftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || P == null) {
            throw new IllegalArgumentException("null polynomials not allowed");
        }
        GenSolvablePolynomialRing<C> ring = P.ring;
        if (ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        GCDcoFactors<C> ret;
        if (P.isZERO() || S.isZERO()) {
            ret = new GCDcoFactors<C>(this, P, S, P, S, ring.getONE(), ring.getONE());
            return ret;
        }
        // compute on coefficients
        C contP = rightBaseContent(P);
        C contS = rightBaseContent(S);
        C contPS = contP.rightGcd(contS);
        if (contPS.signum() < 0) {
            contPS = contPS.negate();
        }
        if (debug) {
            //System.out.println("contP = " + contP + ", contS = " + contS + ", rightGcd(contP,contS) = " + contPS);
            C r1 = contP.rightDivide(contPS);
            boolean t = r1.multiply(contPS).equals(contP);
            if (!t) {
                System.out.println("r1: " + r1 + " * " + contPS + " != " + contP + ", r1*cP="
                        + r1.multiply(contPS));
            }
            C r2 = contS.rightDivide(contPS);
            t = r2.multiply(contPS).equals(contS);
            if (!t) {
                System.out.println("r2: " + r2 + " * " + contPS + " != " + contS + ", r2*cS="
                        + r2.multiply(contPS));
            }
            //System.out.println("rightGcd(contP,contS) = " + contPS);
        }
        GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) P.rightDivideCoeff(contP);
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) S.rightDivideCoeff(contS);
        if (debug) {
            boolean t = p.multiply(contP).equals(P);
            if (!t) {
                System.out.println("p: " + p + " * " + contP + " != " + P + ", p*cP=" + p.multiply(contP));
            }
            t = s.multiply(contS).equals(S);
            if (!t) {
                System.out.println("s: " + s + " * " + contS + " != " + S + ", s*cS=" + s.multiply(contS));
            }
        }
        // compute on main variable
        if (p.isONE()) {
            ret = new GCDcoFactors<C>(this, P, S, p, s, ring.getONE(), ring.valueOf(contPS));
            return ret;
        }
        if (s.isONE()) {
            ret = new GCDcoFactors<C>(this, P, S, p, s, ring.getONE(), ring.valueOf(contPS));
            return ret;
        }
        boolean field = ring.coFac.isField();
        GenSolvablePolynomial<C> r = p;
        GenSolvablePolynomial<C> q = s;
        GenSolvablePolynomial<C> x;
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C>rightBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
                //System.out.println("baseGCD: lc(q) = " + q.leadingBaseCoefficient() + ", lc(r) = " + r.leadingBaseCoefficient());
            } else {
                r = x;
            }
            //System.out.println("baseGCD: r = " + r);
        }
        q = rightBasePrimitivePart(q);
        q = (GenSolvablePolynomial<C>) q.abs();
        //q = rightBasePrimitivePart(q);
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        p = (GenSolvablePolynomial<C>) P.rightDivideCoeff(contPS); // not contP here
        s = (GenSolvablePolynomial<C>) S.rightDivideCoeff(contPS); // not contS here
        GenSolvablePolynomial<C> p1 = FDUtil.<C>rightBasePseudoQuotient(p, q); // TODO
        GenSolvablePolynomial<C> s1 = FDUtil.<C>rightBasePseudoQuotient(s, q); // TODO
        //System.out.println("p1 = " + p1 + ", s1 = " + s1);
        if (debug) {
            boolean t = q.multiply(p1).equals(p);
            if (!t) {
                GenSolvablePolynomial<C> p1q = p1.multiply(q);
                GenSolvablePolynomial<C> pp = p;
                C c1 = p1q.leadingBaseCoefficient();
                C c2 = pp.leadingBaseCoefficient();
                C[] oc = rightOreCond(c1, c2);
                p1q = p1q.multiply(oc[0]);
                pp = pp.multiply(oc[1]);
                if (!p1q.equals(pp)) {
                    System.out.println("p1q: " + p1q + " != " + pp);
                }
            }
            t = q.multiply(s1).equals(s);
            if (!t) {
                GenSolvablePolynomial<C> s1q = q.multiply(s1);
                GenSolvablePolynomial<C> sp = s;
                C c1 = s1q.leadingBaseCoefficient();
                C c2 = sp.leadingBaseCoefficient();
                C[] oc = rightOreCond(c1, c2);
                s1q = s1q.multiply(oc[0]);
                sp = sp.multiply(oc[1]);
                if (!s1q.equals(sp)) {
                    System.out.println("s1q: " + s1q + " != " + sp);
                }
            }
            t = p.multiply(contPS).equals(P); // contPS q p1 == P
            if (!t) {
                System.out.println("p1P: " + contPS + " * " + p + " != " + P);
            }
            t = s.multiply(contPS).equals(S);
            if (!t) {
                System.out.println("s1S: " + contPS + " * " + s + " != " + S);
            }
            //System.out.println("isField: " + field);
        }
        ret = new GCDcoFactors<C>(this, P, S, p1, s1, q, ring.valueOf(contPS));
        return ret;
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return l = gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenSolvablePolynomialRing<C> ring = P.ring;
        if (ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        // compute on coefficients
        C contP = leftBaseContent(P);
        C contS = leftBaseContent(S);
        C contPS = contP.leftGcd(contS);
        if (contPS.signum() < 0) {
            contPS = contPS.negate();
        }
        if (debug) {
            //System.out.println("contP = " + contP + ", contS = " + contS + ", leftGcd(contP,contS) = " + contPS);
            C r1 = contP.leftDivide(contPS);
            boolean t = contPS.multiply(r1).equals(contP);
            if (!t) {
                System.out.println("r1: " + r1 + " * " + contPS + " != " + contP + ", r1*cP="
                        + contPS.multiply(r1));
            }
            C r2 = contS.leftDivide(contPS);
            t = contPS.multiply(r2).equals(contS);
            if (!t) {
                System.out.println("r2: " + r2 + " * " + contPS + " != " + contS + ", r2*cS="
                        + contPS.multiply(r2));
            }
            //System.out.println("contPS = " + contPS);
        }
        GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) P.leftDivideCoeff(contP);
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) S.leftDivideCoeff(contS);
        if (debug) {
            boolean t = p.multiplyLeft(contP).equals(P);
            if (!t) {
                System.out.println(
                        "p: " + p + " * " + contP + " != " + P + ", p*cP=" + p.multiplyLeft(contP));
            }
            t = s.multiplyLeft(contS).equals(S);
            if (!t) {
                System.out.println(
                        "s: " + s + " * " + contS + " != " + S + ", s*cS=" + s.multiplyLeft(contS));
            }
        }
        // compute on main variable
        if (p.isONE()) {
            return ring.valueOf(contPS);
        }
        if (s.isONE()) {
            return ring.valueOf(contPS);
        }
        boolean field = ring.coFac.isField();
        GenSolvablePolynomial<C> r = p;
        GenSolvablePolynomial<C> q = s;
        GenSolvablePolynomial<C> x;
        if (r.degree(0) > q.degree(0)) {
            x = r;
            r = q;
            q = x;
        }
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C>rightBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = x;
            }
            //System.out.println("baseGCD: r = " + r);
        }
        q = leftBasePrimitivePart(q);
        //q = rightBasePrimitivePart(q);
        //q = (GenSolvablePolynomial<C>) q.abs();
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        GenSolvablePolynomial<C> p1 = FDUtil.<C>rightBasePseudoQuotient(p, q); // TODO
        GenSolvablePolynomial<C> s1 = FDUtil.<C>rightBasePseudoQuotient(s, q); // TODO
        //System.out.println("p1 = " + p1 + ", s1 = " + s1);
        if (debug) {
            boolean t = q.multiply(p1).equals(p);
            if (!t) {
                GenSolvablePolynomial<C> p1q = q.multiply(p1);
                GenSolvablePolynomial<C> pp = p;
                C c1 = p1q.leadingBaseCoefficient();
                C c2 = pp.leadingBaseCoefficient();
                C[] oc = leftOreCond(c1, c2);
                p1q = p1q.multiplyLeft(oc[0]);
                pp = pp.multiplyLeft(oc[1]);
                if (!p1q.equals(pp)) {
                    System.out.println("Ore cond, p1q: " + p1q + " != " + pp);
                }
            }
            t = q.multiply(s1).equals(s);
            if (!t) {
                GenSolvablePolynomial<C> s1q = q.multiply(s1);
                GenSolvablePolynomial<C> sp = s;
                C c1 = s1q.leadingBaseCoefficient();
                C c2 = sp.leadingBaseCoefficient();
                C[] oc = leftOreCond(c1, c2);
                s1q = s1q.multiplyLeft(oc[0]);
                sp = sp.multiplyLeft(oc[1]);
                if (!s1q.equals(sp)) {
                    System.out.println("Ore cond, s1q: " + s1q + " != " + sp);
                }
            }
            t = p.multiplyLeft(contP).equals(P); // contPS q p1 == P
            if (!t) {
                System.out.println("p1P: " + contPS + " * " + p + " != " + P);
            }
            t = s.multiplyLeft(contS).equals(S); // PS
            if (!t) {
                System.out.println("s1S: " + contPS + " * " + s + " != " + S);
            }
            //System.out.println("isField: " + field);
        }
        return q.multiplyLeft(contPS);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return r = gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenSolvablePolynomialRing<C> ring = P.ring;
        if (ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        // compute on coefficients
        C contP = rightBaseContent(P);
        C contS = rightBaseContent(S);
        C contPS = contP.rightGcd(contS);
        if (contPS.signum() < 0) {
            contPS = contPS.negate();
        }
        if (debug) {
            //System.out.println("contP = " + contP + ", contS = " + contS + ", rightGcd(contP,contS) = " + contPS);
            C r1 = contP.rightDivide(contPS);
            boolean t = r1.multiply(contPS).equals(contP);
            if (!t) {
                System.out.println("r1: " + r1 + " * " + contPS + " != " + contP + ", r1*cP="
                        + r1.multiply(contPS));
            }
            C r2 = contS.rightDivide(contPS);
            t = r2.multiply(contPS).equals(contS);
            if (!t) {
                System.out.println("r2: " + r2 + " * " + contPS + " != " + contS + ", r2*cS="
                        + r2.multiply(contPS));
            }
            //System.out.println("contPS = " + contPS);
        }
        GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) P.rightDivideCoeff(contP);
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) S.rightDivideCoeff(contS);
        if (debug) {
            boolean t = p.multiply(contP).equals(P);
            if (!t) {
                System.out.println("p: " + p + " * " + contP + " != " + P + ", p*cP=" + p.multiply(contP));
            }
            t = s.multiply(contS).equals(S);
            if (!t) {
                System.out.println("s: " + s + " * " + contS + " != " + S + ", s*cS=" + s.multiply(contS));
            }
        }
        // compute on main variable
        if (p.isONE()) {
            return ring.valueOf(contPS);
        }
        if (s.isONE()) {
            return ring.valueOf(contPS);
        }
        boolean field = ring.coFac.isField();
        GenSolvablePolynomial<C> r = p;
        GenSolvablePolynomial<C> q = s;
        GenSolvablePolynomial<C> x;
        if (r.degree(0) > q.degree(0)) {
            x = r;
            r = q;
            q = x;
        }
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C>leftBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = x;
            }
            //System.out.println("baseGCD: r = " + r);
        }
        //q = leftBasePrimitivePart(q);
        q = rightBasePrimitivePart(q);
        //q = (GenSolvablePolynomial<C>) q.abs();
        //System.out.println("baseGCD: q = " + q + ", r = " + r);
        GenSolvablePolynomial<C> p1 = FDUtil.<C>leftBasePseudoQuotient(p, q); // TODO
        GenSolvablePolynomial<C> s1 = FDUtil.<C>leftBasePseudoQuotient(s, q); // TODO
        //System.out.println("p1 = " + p1 + ", s1 = " + s1);
        if (debug) {
            boolean t = p1.multiply(q).equals(p);
            if (!t) {
                GenSolvablePolynomial<C> p1q = p1.multiply(q);
                GenSolvablePolynomial<C> pp = p;
                C c1 = p1q.leadingBaseCoefficient();
                C c2 = pp.leadingBaseCoefficient();
                C[] oc = rightOreCond(c1, c2);
                p1q = p1q.multiply(oc[0]);
                pp = pp.multiply(oc[1]);
                if (!p1q.equals(pp)) {
                    System.out.println("Ore cond, p1q: " + p1q + " != " + pp);
                }
            }
            t = s1.multiply(q).equals(s);
            if (!t) {
                GenSolvablePolynomial<C> s1q = s1.multiply(q);
                GenSolvablePolynomial<C> sp = s;
                C c1 = s1q.leadingBaseCoefficient();
                C c2 = sp.leadingBaseCoefficient();
                C[] oc = rightOreCond(c1, c2);
                s1q = s1q.multiply(oc[0]);
                sp = sp.multiply(oc[1]);
                if (!s1q.equals(sp)) {
                    System.out.println("Ore cond, s1q: " + s1q + " != " + sp);
                }
            }
            t = p.multiply(contP).equals(P); // contPS q p1 == P
            if (!t) {
                System.out.println("p1P: " + contPS + " * " + p + " != " + P);
            }
            t = s.multiply(contS).equals(S); // PS
            if (!t) {
                System.out.println("s1S: " + contPS + " * " + s + " != " + S);
            }
            //System.out.println("isField: " + field);
        }
        return q.multiply(contPS);
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     * deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     * deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        return P.ring.getONE();
    }

}
