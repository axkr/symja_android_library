/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Serializable;


/**
 * Container for overlap words.
 * A container of four words l1, r1, l2, r2. 
 * @author Heinz Kredel
 */

public class Overlap implements Serializable {

    public final Word l1;
    public final Word r1;
    public final Word l2;
    public final Word r2;


    /**
     * Constructor.
     */
    public Overlap(Word l1, Word r1, Word l2, Word r2) {
        this.l1 = l1;
        this.r1 = r1;
        this.l2 = l2;
        this.r2 = r2;
    }


    /**
     * Is word overlap.
     * @param u word
     * @param v word
     * @return true if l1 * u * r1 = l2 * v * r2, else false.
     */
    public boolean isOverlap(Word u, Word v) {
        Word a = l1.multiply(u).multiply(r1);
        Word b = l2.multiply(v).multiply(r2);
        boolean t = a.equals(b);
        if ( !t ) {
            System.out.println("a = " + a + " !=  b = " + b);
        }
        return t;
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("Overlap[");
        s.append(l1);
        s.append(", ");
        s.append(r1);
        s.append(", ");
        s.append(l2);
        s.append(", ");
        s.append(r2);
        s.append("]");
        return s.toString();
    }

}
