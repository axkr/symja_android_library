/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.Word;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NoncomRingElem;
import edu.jas.structure.NotDivisibleException;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.QuotPair;
import edu.jas.structure.Value;


/**
 * WordResidue ring element based on GenWordPolynomial with GcdRingElem
 * interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class WordResidue<C extends GcdRingElem<C>> implements GcdRingElem<WordResidue<C>>,
                NoncomRingElem<WordResidue<C>>, QuotPair<GenWordPolynomial<C>>, Value<GenWordPolynomial<C>> {


    /**
     * WordResidue class factory data structure.
     */
    public final WordResidueRing<C> ring;


    /**
     * Value part of the element data structure.
     */
    public final GenWordPolynomial<C> val;


    /**
     * Flag to remember if this residue element is a unit. -1 is unknown, 1 is
     * unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /**
     * The constructor creates a WordResidue object from a ring factory.
     * @param r solvable residue ring factory.
     */
    public WordResidue(WordResidueRing<C> r) {
        this(r, r.ring.getZERO(), 0);
    }


    /**
     * The constructor creates a WordResidue object from a ring factory and a
     * polynomial.
     * @param r solvable residue ring factory.
     * @param a solvable polynomial.
     */
    public WordResidue(WordResidueRing<C> r, GenWordPolynomial<C> a) {
        this(r, a, -1);
    }


    /**
     * The constructor creates a WordResidue object from a ring factory, a
     * polynomial and an indicator if a is a unit.
     * @param r solvable residue ring factory.
     * @param a solvable polynomial.
     * @param u isunit indicator, -1, 0, 1.
     */
    public WordResidue(WordResidueRing<C> r, GenWordPolynomial<C> a, int u) {
        ring = r;
        val = ring.ideal.normalform(a); //.monic() no go
        if (u == 0 || u == 1) {
            isunit = u;
            return;
        }
        if (val.isZERO()) {
            isunit = 0;
            return;
        }
        if (ring.isField()) {
            isunit = 1;
            return;
        }
        if (val.isUnit()) {
            isunit = 1;
            //} else { // not possible
            //isunit = 0;
        }
        isunit = -1;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public WordResidueRing<C> factory() {
        return ring;
    }


    /**
     * Value. Returns the value.
     * @see edu.jas.structure.Value#value()
     */
    public GenWordPolynomial<C> value() {
        return val;
    }


    /**
     * Numerator. Returns the value.
     * @see edu.jas.structure.QuotPair#numerator()
     */
    public GenWordPolynomial<C> numerator() {
        return val;
    }


    /**
     * Denominator. Returns 1.
     * @see edu.jas.structure.QuotPair#denominator()
     */
    public GenWordPolynomial<C> denominator() {
        return ring.ring.getONE();
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public WordResidue<C> copy() {
        return new WordResidue<C>(ring, val, isunit);
    }


    /**
     * Is WordResidue zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.isZERO();
    }


    /**
     * Is WordResidue one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.isONE();
    }


    /**
     * Is WordResidue unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (isunit > 0) {
            return true;
        }
        if (isunit == 0) {
            return false;
        }
        // not jet known
        boolean u = ring.ideal.isUnit(val);
        //System.out.println("WordResidue.isUnit " + val);
        if (u) {
            isunit = 1; // seems to be wrong for solvable polynomial rings
        } else {
            isunit = 0;
        }
        return isunit > 0;
    }


    /**
     * Is WordResidue a constant.
     * @return true if this.val is a constant polynomial, else false.
     */
    public boolean isConstant() {
        return val.isConstant();
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            return val.toString();
        }
        return "WordResidue[ " + val.toString() + " mod " + ring.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return val.toScript();
        // return "PolyWordResidue( " + val.toScript() 
        //                         + ", " + ring.toScript() + " )";
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * WordResidue comparison.
     * @param b WordResidue.
     * @return sign(this-b), 0 means that this and b are equivalent in this
     *         residue class ring.
     */
    @Override
    public int compareTo(WordResidue<C> b) {
        GenWordPolynomial<C> v = b.val;
        if (!ring.equals(b.ring)) {
            v = ring.ideal.normalform(v);
        }
        return val.compareTo(v);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @return true means that this and b are equivalent in this residue class
     *         ring.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof WordResidue)) {
            return false;
        }
        WordResidue<C> a = null;
        try {
            a = (WordResidue<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return compareTo(a) == 0;
    }


    /**
     * Hash code for this residue.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        h = 37 * h + val.hashCode();
        return h;
    }


    /**
     * WordResidue absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public WordResidue<C> abs() {
        return new WordResidue<C>(ring, val.abs(), isunit);
    }


    /**
     * WordResidue summation.
     * @param S WordResidue.
     * @return this+S.
     */
    public WordResidue<C> sum(WordResidue<C> S) {
        return new WordResidue<C>(ring, val.sum(S.val));
    }


    /**
     * WordResidue negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public WordResidue<C> negate() {
        return new WordResidue<C>(ring, val.negate(), isunit);
    }


    /**
     * WordResidue signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /**
     * WordResidue subtraction.
     * @param S WordResidue.
     * @return this-S.
     */
    public WordResidue<C> subtract(WordResidue<C> S) {
        return new WordResidue<C>(ring, val.subtract(S.val));
    }


    /**
     * WordResidue left division.
     * @param S WordResidue.
     * @return left, with left*S = this
     */
    public WordResidue<C> divide(WordResidue<C> S) {
        if (ring.isField()) {
            return multiply(S.inverse());
        }
        try {
            return multiply(S.inverse());
        } catch (NotInvertibleException ignored) {
            System.out.println("catch: " + ignored);
            //ignored.printStackTrace();
            // ignored
        } catch (UnsupportedOperationException ignored) {
            //System.out.println("catch: " + ignored);
            //ignored.printStackTrace();
            // ignored
        }
        List<GenWordPolynomial<C>> L = new ArrayList<GenWordPolynomial<C>>(1);
        L.add(ring.ring.getZERO());
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        //@SuppressWarnings("unused")
        GenWordPolynomial<C> x = ring.bb.red.leftNormalform(L, V, val);
        GenWordPolynomial<C> y = L.get(0);
        GenWordPolynomial<C> t = y.multiply(S.val).sum(x);
        if (!val.equals(t)) {
            throw new NotDivisibleException("val != t: val = " + val + ", t = " + t);
        }
        return new WordResidue<C>(ring, y);
    }


    /**
     * WordResidue two-sided division.
     * @param S WordResidue.
     * @return [left, right] with left*S*right + remainder = this.
     */
    @SuppressWarnings("unchecked")
    public WordResidue<C>[] twosidedDivide(WordResidue<C> S) {
        List<GenWordPolynomial<C>> L = new ArrayList<GenWordPolynomial<C>>(1);
        L.add(ring.ring.getZERO());
        List<GenWordPolynomial<C>> R = new ArrayList<GenWordPolynomial<C>>(1);
        R.add(ring.ring.getZERO());
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        GenWordPolynomial<C> x = ring.bb.red.normalform(L, R, V, val);
        GenWordPolynomial<C> y = L.get(0);
        GenWordPolynomial<C> z = R.get(0);
        if (!ring.bb.red.isReductionNF(L, R, V, val, x)) {
            throw new NotDivisibleException("val != x: val = " + val + ", S.val = " + S.val);
        }
        WordResidue<C>[] ret = new WordResidue[2];
        ret[0] = new WordResidue<C>(ring, y);
        ret[1] = new WordResidue<C>(ring, z);
        return ret;
    }


    /**
     * WordResidue right division.
     * @param S WordResidue.
     * @return right, with S * right = this
     */
    public WordResidue<C> rightDivide(WordResidue<C> S) {
        if (ring.isField()) {
            return multiply(S.inverse());
        }
        try {
            return multiply(S.inverse());
        } catch (NotInvertibleException ignored) {
            System.out.println("catch: " + ignored);
            //ignored.printStackTrace();
            // ignored
        } catch (UnsupportedOperationException ignored) {
            //System.out.println("catch: " + ignored);
            //ignored.printStackTrace();
            // ignored
        }
        List<GenWordPolynomial<C>> R = new ArrayList<GenWordPolynomial<C>>(1);
        R.add(ring.ring.getZERO());
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        //@SuppressWarnings("unused")
        GenWordPolynomial<C> x = ring.bb.red.rightNormalform(R, V, val);
        GenWordPolynomial<C> y = R.get(0);
        GenWordPolynomial<C> t = S.val.multiply(y).sum(x);
        if (!val.equals(t)) {
            throw new NotDivisibleException("val != t: val = " + val + ", t = " + t);
        }
        return new WordResidue<C>(ring, y);
    }


    /**
     * WordResidue inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined.
     */
    public WordResidue<C> inverse() {
        GenWordPolynomial<C> x = ring.ideal.inverse(val);
        WordResidue<C> xp = new WordResidue<C>(ring, x, 1);
        if (xp.isZERO()) {
            throw new NotInvertibleException(
                            "(" + x + ") * (" + val + ") = " + x.multiply(val) + " = 0 mod " + ring.ideal);
        }
        if (!xp.multiply(this).isONE()) {
            throw new NotInvertibleException(
                            "(" + x + ") * (" + val + ") = " + x.multiply(val) + " != 1 mod " + ring.ideal);
        }
        return xp;
    }


    /**
     * WordResidue remainder.
     * @param S WordResidue.
     * @return this - (this/S) * S.
     */
    public WordResidue<C> remainder(WordResidue<C> S) {
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        GenWordPolynomial<C> x = ring.bb.red.leftNormalform(V, val);
        return new WordResidue<C>(ring, x);
    }


    /**
     * WordResidue right remainder.
     * @param S WordResidue.
     * @return r = this - S * (S/right), where S * right = this.
     */
    public WordResidue<C> rightRemainder(WordResidue<C> S) {
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        GenWordPolynomial<C> x = ring.bb.red.rightNormalform(V, val);
        return new WordResidue<C>(ring, x);
    }


    /**
     * WordResidue two-sided remainder.
     * @param S WordResidue.
     * @return r = this - left*S*right.
     */
    public WordResidue<C> twosidedRemainder(WordResidue<C> S) {
        List<GenWordPolynomial<C>> V = new ArrayList<GenWordPolynomial<C>>(1);
        V.add(S.val);
        GenWordPolynomial<C> x = ring.bb.red.normalform(V, val);
        WordResidue<C> ret = new WordResidue<C>(ring, x);
        return ret;
    }


    /**
     * WordResidue multiplication.
     * @param S WordResidue.
     * @return this*S.
     */
    public WordResidue<C> multiply(WordResidue<C> S) {
        GenWordPolynomial<C> x = val.multiply(S.val);
        int i = -1;
        if (isunit == 1 && S.isunit == 1) {
            i = 1;
        } else if (isunit == 0 || S.isunit == 0) {
            i = 0;
        }
        return new WordResidue<C>(ring, x, i);
    }


    /**
     * WordResidue multiplication.
     * @param S GenWordPolynomial.
     * @return this*S.
     */
    public WordResidue<C> multiply(GenWordPolynomial<C> S) {
        GenWordPolynomial<C> x = val.multiply(S);
        int i = -1;
        if (isunit == 1 && S.isUnit()) {
            i = 1;
        } else if (isunit == 0 || !S.isUnit()) {
            i = 0;
        }
        return new WordResidue<C>(ring, x, i);
    }


    /*
     * WordResidue multiplication.
     * @param s coefficient.
     * @return this*s.
     */
    public WordResidue<C> multiply(C s) {
        GenWordPolynomial<C> x = val.multiply(s);
        int i = -1;
        if (isunit == 1 && s.isUnit()) {
            i = 1;
        } else if (isunit == 0 || !s.isUnit()) {
            i = 0;
        }
        return new WordResidue<C>(ring, x, i);
    }


    /**
     * WordResidue multiplication.
     * @param e word.
     * @return this*e.
     */
    public WordResidue<C> multiply(Word e) {
        GenWordPolynomial<C> x = val.multiply(e);
        int i = -1;
        if (isunit == 1 && e.isONE()) {
            i = 1;
        } else if (isunit == 0 || !e.isONE()) {
            i = 0;
        }
        return new WordResidue<C>(ring, x, i);
    }


    /**
     * WordResidue monic.
     * @return this with monic value part.
     */
    public WordResidue<C> monic() {
        return new WordResidue<C>(ring, val.monic(), isunit);
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public WordResidue<C> gcd(WordResidue<C> b) {
        throw new UnsupportedOperationException("gcd not implemented");
        // GenWordPolynomial<C> x = ring.engine.gcd(val, b.val);
        // int i = -1; // gcd might become a unit
        // if (x.isONE()) {
        //     i = 1;
        // } else {
        //     System.out.println("WordResidue gcd = " + x);
        // }
        // if (isunit == 1 && b.isunit == 1) {
        //     i = 1;
        // }
        // return new WordResidue<C>(ring, x, i);
    }


    /**
     * Extended greatest common divisor. <b>Note: </b>Not implemented, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public WordResidue<C>[] egcd(WordResidue<C> b) {
        throw new UnsupportedOperationException("egcd not implemented");
    }
}
