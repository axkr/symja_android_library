/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Solvable polynomials factorization domain utilities, for example recursive
 * pseudo remainder.
 *
 * @author Heinz Kredel
 */

public class FDUtil {


    private static final Logger logger = Logger.getLogger(FDUtil.class);


    private static final boolean debug = true; //logger.isDebugEnabled();


    private static final boolean info = logger.isInfoEnabled();


    /**
     * GenSolvablePolynomial sparse pseudo remainder for univariate polynomials.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     * remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> leftBaseSparsePseudoRemainder(
            GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return leftBasePseudoQuotientRemainder(P, S)[1];
    }


    /**
     * GenSolvablePolynomial sparse right pseudo remainder for univariate
     * polynomials.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     * remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> rightBaseSparsePseudoRemainder(
            GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return rightBasePseudoQuotientRemainder(P, S)[1];
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient for univariate polynomials
     * or exact division.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return quotient with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     * remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> leftBasePseudoQuotient(
            GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return leftBasePseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial right sparse pseudo quotient for univariate
     * polynomials or exact division.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return quotient with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     * remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> rightBasePseudoQuotient(
            GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return rightBasePseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient and remainder for univariate
     * polynomials or exact division.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ore(ldcf(S)<sup>m'</sup>) P =
     * quotient * S + remainder. m' &le; deg(P)-deg(S)
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
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorFake<C>(P.ring.coFac);
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
                //System.out.println("q = " + q);
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        int sp = P.signum();
        int ss = S.signum();
        int sq = q.signum();
        // sp = ss * sq
        if (sp != ss * sq) {
            q = (GenSolvablePolynomial<C>) q.negate();
            r = (GenSolvablePolynomial<C>) r.negate();
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient and remainder for univariate
     * polynomials or exact division.
     *
     * @param <C> coefficient type.
     * @param P   GenSolvablePolynomial.
     * @param S   nonzero GenSolvablePolynomial.
     * @return [ quotient, remainder ] with P ore(ldcf(S)<sup>m'</sup>) = S *
     * quotient + remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] rightBasePseudoQuotientRemainder(
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
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorFake<C>(P.ring.coFac);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        GenSolvablePolynomial<C> q = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                h = S.multiply(f); // coeff a
                C c = h.leadingBaseCoefficient();
                // need ga, gc: a ga = c gc
                C[] oc = fd.rightOreCond(a, c);
                C ga = oc[0];
                C gc = oc[1];
                r = r.multiply(ga); // coeff a ga, exp f
                h = h.multiply(gc); // coeff c gc, exp f wanted but is exp f * coeff c gc, okay for base
                q = q.multiply(ga); // c
                q = (GenSolvablePolynomial<C>) q.sum(gc, f); // a
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        int sp = P.signum();
        int ss = S.signum();
        int sq = q.signum();
        // sp = ss * sq
        if (sp != ss * sq) {
            q = (GenSolvablePolynomial<C>) q.negate();
            r = (GenSolvablePolynomial<C>) r.negate();
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * Is recursive GenSolvablePolynomial pseudo quotient and remainder. For
     * recursive polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return true, if P ~= q * S + r, else false.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     * <b>Note:</b> not always meaningful and working
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> boolean isRecursivePseudoQuotientRemainder(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S,
            GenSolvablePolynomial<GenPolynomial<C>> q, GenSolvablePolynomial<GenPolynomial<C>> r) {
        GenSolvablePolynomial<GenPolynomial<C>> rhs, lhs;
        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(S).sum(r);
        lhs = P;
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
            //System.out.println("lhs = " + lhs);
            //System.out.println("rhs = " + rhs);
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
     * GenSolvablePolynomial sparse pseudo remainder for recursive solvable
     * polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     * remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial, edu.jas.poly.GenPolynomial)
     * .
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveSparsePseudoRemainder(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursivePseudoQuotientRemainder(P, S)[1];
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient for recursive
     * polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return quotient with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     * remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursivePseudoQuotient(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursivePseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient and remainder for
     * recursive polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ore(ldcf(S)<sup>m'</sup>) P =
     * quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
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
            ExpVector g = r.leadingExpVector();
            ExpVector f = g;
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
                if (!r.isZERO() && g.equals(r.leadingExpVector())) {
                    throw new RuntimeException("degree not descending: r = " + r);
                }
            } else {
                break;
            }
        }
        int sp = P.signum();
        int ss = S.signum();
        int sq = q.signum();
        // sp = ss * sq
        if (sp != ss * sq) {
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.negate();
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.negate();
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * Is recursive GenSolvablePolynomial right pseudo quotient and remainder.
     * For recursive polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return true, if P ~= S * q + r, else false.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     * <b>Note:</b> not always meaningful and working
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> boolean isRecursiveRightPseudoQuotientRemainder(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S,
            GenSolvablePolynomial<GenPolynomial<C>> q, GenSolvablePolynomial<GenPolynomial<C>> r) {
        GenSolvablePolynomial<GenPolynomial<C>> rhs, lhs;
        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) S.multiply(q).sum(r);
        lhs = P;
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
            lhs = lhs.multiplyLeft(ldcf); // side?
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pp = P;
        rhs = S.multiply(q);
        //System.out.println("rhs,2 = " + rhs);
        for (long i = 0; i <= d; i++) {
            lhs = (GenSolvablePolynomial<GenPolynomial<C>>) Pp.subtract(r);
            //System.out.println("lhs = " + lhs);
            //System.out.println("rhs = " + rhs);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                //System.out.println("lhs,2 = " + lhs);
                return true;
            }
            Pp = Pp.multiplyLeft(ldcf); // side?
        }
        GenPolynomialRing<C> cofac = (GenPolynomialRing) P.ring.coFac;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>(cofac.coFac);

        //GenSolvablePolynomial<GenPolynomial<C>> pr = P.rightRecursivePolynomial();
        RecSolvablePolynomial<C> pr = (RecSolvablePolynomial<C>) P.rightRecursivePolynomial();
        GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) pr.leadingBaseCoefficient();

        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) S.multiply(q).sum(r);
        //GenSolvablePolynomial<GenPolynomial<C>> rr = rhs.rightRecursivePolynomial();
        RecSolvablePolynomial<C> rr = (RecSolvablePolynomial<C>) rhs.rightRecursivePolynomial();
        GenSolvablePolynomial<C> b = (GenSolvablePolynomial<C>) rr.leadingBaseCoefficient();

        GenSolvablePolynomial<C>[] oc = fd.rightOreCond(a, b);
        GenPolynomial<C> ga = oc[0];
        GenPolynomial<C> gb = oc[1];
        //System.out.println("FDQR: OreCond:  a = " +  a + ",  b = " +  b);
        //System.out.println("FDQR: OreCond: ga = " + ga + ", gb = " + gb);
        // a ga = d gd
        GenSolvablePolynomial<GenPolynomial<C>> Pa = pr.multiplyRightComm(ga); // coeff a ga
        GenSolvablePolynomial<GenPolynomial<C>> Rb = rr.multiplyRightComm(gb); // coeff b gb
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
     * GenSolvablePolynomial right sparse pseudo remainder for recursive
     * solvable polynomials. <b>Note:</b> uses right multiplication of P by
     * ldcf(S), not always applicable.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = quotient * S +
     * remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial, edu.jas.poly.GenPolynomial)
     * .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightSparsePseudoRemainder(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursiveRightPseudoQuotientRemainder(P, S)[1];
    }


    /**
     * GenSolvablePolynomial recursive right pseudo quotient for recursive
     * polynomials.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return quotient with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     * remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveRightPseudoQuotient(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        return recursiveRightPseudoQuotientRemainder(P, S)[0];
    }


    /**
     * GenSolvablePolynomial right sparse pseudo quotient and remainder for
     * recursive solvable polynomials. <b>Note:</b> uses right multiplication of
     * P by ldcf(S), not always applicable.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param S   nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ore(ldcf(S)<sup>m'</sup>) = S * quotient +
     * remainder.
     * @see edu.jas.poly.PolyUtil#recursiveSparsePseudoRemainder(edu.jas.poly.GenPolynomial, edu.jas.poly.GenPolynomial)
     * .
     */
    @SuppressWarnings("unchecked")
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
        GenSolvablePolynomial<GenPolynomial<C>> h, q, r;
        RecSolvablePolynomial<C> hr, rr, qr;
        r = P;
        qr = (RecSolvablePolynomial<C>) S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector g = r.leadingExpVector();
            ExpVector f = g;
            if (f.multipleOf(e)) {
                f = f.subtract(e);
                //System.out.println("f = " + f);
                h = S.multiply(f); // coeff c, exp e (f/e)
                hr = (RecSolvablePolynomial<C>) h.rightRecursivePolynomial();
                rr = (RecSolvablePolynomial<C>) r.rightRecursivePolynomial();
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) rr.leadingBaseCoefficient();
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) hr.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.rightOreCond(a, d);
                GenPolynomial<C> ga = oc[0]; // d
                GenPolynomial<C> gd = oc[1]; // a
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                // a ga = d gd
                //rr = rr.multiply(ga);   // exp f, coeff a ga 
                //hr = hr.multiply(gd,f); // exp f, coeff d gd
                rr = rr.multiplyRightComm(ga); // exp f, coeff a ga 
                hr = hr.multiplyRightComm(gd); // exp f, coeff d gd  ///.shift(f)
                h = hr.evalAsRightRecursivePolynomial();
                r = rr.evalAsRightRecursivePolynomial();
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
                qr = qr.multiplyRightComm(ga); // d
                qr = (RecSolvablePolynomial<C>) qr.sum(gd, f); // a // same for right coefficients
                //System.out.println("q = " + qr); //.leadingMonomial());
                if (!r.isZERO() && g.equals(r.leadingExpVector())) {
                    throw new RuntimeException("something is wrong: g == lc(r), terms not descending " + r);
                }
                //System.out.println("r = " +  r + ", qr = " +  qr);
            } else {
                break;
            }
        }
        q = qr.evalAsRightRecursivePolynomial();
        int sp = P.signum();
        int ss = S.signum();
        int sq = q.signum();
        // sp = ss * sq
        if (sp != ss * sq) {
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.negate();
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.negate();
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    // ----------------------------------------------------------------------


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * exact division by coefficient ring element.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param s   GenSolvablePolynomial.
     * @return P/s.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivideRightEval(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pr = P.rightRecursivePolynomial();
        GenSolvablePolynomial<GenPolynomial<C>> Qr = FDUtil.<C>recursiveLeftDivide(Pr, s); // Left/Right
        GenSolvablePolynomial<GenPolynomial<C>> Q = Qr.evalAsRightRecursivePolynomial();
        if (debug) {
            if (!Qr.multiplyLeft(s).equals(Pr)) {
                System.out.println("rDivREval: Pr   = " + Pr + ", P = " + P);
                System.out.println("rDivREval: Qr   = " + Qr + ", Q = " + Q);
                System.out.println("rDivREval: s*Qr = " + Qr.multiplyLeft(s) + ", s = " + s);
                System.out.println("rDivREval: Qr*s = " + Qr.multiply(s));
                //System.out.println("rDivREval: P.ring == Q.ring: " + P.ring.equals(Q.ring) );
                throw new RuntimeException("rDivREval: s*Qr != Pr");
            }
            if (!Q.multiply(s).equals(P)) {
                System.out.println("rDivREval: P   = " + P + ", right(P) = " + Pr);
                System.out.println("rDivREval: Q   = " + Q + ", right(Q) = " + Qr);
                System.out.println("rDivREval: Q*s = " + Q.multiply(s) + ", s = " + s);
                System.out.println("rDivREval: s*Q = " + Q.multiplyLeft(s));
                //System.out.println("rDivREval: P.ring == Q.ring: " + P.ring.equals(Q.ring) );
                throw new RuntimeException("rDivREval: Q*s != P");
            }
        }
        return Q;
    }


    /**
     * GenSolvablePolynomial left recursive quotient for recursive polynomials
     * and exact division by coefficient ring element.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param s   GenSolvablePolynomial.
     * @return this/s.
     */
    @SuppressWarnings({"unchecked", "cast"})
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
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = (GenSolvablePolynomialRing<GenPolynomial<C>>) P.ring;
        GenSolvablePolynomial<GenPolynomial<C>> onep = rfac.getONE();
        //ExpVector zero = rfac.evzero;
        GenSolvablePolynomial<GenPolynomial<C>> q = rfac.getZERO();
        GenSolvablePolynomial<GenPolynomial<C>> r;
        GenSolvablePolynomial<GenPolynomial<C>> p = P; //.ring.getZERO().copy();
        while (!p.isZERO()) {
            // for (Map.Entry<ExpVector, GenPolynomial<C>> m1 : P.getMap().entrySet()) {
            Map.Entry<ExpVector, GenPolynomial<C>> m1 = p.leadingMonomial();
            GenSolvablePolynomial<C> c1 = (GenSolvablePolynomial<C>) m1.getValue();
            ExpVector e1 = m1.getKey();
            GenSolvablePolynomial<C> c = c1.divide(s);
            if (c.isZERO()) {
                throw new RuntimeException("something is wrong: c is zero, c1 = " + c1 + ", s = " + s);
            }
            r = onep.multiplyLeft(c.multiply(s), e1); // right: (c e1) * 1 * (s zero)
            if (!c1.equals(r.leadingBaseCoefficient())) {
                System.out.println("recRightDivide: lc(r) = " + r.leadingBaseCoefficient() + ", c1 = " + c1);
                System.out.println("recRightDivide: lc(r) = " + c.multiply(s) + ", c = " + c + ", s = " + s);
                throw new RuntimeException("something is wrong: lc(r) != c*s");
            }
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            if (!p.isZERO() && e1.compareTo(p.leadingExpVector()) == 0) {
                System.out.println("recRightDivide: c     = " + c);
                System.out.println("recRightDivide: lt(p) = " + p.leadingExpVector() + ", e1 = " + e1);
                System.out.println("recRightDivide: c1/s  = " + c1.divide(s));
                System.out.println("recRightDivide: s*c   = " + s.multiply(c));
                System.out.println("recRightDivide: c*s   = " + c.multiply(s));
                throw new RuntimeException("something is wrong: degree not descending");
            }
            q = (RecSolvablePolynomial<C>) q.sum(c, e1);
        }
        return q;
    }


    /*
     * GenSolvablePolynomial recursive right quotient for recursive polynomials
     * and partial right exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenSolvablePolynomial.
     * @return this/s.
     @SuppressWarnings("unchecked")
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
    */


    /*
     * GenSolvablePolynomial right quotient for polynomials and partial right
     * exact division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param s GenSolvablePolynomial, constant wrt. the main variable.
     * @return P/s.
     @SuppressWarnings("unchecked")
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
    */


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * partial right exact division by coefficient ring element.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param s   GenSolvablePolynomial.
     * @return P/s.
     */
    @SuppressWarnings({"unchecked", "cast"})
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
        RecSolvablePolynomial<C> onep = rfac.getONE();
        //ExpVector zero = rfac.evzero;
        RecSolvablePolynomial<C> q = rfac.getZERO();
        RecSolvablePolynomial<C> r;
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P;
        //System.out.println("recRightDivide: p = " + p + ", s = " + s);
        while (!p.isZERO()) {
            Map.Entry<ExpVector, GenPolynomial<C>> m1 = p.leadingMonomial();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) m1.getValue();
            ExpVector f = m1.getKey();
            GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) a.rightDivide(s);
            if (c.isZERO()) {
                //logger.info("something is wrong: c is zero, a = " + a + ", s = " + s);
                throw new RuntimeException("something is wrong: c is zero, a = " + a + ", s = " + s);
            }
            //r = onep.multiply(c, f, s, zero); // right: (c f) * 1 * (s zero)
            ///r = onep.multiply(c.multiply(s), f); //, c, zero); // 
            r = onep.multiply(s.multiply(c), f); //, c, zero); // 
            if (!a.equals(r.leadingBaseCoefficient())) {
                System.out.println("recRightDivide: a   = " + a + ", lc(r) = " + r.leadingBaseCoefficient());
                System.out.println("recRightDivide: c*s = " + c.multiply(s) + ", s = " + s + ", c = " + c);
                System.out.println(
                        "recRightDivide: s*c = " + s.multiply(c) + ", a%s = " + a.rightRemainder(s));
                throw new RuntimeException("something is wrong: c*s != a: " + rfac.toScript());
            }
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            if (!p.isZERO() && f.compareTo(p.leadingExpVector()) == 0) {
                System.out.println("recRightDivide: c     = " + c);
                System.out.println("recRightDivide: lt(p) = " + p.leadingExpVector() + ", f = " + f);
                System.out.println("recRightDivide: a/s   = " + a.divide(s));
                System.out.println("recRightDivide: s*c   = " + s.multiply(c));
                System.out.println("recRightDivide: c*s   = " + c.multiply(s));
                throw new RuntimeException("something is wrong: degree not descending");
            }
            q = (RecSolvablePolynomial<C>) q.sum(c, f);
        }
        return q;
    }


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and
     * partial left exact division by coefficient ring element.
     *
     * @param <C> coefficient type.
     * @param P   recursive GenSolvablePolynomial.
     * @param s   GenSolvablePolynomial.
     * @return P/s.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveLeftDivide(
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
        RecSolvablePolynomial<C> onep = rfac.getONE();
        //ExpVector zero = rfac.evzero;
        RecSolvablePolynomial<C> q = rfac.getZERO();
        RecSolvablePolynomial<C> r, Pp;
        //RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P;
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) P.rightRecursivePolynomial();
        Pp = p;
        while (!p.isZERO()) {
            ExpVector f = p.leadingExpVector();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) p.leadingBaseCoefficient();
            GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) a.divide(s);
            ///GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) a.rightDivide(s);
            if (c.isZERO()) {
                throw new RuntimeException("something is wrong: c is zero, a = " + a + ", s = " + s);
            }
            //r = onep.multiply(c, f, s, zero); // right: (c f) * 1 * (s zero)
            r = onep.multiplyLeft(c.multiply(s), f); // right: (c*s f) * one
            ///r = onep.multiplyLeft(s.multiply(c), f); // left: (s*c f) * one
            if (!a.equals(r.leadingBaseCoefficient())) {
                System.out.println("recLeftDivide: a        = " + a);
                C ac = a.leadingBaseCoefficient();
                C rc = r.leadingBaseCoefficient().leadingBaseCoefficient();
                C cc = rc.inverse().multiply(ac);
                System.out.println("recLeftDivide: cc       = " + cc);
                c = c.multiply(cc);
                r = onep.multiplyLeft(c.multiply(s), f); // right: (c f) * 1 * (s zero)
                ///r = onep.multiplyLeft(s.multiply(c), f); // left: (s*c f) * 1
                System.out.println("recLeftDivide: lc(r)    = " + r.leadingBaseCoefficient());
                throw new RuntimeException("something is wrong: c*s != a: " + rfac.toScript());
            }
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            if (!p.isZERO() && f.compareTo(p.leadingExpVector()) == 0) {
                System.out.println("recLeftDivide: P        = " + P + ", s = " + s);
                System.out.println("recLeftDivide: right(P) = " + Pp);
                System.out.println("recLeftDivide: c        = " + c);
                System.out.println("recLeftDivide: lt(p)    = " + p.leadingExpVector() + ", f = " + f);
                System.out.println("recLeftDivide: a/s      = " + a.divide(s));
                System.out.println("recLeftDivide: a\\s      = " + a.rightDivide(s));
                System.out.println("recLeftDivide: s*c      = " + s.multiply(c));
                System.out.println("recLeftDivide: c*s      = " + c.multiply(s));
                throw new RuntimeException("something is wrong: degree not descending");
            }
            q = (RecSolvablePolynomial<C>) q.sum(c, f);
        }
        q = (RecSolvablePolynomial<C>) q.evalAsRightRecursivePolynomial();
        return q;
    }


    /**
     * Integral solvable polynomial from solvable rational function
     * coefficients. Represent as polynomial with integral solvable polynomial
     * coefficients by multiplication with the lcm(??) of the numerators of the
     * rational function coefficients.
     *
     * @param fac result polynomial factory.
     * @param A   polynomial with solvable rational function coefficients to be
     *            converted.
     * @return polynomial with integral solvable polynomial coefficients.
     */
    @SuppressWarnings({"unchecked", "cast"})
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
     *
     * @param fac result polynomial factory.
     * @param L   list of polynomials with solvable rational function coefficients
     *            to be converted.
     * @return list of polynomials with integral solvable polynomial
     * coefficients.
     */
    @SuppressWarnings("unchecked")
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
     * coefficients. Represent as polynomial with type SolvableQuotient
     * <C> coefficients.
     *
     * @param fac result polynomial factory.
     * @param A   polynomial with integral solvable polynomial coefficients to be
     *            converted.
     * @return polynomial with type SolvableQuotient<C> coefficients.
     */
    @SuppressWarnings("unchecked")
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
     * coefficients. Represent as polynomial with type SolvableQuotient
     * <C> coefficients.
     *
     * @param fac result polynomial factory.
     * @param L   list of polynomials with integral solvable polynomial
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


    /**
     * Left greatest common divisor and cofactors.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return [ g=leftGcd(n,d), n/g, d/g ]
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] leftGcdCofactors(
            GenSolvablePolynomialRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        //GreatestCommonDivisorAbstract<C> e1 = new GreatestCommonDivisorSimple<C>(r.coFac);
        GreatestCommonDivisorAbstract<C> e1 = new GreatestCommonDivisorPrimitive<C>(r.coFac);
        //GreatestCommonDivisorAbstract<C> engine = new GreatestCommonDivisorFake<C>(r.coFac);
        GreatestCommonDivisorAbstract<C> e2 = new GreatestCommonDivisorSyzygy<C>(r.coFac);
        GreatestCommonDivisorAbstract<C> engine = new SGCDParallelProxy<C>(r.coFac, e1, e2);
        if (info) {
            logger.info("leftGCD_in: " + n + ", " + d);
        }
        GenSolvablePolynomial<C>[] res = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[3];
        res[0] = engine.leftGcd(n, d);
        //res[0] = PolyModUtil.<C> syzLeftGcd(r, n, d);
        res[1] = n;
        res[2] = d;
        if (res[0].isONE()) {
            return res;
        }
        if (info) {
            logger.info("leftGCD_out: " + res[0]);
        }
        GenSolvablePolynomial<C>[] nqr;
        nqr = FDUtil.<C>rightBasePseudoQuotientRemainder(n, res[0]);
        if (!nqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        GenSolvablePolynomial<C>[] dqr;
        dqr = FDUtil.<C>rightBasePseudoQuotientRemainder(d, res[0]);
        if (!dqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        res[1] = nqr[0];
        res[2] = dqr[0];
        return res;
    }


    /**
     * Right greatest common divisor and cofactors.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return [ g=rightGcd(n,d), n/g, d/g ]
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] rightGcdCofactors(
            GenSolvablePolynomialRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        //GreatestCommonDivisorAbstract<C> e1 = new GreatestCommonDivisorSimple<C>(r.coFac);
        //GreatestCommonDivisorAbstract<C> e1 = new GreatestCommonDivisorPrimitive<C>(r.coFac);
        GreatestCommonDivisorAbstract<C> engine = new GreatestCommonDivisorFake<C>(r.coFac);
        //GreatestCommonDivisorAbstract<C> e2 = new GreatestCommonDivisorSyzygy<C>(r.coFac);
        //GreatestCommonDivisorAbstract<C> engine = new SGCDParallelProxy<C>(r.coFac, e1, e2);
        if (info) {
            logger.info("rightGCD_in: " + n + ", " + d);
        }
        GenSolvablePolynomial<C>[] res = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[3];
        res[0] = engine.rightGcd(n, d);
        //res[0] = PolyModUtil.<C> syzRightGcd(r, n, d);
        res[1] = n;
        res[2] = d;
        if (res[0].isONE()) {
            return res;
        }
        if (info) {
            logger.info("rightGCD_out: " + res[0]);
        }
        GenSolvablePolynomial<C>[] nqr;
        nqr = FDUtil.<C>leftBasePseudoQuotientRemainder(n, res[0]);
        if (!nqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        GenSolvablePolynomial<C>[] dqr;
        dqr = FDUtil.<C>leftBasePseudoQuotientRemainder(d, res[0]);
        if (!dqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        res[1] = nqr[0];
        res[2] = dqr[0];
        return res;
    }

}
