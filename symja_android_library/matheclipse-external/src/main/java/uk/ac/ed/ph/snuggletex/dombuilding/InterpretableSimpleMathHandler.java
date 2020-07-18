/* $Id: InterpretableSimpleMathHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * This builder calls back directly to the {@link DOMBuilder} to handle mathematical
 * tokens with certain types of simple semantic interpretations.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class InterpretableSimpleMathHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token) {
        builder.appendSimpleMathElement(parentElement, token);
    }
}
