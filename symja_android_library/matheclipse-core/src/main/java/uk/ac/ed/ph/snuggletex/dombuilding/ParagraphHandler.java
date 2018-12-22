/* $Id: ParagraphHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * This builds the content of a (fixed) {@link CorePackageDefinitions#CMD_PARAGRAPH} token.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class ParagraphHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        buildParagraph(builder, parentElement, token.getArguments()[0].getContents());
    }
    
    /**
     * Builds a single paragraph, being careful to consider how best to represent it in the output
     * tree at the existing position.
     * 
     * @param builder
     * @param parentElement
     * @param inlineContent
     * @throws DOMException
     * @throws SnuggleParseException
     */
    private void buildParagraph(DOMBuilder builder, Element parentElement, List<FlowToken> inlineContent)
            throws SnuggleParseException {
        Element resultElement;
        boolean isInline;
        if (builder.isBuildingMathMLIsland()) {
            /* Paragraphs inside MathML are not allowed!
             * 
             * So we won't create an element. Instead, once building reaching the text content,
             * it will be wrapped inside an <mtext/>
             */
            resultElement = parentElement;
            isInline = true;
        }
        else {
            resultElement = builder.appendXHTMLElement(parentElement, "p");
            isInline = false;
        }
        
        /* Handle content */
        builder.handleTokens(resultElement, inlineContent, !isInline);
    }
}
