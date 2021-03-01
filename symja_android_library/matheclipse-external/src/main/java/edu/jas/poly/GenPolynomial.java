/*
 * $Id$
 */

package edu.jas.poly;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.PreemptingException;
import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.MapEntry;


/**
 * GenPolynomial generic polynomials implementing RingElem. n-variate ordered
 * polynomials over coefficients C. The variables commute with each other and
 * with the coefficients. For non-commutative coefficients some care is taken to
 * respect the multiplication order.
 *
 * Objects of this class are intended to be immutable. The implementation is
 * based on TreeMap respectively SortedMap from exponents to coefficients. Only
 * the coefficients are modeled with generic types, the exponents are fixed to
 * ExpVector with long entries (@see edu.jas.poly.ExpVector StorUnit). C can
 * also be a non integral domain, e.g. a ModInteger, i.e. it may contain zero
 * divisors, since multiply() does check for zeros. <b>Note:</b> multiply() now
 * checks for wrong method dispatch for GenSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class GenPolynomial<C extends RingElem<C>>
                implements RingElem<GenPolynomial<C>>, /* not yet Polynomial<C> */
                Iterable<Monomial<C>> {


    /**
     * The factory for the polynomial ring.
     */
    public final GenPolynomialRing<C> ring;


    /**
     * The data structure for polynomials.
     */
    protected final SortedMap<ExpVector, C> val; // do not change to TreeMap


    /**
     * Stored hash code.
     */
    transient protected int hash = -1;


    /**
     * Stored bitLength.
     */
    transient protected long blen = -1;


    private static final Logger logger = LogManager.getLogger(GenPolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    // protected GenPolynomial() { ring = null; val = null; } // don't use


    /**
     * Private constructor for GenPolynomial.
     * @param r polynomial ring factory.
     * @param t TreeMap with correct ordering.
     */
    private GenPolynomial(GenPolynomialRing<C> r, TreeMap<ExpVector, C> t) {
        ring = r;
        val = t;
        if (ring.checkPreempt) {
            if (Thread.currentThread().isInterrupted()) {
                logger.debug("throw PreemptingException");
                throw new PreemptingException();
            }
        }
    }


    /**
     * Constructor for zero GenPolynomial.
     * @param r polynomial ring factory.
     */
    public GenPolynomial(GenPolynomialRing<C> r) {
        this(r, new TreeMap<ExpVector, C>(r.tord.getDescendComparator()));
    }


    /**
     * Constructor for GenPolynomial c * x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e exponent.
     */
    public GenPolynomial(GenPolynomialRing<C> r, C c, ExpVector e) {
        this(r);
        if (!c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for GenPolynomial c * x<sup>0</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     */
    public GenPolynomial(GenPolynomialRing<C> r, C c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for GenPolynomial x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param e exponent.
     */
    public GenPolynomial(GenPolynomialRing<C> r, ExpVector e) {
        this(r, r.coFac.getONE(), e);
    }


    /**
     * Constructor for GenPolynomial.
     * @param r polynomial ring factory.
     * @param v the SortedMap of some other polynomial.
     */
    protected GenPolynomial(GenPolynomialRing<C> r, SortedMap<ExpVector, C> v) {
        this(r);
        if (v.size() > 0) {
            GenPolynomialRing.creations++;
            val.putAll(v); // assume no zero coefficients and val is empty
            assert !val.values().contains(r.coFac.getZERO()) : "illegal coefficient zero: " + v;
        }
    }


    /**
     * Constructor for GenPolynomial.
     * @param r polynomial ring factory.
     * @param v some Map from ExpVector to coefficients.
     */
    protected GenPolynomial(GenPolynomialRing<C> r, Map<ExpVector, C> v) {
        this(r);
        if (v.size() > 0) {
            GenPolynomialRing.creations++;
            val.putAll(v); // assume no zero coefficients and val is empty
            assert !val.values().contains(r.coFac.getZERO()) : "illegal coefficient zero: " + v;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public GenPolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Copy this GenPolynomial.
     * @return copy of this.
     */
    public GenPolynomial<C> copy() {
        return new GenPolynomial<C>(ring, this.val);
    }


    /**
     * Length of GenPolynomial.
     * @return number of coefficients of this GenPolynomial.
     */
    public int length() {
        return val.size();
    }


    /**
     * ExpVector to coefficient map of GenPolynomial.
     * @return val as unmodifiable SortedMap.
     */
    public SortedMap<ExpVector, C> getMap() {
        // return val;
        return Collections.<ExpVector, C> unmodifiableSortedMap(val);
    }


    /**
     * Put an ExpVector to coefficient entry into the internal map of this
     * GenPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param c coefficient.
     * @param e exponent.
     */
    public void doPutToMap(ExpVector e, C c) {
        if (debug) {
            C a = val.get(e);
            if (a != null) {
                logger.error("map entry exists " + e + " to " + a + " new " + c);
            }
            hash = -1;
            blen = -1;
        }
        if (!c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Remove an ExpVector to coefficient entry from the internal map of this
     * GenPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param e exponent.
     * @param c expected coefficient, null for ignore.
     */
    public void doRemoveFromMap(ExpVector e, C c) {
        C b = val.remove(e);
        if (true) { //||debug
            hash = -1;
            blen = -1;
            if (c == null) { // ignore b
                return;
            }
            if (!c.equals(b)) {
                logger.error("map entry wrong " + e + " to " + c + " old " + b);
                throw new RuntimeException("c != b");
            }
        }
    }


    /**
     * Put an a sorted map of exponents to coefficients into the internal map of
     * this GenPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param vals sorted map of exponents and coefficients.
     */
    public void doPutToMap(SortedMap<ExpVector, C> vals) {
        for (Map.Entry<ExpVector, C> me : vals.entrySet()) {
            ExpVector e = me.getKey();
            if (debug) {
                C a = val.get(e);
                if (a != null) {
                    logger.error("map entry exists " + e + " to " + a + " new " + me.getValue());
                }
                hash = -1;
                blen = -1;
            }
            C c = me.getValue();
            if (!c.isZERO()) {
                val.put(e, c);
            }
        }
    }


    /**
     * String representation of GenPolynomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (ring.vars != null) {
            return toString(ring.vars);
        }
        StringBuffer s = new StringBuffer();
        s.append(this.getClass().getSimpleName() + ":");
        s.append(ring.coFac.getClass().getSimpleName());
        if (ring.coFac.characteristic().signum() != 0) {
            s.append("(" + ring.coFac.characteristic() + ")");
        }
        s.append("[ ");
        boolean first = true;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append(m.getValue().toString());
            s.append(" ");
            s.append(m.getKey().toString());
        }
        s.append(" ] "); // no not use: ring.toString() );
        return s.toString();
    }


    /**
     * String representation of GenPolynomial.
     * @param v names for variables.
     * @see java.lang.Object#toString()
     */
    public String toString(String[] v) {
        StringBuffer s = new StringBuffer();
        if (PrettyPrint.isTrue()) {
            if (val.isEmpty()) {
                s.append("0");
            } else {
                // s.append( "( " );
                boolean first = true;
                for (Map.Entry<ExpVector, C> m : val.entrySet()) {
                    C c = m.getValue();
                    if (first) {
                        first = false;
                    } else {
                        if (c.signum() < 0) {
                            s.append(" - ");
                            c = c.negate();
                        } else {
                            s.append(" + ");
                        }
                    }
                    ExpVector e = m.getKey();
                    if (!c.isONE() || e.isZERO()) {
                        String cs = c.toString();
                        //if (c instanceof GenPolynomial || c instanceof AlgebraicNumber) {
                        if (cs.indexOf("-") >= 0 || cs.indexOf("+") >= 0) {
                            s.append("( ");
                            s.append(cs);
                            s.append(" )");
                        } else {
                            s.append(cs);
                        }
                        s.append(" ");
                    }
                    if (e != null && v != null) {
                        s.append(e.toString(v));
                    } else {
                        s.append(e);
                    }
                }
                //s.append(" )");
            }
        } else {
            s.append(this.getClass().getSimpleName() + "[ ");
            if (val.isEmpty()) {
                s.append("0");
            } else {
                boolean first = true;
                for (Map.Entry<ExpVector, C> m : val.entrySet()) {
                    C c = m.getValue();
                    if (first) {
                        first = false;
                    } else {
                        if (c.signum() < 0) {
                            s.append(" - ");
                            c = c.negate();
                        } else {
                            s.append(" + ");
                        }
                    }
                    ExpVector e = m.getKey();
                    if (!c.isONE() || e.isZERO()) {
                        s.append(c.toString());
                        s.append(" ");
                    }
                    s.append(e.toString(v));
                }
            }
            s.append(" ] "); // no not use: ring.toString() );
        }
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        if (isZERO()) {
            return "0";
        }
        StringBuffer s = new StringBuffer();
        if (val.size() > 1) {
            s.append("( ");
        }
        String[] v = ring.vars;
        if (v == null) {
            v = GenPolynomialRing.newVars("x", ring.nvar);
        }
        Pattern klammern = Pattern.compile("\\s*\\(.+\\)\\s*");
        boolean first = true;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            C c = m.getValue();
            if (first) {
                first = false;
            } else {
                if (c.signum() < 0) {
                    s.append(" - ");
                    c = c.negate();
                } else {
                    s.append(" + ");
                }
            }
            ExpVector e = m.getKey();
            String cs = c.toScript();
            boolean parenthesis = (cs.indexOf("-") >= 0 || cs.indexOf("+") >= 0)
                            && !klammern.matcher(cs).matches();
            if (!c.isONE() || e.isZERO()) {
                if (parenthesis) {
                    s.append("( ");
                }
                s.append(cs);
                if (parenthesis) {
                    s.append(" )");
                }
                if (!e.isZERO()) {
                    s.append(" * ");
                }
            }
            s.append(e.toScript(v));
        }
        if (val.size() > 1) {
            s.append(" )");
        }
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * Is GenPolynomial&lt;C&gt; zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.isEmpty();
    }


    /**
     * Is GenPolynomial&lt;C&gt; one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.evzero);
        if (c == null) {
            return false;
        }
        return c.isONE();
    }


    /**
     * Is GenPolynomial&lt;C&gt; a unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.evzero);
        if (c == null) {
            return false;
        }
        return c.isUnit();
    }


    /**
     * Is GenPolynomial&lt;C&gt; a constant.
     * @return If this is a constant polynomial then true is returned, else
     *         false.
     */
    public boolean isConstant() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.evzero);
        if (c == null) {
            return false;
        }
        return true;
    }


    /**
     * Is GenPolynomial&lt;C&gt; homogeneous.
     * @return true, if this is homogeneous, else false.
     */
    public boolean isHomogeneous() {
        if (val.size() <= 1) {
            return true;
        }
        long deg = -1;
        for (ExpVector e : val.keySet()) {
            if (deg < 0) {
                deg = e.totalDeg();
            } else if (deg != e.totalDeg()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (B == null) {
            return false;
        }
        if (!(B instanceof GenPolynomial)) {
            return false;
        }
        GenPolynomial<C> a = (GenPolynomial<C>) B;
        return this.compareTo(a) == 0;
    }


    /**
     * Hash code for this polynomial.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = hash;
        if (h == -1) {
            //not in sync with equals(): h = (ring.hashCode() << 27);
            h = val.hashCode();
            hash = h;
            //System.out.println("GenPolynomial.hashCode: " + h);
        }
        return h;
    }


    /**
     * GenPolynomial comparison.
     * @param b GenPolynomial.
     * @return sign(this-b).
     */
    public int compareTo(GenPolynomial<C> b) {
        if (b == null) {
            return 1;
        }
        SortedMap<ExpVector, C> av = this.val;
        SortedMap<ExpVector, C> bv = b.val;
        Iterator<Map.Entry<ExpVector, C>> ai = av.entrySet().iterator();
        Iterator<Map.Entry<ExpVector, C>> bi = bv.entrySet().iterator();
        int s = 0;
        int c = 0;
        while (ai.hasNext() && bi.hasNext()) {
            Map.Entry<ExpVector, C> aie = ai.next();
            Map.Entry<ExpVector, C> bie = bi.next();
            ExpVector ae = aie.getKey();
            ExpVector be = bie.getKey();
            s = ae.compareTo(be);
            if (s != 0) {
                //System.out.println("s = " + s + ", " + ring.toScript(ae) + ", " + ring.toScript(be));
                return s;
            }
            if (c == 0) {
                C ac = aie.getValue(); //av.get(ae);
                C bc = bie.getValue(); //bv.get(be);
                c = ac.compareTo(bc);
            }
        }
        if (ai.hasNext()) {
            //System.out.println("ai = " + ai);
            return 1;
        }
        if (bi.hasNext()) {
            //System.out.println("bi = " + bi);
            return -1;
        }
        //if (c != 0) {
        //System.out.println("c = " + c);
        //}
        // now all keys are equal
        return c;
    }


    /**
     * GenPolynomial signum.
     * @return sign(ldcf(this)).
     */
    public int signum() {
        if (this.isZERO()) {
            return 0;
        }
        ExpVector t = val.firstKey();
        C c = val.get(t);
        return c.signum();
    }


    /**
     * Number of variables.
     * @return ring.nvar.
     */
    public int numberOfVariables() {
        return ring.nvar;
    }


    /**
     * Leading monomial.
     * @return first map entry.
     */
    public Map.Entry<ExpVector, C> leadingMonomial() {
        if (val.isEmpty()) {
            return null;
        }
        //Iterator<Map.Entry<ExpVector, C>> ai = val.entrySet().iterator();
        //return ai.next();
        ExpVector e = val.firstKey();
        return new MapEntry<ExpVector, C>(e, val.get(e));
    }


    /**
     * Leading exponent vector.
     * @return first exponent.
     */
    public ExpVector leadingExpVector() {
        if (val.isEmpty()) {
            return null; // ring.evzero? needs many changes 
        }
        return val.firstKey();
    }


    /**
     * Trailing exponent vector.
     * @return last exponent.
     */
    public ExpVector trailingExpVector() {
        if (val.isEmpty()) {
            return null; //ring.evzero; // or null ?;
        }
        return val.lastKey();
    }


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingBaseCoefficient() {
        if (val.isEmpty()) {
            return ring.coFac.getZERO();
        }
        return val.get(val.firstKey());
    }


    /**
     * Trailing base coefficient.
     * @return coefficient of constant term.
     */
    public C trailingBaseCoefficient() {
        C c = val.get(ring.evzero);
        if (c == null) {
            return ring.coFac.getZERO();
        }
        return c;
    }


    /**
     * Coefficient.
     * @param e exponent.
     * @return coefficient for given exponent.
     */
    public C coefficient(ExpVector e) {
        C c = val.get(e);
        if (c == null) {
            c = ring.coFac.getZERO();
        }
        return c;
    }


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public GenPolynomial<C> reductum() {
        if (val.size() <= 1) {
            return ring.getZERO();
        }
        Iterator<ExpVector> ai = val.keySet().iterator();
        ExpVector lt = ai.next();
        lt = ai.next(); // size > 1
        SortedMap<ExpVector, C> red = val.tailMap(lt);
        GenPolynomial<C> r = ring.getZERO().copy();
        r.doPutToMap(red); //  new GenPolynomial<C>(ring, red);
        return r;
    }


    /**
     * Degree in variable i.
     * @return maximal degree in the variable i.
     */
    public long degree(int i) {
        if (val.isEmpty()) {
            return -1L; // 0 or -1 ?;
        }
        int j;
        if (i >= 0) {
            j = ring.nvar - 1 - i;
        } else { // python like -1 means main variable
            j = ring.nvar + i;
        }
        long deg = 0;
        if (j < 0) {
            return deg;
        }
        for (ExpVector e : val.keySet()) {
            long d = e.getVal(j);
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Maximal degree.
     * @return maximal degree in any variables.
     */
    public long degree() {
        if (val.isEmpty()) {
            return -1L; // 0 or -1 ?;
        }
        long deg = 0;
        for (ExpVector e : val.keySet()) {
            long d = e.maxDeg();
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Minimal degree. <b>Author:</b> Youssef Elbarbary
     * @return minimal degree in any variables.
     */
    public long degreeMin() {
        if (val.isEmpty()) {
            return -1L; // 0 or -1 ?;
        }
        long deg = Long.MAX_VALUE;
        for (ExpVector e : val.keySet()) {
            long d = e.minDeg();
            if (d < deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Total degree.
     * @return total degree in any variables.
     */
    public long totalDegree() {
        if (val.isEmpty()) {
            return -1L; // 0 or -1 ?;
        }
        long deg = 0;
        for (ExpVector e : val.keySet()) {
            long d = e.totalDeg();
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Weight degree.
     * @return weight degree in all variables.
     */
    public long weightDegree() {
        long[][] w = ring.tord.getWeight();
        if (w == null || w.length == 0) {
            return totalDegree(); // assume weight 1 
        }
        if (val.isEmpty()) {
            return -1L; // 0 or -1 ?;
        }
        long deg = 0;
        for (ExpVector e : val.keySet()) {
            long d = e.weightDeg(w);
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Leading weight polynomial.
     * @return polynomial with terms of maximal weight degree.
     */
    public GenPolynomial<C> leadingWeightPolynomial() {
        if (val.isEmpty()) {
            return ring.getZERO();
        }
        long[][] w = ring.tord.getWeight();
        long maxw = weightDegree();
        GenPolynomial<C> wp = ring.getZERO().copy();
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            ExpVector e = m.getKey();
            long d = e.weightDeg(w);
            if (d >= maxw) {
                wp.val.put(e, m.getValue());
            }
        }
        return wp;
    }


    /**
     * Leading facet normal polynomial.
     * @param u leading exponent vector.
     * @param uv exponent vector of facet normal.
     * @return polynomial with terms of facet normal.
     */
    public GenPolynomial<C> leadingFacetPolynomial(ExpVector u, ExpVector uv) {
        if (val.isEmpty()) {
            return ring.getZERO();
        }
        long[] normal = uv.getVal();
        GenPolynomial<C> fp = ring.getZERO().copy();
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            ExpVector e = m.getKey();
            if (u.equals(e)) {
                fp.val.put(e, m.getValue());
            } else {
                ExpVector v = u.subtract(e);
                if (v.compareTo(uv) == 0) { // || v.negate().compareTo(uv) == 0
                    fp.val.put(e, m.getValue());
                } else { // check for v parallel to uv
                    long ab = v.weightDeg(normal); //scalarProduct(v, uv);
                    long a = v.weightDeg(v.getVal()); //scalarProduct(v, v);
                    long b = uv.weightDeg(normal); //scalarProduct(uv, uv);
                    if (ab * ab == a * b) { // cos == 1
                        fp.val.put(e, m.getValue());
                        logger.info("ab = " + ab + ", a = " + a + ", b = " + b + ", u = " + u + ", e = " + e
                                        + ", v = " + v);
                    }
                }
            }
        }
        return fp;
    }


    /**
     * Is GenPolynomial&lt;C&gt; homogeneous with respect to a weight.
     * @return true, if this is weight homogeneous, else false.
     */
    public boolean isWeightHomogeneous() {
        if (val.size() <= 1) {
            return true;
        }
        long[][] w = ring.tord.getWeight();
        if (w == null || w.length == 0) {
            return isHomogeneous(); // assume weights = 1
        }
        long deg = -1;
        for (ExpVector e : val.keySet()) {
            if (deg < 0) {
                deg = e.weightDeg(w);
            } else if (deg != e.weightDeg(w)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Maximal degree vector.
     * @return maximal degree vector of all variables.
     */
    public ExpVector degreeVector() {
        if (val.isEmpty()) {
            return null; //deg;
        }
        ExpVector deg = ring.evzero;
        for (ExpVector e : val.keySet()) {
            deg = deg.lcm(e);
        }
        return deg;
    }


    /**
     * Delta of exponent vectors.
     * @return list of u-v, where u = lt() and v != u in this.
     */
    public List<ExpVector> deltaExpVectors() {
        List<ExpVector> de = new ArrayList<ExpVector>(val.size());
        if (val.isEmpty()) {
            return de;
        }
        ExpVector u = null;
        for (ExpVector e : val.keySet()) {
            if (u == null) {
                u = e;
            } else {
                ExpVector v = u.subtract(e);
                de.add(v);
            }
        }
        return de;
    }


    /**
     * Delta of exponent vectors.
     * @param u marked ExpVector in this.expVectors
     * @return list of u-v, where v != u in this.expVectors.
     */
    public List<ExpVector> deltaExpVectors(ExpVector u) {
        List<ExpVector> de = new ArrayList<ExpVector>(val.size());
        if (val.isEmpty()) {
            return de;
        }
        for (ExpVector e : val.keySet()) {
            ExpVector v = u.subtract(e);
            if (v.isZERO()) {
                continue;
            }
            de.add(v);
        }
        return de;
    }


    /**
     * GenPolynomial maximum norm.
     * @return ||this|| the maximum of all absolute values of coefficients.
     */
    public C maxNorm() {
        C n = ring.getZEROCoefficient();
        for (C c : val.values()) {
            C x = c.abs();
            if (n.compareTo(x) < 0) {
                n = x;
            }
        }
        return n;
    }


    /**
     * GenPolynomial sum norm.
     * @return sum of all absolute values of coefficients.
     */
    public C sumNorm() {
        C n = ring.getZEROCoefficient();
        for (C c : val.values()) {
            C x = c.abs();
            n = n.sum(x);
        }
        return n;
    }


    /**
     * GenPolynomial square norm.
     * @return the sum all squared values of coefficients.
     */
    public C squareNorm() {
        C n = ring.getZEROCoefficient();
        for (C c : val.values()) {
            C x = c.abs();
            x = x.multiply(x);
            n = n.sum(x);
        }
        return n;
    }


    /**
     * GenPolynomial summation.
     * @param S GenPolynomial.
     * @return this+S.
     */
    //public <T extends GenPolynomial<C>> T sum(T /*GenPolynomial<C>*/ S) {
    public GenPolynomial<C> sum(GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (this.length() < (3 * S.length()) / 5) {
            return S.sum(this); // performance
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector e = me.getKey();
            C y = me.getValue(); // assert y != null
            C x = nv.get(e);
            if (x != null) {
                x = x.sum(y);
                if (!x.isZERO()) {
                    nv.put(e, x);
                } else {
                    nv.remove(e);
                }
            } else {
                nv.put(e, y);
            }
        }
        return n;
    }


    /**
     * GenPolynomial addition. This method is not very efficient, since this is
     * copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this + a x<sup>e</sup>.
     */
    public GenPolynomial<C> sum(C a, ExpVector e) {
        if (a == null) {
            return this;
        }
        if (a.isZERO()) {
            return this;
        }
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        //if ( nv.size() == 0 ) { nv.put(e,a); return n; }
        C x = nv.get(e);
        if (x != null) {
            x = x.sum(a);
            if (!x.isZERO()) {
                nv.put(e, x);
            } else {
                nv.remove(e);
            }
        } else {
            nv.put(e, a);
        }
        return n;
    }


    /**
     * GenPolynomial addition. This method is not very efficient, since this is
     * copied.
     * @param m monomial.
     * @return this + m.
     */
    public GenPolynomial<C> sum(Monomial<C> m) {
        return sum(m.coefficient(), m.exponent());
    }


    /**
     * GenPolynomial addition. This method is not very efficient, since this is
     * copied.
     * @param a coefficient.
     * @return this + a x<sup>0</sup>.
     */
    public GenPolynomial<C> sum(C a) {
        return sum(a, ring.evzero);
    }


    /**
     * GenPolynomial destructive summation.
     * @param S GenPolynomial.
     */
    public void doAddTo(GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return;
        }
        if (this.isZERO()) {
            this.val.putAll(S.val);
            return;
        }
        assert (ring.nvar == S.ring.nvar);
        SortedMap<ExpVector, C> nv = this.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector e = me.getKey();
            C y = me.getValue(); // assert y != null
            C x = nv.get(e);
            if (x != null) {
                x = x.sum(y);
                if (!x.isZERO()) {
                    nv.put(e, x);
                } else {
                    nv.remove(e);
                }
            } else {
                nv.put(e, y);
            }
        }
        return;
    }


    /**
     * GenPolynomial destructive summation.
     * @param a coefficient.
     * @param e exponent.
     */
    public void doAddTo(C a, ExpVector e) {
        if (a == null || a.isZERO()) {
            return;
        }
        SortedMap<ExpVector, C> nv = this.val;
        C x = nv.get(e);
        if (x != null) {
            x = x.sum(a);
            if (!x.isZERO()) {
                nv.put(e, x);
            } else {
                nv.remove(e);
            }
        } else {
            nv.put(e, a);
        }
        return;
    }


    /**
     * GenPolynomial destructive summation.
     * @param a coefficient.
     */
    public void doAddTo(C a) {
        doAddTo(a, ring.evzero);
    }


    /**
     * GenPolynomial subtraction.
     * @param S GenPolynomial.
     * @return this-S.
     */
    public GenPolynomial<C> subtract(GenPolynomial<C> S) {
        if (S == null) {
            return this;
        }
        if (S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.negate();
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector e = me.getKey();
            C y = me.getValue(); // assert y != null
            C x = nv.get(e);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(e, x);
                } else {
                    nv.remove(e);
                }
            } else {
                nv.put(e, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial subtraction. This method is not very efficient, since this
     * is copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this - a x<sup>e</sup>.
     */
    public GenPolynomial<C> subtract(C a, ExpVector e) {
        if (a == null || a.isZERO()) {
            return this;
        }
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        C x = nv.get(e);
        if (x != null) {
            x = x.subtract(a);
            if (!x.isZERO()) {
                nv.put(e, x);
            } else {
                nv.remove(e);
            }
        } else {
            nv.put(e, a.negate());
        }
        return n;
    }


    /**
     * GenPolynomial subtraction. This method is not very efficient, since this
     * is copied.
     * @param m monomial.
     * @return this - m.
     */
    public GenPolynomial<C> subtract(Monomial<C> m) {
        return subtract(m.coefficient(), m.exponent());
    }


    /**
     * GenPolynomial subtract. This method is not very efficient, since this is
     * copied.
     * @param a coefficient.
     * @return this + a x<sup>0</sup>.
     */
    public GenPolynomial<C> subtract(C a) {
        return subtract(a, ring.evzero);
    }


    /**
     * GenPolynomial subtract a multiple.
     * @param a coefficient.
     * @param S GenPolynomial.
     * @return this - a S.
     */
    public GenPolynomial<C> subtractMultiple(C a, GenPolynomial<C> S) {
        if (a == null || a.isZERO()) {
            return this;
        }
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.multiply(a.negate());
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            C y = me.getValue(); // assert y != null
            y = a.multiply(y);
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial subtract a multiple.
     * @param a coefficient.
     * @param e exponent.
     * @param S GenPolynomial.
     * @return this - a x<sup>e</sup> S.
     */
    public GenPolynomial<C> subtractMultiple(C a, ExpVector e, GenPolynomial<C> S) {
        if (a == null || a.isZERO()) {
            return this;
        }
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.multiply(a.negate(), e);
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y);
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial scale and subtract a multiple.
     * @param b scale factor.
     * @param a coefficient.
     * @param S GenPolynomial.
     * @return this * b - a S.
     */
    public GenPolynomial<C> scaleSubtractMultiple(C b, C a, GenPolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiply(b);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiply(b);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiply(a.negate()); //left?
        }
        if (b.isONE()) {
            return subtractMultiple(a, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.multiply(b);
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            //f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // now y can be zero
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial scale and subtract a multiple.
     * @param b scale factor.
     * @param a coefficient.
     * @param e exponent.
     * @param S GenPolynomial.
     * @return this * b - a x<sup>e</sup> S.
     */
    public GenPolynomial<C> scaleSubtractMultiple(C b, C a, ExpVector e, GenPolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiply(b);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiply(b);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiply(a.negate(), e);
        }
        if (b.isONE()) {
            return subtractMultiple(a, e, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.multiply(b);
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // now y can be zero
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial scale and subtract a multiple.
     * @param b scale factor.
     * @param g scale exponent.
     * @param a coefficient.
     * @param e exponent.
     * @param S GenPolynomial.
     * @return this * a x<sup>g</sup> - a x<sup>e</sup> S.
     */
    public GenPolynomial<C> scaleSubtractMultiple(C b, ExpVector g, C a, ExpVector e, GenPolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiply(b, g);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiply(b, g);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiply(a.negate(), e);
        }
        if (b.isONE() && g.isZERO()) {
            return subtractMultiple(a, e, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenPolynomial<C> n = this.multiply(b, g);
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // y can be zero now
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenPolynomial negation, alternative implementation.
     * @return -this.
     */
    public GenPolynomial<C> negateAlt() {
        GenPolynomial<C> n = ring.getZERO().copy();
        SortedMap<ExpVector, C> v = n.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            C x = m.getValue(); // != null, 0
            v.put(m.getKey(), x.negate());
        }
        return n;
    }


    /**
     * GenPolynomial negation.
     * @return -this.
     */
    public GenPolynomial<C> negate() {
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> v = n.val;
        for (Map.Entry<ExpVector, C> m : v.entrySet()) {
            C x = m.getValue(); // != null, 0
            m.setValue(x.negate()); // okay
        }
        return n;
    }


    /**
     * GenPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
     * @return abs(this).
     */
    public GenPolynomial<C> abs() {
        if (leadingBaseCoefficient().signum() < 0) {
            return this.negate();
        }
        return this;
    }


    /**
     * GenPolynomial multiplication.
     * @param S GenPolynomial.
     * @return this*S.
     */
    public GenPolynomial<C> multiply(GenPolynomial<C> S) {
        if (S == null) {
            return ring.getZERO();
        }
        if (S.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.nvar == S.ring.nvar);
        if (this instanceof GenSolvablePolynomial && S instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(S) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
            return T.multiply(Sp);
        }
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            for (Map.Entry<ExpVector, C> m2 : S.val.entrySet()) {
                C c2 = m2.getValue();
                ExpVector e2 = m2.getKey();
                C c = c1.multiply(c2); // check non zero if not domain
                if (!c.isZERO()) {
                    ExpVector e = e1.sum(e2);
                    C c0 = pv.get(e);
                    if (c0 == null) {
                        pv.put(e, c);
                    } else {
                        c0 = c0.sum(c);
                        if (!c0.isZERO()) {
                            pv.put(e, c0);
                        } else {
                            pv.remove(e);
                        }
                    }
                }
            }
        }
        return p;
    }


    /**
     * GenPolynomial multiplication. Product with coefficient ring element.
     * @param s coefficient.
     * @return this*s.
     */
    public GenPolynomial<C> multiply(C s) {
        if (s == null || s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (this instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(s) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            return T.multiply(s);
        }
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            C a = m.getValue();
            ExpVector e = m.getKey();
            C c = a.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                pv.put(e, c); // not m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenPolynomial left multiplication. Left product with coefficient ring
     * element.
     * @param s coefficient.
     * @return s*this.
     */
    public GenPolynomial<C> multiplyLeft(C s) {
        if (s == null || s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (this instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(s) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            return T.multiplyLeft(s);
        }
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            C a = m.getValue();
            ExpVector e = m.getKey();
            C c = s.multiply(a);
            if (!c.isZERO()) {
                pv.put(e, c);
            }
        }
        return p;
    }


    /**
     * GenPolynomial monic, i.e. leadingCoefficient == 1. If leadingCoefficient
     * is not invertible returns this unmodified.
     * @return monic(this).
     */
    public GenPolynomial<C> monic() {
        if (this.isZERO()) {
            return this;
        }
        C lc = leadingBaseCoefficient();
        if (!lc.isUnit()) {
            //System.out.println("lc = "+lc);
            return this;
        }
        C lm = lc.inverse();
        return multiplyLeft(lm);
    }


    /**
     * GenPolynomial multiplication. Product with ring element and exponent
     * vector.
     * @param s coefficient.
     * @param e exponent.
     * @return this * s x<sup>e</sup>.
     */
    public GenPolynomial<C> multiply(C s, ExpVector e) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (e == null) { // exp vector of zero polynomial
            return ring.getZERO();
        }
        if (this instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(s,e) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            return T.multiply(s, e);
        }
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            C c = c1.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                ExpVector e2 = e1.sum(e);
                pv.put(e2, c);
            }
        }
        return p;
    }


    /**
     * GenPolynomial multiplication. Product with exponent vector.
     * @param e exponent (!= null).
     * @return this * x<sup>e</sup>.
     */
    public GenPolynomial<C> multiply(ExpVector e) {
        if (e == null) { // exp vector of zero polynomial
            return ring.getZERO();
        }
        // assert e != null. This is seldom allowed.
        if (this.isZERO()) {
            return this;
        }
        if (this instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(e) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            return T.multiply(e);
        }
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            ExpVector e2 = e1.sum(e);
            pv.put(e2, c1);
        }
        return p;
    }


    /**
     * GenPolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m.
     */
    public GenPolynomial<C> multiply(Map.Entry<ExpVector, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * GenPolynomial division. Division by coefficient ring element. Fails, if
     * exact division is not possible.
     * @param s coefficient.
     * @return s**(-1) * this.
     */
    public GenPolynomial<C> divide(C s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        if (this.isZERO()) {
            return this;
        }
        //C t = s.inverse();
        //return multiply(t);
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s);
            if (debug) {
                C x = c1.remainder(s);
                if (!x.isZERO()) {
                    logger.info("divide x = " + x);
                    throw new ArithmeticException("no exact division: " + c1 + "/" + s);
                }
            }
            if (c.isZERO()) {
                throw new ArithmeticException("no exact division: " + c1 + "/" + s + ", in " + this);
            }
            pv.put(e, c); // not m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial right division. Right division by coefficient ring element.
     * Fails, if exact division is not possible.
     * @param s coefficient.
     * @return this * s**(-1).
     */
    public GenPolynomial<C> rightDivideCoeff(C s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        if (this.isZERO()) {
            return this;
        }
        //C t = s.inverse();
        //return multiply(t);
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.rightDivide(s);
            if (debug) {
                C x = c1.rightRemainder(s);
                if (!x.isZERO()) {
                    logger.info("divide x = " + x);
                    throw new ArithmeticException("no exact division: " + c1 + "/" + s);
                }
            }
            if (c.isZERO()) {
                throw new ArithmeticException("no exact division: " + c1 + "/" + s + ", in " + this);
            }
            pv.put(e, c); // not m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial left division. Left division by coefficient ring element.
     * Fails, if exact division is not possible.
     * @param s coefficient.
     * @return s**(-1) * this.
     */
    public GenPolynomial<C> leftDivideCoeff(C s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        if (this.isZERO()) {
            return this;
        }
        //C t = s.inverse();
        //return multiply(t);
        GenPolynomial<C> p = ring.getZERO().copy();
        SortedMap<ExpVector, C> pv = p.val;
        for (Map.Entry<ExpVector, C> m : val.entrySet()) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.leftDivide(s);
            if (debug) {
                C x = c1.leftRemainder(s);
                if (!x.isZERO()) {
                    logger.info("divide x = " + x);
                    throw new ArithmeticException("no exact division: " + c1 + "/" + s);
                }
            }
            if (c.isZERO()) {
                throw new ArithmeticException("no exact division: " + c1 + "/" + s + ", in " + this);
            }
            pv.put(e, c); // not m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial division with remainder. Fails, if exact division by
     * leading base coefficient is not possible. Meaningful only for univariate
     * polynomials over fields, but works in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder and
     *         deg(remainder) &lt; deg(S) or remiander = 0.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C>[] quotientRemainder(GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException("lbcf not invertible " + c);
        }
        C ci = c.inverse();
        assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> q = ring.getZERO().copy();
        GenPolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                ExpVector g = f.subtract(e);
                a = a.multiply(ci);
                q = q.sum(a, g);
                h = S.multiply(a, g);
                r = r.subtract(h);
                assert (!f.equals(r.leadingExpVector())) : "leadingExpVector not descending: " + f;
            } else {
                break;
            }
        }
        GenPolynomial<C>[] ret = new GenPolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenPolynomial division. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return quotient with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     */
    public GenPolynomial<C> divide(GenPolynomial<C> S) {
        if (this instanceof GenSolvablePolynomial || S instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            //logger.debug("warn: wrong method dispatch in JRE multiply(S) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
            return T.quotientRemainder(Sp)[0];
        }
        return quotientRemainder(S)[0];
    }


    /**
     * GenPolynomial remainder. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return remainder with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     */
    public GenPolynomial<C> remainder(GenPolynomial<C> S) {
        if (this instanceof GenSolvablePolynomial || S instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            //logger.debug("warn: wrong method dispatch in JRE multiply(S) - trying to fix");
            GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
            GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
            return T.quotientRemainder(Sp)[1];
        }
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException("lbc not invertible " + c);
        }
        C ci = c.inverse();
        assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                ExpVector g = f.subtract(e);
                //logger.info("red div = " + e);
                a = a.multiply(ci);
                h = S.multiply(a, g);
                r = r.subtract(h);
                assert (!f.equals(r.leadingExpVector())) : "leadingExpVector not descending: " + f;
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial greatest common divisor. Only for univariate polynomials
     * over fields.
     * @param S GenPolynomial.
     * @return gcd(this,S).
     */
    public GenPolynomial<C> gcd(GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (ring.nvar != 1) {
            throw new IllegalArgumentException("not univariate polynomials" + ring);
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        while (!r.isZERO()) {
            x = q.remainder(r);
            q = r;
            r = x;
        }
        return q.monic(); // normalize
    }


    /**
     * GenPolynomial extended greatest comon divisor. Only for univariate
     * polynomials over fields.
     * @param S GenPolynomial.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C>[] egcd(GenPolynomial<C> S) {
        GenPolynomial<C>[] ret = new GenPolynomial[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            ret[1] = this.ring.getONE();
            ret[2] = this.ring.getZERO();
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = S;
            ret[1] = this.ring.getZERO();
            ret[2] = this.ring.getONE();
            return ret;
        }
        if (ring.nvar != 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " not univariate polynomials" + ring);
        }
        if (this.isConstant() && S.isConstant()) {
            C t = this.leadingBaseCoefficient();
            C s = S.leadingBaseCoefficient();
            C[] gg = t.egcd(s);
            //System.out.println("coeff gcd = " + Arrays.toString(gg));
            GenPolynomial<C> z = this.ring.getZERO();
            ret[0] = z.sum(gg[0]);
            ret[1] = z.sum(gg[1]);
            ret[2] = z.sum(gg[2]);
            return ret;
        }
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        GenPolynomial<C> c1 = ring.getONE().copy();
        GenPolynomial<C> d1 = ring.getZERO().copy();
        GenPolynomial<C> c2 = ring.getZERO().copy();
        GenPolynomial<C> d2 = ring.getONE().copy();
        GenPolynomial<C> x1;
        GenPolynomial<C> x2;
        while (!r.isZERO()) {
            qr = q.quotientRemainder(r);
            q = qr[0];
            x1 = c1.subtract(q.multiply(d1));
            x2 = c2.subtract(q.multiply(d2));
            c1 = d1;
            c2 = d2;
            d1 = x1;
            d2 = x2;
            q = r;
            r = qr[1];
        }
        // normalize ldcf(q) to 1, i.e. make monic
        C g = q.leadingBaseCoefficient();
        if (g.isUnit()) {
            C h = g.inverse();
            q = q.multiply(h);
            c1 = c1.multiply(h);
            c2 = c2.multiply(h);
        }
        //assert ( ((c1.multiply(this)).sum( c2.multiply(S)).equals(q) )); 
        ret[0] = q;
        ret[1] = c1;
        ret[2] = c2;
        return ret;
    }


    /**
     * GenPolynomial half extended greatest comon divisor. Only for univariate
     * polynomials over fields.
     * @param S GenPolynomial.
     * @return [ gcd(this,S), a ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C>[] hegcd(GenPolynomial<C> S) {
        GenPolynomial<C>[] ret = new GenPolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            ret[1] = this.ring.getONE();
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = S;
            return ret;
        }
        if (ring.nvar != 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " not univariate polynomials" + ring);
        }
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        GenPolynomial<C> c1 = ring.getONE().copy();
        GenPolynomial<C> d1 = ring.getZERO().copy();
        GenPolynomial<C> x1;
        while (!r.isZERO()) {
            qr = q.quotientRemainder(r);
            q = qr[0];
            x1 = c1.subtract(q.multiply(d1));
            c1 = d1;
            d1 = x1;
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
        //assert ( ((c1.multiply(this)).remainder(S).equals(q) )); 
        ret[0] = q;
        ret[1] = c1;
        return ret;
    }


    /**
     * GenPolynomial inverse. Required by RingElem. Throws not invertible
     * exception.
     */
    public GenPolynomial<C> inverse() {
        if (isUnit()) { // only possible if ldbcf is unit
            C c = leadingBaseCoefficient().inverse();
            return ring.getONE().multiply(c);
        }
        throw new NotInvertibleException("element not invertible " + this + " :: " + ring);
    }


    /**
     * GenPolynomial modular inverse. Only for univariate polynomials over
     * fields.
     * @param m GenPolynomial.
     * @return a with with a*this = 1 mod m.
     */
    public GenPolynomial<C> modInverse(GenPolynomial<C> m) {
        if (this.isZERO()) {
            throw new NotInvertibleException("zero is not invertible");
        }
        GenPolynomial<C>[] hegcd = this.hegcd(m);
        GenPolynomial<C> a = hegcd[0];
        if (!a.isUnit()) { // gcd != 1
            throw new AlgebraicNotInvertibleException("element not invertible, gcd != 1", m, a, m.divide(a));
        }
        GenPolynomial<C> b = hegcd[1];
        if (b.isZERO()) { // when m divides this, e.g. m.isUnit()
            throw new NotInvertibleException("element not invertible, divisible by modul");
        }
        return b;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend all ExpVectors by
     * i elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public GenPolynomial<C> extend(GenPolynomialRing<C> pfac, int j, long k) {
        if (ring.equals(pfac)) { // nothing to do
            return this;
        }
        GenPolynomial<C> Cp = pfac.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        int i = pfac.nvar - ring.nvar;
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector f = e.extend(i, j, k);
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * Extend lower variables. Used e.g. in module embedding. Extend all
     * ExpVectors by i lower elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public GenPolynomial<C> extendLower(GenPolynomialRing<C> pfac, int j, long k) {
        if (ring.equals(pfac)) { // nothing to do
            return this;
        }
        GenPolynomial<C> Cp = pfac.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        int i = pfac.nvar - ring.nvar;
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector f = e.extendLower(i, j, k);
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Remove i elements of
     * each ExpVector.
     * @param pfac contracted polynomial ring factory (by i variables).
     * @return Map of exponents and contracted polynomials. <b>Note:</b> could
     *         return SortedMap
     */
    public Map<ExpVector, GenPolynomial<C>> contract(GenPolynomialRing<C> pfac) {
        GenPolynomial<C> zero = pfac.getZERO(); //not pfac.coFac;
        TermOrder t = new TermOrder(TermOrder.INVLEX);
        Map<ExpVector, GenPolynomial<C>> B = new TreeMap<ExpVector, GenPolynomial<C>>(
                        t.getAscendComparator());
        if (this.isZERO()) {
            return B;
        }
        int i = ring.nvar - pfac.nvar;
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector f = e.contract(0, i);
            ExpVector g = e.contract(i, e.length() - i);
            GenPolynomial<C> p = B.get(f);
            if (p == null) {
                p = zero;
            }
            p = p.sum(a, g);
            B.put(f, p);
        }
        return B;
    }


    /**
     * Contract variables to coefficient polynomial. Remove i elements of each
     * ExpVector, removed elements must be zero.
     * @param pfac contracted polynomial ring factory (by i variables).
     * @return contracted coefficient polynomial.
     */
    public GenPolynomial<C> contractCoeff(GenPolynomialRing<C> pfac) {
        Map<ExpVector, GenPolynomial<C>> ms = contract(pfac);
        GenPolynomial<C> c = pfac.getZERO();
        for (Map.Entry<ExpVector, GenPolynomial<C>> m : ms.entrySet()) {
            if (m.getKey().isZERO()) {
                c = m.getValue();
            } else {
                throw new RuntimeException("wrong coefficient contraction " + m + ", pol =  " + c);
            }
        }
        return c;
    }


    /**
     * Extend univariate to multivariate polynomial. This is an univariate
     * polynomial in variable i of the polynomial ring, it is extended to the
     * given polynomial ring.
     * @param pfac extended polynomial ring factory.
     * @param i index of the variable of this polynomial in pfac.
     * @return extended multivariate polynomial.
     */
    public GenPolynomial<C> extendUnivariate(GenPolynomialRing<C> pfac, int i) {
        if (i < 0 || pfac.nvar < i) {
            throw new IllegalArgumentException("index " + i + "out of range " + pfac.nvar);
        }
        if (ring.nvar != 1) {
            throw new IllegalArgumentException("polynomial not univariate " + ring.nvar);
        }
        if (this.isONE()) {
            return pfac.getONE();
        }
        int j = pfac.nvar - 1 - i;
        GenPolynomial<C> Cp = pfac.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            long n = e.getVal(0);
            C a = y.getValue();
            ExpVector f = ExpVector.create(pfac.nvar, j, n);
            C.put(f, a); // assert not contained
        }
        return Cp;
    }


    /**
     * Make homogeneous.
     * @param pfac extended polynomial ring factory (by 1 variable).
     * @return homogeneous polynomial.
     */
    public GenPolynomial<C> homogenize(GenPolynomialRing<C> pfac) {
        if (ring.equals(pfac)) { // not implemented
            throw new UnsupportedOperationException("case with same ring not implemented");
        }
        GenPolynomial<C> Cp = pfac.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        long deg = totalDegree();
        //int i = pfac.nvar - ring.nvar;
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long d = deg - e.totalDeg();
            ExpVector f = e.extend(1, 0, d);
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * Dehomogenize.
     * @param pfac contracted polynomial ring factory (by 1 variable).
     * @return in homogeneous polynomial.
     */
    public GenPolynomial<C> deHomogenize(GenPolynomialRing<C> pfac) {
        if (ring.equals(pfac)) { // not implemented
            throw new UnsupportedOperationException("case with same ring not implemented");
        }
        GenPolynomial<C> Cp = pfac.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector f = e.contract(1, pfac.nvar);
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial with reversed variables.
     */
    public GenPolynomial<C> reverse(GenPolynomialRing<C> oring) {
        GenPolynomial<C> Cp = oring.getZERO().copy();
        if (this.isZERO()) {
            return Cp;
        }
        int k = -1;
        if (oring.tord.getEvord2() != 0 && oring.partial) {
            k = oring.tord.getSplit();
        }

        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        ExpVector f;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector e = y.getKey();
            if (k >= 0) {
                f = e.reverse(k);
            } else {
                f = e.reverse();
            }
            C a = y.getValue();
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * GenPolynomial inflate. Only for univariate polynomials over fields.
     * @param e exponent.
     * @return this(x**e)
     */
    public GenPolynomial<C> inflate(long e) {
        if (e == 1) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        if (ring.nvar != 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " not univariate polynomial" + ring);
        }
        GenPolynomial<C> Cp = ring.getZERO().copy();
        Map<ExpVector, C> C = Cp.val; //getMap();
        Map<ExpVector, C> A = val;
        ExpVector f;
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            ExpVector g = y.getKey();
            f = g.scalarMultiply(e);
            C a = y.getValue();
            C.put(f, a);
        }
        return Cp;
    }


    /**
     * Iterator over coefficients.
     * @return val.values().iterator().
     */
    public Iterator<C> coefficientIterator() {
        return val.values().iterator();
    }


    /**
     * Iterator over exponents.
     * @return val.keySet().iterator().
     */
    public Iterator<ExpVector> exponentIterator() {
        return val.keySet().iterator();
    }


    /**
     * Iterator over monomials.
     * @return a PolyIterator.
     */
    public Iterator<Monomial<C>> iterator() {
        return new PolyIterator<C>(val);
    }


    /**
     * Spliterator over monomials.
     * @return a PolySpliterator.
     */
    public Spliterator<Monomial<C>> spliterator() {
        return new PolySpliterator<C>(val);
    }


    /**
     * Map a unary function to the coefficients.
     * @param f evaluation functor.
     * @return new polynomial with coefficients f(this.coefficients).
     */
    public GenPolynomial<C> map(final UnaryFunctor<? super C, C> f) {
        GenPolynomial<C> n = ring.getZERO().copy();
        SortedMap<ExpVector, C> nv = n.val;
        for (Map.Entry<ExpVector, C> m : this.val.entrySet()) {
            //logger.info("m = " + m);
            C c = f.eval(m.getValue());
            if (c != null && !c.isZERO()) {
                nv.put(m.getKey(), c);
            }
        }
        return n;
    }


    /*
     * Map a unary function to the coefficients.
     * @param f evaluation functor.
     * @return new polynomial with coefficients f(this.coefficients).
     */
    GenPolynomial<C> mapWrong(final UnaryFunctor<? super C, C> f) {
        GenPolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        for (Map.Entry<ExpVector, C> m : nv.entrySet()) {
            //logger.info("m = " + m);
            C c = f.eval(m.getValue());
            if (c != null && !c.isZERO()) {
                m.setValue(c); // not okay
            } else {
                // not possible nv.remove(m.getKey());
            }
        }
        return n;
    }


    /**
     * Map a function to the polynomial stream entries.
     * @param f evaluation functor.
     * @return new polynomial with f(this.entries).
     */
    public GenPolynomial<C> mapOnStream(
                    final Function<? super Map.Entry<ExpVector, C>, ? extends Map.Entry<ExpVector, C>> f) {
        return mapOnStream(f, false);
    }


    /**
     * Map a function to the polynomial stream entries.
     * @param f evaluation functor.
     * @return new polynomial with f(this.entries).
     */
    public GenPolynomial<C> mapOnStream(
                    final Function<? super Map.Entry<ExpVector, C>, ? extends Map.Entry<ExpVector, C>> f,
                    boolean parallel) {
        Stream<Map.Entry<ExpVector, C>> st;
        if (parallel) {
            st = val.entrySet().parallelStream();
        } else {
            st = val.entrySet().stream();
        }
        Map<ExpVector, C> m = st.map(f).filter(me -> !me.getValue().isZERO())
                        .collect(Collectors.toMap(me -> me.getKey(), me -> me.getValue()));
        //Stream<Map.Entry<ExpVector,C>> stp = st.map(f);
        //Stream<Map.Entry<ExpVector,C>> stf = stp.filter(me -> !me.getValue().isZERO());
        //Map<ExpVector,C> m = stf.collect(Collectors.toMap(me -> me.getKey(), me -> me.getValue()));
        return new GenPolynomial<C>(ring, m);
    }


    /**
     * Returns the number of bits in the representation of this polynomial.
     * @return number of bits in the representation of this polynomial,
     *         including sign bits.
     */
    public long bitLength() {
        if (blen < 0L) {
            long n = 0L;
            for (Monomial<C> m : this) {
                n += m.e.bitLength();
                //n += m.c.bitLength(); // todo add bitLength to Element
                try { // hack
                    Method method = m.c.getClass().getMethod("bitLength", (Class<?>[]) null);
                    n += (Long) method.invoke(m.c, (Object[]) null);
                } catch (NoSuchMethodException e) {
                    logger.error("Exception, class: " + m.c.getClass());
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    logger.error("Exception, class: " + m.c.getClass());
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    logger.error("Exception, class: " + m.c.getClass());
                    throw new RuntimeException(e);
                }
            }
            blen = n;
            //System.out.println("bitLength(poly) = " + blen);
        }
        return blen;
    }

    //private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    //    out.defaultWriteObject();
    //}


    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        blen = -1;
        hash = -1;
    }
}
