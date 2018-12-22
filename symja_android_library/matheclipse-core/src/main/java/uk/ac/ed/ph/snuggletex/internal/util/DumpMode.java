/* $Id: DumpMode.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

/**
 * Enumerates the different options that can be used when specifying how to dump
 * out properties or types in {@link ObjectDumper}.
 * <p>
 * They are listed in order of verbosity.
 *
 * (This is copied from <tt>ph-commons-util</tt>.)
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public enum DumpMode {
    
    /**
     * Ignores the given property.
     */
    IGNORE,

    /**
     * Calls {@link Object#toString()} on the given type or property.
     */
    TO_STRING,
    
    /**
     * Uses {@link ObjectDumper} to do a deep dump of the given type or property.
     */
    DEEP,
    
    ;
}
