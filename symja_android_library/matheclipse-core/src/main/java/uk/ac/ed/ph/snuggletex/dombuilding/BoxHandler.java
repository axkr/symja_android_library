/* $Id: BoxHandler.java 537 2010-03-19 12:03:12Z davemckain $
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
 * Handles <tt>\\mbox</tt>, <tt>\\fbox</tt> and friends.
 *
 * @author  David McKain
 * @version $Revision: 537 $
 */
public final class BoxHandler implements CommandHandler {
    
    private final String xhtmlClassName;
    
    public BoxHandler(final String xhtmlClassName) {
        this.xhtmlClassName = xhtmlClassName;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        /* We just descend into contents - \mbox doesn't actually "do" anything to the output
         * though its children will output different things because of a combination of being
         * in LR mode and the XML application the parent element belongs.
         */
        Element containerElement;
        if (builder.isBuildingMathMLIsland()) {
            containerElement = builder.appendMathMLElement(parentElement, "mrow");
        }
        else {
            containerElement = builder.appendXHTMLElement(parentElement, "span");
            builder.applyCSSStyle(containerElement, xhtmlClassName);
        }
        builder.handleTokens(containerElement, token.getArguments()[0], false);
    }

}
