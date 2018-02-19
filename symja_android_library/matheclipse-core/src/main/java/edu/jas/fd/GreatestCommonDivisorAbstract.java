/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.jas.gbufd.SolvableSyzygyAbstract;
import edu.jas.gbufd.SolvableSyzygySeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.StarRingElem;
import edu.jas.ufd.GCDFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class GreatestCommonDivisorAbstract<C extends GcdRingElem<C>>
        implements GreatestCommonDivisor<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Engine for syzygy computation.
     */
    final SolvableSyzygyAbstract<C> syz;


    /**
     * Coefficient ring.
     */
    final RingFactory<C> coFac;


    /*
     * Engine for commutative gcd computation.
     */
    //edu.jas.ufd.GreatestCommonDivisorAbstract<C> cgcd;


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorAbstract(RingFactory<C> cf) {
        this(cf, new SolvableSyzygySeq<C>(cf));
    }


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     * @param s  algorithm for SolvableSyzygy computation.
     */
    public GreatestCommonDivisorAbstract(RingFactory<C> cf, SolvableSyzygyAbstract<C> s) {
        coFac = cf;
        syz = s;
        //cgcd = GCDFactory.<C> getImplementation(pfac.coFac);
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenSolvablePolynomial base coefficient content.
     *
     * @param P GenSolvablePolynomial.
     * @return cont(P) with pp(P)*cont(P) = P.
     */
    public C leftBaseContent(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        if (P.ring.coFac.isField()) { // so to make monic
            return P.leadingBaseCoefficient();
        }
        C d = null;
        for (C c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = d.leftGcd(c);
            }
            if (d.isONE()) {
                return d;
            }
        }
        if (d.signum() < 0) {
            d = d.negate();
        }
        return d;
    }


    /**
     * GenSolvablePolynomial right base coefficient content.
     *
     * @param P GenSolvablePolynomial.
     * @return cont(P) with cont(P)*pp(P) = P.
     */
    public C rightBaseContent(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        if (P.ring.coFac.isField()) { // so to make monic
            return P.leadingBaseCoefficient(); // todo check move to right
        }
        C d = null;
        for (C c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = d.rightGcd(c); // DONE does now exist
            }
            if (d.isONE()) {
                return d;
            }
        }
        if (d.signum() < 0) {
            d = d.negate();
        }
        return d;
    }


    /**
     * GenSolvablePolynomial base coefficient primitive part.
     *
     * @param P GenSolvablePolynomial.
     * @return pp(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> leftBasePrimitivePart(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = leftBaseContent(P);
        if (d.isONE()) {
            return P;
        }
        if (P.ring.coFac.isField()) { // make monic
            return P.multiplyLeft(d.inverse()); // avoid the divisions
            //return P.multiply( d.inverse() ); // avoid the divisions
        }
        //GenSolvablePolynomial<C> pp = (GenSolvablePolynomial<C>) P.rightDivideCoeff(d); // rightDivide TODO/done
        GenSolvablePolynomial<C> pp = (GenSolvablePolynomial<C>) P.leftDivideCoeff(d); // TODO
        if (debug) {
            GenSolvablePolynomial<C> p = pp.multiplyLeft(d);
            if (!p.equals(P)) {
                throw new ArithmeticException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * GenSolvablePolynomial right base coefficient primitive part.
     *
     * @param P GenSolvablePolynomial.
     * @return pp(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> rightBasePrimitivePart(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = rightBaseContent(P);
        if (d.isONE()) {
            return P;
        }
        if (P.ring.coFac.isField()) { // make monic
            return P.multiplyLeft(d.inverse()); // avoid the divisions
        }
        GenSolvablePolynomial<C> pp = (GenSolvablePolynomial<C>) P.leftDivideCoeff(d); // leftDivide TODO/done
        if (debug) {
            GenSolvablePolynomial<C> p = pp.multiplyLeft(d);
            if (!p.equals(P)) {
                throw new ArithmeticException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses sparse
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    public abstract GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P,
                                                         GenSolvablePolynomial<C> S);


    /**
     * Univariate GenSolvablePolynomial right greatest common divisor. Uses
     * sparse pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    public abstract GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P,
                                                          GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial commuting recursive content.
     *
     * @param P recursive GenSolvablePolynomial with commuting main and
     *          coefficient variables.
     * @return cont(P) with cont(P)*pp(P) = pp(P)*cont(P).
     */
    public GenSolvablePolynomial<C> recursiveContent(GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P instanceof RecSolvablePolynomial) {
            RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) P.ring;
            if (!rfac.coeffTable.isEmpty()) {
                throw new IllegalArgumentException("P is a RecSolvablePolynomial, use recursiveContent()");
            }
        }
        if (P.isZERO()) {
            return (GenSolvablePolynomial<C>) P.ring.getZEROCoefficient();
        }
        if (P.isONE()) {
            return (GenSolvablePolynomial<C>) P.ring.getONECoefficient();
        }
        if (P.leadingBaseCoefficient().isONE()) {
            return (GenSolvablePolynomial<C>) P.ring.getONECoefficient();
        }
        //GenSolvablePolynomial<GenPolynomial<C>> p = P;
        GenSolvablePolynomial<C> d = null;
        for (GenPolynomial<C> cp : P.getMap().values()) {
            GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) cp;
            if (d == null) {
                d = c;
            } else {
                ///d = leftGcd(d, c); // go to recursion
                d = rightGcd(d, c); // go to recursion
            }
            if (d.isONE()) {
                return d;
            }
        }
        return (GenSolvablePolynomial<C>) d.abs();
    }


    /**
     * GenSolvablePolynomial right recursive content.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return cont(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> rightRecursiveContent(GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P != null");
        }
        if (P.isZERO()) {
            return (GenSolvablePolynomial<C>) P.ring.getZEROCoefficient();
        }
        if (P.leadingBaseCoefficient().isONE()) {
            return (GenSolvablePolynomial<C>) P.ring.getONECoefficient();
        }
        GenSolvablePolynomial<C> d = null, cs = null, x;
        GenSolvablePolynomial<GenPolynomial<C>> Pr = P.rightRecursivePolynomial();
        logger.info("RI-recCont: P = " + P + ", right(P) = " + Pr);
        for (GenPolynomial<C> c : Pr.getMap().values()) {
            cs = (GenSolvablePolynomial<C>) c;
            if (d == null) {
                d = cs;
            } else {
                x = d;
                d = leftGcd(d, cs); // go to recursion, P = P'*gcd(P,S)
                ///d = rightGcd(d, cs); // go to recursion,  P = gcd(P,S)*P'
                logger.info("RI-recCont: d = " + x + ", cs = " + cs + ", d = " + d);
            }
            if (d.isONE()) {
                return d;
            }
        }
        return (GenSolvablePolynomial<C>) d.abs();
    }


    /**
     * GenSolvablePolynomial right recursive primitive part.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return pp(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursivePrimitivePart(
            GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenSolvablePolynomial<C> d = rightRecursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> pp;
        pp = FDUtil.<C>recursiveLeftDivide(P, d); //RightEval
        if (debug) { // not checkable
            if (!P.equals(pp.multiply(d))) {
                System.out.println("RI-ppart, P         = " + P);
                System.out.println("RI-ppart, cont(P)   = " + d);
                System.out.println("RI-ppart, pp(P)     = " + pp);
                System.out.println("RI-ppart, pp(P)c(P) = " + pp.multiply(d));
                throw new RuntimeException("RI-primitivePart: P != pp(P)*cont(P)");
            }
        }
        return pp;
    }


    /**
     * GenSolvablePolynomial left recursive content.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return cont(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> leftRecursiveContent(GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P != null");
        }
        if (P.isZERO()) {
            return (GenSolvablePolynomial<C>) P.ring.getZEROCoefficient();
        }
        if (P.leadingBaseCoefficient().isONE()) {
            return (GenSolvablePolynomial<C>) P.ring.getONECoefficient();
        }
        GenSolvablePolynomial<C> d = null, cs = null;
        GenSolvablePolynomial<GenPolynomial<C>> Pr = P; //FDUtil.<C> rightRecursivePolynomial(P);
        logger.info("recCont: P = " + P + ", right(P) = " + Pr);
        for (GenPolynomial<C> c : Pr.getMap().values()) {
            cs = (GenSolvablePolynomial<C>) c;
            if (d == null) {
                d = cs;
            } else {
                d = rightGcd(d, cs); // go to recursion
                ///d = leftGcd(d, cs); // go to recursion
                logger.info("recCont: cs = " + cs + ", d = " + d);
            }
            if (d.isONE()) {
                return d;
            }
        }
        return (GenSolvablePolynomial<C>) d.abs();
    }


    /**
     * GenSolvablePolynomial left recursive primitive part.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return pp(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursivePrimitivePart(
            GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenSolvablePolynomial<C> d = leftRecursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> pp;
        pp = FDUtil.<C>recursiveRightDivide(P, d);
        if (debug) { // not checkable
            if (!P.equals(pp.multiplyLeft(d))) {
                System.out.println("ppart, P         = " + P);
                System.out.println("ppart, cont(P)   = " + d);
                System.out.println("ppart, pp(P)     = " + pp);
                System.out.println("ppart, pp(P)c(P) = " + pp.multiplyLeft(d));
                throw new RuntimeException("primitivePart: P != cont(P)*pp(P)");
            }
        }
        return pp;
    }


    /**
     * GenSolvablePolynomial base recursive content.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return baseCont(P) with pp(P)*cont(P) = P.
     */
    public C baseRecursiveContent(GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            GenSolvablePolynomialRing<C> cf = (GenSolvablePolynomialRing<C>) P.ring.coFac;
            return cf.coFac.getZERO();
        }
        C d = null;
        for (GenPolynomial<C> cp : P.getMap().values()) {
            GenSolvablePolynomial<C> c = (GenSolvablePolynomial<C>) cp;
            C cc = leftBaseContent(c);
            if (d == null) {
                d = cc;
            } else {
                d = gcd(d, cc);
            }
            if (d.isONE()) {
                return d;
            }
        }
        return d.abs();
    }


    /**
     * GenSolvablePolynomial base recursive primitive part.
     *
     * @param P recursive GenSolvablePolynomial.
     * @return basePP(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<GenPolynomial<C>> baseRecursivePrimitivePart(
            GenSolvablePolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = baseRecursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> pp = (GenSolvablePolynomial<GenPolynomial<C>>) PolyUtil
                .<C>baseRecursiveDivide(P, d);
        return pp;
    }


    /**
     * GenSolvablePolynomial left recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P recursive GenSolvablePolynomial.
     * @param S recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     * deg_main(p) = deg_main(s) == 0.
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveGcd(GenSolvablePolynomial<GenPolynomial<C>> P,
                                                                    GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar == 1) {
            return leftRecursiveUnivariateGcd(P, S);
        }
        // distributed polynomials gcd
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = P.ring;
        GenSolvablePolynomialRing<C> dfac;
        if (rfac instanceof RecSolvablePolynomialRing) {
            RecSolvablePolynomialRing<C> rf = (RecSolvablePolynomialRing<C>) rfac;
            dfac = RecSolvablePolynomialRing.<C>distribute(rf);
        } else {
            GenSolvablePolynomialRing<C> df = (GenSolvablePolynomialRing) rfac;
            dfac = df.distribute();
        }
        GenSolvablePolynomial<C> Pd = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(dfac, P);
        GenSolvablePolynomial<C> Sd = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(dfac, S);
        GenSolvablePolynomial<C> Dd = leftGcd(Pd, Sd);
        // convert to recursive
        GenSolvablePolynomial<GenPolynomial<C>> C = (GenSolvablePolynomial<GenPolynomial<C>>) PolyUtil
                .<C>recursive(rfac, Dd);
        return C;
    }


    /**
     * GenSolvablePolynomial right recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P recursive GenSolvablePolynomial.
     * @param S recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     * deg_main(p) = deg_main(s) == 0.
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar == 1) {
            return rightRecursiveUnivariateGcd(P, S);
        }
        // distributed polynomials gcd
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = P.ring;
        GenSolvablePolynomialRing<C> dfac;
        if (rfac instanceof RecSolvablePolynomialRing) {
            RecSolvablePolynomialRing<C> rf = (RecSolvablePolynomialRing<C>) rfac;
            dfac = RecSolvablePolynomialRing.<C>distribute(rf);
        } else {
            GenSolvablePolynomialRing<C> df = (GenSolvablePolynomialRing) rfac;
            dfac = df.distribute();
        }
        GenSolvablePolynomial<C> Pd = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(dfac, P);
        GenSolvablePolynomial<C> Sd = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(dfac, S);
        GenSolvablePolynomial<C> Dd = rightGcd(Pd, Sd);
        // convert to recursive
        GenSolvablePolynomial<GenPolynomial<C>> C = (GenSolvablePolynomial<GenPolynomial<C>>) PolyUtil
                .<C>recursive(rfac, Dd);
        return C;
    }


    /**
     * Univariate GenSolvablePolynomial recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     * deg_main(p) = deg_main(s) == 0.
     */
    public abstract GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S);


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     * deg_main(p) = deg_main(s) == 0.
     */
    public abstract GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S);


    /**
     * GenSolvablePolynomial right content.
     *
     * @param P GenSolvablePolynomial.
     * @return cont(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> rightContent(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            // baseContent not possible by return type
            throw new IllegalArgumentException("use baseContent() for univariate polynomials");
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac // = (RecSolvablePolynomialRing<C>) 
                = pfac.recursive(1);

        GenSolvablePolynomial<GenPolynomial<C>> Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac,
                P);
        GenSolvablePolynomial<C> D = rightRecursiveContent(Pr);
        return D;
    }


    /**
     * GenSolvablePolynomial right primitive part.
     *
     * @param P GenSolvablePolynomial.
     * @return pp(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> rightPrimitivePart(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return rightBasePrimitivePart(P);
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = /*(RecSolvablePolynomialRing<C>)*/pfac
                .recursive(1);

        GenSolvablePolynomial<GenPolynomial<C>> Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac,
                P);
        GenSolvablePolynomial<GenPolynomial<C>> PP = rightRecursivePrimitivePart(Pr);

        GenSolvablePolynomial<C> D = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(pfac, PP);
        return D;
    }


    /**
     * GenSolvablePolynomial left content.
     *
     * @param P GenSolvablePolynomial.
     * @return cont(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> leftContent(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            // baseContent not possible by return type
            throw new IllegalArgumentException("use baseContent() for univariate polynomials");
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac // = (RecSolvablePolynomialRing<C>) 
                = pfac.recursive(1);

        GenSolvablePolynomial<GenPolynomial<C>> Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac,
                P);
        GenSolvablePolynomial<C> D = leftRecursiveContent(Pr);
        return D;
    }


    /**
     * GenSolvablePolynomial left primitive part.
     *
     * @param P GenSolvablePolynomial.
     * @return pp(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> leftPrimitivePart(GenSolvablePolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P == null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return leftBasePrimitivePart(P);
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = /*(RecSolvablePolynomialRing<C>)*/pfac
                .recursive(1);

        GenSolvablePolynomial<GenPolynomial<C>> Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac,
                P);
        GenSolvablePolynomial<GenPolynomial<C>> PP = leftRecursivePrimitivePart(Pr);

        GenSolvablePolynomial<C> D = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(pfac, PP);
        return D;
    }


    /**
     * GenSolvablePolynomial division. Indirection to GenSolvablePolynomial
     * method.
     *
     * @param a GenSolvablePolynomial.
     * @param b coefficient.
     * @return a' = a/b with a = a'*b.
     */
    public GenSolvablePolynomial<C> divide(GenSolvablePolynomial<C> a, C b) {
        if (b == null || b.isZERO()) {
            throw new IllegalArgumentException("division by zero");

        }
        if (a == null || a.isZERO()) {
            return a;
        }
        return (GenSolvablePolynomial<C>) a.divide(b);
    }


    /*
     * GenSolvablePolynomial right division. Indirection to GenSolvablePolynomial
     * method.
     * @param a GenSolvablePolynomial.
     * @param b coefficient.
     * @return a' = a/b with a = b*a'.
    public GenSolvablePolynomial<C> rightDivide(GenSolvablePolynomial<C> a, C b) {
        if (b == null || b.isZERO()) {
            throw new IllegalArgumentException("division by zero");
    
        }
        if (a == null || a.isZERO()) {
            return a;
        }
        return (GenSolvablePolynomial<C>) a.rightDivide(b);
    }
     */


    /**
     * Coefficient greatest common divisor. Indirection to coefficient method.
     *
     * @param a coefficient.
     * @param b coefficient.
     * @return gcd(a, b) with a = a'*gcd(a,b) and b = b'*gcd(a,b).
     */
    public C gcd(C a, C b) {
        if (b == null || b.isZERO()) {
            return a;
        }
        if (a == null || a.isZERO()) {
            return b;
        }
        return a.gcd(b);
    }


    /**
     * GenSolvablePolynomial greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     * deg_main(p) = deg_main(s) == 0.
     */
    public GenSolvablePolynomial<C> leftGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.isONE()) {
            return P;
        }
        if (S.isONE()) {
            return S;
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenSolvablePolynomial<C> T = leftBaseGcd(P, S);
            return T;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1); //RecSolvablePolynomialRing<C>
        GenSolvablePolynomial<GenPolynomial<C>> Pr, Sr, Dr;
        Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac, P);
        Sr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac, S);
        Dr = leftRecursiveUnivariateGcd(Pr, Sr);
        GenSolvablePolynomial<C> D = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(pfac, Dr);
        if (debug) { // not checkable
            GenSolvablePolynomial<C> ps = FDUtil.<C>rightBaseSparsePseudoRemainder(P, D);
            GenSolvablePolynomial<C> ss = FDUtil.<C>rightBaseSparsePseudoRemainder(S, D);
            if (!ps.isZERO() || !ss.isZERO()) {
                System.out.println("fullGcd, D  = " + D);
                System.out.println("fullGcd, P  = " + P);
                System.out.println("fullGcd, S  = " + S);
                System.out.println("fullGcd, ps = " + ps);
                System.out.println("fullGcd, ss = " + ss);
                throw new RuntimeException("fullGcd: not divisible");
            }
            logger.info("fullGcd(P,S) okay: D = " + D + ", P = " + P + ", S = " + S);
        }
        return D;
    }


    /**
     * GenSolvablePolynomial left least common multiple.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return lcm(P, S) with lcm(P,S) = P'*P = S'*S.
     */
    public GenSolvablePolynomial<C> leftLcm(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        GenSolvablePolynomial<C>[] oc = leftOreCond(P, S);
        return oc[0].multiply(P);
    }


    /**
     * GenSolvablePolynomial right greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P, S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     * deg_main(p) = deg_main(s) == 0.
     */
    public GenSolvablePolynomial<C> rightGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.isONE()) {
            return P;
        }
        if (S.isONE()) {
            return S;
        }
        GenSolvablePolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenSolvablePolynomial<C> T = rightBaseGcd(P, S);
            return T;
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1); //RecSolvablePolynomialRing<C>
        GenSolvablePolynomial<GenPolynomial<C>> Pr, Sr, Dr;
        Pr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac, P);
        Sr = (RecSolvablePolynomial<C>) PolyUtil.<C>recursive(rfac, S);
        Dr = rightRecursiveUnivariateGcd(Pr, Sr);
        GenSolvablePolynomial<C> D = (GenSolvablePolynomial<C>) PolyUtil.<C>distribute(pfac, Dr);
        if (debug) { // not checkable
            GenSolvablePolynomial<C> ps = FDUtil.<C>leftBaseSparsePseudoRemainder(P, D);
            GenSolvablePolynomial<C> ss = FDUtil.<C>leftBaseSparsePseudoRemainder(S, D);
            if (!ps.isZERO() || !ss.isZERO()) {
                System.out.println("RI-fullGcd, D  = " + D);
                System.out.println("RI-fullGcd, P  = " + P);
                System.out.println("RI-fullGcd, S  = " + S);
                System.out.println("RI-fullGcd, ps = " + ps);
                System.out.println("RI-fullGcd, ss = " + ss);
                throw new RuntimeException("RI-fullGcd: not divisible");
            }
            logger.info("RI-fullGcd(P,S) okay: D = " + D);
        }
        return D;
    }


    /**
     * GenSolvablePolynomial right least common multiple.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return lcm(P, S) with lcm(P,S) = P*P' = S*S'.
     */
    public GenSolvablePolynomial<C> rightLcm(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        GenSolvablePolynomial<C>[] oc = rightOreCond(P, S);
        return P.multiply(oc[0]);
    }


    /**
     * List of GenSolvablePolynomials left greatest common divisor.
     *
     * @param A non empty list of GenSolvablePolynomials.
     * @return gcd(A_i) with A_i = A'_i*gcd(A_i)*a_i, where deg_main(a_i) == 0.
     */
    public GenSolvablePolynomial<C> leftGcd(List<GenSolvablePolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            throw new IllegalArgumentException("A may not be empty");
        }
        GenSolvablePolynomial<C> g = A.get(0);
        for (int i = 1; i < A.size(); i++) {
            GenSolvablePolynomial<C> f = A.get(i);
            g = leftGcd(g, f);
        }
        return g;
    }


    /**
     * GenSolvablePolynomial co-prime list.
     *
     * @param A list of GenSolvablePolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     * a in A there exists b in B with b|a. B does not contain zero or
     * constant polynomials.
     */
    public List<GenSolvablePolynomial<C>> leftCoPrime(List<GenSolvablePolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenSolvablePolynomial<C>> B = new ArrayList<GenSolvablePolynomial<C>>(A.size());
        // make a coprime to rest of list
        GenSolvablePolynomial<C> a = A.get(0);
        //System.out.println("a = " + a);
        if (!a.isZERO() && !a.isConstant()) {
            for (int i = 1; i < A.size(); i++) {
                GenSolvablePolynomial<C> b = A.get(i);
                GenSolvablePolynomial<C> g = leftGcd(a, b);
                if (!g.isONE()) {
                    a = FDUtil.<C>leftBasePseudoQuotient(a, g);
                    b = FDUtil.<C>leftBasePseudoQuotient(b, g);
                    GenSolvablePolynomial<C> gp = leftGcd(a, g); //.abs();
                    while (!gp.isONE()) {
                        a = FDUtil.<C>leftBasePseudoQuotient(a, gp);
                        g = FDUtil.<C>leftBasePseudoQuotient(g, gp);
                        B.add(g); // gcd(a,g) == 1
                        g = gp;
                        gp = leftGcd(a, gp);
                    }
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1
                    }
                }
                if (!b.isZERO() && !b.isConstant()) {
                    B.add(b); // gcd(a,b) == 1
                }
            }
        } else {
            B.addAll(A.subList(1, A.size()));
        }
        // make rest coprime
        B = leftCoPrime(B);
        //System.out.println("B = " + B);
        if (!a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/) {
            a = (GenSolvablePolynomial<C>) a.abs();
            B.add(a);
        }
        return B;
    }


    /**
     * GenSolvablePolynomial left co-prime list.
     *
     * @param A list of GenSolvablePolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     * a in A there exists b in B with b|a. B does not contain zero or
     * constant polynomials.
     */
    public List<GenSolvablePolynomial<C>> leftCoPrimeRec(List<GenSolvablePolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenSolvablePolynomial<C>> B = new ArrayList<GenSolvablePolynomial<C>>();
        // make a co-prime to rest of list
        for (GenSolvablePolynomial<C> a : A) {
            //System.out.println("a = " + a);
            B = leftCoPrime(a, B);
            //System.out.println("B = " + B);
        }
        return B;
    }


    /**
     * GenSolvablePolynomial left co-prime list.
     *
     * @param a GenSolvablePolynomial.
     * @param P co-prime list of GenSolvablePolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for non-constant a
     * there exists b in P with b|a. B does not contain zero or constant
     * polynomials.
     */
    public List<GenSolvablePolynomial<C>> leftCoPrime(GenSolvablePolynomial<C> a,
                                                      List<GenSolvablePolynomial<C>> P) {
        if (a == null || a.isZERO() || a.isConstant()) {
            return P;
        }
        List<GenSolvablePolynomial<C>> B = new ArrayList<GenSolvablePolynomial<C>>(P.size() + 1);
        // make a coprime to elements of the list P
        for (int i = 0; i < P.size(); i++) {
            GenSolvablePolynomial<C> b = P.get(i);
            GenSolvablePolynomial<C> g = leftGcd(a, b);
            if (!g.isONE()) {
                a = FDUtil.<C>leftBasePseudoQuotient(a, g);
                b = FDUtil.<C>leftBasePseudoQuotient(b, g);
                // make g co-prime to new a, g is co-prime to c != b in P, B
                GenSolvablePolynomial<C> gp = leftGcd(a, g);
                while (!gp.isONE()) {
                    a = FDUtil.<C>leftBasePseudoQuotient(a, gp);
                    g = FDUtil.<C>leftBasePseudoQuotient(g, gp);
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = leftGcd(a, gp);
                }
                // make new g co-prime to new b
                gp = leftGcd(b, g);
                while (!gp.isONE()) {
                    b = FDUtil.<C>leftBasePseudoQuotient(b, gp);
                    g = FDUtil.<C>leftBasePseudoQuotient(g, gp);
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = leftGcd(b, gp);
                }
                if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                    B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                }
            }
            if (!b.isZERO() && !b.isConstant() /*&& !B.contains(b)*/) {
                B.add(b); // gcd(a,b) == 1 and gcd(b,c) == 1 for c != b in P, B
            }
        }
        if (!a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/) {
            B.add(a);
        }
        return B;
    }


    /**
     * GenSolvablePolynomial test for co-prime list.
     *
     * @param A list of GenSolvablePolynomials.
     * @return true if gcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isLeftCoPrime(List<GenSolvablePolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return true;
        }
        if (A.size() == 1) {
            return true;
        }
        for (int i = 0; i < A.size(); i++) {
            GenSolvablePolynomial<C> a = A.get(i);
            for (int j = i + 1; j < A.size(); j++) {
                GenSolvablePolynomial<C> b = A.get(j);
                GenSolvablePolynomial<C> g = leftGcd(a, b);
                if (!g.isONE()) {
                    System.out.println("not co-prime, a: " + a);
                    System.out.println("not co-prime, b: " + b);
                    System.out.println("not co-prime, g: " + g);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * GenSolvablePolynomial test for left co-prime list of given list.
     *
     * @param A list of GenSolvablePolynomials.
     * @param P list of co-prime GenSolvablePolynomials.
     * @return true if isCoPrime(P) and for all a in A exists p in P with p | a,
     * else false.
     */
    public boolean isLeftCoPrime(List<GenSolvablePolynomial<C>> P, List<GenSolvablePolynomial<C>> A) {
        if (!isLeftCoPrime(P)) {
            return false;
        }
        if (A == null || A.isEmpty()) {
            return true;
        }
        for (GenSolvablePolynomial<C> q : A) {
            if (q.isZERO() || q.isConstant()) {
                continue;
            }
            boolean divides = false;
            for (GenSolvablePolynomial<C> p : P) {
                GenSolvablePolynomial<C> a = FDUtil.<C>leftBaseSparsePseudoRemainder(q, p);
                if (a.isZERO()) { // p divides q
                    divides = true;
                    break;
                }
            }
            if (!divides) {
                System.out.println("no divisor for: " + q);
                return false;
            }
        }
        return true;
    }


    /**
     * Univariate GenSolvablePolynomial extended greatest common divisor. Uses
     * sparse pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return [ gcd(P,S), a, b ] with a*P + b*S = gcd(P,S).
     */
    @SuppressWarnings({"unchecked", "cast"})
    public GenSolvablePolynomial<C>[] baseExtendedGcd(GenSolvablePolynomial<C> P,
                                                      GenSolvablePolynomial<C> S) {
        //return P.egcd(S);
        GenSolvablePolynomial<C>[] hegcd = baseHalfExtendedGcd(P, S);
        GenSolvablePolynomial<C>[] ret = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[3];
        ret[0] = hegcd[0];
        ret[1] = hegcd[1];
        GenSolvablePolynomial<C> x = (GenSolvablePolynomial<C>) hegcd[0].subtract(hegcd[1].multiply(P));
        GenSolvablePolynomial<C>[] qr = FDUtil.<C>leftBasePseudoQuotientRemainder(x, S);
        // assert qr[1].isZERO() 
        ret[2] = qr[0];
        return ret;
    }


    /**
     * Univariate GenSolvablePolynomial half extended greatest comon divisor.
     * Uses sparse pseudoRemainder for remainder.
     *
     * @param S GenSolvablePolynomial.
     * @return [ gcd(P,S), a ] with a*P + b*S = gcd(P,S).
     */
    @SuppressWarnings({"unchecked", "cast"})
    public GenSolvablePolynomial<C>[] baseHalfExtendedGcd(GenSolvablePolynomial<C> P,
                                                          GenSolvablePolynomial<C> S) {
        if (P == null || S == null) {
            throw new IllegalArgumentException("null P or S not allowed");
        }
        GenSolvablePolynomial<C>[] ret = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if (S.isZERO()) {
            ret[0] = P;
            ret[1] = P.ring.getONE();
            return ret;
        }
        if (P.isZERO()) {
            ret[0] = S;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if (P.ring.nvar != 1) {
            throw new IllegalArgumentException("for univariate polynomials only " + P.ring);
        }
        GenSolvablePolynomial<C> q = P;
        GenSolvablePolynomial<C> r = S;
        GenSolvablePolynomial<C> c1 = P.ring.getONE().copy();
        GenSolvablePolynomial<C> d1 = P.ring.getZERO().copy();
        while (!r.isZERO()) {
            GenSolvablePolynomial<C>[] qr = FDUtil.<C>leftBasePseudoQuotientRemainder(q, r);
            //q.divideAndRemainder(r);
            q = qr[0];
            GenSolvablePolynomial<C> x = (GenSolvablePolynomial<C>) c1.subtract(q.multiply(d1));
            c1 = d1;
            d1 = x;
            q = r;
            r = qr[1];
        }
        // normalize ldcf(q) to 1, i.e. make monic
        C g = q.leadingBaseCoefficient();
        if (g.isUnit()) {
            C h = g.inverse();
            q = q.multiply(h);
            c1 = c1.multiply(h);
        }
        //assert ( ((c1.multiply(P)).remainder(S).equals(q) )); 
        ret[0] = q;
        ret[1] = c1;
        return ret;
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor diophantine
     * version.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @param c univariate GenSolvablePolynomial.
     * @return [ a, b ] with a*P + b*S = c and deg(a) &lt; deg(S).
     */
    @SuppressWarnings({"unchecked", "cast"})
    public GenSolvablePolynomial<C>[] baseGcdDiophant(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S,
                                                      GenSolvablePolynomial<C> c) {
        GenSolvablePolynomial<C>[] egcd = baseExtendedGcd(P, S);
        GenSolvablePolynomial<C> g = egcd[0];
        GenSolvablePolynomial<C>[] qr = FDUtil.<C>leftBasePseudoQuotientRemainder(c, g);
        if (!qr[1].isZERO()) {
            throw new ArithmeticException("not solvable, r = " + qr[1] + ", c = " + c + ", g = " + g);
        }
        GenSolvablePolynomial<C> q = qr[0];
        GenSolvablePolynomial<C> a = egcd[1].multiply(q);
        GenSolvablePolynomial<C> b = egcd[2].multiply(q);
        if (!a.isZERO() && a.degree(0) >= S.degree(0)) {
            qr = FDUtil.<C>leftBasePseudoQuotientRemainder(a, S);
            a = qr[1];
            b = (GenSolvablePolynomial<C>) b.sum(P.multiply(qr[0]));
        }
        GenSolvablePolynomial<C>[] ret = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        ret[0] = a;
        ret[1] = b;
        if (debug) {
            GenSolvablePolynomial<C> y = (GenSolvablePolynomial<C>) ret[0].multiply(P)
                    .sum(ret[1].multiply(S));
            if (!y.equals(c)) {
                System.out.println("P  = " + P);
                System.out.println("S  = " + S);
                System.out.println("c  = " + c);
                System.out.println("a  = " + a);
                System.out.println("b  = " + b);
                System.out.println("y  = " + y);
                throw new ArithmeticException("not diophant, x = " + y.subtract(c));
            }
        }
        return ret;
    }


    /**
     * Coefficient left Ore condition. Generators for the left Ore condition of
     * two coefficients.
     *
     * @param a coefficient.
     * @param b coefficient.
     * @return [oa, ob] = leftOreCond(a,b), with oa*a == ob*b.
     */
    @SuppressWarnings("unchecked")
    public C[] leftOreCond(C a, C b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        C[] oc = (C[]) new GcdRingElem[2];
        if (a instanceof GenSolvablePolynomial && b instanceof GenSolvablePolynomial) {
            GenSolvablePolynomial ap = (GenSolvablePolynomial) a;
            GenSolvablePolynomial bp = (GenSolvablePolynomial) b;
            GenSolvablePolynomial[] ocp = leftOreCond(ap, bp);
            oc[0] = (C) ocp[0];
            oc[1] = (C) ocp[1];
            return oc;
        }
        RingFactory<C> rf = coFac; // not usable: (RingFactory<C>) a.factory();
        if (a.equals(b)) { // required because of rationals gcd
            oc[0] = rf.getONE();
            oc[1] = rf.getONE();
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        if (a.equals(b.negate())) { // required because of rationals gcd
            oc[0] = rf.getONE();
            oc[1] = rf.getONE().negate();
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        if (rf.isCommutative()) {
            if (debug) {
                logger.info("left Ore condition on coefficients, commutative case: " + a + ", " + b);
            }
            C gcd = a.gcd(b);
            if (gcd.isONE()) {
                oc[0] = b;
                oc[1] = a;
                if (oc[0].compareTo(rf.getZERO()) < 0 && oc[1].compareTo(rf.getZERO()) < 0) {
                    oc[0] = oc[0].negate();
                    oc[1] = oc[1].negate();
                }
                logger.info("Ore multiple: " + Arrays.toString(oc));
                return oc;
            }
            C p = a.multiply(b);
            C lcm = p.divide(gcd).abs();
            oc[0] = lcm.divide(a);
            oc[1] = lcm.divide(b);
            if (oc[0].compareTo(rf.getZERO()) < 0 && oc[1].compareTo(rf.getZERO()) < 0) {
                oc[0] = oc[0].negate();
                oc[1] = oc[1].negate();
            }
            logger.info("Ore multiple: lcm=" + lcm + ", gcd=" + gcd + ", " + Arrays.toString(oc));
            return oc;
        }
        // now non-commutative
        if (rf.isField()) {
            logger.info("left Ore condition on coefficients, skew field " + rf + " case: " + a + ", " + b);
            //C gcd = a.gcd(b); // always one 
            //C lcm = rf.getONE();
            oc[0] = a.inverse(); //lcm.divide(a);
            oc[1] = b.inverse(); //lcm.divide(b);
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        if (b instanceof StarRingElem) {
            logger.info("left Ore condition on coefficients, StarRing case: " + a + ", " + b);
            C bs = (C) ((StarRingElem) b).conjugate();
            oc[0] = bs.multiply(b); // bar(b) b a = s a 
            oc[1] = a.multiply(bs); // a bar(b) b = a s
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        throw new UnsupportedOperationException(
                "leftOreCond not implemented for " + rf.getClass() + ", rf = " + rf.toScript());
        //return oc;
    }


    /**
     * Left Ore condition. Generators for the left Ore condition of two solvable
     * polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with p*a = q*b
     */
    @SuppressWarnings({"unchecked", "cast"})
    public GenSolvablePolynomial<C>[] leftOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        GenSolvablePolynomialRing<C> pfac = a.ring;
        GenSolvablePolynomial<C>[] oc = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        if (a.equals(b)) {
            oc[0] = pfac.getONE();
            oc[1] = pfac.getONE();
            return oc;
        }
        if (a.equals(b.negate())) {
            oc[0] = pfac.getONE();
            oc[1] = (GenSolvablePolynomial<C>) pfac.getONE().negate();
            return oc;
        }
        if (pfac.isCommutative()) {
            logger.info("left Ore condition, polynomial commutative case: " + a + ", " + b);
            edu.jas.ufd.GreatestCommonDivisorAbstract<C> cgcd = GCDFactory.<C>getImplementation(pfac.coFac);
            GenSolvablePolynomial<C> lcm = (GenSolvablePolynomial<C>) cgcd.lcm(a, b);
            //oc[0] = FDUtil.<C> basePseudoQuotient(lcm, a);
            //oc[1] = FDUtil.<C> basePseudoQuotient(lcm, b);
            oc[0] = (GenSolvablePolynomial<C>) PolyUtil.<C>basePseudoDivide(lcm, a);
            oc[1] = (GenSolvablePolynomial<C>) PolyUtil.<C>basePseudoDivide(lcm, b);
            logger.info("Ore multiple: " + lcm + ", " + Arrays.toString(oc));
            return oc;
        }
        oc = syz.leftOreCond(a, b);
        //logger.info("Ore multiple: " + oc[0].multiply(a) + ", " + Arrays.toString(oc));
        return oc;
    }


    /**
     * Coefficient rigth Ore condition. Generators for the right Ore condition
     * of two coefficients.
     *
     * @param a coefficient.
     * @param b coefficient.
     * @return [oa, ob] = rightOreCond(a,b), with a*oa == b*ob.
     */
    @SuppressWarnings("unchecked")
    public C[] rightOreCond(C a, C b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        C[] oc = (C[]) new GcdRingElem[2];
        if (a instanceof GenSolvablePolynomial && b instanceof GenSolvablePolynomial) {
            GenSolvablePolynomial ap = (GenSolvablePolynomial) a;
            GenSolvablePolynomial bp = (GenSolvablePolynomial) b;
            GenSolvablePolynomial[] ocp = rightOreCond(ap, bp);
            oc[0] = (C) ocp[0];
            oc[1] = (C) ocp[1];
            return oc;
        }
        RingFactory<C> rf = coFac; // not usable: (RingFactory<C>) a.factory();
        if (a.equals(b)) { // required because of rationals gcd
            oc[0] = rf.getONE();
            oc[1] = rf.getONE();
            return oc;
        }
        if (a.equals(b.negate())) { // required because of rationals gcd
            oc[0] = rf.getONE();
            oc[1] = rf.getONE().negate();
            return oc;
        }
        if (rf.isCommutative()) {
            logger.info("right Ore condition on coefficients, commutative case: " + a + ", " + b);
            C gcd = a.gcd(b);
            if (gcd.isONE()) {
                oc[0] = b;
                oc[1] = a;
                if (oc[0].compareTo(rf.getZERO()) < 0 && oc[1].compareTo(rf.getZERO()) < 0) {
                    oc[0] = oc[0].negate();
                    oc[1] = oc[1].negate();
                }
                return oc;
            }
            C p = a.multiply(b);
            C lcm = p.divide(gcd).abs();
            oc[0] = lcm.divide(a);
            oc[1] = lcm.divide(b);
            if (oc[0].compareTo(rf.getZERO()) < 0 && oc[1].compareTo(rf.getZERO()) < 0) {
                oc[0] = oc[0].negate();
                oc[1] = oc[1].negate();
            }
            logger.info("Ore multiple: " + lcm + ", " + Arrays.toString(oc));
            return oc;
        }
        // now non-commutative
        if (rf.isField()) {
            logger.info("right Ore condition on coefficients, skew field " + rf + " case: " + a + ", " + b);
            //C gcd = a.gcd(b); // always one 
            //C lcm = rf.getONE();
            oc[0] = a.inverse(); //lcm.divide(a);
            oc[1] = b.inverse(); //lcm.divide(b);
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        if (b instanceof StarRingElem) {
            logger.info("right Ore condition on coefficients, StarRing case: " + a + ", " + b);
            C bs = (C) ((StarRingElem) b).conjugate();
            oc[0] = b.multiply(bs); // a b bar(b) = a s
            oc[1] = bs.multiply(a); // b bar(b) a = s a 
            logger.info("Ore multiple: " + Arrays.toString(oc));
            return oc;
        }
        throw new UnsupportedOperationException(
                "rightOreCond not implemented for " + rf.getClass() + ", rf = " + rf.toScript());
        //return oc;
    }


    /**
     * Right Ore condition. Generators for the right Ore condition of two
     * solvable polynomials.
     *
     * @param a solvable polynomial
     * @param b solvable polynomial
     * @return [p, q] with a*p = b*q
     */
    @SuppressWarnings({"unchecked", "cast"})
    public GenSolvablePolynomial<C>[] rightOreCond(GenSolvablePolynomial<C> a, GenSolvablePolynomial<C> b) {
        if (a == null || a.isZERO() || b == null || b.isZERO()) {
            throw new IllegalArgumentException("a and b must be non zero");
        }
        GenSolvablePolynomialRing<C> pfac = a.ring;
        GenSolvablePolynomial<C>[] oc = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[2];
        if (a.equals(b)) {
            oc[0] = pfac.getONE();
            oc[1] = pfac.getONE();
            return oc;
        }
        if (a.equals(b.negate())) {
            oc[0] = pfac.getONE();
            oc[1] = (GenSolvablePolynomial<C>) pfac.getONE().negate();
            return oc;
        }
        if (pfac.isCommutative()) {
            logger.info("right Ore condition, polynomial commutative case: " + a + ", " + b);
            edu.jas.ufd.GreatestCommonDivisorAbstract<C> cgcd = GCDFactory.<C>getImplementation(pfac.coFac);
            GenSolvablePolynomial<C> lcm = (GenSolvablePolynomial<C>) cgcd.lcm(a, b);
            //oc[0] = FDUtil.<C> basePseudoQuotient(lcm, a);
            //oc[1] = FDUtil.<C> basePseudoQuotient(lcm, b);
            oc[0] = (GenSolvablePolynomial<C>) PolyUtil.<C>basePseudoDivide(lcm, a);
            oc[1] = (GenSolvablePolynomial<C>) PolyUtil.<C>basePseudoDivide(lcm, b);
            logger.info("Ore multiple: " + lcm + ", " + Arrays.toString(oc));
            return oc;
        }
        oc = syz.rightOreCond(a, b);
        //logger.info("Ore multiple: " + oc[0].multiply(a) + ", " + Arrays.toString(oc));
        return oc;
    }

}
