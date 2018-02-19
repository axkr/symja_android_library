/*
 * $Id$
 */

package edu.jas.poly;
// todo: move to edu.jas.poly

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.QuotPairFactory;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * QLRSolvablePolynomialRing generic recursive solvable polynomial
 * factory implementing RingFactory and extending
 * GenSolvablePolynomialRing factory.  Factory for n-variate ordered
 * solvable polynomials over solvable quotient, local and
 * local-residue coefficients. The non-commutative multiplication
 * relations are maintained in a relation table and the
 * non-commutative multiplication relations between the coefficients
 * and the main variables are maintained in a coefficient relation
 * table. Almost immutable object, except variable names and relation
 * table contents.
 *
 * @param <C> polynomial coefficient type
 * @param <D> quotient coefficient type
 * @author Heinz Kredel
 */

public class QLRSolvablePolynomialRing<C extends GcdRingElem<C> & QuotPair<GenPolynomial<D>>,
        D extends GcdRingElem<D>>
        extends GenSolvablePolynomialRing<C> {


    private static final Logger logger = Logger.getLogger(QLRSolvablePolynomialRing.class);

    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Recursive solvable polynomial ring with polynomial coefficients.
     */
    public final RecSolvablePolynomialRing<D> polCoeff;


    /**
     * The constant polynomial 0 for this ring. Hides super ZERO.
     */
    public final QLRSolvablePolynomial<C, D> ZERO;


    /**
     * The constant polynomial 1 for this ring. Hides super ONE.
     */
    public final QLRSolvablePolynomial<C, D> ONE;


    /**
     * Factory to create coefficients.
     */
    public final QuotPairFactory<GenPolynomial<D>, C> qpfac;


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order and commutative relations.
     *
     * @param cf factory for coefficients of type C.
     * @param n  number of variables.
     */
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n) {
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n,
                                     RelationTable<C> rt) {
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t) {
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t,
                                     RelationTable<C> rt) {
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v) {
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, TermOrder t, String[] v) {
        this(cf, v.length, t, v, null);
    }


    /**
     * The constructor creates a solvable polynomial factory object with the
     * default term order.
     *
     * @param cf factory for coefficients of type C.
     * @param v  names for the variables.
     */
    public QLRSolvablePolynomialRing(RingFactory<C> cf, String[] v) {
        this(cf, v.length, new TermOrder(), v, null);
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, GenSolvablePolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars(), null);
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, QLRSolvablePolynomialRing o) {
        this(cf, (GenSolvablePolynomialRing) o);
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
    public QLRSolvablePolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v,
                                     RelationTable<C> rt) {
        super(cf, n, t, v, rt);
        //if (rt == null) { // handled in super }
        qpfac = (QuotPairFactory<GenPolynomial<D>, C>) cf; // crucial part of type
        RingFactory<GenPolynomial<D>> cfring = qpfac.pairFactory(); // == coFac.ring
        polCoeff = new RecSolvablePolynomialRing<D>(cfring, n, t, v);
        if (table.size() > 0) { // TODO
            ExpVector e = null;
            ExpVector f = null;
            GenSolvablePolynomial<GenPolynomial<D>> p = null;
            polCoeff.table.update(e, f, p); // from rt
            throw new RuntimeException("TODO");
        }
        ZERO = new QLRSolvablePolynomial<C, D>(this);
        C coeff = coFac.getONE();
        ONE = new QLRSolvablePolynomial<C, D>(this, coeff, evzero);
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
            res += "\n" + polCoeff.coeffTable.toString(vars);
            res += "\n" + polCoeff.table.toString(vars);
        } else {
            res += ", #rel = " + table.size() + " + " + polCoeff.coeffTable.size() + " + " + polCoeff.table.size();
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
            s.append(((RingElem<C>) coFac).toScriptFactory());
        } else {
            s.append(coFac.toScript().trim());
        }
        s.append(",\"" + varsToString() + "\",");
        String to = tord.toScript();
        s.append(to);
        String rel = "";
        if (table.size() > 0) {
            rel = table.toScript();
            s.append(",rel=");
            s.append(rel);
        }
        if (polCoeff.coeffTable.size() > 0) {
            String crel = polCoeff.coeffTable.toScript();
            s.append(",coeffrel=");
            s.append(crel);
        }
        if (polCoeff.table.size() > 0) { // should not be printed
            String polrel = polCoeff.table.toScript();
            if (!rel.equals(polrel)) {
                s.append(",polrel=");
                s.append(polrel);
            }
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
        if (!(other instanceof QLRSolvablePolynomialRing)) {
            return false;
        }
        QLRSolvablePolynomialRing<C, D> oring = null;
        try {
            oring = (QLRSolvablePolynomialRing<C, D>) other;
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
     *
     * @see Object#hashCode()
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
     *
     * @return 0 as QLRSolvablePolynomial.
     */
    @Override
    public QLRSolvablePolynomial<C, D> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     *
     * @return 1 as QLRSolvablePolynomial.
     */
    @Override
    public QLRSolvablePolynomial<C, D> getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     *
     * @return true if this ring is commutative, else false.
     */
    @Override
    public boolean isCommutative() {
        if (polCoeff.isCommutative()) {
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
    @Override
    @SuppressWarnings("unchecked")
    public boolean isAssociative() {
        if (!coFac.isAssociative()) {
            return false;
        }
        //System.out.println("polCoeff = " + polCoeff.toScript());
        if (!polCoeff.isAssociative()) { // not done via generators??
            return false;
        }
        QLRSolvablePolynomial<C, D> Xi, Xj, Xk, p, q;
        List<GenPolynomial<C>> gens = generators();
        //System.out.println("QLR gens = " + gens);
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = (QLRSolvablePolynomial<C, D>) gens.get(i);
            if (Xi.degree() == 0) {
                C lbc = Xi.leadingBaseCoefficient();
                if (lbc.numerator().degree() == 0 && lbc.denominator().degree() == 0) {
                    //System.out.println("qlr assoc skip: Xi = " + lbc);
                    continue; // skip
                }
            }
            for (int j = i + 1; j < ngen; j++) {
                Xj = (QLRSolvablePolynomial<C, D>) gens.get(j);
                if (Xj.degree() == 0) {
                    C lbc = Xi.leadingBaseCoefficient();
                    if (lbc.numerator().degree() == 0 && lbc.denominator().degree() == 0) {
                        //System.out.println("qlr assoc skip: Xj = " + lbc);
                        continue; // skip
                    }
                }
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (QLRSolvablePolynomial<C, D>) gens.get(k);
                    if (Xi.degree() == 0 && Xj.degree() == 0 && Xk.degree() == 0) {
                        //System.out.println("qlr assoc degree == 0");
                        continue; // skip
                    }
                    try {
                        p = Xk.multiply(Xj).multiply(Xi);
                        q = Xk.multiply(Xj.multiply(Xi));
                        //System.out.println("qlr assoc: p = " + p);
                        //System.out.println("qlr assoc: q = " + q);
                    } catch (IllegalArgumentException e) {
                        System.out.println("qlr assoc: Xi = " + Xi);
                        System.out.println("qlr assoc: Xj = " + Xj);
                        System.out.println("qlr assoc: Xk = " + Xk);
                        e.printStackTrace();
                        continue;
                    }
                    if (!p.equals(q)) {
                        if (logger.isInfoEnabled()) {
                            //System.out.println("qlr assoc: Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
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
        return true;
    }


    /**
     * Get a (constant) QLRSolvablePolynomial&lt;C&gt; element from a long
     * value.
     *
     * @param a long.
     * @return a QLRSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public QLRSolvablePolynomial<C, D> fromInteger(long a) {
        return new QLRSolvablePolynomial<C, D>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Get a (constant) QLRSolvablePolynomial&lt;C&gt; element from a
     * BigInteger value.
     *
     * @param a BigInteger.
     * @return a QLRSolvablePolynomial&lt;C&gt;.
     */
    @Override
    public QLRSolvablePolynomial<C, D> fromInteger(BigInteger a) {
        return new QLRSolvablePolynomial<C, D>(this, coFac.fromInteger(a), evzero);
    }


    /**
     * Random solvable polynomial. Generates a random solvable polynomial with k
     * = 5, l = n, d = (nvar == 1) ? n : 3, q = (nvar == 1) ? 0.7 : 0.3.
     *
     * @param n number of terms.
     * @return a random solvable polynomial.
     */
    @Override
    public QLRSolvablePolynomial<C, D> random(int n) {
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
    public QLRSolvablePolynomial<C, D> random(int n, Random rnd) {
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
    public QLRSolvablePolynomial<C, D> random(int k, int l, int d, float q) {
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
    @SuppressWarnings("unchecked")
    public QLRSolvablePolynomial<C, D> random(int k, int l, int d, float q, Random rnd) {
        QLRSolvablePolynomial<C, D> r = getZERO(); // copy( ZERO );
        ExpVector e;
        C a;
        // add random coeffs and exponents
        for (int i = 0; i < l; i++) {
            e = ExpVector.EVRAND(nvar, d, q, rnd);
            a = coFac.random(k, rnd);
            r = (QLRSolvablePolynomial<C, D>) r.sum(a, e);
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
    public QLRSolvablePolynomial<C, D> copy(QLRSolvablePolynomial<C, D> c) {
        return new QLRSolvablePolynomial<C, D>(this, c.getMap());
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     *
     * @param s String.
     * @return QLRSolvablePolynomial from s.
     */
    @Override
    public QLRSolvablePolynomial<C, D> parse(String s) {
        return parse(new StringReader(s));
    }


    /**
     * Parse a solvable polynomial with the use of GenPolynomialTokenizer
     *
     * @param r Reader.
     * @return next QLRSolvablePolynomial from r.
     */
    @Override
    @SuppressWarnings("unchecked")
    public QLRSolvablePolynomial<C, D> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this, r);
        QLRSolvablePolynomial<C, D> p = null;
        try {
            GenSolvablePolynomial<C> s = pt.nextSolvablePolynomial();
            p = new QLRSolvablePolynomial<C, D>(this, s);
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
    @SuppressWarnings("unchecked")
    public QLRSolvablePolynomial<C, D> univariate(int i) {
        return (QLRSolvablePolynomial<C, D>) super.univariate(i);
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
    @SuppressWarnings("unchecked")
    public QLRSolvablePolynomial<C, D> univariate(int i, long e) {
        return (QLRSolvablePolynomial<C, D>) super.univariate(i, e);
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
    @SuppressWarnings("unchecked")
    public QLRSolvablePolynomial<C, D> univariate(int modv, int i, long e) {
        return (QLRSolvablePolynomial<C, D>) super.univariate(modv, i, e);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     *
     * @return List(X_1, ..., X_n) a list of univariate polynomials.
     */
    //todo Override
    @SuppressWarnings("unchecked")
    public List<QLRSolvablePolynomial<C, D>> recUnivariateList() {
        //return castToSolvableList( super.univariateList() );
        return (List<QLRSolvablePolynomial<C, D>>) (Object) univariateList(0, 1L);
    }


    /**
     * Generate list of univariate polynomials in all variables.
     *
     * @param modv number of module variables.
     * @return List(X_1, ..., X_n) a list of univariate polynomials.
     */
    //todo Override
    @SuppressWarnings("unchecked")
    public List<QLRSolvablePolynomial<C, D>> recUnivariateList(int modv) {
        return (List<QLRSolvablePolynomial<C, D>>) (Object) univariateList(modv, 1L);
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
    public List<QLRSolvablePolynomial<C, D>> recUnivariateList(int modv, long e) {
        List<QLRSolvablePolynomial<C, D>> pols = new ArrayList<QLRSolvablePolynomial<C, D>>(nvar);
        int nm = nvar - modv;
        for (int i = 0; i < nm; i++) {
            QLRSolvablePolynomial<C, D> p = univariate(modv, nm - 1 - i, e);
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
     public List<QLRSolvablePolynomial<C,D>> univariateList(int modv, long e) {
     List<GenPolynomial<C>> pol = super.univariateList(modv,e);
     UnaryFunctor<GenPolynomial<C>,QLRSolvablePolynomial<C,D>> fc 
     = new UnaryFunctor<GenPolynomial<C>,QLRSolvablePolynomial<C,D>>() {
     public QLRSolvablePolynomial<C,D> eval(GenPolynomial<C> p) {
     if ( ! (p instanceof QLRSolvablePolynomial) ) {
     throw new RuntimeException("no solvable polynomial "+p);
     }
     return (QLRSolvablePolynomial<C,D>) p;
     }
     };
     List<QLRSolvablePolynomial<C,D>> pols 
     = ListUtil.<GenPolynomial<C>,QLRSolvablePolynomial<C,D>>map(this,pol,fc);
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
    public QLRSolvablePolynomialRing<C, D> extend(int i) {
        GenPolynomialRing<C> pfac = super.extend(i);
        QLRSolvablePolynomialRing<C, D> spfac = new QLRSolvablePolynomialRing<C, D>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.getVars());
        spfac.table.extend(this.table);
        spfac.polCoeff.coeffTable.extend(this.polCoeff.coeffTable);
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
    public QLRSolvablePolynomialRing<C, D> contract(int i) {
        GenPolynomialRing<C> pfac = super.contract(i);
        QLRSolvablePolynomialRing<C, D> spfac = new QLRSolvablePolynomialRing<C, D>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.getVars());
        spfac.table.contract(this.table);
        spfac.polCoeff.coeffTable.contract(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public QLRSolvablePolynomialRing<C, D> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @param partial true for partialy reversed term orders.
     * @return solvable polynomial ring factory with reversed variables.
     */
    @Override
    public QLRSolvablePolynomialRing<C, D> reverse(boolean partial) {
        GenPolynomialRing<C> pfac = super.reverse(partial);
        QLRSolvablePolynomialRing<C, D> spfac = new QLRSolvablePolynomialRing<C, D>(pfac.coFac, pfac.nvar,
                pfac.tord, pfac.getVars());
        spfac.partial = partial;
        spfac.table.reverse(this.table);
        spfac.polCoeff.coeffTable.reverse(this.polCoeff.coeffTable);
        return spfac;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type C coefficients.
     *
     * @param A polynomial with integral polynomial coefficients to be
     *          converted.
     * @return polynomial with type C coefficients.
     */
    public QLRSolvablePolynomial<C, D> fromPolyCoefficients(GenSolvablePolynomial<GenPolynomial<D>> A) {
        QLRSolvablePolynomial<C, D> B = getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, GenPolynomial<D>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenSolvablePolynomial<D> a = (GenSolvablePolynomial<D>) y.getValue();
            //C p = new C(qfac, a); 
            C p = qpfac.create(a);
            if (!p.isZERO()) {
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from rational polynomial coefficients. Represent as
     * polynomial with type GenSolvablePolynomial<C> coefficients.
     *
     * @param A polynomial with rational polynomial coefficients to be
     *          converted.
     * @return polynomial with type GenSolvablePolynomial<C> coefficients.
     */
    public RecSolvablePolynomial<D> toPolyCoefficients(QLRSolvablePolynomial<C, D> A) {
        RecSolvablePolynomial<D> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            if (!a.denominator().isONE()) {
                throw new IllegalArgumentException("den != 1 not supported: " + a);
            }
            GenPolynomial<D> p = a.numerator(); // can not be zero
            if (!p.isZERO()) {
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Integral function from rational polynomial coefficients. Represent as
     * polynomial with type GenSolvablePolynomial coefficients.
     *
     * @param A polynomial with rational polynomial coefficients to be
     *          converted.
     * @return polynomial with type GenSolvablePolynomial coefficients.
     */
    public RecSolvablePolynomial<D> toPolyCoefficients(GenPolynomial<C> A) {
        RecSolvablePolynomial<D> B = polCoeff.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            if (!a.denominator().isONE()) {
                throw new IllegalArgumentException("den != 1 not supported: " + a);
            }
            GenPolynomial<D> p = a.numerator(); // can not be zero
            if (!p.isZERO()) {
                B.doPutToMap(e, p);
            }
        }
        return B;
    }

}
