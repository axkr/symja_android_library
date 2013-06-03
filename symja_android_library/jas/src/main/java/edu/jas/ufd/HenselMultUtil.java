/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Hensel multivariate lifting utilities.
 * @author Heinz Kredel
 */

public class HenselMultUtil {


    private static final Logger logger = Logger.getLogger(HenselMultUtil.class);


    private static final boolean debug = logger.isInfoEnabled();


    /**
     * Modular diophantine equation solution and lifting algorithm. Let p =
     * A_i.ring.coFac.modul() and assume ggt(A,B) == 1 mod p.
     * @param A modular GenPolynomial, mod p^k
     * @param B modular GenPolynomial, mod p^k
     * @param C modular GenPolynomial, mod p^k
     * @param V list of substitution values, mod p^k
     * @param d desired approximation exponent (x_i-v_i)^d.
     * @param k desired approximation exponent p^k.
     * @return [s, t] with s A' + t B' = C mod p^k, with A' = B, B' = A.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> liftDiophant(
                    GenPolynomial<MOD> A, GenPolynomial<MOD> B, GenPolynomial<MOD> C, List<MOD> V, long d,
                    long k) throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = C.ring;
        if (pkfac.nvar == 1) { // V, d ignored
            return HenselUtil.<MOD> liftDiophant(A, B, C, k);
        }
        if (!pkfac.equals(A.ring)) {
            throw new IllegalArgumentException("A.ring != pkfac: " + A.ring + " != " + pkfac);
        }

        // evaluate at v_n:
        List<MOD> Vp = new ArrayList<MOD>(V);
        MOD v = Vp.remove(Vp.size() - 1);
        //GenPolynomial<MOD> zero = pkfac.getZERO();
        // (x_n - v)
        GenPolynomial<MOD> mon = pkfac.getONE();
        GenPolynomial<MOD> xv = pkfac.univariate(0, 1);
        xv = xv.subtract(pkfac.fromInteger(v.getSymmetricInteger().getVal()));
        //System.out.println("xv = " + xv);
        // A(v), B(v), C(v)
        ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>) pkfac.coFac;
        MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        //System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> ckfac = pkfac.contract(1);
        GenPolynomial<MOD> Ap = PolyUtil.<MOD> evaluateMain(ckfac, A, vp);
        GenPolynomial<MOD> Bp = PolyUtil.<MOD> evaluateMain(ckfac, B, vp);
        GenPolynomial<MOD> Cp = PolyUtil.<MOD> evaluateMain(ckfac, C, vp);
        //System.out.println("Ap = " + Ap);
        //System.out.println("Bp = " + Bp);
        //System.out.println("Cp = " + Cp);

        // recursion:
        List<GenPolynomial<MOD>> su = HenselMultUtil.<MOD> liftDiophant(Ap, Bp, Cp, Vp, d, k);
        //System.out.println("su@p^" + k + " = " + su);
        //System.out.println("coFac = " + su.get(0).ring.coFac.toScript());
        if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Bp, Ap, su.get(0), su.get(1), Cp)) {
            //System.out.println("isDiophantLift: false");
            throw new NoLiftingException("isDiophantLift: false");
        }
        if (!ckfac.equals(su.get(0).ring)) {
            throw new IllegalArgumentException("qfac != ckfac: " + su.get(0).ring + " != " + ckfac);
        }
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), pkfac);
        //GenPolynomialRing<BigInteger> cifac = new GenPolynomialRing<BigInteger>(new BigInteger(), ckfac);
        //System.out.println("ifac = " + ifac.toScript());
        String[] mn = new String[] { pkfac.getVars()[pkfac.nvar - 1] };
        GenPolynomialRing<GenPolynomial<MOD>> qrfac = new GenPolynomialRing<GenPolynomial<MOD>>(ckfac, 1, mn);
        //System.out.println("qrfac = " + qrfac);

        List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<BigInteger>> supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
        for (GenPolynomial<MOD> s : su) {
            GenPolynomial<MOD> sp = s.extend(pkfac, 0, 0L);
            sup.add(sp);
            GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, sp);
            supi.add(spi);
        }
        //System.out.println("sup  = " + sup);
        //System.out.println("supi = " + supi);
        GenPolynomial<BigInteger> Ai = PolyUtil.integerFromModularCoefficients(ifac, A);
        GenPolynomial<BigInteger> Bi = PolyUtil.integerFromModularCoefficients(ifac, B);
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, C);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Bi = " + Bi);
        //System.out.println("Ci = " + Ci);
        //GenPolynomial<MOD> aq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, Ai);
        //GenPolynomial<MOD> bq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, Bi);
        //System.out.println("aq = " + aq);
        //System.out.println("bq = " + bq);

        // compute error:
        GenPolynomial<BigInteger> E = Ci; // - sum_i s_i b_i
        E = E.subtract(Bi.multiply(supi.get(0)));
        E = E.subtract(Ai.multiply(supi.get(1)));
        //System.out.println("E     = " + E);
        if (E.isZERO()) {
            logger.info("liftDiophant leaving on zero E");
            return sup;
        }
        GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
        //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
        logger.info("Ep(0," + pkfac.nvar + ") = " + Ep);
        if (Ep.isZERO()) {
            logger.info("liftDiophant leaving on zero Ep mod p^k");
            return sup;
        }
        for (int e = 1; e <= d; e++) {
            //System.out.println("\ne = " + e + " -------------------------------------- " + pkfac.nvar);
            GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(qrfac, Ep);
            //System.out.println("Epr   = " + Epr);
            UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(qrfac);
            //System.out.println("psfac = " + psfac);
            TaylorFunction<GenPolynomial<MOD>> F = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
            //System.out.println("F     = " + F);
            List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
            GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
            Vs.add(vq);
            //System.out.println("Vs    = " + Vs);
            UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(F, vq);
            //System.out.println("Epst  = " + Epst);
            GenPolynomial<MOD> cm = Epst.coefficient(e);
            //System.out.println("cm   = " + cm + ", cm.ring   = " + cm.ring.toScript());

            // recursion:
            List<GenPolynomial<MOD>> S = HenselMultUtil.<MOD> liftDiophant(Ap, Bp, cm, Vp, d, k);
            //System.out.println("S    = " + S);
            if (!ckfac.coFac.equals(S.get(0).ring.coFac)) {
                throw new IllegalArgumentException("ckfac != pkfac: " + ckfac.coFac + " != "
                                + S.get(0).ring.coFac);
            }
            if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap, Bp, S.get(1), S.get(0), cm)) {
                //System.out.println("isDiophantLift: false");
                throw new NoLiftingException("isDiophantLift: false");
            }
            mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
            //System.out.println("mon  = " + mon);
            List<GenPolynomial<MOD>> Sp = new ArrayList<GenPolynomial<MOD>>(S.size());
            int i = 0;
            supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
            for (GenPolynomial<MOD> dd : S) {
                //System.out.println("dd = " + dd);
                GenPolynomial<MOD> de = dd.extend(pkfac, 0, 0L);
                GenPolynomial<MOD> dm = de.multiply(mon);
                Sp.add(dm);
                de = sup.get(i).sum(dm);
                //System.out.println("dd = " + dd);
                sup.set(i++, de);
                GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, dm);
                supi.add(spi);
            }
            //System.out.println("Sp   = " + Sp);
            //System.out.println("sup  = " + sup);
            //System.out.println("supi = " + supi);
            // compute new error
            //E = E; // - sum_i s_i b_i
            E = E.subtract(Bi.multiply(supi.get(0)));
            E = E.subtract(Ai.multiply(supi.get(1)));
            //System.out.println("E     = " + E);
            if (E.isZERO()) {
                logger.info("liftDiophant leaving on zero E");
                return sup;
            }
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
            //System.out.println("Ep(" + e + "," + pkfac.nvar + ") = " + Ep); 
            logger.info("Ep(" + e + "," + pkfac.nvar + ") = " + Ep);
            if (Ep.isZERO()) {
                logger.info("liftDiophant leaving on zero Ep mod p^k");
                return sup;
            }
        }
        //System.out.println("*** done: " + pkfac.nvar);
        return sup;
    }


    /**
     * Modular diophantine equation solution and lifting algorithm. Let p =
     * A_i.ring.coFac.modul() and assume ggt(a,b) == 1 mod p, for a, b in A.
     * @param A list of modular GenPolynomials, mod p^k
     * @param C modular GenPolynomial, mod p^k
     * @param V list of substitution values, mod p^k
     * @param d desired approximation exponent (x_i-v_i)^d.
     * @param k desired approximation exponent p^k.
     * @return [s_1,..., s_n] with sum_i s_i A_i' = C mod p^k, with Ai' =
     *         prod_{j!=i} A_j.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> liftDiophant(
                    List<GenPolynomial<MOD>> A, GenPolynomial<MOD> C, List<MOD> V, long d, long k)
                    throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = C.ring;
        if (pkfac.nvar == 1) { // V, d ignored
            return HenselUtil.<MOD> liftDiophant(A, C, k);
        }
        if (!pkfac.equals(A.get(0).ring)) {
            throw new IllegalArgumentException("A.ring != pkfac: " + A.get(0).ring + " != " + pkfac);
        }
        // co-products
        GenPolynomial<MOD> As = pkfac.getONE();
        for (GenPolynomial<MOD> a : A) {
            As = As.multiply(a);
        }
        List<GenPolynomial<MOD>> Bp = new ArrayList<GenPolynomial<MOD>>(A.size());
        for (GenPolynomial<MOD> a : A) {
            GenPolynomial<MOD> b = PolyUtil.<MOD> basePseudoDivide(As, a);
            Bp.add(b);
        }

        // evaluate at v_n:
        List<MOD> Vp = new ArrayList<MOD>(V);
        MOD v = Vp.remove(Vp.size() - 1);
        // (x_n - v)
        GenPolynomial<MOD> mon = pkfac.getONE();
        GenPolynomial<MOD> xv = pkfac.univariate(0, 1);
        xv = xv.subtract(pkfac.fromInteger(v.getSymmetricInteger().getVal()));
        //System.out.println("xv = " + xv);
        // A(v), B(v), C(v)
        ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>) pkfac.coFac;
        MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        //System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> ckfac = pkfac.contract(1);
        List<GenPolynomial<MOD>> Ap = new ArrayList<GenPolynomial<MOD>>(A.size());
        for (GenPolynomial<MOD> a : A) {
            GenPolynomial<MOD> ap = PolyUtil.<MOD> evaluateMain(ckfac, a, vp);
            Ap.add(ap);
        }
        GenPolynomial<MOD> Cp = PolyUtil.<MOD> evaluateMain(ckfac, C, vp);
        //System.out.println("Ap = " + Ap);
        //System.out.println("Cp = " + Cp);

        // recursion:
        List<GenPolynomial<MOD>> su = HenselMultUtil.<MOD> liftDiophant(Ap, Cp, Vp, d, k);
        //System.out.println("su@p^" + k + " = " + su);
        //System.out.println("coFac = " + su.get(0).ring.coFac.toScript());
        if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap, su, Cp)) {
            //System.out.println("isDiophantLift: false");
            throw new NoLiftingException("isDiophantLift: false");
        }
        if (!ckfac.equals(su.get(0).ring)) {
            throw new IllegalArgumentException("qfac != ckfac: " + su.get(0).ring + " != " + ckfac);
        }
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), pkfac);
        //GenPolynomialRing<BigInteger> cifac = new GenPolynomialRing<BigInteger>(new BigInteger(), ckfac);
        //System.out.println("ifac = " + ifac.toScript());
        String[] mn = new String[] { pkfac.getVars()[pkfac.nvar - 1] };
        GenPolynomialRing<GenPolynomial<MOD>> qrfac = new GenPolynomialRing<GenPolynomial<MOD>>(ckfac, 1, mn);
        //System.out.println("qrfac = " + qrfac);

        List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<BigInteger>> supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
        for (GenPolynomial<MOD> s : su) {
            GenPolynomial<MOD> sp = s.extend(pkfac, 0, 0L);
            sup.add(sp);
            GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, sp);
            supi.add(spi);
        }
        //System.out.println("sup  = " + sup);
        //System.out.println("supi = " + supi);
        List<GenPolynomial<BigInteger>> Ai = new ArrayList<GenPolynomial<BigInteger>>(A.size());
        for (GenPolynomial<MOD> a : A) {
            GenPolynomial<BigInteger> ai = PolyUtil.integerFromModularCoefficients(ifac, a);
            Ai.add(ai);
        }
        List<GenPolynomial<BigInteger>> Bi = new ArrayList<GenPolynomial<BigInteger>>(A.size());
        for (GenPolynomial<MOD> b : Bp) {
            GenPolynomial<BigInteger> bi = PolyUtil.integerFromModularCoefficients(ifac, b);
            Bi.add(bi);
        }
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, C);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Ci = " + Ci);

        List<GenPolynomial<MOD>> Aq = new ArrayList<GenPolynomial<MOD>>(A.size());
        for (GenPolynomial<BigInteger> ai : Ai) {
            GenPolynomial<MOD> aq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, ai);
            Aq.add(aq);
        }
        //System.out.println("Aq = " + Aq);

        // compute error:
        GenPolynomial<BigInteger> E = Ci; // - sum_i s_i b_i
        int i = 0;
        for (GenPolynomial<BigInteger> bi : Bi) {
            E = E.subtract(bi.multiply(supi.get(i++)));
        }
        //System.out.println("E     = " + E);
        if (E.isZERO()) {
            logger.info("liftDiophant leaving on zero E");
            return sup;
        }
        GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
        //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
        logger.info("Ep(0," + pkfac.nvar + ") = " + Ep);
        if (Ep.isZERO()) {
            logger.info("liftDiophant leaving on zero Ep mod p^k");
            return sup;
        }
        for (int e = 1; e <= d; e++) {
            //System.out.println("\ne = " + e + " -------------------------------------- " + pkfac.nvar);
            GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(qrfac, Ep);
            //System.out.println("Epr   = " + Epr);
            UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(qrfac);
            //System.out.println("psfac = " + psfac);
            TaylorFunction<GenPolynomial<MOD>> F = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
            //System.out.println("F     = " + F);
            List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
            GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
            Vs.add(vq);
            //System.out.println("Vs    = " + Vs);
            UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(F, vq);
            //System.out.println("Epst  = " + Epst);
            GenPolynomial<MOD> cm = Epst.coefficient(e);
            //System.out.println("cm   = " + cm + ", cm.ring   = " + cm.ring.toScript());
            if (cm.isZERO()) {
                continue;
            }
            // recursion:
            List<GenPolynomial<MOD>> S = HenselMultUtil.<MOD> liftDiophant(Ap, cm, Vp, d, k);
            //System.out.println("S    = " + S);
            if (!ckfac.coFac.equals(S.get(0).ring.coFac)) {
                throw new IllegalArgumentException("ckfac != pkfac: " + ckfac.coFac + " != "
                                + S.get(0).ring.coFac);
            }
            if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap, S, cm)) {
                //System.out.println("isDiophantLift: false");
                throw new NoLiftingException("isDiophantLift: false");
            }
            mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
            //System.out.println("mon  = " + mon);
            List<GenPolynomial<MOD>> Sp = new ArrayList<GenPolynomial<MOD>>(S.size());
            i = 0;
            supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
            for (GenPolynomial<MOD> dd : S) {
                //System.out.println("dd = " + dd);
                GenPolynomial<MOD> de = dd.extend(pkfac, 0, 0L);
                GenPolynomial<MOD> dm = de.multiply(mon);
                Sp.add(dm);
                de = sup.get(i).sum(dm);
                //System.out.println("dd = " + dd);
                sup.set(i++, de);
                GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, dm);
                supi.add(spi);
            }
            //System.out.println("Sp   = " + Sp);
            //System.out.println("sup  = " + sup);
            //System.out.println("supi = " + supi);
            // compute new error
            //E = E; // - sum_i s_i b_i
            i = 0;
            for (GenPolynomial<BigInteger> bi : Bi) {
                E = E.subtract(bi.multiply(supi.get(i++)));
            }
            //System.out.println("E     = " + E);
            if (E.isZERO()) {
                logger.info("liftDiophant leaving on zero E");
                return sup;
            }
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
            //System.out.println("Ep(" + e + "," + pkfac.nvar + ") = " + Ep); 
            logger.info("Ep(" + e + "," + pkfac.nvar + ") = " + Ep);
            if (Ep.isZERO()) {
                logger.info("liftDiophant leaving on zero Ep mod p^k");
                return sup;
            }
        }
        //System.out.println("*** done: " + pkfac.nvar);
        return sup;
    }


    /**
     * Modular Hensel lifting algorithm on coefficients test. Let p =
     * f_i.ring.coFac.modul() and assume C == prod_{0,...,n-1} f_i mod p with
     * gcd(f_i,f_j) == 1 mod p for i != j
     * @param C integer polynomial
     * @param Cp GenPolynomial mod p^k
     * @param F = [f_0,...,f_{n-1}] list of monic modular polynomials.
     * @param k approximation exponent.
     * @param L = [g_0,...,g_{n-1}] list of lifted modular polynomials.
     * @return true if C = prod_{0,...,n-1} g_i mod p^k, else false.
     * @deprecated use isHenselLift() without parameter k 
     */
    @Deprecated
    public static <MOD extends GcdRingElem<MOD> & Modular> boolean isHenselLift(GenPolynomial<BigInteger> C,
                    GenPolynomial<MOD> Cp, List<GenPolynomial<MOD>> F, long k, List<GenPolynomial<MOD>> L) {
        return isHenselLift(C,Cp,F,L);
    } 


