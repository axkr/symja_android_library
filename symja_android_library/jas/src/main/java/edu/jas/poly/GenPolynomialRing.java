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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.PreemptStatus;
import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;


/**
 * GenPolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for n-variate ordered polynomials over C. Almost immutable object,
 * except variable names.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GenPolynomialRing<C extends RingElem<C>> implements RingFactory<GenPolynomial<C>>,
                Iterable<GenPolynomial<C>> {


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
     * The names of all known variables.
     */
    private static Set<String> knownVars = new HashSet<String>();


    /**
     * The constant polynomial 0 for this ring.
     */
    public final GenPolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring.
     */
    public final GenPolynomial<C> ONE;


    /**
     * The constant exponent vector 0 for this ring.
     */
    public final ExpVector evzero;


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
    private static final Logger logger = Logger.getLogger(GenPolynomialRing.class);


    /**
     * Flag to enable if preemptive interrrupt is checked.
     */
    final boolean checkPreempt = PreemptStatus.isAllowed();


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
        vars = v;
        ZERO = new GenPolynomial<C>(this);
        C coeff = coFac.getONE();
        evzero = ExpVector.create(nvar);
        ONE = new GenPolynomial<C>(this, coeff, evzero);
        if (vars == null) {
            if (PrettyPrint.isTrue()) {
                vars = newVars("x", nvar);
            }
        } else {
            if (vars.length != nvar) {
                throw new IllegalArgumentException("incompatible variable size " + vars.length + ", " + nvar);
            }
            addVars(vars);
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
    @Override
    public String toString() {
        String res = null;
        if (PrettyPrint.isTrue() && coFac != null) {
            String scf = coFac.getClass().getSimpleName();
            if (coFac instanceof AlgebraicNumberRing) {
                AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
                //String[] v = an.ring.vars;
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
            //  + coFac.getClass().getSimpleName();
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
    //JAVA6only: @Override
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
        String to = tord.toString();
        if (tord.getEvord() == TermOrder.INVLEX) {
            to = ",PolyRing.lex";
        }
        if (tord.getEvord() == TermOrder.IGRLEX) {
            to = ",PolyRing.grad";
        }
        s.append(to);
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
        if (!(other instanceof GenPolynomialRing)) {
            return false;
        }
        GenPolynomialRing<C> oring = null;
        try {
            oring = (GenPolynomialRing<C>) other;
        } catch (ClassCastException ignored) {
        }
        if (oring == null) {
            return false;
        }
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
        if (!Arrays.equals(vars, oring.vars)) {
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
        h += tord.hashCode();
        return h;
    }


    /**
     * Get the variable names.
     * @return vars.
     */
    public String[] getVars() {
        return vars; // Java-5: Arrays.copyOf(vars,vars.length);
    }


    /**
     * Set the variable names.
     * @return old vars.
     */
    public String[] setVars(String[] v) {
        if (v.length != nvar) {
            throw new IllegalArgumentException("v not matching number of variables: " + Arrays.toString(v)
                            + ", nvar " + nvar);
        }
        String[] t = vars;
        vars = v; // Java-5: Arrays.copyOf(v,v.length);
        return t;
    }


    /**
     * Get a String representation of the variable names.
     * @return names seperated by commas.
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
    public GenPolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as GenPolynomial<C>.
     */
    public GenPolynomial<C> getONE() {
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
        return new GenPolynomial<C>(this, coFac.getONE(), e);
    }


    /**
     * Get a GenPolynomial&lt;C&gt; element from a coeffcient and an exponent
     * vector.
     * @param a coefficient.
     * @param e exponent vector.
     * @return a GenPolynomial&lt;C&gt;.
     */
    public GenPolynomial<C> valueOf(C a, ExpVector e) {
        return new GenPolynomial<C>(this, a, e);
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
     * (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int n, Random rnd) {
        if (nvar == 1) {
            return random(3, n, n, 0.7f, rnd);
        }
        return random(5, n, 3, 0.3f, rnd);
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
            e = ExpVector.EVRAND(nvar, d, q, rnd);
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
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        GenPolynomial<C> p = null;
        try {
            p = (GenPolynomial<C>) pt.nextPolynomial();
        } catch (IOException e) {
            logger.error(e.toString() + " parse " + this);
            p = ZERO;
        }
        return p;
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
        // add module variable names
        String[] v = newVars("e", i);
        return extend(v);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn).
     * @param vn names for extended variables.
     * @return extended polynomial ring factory.
     */
    public GenPolynomialRing<C> extend(String[] vn) {
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

        TermOrder to = tord.extend(nvar, i);
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
        TermOrder to = tord.extendLower(nvar, i);
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
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial ring factory with reversed variables.
     */
    public GenPolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return polynomial ring factory with reversed variables.
     */
    public GenPolynomialRing<C> reverse(boolean partial) {
        String[] v = null;
        if (vars != null) { // vars are not inversed
            v = new String[vars.length];
            int k = tord.getSplit();
            if (partial && k < vars.length) {
                for (int j = 0; j < k; j++) {
                    v[vars.length - k + j] = vars[vars.length - 1 - j];
                }
                for (int j = 0; j < vars.length - k; j++) {
                    v[j] = vars[j];
                }
            } else {
                for (int j = 0; j < vars.length; j++) {
                    v[j] = vars[vars.length - 1 - j];
                }
            }
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
        synchronized (knownVars) {
            int m = knownVars.size();
            String name = prefix + m;
            for (int i = 0; i < n; i++) {
                while (knownVars.contains(name)) {
                    m++;
                    name = prefix + m;
                }
                vars[i] = name;
                //System.out.println("new variable: " + name);
                knownVars.add(name);
                m++;
                name = prefix + m;
            }
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


    /**
     * Add variable names.
     * @param vars variable names to be recorded.
     */
    public static void addVars(String[] vars) {
        if (vars == null) {
            return;
        }
        synchronized (knownVars) {
            for (int i = 0; i < vars.length; i++) {
                knownVars.add(vars[i]); // eventualy names 'overwritten'
            }
        }
    }


    /**
     * Get a GenPolynomial iterator.
     * @return an iterator over all polynomials.
     */
    public Iterator<GenPolynomial<C>> iterator() {
        if (coFac.isFinite()) {
            return new GenPolynomialIterator<C>(this);
        }
        logger.warn("ring of coefficients " + coFac
                        + " is infinite, constructing iterator only over monomials");
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
            if (coeffiter.size() == 1) { // shorten frist iterator by one element
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
        throw new UnsupportedOperationException("cannnot remove elements");
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

        // Cantor iterator for exponents and coeffcients
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
        throw new UnsupportedOperationException("cannnot remove elements");
    }
}
