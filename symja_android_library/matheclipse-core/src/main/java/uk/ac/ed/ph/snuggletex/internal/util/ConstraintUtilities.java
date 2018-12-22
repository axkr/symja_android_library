/* $Id: ConstraintUtilities.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

/**
 * Simple utility methods for enforcing various types of data constraints.
 * <p>
 * Some of the functionality here is reproduced in Spring (and in other 3rd
 * party JARs), but we need this to work at the client-side too and may opt not
 * to such 3rd party products there. Regardless, this is trivial anyway!
 * 
 * (This is copied from <tt>ph-commons-util</tt>.)
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class ConstraintUtilities {

    public static void ensureNotNull(Object value) {
        ensureNotNull(value, "Object");
    }

    /**
     * Checks that the given object is non-null, throwing an
     * IllegalArgumentException if the check fails. If the check succeeds then
     * nothing happens.
     *
     * @param value object to test
     * @param objectName name to give to supplied Object when constructing Exception message.
     *
     * @throws IllegalArgumentException if an error occurs.
     */
    public static void ensureNotNull(Object value, String objectName) {
        if (value==null) {
            throw new IllegalArgumentException(objectName + " must not be null");
        }
    }
}
