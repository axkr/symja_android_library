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
import edu.jas.poly.RecSolvableWordPolynomial;
import edu.jas.poly.TableRelation;
import edu.jas.structure.GcdRingElem;


/**
 * ResidueSolvableWordPolynomial solvable polynomials with WordResidue
 * coefficients implementing RingElem. n-variate ordered solvable polynomials
 * over non-commutative word residue coefficients. Objects of this class are
 * intended to be immutable. The implementation is based on TreeMap respectively
 * SortedMap from exponents to coefficients by extension of GenPolynomial.
 * Will eventually be deprecated.
 * @param <C> base coefficient type
 * @author Heinz Kredel
 */

public class ResidueSolvableWordPolynomial<C extends GcdRingElem<C>> extends
                GenSolvablePolynomial<WordResidue<C>> {


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final ResidueSolvableWordPolynomialRing<C> ring;


    private static final Logger logger = LogManager.getLogger(ResidueSolvableWordPolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for zero ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     */
    public ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     * @param e exponent.
     */
    public ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r, ExpVector e) {
        this(r);
        val.put(e, ring.getONECoefficient());
    }


    /**
     * Constructor for ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient word residue.
     * @param e exponent.
     */
    public ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r, WordResidue<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient word residue.
     */
    public ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r, WordResidue<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r,
                    GenSolvablePolynomial<WordResidue<C>> S) {
        this(r, S.getMap());
    }


    /**
     * Constructor for ResidueSolvableWordPolynomial.
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected ResidueSolvableWordPolynomial(ResidueSolvableWordPolynomialRing<C> r,
                    SortedMap<ExpVector, WordResidue<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public ResidueSolvableWordPolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this ResidueSolvableWordPolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
    public ResidueSolvableWordPolynomial<C> copy() {
        return new ResidueSolvableWordPolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof ResidueSolvableWordPolynomial)) {
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
     * ResidueSolvableWordPolynomial multiplication.
     * @param Bp ResidueSolvableWordPolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // cannot @Override, @NoOverride
    public ResidueSolvableWordPolynomial<C> multiply(ResidueSolvableWordPolynomial<C> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        assert (ring.nvar == Bp.ring.nvar);
        logger.debug("ring = {}", ring);
        ExpVector Z = ring.evzero;
        ResidueSolvableWordPolynomial<C> Dp = ring.getZERO().copy();
        ResidueSolvableWordPolynomial<C> zero = ring.getZERO().copy();
        WordResidue<C> one = ring.getONECoefficient();

        Map<ExpVector, WordResidue<C>> A = val;
        Map<ExpVector, WordResidue<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, WordResidue<C>>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, WordResidue<C>> y : A.entrySet()) {
            WordResidue<C> a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = {}, a = {}", e, a);
            for (Map.Entry<ExpVector, WordResidue<C>> x : Bk) {
                WordResidue<C> b = x.getValue();
                ExpVector f = x.getKey();
                if (debug)
                    logger.info("f = {}, b = {}", f, b);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0;
                if (fp.length > 0) {
                    fl1 = fp[fp.length - 1];
                }
                int fl1s = ring.nvar + 1 - fl1;
                // polynomial/residue coefficient multiplication 
                ResidueSolvableWordPolynomial<C> Cps = ring.getZERO().copy();
                if (ring.polCoeff.coeffTable.isEmpty() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new ResidueSolvableWordPolynomial<C>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = {}, e = {}", b, e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = {}, e = {}", b, e);
                    // recursive polynomial coefficient multiplication : e * b.val
                    RecSolvableWordPolynomial<C> rsp1 = new RecSolvableWordPolynomial<C>(ring.polCoeff, e);
                    RecSolvableWordPolynomial<C> rsp2 = new RecSolvableWordPolynomial<C>(ring.polCoeff, b.val);
                    RecSolvableWordPolynomial<C> rsp3 = rsp1.multiply(rsp2);
                    Cps = ring.fromPolyCoefficients(rsp3);
                }
                if (debug) {
                    logger.info("coeff-poly: Cps = {}", Cps);
                }
                // polynomial multiplication 
                ResidueSolvableWordPolynomial<C> Dps = ring.getZERO().copy();
                ResidueSolvableWordPolynomial<C> Ds = null;
                ResidueSolvableWordPolynomial<C> D1, D2;
                if (ring.table.isEmpty() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = {}, e = {}", b, e);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new ResidueSolvableWordPolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = {}, f = {}", Cps, f);
                    for (Map.Entry<ExpVector, WordResidue<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        WordResidue<C> c = z.getValue();
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
                            Ds = (ResidueSolvableWordPolynomial<C>) zero.sum(one, h); // symmetric!
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
                            TableRelation<WordResidue<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = {}, f  = {}, rel = {}", g, f, rel);
                            Ds = new ResidueSolvableWordPolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new ResidueSolvableWordPolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new ResidueSolvableWordPolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new ResidueSolvableWordPolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new ResidueSolvableWordPolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // assume c commutes with Cs
                        Dps = (ResidueSolvableWordPolynomial<C>) Dps.sum(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                logger.debug("Ds = {}", Ds);
                Dp = (ResidueSolvableWordPolynomial<C>) Dp.sum(Ds);
            } // end B loop
        } // end A loop
        return Dp;
    }


    /**
     * ResidueSolvableWordPolynomial left and right multiplication. Product with
     * two polynomials.
     * @param S ResidueSolvableWordPolynomial.
     * @param T ResidueSolvableWordPolynomial.
     * @return S*this*T.
     */
    // cannot @Override, @NoOverride
    public ResidueSolvableWordPolynomial<C> multiply(ResidueSolvableWordPolynomial<C> S,
                    ResidueSolvableWordPolynomial<C> T) {
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
     * ResidueSolvableWordPolynomial multiplication. Product with coefficient
     * ring element.
     * @param b coefficient polynomial.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    //public GenSolvablePolynomial<WordResidue<C>> multiply(WordResidue<C> b) {
    public ResidueSolvableWordPolynomial<C> multiply(WordResidue<C> b) {
        ResidueSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Cp = ring.valueOf(b);
        return multiply(Cp);
    }


    /**
     * ResidueSolvableWordPolynomial left and right multiplication. Product with
     * coefficient ring element.
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(WordResidue<C> b, WordResidue<C> c) {
        ResidueSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        ResidueSolvableWordPolynomial<C> Cb = ring.valueOf(b);
        ResidueSolvableWordPolynomial<C> Cc = ring.valueOf(c);
        return Cb.multiply(this).multiply(Cc);
    }


    /*
     * ResidueSolvableWordPolynomial multiplication. Product with coefficient ring
     * element.
     * @param b coefficient of coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    //@Override not possible, @NoOverride
    //public ResidueSolvableWordPolynomial<C> multiply(C b) { ... }


    /**
     * ResidueSolvableWordPolynomial multiplication. Product with exponent
     * vector.
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        WordResidue<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * ResidueSolvableWordPolynomial left and right multiplication. Product with
     * exponent vector.
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     *         multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        WordResidue<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Product with ring element
     * and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(WordResidue<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvableWordPolynomial<C> Cp = ring.valueOf(b, e);
        return multiply(Cp);
    }


    /**
     * ResidueSolvableWordPolynomial left and right multiplication. Product with
     * ring element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     *         solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(WordResidue<C> b, ExpVector e, WordResidue<C> c,
                    ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvableWordPolynomial<C> Cp = ring.valueOf(b, e);
        ResidueSolvableWordPolynomial<C> Dp = ring.valueOf(c, f);
        return multiply(Cp, Dp);
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Left product with ring
     * element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiplyLeft(WordResidue<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        ResidueSolvableWordPolynomial<C> Cp = ring.valueOf(b, e);
        return Cp.multiply(this);
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Left product with exponent
     * vector.
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        ResidueSolvableWordPolynomial<C> Cp = ring.valueOf(e);
        return Cp.multiply(this);
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Left product with
     * coefficient ring element.
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiplyLeft(WordResidue<C> b) {
        ResidueSolvableWordPolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, WordResidue<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, WordResidue<C>> Am = val;
        WordResidue<C> c;
        for (Map.Entry<ExpVector, WordResidue<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            WordResidue<C> a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Left product with
     * 'monomial'.
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiplyLeft(Map.Entry<ExpVector, WordResidue<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public ResidueSolvableWordPolynomial<C> multiply(Map.Entry<ExpVector, WordResidue<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * ResidueSolvableWordPolynomial multiplication. Commutative product with
     * exponent vector.
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected ResidueSolvableWordPolynomial<C> shift(ExpVector f) {
        ResidueSolvableWordPolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, WordResidue<C>> Cm = C.val;
        Map<ExpVector, WordResidue<C>> Bm = this.val;
        for (Map.Entry<ExpVector, WordResidue<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            WordResidue<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
