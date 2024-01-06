/*
 * $Id$
 */

package edu.jas.poly;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.PreemptStatus;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenVector;


/**
 * GenExteriorPolynomialRing generic antisymmetric polynomial factory
 * implementing RingFactory; Factory for antisymmetric polynomials (in fact
 * vectors) over C. Objects of this class are intended to be immutable. Only the
 * coefficients are modeled with generic types, the "exponents" are fixed to
 * IndexList. C can also be a non integral domain, e.g. a ModInteger, i.e. it
 * may contain zero divisors, since multiply() does check for zero coefficients
 * and index lists.
 * @see "masnc.DIPE.mi from SAC2/MAS"
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public final class GenExteriorPolynomialRing<C extends RingElem<C>>
                implements RingFactory<GenExteriorPolynomial<C>> {
    /*, Iterable<GenExteriorPolynomial<C>>*/


    /**
     * The factory for the coefficients.
     */
    public final RingFactory<C> coFac;


    /**
     * The factory for the IndexList.
     */
    public final IndexFactory ixfac;


    /**
     * The constant polynomial 0 for this ring.
     */
    public final GenExteriorPolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring.
     */
    public final GenExteriorPolynomial<C> ONE;


    /**
     * The constant empty index lists exponent for this ring.
     */
    public final IndexList wone;


    /**
     * A default random sequence generator.
     */
    final static Random random = new Random();


    /**
     * Indicator if this ring is a field.
     */
    private int isField = -1; // initially unknown


    /**
     * Log4j logger object.
     */
    private static final Logger logger = LogManager.getLogger(GenExteriorPolynomialRing.class);


    /**
     * Flag to enable if preemptive interrupt is checked.
     */
    final boolean checkPreempt = PreemptStatus.isAllowed();


    /**
     * The constructor creates a polynomial factory object with the default term
     * order.
     * @param cf factory for coefficients of type C.
     * @param wf factory for index list.
     */
    public GenExteriorPolynomialRing(RingFactory<C> cf, IndexFactory wf) {
        coFac = cf;
        ixfac = wf;
        ZERO = new GenExteriorPolynomial<C>(this);
        C coeff = coFac.getONE();
        wone = ixfac.getONE();
        ONE = new GenExteriorPolynomial<C>(this, coeff, wone);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param s array of variable names.
     */
    public GenExteriorPolynomialRing(RingFactory<C> cf, String[] s) {
        this(cf, new IndexFactory(s.length));
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param s string of single letter variable names.
     */
    public GenExteriorPolynomialRing(RingFactory<C> cf, String s) {
        this(cf, new IndexFactory(s.length())); // todo
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param o other polynomial ring.
     */
    public GenExteriorPolynomialRing(RingFactory<C> cf, GenExteriorPolynomialRing o) {
        this(cf, o.ixfac);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param fac polynomial ring.
     */
    public GenExteriorPolynomialRing(GenPolynomialRing<C> fac) {
        this(fac.coFac, new IndexFactory(fac.nvar));
    }


    /**
     * Copy this factory.
     * @return a clone of this.
     */
    public GenExteriorPolynomialRing<C> copy() {
        return new GenExteriorPolynomialRing<C>(coFac, this);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("ExteriorPolyRing(");
        if (coFac instanceof RingElem) {
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toString().trim());
        }
        s.append(", \"");
        s.append(ixfac.toString());
        s.append("\")");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        StringBuffer s = new StringBuffer();
        switch (Scripting.getLang()) {
        case Ruby:
            s.append("ExtPolyRing.new(");
            break;
        case Python:
        default:
            s.append("ExtPolyRing(");
        }
        if (coFac instanceof RingElem) {
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(", \"");
        s.append(ixfac.toScript());
        s.append("\")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof GenExteriorPolynomialRing)) {
            return false;
        }
        GenExteriorPolynomialRing<C> oring = (GenExteriorPolynomialRing<C>) other;
        if (!coFac.equals(oring.coFac)) {
            return false;
        }
        if (!ixfac.equals(oring.ixfac)) {
            return false;
        }
        return true;
    }


    /**
     * Hash code for this polynomial ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = (coFac.hashCode() << 11);
        h += ixfac.hashCode();
        return h;
    }


    /*
     * Get the variable names.
     * @return vars.
    public String[] getVars() {
        return ixfac.getVars(); // Java-5: Arrays.copyOf(vars,vars.length);
    }
     */


    /**
     * Get the zero element from the coefficients.
     * @return 0 as C.
     */
    public C getZEROCoefficient() {
        return coFac.getZERO();
    }


    /**
     * Get the one element from the coefficients.
     * @return 1 as C.
     */
    public C getONECoefficient() {
        return coFac.getONE();
    }


    /**
     * Get the zero element.
     * @return 0 as GenExteriorPolynomial<C>.
     */
    public GenExteriorPolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as GenExteriorPolynomial<C>.
     */
    public GenExteriorPolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative() && ixfac.length() < 1;
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return coFac.isAssociative();
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return coFac.isFinite(); // ixfac.isFinite() is true
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        if (isField > 0) {
            return true;
        }
        if (isField == 0) {
            return false;
        }
        if (coFac.isField() && ixfac.length() < 1) {
            isField = 1;
            return true;
        }
        isField = 0;
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
     * Get a (constant) GenExteriorPolynomial element from a coefficient value.
     * @param a coefficient.
     * @return a constant GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> valueOf(C a) {
        return new GenExteriorPolynomial<C>(this, a);
    }


    /**
     * Get a GenExteriorPolynomial element from a index list.
     * @param e index list.
     * @return a GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> valueOf(IndexList e) {
        return valueOf(coFac.getONE(), ixfac.valueOf(e));
    }


    /**
     * Get a GenExteriorPolynomial from an ExpVector.
     * @param e exponent vector.
     * @return a GenExteriorPolynomial, if exponents are &gt; 1 return ZERO.
     */
    public GenExteriorPolynomial<C> valueOf(ExpVector e) {
        return valueOf(coFac.getONE(), ixfac.valueOf(e));
    }


    /**
     * Get a GenExteriorPolynomial from a coefficient and a IndexList.
     * @param a coefficient.
     * @param e word.
     * @return a GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> valueOf(C a, IndexList e) {
        return new GenExteriorPolynomial<C>(this, a, e);
    }


    /**
     * Get a GenExteriorPolynomial from a coefficient and an ExpVector.
     * @param a coefficient.
     * @param e exponent vector.
     * @return a GenExteriorPolynomial, if exponents are &gt; 1 return ZERO.
     */
    public GenExteriorPolynomial<C> valueOf(C a, ExpVector e) {
        return new GenExteriorPolynomial<C>(this, a, ixfac.valueOf(e));
    }


    /**
     * Get a GenExteriorPolynomial from a multivariate GenPolynomial, terms with
     * exponents &gt; 1 are set to zero.
     * @param a multivariate GenPolynomial.
     * @return multivariate a GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> valueOf(GenPolynomial<C> a) {
        if (a.isZERO()) {
            return getZERO();
        }
        if (a.isONE()) {
            return getONE();
        }
        GenExteriorPolynomial<C> p = this.getZERO().copy();
        for (Map.Entry<ExpVector, C> m : a.val.entrySet()) {
            C c = m.getValue();
            ExpVector e = m.getKey();
            IndexList w = ixfac.valueOf(e);
            if (!w.isZERO()) {
                p.doPutToMap(w, c);
            }
        }
        return p;
    }


    /**
     * Get a GenExteriorPolynomial from a GenExteriorPolynomial with conformant
     * index lists.
     * @param a GenExteriorPolynomial.
     * @return a GenExteriorPolynomial with conformant index lists.
     */
    public GenExteriorPolynomial<C> valueOf(GenExteriorPolynomial<C> a) {
        if (a.isZERO()) {
            return getZERO();
        }
        if (a.isONE()) {
            return getONE();
        }
        GenExteriorPolynomial<C> p = this.getZERO().copy();
        for (Map.Entry<IndexList, C> m : a.val.entrySet()) {
            C c = m.getValue();
            IndexList e = m.getKey();
            if (!e.isZERO()) {
                IndexList w = ixfac.valueOf(e.val);
                if (!w.isZERO()) {
                    p.doPutToMap(w, c);
                }
            }
        }
        return p;
    }


    /**
     * Get a list of GenExteriorPolynomials from a list of GenPolynomials.
     * @param A GenPolynomial list.
     * @return a GenExteriorPolynomial list.
     */
    public List<GenExteriorPolynomial<C>> valueOf(List<GenPolynomial<C>> A) {
        List<GenExteriorPolynomial<C>> B = new ArrayList<GenExteriorPolynomial<C>>(A.size());
        if (A.isEmpty()) {
            return B;
        }
        for (GenPolynomial<C> a : A) {
            GenExteriorPolynomial<C> b = valueOf(a);
            B.add(b);
        }
        return B;
    }


    /**
     * Get a GenExteriorPolynomial maximal index list element.
     * @return a GenExteriorPolynomial with maximal index list.
     */
    public GenExteriorPolynomial<C> getIMAX() {
        return valueOf(coFac.getONE(), ixfac.imax);
    }


    /**
     * Get a (constant) GenExteriorPolynomial from a long value.
     * @param a long.
     * @return a GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> fromInteger(long a) {
        return new GenExteriorPolynomial<C>(this, coFac.fromInteger(a), wone);
    }


    /**
     * Get a (constant) GenExteriorPolynomial from a BigInteger value.
     * @param a BigInteger.
     * @return a GenExteriorPolynomial&lt;C&gt;.
     */
    public GenExteriorPolynomial<C> fromInteger(BigInteger a) {
        return new GenExteriorPolynomial<C>(this, coFac.fromInteger(a), wone);
    }


    /**
     * Get a GenExteriorPolynomial from a GenVector.
     * @param a GenVector.
     * @return a GenExteriorPolynomial.
     */
    public GenExteriorPolynomial<C> fromVector(GenVector<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        List<IndexList> gen = ixfac.generators();
        //System.out.println("gen = " + gen);
        GenExteriorPolynomial<C> ret = copy(ZERO);
        SortedMap<IndexList, C> tm = ret.val;
        int i = -1;
        for (C m : a.val) {
            i++;
            if (m.isZERO()) {
                continue;
            }
            IndexList ix = gen.get(i); //new IndexList(ixfac, new int[]{ i });
            tm.put(ix, m);
        }
        return ret;
    }


    /**
     * Get a list of GenExteriorPolynomials from a GenMatrix.
     * @param A GenMatrix
     * @return a list of GenExteriorPolynomials.
     */
    public List<GenExteriorPolynomial<C>> fromMatrix(GenMatrix<C> A) {
        List<GenExteriorPolynomial<C>> L = new ArrayList<GenExteriorPolynomial<C>>();
        if (A == null || A.isZERO()) { //|| A.isZERO()
            return L;
        }
        for (int i = 0; i < A.ring.rows; i++) {
            GenVector<C> v = A.getRow(i);
            if (v.isZERO()) {
                continue;
            }
            GenExteriorPolynomial<C> ep = fromVector(v);
            //System.out.println("ep = " + ep);
            L.add(ep);
        }
        return L;
    }


    /**
     * Determinant form exterior polynomial / matrix.
     * @param A list of GenExteriorPolynomials
     * @return determinant of 'matrix' A.
     */
    public C determinant(List<GenExteriorPolynomial<C>> A) {
        C det = coFac.getZERO();
        if (A == null || A.isEmpty()) {
            return det;
        }
        GenExteriorPolynomial<C> p = getONE();
        for (GenExteriorPolynomial<C> ep : A) {
            if (ep.isZERO()) {
                return det;
            }
            p = p.multiply(ep);
            //System.out.println("p = " + p);
            if (p.isZERO()) {
                return det;
            }
        }
        if (p.length() > 1) {
            return det;
        }
        det = p.leadingBaseCoefficient();
        // IndexList di = p.leadingIndexList();
        // if (di.equals(ixfac.imax)) { // to big
        //     det = p.leadingBaseCoefficient();
        // } else {
        //     System.out.println("p in det = " + p);
        // }
        return det;
    }


    /**
     * Get a GenExteriorPolynomial from a univariate GenPolynomial. Different
     * exponents are converted to different indexes.
     * @param a univariate GenPolynomial.
     * @return a multivariate GenExteriorPolynomial.
     */
    @SuppressWarnings("unchecked")
    public GenExteriorPolynomial<C> fromPolynomial(GenPolynomial<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        if (a.ring.nvar != 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        int deg = (int) a.degree();
        if (deg > ixfac.imax.maxDeg()) {
            throw new IllegalArgumentException(
                            "ensure deg <= ixfax.maxDeg: " + deg + " > " + ixfac.imax.maxDeg());
        }
        if (ixfac.imax.minDeg() > 0) {
            throw new IllegalArgumentException("ensure ixfax.minDeg == 0: " + ixfac.imax.minDeg());
        }
        List<IndexList> gen = ixfac.generators();
        //System.out.println("gen = " + gen);
        GenExteriorPolynomial<C> ret = copy(ZERO);
        SortedMap<IndexList, C> tm = ret.val;
        for (Monomial m : a) {
            int e = (int) m.e.getVal(0);
            IndexList ix = gen.get(e); //new IndexList(ixfac, new int[]{ e });
            C co = (C) m.c;
            tm.put(ix, co);
        }
        return ret;
    }


    /**
     * Resultant of two commutative polynaomials.
     * @param A GenPolynomial
     * @param B GenPolynomial
     * @return res(A,B).
     */
    public C resultant(GenPolynomial<C> A, GenPolynomial<C> B) {
        C det = coFac.getZERO();
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            return det;
        }
        int m = (int) A.degree();
        int n = (int) B.degree();
        GenExteriorPolynomial<C> a, b;
        a = fromPolynomial(A);
        b = fromPolynomial(B);
        if (m < n) {
            GenExteriorPolynomial<C> c = a;
            a = b;
            b = c;
            int t = m;
            m = n;
            n = t;
        }
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        List<GenExteriorPolynomial<C>> mat = new ArrayList<GenExteriorPolynomial<C>>(m + n);
        for (int i = n - 1; i >= 0; i--) {
            GenExteriorPolynomial<C> c = a.shiftIndex(i);
            mat.add(c);
        }
        for (int i = m - 1; i >= 0; i--) {
            GenExteriorPolynomial<C> c = b.shiftIndex(i);
            mat.add(c);
        }
        //System.out.println("mat = " + mat);
        det = determinant(mat);
        return det;
    }


    /**
     * Random polynomial. Generates a random polynomial.
     * @param n number of terms.
     * @return a random polynomial.
     */
    public GenExteriorPolynomial<C> random(int n) {
        return random(n, random);
    }


    /**
     * Random polynomial. Generates a random polynomial with k = 5, l = n, d =
     * 3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenExteriorPolynomial<C> random(int n, Random rnd) {
        return random(5, n, 3, rnd);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal length of a random word.
     * @return a random polynomial.
     */
    public GenExteriorPolynomial<C> random(int k, int l, int d) {
        return random(k, l, d, random);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal length of a random word.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenExteriorPolynomial<C> random(int k, int l, int d, Random rnd) {
        GenExteriorPolynomial<C> r = getZERO(); //.clone() or copy( ZERO );
        // add l random coeffs and words of maximal length d
        int dm = Math.min(d, ixfac.imaxlength); //length());
        for (int i = 0; i < l; i++) {
            int di = Math.abs(rnd.nextInt() % dm);
            IndexList e = ixfac.random(di, 0.5f, rnd).abs(); // s only = 0, 1
            //System.out.println("e++ = " + e);
            C a = coFac.random(k, rnd);
            //System.out.println("e = " + e + " a = " + a);
            r = r.sum(a, e); // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Generate a random k-form polynomial.
     * @param kl bitsize of random coefficients.
     * @param l number of terms.
     * @param k length of any random word.
     * @return a random k-form polynomial.
     */
    public GenExteriorPolynomial<C> randomForm(int kl, int l, int k) {
        return randomForm(kl, l, k, random);
    }


    /**
     * Generate a random k-form polynomial.
     * @param kl bitsize of random coefficients.
     * @param l number of terms.
     * @param k length of any random word.
     * @param rnd is a source for random bits.
     * @return a random k-form polynomial.
     */
    public GenExteriorPolynomial<C> randomForm(int kl, int l, int k, Random rnd) {
        GenExteriorPolynomial<C> r = getZERO(); //.copy(); // or copy( ZERO );
        // add l random coeffs and index lists of length k
        if (k > ixfac.imaxlength || k < 0) {
            return r;
        }
        if (k == 0) {
            return getZERO();
        }
        for (int i = 0; i < l; i++) {
            IndexList e = ixfac.random(k, 0.5f, rnd).abs(); // s only = 0, 1
            //System.out.println("e_k = " + e);
            if (e.length() != k) {
                continue;
            }
            C a = coFac.random(kl, rnd);
            //System.out.println("e = " + e + " a = " + a);
            r = r.sum(a, e); // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c polynomial to copy.
     * @return a copy of c.
     */
    public GenExteriorPolynomial<C> copy(GenExteriorPolynomial<C> c) {
        return new GenExteriorPolynomial<C>(this, c.val);
    }


    /**
     * Parse a polynomial with the use of GenExteriorPolynomialTokenizer.
     * @param s String.
     * @return GenExteriorPolynomial from s.
     */
    public GenExteriorPolynomial<C> parse(String s) {
        String val = s;
        if (!s.contains("|")) {
            val = val.replace("{", "").replace("}", "");
        }
        return parse(new StringReader(val));
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer.
     * @param r Reader.
     * @return next GenExteriorPolynomial from r.
     */
    @SuppressWarnings("unchecked")
    public GenExteriorPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(r);
        GenExteriorPolynomial<C> a;
        try {
            a = tok.nextExteriorPolynomial(this);
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
            logger.error("{} parse {}", e, this);
        }
        return a;
        //throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Generate univariate polynomial in a given variable.
     * @param i the index of the variable.
     * @return X_i as univariate polynomial.
     */
    public GenExteriorPolynomial<C> univariate(int i) {
        GenExteriorPolynomial<C> p = getZERO();
        List<IndexList> wgen = ixfac.generators();
        if (0 <= i && i < wgen.size()) {
            C one = coFac.getONE();
            IndexList f = wgen.get(i);
            p = p.sum(one, f);
        }
        return p;
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    public List<GenExteriorPolynomial<C>> univariateList() {
        int n = ixfac.length();
        List<GenExteriorPolynomial<C>> pols = new ArrayList<GenExteriorPolynomial<C>>(n);
        for (int i = 0; i < n; i++) {
            GenExteriorPolynomial<C> p = univariate(i);
            pols.add(p);
        }
        return pols;
    }


    /**
     * Get the generating elements <b>excluding</b> the generators for the
     * coefficient ring.
     * @return a list of generating elements for this ring.
     */
    public List<GenExteriorPolynomial<C>> getGenerators() {
        List<GenExteriorPolynomial<C>> univs = univariateList();
        List<GenExteriorPolynomial<C>> gens = new ArrayList<GenExteriorPolynomial<C>>(univs.size() + 1);
        gens.add(getONE());
        gens.addAll(univs);
        return gens;
    }


    /**
     * Get a list of all generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<GenExteriorPolynomial<C>> generators() {
        List<C> cogens = coFac.generators();
        List<GenExteriorPolynomial<C>> univs = univariateList();
        List<GenExteriorPolynomial<C>> gens = new ArrayList<GenExteriorPolynomial<C>>(
                        univs.size() + cogens.size());
        for (C c : cogens) {
            gens.add(getONE().multiply(c));
        }
        gens.addAll(univs);
        return gens;
    }

}
