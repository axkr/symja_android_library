/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * LocalSolvablePolynomialRing generic recursive solvable polynomial factory
 * implementing RingFactory and extending GenSolvablePolynomialRing factory.
 * Factory for n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. The non-commutative multiplication relations are maintained in
 * a relation table and the non-commutative multiplication relations between the
 * coefficients and the main variables are maintained in a coefficient relation
 * table. Almost immutable object, except variable names and relation table
 * contents.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 * will be deprecated use QLRSolvablePolynomialRing
 */

public class LocalSolvablePolynomialRing<C extends GcdRingElem<C>> extends
                GenSolvablePolynomialRing<SolvableLocal<C>> {


    /*
     * The solvable multiplication relations between variables and coefficients.
     */
    //public final RelationTable<GenPolynomial<C>> coeffTable;


    /**
     * Recursive solvable polynomial ring with polynomial coefficients.
     */
    public final RecSolvablePolynomialRing<C> polCoeff;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final LocalSolvablePolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final LocalSolvablePolynomial<C> ONE;


    private static final Logger logger = LogManager.getLogger(LocalSolvablePolynomialRing.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param rt solvable multiplication relations.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n,
                    RelationTable<SolvableLocal<C>> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n, TermOrder t) {
        this(cf, n, t, null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param rt solvable multiplication relations.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n, TermOrder t,
                    RelationTable<SolvableLocal<C>> rt) {
        this(cf, n, t, null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param v names for the variables.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, String[] v) {
        this(cf, v.length, new TermOrder(), v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param v names for the variables.
     * @param rt solvable multiplication relations.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, int n, TermOrder t, String[] v,
                    RelationTable<SolvableLocal<C>> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        SolvableLocalRing<C> cfring = (SolvableLocalRing<C>) cf; // == coFac
        polCoeff = new RecSolvablePolynomialRing<C>(cfring.ring, n, t, v);
        if (table.size() > 0) { // TODO
            ExpVector e = null;
            ExpVector f = null;
            GenSolvablePolynomial<GenPolynomial<C>> p = null;
            polCoeff.table.update(e, f, p); // from rt
        }
        //coeffTable = polCoeff.coeffTable; //new RelationTable<GenPolynomial<C>>(polCoeff, true);
        ZERO = new LocalSolvablePolynomial<C>(this);
        SolvableLocal<C> coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new LocalSolvablePolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, GenSolvablePolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars(), null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public LocalSolvablePolynomialRing(RingFactory<SolvableLocal<C>> cf, LocalSolvablePolynomialRing o) {
        this(cf, (GenSolvablePolynomialRing) o);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String res = super.toString();
        if (PrettyPrint.isTrue()) {
            //res += "\n" + table.toString(vars);
            res += "\n" + polCoeff.coeffTable.toString(vars);
        } else {
            res += ", #rel = " + table.size() + " + " + polCoeff.coeffTable.size();
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
            s.append("SolvPolyRing.new(");
            break;
        case Python:
        default:
            s.append("SolvPolyRing(");
        }
        if (coFac instanceof RingElem) {
            s.append(((RingElem<SolvableLocal<C>>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(",\"" + varsToString() + "\",");
        String to = tord.toScript();
        s.append(to);
        if (table.size() > 0) {
            String rel = table.toScript();
            s.append(",rel=");
            s.append(rel);
        }
        if (polCoeff.coeffTable.size() > 0) {
            String rel = polCoeff.coeffTable.toScript();
            s.append(",coeffrel=");
            s.append(rel);
        }
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
        if (!(other instanceof LocalSolvablePolynomialRing)) {
            return false;
        }
        LocalSolvablePolynomialRing<C> oring = null;
        try {
            oring = (LocalSolvablePolynomialRing<C>) other;
        } catch (ClassCastException ignored) {
        }
        if (oring == null) {
            return false;
        }
        // do a super.equals( )
        if (!super.equals(other)) {
            return false;
        }
        // check same base relations
        //if ( ! table.equals(oring.table) ) { // done in super
        //    return false;
        //}
        if (!polCoeff.coeffTable.equals(oring.polCoeff.coeffTable)) {
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
        h = super.hashCode();
        h = 37 * h + table.hashCode(); // may be different after some computations
        h = 37 * h + polCoeff.coeffTable.hashCode(); // may be different
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as LocalSolvablePolynomial<C>.
     */
    @Override
    public LocalSolvablePolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as LocalSolvablePolynomial<C>.
     */
    @Override
    public LocalSolvablePolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (polCoeff.coeffTable.size() == 0) {
            return super.isCommutative();
        }
        // check structure of relations?
        return false;
    }


    /**
     * Query if this ring is associative. Test if the relations between the mian
     * variables and the coefficient generators define an associative solvable
     * ring.
     * @return true, if this ring is associative, else false.
     */
    @Override
    public boolean isAssociative() {
        if (!coFac.isAssociative()) {
            return false;
        }
        LocalSolvablePolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<SolvableLocal<C>>> gens = generators();
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (LocalSolvablePolynomial<C>) gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = (LocalSolvablePolynomial<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (LocalSolvablePolynomial<C>) gens.get(k);
                    try {
                        p = Xk.multiply(Xj).multiply(Xi);
                        q = Xk.multiply(Xj.multiply(Xi));
                    } catch (IllegalArgumentException e) {
                        //e.printStackTrace();
                        continue;
                    }
                    if (p.compareTo(q) != 0) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
                            logger.info("p = ( Xk * Xj ) * Xi = " + p);
                            logger.info("q = Xk * ( Xj * Xi ) = " + q);
                            logger.info("q-p = " + p.subtract(q));
                        }
                        return false;
                    }
                }
            }
        }
        return true; //coFac.isAssociative();
    }


    /**
     * Get a (constant) LocalSolvablePolynomial&lt;C&gt; element from a long
     * value.
     * @param a long.
     * @return a LocalSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public LocalSolvablePolynomial<C> fromInteger(long a) {
        return new LocalSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) LocalSolvablePolynomial&lt;C&gt; element from a
     * BigInteger value.
     * @param a BigInteger.
     * @return a LocalSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public LocalSolvablePolynomial<C> fromInteger(BigInteger a) {
        return new LocalSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> random(int n) {
        return random(n, random);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random solvable polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> random(int n, Random rnd) {
        if (nvar == 1) {
            return random(5, n, n, 0.7f, rnd);
        }
        return random(5, n, 3, 0.3f, rnd);
    }


    /**
     * Generate a random solvable polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random solvable polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> random(int k, int l, int d, float q) {
        return random(k, l, d, q, random);
    }


    /**
     * Random solvable polynomial.
     * @param k size of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return a random solvable polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        LocalSolvablePolynomial<C> r = getZERO(); // copy( ZERO ); 
        ExpVector e;
        SolvableLocal<C> a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.random(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (LocalSolvablePolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public LocalSolvablePolynomial<C> copy(LocalSolvablePolynomial<C> c) {
        return new LocalSolvablePolynomial<C>(this, c.getMap());
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param s String.
     * @return LocalSolvablePolynomial from s.
     */
    @Override
    public LocalSolvablePolynomial<C> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param r Reader.
     * @return next LocalSolvablePolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public LocalSolvablePolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        LocalSolvablePolynomial<C> p = null;
        try {
            GenSolvablePolynomial<SolvableLocal<C>> s = pt.nextSolvablePolynomial();
            p = new LocalSolvablePolynomial<C>(this, s);
        } catch (IOException e) {
            logger.error(e.toString() + " parse " + this);
            p = ZERO;
        }
        return p;
    }


    /**
     * Generate univariate solvable polynomial in a given variable.
     * @param i the index of the variable.
     * @return X_i as solvable univariate polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> univariate(int i) {
        return (LocalSolvablePolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> univariate(int i, long e) {
        return (LocalSolvablePolynomial<C>) super.univariate(i, e);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param modv number of module variables.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public LocalSolvablePolynomial<C> univariate(int modv, int i, long e) {
        return (LocalSolvablePolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<LocalSolvablePolynomial<C>> univariateList() {
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<LocalSolvablePolynomial<C>> univariateList(int modv) {
        return univariateList(modv, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables with given
     * exponent.
     * @param modv number of module variables.
     * @param e the exponent of the variables.
     * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
     */
    @Override
    public List<LocalSolvablePolynomial<C>> univariateList(int modv, long e) {
        List<LocalSolvablePolynomial<C>> pols = new ArrayList<LocalSolvablePolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            LocalSolvablePolynomial<C> p = univariate(modv, nm - 1 - i, e);
            pols.add(p);
        }
        return pols;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public LocalSolvablePolynomialRing<C> extend(int i) {
        return extend(i,false);
    }

    
    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @param top true for TOP term order, false for POT term order.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public LocalSolvablePolynomialRing<C> extend(int i, boolean top) {
        GenPolynomialRing<SolvableLocal<C>> pfac = super.extend(i, top);
        LocalSolvablePolynomialRing<C> spfac = new LocalSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.table.extend(this.table);
        spfac.polCoeff.coeffTable.extend(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting
     * variables.
     * @param vn names for extended variables.
     * @return extended polynomial ring factory.
     */
    @Override
    public LocalSolvablePolynomialRing<C> extend(String[] vn) {
        return extend(vn, false);
    }

    
    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting
     * variables.
     * @param vn names for extended variables.
     * @param top true for TOP term order, false for POT term order.
     * @return extended polynomial ring factory.
     */
    @Override
    public LocalSolvablePolynomialRing<C> extend(String[] vn, boolean top) {
        GenPolynomialRing<SolvableLocal<C>> pfac = super.extend(vn, top);
        LocalSolvablePolynomialRing<C> spfac = new LocalSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.table.extend(this.table);
        spfac.polCoeff.coeffTable.extend(this.polCoeff.coeffTable);
        return spfac;
    }

 
    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     * @param i number of variables to remove.
     * @return contracted solvable polynomial ring factory.
     */
    @Override
    public LocalSolvablePolynomialRing<C> contract(int i) {
        GenPolynomialRing<SolvableLocal<C>> pfac = super.contract(i);
        LocalSolvablePolynomialRing<C> spfac = new LocalSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.table.contract(this.table);
        spfac.polCoeff.coeffTable.contract(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public LocalSolvablePolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public LocalSolvablePolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<SolvableLocal<C>> pfac = super.reverse(partial);
        LocalSolvablePolynomialRing<C> spfac = new LocalSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.polCoeff.coeffTable.reverse(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type SolvableLocal<C> coefficients.
     * @param A polynomial with integral polynomial coefficients to be
     *            converted.
     * @return polynomial with type SolvableLocal<C> coefficients.
     */
    public LocalSolvablePolynomial<C> fromPolyCoefficients(GenSolvablePolynomial<GenPolynomial<C>> A) {
        LocalSolvablePolynomial<C> B = getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<SolvableLocal<C>> cfac = coFac;
        SolvableLocalRing<C> qfac = (SolvableLocalRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) y.getValue();
            SolvableLocal<C> p = new SolvableLocal<C>(qfac, a); // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from rational polynomial coefficients. Represent as
     * polynomial with type GenSolvablePolynomial<C> coefficients.
     * @param A polynomial with rational polynomial coefficients to be
     *            converted.
     * @return polynomial with type GenSolvablePolynomial<C> coefficients.
     */
    public RecSolvablePolynomial<C> toPolyCoefficients(LocalSolvablePolynomial<C> A) {
        RecSolvablePolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, SolvableLocal<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            SolvableLocal<C> a = y.getValue();
            if (!a.den.isONE()) {
                throw new IllegalArgumentException("den != 1 not supported: " + a);
            }
            GenPolynomial<C> p = a.num; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from rational polynomial coefficients. Represent as
     * polynomial with type GenSolvablePolynomial<C> coefficients.
     * @param A polynomial with rational polynomial coefficients to be
     *            converted.
     * @return polynomial with type GenSolvablePolynomial<C> coefficients.
     */
    public RecSolvablePolynomial<C> toPolyCoefficients(GenPolynomial<SolvableLocal<C>> A) {
        RecSolvablePolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, SolvableLocal<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            SolvableLocal<C> a = y.getValue();
            if (!a.den.isONE()) {
                throw new IllegalArgumentException("den != 1 not supported: " + a);
            }
            GenPolynomial<C> p = a.num; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }

}
