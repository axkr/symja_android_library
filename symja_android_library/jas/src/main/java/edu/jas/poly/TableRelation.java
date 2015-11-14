/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Serializable;

import edu.jas.structure.RingElem;


/**
 * TableRelation container for storage and printing in RelationTable.
 * @author Heinz Kredel
 */
public class TableRelation<C extends RingElem<C>> implements Serializable {


    /**
     * First ExpVector of the data structure.
     */
    public final ExpVector e;


    /**
     * Second ExpVector of the data structure.
     */
    public final ExpVector f;


    /**
     * GenSolvablePolynomial of the data structure.
     */
    public final GenSolvablePolynomial<C> p;


    /**
     * Constructor to setup the data structure.
     * @param e first term.
     * @param f second term.
     * @param p product polynomial.
     */
    public TableRelation(ExpVector e, ExpVector f, GenSolvablePolynomial<C> p) {
        this.e = e;
        this.f = f;
        this.p = p;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("TableRelation[");
        s.append("" + e);
        s.append(" .*. ");
        s.append("" + f);
        s.append(" = ");
        s.append("" + p);
        s.append("]");
        return s.toString();
    }

}
