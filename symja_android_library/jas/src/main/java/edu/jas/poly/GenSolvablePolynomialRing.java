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
 * GenSolvablePolynomialRing generic solvable polynomial factory implementing
 * RingFactory and extending GenPolynomialRing factory. Factory for n-variate
 * ordered solvable polynomials over C. The non-commutative multiplication
 * relations are maintained in a relation table. Almost immutable object, except
 * variable names and relation table contents.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public class GenSolvablePolynomialRing<C extends RingElem<C>> extends GenPolynomialRing<C> {


    //  implements RingFactory< GenSolvablePolynomial<C> > {


    /**
     * The solvable multiplication relations.
     */
    public final RelationTable<C> table;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final GenSolvablePolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final GenSolvablePolynomial<C> ONE;


    private static final Logger logger = Logger.getLogger(GenSolvablePolynomialRing.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param rt solvable multiplication relations.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n, RelationTable<C> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t) {
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
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t, RelationTable<C> rt) {
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
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, String[] v) {
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
    public GenSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v, RelationTable<C> rt) {
        super(cf, n, t, v);
        if (rt == null) {
            table = new RelationTable<C>(this);
        } else {
            table = rt;
        }
        ZERO = new GenSolvablePolynomial<C>(this);
        C coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new GenSolvablePolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public GenSolvablePolynomialRing(RingFactory<C> cf, GenSolvablePolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars(), null);
    }


    /**
     * Generate the relation table of the solvable polynomial ring 
     * from a relation generator.
     * @param rg relation generator.
     */
    public void addRelations(RelationGenerator<C> rg) {
        rg.generate(this);
    }


    /**
     * Generate the relation table of the solvable polynomial ring 
     * from a polynomial list of relations.
     * @param rel polynomial list of relations [..., ei, fj, pij, ... ] with ei * fj = pij.
     */
    public void addRelations(List<GenPolynomial<C>> rel) {
        table.addRelations(rel);
    }


    /**
     * Generate the relation table of the solvable polynomial ring 
     * from a solvable polynomial list of relations.
     * @param rel solvable polynomial list of relations [..., ei, fj, pij, ... ] with ei * fj = pij.
     */
    public void addSolvRelations(List<GenSolvablePolynomial<C>> rel) {
        table.addSolvRelations(rel);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String res = super.toString();
        if (PrettyPrint.isTrue()) {
            res += "\n" + table.toString(vars);
        } else {
            res += ", #rel = " + table.size();
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
            s.append(((RingElem<C>) coFac).toScriptFactory());
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
        if (!(other instanceof GenSolvablePolynomialRing)) {
            return false;
        }
        GenSolvablePolynomialRing<C> oring = null;
        try {
            oring = (GenSolvablePolynomialRing<C>) other;
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
        if (! table.equals(oring.table)) {
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
        h = 37 * h + table.hashCode();
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as GenSolvablePolynomial<C>.
     */
    @Override
    public GenSolvablePolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as GenSolvablePolynomial<C>.
     */
    @Override
    public GenSolvablePolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (table.isEmpty()) {
            return super.isCommutative();
        }
        return false;
    }


    /**
     * Query if this ring is associative. Test if the relations define an
     * associative solvable ring.
     * @return true, if this ring is associative, else false.
     */
    @Override
    public boolean isAssociative() {
        GenSolvablePolynomial<C> Xi, Xj, Xk, p, q;
        for (int i = 0; i < nvar; i++) {
            Xi = univariate(i);
            for (int j = i + 1; j < nvar; j++) {
                Xj = univariate(j);
                for (int k = j + 1; k < nvar; k++) {
                    Xk = univariate(k);
                    p = Xk.multiply(Xj).multiply(Xi);
                    q = Xk.multiply(Xj.multiply(Xi));
                    if (!p.equals(q)) {
                        if (true||debug) { 
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
     * Get a (constant) GenSolvablePolynomial&lt;C&gt; element from a long
     * value.
     * @param a long.
     * @return a GenSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public GenSolvablePolynomial<C> fromInteger(long a) {
        return new GenSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) GenSolvablePolynomial&lt;C&gt; element from a BigInteger
     * value.
     * @param a BigInteger.
     * @return a GenSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public GenSolvablePolynomial<C> fromInteger(BigInteger a) {
        return new GenSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public GenSolvablePolynomial<C> random(int n) {
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
    public GenSolvablePolynomial<C> random(int n, Random rnd) {
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
    public GenSolvablePolynomial<C> random(int k, int l, int d, float q) {
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
    public GenSolvablePolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        GenSolvablePolynomial<C> r = getZERO(); //.clone();
        // copy( ZERO ); 
        // new GenPolynomial<C>( this, getZERO().val );
        ExpVector e;
        C a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.EVRAND(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (GenSolvablePolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public GenSolvablePolynomial<C> copy(GenSolvablePolynomial<C> c) {
        return new GenSolvablePolynomial<C>(this, c.val);
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param s String.
     * @return GenSolvablePolynomial from s.
     */
    @Override
    public GenSolvablePolynomial<C> parse(String s) {
        //return getZERO();
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param r Reader.
     * @return next GenSolvablePolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        GenSolvablePolynomial<C> p = null;
        try {
            p = (GenSolvablePolynomial<C>) pt.nextSolvablePolynomial();
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
    public GenSolvablePolynomial<C> univariate(int i) {
        return (GenSolvablePolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public GenSolvablePolynomial<C> univariate(int i, long e) {
        return (GenSolvablePolynomial<C>) super.univariate(i, e);
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
    public GenSolvablePolynomial<C> univariate(int modv, int i, long e) {
        return (GenSolvablePolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<GenSolvablePolynomial<C>> univariateList() {
        //return castToSolvableList( super.univariateList() );
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<GenSolvablePolynomial<C>> univariateList(int modv) {
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
    public List<GenSolvablePolynomial<C>> univariateList(int modv, long e) {
        List<GenSolvablePolynomial<C>> pols = new ArrayList<GenSolvablePolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            GenSolvablePolynomial<C> p = univariate(modv, nm - 1 - i, e);
            pols.add(p);
        }
        return pols;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i. New variables commute with the exiting variables.
     * @param i number of variables to extend.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public GenSolvablePolynomialRing<C> extend(int i) {
        GenPolynomialRing<C> pfac = super.extend(i);
        GenSolvablePolynomialRing<C> spfac = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.table.extend(this.table);
        return spfac;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by length(vn). New variables commute with the exiting variables.
     * @param vn names for extended variables.
     * @return extended polynomial ring factory.
     */
    @Override
    public GenSolvablePolynomialRing<C> extend(String[] vn) {
        GenPolynomialRing<C> pfac = super.extend(vn);
        GenSolvablePolynomialRing<C> spfac = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        //GenSolvablePolynomialRing<C> spfac = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac);
        spfac.table.extend(this.table);
        return spfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     * @param i number of variables to remove.
     * @return contracted solvable polynomial ring factory.
     */
    @Override
    public GenSolvablePolynomialRing<C> contract(int i) {
        GenPolynomialRing<C> pfac = super.contract(i);
        GenSolvablePolynomialRing<C> spfac = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.table.contract(this.table);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public GenSolvablePolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public GenSolvablePolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<C> pfac = super.reverse(partial);
        GenSolvablePolynomialRing<C> spfac = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.vars);
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        return spfac;
    }


    /**
     * Recursive representation as polynomial ring with i main variables.
     * @param i number of main variables.
     * @return recursive polynomial ring factory.
     */
    @Override
    public GenSolvablePolynomialRing<GenPolynomial<C>> recursive(int i) {
        if (i <= 0 || i >= nvar) {
            throw new IllegalArgumentException("wrong: 0 < " + i + " < " + nvar);
        }
        GenSolvablePolynomialRing<C> cfac = contract(i);
        //System.out.println("cvars = " + Arrays.toString(cfac.vars));
        //System.out.println("vars = " + Arrays.toString(vars));
        String[] v = null;
        if (vars != null) {
            v = new String[i];
            int k = 0;
            for (int j = nvar - i; j < nvar; j++) {
                v[k++] = vars[j];
            }
        }
        //System.out.println("v = " + Arrays.toString(v));
        TermOrder to = tord.contract(0, i); // ??
        RecSolvablePolynomialRing<C> pfac = new RecSolvablePolynomialRing<C>(cfac, i, to, v);
        //System.out.println("pfac = " + pfac.toScript());
        pfac.table.recursive(table);
        pfac.coeffTable.recursive(table);
        return pfac;
    }


    /**
     * Distributive representation as polynomial with all main variables.
     * @return distributive polynomial ring factory.
     */
    @Override
    public GenSolvablePolynomialRing<C> distribute() {
        if ( !(coFac instanceof GenPolynomialRing) ) {
            return this;
        }
        RingFactory cf = coFac;
        RingFactory<GenPolynomial<C>> cfp = (RingFactory<GenPolynomial<C>>) cf;
        GenPolynomialRing cr = (GenPolynomialRing) cfp;
        //System.out.println("cr = " + cr.toScript());
        GenPolynomialRing<C> fac;
        if ( cr.vars != null ) {
            fac = cr.extend(vars);
        } else {
            fac = cr.extend(nvar);
        }
        //System.out.println("fac = " + fac.toScript());
        // fac could be a commutative polynomial ring, coefficient relations
        GenSolvablePolynomialRing<C> pfac 
            = new GenSolvablePolynomialRing<C>(fac.coFac, fac.nvar, this.tord, fac.vars);
        //System.out.println("pfac = " + pfac.toScript());
        if (fac instanceof GenSolvablePolynomialRing) {
            GenSolvablePolynomialRing<C> sfac = (GenSolvablePolynomialRing<C>) fac;
            List<GenSolvablePolynomial<C>> rlc = sfac.table.relationList(); 
            pfac.table.addSolvRelations(rlc);
            //System.out.println("pfac = " + pfac.toScript());
        }
        // main relations
        List<GenPolynomial<GenPolynomial<C>>> rl = (List<GenPolynomial<GenPolynomial<C>>>) (List) 
	    PolynomialList.castToList( table.relationList() ); 
        List<GenPolynomial<C>> rld = PolyUtil.<C> distribute(pfac,rl); 
        pfac.table.addRelations(rld);
        //System.out.println("pfac = " + pfac.toScript());
        // coeffTable not avaliable here
        return pfac;
    }

}
