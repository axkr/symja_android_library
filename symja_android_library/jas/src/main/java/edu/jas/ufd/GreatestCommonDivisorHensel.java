/*
 * $Id: GreatestCommonDivisorHensel.java 4067 2012-07-27 16:17:35Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Greatest common divisor algorithms with subresultant polynomial remainder
 * sequence and univariate Hensel lifting.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorHensel<MOD extends GcdRingElem<MOD> & Modular> extends
                GreatestCommonDivisorAbstract<BigInteger> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorHensel.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Flag for linear or quadratic Hensel lift.
     */
    public final boolean quadratic;


    /**
     * Fall back gcd algorithm.
     */
    public final GreatestCommonDivisorAbstract<BigInteger> iufd;


    /**
     * Constructor.
     */
    public GreatestCommonDivisorHensel() {
        this(true);
    }


    /**
     * Constructor.
     * @param quadratic use quadratic Hensel lift.
     */
    public GreatestCommonDivisorHensel(boolean quadratic) {
        this.quadratic = quadratic;
        iufd = new GreatestCommonDivisorSubres<BigInteger>();
    }


    /**
     * Univariate GenPolynomial greatest comon divisor. Uses univariate Hensel
     * lifting.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<BigInteger> baseGcd(GenPolynomial<BigInteger> P, GenPolynomial<BigInteger> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        GenPolynomialRing<BigInteger> fac = P.ring;
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<BigInteger> q;
        GenPolynomial<BigInteger> r;
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
        // compute contents and primitive parts
        BigInteger a = baseContent(r);
        BigInteger b = baseContent(q);
        // gcd of coefficient contents
        BigInteger c = gcd(a, b); // indirection
        r = divide(r, a); // indirection
        q = divide(q, b); // indirection
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        // compute normalization factor
        BigInteger ac = r.leadingBaseCoefficient();
        BigInteger bc = q.leadingBaseCoefficient();
        BigInteger cc = gcd(ac, bc); // indirection
        // compute degree vectors, only univeriate
        ExpVector rdegv = r.degreeVector();
        ExpVector qdegv = q.degreeVector();
        //initialize prime list and degree vector
        PrimeList primes = new PrimeList(PrimeList.Range.medium);
        int pn = 50; //primes.size();

        ModularRingFactory<MOD> cofac;
        GenPolynomial<MOD> qm;
        GenPolynomial<MOD> qmf;
        GenPolynomial<MOD> rm;
        GenPolynomial<MOD> rmf;
        GenPolynomial<MOD> cmf;
        GenPolynomialRing<MOD> mfac;
        GenPolynomial<MOD> cm = null;
        GenPolynomial<MOD>[] ecm = null;
        GenPolynomial<MOD> sm = null;
        GenPolynomial<MOD> tm = null;
        HenselApprox<MOD> lift = null;
        if (debug) {
            logger.debug("c = " + c);
            logger.debug("cc = " + cc);
            logger.debug("primes = " + primes);
        }

        int i = 0;
        for (java.math.BigInteger p : primes) {
            //System.out.println("next run ++++++++++++++++++++++++++++++++++");
            if (++i >= pn) {
                logger.error("prime list exhausted, pn = " + pn);
                logger.info("primes = " + primes);
                return iufd.baseGcd(P, S);
                //throw new ArithmeticException("prime list exhausted");
            }
            // initialize coefficient factory and map normalization factor
            //cofac = new ModIntegerRing(p, true);
            if (ModLongRing.MAX_LONG.compareTo(p) > 0) {
                cofac = (ModularRingFactory) new ModLongRing(p, true);
            } else {
                cofac = (ModularRingFactory) new ModIntegerRing(p, true);
            }
            MOD nf = cofac.fromInteger(cc.getVal());
            if (nf.isZERO()) {
                continue;
            }
            nf = cofac.fromInteger(q.leadingBaseCoefficient().getVal());
            if (nf.isZERO()) {
                continue;
            }
            nf = cofac.fromInteger(r.leadingBaseCoefficient().getVal());
            if (nf.isZERO()) {
                continue;
            }
            // initialize polynomial factory and map polynomials
            mfac = new GenPolynomialRing<MOD>(cofac, fac.nvar, fac.tord, fac.getVars());
            qm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, q);
            if (!qm.degreeVector().equals(qdegv)) {
                continue;
            }
            rm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, r);
            if (!rm.degreeVector().equals(rdegv)) {
                continue;
            }
            if (debug) {
                logger.info("cofac = " + cofac.getIntegerModul());
            }

            // compute univariate modular gcd
            cm = qm.gcd(rm);

            // test for constant g.c.d
            if (cm.isConstant()) {
                logger.debug("cm, constant = " + cm);
                return fac.getONE().multiply(c);
            }

            // compute factors and gcd with factor
            GenPolynomial<BigInteger> crq;
            rmf = rm.divide(cm); // rm = cm * rmf
            ecm = cm.egcd(rmf);
            if (ecm[0].isONE()) {
                //logger.debug("gcd() first factor " + rmf);
                crq = r;
                cmf = rmf;
                sm = ecm[1];
                tm = ecm[2];
            } else {
                qmf = qm.divide(cm); // qm = cm * qmf
                ecm = cm.egcd(qmf);
                if (ecm[0].isONE()) {
                    //logger.debug("gcd() second factor " + qmf);
                    crq = q;
                    cmf = qmf;
                    sm = ecm[1];
                    tm = ecm[2];
                } else {
                    logger.info("giving up on Hensel gcd reverting to Subres gcd");
                    return iufd.baseGcd(P, S);
                }
            }
            BigInteger cn = crq.maxNorm();
            cn = cn.multiply(crq.leadingBaseCoefficient().abs());
            cn = cn.multiply(cn.fromInteger(2));
            if (debug) {
                System.out.println("crq = " + crq);
                System.out.println("cm  = " + cm);
                System.out.println("cmf = " + cmf);
                System.out.println("sm  = " + sm);
                System.out.println("tm  = " + tm);
                System.out.println("cn  = " + cn);
            }
            try {
                if (quadratic) {
                    lift = HenselUtil.liftHenselQuadratic(crq, cn, cm, cmf, sm, tm);
                } else {
                    lift = HenselUtil.liftHensel(crq, cn, cm, cmf, sm, tm);
                }
            } catch (NoLiftingException nle) {
                logger.info("giving up on Hensel gcd reverting to Subres gcd " + nle);
                return iufd.baseGcd(P, S);
            }
            q = lift.A;
            if (debug) {
                System.out.println("q   = " + q);
                System.out.println("qf  = " + lift.B);
            }
            q = basePrimitivePart(q);
            q = q.multiply(c).abs();
            if (PolyUtil.<BigInteger> baseSparsePseudoRemainder(P, q).isZERO()
                            && PolyUtil.<BigInteger> baseSparsePseudoRemainder(S, q).isZERO()) {
                break;
            }
            logger.info("final devision not successfull");
            //System.out.println("P rem q = " + PolyUtil.<BigInteger>basePseudoRemainder(P,q));
            //System.out.println("S rem q = " + PolyUtil.<BigInteger>basePseudoRemainder(S,q));
            //break;
        }
        return q;
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor. Uses
     * multivariate Hensel list.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<BigInteger>> recursiveUnivariateGcd(
                    GenPolynomial<GenPolynomial<BigInteger>> P, GenPolynomial<GenPolynomial<BigInteger>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<GenPolynomial<BigInteger>> q;
        GenPolynomial<GenPolynomial<BigInteger>> r;
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
        GenPolynomial<BigInteger> a = recursiveContent(r);
        GenPolynomial<BigInteger> b = recursiveContent(q);

        GenPolynomial<BigInteger> c = gcd(a, b); // go to recursion
        //System.out.println("rgcd c = " + c);
        r = PolyUtil.<BigInteger> recursiveDivide(r, a);
        q = PolyUtil.<BigInteger> recursiveDivide(q, b);
        a = PolyUtil.<BigInteger> basePseudoDivide(a, c);
        b = PolyUtil.<BigInteger> basePseudoDivide(b, c);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        // convert from Z[y1,...,yr][x] to Z[x][y1,...,yr] to Z[x,y1,...,yr]
        GenPolynomial<GenPolynomial<BigInteger>> qs = PolyUtil.<BigInteger> switchVariables(q);
        GenPolynomial<GenPolynomial<BigInteger>> rs = PolyUtil.<BigInteger> switchVariables(r);

        GenPolynomialRing<GenPolynomial<BigInteger>> rfac = qs.ring;
        RingFactory<GenPolynomial<BigInteger>> rrfac = rfac.coFac;
        GenPolynomialRing<BigInteger> cfac = (GenPolynomialRing<BigInteger>) rrfac;
        GenPolynomialRing<BigInteger> dfac = cfac.extend(rfac.getVars());
        //System.out.println("pfac = " + P.ring.toScript());
        //System.out.println("rfac = " + rfac.toScript());
        //System.out.println("dfac = " + dfac.toScript());

        GenPolynomial<BigInteger> qd = PolyUtil.<BigInteger> distribute(dfac, qs);
        GenPolynomial<BigInteger> rd = PolyUtil.<BigInteger> distribute(dfac, rs);
        //System.out.println("qd = " + qd);
        //System.out.println("rd = " + rd);
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // compute normalization factor
        BigInteger ac = rd.leadingBaseCoefficient();
        BigInteger bc = qd.leadingBaseCoefficient();
        BigInteger cc = gcd(ac, bc); // indirection

        //initialize prime list
        PrimeList primes = new PrimeList(PrimeList.Range.medium); // PrimeList.Range.medium);
        Iterator<java.math.BigInteger> primeIter = primes.iterator();
        int pn = 50; //primes.size();

        // double check variables
        // need qe,re,qd,rd,a,b
        GenPolynomial<MOD> qe0;
        GenPolynomial<MOD> re0;
        GenPolynomial<MOD> ce0 = null;

        for (int i = 0; i < 11; i++) { // meta loop
            //System.out.println("======================================================= run " 
            //                   + dfac.nvar + ", " + i);
            java.math.BigInteger p = null; //new java.math.BigInteger("19"); //primes.next();
            // 5 small, 5 medium and 1 large size primes
            if (i == 0) { // medium size
                primes = new PrimeList(PrimeList.Range.medium);
                primeIter = primes.iterator();
            }
            if (i == 5) { // smal size
                primes = new PrimeList(PrimeList.Range.small);
                primeIter = primes.iterator();
                p = primeIter.next(); // 2
                p = primeIter.next(); // 3
                p = primeIter.next(); // 5
                p = primeIter.next(); // 7
            }
            if (i == 10) { // large size
                primes = new PrimeList(PrimeList.Range.large);
                primeIter = primes.iterator();
            }
            ModularRingFactory<MOD> cofac = null;
            int pi = 0;
            while (pi < pn && primeIter.hasNext()) {
                p = primeIter.next();
                //p = new java.math.BigInteger("19");
                logger.info("prime = " + p);
                // initialize coefficient factory and map normalization factor and polynomials
                ModularRingFactory<MOD> cf = null;
                if (ModLongRing.MAX_LONG.compareTo(p) > 0) {
                    cf = (ModularRingFactory) new ModLongRing(p, true);
                } else {
                    cf = (ModularRingFactory) new ModIntegerRing(p, true);
                }
                MOD nf = cf.fromInteger(cc.getVal());
                if (nf.isZERO()) {
                    continue;
                }
                nf = cf.fromInteger(q.leadingBaseCoefficient().leadingBaseCoefficient().getVal());
                if (nf.isZERO()) {
                    continue;
                }
                nf = cf.fromInteger(r.leadingBaseCoefficient().leadingBaseCoefficient().getVal());
                if (nf.isZERO()) {
                    continue;
                }
                cofac = cf;
                break;
            }
            if (cofac == null) { // no lucky prime found
                System.out.println("giving up on Hensel gcd reverting to Subres gcd");
                GenPolynomial<GenPolynomial<BigInteger>> T = iufd.recursiveUnivariateGcd(P, S);
                return T.abs().multiply(c); //.abs();
            }
            //System.out.println("cofac = " + cofac);

            List<MOD> V = new ArrayList<MOD>(P.ring.nvar);
            GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(cofac, dfac);
            //System.out.println("mfac = " + mfac.toScript());
            GenPolynomial<MOD> qm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, qd);
            GenPolynomial<MOD> rm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, rd);
            //System.out.println("qm = " + qm);
            //System.out.println("rm = " + rm);

            // search evaluation point and evaluate
            GenPolynomialRing<MOD> ckfac = mfac;
            GenPolynomial<MOD> qe = qm;
            GenPolynomial<MOD> re = rm;
            GenPolynomial<MOD> qep;
            GenPolynomial<MOD> rep;
            for (int j = dfac.nvar; j > 1; j--) {
                // evaluation to univariate case
                long degq = qe.degree(ckfac.nvar - 2);
                long degr = re.degree(ckfac.nvar - 2);
                ckfac = ckfac.contract(1);
                long vi = 1L; //(long)(dfac.nvar-j); // 1L; 0 not so good for small p
                if (p.longValue() > 1000L) {
                    //vi = (long)j+1L;
                    vi = 0L;
                }
                // search small evaluation point
                while (true) {
                    MOD vp = cofac.fromInteger(vi++);
                    //System.out.println("vp = " + vp);
                    if (vp.isZERO() && vi != 1L) { // all elements of Z_p exhausted
                        qe = null;
                        re = null;
                        break;
                        //GenPolynomial<GenPolynomial<BigInteger>> T = iufd.recursiveUnivariateGcd(P, S);
                        //return T.abs().multiply(c); //.abs();
                    }
                    qep = PolyUtil.<MOD> evaluateMain(ckfac, qe, vp);
                    rep = PolyUtil.<MOD> evaluateMain(ckfac, re, vp);
                    //System.out.println("qep = " + qep);
                    //System.out.println("rep = " + rep);

                    // check lucky evaluation point 
                    MOD ql = qep.leadingBaseCoefficient();
                    MOD rl = rep.leadingBaseCoefficient();
                    if (ql.isZERO() || rl.isZERO()) { // nearly non sense
                        continue;
                    }
                    if (degq != qep.degree(ckfac.nvar - 1)) {
                        //System.out.println("degv(qe) = " + qe.degreeVector());
                        //System.out.println("deg(qe) = " + degq + ", deg(qe) = " + qep.degree(ckfac.nvar-1));
                        continue;
                    }
                    if (degr != rep.degree(ckfac.nvar - 1)) {
                        //System.out.println("degv(re) = " + re.degreeVector());
                        //System.out.println("deg(re) = " + degr + ", deg(re) = " + rep.degree(ckfac.nvar-1));
                        continue;
                    }
                    V.add(vp);
                    qe = qep;
                    re = rep;
                    break;
                }
                if (qe == null && re == null) {
                    break;
                }
            }
            if (qe == null && re == null) {
                continue;
            }
            logger.info("evaluation points  = " + V);
            //System.out.println("qe = " + qe);
            //System.out.println("re = " + re);

            // recursion base:
            GreatestCommonDivisorAbstract<MOD> mufd = GCDFactory.getImplementation(cofac);
            GenPolynomial<MOD> ce = mufd.baseGcd(qe, re);
            if (ce.isConstant()) {
                return P.ring.getONE().multiply(c);
            }
            //System.out.println("ce = " + ce);
            logger.info("base gcd = " + ce);
            //System.out.println("c = " + c);

            // double check 
            // need qe,re,qd,rd,a,b
            if (i == 0) {
                qe0 = qe;
                re0 = re;
                ce0 = ce;
                continue;
            }
            long d0 = ce0.degree(0);
            long d1 = ce.degree(0);
            //System.out.println("d0, d1 = " + d0 + ", " + d1);
            if (d1 < d0) {
                qe0 = qe;
                re0 = re;
                ce0 = ce;
                continue;
            } else if (d1 > d0) {
                continue;
            }
            // d0 == d1 is ok
            long dx = r.degree(0);
            //System.out.println("d0, dx = " + d0 + ", " + dx);
            if (d0 == dx) { // gcd == r ??
                if (PolyUtil.<BigInteger> recursivePseudoRemainder(q, r).isZERO()) {
                    r = r.abs().multiply(c); //.abs();
                    logger.info("exit with r | q : " + r);
                    return r;
                }
                continue;
            }

            // norm
            BigInteger mn = null;
            //mn = mn.multiply(cc).multiply(mn.fromInteger(2));

            // prepare lifting
            GenPolynomial<MOD> re1 = PolyUtil.<MOD> basePseudoDivide(re, ce);
            GenPolynomial<MOD> qe1 = PolyUtil.<MOD> basePseudoDivide(qe, ce);
            GenPolynomial<BigInteger> ui;
            GenPolynomial<MOD> he;
            GenPolynomial<BigInteger> g;
            if (mufd.baseGcd(re1, ce).isONE()) {
                ui = rd;
                he = re1;
                BigInteger an = rd.maxNorm();
                mn = an.multiply(cc).multiply(new BigInteger(2L));
                g = a;
            } else if (mufd.baseGcd(qe1, ce).isONE()) {
                ui = qd;
                he = qe1;
                BigInteger bn = qd.maxNorm();
                mn = bn.multiply(cc).multiply(new BigInteger(2L));
                g = b;
            } else {
                break;
                //System.out.println("giving up on Hensel gcd reverting to Subres gcd");
            }
            //System.out.println("ui = " + ui);

            long k = Power.logarithm(new BigInteger(p), mn);
            //System.out.println("mn = " + mn);
            //System.out.println("k = " + k);

            List<GenPolynomial<MOD>> F = new ArrayList<GenPolynomial<MOD>>(2);
            F.add(ce);
            F.add(he);
            List<GenPolynomial<BigInteger>> G = new ArrayList<GenPolynomial<BigInteger>>(2);
            G.add(g.ring.getONE()); // TODO
            G.add(g.ring.getONE());
            List<GenPolynomial<MOD>> lift;
            try {
                lift = HenselMultUtil.<MOD> liftHenselFull(ui, F, V, k, G);
                logger.info("lift = " + lift);
            } catch (NoLiftingException nle) {
                //System.out.println("exception : " + nle);
                continue;
            } catch (ArithmeticException ae) {
                //System.out.println("exception : " + ae);
                continue;
            }

            // convert Ci from Z_{p^k}[x,y1,...,yr] to Z[x,y1,...,yr] to Z[x][y1,...,yr] to Z[y1,...,yr][x]
            GenPolynomial<BigInteger> ci = PolyUtil.integerFromModularCoefficients(dfac, lift.get(0));
            GenPolynomial<GenPolynomial<BigInteger>> Cr = PolyUtil.<BigInteger> recursive(rfac, ci);
            GenPolynomial<GenPolynomial<BigInteger>> Cs = PolyUtil.<BigInteger> switchVariables(Cr);
            if (!Cs.ring.equals(P.ring)) {
                System.out.println("Cs.ring = " + Cs.ring + ", P.ring = " + P.ring);
            }
            q = recursivePrimitivePart(Cs);
            q = q.abs().multiply(c); //.abs();
            if (PolyUtil.<BigInteger> recursivePseudoRemainder(P, q).isZERO()
                            && PolyUtil.<BigInteger> recursivePseudoRemainder(S, q).isZERO()) {
                return q;
            }
            //System.out.println("bad q = " + q);
            //System.out.println("bad P/q = " + PolyUtil.<BigInteger> recursivePseudoRemainder(P,q));
            //System.out.println("bad S/q = " + PolyUtil.<BigInteger> recursivePseudoRemainder(S,q));
        } // end for meta loop
          // Hensel gcd failed
        GenPolynomial<GenPolynomial<BigInteger>> T = iufd.recursiveUnivariateGcd(P, S);
        T = T.abs().multiply(c); //.abs();
        logger.info("giving up on Hensel gcd reverting to Subres gcd " + T);
        //System.out.println("giving up on Hensel gcd reverted to Subres gcd: " + T);
        return T;
    }

}
