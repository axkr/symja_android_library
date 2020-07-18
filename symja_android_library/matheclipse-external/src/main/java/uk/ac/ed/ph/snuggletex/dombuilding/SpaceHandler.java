/* $Id: SpaceHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Creates preset spacing elements in MATH and/or TEXT modes.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SpaceHandler implements CommandHandler {

    /** String to use to create space text Node in TEXT mode, null if not supported */
    private final String textString;
    
    /** Width attribute for <tt>mspace</tt> Element in MATH mode, null if not supported */
    private final String mathWidth;

    public SpaceHandler(final String textString, final String mathWidth) {
        this.textString = textString;
        this.mathWidth = mathWidth;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token) {
        if (token.getLatexMode()==LaTeXMode.MATH) {
            /* Create <mspace/> */
            if (mathWidth!=null) {
                Element mspace = builder.appendMathMLElement(parentElement, "mspace");
                mspace.setAttribute("width", mathWidth);
            }
            else {
                throw new SnuggleLogicException("Spacing token " + token + " expectedly used in MATH mode");
            }

        }
        else {
            /* Text mode, so this just a character */
            if (textString!=null) {
                builder.appendTextNode(parentElement, textString, false);
            }
            else {
                throw new SnuggleLogicException("Spacing token " + token + " expectedly used in TEXT mode");
            }
        }
    }
}
