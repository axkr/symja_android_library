/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * SolvableResidue ring element based on GenSolvablePolynomial with RingElem
 * interface. Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class SolvableResidue<C extends GcdRingElem<C>> implements GcdRingElem<SolvableResidue<C>> {


    /**
     * SolvableResidue class factory data structure.
     */
    public final SolvableResidueRing<C> ring;


    /**
     * Value part of the element data structure.
     */
    public final GenSolvablePolynomial<C> val;


    /**
     * Flag to remember if this residue element is a unit. -1 is unknown, 1 is
     * unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /**
     * The constructor creates a SolvableResidue object from a ring factory.
     * @param r solvable residue ring factory.
     */
    public SolvableResidue(SolvableResidueRing<C> r) {
        this(r, r.ring.getZERO(), 0);
    }


    /**
     * The constructor creates a SolvableResidue object from a ring factory and
     * a polynomial.
     * @param r solvable residue ring factory.
     * @param a solvable polynomial.
     */
    public SolvableResidue(SolvableResidueRing<C> r, GenSolvablePolynomial<C> a) {
        this(r, a, -1);
    }


    /**
     * The constructor creates a SolvableResidue object from a ring factory, a
     * polynomial and an indicator if a is a unit.
     * @param r solvable residue ring factory.
     * @param a solvable polynomial.
     * @param u isunit indicator, -1, 0, 1.
     */
    public SolvableResidue(SolvableResidueRing<C> r, GenSolvablePolynomial<C> a, int u) {
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
    public SolvableResidueRing<C> factory() {
        return ring;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public SolvableResidue<C> copy() {
        return new SolvableResidue<C>(ring, val, isunit);
    }


    /**
     * Is SolvableResidue zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.isZERO();
    }


    /**
     * Is SolvableResidue one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.isONE();
    }


    /**
     * Is SolvableResidue unit.
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
        if (u) {
            isunit = 1; // seems to be wrong for solvable polynomial rings
        } else {
            isunit = 0;
        }
        return isunit > 0;
    }


    /**
     * Is SolvableResidue a constant.
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
            return val.toString(ring.ring.getVars());
        }
        return "SolvableResidue[ " + val.toString() + " mod " + ring.toString() + " ]";
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
        //         return "PolySolvableResidue( " + val.toScript() 
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
     * SolvableResidue comparison.
     * @param b SolvableResidue.
     * @return sign(this-b), 0 means that this and b are equivalent in this
     *         residue class ring.
     */
    @Override
    public int compareTo(SolvableResidue<C> b) {
        GenSolvablePolynomial<C> v = b.val;
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
        if (!(b instanceof SolvableResidue)) {
            return false;
        }
        SolvableResidue<C> a = null;
        try {
            a = (SolvableResidue<C>) b;
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
     * SolvableResidue absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public SolvableResidue<C> abs() {
        return new SolvableResidue<C>(ring, (GenSolvablePolynomial<C>) val.abs(), isunit);
    }


    /**
     * SolvableResidue summation.
     * @param S SolvableResidue.
     * @return this+S.
     */
    public SolvableResidue<C> sum(SolvableResidue<C> S) {
        return new SolvableResidue<C>(ring, (GenSolvablePolynomial<C>) val.sum(S.val));
    }


    /**
     * SolvableResidue negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public SolvableResidue<C> negate() {
        return new SolvableResidue<C>(ring, (GenSolvablePolynomial<C>) val.negate(), isunit);
    }


    /**
     * SolvableResidue signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /**
     * SolvableResidue subtraction.
     * @param S SolvableResidue.
     * @return this-S.
     */
    public SolvableResidue<C> subtract(SolvableResidue<C> S) {
        return new SolvableResidue<C>(ring, (GenSolvablePolynomial<C>) val.subtract(S.val));
    }


    /**
     * SolvableResidue division.
     * @param S SolvableResidue.
     * @return this/S.
     */
    public SolvableResidue<C> divide(SolvableResidue<C> S) {
        if (ring.isField()) {
            return multiply(S.inverse());
        }
        List<GenSolvablePolynomial<C>> Q = new ArrayList<GenSolvablePolynomial<C>>(1);
        Q.add(ring.ring.getZERO());
        List<GenSolvablePolynomial<C>> V = new ArrayList<GenSolvablePolynomial<C>>(1);
        V.add(val);
        GenSolvablePolynomial<C> x = ring.bb.sred.leftNormalform(Q, V, S.val);
        GenSolvablePolynomial<C> y = Q.get(0);
        return new SolvableResidue<C>(ring, y);
    }


    /**
     * SolvableResidue inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined.
     */
    public SolvableResidue<C> inverse() {
        GenSolvablePolynomial<C> x = ring.ideal.inverse(val);
        SolvableResidue<C> xp = new SolvableResidue<C>(ring, x, 1);
        if ( xp.isZERO() ) {
            throw new NotInvertibleException("(" + x + ") * (" + val + ") = " + x.multiply(val) + " = 0 mod " + ring.ideal);
        }
        return xp;
    }


    /**
     * SolvableResidue remainder.
     * @param S SolvableResidue.
     * @return this - (this/S)*S.
     */
    public SolvableResidue<C> remainder(SolvableResidue<C> S) {
        List<GenSolvablePolynomial<C>> V = new ArrayList<GenSolvablePolynomial<C>>(1);
        V.add(val);
        GenSolvablePolynomial<C> x = ring.bb.sred.leftNormalform(V, S.val);
        return new SolvableResidue<C>(ring, x);
    }


    /**
     * SolvableResidue multiplication.
     * @param S SolvableResidue.
     * @return this*S.
     */
    public SolvableResidue<C> multiply(SolvableResidue<C> S) {
        GenSolvablePolynomial<C> x = val.multiply(S.val);
        int i = -1;
        if (isunit == 1 && S.isunit == 1) {
            i = 1;
        } else if (isunit == 0 || S.isunit == 0) {
            i = 0;
        }
        return new SolvableResidue<C>(ring, x, i);
    }


    /**
     * SolvableResidue monic.
     * @return this with monic value part.
     */
    public SolvableResidue<C> monic() {
        return new SolvableResidue<C>(ring, (GenSolvablePolynomial<C>) val.monic(), isunit);
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public SolvableResidue<C> gcd(SolvableResidue<C> b) {
        throw new UnsupportedOperationException("gcd not implemented");
        // GenSolvablePolynomial<C> x = ring.engine.gcd(val, b.val);
        // int i = -1; // gcd might become a unit
        // if (x.isONE()) {
        //     i = 1;
        // } else {
        //     System.out.println("SolvableResidue gcd = " + x);
        // }
        // if (isunit == 1 && b.isunit == 1) {
        //     i = 1;
        // }
        // return new SolvableResidue<C>(ring, x, i);
    }


    /**
     * Extended greatest common divisor. <b>Note: </b>Not implemented, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public SolvableResidue<C>[] egcd(SolvableResidue<C> b) {
        throw new UnsupportedOperationException("egcd not implemented");
    }
}
