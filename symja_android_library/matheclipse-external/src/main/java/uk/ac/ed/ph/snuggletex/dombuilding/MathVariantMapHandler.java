/* $Id: MathVariantMapHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.util.ArrayListStack;
import uk.ac.ed.ph.snuggletex.definitions.MathVariantMap;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * This handles changes to the current "mathvariant" caused by things like <tt>\\mathcal</tt>
 * and friends.
 * <p>
 * Because these commands' argument may consist of multiple tokens, we manage the application
 * of these "styles" within {@link DOMBuilder} so all this handler does is push/pop a variant
 * to the appropriate Stack.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathVariantMapHandler implements CommandHandler {
    
    /** Resulting {@link MathVariantMap} */
    private final MathVariantMap characterMap;
    
    public MathVariantMapHandler(final MathVariantMap characterMap) {
        this.characterMap = characterMap;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        ArrayListStack<MathVariantMap> mathVariantMapStack = builder.getMathVariantMapStack();
        
        mathVariantMapStack.push(characterMap);
        builder.handleTokens(parentElement, token.getArguments()[0], true);
        mathVariantMapStack.pop();
    }

}
