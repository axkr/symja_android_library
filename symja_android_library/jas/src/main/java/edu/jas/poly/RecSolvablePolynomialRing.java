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
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * RecSolvablePolynomialRing generic recursive solvable polynomial factory
 * implementing RingFactory and extending GenSolvablePolynomialRing factory.
 * Factory for n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. The non-commutative multiplication relations are maintained in
 * a relation table and the non-commutative multiplication relations between the
 * coefficients and the main variables are maintained in a coefficient relation
 * table. Almost immutable object, except variable names and relation table
 * contents.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public class RecSolvablePolynomialRing<C extends RingElem<C>> extends
                GenSolvablePolynomialRing<GenPolynomial<C>> {


    /**
     * The solvable multiplication relations between variables and coefficients.
     */
    public final RelationTable<GenPolynomial<C>> coeffTable;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final RecSolvablePolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final RecSolvablePolynomial<C> ONE;


    private static final Logger logger = Logger.getLogger(RecSolvablePolynomialRing.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param rt solvable multiplication relations.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n,
                    RelationTable<GenPolynomial<C>> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n, TermOrder t) {
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
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n, TermOrder t,
                    RelationTable<GenPolynomial<C>> rt) {
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
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, String[] v) {
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
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, int n, TermOrder t, String[] v,
                    RelationTable<GenPolynomial<C>> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        coeffTable = new RelationTable<GenPolynomial<C>>(this, true);
        ZERO = new RecSolvablePolynomial<C>(this);
        GenPolynomial<C> coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new RecSolvablePolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public RecSolvablePolynomialRing(RingFactory<GenPolynomial<C>> cf, RecSolvablePolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars(), null);
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
            res += "\n" + coeffTable.toString(vars);
        } else {
            res += ", #rel = " + table.size() + " + " + coeffTable.size();
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
            s.append(((RingElem<GenPolynomial<C>>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(",\"" + varsToString() + "\",");
        String to = tord.toString();
        if (tord.getEvord() == TermOrder.INVLEX) {
            to = "PolyRing.lex";
        }
        if (tord.getEvord() == TermOrder.IGRLEX) {
            to = "PolyRing.grad";
        }
        s.append(to);
        if (table.size() > 0) {
            String rel = table.toScript();
            s.append(",rel=");
            s.append(rel);
        }
        if (coeffTable.size() > 0) {
            String rel = coeffTable.toScript();
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
        if (!(other instanceof RecSolvablePolynomialRing)) {
            return false;
        }
        // do a super.equals( )
        if (!super.equals(other)) {
            return false;
        }
        RecSolvablePolynomialRing<C> oring = null;
        try {
            oring = (RecSolvablePolynomialRing<C>) other;
        } catch (ClassCastException ignored) {
        }
        if (oring == null) {
            return false;
        }
        // @todo check same base relations
        //if ( ! table.equals(oring.table) ) {
        //    return false;
        //}
        //if ( ! coeffTable.equals(oring.coeffTable) ) {
        //    return false;
        //}
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
        h = 37 * h + coeffTable.hashCode(); // may be different
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as RecSolvablePolynomial<C>.
     */
    @Override
    public RecSolvablePolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as RecSolvablePolynomial<C>.
     */
    @Override
    public RecSolvablePolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (coeffTable.size() == 0) {
            return super.isCommutative();
        }
        // todo: check structure of relations
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
        RecSolvablePolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<GenPolynomial<C>>> gens = generators();
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (RecSolvablePolynomial<C>) gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = (RecSolvablePolynomial<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (RecSolvablePolynomial<C>) gens.get(k);
                    p = Xk.multiply(Xj).multiply(Xi);
                    q = Xk.multiply(Xj.multiply(Xi));
                    if (!p.equals(q)) {
                        if (true || debug) {
                            logger.info("Xi = " + Xi + ", Xj = " + Xj + ", Xk = " + Xk);
                            logger.info("p = ( Xk * Xj ) * Xi = " + p);
                            logger.info("q = Xk * ( Xj * Xi ) = " + q);
                        }
                        return false;
                    }
                }
            }
        }
        return coFac.isAssociative();
    }


    /**
     * Get a (constant) RecSolvablePolynomial&lt;C&gt; element from a long
     * value.
     * @param a long.
     * @return a RecSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvablePolynomial<C> fromInteger(long a) {
        return new RecSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) RecSolvablePolynomial&lt;C&gt; element from a BigInteger
     * value.
     * @param a BigInteger.
     * @return a RecSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvablePolynomial<C> fromInteger(BigInteger a) {
        return new RecSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public RecSolvablePolynomial<C> random(int n) {
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
    public RecSolvablePolynomial<C> random(int n, Random rnd) {
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
    public RecSolvablePolynomial<C> random(int k, int l, int d, float q) {
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
    public RecSolvablePolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        RecSolvablePolynomial<C> r = getZERO(); // copy( ZERO ); 
        ExpVector e;
        GenPolynomial<C> a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.EVRAND(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (RecSolvablePolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public RecSolvablePolynomial<C> copy(RecSolvablePolynomial<C> c) {
        return new RecSolvablePolynomial<C>(this, c.val);
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param s String.
     * @return RecSolvablePolynomial from s.
     */
    @Override
    public RecSolvablePolynomial<C> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param r Reader.
     * @return next RecSolvablePolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public RecSolvablePolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        RecSolvablePolynomial<C> p = null;
        try {
            GenSolvablePolynomial<GenPolynomial<C>> s = pt.nextSolvablePolynomial();
            p = new RecSolvablePolynomial<C>(this, s);
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
    public RecSolvablePolynomial<C> univariate(int i) {
        return (RecSolvablePolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public RecSolvablePolynomial<C> univariate(int i, long e) {
        return (RecSolvablePolynomial<C>) super.univariate(i, e);
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
    public RecSolvablePolynomial<C> univariate(int modv, int i, long e) {
        return (RecSolvablePolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    //todo Override
    public List<RecSolvablePolynomial<C>> recUnivariateList() {
        //return castToSolvableList( super.univariateList() );
        return (List<RecSolvablePolynomial<C>>) (Object) univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    //todo Override
    public List<RecSolvablePolynomial<C>> recUnivariateList(int modv) {
        return (List<RecSolvablePolynomial<C>>) (Object) univariateList(modv, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables with given
     * exponent.
     * @param modv number of module variables.
     * @param e the exponent of the variables.
     * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
     */
    //todo Override
    public List<RecSolvablePolynomial<C>> recUnivariateList(int modv, long e) {
        List<RecSolvablePolynomial<C>> pols = new ArrayList<RecSolvablePolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            RecSolvablePolynomial<C> p = univariate(modv, nm - 1 - i, e);
            pols.add(p);
        }
        return pols;
    }


    /*
     * Generate list of univariate polynomials in all variables with given exponent.
     * @param modv number of module variables.
     * @param e the exponent of the variables.
     * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
     @Override
     public List<RecSolvablePolynomial<C>> univariateList(int modv, long e) {
     List<GenPolynomial<C>> pol = super.univariateList(modv,e);
     UnaryFunctor<GenPolynomial<C>,RecSolvablePolynomial<C>> fc 
     = new UnaryFunctor<GenPolynomial<C>,RecSolvablePolynomial<C>>() {
     public RecSolvablePolynomial<C> eval(GenPolynomial<C> p) {
     if ( ! (p instanceof RecSolvablePolynomial) ) {
     throw new RuntimeException("no solvable polynomial "+p);
     }
     return (RecSolvablePolynomial<C>) p;
     }
     };
     List<RecSolvablePolynomial<C>> pols 
     = ListUtil.<GenPolynomial<C>,RecSolvablePolynomial<C>>map(this,pol,fc);
     return pols;
     }
    */


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public RecSolvablePolynomialRing<C> extend(int i) {
        GenPolynomialRing<GenPolynomial<C>> pfac = super.extend(i);
        RecSolvablePolynomialRing<C> spfac = new RecSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.table.extend(this.table);
        spfac.coeffTable.extend(this.coeffTable);
        return spfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     * @param i number of variables to remove.
     * @return contracted solvable polynomial ring factory.
     */
    @Override
    public RecSolvablePolynomialRing<C> contract(int i) {
        GenPolynomialRing<GenPolynomial<C>> pfac = super.contract(i);
        RecSolvablePolynomialRing<C> spfac = new RecSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.table.contract(this.table);
        spfac.coeffTable.contract(this.coeffTable);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public RecSolvablePolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public RecSolvablePolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<GenPolynomial<C>> pfac = super.reverse(partial);
        RecSolvablePolynomialRing<C> spfac = new RecSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.coeffTable.reverse(this.coeffTable);
        return spfac;
    }

}
