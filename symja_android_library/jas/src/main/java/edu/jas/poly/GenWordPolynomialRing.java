/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.PreemptStatus;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * GenWordPolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for non-commutative string polynomials over C.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public final class GenWordPolynomialRing<C extends RingElem<C>> implements RingFactory<GenWordPolynomial<C>>
       /*, Iterable<GenWordPolynomial<C>>*/{


    /**
     * The factory for the coefficients.
     */
    public final RingFactory<C> coFac;


    /**
     * The factory for the alphabet.
     */
    public final WordFactory alphabet;


    /**
     * The constant polynomial 0 for this ring.
     */
    public final GenWordPolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring.
     */
    public final GenWordPolynomial<C> ONE;


    /**
     * The constant empty word exponent for this ring.
     */
    public final Word wone;


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
    private static final Logger logger = Logger.getLogger(GenWordPolynomialRing.class);


    /**
     * Flag to enable if preemptive interrrupt is checked.
     */
    final boolean checkPreempt = PreemptStatus.isAllowed();


    /**
     * The constructor creates a polynomial factory object with the default term
     * order.
     * @param cf factory for coefficients of type C.
     * @param wf factory for strings.
     */
    public GenWordPolynomialRing(RingFactory<C> cf, WordFactory wf) {
        coFac = cf;
        alphabet = wf;
        ZERO = new GenWordPolynomial<C>(this);
        C coeff = coFac.getONE();
        wone = wf.getONE();
        ONE = new GenWordPolynomial<C>(this, coeff, wone);
    }


    /**
     * The constructor creates a polynomial factory object. 
     * @param cf factory for coefficients of type C.
     * @param o other polynomial ring.
     */
    public GenWordPolynomialRing(RingFactory<C> cf, GenWordPolynomialRing o) {
        this(cf, o.alphabet);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param fac polynomial ring.
     */
    public GenWordPolynomialRing(GenPolynomialRing fac) {
        this(fac.coFac, new WordFactory(fac.vars));
    }


    /**
     * Copy this factory.
     * @return a clone of this.
     */
    public GenWordPolynomialRing<C> copy() {
        return new GenWordPolynomialRing<C>(coFac, this);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("WordPolyRing(");
        if (coFac instanceof RingElem) {
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toString().trim());
        }
        s.append(",");
        s.append(alphabet.toString());
        s.append(")");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        StringBuffer s = new StringBuffer();
        switch (Scripting.getLang()) {
        case Ruby:
            s.append("WordPolyRing.new(");
            break;
        case Python:
        default:
            s.append("WordPolyRing(");
        }
        if (coFac instanceof RingElem) {
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(",");
        s.append(alphabet.toScript());
        s.append(")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (!(other instanceof GenWordPolynomialRing)) {
            return false;
        }
        GenWordPolynomialRing<C> oring = null;
        try {
            oring = (GenWordPolynomialRing<C>) other;
        } catch (ClassCastException ignored) {
        }
        if (oring == null) {
            return false;
        }
        if (!coFac.equals(oring.coFac)) {
            return false;
        }
        if (!alphabet.equals(oring.alphabet)) {
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
        h += alphabet.hashCode();
        return h;
    }


    /**
     * Get the variable names.
     * @return vars.
     */
    public String getVars() {
        return alphabet.getVal(); // Java-5: Arrays.copyOf(vars,vars.length);
    }


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
     * @return 0 as GenWordPolynomial<C>.
     */
    public GenWordPolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as GenWordPolynomial<C>.
     */
    public GenWordPolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative() && alphabet.isFinite();
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
        return alphabet.isFinite() && coFac.isFinite();
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
        if (coFac.isField() && alphabet.isFinite()) {
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
     * Get a (constant) GenWordPolynomial&lt;C&gt; element from a coefficient
     * value.
     * @param a coefficient.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> valueOf(C a) {
        return new GenWordPolynomial<C>(this, a);
    }


    /**
     * Get a GenWordPolynomial&lt;C&gt; element from a word.
     * @param e word.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> valueOf(Word e) {
        return new GenWordPolynomial<C>(this, coFac.getONE(), e);
    }


    /**
     * Get a GenWordPolynomial&lt;C&gt; element from a coeffcient and a word.
     * @param a coefficient.
     * @param e word.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> valueOf(C a, Word e) {
        return new GenWordPolynomial<C>(this, a, e);
    }


    /**
     * Get a GenWordPolynomial&lt;C&gt; element from a GenPolynomial&lt;C&gt;.
     * @param a GenPolynomial.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> valueOf(GenPolynomial<C> a) {
        if ( a.isZERO() ) {
            return getZERO();
        }
        if ( a.isONE() ) {
            return getONE();
        }
        GenWordPolynomial<C> p = this.getZERO().copy();
        for (Map.Entry<ExpVector, C> m : a.val.entrySet()) {
            C c = m.getValue();
            ExpVector e = m.getKey();
            Word w = alphabet.valueOf(e);
            p.doPutToMap(w,c);
        }
        return p;
    }


    /**
     * Get a list of GenWordPolynomial&lt;C&gt; element from a list of GenPolynomial&lt;C&gt;.
     * @param A GenPolynomial list.
     * @return a GenWordPolynomial&lt;C&gt; list.
     */
    public List<GenWordPolynomial<C>> valueOf(List<GenPolynomial<C>> A) {
        List<GenWordPolynomial<C>> B = new ArrayList<GenWordPolynomial<C>>(A.size());
        if ( A.isEmpty() ) {
            return B;
        }
        for (GenPolynomial<C> a : A) {
            GenWordPolynomial<C> b = valueOf(a);
            B.add(b);
        }
        return B;
    }


    /**
     * Get a (constant) GenWordPolynomial&lt;C&gt; element from a long value.
     * @param a long.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> fromInteger(long a) {
        return new GenWordPolynomial<C>(this, coFac.fromInteger(a), wone);
    }


    /**
     * Get a (constant) GenWordPolynomial&lt;C&gt; element from a BigInteger
     * value.
     * @param a BigInteger.
     * @return a GenWordPolynomial&lt;C&gt;.
     */
    public GenWordPolynomial<C> fromInteger(BigInteger a) {
        return new GenWordPolynomial<C>(this, coFac.fromInteger(a), wone);
    }


    /**
     * Random polynomial. Generates a random polynomial.
     * @param n number of terms.
     * @return a random polynomial.
     */
    public GenWordPolynomial<C> random(int n) {
        return random(n, random);
    }


    /**
     * Random polynomial. Generates a random polynomial with k = 5, l = n, d = 3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenWordPolynomial<C> random(int n, Random rnd) {
        return random(5, n, 3, rnd);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal length of a random word.
     * @return a random polynomial.
     */
    public GenWordPolynomial<C> random(int k, int l, int d) {
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
    public GenWordPolynomial<C> random(int k, int l, int d, Random rnd) {
        GenWordPolynomial<C> r = getZERO(); //.clone() or copy( ZERO ); 
        // add l random coeffs and words of maximal length d
        for (int i = 0; i < l; i++) {
            int di = Math.abs(rnd.nextInt() % d);
            Word e = alphabet.random(di, rnd);
            C a = coFac.random(k, rnd);
            r = r.sum(a, e); // somewhat inefficient but clean
            //System.out.println("e = " + e + " a = " + a);
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c polynomial to copy.
     * @return a copy of c.
     */
    public GenWordPolynomial<C> copy(GenWordPolynomial<C> c) {
        return new GenWordPolynomial<C>(this, c.val);
    }


    /**
     * Parse a polynomial with the use of GenWordPolynomialTokenizer.
     * @param s String.
     * @return GenWordPolynomial from s.
     */
    public GenWordPolynomial<C> parse(String s) {
        String val = s;
        if (!s.contains("|")) {
            val = val.replace("{", "").replace("}", "");
        }
        return parse(new StringReader(val));
    }


    /**
     * Parse a polynomial with the use of GenWordPolynomialTokenizer.
     * @param r Reader.
     * @return next GenWordPolynomial from r.
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial<C> parse(Reader r) {
        logger.error("parse not implemented");
        throw new UnsupportedOperationException("not implemented");
        //         GenWordPolynomialTokenizer pt = new GenWordPolynomialTokenizer(this, r);
        //         GenWordPolynomial<C> p = null;
        //         try {
        //             p = (GenWordPolynomial<C>) pt.nextPolynomial();
        //         } catch (IOException e) {
        //             logger.error(e.toString() + " parse " + this);
        //             p = ZERO;
        //         }
        //         return p;
    }


    /**
     * Generate univariate polynomial in a given variable.
     * @param i the index of the variable.
     * @return X_i as univariate polynomial.
     */
    public GenWordPolynomial<C> univariate(int i) {
        GenWordPolynomial<C> p = getZERO();
        List<Word> wgen = alphabet.generators();
        if (0 <= i && i < wgen.size()) {
            C one = coFac.getONE();
            Word f = wgen.get(i);
            p = p.sum(one, f);
        }
        return p;
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    public List<GenWordPolynomial<C>> univariateList() {
        int n = alphabet.length();
        List<GenWordPolynomial<C>> pols = new ArrayList<GenWordPolynomial<C>>(n);
        for (int i = 0; i < n; i++) {
            GenWordPolynomial<C> p = univariate(i);
            pols.add(p);
        }
        return pols;
    }


    /**
     * Get the generating elements <b>excluding</b> the generators for the coefficient
     * ring.
     * @return a list of generating elements for this ring.
     */
    public List<GenWordPolynomial<C>> getGenerators() {
        List<GenWordPolynomial<C>> univs = univariateList();
        List<GenWordPolynomial<C>> gens = new ArrayList<GenWordPolynomial<C>>(univs.size() + 1);
        gens.add(getONE());
        gens.addAll(univs);
        return gens;
    }


    /**
     * Get a list of all generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<GenWordPolynomial<C>> generators() {
        List<C> cogens = coFac.generators();
        List<GenWordPolynomial<C>> univs = univariateList();
        List<GenWordPolynomial<C>> gens = new ArrayList<GenWordPolynomial<C>>(univs.size() + cogens.size());
        for (C c : cogens) {
            gens.add(getONE().multiply(c));
        }
        gens.addAll(univs);
        return gens;
    }

}
