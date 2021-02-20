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
 * LocalSolvablePolynomial generic recursive solvable polynomials implementing
 * RingElem. n-variate ordered solvable polynomials over solvable polynomial
 * coefficients. Objects of this class are intended to be immutable. The
 * implementation is based on TreeMap respectively SortedMap from exponents to
 * coefficients by extension of GenPolynomial.
 * Will be deprecated use QLRSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class LocalSolvablePolynomial<C extends GcdRingElem<C>> extends
                GenSolvablePolynomial<SolvableLocal<C>> {


    /**
     * The factory for the recursive solvable polynomial ring. Hides super.ring.
     */
    public final LocalSolvablePolynomialRing<C> ring;


    private static final Logger logger = LogManager.getLogger(LocalSolvablePolynomial.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for zero LocalSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     */
    public LocalSolvablePolynomial(LocalSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for LocalSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     * @param e exponent.
     */
    public LocalSolvablePolynomial(LocalSolvablePolynomialRing<C> r, SolvableLocal<C> c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for LocalSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param c coefficient polynomial.
     */
    public LocalSolvablePolynomial(LocalSolvablePolynomialRing<C> r, SolvableLocal<C> c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for LocalSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param S solvable polynomial.
     */
    public LocalSolvablePolynomial(LocalSolvablePolynomialRing<C> r, GenSolvablePolynomial<SolvableLocal<C>> S) {
        this(r, S.getMap());
    }


    /**
     * Constructor for LocalSolvablePolynomial.
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected LocalSolvablePolynomial(LocalSolvablePolynomialRing<C> r,
                    SortedMap<ExpVector, SolvableLocal<C>> v) {
        this(r);
        val.putAll(v); // assume no zero coefficients
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public LocalSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this LocalSolvablePolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
    public LocalSolvablePolynomial<C> copy() {
        return new LocalSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof LocalSolvablePolynomial)) {
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
     * LocalSolvablePolynomial multiplication.
     * @param Bp LocalSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // not @Override
    public LocalSolvablePolynomial<C> multiply(LocalSolvablePolynomial<C> Bp) {
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
        LocalSolvablePolynomial<C> Dp = ring.getZERO().copy();
        LocalSolvablePolynomial<C> zero = ring.getZERO().copy();
        SolvableLocal<C> one = ring.getONECoefficient();

        //LocalSolvablePolynomial<C> C1 = null;
        //LocalSolvablePolynomial<C> C2 = null;
        Map<ExpVector, SolvableLocal<C>> A = val;
        Map<ExpVector, SolvableLocal<C>> B = Bp.val;
        Set<Map.Entry<ExpVector, SolvableLocal<C>>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, SolvableLocal<C>> y : A.entrySet()) {
            SolvableLocal<C> a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.info("e = " + e + ", a = " + a);
            //int[] ep = e.dependencyOnVariables();
            //int el1 = ring.nvar + 1;
            //if (ep.length > 0) {
            //    el1 = ep[0];
            //}
            //int el1s = ring.nvar + 1 - el1;
            for (Map.Entry<ExpVector, SolvableLocal<C>> x : Bk) {
                SolvableLocal<C> b = x.getValue();
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
                LocalSolvablePolynomial<C> Cps = ring.getZERO().copy();
                //LocalSolvablePolynomial<C> Cs;
                LocalSolvablePolynomial<C> qp;
                if (ring.polCoeff.coeffTable.isEmpty() || b.isConstant() || e.isZERO()) { // symmetric
                    Cps = new LocalSolvablePolynomial<C>(ring, b, e);
                    if (debug)
                        logger.info("symmetric coeff: b = " + b + ", e = " + e);
                } else { // unsymmetric
                    if (debug)
                        logger.info("unsymmetric coeff: b = " + b + ", e = " + e);
                    // compute e * b as ( e * 1/b.den ) * b.num
                    if (b.den.isONE()) { // recursion base
                        // for (Map.Entry<ExpVector, C> z : b.num.getMap().entrySet()) {
                        //     C c = z.getValue();
                        //     SolvableLocal<C> cc = b.ring.getONE().multiply(c);
                        //     ExpVector g = z.getKey();
                        //     if (debug)
                        //         logger.info("g = " + g + ", c = " + c);
                        //     int[] gp = g.dependencyOnVariables();
                        //     int gl1 = 0;
                        //     if (gp.length > 0) {
                        //         gl1 = gp[gp.length - 1];
                        //     }
                        //     int gl1s = b.ring.ring.nvar + 1 - gl1;
                        //     if (debug) {
                        //         logger.info("gl1s = " + gl1s);
                        //     }
                        //     // split e = e1 * e2, g = g1 * g2
                        //     ExpVector e1 = e;
                        //     ExpVector e2 = Z;
                        //     if (!e.isZERO()) {
                        //         e1 = e.subst(el1, 0);
                        //         e2 = Z.subst(el1, e.getVal(el1));
                        //     }
                        //     ExpVector e4;
                        //     ExpVector g1 = g;
                        //     ExpVector g2 = Zc;
                        //     if (!g.isZERO()) {
                        //         g1 = g.subst(gl1, 0);
                        //         g2 = Zc.subst(gl1, g.getVal(gl1));
                        //     }
                        //     if (debug)
                        //         logger.info("coeff, e1 = " + e1 + ", e2 = " + e2);
                        //     if (debug)
                        //         logger.info("coeff, g1 = " + g1 + ", g2 = " + g2);
                        //     TableRelation<GenPolynomial<C>> crel = ring.coeffTable.lookup(e2, g2);
                        //     if (debug)
                        //         logger.info("coeff, crel = " + crel.p);
                        //     if (debug)
                        //         logger.info("coeff, e  = " + e + " g, = " + g + ", crel = " + crel);
                        //     Cs = ring.fromPolyCoefficients(crel.p); //LocalSolvablePolynomial<C>(ring, crel.p);
                        //     // rest of multiplication and update relations
                        //     if (crel.f != null) {
                        //         SolvableLocal<C> c2 = b.ring.getONE().multiply(crel.f);
                        //         C2 = new LocalSolvablePolynomial<C>(ring, c2, Z);
                        //         Cs = Cs.multiply(C2);
                        //         if (crel.e == null) {
                        //             e4 = e2;
                        //         } else {
                        //             e4 = e2.subtract(crel.e);
                        //         }
                        //         ring.coeffTable.update(e4, g2, ring.toPolyCoefficients(Cs));
                        //     }
                        //     if (crel.e != null) { // process left part
                        //         C1 = new LocalSolvablePolynomial<C>(ring, one, crel.e);
                        //         Cs = C1.multiply(Cs);
                        //         ring.coeffTable.update(e2, g2, ring.toPolyCoefficients(Cs));
                        //     }
                        //     if (!g1.isZERO()) { // process right part
                        //         SolvableLocal<C> c2 = b.ring.getONE().multiply(g1);
                        //         C2 = new LocalSolvablePolynomial<C>(ring, c2, Z);
                        //         Cs = Cs.multiply(C2);
                        //     }
                        //     if (!e1.isZERO()) { // process left part
                        //         C1 = new LocalSolvablePolynomial<C>(ring, one, e1);
                        //         Cs = C1.multiply(Cs);
                        //     }
                        //     //System.out.println("e1*Cs*g1 = " + Cs);
                        //     Cs = Cs.multiplyLeft(cc); // cc * Cs
                        //     Cps = (LocalSolvablePolynomial<C>) Cps.sum(Cs);
                        // } // end b.num loop 
                        // recursive polynomial coefficient multiplication : e * b.num
                        RecSolvablePolynomial<C> rsp1 = new RecSolvablePolynomial<C>(ring.polCoeff, e);
                        RecSolvablePolynomial<C> rsp2 = new RecSolvablePolynomial<C>(ring.polCoeff, b.num);
                        RecSolvablePolynomial<C> rsp3 = rsp1.multiply(rsp2);
                        LocalSolvablePolynomial<C> rsp = ring.fromPolyCoefficients(rsp3);
                        Cps = rsp;
                        // if (rsp.compareTo(Cps) != 0) {
                        //     logger.info("coeff-poly: Cps = " + Cps);
                        //     logger.info("coeff-poly: rsp = " + rsp);
                        //     //System.out.println("rsp.compareTo(Cps) != 0");
                        //     //} else {
                        //     //System.out.println("rsp.compareTo(Cps) == 0");
                        // }
                    } else { // b.den != 1
                        if (debug)
                            logger.info("coeff-num: Cps = " + Cps + ", num = " + b.num + ", den = " + b.den);
                        Cps = new LocalSolvablePolynomial<C>(ring, b.ring.getONE(), e);

                        // coefficient multiplication with 1/den: 
                        LocalSolvablePolynomial<C> qv = Cps;
                        SolvableLocal<C> qden = new SolvableLocal<C>(b.ring, b.den); // den/1
                        //System.out.println("qv = " + qv + ", den = " + den);
                        // recursion with den==1:
                        LocalSolvablePolynomial<C> v = qv.multiply(qden);
                        LocalSolvablePolynomial<C> vl = qv.multiplyLeft(qden);
                        //System.out.println("v = " + v + ", vl = " + vl + ", qden = " + qden);
                        LocalSolvablePolynomial<C> vr = (LocalSolvablePolynomial<C>) v.subtract(vl);
                        SolvableLocal<C> qdeni = new SolvableLocal<C>(b.ring, b.ring.ring.getONE(), b.den);
                        //System.out.println("vr = " + vr + ", qdeni = " + qdeni);
                        // recursion with smaller head term:
                        LocalSolvablePolynomial<C> rq = vr.multiply(qdeni);
                        qp = (LocalSolvablePolynomial<C>) qv.subtract(rq);
                        qp = qp.multiplyLeft(qdeni);
                        //System.out.println("qp_i = " + qp);
                        Cps = qp;

                        if (!b.num.isONE()) {
                            SolvableLocal<C> qnum = new SolvableLocal<C>(b.ring, b.num); // num/1
                            // recursion with den == 1:
                            Cps = Cps.multiply(qnum);
                        }
                    }
                } // end coeff
                if (debug)
                    logger.info("coeff-den: Cps = " + Cps);
                // polynomial multiplication 
                LocalSolvablePolynomial<C> Dps = ring.getZERO().copy();
                LocalSolvablePolynomial<C> Ds = null;
                LocalSolvablePolynomial<C> D1, D2;
                if (ring.table.isEmpty() || Cps.isConstant() || f.isZERO()) { // symmetric
                    if (debug)
                        logger.info("symmetric poly: b = " + b + ", e = " + e);
                    ExpVector g = e.sum(f);
                    if (Cps.isConstant()) {
                        Ds = new LocalSolvablePolynomial<C>(ring, Cps.leadingBaseCoefficient(), g); // symmetric!
                    } else {
                        Ds = Cps.shift(f); // symmetric
                    }
                } else { // eventually unsymmetric
                    if (debug)
                        logger.info("unsymmetric poly: Cps = " + Cps + ", f = " + f);
                    for (Map.Entry<ExpVector, SolvableLocal<C>> z : Cps.val.entrySet()) {
                        // split g = g1 * g2, f = f1 * f2
                        SolvableLocal<C> c = z.getValue();
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
                            Ds = (LocalSolvablePolynomial<C>) zero.sum(one, h); // symmetric!
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
                            TableRelation<SolvableLocal<C>> rel = ring.table.lookup(g2, f2);
                            if (debug)
                                logger.info("poly, g  = " + g + ", f  = " + f + ", rel = " + rel);
                            Ds = new LocalSolvablePolynomial<C>(ring, rel.p); //ring.copy(rel.p);
                            if (rel.f != null) {
                                D2 = new LocalSolvablePolynomial<C>(ring, one, rel.f);
                                Ds = Ds.multiply(D2);
                                if (rel.e == null) {
                                    g4 = g2;
                                } else {
                                    g4 = g2.subtract(rel.e);
                                }
                                ring.table.update(g4, f2, Ds);
                            }
                            if (rel.e != null) {
                                D1 = new LocalSolvablePolynomial<C>(ring, one, rel.e);
                                Ds = D1.multiply(Ds);
                                ring.table.update(g2, f2, Ds);
                            }
                            if (!f1.isZERO()) {
                                D2 = new LocalSolvablePolynomial<C>(ring, one, f1);
                                Ds = Ds.multiply(D2);
                                //ring.table.update(?,f1,Ds)
                            }
                            if (!g1.isZERO()) {
                                D1 = new LocalSolvablePolynomial<C>(ring, one, g1);
                                Ds = D1.multiply(Ds);
                                //ring.table.update(e1,?,Ds)
                            }
                        }
                        Ds = Ds.multiplyLeft(c); // c * Ds
                        Dps = (LocalSolvablePolynomial<C>) Dps.sum(Ds);
                    } // end Dps loop
                    Ds = Dps;
                }
                Ds = Ds.multiplyLeft(a); // multiply(a,b); // non-symmetric 
                if (debug)
                    logger.debug("Ds = " + Ds);
                Dp = (LocalSolvablePolynomial<C>) Dp.sum(Ds);
            } // end B loop
        } // end A loop
          //System.out.println("this * Bp = " + Dp);
        return Dp;
    }


    /**
     * LocalSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     * @param S LocalSolvablePolynomial.
     * @param T LocalSolvablePolynomial.
     * @return S*this*T.
     */
    // not @Override
    public LocalSolvablePolynomial<C> multiply(LocalSolvablePolynomial<C> S, LocalSolvablePolynomial<C> T) {
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
     * LocalSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     * @param b solvable coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(SolvableLocal<C> b) {
        LocalSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (b.isONE()) {
            return this;
        }
        Cp = new LocalSolvablePolynomial<C>(ring, b, ring.evzero);
        return multiply(Cp);
    }


    /**
     * LocalSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     * @param b coefficient polynomial.
     * @param c coefficient polynomial.
     * @return b*this*c, where * is coefficient multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(SolvableLocal<C> b, SolvableLocal<C> c) {
        LocalSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        if (b.isONE() && c.isONE()) {
            return this;
        }
        Cp = new LocalSolvablePolynomial<C>(ring, b, ring.evzero);
        LocalSolvablePolynomial<C> Dp = new LocalSolvablePolynomial<C>(ring, c, ring.evzero);
        return multiply(Cp, Dp);
    }


    /**
     * LocalSolvablePolynomial multiplication. Product with exponent vector.
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableLocal<C> b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * LocalSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     *         multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(ExpVector e, ExpVector f) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        SolvableLocal<C> b = ring.getONECoefficient();
        return multiply(b, e, b, f);
    }


    /**
     * LocalSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(SolvableLocal<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (b.isONE() && e.isZERO()) {
            return this;
        }
        LocalSolvablePolynomial<C> Cp = new LocalSolvablePolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * LocalSolvablePolynomial left and right multiplication. Product with ring
     * element and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @param c coefficient polynomial.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     *         solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(SolvableLocal<C> b, ExpVector e, SolvableLocal<C> c,
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
        LocalSolvablePolynomial<C> Cp = new LocalSolvablePolynomial<C>(ring, b, e);
        LocalSolvablePolynomial<C> Dp = new LocalSolvablePolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * LocalSolvablePolynomial multiplication. Left product with ring element
     * and exponent vector.
     * @param b coefficient polynomial.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiplyLeft(SolvableLocal<C> b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        LocalSolvablePolynomial<C> Cp = new LocalSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * LocalSolvablePolynomial multiplication. Left product with exponent
     * vector.
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        SolvableLocal<C> b = ring.getONECoefficient();
        LocalSolvablePolynomial<C> Cp = new LocalSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * LocalSolvablePolynomial multiplication. Left product with coefficient
     * ring element.
     * @param b coefficient polynomial.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiplyLeft(SolvableLocal<C> b) {
        LocalSolvablePolynomial<C> Cp = ring.getZERO().copy();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Map<ExpVector, SolvableLocal<C>> Cm = Cp.val; //getMap();
        Map<ExpVector, SolvableLocal<C>> Am = val;
        SolvableLocal<C> c;
        for (Map.Entry<ExpVector, SolvableLocal<C>> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            SolvableLocal<C> a = y.getValue();
            c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * LocalSolvablePolynomial multiplication. Left product with 'monomial'.
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector, SolvableLocal<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * LocalSolvablePolynomial multiplication. Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public LocalSolvablePolynomial<C> multiply(Map.Entry<ExpVector, SolvableLocal<C>> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * LocalSolvablePolynomial multiplication. Left product with coefficient
     * ring element.
     * @param f exponent vector.
     * @return B*f, where * is commutative multiplication.
     */
    protected LocalSolvablePolynomial<C> shift(ExpVector f) {
        LocalSolvablePolynomial<C> C = ring.getZERO().copy();
        if (this.isZERO()) {
            return C;
        }
        if (f == null || f.isZERO()) {
            return this;
        }
        Map<ExpVector, SolvableLocal<C>> Cm = C.val;
        Map<ExpVector, SolvableLocal<C>> Bm = this.val;
        for (Map.Entry<ExpVector, SolvableLocal<C>> y : Bm.entrySet()) {
            ExpVector e = y.getKey();
            SolvableLocal<C> a = y.getValue();
            ExpVector d = e.sum(f);
            if (!a.isZERO()) {
                Cm.put(d, a);
            }
        }
        return C;
    }

}
