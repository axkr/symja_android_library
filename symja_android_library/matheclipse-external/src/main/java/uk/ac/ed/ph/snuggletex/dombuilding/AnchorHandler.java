/* $Id: AnchorHandler.java 525 2010-01-05 14:07:36Z davemckain $
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
 * Handles the <tt>\\anchor</tt> command, generating an XHTML anchor.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class AnchorHandler implements CommandHandler {

    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        /* Make sure anchor is a proper XML name */
        ArgumentContainerToken anchorToken = token.getArguments()[0];
        String rawAnchorId = builder.extractStringValue(anchorToken);
        String validatedId = builder.validateXMLId(parentElement, anchorToken, rawAnchorId);
        if (validatedId!=null) {
            /* Create <a/> element */
            Element aElement = builder.appendXHTMLElement(parentElement, "a");
            aElement.setAttribute("id", validatedId);
            aElement.setAttribute("name", validatedId);
            aElement.setIdAttribute("id", true);
        }
    }
}
