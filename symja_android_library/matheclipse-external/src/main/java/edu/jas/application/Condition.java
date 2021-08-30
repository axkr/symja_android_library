/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.gbufd.MultiplicativeSet;
import edu.jas.gbufd.MultiplicativeSetSquarefree;
//import edu.jas.gbufd.MultiplicativeSetFactors;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Condition. Container for an ideal of polynomials considered to be zero and a
 * multiplicative set of polynomials considered to be non-zero.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class Condition<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = LogManager.getLogger(Condition.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Colors.
     */
    public static enum Color {
        GREEN, RED, WHITE
    };


    /**
     * Data structure for condition zero.
     */
    public final Ideal<C> zero;


    /**
     * Data structure for condition non-zero.
     */
    public final MultiplicativeSet<C> nonZero;


    /**
     * Condition constructor. Constructs an empty condition with squarefree
     * multiplicative set.
     * @param ring polynomial ring factory for coefficients.
     */
    public Condition(GenPolynomialRing<C> ring) {
        this(new Ideal<C>(ring), new MultiplicativeSetSquarefree<C>(ring));
        //this(new Ideal<C>(ring), new MultiplicativeSetFactors<C>(ring));
        //if (ring == null) { // too late to test
        //    throw new IllegalArgumentException("only for non null rings");
        //}
    }


    /**
     * Condition constructor. Constructs a condition with squarefree
     * multiplicative set.
     * @param z an ideal of zero polynomials.
     */
    public Condition(Ideal<C> z) {
        this(z, new MultiplicativeSetSquarefree<C>(z.list.ring));
        //this(z,new MultiplicativeSetFactors<C>(z.list.ring));
    }


    /**
     * Condition constructor.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(MultiplicativeSet<C> nz) {
        this(new Ideal<C>(nz.ring), nz);
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(Ideal<C> z, MultiplicativeSet<C> nz) {
        if (z == null || nz == null) {
            throw new IllegalArgumentException("only for non null condition parts");
        }
        zero = z;
        nonZero = nz;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Condition[ 0 == " + zero.getList().toString() + ", 0 != " + nonZero.mset.toString() + " ]";
    }


    /**
     * toScript.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        return "Condition[ 0 == " + zero.getList().toString() + ", 0 != " + nonZero.mset.toString() + " ]";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        Condition<C> c = null;
        try {
            c = (Condition<C>) ob;
        } catch (ClassCastException e) {
            return false;
        }
        if (c == null) {
            return false;
        }
        if (!zero.equals(c.zero)) {
            return false;
        }
        if (!nonZero.equals(c.nonZero)) {
            return false;
        }
        // better:
        //if ( nonZero.removeFactors(c.nonZero).size() != 0 ) {
        //    return false;
        //}
        //if ( c.nonZero.removeFactors(nonZero).size() != 0 ) {
        //    return false;
        //}
        return true;
    }


    /**
     * Hash code for this condition.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = zero.getList().hashCode();
        h = h << 17;
        h += nonZero.hashCode();
        // h = h << 11;
        // h += pairlist.hashCode();
        return h;
    }


    /**
     * Is empty condition.
     * @return true if this is the empty condition, else false.
     */
    public boolean isEmpty() {
        return (zero.isZERO() && nonZero.isEmpty());
    }


    /**
     * Is contradictory.
     * @return true if this condition is contradictory, else false.
     */
    public boolean isContradictory() {
        if (zero.isZERO()) {
            return false;
        }
        boolean t = zero.isONE();
        if (t) {
            logger.info("contradiction zero.isONE(): " + this);
            return t;
        }
        for (GenPolynomial<C> p : zero.getList()) {
            t = nonZero.contains(p);
            if (t) {
                logger.info("contradiction nonZero.contains(zero): " + this + ", pol = " + p);
                return t;
            }
        }
        for (GenPolynomial<C> p : nonZero.mset) {
            t = zero.contains(p);
            if (t) {
                logger.info("contradiction zero.contains(nonZero): " + this + ", pol = " + p);
                return t;
            }
        }
        return false;
    }


    /**
     * Extend condition with zero polynomial.
     * @param z a polynomial to be treated as zero.
     * @return new condition.
     */
    public Condition<C> extendZero(GenPolynomial<C> z) {
        // assert color(z) == white 
        z = z.monic();
        Ideal<C> idz = zero.sum(z);
        logger.info("added to ideal: " + z);

        Condition<C> nc = new Condition<C>(idz, nonZero);
        //if (true) {
        return nc.simplify();
        //}
        //return nc;
    }


    /**
     * Extend condition with non-zero polynomial.
     * @param nz a polynomial to be treated as non-zero.
     * @return new condition.
     */
    public Condition<C> extendNonZero(GenPolynomial<C> nz) {
        // assert color(nz) == white 
        GenPolynomial<C> n = zero.normalform(nz).monic();
        if (n == null || n.isZERO()) {
            return this;
        }
        MultiplicativeSet<C> ms = nonZero.add(n);
        logger.info("added to non zero: " + n);

        Condition<C> nc = new Condition<C>(zero, ms);
        //if (true) {
        return nc.simplify();
        //}
        //return nc;
    }


    /**
     * Simplify zero and non-zero polynomial conditions.
     * @return new simplified condition.
     */
    public Condition<C> simplify() {
        //if (false) { // no simplification
        //    return this;
        //}
        Ideal<C> idz = zero.squarefree();
        if (!idz.getList().equals(zero.getList())) {
            logger.info("simplify squarefree: " + zero.getList() + " to " + idz.getList());
        }
        List<GenPolynomial<C>> ml = idz.normalform(nonZero.mset);
        MultiplicativeSet<C> ms = nonZero;
        if (!ml.equals(nonZero.mset)) {
            if (ml.size() != nonZero.mset.size()) {
                logger.info("contradiction(==0):");
                logger.info("simplify normalform contradiction: " + nonZero.mset + " to " + ml);
                return null;
            }
            logger.info("simplify normalform: " + nonZero.mset + " to " + ml);
            ms = nonZero.replace(ml);
        }
        List<GenPolynomial<C>> Z = ms.removeFactors(idz.getList());
        if (!Z.equals(idz.getList())) {
            if (Z.size() != idz.getList().size()) { // never true
                logger.info("contradiction(!=0):");
                logger.info("simplify removeFactors contradiction: " + idz.getList() + " to " + Z);
                return null;
            }
            logger.info("simplify removeFactors: " + idz.getList() + " to " + Z);
            idz = new Ideal<C>(zero.getRing(), Z); // changes ideal
        }
        Condition<C> nc = new Condition<C>(idz, ms);
        if (nc.isContradictory()) { // eventually a factor became 1
            logger.info("simplify contradiction: " + nc);
            return null;
        }
        if (idz.equals(zero) && ms.equals(nonZero)) {
            return this;
        }
        logger.info("condition simplified: " + this + " to " + nc);
        //if (false) { // no further simplification
        //    return nc;
        //}
        return nc.simplify();
    }


    /**
     * Determine color of polynomial.
     * @param c polynomial to be colored.
     * @return color of c.
     */
    public Color color(GenPolynomial<C> c) {
        GenPolynomial<C> m = zero.normalform(c).monic();
        //non-sense: m = nonZero.removeFactors(m);
        if (m.isZERO()) { // zero.contains(m)
            // System.out.println("m in id = " + m);
            return Color.GREEN;
        }
        if (m.isConstant()) {
            // System.out.println("m constant " + m);
            return Color.RED;
        }
        if (nonZero.contains(m) || nonZero.contains(c)) {
            // System.out.println("m or c in nonzero " + m);
            return Color.RED;
        }
        //System.out.println("m white " + m + ", c = " + c);
        return Color.WHITE;
    }


    /**
     * Determine polynomial. If this condition does not determine the
     * polynomial, then a run-time exception is thrown.
     * @param A polynomial.
     * @return new determined colored polynomial.
     */
    public ColorPolynomial<C> determine(GenPolynomial<GenPolynomial<C>> A) {
        ColorPolynomial<C> cp = null;
        if (A == null) {
            return cp;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> green = zero;
        GenPolynomial<GenPolynomial<C>> red = zero;
        GenPolynomial<GenPolynomial<C>> white = zero;
        if (A.isZERO()) {
            cp = new ColorPolynomial<C>(green, red, white);
            return cp;
        }
        GenPolynomial<GenPolynomial<C>> Ap = A;
        GenPolynomial<GenPolynomial<C>> Bp;
        while (!Ap.isZERO()) {
            Map.Entry<ExpVector, GenPolynomial<C>> m = Ap.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> c = m.getValue();
            Bp = Ap.reductum();
            // System.out.println( "color(" + c + ") = " + color(c) );
            switch (color(c)) {
            case GREEN:
                green = green.sum(c, e);
                Ap = Bp;
                continue;
            case RED:
                red = red.sum(c, e);
                white = Bp;
                return new ColorPolynomial<C>(green, red, white);
                // since break is not possible
            case WHITE:
            default:
                logger.info("recheck undetermined coeff c = " + c + ", cond = " + this);
                if (extendZero(c) == null) { // contradiction if colored green
                    logger.info("recheck assume red");
                    red = red.sum(c, e); // assume red
                    white = Bp;
                    return new ColorPolynomial<C>(green, red, white);
                }
                if (extendNonZero(c) == null) { // contradiction if colored red
                    logger.info("recheck assume green");
                    green = green.sum(c, e); // assume green
                    Ap = Bp;
                    continue;
                }
                System.out.println("undetermined cond       = " + this);
                System.out.println("undetermined poly     A = " + A);
                System.out.println("undetermined poly green = " + green);
                System.out.println("undetermined poly   red = " + red);
                System.out.println("undetermined poly    Bp = " + Bp);
                System.out.println("undetermined coeff    c = " + c);
                throw new RuntimeException("undetermined, c is white = " + c);
                // is catched in minimalGB
            }
        }
        cp = new ColorPolynomial<C>(green, red, white);
        // System.out.println("determined = " + cp);
        return cp;
    }


    /**
     * Determine list of polynomials. If this condition does not determine all
     * polynomials, then a run-time exception is thrown. The returned list does
     * not contain polynomials with all green terms.
     * @param L list of polynomial.
     * @return new determined list of colored polynomials.
     */
    public List<ColorPolynomial<C>> determine(List<GenPolynomial<GenPolynomial<C>>> L) {
        List<ColorPolynomial<C>> cl = null;
        if (L == null) {
            return cl;
        }
        cl = new ArrayList<ColorPolynomial<C>>(L.size());
        for (GenPolynomial<GenPolynomial<C>> A : L) {
            ColorPolynomial<C> c = determine(A);
            if (c != null && !c.isZERO()) {
                cl.add(c);
            }
        }
        return cl;
    }


    /**
     * Re determine colored polynomial.
     * @param s colored polynomial.
     * @return determined colored polynomial wrt. this.conditions.
     */
    public ColorPolynomial<C> reDetermine(ColorPolynomial<C> s) {
        ColorPolynomial<C> p = determine(s.getEssentialPolynomial());
        // assume green terms stay green wrt. this condition
        GenPolynomial<GenPolynomial<C>> g = s.green.sum(p.green);
        p = new ColorPolynomial<C>(g, p.red, p.white);
        return p;
    }


    /**
     * Re determine list of colored polynomials.
     * @param S list of colored polynomials.
     * @return list of determined colored polynomials wrt. this.conditions.
     */
    public List<ColorPolynomial<C>> reDetermine(List<ColorPolynomial<C>> S) {
        if (S == null || S.isEmpty()) {
            return S;
        }
        List<ColorPolynomial<C>> P = new ArrayList<ColorPolynomial<C>>();
        for (ColorPolynomial<C> s : S) {
            ColorPolynomial<C> p = reDetermine(s);
            P.add(p);
        }
        return P;
    }


    /**
     * Is determined colored polynomial.
     * @param s colored polynomial.
     * @return true if the colored polynomial is correctly determined wrt.
     *         this.condition.
     */
    public boolean isDetermined(ColorPolynomial<C> s) {
        ColorPolynomial<C> p = determine(s.getPolynomial());
        boolean t = p.equals(s);
        if (!t) {
            System.out.println("not determined s    = " + s);
            System.out.println("not determined p    = " + p);
            System.out.println("not determined cond = " + this);
        }
        return t;
    }


    /**
     * Is determined list of colored polynomial.
     * @param S list of colored polynomials.
     * @return true if the colored polynomials in S are correctly determined
     *         wrt. this.condition.
     */
    public boolean isDetermined(List<ColorPolynomial<C>> S) {
        if (S == null) {
            return true;
        }
        for (ColorPolynomial<C> p : S) {
            if (!isDetermined(p)) {
                return false;
            }
        }
        return true;
    }

}
