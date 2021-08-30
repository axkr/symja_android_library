
/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Map;

import edu.jas.structure.ElemFactory;
// import edu.jas.structure.RingFactory;
import edu.jas.structure.Element;
import edu.jas.structure.RingElem;


/**
 * Monomial class. Represents pairs of exponent vectors and coefficients.
 * Adaptor for Map.Entry.
 * @author Heinz Kredel
 */
public final class Monomial<C extends RingElem<C>> implements Element<Monomial<C>> {


    /**
     * Exponent of monomial.
     */
    public final ExpVector e;


    /**
     * Coefficient of monomial.
     */
    public final C c;


    /**
     * Constructor of monomial.
     * @param me a MapEntry.
     */
    public Monomial(Map.Entry<ExpVector, C> me) {
        this(me.getKey(), me.getValue());
    }


    /**
     * Constructor of monomial.
     * @param e exponent.
     * @param c coefficient.
     */
    public Monomial(ExpVector e, C c) {
        this.e = e;
        this.c = c;
    }


    /**
     * Getter for exponent.
     * @return exponent.
     */
    public ExpVector exponent() {
        return e;
    }


    /**
     * Getter for coefficient.
     * @return coefficient.
     */
    public C coefficient() {
        return c;
    }


    /**
     * Clone this Element.
     * @return Creates and returns a copy of this Element.
     */
    public Monomial<C> copy() {
        return new Monomial<C>(e, c);
    }


    /**
     * String representation of Monomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return c.toString() + " " + e.toString();
    }


    /**
     * Script representation of Monomial.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        if (c.isZERO()) {
            return "0";
        }
        StringBuffer sb = new StringBuffer();
        if (!c.isONE()) {
            sb.append(c.toScript());
            if (e.signum() != 0) {
                sb.append(" * ");
            }
        }
        sb.append(e.toScript());
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python and Ruby case
        throw new UnsupportedOperationException("no factory for Monomial");
    }


    /**
     * Get the corresponding element factory.
     * @return null, factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ElemFactory<Monomial<C>> factory() {
        //return new GenPolynomialRing<C>((RingFactory<C>)c.factory(), e.length());
        throw new UnsupportedOperationException("no factory for Monomial");
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof Monomial)) {
            return false;
        }
        Monomial<C> b = (Monomial<C>) B;
        return (compareTo(b) == 0);
    }


    /**
     * hashCode.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = e.hashCode();
        h = (h << 4) + c.hashCode();
        return h;
    }


    /**
     * Monomial comparison.
     * @param S Monomial.
     * @return SIGN(this-S).
     */
    @Override
    public int compareTo(Monomial<C> S) {
        if (S == null) {
            return 1;
        }
        int s = e.compareTo(S.e);
        if (s != 0) {
            return s;
        }
        return c.compareTo(S.c);
    }

}
