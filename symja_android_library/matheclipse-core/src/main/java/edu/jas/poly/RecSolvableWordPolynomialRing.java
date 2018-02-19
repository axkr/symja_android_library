/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * RecSolvableWordPolynomialRing generic recursive solvable polynomial factory
 * implementing RingFactory and extending GenSolvablePolynomialRing factory.
 * Factory for n-variate ordered solvable polynomials over non-commutative word
 * polynomial coefficients. The non-commutative multiplication relations are
 * maintained in a relation table and the non-commutative multiplication
 * relations between the coefficients and the main variables are maintained in a
 * coefficient relation table. Almost immutable object, except variable names
 * and relation table contents.
 *
 * @param <C> base coefficient type.
 * @author Heinz Kredel
 */

public class RecSolvableWordPolynomialRing<C extends RingElem<C>> extends
        GenSolvablePolynomialRing<GenWordPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(RecSolvableWordPolynomialRing.class);
    private static final boolean debug = logger.isDebugEnabled();
    /**
     * The solvable multiplication relations between variables and coefficients.
     */
    public final RelationTable<GenWordPolynomial<C>> coeffTable;
    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final RecSolvableWordPolynomial<C> ZERO;
    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final RecSolvableWordPolynomial<C> ONE;


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     * @param rt solvable multiplication relations.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n,
                                         RelationTable<GenWordPolynomial<C>> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     * @param t  a term order.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n, TermOrder t) {
        this(cf, n, t, null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     * @param t  a term order.
     * @param rt solvable multiplication relations.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n, TermOrder t,
                                         RelationTable<GenWordPolynomial<C>> rt) {
        this(cf, n, t, null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     * @param t  a term order.
     * @param v  names for the variables.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     *
     * @param cf factory for coefficients of type C.
     * @param t  a term order.
     * @param v  names for the variables.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     *
     * @param cf factory for coefficients of type C.
     * @param v  names for the variables.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, String[] v) {
        this(cf, v.length, new TermOrder(), v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     * @param t  a term order.
     * @param v  names for the variables.
     * @param rt solvable multiplication relations.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, int n, TermOrder t,
                                         String[] v, RelationTable<GenWordPolynomial<C>> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        coeffTable = new RelationTable<GenWordPolynomial<C>>(this, true);
        ZERO = new RecSolvableWordPolynomial<C>(this);
        GenWordPolynomial<C> coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new RecSolvableWordPolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     *
     * @param cf factory for coefficients of type C.
     * @param o  other solvable polynomial ring.
     */
    public RecSolvableWordPolynomialRing(RingFactory<GenWordPolynomial<C>> cf, RecSolvableWordPolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars(), null);
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
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
     *
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
            s.append(((RingElem<GenWordPolynomial<C>>) coFac).toScriptFactory());
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
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof RecSolvableWordPolynomialRing)) {
            return false;
        }
        // do a super.equals( )
        if (!super.equals(other)) {
            return false;
        }
        RecSolvableWordPolynomialRing<C> oring = (RecSolvableWordPolynomialRing<C>) other;
        // check same base relations
        //if ( ! table.equals(oring.table) ) { // done in super
        //    return false;
        //}
        if (!coeffTable.equals(oring.coeffTable)) {
            return false;
        }
        return true;
    }


    /**
     * Hash code for this polynomial ring.
     *
     * @see Object#hashCode()
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
     *
     * @return 0 as RecSolvableWordPolynomial<C>.
     */
    @Override
    public RecSolvableWordPolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     *
     * @return 1 as RecSolvableWordPolynomial<C>.
     */
    @Override
    public RecSolvableWordPolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     *
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (coeffTable.isEmpty()) {
            return super.isCommutative();
        }
        return false;
    }


    /**
     * Query if this ring is associative. Test if the relations between the mian
     * variables and the coefficient generators define an associative solvable
     * ring.
     *
     * @return true, if this ring is associative, else false.
     */
    @SuppressWarnings("unused")
    @Override
    public boolean isAssociative() {
        if (!coFac.isAssociative()) {
            return false;
        }
        RecSolvableWordPolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<GenWordPolynomial<C>>> gens = generators();
        //System.out.println("Rec word gens = " + gens);
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (RecSolvableWordPolynomial<C>) gens.get(i);
            if (Xi.isONE()) {
                continue;
            }
            for (int j = i + 1; j < ngen; j++) {
                Xj = (RecSolvableWordPolynomial<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (RecSolvableWordPolynomial<C>) gens.get(k);
                    try {
                        p = Xk.multiply(Xj).multiply(Xi);
                        q = Xk.multiply(Xj.multiply(Xi));
                        if (!p.equals(q)) {
                            if (true || debug) {
                                logger.info("Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
                                logger.info("p = ( Xk * Xj ) * Xi = " + p);
                                logger.info("q = Xk * ( Xj * Xi ) = " + q);
                            }
                            return false;
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        System.out.println("Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
                    }
                }
            }
        }
        return true;
    }


    /**
     * Get a (constant) RecSolvableWordPolynomial&lt;C&gt; element from a
     * coefficient value.
     *
     * @param a coefficient.
     * @return a RecSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvableWordPolynomial<C> valueOf(GenWordPolynomial<C> a) {
        return new RecSolvableWordPolynomial<C>(this, a);
    }


    /**
     * Get a RecSolvableWordPolynomial&lt;C&gt; element from an ExpVector.
     *
     * @param e exponent vector.
     * @return a RecSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvableWordPolynomial<C> valueOf(ExpVector e) {
        return valueOf(coFac.getONE(), e);
    }


    /**
     * Get a RecSolvableWordPolynomial&lt;C&gt; element from a coeffcient and an
     * ExpVector.
     *
     * @param a coefficient.
     * @param e exponent vector.
     * @return a RecSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvableWordPolynomial<C> valueOf(GenWordPolynomial<C> a, ExpVector e) {
        return new RecSolvableWordPolynomial<C>(this, a, e);
    }


    /**
     * Get a (constant) RecSolvableWordPolynomial&lt;C&gt; element from a long
     * value.
     *
     * @param a long.
     * @return a RecSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvableWordPolynomial<C> fromInteger(long a) {
        return new RecSolvableWordPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) RecSolvableWordPolynomial&lt;C&gt; element from a
     * BigInteger value.
     *
     * @param a BigInteger.
     * @return a RecSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvableWordPolynomial<C> fromInteger(BigInteger a) {
        return new RecSolvableWordPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     *
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> random(int n) {
        return random(n, random);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     *
     * @param n   number of terms.
     * @param rnd is a source for random bits.
     * @return a random solvable polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> random(int n, Random rnd) {
        if (nvar == 1) {
            return random(5, n, n, 0.7f, rnd);
        }
        return random(5, n, 3, 0.3f, rnd);
    }


    /**
     * Generate a random solvable polynomial.
     *
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random solvable polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> random(int k, int l, int d, float q) {
        return random(k, l, d, q, random);
    }


    /**
     * Random solvable polynomial.
     *
     * @param k   size of random coefficients.
     * @param l   number of terms.
     * @param d   maximal degree in each variable.
     * @param q   density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return a random solvable polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        RecSolvableWordPolynomial<C> r = getZERO(); // copy( ZERO ); 
        ExpVector e;
        GenWordPolynomial<C> a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.EVRAND(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (RecSolvableWordPolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     *
     * @param c
     * @return a copy of c.
     */
    public RecSolvableWordPolynomial<C> copy(RecSolvableWordPolynomial<C> c) {
        return new RecSolvableWordPolynomial<C>(this, c.val);
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     *
     * @param s String.
     * @return RecSolvableWordPolynomial from s.
     */
    @Override
    public RecSolvableWordPolynomial<C> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     *
     * @param r Reader.
     * @return next RecSolvableWordPolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public RecSolvableWordPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        RecSolvableWordPolynomial<C> p = null;
        try {
            GenSolvablePolynomial<GenWordPolynomial<C>> s = pt.nextSolvablePolynomial();
            p = new RecSolvableWordPolynomial<C>(this, s);
        } catch (IOException e) {
            logger.error(e.toString() + " parse " + this);
            p = ZERO;
        }
        return p;
    }


    /**
     * Generate univariate solvable polynomial in a given variable.
     *
     * @param i the index of the variable.
     * @return X_i as solvable univariate polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> univariate(int i) {
        return (RecSolvableWordPolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     *
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> univariate(int i, long e) {
        return (RecSolvableWordPolynomial<C>) super.univariate(i, e);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     *
     * @param modv number of module variables.
     * @param i    the index of the variable.
     * @param e    the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public RecSolvableWordPolynomial<C> univariate(int modv, int i, long e) {
        return (RecSolvableWordPolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     *
     * @return List(X_1, ..., X_n) a list of univariate polynomials.
     */
    //todo Override
    @SuppressWarnings("unchecked")
    public List<RecSolvableWordPolynomial<C>> recUnivariateList() {
        //return castToSolvableList( super.univariateList() );
        return (List<RecSolvableWordPolynomial<C>>) (Object) univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     *
     * @param modv number of module variables.
     * @return List(X_1, ..., X_n) a list of univariate polynomials.
     */
    //todo Override
    @SuppressWarnings("unchecked")
    public List<RecSolvableWordPolynomial<C>> recUnivariateList(int modv) {
        return (List<RecSolvableWordPolynomial<C>>) (Object) univariateList(modv, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables with given
     * exponent.
     *
     * @param modv number of module variables.
     * @param e    the exponent of the variables.
     * @return List(X_1^e, ..., X_n^e) a list of univariate polynomials.
     */
    //todo Override
    public List<RecSolvableWordPolynomial<C>> recUnivariateList(int modv, long e) {
        List<RecSolvableWordPolynomial<C>> pols = new ArrayList<RecSolvableWordPolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            RecSolvableWordPolynomial<C> p = univariate(modv, nm - 1 - i, e);
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
     public List<RecSolvableWordPolynomial<C>> univariateList(int modv, long e) {
        List<GenPolynomial<C>> pol = super.univariateList(modv,e);
        UnaryFunctor<GenPolynomial<C>,RecSolvableWordPolynomial<C>> fc 
          = new UnaryFunctor<GenPolynomial<C>,RecSolvableWordPolynomial<C>>() {
               public RecSolvableWordPolynomial<C> eval(GenPolynomial<C> p) {
                  if ( ! (p instanceof RecSolvableWordPolynomial) ) {
                      throw new RuntimeException("no solvable polynomial "+p);
                  }
                  return (RecSolvableWordPolynomial<C>) p;
               }
            };
        List<RecSolvableWordPolynomial<C>> pols 
           = ListUtil.<GenPolynomial<C>,RecSolvableWordPolynomial<C>>map(this,pol,fc);
        return pols;
     }
     */


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     *
     * @param i number of variables to extend.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public RecSolvableWordPolynomialRing<C> extend(int i) {
        GenSolvablePolynomialRing<GenWordPolynomial<C>> pfac = super.extend(i);
        RecSolvableWordPolynomialRing<C> spfac = new RecSolvableWordPolynomialRing<C>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.vars, pfac.table);
        //spfac.table.extend(this.table); // pfac.table
        spfac.coeffTable.extend(this.coeffTable);
        return spfac;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting
     * variables.
     *
     * @param vs names for extended variables.
     * @return extended polynomial ring factory.
     */
    @Override
    public RecSolvableWordPolynomialRing<C> extend(String[] vs) {
        GenSolvablePolynomialRing<GenWordPolynomial<C>> pfac = super.extend(vs);
        RecSolvableWordPolynomialRing<C> spfac = new RecSolvableWordPolynomialRing<C>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.vars, pfac.table);
        //spfac.table.extend(this.table); // pfac.table??
        spfac.coeffTable.extend(this.coeffTable);
        return spfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     *
     * @param i number of variables to remove.
     * @return contracted solvable polynomial ring factory.
     */
    @Override
    public RecSolvableWordPolynomialRing<C> contract(int i) {
        GenPolynomialRing<GenWordPolynomial<C>> pfac = super.contract(i);
        RecSolvableWordPolynomialRing<C> spfac = new RecSolvableWordPolynomialRing<C>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.vars);
        spfac.table.contract(this.table);
        spfac.coeffTable.contract(this.coeffTable);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public RecSolvableWordPolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public RecSolvableWordPolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<GenWordPolynomial<C>> pfac = super.reverse(partial);
        RecSolvableWordPolynomialRing<C> spfac = new RecSolvableWordPolynomialRing<C>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.vars);
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.coeffTable.reverse(this.coeffTable);
        return spfac;
    }


    /* not possible:
     * Distributive representation as polynomial with all main variables.
     * @return distributive polynomial ring factory.
    @SuppressWarnings({"cast","unchecked"})
    public static <C extends RingElem<C>> // must be static because of types
       GenSolvablePolynomialRing<C> distribute(RecSolvableWordPolynomialRing<C> rf) {
    }
     */


    /**
     * Permutation of polynomial ring variables.
     *
     * @param P permutation.
     * @return P(this).
     */
    @Override
    public GenSolvablePolynomialRing<GenWordPolynomial<C>> permutation(List<Integer> P) {
        if (!coeffTable.isEmpty()) {
            throw new UnsupportedOperationException("permutation with coeff relations: " + this);
        }
        GenSolvablePolynomialRing<GenWordPolynomial<C>> pfac = (GenSolvablePolynomialRing<GenWordPolynomial<C>>) super
                .permutation(P);
        return pfac;
    }

}
