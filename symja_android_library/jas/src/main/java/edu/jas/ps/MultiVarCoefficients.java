/*
 * $Id$
 */

package edu.jas.ps;


import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Abstract class for generating functions for coefficients of multivariate
 * power series. This class handles the caching itself.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public abstract class MultiVarCoefficients<C extends RingElem<C>> implements Serializable {


    /**
     * Ring factory for polynomials.
     */
    public final GenPolynomialRing<C> pfac;


    /**
     * Cache for already computed coefficients.
     */
    public final HashMap<Long, GenPolynomial<C>> coeffCache;


    /**
     * Indicator if all coefficients of a homogeneous degree have been
     * constructed.
     */
    public final BitSet homCheck;


    /**
     * Cache for known zero coefficients. Required because zero coefficients are
     * not stored in the polynomials.
     */
    public final HashSet<ExpVector> zeroCache;


    /**
     * Public constructor.
     * @param pf multivariate power series ring factory.
     */
    public MultiVarCoefficients(MultiVarPowerSeriesRing<C> pf) {
        this(pf.polyRing(), new HashMap<Long, GenPolynomial<C>>(), new HashSet<ExpVector>());
    }


    /**
     * Public constructor with some pre-filled caches.
     * @param pf multivariate power series ring factory.
     * @param hc pre-filled homogeneous check bit-set.
     */
    public MultiVarCoefficients(MultiVarPowerSeriesRing<C> pf, BitSet hc) {
        this(pf.polyRing(), new HashMap<Long, GenPolynomial<C>>(), new HashSet<ExpVector>(), hc);
    }


    /**
     * Public constructor.
     * @param pf polynomial ring factory.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf) {
        this(pf, new HashMap<Long, GenPolynomial<C>>(), new HashSet<ExpVector>());
    }


    /**
     * Public with pre-filled coefficient cache.
     * @param pf polynomial ring factory.
     * @param cache pre-filled coefficient cache.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf, HashMap<Long, GenPolynomial<C>> cache) {
        this(pf, cache, new HashSet<ExpVector>());
    }


    /**
     * Public constructor with pre-filled caches.
     * @param pf polynomial ring factory.
     * @param cache pre-filled coefficient cache.
     * @param zeros pre-filled zero coefficient cache.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf, HashMap<Long, GenPolynomial<C>> cache,
            HashSet<ExpVector> zeros) {
        this(pf, cache, zeros, new BitSet());
    }


    /**
     * Public constructor with pre-filled caches.
     * @param pf polynomial ring factory.
     * @param hc pre-filled homogeneous check bit-set.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf, BitSet hc) {
        this(pf, new HashMap<Long, GenPolynomial<C>>(), new HashSet<ExpVector>(), hc);
    }


    /**
     * Public constructor with pre-filled caches.
     * @param pf polynomial ring factory.
     * @param cache pre-filled coefficient cache.
     * @param hc pre-filled homogeneous check bit-set.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf, HashMap<Long, GenPolynomial<C>> cache, BitSet hc) {
        this(pf, cache, new HashSet<ExpVector>(), hc);
    }


    /**
     * Public constructor with pre-filled caches.
     * @param pf polynomial ring factory.
     * @param cache pre-filled coefficient cache.
     * @param zeros pre-filled zero coefficient cache.
     * @param hc pre-filled homogeneous check bit-set.
     */
    public MultiVarCoefficients(GenPolynomialRing<C> pf, HashMap<Long, GenPolynomial<C>> cache,
            HashSet<ExpVector> zeros, BitSet hc) {
        pfac = pf;
        coeffCache = cache;
        zeroCache = zeros;
        homCheck = hc;
    }


    /**
     * Get cached coefficient or generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    public C get(ExpVector index) {
        //if (index.signum() < 0) { // better assert
        //    throw new IllegalArgumentException("negative signum not allowed " + index);
        //}
        //if (coeffCache == null) { // not possible
        //    return generate(index);
        //}
        long tdeg = index.totalDeg();
        GenPolynomial<C> p = coeffCache.get(tdeg);
        if (p == null) {
            p = pfac.getZERO().copy();
            coeffCache.put(tdeg, p);
        }
        C c = p.coefficient(index);
        if (!c.isZERO()) {
            return c;
        }
        if (homCheck.get((int) tdeg)) { // rely on p
            return c;
        }
        if (zeroCache.contains(index)) {
            return c;
        }
        C g = generate(index);
        if (g.isZERO()) {
            zeroCache.add(index);
        } else {
            p.doPutToMap(index, g);
        }
        return g;
    }


    /**
     * Homogeneous part.
     * @param tdeg requested degree.
     * @return polynomial part of given degree.
     */
    public GenPolynomial<C> getHomPart(long tdeg) {
        if (coeffCache == null) {
            throw new IllegalArgumentException("null cache not allowed");
        }
        GenPolynomial<C> p = coeffCache.get(tdeg);
        if (p == null) {
            p = pfac.getZERO().copy();
            coeffCache.put(tdeg, p);
        } 
        // trust contents?
        if (homCheck.get((int) tdeg)) {
            return p;
        }
        // check correct contents or generate coefficients
        ExpVectorIterable eiter = new ExpVectorIterable(pfac.nvar, tdeg);
        for (ExpVector e : eiter) {
            if (zeroCache.contains(e)) {
                if ( !zeroCache.remove(e) ) { // clean-up unused
                    System.out.println("not removed e = " + e); // cannot happen
                }
                continue;
            }
            if (!p.coefficient(e).isZERO()) {
                continue;
            }
            C g = generate(e);
            if (!g.isZERO()) {
                p.doPutToMap(e, g);
            }
        }
        homCheck.set((int) tdeg);
        //System.out.println("homCheck = " + homCheck);
        //System.out.println("coeffCache = " + coeffCache.keySet());
        return p;
    }


    /**
     * Generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    protected abstract C generate(ExpVector index);

}
