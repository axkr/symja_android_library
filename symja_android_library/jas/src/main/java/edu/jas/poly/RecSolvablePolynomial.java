/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * RecSolvablePolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. Objects of this class are intended to be immutable. The
 * implementation is based on TreeMap respectively SortedMap from exponents to
 * coefficients by extension of GenPolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RecSolvablePolynomial<C extends RingElem<C>> extends GenSolvablePolynomial<GenPolynomial<C>> {


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final RecSolvablePolynomialRing<C> ring;


    private static final Logger logger = Logger.getLogger(RecSolvablePolynomial.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for zero RecSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for RecSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param e exponent.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, ExpVector e) {
        this(r);
        val.put(e, ring.getONECoefficient());
    }


    /**
     * Constructor for RecSolvablePolynomial.
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
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, GenPolynomial<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for RecSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, GenSolvablePolynomial<GenPolynomial<C>> S) {
        this(r, S.val);
    }


    /**
     * Constructor for RecSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected RecSolvablePolynomial(RecSolvablePolynomialRing<C> r, SortedMap<ExpVector, GenPolynomial<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public RecSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this RecSolvablePolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
    public RecSolvablePolynomial<C> copy() {
        return new RecSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof RecSolvablePolynomial)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * RecSolvablePolynomial multiplication.
     * @param Bp RecSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    public RecSolvablePolynomial<C> multiply(RecSolvablePolynomial<C> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.nvar == Bp.ring.nvar);
        if (debug) {
            logger.debug("ring = " + ring);
        }
        ExpVector Z = ring.evzero;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) ring.coFac;
        ExpVector Zc = cfac.evzero;
        RecSolvablePolynomial<C> Dp = ring.getZERO().copy();
        RecSolvablePolynomial<C> zero = ring.getZERO().copy();
        GenPolynomial<C> one = ring.getONECoefficient();

        RecSolvablePolynomial<C> C1 = null;
        RecSolvablePolynomial<C> C2 = null;
        Map<ExpVector, GenPolynomial<C>> A = val;
        Map<ExpVector, GenPolynomial<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, GenPolynomial<C>>> Bk = B.entrySet();
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
                // polynomial coefficient multiplication 
                RecSolvablePolynomial<C> Cps = ring.getZERO().copy();
                RecSolvablePolynomial<C> Cs = null;
                if (ring.coeffTable.isEmpty() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new RecSolvablePolynomial<C>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = " + b + ", e = " + e);
                    for (Map.Entry<ExpVector, C> z : b.val.entrySet()) {
                        C c = z.getValue();
                        GenPolynomial<C> cc = b.ring.getONE().multiply(c);
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
                        // split e = e1 * e2, g = g1 * g2
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
                        if (debug)
                            logger.info("coeff, e1 = " + e1 + ", e2 = " + e2 + ", Cps = " + Cps);
                        if (debug)
                            logger.info("coeff, g1 = " + g1 + ", g2 = " + g2);
                        TableRelation<GenPolynomial<C>> crel = ring.coeffTable.lookup(e2, g2);
                        if (debug)
                            logger.info("coeff, crel = " + crel.p);
                        //logger.info("coeff, e  = " + e + " g, = " + g + ", crel = " + crel);
                        Cs = new RecSolvablePolynomial<C>(ring, crel.p);
                        // rest of multiplication and update relations
                        if (crel.f != null) {
                            GenPolynomial<C> c2 = b.ring.getONE().multiply(crel.f);
                            C2 = new RecSolvablePolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                            if (crel.e == null) {
                                e4 = e2;
                            } else {
                                e4 = e2.subtract(crel.e);
                            }
                            ring.coeffTable.update(e4, g2, Cs);
                        }
                        if (crel.e != null) { // process left part
                            C1 = new RecSolvablePolynomial<C>(ring, one, crel.e);
                            Cs = C1.multiply(Cs);
                            ring.coeffTable.update(e2, g2, Cs);
                        }
                        if (!g1.isZERO()) { // process right part
                            GenPolynomial<C> c2 = b.ring.getONE().multiply(g1);
                            C2 = new RecSolvablePolynomial<C>(ring, c2, Z);
                            Cs = Cs.multiply(C2);
                        }
                        if (!e1.isZERO()) { // process left part
                            C1 = new RecSolvablePolynomial<C>(ring, one, e1);
                            Cs = C1.multiply(Cs);
                        }
                        //System.out.println("e1*Cs*g1 = " + Cs);
                        Cs = Cs.multiplyLeft(cc); // assume c, coeff(cc) commutes with Cs
                        Cps = (RecSolvablePolynomial<C>) Cps.sum(Cs);
                    } // end b loop 
                    if (debug)
                        logger.info("coeff, Cs = " + Cs + ", Cps = " + Cps);
                }
                if (debug)
                    logger.info("coeff-poly: Cps = " + Cps);
                // polynomial multiplication 
                RecSolvablePolynomial<C> Dps = ring.getZERO().copy();
                RecSolvablePolynomial<C> Ds = null;
                RecSolvablePolynomial<C> D1, D2;
                if (ring.table.isEmpty() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = " + b + ", e = " + e);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new RecSolvablePolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = shift(Cps, f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = " + Cps + ", f = " + f);
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
                            Ds = (RecSolvablePolynomial<C>) zero.sum(one, h); // symmetric!
                        } else {
                            ExpVector g1 = g.subst(gl1, 0);
                            ExpVector g2 = Z.subst(gl1, g.getVal(el1));
                            ExpVector g4;
                            ExpVector f1 = f.subst(fl1, 0);
                            ExpVector f2 = Z.subst(fl1, f.getVal(fl1));
                            if (debug)
                                logger.info("poly, g1 = " + g1 + ", f1 = " + f1 + ", Dps = " + Dps);
                            if (debug)
                                logger.info("poly, g2 = " + g2 + ", f2 = " + f2);
                            TableRelation<GenPolynomial<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new RecSolvablePolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new RecSolvablePolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new RecSolvablePolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new RecSolvablePolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new RecSolvablePolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // assume c commutes with Cs
                        Dps = (RecSolvablePolynomial<C>) Dps.sum(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.debug("Ds = " + Ds);
                Dp = (RecSolvablePolynomial<C>) Dp.sum(Ds);
            } // end B loop
        } // end A loop
        return Dp;
    }


    /**
     * RecSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     * @param S RecSolvablePolynomial.
     * @param T RecSolvablePolynomial.
     * @return S*this*T.
     */
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
     * @param b coefficient polynomial.
     * @return this*b, where * is coefficient multiplication.
     */
    //todo Override
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
        RecSolvablePolynomial<C> Cb = new RecSolvablePolynomial<C>(ring, b, ring.evzero);
        RecSolvablePolynomial<C> Cc = new RecSolvablePolynomial<C>(ring, c, ring.evzero);
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


    /**
     * RecSolvablePolynomial multiplication. Product with exponent vector.
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
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     *         multiplication.
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
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = new RecSolvablePolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * RecSolvablePolynomial left and right multiplication. Product with ring
     * element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     *         solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiply(GenPolynomial<C> b, ExpVector e, GenPolynomial<C> c, ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = new RecSolvablePolynomial<C>(ring, b, e);
        RecSolvablePolynomial<C> Dp = new RecSolvablePolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with ring element and
     * exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(GenPolynomial<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        RecSolvablePolynomial<C> Cp = new RecSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with exponent vector.
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public RecSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        GenPolynomial<C> b = ring.getONECoefficient();
        RecSolvablePolynomial<C> Cp = new RecSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * RecSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
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


    /*
     * RecSolvablePolynomial multiplication. 
     * Left product with coefficient ring element.
     * @param B solvable polynomial.
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected RecSolvablePolynomial<C> shift(RecSolvablePolynomial<C> B, ExpVector f) {
        RecSolvablePolynomial<C> C = ring.getZERO().copy();
        if (B == null || B.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return B;
        }
        Map<ExpVector, GenPolynomial<C>> Cm = C.val;
        Map<ExpVector, GenPolynomial<C>> Bm = B.val;
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

}
