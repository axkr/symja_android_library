/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


/**
 * Container for lists of overlap words.
 * List of Overlaps.
 * @author Heinz Kredel
 */

public class OverlapList implements Serializable {

    public final List<Overlap> ols;


    /**
     * Constructor.
     */
    public OverlapList() {
        ols = new ArrayList<Overlap>();
    }


    /**
     * Add to list.
     */
    public void add(Overlap ol) {
        ols.add(ol);
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ols.toString();
    }


    /**
     * Is word overlap list.
     * @param u word
     * @param v word
     * @return true if l1 * u * r1 = l2 * v * r2 for all overlaps, else false.
     */
    public boolean isOverlap(Word u, Word v) {
        for (Overlap ol : ols ) {
            if ( !ol.isOverlap(u,v) ) {
                return false;
            }
        }
        return true;
    }

}
