/*
 * $Id$
 */

package edu.jas.fd;


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
 * QuotSolvablePolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. Objects of this class are intended to be immutable. The
 * implementation is based on TreeMap respectively SortedMap from exponents to
 * coefficients by extension of GenPolynomial.
 * Will be deprecated use QLRSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class QuotSolvablePolynomial<C extends GcdRingElem<C>> extends
                GenSolvablePolynomial<SolvableQuotient<C>> {


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final QuotSolvablePolynomialRing<C> ring;


    private static final Logger logger = LogManager.getLogger(QuotSolvablePolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for zero QuotSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     */
    public QuotSolvablePolynomial(QuotSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for QuotSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public QuotSolvablePolynomial(QuotSolvablePolynomialRing<C> r, SolvableQuotient<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for QuotSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public QuotSolvablePolynomial(QuotSolvablePolynomialRing<C> r, SolvableQuotient<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for QuotSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public QuotSolvablePolynomial(QuotSolvablePolynomialRing<C> r,
                    GenSolvablePolynomial<SolvableQuotient<C>> S) {
        this(r, S.getMap());
    }


    /**
     * Constructor for QuotSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected QuotSolvablePolynomial(QuotSolvablePolynomialRing<C> r,
                    SortedMap<ExpVector, SolvableQuotient<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public QuotSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this QuotSolvablePolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
    public QuotSolvablePolynomial<C> copy() {
        return new QuotSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof QuotSolvablePolynomial)) {
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
     * QuotSolvablePolynomial multiplication.
     * @param Bp QuotSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // not @Override
    public QuotSolvablePolynomial<C> multiply(QuotSolvablePolynomial<C> Bp) {
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
        QuotSolvablePolynomial<C> Dp = ring.getZERO().copy();
        QuotSolvablePolynomial<C> zero = ring.getZERO().copy();
        SolvableQuotient<C> one = ring.getONECoefficient();

        //QuotSolvablePolynomial<C> C1 = null;
        //QuotSolvablePolynomial<C> C2 = null;
        Map<ExpVector, SolvableQuotient<C>> A = val;
        Map<ExpVector, SolvableQuotient<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, SolvableQuotient<C>>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, SolvableQuotient<C>> y : A.entrySet()) {
            SolvableQuotient<C> a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = " + e + ", a = " + a);
            // int[] ep = e.dependencyOnVariables();
            // int el1 = ring.nvar + 1;
            // if (ep.length > 0) {
            //     el1 = ep[0];
            // }
            // int el1s = ring.nvar + 1 - el1;
            for (Map.Entry<ExpVector, SolvableQuotient<C>> x : Bk) {
                SolvableQuotient<C> b = x.getValue();
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
                QuotSolvablePolynomial<C> Cps = ring.getZERO().copy();
                //QuotSolvablePolynomial<C> Cs;
                QuotSolvablePolynomial<C> qp;
                if (ring.polCoeff.coeffTable.isEmpty() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new QuotSolvablePolynomial<C>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = " + b + ", e = " + e);
                    // compute e * b as ( e * 1/b.den ) * b.num
                    if (b.den.isONE()) { // recursion base
                        // recursive polynomial coefficient multiplication : e * b.num
                        RecSolvablePolynomial<C> rsp1 = new RecSolvablePolynomial<C>(ring.polCoeff, e);
                        RecSolvablePolynomial<C> rsp2 = new RecSolvablePolynomial<C>(ring.polCoeff, b.num);
                        RecSolvablePolynomial<C> rsp3 = rsp1.multiply(rsp2);
                        QuotSolvablePolynomial<C> rsp = ring.fromPolyCoefficients(rsp3);
                        Cps = rsp;
                    } else { // b.den != 1
                        if (debug)
                            logger.info("coeff-num: Cps = " + Cps + ", num = " + b.num + ", den = " + b.den);
                        Cps = new QuotSolvablePolynomial<C>(ring, b.ring.getONE(), e);

                        // coefficient multiplication with 1/den: 
                        QuotSolvablePolynomial<C> qv = Cps;
                        SolvableQuotient<C> qden = new SolvableQuotient<C>(b.ring, b.den); // den/1
                        //System.out.println("qv = " + qv + ", den = " + den);
                        // recursion with den==1:
                        QuotSolvablePolynomial<C> v = qv.multiply(qden);
                        QuotSolvablePolynomial<C> vl = qv.multiplyLeft(qden);
                        //System.out.println("v = " + v + ", vl = " + vl + ", qden = " + qden);
                        QuotSolvablePolynomial<C> vr = (QuotSolvablePolynomial<C>) v.subtract(vl);
                        SolvableQuotient<C> qdeni = new SolvableQuotient<C>(b.ring, b.ring.ring.getONE(),
                                        b.den);
                        //System.out.println("vr = " + vr + ", qdeni = " + qdeni);
                        // recursion with smaller head term:
                        QuotSolvablePolynomial<C> rq = vr.multiply(qdeni);
                        qp = (QuotSolvablePolynomial<C>) qv.subtract(rq);
                        qp = qp.multiplyLeft(qdeni);
                        //System.out.println("qp_i = " + qp);
                        Cps = qp;

                        if (!b.num.isONE()) {
                            SolvableQuotient<C> qnum = new SolvableQuotient<C>(b.ring, b.num); // num/1
                            // recursion with den == 1:
                            Cps = Cps.multiply(qnum);
                        }
                    }
                } // end coeff
                if (debug)
                    logger.info("coeff-den: Cps = " + Cps);
                // polynomial multiplication 
                QuotSolvablePolynomial<C> Dps = ring.getZERO().copy();
                QuotSolvablePolynomial<C> Ds = null;
                QuotSolvablePolynomial<C> D1, D2;
                if (ring.table.isEmpty() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = " + b + ", e = " + e);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new QuotSolvablePolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = " + Cps + ", f = " + f);
                    for (Map.Entry<ExpVector, SolvableQuotient<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        SolvableQuotient<C> c = z.getValue();
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
                            Ds = (QuotSolvablePolynomial<C>) zero.sum(one, h); // symmetric!
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
                            TableRelation<SolvableQuotient<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new QuotSolvablePolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new QuotSolvablePolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new QuotSolvablePolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new QuotSolvablePolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new QuotSolvablePolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // c * Ds
                        Dps = (QuotSolvablePolynomial<C>) Dps.sum(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.debug("Ds = " + Ds);
                Dp = (QuotSolvablePolynomial<C>) Dp.sum(Ds);
            } // end B loop
        } // end A loop
          //System.out.println("this * Bp = " + Dp);
        return Dp;
    }


    /**
     * QuotSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     * @param S QuotSolvablePolynomial.
     * @param T QuotSolvablePolynomial.
     * @return S*this*T.
     */
    // not @Override
    public QuotSolvablePolynomial<C> multiply(QuotSolvablePolynomial<C> S, QuotSolvablePolynomial<C> T) {
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
     * QuotSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     * @param b solvable coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(SolvableQuotient<C> b) {
        QuotSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (b.isONE()) {
            return this;
        }
        Cp = new QuotSolvablePolynomial<C>(ring, b, ring.evzero);
        return multiply(Cp);
    }


    /**
     * QuotSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(SolvableQuotient<C> b, SolvableQuotient<C> c) {
        QuotSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        if (b.isONE() && c.isONE()) {
            return this;
        }
        Cp = new QuotSolvablePolynomial<C>(ring, b, ring.evzero);
        QuotSolvablePolynomial<C> Dp = new QuotSolvablePolynomial<C>(ring, c, ring.evzero);
        return multiply(Cp, Dp);
    }


    /**
     * QuotSolvablePolynomial multiplication. Product with exponent vector.
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableQuotient<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * QuotSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     *         multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        SolvableQuotient<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * QuotSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(SolvableQuotient<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (b.isONE() && e.isZERO()) {
            return this;
        }
        QuotSolvablePolynomial<C> Cp = new QuotSolvablePolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * QuotSolvablePolynomial left and right multiplication. Product with ring
     * element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     *         solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(SolvableQuotient<C> b, ExpVector e, SolvableQuotient<C> c,
                    ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        if (b.isONE() && e.isZERO() && c.isONE() && f.isZERO()) {
            return this;
        }
        QuotSolvablePolynomial<C> Cp = new QuotSolvablePolynomial<C>(ring, b, e);
        QuotSolvablePolynomial<C> Dp = new QuotSolvablePolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * QuotSolvablePolynomial multiplication. Left product with ring element and
     * exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiplyLeft(SolvableQuotient<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        QuotSolvablePolynomial<C> Cp = new QuotSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * QuotSolvablePolynomial multiplication. Left product with exponent vector.
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableQuotient<C> b = ring.getONECoefficient();
        QuotSolvablePolynomial<C> Cp = new QuotSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * QuotSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiplyLeft(SolvableQuotient<C> b) {
        QuotSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, SolvableQuotient<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, SolvableQuotient<C>> Am = val;
        SolvableQuotient<C> c;
        for (Map.Entry<ExpVector, SolvableQuotient<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            SolvableQuotient<C> a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * QuotSolvablePolynomial multiplication. Left product with 'monomial'.
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector, SolvableQuotient<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * QuotSolvablePolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public QuotSolvablePolynomial<C> multiply(Map.Entry<ExpVector, SolvableQuotient<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * QuotSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected QuotSolvablePolynomial<C> shift(ExpVector f) {
        QuotSolvablePolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, SolvableQuotient<C>> Cm = C.val;
        Map<ExpVector, SolvableQuotient<C>> Bm = this.val;
        for (Map.Entry<ExpVector, SolvableQuotient<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            SolvableQuotient<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
