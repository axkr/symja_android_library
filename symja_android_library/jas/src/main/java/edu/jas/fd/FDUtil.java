/*
 * $Id$
 */

package edu.jas.fd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.gbufd.SolvableQuotient;
import edu.jas.gbufd.SolvableQuotientRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Factorization domain utilities, for example recursive pseudo remainder.
 * @author Heinz Kredel
 */

public class FDUtil {


    private static final Logger logger = Logger.getLogger(FDUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * GenSolvablePolynomial sparse pseudo remainder for univariate polynomials.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     *         remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> leftBaseSparsePseudoRemainder(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        if (P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        if (P instanceof RecSolvablePolynomial) {
            RecSolvablePolynomial<C> Pr = (RecSolvablePolynomial) P;
            if (!Pr.ring.coeffTable.isEmpty()) {
                throw new UnsupportedOperationException(
                                "RecSolvablePolynomial with twisted coeffs not supported");
            }
        }
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(P.ring.coFac);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff a
                C c = h.leadingBaseCoefficient();
                // need ga, gc: ga a = gc c
                C[] oc = fd.leftOreCond(a, c);
                C ga = oc[0];
                C gc = oc[1];
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gc); // coeff gc c, exp f
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse right pseudo remainder for univariate
     * polynomials.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     *         remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> rightBaseSparsePseudoRemainder(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        if (P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        if (P instanceof RecSolvablePolynomial) {
            RecSolvablePolynomial<C> Pr = (RecSolvablePolynomial) P;
            if (!Pr.ring.coeffTable.isEmpty()) {
                throw new UnsupportedOperationException(
                                "RecSolvablePolynomial with twisted coeffs not supported");
            }
        }
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(P.ring.coFac);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);
                h = S.multiply(f); // coeff a
                C a = r.leadingBaseCoefficient();
                C c = h.leadingBaseCoefficient();
                // need ga, gc: ga a = gc c
                C[] oc = fd.rightOreCond(a, c);
                C ga = oc[0];
                C gc = oc[1];
                r = r.multiply(ga); // coeff a c, exp f
                h = h.multiply(gc); // coeff a c, exp f
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient for univariate polynomials
     * or exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> leftBasePseudoQuotient(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return leftBasePseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient and remainder for univariate
     * polynomials or exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ldcf(S)<sup>m'</sup> P = quotient *
     *         S + remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] leftBasePseudoQuotientRemainder(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) { // ok if exact division
        //    throw new RuntimeException("univariate polynomials only");
        //}
        GenSolvablePolynomial<C>[] ret = new GenSolvablePolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if (P.isZERO() || S.isONE()) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if (P instanceof RecSolvablePolynomial) {
            RecSolvablePolynomial<C> Pr = (RecSolvablePolynomial) P;
            if (!Pr.ring.coeffTable.isEmpty()) {
                throw new UnsupportedOperationException(
                                "RecSolvablePolynomial with twisted coeffs not supported");
            }
        }
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(P.ring.coFac);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        GenSolvablePolynomial<C> q = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff a
                C c = h.leadingBaseCoefficient();
                // need ga, gc: ga a = gc c
                C[] oc = fd.leftOreCond(a, c);
                C ga = oc[0];
                C gc = oc[1];
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gc); // coeff gc c, exp f
                q = q.multiplyLeft(ga); // c
                q = (GenSolvablePolynomial<C>) q.sum(gc, f); // a
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenSolvablePolynomial sparse pseudo remainder for recursive solvable
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     *         remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveSparsePseudoRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        //if (S.isConstant()) {
        //    return P.ring.getZERO();
        //}
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);

        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) r.leadingBaseCoefficient();
                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff c, exp (f-e) e
                //wrong: h = S.multiply(f); // coeff c, exp e (f-e)
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) h.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, d);
                GenPolynomial<C> ga = oc[0];
                GenPolynomial<C> gd = oc[1];
                // ga a = gd d
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gd); // coeff gd d, exp f
                //if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                //    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient()
                //                    + ", lc(h) = " + h.leadingBaseCoefficient());
                //}
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
                //System.out.println("recRem:  lm(r) = " +  r.leadingMonomial());
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * Is recursive GenSolvablePolynomial pseudo quotient and remainder. For
     * recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return true, if P ~= q * S + r, else false.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     *      <b>Note:</b> not always meaningful and working
     */
    public static <C extends GcdRingElem<C>> boolean isRecursivePseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S,
                    GenSolvablePolynomial<GenPolynomial<C>> q, GenSolvablePolynomial<GenPolynomial<C>> r) {
        GenSolvablePolynomial<GenPolynomial<C>> rhs = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(S)
                        .sum(r);
        GenSolvablePolynomial<GenPolynomial<C>> lhs = P;
        GenPolynomial<C> ldcf = S.leadingBaseCoefficient();
        long d = P.degree(0) - S.degree(0) + 1;
        d = (d > 0 ? d : -d + 2);
        for (long i = 0; i <= d; i++) {
            //System.out.println("lhs = " + lhs);
            //System.out.println("rhs = " + rhs);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                return true;
            }
            lhs = lhs.multiply(ldcf);
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pp = P;
        rhs = q.multiply(S);
        //System.out.println("rhs,2 = " + rhs);
        for (long i = 0; i <= d; i++) {
            lhs = (GenSolvablePolynomial<GenPolynomial<C>>) Pp.subtract(r);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                //System.out.println("lhs,2 = " + lhs);
                return true;
            }
            Pp = Pp.multiply(ldcf);
        }
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);
        GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) P.leadingBaseCoefficient();
        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(S).sum(r);
        GenSolvablePolynomial<C> b = (GenSolvablePolynomial<C>) rhs.leadingBaseCoefficient();
        GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, b);
        GenPolynomial<C> ga = oc[0];
        GenPolynomial<C> gb = oc[1];
        //System.out.println("FDQR: OreCond:  a = " +  a + ",  b = " +  b);
        //System.out.println("FDQR: OreCond: ga = " + ga + ", gb = " + gb);
        // ga a = gd d
        GenSolvablePolynomial<GenPolynomial<C>> Pa = P.multiplyLeft(ga); // coeff ga a
        GenSolvablePolynomial<GenPolynomial<C>> Rb = rhs.multiplyLeft(gb); // coeff gb b  
        GenSolvablePolynomial<GenPolynomial<C>> D = (GenSolvablePolynomial<GenPolynomial<C>>) Pa.subtract(Rb);
        if (D.isZERO()) {
            return true;
        }
        if (debug) {
            logger.info("not QR: D = " + D);
        }
        //System.out.println("FDQR: Pa = " + Pa);
        //System.out.println("FDQR: Rb = " + Rb);
        //System.out.println("FDQR: Pa-Rb = " + D);
        return false;
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient for recursive
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return quotient with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     *         remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursivePseudoQuotient(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursivePseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient and remainder for
     * recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ore(ldcf(S)<sup>m'</sup>) P =
     *         quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>>[] recursivePseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) { // ok if exact division
        //    throw new RuntimeException("univariate polynomials only");
        //}
        GenSolvablePolynomial<GenPolynomial<C>>[] ret = new GenSolvablePolynomial[2];
        if (P == null || P.isZERO()) {
            ret[0] = S.ring.getZERO();
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if (S.isONE()) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);

        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        GenSolvablePolynomial<GenPolynomial<C>> q = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff c, exp (f/e) e
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) r.leadingBaseCoefficient();
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) h.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, d);
                GenPolynomial<C> ga = oc[0]; // d
                GenPolynomial<C> gd = oc[1]; // a
                // ga a = gd d
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gd); // coeff gd d, exp f
                q = q.multiplyLeft(ga); // d
                q = (GenSolvablePolynomial<GenPolynomial<C>>) q.sum(gd, f); // a
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * Is recursive GenSolvablePolynomial right pseudo quotient and remainder.
     * For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return true, if P ~= S * q + r, else false.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     *      <b>Note:</b> not always meaningful and working
     */
    public static <C extends GcdRingElem<C>> boolean isRecursiveRightPseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S,
                    GenSolvablePolynomial<GenPolynomial<C>> q, GenSolvablePolynomial<GenPolynomial<C>> r) {
        GenSolvablePolynomial<GenPolynomial<C>> rhs = (GenSolvablePolynomial<GenPolynomial<C>>) S.multiply(q)
                        .sum(r);
        GenSolvablePolynomial<GenPolynomial<C>> lhs = P;
        GenPolynomial<C> ldcf = S.leadingBaseCoefficient();
        long d = P.degree(0) - S.degree(0) + 1;
        d = (d > 0 ? d : -d + 2);
        for (long i = 0; i <= d; i++) {
            //System.out.println("lhs = " + lhs);
            //System.out.println("rhs = " + rhs);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                return true;
            }
            lhs = lhs.multiply(ldcf); // side?
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pp = P;
        rhs = S.multiply(q);
        //System.out.println("rhs,2 = " + rhs);
        for (long i = 0; i <= d; i++) {
            lhs = (GenSolvablePolynomial<GenPolynomial<C>>) Pp.subtract(r);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                //System.out.println("lhs,2 = " + lhs);
                return true;
            }
            Pp = Pp.multiply(ldcf); // side?
        }
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);

        GenSolvablePolynomial<GenPolynomial<C>> pr = P.rightRecursivePolynomial();
        GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) pr.leadingBaseCoefficient();

        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) S.multiply(q).sum(r);
        GenSolvablePolynomial<GenPolynomial<C>> rr = rhs.rightRecursivePolynomial();
        GenSolvablePolynomial<C> b = (GenSolvablePolynomial<C>) rr.leadingBaseCoefficient();

        GenSolvablePolynomial<C>[] oc = fd.rightOreCond(a, b);
        GenPolynomial<C> ga = oc[0];
        GenPolynomial<C> gb = oc[1];
        //System.out.println("FDQR: OreCond:  a = " +  a + ",  b = " +  b);
        //System.out.println("FDQR: OreCond: ga = " + ga + ", gb = " + gb);
        // a ga = d gd
        GenSolvablePolynomial<GenPolynomial<C>> Pa = FDUtil.<C> multiplyRightRecursivePolynomial(pr, ga); // coeff a ga
        GenSolvablePolynomial<GenPolynomial<C>> Rb = FDUtil.<C> multiplyRightRecursivePolynomial(rr, gb); // coeff b gb
        //System.out.println("right(P)  = " +  pr + ",  P   = " +  P);
        //System.out.println("right(rhs)= " +  rr + ",  rhs = " +  rhs);
        //System.out.println("Pa        = " +  Pa + ",  ga  = " +  ga);
        //System.out.println("Rb        = " +  Rb + ",  gb  = " +  gb);
        GenSolvablePolynomial<GenPolynomial<C>> D = (GenSolvablePolynomial<GenPolynomial<C>>) Pa.subtract(Rb);
        if (D.isZERO()) {
            return true;
        }
        if (true) {
            System.out.println("Pa = " + Pa);
            System.out.println("Rb = " + Rb);
            logger.info("not right QR: Pa-Rb = " + D);
        }
        return false;
    }


    /**
     * GenSolvablePolynomial recursive right pseudo quotient for recursive
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return quotient with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     *         remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightPseudoQuotient(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursiveRightPseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial right sparse pseudo remainder for recursive
     * solvable polynomials. <b>Note:</b> uses right multiplication of P by
     * ldcf(S), not always applicable.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ldcf(S)<sup>m'</sup> = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightSparsePseudoRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursiveRightPseudoQuotientRemainder(P, S)[1];
    }


    /**
     * GenSolvablePolynomial right sparse pseudo remainder for recursive
     * solvable polynomials. <b>Note:</b> uses right multiplication of P by
     * ldcf(S), not always applicable.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = quotient * S +
     *         remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightSparsePseudoRemainderOld(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        //GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);

                h = S.multiply(f); // coeff c, exp (f-e) e
                GenPolynomial<C> d = h.leadingBaseCoefficient();
                GenPolynomial<C> a = r.leadingBaseCoefficient();

                r = r.multiply(d); // coeff a d, exp f
                h = h.multiply(a); // coeff a d, exp f

                //if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                //    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient()
                //                               + ", lc(h) = " + h.leadingBaseCoefficient());
                //}
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial right sparse pseudo quotient and remainder for
     * recursive solvable polynomials. <b>Note:</b> uses right multiplication of
     * P by ldcf(S), not always applicable.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     *         remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>>[] recursiveRightPseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        GenSolvablePolynomial<GenPolynomial<C>>[] ret = new GenSolvablePolynomial[2];
        if (P == null || P.isZERO()) {
            ret[0] = S.ring.getZERO();
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if (S.isONE()) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        //if (S.isConstant()) {
        //    ret[0] = P.ring.getZERO();
        //    ret[1] = S.ring.getZERO();
        //    return ret;
        //}
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);

        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h, q, hr, rr;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        GenSolvablePolynomial<GenPolynomial<C>> qr = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);
                h = S.multiply(f); // coeff c, exp e (f/e)
                hr = h.rightRecursivePolynomial();
                rr = r.rightRecursivePolynomial();
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) rr.leadingBaseCoefficient();
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) hr.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.rightOreCond(a, d);
                GenPolynomial<C> ga = oc[0]; // d
                GenPolynomial<C> gd = oc[1]; // a
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                // a ga = d gd
                rr = FDUtil.<C> multiplyRightRecursivePolynomial(rr, ga); // exp f, coeff a ga, 
                hr = FDUtil.<C> multiplyRightRecursivePolynomial(hr, gd); // exp f, coeff d gd
                h = hr.evalAsRightRecursivePolynomial();
                r = rr.evalAsRightRecursivePolynomial();
                //if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                //    throw new RuntimeException("can not happen: lc(r) = " + r.leadingBaseCoefficient()
                //                               + ", lc(h) = " + h.leadingBaseCoefficient());
                //}
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
                qr = FDUtil.<C> multiplyRightRecursivePolynomial(qr, ga); // d
                qr = (GenSolvablePolynomial<GenPolynomial<C>>) qr.sum(gd, f); // a // same for right coefficients
                //System.out.println("r = " +  r + ", qr = " +  qr);
            } else {
                break;
            }
        }
        q = qr.evalAsRightRecursivePolynomial();
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenSolvablePolynomial.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivideRightEval(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pr = P.rightRecursivePolynomial();
        GenSolvablePolynomial<GenPolynomial<C>> Qr = FDUtil.<C> recursiveDivide(Pr, s);
        GenSolvablePolynomial<GenPolynomial<C>> Q = Qr.evalAsRightRecursivePolynomial();
        if (debug) {
            if (!Q.multiply(s).equals(P)) {
                System.out.println("rDivREval: P   = " + P + ", right(P) = " + Pr);
                System.out.println("rDivREval: Q   = " + Q + ", right(Q) = " + Qr);
                System.out.println("rDivREval: Q*s = " + Q.multiply(s) + ", s = " + s);
                //System.out.println("rDivREval: P.ring == Q.ring: " + P.ring.equals(Q.ring) );
                throw new RuntimeException("rDivREval: Q*s != P");
            }
        }
        return Q;
    }


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenSolvablePolynomial.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivide(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> p = P.ring.getZERO().copy();
        for (Map.Entry<ExpVector, GenPolynomial<C>> m1 : P.getMap().entrySet()) {
            GenSolvablePolynomial<C> c1 = (GenSolvablePolynomial<C>) m1.getValue();
            ExpVector e1 = m1.getKey();
            //GenSolvablePolynomial<C> c = FDUtil.<C> basePseudoQuotient(c1, s);
            GenSolvablePolynomial<C>[] QR = FDUtil.<C> leftBasePseudoQuotientRemainder(c1, s);
            GenSolvablePolynomial<C> c = QR[0];
            if (debug) {
                if (!QR[1].isZERO()) {
                    System.out.println("rDiv, P   = " + P);
                    System.out.println("rDiv, c1  = " + c1);
                    System.out.println("rDiv, s   = " + s);
                    System.out.println("rDiv, c   = " + c + ", r = " + QR[1]);
                    System.out.println("rDiv, c*s = " + c.multiply(s));
                    System.out.println("rDiv, s*c = " + s.multiply(c));
                    throw new RuntimeException("something is wrong: rem = " + QR[1]);
                }
            }
            if (!c.isZERO()) {
                //pv.put(e1, c); 
                p.doPutToMap(e1, c);
            } else {
                System.out.println("rDiv, P  = " + P);
                System.out.println("rDiv, c1 = " + c1);
                System.out.println("rDiv, s  = " + s);
                System.out.println("rDiv, c  = " + c);
                throw new RuntimeException("something is wrong: c is zero");
            }
        }
        return p;
    }


    /**
     * GenSolvablePolynomial recursive right quotient for recursive polynomials
     * and partial right exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenSolvablePolynomial.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivideRightPolynomial(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> p = P.ring.getZERO().copy();
        GenSolvablePolynomial<GenPolynomial<C>> Pr = P.rightRecursivePolynomial();
        logger.info("P = " + P + ", right(P) = " + Pr + ", left(s) = " + s);
        for (Map.Entry<ExpVector, GenPolynomial<C>> m1 : Pr.getMap().entrySet()) {
            GenSolvablePolynomial<C> c1 = (GenSolvablePolynomial<C>) m1.getValue();
            ExpVector e1 = m1.getKey();
            //GenSolvablePolynomial<C> c = FDUtil.<C> basePseudoQuotient(c1, s);
            GenSolvablePolynomial<C> c = FDUtil.<C> divideRightPolynomial(c1, s);
            //GenSolvablePolynomial<C>[] QR = FDUtil.<C> basePseudoQuotientRemainder(c1, s);
            if (!c.isZERO()) {
                //pv.put(e1, c); 
                p.doPutToMap(e1, c);
            }
        }
        GenSolvablePolynomial<GenPolynomial<C>> pl = p.evalAsRightRecursivePolynomial();
        logger.info("pl = " + pl + ", p = " + p);
        return pl;
    }


    /**
     * GenSolvablePolynomial right quotient for polynomials and partial right
     * exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param s GenSolvablePolynomial, constant wrt. the main variable.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> divideRightPolynomial(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        GenSolvablePolynomial<C> q;
        if (pfac.nvar <= 1) {
            GenSolvablePolynomial<C>[] QR1;
            QR1 = FDUtil.<C> leftBasePseudoQuotientRemainder(P, s);
            q = QR1[0];
            if (debug) {
                if (!QR1[1].isZERO()) {
                    System.out.println("rDivPol, P = " + P);
                    System.out.println("rDivPol, s = " + s);
                    throw new RuntimeException("non zero remainder, q = " + q + ", r = " + QR1[1]);
                }
            }
            return q;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenSolvablePolynomial<GenPolynomial<C>> pr, sr, qr, rr;
        GenSolvablePolynomial<GenPolynomial<C>>[] QR;
        pr = (GenSolvablePolynomial<GenPolynomial<C>>) PolyUtil.<C> recursive(rfac, P);
        sr = (GenSolvablePolynomial<GenPolynomial<C>>) PolyUtil.<C> recursive(rfac, s);
        //qr = FDUtil.<C> recursiveDivideRightPolynomial(pr,sc);
        QR = FDUtil.<C> recursiveRightPseudoQuotientRemainder(pr, sr);
        //QR = FDUtil.<C> recursivePseudoQuotientRemainder(pr,sr);
        qr = QR[0];
        rr = QR[1];
        if (debug) {
            if (!rr.isZERO()) {
                System.out.println("rDivPol, pr = " + pr);
                System.out.println("rDivPol, sr = " + sr);
                throw new RuntimeException("non zero remainder, q = " + qr + ", r = " + rr);
            }
        }
        q = (GenSolvablePolynomial<C>) PolyUtil.<C> distribute(pfac, qr);
        return q;
    }


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * partial right exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenSolvablePolynomial.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightDivide(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        if (!(P instanceof RecSolvablePolynomial)) {
            //return FDUtil.<C> recursiveDivide(P,s);
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) P.ring;
        if (rfac.coeffTable.isEmpty()) {
            //return FDUtil.<C> recursiveDivide(P,s);
        }
        //GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rfac.coFac;
        //GenPolynomial<C> one = cfac.getONE();
        RecSolvablePolynomial<C> onep = rfac.getONE();
        ExpVector zero = rfac.evzero;
        RecSolvablePolynomial<C> q = rfac.getZERO();
        RecSolvablePolynomial<C> r;
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P;
        //System.out.println("recRightDivide: p = " + p + ", s = " + s);
        while (!p.isZERO()) {
            ExpVector f = p.leadingExpVector();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) p.leadingBaseCoefficient();
            //GenSolvablePolynomial<C> c = FDUtil.<C> basePseudoQuotient(a, s);
            GenSolvablePolynomial<C>[] QR = FDUtil.<C> leftBasePseudoQuotientRemainder(a, s);
            //GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) a.divide(s);
            //System.out.println("content QR[1] = " + QR[1]);
            if (debug) {
                if (!QR[1].isZERO() || !a.remainder(s).isZERO()) {
                    logger.info("no exact division, rem = " + a.remainder(s) + ", r =" + QR[1]);
                    throw new RuntimeException("no exact division: r = " + QR[1]);
                }
            }
            GenSolvablePolynomial<C> c = QR[0];
            // c * s = a
            if (c.isZERO()) {
                System.out.println("rDiv, P  = " + P);
                System.out.println("rDiv, a  = " + a);
                System.out.println("rDiv, s  = " + s);
                System.out.println("rDiv, c  = " + c);
                throw new RuntimeException("something is wrong: c is zero");
            }
            //System.out.println("recRightDivide: a = " + a + ", c = " + c + ", f = " + f);
            r = onep.multiply(c, f, s, zero); // right: (c f) * 1 * (s zero)
            //System.out.println("recRightDivide: r   = " + r);
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            q = (RecSolvablePolynomial<C>) q.sum(c, f);
        }
        return q;
    }


    /*
     * RecSolvablePolynomial right coefficients from left coefficients.
     * <b>Note:</b> R is represented as a polynomial with left coefficients, the
     * implementation can at the moment not distinguish between left and right
     * coefficients.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @return R = sum( X<sup>i</sup> b<sub>i</sub> ), with P =
     *         sum(a<sub>i</sub> X<sup>i</sup> ) and eval(sum(X<sup>i</sup>
     *         b<sub>i</sub>)) == sum(a<sub>i</sub> X<sup>i</sup>)

    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> rightRecursivePolynomial(
                    GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        if (!(P instanceof RecSolvablePolynomial)) {
            return P;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) P.ring;
        if (rfac.coeffTable.isEmpty()) {
            return P;
        }
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rfac.coFac;
        GenPolynomial<C> one = cfac.getONE();
        RecSolvablePolynomial<C> onep = rfac.getONE();
        ExpVector zero = rfac.evzero;
        RecSolvablePolynomial<C> R = rfac.getZERO();
        RecSolvablePolynomial<C> r;
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P;
        while (!p.isZERO()) {
            ExpVector f = p.leadingExpVector();
            GenPolynomial<C> a = p.leadingBaseCoefficient();
            //r = h.multiply(a); // wrong method dispatch // right: f*a
            r = onep.multiply(one, f, a, zero); // right: (1 f) * 1 * (a zero)
            //System.out.println("a,f = " + a + ", " + f); // + ", h.ring = " + h.ring.toScript());
            //System.out.println("f*a = " + r); // + ", r.ring = " + r.ring.toScript());
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            R = (RecSolvablePolynomial<C>) R.sum(a, f);
        }
        return R;
    }
    */

    /*
     * Evaluate RecSolvablePolynomial as right coefficients polynomial.
     * <b>Note:</b> R is represented as a polynomial with left coefficients, the
     * implementation can at the moment not distinguish between left and right
     * coefficients.
     * @param <C> coefficient type.
     * @param R GenSolvablePolynomial with right coefficients.
     * @return P as evaluated polynomial R. R = sum( X<sup>i</sup> b<sub>i</sub>
     *         ), P = sum(a<sub>i</sub> X<sup>i</sup> ) = eval(sum(X<sup>i</sup>
     *         b<sub>i</sub>))

    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> evalAsRightRecursivePolynomial(
                    GenSolvablePolynomial<GenPolynomial<C>> R) {
        if (R == null || R.isZERO()) {
            return R;
        }
        if (!(R instanceof RecSolvablePolynomial)) {
            return R;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) R.ring;
        if (rfac.coeffTable.isEmpty()) {
            return R;
        }
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rfac.coFac;
        GenPolynomial<C> one = cfac.getONE();
        RecSolvablePolynomial<C> onep = rfac.getONE();
        ExpVector zero = rfac.evzero;
        RecSolvablePolynomial<C> q = rfac.getZERO();
        RecSolvablePolynomial<C> s;
        RecSolvablePolynomial<C> r = (RecSolvablePolynomial<C>) R;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : r.getMap().entrySet()) {
            ExpVector f = y.getKey();
            GenPolynomial<C> a = y.getValue();
            // f.multiply(a); // wrong method dispatch // right: f*a
            // onep.multiply(f).multiply(a) // should do now
            s = onep.multiply(one, f, a, zero); // right: (1 f) * 1 * (a zero)
            q = (RecSolvablePolynomial<C>) q.sum(s);
        }
        return q;
    }
    */

    /*
     * Test RecSolvablePolynomial right coefficients polynomial. <b>Note:</b> R
     * is represented as a polynomial with left coefficients, the implementation
     * can at the moment not distinguish between left and right coefficients.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial with left coefficients.
     * @param R GenSolvablePolynomial with right coefficients.
     * @return true, if R is polynomial with right coefficients of P. R = sum(
     *         X<sup>i</sup> b<sub>i</sub> ), with P = sum(a<sub>i</sub>
     *         X<sup>i</sup> ) and eval(sum(X<sup>i</sup> b<sub>i</sub>)) ==
     *         sum(a<sub>i</sub> X<sup>i</sup>)
              
    public static <C extends RingElem<C>> boolean isRightRecursivePolynomial(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> R) {
        if (P == null) {
            return R == null;
        }
        if (P.isZERO()) {
            return R.isZERO();
        }
        if (!(P instanceof RecSolvablePolynomial)) {
            return !(R instanceof RecSolvablePolynomial);
        }
        if (!(R instanceof RecSolvablePolynomial)) {
            return false;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) P.ring;
        if (rfac.coeffTable.isEmpty()) {
            RecSolvablePolynomialRing<C> rf = (RecSolvablePolynomialRing<C>) R.ring;
            return rf.coeffTable.isEmpty();
        }
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P;
        RecSolvablePolynomial<C> q = (RecSolvablePolynomial<C>) R.evalAsRightRecursivePolynomial();
        p = (RecSolvablePolynomial<C>) PolyUtil.<C> monic(p);
        q = (RecSolvablePolynomial<C>) PolyUtil.<C> monic(q);
        return p.equals(q);
    }
    */

    /**
     * RecSolvablePolynomial right coefficients multiply. <b>Note:</b> R is
     * represented as a polynomial with left coefficients, the implementation
     * can at the moment not distinguish between left and right coefficients.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial with right coefficients.
     * @param b coefficient.
     * @return R = P * b
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> multiplyRightRecursivePolynomial(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenPolynomial<C> b) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> Cp = P.ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        //Map<ExpVector, GenPolynomial<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, GenPolynomial<C>> Am = P.getMap(); //val;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            GenPolynomial<C> c = a.multiply(b);
            if (!c.isZERO()) {
                //Cm.put(e, c);
                Cp.doPutToMap(e, c);
            }
        }
        return Cp;
    }


    /**
     * Integral solvable polynomial from solvable rational function
     * coefficients. Represent as polynomial with integral solvable polynomial
     * coefficients by multiplication with the lcm(??) of the numerators of the
     * rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with solvable rational function coefficients to be
     *            converted.
     * @return polynomial with integral solvable polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> integralFromQuotientCoefficients(
                    GenSolvablePolynomialRing<GenPolynomial<C>> fac,
                    GenSolvablePolynomial<SolvableQuotient<C>> A) {
        GenSolvablePolynomial<GenPolynomial<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        GenSolvablePolynomial<C> c = null;
        GenSolvablePolynomial<C> d;
        GenSolvablePolynomial<C> x;
        GenSolvablePolynomial<C> z;
        GenPolynomialRing<C> cofac = (GenPolynomialRing) fac.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorPrimitive<C>(cofac.coFac);
        int s = 0;
        // lcm/ore of denominators ??
        Map<ExpVector, SolvableQuotient<C>> Am = A.getMap();
        for (SolvableQuotient<C> y : Am.values()) {
            x = y.den;
            // c = lcm(c,x)
            if (c == null) {
                c = x;
                s = x.signum();
            } else {
                d = fd.leftGcd(c, x);
                z = (GenSolvablePolynomial<C>) x.divide(d); // ??
                c = z.multiply(c); // ?? multiplyLeft
            }
        }
        if (s < 0) {
            c = (GenSolvablePolynomial<C>) c.negate();
        }
        for (Map.Entry<ExpVector, SolvableQuotient<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            SolvableQuotient<C> a = y.getValue();
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide(a.den);
            GenPolynomial<C> p = a.num.multiply(b);
            //B = B.sum( p, e ); // inefficient
            B.doPutToMap(e, p);
        }
        return B;
    }


    /**
     * Integral solvable polynomial from solvable rational function
     * coefficients. Represent as polynomial with integral solvable polynomial
     * coefficients by multiplication with the lcm(??) of the numerators of the
     * solvable rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with solvable rational function coefficients
     *            to be converted.
     * @return list of polynomials with integral solvable polynomial
     *         coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenSolvablePolynomial<GenPolynomial<C>>> integralFromQuotientCoefficients(
                    GenSolvablePolynomialRing<GenPolynomial<C>> fac,
                    Collection<GenSolvablePolynomial<SolvableQuotient<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenSolvablePolynomial<GenPolynomial<C>>> list = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                        L.size());
        for (GenSolvablePolynomial<SolvableQuotient<C>> p : L) {
            list.add(integralFromQuotientCoefficients(fac, p));
        }
        return list;
    }


    /**
     * Solvable rational function from integral solvable polynomial
     * coefficients. Represent as polynomial with type SolvableQuotient<C>
     * coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral solvable polynomial coefficients to be
     *            converted.
     * @return polynomial with type SolvableQuotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<SolvableQuotient<C>> quotientFromIntegralCoefficients(
                    GenSolvablePolynomialRing<SolvableQuotient<C>> fac,
                    GenSolvablePolynomial<GenPolynomial<C>> A) {
        GenSolvablePolynomial<SolvableQuotient<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<SolvableQuotient<C>> cfac = fac.coFac;
        SolvableQuotientRing<C> qfac = (SolvableQuotientRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) y.getValue();
            SolvableQuotient<C> p = new SolvableQuotient<C>(qfac, a); // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Solvable rational function from integral solvable polynomial
     * coefficients. Represent as polynomial with type SolvableQuotient<C>
     * coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral solvable polynomial
     *            coefficients to be converted.
     * @return list of polynomials with type SolvableQuotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenSolvablePolynomial<SolvableQuotient<C>>> quotientFromIntegralCoefficients(
                    GenSolvablePolynomialRing<SolvableQuotient<C>> fac,
                    Collection<GenSolvablePolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenSolvablePolynomial<SolvableQuotient<C>>> list = new ArrayList<GenSolvablePolynomial<SolvableQuotient<C>>>(
                        L.size());
        for (GenSolvablePolynomial<GenPolynomial<C>> p : L) {
            list.add(quotientFromIntegralCoefficients(fac, p));
        }
        return list;
    }

}
