/* $Id:ErrorCode.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import uk.ac.ed.ph.snuggletex.ErrorGroup;
import uk.ac.ed.ph.snuggletex.SnugglePackage;

/**
 * Defines the groupings for the various types of client-induced errors that can arise 
 * when using the functionality provided by the core SnuggleTeX module.
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum CoreErrorGroup implements ErrorGroup {
    
    TTE(), /* Tokenisation errors */
    TFE(), /* Token fixing errors */
    TDE(), /* DOM Building errors */

    ;
    
    public String getName() {
        return name();
    }

    public SnugglePackage getPackage() {
        return CorePackageDefinitions.getPackage();
    }
}
