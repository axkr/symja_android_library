/*
 * $Id$
 */

package edu.jas.root;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.structure.GcdRingElem;


/**
 * RealAlgebraicNumber root tuple.
 *
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class RealRootTuple<C extends GcdRingElem<C> & Rational> implements Serializable {


    /**
     * Tuple of RealAlgebraicNumbers.
     */
    public final List<RealAlgebraicNumber<C>> tuple;


    /**
     * Constructor.
     *
     * @param t list of roots.
     */
    public RealRootTuple(List<RealAlgebraicNumber<C>> t) {
        if (t == null) {
            throw new IllegalArgumentException("null tuple not allowed");
        }
        tuple = t;
    }


    /**
     * String representation of tuple.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return tuple.toString();
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this Rectangle.
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer("[");
        boolean first = true;
        for (RealAlgebraicNumber<C> r : tuple) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(r.toScript());
        }
        return sb.toString();
    }


    /**
     * Contains a point.
     *
     * @param c real root tuple representing a point.
     * @return true if c is contained in this root tuple, else false.
     */
    public boolean contains(RealRootTuple<C> c) {
        return contains(c.tuple);
    }


    /**
     * Contains a point.
     *
     * @param c list of real algebraic numbers representing a point.
     * @return true if c is contained in this root tuple, else false.
     */
    public boolean contains(List<RealAlgebraicNumber<C>> c) {
        int i = 0;
        for (RealAlgebraicNumber<C> r : tuple) {
            RealAlgebraicNumber<C> cn = c.get(i++);
            boolean t = r.ring.root.contains(cn.ring.root);
            if (!t) {
                return false;
            }
        }
        return true;
    }


    /**
     * Random point of real root tuple.
     *
     * @return a random point contained in this real root tuple.
     */
    public List<C> randomPoint() {
        List<C> tp = new ArrayList<C>(tuple.size());
        for (RealAlgebraicNumber<C> r : tuple) {
            C rp = r.ring.root.randomPoint();
            tp.add(rp);
        }
        return tp;
    }


    /**
     * Refine root isolating intervals.
     *
     * @param eps desired interval length.
     */
    public void refineRoot(BigRational eps) {
        for (RealAlgebraicNumber<C> r : tuple) {
            r.ring.refineRoot(eps);
        }
        return;
    }


    /**
     * Copy this.
     *
     * @return a copy of this.
     */
    public RealRootTuple<C> copy() {
        return new RealRootTuple<C>(new ArrayList<RealAlgebraicNumber<C>>(tuple));
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        RealRootTuple<C> a = null;
        try {
            a = (RealRootTuple<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return tuple.equals(a.tuple);
    }


    /**
     * Hash code for this Rectangle.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return tuple.hashCode();
    }


    /**
     * Rational approximation of each coordinate.
     *
     * @return list of coordinate points.
     */
    public List<BigRational> getRational() {
        List<BigRational> center = new ArrayList<BigRational>(tuple.size());
        for (RealAlgebraicNumber<C> rr : tuple) {
            BigRational r = rr.getRational();
            center.add(r);
        }
        return center;
    }


    /**
     * Decimal approximation of each coordinate.
     *
     * @return list of coordinate points.
     */
    public List<BigDecimal> decimalMagnitude() {
        List<BigDecimal> center = new ArrayList<BigDecimal>(tuple.size());
        for (RealAlgebraicNumber<C> rr : tuple) {
            BigDecimal r = rr.decimalMagnitude();
            center.add(r);
        }
        return center;
    }


    /**
     * Rational Length.
     *
     * @return max |v_i|;
     */
    public BigRational rationalLength() {
        BigRational len = new BigRational();
        for (RealAlgebraicNumber<C> rr : tuple) {
            BigRational r = rr.ring.root.rationalLength();
            int s = len.compareTo(r);
            if (s < 0) {
                len = r;
            }
        }
        return len;
    }


    /**
     * Signum.
     *
     * @return ?;
     */
    public int signum() {
        int s = 0;
        for (RealAlgebraicNumber<C> rr : tuple) {
            int rs = rr.signum();
            if (rs != 0) {
                s = rs;
            }
        }
        return s;
    }

}
