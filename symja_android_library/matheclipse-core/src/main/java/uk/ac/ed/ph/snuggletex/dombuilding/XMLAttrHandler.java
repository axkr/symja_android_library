/* $Id: XMLAttrHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Handles lonely instances of <tt>\\xmlAttr</tt>, which results in {@link CoreErrorCode#TDEX02}
 * being emitted.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class XMLAttrHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        builder.appendOrThrowError(parentElement, token, CoreErrorCode.TDEX02);
    }

}
