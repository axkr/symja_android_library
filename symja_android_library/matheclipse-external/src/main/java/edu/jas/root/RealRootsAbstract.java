/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;


/**
 * Real roots abstract class.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public abstract class RealRootsAbstract<C extends RingElem<C> & Rational> implements RealRoots<C> {


    private static final Logger logger = LogManager.getLogger(RealRootsAbstract.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Real root bound. With f(-M) * f(M) != 0.
     * @param f univariate polynomial.
     * @return M such that -M &lt; root(f) &lt; M.
     */
    public C realRootBound(GenPolynomial<C> f) {
        if (f == null) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C M = cfac.getONE();
        if (f.isZERO()) {
            return M;
        }
        if (f.isConstant()) {
            M = f.leadingBaseCoefficient().abs().sum(cfac.getONE());
            return M;
        }
        C a = f.leadingBaseCoefficient().abs();
        for (C c : f.getMap().values()) {
            C d = c.abs().divide(a);
            if (M.compareTo(d) < 0) {
                M = d;
            }
        }
        BigRational r = M.getRational();
        logger.info("rational root bound: " + r);
        BigInteger i = new BigInteger(r.numerator().divide(r.denominator()));
        i = i.sum(BigInteger.ONE); // ceiling
        M = cfac.fromInteger(i.getVal());
        M = M.sum(f.ring.coFac.getONE());
        logger.info("integer root bound: " + M);
        //System.out.println("M = " + M);
        return M;
    }


    /**
     * Magnitude bound.
     * @param iv interval.
     * @param f univariate polynomial.
     * @return B such that |f(c)| &lt; B for c in iv.
     */
    @SuppressWarnings("unchecked")
    public C magnitudeBound(Interval<C> iv, GenPolynomial<C> f) {
        if (f == null) {
            return null;
        }
        if (f.isZERO()) {
            return f.ring.coFac.getONE();
        }
        if (f.isConstant()) {
            return f.leadingBaseCoefficient().abs();
        }
        GenPolynomial<C> fa = f.map(new UnaryFunctor<C, C>() {


            public C eval(C a) {
                return a.abs();
            }
        });
        //System.out.println("fa = " + fa);
        C M = iv.left.abs();
        if (M.compareTo(iv.right.abs()) < 0) {
            M = iv.right.abs();
        }
        //System.out.println("M = " + M);
        RingFactory<C> cfac = f.ring.coFac;
        C B = PolyUtil.<C> evaluateMain(cfac, fa, M);
        // works also without this case, only for optimization 
        // to use rational number interval end points
        // can fail if real root is in interval [r,r+1] 
        // for too low precision or too big r, since r is approximation
        //if ((Object) B instanceof RealAlgebraicNumber) {
        //    RealAlgebraicNumber Br = (RealAlgebraicNumber) B;
        //    BigRational r = Br.magnitude();
        //    B = cfac.fromInteger(r.numerator()).divide(cfac.fromInteger(r.denominator()));
        //}
        BigRational r = B.getRational();
        B = cfac.fromInteger(r.numerator()).divide(cfac.fromInteger(r.denominator()));
        //System.out.println("B = " + B);
        return B;
    }


    /**
     * Real minimal root bound.
     * @param f univariate polynomial.
     * @return M such that abs(xi) &gt; M for f(xi) == 0.
     */
    public C realMinimalRootBound(GenPolynomial<C> f) {
        if (f == null) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        // maxNorm root bound
        BigRational mr = f.maxNorm().getRational().sum(BigRational.ONE);
        BigRational di = mr.sum(BigRational.ONE).inverse();
        C B = cfac.fromInteger(di.numerator()).divide(cfac.fromInteger(di.denominator()));
        //System.out.println("B = " + B + ", sign(B) = " + B.signum());
        return B;
    }


    /**
     * Real minimal root separation.
     * @param f univariate polynomial.
     * @return M such that abs(xi-xj) &gt; M for roots xi, xj of f.
     */
    public C realMinimalRootSeparation(GenPolynomial<C> f) {
        if (f == null) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        // sumNorm root bound
        BigRational pr = f.sumNorm().getRational();
        pr = pr.sum(BigRational.ONE);
        BigRational sep = BigRational.ZERO;
        long n = f.degree();
        if (n > 0) {
            sep = pr.power(2 * n).multiply(pr.fromInteger(n).power(n + 1)).inverse();
        }
        //System.out.println("sep = " + sep + ", sign(sep) = " + sep.signum());
        C M = cfac.fromInteger(sep.numerator()).divide(cfac.fromInteger(sep.denominator()));
        return M;
    }


    /**
     * Bi-section point.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial, non-zero.
     * @return a point c in the interval iv such that f(c) != 0.
     */
    public C bisectionPoint(Interval<C> iv, GenPolynomial<C> f) {
        if (f == null) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        C c = iv.left.sum(iv.right);
        c = c.divide(two);
        if (f.isZERO() || f.isConstant()) {
            return c;
        }
        C m = PolyUtil.<C> evaluateMain(cfac, f, c);
        while (m.isZERO()) {
            C d = iv.left.sum(c);
            d = d.divide(two);
            if (d.equals(c)) {
                d = iv.right.sum(c);
                d = d.divide(two);
                if (d.equals(c)) {
                    throw new RuntimeException("should not happen " + iv);
                }
            }
            c = d;
            m = PolyUtil.<C> evaluateMain(cfac, f, c);
            //System.out.println("c = " + c);
        }
        //System.out.println("c = " + c);
        return c;
    }


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @return a list of isolating intervalls for the real roots of f.
     */
    public abstract List<Interval<C>> realRoots(GenPolynomial<C> f);


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @param eps requested intervals length.
     * @return a list of isolating intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> realRoots(GenPolynomial<C> f, C eps) {
        return realRoots(f, eps.getRational());
    }


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @param eps requested intervals length.
     * @return a list of isolating intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> realRoots(GenPolynomial<C> f, BigRational eps) {
        List<Interval<C>> iv = realRoots(f);
        return refineIntervals(iv, f, eps);
    }


    /**
     * Sign changes on interval bounds.
     * @param iv root isolating interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return true if f(left) * f(right) &lt; 0, else false
     */
    public boolean signChange(Interval<C> iv, GenPolynomial<C> f) {
        if (f == null) {
            return false;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C l = PolyUtil.<C> evaluateMain(cfac, f, iv.left);
        C r = PolyUtil.<C> evaluateMain(cfac, f, iv.right);
        return l.signum() * r.signum() < 0;
    }


    /**
     * Number of real roots in interval.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return number of real roots of f in I.
     */
    public abstract long realRootCount(Interval<C> iv, GenPolynomial<C> f);


    /**
     * Half interval.
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @return a new interval v such that |v| &lt; |iv|/2.
     */
    public Interval<C> halfInterval(Interval<C> iv, GenPolynomial<C> f) {
        if (f == null || f.isZERO()) {
            return iv;
        }
        BigRational len = iv.rationalLength();
        BigRational two = len.factory().fromInteger(2);
        BigRational eps = len.divide(two);
        return refineInterval(iv, f, eps);
    }


    /**
     * Refine interval.
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a new interval v such that |v| &lt; eps.
     */
    public Interval<C> refineInterval(Interval<C> iv, GenPolynomial<C> f, BigRational eps) {
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return iv;
        }
        if (iv.rationalLength().compareTo(eps) < 0) {
            return iv;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        Interval<C> v = iv;
        while (v.rationalLength().compareTo(eps) >= 0) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            //System.out.println("c = " + c);
            //c = RootUtil.<C>bisectionPoint(v,f);
            if (PolyUtil.<C> evaluateMain(cfac, f, c).isZERO()) {
                v = new Interval<C>(c, c);
                break;
            }
            Interval<C> iv1 = new Interval<C>(v.left, c);
            if (signChange(iv1, f)) {
                v = iv1;
            } else {
                v = new Interval<C>(c, v.right);
            }
        }
        return v;
    }


    /**
     * Refine intervals.
     * @param V list of isolating intervals with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested intervals length.
     * @return a list of new intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> refineIntervals(List<Interval<C>> V, GenPolynomial<C> f, BigRational eps) {
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return V;
        }
        List<Interval<C>> IV = new ArrayList<Interval<C>>();
        for (Interval<C> v : V) {
            Interval<C> iv = refineInterval(v, f, eps);
            IV.add(iv);
        }
        return IV;
    }


    /**
     * Invariant interval for algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return v with v a new interval contained in iv such that g(v) != 0.
     */
    public abstract Interval<C> invariantSignInterval(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g);


    /**
     * Real algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0,
     *            with iv such that g(iv) != 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(iv)) .
     */
    public int realIntervalSign(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g) {
        if (g == null || g.isZERO()) {
            return 0;
        }
        if (f == null || f.isZERO() || f.isConstant()) {
            return g.signum();
        }
        if (g.isConstant()) {
            return g.signum();
        }
        RingFactory<C> cfac = f.ring.coFac;
        C c = iv.left.sum(iv.right);
        c = c.divide(cfac.fromInteger(2));
        C ev = PolyUtil.<C> evaluateMain(cfac, g, c);
        //System.out.println("ev = " + ev);
        return ev.signum();
    }


    /**
     * Real algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(v)), with v a new interval contained in iv such that g(v)
     *         != 0.
     */
    public int realSign(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g) {
        if (g == null || g.isZERO()) {
            return 0;
        }
        if (f == null || f.isZERO() || f.isConstant()) {
            return g.signum();
        }
        if (g.isConstant()) {
            return g.signum();
        }
        Interval<C> v = invariantSignInterval(iv, f, g);
        return realIntervalSign(v, f, g);
    }


    /**
     * Invariant interval for algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return v with v a new interval contained in iv such that |g(a) - g(b)|
     *         &lt; eps for a, b in v in iv.
     */
    public Interval<C> invariantMagnitudeInterval(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g,
                    BigRational eps) {
        Interval<C> v = iv;
        if (g == null || g.isZERO()) {
            return v;
        }
        if (g.isConstant()) {
            return v;
        }
        if (f == null || f.isZERO() || f.isConstant()) { // ?
            return v;
        }
        GenPolynomial<C> gp = PolyUtil.<C> baseDeriviative(g);
        //System.out.println("g  = " + g);
        //System.out.println("gp = " + gp);
        C B = magnitudeBound(iv, gp);
        //System.out.println("B = " + B);
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        while (B.multiply(v.length()).getRational().compareTo(eps) >= 0) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            Interval<C> im = new Interval<C>(c, v.right);
            if (signChange(im, f)) {
                v = im;
            } else {
                v = new Interval<C>(v.left, c);
            }
            //System.out.println("v = " + v.toDecimal());
        }
        return v;
    }


    /**
     * Real algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0,
     *            with iv such that |g(a) - g(b)| &lt; eps for a, b in iv.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return g(iv) .
     */
    public C realIntervalMagnitude(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g) {
        if (g.isZERO() || g.isConstant()) {
            return g.leadingBaseCoefficient();
        }
        RingFactory<C> cfac = f.ring.coFac;
        C evl = PolyUtil.<C> evaluateMain(cfac, g, iv.left);
        C evr = PolyUtil.<C> evaluateMain(cfac, g, iv.right);
        C ev = evl;
        if (evl.compareTo(evr) <= 0) {
            ev = evr;
        }
        //System.out.println("ev = " + ev + ", evl = " + evl + ", evr = " + evr + ", iv = " + iv);
        return ev;
    }


    /**
     * Real algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return g(iv) .
     */
    public C realMagnitude(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g, BigRational eps) {
        if (g.isZERO() || g.isConstant()) {
            return g.leadingBaseCoefficient();
        }
        Interval<C> v = invariantMagnitudeInterval(iv, f, g, eps);
        return realIntervalMagnitude(v, f, g);
    }


    /**
     * Real algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0,
     *            with iv such that |g(a) - g(b)| &lt; eps for a, b in iv.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return Interval( g(iv.left), g(iv.right) ) .
     */
    public Interval<C> realIntervalMagnitudeInterval(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g) {
        if (g.isZERO() || g.isConstant()) {
            return iv;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C evl = PolyUtil.<C> evaluateMain(cfac, g, iv.left);
        C evr = PolyUtil.<C> evaluateMain(cfac, g, iv.right);
        Interval<C> ev = new Interval<C>(evr, evl);
        if (evl.compareTo(evr) <= 0) {
            ev = new Interval<C>(evl, evr);
        }
        System.out.println("ev = " + ev + ", iv = " + iv);
        return ev;
    }


    /**
     * Approximate real root.
     * @param iv real root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a decimal approximation d such that |d-v| &lt; eps, for f(v) = 0,
     *         v real.
     */
    public BigDecimal approximateRoot(Interval<C> iv, GenPolynomial<C> f, BigRational eps)
                    throws NoConvergenceException {
        if (iv == null) {
            throw new IllegalArgumentException("null interval not allowed");
        }
        BigDecimal d = iv.toDecimal();
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return d;
        }
        if (iv.rationalLength().compareTo(eps) < 0) {
            return d;
        }
        BigDecimal left = new BigDecimal(iv.left.getRational());
        BigDecimal right = new BigDecimal(iv.right.getRational());
        BigRational reps = eps.getRational();
        BigDecimal e = new BigDecimal(reps);
        BigDecimal q = new BigDecimal("0.25");
        //System.out.println("left  = " + left);
        //System.out.println("right = " + right);
        e = e.multiply(d); // relative error
        //System.out.println("e     = " + e);
        BigDecimal dc = BigDecimal.ONE;
        // polynomials with decimal coefficients
        GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, f.ring);
        GenPolynomial<BigDecimal> df = PolyUtil.<C> decimalFromRational(dfac, f);
        GenPolynomial<C> fp = PolyUtil.<C> baseDeriviative(f);
        GenPolynomial<BigDecimal> dfp = PolyUtil.<C> decimalFromRational(dfac, fp);
        // Newton Raphson iteration: x_{n+1} = x_n - f(x_n)/f'(x_n)
        int i = 0;
        final int MITER = 50;
        int dir = 0;
        while (i++ < MITER) {
            BigDecimal fx = PolyUtil.<BigDecimal> evaluateMain(dc, df, d); // f(d)
            if (fx.isZERO()) {
                return d;
            }
            BigDecimal fpx = PolyUtil.<BigDecimal> evaluateMain(dc, dfp, d); // f'(d)
            if (fpx.isZERO()) {
                throw new NoConvergenceException("zero deriviative should not happen");
            }
            BigDecimal x = fx.divide(fpx);
            BigDecimal dx = d.subtract(x);
            //System.out.println("dx = " + dx + ", d = " + d);
            if (d.subtract(dx).abs().compareTo(e) <= 0) {
                return dx;
            }
            while (dx.compareTo(left) < 0 || dx.compareTo(right) > 0) {
                // dx < left: dx - left < 0
                // dx > right: dx - right > 0
                //System.out.println("trying to leave interval");
                if (i++ > MITER) { // dx > right: dx - right > 0
                    throw new NoConvergenceException("no convergence after " + i + " steps");
                }
                if (i > MITER / 2 && dir == 0) {
                    BigDecimal sd = new BigDecimal(iv.randomPoint().getRational());
                    d = sd;
                    x = sd.getZERO();
                    logger.info("trying new random starting point " + d);
                    i = 0;
                    dir = 1;
                }
                if (i > MITER / 2 && dir == 1) {
                    BigDecimal sd = new BigDecimal(iv.randomPoint().getRational());
                    d = sd;
                    x = sd.getZERO();
                    logger.info("trying new random starting point " + d);
                    //i = 0;
                    dir = 2; // end
                }
                x = x.multiply(q); // x * 1/4
                dx = d.subtract(x);
                //System.out.println(" x = " + x);
                //System.out.println("dx = " + dx);
            }
            d = dx;
        }
        throw new NoConvergenceException("no convergence after " + i + " steps");
    }


    /**
     * Approximate real roots.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a list of decimal approximations d such that |d-v| &lt; eps for
     *         all real v with f(v) = 0.
     */
    public List<BigDecimal> approximateRoots(GenPolynomial<C> f, BigRational eps) {
        List<Interval<C>> iv = realRoots(f);
        List<BigDecimal> roots = new ArrayList<BigDecimal>(iv.size());
        for (Interval<C> i : iv) {
            BigDecimal r = null; //approximateRoot(i, f, eps); roots.add(r);
            while (r == null) {
                try {
                    r = approximateRoot(i, f, eps);
                    roots.add(r);
                } catch (NoConvergenceException e) {
                    // fall back to exact algorithm
                    //System.out.println("" + e);
                    BigRational len = i.rationalLength();
                    len = len.divide(len.factory().fromInteger(1000));
                    i = refineInterval(i, f, len);
                    logger.info("fall back rootRefinement = " + i);
                }
            }
        }
        return roots;
    }


    /**
     * Test if x is an approximate real root.
     * @param x approximate real root.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return true if x is a decimal approximation of a real v with f(v) = 0
     *         with |d-v| &lt; eps, else false.
     */
    public boolean isApproximateRoot(BigDecimal x, GenPolynomial<C> f, C eps) {
        if (x == null) {
            throw new IllegalArgumentException("null root not allowed");
        }
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return true;
        }
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.multiply(new BigDecimal("1000")); // relax
        BigDecimal dc = BigDecimal.ONE;
        // polynomials with decimal coefficients
        GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, f.ring);
        GenPolynomial<BigDecimal> df = PolyUtil.<C> decimalFromRational(dfac, f);
        GenPolynomial<C> fp = PolyUtil.<C> baseDeriviative(f);
        GenPolynomial<BigDecimal> dfp = PolyUtil.<C> decimalFromRational(dfac, fp);
        //
        return isApproximateRoot(x, df, dfp, e);
    }


    /**
     * Test if x is an approximate real root.
     * @param x approximate real root.
     * @param f univariate polynomial, non-zero.
     * @param fp univariate polynomial, non-zero, deriviative of f.
     * @param eps requested interval length.
     * @return true if x is a decimal approximation of a real v with f(v) = 0
     *         with |d-v| &lt; eps, else false.
     */
    public boolean isApproximateRoot(BigDecimal x, GenPolynomial<BigDecimal> f, GenPolynomial<BigDecimal> fp,
                    BigDecimal eps) {
        if (x == null) {
            throw new IllegalArgumentException("null root not allowed");
        }
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return true;
        }
        BigDecimal dc = BigDecimal.ONE; // only for clarity
        // f(x)
        BigDecimal fx = PolyUtil.<BigDecimal> evaluateMain(dc, f, x);
        //System.out.println("fx    = " + fx);
        if (fx.isZERO()) {
            return true;
        }
        // f'(x)
        BigDecimal fpx = PolyUtil.<BigDecimal> evaluateMain(dc, fp, x); // f'(d)
        //System.out.println("fpx   = " + fpx);
        if (fpx.isZERO()) {
            return false;
        }
        BigDecimal d = fx.divide(fpx);
        if (d.isZERO()) {
            return true;
        }
        if (d.abs().compareTo(eps) <= 0) {
            return true;
        }
        System.out.println("x     = " + x);
        System.out.println("d     = " + d);
        return false;
    }


    /**
     * Test if each x in R is an approximate real root.
     * @param R ist of approximate real roots.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return true if each x in R is a decimal approximation of a real v with
     *         f(v) = 0 with |d-v| &lt; eps, else false.
     */
    public boolean isApproximateRoot(List<BigDecimal> R, GenPolynomial<C> f, BigRational eps) {
        if (R == null) {
            throw new IllegalArgumentException("null root not allowed");
        }
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return true;
        }
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.multiply(new BigDecimal("1000")); // relax
        BigDecimal dc = BigDecimal.ONE;
        // polynomials with decimal coefficients
        GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, f.ring);
        GenPolynomial<BigDecimal> df = PolyUtil.<C> decimalFromRational(dfac, f);
        GenPolynomial<C> fp = PolyUtil.<C> baseDeriviative(f);
        GenPolynomial<BigDecimal> dfp = PolyUtil.<C> decimalFromRational(dfac, fp);
        for (BigDecimal x : R) {
            if (!isApproximateRoot(x, df, dfp, e)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Fourier sequence.
     * @param f univariate polynomial.
     * @return (f, f', ..., f(n)) a Fourier sequence for f.
     */
    public List<GenPolynomial<C>> fourierSequence(GenPolynomial<C> f) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if (f == null || f.isZERO()) {
            return S;
        }
        long d = f.degree();
        GenPolynomial<C> F = f;
        S.add(F);
        while (d-- > 0) {
            GenPolynomial<C> G = PolyUtil.<C> baseDeriviative(F);
            F = G;
            S.add(F);
        }
        //System.out.println("F = " + F);
        return S;
    }


    /**
     * Thom sign sequence.
     * @param f univariate polynomial.
     * @param v interval for a real root, f(v.left) * f(v.right) &lt; 0.
     * @return (s1, s2, ..., sn) = (sign(f'(v)), .... sign(f(n)(v))) a Thom sign
     *         sequence for the real root in v of f.
     */
    public List<Integer> signSequence(GenPolynomial<C> f, Interval<C> v) {
        List<Integer> S = new ArrayList<Integer>();
        GenPolynomial<C> fp = PolyUtil.<C> baseDeriviative(f);
        List<GenPolynomial<C>> fs = fourierSequence(fp);
        for (GenPolynomial<C> p : fs) {
            int s = realSign(v, f, p);
            S.add(s);
        }
        return S;
    }


    /**
     * Root number.
     * @param f univariate polynomial.
     * @param v interval for a real root, f(v.left) * f(v.right) &lt; 0.
     * @return r the number of this root in the sequence a1 &lt; a2 &lt; ...,
     *         &lt; am of all real roots of f
     */
    public Long realRootNumber(GenPolynomial<C> f, Interval<C> v) {
        C M = realRootBound(f);
        Interval<C> iv = new Interval<C>(M.negate(), v.right);
        long r = realRootCount(iv, f);
        if (r <= 0) {
            logger.warn("no real root in interval " + v);
        }
        return r;
    }

}
