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
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.RecSolvableWordPolynomial;
import edu.jas.poly.RecSolvableWordPolynomialRing;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * ResidueSolvableWordPolynomialRing solvable polynomial with word residue
 * coefficients factory. It implements RingFactory and extends
 * GenSolvablePolynomialRing factory. Factory for n-variate ordered solvable
 * polynomials over non-commutative word residue coefficients. The
 * non-commutative multiplication relations are maintained in a relation table
 * and the non-commutative multiplication relations between the coefficients and
 * the main variables are maintained in a coefficient relation table. Almost
 * immutable object, except variable names and relation table contents.
 * Will eventually be deprecated.
 * @param <C> base coefficient type.
 * @author Heinz Kredel
 */

public class ResidueSolvableWordPolynomialRing<C extends GcdRingElem<C>> extends
                GenSolvablePolynomialRing<WordResidue<C>> {


    /**
     * Recursive solvable polynomial ring with polynomial coefficients.
     */
    public final RecSolvableWordPolynomialRing<C> polCoeff;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final ResidueSolvableWordPolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final ResidueSolvableWordPolynomial<C> ONE;


    private static final Logger logger = LogManager.getLogger(ResidueSolvableWordPolynomialRing.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param rt solvable multiplication relations.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n,
                    RelationTable<WordResidue<C>> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n, TermOrder t) {
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
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n, TermOrder t,
                    RelationTable<WordResidue<C>> rt) {
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
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, String[] v) {
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
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf, int n, TermOrder t, String[] v,
                    RelationTable<WordResidue<C>> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        WordResidueRing<C> cfring = (WordResidueRing<C>) cf; // == coFac
        polCoeff = new RecSolvableWordPolynomialRing<C>(cfring.ring, n, t, v);
        if (table.size() > 0) {
            List<GenSolvablePolynomial<GenWordPolynomial<C>>> nt
                = new ArrayList<GenSolvablePolynomial<GenWordPolynomial<C>>>(); 
            for (GenSolvablePolynomial<WordResidue<C>> q : table.relationList()) {
                nt.add( this.toPolyCoefficients(q) ); // only with den == 1
            }
            polCoeff.table.addSolvRelations(nt);
        }
        ZERO = new ResidueSolvableWordPolynomial<C>(this);
        WordResidue<C> coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new ResidueSolvableWordPolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public ResidueSolvableWordPolynomialRing(RingFactory<WordResidue<C>> cf,
                    ResidueSolvableWordPolynomialRing o) {
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
            s.append(((RingElem<WordResidue<C>>) coFac).toScriptFactory());
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
        // if (polCoeff.coeffTable.size() > 0) {
        //     String rel = polCoeff.coeffTable.toScript();
        //     s.append(",coeffrel=");
        //     s.append(rel);
        // }
        s.append(")");
        String cpol = polCoeff.toScript();
        s.append("\n  # ");
        s.append(cpol);
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
        if (!(other instanceof ResidueSolvableWordPolynomialRing)) {
            return false;
        }
        // do a super.equals( )
        if (!super.equals(other)) {
            return false;
        }
        ResidueSolvableWordPolynomialRing<C> oring = (ResidueSolvableWordPolynomialRing<C>) other;
        // check same base relations done in super
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
     * @return 0 as ResidueSolvableWordPolynomial<C>.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as ResidueSolvableWordPolynomial<C>.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (polCoeff.coeffTable.isEmpty()) {
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
        ResidueSolvableWordPolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<WordResidue<C>>> gens = generators();
        //System.out.println("Rec word gens = " + gens);
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (ResidueSolvableWordPolynomial<C>) gens.get(i);
            if (Xi.isONE()) {
                continue;
            }
            for (int j = i + 1; j < ngen; j++) {
                Xj = (ResidueSolvableWordPolynomial<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (ResidueSolvableWordPolynomial<C>) gens.get(k);
                    try {
                        p = Xk.multiply(Xj).multiply(Xi);
                        q = Xk.multiply(Xj.multiply(Xi));
                        if (!p.equals(q)) {
                            logger.info("Xk = {}, Xj = {}, Xi = {}", Xk, Xj, Xi);
                            logger.info("p = ( Xk * Xj ) * Xi = {}", p);
                            logger.info("q = Xk * ( Xj * Xi ) = {}", q);
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
     * Get a (constant) ResidueSolvableWordPolynomial&lt;C&gt; element from a
     * coefficient value.
     * @param a coefficient.
     * @return a ResidueSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> valueOf(WordResidue<C> a) {
        return new ResidueSolvableWordPolynomial<C>(this, a, evzero);
    }


    /**
     * Get a ResidueSolvableWordPolynomial&lt;C&gt; element from an ExpVector.
     * @param e exponent vector.
     * @return a ResidueSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> valueOf(ExpVector e) {
        return valueOf(coFac.getONE(), e);
    }


    /**
     * Get a ResidueSolvableWordPolynomial&lt;C&gt; element from a coefficient
     * and an ExpVector.
     * @param a coefficient.
     * @param e exponent vector.
     * @return a ResidueSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> valueOf(WordResidue<C> a, ExpVector e) {
        return new ResidueSolvableWordPolynomial<C>(this, a, e);
    }


    /**
     * Get a (constant) ResidueSolvableWordPolynomial&lt;C&gt; element from a
     * long value.
     * @param a long.
     * @return a ResidueSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> fromInteger(long a) {
        return new ResidueSolvableWordPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) ResidueSolvableWordPolynomial&lt;C&gt; element from a
     * BigInteger value.
     * @param a BigInteger.
     * @return a ResidueSolvableWordPolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> fromInteger(BigInteger a) {
        return new ResidueSolvableWordPolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> random(int n) {
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
    public ResidueSolvableWordPolynomial<C> random(int n, Random rnd) {
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
    public ResidueSolvableWordPolynomial<C> random(int k, int l, int d, float q) {
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
    public ResidueSolvableWordPolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        ResidueSolvableWordPolynomial<C> r = getZERO(); // copy( ZERO ); 
        ExpVector e;
        WordResidue<C> a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.random(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (ResidueSolvableWordPolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public ResidueSolvableWordPolynomial<C> copy(ResidueSolvableWordPolynomial<C> c) {
        return new ResidueSolvableWordPolynomial<C>(this, c.getMap());
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param s String.
     * @return ResidueSolvableWordPolynomial from s.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param r Reader.
     * @return next ResidueSolvableWordPolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResidueSolvableWordPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        ResidueSolvableWordPolynomial<C> p = null;
        try {
            GenSolvablePolynomial<WordResidue<C>> s = pt.nextSolvablePolynomial();
            p = new ResidueSolvableWordPolynomial<C>(this, s);
        } catch (IOException e) {
            logger.error(e.toString() + " parse {}", this);
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
    public ResidueSolvableWordPolynomial<C> univariate(int i) {
        return (ResidueSolvableWordPolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> univariate(int i, long e) {
        return (ResidueSolvableWordPolynomial<C>) super.univariate(i, e);
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
    public ResidueSolvableWordPolynomial<C> univariate(int modv, int i, long e) {
        return (ResidueSolvableWordPolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<ResidueSolvableWordPolynomial<C>> univariateList() {
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<ResidueSolvableWordPolynomial<C>> univariateList(int modv) {
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
    public List<ResidueSolvableWordPolynomial<C>> univariateList(int modv, long e) {
        List<ResidueSolvableWordPolynomial<C>> pols = new ArrayList<ResidueSolvableWordPolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            ResidueSolvableWordPolynomial<C> p = univariate(modv, nm - 1 - i, e);
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
    public ResidueSolvableWordPolynomialRing<C> extend(int i) {
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
    public ResidueSolvableWordPolynomialRing<C> extend(int i, boolean top) {
        GenSolvablePolynomialRing<WordResidue<C>> pfac = super.extend(i, top);
        ResidueSolvableWordPolynomialRing<C> spfac = new ResidueSolvableWordPolynomialRing<C>(pfac.coFac,
                        pfac.nvar, pfac.tord, pfac.getVars());
        spfac.table.extend(this.table); 
        spfac.polCoeff.coeffTable.extend(this.polCoeff.coeffTable);
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
    public ResidueSolvableWordPolynomialRing<C> extend(String[] vs) {
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
    public ResidueSolvableWordPolynomialRing<C> extend(String[] vs, boolean top) {
        GenSolvablePolynomialRing<WordResidue<C>> pfac = super.extend(vs, top);
        ResidueSolvableWordPolynomialRing<C> spfac = new ResidueSolvableWordPolynomialRing<C>(pfac.coFac,
                        pfac.nvar, pfac.tord, pfac.getVars());
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
    public ResidueSolvableWordPolynomialRing<C> contract(int i) {
        GenPolynomialRing<WordResidue<C>> pfac = super.contract(i);
        ResidueSolvableWordPolynomialRing<C> spfac = new ResidueSolvableWordPolynomialRing<C>(pfac.coFac,
                        pfac.nvar, pfac.tord, pfac.getVars()); 
        spfac.table.contract(this.table);
        spfac.polCoeff.coeffTable.contract(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public ResidueSolvableWordPolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partially reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public ResidueSolvableWordPolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<WordResidue<C>> pfac = super.reverse(partial);
        ResidueSolvableWordPolynomialRing<C> spfac = new ResidueSolvableWordPolynomialRing<C>(pfac.coFac,
                        pfac.nvar, pfac.tord, pfac.getVars()); //, pfac.table);
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.polCoeff.coeffTable.reverse(this.polCoeff.coeffTable);
        return spfac;
    }


    /* not possible:
     * Distributive representation as polynomial with all main variables.
     * @return distributive polynomial ring factory.
    @SuppressWarnings({"cast","unchecked"})
    public static <C extends RingElem<C>> // must be static because of types
       GenSolvablePolynomialRing<C> distribute(ResidueSolvableWordPolynomialRing<C> rf) {
    }
     */


    /**
     * Permutation of polynomial ring variables.
     * @param P permutation.
     * @return P(this).
     */
    @Override
    public GenSolvablePolynomialRing<WordResidue<C>> permutation(List<Integer> P) {
        if (!polCoeff.coeffTable.isEmpty()) {
            throw new UnsupportedOperationException("permutation with coeff relations: " + this);
        }
        GenSolvablePolynomialRing<WordResidue<C>> pfac = (GenSolvablePolynomialRing<WordResidue<C>>) super
                        .permutation(P);
        return pfac;
    }


    /**
     * Word residue coefficients from integral word polynomial coefficients.
     * Represent as polynomial with type WordResidue<C> coefficients.
     * @param A polynomial with integral word polynomial coefficients to be
     *            converted.
     * @return polynomial with type WordResidue<C> coefficients.
     */
    public ResidueSolvableWordPolynomial<C> fromPolyCoefficients(GenSolvablePolynomial<GenWordPolynomial<C>> A) {
        ResidueSolvableWordPolynomial<C> B = getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<WordResidue<C>> cfac = coFac;
        WordResidueRing<C> qfac = (WordResidueRing<C>) cfac;
        for (Map.Entry<ExpVector, GenWordPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenWordPolynomial<C> a = y.getValue();
            WordResidue<C> p = new WordResidue<C>(qfac, a); // can be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral word function from word residue coefficients. Represent as
     * polynomial with type GenWordPolynomial<C> coefficients.
     * @param A polynomial with word residue coefficients to be converted.
     * @return polynomial with type GenWordPolynomial<C> coefficients.
     */
    public RecSolvableWordPolynomial<C> toPolyCoefficients(ResidueSolvableWordPolynomial<C> A) {
        RecSolvableWordPolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, WordResidue<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            WordResidue<C> a = y.getValue();
            GenWordPolynomial<C> p = a.val; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral word function from word residue coefficients. Represent as
     * polynomial with type GenWordPolynomial<C> coefficients.
     * @param A polynomial with word residue coefficients to be converted.
     * @return polynomial with type GenWordPolynomial<C> coefficients.
     */
    public RecSolvableWordPolynomial<C> toPolyCoefficients(GenPolynomial<WordResidue<C>> A) {
        RecSolvableWordPolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, WordResidue<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            WordResidue<C> a = y.getValue();
            GenWordPolynomial<C> p = a.val; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }
}
