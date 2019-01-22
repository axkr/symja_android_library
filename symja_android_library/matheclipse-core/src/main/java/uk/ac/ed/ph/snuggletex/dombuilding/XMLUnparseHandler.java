/* $Id: XMLUnparseHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.SerializationSpecifier;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.utilities.SerializationOptions;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Handles the special <tt>xmlUnparsed</tt> command and environment.
 * <p>
 * This has the effect of handling content as normal, before effectively putting the results
 * inside a CDATA section. This is very useful for documenting how SnuggleTeX works, but may
 * be useful for other purposes as well.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class XMLUnparseHandler implements EnvironmentHandler, CommandHandler {

    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        handle(builder, parentElement, token.getArguments()[0], false);
    }
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        handle(builder, parentElement, token.getContent(), true);
    }
    
    private void handle(DOMBuilder builder, Element parentElement, 
            final ArgumentContainerToken content, final boolean isBlock)
            throws SnuggleParseException {
        /* Build children as normal */
        builder.handleTokens(parentElement, content, true);
        
        /* Serialize child content to text and replace all children with this text inside
         * an appropriate container  */
        SerializationSpecifier options = new SerializationOptions();
        options.setIndenting(isBlock);
        options.setEncoding("UTF-8");
        String parentContentUnparsed = XMLUtilities.serializeNodeChildren(builder.getSessionContext().getStylesheetManager(),
                parentElement, options);
        NodeList childNodes = parentElement.getChildNodes();
        for (int i=childNodes.getLength()-1; i>=0; i--) {
            parentElement.removeChild(childNodes.item(i));
        }
        Element resultElement = builder.appendXHTMLTextElement(parentElement,
                isBlock ? "pre" : "tt",
                parentContentUnparsed, true);
        resultElement.setAttribute("class", "unparsed-xml");
    }
}
