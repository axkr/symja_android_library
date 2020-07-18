/* $Id:TextFlowContext.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

/**
 * This enumeration specifies how text-based {@link FlowToken}s should be handled when being merged
 * into the resulting XHTML DOM.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum TextFlowContext {
    
    /** This token will start a new XHTML Block */
    START_NEW_XHTML_BLOCK,
    
    /** This token is allowed in inline XHTML */
    ALLOW_INLINE,
    
    /** This token makes no output and can be used anywhere */
    IGNORE,
    
    ;

}
