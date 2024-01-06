/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.PreemptingException;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;


/**
 * GenExteriorPolynomial generic polynomials implementing RingElem.
 * Antisymmetric polynomials (in fact vectors) over C. Objects of this class are
 * intended to be immutable. The implementation is based on TreeMap respectively
 * SortedMap from index lists to coefficients. Only the coefficients are modeled
 * with generic types, the "exponents" are fixed to IndexList. C can also be a
 * non integral domain, e.g. a ModInteger, i.e. it may contain zero divisors,
 * since multiply() does check for zero coefficients and index lists.
 * @see "masnc.DIPE.mi#EIVEPR from SAC2/MAS"
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public final class GenExteriorPolynomial<C extends RingElem<C>>
                implements RingElem<GenExteriorPolynomial<C>>, Iterable<IndexListMonomial<C>> {


    /**
     * The factory for the polynomial ring.
     */
    public final GenExteriorPolynomialRing<C> ring;


    /**
     * The data structure for polynomials.
     */
    final SortedMap<IndexList, C> val; // do not change to TreeMap


    private static final Logger logger = LogManager.getLogger(GenExteriorPolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    // protected GenExteriorPolynomial() { ring = null; val = null; } // don't use


    /**
     * Private constructor for GenExteriorPolynomial.
     * @param r polynomial ring factory.
     * @param t TreeMap with correct ordering.
     */
    private GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, TreeMap<IndexList, C> t) {
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
     * Constructor for zero GenExteriorPolynomial.
     * @param r polynomial ring factory.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r) {
        this(r, new TreeMap<IndexList, C>(r.ixfac.getDescendComparator()));
    }


    /**
     * Constructor for GenExteriorPolynomial c * x<sub>e</sub>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e word.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, C c, IndexList e) {
        this(r);
        if (!c.isZERO() && !e.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for GenExteriorPolynomial c * x<sub>0</sub>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, C c) {
        this(r, c, r.wone);
    }


    /**
     * Constructor for GenExteriorPolynomial x<sub>e</sub>.
     * @param r polynomial ring factory.
     * @param e index list.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, IndexList e) {
        this(r, r.coFac.getONE(), e);
    }


    /**
     * Constructor for GenExteriorPolynomial x<sub>e</sub>.
     * @param r polynomial ring factory.
     * @param e exponent vector.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, ExpVector e) {
        this(r, r.coFac.getONE(), r.ixfac.valueOf(e));
    }


    /**
     * Constructor for GenExteriorPolynomial c * x<sub>e</sub>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e exponent vector.
     */
    public GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, C c, ExpVector e) {
        this(r, c, r.ixfac.valueOf(e));
    }


    /**
     * Constructor for GenExteriorPolynomial.
     * @param r polynomial ring factory.
     * @param v the SortedMap of some other polynomial.
     */
    protected GenExteriorPolynomial(GenExteriorPolynomialRing<C> r, SortedMap<IndexList, C> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients and index lists
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public GenExteriorPolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Copy this GenExteriorPolynomial.
     * @return copy of this.
     */
    public GenExteriorPolynomial<C> copy() {
        return new GenExteriorPolynomial<C>(ring, this.val);
    }


    /**
     * Length of GenExteriorPolynomial.
     * @return number of coefficients of this GenExteriorPolynomial.
     */
    public int length() {
        return val.size();
    }


    /**
     * IndexList to coefficient map of GenExteriorPolynomial.
     * @return val as unmodifiable SortedMap.
     */
    public SortedMap<IndexList, C> getMap() {
        // return val;
        return Collections.<IndexList, C> unmodifiableSortedMap(val);
    }


    /**
     * Put a IndexList to coefficient entry into the internal map of this
     * GenExteriorPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param c coefficient.
     * @param e index list.
     */
    public void doPutToMap(IndexList e, C c) {
        if (debug) {
            C a = val.get(e);
            if (a != null) {
                logger.error("map entry exists {} to {} new {}", e, a, c);
            }
        }
        if (!c.isZERO() && !e.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Remove a IndexList to coefficient entry from the internal map of this
     * GenExteriorPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param e IndexList.
     * @param c expected coefficient, null for ignore.
     */
    public void doRemoveFromMap(IndexList e, C c) {
        C b = val.remove(e);
        if (debug) {
            if (c == null) { // ignore b
                return;
            }
            if (!c.equals(b)) {
                logger.error("map entry wrong {} to {} old {}", e, c, b);
            }
        }
    }


    /**
     * Put an a sorted map of index list to coefficients into the internal map
     * of this GenExteriorPolynomial. <b>Note:</b> Do not use this method unless
     * you are constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param vals sorted map of index list and coefficients.
     */
    public void doPutToMap(SortedMap<IndexList, C> vals) {
        for (Map.Entry<IndexList, C> me : vals.entrySet()) {
            IndexList e = me.getKey();
            if (debug) {
                C a = val.get(e);
                if (a != null) {
                    logger.error("map entry exists {} to {} new {}", e, a, me.getValue());
                }
            }
            C c = me.getValue();
            if (!c.isZERO() && !e.isZERO()) {
                val.put(e, c);
            }
        }
    }


    /**
     * String representation of GenExteriorPolynomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isZERO()) {
            return "0";
        }
        if (isONE()) {
            return "1";
        }
        StringBuffer s = new StringBuffer();
        if (val.size() > 1) {
            s.append("( ");
        }
        boolean parenthesis = false;
        boolean first = true;
        for (Map.Entry<IndexList, C> m : val.entrySet()) {
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
            IndexList e = m.getKey();
            if (!c.isONE() || e.isONE()) {
                if (parenthesis) {
                    s.append("( ");
                }
                String cs = c.toString();
                if (cs.indexOf("+") >= 0 || cs.indexOf("-") >= 0) {
                    s.append("( " + cs + " )");
                } else {
                    s.append(cs);
                }
                if (parenthesis) {
                    s.append(" )");
                }
                //if (!e.isONE()) {
                //    s.append(" ");
                //}
            }
            s.append(" ");
            s.append(e.toString());
        }
        if (val.size() > 1) {
            s.append(" )");
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
        if (isZERO()) {
            return "0";
        }
        if (isONE()) {
            return "1";
        }
        StringBuffer s = new StringBuffer();
        if (val.size() > 1) {
            s.append("( ");
        }
        final boolean parenthesis = false;
        boolean first = true;
        for (Map.Entry<IndexList, C> m : val.entrySet()) {
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
            IndexList e = m.getKey();
            if (!c.isONE() || e.isONE()) {
                if (parenthesis) {
                    s.append("( ");
                }
                String cs = c.toScript();
                if (cs.indexOf("+") >= 0 || cs.indexOf("-") >= 0) {
                    s.append("( " + cs + " )");
                } else {
                    s.append(cs);
                }
                if (parenthesis) {
                    s.append(" )");
                }
                if (!e.isONE()) {
                    s.append(" * ");
                }
            }
            s.append(e.toScript());
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
     * Is GenExteriorPolynomial&lt;C&gt; zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return (val.size() == 0);
    }


    /**
     * Is GenExteriorPolynomial&lt;C&gt; one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.wone);
        if (c == null) {
            return false;
        }
        return c.isONE();
    }


    /**
     * Is GenExteriorPolynomial&lt;C&gt; a unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.wone);
        if (c == null) {
            return false;
        }
        return c.isUnit();
    }


    /**
     * Is GenExteriorPolynomial&lt;C&gt; a constant.
     * @return If this is a constant polynomial then true is returned, else
     *         false.
     */
    public boolean isConstant() {
        if (val.size() != 1) {
            return false;
        }
        C c = val.get(ring.wone);
        if (c == null) {
            return false;
        }
        return true;
    }


    /**
     * Is GenExteriorPolynomial&lt;C&gt; homogeneous.
     * @return true, if this is homogeneous, else false.
     */
    public boolean isHomogeneous() {
        if (val.size() <= 1) {
            return true;
        }
        long deg = -1;
        for (IndexList e : val.keySet()) {
            if (deg < 0) {
                deg = e.degree();
            } else if (deg != e.degree()) {
                return false;
            }
        }
        return true;
    }


    /**
     * k-form part.
     * @param k requested k-form part.
     * @return k-form part of given degree of this.
     */
    public GenExteriorPolynomial<C> form(long k) {
        return homogeneousPart(k);
    }


    /**
     * Homogeneous part.
     * @param tdeg requested degree of part.
     * @return polynomial part of given degree.
     */
    public GenExteriorPolynomial<C> homogeneousPart(long tdeg) {
        if (isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> h = ring.getZERO().copy();
        SortedMap<IndexList, C> hv = h.val;
        for (Map.Entry<IndexList, C> me : val.entrySet()) {
            IndexList e = me.getKey();
            C y = me.getValue(); // assert y != null
            if (e.degree() != tdeg) {
                continue;
            } else {
                hv.put(e, y);
            }
        }
        return h;
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
        if (!(B instanceof GenExteriorPolynomial)) {
            return false;
        }
        GenExteriorPolynomial<C> a = (GenExteriorPolynomial<C>) B;
        return this.compareTo(a) == 0;
    }


    /**
     * Hash code for this polynomial.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = (ring.hashCode() << 27);
        h += val.hashCode();
        return h;
    }


    /**
     * GenExteriorPolynomial comparison.
     * @param b GenExteriorPolynomial.
     * @return sign(this-b).
     */
    public int compareTo(GenExteriorPolynomial<C> b) {
        if (b == null) {
            return 1;
        }
        SortedMap<IndexList, C> av = this.val;
        SortedMap<IndexList, C> bv = b.val;
        Iterator<Map.Entry<IndexList, C>> ai = av.entrySet().iterator();
        Iterator<Map.Entry<IndexList, C>> bi = bv.entrySet().iterator();
        int s = 0;
        int c = 0;
        while (ai.hasNext() && bi.hasNext()) {
            Map.Entry<IndexList, C> aie = ai.next();
            Map.Entry<IndexList, C> bie = bi.next();
            IndexList ae = aie.getKey();
            IndexList be = bie.getKey();
            s = ae.compareTo(be);
            //System.out.println("s = " + s + ", " + ae + ", " +be);
            if (s != 0) {
                return s;
            }
            //if (c == 0) { // ??
            C ac = aie.getValue(); //av.get(ae);
            C bc = bie.getValue(); //bv.get(be);
            c = ac.compareTo(bc);
            //}
        }
        if (ai.hasNext()) {
            return 1;
        }
        if (bi.hasNext()) {
            return -1;
        }
        // now all keys are equal
        return c;
    }


    /**
     * GenExteriorPolynomial signum.
     * @return sign(ldcf(this)).
     */
    public int signum() {
        if (this.isZERO()) {
            return 0;
        }
        IndexList t = val.firstKey();
        C c = val.get(t);
        return c.signum();
    }


    /**
     * Number of variables.
     * @return ring.ixfac.length().
     */
    public int numberOfVariables() {
        return ring.ixfac.length(); // some times
    }


    /**
     * Leading monomial.
     * @return first map entry.
     */
    public Map.Entry<IndexList, C> leadingMonomial() {
        if (val.size() == 0)
            return null;
        Iterator<Map.Entry<IndexList, C>> ai = val.entrySet().iterator();
        return ai.next();
    }


    /**
     * Leading index list.
     * @return first highest index list.
     */
    public IndexList leadingIndexList() {
        if (val.size() == 0) {
            return ring.wone; // null? needs changes?
        }
        return val.firstKey();
    }


    /**
     * Trailing index list.
     * @return last lowest index list.
     */
    public IndexList trailingIndexList() {
        if (val.size() == 0) {
            return ring.wone; // or null ?;
        }
        return val.lastKey();
    }


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingBaseCoefficient() {
        if (val.size() == 0) {
            return ring.coFac.getZERO();
        }
        return val.get(val.firstKey());
    }


    /**
     * Trailing base coefficient.
     * @return coefficient of constant term.
     */
    public C trailingBaseCoefficient() {
        C c = val.get(ring.wone);
        if (c == null) {
            return ring.coFac.getZERO();
        }
        return c;
    }


    /**
     * Coefficient.
     * @param e index list.
     * @return coefficient for given index list.
     */
    public C coefficient(IndexList e) {
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
    public GenExteriorPolynomial<C> reductum() {
        if (val.size() <= 1) {
            return ring.getZERO();
        }
        Iterator<IndexList> ai = val.keySet().iterator();
        IndexList lt = ai.next();
        lt = ai.next(); // size > 1
        SortedMap<IndexList, C> red = val.tailMap(lt);
        return new GenExteriorPolynomial<C>(ring, red);
    }


    /**
     * Index degree.
     * @return maximal length of indexes.
     */
    public long degree() {
        if (val.size() == 0) {
            return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for (IndexList e : val.keySet()) {
            long d = e.degree();
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * Index maximal degree.
     * @return maximal degree of indexes.
     */
    public long maxDegree() {
        if (val.size() == 0) {
            return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for (IndexList e : val.keySet()) {
            long d = e.maxDeg();
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * GenExteriorPolynomial maximum norm.
     * @return ||this||.
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
     * GenExteriorPolynomial sum norm.
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
     * GenExteriorPolynomial summation.
     * @param S GenExteriorPolynomial.
     * @return this+S.
     */
    public GenExteriorPolynomial<C> sum(GenExteriorPolynomial<C> S) {
        if (S == null) {
            return this;
        }
        if (S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        assert (ring.ixfac == S.ring.ixfac);
        GenExteriorPolynomial<C> n = this.copy();
        SortedMap<IndexList, C> nv = n.val;
        SortedMap<IndexList, C> sv = S.val;
        for (Map.Entry<IndexList, C> me : sv.entrySet()) {
            IndexList e = me.getKey();
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
     * GenExteriorPolynomial addition. This method is not very efficient, since
     * this is copied.
     * @param a coefficient.
     * @param e index list.
     * @return this + a e.
     */
    public GenExteriorPolynomial<C> sum(C a, IndexList e) {
        if (a == null) {
            return this;
        }
        if (a.isZERO()) {
            return this;
        }
        if (e == null) {
            return this;
        }
        if (e.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> n = this.copy();
        SortedMap<IndexList, C> nv = n.val;
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
     * GenExteriorPolynomial addition. This method is not very efficient, since
     * this is copied.
     * @param a coefficient.
     * @return this + a x<sub>0</sub>.
     */
    public GenExteriorPolynomial<C> sum(C a) {
        return sum(a, ring.wone);
    }


    /**
     * GenExteriorPolynomial subtraction.
     * @param S GenExteriorPolynomial.
     * @return this-S.
     */
    public GenExteriorPolynomial<C> subtract(GenExteriorPolynomial<C> S) {
        if (S == null) {
            return this;
        }
        if (S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.negate();
        }
        assert (ring.ixfac == S.ring.ixfac);
        GenExteriorPolynomial<C> n = this.copy();
        SortedMap<IndexList, C> nv = n.val;
        SortedMap<IndexList, C> sv = S.val;
        for (Map.Entry<IndexList, C> me : sv.entrySet()) {
            IndexList e = me.getKey();
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
     * GenExteriorPolynomial subtraction. This method is not very efficient,
     * since this is copied.
     * @param a coefficient.
     * @param e index list.
     * @return this - a e.
     */
    public GenExteriorPolynomial<C> subtract(C a, IndexList e) {
        if (a == null) {
            return this;
        }
        if (a.isZERO()) {
            return this;
        }
        if (e == null) {
            return this;
        }
        if (e.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> n = this.copy();
        SortedMap<IndexList, C> nv = n.val;
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
     * GenExteriorPolynomial subtract. This method is not very efficient, since
     * this is copied.
     * @param a coefficient.
     * @return this + a x<sub>0</sub>.
     */
    public GenExteriorPolynomial<C> subtract(C a) {
        return subtract(a, ring.wone);
    }


    /**
     * GenExteriorPolynomial negation.
     * @return -this.
     */
    public GenExteriorPolynomial<C> negate() {
        GenExteriorPolynomial<C> n = ring.getZERO().copy();
        SortedMap<IndexList, C> v = n.val;
        for (Map.Entry<IndexList, C> m : val.entrySet()) {
            C x = m.getValue(); // != null, 0
            v.put(m.getKey(), x.negate());
        }
        return n;
    }


    /**
     * GenExteriorPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
     * @return abs(this).
     */
    public GenExteriorPolynomial<C> abs() {
        if (leadingBaseCoefficient().signum() < 0) {
            return this.negate();
        }
        return this;
    }


    /**
     * GenExteriorPolynomial multiplication.
     * @param S GenExteriorPolynomial.
     * @return this /\\ S.
     */
    public GenExteriorPolynomial<C> multiply(GenExteriorPolynomial<C> S) {
        if (S == null) {
            return ring.getZERO();
        }
        if (S.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.ixfac == S.ring.ixfac) : " " + ring + " != " + S.ring;
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            for (Map.Entry<IndexList, C> m2 : S.val.entrySet()) {
                C c2 = m2.getValue();
                IndexList e2 = m2.getKey();
                C c = c1.multiply(c2); // check non zero if not domain
                if (!c.isZERO()) {
                    IndexList e = e1.multiply(e2);
                    if (e.isZERO()) { // since exterior algebra
                        continue;
                    }
                    if (e.sign < 0) { // propagate sign to coefficient
                        c = c.negate();
                        e = e.negate();
                    }
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
     * GenExteriorPolynomial interior left multiplication.
     * @param S GenExteriorPolynomial.
     * @return this _| S.
     */
    public GenExteriorPolynomial<C> interiorLeftProduct(GenExteriorPolynomial<C> S) {
        if (S == null) {
            return ring.getZERO();
        }
        if (S.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.ixfac == S.ring.ixfac) : " " + ring + " != " + S.ring;
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            for (Map.Entry<IndexList, C> m2 : S.val.entrySet()) {
                C c2 = m2.getValue();
                IndexList e2 = m2.getKey();
                C c = c1.multiply(c2); // check non zero if not domain
                if (!c.isZERO()) {
                    IndexList e = e1.interiorLeftProduct(e2);
                    if (e.isZERO()) { // since exterior algebra
                        continue;
                    }
                    if (e.sign < 0) { // propagate sign to coefficient
                        c = c.negate();
                        e = e.negate();
                    }
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
     * GenExteriorPolynomial interior right multiplication.
     * @param S GenExteriorPolynomial.
     * @return this |_ S.
     */
    public GenExteriorPolynomial<C> interiorRightProduct(GenExteriorPolynomial<C> S) {
        if (S == null) {
            return ring.getZERO();
        }
        if (S.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.ixfac == S.ring.ixfac) : " " + ring + " != " + S.ring;
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            for (Map.Entry<IndexList, C> m2 : S.val.entrySet()) {
                C c2 = m2.getValue();
                IndexList e2 = m2.getKey();
                C c = c1.multiply(c2); // check non zero if not domain
                if (!c.isZERO()) {
                    IndexList e = e1.interiorRightProduct(e2);
                    if (e.isZERO()) { // since exterior algebra
                        continue;
                    }
                    if (e.sign < 0) { // propagate sign to coefficient
                        c = c.negate();
                        e = e.negate();
                    }
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
     * GenExteriorPolynomial left and right multiplication. Product with two
     * polynomials.
     * @param S GenExteriorPolynomial.
     * @param T GenExteriorPolynomial.
     * @return S*this*T.
     */
    public GenExteriorPolynomial<C> multiply(GenExteriorPolynomial<C> S, GenExteriorPolynomial<C> T) {
        if (S.isZERO() || T.isZERO()) {
            return ring.getZERO();
        }
        if (S.isONE()) {
            return multiply(T);
        }
        if (T.isONE()) {
            return S.multiply(this);
        }
        return S.multiply(this).multiply(T);
    }


    /**
     * GenExteriorPolynomial multiplication. Product with coefficient ring
     * element.
     * @param s coefficient.
     * @return this*s.
     */
    public GenExteriorPolynomial<C> multiply(C s) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            C c = c1.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                pv.put(e1, c); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenExteriorPolynomial multiplication. Product with coefficient ring
     * element.
     * @param s coefficient.
     * @param t coefficient.
     * @return s*this*t.
     */
    public GenExteriorPolynomial<C> multiply(C s, C t) {
        if (s == null || t == null) {
            return ring.getZERO();
        }
        if (s.isZERO() || t.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e = m1.getKey();
            C c = s.multiply(c1).multiply(t); // check non zero if not domain
            if (!c.isZERO()) {
                pv.put(e, c); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenExteriorPolynomial monic, i.e. leadingCoefficient == 1. If
     * leadingCoefficient is not invertible returns this unmodified.
     * @return monic(this).
     */
    public GenExteriorPolynomial<C> monic() {
        if (this.isZERO()) {
            return this;
        }
        C lc = leadingBaseCoefficient();
        if (!lc.isUnit()) {
            //System.out.println("lc = "+lc);
            return this;
        }
        C lm = lc.inverse();
        return multiply(lm);
    }


    /**
     * GenExteriorPolynomial multiplication. Product with ring element and index
     * list.
     * @param s coefficient.
     * @param e left index list.
     * @return this * s e.
     */
    public GenExteriorPolynomial<C> multiply(C s, IndexList e) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (e == null) {
            return ring.getZERO();
        }
        if (e.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            C c = c1.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                IndexList e2 = e1.multiply(e);
                if (e2.isZERO()) { // since exterior algebra
                    continue;
                }
                if (e2.sign < 0) { // propagate sign to coefficient
                    c = c.negate();
                    e2 = e2.negate();
                }
                pv.put(e2, c);
            }
        }
        return p;
    }


    /**
     * GenExteriorPolynomial left and right multiplication. Product with ring
     * element and two index lists.
     * @param e left index list.
     * @param f right index list.
     * @return e * this * f.
     */
    public GenExteriorPolynomial<C> multiply(IndexList e, IndexList f) {
        if (e == null) {
            return ring.getZERO();
        }
        if (e.isZERO()) {
            return ring.getZERO();
        }
        if (f == null) {
            return ring.getZERO();
        }
        if (f.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (e.isONE()) {
            return multiply(f);
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c = m1.getValue();
            IndexList e1 = m1.getKey();
            IndexList ef = e1.multiply(f);
            if (ef.isZERO()) { // since exterior algebra
                continue;
            }
            if (ef.sign < 0) { // propagate sign to coefficient
                c = c.negate();
                ef = ef.negate();
            }
            IndexList e2 = e.multiply(ef);
            if (e2.isZERO()) { // since exterior algebra
                continue;
            }
            if (e2.sign < 0) { // propagate sign to coefficient
                c = c.negate();
                e2 = e2.negate();
            }
            pv.put(e2, c);
        }
        return p;
    }


    /**
     * GenExteriorPolynomial left and right multiplication. Product with ring
     * element and two index lists.
     * @param s coefficient.
     * @param e left index list.
     * @param f right index list.
     * @return e * this * s * f.
     */
    public GenExteriorPolynomial<C> multiply(C s, IndexList e, IndexList f) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (e == null) {
            return ring.getZERO();
        }
        if (e.isZERO()) {
            return ring.getZERO();
        }
        if (f == null) {
            return ring.getZERO();
        }
        if (f.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (e.isONE()) {
            return multiply(s, f);
        }
        C c = ring.coFac.getONE();
        return multiply(c, e, s, f); // sic, history
    }


    /**
     * GenExteriorPolynomial left and right multiplication. Product with ring
     * element and two index lists.
     * @param s coefficient.
     * @param e left index list.
     * @param t coefficient.
     * @param f right index list.
     * @return s * e * this * t * f.
     */
    public GenExteriorPolynomial<C> multiply(C s, IndexList e, C t, IndexList f) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (t == null) {
            return ring.getZERO();
        }
        if (t.isZERO()) {
            return ring.getZERO();
        }
        if (e == null) {
            return ring.getZERO();
        }
        if (e.isZERO()) {
            return ring.getZERO();
        }
        if (f == null) {
            return ring.getZERO();
        }
        if (f.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            C c = s.multiply(c1).multiply(t); // check non zero if not domain
            if (!c.isZERO()) {
                IndexList e1 = m1.getKey();
                //IndexList e2 = e.multiply(e1).multiply(f);
                IndexList ef = e1.multiply(f);
                if (ef.isZERO()) { // since exterior algebra
                    continue;
                }
                if (ef.sign < 0) { // propagate sign to coefficient
                    c = c.negate();
                    ef = ef.negate();
                }
                IndexList e2 = e.multiply(ef);
                if (e2.isZERO()) { // since exterior algebra
                    continue;
                }
                if (e2.sign < 0) { // propagate sign to coefficient
                    c = c.negate();
                    e2 = e2.negate();
                }
                pv.put(e2, c);
            }
        }
        return p;
    }


    /**
     * GenExteriorPolynomial multiplication. Product with index list.
     * @param e index list (!= null).
     * @return this * e.
     */
    public GenExteriorPolynomial<C> multiply(IndexList e) {
        if (e == null) {
            return ring.getZERO();
        }
        if (e.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            IndexList e1 = m1.getKey();
            IndexList e2 = e1.multiply(e);
            if (e2.isZERO()) { // since exterior algebra
                continue;
            }
            if (e2.sign < 0) { // propagate sign to coefficient
                c1 = c1.negate();
                e2 = e2.negate();
            }
            pv.put(e2, c1);
        }
        return p;
    }


    /**
     * GenExteriorPolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m.
     */
    public GenExteriorPolynomial<C> multiply(Map.Entry<IndexList, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * GenExteriorPolynomial division. Division by coefficient ring element.
     * Fails, if exact division is not possible.
     * @param s coefficient.
     * @return this/s.
     */
    public GenExteriorPolynomial<C> divide(C s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException(this.getClass().getName() + " division by zero");
        }
        if (this.isZERO() || s.isONE()) {
            return this;
        }
        //C t = s.inverse();
        //return multiply(t);
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m : val.entrySet()) {
            IndexList e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s); // is divideLeft
            if (debug) {
                C x = c1.remainder(s);
                if (!x.isZERO()) {
                    logger.info("divide x = {}", x);
                    throw new ArithmeticException(
                                    this.getClass().getName() + " no exact division: " + c1 + "/" + s);
                }
            }
            if (c.isZERO()) {
                throw new ArithmeticException(this.getClass().getName() + " no exact division: " + c1 + "/"
                                + s + ", in " + this);
            }
            pv.put(e, c); // or m1.setValue( c )
        }
        return p;
    }


    /**
     * GenExteriorPolynomial coefficient primitive part. Division by gcd of
     * coefficients.
     * @return this/gcd(coeff(this)).
     */
    public GenExteriorPolynomial<C> coeffPrimitivePart() {
        if (this.isZERO() || this.isONE()) {
            return this;
        }
        C s = ring.coFac.getZERO();
        for (C c : val.values()) {
            s = s.gcd(c);
        }
        return divide(s).abs();
    }


    /**
     * GenExteriorPolynomial division with remainder. Fails, if exact division
     * by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     * @param S nonzero GenExteriorPolynomial with invertible leading
     *            coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder and
     *         deg(remainder) &lt; deg(S) or remainder = 0.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    @SuppressWarnings("unchecked")
    public GenExteriorPolynomial<C>[] quotientRemainder(GenExteriorPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        GenExteriorPolynomial<C>[] ret = new GenExteriorPolynomial[2];
        if (this.isZERO()) {
            ret[0] = ring.getZERO();
            ret[1] = ring.getZERO();
            return ret;
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException("lbcf not invertible " + c);
        }
        C ci = c.inverse();
        C one = ring.coFac.getONE();
        assert (ring.ixfac == S.ring.ixfac);
        IndexFactory.IndexListComparator cmp = ring.ixfac.getDescendComparator();
        IndexList e = S.leadingIndexList();
        GenExteriorPolynomial<C> h;
        GenExteriorPolynomial<C> q = ring.getZERO().copy();
        GenExteriorPolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            IndexList f = r.leadingIndexList();
            if (e.divides(f)) {
                C a = r.leadingBaseCoefficient();
                //IndexList gl = e.interiorLeftProduct(f);
                IndexList g = e.interiorRightProduct(f);
                //System.out.println("divRem: f = " + f + ", e = " + e + ", g = " + g);
                a = a.multiply(ci);
                q = q.sum(a, g); // q + a g
                h = S.multiply(a, g); // a g * S
                r = r.subtract(h);
                IndexList fr = r.leadingIndexList();
                if (cmp.compare(f, fr) > 0) { // non noetherian reduction // todo
                    throw new RuntimeException("possible infinite loop: f = " + f + ", fr = " + fr);
                }
            } else {
                break;
            }
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenExteriorPolynomial division. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenExteriorPolynomial with invertible leading
     *            coefficient.
     * @return quotient with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public GenExteriorPolynomial<C> divide(GenExteriorPolynomial<C> S) {
        return quotientRemainder(S)[0];
    }


    /**
     * GenExteriorPolynomial remainder. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenExteriorPolynomial with invertible leading
     *            coefficient.
     * @return remainder with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
     *      .
     */
    public GenExteriorPolynomial<C> remainder(GenExteriorPolynomial<C> S) {
        return quotientRemainder(S)[1];
    }


    /**
     * GenExteriorPolynomial greatest common divisor. <b>Note:</b> not
     * implemented.
     * @param S GenExteriorPolynomial.
     * @return gcd(this,S).
     */
    public GenExteriorPolynomial<C> gcd(GenExteriorPolynomial<C> S) {
        throw new UnsupportedOperationException("no gcd for exterior polynomials");
    }


    /**
     * GenExteriorPolynomial extended greatest common divisor. <b>Note:</b> not
     * implemented.
     * @param S GenExteriorPolynomial.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public GenExteriorPolynomial<C>[] egcd(GenExteriorPolynomial<C> S) {
        throw new UnsupportedOperationException("no gcd for exterior polynomials");
    }


    /**
     * GenExteriorPolynomial inverse. Required by RingElem. Throws not
     * invertible exception.
     */
    public GenExteriorPolynomial<C> inverse() {
        if (isUnit()) { // only possible if ldbcf is unit
            C c = leadingBaseCoefficient().inverse();
            return ring.getONE().multiply(c);
        }
        throw new NotInvertibleException("element not invertible " + this + " :: " + ring);
    }


    /**
     * GenExteriorPolynomial shift index. Add number to each index.
     * @param s shift index by this number.
     * @return this.shift(s).
     */
    public GenExteriorPolynomial<C> shiftIndex(int s) {
        //if (ring.ixfac.length() != 1) {
        //    throw new IllegalArgumentException("only 'univariate' polynomials");
        //}
        if (s == 0) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        List<IndexList> gen = ring.ixfac.generators();
        //System.out.println("gen = " + gen);
        long d = maxDegree();
        //System.out.println("d = " + d);
        if (d + s >= gen.size()) {
            throw new IllegalArgumentException("ixfac to small: " + (d + s) + " >= " + gen.size());
        }
        GenExteriorPolynomial<C> p = ring.getZERO().copy();
        SortedMap<IndexList, C> pv = p.val;
        for (Map.Entry<IndexList, C> m1 : val.entrySet()) {
            C c = m1.getValue();
            IndexList ei = m1.getKey();
            int e = ei.getVal(0);
            e = e + s;
            IndexList fi = gen.get(e);
            pv.put(fi, c);
        }
        return p;
    }


    /**
     * Iterator over coefficients.
     * @return val.values().iterator().
     */
    public Iterator<C> coefficientIterator() {
        return val.values().iterator();
    }


    /**
     * Iterator over index lists.
     * @return val.keySet().iterator().
     */
    public Iterator<IndexList> indexListIterator() {
        return val.keySet().iterator();
    }


    /**
     * Iterator over monomials.
     * @return a PolyIterator.
     */
    public Iterator<IndexListMonomial<C>> iterator() {
        return new IndexListPolyIterator<C>(val);
    }


    /**
     * Map a unary function to the coefficients.
     * @param f evaluation functor.
     * @return new polynomial with coefficients f(this(e)).
     */
    public GenExteriorPolynomial<C> map(final UnaryFunctor<? super C, C> f) {
        GenExteriorPolynomial<C> n = ring.getZERO().copy();
        SortedMap<IndexList, C> nv = n.val;
        for (Map.Entry<IndexList, C> m : this.val.entrySet()) {
            //logger.info("m = {}", m);
            C c = f.eval(m.getValue());
            if (c != null && !c.isZERO()) {
                nv.put(m.getKey(), c);
            }
        }
        return n;
    }

}
