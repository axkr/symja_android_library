/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordGroebnerBaseSeq;
import edu.jas.gb.WordReduction;
import edu.jas.gb.WordReductionSeq;
import edu.jas.gbufd.PolyGBUtil;
import edu.jas.kern.Scripting;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.Word;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Word Ideal implements some methods for ideal arithmetic, for example
 * containment, sum or product. <b>Note:</b> only two-sided ideals.
 * @author Heinz Kredel
 */
public class WordIdeal<C extends GcdRingElem<C>> implements Comparable<WordIdeal<C>>, Serializable {


    private static final Logger logger = LogManager.getLogger(WordIdeal.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The data structure is a list of word polynomials.
     */
    protected List<GenWordPolynomial<C>> list;


    /**
     * Reference to the word polynomial ring.
     */
    protected GenWordPolynomialRing<C> ring;


    /**
     * Indicator if list is a Groebner Base.
     */
    protected boolean isGB;


    /**
     * Indicator if test has been performed if this is a Groebner Base.
     */
    protected boolean testGB;


    /**
     * Groebner base engine.
     */
    protected final WordGroebnerBaseAbstract<C> bb;


    /**
     * Reduction engine.
     */
    protected final WordReduction<C> red;


    /**
     * Constructor.
     * @param ring word polynomial ring
     */
    public WordIdeal(GenWordPolynomialRing<C> ring) {
        this(ring, new ArrayList<GenWordPolynomial<C>>());
    }


    /**
     * Constructor.
     * @param ring word polynomial ring
     * @param list word polynomial list
     */
    public WordIdeal(GenWordPolynomialRing<C> ring, List<GenWordPolynomial<C>> list) {
        this(ring, list, false);
    }


    /**
     * Constructor.
     * @param ring word polynomial ring
     * @param list word polynomial list
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public WordIdeal(GenWordPolynomialRing<C> ring, List<GenWordPolynomial<C>> list,
                    WordGroebnerBaseAbstract<C> bb, WordReduction<C> red) {
        this(ring, list, false, bb, red);
    }


    /**
     * Constructor.
     * @param ring word polynomial ring
     * @param list word polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     */
    public WordIdeal(GenWordPolynomialRing<C> ring, List<GenWordPolynomial<C>> list, boolean gb) {
        this(ring, list, gb, new WordGroebnerBaseSeq<C>(), new WordReductionSeq<C>());
        //this(list, gb, topt, GBFactory.getImplementation(list.ring.coFac));
    }


    /**
     * Constructor.
     * @param ring word polynomial ring
     * @param list word polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     */
    public WordIdeal(GenWordPolynomialRing<C> ring, List<GenWordPolynomial<C>> list, boolean gb,
                    WordGroebnerBaseAbstract<C> bb) {
        this(ring, list, gb, bb, bb.red);
    }


    /**
     * Constructor.
     * @param ring word polynomial ring
     * @param list word polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public WordIdeal(GenWordPolynomialRing<C> ring, List<GenWordPolynomial<C>> list, boolean gb,
                    WordGroebnerBaseAbstract<C> bb, WordReduction<C> red) {
        if (ring == null) {
            throw new IllegalArgumentException("ring may not be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("list may not be null");
        }
        this.ring = ring;
        this.list = list;
        this.isGB = gb;
        this.testGB = (gb ? true : false); // ??
        this.bb = bb;
        this.red = red;
        if (debug) {
            logger.info("constructed: {}", this);
        }
    }


    /**
     * Clone this.
     * @return a copy of this.
     */
    public WordIdeal<C> copy() {
        return new WordIdeal<C>(ring, new ArrayList<GenWordPolynomial<C>>(list), isGB, bb, red);
    }


    /**
     * Get the List of GenWordPolynomials.
     * @return (cast) list.list
     */
    public List<GenWordPolynomial<C>> getList() {
        return list;
    }


    /**
     * Get the GenWordPolynomialRing.
     * @return (cast) list.ring
     */
    public GenWordPolynomialRing<C> getRing() {
        return ring;
    }


    /**
     * Get the zero ideal.
     * @return ideal(0)
     */
    public WordIdeal<C> getZERO() {
        List<GenWordPolynomial<C>> z = new ArrayList<GenWordPolynomial<C>>(0);
        return new WordIdeal<C>(ring, z, true, bb, red);
    }


    /**
     * Get the one ideal.
     * @return ideal(1)
     */
    public WordIdeal<C> getONE() {
        List<GenWordPolynomial<C>> one = new ArrayList<GenWordPolynomial<C>>(1);
        one.add(getRing().getONE());
        return new WordIdeal<C>(ring, one, true, bb, red);
    }


    /**
     * String representation of the word ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("(");
        boolean first = true;
        for (GenWordPolynomial<C> p : list) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(p.toString());
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        StringBuffer s = new StringBuffer();
        switch (Scripting.getLang()) {
        case Ruby:
            s.append("WordPolyIdeal.new(");
            break;
        case Python:
        default:
            s.append("WordIdeal(");
        }
        if (ring != null) {
            s.append(ring.toScript());
        }
        if (list == null) {
            s.append(")");
            return s.toString();
        }
        switch (Scripting.getLang()) {
        case Ruby:
            s.append(",\"\",[");
            break;
        case Python:
        default:
            s.append(",list=[");
        }
        boolean first = true;
        String sa = null;
        for (GenWordPolynomial<C> oa : list) {
            sa = oa.toScript();
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            //s.append("( " + sa + " )");
            s.append(sa);
        }
        s.append("])");
        return s.toString();
    }


    /**
     * Comparison with any other object. <b>Note:</b> If not both ideals are
     * Groebner Bases, then false may be returned even the ideals are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof WordIdeal)) {
            logger.warn("equals no Ideal");
            return false;
        }
        WordIdeal<C> B = null;
        try {
            B = (WordIdeal<C>) b;
        } catch (ClassCastException ignored) {
            return false;
        }
        SortedSet<GenWordPolynomial<C>> s = new TreeSet<GenWordPolynomial<C>>(list);
        SortedSet<GenWordPolynomial<C>> t = new TreeSet<GenWordPolynomial<C>>(B.list);
        if (isGB && B.isGB) { //requires also monic polys
            return s.equals(t);
        }
        if (s.equals(t)) {
            return true;
        }
        //System.out.println("no GBs contains");
        return this.contains(B) && B.contains(this);
    }


    /**
     * WordIdeal comparison.
     * @param L other word ideal.
     * @return compareTo() of polynomial lists.
     */
    public int compareTo(WordIdeal<C> L) {
        int si = Math.min(L.list.size(), list.size());
        int s = 0;
        final Comparator<Word> wc = ring.alphabet.getAscendComparator();
        Comparator<GenWordPolynomial<C>> cmp = new Comparator<GenWordPolynomial<C>>() {


            public int compare(GenWordPolynomial<C> p1, GenWordPolynomial<C> p2) {
                Word w1 = p1.leadingWord();
                Word w2 = p2.leadingWord();
                if (w1 == null) {
                    return -1; // dont care
                }
                if (w2 == null) {
                    return 1; // dont care
                }
                if (w1.length() != w2.length()) {
                    if (w1.length() > w2.length()) {
                        return 1; // dont care
                    }
                    return -1; // dont care
                }
                return wc.compare(w1, w2);
            }
        };

        List<GenWordPolynomial<C>> l1 = new ArrayList<GenWordPolynomial<C>>(list);
        List<GenWordPolynomial<C>> l2 = new ArrayList<GenWordPolynomial<C>>(L.list);
        //Collections.sort(l1);
        //Collections.sort(l2);
        Collections.sort(l1, cmp);
        Collections.sort(l2, cmp);
        for (int i = 0; i < si; i++) {
            GenWordPolynomial<C> a = l1.get(i);
            GenWordPolynomial<C> b = l2.get(i);
            s = a.compareTo(b);
            if (s != 0) {
                return s;
            }
        }
        if (list.size() > si) {
            return 1;
        }
        if (L.list.size() > si) {
            return -1;
        }
        return s;
    }


    /**
     * Hash code for this word ideal.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = list.hashCode();
        if (isGB) {
            h = h << 1;
        }
        if (testGB) {
            h += 1;
        }
        return h;
    }


    /**
     * Test if ZERO ideal.
     * @return true, if this is the 0 ideal, else false
     */
    public boolean isZERO() {
        //return list.isZERO();
        if (list == null) { // never true
            return true;
        }
        for (GenWordPolynomial<C> p : list) {
            if (p == null) {
                continue;
            }
            if (!p.isZERO()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Test if ONE is contained in the ideal. To test for a proper ideal use
     * <code>! id.isONE()</code>.
     * @return true, if this is the 1 ideal, else false
     */
    public boolean isONE() {
        //return list.isONE();
        if (list == null) {
            return false;
        }
        for (GenWordPolynomial<C> p : list) {
            if (p == null) {
                continue;
            }
            if (p.isONE()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Test if this is a twosided Groebner base.
     * @return true, if this is a twosided Groebner base, else false
     */
    public boolean isGB() {
        if (testGB) {
            return isGB;
        }
        logger.warn("isGB computing");
        isGB = bb.isGB(getList());
        testGB = true;
        return isGB;
    }


    /**
     * Do Groebner Base. Compute the Groebner Base for this ideal.
     */
    @SuppressWarnings("unchecked")
    public void doGB() {
        if (isGB && testGB) {
            return;
        }
        List<GenWordPolynomial<C>> G = getList();
        logger.info("doGB computing = {}", G);
        list = bb.GB(G);
        isGB = true;
        testGB = true;
        return;
    }


    /**
     * Groebner Base. Get a Groebner Base for this ideal.
     * @return twosidedGB(this)
     */
    public WordIdeal<C> GB() {
        if (isGB) {
            return this;
        }
        doGB();
        return this;
    }


    /**
     * Word ideal containment. Test if B is contained in this ideal. Note: this
     * is eventually modified to become a Groebner Base.
     * @param B word ideal
     * @return true, if B is contained in this, else false
     */
    public boolean contains(WordIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return true;
        }
        return contains(B.getList());
    }


    /**
     * Word ideal containment. Test if b is contained in this ideal. Note: this
     * is eventually modified to become a Groebner Base.
     * @param b word polynomial
     * @return true, if b is contained in this, else false
     */
    public boolean contains(GenWordPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (this.isZERO()) {
            return false;
        }
        if (!isGB) {
            doGB();
        }
        GenWordPolynomial<C> z = red.normalform(getList(), b);
        if (z == null || z.isZERO()) {
            return true;
        }
        return false;
    }


    /**
     * Word ideal containment. Test if each b in B is contained in this ideal.
     * Note: this is eventually modified to become a Groebner Base.
     * @param B list of word polynomials
     * @return true, if each b in B is contained in this, else false
     */
    public boolean contains(List<GenWordPolynomial<C>> B) {
        if (B == null || B.size() == 0) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (!isGB) {
            doGB();
        }
        for (GenWordPolynomial<C> b : B) {
            if (b == null) {
                continue;
            }
            if (!contains(b)) {
                System.out.println("contains nf(b) != 0: " + b);
                return false;
            }
        }
        return true;
    }


    /**
     * Word ideal summation. Generators for the sum of ideals. Note: if both
     * ideals are Groebner bases, a Groebner base is returned.
     * @param B word ideal
     * @return ideal(this+B)
     */
    public WordIdeal<C> sum(WordIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return B;
        }
        return sum(B.getList());
        // int s = getList().size() + B.getList().size();
        // List<GenWordPolynomial<C>> c;
        // c = new ArrayList<GenWordPolynomial<C>>(s);
        // c.addAll(getList());
        // c.addAll(B.getList());
        // WordIdeal<C> I = new WordIdeal<C>(getRing(), c, false, bb);
        // if (isGB && B.isGB) {
        //     I.doGB();
        // }
        // return I;
    }


    /**
     * Word summation. Generators for the sum of ideal and a polynomial. Note:
     * if this ideal is a Groebner base, a Groebner base is returned.
     * @param b word polynomial
     * @return ideal(this+{b})
     */
    public WordIdeal<C> sum(GenWordPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        int s = getList().size() + 1;
        List<GenWordPolynomial<C>> c;
        c = new ArrayList<GenWordPolynomial<C>>(s);
        c.addAll(getList());
        c.add(b);
        WordIdeal<C> I = new WordIdeal<C>(getRing(), c, false, bb);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Word summation. Generators for the sum of this ideal and a list of
     * polynomials. Note: if this ideal is a Groebner base, a Groebner base is
     * returned.
     * @param L list of word polynomials
     * @return ideal(this+L)
     */
    public WordIdeal<C> sum(List<GenWordPolynomial<C>> L) {
        if (L == null || L.isEmpty()) {
            return this;
        }
        int s = getList().size() + L.size();
        List<GenWordPolynomial<C>> c = new ArrayList<GenWordPolynomial<C>>(s);
        c.addAll(getList());
        c.addAll(L);
        WordIdeal<C> I = new WordIdeal<C>(getRing(), c, false, bb);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Product. Generators for the product of ideals. Note: if both ideals are
     * Groebner bases, a Groebner base is returned.
     * @param B word ideal
     * @return ideal(this*B)
     */
    public WordIdeal<C> product(WordIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = getList().size() * B.getList().size();
        List<GenWordPolynomial<C>> c;
        c = new ArrayList<GenWordPolynomial<C>>(s);
        for (GenWordPolynomial<C> p : getList()) {
            for (GenWordPolynomial<C> q : B.getList()) {
                q = p.multiply(q);
                c.add(q);
            }
        }
        WordIdeal<C> I = new WordIdeal<C>(getRing(), c, false, bb);
        if (isGB && B.isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Left product. Generators for the product this by a polynomial.
     * @param b word polynomial
     * @return ideal(this*b)
     */
    public WordIdeal<C> product(GenWordPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        List<GenWordPolynomial<C>> c;
        c = new ArrayList<GenWordPolynomial<C>>(getList().size());
        for (GenWordPolynomial<C> p : getList()) {
            GenWordPolynomial<C> q = p.multiply(b);
            c.add(q);
        }
        WordIdeal<C> I = new WordIdeal<C>(getRing(), c, false, bb);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /*
     * Intersection. Generators for the intersection of ideals. Using an
     * iterative algorithm.
     * @param Bl list of word ideals
     * @return ideal(cap_i B_i), a Groebner base
     */
    public WordIdeal<C> intersect(List<WordIdeal<C>> Bl) {
        if (Bl == null || Bl.size() == 0) {
            return getZERO();
        }
        WordIdeal<C> I = null;
        for (WordIdeal<C> B : Bl) {
            if (I == null) {
                I = B;
                continue;
            }
            if (I.isONE()) {
                return I;
            }
            I = I.intersect(B);
        }
        return I;
    }


    /*
     * Intersection. Generators for the intersection of ideals.
     * @param B word ideal
     * @return ideal(this cap B), a Groebner base
     */
    public WordIdeal<C> intersect(WordIdeal<C> B) {
        if (B == null || B.isZERO()) { // (0)
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        List<GenWordPolynomial<C>> c = PolyGBUtil.<C> intersect(getRing(), getList(), B.getList(), bb);
        WordIdeal<C> I = new WordIdeal<C>(getRing(), c, true, bb);
        return I;
    }


    /*
     * Intersection. Generators for the intersection of a ideal with a
     * word polynomial ring. The polynomial ring of this ideal must be
     * a contraction of R and the TermOrder must be an elimination
     * order.
     * @param R word polynomial ring
     * @return ideal(this cap R)
     */
    public WordIdeal<C> intersect(GenWordPolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        List<GenWordPolynomial<C>> H = PolyUtil.<C> intersect(R, getList());
        return new WordIdeal<C>(R, H, isGB, bb);
    }


    /*
     * Eliminate. Generators for the intersection of this ideal with a word
     * polynomial ring. The word polynomial ring of this ideal must be a
     * contraction of R and the TermOrder must be an elimination order.
     * @param R word polynomial ring
     * @return ideal(this cap R)
     */
    public WordIdeal<C> eliminate(GenWordPolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        if (getRing().equals(R)) {
            return this;
        }
        return intersect(R);
    }


    /*
     * Quotient. Generators for the word ideal quotient.
     * @param h word polynomial
     * @return ideal(this : h), a Groebner base
    public WordIdeal<C> quotient(GenWordPolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        List<GenWordPolynomial<C>> H;
        H = new ArrayList<GenWordPolynomial<C>>(1);
        H.add(h);
        WordIdeal<C> Hi = new WordIdeal<C>(getRing(), H, true, bb);
        WordIdeal<C> I = this.intersect(Hi);
        List<GenWordPolynomial<C>> Q;
        Q = new ArrayList<GenWordPolynomial<C>>(I.getList().size());
        for (GenWordPolynomial<C> q : I.getList()) {
            q = (GenWordPolynomial<C>) q.divide(h); // remainder == 0
            Q.add(q);
        }
        return new WordIdeal<C>(getRing(), Q, true, bb);
    }
     */


    /*
     * Quotient. Generators for the word ideal quotient.
     * @param H word ideal
     * @return ideal(this : H), a Groebner base
    public WordIdeal<C> quotient(WordIdeal<C> H) {
        if (H == null || H.isZERO()) { // == (0)
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        WordIdeal<C> Q = null;
        for (GenWordPolynomial<C> h : H.getList()) {
            WordIdeal<C> Hi = this.quotient(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }
     */


    /**
     * Power. Generators for the power of this word ideal. Note: if this ideal
     * is a Groebner base, a Groebner base is returned.
     * @param d integer
     * @return ideal(this^d)
     */
    public WordIdeal<C> power(int d) {
        if (d <= 0) {
            return getONE();
        }
        if (this.isZERO() || this.isONE()) {
            return this;
        }
        WordIdeal<C> c = this;
        for (int i = 1; i < d; i++) {
            c = c.product(this);
        }
        return c;
    }


    /**
     * Normalform for element.
     * @param h word polynomial
     * @return left normalform of h with respect to this
     */
    public GenWordPolynomial<C> normalform(GenWordPolynomial<C> h) {
        if (h == null) {
            return h;
        }
        if (h.isZERO()) {
            return h;
        }
        if (this.isZERO()) {
            return h;
        }
        GenWordPolynomial<C> r;
        r = red.normalform(getList(), h);
        return r;
    }


    /**
     * Normalform for list of word elements.
     * @param L word polynomial list
     * @return list of left normalforms of the elements of L with respect to
     *         this
     */
    public List<GenWordPolynomial<C>> normalform(List<GenWordPolynomial<C>> L) {
        if (L == null) {
            return L;
        }
        if (L.size() == 0) {
            return L;
        }
        if (this.isZERO()) {
            return L;
        }
        List<GenWordPolynomial<C>> M = new ArrayList<GenWordPolynomial<C>>(L.size());
        for (GenWordPolynomial<C> h : L) {
            GenWordPolynomial<C> r = normalform(h);
            if (r != null && !r.isZERO()) {
                M.add(r);
            }
        }
        return M;
    }


    /**
     * Inverse for element modulo this ideal.
     * @param h word polynomial
     * @return inverse of h with respect to this, if defined
     */
    public GenWordPolynomial<C> inverse(GenWordPolynomial<C> h) {
        if (h == null || h.isZERO()) {
            throw new NotInvertibleException("zero not invertible");
        }
        if (this.isZERO()) {
            throw new NotInvertibleException("zero ideal");
        }
        if (h.isUnit()) {
            return h.inverse();
        }
        if (isUnit(h)) {
            logger.warn("{} is invertable, but inverse not computed", h);
        }
        throw new UnsupportedOperationException("inverse of " + h);
        /* TODO compute inverse
        doGB();
        List<GenWordPolynomial<C>> F = new ArrayList<GenWordPolynomial<C>>(1 + list.list.size());
        F.add(h);
        F.addAll(getList());
        //System.out.println("F = " + F);
        WordExtendedGB<C> x = bb.extLeftGB(F);
        List<GenWordPolynomial<C>> G = x.G;
        //System.out.println("G = " + G);
        GenWordPolynomial<C> one = null;
        int i = -1;
        for (GenWordPolynomial<C> p : G) {
            i++;
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                one = p;
                break;
            }
        }
        if (one == null) {
            throw new NotInvertibleException("one == null: h = " + h);
        }
        List<GenWordPolynomial<C>> row = x.G2F.get(i); // != -1
        //System.out.println("row = " + row);
        GenWordPolynomial<C> g = row.get(0);
        if (g == null || g.isZERO()) {
            throw new NotInvertibleException("g == 0: h = " + h);
        }
        GenWordPolynomial<C> gp = red.leftNormalform(getList(), g);
        if (gp.isZERO()) { // can happen with solvable rings
            throw new NotInvertibleException("solv|gp == 0: h = " + h + ", g = " + g);
        }
        // adjust leading coefficient of g to get g*h == 1
        GenWordPolynomial<C> f = g.multiply(h);
        //System.out.println("f = " + f);
        GenWordPolynomial<C> k = red.leftNormalform(getList(), f);
        //System.out.println("k = " + k);
        if (!k.isONE()) {
            C lbc = k.leadingBaseCoefficient();
            lbc = lbc.inverse();
            g = g.multiply(lbc);
        }
        return g;
        */
    }


    /**
     * Test if element is a unit modulo this ideal.
     * @param h word polynomial
     * @return true if h is a unit with respect to this, else false
     */
    public boolean isUnit(GenWordPolynomial<C> h) {
        if (h == null || h.isZERO()) {
            return false;
        }
        if (this.isZERO()) {
            return false;
        }
        if (h.isUnit()) {
            return true;
        }
        // test this + (h) == 1: then ex p, q: pp*this**qq + p*h*q = 1
        List<GenWordPolynomial<C>> F = new ArrayList<GenWordPolynomial<C>>(1 + list.size());
        F.add(h);
        F.addAll(getList());
        List<GenWordPolynomial<C>> G = bb.GB(F);
        if (debug) {
            logger.info("isUnit GB = {}", G);
        }
        for (GenWordPolynomial<C> p : G) {
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Ideal common zero test.
     * @return -1, 0 or 1 if dimension(this) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest() {
        if (this.isZERO()) {
            return 1;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return -1;
        }
        return bb.commonZeroTest(getList());
    }


    /**
     * Test if this ideal is maximal.
     * @return true, if this is certainly maximal and not one, 
     *         false, if this is one, has dimension &ge; 1 or it is not jet determined if it is maximal.
     */
    public boolean isMaximal() {
        if (commonZeroTest() != 0) {
            return false;
        }
        for (Long d : univariateDegrees()) {
            if (d > 1L) {
                return false;
            }
        }
        return true;
    }


    /**
     * Univariate head term degrees.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees() {
        List<Long> ud = new ArrayList<Long>();
        if (this.isZERO()) {
            return ud;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return ud;
        }
        return bb.univariateDegrees(getList());
    }


    /*
     * Construct univariate polynomials of minimal degree in all variables in
     * zero dimensional ideal(G).
     * @return list of univariate word polynomial of minimal degree in each
     *         variable in ideal(G)
     */


    /*
     * Construct univariate polynomial of minimal degree in variable i in zero
     * dimensional ideal(G).
     * @param i variable index.
     * @return univariate word polynomial of minimal degree in variable i in
     *         ideal(G)
     */

}
