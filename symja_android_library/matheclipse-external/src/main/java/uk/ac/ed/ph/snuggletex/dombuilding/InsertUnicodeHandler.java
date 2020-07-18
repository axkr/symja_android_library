/* $Id: InsertUnicodeHandler.java 583 2010-05-24 10:28:13Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Handles the non-standard <tt>\\ux</tt> command to insert a Unicode character into the output.
 * <p>
 * NOTE: This is experimental and currently doesn't generate nice MathML, so is subject to change
 * or disappear. Use at your peril!
 * FIXME: Might be sensible to merge this with proposed mechanism for entering Unicode characters.
 * 
 * @since 1.2.2
 *
 * @author  David McKain
 * @version $Revision: 583 $
 */
public final class InsertUnicodeHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        ArgumentContainerToken nameArgument = token.getArguments()[0];
        String hexCode = nameArgument.getSlice().extract().toString().trim();
        
        try {
            int codePoint = Integer.parseInt(hexCode, 16);
            if (codePoint >= Character.MIN_VALUE && codePoint <= Character.MAX_VALUE) {
                builder.appendTextNode(parentElement, String.valueOf((char) codePoint), true);
            }
            else {
                /* Error: Unicode code point is out of the supported range */
                builder.appendOrThrowError(parentElement, token, CoreErrorCode.TDEXU1, hexCode);
            }
        }
        catch (NumberFormatException e) {
            /* Error: Unicode code point was expected to be hexadecimal */
            builder.appendOrThrowError(parentElement, token, CoreErrorCode.TDEXU0, hexCode);
        }
    }
}
