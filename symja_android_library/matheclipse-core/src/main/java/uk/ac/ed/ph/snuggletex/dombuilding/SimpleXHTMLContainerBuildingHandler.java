/* $Id: SimpleXHTMLContainerBuildingHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;

import org.w3c.dom.Element;

/**
 * This trivial handler takes an Environment or 1-argument Command and simply maps it to
 * an XHTML element with the given name and CSS class. It then descends into the content as
 * normal.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public class SimpleXHTMLContainerBuildingHandler implements CommandHandler, EnvironmentHandler {
    
    private final String xhtmlElementName;
    private final String cssClassName;
    
    public SimpleXHTMLContainerBuildingHandler(final String xhtmlElementName) {
        this(xhtmlElementName, null);
    }
    
    public SimpleXHTMLContainerBuildingHandler(final String xhtmlElementName, final String cssClassName) {
        this.xhtmlElementName = xhtmlElementName;
        this.cssClassName = cssClassName;
    }

    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        handleContent(builder, parentElement, token.getArguments()[0]);
    }
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        handleContent(builder, parentElement, token.getContent());
    }
    
    protected void handleContent(DOMBuilder builder, Element parentElement, ArgumentContainerToken contentToken)
            throws SnuggleParseException {
        Element containerElement = builder.appendXHTMLElement(parentElement, xhtmlElementName);
        if (cssClassName!=null) {
            builder.applyCSSStyle(containerElement, cssClassName);
        }
        /* Descend into content */
        builder.handleTokens(containerElement, contentToken.getContents(), true);
    }
}
