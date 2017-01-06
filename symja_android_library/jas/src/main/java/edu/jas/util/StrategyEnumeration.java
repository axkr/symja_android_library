/*
 * $Id: StrategyEnumeration.java 5686 2017-01-03 08:42:06Z kredel $
 */

package edu.jas.util;


/**
 * StrategyEnumeration. This class names possible / implemented strategies for
 * thread pools.
 * @author Heinz Kredel
 */

public final class StrategyEnumeration {


    public static final StrategyEnumeration FIFO = new StrategyEnumeration();


    public static final StrategyEnumeration LIFO = new StrategyEnumeration();


    private StrategyEnumeration() {
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        if (this == FIFO) {
            return "FIFO strategy";
        }
        return "LIFO strategy";
    }

}
