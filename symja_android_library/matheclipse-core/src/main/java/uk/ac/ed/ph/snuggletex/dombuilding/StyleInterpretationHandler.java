/* $Id: StyleInterpretationHandler.java 548 2010-04-15 16:10:35Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.semantics.StyleDeclarationInterpretation;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import org.w3c.dom.Element;

/**
 * Builds islands of styled content, e.g. the <tt>bf</tt> environment, or single-argument
 * commands like <tt>\\textrm</tt> and <tt>\\underline</tt>.
 * 
 * <h2>Notes</h2>
 * 
 * <ul>
 *   <li>This code assumes that any commands handled here have one argument containing the content</li>
 *   <li>
 *     Note that some old TeX commands like <tt>\\bf</tt> will have been converted to environments
 *     before getting here!
 *   </li>
 * </ul>
 *
 * @author  David McKain
 * @version $Revision: 548 $
 */
public final class StyleInterpretationHandler implements CommandHandler, EnvironmentHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        StyleDeclarationInterpretation styleInterpretation = (StyleDeclarationInterpretation) token
            .getCommand()
            .getInterpretation(InterpretationType.STYLE_DECLARATION);
        handleContent(builder, parentElement, styleInterpretation, token.getArguments()[0]);
    }
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        StyleDeclarationInterpretation styleInterpretation = (StyleDeclarationInterpretation) token
            .getEnvironment()
            .getInterpretation(InterpretationType.STYLE_DECLARATION);
        handleContent(builder, parentElement, styleInterpretation, token.getContent());
    }
    
    
    public void handleContent(final DOMBuilder builder, final Element parentElement,
            final StyleDeclarationInterpretation interpretation, final ArgumentContainerToken contentContainerToken)
            throws SnuggleParseException {
        Element result = parentElement; /* Default if we can't do any sensible styling */
        if (builder.isBuildingMathMLIsland()) {
            /* We're doing MathML. We create an <mstyle/> element, but only if we can reasonably
             * handle this style.
             * 
             * Note: Even though there is no \mathsc{...}, we can legally end up here if doing
             * something like \mbox{\sc ....} so we'll ignore unsupported stylings, rather than
             * failing.
             * 
             * Regression Note: If the content is truly empty, we'll generate an empty <mrow/>
             * instead of an empty <mstyle/>
             */
            String mathVariant = interpretation.getTargetMathMLMathVariantName();
            if (mathVariant!=null && !contentContainerToken.getContents().isEmpty()) {
                result = builder.appendMathMLElement(parentElement, "mstyle");
                result.setAttribute("mathvariant", mathVariant);
            }
            else {
                result = builder.appendMathMLElement(parentElement, "mrow");
            }
        }
        else {
            /* We're doing XHTML */
            boolean hasBlockContent = false;
            for (FlowToken contentToken : contentContainerToken) {
                if (contentToken.getTextFlowContext()==TextFlowContext.START_NEW_XHTML_BLOCK) {
                    hasBlockContent = true;
                }
            }
            if (hasBlockContent && interpretation.getTargetBlockXHTMLElementName()!=null) {
                result = builder.appendXHTMLElement(parentElement, interpretation.getTargetBlockXHTMLElementName());
                if (interpretation.getTargetBlockCSSClassName()!=null) {
                    builder.applyCSSStyle(result, interpretation.getTargetBlockCSSClassName());
                }
            }
            else if (!hasBlockContent && interpretation.getTargetInlineXHTMLElementName()!=null) {
                result = builder.appendXHTMLElement(parentElement, interpretation.getTargetInlineXHTMLElementName());
                if (interpretation.getTargetInlineCSSClassName()!=null) {
                    builder.applyCSSStyle(result, interpretation.getTargetInlineCSSClassName());
                }
            }
        }
        /* Descend as normal */
        builder.handleTokens(result, contentContainerToken, false);
    }
}
