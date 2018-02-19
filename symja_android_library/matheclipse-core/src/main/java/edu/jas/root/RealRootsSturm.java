/*
 * $Id$
 */

package edu.jas.root;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Real root isolation using Sturm sequences.
 *
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class RealRootsSturm<C extends RingElem<C> & Rational> extends RealRootsAbstract<C> {


    private static final Logger logger = Logger.getLogger(RealRootsSturm.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Sturm sequence.
     *
     * @param f univariate polynomial.
     * @return a Sturm sequence for f.
     */
    public List<GenPolynomial<C>> sturmSequence(GenPolynomial<C> f) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if (f == null || f.isZERO()) {
            return S;
        }
        if (f.isConstant()) {
            S.add(f.monic());
            return S;
        }
        GenPolynomial<C> F = f;
        S.add(F);
        GenPolynomial<C> G = PolyUtil.<C>baseDeriviative(f);
        while (!G.isZERO()) {
            GenPolynomial<C> r = F.remainder(G);
            F = G;
            G = r.negate();
            S.add(F/*.monic()*/);
        }
        //System.out.println("F = " + F);
        if (F.isConstant()) {
            return S;
        }
        // make squarefree
        List<GenPolynomial<C>> Sp = new ArrayList<GenPolynomial<C>>(S.size());
        for (GenPolynomial<C> p : S) {
            p = p.divide(F);
            Sp.add(p);
        }
        return Sp;
    }


    /**
     * Isolating intervals for the real roots.
     *
     * @param f univariate polynomial.
     * @return a list of isolating intervals for the real roots of f.
     */
    @SuppressWarnings("cast")
    @Override
    public List<Interval<C>> realRoots(GenPolynomial<C> f) {
        List<Interval<C>> R = new ArrayList<Interval<C>>();
        if (f == null || f.isConstant()) {
            return R;
        }
        if (f.isZERO()) {
            C z = f.ring.coFac.getZERO();
            R.add(new Interval<C>(z));
            return R;
        }
        if (f.degree(0) == 1L) {
            C z = f.monic().trailingBaseCoefficient().negate();
            R.add(new Interval<C>(z));
            return R;
        }
        GenPolynomial<C> F = f;
        C M = realRootBound(F); // M != 0, since >= 2
        Interval<C> iv = new Interval<C>(M.negate(), M);
        //System.out.println("iv = " + iv);
        List<GenPolynomial<C>> S = sturmSequence(F);
        //System.out.println("S = " + S);
        //System.out.println("f_S = " + S.get(0));
        List<Interval<C>> Rp = realRoots(iv, S);
        if (logger.isInfoEnabled() && !(((Object) f.ring.coFac) instanceof BigRational)) {
            //logger.info("realRoots bound: " + iv);
            logger.info("realRoots: " + Rp);
        }
        R.addAll(Rp);
        return R;
    }


    /**
     * Isolating intervals for the real roots.
     *
     * @param iv interval with f(left) * f(right) != 0.
     * @param S  sturm sequence for f and I.
     * @return a list of isolating intervals for the real roots of f in I.
     */
    public List<Interval<C>> realRoots(Interval<C> iv, List<GenPolynomial<C>> S) {
        List<Interval<C>> R = new ArrayList<Interval<C>>();
        GenPolynomial<C> f = S.get(0); // squarefree part
        if (f.isZERO()) {
            C z = f.leadingBaseCoefficient();
            if (!iv.contains(z)) {
                throw new IllegalArgumentException(
                        "root not in interval: f = " + f + ", iv = " + iv + ", z = " + z);
            }
            Interval<C> iv1 = new Interval<C>(z);
            R.add(iv1);
            return R;
        }
        if (f.isConstant()) {
            return R;
            //throw new IllegalArgumentException("f has no root: f = " + f + ", iv = " + iv);
        }
        if (f.degree(0) == 1L) {
            C z = f.monic().trailingBaseCoefficient().negate();
            if (!iv.contains(z)) {
                return R;
                //throw new IllegalArgumentException("root not in interval: f = " + f + ", iv = " + iv + ", z = " + z);
            }
            Interval<C> iv1 = new Interval<C>(z);
            R.add(iv1);
            return R;
        }
        //System.out.println("iv = " + iv);
        // check sign variations at interval bounds
        long v = realRootCount(iv, S);
        //System.out.println("v = " + v);
        if (v == 0) {
            return R;
        }
        if (v == 1) {
            iv = excludeZero(iv, S);
            R.add(iv);
            return R;
        }
        // now v &gt; 1
        // bi-sect interval, such that f(c) != 0
        C c = bisectionPoint(iv, f);
        //System.out.println("c = " + c);
        // recursion on both sub-intervals
        Interval<C> iv1 = new Interval<C>(iv.left, c);
        Interval<C> iv2 = new Interval<C>(c, iv.right);
        List<Interval<C>> R1 = realRoots(iv1, S);
        //System.out.println("R1 = " + R1);
        if (debug) {
            logger.info("R1 = " + R1);
        }
        List<Interval<C>> R2 = realRoots(iv2, S);
        //System.out.println("R2 = " + R2);
        if (debug) {
            logger.info("R2 = " + R2);
        }

        // refine isolating intervals if adjacent 
        if (R1.isEmpty()) {
            R.addAll(R2);
            return R;
        }
        if (R2.isEmpty()) {
            R.addAll(R1);
            return R;
        }
        iv1 = R1.get(R1.size() - 1); // last
        iv2 = R2.get(0); // first
        if (iv1.right.compareTo(iv2.left) < 0) {
            R.addAll(R1);
            R.addAll(R2);
            return R;
        }
        // now iv1.right == iv2.left
        //System.out.println("iv1 = " + iv1);
        //System.out.println("iv2 = " + iv2);
        R1.remove(iv1);
        R2.remove(iv2);
        while (iv1.right.equals(iv2.left)) {
            C d1 = bisectionPoint(iv1, f);
            C d2 = bisectionPoint(iv2, f);
            Interval<C> iv11 = new Interval<C>(iv1.left, d1);
            Interval<C> iv12 = new Interval<C>(d1, iv1.right);
            Interval<C> iv21 = new Interval<C>(iv2.left, d2);
            Interval<C> iv22 = new Interval<C>(d2, iv2.right);

            boolean b11 = signChange(iv11, f);
            boolean b12 = signChange(iv12, f); // TODO check unnecessary
            //boolean b21 = signChange(iv21, f); // TODO check unused or unnecessary
            boolean b22 = signChange(iv22, f);
            if (b11) {
                iv1 = iv11;
                if (b22) {
                    iv2 = iv22;
                } else {
                    iv2 = iv21;
                }
                break; // done, refine
            }
            if (b22) {
                iv2 = iv22;
                if (b12) {
                    iv1 = iv12;
                } else {
                    iv1 = iv11;
                }
                break; // done, refine
            }
            iv1 = iv12;
            iv2 = iv21;
            //System.out.println("iv1 = " + iv1);
            //System.out.println("iv2 = " + iv2);
        }
        R.addAll(R1);
        R.add(iv1);
        R.add(iv2);
        R.addAll(R2);
        return R;
    }


    /**
     * Number of real roots in interval.
     *
     * @param iv interval with f(left) * f(right) != 0.
     * @param S  sturm sequence for f and I.
     * @return number of real roots of f in I.
     */
    public long realRootCount(Interval<C> iv, List<GenPolynomial<C>> S) {
        // check sign variations at interval bounds
        GenPolynomial<C> f = S.get(0); // squarefree part
        //System.out.println("iv = " + iv);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C>evaluateMain(cfac, S, iv.left);
        List<C> r = PolyUtil.<C>evaluateMain(cfac, S, iv.right);
        long v = RootUtil.<C>signVar(l) - RootUtil.<C>signVar(r);
        //System.out.println("v = " + v);
        if (v < 0L) {
            v = -v;
        }
        return v;
    }


    /**
     * Number of real roots in interval.
     *
     * @param iv interval with f(left) * f(right) != 0.
     * @param f  univariate polynomial.
     * @return number of real roots of f in I.
     */
    @Override
    public long realRootCount(Interval<C> iv, GenPolynomial<C> f) {
        if (f == null || f.isConstant()) { // ? 
            return 0L;
        }
        if (f.isZERO()) {
            C z = f.leadingBaseCoefficient();
            if (!iv.contains(z)) {
                return 0L;
            }
            return 1L;
        }
        List<GenPolynomial<C>> S = sturmSequence(f);
        return realRootCount(iv, S);
    }


    /**
     * Invariant interval for algebraic number sign.
     *
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f  univariate polynomial, non-zero.
     * @param g  univariate polynomial, gcd(f,g) == 1.
     * @return v with v a new interval contained in iv such that g(w) != 0 for w
     * in v.
     */
    @Override
    public Interval<C> invariantSignInterval(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g) {
        Interval<C> v = iv;
        if (g == null || g.isZERO()) {
            //throw new IllegalArgumentException("g == 0");
            return v;
        }
        if (g.isConstant()) {
            return v;
        }
        if (f == null || f.isZERO()) { // ? || f.isConstant()
            throw new IllegalArgumentException("f == 0");
            //return v;
        }
        List<GenPolynomial<C>> Sg = sturmSequence(g.monic());
        Interval<C> ivp = invariantSignInterval(iv, f, Sg);
        return ivp;
    }


    /**
     * Invariant interval for algebraic number sign.
     *
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f  univariate polynomial, non-zero.
     * @param Sg Sturm sequence for g, a univariate polynomial with gcd(f,g) ==
     *           1.
     * @return v with v a new interval contained in iv such that g(w) != 0 for w
     * in v.
     */
    public Interval<C> invariantSignInterval(Interval<C> iv, GenPolynomial<C> f, List<GenPolynomial<C>> Sg) {
        Interval<C> v = iv;
        GenPolynomial<C> g = Sg.get(0);
        if (g == null || g.isZERO()) {
            return v;
        }
        if (g.isConstant()) {
            return v;
        }
        if (f == null || f.isZERO()) { // ? || f.isConstant()
            return v;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);

        while (true) {
            long n = realRootCount(v, Sg);
            logger.debug("n = " + n);
            if (n == 0) {
                return v;
            }
            C c = v.left.sum(v.right);
            c = c.divide(two);
            Interval<C> im = new Interval<C>(c, v.right);
            if (signChange(im, f)) {
                v = im;
            } else {
                v = new Interval<C>(v.left, c);
            }
        }
        // return v;
    }


    /**
     * Exclude zero.
     *
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param S  sturm sequence for f and I.
     * @return a new interval v such that v &lt; 0 or v &gt; 0.
     */
    public Interval<C> excludeZero(Interval<C> iv, List<GenPolynomial<C>> S) {
        if (S == null || S.isEmpty()) {
            return iv;
        }
        C zero = S.get(0).ring.coFac.getZERO();
        if (!iv.contains(zero)) {
            return iv;
        }
        Interval<C> vn = new Interval<C>(iv.left, zero);
        if (realRootCount(vn, S) == 1) {
            return vn;
        }
        vn = new Interval<C>(zero, iv.right);
        return vn;
    }

}
