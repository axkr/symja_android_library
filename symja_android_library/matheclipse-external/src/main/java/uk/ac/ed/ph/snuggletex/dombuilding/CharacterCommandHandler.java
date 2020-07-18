/* $Id: CharacterCommandHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * This handles simple commands that essentially just stand in for single-character identifiers
 * that don't have any particular meaning when used in MATH mode (e.g. '$').
 * <p.
 * In TEXT mode, the resulting character is appending to the DOM directly.
 * <p>
 * In MATH mode, the character is wrapped inside an <tt>mi</tt> element.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class CharacterCommandHandler implements CommandHandler {
    
    private final String outputCharacter;
    
    public CharacterCommandHandler(final String outputCharacter) {
        this.outputCharacter = outputCharacter;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token) {
        if (token.getLatexMode()==LaTeXMode.MATH) {
            builder.appendMathMLIdentifierElement(parentElement, outputCharacter);
        }
        else {
            builder.appendTextNode(parentElement, outputCharacter, false);
        }
    }
}
