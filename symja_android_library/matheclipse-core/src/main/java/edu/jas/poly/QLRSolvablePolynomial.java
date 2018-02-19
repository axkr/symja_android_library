/*
 * $Id$
 */

package edu.jas.poly;


// todo: move to edu.jas.poly


import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.RingFactory;


/**
 * QLRSolvablePolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over solvable quotient,
 * local and local-residue coefficients. Objects of this class are intended to
 * be immutable. The implementation is based on TreeMap respectively SortedMap
 * from exponents to coefficients by extension of GenPolynomial.
 *
 * @param <C> polynomial coefficient type
 * @param <D> quotient coefficient type
 * @author Heinz Kredel
 */

public class QLRSolvablePolynomial<C extends GcdRingElem<C> & QuotPair<GenPolynomial<D>>, D extends GcdRingElem<D>>
        extends GenSolvablePolynomial<C> {


    private static final Logger logger = Logger.getLogger(QLRSolvablePolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final QLRSolvablePolynomialRing<C, D> ring;


    /**
     * Constructor for zero QLRSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     */
    public QLRSolvablePolynomial(QLRSolvablePolynomialRing<C, D> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for QLRSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public QLRSolvablePolynomial(QLRSolvablePolynomialRing<C, D> r, C c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for QLRSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public QLRSolvablePolynomial(QLRSolvablePolynomialRing<C, D> r, C c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for QLRSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public QLRSolvablePolynomial(QLRSolvablePolynomialRing<C, D> r, GenSolvablePolynomial<C> S) {
        this(r, S.getMap());
    }


    /**
     * Constructor for QLRSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected QLRSolvablePolynomial(QLRSolvablePolynomialRing<C, D> r, SortedMap<ExpVector, C> v) {
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
    public QLRSolvablePolynomialRing<C, D> factory() {
        return ring;
    }


    /**
     * Clone this QLRSolvablePolynomial.
     *
     * @see Object#clone()
     */
    @Override
    public QLRSolvablePolynomial<C, D> copy() {
        return new QLRSolvablePolynomial<C, D>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof QLRSolvablePolynomial)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * QLRSolvablePolynomial multiplication.
     *
     * @param Bp QLRSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // not @Override
    public QLRSolvablePolynomial<C, D> multiply(QLRSolvablePolynomial<C, D> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return ring.getZERO();
        }
        if (this.isZERO()) {
            return this;
        }
        if (Bp.isONE()) {
            return this;
        }
        if (this.isONE()) {
            return Bp;
        }
        assert (ring.nvar == Bp.ring.nvar);
        if (debug) {
            logger.debug("ring = " + ring);
        }
        //System.out.println("this = " + this + ", Bp = " + Bp);
        ExpVector Z = ring.evzero;
        QLRSolvablePolynomial<C, D> Dp = ring.getZERO().copy();
        QLRSolvablePolynomial<C, D> zero = ring.getZERO().copy();
        C one = ring.getONECoefficient();

        Map<ExpVector, C> A = val;
        Map<ExpVector, C> B = Bp.val;
        Set<Map.Entry<ExpVector, C>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            C a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = " + e + ", a = " + a);
            //int[] ep = e.dependencyOnVariables();
            //int el1 = ring.nvar + 1;
            //if (ep.length > 0) {
            //    el1 = ep[0];
            //}
            //int el1s = ring.nvar + 1 - el1;
            for (Map.Entry<ExpVector, C> x : Bk) {
                C b = x.getValue();
                ExpVector f = x.getKey();
                if (debug)
                    logger.info("f = " + f + ", b = " + b);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0;
                if (fp.length > 0) {
                    fl1 = fp[fp.length - 1];
                }
                int fl1s = ring.nvar + 1 - fl1;
                // polynomial with coefficient multiplication 
                QLRSolvablePolynomial<C, D> Cps = ring.getZERO().copy();
                //QLRSolvablePolynomial<C, D> Cs;
                QLRSolvablePolynomial<C, D> qp;
                if (ring.polCoeff.isCommutative() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new QLRSolvablePolynomial<C, D>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = " + b + ", e = " + e);
                    // compute e * b as ( e * 1/b.den ) * b.num
                    if (b.denominator().isONE()) { // recursion base
                        // recursive polynomial coefficient multiplication : e * b.num
                        RecSolvablePolynomial<D> rsp1 = new RecSolvablePolynomial<D>(ring.polCoeff, e);
                        RecSolvablePolynomial<D> rsp2 = new RecSolvablePolynomial<D>(ring.polCoeff,
                                b.numerator());
                        RecSolvablePolynomial<D> rsp3 = rsp1.multiply(rsp2);
                        QLRSolvablePolynomial<C, D> rsp = ring.fromPolyCoefficients(rsp3);
                        Cps = rsp;
                    } else { // b.denominator() != 1
                        if (debug)
                            logger.info("coeff-num: Cps = " + Cps + ", num = " + b.numerator() + ", den = "
                                    + b.denominator());
                        RingFactory<C> bfq = (RingFactory<C>) b.factory();
                        Cps = new QLRSolvablePolynomial<C, D>(ring, bfq.getONE(), e);

                        // coefficient multiplication with 1/den: 
                        QLRSolvablePolynomial<C, D> qv = Cps;
                        //C qden = new C(b.denominator().factory(), b.denominator()); // den/1
                        C qden = ring.qpfac.create(b.denominator()); // den/1
                        //System.out.println("qv = " + qv + ", den = " + den);
                        // recursion with den==1:
                        QLRSolvablePolynomial<C, D> v = qv.multiply(qden);
                        QLRSolvablePolynomial<C, D> vl = qv.multiplyLeft(qden);
                        //System.out.println("v = " + v + ", vl = " + vl + ", qden = " + qden);
                        QLRSolvablePolynomial<C, D> vr = (QLRSolvablePolynomial<C, D>) v.subtract(vl);
                        //C qdeni = new C(b.factory(), b.factory().getONE().numerator(), b.denominator());
                        C qdeni = ring.qpfac.create(ring.qpfac.pairFactory().getONE(), b.denominator()); // 1/den
                        //System.out.println("vr = " + vr + ", qdeni = " + qdeni);
                        // recursion with smaller head term:
                        if (qv.leadingExpVector().equals(vr.leadingExpVector())) {
                            throw new IllegalArgumentException("qr !> vr: qv = " + qv + ", vr = " + vr);
                        }
                        QLRSolvablePolynomial<C, D> rq = vr.multiply(qdeni);
                        qp = (QLRSolvablePolynomial<C, D>) qv.subtract(rq);
                        qp = qp.multiplyLeft(qdeni);
                        //System.out.println("qp_i = " + qp);
                        Cps = qp;

                        if (!b.numerator().isONE()) {
                            //C qnum = new C(b.denominator().factory(), b.numerator()); // num/1
                            C qnum = ring.qpfac.create(b.numerator()); // num/1
                            // recursion with den == 1:
                            Cps = Cps.multiply(qnum);
                        }
                    }
                } // end coeff
                if (debug)
                    logger.info("coeff-den: Cps = " + Cps);
                // polynomial multiplication 
                QLRSolvablePolynomial<C, D> Dps = ring.getZERO().copy();
                QLRSolvablePolynomial<C, D> Ds = null;
                QLRSolvablePolynomial<C, D> D1, D2;
                if (ring.isCommutative() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = " + b + ", e = " + e);
                    if (Cps.isConstant()) {
                        ExpVector g = e.sum(f);
                        Ds = new QLRSolvablePolynomial<C, D>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = " + Cps + ", f = " + f);
                    for (Map.Entry<ExpVector, C> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        C c = z.getValue();
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
                            Ds = (QLRSolvablePolynomial<C, D>) zero.sum(one, h); // symmetric!
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
                            TableRelation<C> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new QLRSolvablePolynomial<C, D>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new QLRSolvablePolynomial<C, D>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new QLRSolvablePolynomial<C, D>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new QLRSolvablePolynomial<C, D>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new QLRSolvablePolynomial<C, D>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // c * Ds
                        //Dps = (QLRSolvablePolynomial<C, D>) Dps.sum(Ds);
                        Dps.doAddTo(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.debug("Ds = " + Ds);
                //Dp = (QLRSolvablePolynomial<C, D>) Dp.sum(Ds);
                Dp.doAddTo(Ds);
            } // end B loop
        } // end A loop
        //System.out.println("this * Bp = " + Dp);
        return Dp;
    }


    /**
     * QLRSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     *
     * @param S QLRSolvablePolynomial.
     * @param T QLRSolvablePolynomial.
     * @return S*this*T.
     */
    // not @Override
    public QLRSolvablePolynomial<C, D> multiply(QLRSolvablePolynomial<C, D> S, QLRSolvablePolynomial<C, D> T) {
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
     * QLRSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     *
     * @param b solvable coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(C b) {
        QLRSolvablePolynomial<C, D> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (b.isONE()) {
            return this;
        }
        Cp = new QLRSolvablePolynomial<C, D>(ring, b, ring.evzero);
        return multiply(Cp);
    }


    /**
     * QLRSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     *
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(C b, C c) {
        QLRSolvablePolynomial<C, D> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        if (b.isONE() && c.isONE()) {
            return this;
        }
        Cp = new QLRSolvablePolynomial<C, D>(ring, b, ring.evzero);
        QLRSolvablePolynomial<C, D> Dp = new QLRSolvablePolynomial<C, D>(ring, c, ring.evzero);
        return multiply(Cp, Dp);
    }


    /**
     * QLRSolvablePolynomial multiplication. Product with exponent vector.
     *
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        C b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * QLRSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     *
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     * multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        C b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * QLRSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(C b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (b.isONE() && e.isZERO()) {
            return this;
        }
        QLRSolvablePolynomial<C, D> Cp = new QLRSolvablePolynomial<C, D>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * QLRSolvablePolynomial left and right multiplication. Product with ring
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
    public QLRSolvablePolynomial<C, D> multiply(C b, ExpVector e, C c, ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        if (b.isONE() && e.isZERO() && c.isONE() && f.isZERO()) {
            return this;
        }
        QLRSolvablePolynomial<C, D> Cp = new QLRSolvablePolynomial<C, D>(ring, b, e);
        QLRSolvablePolynomial<C, D> Dp = new QLRSolvablePolynomial<C, D>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * QLRSolvablePolynomial multiplication. Left product with ring element and
     * exponent vector.
     *
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiplyLeft(C b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        QLRSolvablePolynomial<C, D> Cp = new QLRSolvablePolynomial<C, D>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * QLRSolvablePolynomial multiplication. Left product with exponent vector.
     *
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        C b = ring.getONECoefficient();
        QLRSolvablePolynomial<C, D> Cp = new QLRSolvablePolynomial<C, D>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * QLRSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
     *
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiplyLeft(C b) {
        QLRSolvablePolynomial<C, D> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, C> Cm = Cp.val; //getMap();
        Map<ExpVector, C> Am = val;
        C c;
        for (Map.Entry<ExpVector, C> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * QLRSolvablePolynomial multiplication. Left product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiplyLeft(Map.Entry<ExpVector, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * QLRSolvablePolynomial multiplication. Product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public QLRSolvablePolynomial<C, D> multiply(Map.Entry<ExpVector, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * QLRSolvablePolynomial multiplication with exponent vector.
     *
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected QLRSolvablePolynomial<C, D> shift(ExpVector f) {
        QLRSolvablePolynomial<C, D> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, C> Cm = C.val;
        Map<ExpVector, C> Bm = this.val;
        for (Map.Entry<ExpVector, C> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
