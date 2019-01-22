/* $Id: MathStackrelHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Handles the <tt>\\stackrel</tt> command.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathStackrelHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        Element result = builder.appendMathMLElement(parentElement, "mover");

        /* Extract the top and bottom tokens */
        ArgumentContainerToken topToken = token.getArguments()[0];
        ArgumentContainerToken bottomToken = token.getArguments()[1];
        
        /* Generate MathML content (note the change of order!) */
        builder.handleMathTokensAsSingleElement(result, bottomToken);
        builder.handleMathTokensAsSingleElement(result, topToken);
    }
}
