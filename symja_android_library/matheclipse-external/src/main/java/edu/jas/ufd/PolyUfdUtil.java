/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrderByName;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.ListUtil;


/**
 * Polynomial ufd utilities. For example conversion between different
 * representations and Kronecker substitution.
 * @author Heinz Kredel
 */

public class PolyUfdUtil {


    private static final Logger logger = LogManager.getLogger(PolyUfdUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Factors of Quotient rational function.
     * @param A rational function to be factored.
     * @return list of irreducible rational function parts.
     */
    public static <C extends GcdRingElem<C>> SortedMap<Quotient<C>, Long> factors(Quotient<C> A) {
        SortedMap<Quotient<C>, Long> factors = new TreeMap<Quotient<C>, Long>();
        if (A == null || A.isZERO()) {
            return factors;
        }
        if (A.abs().isONE()) {
            factors.put(A, 1L);
            return factors;
        }
        QuotientRing<C> qfac = A.ring;
        GenPolynomialRing<C> fac = qfac.ring;
        FactorAbstract<C> eng = FactorFactory.<C> getImplementation(fac.coFac);
        GenPolynomial<C> n = A.num;
        SortedMap<GenPolynomial<C>, Long> numfactors = eng.factors(n);
        for (Map.Entry<GenPolynomial<C>, Long> me : numfactors.entrySet()) {
            GenPolynomial<C> f = me.getKey();
            Long e = me.getValue();
            Quotient<C> q = new Quotient<C>(qfac, f);
            factors.put(q, e);
        }
        GenPolynomial<C> d = A.den;
        if (d.isONE()) {
            return factors;
        }
        GenPolynomial<C> one = fac.getONE();
        SortedMap<GenPolynomial<C>, Long> denfactors = eng.factors(d);
        for (Map.Entry<GenPolynomial<C>, Long> me : denfactors.entrySet()) {
            GenPolynomial<C> f = me.getKey();
            Long e = me.getValue();
            Quotient<C> q = new Quotient<C>(qfac, one, f);
            factors.put(q, e);
        }
        return factors;
    }


    /**
     * Quotient is (squarefree) factorization.
     * @param P Quotient.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public static <C extends GcdRingElem<C>> boolean isFactorization(Quotient<C> P,
                    SortedMap<Quotient<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        Quotient<C> t = P.ring.getONE();
        for (Map.Entry<Quotient<C>, Long> me : F.entrySet()) {
            Quotient<C> f = me.getKey();
            Long E = me.getValue();
            long e = E.longValue();
            Quotient<C> g = f.power(e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if (f) {
                return f;
            }
            logger.info("no factorization(map): F = " + F + ", P = " + P + ", t = " + t);
        }
        return f;
    }


    /**
     * Integral polynomial from rational function coefficients. Represent as
     * polynomial with integral polynomial coefficients by multiplication with
     * the lcm of the numerators of the rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with rational function coefficients to be converted.
     * @return polynomial with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> integralFromQuotientCoefficients(
                    GenPolynomialRing<GenPolynomial<C>> fac, GenPolynomial<Quotient<C>> A) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        GenPolynomial<C> c = null;
        GenPolynomial<C> d;
        GenPolynomial<C> x;
        GreatestCommonDivisor<C> ufd = new GreatestCommonDivisorSubres<C>();
        int s = 0;
        // lcm of denominators
        for (Quotient<C> y : A.getMap().values()) {
            x = y.den;
            // c = lcm(c,x)
            if (c == null) {
                c = x;
                s = x.signum();
            } else {
                d = ufd.gcd(c, x);
                c = c.multiply(x.divide(d));
            }
        }
        if (s < 0) {
            c = c.negate();
        }
        for (Map.Entry<ExpVector, Quotient<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            Quotient<C> a = y.getValue();
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide(a.den);
            GenPolynomial<C> p = a.num.multiply(b);
            //B = B.sum( p, e ); // inefficient
            B.doPutToMap(e, p);
        }
        return B;
    }


    /**
     * Integral polynomial from rational function coefficients. Represent as
     * polynomial with integral polynomial coefficients by multiplication with
     * the lcm of the numerators of the rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomial with rational function coefficients to be
     *            converted.
     * @return list of polynomials with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<GenPolynomial<C>>> integralFromQuotientCoefficients(
                    GenPolynomialRing<GenPolynomial<C>> fac, Collection<GenPolynomial<Quotient<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenPolynomial<GenPolynomial<C>>> list = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L.size());
        for (GenPolynomial<Quotient<C>> p : L) {
            list.add(integralFromQuotientCoefficients(fac, p));
        }
        return list;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral polynomial coefficients to be
     *            converted.
     * @return polynomial with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<Quotient<C>> quotientFromIntegralCoefficients(
                    GenPolynomialRing<Quotient<C>> fac, GenPolynomial<GenPolynomial<C>> A) {
        GenPolynomial<Quotient<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<Quotient<C>> cfac = fac.coFac;
        QuotientRing<C> qfac = (QuotientRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            Quotient<C> p = new Quotient<C>(qfac, a); // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral polynomial coefficients to be
     *            converted.
     * @return list of polynomials with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<Quotient<C>>> quotientFromIntegralCoefficients(
                    GenPolynomialRing<Quotient<C>> fac, Collection<GenPolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenPolynomial<Quotient<C>>> list = new ArrayList<GenPolynomial<Quotient<C>>>(L.size());
        for (GenPolynomial<GenPolynomial<C>> p : L) {
            list.add(quotientFromIntegralCoefficients(fac, p));
        }
        return list;
    }


    /**
     * From BigInteger coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; coefficients to
     *            be converted.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> fromIntegerCoefficients(
                    GenPolynomialRing<GenPolynomial<C>> fac, GenPolynomial<GenPolynomial<BigInteger>> A) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<GenPolynomial<C>> cfac = fac.coFac;
        GenPolynomialRing<C> rfac = (GenPolynomialRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<BigInteger>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<BigInteger> a = y.getValue();
            GenPolynomial<C> p = PolyUtil.<C> fromIntegerCoefficients(rfac, a);
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * From BigInteger coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param L polynomial list with GenPolynomial&lt;BigInteger&gt;
     *            coefficients to be converted.
     * @return polynomial list with polynomials with type GenPolynomial&lt;C&gt;
     *         coefficients.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<GenPolynomial<C>>> fromIntegerCoefficients(
                    GenPolynomialRing<GenPolynomial<C>> fac,
                    List<GenPolynomial<GenPolynomial<BigInteger>>> L) {
        List<GenPolynomial<GenPolynomial<C>>> K = null;
        if (L == null) {
            return K;
        }
        K = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L.size());
        if (L.size() == 0) {
            return K;
        }
        for (GenPolynomial<GenPolynomial<BigInteger>> a : L) {
            GenPolynomial<GenPolynomial<C>> b = fromIntegerCoefficients(fac, a);
            K.add(b);
        }
        return K;
    }


    //------------------------------


    /**
     * BigInteger from BigRational coefficients. Represent as polynomial with
     * type GenPolynomial&lt;BigInteger&gt; coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigRational&gt; coefficients to
     *            be converted.
     * @return polynomial with type GenPolynomial&lt;BigInteger&gt;
     *         coefficients.
     */
    public static GenPolynomial<GenPolynomial<BigInteger>> integerFromRationalCoefficients(
                    GenPolynomialRing<GenPolynomial<BigInteger>> fac,
                    GenPolynomial<GenPolynomial<BigRational>> A) {
        GenPolynomial<GenPolynomial<BigInteger>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        java.math.BigInteger gcd = null;
        java.math.BigInteger lcm = null;
        int sLCM = 0;
        int sGCD = 0;
        // lcm of all denominators
        for (GenPolynomial<BigRational> av : A.getMap().values()) {
            for (BigRational y : av.getMap().values()) {
                java.math.BigInteger numerator = y.numerator();
                java.math.BigInteger denominator = y.denominator();
                // lcm = lcm(lcm,x)
                if (lcm == null) {
                    lcm = denominator;
                    sLCM = denominator.signum();
                } else {
                    java.math.BigInteger d = lcm.gcd(denominator);
                    lcm = lcm.multiply(denominator.divide(d));
                }
                // gcd = gcd(gcd,x)
                if (gcd == null) {
                    gcd = numerator;
                    sGCD = numerator.signum();
                } else {
                    gcd = gcd.gcd(numerator);
                }
            }
            //System.out.println("gcd = " + gcd + ", lcm = " + lcm);
        }
        if (sLCM < 0) {
            lcm = lcm.negate();
        }
        if (sGCD < 0) {
            gcd = gcd.negate();
        }
        //System.out.println("gcd** = " + gcd + ", lcm = " + lcm);
        RingFactory<GenPolynomial<BigInteger>> cfac = fac.coFac;
        GenPolynomialRing<BigInteger> rfac = (GenPolynomialRing<BigInteger>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<BigRational>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<BigRational> a = y.getValue();
            // common denominator over all coefficients
            GenPolynomial<BigInteger> p = PolyUtil.integerFromRationalCoefficients(rfac, gcd, lcm, a);
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * BigInteger from BigRational coefficients. Represent as polynomial with
     * type GenPolynomial&lt;BigInteger&gt; coefficients.
     * @param fac result polynomial factory.
     * @param L polynomial list with GenPolynomial&lt;BigRational&gt;
     *            coefficients to be converted.
     * @return polynomial list with polynomials with type
     *         GenPolynomial&lt;BigInteger&gt; coefficients.
     */
    public static List<GenPolynomial<GenPolynomial<BigInteger>>> integerFromRationalCoefficients(
                    GenPolynomialRing<GenPolynomial<BigInteger>> fac,
                    List<GenPolynomial<GenPolynomial<BigRational>>> L) {
        List<GenPolynomial<GenPolynomial<BigInteger>>> K = null;
        if (L == null) {
            return K;
        }
        K = new ArrayList<GenPolynomial<GenPolynomial<BigInteger>>>(L.size());
        if (L.isEmpty()) {
            return K;
        }
        for (GenPolynomial<GenPolynomial<BigRational>> a : L) {
            GenPolynomial<GenPolynomial<BigInteger>> b = integerFromRationalCoefficients(fac, a);
            K.add(b);
        }
        return K;
    }


    /**
     * Introduce lower variable. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients.
     * @param rfac result polynomial factory.
     * @param A polynomial to be extended.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> introduceLowerVariable(
                    GenPolynomialRing<GenPolynomial<C>> rfac, GenPolynomial<C> A) {
        if (A == null || rfac == null) {
            return null;
        }
        GenPolynomial<GenPolynomial<C>> Pc = rfac.getONE().multiply(A);
        if (Pc.isZERO()) {
            return Pc;
        }
        Pc = PolyUtil.<C> switchVariables(Pc);
        return Pc;
    }


    /**
     * From AlgebraicNumber coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param rfac result polynomial factory.
     * @param A polynomial with AlgebraicNumber coefficients to be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> substituteFromAlgebraicCoefficients(
                    GenPolynomialRing<GenPolynomial<C>> rfac, GenPolynomial<AlgebraicNumber<C>> A, long k) {
        if (A == null || rfac == null) {
            return null;
        }
        if (A.isZERO()) {
            return rfac.getZERO();
        }
        // setup x - k alpha
        GenPolynomialRing<AlgebraicNumber<C>> apfac = A.ring;
        GenPolynomial<AlgebraicNumber<C>> x = apfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) A.ring.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.subtract(ka.multiply(alpha)); // x - k alpha
        //System.out.println("x - k alpha = " + s);
        //System.out.println("s.ring = " + s.ring.toScript());
        if (debug) {
            logger.info("x - k alpha: " + s);
        }
        // substitute, convert and switch
        //System.out.println("Asubs = " + A);
        GenPolynomial<AlgebraicNumber<C>> B;
        if (s.ring.nvar <= 1) {
            B = PolyUtil.<AlgebraicNumber<C>> substituteMain(A, s);
        } else {
            B = PolyUtil.<AlgebraicNumber<C>> substituteUnivariateMult(A, s);
        }
        //System.out.println("Bsubs = " + B);
        GenPolynomial<GenPolynomial<C>> Pc = PolyUtil.<C> fromAlgebraicCoefficients(rfac, B); // Q[alpha][x]
        //System.out.println("Pc[a,x] = " + Pc);
        Pc = PolyUtil.<C> switchVariables(Pc); // Q[x][alpha]
        //System.out.println("Pc[x,a] = " + Pc);
        return Pc;
    }


    /**
     * Convert to AlgebraicNumber coefficients. Represent as polynomial with
     * AlgebraicNumber<C> coefficients, C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; coefficients to
     *            be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<AlgebraicNumber<C>> substituteConvertToAlgebraicCoefficients(
                    GenPolynomialRing<AlgebraicNumber<C>> pfac, GenPolynomial<C> A, long k) {
        if (A == null || pfac == null) {
            return null;
        }
        if (A.isZERO()) {
            return pfac.getZERO();
        }
        // convert to Q(alpha)[x]
        GenPolynomial<AlgebraicNumber<C>> B = PolyUtil.<C> convertToAlgebraicCoefficients(pfac, A);
        // setup x .+. k alpha for back substitution
        GenPolynomial<AlgebraicNumber<C>> x = pfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.sum(ka.multiply(alpha)); // x + k alpha
        // substitute
        //System.out.println("s.ring = " + s.ring.toScript());
        GenPolynomial<AlgebraicNumber<C>> N;
        if (s.ring.nvar <= 1) {
            N = PolyUtil.<AlgebraicNumber<C>> substituteMain(B, s);
        } else {
            N = PolyUtil.<AlgebraicNumber<C>> substituteUnivariateMult(B, s);
        }
        return N;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients.
     * @param A uni or multivariate polynomial from
     *            GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @param k for (y - k x) substitution.
     * @return norm(A) = res_x(A(x,y),m(x)) in GenPolynomialRing&lt;C&gt;.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> norm(GenPolynomial<AlgebraicNumber<C>> A,
                    long k) {
        if (A == null) {
            return null;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = A.ring; // Q(alpha)[x]
        //if (pfac.nvar > 1) {
        //    throw new IllegalArgumentException("only for univariate polynomials");
        //}
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        if (A.isZERO()) {
            return cfac.getZERO();
        }
        AlgebraicNumber<C> ldcf = A.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            A = A.monic();
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pfac);
        //System.out.println("rfac = " + rfac.toScript());

        // transform minimal polynomial to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = PolyUfdUtil.<C> introduceLowerVariable(rfac, agen);

        // transform to bi-variate polynomial, 
        // switching varaible sequence from Q[alpha][x] to Q[X][alpha]
        GenPolynomial<GenPolynomial<C>> Pc = PolyUfdUtil.<C> substituteFromAlgebraicCoefficients(rfac, A, k);
        Pc = PolyUtil.<C> monic(Pc);
        //System.out.println("Pc = " + Pc.toScript() + " :: " + Pc.ring.toScript());

        GreatestCommonDivisorSubres<C> engine = new GreatestCommonDivisorSubres<C>( /*cfac.coFac*/);
        // = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( cfac.coFac );

        GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveUnivariateResultant(Pc, Ac);
        //System.out.println("Rc = " + Rc.toScript());
        GenPolynomial<C> res = Rc.leadingBaseCoefficient();
        res = res.monic();
        return res;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients.
     * @param A polynomial from GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return norm(A) = resultant_x( A(x,y), m(x) ) in K[y].
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> norm(GenPolynomial<AlgebraicNumber<C>> A) {
        return norm(A, 0L);
    }


    /**
     * Ensure that the field property is determined. Checks if modul is
     * irreducible and modifies the algebraic number ring.
     * @param afac algebraic number ring.
     */
    public static <C extends GcdRingElem<C>> void ensureFieldProperty(AlgebraicNumberRing<C> afac) {
        if (afac.getField() != -1) {
            return;
        }
        if (!afac.ring.coFac.isField()) {
            afac.setField(false);
            return;
        }
        Factorization<C> mf = FactorFactory.<C> getImplementation(afac.ring);
        if (mf.isIrreducible(afac.modul)) {
            afac.setField(true);
        } else {
            afac.setField(false);
        }
    }


    /**
     * Construct a random irreducible univariate polynomial of degree d.
     * @param cfac coefficient polynomial ring.
     * @param degree of random polynomial.
     * @return irreducible univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> randomIrreduciblePolynomial(RingFactory<C> cfac,
                    int degree) {
        if (!cfac.isField()) {
            throw new IllegalArgumentException("coefficient ring must be a field " + cfac);
        }
        GenPolynomialRing<C> ring = new GenPolynomialRing<C>(cfac, 1, TermOrderByName.INVLEX);
        return randomIrreduciblePolynomial(ring, degree);
    }


    /**
     * Construct a random irreducible univariate polynomial of degree d.
     * @param ring coefficient ring.
     * @param degree of random polynomial.
     * @return irreducible univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> randomIrreduciblePolynomial(
                    GenPolynomialRing<C> ring, int degree) {
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficient ring must be a field " + ring.coFac);
        }
        Factorization<C> eng = FactorFactory.<C> getImplementation(ring);
        GenPolynomial<C> mod = ring.getZERO();
        int k = ring.coFac.characteristic().bitLength(); // log
        if (k < 3) {
            k = 7;
        }
        int l = degree / 2 + 2;
        int d = degree + 1;
        float q = 0.55f;
        for (;;) {
            mod = ring.random(k, l, d, q).monic();
            if (mod.degree() != degree) {
                mod = mod.sum(ring.univariate(0, degree));
            }
            if (mod.trailingBaseCoefficient().isZERO()) {
                mod = mod.sum(ring.getONE());
            }
            //System.out.println("algebriacNumberField: mod = " + mod + ", k = " + k);
            if (eng.isIrreducible(mod)) {
                break;
            }
        }
        return mod;
    }


    /**
     * Construct an algebraic number field of degree d. Uses a random
     * irreducible polynomial of degree d as modulus of the algebraic number
     * ring.
     * @param cfac coefficient ring.
     * @param degree of random polynomial.
     * @return algebraic number field.
     */
    public static <C extends GcdRingElem<C>> AlgebraicNumberRing<C> algebraicNumberField(RingFactory<C> cfac,
                    int degree) {
        GenPolynomial<C> mod = randomIrreduciblePolynomial(cfac, degree);
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(mod, true);
        return afac;
    }


    /**
     * Construct an algebraic number field of degree d. Uses a random
     * irreducible polynomial of degree d as modulus of the algebraic number
     * ring.
     * @param ring coefficient polynomial ring.
     * @param degree of random polynomial.
     * @return algebraic number field.
     */
    public static <C extends GcdRingElem<C>> AlgebraicNumberRing<C> algebraicNumberField(
                    GenPolynomialRing<C> ring, int degree) {
        GenPolynomial<C> mod = randomIrreduciblePolynomial(ring, degree);
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(mod, true);
        return afac;
    }


    /**
     * Construct Berlekamp Q matrix.
     * @param A univariate modular polynomial.
     * @return Q matrix.
     */
    public static <C extends GcdRingElem<C>> ArrayList<ArrayList<C>> constructQmatrix(GenPolynomial<C> A) {
        ArrayList<ArrayList<C>> Q = new ArrayList<ArrayList<C>>();
        if (A == null || A.isZERO()) {
            return Q;
        }
        GenPolynomialRing<C> pfac = A.ring;
        //System.out.println("pfac = " + pfac.toScript());
        java.math.BigInteger q = pfac.coFac.characteristic(); //.longValueExact();
        int lq = q.bitLength(); //Power.logarithm(2, q);
        if (pfac.coFac instanceof AlgebraicNumberRing) {
            lq = (int) ((AlgebraicNumberRing) pfac.coFac).extensionDegree();
            q = q.pow(lq); //Power.power(q, lq);
        }
        logger.info("Q matrix for cfac = " + q);
        long d = A.degree(0);
        GenPolynomial<C> x = pfac.univariate(0);
        //System.out.println("x = " + x.toScript());
        GenPolynomial<C> r = pfac.getONE();
        //System.out.println("r = " + r.toScript());
        List<GenPolynomial<C>> Qp = new ArrayList<GenPolynomial<C>>();
        Qp.add(r);
        GenPolynomial<C> pow = Power.<GenPolynomial<C>> modPositivePower(x, q, A);
        //System.out.println("pow = " + pow.toScript());
        Qp.add(pow);
        r = pow;
        for (int i = 2; i < d; i++) {
            r = r.multiply(pow).remainder(A);
            Qp.add(r);
        }
        //System.out.println("Qp = " + Qp);
        UnivPowerSeriesRing<C> psfac = new UnivPowerSeriesRing<C>(pfac);
        //System.out.println("psfac = " + psfac.toScript());
        for (GenPolynomial<C> p : Qp) {
            UnivPowerSeries<C> ps = psfac.fromPolynomial(p);
            //System.out.println("ps = " + ps.toScript());
            ArrayList<C> pr = new ArrayList<C>();
            for (int i = 0; i < d; i++) {
                C c = ps.coefficient(i);
                pr.add(c);
            }
            Q.add(pr);
        }
        //System.out.println("Q = " + Q);
        return Q;
    }


    /**
     * Polynomial suitable evaluation points. deg(B) = deg(A(x_1,...)) and B is
     * also squarefree.
     * @param A squarefree polynomial in r variables.
     * @return L list of evaluation points and a squarefree univariate
     *         Polynomial B = A(x_1,L_1,...L_{r-2}).
     * @see "sacring.SACPFAC.mi#IPCEVP from SAC2/MAS"
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> EvalPoints<C> evaluationPoints(GenPolynomial<C> A) {
        ArrayList<C> L = new ArrayList<C>();
        if (A == null) {
            throw new IllegalArgumentException("A is null");
        }
        GenPolynomialRing<C> pfac = A.ring;
        if (pfac.nvar <= 1) {
            return new EvalPoints<C>(A, A, L);
        }
        GenPolynomial<C> B = A;
        if (A.isZERO() || A.isONE()) {
            return new EvalPoints<C>(A, A, L);
        }
        SquarefreeAbstract<C> sengine = SquarefreeFactory.<C> getImplementation(pfac.coFac);
        //long dega = A.degree(0);
        GenPolynomialRing<C> rpfac = pfac;
        GenPolynomial<C> ape = A;
        C one = pfac.coFac.getONE();
        C ll = pfac.coFac.getZERO();
        for (int i = pfac.nvar; i > 1; i--) {
            //System.out.println("rpfac = " + rpfac.toScript());
            GenPolynomialRing<GenPolynomial<C>> rfac = rpfac.recursive(1);
            GenPolynomialRing<C> cpfac = (GenPolynomialRing) rfac.coFac;
            GenPolynomial<GenPolynomial<C>> ap = PolyUtil.<C> recursive(rfac, ape);
            //System.out.println("ap = " + ap);
            long degd = ape.degree(rpfac.nvar - 2);
            boolean unlucky = true;
            long s = 0;
            C Vi = null;
            while (unlucky) {
                //System.out.println("ll = " + ll);
                Vi = ll;
                if (ll.signum() > 0) {
                    ll = ll.negate();
                } else {
                    ll = one.subtract(ll);
                }
                ape = PolyUtil.<C> evaluateMainRecursive(cpfac, ap, Vi);
                //System.out.println("ape = " + ape);
                //long degp = ape.degree(0);
                long degc = ape.degree(cpfac.nvar - 1);
                //System.out.println("degc = " + degc + ", degd = " + degd);
                if (degd != degc) {
                    continue;
                }
                if (!sengine.isSquarefree(ape)) {
                    //System.out.println("not squarefree");
                    continue;
                }
                //System.out.println("isSquarefree");
                //ap = ape;
                unlucky = false;
                if (s++ > 300l) {
                    throw new RuntimeException(s + " evaluations not squarefree: " + Vi + ", " + ape);
                    //break;
                }
            }
            L.add(Vi);
            rpfac = cpfac;
        }
        B = ape;
        return new EvalPoints<C>(A, B, L);
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct a
     * univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> substituteKronecker(GenPolynomial<C> A) {
        if (A == null) {
            return A;
        }
        long d = A.degree() + 1L;
        return substituteKronecker(A, d);
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct a
     * univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> substituteKronecker(GenPolynomial<C> A,
                    long d) {
        if (A == null) {
            return A;
        }
        RingFactory<C> cfac = A.ring.coFac;
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac, 1);
        GenPolynomial<C> B = ufac.getZERO().copy();
        if (A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = 0L;
            long h = 1L;
            for (int i = 0; i < e.length(); i++) {
                long j = e.getVal(i) * h;
                f += j;
                h *= d;
            }
            ExpVector g = ExpVector.create(1, 0, f);
            B.doPutToMap(g, a);
        }
        return B;
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct
     * univariate polynomials.
     * @param A list of polynomials to be converted.
     * @return a list of univariate polynomials.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> substituteKronecker(
                    List<GenPolynomial<C>> A, int d) {
        if (A == null || A.get(0) == null) {
            return null;
        }
        return ListUtil.<GenPolynomial<C>, GenPolynomial<C>> map(A, new SubstKronecker<C>(d));
    }


    /**
     * Kronecker back substitution. Substitute x**d**(i-1) to x_i to construct a
     * multivariate polynomial.
     * @param A polynomial to be converted.
     * @param fac result polynomial factory.
     * @return a multivariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> backSubstituteKronecker(
                    GenPolynomialRing<C> fac, GenPolynomial<C> A, long d) {
        if (A == null) {
            return A;
        }
        if (fac == null) {
            throw new IllegalArgumentException("null factory not allowed ");
        }
        int n = fac.nvar;
        GenPolynomial<C> B = fac.getZERO().copy();
        if (A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = e.getVal(0);
            ExpVector g = ExpVector.create(n);
            for (int i = 0; i < n; i++) {
                long j = f % d;
                f /= d;
                g = g.subst(i, j);
            }
            B.doPutToMap(g, a);
        }
        return B;
    }


    /**
     * Kronecker back substitution. Substitute x**d**(i-1) to x_i to construct
     * multivariate polynomials.
     * @param A list of polynomials to be converted.
     * @param fac result polynomial factory.
     * @return a list of multivariate polynomials.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> backSubstituteKronecker(
                    GenPolynomialRing<C> fac, List<GenPolynomial<C>> A, long d) {
        return ListUtil.<GenPolynomial<C>, GenPolynomial<C>> map(A, new BackSubstKronecker<C>(fac, d));
    }

}


/**
 * Kronecker substitutuion functor.
 */
class SubstKronecker<C extends GcdRingElem<C>> implements UnaryFunctor<GenPolynomial<C>, GenPolynomial<C>> {


    final long d;


    public SubstKronecker(long d) {
        this.d = d;
    }


    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if (c == null) {
            return null;
        }
        return PolyUfdUtil.<C> substituteKronecker(c, d);
    }
}


/**
 * Kronecker back substitutuion functor.
 */
class BackSubstKronecker<C extends GcdRingElem<C>>
                implements UnaryFunctor<GenPolynomial<C>, GenPolynomial<C>> {


    final long d;


    final GenPolynomialRing<C> fac;


    public BackSubstKronecker(GenPolynomialRing<C> fac, long d) {
        this.d = d;
        this.fac = fac;
    }


    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if (c == null) {
            return null;
        }
        return PolyUfdUtil.<C> backSubstituteKronecker(fac, c, d);
    }
}
