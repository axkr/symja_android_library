/*
 * $Id$
 */

package edu.jas.util;


// import org.apache.log4j.Logger;


/**
 * Array utilities. For example copyOf from Java 6. <b>Note:</b> unused at the
 * moment since it is not working in Java 5.
 * @author Heinz Kredel
 */
public class ArrayUtil {


    //private static final Logger logger = Logger.getLogger(ArrayUtil.class);
    // private static boolean debug = logger.isDebugEnabled();


    /**
     * Copy the specified array.
     * @param original array.
     * @param newLength new array length.
     * @return copy of original.
     */
    public static <T> T[] copyOf(T[] original, int newLength) {
        T[] copy = (T[]) new Object[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }


    /**
     * Copy the specified array.
     * @param original array.
     * @return copy of original.
     */
    public static <T> T[] copyOf(T[] original) {
        return copyOf(original, original.length);
    }

}
