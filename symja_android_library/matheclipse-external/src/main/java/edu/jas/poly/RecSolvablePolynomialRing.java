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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * coefficients and the variables are maintained in a coefficient-polynomial
 * relation table. Almost immutable object, except variable names and relation
 * table contents.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public class RecSolvablePolynomialRing<C extends RingElem<C>>
                extends GenSolvablePolynomialRing<GenPolynomial<C>> {


    /**
     * The solvable multiplication relations between variables and coefficients.
     */
    public final RelationTable<GenPolynomial<C>> coeffTable;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public /*final*/ RecSolvablePolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public /*final*/ RecSolvablePolynomial<C> ONE;


    private static final Logger logger = LogManager.getLogger(RecSolvablePolynomialRing.class);


    //private static final boolean debug = logger.isDebugEnabled();


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
     * Generate the coefficient relation table of the solvable
     * polynomial ring from a polynomial list of relations.
     * @param rel polynomial list of relations [..., ei, fj, pij, ... ] with ei
     *            * fj = pij.
     */
    public void addCoeffRelations(List<GenPolynomial<GenPolynomial<C>>> rel) {
        coeffTable.addRelations(rel);
    }


    /**
     * Generate the coefficient relation table of the solvable
     * polynomial ring from a solvable polynomial list of relations.
     * @param rel solvable polynomial list of relations [..., ei, fj, pij, ... ]
     *            with ei * fj = pij.
     */
    public void addSolvCoeffRelations(List<GenSolvablePolynomial<GenPolynomial<C>>> rel) {
        coeffTable.addSolvRelations(rel);
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
        String to = tord.toScript();
        s.append(to);
        if (coeffTable.size() > 0) {
            String rel = coeffTable.toScript();
            s.append(",coeffpolrel=");
            s.append(rel);
        }
        if (table.size() > 0) {
            String rel = table.toScript();
            s.append(",rel=");
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
        if (other == null) {
            return false;
        }
        if (!(other instanceof RecSolvablePolynomialRing)) {
            return false;
        }
        // do a super.equals( )
        if (!super.equals(other)) {
            return false;
        }
        RecSolvablePolynomialRing<C> oring = (RecSolvablePolynomialRing<C>) other;
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
        if (ZERO == null || !ZERO.isZERO()) { // happened since May 5 2022
            // Name        : java-11-openjdk-headless, java-17-openjdk-headless
            // Version     : 11.0.15.0, 17.0.4
            // Release     : 150000.3.80.1, 150400.3.3.1
            RecSolvablePolynomial<C> x = ZERO;
            ZERO = new RecSolvablePolynomial<C>(this);
            logger.info("warn: ZERO@get |{}| wrong fix to {}", x, ZERO);
        }
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as RecSolvablePolynomial<C>.
     */
    @Override
    public RecSolvablePolynomial<C> getONE() {
        if (ONE == null || !ONE.isONE()) {
           ONE = new RecSolvablePolynomial<C>(this, coFac.getONE(), evzero);
           logger.info("warn: ONE@get {}", ONE);
        }
        return ONE;
    }


    /**
     * Query if this ring is commutative.
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
     * @return true, if this ring is associative, else false.
     */
    @SuppressWarnings("unused")
    @Override
    public boolean isAssociative() {
        if (!coFac.isAssociative()) {
            return false;
        }
        RecSolvablePolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<GenPolynomial<C>>> gens = generators();
        //System.out.println("Rec gens = " + gens);
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
                        if (logger.isInfoEnabled()) {
                            logger.info("Xi = {}, Xj = {}, Xk = {}", Xi, Xj, Xk);
                            logger.info("p = ( Xk * Xj ) * Xi = {}", p);
                            logger.info("q = Xk * ( Xj * Xi ) = {}", q);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Get a (constant) RecSolvablePolynomial&lt;C&gt; element from a
     * coefficient value.
     * @param a coefficient.
     * @return a RecSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvablePolynomial<C> valueOf(GenPolynomial<C> a) {
        return new RecSolvablePolynomial<C>(this, a);
    }


    /**
     * Get a RecSolvablePolynomial&lt;C&gt; element from an exponent vector.
     * @param e exponent vector.
     * @return a RecSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvablePolynomial<C> valueOf(ExpVector e) {
        return new RecSolvablePolynomial<C>(this, coFac.getONE(), e);
    }


    /**
     * Get a RecSolvablePolynomial&lt;C&gt; element from a coefficient and an
     * exponent vector.
     * @param a coefficient.
     * @param e exponent vector.
     * @return a RecSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public RecSolvablePolynomial<C> valueOf(GenPolynomial<C> a, ExpVector e) {
        return new RecSolvablePolynomial<C>(this, a, e);
    }


    /**
     * Get a (constant) RecSolvablePolynomial&lt;C&gt; element from a long value
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
        return random(2, n, 3, 0.3f, rnd);
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
            e = ExpVector.random(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            //System.out.println("a_random = " + a);
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
            logger.error("{} parse {}", e, this);
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
    @Override
    public List<RecSolvablePolynomial<C>> univariateList() {
        //return castToSolvableList( super.univariateList() );
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<RecSolvablePolynomial<C>> univariateList(int modv) {
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
    public List<RecSolvablePolynomial<C>> univariateList(int modv, long e) {
        List<RecSolvablePolynomial<C>> pols = new ArrayList<RecSolvablePolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            RecSolvablePolynomial<C> p = univariate(modv, nm - 1 - i, e);
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
    public RecSolvablePolynomialRing<C> extend(int i) {
        return extend(i, false);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @param top true for TOP term order, false for POT term order.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public RecSolvablePolynomialRing<C> extend(int i, boolean top) {
        GenSolvablePolynomialRing<GenPolynomial<C>> pfac = super.extend(i, top);
        RecSolvablePolynomialRing<C> spfac = new RecSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars, pfac.table);
        //spfac.table.extend(this.table); // pfac.table
        spfac.coeffTable.extend(this.coeffTable);
        return spfac;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting
     * variables.
     * @param vs names for extended variables.
     * @return extended polynomial ring factory.
     */
    @Override
    public RecSolvablePolynomialRing<C> extend(String[] vs) {
        return extend(vs, false);
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting
     * variables.
     * @param vs names for extended variables.
     * @param top true for TOP term order, false for POT term order.
     * @return extended polynomial ring factory.
     */
    @Override
    public RecSolvablePolynomialRing<C> extend(String[] vs, boolean top) {
        GenSolvablePolynomialRing<GenPolynomial<C>> pfac = super.extend(vs, top);
        RecSolvablePolynomialRing<C> spfac = new RecSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars, pfac.table);
        //spfac.table.extend(this.table); // is in pfac.table
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
     * @param partial true for partially reversed term orders.
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


    /**
     * Distributive representation as polynomial with all main variables.
     * @return distributive polynomial ring factory.
     */
    @SuppressWarnings({ "cast", "unchecked" })
    public static <C extends RingElem<C>> // must be static because of types
    GenSolvablePolynomialRing<C> distribute(RecSolvablePolynomialRing<C> rf) {
        // setup solvable polynomial ring
        GenSolvablePolynomialRing<C> fring = (GenSolvablePolynomialRing<C>) (GenSolvablePolynomialRing) rf;
        GenSolvablePolynomialRing<C> pfd = fring.distribute();
        // add coefficient relations:
        List<GenPolynomial<GenPolynomial<C>>> rl = (List<GenPolynomial<GenPolynomial<C>>>) (List) PolynomialList
                        .castToList(rf.coeffTable.relationList());
        List<GenPolynomial<C>> rld = PolyUtil.<C> distribute(pfd, rl);
        pfd.table.addRelations(rld);
        //System.out.println("pfd = " + pfd.toScript());
        return pfd;
    }


    /**
     * Permutation of polynomial ring variables.
     * @param P permutation.
     * @return P(this).
     */
    @Override
    public GenSolvablePolynomialRing<GenPolynomial<C>> permutation(List<Integer> P) {
        if (!coeffTable.isEmpty()) {
            throw new UnsupportedOperationException("permutation with coeff relations: " + this);
        }
        GenSolvablePolynomialRing<GenPolynomial<C>> pfac = (GenSolvablePolynomialRing<GenPolynomial<C>>) super.permutation(
                        P);
        return pfac;
    }

}
