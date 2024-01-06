
/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Map;

import edu.jas.structure.RingElem;


/**
 * IndexListMonomial class. Represents pairs of index lists and coefficients.
 * Adaptor for Map.Entry.
 * @author Heinz Kredel
 */

public final class IndexListMonomial<C extends RingElem<C>> {


    /**
     * IndexList of monomial.
     */
    public final IndexList e;


    /**
     * Coefficient of monomial.
     */
    public final C c;


    /**
     * Constructor of word monomial.
     * @param me a MapEntry.
     */
    public IndexListMonomial(Map.Entry<IndexList, C> me) {
        this(me.getKey(), me.getValue());
    }


    /**
     * Constructor of word monomial.
     * @param e index list.
     * @param c coefficient.
     */
    public IndexListMonomial(IndexList e, C c) {
        this.e = e;
        this.c = c;
    }


    /**
     * Getter for index list.
     * @return index list.
     */
    public IndexList indexlist() {
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
     * String representation of Monomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return c.toString() + " " + e.toString();
    }

}
