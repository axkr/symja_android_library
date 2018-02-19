/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import edu.jas.structure.RingElem;


/**
 * RecSolvableWordPolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over non-commutative word
 * polynomial coefficients. Objects of this class are intended to be immutable.
 * The implementation is based on TreeMap respectively SortedMap from exponents
 * to coefficients by extension of GenPolynomial.
 *
 * @param <C> base coefficient type
 * @author Heinz Kredel
 */

public class RecSolvableWordPolynomial<C extends RingElem<C>> extends
        GenSolvablePolynomial<GenWordPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(RecSolvableWordPolynomial.class);
    private static final boolean debug = logger.isDebugEnabled();
    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final RecSolvableWordPolynomialRing<C> ring;


    /**
     * Constructor for zero RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     */
    public RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param e exponent.
     */
    public RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r, ExpVector e) {
        this(r);
        val.put(e, ring.getONECoefficient());
    }


    /**
     * Constructor for RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r, GenWordPolynomial<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r, GenWordPolynomial<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r,
                                     GenSolvablePolynomial<GenWordPolynomial<C>> S) {
        this(r, S.val);
    }


    /**
     * Constructor for RecSolvableWordPolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected RecSolvableWordPolynomial(RecSolvableWordPolynomialRing<C> r,
                                        SortedMap<ExpVector, GenWordPolynomial<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     *
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public RecSolvableWordPolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this RecSolvableWordPolynomial.
     *
     * @see Object#clone()
     */
    @Override
    public RecSolvableWordPolynomial<C> copy() {
        return new RecSolvableWordPolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof RecSolvableWordPolynomial)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * RecSolvableWordPolynomial multiplication.
     *
     * @param Bp RecSolvableWordPolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // cannot @Override, @NoOverride
    public RecSolvableWordPolynomial<C> multiply(RecSolvableWordPolynomial<C> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.nvar == Bp.ring.nvar);
        if (debug) {
            logger.info("ring = " + ring.toScript());
        }
        final boolean commute = ring.table.isEmpty();
        final boolean commuteCoeff = ring.coeffTable.isEmpty();
        //GenWordPolynomialRing<C> cfac = (GenWordPolynomialRing<C>) ring.coFac;
        RecSolvableWordPolynomial<C> Dp = ring.getZERO().copy();
        RecSolvableWordPolynomial<C> zero = ring.getZERO(); //.copy(); not needed
        ExpVector Z = ring.evzero;
        //Word Zc = cfac.wone;
        GenWordPolynomial<C> one = ring.getONECoefficient();

        RecSolvableWordPolynomial<C> C1 = null;
        RecSolvableWordPolynomial<C> C2 = null;
        Map<ExpVector, GenWordPolynomial<C>> A = val;
        Map<ExpVector, GenWordPolynomial<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, GenWordPolynomial<C>>> Bk = B.entrySet();
        if (debug)
            logger.info("input A = " + this);
        for (Map.Entry<ExpVector, GenWordPolynomial<C>> y : A.entrySet()) {
            GenWordPolynomial<C> a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = " + e + ", a = " + a);
            int[] ep = e.dependencyOnVariables();
            int el1 = ring.nvar + 1;
            if (ep.length > 0) {
                el1 = ep[0];
            }
            //int el1s = ring.nvar + 1 - el1;
            if (debug)
                logger.info("input B = " + Bp);
            for (Map.Entry<ExpVector, GenWordPolynomial<C>> x : Bk) {
                GenWordPolynomial<C> b = x.getValue();
                ExpVector f = x.getKey();
                if (debug)
                    logger.info("f = " + f + ", b = " + b);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0;
                if (fp.length > 0) {
                    fl1 = fp[fp.length - 1];
                }
                int fl1s = ring.nvar + 1 - fl1;
                // polynomial coefficient multiplication e*b = P_eb, for a*((e*b)*f)
                RecSolvableWordPolynomial<C> Cps = ring.getZERO().copy();
                RecSolvableWordPolynomial<C> Cs = null;
                if (commuteCoeff || b.isConstant() || e.isZERO()) { // symmetric
                    //Cps = (RecSolvableWordPolynomial<C>) zero.sum(b, e);
                    Cps.doAddTo(b, e);
                    if (debug)
                        logger.info("symmetric coeff, e*b: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff, e*b: b = " + b + ", e = " + e);
                    for (Map.Entry<Word, C> z : b.val.entrySet()) {
                        C c = z.getValue();
                        GenWordPolynomial<C> cc = b.ring.getONE().multiply(c);
                        Word g = z.getKey();
                        if (debug)
                            logger.info("g = " + g + ", c = " + c);
                        // split e = e1 * e2, g = g2 * g1
                        ExpVector g2 = g.leadingExpVector();
                        Word g1 = g.reductum();
                        //ExpVector g1;
                        //System.out.println("c = " + c + ", g = " + g + ", g1 = " + g1 + ", g1 == 1: " + g1.isONE());
                        ExpVector e1 = e;
                        ExpVector e2 = Z;
                        if (!e.isZERO()) {
                            e1 = e.subst(el1, 0);
                            e2 = Z.subst(el1, e.getVal(el1));
                        }
                        if (debug) {
                            logger.info("coeff, e1 = " + e1 + ", e2 = " + e2 + ", Cps = " + Cps);
                            logger.info("coeff, g2 = " + g2 + ", g1 = " + g1);
                        }
                        TableRelation<GenWordPolynomial<C>> crel = ring.coeffTable.lookup(e2, g2);
                        if (debug)
                            logger.info("coeff, crel = " + crel.p);
                        //System.out.println("coeff, e  = " + e + ", g = " + g + ", crel = " + crel);
                        Cs = new RecSolvableWordPolynomial<C>(ring, crel.p);
                        // rest of multiplication and update relations
                        if (crel.f != null) { // process remaining right power
                            //GenWordPolynomial<C> c2 = b.ring.getONE().multiply(crel.f);
                            GenWordPolynomial<C> c2 = b.ring.valueOf(crel.f);
                            C2 = ring.valueOf(c2); //new RecSolvableWordPolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                            ExpVector e4;
                            if (crel.e == null) {
                                e4 = e2;
                            } else {
                                e4 = e2.subtract(crel.e);
                            }
                            ring.coeffTable.update(e4, g2, Cs);
                        }
                        if (crel.e != null) { // process remaining left power
                            C1 = ring.valueOf(crel.e); //new RecSolvableWordPolynomial<C>(ring, one, crel.e);
                            Cs = C1.multiply(Cs);
                            ring.coeffTable.update(e2, g2, Cs);
                        }
                        if (!g1.isONE()) { // process remaining right part
                            //GenWordPolynomial<C> c2 = b.ring.getONE().multiply(g1);
                            GenWordPolynomial<C> c2 = b.ring.valueOf(g1);
                            C2 = ring.valueOf(c2); //new RecSolvableWordPolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                        }
                        if (!e1.isZERO()) { // process remaining left part
                            C1 = ring.valueOf(e1); //new RecSolvableWordPolynomial<C>(ring, one, e1);
                            Cs = C1.multiply(Cs);
                        }
                        //System.out.println("e1*Cs*g1 = " + Cs);
                        Cs = Cs.multiplyLeft(cc); // assume c, coeff(cc) commutes with Cs
                        //Cps = (RecSolvableWordPolynomial<C>) Cps.sum(Cs);
                        Cps.doAddTo(Cs);
                    } // end b loop 
                    if (debug)
                        logger.info("coeff, Cs = " + Cs + ", Cps = " + Cps);
                    //System.out.println("coeff loop end, Cs = " + Cs + ", Cps = " + Cps);
                }
                if (debug)
                    logger.info("coeff-poly: Cps = " + Cps);
                // polynomial multiplication P_eb*f, for a*(P_eb*f)
                RecSolvableWordPolynomial<C> Dps = ring.getZERO().copy();
                RecSolvableWordPolynomial<C> Ds = null;
                RecSolvableWordPolynomial<C> D1, D2;
                if (commute || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly, P_eb*f: Cps = " + Cps + ", f = " + f);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = ring.valueOf(Cps.leadingBaseCoefficient(), g); //new RecSolvableWordPolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly, P_eb*f: Cps = " + Cps + ", f = " + f);
                    for (Map.Entry<ExpVector, GenWordPolynomial<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        GenWordPolynomial<C> c = z.getValue();
                        ExpVector g = z.getKey();
                        if (debug)
                            logger.info("g = " + g + ", c = " + c);
                        int[] gp = g.dependencyOnVariables();
                        int gl1 = ring.nvar + 1;
                        if (gp.length > 0) {
                            gl1 = gp[0];
                        }
                        int gl1s = ring.nvar + 1 - gl1;
                        if (gl1s <= fl1s) { // symmetric
                            ExpVector h = g.sum(f);
                            if (debug)
                                logger.info("disjoint poly: g = " + g + ", f = " + f + ", h = " + h);
                            Ds = (RecSolvableWordPolynomial<C>) zero.sum(one, h); // symmetric!
                        } else {
                            ExpVector g1 = g.subst(gl1, 0);
                            ExpVector g2 = Z.subst(gl1, g.getVal(gl1)); // bug el1, gl1
                            ExpVector g4;
                            ExpVector f1 = f.subst(fl1, 0);
                            ExpVector f2 = Z.subst(fl1, f.getVal(fl1));
                            if (debug) {
                                logger.info("poly, g1 = " + g1 + ", f1 = " + f1 + ", Dps = " + Dps);
                                logger.info("poly, g2 = " + g2 + ", f2 = " + f2);
                            }
                            TableRelation<GenWordPolynomial<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new RecSolvableWordPolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = ring.valueOf(rel.f); //new RecSolvableWordPolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = ring.valueOf(rel.e); //new RecSolvableWordPolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = ring.valueOf(f1); //new RecSolvableWordPolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = ring.valueOf(g1); //new RecSolvableWordPolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        //System.out.println("main loop, Cs = " + Cs + ", c = " + c);
                        Ds = Ds.multiplyLeft(c); // assume c commutes with Cs
                        //Dps = (RecSolvableWordPolynomial<C>) Dps.sum(Ds);
                        Dps.doAddTo(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                if (debug) {
                    logger.info("recursion+: Ds = " + Ds + ", a = " + a);
                }
                // polynomial coefficient multiplication a*(P_eb*f) = a*Ds
                //System.out.println("main loop, Ds = " + Ds + ", a = " + a);
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.info("recursion-: Ds = " + Ds);
                //Dp = (RecSolvableWordPolynomial<C>) Dp.sum(Ds);
                Dp.doAddTo(Ds);
                if (debug)
                    logger.info("end B loop: Dp = " + Dp);
            } // end B loop
            if (debug)
                logger.info("end A loop: Dp = " + Dp);
        } // end A loop
        return Dp;
    }


    /**
     * RecSolvableWordPolynomial left and right multiplication. Product with two
     * polynomials.
     *
     * @param S RecSolvableWordPolynomial.
     * @param T RecSolvableWordPolynomial.
     * @return S*this*T.
     */
    // cannot @Override, @NoOverride
    public RecSolvableWordPolynomial<C> multiply(RecSolvableWordPolynomial<C> S,
                                                 RecSolvableWordPolynomial<C> T) {
        if (S.isZERO() || T.isZERO() || this.isZERO()) {
            return ring.getZERO();
        }
        if (S.isONE()) {
            return multiply(T);
        }
        if (T.isONE()) {
            return S.multiply(this);
        }
        return S.multiply(this).multiply(T);
    }


    /**
     * RecSolvableWordPolynomial multiplication. Product with coefficient ring
     * element.
     *
     * @param b coefficient polynomial.
     * @return this*b, where * is coefficient multiplication.
     */
    //todo @Override, @NoOverride
    //public RecSolvableWordPolynomial<C> multiply(GenWordPolynomial<C> b) {
    //public GenSolvablePolynomial<GenWordPolynomial<C>> multiply(GenWordPolynomial<C> b) {
    public RecSolvableWordPolynomial<C> recMultiply(GenWordPolynomial<C> b) {
        RecSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Cp = ring.valueOf(b); //new RecSolvableWordPolynomial<C>(ring, b, ring.evzero);
        return multiply(Cp);
    }


    /**
     * RecSolvableWordPolynomial left and right multiplication. Product with
     * coefficient ring element.
     *
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(GenWordPolynomial<C> b, GenWordPolynomial<C> c) {
        RecSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        RecSolvableWordPolynomial<C> Cb = ring.valueOf(b); //new RecSolvableWordPolynomial<C>(ring, b, ring.evzero);
        RecSolvableWordPolynomial<C> Cc = ring.valueOf(c); //new RecSolvableWordPolynomial<C>(ring, c, ring.evzero);
        return Cb.multiply(this).multiply(Cc);
    }


    /*
     * RecSolvableWordPolynomial multiplication. Product with coefficient ring
     * element.
     * @param b coefficient of coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    //@Override not possible, @NoOverride
    //public RecSolvableWordPolynomial<C> multiply(C b) { ... }


    /**
     * RecSolvableWordPolynomial multiplication. Product with exponent vector.
     *
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * RecSolvableWordPolynomial left and right multiplication. Product with
     * exponent vector.
     *
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     * multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        GenWordPolynomial<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * RecSolvableWordPolynomial multiplication. Product with ring element and
     * exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(GenWordPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvableWordPolynomial<C> Cp = ring.valueOf(b, e); //new RecSolvableWordPolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * RecSolvableWordPolynomial left and right multiplication. Product with
     * ring element and exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     * solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(GenWordPolynomial<C> b, ExpVector e, GenWordPolynomial<C> c,
                                                 ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        RecSolvableWordPolynomial<C> Cp = ring.valueOf(b, e); //new RecSolvableWordPolynomial<C>(ring, b, e);
        RecSolvableWordPolynomial<C> Dp = ring.valueOf(c, f); //new RecSolvableWordPolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * RecSolvableWordPolynomial multiplication. Left product with ring element
     * and exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiplyLeft(GenWordPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvableWordPolynomial<C> Cp = ring.valueOf(b, e); //new RecSolvableWordPolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvableWordPolynomial multiplication. Left product with exponent
     * vector.
     *
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        //GenWordPolynomial<C> b = ring.getONECoefficient();
        RecSolvableWordPolynomial<C> Cp = ring.valueOf(e); //new RecSolvableWordPolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvableWordPolynomial multiplication. Left product with coefficient
     * ring element.
     *
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiplyLeft(GenWordPolynomial<C> b) {
        RecSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, GenWordPolynomial<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, GenWordPolynomial<C>> Am = val;
        GenWordPolynomial<C> c;
        for (Map.Entry<ExpVector, GenWordPolynomial<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            GenWordPolynomial<C> a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * RecSolvableWordPolynomial multiplication. Left product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiplyLeft(Map.Entry<ExpVector, GenWordPolynomial<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * RecSolvableWordPolynomial multiplication. Product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvableWordPolynomial<C> multiply(Map.Entry<ExpVector, GenWordPolynomial<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * RecSolvableWordPolynomial multiplication. Commutative product with
     * exponent vector.
     *
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected RecSolvableWordPolynomial<C> shift(ExpVector f) {
        RecSolvableWordPolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, GenWordPolynomial<C>> Cm = C.val;
        Map<ExpVector, GenWordPolynomial<C>> Bm = this.val;
        for (Map.Entry<ExpVector, GenWordPolynomial<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            GenWordPolynomial<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
