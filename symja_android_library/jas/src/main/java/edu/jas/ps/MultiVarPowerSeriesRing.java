/*
 * $Id$
 */

package edu.jas.ps;


import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.ListUtil;


/**
 * Multivariate power series ring implementation. Uses lazy evaluated generating
 * function for coefficients.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class MultiVarPowerSeriesRing<C extends RingElem<C>> implements RingFactory<MultiVarPowerSeries<C>> {


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random();


    /**
     * Default truncate.
     */
    public final static int DEFAULT_TRUNCATE = 7;


    /**
     * Truncate.
     */
    int truncate;


    /**
     * Zero ExpVector.
     */
    public final ExpVector EVZERO;


    /**
     * Coefficient ring factory.
     */
    public final RingFactory<C> coFac;


    /**
     * The number of variables.
     */
    public final int nvar;


    /**
     * The names of the variables. This value can be modified.
     */
    protected String[] vars;


    /**
     * The constant power series 1 for this ring.
     */
    public final MultiVarPowerSeries<C> ONE;


    /**
     * The constant power series 0 for this ring.
     */
    public final MultiVarPowerSeries<C> ZERO;


    /**
     * No argument constructor.
     */
    @SuppressWarnings("unused")
    private MultiVarPowerSeriesRing() {
        throw new IllegalArgumentException("do not use no-argument constructor");
    }


    /**
     * Constructor.
     * @param fac polynomial ring factory.
     */
    public MultiVarPowerSeriesRing(GenPolynomialRing<C> fac) {
        this(fac.coFac, fac.nvar, fac.getVars());
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, int nv) {
        this(coFac, nv, DEFAULT_TRUNCATE);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param truncate index of truncation.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, int nv, int truncate) {
        this(coFac, nv, truncate, null);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param names of the variables.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, String[] names) {
        this(coFac, names.length, DEFAULT_TRUNCATE, names);
    }


    /**
     * Constructor.
     * @param cofac coefficient ring factory.
     * @param nv number of variables.
     * @param names of the variables.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> cofac, int nv, String[] names) {
        this(cofac, nv, DEFAULT_TRUNCATE, names);
    }


    /**
     * Constructor.
     * @param cofac coefficient ring factory.
     * @param truncate index of truncation.
     * @param names of the variables.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> cofac, int nv, int truncate, String[] names) {
        this.coFac = cofac;
        this.nvar = nv;
        this.truncate = truncate;
        vars = names;
        if (vars == null) {
            if (PrettyPrint.isTrue()) {
                vars = GenPolynomialRing.newVars("x", nvar);
            }
        } else {
            if (vars.length != nvar) {
                throw new IllegalArgumentException("incompatible variable size " + vars.length + ", " + nvar);
            }
            GenPolynomialRing.addVars(vars);
        }
        EVZERO = ExpVector.create(nvar);
        ONE = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                if (i.isZERO()) {
                    return coFac.getONE();
                }
                return coFac.getZERO();
            }
        });
        ZERO = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                return coFac.getZERO();
            }
        });
    }


    /**
     * Fixed point construction.
     * @param map a mapping of power series.
     * @return fix point wrt map.
     */
    // Cannot be a static method because a power series ring is required.
    public MultiVarPowerSeries<C> fixPoint(MultiVarPowerSeriesMap<C> map) {
        MultiVarPowerSeries<C> ps1 = new MultiVarPowerSeries<C>(this);
        MultiVarPowerSeries<C> ps2 = map.map(ps1);
        ps1.lazyCoeffs = ps2.lazyCoeffs;
        return ps2;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String scf = coFac.getClass().getSimpleName();
        sb.append(scf + "((" + varsToString() + "))");
        return sb.toString();
    }


    /**
     * Get a String representation of the variable names.
     * @return names separated by commas.
     */
    public String varsToString() {
        if (vars == null) {
            return "#" + nvar;
        }
        return ExpVector.varsToString(vars);
        //return Arrays.toString(vars);
    }


    /**
     * Get the variable names.
     * @return names.
     */
    public String[] getVars() {
        return vars; // Java-5: Arrays.copyOf(vars,vars.length);
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("MPS(");
        String f = null;
        try {
            f = ((RingElem<C>) coFac).toScriptFactory(); // sic
        } catch (Exception e) {
            f = coFac.toScript();
        }
        s.append(f + ",\"" + varsToString() + "\"," + truncate + ")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        MultiVarPowerSeriesRing<C> a = null;
        try {
            a = (MultiVarPowerSeriesRing<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
        if (!coFac.equals(a.coFac)) {
            return false;
        }
        if (Arrays.equals(vars, a.vars)) {
            return true;
        }
        return false;
    }


    /**
     * Hash code for this .
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = coFac.hashCode();
        h = h << 7;
        h += (Arrays.hashCode(vars) << 17);
        h += truncate;
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getONE() {
        return ONE;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<MultiVarPowerSeries<C>> generators() {
        List<C> rgens = coFac.generators();
        List<MultiVarPowerSeries<C>> gens = new ArrayList<MultiVarPowerSeries<C>>(rgens.size());
        for (final C cg : rgens) {
            MultiVarPowerSeries<C> g = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


                @Override
                public C generate(ExpVector i) {
                    if (i.isZERO()) {
                        return cg;
                    }
                    return coFac.getZERO();
                }
            });
            gens.add(g);
        }
        for (int i = 0; i < nvar; i++) {
            gens.add(ONE.shift(1, nvar - 1 - i));
        }
        return gens;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /**
     * Truncate.
     * @return truncate index of power series.
     */
    public int truncate() {
        return truncate;
    }


    /**
     * Set truncate.
     * @param t new truncate index.
     * @return old truncate index of power series.
     */
    public int setTruncate(int t) {
        if (t < 0) {
            throw new IllegalArgumentException("negative truncate not allowed");
        }
        int ot = truncate;
        truncate = t;
        ONE.setTruncate(t);
        ZERO.setTruncate(t);
        return ot;
    }


    /**
     * Get the power series of the exponential function.
     * @param r variable for the direction.
     * @return exp(x_r) as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getEXP(final int r) {
        return fixPoint(new MultiVarPowerSeriesMap<C>() {


            public MultiVarPowerSeries<C> map(MultiVarPowerSeries<C> e) {
                return e.integrate(coFac.getONE(), r);
            }
        });
    }


    /**
     * Get the power series of the sinus function.
     * @param r variable for the direction.
     * @return sin(x_r) as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getSIN(final int r) {
        return fixPoint(new MultiVarPowerSeriesMap<C>() {


            public MultiVarPowerSeries<C> map(MultiVarPowerSeries<C> s) {
                return s.negate().integrate(coFac.getONE(), r).integrate(coFac.getZERO(), r);
            }
        });
    }


    /**
     * Get the power series of the cosinus function.
     * @param r variable for the direction.
     * @return cos(x_r) as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getCOS(final int r) {
        return fixPoint(new MultiVarPowerSeriesMap<C>() {


            public MultiVarPowerSeries<C> map(MultiVarPowerSeries<C> c) {
                return c.negate().integrate(coFac.getZERO(), r).integrate(coFac.getONE(), r);
            }
        });
    }


    /**
     * Get the power series of the tangens function.
     * @param r variable for the direction.
     * @return tan(x_r) as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getTAN(final int r) {
        return fixPoint(new MultiVarPowerSeriesMap<C>() {


            public MultiVarPowerSeries<C> map(MultiVarPowerSeries<C> t) {
                return t.multiply(t).sum(getONE()).integrate(coFac.getZERO(), r);
            }
        });
    }


    /**
     * Solve an partial differential equation. y_r' = f(y_r) with y_r(0) = c.
     * @param f a MultiVarPowerSeries<C>.
     * @param c integration constant.
     * @param r variable for the direction.
     * @return f.integrate(c).
     */
    public MultiVarPowerSeries<C> solvePDE(MultiVarPowerSeries<C> f, C c, int r) {
        return f.integrate(c, r);
    }


    /**
     * Query if this ring is commuative.
     * @return true, if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return coFac.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return coFac.characteristic();
    }


    /**
     * Get a (constant) MultiVarPowerSeries&lt;C&gt; from a long value.
     * @param a long.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromInteger(final long a) {
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                if (i.isZERO()) {
                    return coFac.fromInteger(a);
                }
                return coFac.getZERO();
            }
        });
    }


    /**
     * Get a (constant) MultiVarPowerSeries&lt;C&gt; from a
     * java.math.BigInteger.
     * @param a BigInteger.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromInteger(final java.math.BigInteger a) {
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                if (i.isZERO()) {
                    return coFac.fromInteger(a);
                }
                return coFac.getZERO();
            }
        });
    }


    /**
     * Get the corresponding GenPolynomialRing&lt;C&gt;.
     * @return GenPolynomialRing&lt;C&gt;.
     */
    public GenPolynomialRing<C> polyRing() {
        return new GenPolynomialRing<C>(coFac, nvar, vars);
    }


    /**
     * Get a MultiVarPowerSeries&lt;C&gt; from a GenPolynomial&lt;C&gt;.
     * @param a GenPolynomial&lt;C&gt;.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromPolynomial(GenPolynomial<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        if (a.isONE()) {
            return ONE;
        }
        GenPolynomialRing<C> pfac = polyRing();
        HashMap<Long, GenPolynomial<C>> cache = new HashMap<Long, GenPolynomial<C>>();
        int mt = 0;
        for (Monomial<C> m : a) {
            ExpVector e = m.exponent();
            long t = e.totalDeg();
            mt = Math.max(mt, (int) t);
            GenPolynomial<C> p = cache.get(t);
            if (p == null) {
                p = pfac.getZERO().copy();
                cache.put(t, p);
            }
            p.doPutToMap(e, m.coefficient());
        }
        mt++;
        if (mt > truncate()) {
            setTruncate(mt);
        }
        BitSet check = new BitSet();
        for (int i = 0; i <= truncate(); i++) {
            check.set(i);
            if (cache.get((long) i) == null) {
                GenPolynomial<C> p = pfac.getZERO().copy();
                cache.put((long) i, p);
                //System.out.println("p zero for deg i = " + i);
            }
        }

        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(pfac, cache, check) {


            @Override
            public C generate(ExpVector e) {
                // cached coefficients returned by get
                return coFac.getZERO();
            }
        });
    }


    /**
     * Get a list of MultiVarPowerSeries&lt;C&gt; from a list of
     * GenPolynomial&lt;C&gt;.
     * @param A list of GenPolynomial&lt;C&gt;.
     * @return a list of MultiVarPowerSeries&lt;C&gt;.
     */
    public List<MultiVarPowerSeries<C>> fromPolynomial(List<GenPolynomial<C>> A) {
        return ListUtil.<GenPolynomial<C>, MultiVarPowerSeries<C>> map(A,
                        new UnaryFunctor<GenPolynomial<C>, MultiVarPowerSeries<C>>() {


                            public MultiVarPowerSeries<C> eval(GenPolynomial<C> c) {
                                return fromPolynomial(c);
                            }
                        });
    }


    /**
     * Get a MultiVarPowerSeries&lt;C&gt; from a univariate power series.
     * @param ps UnivPowerSeries&lt;C&gt;.
     * @param r variable for the direction.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromPowerSeries(final UnivPowerSeries<C> ps, final int r) {
        if (ps == null) {
            return ZERO;
        }
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                if (i.isZERO()) {
                    return ps.coefficient(0);
                }
                int[] dep = i.dependencyOnVariables();
                if (dep.length != 1) {
                    return coFac.getZERO();
                }
                if (dep[0] != r) {
                    return coFac.getZERO();
                }
                int j = (int) i.getVal(r);
                if (j > 0) {
                    return ps.coefficient(j);
                }
                return coFac.getZERO();
            }
        });
    }


    /**
     * Generate a random power series with k = 5, d = 0.7.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random() {
        return random(5, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bit-size of random coefficients.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k) {
        return random(k, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bit-size of random coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k, Random rnd) {
        return random(k, 0.7f, rnd);
    }


    /**
     * Generate a random power series.
     * @param k bit-size of random coefficients.
     * @param d density of non-zero coefficients.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k, float d) {
        return random(k, d, random);
    }


    /**
     * Generate a random power series.
     * @param k bit-size of random coefficients.
     * @param d density of non-zero coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(final int k, final float d, final Random rnd) {
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            @Override
            public C generate(ExpVector i) {
                // cached coefficients returned by get
                C c;
                float f = rnd.nextFloat();
                if (f < d) {
                    c = coFac.random(k, rnd);
                } else {
                    c = coFac.getZERO();
                }
                return c;
            }
        });
    }


    /**
     * Copy power series.
     * @param c a power series.
     * @return a copy of c.
     */
    public MultiVarPowerSeries<C> copy(MultiVarPowerSeries<C> c) {
        return new MultiVarPowerSeries<C>(this, c.lazyCoeffs);
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param s String.
     * @return power series from s.
     */
    public MultiVarPowerSeries<C> parse(String s) {
        throw new UnsupportedOperationException("parse for power series not implemented");
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param r Reader.
     * @return next power series from r.
     */
    public MultiVarPowerSeries<C> parse(Reader r) {
        throw new UnsupportedOperationException("parse for power series not implemented");
    }


    /**
     * Taylor power series.
     * @param f function.
     * @param a expansion point.
     * @return Taylor series of f.
     */
    public MultiVarPowerSeries<C> seriesOfTaylor(final TaylorFunction<C> f, final List<C> a) {
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {


            TaylorFunction<C> der = f;


            // Map<ExpVextor,TaylorFunction<C>> pderCache = ...
            final List<C> v = a;


            @Override
            public C generate(ExpVector i) {
                C c;
                int s = i.signum();
                if (s == 0) {
                    c = der.evaluate(v);
                    return c;
                }
                TaylorFunction<C> pder = der.deriviative(i);
                if (pder.isZERO()) {
                    return coFac.getZERO();
                }
                c = pder.evaluate(v);
                if (c.isZERO()) {
                    return c;
                }
                long f = pder.getFacul();
                c = c.divide(coFac.fromInteger(f));
                return c;
            }
        });
    }

}
