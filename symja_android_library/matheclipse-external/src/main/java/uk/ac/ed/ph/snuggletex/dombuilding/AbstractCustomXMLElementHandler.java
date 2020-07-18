/* $Id: AbstractCustomXMLElementHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.SimpleToken;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * Provides base functionality for the commands and environments that build custom
 * XML elements.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public abstract class AbstractCustomXMLElementHandler implements EnvironmentHandler, CommandHandler {
    
    /**
     * Subclasses should fill in to return true if they are building a block element or
     * false if they are building an inline element.
     */
    protected abstract boolean isBlock();
    
    public void handleEnvironment(final DOMBuilder builder, final Element parentElement,
            final EnvironmentToken token)
            throws SnuggleParseException {
        buildCustomElement(builder, parentElement, token.getOptionalArgument(), token.getArguments()[0],
                token.getArguments()[1], token.getContent());
    }
    
    public void handleCommand(final DOMBuilder builder, final Element parentElement,
            final CommandToken token)
            throws SnuggleParseException {
        buildCustomElement(builder, parentElement, token.getOptionalArgument(), token.getArguments()[0],
                token.getArguments()[1], token.getArguments()[2]);
    }
    
    protected void buildCustomElement(final DOMBuilder builder, final Element parentElement,
            final ArgumentContainerToken attrsToken, final ArgumentContainerToken namespaceToken,
            final ArgumentContainerToken qNameToken, final ArgumentContainerToken contentToken)
            throws SnuggleParseException {
        /* Get namespace URI and make sure that it really is a URI */
        String namespaceUri = builder.extractStringValue(namespaceToken);
        if (builder.validateURI(parentElement, namespaceToken, namespaceUri)==null) {
            /* Invalid URI; error will have been recorded already */
        }
        
        /* Extract qName */
        String qName = builder.extractStringValue(qNameToken);
        
        /* Now try to build resulting element */
        Element resultElement;
        try {
            resultElement = builder.getDocument().createElementNS(namespaceUri, qName);
        }
        catch (DOMException e) {
            if (e.code==DOMException.INVALID_CHARACTER_ERR || e.code==DOMException.NAMESPACE_ERR) {
                /* Error: Must be a bad QName */
                builder.appendOrThrowError(parentElement, qNameToken, CoreErrorCode.TDEX01, qName);
                return;
            }
            throw e;
        }
        parentElement.appendChild(resultElement);
        
        /* Add attributes, if provided */
        if (attrsToken!=null) {
            extractAttributes(builder, parentElement, resultElement, attrsToken);
        }
        
        /* Handle content */
        builder.handleTokens(resultElement, contentToken, isBlock());
    }
    
    private void extractAttributes(final DOMBuilder builder, final Element parentElement,
            final Element resultElement, final ArgumentContainerToken attrsToken)
            throws SnuggleParseException {
        CommandToken resolvedAttrToken;
        for (FlowToken rawAttrToken : attrsToken) {
            if (rawAttrToken.isCommand(CorePackageDefinitions.CMD_XML_ATTR)) {
                resolvedAttrToken = (CommandToken) rawAttrToken;
                String namespace = builder.extractStringValue(resolvedAttrToken.getArguments()[0]);
                String qName = builder.extractStringValue(resolvedAttrToken.getArguments()[1]);
                String value = builder.extractStringValue(resolvedAttrToken.getArguments()[2]);
                resultElement.setAttributeNS(namespace, qName, value);
            }
            else if (rawAttrToken.getType()==TokenType.ERROR) {
                /* Keep this */
                builder.handleToken(parentElement, rawAttrToken);
            }
            else if (rawAttrToken.getType()==TokenType.TEXT_MODE_TEXT && ((SimpleToken) rawAttrToken).getSlice().isWhitespace()) {
                /* Whitespace can be ignored */
            }
            else {
                /* Error: Expected a CoreEngineConfigurer.XML_ATTR token here */
                builder.appendOrThrowError(parentElement, rawAttrToken, CoreErrorCode.TDEX00);
            }
        }
    }
}
