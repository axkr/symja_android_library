/* $Id: MathLimitsHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder.OutputContext;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Handles the mathematical "limit" tokens like {@link CorePackageDefinitions#CMD_MSUB_OR_MUNDER}, generating
 * either <tt>msub</tt> or <tt>munder</tt> as appropriate, with analogous results for
 * {@link CorePackageDefinitions#CMD_MSUP_OR_MOVER} and {@link CorePackageDefinitions#CMD_MSUBSUP_OR_MUNDEROVER}.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathLimitsHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        /* Get the token to which the limit is being applied to, which is precisely the
         * first argument.
         */
        List<FlowToken> limitand = token.getArguments()[0].getContents(); /* Ha! */
        
        /* Decide whether we should do a munder/mover in preference to the more common
         * msub/msup. This decision is made on whether the "limitand" has been flagged
         * as having a certain Interpretation.
         */ 
        boolean isUnderOver = builder.getOutputContext()==OutputContext.MATHML_BLOCK
            && limitand.size()==1
            && limitand.get(0).hasInterpretationType(InterpretationType.MATH_BIG_LIMIT_OWNER);
        BuiltinCommand command = token.getCommand();
        String elementName;
        if (command.equals(CorePackageDefinitions.CMD_MSUB_OR_MUNDER)) {
            elementName = isUnderOver ? "munder" : "msub";
        }
        else if (command.equals(CorePackageDefinitions.CMD_MSUP_OR_MOVER)) {
            elementName = isUnderOver ? "mover" : "msup";
        }
        else if (command.equals(CorePackageDefinitions.CMD_MSUBSUP_OR_MUNDEROVER)) {
            elementName = isUnderOver ? "munderover" : "msubsup";
        }
        else {
            throw new SnuggleLogicException("Unexpected limit command " + command);
        }
        /* And we can now just build up the MathML quite trivially */
        Element result = builder.appendMathMLElement(parentElement, elementName);
        for (ArgumentContainerToken argument : token.getArguments()) {
            builder.handleMathTokensAsSingleElement(result, argument);
        }
    }
}
