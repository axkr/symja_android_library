/*
 * $Id$
 */

package edu.jas.poly;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.PreemptStatus;
import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.StarRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;


/**
 * GenPolynomialRing generic polynomial factory. It implements RingFactory for
 * n-variate ordered polynomials over coefficients C. The variables commute with
 * each other and with the coefficients. For non-commutative coefficients some
 * care is taken to respect the multiplication order.
 *
 * Almost immutable object, except variable names.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GenPolynomialRing<C extends RingElem<C>>
                implements RingFactory<GenPolynomial<C>>, Iterable<GenPolynomial<C>> {


    /**
     * The factory for the coefficients.
     */
    public final RingFactory<C> coFac;


    /**
     * The number of variables.
     */
    public final int nvar;


    /**
     * The term order.
     */
    public final TermOrder tord;


    /**
     * True for partially reversed variables.
     */
    protected boolean partial;


    /**
     * The names of the variables. This value can be modified.
     */
    protected String[] vars;


    /**
     * Counter to distinguish new variables.
     */
    private static AtomicLong varCounter = new AtomicLong(0L);


    /**
     * The constant polynomial 0 for this ring.
     */
    public /*final*/ GenPolynomial<C> ZERO; // volatile not meaningful by DL


    /**
     * The constant polynomial 1 for this ring.
     */
    public /*final*/ GenPolynomial<C> ONE; // volatile not meaningful by DL


    /**
     * The constant exponent vector 0 for this ring.
     */
    public final ExpVector evzero; // volatile not meaningful by DL


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random();


    /**
     * Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /**
     * Log4j logger object.
     */
    private static final Logger logger = LogManager.getLogger(GenPolynomialRing.class);


    /**
     * Count for number of polynomial creations.
     */
    static int creations = 0;


    /**
     * Flag to enable if preemptive interrupt is checked.
     */
    volatile boolean checkPreempt = PreemptStatus.isAllowed();


    /**
     * The constructor creates a polynomial factory object with the default term
     * order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public GenPolynomialRing(RingFactory<C> cf, int n) {
        this(cf, n, new TermOrder(), null);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public GenPolynomialRing(RingFactory<C> cf, int n, TermOrder t) {
        this(cf, n, t, null);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public GenPolynomialRing(RingFactory<C> cf, String[] v) {
        this(cf, v.length, v);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param v names for the variables.
     */
    public GenPolynomialRing(RingFactory<C> cf, int n, String[] v) {
        this(cf, n, new TermOrder(), v);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public GenPolynomialRing(RingFactory<C> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     * @param t a term order.
     */
    public GenPolynomialRing(RingFactory<C> cf, String[] v, TermOrder t) {
        this(cf, v.length, t, v);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param v names for the variables.
     */
    public GenPolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v) {
        coFac = cf;
        nvar = n;
        tord = t;
        partial = false;
        if (v == null) {
            vars = null;
        } else {
            vars = Arrays.copyOf(v, v.length); // > Java-5
        }
        C coeff = coFac.getONE();
        synchronized (this) {
           evzero = ExpVector.create(nvar);
           ZERO = new GenPolynomial<C>(this);
           ONE = new GenPolynomial<C>(this, coeff, evzero);
        }
        //logger.debug("ZERO {} {}", ZERO.toString(), ZERO.val);
        //System.out.println("thread@ZERO: " + Thread.currentThread());
        if (vars == null) {
            if (PrettyPrint.isTrue()) {
                vars = newVars("x", nvar);
            }
        } else {
            if (vars.length != nvar) {
                throw new IllegalArgumentException("incompatible variable size " + vars.length + ", " + nvar);
            }
            // addVars(vars);
        }
    }


    /**
     * The constructor creates a polynomial factory object with the the same
     * term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ.
     * @param cf factory for coefficients of type C.
     * @param o other polynomial ring.
     */
    public GenPolynomialRing(RingFactory<C> cf, GenPolynomialRing o) {
        this(cf, o.nvar, o.tord, o.vars);
    }


    /**
     * The constructor creates a polynomial factory object with the the same
     * coefficient factory, number of variables and variable names as the given
     * polynomial factory, only the term order differs.
     * @param to term order.
     * @param o other polynomial ring.
     */
    public GenPolynomialRing(GenPolynomialRing<C> o, TermOrder to) {
        this(o.coFac, o.nvar, to, o.vars);
    }


    /**
     * Copy this factory.
     * @return a clone of this.
     */
    public GenPolynomialRing<C> copy() {
        return new GenPolynomialRing<C>(coFac, this);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("cast")
    @Override
    public String toString() {
        String res = null;
        if (PrettyPrint.isTrue()) { // wrong: && coFac != null
            String scf = coFac.getClass().getSimpleName();
            if (coFac instanceof AlgebraicNumberRing) {
                AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
                res = "AN[ (" + an.ring.varsToString() + ") (" + an.toString() + ") ]";
            }
            if (coFac instanceof GenPolynomialRing) {
                GenPolynomialRing rf = (GenPolynomialRing) coFac;
                //String[] v = rf.vars;
                //RingFactory cf = rf.coFac;
                //String cs;
                //if (cf instanceof ModIntegerRing) {
                //    cs = cf.toString();
                //} else {
                //    cs = " " + cf.getClass().getSimpleName();
                //}
                //res = "IntFunc" + "{" + cs + "( " + rf.varsToString() + " )" + " } ";
                res = "IntFunc" + "( " + rf.toString() + " )";
            }
            if (((Object) coFac) instanceof ModIntegerRing) {
                ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
                res = "Mod " + mn.getModul() + " ";
            }
            if (res == null) {
                res = coFac.toString();
                if (res.matches("[0-9].*")) {
                    res = scf;
                }
            }
            res += "( " + varsToString() + " ) " + tord.toString() + " ";
        } else {
            res = this.getClass().getSimpleName() + "[ " + coFac.toString() + " ";
            if (coFac instanceof AlgebraicNumberRing) {
                AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
                res = "AN[ (" + an.ring.varsToString() + ") (" + an.modul + ") ]";
            }
            if (coFac instanceof GenPolynomialRing) {
                GenPolynomialRing rf = (GenPolynomialRing) coFac;
                //String[] v = rf.vars;
                //RingFactory cf = rf.coFac;
                //String cs;
                //if (cf instanceof ModIntegerRing) {
                //    cs = cf.toString();
                //} else {
                //    cs = " " + cf.getClass().getSimpleName();
                //}
                //res = "IntFunc{ " + cs + "( " + rf.varsToString() + " )" + " } ";
                res = "IntFunc" + "( " + rf.toString() + " )";
            }
            if (((Object) coFac) instanceof ModIntegerRing) {
                ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
                res = "Mod " + mn.getModul() + " ";
            }
            //res += ", " + nvar + ", " + tord.toString() + ", " + varsToString() + ", " + partial + " ]";
            res += "( " + varsToString() + " ) " + tord.toString() + " ]";
        }
        return res;
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
            s.append("PolyRing.new(");
            break;
        case Python:
        default:
            s.append("PolyRing(");
        }
        if (coFac instanceof RingElem) {
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(",\"" + varsToString() + "\"");
        String to = tord.toScript();
        s.append("," + to);
        s.append(")");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation of an ExpVector of this
     * ring.
     * @param e exponent vector
     * @return script compatible representation for the ExpVector.
     */
    public String toScript(ExpVector e) {
        if (e == null) {
            return "null";
        }
        if (vars != null) {
            return e.toScript(vars);
        }
        return e.toScript();
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
        if (!(other instanceof GenPolynomialRing)) {
            return false;
        }
        GenPolynomialRing<C> oring = (GenPolynomialRing<C>) other;
        if (nvar != oring.nvar) {
            return false;
        }
        if (!coFac.equals(oring.coFac)) {
            return false;
        }
        if (!tord.equals(oring.tord)) {
            return false;
        }
        // same variables required ?
        if (!Arrays.deepEquals(vars, oring.vars)) {
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
        h = (nvar << 27);
        h += (coFac.hashCode() << 11);
        h += (tord.hashCode() << 9);
        h += Arrays.hashCode(vars);
        //System.out.println("GenPolynomialRing.hashCode: " + h);
        return h;
    }


    /**
     * Get the number of polynomial creations.
     * @return creations.
     */
    public int getCreations() {
        return creations;
    }


    /**
     * Get the variable names.
     * @return vars.
     */
    public String[] getVars() {
        return Arrays.copyOf(vars, vars.length); // > Java-5
    }


    /**
     * Set the variable names.
     * @return old vars.
     */
    public String[] setVars(String[] v) {
        if (v.length != nvar) {
            throw new IllegalArgumentException(
                            "v not matching number of variables: " + Arrays.toString(v) + ", nvar " + nvar);
        }
        String[] t = vars;
        vars = Arrays.copyOf(v, v.length); // > Java-5 
        return t;
    }


    /**
     * Get a String representation of the variable names.
     * @return names separated by commas.
     */
    public String varsToString() {
        if (vars == null) {
            return "#" + nvar;
        }
        //return Arrays.toString(vars);
        return ExpVector.varsToString(vars);
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
     * @return 0 as GenPolynomial<C>.
     */
    public synchronized GenPolynomial<C> getZERO() {
        if (ZERO == null || !ZERO.isZERO()) { // happened since May 5 2022
            // Name        : java-11-openjdk-headless, java-17-openjdk-headless
            // Version     : 11.0.15.0, 17.0.4
            // Release     : 150000.3.80.1, 150400.3.3.1
            GenPolynomial<C> x = ZERO;
            ZERO = new GenPolynomial<C>(this);
            logger.info("warn: ZERO@get |{}| wrong fix to {}", x, ZERO);
        }
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as GenPolynomial<C>.
     */
    public synchronized GenPolynomial<C> getONE() {
        if (ONE == null || !ONE.isONE()) {
           ONE = new GenPolynomial<C>(this, coFac.getONE(), evzero);
           logger.info("warn: ONE@get {}", ONE);
        }
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
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
        if (isField > 0) {
            return true;
        }
        if (isField == 0) {
            return false;
        }
        if (coFac.isField() && nvar == 0) {
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
     * Get a (constant) GenPolynomial&lt;C&gt; element from a coefficient value.
     * @param a coefficient.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> valueOf(C a) {
        return new GenPolynomial<C>(this, a);
    }


    /**
     * Get a GenPolynomial&lt;C&gt; element from an exponent vector.
     * @param e exponent vector.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> valueOf(ExpVector e) {
        if (e == null) {
            return getZERO();
        }
        return new GenPolynomial<C>(this, coFac.getONE(), e);
    }


    /**
     * Get a GenPolynomial&lt;C&gt; element from a list of exponent vectors.
     * @param E list of exponent vector.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public List<GenPolynomial<C>> valueOf(Iterable<ExpVector> E) {
        if (E == null) {
            return null;
        }
        List<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>(); //E.size());
        for (ExpVector e : E) {
            GenPolynomial<C> p = valueOf(e);
            P.add(p);
        }
        return P;
    }


    /**
     * Get a GenPolynomial&lt;C&gt; element from a coefficient and an exponent
     * vector.
     * @param a coefficient.
     * @param e exponent vector.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> valueOf(C a, ExpVector e) {
        return new GenPolynomial<C>(this, a, e);
    }


    /**
     * Get a GenPolynomial&lt;C&gt; element from a monomial.
     * @param m monomial.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> valueOf(Monomial<C> m) {
        return new GenPolynomial<C>(this, m.c, m.e);
    }


    /**
     * Get a (constant) GenPolynomial&lt;C&gt; element from a long value.
     * @param a long.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> fromInteger(long a) {
        return new GenPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) GenPolynomial&lt;C&gt; element from a BigInteger value.
     * @param a BigInteger.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> fromInteger(BigInteger a) {
        return new GenPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a GenPolynomial&lt;C&gt; from a GenVector&lt;C&gt;.
     * @param a GenVector&lt;C&gt;.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> fromVector(GenVector<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        if (nvar != 1) {
            throw new IllegalArgumentException("no univariate polynomial ring");
        }
        GenPolynomial<C> ret = copy(ZERO);
        SortedMap<ExpVector, C> tm = ret.val;
        long i = -1;
        for (C m : a.val) {
            i++;
            if (m.isZERO()) {
                continue;
            }
            ExpVector e = ExpVector.create(1, 0, i);
            tm.put(e, m);
        }
        return ret;
    }


    /**
     * Random polynomial. Generates a random polynomial with k = 5, l = n, d =
     * (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int n) {
        return random(n, random);
    }


    /**
     * Random polynomial. Generates a random polynomial with k = 5, l = n, d =
     * n, q = (nvar == 1) ? 0.5 : 0.3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int n, Random rnd) {
        if (nvar == 1) {
            return random(3, n, n, 0.5f, rnd);
        }
        return random(3, n, n, 0.3f, rnd);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int k, int l, int d, float q) {
        return random(k, l, d, q, random);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        GenPolynomial<C> r = getZERO(); //.clone() or copy( ZERO ); 
        ExpVector e;
        C a;
        // add l random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.random(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = r.sum(a, e); // somewhat inefficient but clean
            //System.out.println("e = " + e + " a = " + a);
        }
        // System.out.println("r = " + r);
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public GenPolynomial<C> copy(GenPolynomial<C> c) {
        //System.out.println("GP copy = " + this);
        return new GenPolynomial<C>(this, c.val);
    }


    /**
     * Copy polynomial list.
     * @param L polynomial list
     * @return a copy of L in this ring.
     */
    public List<GenPolynomial<C>> copy(List<GenPolynomial<C>> L) {
        if (L == null) {
            return L;
        }
        List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(L.size());
        for (GenPolynomial<C> a : L) {
            R.add(copy(a));
        }
        return R;
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer.
     * @param s String.
     * @return GenPolynomial from s.
     */
    public GenPolynomial<C> parse(String s) {
        String val = s;
        if (!s.contains("|")) {
            val = val.replace("{", "").replace("}", "");
        }
        return parse(new StringReader(val));
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer.
     * @param r Reader.
     * @return next GenPolynomial from r.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public GenPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        GenPolynomial<C> p = null;
        try {
            p = (GenPolynomial<C>) pt.nextPolynomial();
        } catch (IOException e) {
            logger.error("{} parse {}", e, this);
            p = ZERO;
        }
        return p;
    }


    /**
     * Generate univariate polynomial in a given variable with given exponent.
     * @param x the name of a variable.
     * @return x as univariate polynomial.
     */
    public GenPolynomial<C> univariate(String x) {
        return univariate(x, 1L);
    }


    /**
     * Generate univariate polynomial in a given variable with given exponent.
     * @param x the name of the variable.
     * @param e the exponent of the variable.
     * @return x^e as univariate polynomial.
     */
    public GenPolynomial<C> univariate(String x, long e) {
        if (vars == null) { // should not happen
            throw new IllegalArgumentException("no variables defined for polynomial ring");
        }
        if (x == null || x.isEmpty()) {
            throw new IllegalArgumentException("no variable name given");
        }
        int i;
        for (i = 0; i < vars.length; i++) {
            if (x.equals(vars[i])) { // use HashMap or TreeMap
                break;
            }
        }
        if (i >= vars.length) {
            throw new IllegalArgumentException("variable '" + x + "' not defined in polynomial ring");
        }
        return univariate(0, nvar - i - 1, e);
    }


    /**
     * Generate univariate polynomial in a given variable.
     * @param i the index of the variable.
     * @return X_i as univariate polynomial.
     */
    public GenPolynomial<C> univariate(int i) {
        return univariate(0, i, 1L);
    }


    /**
     * Generate univariate polynomial in a given variable with given exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as univariate polynomial.
     */
    public GenPolynomial<C> univariate(int i, long e) {
        return univariate(0, i, e);
    }


    /**
     * Generate univariate polynomial in a given variable with given exponent.
     * @param modv number of module variables.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as univariate polynomial.
     */
    public GenPolynomial<C> univariate(int modv, int i, long e) {
        GenPolynomial<C> p = getZERO();
        int r = nvar - modv;
        if (0 <= i && i < r) {
            C one = coFac.getONE();
            ExpVector f = ExpVector.create(r, i, e);
            if (modv > 0) {
                f = f.extend(modv, 0, 0l);
            }
            p = p.sum(one, f);
        }
        return p;
    }


    /**
     * Get the generating elements excluding the generators for the coefficient
     * ring.
     * @return a list of generating elements for this ring.
     */
    public List<GenPolynomial<C>> getGenerators() {
        List<? extends GenPolynomial<C>> univs = univariateList();
        List<GenPolynomial<C>> gens = new ArrayList<GenPolynomial<C>>(univs.size() + 1);
        gens.add(getONE());
        gens.addAll(univs);
        return gens;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<GenPolynomial<C>> generators() {
        List<? extends C> cogens = coFac.generators();
        List<? extends GenPolynomial<C>> univs = univariateList();
        List<GenPolynomial<C>> gens = new ArrayList<GenPolynomial<C>>(univs.size() + cogens.size());
        for (C c : cogens) {
            gens.add(getONE().multiply(c));
        }
        gens.addAll(univs);
        return gens;
    }


    /**
     * Get a list of the generating elements excluding the module variables.
     * @param modv number of module variables
     * @return list of generators for the polynomial ring.
     */
    public List<GenPolynomial<C>> generators(int modv) {
        List<? extends C> cogens = coFac.generators();
        List<? extends GenPolynomial<C>> univs = univariateList(modv);
        List<GenPolynomial<C>> gens = new ArrayList<GenPolynomial<C>>(univs.size() + cogens.size());
        for (C c : cogens) {
            gens.add(getONE().multiply(c));
        }
        gens.addAll(univs);
        return gens;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return (nvar == 0) && coFac.isFinite();
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    public List<? extends GenPolynomial<C>> univariateList() {
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    public List<? extends GenPolynomial<C>> univariateList(int modv) {
        return univariateList(modv, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables with given
     * exponent.
     * @param modv number of module variables.
     * @param e the exponent of the variables.
     * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
     */
    public List<? extends GenPolynomial<C>> univariateList(int modv, long e) {
        List<GenPolynomial<C>> pols = new ArrayList<GenPolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            GenPolynomial<C> p = univariate(modv, nm - 1 - i, e);
            pols.add(p);
        }
        return pols;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extend(int i) {
        return extend(i, false);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @param top true for TOP term order, false for POT term order.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extend(int i, boolean top) {
        // add module variable names
        String[] v = newVars("e", i);
        return extend(v, top);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn).
     * @param vn names for extended variables.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extend(String[] vn) {
        return extend(vn, false);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn).
     * @param vn names for extended variables.
     * @param top true for TOP term order, false for POT term order.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extend(String[] vn, boolean top) {
        if (vn == null || vars == null) {
            throw new IllegalArgumentException("vn and vars may not be null");
        }
        int i = vn.length;
        String[] v = new String[vars.length + i];
        for (int k = 0; k < vars.length; k++) {
            v[k] = vars[k];
        }
        for (int k = 0; k < vn.length; k++) {
            v[vars.length + k] = vn[k];
        }
        TermOrder to = tord.extend(nvar, i, top);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(coFac, nvar + i, to, v);
        return pfac;
    }


    /**
     * Extend lower variables. Extend number of variables by i.
     * @param i number of variables to extend.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extendLower(int i) {
        String[] v = newVars("e", i);
        return extendLower(v);
    }


    /**
     * Extend lower variables. Extend number of variables by length(vn).
     * @param vn names for extended lower variables.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extendLower(String[] vn) {
        return extendLower(vn, false);
    }


    /**
     * Extend lower variables. Extend number of variables by length(vn).
     * @param vn names for extended lower variables.
     * @param top true for TOP term order, false for POT term order.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extendLower(String[] vn, boolean top) {
        if (vn == null || vars == null) {
            throw new IllegalArgumentException("vn and vars may not be null");
        }
        int i = vn.length;
        String[] v = new String[vars.length + i];
        for (int k = 0; k < vn.length; k++) {
            v[k] = vn[k];
        }
        for (int k = 0; k < vars.length; k++) {
            v[vn.length + k] = vars[k];
        }
        TermOrder to = tord.extendLower(nvar, i, top);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(coFac, nvar + i, to, v);
        return pfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     * @param i number of variables to remove.
     * @return contracted polynomial ring factory.
     */
    public GenPolynomialRing<C> contract(int i) {
        String[] v = null;
        if (vars != null) {
            v = new String[vars.length - i];
            for (int j = 0; j < vars.length - i; j++) {
                v[j] = vars[j];
            }
        }
        TermOrder to = tord.contract(i, nvar - i);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(coFac, nvar - i, to, v);
        return pfac;
    }


    /**
     * Recursive representation as polynomial with i main variables.
     * @param i number of main variables.
     * @return recursive polynomial ring factory.
     */
    public GenPolynomialRing<GenPolynomial<C>> recursive(int i) {
        if (i <= 0 || i >= nvar) {
            throw new IllegalArgumentException("wrong: 0 < " + i + " < " + nvar);
        }
        GenPolynomialRing<C> cfac = contract(i);
        String[] v = null;
        if (vars != null) {
            v = new String[i];
            int k = 0;
            for (int j = nvar - i; j < nvar; j++) {
                v[k++] = vars[j];
            }
        }
        TermOrder to = tord.contract(0, i); // ??
        GenPolynomialRing<GenPolynomial<C>> pfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, i, to, v);
        return pfac;
    }


    /**
     * Distributive representation as polynomial with all main variables.
     * @return distributive polynomial ring factory.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialRing<C> distribute() {
        if (!(coFac instanceof GenPolynomialRing)) {
            return this;
        }
        RingFactory cf = coFac;
        RingFactory<GenPolynomial<C>> cfp = (RingFactory<GenPolynomial<C>>) cf;
        GenPolynomialRing cr = (GenPolynomialRing) cfp;
        GenPolynomialRing<C> pfac;
        if (cr.vars != null) {
            pfac = extend(cr.vars);
        } else {
            pfac = extend(cr.nvar);
        }
        return pfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial ring factory with reversed variables.
     */
    public GenPolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings. The coefficient
     * ring must be commuative.
     * @param partial true for partially reversed term orders.
     * @return polynomial ring factory with reversed variables.
     */
    public GenPolynomialRing<C> reverse(boolean partial) {
        if (!coFac.isCommutative() && !(coFac.getONE() instanceof StarRingElem)) {
            //throw new IllegalArgumentException("reverse coefficients must be commutative or StarRing: " + coFac);
            logger.warn("reverse coefficients should be commutative or StarRing elements: " + coFac);
        }
        String[] v = null;
        if (vars != null) { // vars are not inversed
            v = new String[vars.length];
            int k = tord.getSplit();
            if (partial && k < vars.length) {
                // copy upper
                for (int j = 0; j < k; j++) {
                    //v[vars.length - k + j] = vars[vars.length - 1 - j]; // reverse upper
                    v[vars.length - k + j] = vars[vars.length - k + j];
                }
                // reverse lower
                for (int j = 0; j < vars.length - k; j++) {
                    //v[j] = vars[j]; // copy upper
                    v[j] = vars[vars.length - k - j - 1];
                }
            } else {
                for (int j = 0; j < vars.length; j++) {
                    v[j] = vars[vars.length - 1 - j];
                }
            }
            //System.out.println("vars = " + Arrays.toString(vars));
            //System.out.println("v    = " + Arrays.toString(v));
        }
        TermOrder to = tord.reverse(partial);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(coFac, nvar, to, v);
        pfac.partial = partial;
        return pfac;
    }


    /**
     * Get PolynomialComparator.
     * @return polynomial comparator.
     */
    public PolynomialComparator<C> getComparator() {
        return new PolynomialComparator<C>(tord, false);
    }


    /**
     * Get PolynomialComparator.
     * @param rev for reverse comparator.
     * @return polynomial comparator.
     */
    public PolynomialComparator<C> getComparator(boolean rev) {
        return new PolynomialComparator<C>(tord, rev);
    }


    /**
     * New variable names. Generate new names for variables,
     * @param prefix name prefix.
     * @param n number of variables.
     * @return new variable names.
     */
    public static String[] newVars(String prefix, int n) {
        String[] vars = new String[n];
        for (int i = 0; i < n; i++) {
            long m = varCounter.getAndIncrement();
            vars[i] = prefix + m;
        }
        return vars;
    }


    /**
     * New variable names. Generate new names for variables,
     * @param prefix name prefix.
     * @return new variable names.
     */
    public String[] newVars(String prefix) {
        return newVars(prefix, nvar);
    }


    /**
     * New variable names. Generate new names for variables,
     * @param n number of variables.
     * @return new variable names.
     */
    public static String[] newVars(int n) {
        return newVars("x", n);
    }


    /**
     * New variable names. Generate new names for variables,
     * @return new variable names.
     */
    public String[] newVars() {
        return newVars(nvar);
    }


    /*
     * Add variable names.
     * @param vars variable names to be recorded.
    public static void addVars(String[] vars) {
        if (vars == null) {
            return;
        }
        // synchronized (knownVars) {
        //   for (int i = 0; i < vars.length; i++) {
        //      knownVars.add(vars[i]); // eventually names 'overwritten'
        //   }
        // }
    }
     */


    /**
     * Permute variable names.
     * @param vars variable names.
     * @param P permutation.
     * @return P(vars).
     */
    public static String[] permuteVars(List<Integer> P, String[] vars) {
        if (vars == null || vars.length <= 1) {
            return vars;
        }
        String[] b = new String[vars.length];
        int j = 0;
        for (Integer i : P) {
            b[j++] = vars[i];
        }
        return b;
    }


    /**
     * Permutation of polynomial ring variables.
     * @param P permutation.
     * @return P(this).
     */
    public GenPolynomialRing<C> permutation(List<Integer> P) {
        if (nvar <= 1) {
            return this;
        }
        TermOrder tp = tord.permutation(P);
        if (vars == null) {
            return new GenPolynomialRing<C>(coFac, nvar, tp);
        }
        String[] v1 = new String[vars.length];
        for (int i = 0; i < v1.length; i++) {
            v1[i] = vars[v1.length - 1 - i];
        }
        String[] vp = permuteVars(P, v1);
        String[] v2 = new String[vp.length];
        for (int i = 0; i < vp.length; i++) {
            v2[i] = vp[vp.length - 1 - i];
        }
        return new GenPolynomialRing<C>(coFac, nvar, tp, v2);
    }


    /**
     * Characteristic polynomial of matrix.
     * <b>Note:</b> using Faddeev–LeVerrier algorithm
     * @see https://en.wikipedia.org/wiki/Faddeev%E2%80%93LeVerrier_algorithm
     * @param A a square matrix.
     * @return characteristic polynomial of A.
     */
    public GenPolynomial<C> charPolynomial(GenMatrix<C> A) {
        if (A == null || A.isZERO()) {
            return ZERO;
        }
        if (nvar != 1) {
            throw new IllegalArgumentException("no univariate polynomial ring");
        }
        GenMatrixRing<C> mfac = A.ring;
        int n = mfac.rows;
        java.math.BigInteger c0 = coFac.characteristic();
        if (c0.signum() > 0 && c0.compareTo(java.math.BigInteger.valueOf(n)) <= 0) {
            throw new UnsupportedOperationException("characteristic <= n: " + c0 + " <= " + n);
        }
        GenPolynomial<C> ret = copy(ZERO);
        //SortedMap<ExpVector, C> tm = ret.val;
        GenMatrix<C> M = mfac.getZERO(); //new GenMatrix<C>(A.ring);
        GenMatrix<C> I = mfac.getONE();
        ExpVector e = ExpVector.create(1, 0, n);
        C one = coFac.getONE();
        ret = ret.sum(one, e);
        C c = coFac.getONE();
        GenMatrix<C> Ms = null;
        GenMatrix<C> Mp = null, Mc;
        // M_0 = 0, c_n = 1
        // k = 1..n: M_k = A*M_{k-1} + c_{n-k+1}*I, c_{n-k} = -1/k*trace(A*M_k)
        for (int k = 1; k <= n; k++) {
	    if (Ms == null) {
                Mp = A.multiply(M); // reuse A*Mp? todo
            } else {
                Mp = Ms;
            }
            Mc = I.multiply(c);
	    Mp = Mp.sum(Mc);
            Ms = A.multiply(Mp);
            C cp = Ms.trace();
            C ki = coFac.fromInteger(k).inverse(); // characteristic != k
            cp = cp.multiply(ki).negate();
            M = Mp;
            c = cp;
            e = ExpVector.create(1, 0, n-k);
            ret = ret.sum(c, e);
            //System.out.println("k = " + k + ", c = " + c + ", M = " + M);
        }
        // only for demonstrating how to get the determinant, trace and inverse:
        // C det = coFac.getZERO(); //ret.trailingBaseCoefficient();
        // //System.out.println("n = " + n + ", deg = " + ret.degree());
        // if (n % 2 != 0) {
        //     det = det.negate();
        // }
        // if (! det.isZERO()) {
        //     C d = det.inverse();
        //     if ((n-1) % 2 != 0) {
        //         d = d.negate();
        //     }
        //     Mc = Mp.multiply(d);
        // } else {
        //     Mc = null;
        // }
        // //System.out.println("det = " + det + ", trace = " + c + ", A^{-1} = " + Mc);
        return ret;
    }


    /**
     * Determinant of matrix from characteristic polynomial.
     * <b>Note:</b> using Faddeev–LeVerrier algorithm
     * @see https://en.wikipedia.org/wiki/Faddeev%E2%80%93LeVerrier_algorithm
     * @param P characteristic polynomial of a matrix.
     * @return determinant from characteristic polynomial.
     */
    public C determinantFromCharPol(GenPolynomial<C> P) {
        C det = coFac.getZERO();
        if (P == null || P.isZERO()) {
            return det;
        }
        det = P.trailingBaseCoefficient();
        if (P.degree() % 2 != 0) {
            det = det.negate();
        }
        return det;
    }


    /**
     * Determinant of matrix via characteristic polynomial.
     * <b>Note:</b> using Faddeev–LeVerrier algorithm
     * @see https://en.wikipedia.org/wiki/Faddeev%E2%80%93LeVerrier_algorithm
     * @param A square matrix.
     * @return determinant of A from characteristic polynomial of A.
     */
    public C determinant(GenMatrix<C> A) {
        GenPolynomial<C> P = charPolynomial(A);
        return determinantFromCharPol(P);
    }


    /**
     * Trace of matrix from characteristic polynomial.
     * @param P characteristic polynomial of a matrix.
     * @return trace from characteristic polynomial.
     */
    public C traceFromCharPol(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return coFac.getZERO();
        }
        long n = P.degree();
        ExpVector e = ExpVector.create(1, 0, n-1);
        C t = P.coefficient(e).negate();
        return t;
    }


    /**
     * Get a GenPolynomial iterator.
     * @return an iterator over all polynomials.
     */
    public Iterator<GenPolynomial<C>> iterator() {
        if (coFac.isFinite()) {
            return new GenPolynomialIterator<C>(this);
        }
        logger.warn("ring of coefficients {} is infinite, constructing iterator only over monomials", coFac);
        return new GenPolynomialMonomialIterator<C>(this);
        //throw new IllegalArgumentException("only for finite iterable coefficients implemented");
    }

}


/**
 * Polynomial iterator.
 * @author Heinz Kredel
 */
class GenPolynomialIterator<C extends RingElem<C>> implements Iterator<GenPolynomial<C>> {


    /**
     * data structure.
     */
    final GenPolynomialRing<C> ring;


    final Iterator<List<Long>> eviter;


    final List<ExpVector> powers;


    final List<Iterable<C>> coeffiter;


    Iterator<List<C>> itercoeff;


    GenPolynomial<C> current;


    /**
     * Polynomial iterator constructor.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialIterator(GenPolynomialRing<C> fac) {
        ring = fac;
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();
        List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
        for (int i = 0; i < ring.nvar; i++) {
            tlist.add(li);
        }
        CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
        eviter = ei.iterator();
        RingFactory<C> cf = ring.coFac;
        coeffiter = new ArrayList<Iterable<C>>();
        if (cf instanceof Iterable && cf.isFinite()) {
            Iterable<C> cfi = (Iterable<C>) cf;
            coeffiter.add(cfi);
        } else {
            throw new IllegalArgumentException("only for finite iterable coefficients implemented");
        }
        CartesianProduct<C> tuples = new CartesianProduct<C>(coeffiter);
        itercoeff = tuples.iterator();
        powers = new ArrayList<ExpVector>();
        ExpVector e = ExpVector.create(eviter.next());
        powers.add(e);
        //System.out.println("new e = " + e);
        //System.out.println("powers = " + powers);
        List<C> c = itercoeff.next();
        //System.out.println("coeffs = " + c);
        current = new GenPolynomial<C>(ring, c.get(0), e);
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    public boolean hasNext() {
        return true;
    }


    /**
     * Get next polynomial.
     * @return next polynomial.
     */
    public synchronized GenPolynomial<C> next() {
        GenPolynomial<C> res = current;
        if (!itercoeff.hasNext()) {
            ExpVector e = ExpVector.create(eviter.next());
            powers.add(0, e); // add new ev at beginning
            //System.out.println("new e = " + e);
            //System.out.println("powers = " + powers);
            if (coeffiter.size() == 1) { // shorten first iterator by one element
                coeffiter.add(coeffiter.get(0));
                Iterable<C> it = coeffiter.get(0);
                List<C> elms = new ArrayList<C>();
                for (C elm : it) {
                    elms.add(elm);
                }
                elms.remove(0);
                coeffiter.set(0, elms);
            } else {
                coeffiter.add(coeffiter.get(1));
            }
            CartesianProduct<C> tuples = new CartesianProduct<C>(coeffiter);
            itercoeff = tuples.iterator();
        }
        List<C> coeffs = itercoeff.next();
        //      while ( coeffs.get(0).isZERO() ) {
        //             System.out.println(" skip zero ");
        //          coeffs = itercoeff.next(); // skip tuples with zero in first component
        //      }
        //System.out.println("coeffs = " + coeffs);
        GenPolynomial<C> pol = ring.getZERO().copy();
        int i = 0;
        for (ExpVector f : powers) {
            C c = coeffs.get(i++);
            if (c.isZERO()) {
                continue;
            }
            if (pol.val.get(f) != null) {
                System.out.println("error f in pol = " + f + ", " + pol.getMap().get(f));
                throw new RuntimeException("error in iterator");
            }
            pol.doPutToMap(f, c);
        }
        current = pol;
        return res;
    }


    /**
     * Remove an element if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannot remove elements");
    }
}


/**
 * Polynomial monomial iterator.
 * @author Heinz Kredel
 */
class GenPolynomialMonomialIterator<C extends RingElem<C>> implements Iterator<GenPolynomial<C>> {


    /**
     * data structure.
     */
    final GenPolynomialRing<C> ring;


    final Iterator<List> iter;


    GenPolynomial<C> current;


    /**
     * Polynomial iterator constructor.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialMonomialIterator(GenPolynomialRing<C> fac) {
        ring = fac;
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();
        List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
        for (int i = 0; i < ring.nvar; i++) {
            tlist.add(li);
        }
        CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
        //Iterator<List<Long>> eviter = ei.iterator();

        RingFactory<C> cf = ring.coFac;
        Iterable<C> coeffiter;
        if (cf instanceof Iterable && !cf.isFinite()) {
            Iterable<C> cfi = (Iterable<C>) cf;
            coeffiter = cfi;
        } else {
            throw new IllegalArgumentException("only for infinite iterable coefficients implemented");
        }

        // Cantor iterator for exponents and coefficients
        List<Iterable> eci = new ArrayList<Iterable>(2); // no type parameter
        eci.add(ei);
        eci.add(coeffiter);
        CartesianProductInfinite ecp = new CartesianProductInfinite(eci);
        iter = ecp.iterator();

        List ec = iter.next();
        List<Long> ecl = (List<Long>) ec.get(0);
        C c = (C) ec.get(1); // zero
        ExpVector e = ExpVector.create(ecl);
        //System.out.println("exp    = " + e);
        //System.out.println("coeffs = " + c);
        current = new GenPolynomial<C>(ring, c, e);
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    public boolean hasNext() {
        return true;
    }


    /**
     * Get next polynomial.
     * @return next polynomial.
     */
    @SuppressWarnings("unchecked")
    public synchronized GenPolynomial<C> next() {
        GenPolynomial<C> res = current;

        List ec = iter.next();
        C c = (C) ec.get(1);
        while (c.isZERO()) { // zero already done in first next
            ec = iter.next();
            c = (C) ec.get(1);
        }
        List<Long> ecl = (List<Long>) ec.get(0);
        ExpVector e = ExpVector.create(ecl);
        //System.out.println("exp    = " + e);
        //System.out.println("coeffs = " + c);
        current = new GenPolynomial<C>(ring, c, e);

        return res;
    }


    /**
     * Remove an element if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannot remove elements");
    }
}
