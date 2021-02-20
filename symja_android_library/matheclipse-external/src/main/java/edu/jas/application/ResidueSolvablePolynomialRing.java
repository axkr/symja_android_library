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
 * ResidueSolvablePolynomialRing generic solvable polynomial with residue
 * coefficients factory implementing RingFactory and extending
 * GenSolvablePolynomialRing factory. Factory for n-variate ordered solvable
 * polynomials over solvable residue coefficients. The non-commutative
 * multiplication relations are maintained in a relation table and the
 * non-commutative multiplication relations between the coefficients and the
 * main variables are maintained in a coefficient relation table. Almost
 * immutable object, except variable names and relation table contents.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public class ResidueSolvablePolynomialRing<C extends GcdRingElem<C>> extends
                GenSolvablePolynomialRing<SolvableResidue<C>> {


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
    public final ResidueSolvablePolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final ResidueSolvablePolynomial<C> ONE;


    private static final Logger logger = LogManager.getLogger(ResidueSolvablePolynomialRing.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n) {
        this(cf, n, new TermOrder(), null, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param rt solvable multiplication relations.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n,
                    RelationTable<SolvableResidue<C>> rt) {
        this(cf, n, new TermOrder(), null, rt);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n, TermOrder t) {
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
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n, TermOrder t,
                    RelationTable<SolvableResidue<C>> rt) {
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
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n, TermOrder t, String[] v) {
        this(cf, n, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * given term order and commutative relations.
     * @param cf factory for coefficients of type C.
     * @param t a term order.
     * @param v names for the variables.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     * @param cf factory for coefficients of type C.
     * @param v names for the variables.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, String[] v) {
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
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, int n, TermOrder t, String[] v,
                    RelationTable<SolvableResidue<C>> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        SolvableResidueRing<C> cfring = (SolvableResidueRing<C>) cf; // == coFac
        polCoeff = new RecSolvablePolynomialRing<C>(cfring.ring, n, t, v);
        if (table.size() > 0) { // TODO
            ExpVector e = null;
            ExpVector f = null;
            GenSolvablePolynomial<GenPolynomial<C>> p = null;
            polCoeff.table.update(e, f, p); // from rt
        }
        //coeffTable = polCoeff.coeffTable; //new RelationTable<GenPolynomial<C>>(polCoeff, true);
        ZERO = new ResidueSolvablePolynomial<C>(this);
        SolvableResidue<C> coeff = coFac.getONE();
        //evzero = ExpVector.create(nvar); // from super
        ONE = new ResidueSolvablePolynomial<C>(this, coeff, evzero);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the the
     * same term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ and the
     * solvable multiplication relations are <b>empty</b>.
     * @param cf factory for coefficients of type C.
     * @param o other solvable polynomial ring.
     */
    public ResidueSolvablePolynomialRing(RingFactory<SolvableResidue<C>> cf, ResidueSolvablePolynomialRing o) {
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
            s.append(((RingElem<SolvableResidue<C>>) coFac).toScriptFactory());
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
        if (!(other instanceof ResidueSolvablePolynomialRing)) {
            return false;
        }
        ResidueSolvablePolynomialRing<C> oring = null;
        try {
            oring = (ResidueSolvablePolynomialRing<C>) other;
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
     * @return 0 as ResidueSolvablePolynomial<C>.
     */
    @Override
    public ResidueSolvablePolynomial<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as ResidueSolvablePolynomial<C>.
     */
    @Override
    public ResidueSolvablePolynomial<C> getONE() {
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
    @SuppressWarnings("unused")
    @Override
    public boolean isAssociative() {
        if (!coFac.isAssociative()) {
            return false;
        }
        //System.out.println("polCoeff = " + polCoeff.toScript());
        if (!polCoeff.isAssociative()) { // not done via generators??
            return false;
        }
        ResidueSolvablePolynomial<C> Xi, Xj, Xk, p, q;
        List<GenPolynomial<SolvableResidue<C>>> gens = generators();
        //System.out.println("Residu gens = " + gens);
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (ResidueSolvablePolynomial<C>) gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = (ResidueSolvablePolynomial<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (ResidueSolvablePolynomial<C>) gens.get(k);
                    p = Xk.multiply(Xj).multiply(Xi);
                    q = Xk.multiply(Xj.multiply(Xi));
                    if (!p.equals(q)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
                            logger.info("p = ( Xk * Xj ) * Xi = " + p);
                            logger.info("q = Xk * ( Xj * Xi ) = " + q);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Get a (constant) ResidueSolvablePolynomial&lt;C&gt; element from a long
     * value.
     * @param a long.
     * @return a ResidueSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvablePolynomial<C> fromInteger(long a) {
        return new ResidueSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) ResidueSolvablePolynomial&lt;C&gt; element from a
     * BigInteger value.
     * @param a BigInteger.
     * @return a ResidueSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public ResidueSolvablePolynomial<C> fromInteger(BigInteger a) {
        return new ResidueSolvablePolynomial<C>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public ResidueSolvablePolynomial<C> random(int n) {
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
    public ResidueSolvablePolynomial<C> random(int n, Random rnd) {
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
    public ResidueSolvablePolynomial<C> random(int k, int l, int d, float q) {
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
    public ResidueSolvablePolynomial<C> random(int k, int l, int d, float q, Random rnd) {
        ResidueSolvablePolynomial<C> r = getZERO(); // copy( ZERO ); 
        ExpVector e;
        SolvableResidue<C> a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.random(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (ResidueSolvablePolynomial<C>) r.sum(a, e);
            // somewhat inefficient but clean
        }
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public ResidueSolvablePolynomial<C> copy(ResidueSolvablePolynomial<C> c) {
        return new ResidueSolvablePolynomial<C>(this, c.getMap());
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param s String.
     * @return ResidueSolvablePolynomial from s.
     */
    @Override
    public ResidueSolvablePolynomial<C> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     * @param r Reader.
     * @return next ResidueSolvablePolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResidueSolvablePolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        ResidueSolvablePolynomial<C> p = null;
        try {
            GenSolvablePolynomial<SolvableResidue<C>> s = pt.nextSolvablePolynomial();
            p = new ResidueSolvablePolynomial<C>(this, s);
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
    public ResidueSolvablePolynomial<C> univariate(int i) {
        return (ResidueSolvablePolynomial<C>) super.univariate(i);
    }


    /**
     * Generate univariate solvable polynomial in a given variable with given
     * exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as solvable univariate polynomial.
     */
    @Override
    public ResidueSolvablePolynomial<C> univariate(int i, long e) {
        return (ResidueSolvablePolynomial<C>) super.univariate(i, e);
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
    public ResidueSolvablePolynomial<C> univariate(int modv, int i, long e) {
        return (ResidueSolvablePolynomial<C>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<ResidueSolvablePolynomial<C>> univariateList() {
        return univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     * @param modv number of module variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    @Override
    public List<ResidueSolvablePolynomial<C>> univariateList(int modv) {
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
    public List<ResidueSolvablePolynomial<C>> univariateList(int modv, long e) {
        List<ResidueSolvablePolynomial<C>> pols = new ArrayList<ResidueSolvablePolynomial<C>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            ResidueSolvablePolynomial<C> p = univariate(modv, nm - 1 - i, e);
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
    public ResidueSolvablePolynomialRing<C> extend(int i) {
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
    public ResidueSolvablePolynomialRing<C> extend(int i, boolean top) {
        GenPolynomialRing<SolvableResidue<C>> pfac = super.extend(i, top);
        ResidueSolvablePolynomialRing<C> spfac = new ResidueSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.table.extend(this.table);
        spfac.polCoeff.coeffTable.extend(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param vn names for extended variables.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public ResidueSolvablePolynomialRing<C> extend(String[] vn) {
        return extend(vn, false);
    }

    
    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param vn names for extended variables.
     * @param top true for TOP term order, false for POT term order.
     * @return extended solvable polynomial ring factory.
     */
    @Override
    public ResidueSolvablePolynomialRing<C> extend(String[] vn, boolean top) {
        GenPolynomialRing<SolvableResidue<C>> pfac = super.extend(vn, top);
        ResidueSolvablePolynomialRing<C> spfac = new ResidueSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
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
    public ResidueSolvablePolynomialRing<C> contract(int i) {
        GenPolynomialRing<SolvableResidue<C>> pfac = super.contract(i);
        ResidueSolvablePolynomialRing<C> spfac = new ResidueSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
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
    public ResidueSolvablePolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public ResidueSolvablePolynomialRing<C> reverse(boolean partial) {
        GenPolynomialRing<SolvableResidue<C>> pfac = super.reverse(partial);
        ResidueSolvablePolynomialRing<C> spfac = new ResidueSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                        pfac.tord, pfac.getVars());
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.polCoeff.coeffTable.reverse(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Solvable residue coefficients from integral polynomial coefficients.
     * Represent as polynomial with type SolvableResidue<C> coefficients.
     * @param A polynomial with integral polynomial coefficients to be
     *            converted.
     * @return polynomial with type SolvableResidue<C> coefficients.
     */
    public ResidueSolvablePolynomial<C> fromPolyCoefficients(GenSolvablePolynomial<GenPolynomial<C>> A) {
        ResidueSolvablePolynomial<C> B = getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<SolvableResidue<C>> cfac = coFac;
        SolvableResidueRing<C> qfac = (SolvableResidueRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) y.getValue();
            SolvableResidue<C> p = new SolvableResidue<C>(qfac, a); // can be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from solvable residue coefficients. Represent as
     * polynomial with type GenSolvablePolynomial<C> coefficients.
     * @param A polynomial with solvable residue coefficients to be converted.
     * @return polynomial with type GenSolvablePolynomial<C> coefficients.
     */
    public RecSolvablePolynomial<C> toPolyCoefficients(ResidueSolvablePolynomial<C> A) {
        RecSolvablePolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, SolvableResidue<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            SolvableResidue<C> a = y.getValue();
            GenPolynomial<C> p = a.val; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from solvable residue coefficients. Represent as
     * polynomial with type GenSolvablePolynomial<C> coefficients.
     * @param A polynomial with solvable residue coefficients to be converted.
     * @return polynomial with type GenSolvablePolynomial<C> coefficients.
     */
    public RecSolvablePolynomial<C> toPolyCoefficients(GenPolynomial<SolvableResidue<C>> A) {
        RecSolvablePolynomial<C> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, SolvableResidue<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            SolvableResidue<C> a = y.getValue();
            GenPolynomial<C> p = a.val; // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }

}
