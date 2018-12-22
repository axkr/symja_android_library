/* $Id: MathComplexCommandHandler.java 525 2010-01-05 14:07:36Z davemckain $
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
 * Handles complex Math commands (i.e. ones taking arguments) that map very easily onto
 * MathML elements where each argument becomes a child element of the resulting MathML
 * container.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathComplexCommandHandler implements CommandHandler {
    
    private final String elementName;
    
    public MathComplexCommandHandler(final String elementName) {
        this.elementName = elementName;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        Element result = builder.appendMathMLElement(parentElement, elementName);
        for (ArgumentContainerToken argument : token.getArguments()) {
            builder.handleMathTokensAsSingleElement(result, argument);
        }
    }
}