    /**
     * Modular Hensel lifting algorithm on coefficients test. Let p =
     * f_i.ring.coFac.modul() and assume C == prod_{0,...,n-1} f_i mod p with
     * gcd(f_i,f_j) == 1 mod p for i != j
     * @param C integer polynomial
     * @param Cp GenPolynomial mod p^k
     * @param F = [f_0,...,f_{n-1}] list of monic modular polynomials.
     * @param L = [g_0,...,g_{n-1}] list of lifted modular polynomials.
     * @return true if C = prod_{0,...,n-1} g_i mod p^k, else false.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> boolean isHenselLift(GenPolynomial<BigInteger> C,
                    GenPolynomial<MOD> Cp, List<GenPolynomial<MOD>> F, List<GenPolynomial<MOD>> L) {
        boolean t = true;
        GenPolynomialRing<MOD> qfac = L.get(0).ring;
        GenPolynomial<MOD> q = qfac.getONE();
        for (GenPolynomial<MOD> fi : L) {
            q = q.multiply(fi);
        }
        t = Cp.equals(q);
        if (!t) {
            System.out.println("Cp     = " + Cp);
            System.out.println("q      = " + q);
            System.out.println("Cp != q: " + Cp.subtract(q));
            return t;
        }
        GenPolynomialRing<BigInteger> dfac = C.ring;
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(dfac, q);
        t = C.equals(Ci);
        if (!t) {
            System.out.println("C      = " + C);
            System.out.println("Ci     = " + Ci);
            System.out.println("C != Ci: " + C.subtract(Ci));
            return t;
        }
        // test L mod id(V) == F
        return t;
    }


    /**
     * Modular Hensel lifting algorithm, monic case. Let p =
     * A_i.ring.coFac.modul() and assume ggt(a,b) == 1 mod p, for a, b in A.
     * @param C monic GenPolynomial with integer coefficients
     * @param Cp GenPolynomial mod p^k
     * @param F list of modular GenPolynomials, mod (I_v, p^k )
     * @param V list of integer substitution values
     * @param k desired approximation exponent p^k.
     * @return [g'_1,..., g'_n] with prod_i g'_i = Cp mod p^k.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> liftHenselMonic(
                    GenPolynomial<BigInteger> C, GenPolynomial<MOD> Cp, List<GenPolynomial<MOD>> F,
                    List<BigInteger> V, long k) throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = Cp.ring;
        //if (pkfac.nvar == 1) { // V ignored
        //    return HenselUtil.<MOD> liftHenselMonic(C,F,k);
        //}
        long d = C.degree();
        //System.out.println("d = " + d);
        // prepare stack of polynomial rings and polynomials
        List<GenPolynomialRing<MOD>> Pfac = new ArrayList<GenPolynomialRing<MOD>>();
        List<GenPolynomial<MOD>> Ap = new ArrayList<GenPolynomial<MOD>>();
        List<MOD> Vb = new ArrayList<MOD>();
        MOD v = pkfac.coFac.fromInteger( V.get(0).getVal() );
        Pfac.add(pkfac);
        Ap.add(Cp);
        Vb.add(v);
        GenPolynomialRing<MOD> pf = pkfac;
        GenPolynomial<MOD> ap = Cp;
        for (int j = pkfac.nvar; j > 2; j--) {
            pf = pf.contract(1);
            Pfac.add(0, pf);
            //MOD vp = pkfac.coFac.fromInteger(V.get(j - 2).getSymmetricInteger().getVal());
            MOD vp = pkfac.coFac.fromInteger(V.get(j - 2).getVal());
            //System.out.println("vp     = " + vp);
            Vb.add(1, vp);
            ap = PolyUtil.<MOD> evaluateMain(pf, ap, vp);
            Ap.add(0, ap);
        }
        //System.out.println("Pfac   = " + Pfac);
        if (debug) {
            logger.debug("Pfac   = " + Pfac);
        }
        //System.out.println("Ap     = " + Ap);
        //System.out.println("V      = " + V);
        //System.out.println("Vb     = " + Vb);
        // setup bi-variate base case
        GenPolynomialRing<MOD> pk1fac = F.get(0).ring;
        if (!pkfac.coFac.equals(pk1fac.coFac)) {
            throw new IllegalArgumentException("F.ring != pkfac: " + pk1fac + " != " + pkfac);
        }
        // TODO: adjust leading coefficients
        pkfac = Pfac.get(0);
        //Cp = Ap.get(0);
        //System.out.println("pkfac  = " + pkfac.toScript());
        //System.out.println("pk1fac = " + pk1fac.toScript());
        GenPolynomialRing<BigInteger> i1fac = new GenPolynomialRing<BigInteger>(new BigInteger(), pk1fac);
        //System.out.println("i1fac = " + i1fac.toScript());
        List<GenPolynomial<BigInteger>> Bi = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        for (GenPolynomial<MOD> b : F) {
            GenPolynomial<BigInteger> bi = PolyUtil.integerFromModularCoefficients(i1fac, b);
            Bi.add(bi);
        }
        //System.out.println("Bi = " + Bi);
        // evaluate Cp at v_n:
        //ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>) pkfac.coFac;
        //MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        //System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> ckfac; // = pkfac.contract(1);
        //GenPolynomial<MOD> Cs = PolyUtil.<MOD> evaluateMain(ckfac, Cp, vp);
        //System.out.println("Cp = " + Cp);
        //System.out.println("Cs = " + Cs);

        List<GenPolynomial<MOD>> U = new ArrayList<GenPolynomial<MOD>>(F.size());
        for (GenPolynomial<MOD> b : F) {
            GenPolynomial<MOD> bi = b.extend(pkfac, 0, 0L);
            U.add(bi);
        }
        //System.out.println("U  = " + U);
        List<GenPolynomial<MOD>> U1 = F;
        //System.out.println("U1 = " + U1);

        GenPolynomial<BigInteger> E = C.ring.getZERO();
        List<MOD> Vh = new ArrayList<MOD>();

        while (Pfac.size() > 0) { // loop through stack of polynomial rings
            pkfac = Pfac.remove(0);
            Cp = Ap.remove(0);
            v = Vb.remove(0);
            //Vh.add(0,v);
            //System.out.println("\npkfac = " + pkfac.toScript() + " ================================== " + Vh);

            // (x_n - v)
            GenPolynomial<MOD> mon = pkfac.getONE();
            GenPolynomial<MOD> xv = pkfac.univariate(0, 1);
            xv = xv.subtract(pkfac.fromInteger(v.getSymmetricInteger().getVal()));
            //System.out.println("xv = " + xv);

            long deg = Cp.degree(pkfac.nvar - 1);
            //System.out.println("deg = " + deg);

            GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), pkfac);
            //System.out.println("ifac = " + ifac.toScript());
            List<GenPolynomial<BigInteger>> Bip = new ArrayList<GenPolynomial<BigInteger>>(F.size());
            for (GenPolynomial<BigInteger> b : Bi) {
                GenPolynomial<BigInteger> bi = b.extend(ifac, 0, 0L);
                Bip.add(bi);
            }
            Bi = Bip;
            //System.out.println("Bi = " + Bi);
            GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, Cp);
            //System.out.println("Ci = " + Ci);

            // compute error:
            E = ifac.getONE();
            for (GenPolynomial<BigInteger> bi : Bi) {
                E = E.multiply(bi);
            }
            E = Ci.subtract(E);
            //System.out.println("E     = " + E);
            GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
            //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
            logger.info("Ep(0," + deg + "," + pkfac.nvar + ") = " + Ep);

            String[] mn = new String[] { pkfac.getVars()[pkfac.nvar - 1] };
            ckfac = pkfac.contract(1);
            GenPolynomialRing<GenPolynomial<MOD>> pkrfac = new GenPolynomialRing<GenPolynomial<MOD>>(ckfac,
                            1, mn);
            //System.out.println("pkrfac = " + pkrfac.toScript());

            for (int e = 1; e <= deg && !Ep.isZERO(); e++) {
                //System.out.println("\ne = " + e + " -------------------------------------- " + pkfac.nvar);
                GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(pkrfac, Ep);
                //System.out.println("Epr   = " + Epr);
                UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(
                                pkrfac);
                //System.out.println("psfac = " + psfac);
                TaylorFunction<GenPolynomial<MOD>> T = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
                //System.out.println("T     = " + T);
                List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
                GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
                Vs.add(vq);
                //System.out.println("Vs    = " + Vs + ", Vh = " + Vh);
                UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(T, vq);
                //System.out.println("Epst  = " + Epst);
                logger.info("Epst(" + e + "," + deg + ", " + pkfac.nvar + ") = " + Epst);
                GenPolynomial<MOD> cm = Epst.coefficient(e);
                //System.out.println("cm   = " + cm);
                if (cm.isZERO()) {
                    continue;
                }
                List<GenPolynomial<MOD>> Ud = HenselMultUtil.<MOD> liftDiophant(U1, cm, Vh, d, k);
                //System.out.println("Ud = " + Ud);

                mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
                //System.out.println("mon  = " + mon);
                List<GenPolynomial<MOD>> Sd = new ArrayList<GenPolynomial<MOD>>(Ud.size());
                int i = 0;
                List<GenPolynomial<BigInteger>> Si = new ArrayList<GenPolynomial<BigInteger>>(Ud.size());
                for (GenPolynomial<MOD> dd : Ud) {
                    //System.out.println("dd = " + dd);
                    GenPolynomial<MOD> de = dd.extend(pkfac, 0, 0L);
                    GenPolynomial<MOD> dm = de.multiply(mon);
                    Sd.add(dm);
                    de = U.get(i).sum(dm);
                    //System.out.println("de = " + de);
                    U.set(i++, de);
                    GenPolynomial<BigInteger> si = PolyUtil.integerFromModularCoefficients(ifac, de);
                    Si.add(si);
                }
                //System.out.println("Sd   = " + Sd);
                //System.out.println("U    = " + U);
                //System.out.println("Si   = " + Si);

                // compute new error:
                E = ifac.getONE();
                for (GenPolynomial<BigInteger> bi : Si) {
                    E = E.multiply(bi);
                }
                E = Ci.subtract(E);
                //System.out.println("E     = " + E);
                Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
                //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
                logger.info("Ep(" + e + "," + deg + "," + pkfac.nvar + ") = " + Ep);
            }
            Vh.add(v);
            U1 = U;
            if (Pfac.size() > 0) {
                List<GenPolynomial<MOD>> U2 = new ArrayList<GenPolynomial<MOD>>(U.size());
                pkfac = Pfac.get(0);
                for (GenPolynomial<MOD> b : U) {
                    GenPolynomial<MOD> bi = b.extend(pkfac, 0, 0L);
                    U2.add(bi);
                }
                U = U2;
                //System.out.println("U  = " + U);
            }
        }
        if (E.isZERO()) {
            logger.info("liftHensel leaving with zero E");
        }
        return U;
    }


    /**
     * Modular Hensel lifting algorithm. Let p = A_i.ring.coFac.modul() and
     * assume ggt(a,b) == 1 mod p, for a, b in A.
     * @param C GenPolynomial with integer coefficients
     * @param Cp GenPolynomial C mod p^k
     * @param F list of modular GenPolynomials, mod (I_v, p^k )
     * @param V list of integral substitution values
     * @param k desired approximation exponent p^k.
     * @param G list of leading coefficients of the factors of C.
     * @return [g'_1,..., g'_n] with prod_i g'_i = Cp mod p^k.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> liftHensel(
                    GenPolynomial<BigInteger> C, GenPolynomial<MOD> Cp, List<GenPolynomial<MOD>> F,
                    List<BigInteger> V, long k, List<GenPolynomial<BigInteger>> G) 
           throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = Cp.ring;
        long d = C.degree();
        //System.out.println("C = " + C);
        //System.out.println("Cp = " + Cp);
        //System.out.println("G = " + G);

        //GenPolynomial<BigInteger> cd = G.get(0); // 1
        //System.out.println("cd = " + cd + ", ring = " + C.ring);
        //if ( cd.equals(C.ring.univariate(0)) ) {
        //    System.out.println("cd == G[1]");
        //}
        // G mod p^k, in all variables
        GenPolynomialRing<MOD> pkfac1 = new GenPolynomialRing<MOD>(pkfac.coFac, G.get(0).ring);
        List<GenPolynomial<MOD>> Lp = new ArrayList<GenPolynomial<MOD>>(G.size());
        for (GenPolynomial<BigInteger> cd1 : G) {
            GenPolynomial<MOD> cdq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac1, cd1);
            cdq = cdq.extendLower(pkfac, 0, 0L); // reintroduce lower variable
            Lp.add(cdq);
        }
        logger.info("G modulo p^k: " + Lp); // + ", ring = " + pkfac1);

        // prepare stack of polynomial rings, polynomials and evaluated leading coefficients
        List<GenPolynomialRing<MOD>> Pfac = new ArrayList<GenPolynomialRing<MOD>>();
        List<GenPolynomial<MOD>> Ap = new ArrayList<GenPolynomial<MOD>>();
        List<List<GenPolynomial<MOD>>> Gp = new ArrayList<List<GenPolynomial<MOD>>>();
        List<MOD> Vb = new ArrayList<MOD>();
        //MOD v = V.get(0); // fromInteger
        Pfac.add(pkfac);
        Ap.add(Cp);
        Gp.add(Lp);
        GenPolynomialRing<MOD> pf = pkfac;
        //GenPolynomialRing<MOD> pf1 = pkfac1;
        GenPolynomial<MOD> ap = Cp;
        List<GenPolynomial<MOD>> Lpp = Lp;
        for (int j = pkfac.nvar; j > 2; j--) {
            pf = pf.contract(1);
            Pfac.add(0, pf);
            //MOD vp = pkfac.coFac.fromInteger(V.get(pkfac.nvar - j).getSymmetricInteger().getVal());
            MOD vp = pkfac.coFac.fromInteger(V.get(pkfac.nvar - j).getVal());
            //System.out.println("vp     = " + vp);
            Vb.add(vp);
            ap = PolyUtil.<MOD> evaluateMain(pf, ap, vp);
            Ap.add(0, ap);
            List<GenPolynomial<MOD>> Lps = new ArrayList<GenPolynomial<MOD>>(Lpp.size());
            for (GenPolynomial<MOD> qp : Lpp) {
                GenPolynomial<MOD> qpe = PolyUtil.<MOD> evaluateMain(pf, qp, vp);
                Lps.add(qpe);
            }
            //System.out.println("Lps = " + Lps);
            Lpp = Lps;
            Gp.add(0, Lpp);
        }
        Vb.add( pkfac.coFac.fromInteger( V.get(pkfac.nvar - 2).getVal() ) );
        //System.out.println("Pfac   = " + Pfac);
        if (debug) {
            logger.debug("Pfac   = " + Pfac);
        }
        //System.out.println("Ap     = " + Ap);
        //System.out.println("Gp     = " + Gp);
        //System.out.println("Gp[0]  = " + Gp.get(0) + ", Gp[0].ring = " + Gp.get(0).get(0).ring);
        //System.out.println("V      = " + V);
        //System.out.println("Vb     = " + Vb + ", V == Vb: " + V.equals(Vb));

        // check bi-variate base case
        GenPolynomialRing<MOD> pk1fac = F.get(0).ring;
        if (!pkfac.coFac.equals(pk1fac.coFac)) {
            throw new IllegalArgumentException("F.ring != pkfac: " + pk1fac + " != " + pkfac);
        }

        // init recursion
        List<GenPolynomial<MOD>> U = F;
        //logger.info("to lift U = " + U); // + ", U1.ring = " + U1.get(0).ring);
        GenPolynomial<BigInteger> E = C.ring.getZERO();
        List<MOD> Vh = new ArrayList<MOD>();
        List<GenPolynomial<BigInteger>> Si; // = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        MOD v = null;

        while (Pfac.size() > 0) { // loop through stack of polynomial rings
            pkfac = Pfac.remove(0);
            Cp = Ap.remove(0);
            Lpp = Gp.remove(0);
            v = Vb.remove(Vb.size() - 1); // last in stack
            //System.out.println("\npkfac = " + pkfac.toScript() + " ================================== " + v);
            logger.info("stack loop: pkfac = " + pkfac.toScript() + " v = " + v);

            List<GenPolynomial<MOD>> U1 = U;
            logger.info("to lift U1 = " + U1); // + ", U1.ring = " + U1.get(0).ring);
            U = new ArrayList<GenPolynomial<MOD>>(U1.size());

            // update U, replace leading coefficient if required
            int j = 0;
            for (GenPolynomial<MOD> b : U1) {
                //System.out.println("b = " + b + ", b.ring = " + b.ring);
                GenPolynomial<MOD> bi = b.extend(pkfac, 0, 0L);
                GenPolynomial<MOD> li = Lpp.get(j);
                if (!li.isONE()) {
                    //System.out.println("li = " + li + ", li.ring = " + li.ring);
                    //System.out.println("bi = " + bi);
                    GenPolynomialRing<GenPolynomial<MOD>> pkrfac = pkfac.recursive(pkfac.nvar - 1);
                    //System.out.println("pkrfac = " + pkrfac);
                    GenPolynomial<GenPolynomial<MOD>> br = PolyUtil.<MOD> recursive(pkrfac, bi);
                    //System.out.println("br = " + br);
                    GenPolynomial<GenPolynomial<MOD>> bs = PolyUtil.<MOD> switchVariables(br);
                    //System.out.println("bs = " + bs + ", bs.ring = " + bs.ring);

                    GenPolynomial<GenPolynomial<MOD>> lr = PolyUtil.<MOD> recursive(pkrfac, li);
                    //System.out.println("lr = " + lr);
                    GenPolynomial<GenPolynomial<MOD>> ls = PolyUtil.<MOD> switchVariables(lr);
                    //System.out.println("ls = " + ls + ", ls.ring = " + ls.ring);
                    if (!ls.isConstant() && !ls.isZERO()) {
                        throw new RuntimeException("ls not constant " + ls + ", li = " + li);
                    }
                    bs.doPutToMap(bs.leadingExpVector(), ls.leadingBaseCoefficient());
                    //System.out.println("bs = " + bs + ", bs.ring = " + bs.ring);
                    br = PolyUtil.<MOD> switchVariables(bs);
                    //System.out.println("br = " + br);
                    bi = PolyUtil.<MOD> distribute(pkfac, br);
                    //System.out.println("bi = " + bi);
                }
                U.add(bi);
                j++;
            }
            logger.info("U with leading coefficient replaced = " + U); // + ", U.ring = " + U.get(0).ring);

            // (x_n - v)
            GenPolynomial<MOD> mon = pkfac.getONE();
            GenPolynomial<MOD> xv = pkfac.univariate(0, 1);
            xv = xv.subtract(pkfac.fromInteger(v.getSymmetricInteger().getVal()));
            //System.out.println("xv = " + xv);

            long deg = Cp.degree(pkfac.nvar - 1);
            //System.out.println("deg = " + deg + ", degv = " + Cp.degreeVector());

            // convert to integer polynomials
            GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), pkfac);
            //System.out.println("ifac = " + ifac.toScript());
            List<GenPolynomial<BigInteger>> Bi = PolyUtil.integerFromModularCoefficients(ifac, U);
            //System.out.println("Bi = " + Bi);
            GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, Cp);
            //System.out.println("Ci = " + Ci);

            // compute error:
            E = ifac.getONE();
            for (GenPolynomial<BigInteger> bi : Bi) {
                E = E.multiply(bi);
            }
            //System.out.println("E  = " + E);
            E = Ci.subtract(E);
            //System.out.println("E  = " + E);
            GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
            logger.info("Ep(0," + deg + "," + pkfac.nvar + ") = " + Ep);

            GenPolynomialRing<GenPolynomial<MOD>> pkrfac = pkfac.recursive(1);
            GenPolynomialRing<MOD> ckfac = (GenPolynomialRing<MOD>) pkrfac.coFac;
            //System.out.println("pkrfac = " + pkrfac.toScript());

            for (int e = 1; e <= deg && !Ep.isZERO(); e++) {
                //System.out.println("\ne = " + e + " -------------------------------------- " + deg);
                logger.info("approximation loop: e = " + e + " of deg = " + deg);
                GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(pkrfac, Ep);
                //System.out.println("Epr   = " + Epr);
                UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(
                                pkrfac);
                //System.out.println("psfac = " + psfac);
                TaylorFunction<GenPolynomial<MOD>> T = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
                //System.out.println("T     = " + T);
                GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
                //System.out.println("vq    = " + vq + ", Vh = " + Vh);
                UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(T, vq);
                //System.out.println("Epst  = " + Epst);
                logger.info("Epst(" + e + "," + deg + "," + pkfac.nvar + ") = " + Epst);
                GenPolynomial<MOD> cm = Epst.coefficient(e);
                if (cm.isZERO()) {
                    //System.out.println("cm   = " + cm);
                    continue;
                }
                List<GenPolynomial<MOD>> Ud = HenselMultUtil.<MOD> liftDiophant(U1, cm, Vh, d, k);
                //System.out.println("Ud = " + Ud);

                mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
                //System.out.println("mon  = " + mon);
                List<GenPolynomial<MOD>> Sd = new ArrayList<GenPolynomial<MOD>>(Ud.size());
                int i = 0;
                Si = new ArrayList<GenPolynomial<BigInteger>>(Ud.size());
                for (GenPolynomial<MOD> dd : Ud) {
                    //System.out.println("dd = " + dd);
                    GenPolynomial<MOD> de = dd.extend(pkfac, 0, 0L);
                    GenPolynomial<MOD> dm = de.multiply(mon);
                    Sd.add(dm);
                    de = U.get(i).sum(dm);
                    //System.out.println("de = " + de);
                    U.set(i++, de);
                    GenPolynomial<BigInteger> si = PolyUtil.integerFromModularCoefficients(ifac, de);
                    Si.add(si);
                }
                //System.out.println("Sd   = " + Sd);
                //System.out.println("U    = " + U + ", U.ring = " + U.get(0).ring);
                //System.out.println("Si   = " + Si);

                // compute new error:
                E = ifac.getONE();
                for (GenPolynomial<BigInteger> bi : Si) {
                    E = E.multiply(bi);
                }
                E = Ci.subtract(E);
                //System.out.println("E = " + E);
                Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac, E);
                //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
                logger.info("Ep(" + e + "," + deg + "," + pkfac.nvar + ") = " + Ep);
            }
            Vh.add(v);
            GenPolynomial<MOD> Uf = U.get(0).ring.getONE();
            for (GenPolynomial<MOD> Upp : U) {
                Uf = Uf.multiply(Upp);
            }
            if (false && !Cp.leadingExpVector().equals(Uf.leadingExpVector())) { // not meanigfull test
                System.out.println("\nU    = " + U);
                System.out.println("Cp   = " + Cp);
                System.out.println("Uf   = " + Uf);
                //System.out.println("Cp.ring = " + Cp.ring.toScript() + ", Uf.ring = " + Uf.ring.toScript() + "\n");
                System.out.println("");
                //throw new NoLiftingException("no factorization, Cp != Uf");
            }
        }
        if (E.isZERO()) {
            logger.info("liftHensel leaving with zero E, Ep");
        }
        if (false && debug) {
            // remove normalization required ??
            GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getImplementation(new BigInteger());
            List<GenPolynomial<BigInteger>> Fii = new ArrayList<GenPolynomial<BigInteger>>(U.size());
            for (GenPolynomial<BigInteger> bi : Si) {
                GenPolynomial<BigInteger> ci = ufd.content(bi); //ufd.primitivePart(bi); // ??
                if (!ci.isONE()) {
                    System.out.println("bi = " + bi + ", cont(bi) = " + ci);
                }
                //Fii.add(ci);
            }
            //Si = Fii;
            //System.out.println("Si  = " + Si);
        }
        logger.info("multivariate lift: U = " + U + ", of " + F);
        return U;
    }


    /**
     * Modular Hensel full lifting algorithm. Let p = A_i.ring.coFac.modul() and
     * assume ggt(a,b) == 1 mod p, for a, b in A.
     * @param C GenPolynomial with integer coefficients
     * @param F list of modular GenPolynomials, mod (I_v, p )
     * @param V list of integer substitution values 
     * @param k desired approximation exponent p^k.
     * @param G = [g_1,...,g_n] list of factors of leading coefficients.
     * @return [c_1,..., c_n] with prod_i c_i = C mod p^k.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> liftHenselFull(
                    GenPolynomial<BigInteger> C, List<GenPolynomial<MOD>> F, List<BigInteger> V, long k,
                    List<GenPolynomial<BigInteger>> G) throws NoLiftingException {
        if (F == null || F.size() == 0) {
            return new ArrayList<GenPolynomial<MOD>>();
        }
        GenPolynomialRing<MOD> pkfac = F.get(0).ring;
        //long d = C.degree();
        // setup q = p^k
        RingFactory<MOD> cfac = pkfac.coFac;
        ModularRingFactory<MOD> pcfac = (ModularRingFactory<MOD>) cfac;
        //System.out.println("pcfac = " + pcfac);
        BigInteger p = pcfac.getIntegerModul();
        BigInteger q = Power.positivePower(p, k);
        ModularRingFactory<MOD> mcfac;
        if (ModLongRing.MAX_LONG.compareTo(q.getVal()) > 0) {
            mcfac = (ModularRingFactory) new ModLongRing(q.getVal());
        } else {
            mcfac = (ModularRingFactory) new ModIntegerRing(q.getVal());
        }
        //System.out.println("mcfac = " + mcfac);

        // convert C from Z[...] to Z_q[...]
        GenPolynomialRing<MOD> qcfac = new GenPolynomialRing<MOD>(mcfac, C.ring);
        GenPolynomial<MOD> Cq = PolyUtil.<MOD> fromIntegerCoefficients(qcfac, C);
        //System.out.println("C  = " + C);
        //System.out.println("Cq = " + Cq);

        // convert g_i from Z[...] to Z_q[...]
        GenPolynomialRing<MOD> gcfac = new GenPolynomialRing<MOD>(mcfac, G.get(0).ring);
        List<GenPolynomial<MOD>> GQ = new ArrayList<GenPolynomial<MOD>>();
        boolean allOnes = true;
        for (GenPolynomial<BigInteger> g : G) {
            if (!g.isONE()) {
                allOnes = false;
            }
            GenPolynomial<MOD> gq = PolyUtil.<MOD> fromIntegerCoefficients(gcfac, g);
            GQ.add(gq);
        }
        //System.out.println("G  = " + G);
        //System.out.println("GQ = " + GQ);

        // evaluate C to Z_q[x]
        GenPolynomialRing<MOD> pf = qcfac;
        GenPolynomial<MOD> ap = Cq;
        for (int j = C.ring.nvar; j > 1; j--) {
            pf = pf.contract(1);
            //MOD vp = mcfac.fromInteger(V.get(C.ring.nvar - j).getSymmetricInteger().getVal());
            MOD vp = mcfac.fromInteger(V.get(C.ring.nvar - j).getVal());
            //System.out.println("vp     = " + vp);
            ap = PolyUtil.<MOD> evaluateMain(pf, ap, vp);
            //System.out.println("ap     = " + ap);
        }
        GenPolynomial<MOD> Cq1 = ap;
        //System.out.println("Cq1 = " + Cq1);
        if (Cq1.isZERO()) {
            throw new NoLiftingException("C mod (I, p^k) == 0: " + C);
        }
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), pf);
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, Cq1);
        //System.out.println("Ci  = " + Ci);
        GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getImplementation(new BigInteger());
        Ci = Ci.abs();
        BigInteger cCi = ufd.baseContent(Ci);
        Ci = Ci.divide(cCi);
        //System.out.println("cCi = " + cCi);
        //System.out.println("Ci  = " + Ci);
        ////System.out.println("F.fac = " + F.get(0).ring);

        // evaluate G to Z_q
        List<GenPolynomial<MOD>> GP = new ArrayList<GenPolynomial<MOD>>();
        for (GenPolynomial<MOD> gq : GQ) {
            GenPolynomialRing<MOD> gf = gcfac;
            GenPolynomial<MOD> gp = gq;
            for (int j = gcfac.nvar; j > 1; j--) {
                gf = gf.contract(1);
                //MOD vp = mcfac.fromInteger(V.get(gcfac.nvar - j).getSymmetricInteger().getVal());
                MOD vp = mcfac.fromInteger(V.get(gcfac.nvar - j).getVal());
                //System.out.println("vp     = " + vp);
                gp = PolyUtil.<MOD> evaluateMain(gf, gp, vp);
                //System.out.println("gp     = " + gp);
            }
            GP.add(gp);
        }
        //System.out.println("GP = " + GP); // + ", GP.ring = " + GP.get(0).ring);

        // leading coefficient for recursion base, for Cq1 and list GP 
        BigInteger gi0 = Ci.leadingBaseCoefficient(); // gq0.getSymmetricInteger();
        //System.out.println("gi0 = " + gi0);

        // lift F to Z_{p^k}[x]
        //System.out.println("Ci = " + Ci + ", F = " + F + ", k = " + k + ", p = " + F.get(0).ring + ", gi0 = " + gi0);
        List<GenPolynomial<MOD>> U1 = null;
        if (gi0.isONE()) {
            U1 = HenselUtil.<MOD> liftHenselMonic(Ci, F, k);
        } else {
            U1 = HenselUtil.<MOD> liftHensel(Ci, F, k, gi0); // GI0 TODO ??
        }
        logger.info("univariate lift: Ci = " + Ci + ", F = " + F + ", U1 = " + U1);
        //System.out.println("U1.fac = " + U1.get(0).ring);

        // adjust leading coefficients of U1 with F
        List<GenPolynomial<BigInteger>> U1i = PolyUtil.<MOD> integerFromModularCoefficients(Ci.ring, U1);
        //System.out.println("U1i = " + U1i);
        boolean t = HenselUtil.isHenselLift(Ci, q, p, U1i);
        //System.out.println("isLift(U1) = " + t);
        if (!t) {
            //System.out.println("NoLiftingException, Ci = " + Ci + ", U1i = " + U1i);
            throw new NoLiftingException("Ci = " + Ci + ", U1i = " + U1i);
        }
        MOD cC = mcfac.fromInteger(cCi.getVal());
        List<GenPolynomial<MOD>> U1f = PolyUtil.<MOD> fromIntegerCoefficients(F.get(0).ring, U1i);
        //System.out.println("U1f = " + U1f);
        List<GenPolynomial<MOD>> U1s = new ArrayList<GenPolynomial<MOD>>(U1.size());
        int j = 0;
        int s = 0;
        for (GenPolynomial<MOD> u : U1) {
            GenPolynomial<MOD> uf = U1f.get(j);
            GenPolynomial<MOD> f = F.get(j);
            GenPolynomial<BigInteger> ui = U1i.get(j);
            GenPolynomial<BigInteger> gi = G.get(j);
            if (ui.signum() != gi.signum()) {
                //System.out.println("ui = " + ui + ", gi = " + gi);
                u = u.negate();
                uf = uf.negate();
                s++;
            }
            j++;
            if (uf.isConstant()) {
                //System.out.println("u   = " + u);
                u = u.monic();
                //System.out.println("u  = " + u);
                u = u.multiply(cC);
                cC = cC.divide(cC);
                //System.out.println("u   = " + u);
            } else {
                MOD x = f.leadingBaseCoefficient().divide(uf.leadingBaseCoefficient());
                //System.out.println("x   = " + x + ", xi = " + x.getSymmetricInteger());
                if (!x.isONE()) {
                    MOD xq = mcfac.fromInteger(x.getSymmetricInteger().getVal());
                    //System.out.println("xq  = " + xq);
                    u = u.multiply(xq);
                    cC = cC.divide(xq);
                    //System.out.println("cC  = " + cC);
                }
            }
            U1s.add(u);
        }
        //if ( s % 2 != 0 || !cC.isONE()) {
        if (!cC.isONE()) {
            throw new NoLiftingException("s = " + s + ", Ci = " + Ci + ", U1i = " + U1i + ", cC = " + cC);
        }
        U1 = U1s;
        U1i = PolyUtil.<MOD> integerFromModularCoefficients(Ci.ring, U1);
        //System.out.println("U1i = " + U1i);
        U1f = PolyUtil.<MOD> fromIntegerCoefficients(F.get(0).ring, U1i);
        if (!F.equals(U1f)) { // evtl loop until reached
            System.out.println("F   = " + F);
            System.out.println("U1f = " + U1f);
            throw new NoLiftingException("F = " + F + ", U1f = " + U1f);
        }
        logger.info("multivariate lift: U1 = " + U1);

        // lift U to Z_{p^k}[x,...]
        //System.out.println("C = " + C + ", U1 = " + U1 + ", V = " + V + ", k = " + k + ", q = " + U1.get(0).ring + ", G = " + G);
        List<GenPolynomial<MOD>> U = null;
        if (allOnes) {
            U = HenselMultUtil.<MOD> liftHenselMonic(C, Cq, U1, V, k);
        } else {
            U = HenselMultUtil.<MOD> liftHensel(C, Cq, U1, V, k, G);
        }
        logger.info("multivariate lift: C = " + C + ", U1 = " + U1 + ", U = " + U);
        //System.out.println("U  = " + U);
        //System.out.println("U.fac = " + U.get(0).ring);
        return U;
    }

}
