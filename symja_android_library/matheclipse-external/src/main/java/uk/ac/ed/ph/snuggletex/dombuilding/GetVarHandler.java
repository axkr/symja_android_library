/* $Id: GetVarHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Handles the <tt>\\getvar</tt> macro.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class GetVarHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        String namespace = null;
        if (token.getOptionalArgument()!=null) {
            namespace = builder.extractStringValue(token.getOptionalArgument());
        }
        String variableName = builder.extractStringValue(token.getArguments()[0]);
        Object value = builder.getVariableManager().getVariable(namespace, variableName);
        if (value!=null) {
            builder.appendTextNode(parentElement, value.toString(), false);
        }
    }
}
