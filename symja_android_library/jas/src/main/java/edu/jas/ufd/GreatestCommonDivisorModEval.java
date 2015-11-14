/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * Greatest common divisor algorithms with modular evaluation algorithm for
 * recursion.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorModEval <MOD extends GcdRingElem<MOD> & Modular> 
        extends GreatestCommonDivisorAbstract<MOD> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorModEval.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Modular gcd algorithm to use.
     */
    protected final GreatestCommonDivisorAbstract<MOD> mufd 
       = new GreatestCommonDivisorSimple<MOD>();
    // = new GreatestCommonDivisorPrimitive<MOD>();
    // not okay: = new GreatestCommonDivisorSubres<MOD>();


    /**
     * Univariate GenPolynomial greatest common divisor. 
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<MOD> baseGcd(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        // required as recursion base
        return mufd.baseGcd(P, S);
    }


    /**
     * Recursive univariate GenPolynomial greatest common divisor. 
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<MOD>> recursiveUnivariateGcd(
            GenPolynomial<GenPolynomial<MOD>> P, GenPolynomial<GenPolynomial<MOD>> S) {
        return mufd.recursiveUnivariateGcd(P, S);
    }


    /**
     * GenPolynomial greatest common divisor, modular evaluation algorithm.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<MOD> gcd(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenPolynomialRing<MOD> fac = P.ring;
        // recusion base case for univariate polynomials
        if (fac.nvar <= 1) {
            GenPolynomial<MOD> T = baseGcd(P, S);
            return T;
        }
        long e = P.degree(fac.nvar-1);
        long f = S.degree(fac.nvar-1);
        if ( e == 0 && f == 0 ) {
            GenPolynomialRing<GenPolynomial<MOD>> rfac = fac.recursive(1);
            GenPolynomial<MOD> Pc = PolyUtil.<MOD> recursive(rfac, P).leadingBaseCoefficient();
            GenPolynomial<MOD> Sc = PolyUtil.<MOD> recursive(rfac, S).leadingBaseCoefficient();
            GenPolynomial<MOD> r = gcd(Pc,Sc);
            return r.extend(fac,0,0L);
        }
        GenPolynomial<MOD> q;
        GenPolynomial<MOD> r;
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
        if (debug) {
            logger.debug("degrees: e = " + e + ", f = " + f);
        }
        r = r.abs();
        q = q.abs();
        // setup factories
        ModularRingFactory<MOD> cofac = (ModularRingFactory<MOD>) P.ring.coFac;
        if (!cofac.isField()) {
            logger.warn("cofac is not a field: " + cofac);
        }
        GenPolynomialRing<GenPolynomial<MOD>> rfac = fac.recursive(fac.nvar - 1);
        GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(cofac, rfac);
        GenPolynomialRing<MOD> ufac = (GenPolynomialRing<MOD>) rfac.coFac;
        //GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(cofac, fac.nvar - 1, fac.tord);
        //GenPolynomialRing<MOD> ufac = new GenPolynomialRing<MOD>(cofac, 1, fac.tord);
        //GenPolynomialRing<GenPolynomial<MOD>> rfac = new GenPolynomialRing<GenPolynomial<MOD>>(ufac, fac.nvar - 1, fac.tord);
        // convert polynomials
        GenPolynomial<GenPolynomial<MOD>> qr;
        GenPolynomial<GenPolynomial<MOD>> rr;
        qr = PolyUtil.<MOD> recursive(rfac, q);
        rr = PolyUtil.<MOD> recursive(rfac, r);

        // compute univariate contents and primitive parts
        GenPolynomial<MOD> a = recursiveContent(rr);
        GenPolynomial<MOD> b = recursiveContent(qr);
        // gcd of univariate coefficient contents
        GenPolynomial<MOD> c = gcd(a, b);
        rr = PolyUtil.<MOD> recursiveDivide(rr, a);
        qr = PolyUtil.<MOD> recursiveDivide(qr, b);
        if (rr.isONE()) {
            rr = rr.multiply(c);
            r = PolyUtil.<MOD> distribute(fac, rr);
            return r;
        }
        if (qr.isONE()) {
            qr = qr.multiply(c);
            q = PolyUtil.<MOD> distribute(fac, qr);
            return q;
        }
        // compute normalization factor
        GenPolynomial<MOD> ac = rr.leadingBaseCoefficient();
        GenPolynomial<MOD> bc = qr.leadingBaseCoefficient();
        GenPolynomial<MOD> cc = gcd(ac, bc);
        // compute degrees and degree vectors
        ExpVector rdegv = rr.degreeVector();
        ExpVector qdegv = qr.degreeVector();
        long rd0 = PolyUtil.<MOD> coeffMaxDegree(rr);
        long qd0 = PolyUtil.<MOD> coeffMaxDegree(qr);
        long cd0 = cc.degree(0);
        long G = (rd0 >= qd0 ? rd0 : qd0) + cd0;

        // initialize element and degree vector
        ExpVector wdegv = rdegv.subst(0, rdegv.getVal(0) + 1);
        // +1 seems to be a hack for the unlucky prime test
        MOD inc = cofac.getONE();
        long i = 0;
        long en = cofac.getIntegerModul().longValue() - 1; // just a stopper
        MOD end = cofac.fromInteger(en);
        MOD mi;
        GenPolynomial<MOD> M = null;
        GenPolynomial<MOD> mn;
        GenPolynomial<MOD> qm;
        GenPolynomial<MOD> rm;
        GenPolynomial<MOD> cm;
        GenPolynomial<GenPolynomial<MOD>> cp = null;
        if (debug) {
            logger.debug("c = " + c);
            logger.debug("cc = " + cc);
            logger.debug("G = " + G);
            logger.info("wdegv = " + wdegv);
        }
        for (MOD d = cofac.getZERO(); d.compareTo(end) <= 0; d = d.sum(inc)) {
            if (++i >= en) {
                logger.warn("elements of Z_p exhausted, en = " + en);
                return mufd.gcd(P, S);
                //throw new ArithmeticException("prime list exhausted");
            }
            // map normalization factor
            MOD nf = PolyUtil.<MOD> evaluateMain(cofac, cc, d);
            if (nf.isZERO()) {
                continue;
            }
            // map polynomials
            qm = PolyUtil.<MOD> evaluateFirstRec(ufac, mfac, qr, d);
            if (qm.isZERO() || !qm.degreeVector().equals(qdegv)) {
                continue;
            }
            rm = PolyUtil.<MOD> evaluateFirstRec(ufac, mfac, rr, d);
            if (rm.isZERO() || !rm.degreeVector().equals(rdegv)) {
                continue;
            }
            if (debug) {
                logger.debug("eval d = " + d);
            }
            // compute modular gcd in recursion
            cm = gcd(rm, qm);
            //System.out.println("cm = " + cm);
            // test for constant g.c.d
            if (cm.isConstant()) {
                logger.debug("cm.isConstant = " + cm + ", c = " + c);
                if (c.ring.nvar < cm.ring.nvar) {
                    c = c.extend(mfac, 0, 0);
                }
                cm = cm.abs().multiply(c);
                q = cm.extend(fac, 0, 0);
                logger.debug("q             = " + q + ", c = " + c);
                return q;
            }
            // test for unlucky prime
            ExpVector mdegv = cm.degreeVector();
            if (wdegv.equals(mdegv)) { // TL = 0
                // prime ok, next round
                if (M != null) {
                    if (M.degree(0) > G) {
                        logger.info("deg(M) > G: " + M.degree(0) + " > " + G);
                        // continue; // why should this be required?
                    }
                }
            } else { // TL = 3
                boolean ok = false;
                if (wdegv.multipleOf(mdegv)) { // TL = 2
                    M = null; // init chinese remainder
                    ok = true; // prime ok
                }
                if (mdegv.multipleOf(wdegv)) { // TL = 1
                    continue; // skip this prime
                }
                if (!ok) {
                    M = null; // discard chinese remainder and previous work
                    continue; // prime not ok
                }
            }
            // prepare interpolation algorithm
            cm = cm.multiply(nf);
            if (M == null) {
                // initialize interpolation
                M = ufac.getONE();
                cp = rfac.getZERO();
                wdegv = wdegv.gcd(mdegv); //EVGCD(wdegv,mdegv);
            }
            // interpolate
            mi = PolyUtil.<MOD> evaluateMain(cofac, M, d);
            mi = mi.inverse(); // mod p
            cp = PolyUtil.interpolate(rfac, cp, M, mi, cm, d);
            mn = ufac.getONE().multiply(d);
            mn = ufac.univariate(0).subtract(mn);
            M = M.multiply(mn);
            // test for completion
            if (M.degree(0) > G) {
                break;
            }
            //long cmn = PolyUtil.<MOD>coeffMaxDegree(cp);
            //if ( M.degree(0) > cmn ) {
            // does not work: only if cofactors are also considered?
            // break;
            //}
        }
        // remove normalization
        cp = recursivePrimitivePart(cp).abs();
        cp = cp.multiply(c);
        q = PolyUtil.<MOD> distribute(fac, cp);
        return q;
    }


    /**
     * Univariate GenPolynomial resultant. 
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<MOD> baseResultant(GenPolynomial<MOD> P, GenPolynomial<MOD> S) { 
        // required as recursion base
        return mufd.baseResultant(P, S);
    }


    /**
     * Univariate GenPolynomial recursive resultant. 
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<MOD>> recursiveUnivariateResultant(GenPolynomial<GenPolynomial<MOD>> P,
            GenPolynomial<GenPolynomial<MOD>> S) { 
        // only in this class
        return recursiveResultant(P,S);
    }


    /**
     * GenPolynomial resultant, modular evaluation algorithm.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<MOD> resultant(GenPolynomial<MOD> P, GenPolynomial<MOD> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<MOD> fac = P.ring;
        // recusion base case for univariate polynomials
        if (fac.nvar <= 1) {
            GenPolynomial<MOD> T = mufd.baseResultant(P, S);
            return T;
        }
        long e = P.degree(fac.nvar-1);
        long f = S.degree(fac.nvar-1);
        if ( e == 0 && f == 0 ) {
            GenPolynomialRing<GenPolynomial<MOD>> rfac = fac.recursive(1);
            GenPolynomial<MOD> Pc = PolyUtil.<MOD> recursive(rfac, P).leadingBaseCoefficient();
            GenPolynomial<MOD> Sc = PolyUtil.<MOD> recursive(rfac, S).leadingBaseCoefficient();
            GenPolynomial<MOD> r = resultant(Pc,Sc);
            return r.extend(fac,0,0L);
        }
        GenPolynomial<MOD> q;
        GenPolynomial<MOD> r;
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
        if (debug) {
            logger.debug("degrees: e = " + e + ", f = " + f);
        }
        // setup factories
        ModularRingFactory<MOD> cofac = (ModularRingFactory<MOD>) P.ring.coFac;
        if (!cofac.isField()) {
            logger.warn("cofac is not a field: " + cofac);
        }
        GenPolynomialRing<GenPolynomial<MOD>> rfac = fac.recursive(fac.nvar - 1);
        GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(cofac, rfac);
        GenPolynomialRing<MOD> ufac = (GenPolynomialRing<MOD>) rfac.coFac;

        // convert polynomials
        GenPolynomial<GenPolynomial<MOD>> qr = PolyUtil.<MOD> recursive(rfac, q);
        GenPolynomial<GenPolynomial<MOD>> rr = PolyUtil.<MOD> recursive(rfac, r);

        // compute degrees and degree vectors
        ExpVector qdegv = qr.degreeVector();
        ExpVector rdegv = rr.degreeVector();

        long qd0 = PolyUtil.<MOD> coeffMaxDegree(qr);
        long rd0 = PolyUtil.<MOD> coeffMaxDegree(rr);
        qd0 = ( qd0 == 0 ? 1 : qd0 );
        rd0 = ( rd0 == 0 ? 1 : rd0 );
        long qd1 = qr.degree(); 
        long rd1 = rr.degree();
        qd1 = ( qd1 == 0 ? 1 : qd1 );
        rd1 = ( rd1 == 0 ? 1 : rd1 );
        long G = qd0 * rd1 + rd0 * qd1 + 1;

        // initialize element
        MOD inc = cofac.getONE();
        long i = 0;
        long en = cofac.getIntegerModul().longValue() - 1; // just a stopper
        MOD end = cofac.fromInteger(en);
        MOD mi;
        GenPolynomial<MOD> M = null;
        GenPolynomial<MOD> mn;
        GenPolynomial<MOD> qm;
        GenPolynomial<MOD> rm;
        GenPolynomial<MOD> cm;
        GenPolynomial<GenPolynomial<MOD>> cp = null;
        if (debug) {
            //logger.info("qr    = " + qr + ", q = " + q);
            //logger.info("rr    = " + rr + ", r = " + r);
            //logger.info("qd0   = " + qd0);
            //logger.info("rd0   = " + rd0);
            logger.info("G     = " + G);
            //logger.info("rdegv = " + rdegv); // + ", rr.degree(0) = " + rr.degree(0));
            //logger.info("qdegv = " + qdegv); // + ", qr.degree(0) = " + qr.degree(0));
        }
        for (MOD d = cofac.getZERO(); d.compareTo(end) <= 0; d = d.sum(inc)) {
            if (++i >= en) {
                logger.warn("elements of Z_p exhausted, en = " + en + ", p = " + cofac.getIntegerModul());
                return mufd.resultant(P, S);
                //throw new ArithmeticException("prime list exhausted");
            }
            // map polynomials
            qm = PolyUtil.<MOD> evaluateFirstRec(ufac, mfac, qr, d);
            //logger.info("qr(" + d + ") = " + qm + ", qr = " + qr);
            if (qm.isZERO() || !qm.degreeVector().equals(qdegv)) {
                if (debug) {
                   logger.info("un-lucky evaluation point " + d + ", qm = " + qm.degreeVector() + " < " + qdegv);
                }
                continue;
            }
            rm = PolyUtil.<MOD> evaluateFirstRec(ufac, mfac, rr, d);
            //logger.info("rr(" + d + ") = " + rm + ", rr = " + rr);
            if (rm.isZERO() || !rm.degreeVector().equals(rdegv)) {
                if (debug) {
                    logger.info("un-lucky evaluation point " + d + ", rm = " + rm.degreeVector() + " < " + rdegv);
                }
                continue;
            }
            // compute modular resultant in recursion
            cm = resultant(rm, qm);
            //System.out.println("cm = " + cm);

            // prepare interpolation algorithm
            if (M == null) {
                // initialize interpolation
                M = ufac.getONE();
                cp = rfac.getZERO();
            }
            // interpolate
            mi = PolyUtil.<MOD> evaluateMain(cofac, M, d);
            mi = mi.inverse(); // mod p
            cp = PolyUtil.interpolate(rfac, cp, M, mi, cm, d);
            //logger.info("cp = " + cp);
            mn = ufac.getONE().multiply(d);
            mn = ufac.univariate(0).subtract(mn);
            M = M.multiply(mn);
            // test for completion
            if (M.degree(0) > G) {
                if (debug) {
                    logger.info("last lucky evaluation point " + d);
                }
                break;
            }
            //logger.info("M  = " + M);
        }
        // distribute
        q = PolyUtil.<MOD> distribute(fac, cp);
        return q;
    }

}
