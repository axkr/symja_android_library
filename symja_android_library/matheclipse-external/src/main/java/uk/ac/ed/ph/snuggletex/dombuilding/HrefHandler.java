/* $Id: HrefHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import java.net.URI;

import org.w3c.dom.Element;

/**
 * Handles the SnuggleTeX-specific <tt>\\href</tt> command for easily creating web links.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class HrefHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        /* Extract required link target and resolve */
        String href = token.getArguments()[0].getSlice().extract().toString();
        URI resolvedLink = builder.resolveLink(parentElement, token, href);
        if (resolvedLink==null) {
            /* Bad link - error will have been appended so do nothing here */
            return;
        }
        
        /* Create <a> element with correct href attribute */
        Element aElement = builder.appendXHTMLElement(parentElement, "a");
        aElement.setAttribute("href", resolvedLink.toString());
        
        /* Now show link text, which is other provided explicitly via optional argument or
         * will just be the same as the 'href'
         */
        ArgumentContainerToken optionalArgument = token.getOptionalArgument();
        if (optionalArgument!=null) {
            builder.handleTokens(aElement, optionalArgument, true);
        }
        else {
            builder.appendTextNode(aElement, href, true);
        }
    }
}
