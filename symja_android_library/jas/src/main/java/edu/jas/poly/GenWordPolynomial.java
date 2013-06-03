/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.kern.PreemptingException;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;


/**
 * GenWordPolynomial generic polynomials implementing RingElem. Non-commutative
 * string polynomials over C. Objects of this class are intended to be
 * immutable. The implementation is based on TreeMap respectively SortedMap from
 * words to coefficients. Only the coefficients are modeled with generic
 * types, the "exponents" are fixed to Word. C can also be a non integral domain,
 * e.g. a ModInteger, i.e. it may contain zero divisors, since multiply() does
 * check for zeros.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public final class GenWordPolynomial<C extends RingElem<C>> 
             implements RingElem<GenWordPolynomial<C>>, Iterable<WordMonomial<C>> {


    /**
     * The factory for the polynomial ring.
     */
    public final GenWordPolynomialRing<C> ring;


    /**
     * The data structure for polynomials.
     */
    final SortedMap<Word, C> val; // do not change to TreeMap


    private static final Logger logger = Logger.getLogger(GenWordPolynomial.class);


    private final boolean debug = logger.isDebugEnabled();


    // protected GenWordPolynomial() { ring = null; val = null; } // don't use


    /**
     * Private constructor for GenWordPolynomial.
     * @param r polynomial ring factory.
     * @param t TreeMap with correct ordering.
     */
    private GenWordPolynomial(GenWordPolynomialRing<C> r, TreeMap<Word, C> t) {
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
     * Constructor for zero GenWordPolynomial.
     * @param r polynomial ring factory.
     */
    public GenWordPolynomial(GenWordPolynomialRing<C> r) {
        this(r, new TreeMap<Word, C>(r.alphabet.getDescendComparator()));
    }


    /**
     * Constructor for GenWordPolynomial c * x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e word.
     */
    public GenWordPolynomial(GenWordPolynomialRing<C> r, C c, Word e) {
        this(r);
        if (!c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for GenWordPolynomial c * x<sup>0</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     */
    public GenWordPolynomial(GenWordPolynomialRing<C> r, C c) {
        this(r, c, r.wone);
    }


    /**
     * Constructor for GenWordPolynomial x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param e word.
     */
    public GenWordPolynomial(GenWordPolynomialRing<C> r, Word e) {
        this(r, r.coFac.getONE(), e);
    }


    /**
     * Constructor for GenWordPolynomial.
     * @param r polynomial ring factory.
     * @param v the SortedMap of some other polynomial.
     */
    protected GenWordPolynomial(GenWordPolynomialRing<C> r, SortedMap<Word, C> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public GenWordPolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Copy this GenWordPolynomial.
     * @return copy of this.
     */
    public GenWordPolynomial<C> copy() {
        return new GenWordPolynomial<C>(ring, this.val);
    }


    /**
     * Length of GenWordPolynomial.
     * @return number of coefficients of this GenWordPolynomial.
     */
    public int length() {
        return val.size();
    }


    /**
     * Word to coefficient map of GenWordPolynomial.
     * @return val as unmodifiable SortedMap.
     */
    public SortedMap<Word, C> getMap() {
        // return val;
        return Collections.<Word, C> unmodifiableSortedMap(val);
    }


    /**
     * Put an Word to coefficient entry into the internal map of this
     * GenWordPolynomial. <b>Note:</b> Do not use this method unless you are
     * constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param c coefficient.
     * @param e word.
     */
    public void doPutToMap(Word e, C c) {
        if (debug) {
            C a = val.get(e);
            if (a != null) {
                logger.error("map entry exists " + e + " to " + a + " new " + c);
            }
        }
        if (!c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Put an a sorted map of words to coefficients into the internal map of
     * this GenWordPolynomial. <b>Note:</b> Do not use this method unless you
     * are constructing a new polynomial. this is modified and breaks the
     * immutability promise of this class.
     * @param vals sorted map of wordss and coefficients.
     */
    public void doPutToMap(SortedMap<Word, C> vals) {
        for (Map.Entry<Word, C> me : vals.entrySet()) {
            Word e = me.getKey();
            if (debug) {
                C a = val.get(e);
                if (a != null) {
                    logger.error("map entry exists " + e + " to " + a + " new " + me.getValue());
                }
            }
            C c = me.getValue();
            if (!c.isZERO()) {
                val.put(e, c);
            }
        }
    }


    /**
     * String representation of GenWordPolynomial.
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
        for (Map.Entry<Word, C> m : val.entrySet()) {
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
            Word e = m.getKey();
            if (!c.isONE() || e.isONE()) {
                if (parenthesis) {
                    s.append("( ");
                }
                s.append(c.toString());
                if (parenthesis) {
                    s.append(" )");
                }
                if (!e.isONE()) {
                    s.append(" ");
                }
            }
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
    //JAVA6only: @Override
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
        boolean parenthesis = false;
        boolean first = true;
        for (Map.Entry<Word, C> m : val.entrySet()) {
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
            Word e = m.getKey();
            if (!c.isONE() || e.isONE()) {
                if (parenthesis) {
                    s.append("( ");
                }
                s.append(c.toScript());
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
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * Is GenWordPolynomial&lt;C&gt; zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return (val.size() == 0);
    }


    /**
     * Is GenWordPolynomial&lt;C&gt; one.
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
     * Is GenWordPolynomial&lt;C&gt; a unit.
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
     * Is GenWordPolynomial&lt;C&gt; a constant.
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
     * Is GenWordPolynomial&lt;C&gt; homogeneous.
     * @return true, if this is homogeneous, else false.
     */
    public boolean isHomogeneous() {
        if (val.size() <= 1) {
            return true;
        }
        long deg = -1;
        for (Word e : val.keySet()) {
            if (deg < 0) {
                deg = e.degree();
            } else if (deg != e.degree()) {
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
        if (!(B instanceof GenWordPolynomial)) {
            return false;
        }
        GenWordPolynomial<C> a = null;
        try {
            a = (GenWordPolynomial<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
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
     * GenWordPolynomial comparison.
     * @param b GenWordPolynomial.
     * @return sign(this-b).
     */
    public int compareTo(GenWordPolynomial<C> b) {
        if (b == null) {
            return 1;
        }
        SortedMap<Word, C> av = this.val;
        SortedMap<Word, C> bv = b.val;
        Iterator<Map.Entry<Word, C>> ai = av.entrySet().iterator();
        Iterator<Map.Entry<Word, C>> bi = bv.entrySet().iterator();
        int s = 0;
        int c = 0;
        while (ai.hasNext() && bi.hasNext()) {
            Map.Entry<Word, C> aie = ai.next();
            Map.Entry<Word, C> bie = bi.next();
            Word ae = aie.getKey();
            Word be = bie.getKey();
            s = ae.compareTo(be);
            //System.out.println("s = " + s + ", " + ae + ", " +be);
            if (s != 0) {
                return s;
            }
            if (c == 0) {
                C ac = aie.getValue(); //av.get(ae);
                C bc = bie.getValue(); //bv.get(be);
                c = ac.compareTo(bc);
            }
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
     * GenWordPolynomial signum.
     * @return sign(ldcf(this)).
     */
    public int signum() {
        if (this.isZERO()) {
            return 0;
        }
        Word t = val.firstKey();
        C c = val.get(t);
        return c.signum();
    }


    /**
     * Number of variables.
     * @return ring.alphabet.length().
     */
    public int numberOfVariables() {
        return ring.alphabet.length();
    }


    /**
     * Leading monomial.
     * @return first map entry.
     */
    public Map.Entry<Word, C> leadingMonomial() {
        if (val.size() == 0)
            return null;
        Iterator<Map.Entry<Word, C>> ai = val.entrySet().iterator();
        return ai.next();
    }


    /**
     * Leading word.
     * @return first highest word.
     */
    public Word leadingWord() {
        if (val.size() == 0) {
            return ring.wone; // null? needs changes? 
        }
        return val.firstKey();
    }


    /**
     * Trailing word.
     * @return last lowest word.
     */
    public Word trailingWord() {
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
     * @param e word.
     * @return coefficient for given word.
     */
    public C coefficient(Word e) {
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
    public GenWordPolynomial<C> reductum() {
        if (val.size() <= 1) {
            return ring.getZERO();
        }
        Iterator<Word> ai = val.keySet().iterator();
        Word lt = ai.next();
        lt = ai.next(); // size > 1
        SortedMap<Word, C> red = val.tailMap(lt);
        return new GenWordPolynomial<C>(ring, red);
    }


    /**
     * Maximal degree.
     * @return maximal degree in any variables.
     */
    public long degree() {
        if (val.size() == 0) {
            return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for (Word e : val.keySet()) {
            long d = e.degree();
            if (d > deg) {
                deg = d;
            }
        }
        return deg;
    }


    /**
     * GenWordPolynomial maximum norm.
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
     * GenWordPolynomial sum norm.
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
     * GenWordPolynomial summation.
     * @param S GenWordPolynomial.
     * @return this+S.
     */
    public GenWordPolynomial<C> sum(GenWordPolynomial<C> S) {
        if (S == null) {
            return this;
        }
        if (S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        assert (ring.alphabet == S.ring.alphabet);
        GenWordPolynomial<C> n = this.copy(); //new GenWordPolynomial<C>(ring, val); 
        SortedMap<Word, C> nv = n.val;
        SortedMap<Word, C> sv = S.val;
        for (Map.Entry<Word, C> me : sv.entrySet()) {
            Word e = me.getKey();
            C y = me.getValue(); //sv.get(e); // assert y != null
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
     * GenWordPolynomial addition. This method is not very efficient, since this
     * is copied.
     * @param a coefficient.
     * @param e word.
     * @return this + a e.
     */
    public GenWordPolynomial<C> sum(C a, Word e) {
        if (a == null) {
            return this;
        }
        if (a.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> n = this.copy(); //new GenWordPolynomial<C>(ring, val); 
        SortedMap<Word, C> nv = n.val;
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
     * GenWordPolynomial addition. This method is not very efficient, since this
     * is copied.
     * @param a coefficient.
     * @return this + a x<sup>0</sup>.
     */
    public GenWordPolynomial<C> sum(C a) {
        return sum(a, ring.wone);
    }


    /**
     * GenWordPolynomial subtraction.
     * @param S GenWordPolynomial.
     * @return this-S.
     */
    public GenWordPolynomial<C> subtract(GenWordPolynomial<C> S) {
        if (S == null) {
            return this;
        }
        if (S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.negate();
        }
        assert (ring.alphabet == S.ring.alphabet);
        GenWordPolynomial<C> n = this.copy(); //new GenWordPolynomial<C>(ring, val); 
        SortedMap<Word, C> nv = n.val;
        SortedMap<Word, C> sv = S.val;
        for (Map.Entry<Word, C> me : sv.entrySet()) {
            Word e = me.getKey();
            C y = me.getValue(); //sv.get(e); // assert y != null
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
     * GenWordPolynomial subtraction. This method is not very efficient, since
     * this is copied.
     * @param a coefficient.
     * @param e word.
     * @return this - a e.
     */
    public GenWordPolynomial<C> subtract(C a, Word e) {
        if (a == null) {
            return this;
        }
        if (a.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> n = this.copy(); //new GenWordPolynomial<C>(ring, val); 
        SortedMap<Word, C> nv = n.val;
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
     * GenWordPolynomial subtract. This method is not very efficient, since this
     * is copied.
     * @param a coefficient.
     * @return this + a x<sup>0</sup>.
     */
    public GenWordPolynomial<C> subtract(C a) {
        return subtract(a, ring.wone);
    }


    /**
     * GenWordPolynomial negation.
     * @return -this.
     */
    public GenWordPolynomial<C> negate() {
        GenWordPolynomial<C> n = ring.getZERO().copy();
        SortedMap<Word, C> v = n.val;
        for (Map.Entry<Word, C> m : val.entrySet()) {
            C x = m.getValue(); // != null, 0
            v.put(m.getKey(), x.negate());
        }
        return n;
    }


    /**
     * GenWordPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
     * @return abs(this).
     */
    public GenWordPolynomial<C> abs() {
        if (leadingBaseCoefficient().signum() < 0) {
            return this.negate();
        }
        return this;
    }


    /**
     * GenWordPolynomial multiplication.
     * @param S GenWordPolynomial.
     * @return this*S.
     */
    public GenWordPolynomial<C> multiply(GenWordPolynomial<C> S) {
        if (S == null) {
            return ring.getZERO();
        }
        if (S.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.alphabet == S.ring.alphabet);
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            Word e1 = m1.getKey();
            for (Map.Entry<Word, C> m2 : S.val.entrySet()) {
                C c2 = m2.getValue();
                Word e2 = m2.getKey();
                C c = c1.multiply(c2); // check non zero if not domain
                if (!c.isZERO()) {
                    Word e = e1.multiply(e2);
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
     * GenWordPolynomial left and right multiplication. Product with
     * two polynomials.
     * @param S GenWordPolynomial.
     * @param T GenWordPolynomial.
     * @return S*this*T.
     */
    public GenWordPolynomial<C> multiply(GenWordPolynomial<C> S, GenWordPolynomial<C> T) {
        if ( S.isZERO() || T.isZERO() ) {
            return ring.getZERO();
        }
        if ( S.isONE() ) {
            return multiply(T);
        }
        if ( T.isONE() ) {
            return S.multiply(this);
        }
        return S.multiply(this).multiply(T);
    }


    /**
     * GenWordPolynomial multiplication. Product with coefficient ring element.
     * @param s coefficient.
     * @return this*s.
     */
    public GenWordPolynomial<C> multiply(C s) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            Word e1 = m1.getKey();
            C c = c1.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                pv.put(e1, c); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenWordPolynomial multiplication. Product with coefficient ring element.
     * @param s coefficient.
     * @param t coefficient.
     * @return s*this*t.
     */
    public GenWordPolynomial<C> multiply(C s, C t) {
        if (s == null || t == null) {
            return ring.getZERO();
        }
        if (s.isZERO()||t.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            Word e = m1.getKey();
            C c = s.multiply(c1).multiply(t); // check non zero if not domain
            if (!c.isZERO()) {
                pv.put(e, c); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenWordPolynomial monic, i.e. leadingCoefficient == 1. If
     * leadingCoefficient is not invertible returns this unmodified.
     * @return monic(this).
     */
    public GenWordPolynomial<C> monic() {
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
     * GenWordPolynomial multiplication. Product with ring element and word.
     * @param s coefficient.
     * @param e left word.
     * @return this * s e.
     */
    public GenWordPolynomial<C> multiply(C s, Word e) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            Word e1 = m1.getKey();
            C c = c1.multiply(s); // check non zero if not domain
            if (!c.isZERO()) {
                Word e2 = e1.multiply(e);
                pv.put(e2, c);
            }
        }
        return p;
    }


    /**
     * GenWordPolynomial left and right multiplication. Product with
     * ring element and two words.
     * @param e left word.
     * @param f right word.
     * @return e * this * f.
     */
    public GenWordPolynomial<C> multiply(Word e, Word f) {
        if (this.isZERO()) {
            return this;
        }
        if (e.isONE()) {
            return multiply(f);
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c = m1.getValue();
            Word e1 = m1.getKey();
            Word e2 = e.multiply(e1.multiply(f));
            pv.put(e2, c);
        }
        return p;
    }


    /**
     * GenWordPolynomial left and right multiplication. Product with
     * ring element and two words.
     * @param s coefficient.
     * @param e left word.
     * @param f right word.
     * @return e * this * s * f.
     */
    public GenWordPolynomial<C> multiply(C s, Word e, Word f) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (e.isONE()) {
            return multiply(s, f);
        }
        C c = ring.coFac.getONE();
        return multiply(c,e,s,f); // sic, history
    }


    /**
     * GenWordPolynomial left and right multiplication. Product with
     * ring element and two words.
     * @param s coefficient.
     * @param e left word.
     * @param t coefficient.
     * @param f right word.
     * @return s * e * this * t * f.
     */
    public GenWordPolynomial<C> multiply(C s, Word e, C t, Word f) {
        if (s == null) {
            return ring.getZERO();
        }
        if (s.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            C c = s.multiply(c1).multiply(t); // check non zero if not domain
            if (!c.isZERO()) {
                Word e1 = m1.getKey();
                Word e2 = e.multiply(e1).multiply(f);
                pv.put(e2, c);
            }
        }
        return p;
    }


    /**
     * GenWordPolynomial multiplication. Product with word.
     * @param e word (!= null).
     * @return this * e.
     */
    public GenWordPolynomial<C> multiply(Word e) {
        if (this.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m1 : val.entrySet()) {
            C c1 = m1.getValue();
            Word e1 = m1.getKey();
            Word e2 = e1.multiply(e);
            pv.put(e2, c1);
        }
        return p;
    }


    /**
     * GenWordPolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m.
     */
    public GenWordPolynomial<C> multiply(Map.Entry<Word, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * GenWordPolynomial division. Division by coefficient ring element. Fails,
     * if exact division is not possible.
     * @param s coefficient.
     * @return this/s.
     */
    public GenWordPolynomial<C> divide(C s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException(this.getClass().getName() + " division by zero");
        }
        if (this.isZERO()) {
            return this;
        }
        //C t = s.inverse();
        //return multiply(t);
        GenWordPolynomial<C> p = ring.getZERO().copy();
        SortedMap<Word, C> pv = p.val;
        for (Map.Entry<Word, C> m : val.entrySet()) {
            Word e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s);
            if (debug) {
                C x = c1.remainder(s);
                if (!x.isZERO()) {
                    logger.info("divide x = " + x);
                    throw new ArithmeticException(this.getClass().getName() + " no exact division: " + c1
                                    + "/" + s);
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
     * GenWordPolynomial division with remainder. Fails, if exact division by
     * leading base coefficient is not possible. Meaningful only for univariate
     * polynomials over fields, but works in any case.
     * @param S nonzero GenWordPolynomial with invertible leading coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder and
     *         deg(remainder) &lt; deg(S) or remiander = 0.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial<C>[] quotientRemainder(GenWordPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(this.getClass().getName() + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException(this.getClass().getName() + " lbcf not invertible " + c);
        }
        C ci = c.inverse();
        C one = ring.coFac.getONE();
        assert (ring.alphabet == S.ring.alphabet);
        WordFactory.WordComparator cmp = ring.alphabet.getDescendComparator();
        Word e = S.leadingWord();
        GenWordPolynomial<C> h;
        GenWordPolynomial<C> q = ring.getZERO().copy();
        GenWordPolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            Word f = r.leadingWord();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                Word[] g = f.divideWord(e); // divide not sufficient
                //logger.info("div: f = " + f + ", e = " + e + ", g = " + g[0] + ", " + g[1]);
                a = a.multiply(ci);
                q = q.sum(a, g[0].multiply(g[1]));
                h = S.multiply(a, g[0], one, g[1]);
                r = r.subtract(h);
                Word fr = r.leadingWord();
                if (cmp.compare(f, fr) > 0) { // non noetherian reduction
                    throw new RuntimeException("possible infinite loop: f = " + f + ", fr = " + fr);
                }
            } else {
                break;
            }
        }
        GenWordPolynomial<C>[] ret = new GenWordPolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenWordPolynomial division. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenWordPolynomial with invertible leading coefficient.
     * @return quotient with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    public GenWordPolynomial<C> divide(GenWordPolynomial<C> S) {
        return quotientRemainder(S)[0];
    }


    /**
     * GenWordPolynomial remainder. Fails, if exact division by leading base
     * coefficient is not possible. Meaningful only for univariate polynomials
     * over fields, but works in any case.
     * @param S nonzero GenWordPolynomial with invertible leading coefficient.
     * @return remainder with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    public GenWordPolynomial<C> remainder(GenWordPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(this.getClass().getName() + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException(this.getClass().getName() + " lbc not invertible " + c);
        }
        C ci = c.inverse();
        C one = ring.coFac.getONE();
        assert (ring.alphabet == S.ring.alphabet);
        WordFactory.WordComparator cmp = ring.alphabet.getDescendComparator();
        Word e = S.leadingWord();
        GenWordPolynomial<C> h;
        GenWordPolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            Word f = r.leadingWord();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                Word[] g = f.divideWord(e); // divide not sufficient
                //logger.info("rem: f = " + f + ", e = " + e + ", g = " + g[0] + ", " + g[1]);
                a = a.multiply(ci);
                h = S.multiply(a, g[0], one, g[1]);
                r = r.subtract(h);
                Word fr = r.leadingWord();
                if (cmp.compare(f, fr) > 0) { // non noetherian reduction
                    throw new RuntimeException("possible infinite loop: f = " + f + ", fr = " + fr);
                }
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenWordPolynomial greatest common divisor. Only for univariate
     * polynomials over fields.
     * @param S GenWordPolynomial.
     * @return gcd(this,S).
     */
    public GenWordPolynomial<C> gcd(GenWordPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (ring.alphabet.length() != 1) {
            throw new IllegalArgumentException("no univariate polynomial " + ring);
        }
        GenWordPolynomial<C> x;
        GenWordPolynomial<C> q = this;
        GenWordPolynomial<C> r = S;
        while (!r.isZERO()) {
            x = q.remainder(r);
            q = r;
            r = x;
        }
        return q.monic(); // normalize
    }


    /**
     * GenWordPolynomial extended greatest comon divisor. Only for univariate
     * polynomials over fields.
     * @param S GenWordPolynomial.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial<C>[] egcd(GenWordPolynomial<C> S) {
        GenWordPolynomial<C>[] ret = new GenWordPolynomial[3];
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
        if (ring.alphabet.length() != 1) {
            throw new IllegalArgumentException("no univariate polynomial " + ring);
        }
        if (this.isConstant() && S.isConstant()) {
            C t = this.leadingBaseCoefficient();
            C s = S.leadingBaseCoefficient();
            C[] gg = t.egcd(s);
            //System.out.println("coeff gcd = " + Arrays.toString(gg));
            GenWordPolynomial<C> z = this.ring.getZERO();
            ret[0] = z.sum(gg[0]);
            ret[1] = z.sum(gg[1]);
            ret[2] = z.sum(gg[2]);
            return ret;
        }
        GenWordPolynomial<C>[] qr;
        GenWordPolynomial<C> q = this;
        GenWordPolynomial<C> r = S;
        GenWordPolynomial<C> c1 = ring.getONE().copy();
        GenWordPolynomial<C> d1 = ring.getZERO().copy();
        GenWordPolynomial<C> c2 = ring.getZERO().copy();
        GenWordPolynomial<C> d2 = ring.getONE().copy();
        GenWordPolynomial<C> x1;
        GenWordPolynomial<C> x2;
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
     * GenWordPolynomial half extended greatest comon divisor. Only for
     * univariate polynomials over fields.
     * @param S GenWordPolynomial.
     * @return [ gcd(this,S), a ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial<C>[] hegcd(GenWordPolynomial<C> S) {
        GenWordPolynomial<C>[] ret = new GenWordPolynomial[2];
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
        if (ring.alphabet.length() != 1) {
            throw new IllegalArgumentException("no univariate polynomial " + ring);
        }
        GenWordPolynomial<C>[] qr;
        GenWordPolynomial<C> q = this;
        GenWordPolynomial<C> r = S;
        GenWordPolynomial<C> c1 = ring.getONE().copy();
        GenWordPolynomial<C> d1 = ring.getZERO().copy();
        GenWordPolynomial<C> x1;
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
     * GenWordPolynomial inverse. Required by RingElem. Throws not invertible
     * exception.
     */
    public GenWordPolynomial<C> inverse() {
        if (isUnit()) { // only possible if ldbcf is unit
            C c = leadingBaseCoefficient().inverse();
            return ring.getONE().multiply(c);
        }
        throw new NotInvertibleException("element not invertible " + this + " :: " + ring);
    }


    /**
     * GenWordPolynomial modular inverse. Only for univariate polynomials over
     * fields.
     * @param m GenWordPolynomial.
     * @return a with with a*this = 1 mod m.
     */
    public GenWordPolynomial<C> modInverse(GenWordPolynomial<C> m) {
        if (this.isZERO()) {
            throw new NotInvertibleException("zero is not invertible");
        }
        GenWordPolynomial<C>[] hegcd = this.hegcd(m);
        GenWordPolynomial<C> a = hegcd[0];
        if (!a.isUnit()) { // gcd != 1
            throw new NotInvertibleException("element not invertible, gcd != 1");
        }
        GenWordPolynomial<C> b = hegcd[1];
        if (b.isZERO()) { // when m divides this, e.g. m.isUnit()
            throw new NotInvertibleException("element not invertible, divisible by modul");
        }
        return b;
    }


    /**
     * Iterator over coefficients.
     * @return val.values().iterator().
     */
    public Iterator<C> coefficientIterator() {
        return val.values().iterator();
    }


    /**
     * Iterator over words.
     * @return val.keySet().iterator().
     */
    public Iterator<Word> wordIterator() {
        return val.keySet().iterator();
    }


    /**
     * Iterator over monomials.
     * @return a PolyIterator.
     */
    public Iterator<WordMonomial<C>> iterator() {
        return new WordPolyIterator<C>(val);
    }


    /**
     * Map a unary function to the coefficients.
     * @param f evaluation functor.
     * @return new polynomial with coefficients f(this(e)).
     */
    public GenWordPolynomial<C> map(final UnaryFunctor<? super C, C> f) {
        GenWordPolynomial<C> n = ring.getZERO().copy();
        SortedMap<Word, C> nv = n.val;
        for (WordMonomial<C> m : this) {
            //logger.info("m = " + m);
            C c = f.eval(m.c);
            if (c != null && !c.isZERO()) {
                nv.put(m.e, c);
            }
        }
        return n;
    }

}
