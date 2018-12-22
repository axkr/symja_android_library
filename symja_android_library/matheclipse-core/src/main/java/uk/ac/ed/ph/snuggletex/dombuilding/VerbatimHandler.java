/* $Id:VerbatimBuilder.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.ErrorToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import org.w3c.dom.Element;

/**
 * Handles the <tt>verbatim</tt> environment and <tt>\\verb<//tt> command.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class VerbatimHandler implements CommandHandler, EnvironmentHandler {
    
    /** Set to handled 'starred' variants, e.g <tt>\\verb*</tt> */
    private final boolean starred;
    
    public VerbatimHandler(final boolean starred) {
        this.starred = starred;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token) {
        ArgumentContainerToken argumentToken = token.getArguments()[0];
        String verbContent = argumentToken.getSlice().extract().toString();
        if (starred) {
            verbContent = verbContent.replace(' ', '\u2423'); /* Convert spaces to open boxes */
        }
        else {
            verbContent = verbContent.replace(' ', '\u00a0'); /* Convert spaces to non-breaking spaces */
        }
        Element verbatimElement = builder.appendXHTMLElement(parentElement, "tt");
        verbatimElement.setAttribute("class", "verb");
        builder.appendTextNode(verbatimElement, verbContent, false);
        appendEmbeddedErrors(builder, parentElement, argumentToken);
    }
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token) {
        String verbatimContent = token.getContent().getSlice().extract().toString();
        Element verbatimElement = builder.appendXHTMLElement(parentElement, "pre");
        verbatimElement.setAttribute("class", "verbatim");
        builder.appendTextNode(verbatimElement, verbatimContent, false);
        appendEmbeddedErrors(builder, parentElement, token.getContent());
    }
    
    private void appendEmbeddedErrors(DOMBuilder builder, Element parentElement, ArgumentContainerToken content) {
        /* Extract any errors within the argument token */
        for (FlowToken flowToken : content.getContents()) {
            if (flowToken.getType()==TokenType.ERROR) {
                builder.appendErrorElement(parentElement, (ErrorToken) flowToken);
            }
        }
    }
}
