/* $Id: StringUtilities.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

import java.util.Iterator;

/**
 * Some vaguely useful String-related methods that are used from
 * time to time.
 * <p>
 * (This is a cut-down version of the same class in <tt>ph-commons-util</tt>.)
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class StringUtilities {
	
    /** Shared instance of an empty array of Strings, which is sometimes useful! */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Joins the given collection of Objects using the given
     * separator and each Object's normal toString() method.
     * <p>
     * For example, joining the collection "a", "b", "c" with "/"
     * gives "a/b/c".
     *
     * @param objects collection of Objects to join
     * @param separator separator to use
     * @return objects joined using the given separator.
     */
    public static String join(final Iterable<? extends Object> objects, final CharSequence separator) {
        StringBuilder result = new StringBuilder();
        for (Iterator<? extends Object> iter = objects.iterator(); iter.hasNext(); ) {
            result.append(iter.next().toString());
            if (iter.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    /**
     * Same as {@link #join(Iterable, CharSequence)} but simply takes an array
     * of Objects.
     *
     * @param objects array of Objects to join
     * @param separator separator to use
     */
    public static String join(final Object[] objects, final CharSequence separator) {
        return join(objects, separator, 0, objects.length);
    }
    
    /**
     * Version of {@link #join(Object[], CharSequence)} that allows you to pass in
     * a {@link StringBuilder} that the result will be built up in. This is useful if you need
     * to do add in other stuff later on.
     *
     * @param resultBuilder StringBuilder to append results to
     * @param objects array of Objects to join
     * @param separator separator to use
     */
    public static void join(final StringBuilder resultBuilder, final Object[] objects, 
    		final CharSequence separator) {
        join(resultBuilder, objects, separator, 0, objects.length);
    }
    
    /**
     * Version of {@link #join(Object[], CharSequence)} that allows you to specify a range of
     * indices in the array to join. This can be useful in some cases.
     *
     * @param objects array of Objects to join
     * @param separator separator to use
     * @param startIndex first index to join
     * @param endIndex index after last one to join
     */
    public static String join(final Object[] objects, final CharSequence separator,
    		final int startIndex, final int endIndex) {
        StringBuilder result = new StringBuilder();
        join(result, objects, separator, startIndex, endIndex);
        return result.toString();
    }
    
    /**
     * Version of {@link #join(Object[], CharSequence, int, int)} that allows you to pass in
     * a {@link StringBuilder} that the result will be built up in. This is useful if you need
     * to do add in other stuff later on.
     *
     * @param resultBuilder StringBuilder to append results to
     * @param objects array of Objects to join
     * @param separator separator to use
     * @param startIndex first index to join
     * @param endIndex index after last one to join
     */
    public static void join(final StringBuilder resultBuilder, final Object[] objects,
    		final CharSequence separator, final int startIndex, final int endIndex) {
        boolean hasDoneFirst = false;
        for (int i=startIndex; i<endIndex; i++) {
            if (hasDoneFirst) {
                resultBuilder.append(separator);
            }
            resultBuilder.append(objects[i].toString());
            hasDoneFirst = true;
        }
    }
    
    //------------------------------------------------------------------------
    
    /**
     * Tests whether the given String is null or empty ("").
     */
    public static boolean isNullOrEmpty(final String string) {
        return string==null || string.length()==0;
    }

    /**
     * Convenience method that turns a String to null if it is empty (i.e. "") or null.
     *
     * @param string
     * @return same string if it is non-null and non-empty, otherwise null.
     */
    public static String nullIfEmpty(final String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    /**
     * Convenience method that turns a String to an empty String ("") if it is null.
     *
     * @param string
     * @return same string if it is non-empty, otherwise null.
     */
    public static String emptyIfNull(final String string) {
        return string!=null ? string : "";
    }
    
    //------------------------------------------------------------------------

    /**
     * Trivial helper method to convert a boolean into either
     * "yes" or "no" depending on its state.
     *
     * @param state boolean to convert
     * @return "yes" if true, "no" if false.
     */
    public static String toYesNo(final boolean state) {
        return state ? "yes" : "no";
    }
    
    /**
     * Trivial helper method to convert a boolean into either
     * "true" or "false" depending on its state.
     *
     * @param state boolean to convert
     * @return "true" if true, "false" if false.
     */
    public static String toTrueFalse(final boolean state) {
        return state ? "true" : "false";
    }

    /**
     * Converts the given String argument to a boolean using
     * the scheme "yes"=>true, "no"=>false. Any other value
     * results in an IllegalArgumentException.
     *
     * @param value
     * @return true if "yes", false if "no"
     *
     * @throws IllegalArgumentException if value if null or
     *   neither "yes" or "no"
     */
    public static boolean fromYesNo(final String value) {
    	return fromBinaryValues(value, "yes", "no");
    }

    /**
     * Converts the given String argument to a boolean using
     * the scheme "true"=>true, "false"=>false. Any other value
     * results in an IllegalArgumentException.
     *
     * @param value
     * @return true if "yes", false if "no"
     *
     * @throws IllegalArgumentException if value if null or
     *   neither "yes" or "no"
     */
    public static boolean fromTrueFalse(final String value) {
    	return fromBinaryValues(value, "true", "false");
    }
    
    /**
     * Converts the given String argument to a boolean using
     * the scheme trueValue => true, falseValue => false.
     * Any other value results in an IllegalArgumentException.
     *
     * @param value
     * @param trueValue value returning true
     * @param falseValue value returning false
     *
     * @throws IllegalArgumentException if value if null or
     *   neither trueValue nor falseValue
     */
    public static boolean fromBinaryValues(final String value,
    		final String trueValue, final String falseValue) {
        if (value!=null) {
            if (value.equals(trueValue)) {
                return true;
            }
            else if (value.equals(falseValue)) {
                return false;
            }
        }
        throw new IllegalArgumentException("Argument must be "
        		+ trueValue + " or " + falseValue);
    }    
}
