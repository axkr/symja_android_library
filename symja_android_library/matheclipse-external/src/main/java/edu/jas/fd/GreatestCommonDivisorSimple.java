/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with monic polynomial remainder sequence. If C is a field, then the monic PRS
 * (on coefficients) is computed otherwise no simplifications in the reduction
 * are made.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorSimple<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorSimple.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorSimple(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        boolean field = P.ring.coFac.isField();
        System.out.println("baseGcd: field = " + field + ", is " + P.ring.coFac.toScript());
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<C> q;
        GenSolvablePolynomial<C> r;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else {
            q = P;
            r = S;
        }
        logger.info("degrees: e = {}, f = {}", e, f);
        C c;
        if (field) {
            r = r.monic();
            q = q.monic();
            c = P.ring.getONECoefficient();
        } else {
            r = (GenSolvablePolynomial<C>) r.abs();
            q = (GenSolvablePolynomial<C>) q.abs();
            C a = leftBaseContent(r);
            C b = leftBaseContent(q);
            r = divide(r, a); // indirection
            q = divide(q, b); // indirection
            c = leftGcd(a, b); // indirection
        }
        System.out.println("baseCont: gcd(cont) = " + c);
        if (r.isONE()) {
            return r.multiplyLeft(c);
        }
        if (q.isONE()) {
            return q.multiplyLeft(c);
        }
        GenSolvablePolynomial<C> x;
        logger.info("baseGCD: q = {}", q);
        logger.info("baseGCD: r = {}", r);
        System.out.println("baseGcd: rem = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> leftBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = x;
            }
            System.out.println("baseGcd: rem = " + r);
            logger.info("baseGCD_w: q = {}", q);
            logger.info("baseGCD_w: r = {}", r);
        }
        System.out.println("baseGcd: quot = " + q);
        q = leftBasePrimitivePart(q);
        logger.info("baseGCD: pp(q) = {}", q);
        return (GenSolvablePolynomial<C>) (q.multiplyLeft(c)).abs();
    }


    /**
     * Univariate GenSolvablePolynomial right greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        boolean field = P.ring.coFac.isField();
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<C> q;
        GenSolvablePolynomial<C> r;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else {
            q = P;
            r = S;
        }
        logger.debug("degrees: e = {}, f = {}", e, f);
        C c;
        if (field) {
            r = r.monic();
            q = q.monic();
            c = P.ring.getONECoefficient();
        } else {
            r = (GenSolvablePolynomial<C>) r.abs();
            q = (GenSolvablePolynomial<C>) q.abs();
            C a = rightBaseContent(r);
            C b = rightBaseContent(q);
            r = rightDivide(r, a); // indirection
            q = rightDivide(q, b); // indirection
            c = rightGcd(a, b); // indirection
        }
        //System.out.println("baseCont: gcd(cont) = " + b);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        GenSolvablePolynomial<C> x;
        //System.out.println("baseGCD: q = " + q);
        //System.out.println("baseGCD: r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> rightBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = x;
            }
            //System.out.println("baseGCD: q = " + q);
            //System.out.println("baseGCD: r = " + r);
        }
        q = rightBasePrimitivePart(q);
        ///q = leftBasePrimitivePart(q);
        return (GenSolvablePolynomial<C>) (q.multiply(c)).abs();
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     *         deg_main(p) = deg_main(s) == 0.
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
        //boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        //boolean field = P.leadingBaseCoefficient().ring.isField();
        boolean field = P.ring.coFac.isField();
        System.out.println("recursiveUnivGcd: field = " + field);
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<GenPolynomial<C>> q, r, x, qs, rs, qp, rp;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else if (f < e) {
            q = P;
            r = S;
        } else { // f == e
            if (P.leadingBaseCoefficient().degree() > S.leadingBaseCoefficient().degree()) {
                q = P;
                r = S;
            } else {
                r = P;
                q = S;
            }
        }
        logger.debug("degrees: e = {}, f = {}", e, f);
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } else {
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        }
        GenSolvablePolynomial<C> a = leftRecursiveContent(r);
        System.out.println("recursiveUnivGcd: a = " + a);
        rs = FDUtil.<C> recursiveLeftDivide(r, a);
        System.out.println("recursiveUnivGcd: rs = " + rs);
        //logger.info("recCont a = {}, r = {}", a, r);
        if (!r.equals(rs.multiplyLeft(a))) { // todo: should be rs.multiplyLeft(a))
            System.out.println("recursiveUnivGcd: r         = " + r);
            System.out.println("recursiveUnivGcd: cont(r)   = " + a);
            System.out.println("recursiveUnivGcd: pp(r)     = " + rs);
            System.out.println("recursiveUnivGcd: pp(r)c(r) = " + rs.multiply(a));
            System.out.println("recursiveUnivGcd: c(r)pp(r) = " + rs.multiplyLeft(a));
            throw new RuntimeException("recursiveUnivGcd: r: not divisible");
        }
        r = rs;
        //GenSolvablePolynomial<C> b = rightRecursiveContent(q);
        GenSolvablePolynomial<C> b = leftRecursiveContent(q);
        System.out.println("recursiveUnivGcd: b = " + b);
        qs = FDUtil.<C> recursiveLeftDivide(q, b);
        System.out.println("recursiveUnivGcd: qs = " + qs);
        //qs = FDUtil.<C> recursiveRightDivide(q, b);
        //logger.info("recCont b = {}, q = {}", b, q);
        if (!q.equals(qs.multiplyLeft(b))) { // todo: should be qs.multiplyLeft(b))
            System.out.println("recursiveUnivGcd: q         = " + q);
            System.out.println("recursiveUnivGcd: cont(q)   = " + b);
            System.out.println("recursiveUnivGcd: pp(q)     = " + qs);
            System.out.println("recursiveUnivGcd: pp(q)c(q) = " + qs.multiply(b));
            System.out.println("recursiveUnivGcd: c(q)pp(q) = " + qs.multiplyLeft(b));
            throw new RuntimeException("recursiveUnivGcd: q: not divisible");
        }
        q = qs;
        logger.info("Gcd(content).ring = {}, a = {}, b = {}", a.ring.toScript(), a, b);
        GenSolvablePolynomial<C> c = leftGcd(a, b); // go to recursion
        //GenSolvablePolynomial<C> c = rightGcd(a, b); // go to recursion
        logger.info("Gcd(contents) c = {}, a = {}, b = {}", c, a, b);
        if (r.isONE()) {
            return r.multiplyLeft(c);
        }
        if (q.isONE()) {
            return q.multiplyLeft(c);
        }
        if (debug) {
            logger.info("r.ring = {}", r.ring.toScript());
            logger.info("gcd-loop, start: q = {}, r = {}", q, r);
        }
        System.out.println("recursiveUnivGcd: r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> recursiveSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = PolyUtil.<C> monic(x);
                //r = x.leftMonic();
            } else {
                r = x;
            }
            System.out.println("recursiveUnivGcd: r = " + r);
            if (debug) {
                logger.info("gcd-loop, rem: q = {}, r = {}", q, r);
            }
        }
        logger.info("gcd(div) = {}, rs = {}, qs = {}", q, rs, qs);
        rp = FDUtil.<C> recursiveSparsePseudoRemainder(rs, q);
        qp = FDUtil.<C> recursiveSparsePseudoRemainder(qs, q);
        if (!qp.isZERO() || !rp.isZERO()) {
            logger.info("gcd(div): rem(r,g) = {}, rem(q,g) = {}", rp, qp);
            rp = FDUtil.<C> recursivePseudoQuotient(rs, q);
            qp = FDUtil.<C> recursivePseudoQuotient(qs, q);
            logger.info("gcd(div): r/g = {}, q/g = {}", rp, qp);
            throw new RuntimeException("recGcd: div: not divisible");
        }
        qp = q;
        //q = rightRecursivePrimitivePart(q);
        q = leftRecursivePrimitivePart(q);
        if (!qp.equals(q)) {
            logger.info("gcd(pp) = {}, qp = {}", q, qp);
        }
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiplyLeft(c).abs();
        return q;
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P,S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     *         deg_main(p) = deg_main(s) == 0.
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
        //boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        boolean field = P.ring.coFac.isField();
        System.out.println("r_recursiveUnivGcd: field = " + field);
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<GenPolynomial<C>> q, r, x, qs, rs, qp, rp;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else if (f < e) {
            q = P;
            r = S;
        } else { // f == e
            if (P.leadingBaseCoefficient().degree() > S.leadingBaseCoefficient().degree()) {
                q = P;
                r = S;
            } else {
                r = P;
                q = S;
            }
        }
        logger.debug("RI-degrees: e = {}, f = {}", e, f);
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } else {
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        }
        GenSolvablePolynomial<C> a = rightRecursiveContent(r);
        //no: rs = FDUtil.<C> recursiveLeftDivide(r, a);
        rs = FDUtil.<C> recursiveRightDivide(r, a);
        System.out.println("recursiveUnivGcd: rs = " + rs);
        System.out.println("recursiveUnivGcd: r = " + r + ", rs * a = " + rs.multiply(a));
        System.out.println("recursiveUnivGcd: r = " + r + ", a * rs = " + rs.multiplyLeft(a));
        //logger.info("RI-recCont a = {}, r = {}", a, r);
        //logger.info("RI-recCont r/a = {}, r%a = {}", r, r.subtract(rs.multiplyLeft(a)));
        if (!r.equals(rs.multiply(a))) { // Left
            System.out.println("RI-recGcd: r         = " + r);
            System.out.println("RI-recGcd: cont(r)   = " + a);
            System.out.println("RI-recGcd: pp(r)     = " + rs);
            System.out.println("RI-recGcd: pp(r)c(r) = " + rs.multiply(a));
            System.out.println("RI-recGcd: c(r)pp(r) = " + rs.multiplyLeft(a));
            throw new RuntimeException("RI-recGcd: pp: not divisible");
        }
        r = rs;
        GenSolvablePolynomial<C> b = rightRecursiveContent(q);
        System.out.println("recursiveUnivGcd: b = " + b);
        qs = FDUtil.<C> recursiveRightDivide(q, b);
        System.out.println("recursiveUnivGcd: qs = " + qs);
        System.out.println("recursiveUnivGcd: q = " + q + ", qs * b = " + qs.multiply(b));
        System.out.println("recursiveUnivGcd: q = " + q + ", b * qs = " + qs.multiplyLeft(b));
        //logger.info("RI-recCont b = {}, q = {}", b, q);
        //logger.info("RI-recCont q/b = {}, q%b = {}", qs, q.subtract(qs.multiplyLeft(b)));
        if (!q.equals(qs.multiply(b))) { // Left
            System.out.println("RI-recGcd: q         = " + q);
            System.out.println("RI-recGcd: cont(q)   = " + b);
            System.out.println("RI-recGcd: pp(q)     = " + qs);
            System.out.println("RI-recGcd: pp(q)c(q) = " + qs.multiply(b));
            System.out.println("RI-recGcd: c(q)pp(q) = " + qs.multiplyLeft(b));
            throw new RuntimeException("RI-recGcd: pp: not divisible");
        }
        q = qs;
        //no: GenSolvablePolynomial<C> c = rightGcd(a, b); // go to recursion
        GenSolvablePolynomial<C> c = rightGcd(a, b); // go to recursion
        logger.info("RI-Gcd(contents) c = {}, a = {}, b = {}", c, a, b);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        if (debug) {
            logger.info("RI-r.ring = {}", r.ring.toScript());
            logger.info("RI-gcd-loop, start: q = {}, r = {}", q, r);
        }
        while (!r.isZERO()) {
            x = FDUtil.<C> recursiveRightSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = PolyUtil.<C> monic(x);
            } else {
                r = x;
            }
            if (debug) {
                logger.info("RI-gcd-loop, rem: q = {}, r = {}", q, r);
            }
        }
        logger.info("RI-gcd(div) = {}, rs = {}, qs = {}", q, rs, qs);
        rp = FDUtil.<C> recursiveRightSparsePseudoRemainder(rs, q);
        qp = FDUtil.<C> recursiveRightSparsePseudoRemainder(qs, q);
        if (!qp.isZERO() || !rp.isZERO()) {
            logger.info("RI-gcd(div): rem(r,g) = {}, rem(q,g) = {}", rp, qp);
            rp = FDUtil.<C> recursivePseudoQuotient(rs, q);
            qp = FDUtil.<C> recursivePseudoQuotient(qs, q);
            logger.info("RI-gcd(div): r/g = {}, q/g = {}", rp, qp);
            //logger.info("gcd(div): rp*g = {},  qp*g = {}", rp.multiply(q), qp.multiply(q));
            throw new RuntimeException("recGcd: div: not divisible");
        }
        qp = q;
        logger.info("RI-recGcd(P,S) pre pp okay: qp = {}", qp);
        q = rightRecursivePrimitivePart(q);
        //q = leftRecursivePrimitivePart(q); // sic
        if (!qp.equals(q)) {
            logger.info("RI-gcd(pp) = {}, qp = {}", q, qp); // + ", ring = {}", P.ring.toScript());
        }
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(c).abs();
        return q;
    }

}
