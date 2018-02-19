/*
 * $Id$
 */

package edu.jas.fd;


import java.io.Serializable;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the co-factors of left-right GCD computation. Invariant is left
 * * coA * right = polyA and left * coB * right = polyB.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GCDcoFactors<C extends GcdRingElem<C>> implements Serializable {


    /**
     * GCD algorithm to use for verification.
     */
    public final GreatestCommonDivisorAbstract<C> fd;


    /**
     * Original polynomial A of the GCD computation.
     */
    public final GenSolvablePolynomial<C> polyA;


    /**
     * Original polynomial B of the GCD computation.
     */
    public final GenSolvablePolynomial<C> polyB;


    /**
     * Co-factor of A.
     */
    public final GenSolvablePolynomial<C> coA;


    /**
     * Co-factor of B.
     */
    public final GenSolvablePolynomial<C> coB;


    /**
     * Left GCD of A and B.
     */
    public final GenSolvablePolynomial<C> left;


    /**
     * Right GCD of A and B.
     */
    public final GenSolvablePolynomial<C> right;


    /**
     * Constructor.
     *
     * @param g  GCD algorithm to use for verification.
     * @param a  polynomial A.
     * @param b  polynomial B.
     * @param ca polynomial coA.
     * @param cb polynomial coB.
     * @param l  polynomial left GCD.
     * @param r  polynomial right GCD.
     */
    public GCDcoFactors(GreatestCommonDivisorAbstract<C> g, GenSolvablePolynomial<C> a,
                        GenSolvablePolynomial<C> b, GenSolvablePolynomial<C> ca, GenSolvablePolynomial<C> cb,
                        GenSolvablePolynomial<C> l, GenSolvablePolynomial<C> r) {
        fd = g;
        polyA = a;
        polyB = b;
        coA = ca;
        coB = cb;
        left = l;
        right = r;
    }


    /**
     * Test if the invariants of this are fulfilled.
     *
     * @return true if x * (left * coA * right) = y * (polyA), for x, y with x *
     * lc(left * coA * right) == y * lc(polyA) and x * (left * coB *
     * right) == y * (polyB), for x, y with x * lc(left * coB * right)
     * == y * lc(polyB).
     */
    public boolean isGCD() {
        GenSolvablePolynomial<C> a, ap, bp;
        //C l = left.leadingBaseCoefficient();
        //C c1 = fd.leftBaseContent((GenSolvablePolynomial<C>)a.abs());
        //C c2 = fd.leftBaseContent((GenSolvablePolynomial<C>)polyA.abs());
        //System.out.println("c1 = " + c1 + ", c2 = " + c2 + ", c1/l = " + c1.leftDivide(l) + ", c2/l = " + c2.leftDivide(l));
        //System.out.println("c1 = " + c1 + ", c2 = " + c2 + ", l\\c1 = " + c1.rightDivide(l) + ", l\\c2 = " + c2.rightDivide(l));
        //System.out.println("c1%l = " + c1.leftRemainder(l) + ", c2%l = " + c2.leftRemainder(l));
        //GenSolvablePolynomial<C> a = left.multiply(right).multiply(coA); // left right coA
        //a = (GenSolvablePolynomial<C>)fd.leftBasePrimitivePart(a).abs();
        //System.out.println("a = " + a);
        //if (! a.equals(fd.leftBasePrimitivePart(polyA).abs())) {
        //    System.out.println("a = " + a + ",\nA != " + polyA);
        //    return false;
        //}
        //C d1 = fd.leftBaseContent((GenSolvablePolynomial<C>)b.abs());
        //C d2 = fd.leftBaseContent((GenSolvablePolynomial<C>)polyB.abs());
        //System.out.println("d1 = " + d1 + ", d2 = " + d2 + ", d1/l = " + d1.leftDivide(l) + ", d2/l = " + d2.leftDivide(l));
        //System.out.println("d1 = " + d1 + ", d2 = " + d2 + ", l\\d1 = " + d1.rightDivide(l) + ", l\\d2 = " + d2.rightDivide(l));
        //System.out.println("d1%l = " + d1.leftRemainder(l) + ", d2%l = " + d2.leftRemainder(l));
        //GenSolvablePolynomial<C> b = left.multiply(right).multiply(coB);
        //b = (GenSolvablePolynomial<C>)fd.leftBasePrimitivePart(b).abs();
        //System.out.println("b = " + b);
        //if (! b.abs().equals(fd.leftBasePrimitivePart(polyB).abs())) {
        //    System.out.println("b = " + b + ",\nB != " + polyB);
        //    return false;
        //}
        // check via Ore condition
        a = left.multiply(coA).multiply(right);
        C c1 = a.leadingBaseCoefficient();
        C c2 = polyA.leadingBaseCoefficient();
        C[] oc = fd.leftOreCond(c1, c2);
        ap = a.multiplyLeft(oc[0]);
        bp = polyA.multiplyLeft(oc[1]);
        if (!ap.equals(bp)) {
            //System.out.println("a: ap_l = " + ap + ", bp = " + bp);
            oc = fd.rightOreCond(c1, c2);
            ap = a.multiply(oc[0]);
            bp = polyA.multiply(oc[1]);
            if (!ap.equals(bp)) {
                System.out.println("a: ap_r = " + ap + ", bp = " + bp);
                return false;
            }
        }
        a = left.multiply(coB).multiply(right);
        c1 = a.leadingBaseCoefficient();
        c2 = polyB.leadingBaseCoefficient();
        oc = fd.leftOreCond(c1, c2);
        ap = a.multiplyLeft(oc[0]);
        bp = polyB.multiplyLeft(oc[1]);
        if (!ap.equals(bp)) {
            //System.out.println("b: ap_l = " + ap + ", bp = " + bp);
            oc = fd.rightOreCond(c1, c2);
            ap = a.multiply(oc[0]);
            bp = polyB.multiply(oc[1]);
            if (!ap.equals(bp)) {
                System.out.println("b: ap_r = " + ap + ", bp = " + bp);
                return false;
            }
        }
        return true;
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(left.toString());
        sb.append(" * ");
        sb.append(coA.toString());
        sb.append(" * ");
        sb.append(right.toString());
        sb.append(" == ");
        sb.append(polyA.toString());
        sb.append(",\n ");
        sb.append(left.toString());
        sb.append(" * ");
        sb.append(coB.toString());
        sb.append(" * ");
        sb.append(right.toString());
        sb.append(" == ");
        sb.append(polyB.toString());
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this container.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer();
        sb.append(left.toScript());
        sb.append(" * ");
        sb.append(coA.toScript());
        sb.append(" * ");
        sb.append(right.toScript());
        sb.append(" == ");
        sb.append(polyA.toString());
        sb.append(" && ");
        sb.append(left.toScript());
        sb.append(" * ");
        sb.append(coB.toScript());
        sb.append(" * ");
        sb.append(right.toScript());
        sb.append(" == ");
        sb.append(polyB.toString());
        sb.append(" ");
        return sb.toString();
    }


    /**
     * Hash code for this GCDcoFactors.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = polyA.hashCode();
        h = (h << 7);
        h += polyB.hashCode();
        h = (h << 7);
        h += coA.hashCode();
        h = (h << 7);
        h += coB.hashCode();
        h = (h << 7);
        h += left.hashCode();
        h = (h << 7);
        h += right.hashCode();
        return h;
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (B == null) {
            return false;
        }
        if (!(B instanceof GCDcoFactors)) {
            return false;
        }
        GCDcoFactors<C> a = (GCDcoFactors<C>) B;
        return this.compareTo(a) == 0;
    }


    /**
     * Comparison.
     *
     * @param facs gcd co-factors container.
     * @return sign(this.polyA-facs.polyA) lexicographic &gt;
     * sign(this.polyB-facs.polyB) lexicographic &gt;
     * sign(this.coA-facs.coA) lexicographic &gt;
     * sign(this.coB-facs.coB) lexicographic &gt;
     * sign(this.left-facs.left) lexicographic &gt;
     * sign(this.right-facs.right).
     */
    public int compareTo(GCDcoFactors<C> facs) {
        int s = polyA.compareTo(facs.polyA);
        if (s != 0) {
            return s;
        }
        s = polyB.compareTo(facs.polyB);
        if (s != 0) {
            return s;
        }
        s = coA.compareTo(facs.coA);
        if (s != 0) {
            return s;
        }
        s = coB.compareTo(facs.coB);
        if (s != 0) {
            return s;
        }
        s = left.compareTo(facs.left);
        if (s != 0) {
            return s;
        }
        s = right.compareTo(facs.right);
        if (s != 0) {
            return s;
        }
        return 0;
    }

}
