/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.RingElem;


// import edu.jas.application.ResidueSolvablePolynomial;


/**
 * GenSolvablePolynomial generic solvable polynomials implementing RingElem.
 * n-variate ordered solvable polynomials over C. Objects of this class are
 * intended to be immutable. The implementation is based on TreeMap respectively
 * SortedMap from exponents to coefficients by extension of GenPolybomial. Only
 * the coefficients are modeled with generic types, the exponents are fixed to
 * ExpVector with long entries (this will eventually be changed in the future).
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GenSolvablePolynomial<C extends RingElem<C>> extends GenPolynomial<C> {


    //not possible: implements RingElem< GenSolvablePolynomial<C> > {


    private static final Logger logger = Logger.getLogger(GenSolvablePolynomial.class);
    private static final boolean debug = false; //logger.isDebugEnabled();
    /**
     * The factory for the solvable polynomial ring. Hides super.ring.
     */
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Constructor for zero GenSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     */
    public GenSolvablePolynomial(GenSolvablePolynomialRing<C> r) {
        super(r);
        ring = r;
    }


    /**
     * Constructor for GenSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient.
     * @param e exponent.
     */
    public GenSolvablePolynomial(GenSolvablePolynomialRing<C> r, C c, ExpVector e) {
        this(r);
        if (c != null && !c.isZERO()) {
            val.put(e, c);
        }
    }


    /**
     * Constructor for GenSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param c coefficient.
     */
    public GenSolvablePolynomial(GenSolvablePolynomialRing<C> r, C c) {
        this(r, c, r.evzero);
    }


    /**
     * Constructor for GenSolvablePolynomial.
     *
     * @param r solvable polynomial ring factory.
     * @param v the SortedMap of some other (solvable) polynomial.
     */
    protected GenSolvablePolynomial(GenSolvablePolynomialRing<C> r, SortedMap<ExpVector, C> v) {
        this(r);
        if (v.size() > 0) {
            GenPolynomialRing.creations++;
            val.putAll(v); // assume val is empty and no zero coefficients in v
        }
    }


    /**
     * Get the corresponding element factory.
     *
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public GenSolvablePolynomialRing<C> factory() {
        return ring;
    }


    /**
     * Clone this GenSolvablePolynomial.
     *
     * @see Object#clone()
     */
    @Override
    public GenSolvablePolynomial<C> copy() {
        return new GenSolvablePolynomial<C>(ring, this.val);
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof GenSolvablePolynomial)) {
            return false;
        }
        return super.equals(B);
    }


    /**
     * GenSolvablePolynomial multiplication.
     *
     * @param Bp GenSolvablePolynomial.
     * @return this*Bp, where * denotes solvable multiplication.
     */
    // cannot @Override, @NoOverride
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C> multiply(GenSolvablePolynomial<C> Bp) {
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
        if (this instanceof RecSolvablePolynomial && Bp instanceof RecSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE Rec.multiply(Rec Bp) - trying to fix");
            RecSolvablePolynomial T = (RecSolvablePolynomial) this; // no <C>
            RecSolvablePolynomial Sp = (RecSolvablePolynomial) Bp;
            return (GenSolvablePolynomial<C>) T.multiply(Sp);
        }
        if (this instanceof QLRSolvablePolynomial && Bp instanceof QLRSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE QLR.multiply(QLR Bp) - trying to fix");
            QLRSolvablePolynomial T = (QLRSolvablePolynomial) this; // no <C>
            QLRSolvablePolynomial Sp = (QLRSolvablePolynomial) Bp;
            return (GenSolvablePolynomial<C>) T.multiply(Sp);
        }
        // if (this instanceof ResidueSolvablePolynomial && Bp instanceof ResidueSolvablePolynomial) {
        //     //throw new RuntimeException("wrong method dispatch in JRE ");
        //     logger.info("warn: wrong method dispatch in JRE multiply(Bp) - trying to fix");
        //     ResidueSolvablePolynomial T = (ResidueSolvablePolynomial) this; // no <C>
        //     ResidueSolvablePolynomial Sp = (ResidueSolvablePolynomial) Bp;
        //     return (GenSolvablePolynomial<C>) T.multiply(Sp);
        // }
        final boolean commute = ring.table.isEmpty();
        GenSolvablePolynomial<C> Cp = ring.getZERO().copy(); // needed for doPutToMap and doAddTo
        //GenSolvablePolynomial<C> zero = ring.getZERO(); //.copy(); // copy not needed
        //C one = ring.getONECoefficient();
        ExpVector Z = ring.evzero;

        GenSolvablePolynomial<C> C1 = null;
        GenSolvablePolynomial<C> C2 = null;
        Map<ExpVector, C> A = val;
        Map<ExpVector, C> B = Bp.val;
        Set<Map.Entry<ExpVector, C>> Bk = B.entrySet();
        for (Map.Entry<ExpVector, C> y : A.entrySet()) {
            C a = y.getValue();
            ExpVector e = y.getKey();
            if (debug)
                logger.debug("e = " + e);
            int[] ep = e.dependencyOnVariables();
            int el1 = ring.nvar + 1;
            if (ep.length > 0) {
                el1 = ep[0];
            }
            int el1s = ring.nvar + 1 - el1;
            for (Map.Entry<ExpVector, C> x : Bk) {
                C b = x.getValue();
                ExpVector f = x.getKey();
                if (debug)
                    logger.debug("f = " + f);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0;
                if (fp.length > 0) {
                    fl1 = fp[fp.length - 1];
                }
                int fl1s = ring.nvar + 1 - fl1;
                if (debug) {
                    logger.debug("el1s = " + el1s + " fl1s = " + fl1s);
                }
                GenSolvablePolynomial<C> Cs = null;
                if (commute || el1s <= fl1s) { // symmetric
                    ExpVector g = e.sum(f);
                    //if ( debug ) logger.debug("g = " + g);
                    Cs = ring.valueOf(g); // symmetric! //(GenSolvablePolynomial<C>) zero.sum(one, g); 
                    //no: Cs = new GenSolvablePolynomial<C>(ring, one, g); 
                    //System.out.println("Cs(sym) = " + Cs + ", g = " + g);
                } else { // unsymmetric
                    // split e = e1 * e2, f = f1 * f2
                    ExpVector e1 = e.subst(el1, 0);
                    ExpVector e2 = Z.subst(el1, e.getVal(el1));
                    ExpVector e4;
                    ExpVector f1 = f.subst(fl1, 0);
                    ExpVector f2 = Z.subst(fl1, f.getVal(fl1));
                    //if ( debug ) logger.debug("e1 = " + e1 + " e2 = " + e2);
                    //if ( debug ) logger.debug("f1 = " + f1 + " f2 = " + f2);
                    TableRelation<C> rel = ring.table.lookup(e2, f2);
                    //logger.info("relation = " + rel);
                    Cs = rel.p; //ring.copy( rel.p ); // do not clone() 
                    if (rel.f != null) {
                        C2 = ring.valueOf(rel.f); //(GenSolvablePolynomial<C>) zero.sum(one, rel.f);
                        Cs = Cs.multiply(C2);
                        if (rel.e == null) {
                            e4 = e2;
                        } else {
                            e4 = e2.subtract(rel.e);
                        }
                        ring.table.update(e4, f2, Cs);
                    }
                    if (rel.e != null) {
                        C1 = ring.valueOf(rel.e); //(GenSolvablePolynomial<C>) zero.sum(one, rel.e);
                        Cs = C1.multiply(Cs);
                        ring.table.update(e2, f2, Cs);
                    }
                    if (!f1.isZERO()) {
                        C2 = ring.valueOf(f1); //(GenSolvablePolynomial<C>) zero.sum(one, f1);
                        Cs = Cs.multiply(C2);
                        //ring.table.update(?,f1,Cs)
                    }
                    if (!e1.isZERO()) {
                        C1 = ring.valueOf(e1); //(GenSolvablePolynomial<C>) zero.sum(one, e1);
                        Cs = C1.multiply(Cs);
                        //ring.table.update(e1,?,Cs)
                    }
                }
                //System.out.println("Cs = " + Cs + ", a = " + a + ", b = " + b);
                //C c = a.multiply(b);
                Cs = Cs.multiply(a, b); // now non-symmetric // Cs.multiply(c); is symmetric!
                //if ( debug ) logger.debug("Cs = " + Cs);
                //Cp = (GenSolvablePolynomial<C>) Cp.sum(Cs);
                Cp.doAddTo(Cs);
            }
        }
        return Cp;
    }


    /**
     * GenSolvablePolynomial left and right multiplication. Product with two
     * polynomials.
     *
     * @param S GenSolvablePolynomial.
     * @param T GenSolvablePolynomial.
     * @return S*this*T.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiply(GenSolvablePolynomial<C> S, GenSolvablePolynomial<C> T) {
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
     * GenSolvablePolynomial multiplication. Product with coefficient ring
     * element.
     *
     * @param b coefficient.
     * @return this*b, where * is coefficient multiplication.
     */
    @Override
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C> multiply(C b) {
        GenSolvablePolynomial<C> Cp = ring.getZERO();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (this instanceof RecSolvablePolynomial && b instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE Rec.multiply(b) - trying to fix");
            RecSolvablePolynomial T = (RecSolvablePolynomial) this; // no <C>
            GenSolvablePolynomial Sp = (GenSolvablePolynomial) b;
            return (GenSolvablePolynomial<C>) T.recMultiply(Sp);
        }
        if (this instanceof QLRSolvablePolynomial && b instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE QLR.multiply(Bp) - trying to fix");
            QLRSolvablePolynomial T = (QLRSolvablePolynomial) this; // no <C>
            GenSolvablePolynomial Sp = (GenSolvablePolynomial) b;
            return (GenSolvablePolynomial<C>) T.multiply(Sp);
        }
        // if (this instanceof ResidueSolvablePolynomial && b instanceof GenSolvablePolynomial) {
        //     //throw new RuntimeException("wrong method dispatch in JRE ");
        //     logger.info("warn: wrong method dispatch in JRE multiply(Bp) - trying to fix");
        //     ResidueSolvablePolynomial T = (ResidueSolvablePolynomial) this; // no <C>
        //     GenSolvablePolynomial Sp = (GenSolvablePolynomial) b;
        //     return (GenSolvablePolynomial<C>) T.multiply(Sp);
        // }
        Cp = Cp.copy();
        Map<ExpVector, C> Cm = Cp.val; //getMap();
        Map<ExpVector, C> Am = val;
        for (Map.Entry<ExpVector, C> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            C c = a.multiply(b);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * GenSolvablePolynomial left and right multiplication. Product with
     * coefficient ring element.
     *
     * @param b coefficient.
     * @param c coefficient.
     * @return b*this*c, where * is coefficient multiplication.
     */
    // new method, @NoOverride
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C> multiply(C b, C c) {
        GenSolvablePolynomial<C> Cp = ring.getZERO();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        if (c == null || c.isZERO()) {
            return Cp;
        }
        if (this instanceof RecSolvablePolynomial && b instanceof GenSolvablePolynomial
                && c instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE Rec.multiply(b,c) - trying to fix");
            RecSolvablePolynomial T = (RecSolvablePolynomial) this; // no <C>
            GenSolvablePolynomial Bp = (GenSolvablePolynomial) b;
            GenSolvablePolynomial Dp = (GenSolvablePolynomial) c;
            return (GenSolvablePolynomial<C>) T.multiply(Bp, Dp);
        }
        if (this instanceof QLRSolvablePolynomial && b instanceof GenSolvablePolynomial
                && c instanceof GenSolvablePolynomial) {
            //throw new RuntimeException("wrong method dispatch in JRE ");
            logger.info("warn: wrong method dispatch in JRE QLR.multiply(b,c) - trying to fix");
            QLRSolvablePolynomial T = (QLRSolvablePolynomial) this; // no <C>
            GenSolvablePolynomial Bp = (GenSolvablePolynomial) b;
            GenSolvablePolynomial Dp = (GenSolvablePolynomial) c;
            return (GenSolvablePolynomial<C>) T.multiply(Bp, Dp);
        }
        // if (this instanceof ResidueSolvablePolynomial && b instanceof GenSolvablePolynomial
        //                 && c instanceof GenSolvablePolynomial) {
        //     //throw new RuntimeException("wrong method dispatch in JRE ");
        //     logger.info("warn: wrong method dispatch in JRE multiply(b,c) - trying to fix");
        //     ResidueSolvablePolynomial T = (ResidueSolvablePolynomial) this; // no <C>
        //     GenSolvablePolynomial Bp = (GenSolvablePolynomial) b;
        //     GenSolvablePolynomial Dp = (GenSolvablePolynomial) c;
        //     return (GenSolvablePolynomial<C>) T.multiply(Bp, Dp);
        // }
        Cp = Cp.copy();
        Map<ExpVector, C> Cm = Cp.val; //getMap();
        Map<ExpVector, C> Am = val;
        for (Map.Entry<ExpVector, C> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            C d = b.multiply(a).multiply(c);
            if (!d.isZERO()) {
                Cm.put(e, d);
            }
        }
        return Cp;
    }


    /**
     * GenSolvablePolynomial multiplication. Product with exponent vector.
     *
     * @param e exponent.
     * @return this * x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public GenSolvablePolynomial<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        C b = ring.getONECoefficient();
        return multiply(b, e);
    }


    /**
     * GenSolvablePolynomial left and right multiplication. Product with
     * exponent vector.
     *
     * @param e exponent.
     * @param f exponent.
     * @return x<sup>e</sup> * this * x<sup>f</sup>, where * denotes solvable
     * multiplication.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiply(ExpVector e, ExpVector f) {
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
     * GenSolvablePolynomial multiplication. Product with ring element and
     * exponent vector.
     *
     * @param b coefficient.
     * @param e exponent.
     * @return this * b x<sup>e</sup>, where * denotes solvable multiplication.
     */
    @Override
    public GenSolvablePolynomial<C> multiply(C b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        GenSolvablePolynomial<C> Cp = ring.valueOf(b, e); //new GenSolvablePolynomial<C>(ring, b, e);
        return multiply(Cp);
    }


    /**
     * GenSolvablePolynomial left and right multiplication. Product with ring
     * element and exponent vector.
     *
     * @param b coefficient.
     * @param e exponent.
     * @param c coefficient.
     * @param f exponent.
     * @return b x<sup>e</sup> * this * c x<sup>f</sup>, where * denotes
     * solvable multiplication.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiply(C b, ExpVector e, C c, ExpVector f) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (c == null || c.isZERO()) {
            return ring.getZERO();
        }
        GenSolvablePolynomial<C> Cp = ring.valueOf(b, e); //new GenSolvablePolynomial<C>(ring, b, e);
        GenSolvablePolynomial<C> Dp = ring.valueOf(c, f); //new GenSolvablePolynomial<C>(ring, c, f);
        return multiply(Cp, Dp);
    }


    /**
     * GenSolvablePolynomial multiplication. Left product with ring element and
     * exponent vector.
     *
     * @param b coefficient.
     * @param e exponent.
     * @return b x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiplyLeft(C b, ExpVector e) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        GenSolvablePolynomial<C> Cp = ring.valueOf(b, e); //new GenSolvablePolynomial<C>(ring, b, e);
        return Cp.multiply(this);
    }


    /**
     * GenSolvablePolynomial multiplication. Left product with exponent vector.
     *
     * @param e exponent.
     * @return x<sup>e</sup> * this, where * denotes solvable multiplication.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiplyLeft(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        C b = ring.getONECoefficient();
        return multiplyLeft(b, e);
    }


    /**
     * GenSolvablePolynomial multiplication. Left product with coefficient ring
     * element.
     *
     * @param b coefficient.
     * @return b*this, where * is coefficient multiplication.
     */
    @Override
    public GenSolvablePolynomial<C> multiplyLeft(C b) {
        GenSolvablePolynomial<C> Cp = ring.getZERO();
        if (b == null || b.isZERO()) {
            return Cp;
        }
        Cp = Cp.copy();
        Map<ExpVector, C> Cm = Cp.val; //getMap();
        Map<ExpVector, C> Am = val;
        for (Map.Entry<ExpVector, C> y : Am.entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            C c = b.multiply(a);
            if (!c.isZERO()) {
                Cm.put(e, c);
            }
        }
        return Cp;
    }


    /**
     * GenSolvablePolynomial multiplication. Left product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return m * this, where * denotes solvable multiplication.
     */
    // new method, @NoOverride
    public GenSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiplyLeft(m.getValue(), m.getKey());
    }


    /**
     * GenSolvablePolynomial multiplication. Product with 'monomial'.
     *
     * @param m 'monomial'.
     * @return this * m, where * denotes solvable multiplication.
     */
    @Override
    public GenSolvablePolynomial<C> multiply(Map.Entry<ExpVector, C> m) {
        if (m == null) {
            return ring.getZERO();
        }
        return multiply(m.getValue(), m.getKey());
    }


    /**
     * GenSolvablePolynomial subtract a multiple.
     *
     * @param a coefficient.
     * @param S GenSolvablePolynomial.
     * @return this - a * S.
     */
    public GenSolvablePolynomial<C> subtractMultiple(C a, GenSolvablePolynomial<C> S) {
        if (a == null || a.isZERO()) {
            return this;
        }
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.multiplyLeft(a.negate());
        }
        assert (ring.nvar == S.ring.nvar);
        GenSolvablePolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            C y = me.getValue(); // assert y != null
            y = a.multiply(y);
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenSolvablePolynomial subtract a multiple.
     *
     * @param a coefficient.
     * @param e exponent.
     * @param S GenSolvablePolynomial.
     * @return this - a * x<sup>e</sup> * S.
     */
    public GenSolvablePolynomial<C> subtractMultiple(C a, ExpVector e, GenSolvablePolynomial<C> S) {
        if (a == null || a.isZERO()) {
            return this;
        }
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S.multiplyLeft(a.negate(), e);
        }
        assert (ring.nvar == S.ring.nvar);
        GenSolvablePolynomial<C> n = this.copy();
        SortedMap<ExpVector, C> nv = n.val;
        GenSolvablePolynomial<C> s = S.multiplyLeft(e);
        SortedMap<ExpVector, C> sv = s.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            //f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y);
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenSolvablePolynomial scale and subtract a multiple.
     *
     * @param b scale factor.
     * @param a coefficient.
     * @param S GenSolvablePolynomial.
     * @return b * this - a * S.
     */
    //@Override
    public GenSolvablePolynomial<C> scaleSubtractMultiple(C b, C a, GenSolvablePolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiplyLeft(b);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiplyLeft(b);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiplyLeft(a.negate());
        }
        if (b.isONE()) {
            return subtractMultiple(a, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenSolvablePolynomial<C> n = this.multiplyLeft(b);
        SortedMap<ExpVector, C> nv = n.val;
        SortedMap<ExpVector, C> sv = S.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            //f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // now y can be zero
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenSolvablePolynomial scale and subtract a multiple.
     *
     * @param b scale factor.
     * @param a coefficient.
     * @param e exponent.
     * @param S GenSolvablePolynomial.
     * @return b * this - a * x<sup>e</sup> * S.
     */
    public GenSolvablePolynomial<C> scaleSubtractMultiple(C b, C a, ExpVector e, GenSolvablePolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiplyLeft(b);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiplyLeft(b);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiplyLeft(a.negate(), e);
        }
        if (b.isONE()) {
            return subtractMultiple(a, e, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenSolvablePolynomial<C> n = this.multiplyLeft(b);
        SortedMap<ExpVector, C> nv = n.val;
        GenSolvablePolynomial<C> s = S.multiplyLeft(e);
        SortedMap<ExpVector, C> sv = s.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            //f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // now y can be zero
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenSolvablePolynomial scale and subtract a multiple.
     *
     * @param b scale factor.
     * @param g scale exponent.
     * @param a coefficient.
     * @param e exponent.
     * @param S GenSolvablePolynomial.
     * @return a * x<sup>g</sup> * this - a * x<sup>e</sup> * S.
     */
    public GenSolvablePolynomial<C> scaleSubtractMultiple(C b, ExpVector g, C a, ExpVector e,
                                                          GenSolvablePolynomial<C> S) {
        if (a == null || S == null) {
            return this.multiplyLeft(b, g);
        }
        if (a.isZERO() || S.isZERO()) {
            return this.multiplyLeft(b, g);
        }
        if (this.isZERO() || b == null || b.isZERO()) {
            return S.multiplyLeft(a.negate(), e);
        }
        if (b.isONE() && g.isZERO()) {
            return subtractMultiple(a, e, S);
        }
        assert (ring.nvar == S.ring.nvar);
        GenSolvablePolynomial<C> n = this.multiplyLeft(b, g);
        SortedMap<ExpVector, C> nv = n.val;
        GenSolvablePolynomial<C> s = S.multiplyLeft(e);
        SortedMap<ExpVector, C> sv = s.val;
        for (Map.Entry<ExpVector, C> me : sv.entrySet()) {
            ExpVector f = me.getKey();
            //f = e.sum(f);
            C y = me.getValue(); // assert y != null
            y = a.multiply(y); // y can be zero now
            C x = nv.get(f);
            if (x != null) {
                x = x.subtract(y);
                if (!x.isZERO()) {
                    nv.put(f, x);
                } else {
                    nv.remove(f);
                }
            } else if (!y.isZERO()) {
                nv.put(f, y.negate());
            }
        }
        return n;
    }


    /**
     * GenSolvablePolynomial left monic, i.e. leadingCoefficient == 1. If
     * leadingCoefficient is not invertible returns this abs value.
     *
     * @return monic(this).
     */
    @Override
    public GenSolvablePolynomial<C> monic() {
        if (this.isZERO()) {
            return this;
        }
        C lc = leadingBaseCoefficient();
        if (!lc.isUnit()) {
            return (GenSolvablePolynomial<C>) this.abs();
        }
        try {
            C lm = lc.inverse();
            //System.out.println("lm = "+lm);
            return (GenSolvablePolynomial<C>) multiplyLeft(lm).abs();
        } catch (NotInvertibleException e) {
            logger.info("monic not invertible " + lc);
            //e.printStackTrace();
        }
        return this;
    }


    /**
     * GenSolvablePolynomial left division. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return quotient with this = quotient * S + remainder and
     * deg(remainder) &lt; deg(S) or remiander = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    // cannot @Override, @NoOverride
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> divide(GenSolvablePolynomial<C> S) {
        return quotientRemainder(S)[0];
    }


    /**
     * GenSolvablePolynomial remainder by left division. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return remainder with this = quotient * S + remainder and
     * deg(remainder) &lt; deg(S) or remiander = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    // cannot @Override, @NoOverride
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> remainder(GenSolvablePolynomial<C> S) {
        return quotientRemainder(S)[1];
    }


    /**
     * GenSolvablePolynomial left division with remainder. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder and
     * deg(remainder) &lt; deg(S) or remiander = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    // cannot @Override, @NoOverride
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C>[] quotientRemainder(GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException("lbcf not invertible " + c);
        }
        C ci = c.inverse();
        assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> q = ring.getZERO().copy();
        GenSolvablePolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                //System.out.println("FDQR: f = " + f + ", a = " + a);
                f = f.subtract(e);
                //a = ci.multiply(a); // multiplyLeft
                a = a.multiply(ci); // this is correct!
                q = (GenSolvablePolynomial<C>) q.sum(a, f);
                h = S.multiplyLeft(a, f);
                if (!h.leadingBaseCoefficient().equals(r.leadingBaseCoefficient())) {
                    throw new RuntimeException("something is wrong: r = " + r + ", h = " + h);
                }
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        GenSolvablePolynomial<C>[] ret = new GenSolvablePolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenSolvablePolynomial right division. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return quotient with this = S * quotient + remainder and
     * deg(remainder) &lt; deg(S) or remiander = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> rightDivide(GenSolvablePolynomial<C> S) {
        return rightQuotientRemainder(S)[0];
    }


    /**
     * GenSolvablePolynomial remainder by right division. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return remainder with this = S * quotient + remainder and
     * deg(remainder) &lt; deg(S) or remiander = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> rightRemainder(GenSolvablePolynomial<C> S) {
        return rightQuotientRemainder(S)[1];
    }


    /**
     * GenSolvablePolynomial right division with remainder. Fails, if exact
     * division by leading base coefficient is not possible. Meaningful only for
     * univariate polynomials over fields, but works in any case.
     *
     * @param S nonzero GenSolvablePolynomial with invertible leading
     *          coefficient.
     * @return [ quotient , remainder ] with this = S * quotient + remainder and
     * deg(remainder) &lt; deg(S) or remainder = 0.
     * @see PolyUtil#baseSparsePseudoRemainder(GenPolynomial, GenPolynomial)
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C>[] rightQuotientRemainder(GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if (!c.isUnit()) {
            throw new ArithmeticException("lbcf not invertible " + c);
        }
        C ci = c.inverse();
        assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> q = ring.getZERO().copy();
        GenSolvablePolynomial<C> r = this.copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                //System.out.println("FDQR: f = " + f + ", a = " + a);
                f = f.subtract(e);
                //a = a.multiplyLeft(ci); // not existing
                a = ci.multiply(a); // this is correct!
                q = (GenSolvablePolynomial<C>) q.sum(a, f);
                h = S.multiply(a, f);
                if (!h.leadingBaseCoefficient().equals(r.leadingBaseCoefficient())) {
                    throw new RuntimeException("something is wrong: r = " + r + ", h = " + h);
                }
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        GenSolvablePolynomial<C>[] ret = new GenSolvablePolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * RecSolvablePolynomial right coefficients from left coefficients.
     * <b>Note:</b> R is represented as a polynomial with left coefficients, the
     * implementation can at the moment not distinguish between left and right
     * coefficients.
     *
     * @return R = sum( X<sup>i</sup> b<sub>i</sub> ), with P =
     * sum(a<sub>i</sub> X<sup>i</sup> ) and eval(sum(X<sup>i</sup>
     * b<sub>i</sub>)) == sum(a<sub>i</sub> X<sup>i</sup>)
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> rightRecursivePolynomial() {
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
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) this;
        RecSolvablePolynomial<C> R = (RecSolvablePolynomial<C>) p.rightRecursivePolynomial();
        return (GenSolvablePolynomial<C>) R;
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
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> evalAsRightRecursivePolynomial() {
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
        RecSolvablePolynomial<C> p = (RecSolvablePolynomial<C>) this;
        RecSolvablePolynomial<C> R = (RecSolvablePolynomial<C>) p.evalAsRightRecursivePolynomial();
        return (GenSolvablePolynomial<C>) R;
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
    @SuppressWarnings({"unchecked"})
    public boolean isRightRecursivePolynomial(GenSolvablePolynomial<C> R) {
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
        RecSolvablePolynomial<C> q = (RecSolvablePolynomial<C>) R;
        return p.isRightRecursivePolynomial(q);
    }

}
