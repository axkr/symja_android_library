/* $Id: MrowHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Builds MathML <tt>mrow</tt>s.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public class MrowHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        Element mrow = builder.appendMathMLElement(parentElement, "mrow");
        builder.handleTokens(mrow, token.getArguments()[0], false);
    }
}
