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
 * RecSolvablePolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. Objects of this class are intended to be immutable. The
 * implementation is based on TreeMap respectively SortedMap from exponents to
 * coefficients by extension of GenPolynomial.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RecSolvablePolynomial<C extends RingElem<C>> extends GenSolvablePolynomial<GenPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(RecSolvablePolynomial.class);
    private static final boolean debug = logger.isDebugEnabled();
    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final RecSolvablePolynomialRing<C> ring;


    /**
     * Constructor for zero RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param e exponent.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, ExpVector e) {
        this(r);
        val.put(e, ring.getONECoefficient());
    }


    /**
     * Constructor for RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, GenPolynomial<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, GenPolynomial<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, GenSolvablePolynomial<GenPolynomial<C>> S) {
        this(r, S.val);
    }


    /**
     * Constructor for RecSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, SortedMap<ExpVector, GenPolynomial<C>> v) {
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
    public RecSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this RecSolvablePolynomial.
     *
     * @see Object#clone()
     */
    @Override
    public RecSolvablePolynomial<C> copy() {
        return new RecSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof RecSolvablePolynomial)) {
            return false;
        }
        // compare also coeffTable?
        return super.equals(B);
    }


    /**
     * RecSolvablePolynomial multiplication.
     *
     * @param Bp RecSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // cannot @Override, @NoOverride
    public RecSolvablePolynomial<C> multiply(RecSolvablePolynomial<C> Bp) {
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
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) ring.coFac;
        RecSolvablePolynomial<C> Dp = ring.getZERO().copy();
        ExpVector Z = ring.evzero;
        ExpVector Zc = cfac.evzero;
        GenPolynomial<C> one = ring.getONECoefficient();

        RecSolvablePolynomial<C> C1 = null;
        RecSolvablePolynomial<C> C2 = null;
        Map<ExpVector, GenPolynomial<C>> A = val;
        Map<ExpVector, GenPolynomial<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, GenPolynomial<C>>> Bk = B.entrySet();
        if (debug)
            logger.info("input A = " + this);
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.entrySet()) {
            GenPolynomial<C> a = y.getValue();
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
            for (Map.Entry<ExpVector, GenPolynomial<C>> x : Bk) {
                GenPolynomial<C> b = x.getValue();
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
                RecSolvablePolynomial<C> Cps = ring.getZERO().copy();
                RecSolvablePolynomial<C> Cs = null;
                if (commuteCoeff || b.isConstant() || e.isZERO()) { // symmetric
                    Cps.doAddTo(b, e);
                    if (debug)
                        logger.info("symmetric coeff, e*b: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff, e*b: b = " + b + ", e = " + e);
                    for (Map.Entry<ExpVector, C> z : b.val.entrySet()) {
                        C c = z.getValue();
                        GenPolynomial<C> cc = b.ring.valueOf(c);
                        ExpVector g = z.getKey();
                        if (debug)
                            logger.info("g = " + g + ", c = " + c);
                        int[] gp = g.dependencyOnVariables();
                        int gl1 = 0;
                        if (gp.length > 0) {
                            gl1 = gp[gp.length - 1];
                        }
                        int gl1s = b.ring.nvar + 1 - gl1;
                        if (debug) {
                            logger.info("gl1s = " + gl1s);
                        }
                        // split e = e1 * e2, g = g2 * g1 (= g1 * g2)
                        ExpVector e1 = e;
                        ExpVector e2 = Z;
                        if (!e.isZERO()) {
                            e1 = e.subst(el1, 0);
                            e2 = Z.subst(el1, e.getVal(el1));
                        }
                        ExpVector e4;
                        ExpVector g1 = g;
                        ExpVector g2 = Zc;
                        if (!g.isZERO()) {
                            g1 = g.subst(gl1, 0);
                            g2 = Zc.subst(gl1, g.getVal(gl1));
                        }
                        if (debug) {
                            logger.info("coeff, e1 = " + e1 + ", e2 = " + e2 + ", Cps = " + Cps);
                            logger.info("coeff, g1 = " + g1 + ", g2 = " + g2);
                        }
                        TableRelation<GenPolynomial<C>> crel = ring.coeffTable.lookup(e2, g2);
                        if (debug)
                            logger.info("coeff, crel = " + crel.p);
                        //logger.info("coeff, e  = " + e + " g, = " + g + ", crel = " + crel);
                        Cs = new RecSolvablePolynomial<C>(ring, crel.p);
                        // rest of multiplication and update relations
                        if (crel.f != null) { // process remaining right power
                            GenPolynomial<C> c2 = b.ring.valueOf(crel.f);
                            C2 = new RecSolvablePolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                            if (crel.e == null) {
                                e4 = e2;
                            } else {
                                e4 = e2.subtract(crel.e);
                            }
                            ring.coeffTable.update(e4, g2, Cs);
                        }
                        if (crel.e != null) { // process remaining left power
                            C1 = new RecSolvablePolynomial<C>(ring, one, crel.e);
                            Cs = C1.multiply(Cs);
                            ring.coeffTable.update(e2, g2, Cs);
                        }
                        if (!g1.isZERO()) { // process remaining right part
                            GenPolynomial<C> c2 = b.ring.valueOf(g1);
                            C2 = new RecSolvablePolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                        }
                        if (!e1.isZERO()) { // process remaining left part
                            C1 = new RecSolvablePolynomial<C>(ring, one, e1);
                            Cs = C1.multiply(Cs);
                        }
                        //System.out.println("e1*Cs*g1 = " + Cs);
                        Cs = Cs.multiplyLeft(cc); // assume c, coeff(cc) commutes with Cs
                        //Cps = (RecSolvablePolynomial<C>) Cps.sum(Cs);
                        Cps.doAddTo(Cs);
                    } // end b loop 
                    if (debug)
                        logger.info("coeff, Cs = " + Cs + ", Cps = " + Cps);
                }
                if (debug)
                    logger.info("coeff-poly: Cps = " + Cps);
                // polynomial multiplication P_eb*f, for a*(P_eb*f)
                RecSolvablePolynomial<C> Dps = ring.getZERO().copy();
                RecSolvablePolynomial<C> Ds = null;
                RecSolvablePolynomial<C> D1, D2;
                if (commute || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly, P_eb*f: Cps = " + Cps + ", f = " + f);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new RecSolvablePolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly, P_eb*f: Cps = " + Cps + ", f = " + f);
                    for (Map.Entry<ExpVector, GenPolynomial<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        GenPolynomial<C> c = z.getValue();
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
                            Ds = ring.valueOf(h); // symmetric! 
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
                            TableRelation<GenPolynomial<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new RecSolvablePolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = ring.valueOf(rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = ring.valueOf(rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = ring.valueOf(f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = ring.valueOf(g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // assume c commutes with Cs
                        Dps.doAddTo(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                if (debug) {
                    logger.info("recursion+: Ds = " + Ds + ", a = " + a);
                }
                // polynomial coefficient multiplication a*(P_eb*f) = a*Ds
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.info("recursion-: Ds = " + Ds);
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
     * RecSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     *
     * @param S RecSolvablePolynomial.
     * @param T RecSolvablePolynomial.
     * @return S*this*T.
     */
    // cannot @Override, @NoOverride
    public RecSolvablePolynomial<C> multiply(RecSolvablePolynomial<C> S, RecSolvablePolynomial<C> T) {
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
     * RecSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     *
     * @param b coefficient polynomial.
     * @return this*b, where * is coefficient multiplication.
     */
    //todo @Override, @NoOverride
    //public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b) {
    //public GenSolvablePolynomial<GenPolynomial<C>> multiply(GenPolynomial<C> b) {
    public RecSolvablePolynomial<C> recMultiply(GenPolynomial<C> b) {
        RecSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Cp = new RecSolvablePolynomial<C>(ring, b, ring.evzero);
        return multiply(Cp);
        // wrong:
        // Map<ExpVector, GenPolynomial<C>> Cm = Cp.val; //getMap();
        // Map<ExpVector, GenPolynomial<C>> Am = val;
        // for (Map.Entry<ExpVector, GenPolynomial<C>> y : Am.entrySet()) {
        //     ExpVector e = y.getKey();
        //     GenPolynomial<C> a = y.getValue();
        //     GenPolynomial<C> c = a.multiply(b);
        //     if (!c.isZERO()) {
        //         Cm.put(e, c);
        //     }
        // }
        // return Cp;
    }


    /**
     * RecSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     *
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b, GenPolynomial<C> c) {
        RecSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        RecSolvablePolynomial<C> Cb = ring.valueOf(b);
        RecSolvablePolynomial<C> Cc = ring.valueOf(c);
        return Cb.multiply(this).multiply(Cc);
        // wrong:
        // Map<ExpVector, GenPolynomial<C>> Cm = Cp.val; //getMap();
        // Map<ExpVector, GenPolynomial<C>> Am = val;
        // for (Map.Entry<ExpVector, GenPolynomial<C>> y : Am.entrySet()) {
        //     ExpVector e = y.getKey();
        //     GenPolynomial<C> a = y.getValue();
        //     GenPolynomial<C> d = b.multiply(a).multiply(c);
        //     if (!d.isZERO()) {
        //         Cm.put(e, d);
        //     }
        // }
        // return Cp;
    }


    /*
     * RecSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     * @param b coefficient of coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    //@Override not possible, @NoOverride
    //public RecSolvablePolynomial<C> multiply(C b) { ... }


    /**
     * RecSolvablePolynomial multiplication. Product with exponent vector.
     *
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        GenPolynomial<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * RecSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     *
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     * multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        GenPolynomial<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * RecSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = ring.valueOf(b, e);
        return multiply(Cp);
    }


    /**
     * RecSolvablePolynomial left and right multiplication. Product with ring
     * element and exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     * solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b, ExpVector e, GenPolynomial<C> c, ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = ring.valueOf(b, e);
        RecSolvablePolynomial<C> Dp = ring.valueOf(c, f);
        return multiply(Cp, Dp);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with ring element and
     * exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(GenPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = ring.valueOf(b, e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with exponent vector.
     *
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        RecSolvablePolynomial<C> Cp = ring.valueOf(e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
     *
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(GenPolynomial<C> b) {
        RecSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        GenSolvablePolynomial<C> bb = null;
        if (b instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.debug("warn: wrong method dispatch in JRE multiply(b) - trying to fix");
            bb = (GenSolvablePolynomial<C>) b;
        }
        Map<ExpVector, GenPolynomial<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, GenPolynomial<C>> Am = val;
        GenPolynomial<C> c;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            if (bb != null) {
                GenSolvablePolynomial<C> aa = (GenSolvablePolynomial<C>) a;
                c = bb.multiply(aa);
            } else {
                c = b.multiply(a);
            }
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector, GenPolynomial<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * RecSolvablePolynomial multiplication. Product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(Map.Entry<ExpVector, GenPolynomial<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * RecSolvablePolynomial multiplication. Commutative product with exponent
     * vector.
     *
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    public RecSolvablePolynomial<C> shift(ExpVector f) {
        RecSolvablePolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, GenPolynomial<C>> Cm = C.val;
        Map<ExpVector, GenPolynomial<C>> Bm = this.val;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }


    /**
     * RecSolvablePolynomial multiplication. Commutative product with
     * coefficient.
     *
     * @param b coefficient.
     * @return B*b, where * is commutative multiplication with respect to main variables.
     */
    public RecSolvablePolynomial<C> multiplyRightComm(GenPolynomial<C> b) {
        RecSolvablePolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (b == null || b.isZERO()) {
            return this;
        }
        Map<ExpVector, GenPolynomial<C>> Cm = C.val;
        Map<ExpVector, GenPolynomial<C>> Bm = this.val;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            a = a.multiply(b);
            if (!a.isZERO()) {
                Cm.put(e, a);
            }
        }
        return C;
    }


    /**
     * RecSolvablePolynomial right coefficients from left coefficients.
     * <b>Note:</b> R is represented as a polynomial with left coefficients, the
     * implementation can at the moment not distinguish between left and right
     * coefficients.
     *
     * @return R = sum( X<sup>i</sup> b<sub>i</sub> ), with this =
     * sum(a<sub>i</sub> X<sup>i</sup> ) and eval(sum(X<sup>i</sup>
     * b<sub>i</sub>)) == sum(a<sub>i</sub> X<sup>i</sup>)
     */
    @SuppressWarnings("cast")
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursivePolynomial() {
        if (this.isZERO()) {
            return this;
        }
        if (!(this instanceof RecSolvablePolynomial)) {
            return this;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) ring;
        if (rfac.coeffTable.isEmpty()) {
            return this;
        }
        RecSolvablePolynomial<C> R = rfac.getZERO().copy();
        RecSolvablePolynomial<C> p = this;
        RecSolvablePolynomial<C> r;
        while (!p.isZERO()) {
            ExpVector f = p.leadingExpVector();
            GenPolynomial<C> a = p.leadingBaseCoefficient();
            //r = h.multiply(a); // wrong method dispatch // right: f*a
            //okay: r = onep.multiply(one, f, a, zero); // right: (1 f) * 1 * (a zero)
            r = rfac.valueOf(f).multiply(rfac.valueOf(a)); // right: (1 f) * 1 * (a zero)
            //System.out.println("a,f = " + a + ", " + f); // + ", h.ring = " + h.ring.toScript());
            //System.out.println("f*a = " + r); // + ", r.ring = " + r.ring.toScript());
            p = (RecSolvablePolynomial<C>) p.subtract(r);
            R = (RecSolvablePolynomial<C>) R.sum(a, f);
            //R.doPutToMap(f, a);
        }
        return R;
    }


    /**
     * Evaluate RecSolvablePolynomial as right coefficients polynomial.
     * <b>Note:</b> R is represented as a polynomial with left coefficients, the
     * implementation can at the moment not distinguish between left and right
     * coefficients.
     *
     * @return this as evaluated polynomial R. R = sum( X<sup>i</sup>
     * b<sub>i</sub> ), this = sum(a<sub>i</sub> X<sup>i</sup> ) =
     * eval(sum(X<sup>i</sup> b<sub>i</sub>))
     */
    @SuppressWarnings("cast")
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> evalAsRightRecursivePolynomial() {
        if (this.isONE() || this.isZERO()) {
            return this;
        }
        if (!(this instanceof RecSolvablePolynomial)) {
            return this;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) ring;
        if (rfac.coeffTable.isEmpty()) {
            return this;
        }
        RecSolvablePolynomial<C> q = rfac.getZERO();
        RecSolvablePolynomial<C> s;
        RecSolvablePolynomial<C> r = (RecSolvablePolynomial<C>) this;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : r.getMap().entrySet()) {
            ExpVector f = y.getKey();
            GenPolynomial<C> a = y.getValue();
            // f.multiply(a); // wrong method dispatch // right: f*a
            // onep.multiply(f).multiply(a) // should do now
            //okay: s = onep.multiply(one, f, a, zero); // right: (1 f) * 1 * (a zero)
            s = rfac.valueOf(f).multiply(rfac.valueOf(a)); // right: (1 f) * 1 * (a zero)
            q = (RecSolvablePolynomial<C>) q.sum(s);
        }
        return q;
    }


    /**
     * Test RecSolvablePolynomial right coefficients polynomial. <b>Note:</b> R
     * is represented as a polynomial with left coefficients, the implementation
     * can at the moment not distinguish between left and right coefficients.
     *
     * @param R GenSolvablePolynomial with right coefficients.
     * @return true, if R is polynomial with right coefficients of this. R =
     * sum( X<sup>i</sup> b<sub>i</sub> ), with this = sum(a<sub>i</sub>
     * X<sup>i</sup> ) and eval(sum(X<sup>i</sup> b<sub>i</sub>)) ==
     * sum(a<sub>i</sub> X<sup>i</sup>)
     */
    @SuppressWarnings("cast")
    @Override
    public boolean isRightRecursivePolynomial(GenSolvablePolynomial<GenPolynomial<C>> R) {
        if (this.isZERO()) {
            return R.isZERO();
        }
        if (this.isONE()) {
            return R.isONE();
        }
        if (!(this instanceof RecSolvablePolynomial)) {
            return !(R instanceof RecSolvablePolynomial);
        }
        if (!(R instanceof RecSolvablePolynomial)) {
            return false;
        }
        RecSolvablePolynomialRing<C> rfac = (RecSolvablePolynomialRing<C>) ring;
        if (rfac.coeffTable.isEmpty()) {
            RecSolvablePolynomialRing<C> rf = (RecSolvablePolynomialRing<C>) R.ring;
            return rf.coeffTable.isEmpty();
        }
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) this;
        RecSolvablePolynomial<C> q = (RecSolvablePolynomial<C>) R.evalAsRightRecursivePolynomial();
        p = (RecSolvablePolynomial<C>) PolyUtil.<C>monic(p);
        q = (RecSolvablePolynomial<C>) PolyUtil.<C>monic(q);
        return p.equals(q);
    }

}
