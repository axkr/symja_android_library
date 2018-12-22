/* $Id: ErrorGroup.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorGroup;

/**
 * Interface representing a grouping for a set of related {@link ErrorCode}s.
 * <p>
 * (This is an interface rather than a class as it's convenient for "providers"
 * of error codes to create an enumeration that implements this interface.
 * This is how {@link CoreErrorGroup} works in the SnuggleTeX Core module.)
 * 
 * @see ErrorCode
 * @see SnugglePackage
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface ErrorGroup {
    
    /** 
     * Short name for this group, used as a key when formatting error messages via
     * {@link SnugglePackage#getErrorMessageBundle()}
     */
    String getName();

    /** {@link SnugglePackage} defining this group */
    SnugglePackage getPackage();
    
}