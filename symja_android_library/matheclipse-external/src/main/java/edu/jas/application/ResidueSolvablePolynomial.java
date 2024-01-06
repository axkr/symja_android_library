/*
 * $Id$
 */

package edu.jas.application;


import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.TableRelation;
import edu.jas.structure.GcdRingElem;


/**
 * ResidueSolvablePolynomial generic solvable polynomials with solvable residue
 * coefficients implementing RingElem. n-variate ordered solvable polynomials
 * over solvable residue coefficients. Objects of this class are intended to be
 * immutable. The implementation is based on TreeMap respectively SortedMap from
 * exponents to coefficients by extension of GenPolynomial.
 * Will eventually be deprecated use QLRSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ResidueSolvablePolynomial<C extends GcdRingElem<C>> extends
                GenSolvablePolynomial<SolvableResidue<C>> {


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final ResidueSolvablePolynomialRing<C> ring;


    private static final Logger logger = LogManager.getLogger(ResidueSolvablePolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for zero ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     */
    public ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param e exponent.
     */
    public ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r, ExpVector e) {
        this(r);
        val.put(e, ring.getONECoefficient());
    }


    /**
     * Constructor for ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r, SolvableResidue<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r, SolvableResidue<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r,
                    GenSolvablePolynomial<SolvableResidue<C>> S) {
        this(r, S.getMap());
    }


    /**
     * Constructor for ResidueSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected ResidueSolvablePolynomial(ResidueSolvablePolynomialRing<C> r,
                    SortedMap<ExpVector, SolvableResidue<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public ResidueSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this ResidueSolvablePolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
    public ResidueSolvablePolynomial<C> copy() {
        return new ResidueSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof ResidueSolvablePolynomial)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * Hash code for this polynomial.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * ResidueSolvablePolynomial multiplication.
     * @param Bp ResidueSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    public ResidueSolvablePolynomial<C> multiply(ResidueSolvablePolynomial<C> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.nvar == Bp.ring.nvar);
        logger.debug("ring = {}", ring);
        ExpVector Z = ring.evzero;
        ResidueSolvablePolynomial<C> Dp = ring.getZERO().copy();
        ResidueSolvablePolynomial<C> zero = ring.getZERO().copy();
        SolvableResidue<C> one = ring.getONECoefficient();

        //ResidueSolvablePolynomial<C> C1 = null;
        //ResidueSolvablePolynomial<C> C2 = null;
        Map<ExpVector, SolvableResidue<C>> A = val;
        Map<ExpVector, SolvableResidue<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, SolvableResidue<C>>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, SolvableResidue<C>> y : A.entrySet()) {
            SolvableResidue<C> a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = {}, a = {}", e, a);
            //int[] ep = e.dependencyOnVariables();
            //int el1 = ring.nvar + 1;
            //if (ep.length > 0) {
            //    el1 = ep[0];
            //}
            //int el1s = ring.nvar + 1 - el1;
            for (Map.Entry<ExpVector, SolvableResidue<C>> x : Bk) {
                SolvableResidue<C> b = x.getValue();
                ExpVector f = x.getKey();
                if (debug)
                    logger.info("f = {}, b = {}", f, b);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0;
                if (fp.length > 0) {
                    fl1 = fp[fp.length - 1];
                }
                int fl1s = ring.nvar + 1 - fl1;
                // polynomial coefficient multiplication 
                ResidueSolvablePolynomial<C> Cps = ring.getZERO().copy();
                //ResidueSolvablePolynomial<C> Cs = null;
                if (ring.polCoeff.coeffTable.isEmpty() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new ResidueSolvablePolynomial<C>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = {}, e = {}", b, e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = {}, e = {}", b, e);
                    // recursive polynomial coefficient multiplication : e * b.val
                    RecSolvablePolynomial<C> rsp1 = new RecSolvablePolynomial<C>(ring.polCoeff, e);
                    RecSolvablePolynomial<C> rsp2 = new RecSolvablePolynomial<C>(ring.polCoeff, b.val);
                    RecSolvablePolynomial<C> rsp3 = rsp1.multiply(rsp2);
                    Cps = ring.fromPolyCoefficients(rsp3);
                }
                if (debug) {
                    logger.info("coeff-poly: Cps = {}", Cps);
                }
                // polynomial multiplication 
                ResidueSolvablePolynomial<C> Dps = ring.getZERO().copy();
                ResidueSolvablePolynomial<C> Ds = null;
                ResidueSolvablePolynomial<C> D1, D2;
                if (ring.table.isEmpty() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = {}, e = {}", b, e);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new ResidueSolvablePolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = {}, f = {}", Cps, f);
                    for (Map.Entry<ExpVector, SolvableResidue<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        SolvableResidue<C> c = z.getValue();
                        ExpVector g = z.getKey();
                        if (debug)
                            logger.info("g = {}, c = {}", g, c);
                        int[] gp = g.dependencyOnVariables();
                        int gl1 = ring.nvar + 1;
                        if (gp.length > 0) {
                            gl1 = gp[0];
                        }
                        int gl1s = ring.nvar + 1 - gl1;
                        if (gl1s <= fl1s) { // symmetric
                            ExpVector h = g.sum(f);
                            if (debug)
                                logger.info("disjoint poly: g = {}, f = {}, h = {}", g, f, h);
                            Ds = (ResidueSolvablePolynomial<C>) zero.sum(one, h); // symmetric!
                        } else {
                            ExpVector g1 = g.subst(gl1, 0);
                            ExpVector g2 = Z.subst(gl1, g.getVal(gl1)); // bug el1, gl1
                            ExpVector g4;
                            ExpVector f1 = f.subst(fl1, 0);
                            ExpVector f2 = Z.subst(fl1, f.getVal(fl1));
                            if (debug)
                                logger.info("poly, g1 = {}, f1 = {}, Dps = {}", g1, f1, Dps);
                            if (debug)
                                logger.info("poly, g2 = {}, f2 = {}", g2, f2);
                            TableRelation<SolvableResidue<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = {}, f  = {}, rel = {}", g, f, rel);
                            Ds = new ResidueSolvablePolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new ResidueSolvablePolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new ResidueSolvablePolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new ResidueSolvablePolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new ResidueSolvablePolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // assume c commutes with Cs
                        Dps = (ResidueSolvablePolynomial<C>) Dps.sum(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                logger.debug("Ds = {}", Ds);
                Dp = (ResidueSolvablePolynomial<C>) Dp.sum(Ds);
            } // end B loop
        } // end A loop
        return Dp;
    }


    /**
     * ResidueSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     * @param S ResidueSolvablePolynomial.
     * @param T ResidueSolvablePolynomial.
     * @return S*this*T.
     */
    public ResidueSolvablePolynomial<C> multiply(ResidueSolvablePolynomial<C> S,
                    ResidueSolvablePolynomial<C> T) {
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
     * ResidueSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     * @param b coefficient polynomial.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(SolvableResidue<C> b) {
        ResidueSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Cp = new ResidueSolvablePolynomial<C>(ring, b, ring.evzero);
        return multiply(Cp);
    }


    /**
     * ResidueSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(SolvableResidue<C> b, SolvableResidue<C> c) {
        ResidueSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        ResidueSolvablePolynomial<C> Cb = new ResidueSolvablePolynomial<C>(ring, b, ring.evzero);
        ResidueSolvablePolynomial<C> Cc = new ResidueSolvablePolynomial<C>(ring, c, ring.evzero);
        return Cb.multiply(this).multiply(Cc);
    }


    /**
     * ResidueSolvablePolynomial multiplication. Product with exponent vector.
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableResidue<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * ResidueSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     *         multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        SolvableResidue<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * ResidueSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(SolvableResidue<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvablePolynomial<C> Cp = new ResidueSolvablePolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * ResidueSolvablePolynomial left and right multiplication. Product with
     * ring element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     *         solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(SolvableResidue<C> b, ExpVector e, SolvableResidue<C> c,
                    ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvablePolynomial<C> Cp = new ResidueSolvablePolynomial<C>(ring, b, e);
        ResidueSolvablePolynomial<C> Dp = new ResidueSolvablePolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * ResidueSolvablePolynomial multiplication. Left product with ring element
     * and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiplyLeft(SolvableResidue<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvablePolynomial<C> Cp = new ResidueSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * ResidueSolvablePolynomial multiplication. Left product with exponent
     * vector.
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableResidue<C> b = ring.getONECoefficient();
        ResidueSolvablePolynomial<C> Cp = new ResidueSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * ResidueSolvablePolynomial multiplication. Left product with coefficient
     * ring element.
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiplyLeft(SolvableResidue<C> b) {
        ResidueSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, SolvableResidue<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, SolvableResidue<C>> Am = val;
        SolvableResidue<C> c;
        for (Map.Entry<ExpVector, SolvableResidue<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            SolvableResidue<C> a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * ResidueSolvablePolynomial multiplication. Left product with 'monomial'.
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector, SolvableResidue<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * ResidueSolvablePolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvablePolynomial<C> multiply(Map.Entry<ExpVector, SolvableResidue<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * ResidueSolvablePolynomial multiplication with exponent vector.
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected ResidueSolvablePolynomial<C> shift(ExpVector f) {
        ResidueSolvablePolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, SolvableResidue<C>> Cm = C.val;
        Map<ExpVector, SolvableResidue<C>> Bm = this.val;
        for (Map.Entry<ExpVector, SolvableResidue<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            SolvableResidue<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
