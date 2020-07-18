/* $Id: ListEnvironmentHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.definitions.W3CConstants;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import org.w3c.dom.Element;

/**
 * This handles LaTeX list environments (i.e. <tt>itemize</tt> and <tt>enumerate</tt>).
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class ListEnvironmentHandler implements EnvironmentHandler, CommandHandler {
    
    /**
     * Builds the actual List environment
     */
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        String listElementName = null;
        BuiltinEnvironment environment = token.getEnvironment();
        if (environment==CorePackageDefinitions.ENV_ITEMIZE) {
            listElementName = "ul";
        }
        else if (environment==CorePackageDefinitions.ENV_ENUMERATE) {
            listElementName = "ol";
        }
        else {
            throw new SnuggleLogicException("No logic to handle list environment " + environment.getTeXName());
        }
        Element listElement = builder.appendXHTMLElement(parentElement, listElementName);
        for (FlowToken contentToken : token.getContent()) {
            if (contentToken.isCommand(CorePackageDefinitions.CMD_LIST_ITEM)) {
                builder.handleToken(listElement, contentToken);
            }
            else if (contentToken.getType()==TokenType.ERROR) {
                /* We'll append errors immediately *after* the list environment */
                builder.handleToken(parentElement, contentToken);
            }
            else {
                /* List environments should only contain list items. This should have
                 * been sorted at token fixing so we've got a logic fault if we get here!
                 */
                throw new SnuggleLogicException("List environments can only contain list items - this should have been handled earlier");
            }
        }
    }
    
    /**
     * Builds list items.
     */
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken itemToken)
            throws SnuggleParseException {
        if (itemToken.isCommand(CorePackageDefinitions.CMD_LIST_ITEM)) {
            /* Right, this is one of the special LIST_ITEM tokens, creating during the fixing
             * stage when they are allowed.
             * 
             * Make sure we're building a list */
            if (builder.isParentElement(parentElement, W3CConstants.XHTML_NAMESPACE, "ul", "ol")) {
                Element listItem = builder.appendXHTMLElement(parentElement, "li");
                builder.handleTokens(listItem, itemToken.getArguments()[0], true);
            }
            else {
                /* List items should only appear inside list environments. But since they
                 * can't be specified by clients, this must be a logic fault!
                 */
                throw new SnuggleLogicException("List item outside environment - this should not have occurred");
            }
        }
        else if (itemToken.isCommand(CorePackageDefinitions.CMD_ITEM)) {
            /* This is a standard LaTeX \item. This would have been substituted if it was used
             * in a legal position so we must conclude that it cannot be used here.
             */
            builder.appendOrThrowError(parentElement, itemToken, CoreErrorCode.TDEL00);
        }

    }
}
