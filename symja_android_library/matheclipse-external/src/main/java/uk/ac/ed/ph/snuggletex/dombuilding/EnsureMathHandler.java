/* $Id: EnsureMathHandler.java 525 2010-01-05 14:07:36Z davemckain $
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
 * This handles the <tt>\\ensuremath</tt> command.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class EnsureMathHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        ArgumentContainerToken contentToken = token.getArguments()[0];
        if (builder.isBuildingMathMLIsland()) {
            /* Already doing MathML so we just descend as normal */
            builder.handleTokens(parentElement, contentToken, false);
        }
        else {
            /* Not doing MathML yet so need to open a <math/>.
             * 
             * To do this, we simply delegate to MathEnvironmentBuilder
             */
            builder.buildMathElement(parentElement, token, contentToken, false);
        }
    }
}
