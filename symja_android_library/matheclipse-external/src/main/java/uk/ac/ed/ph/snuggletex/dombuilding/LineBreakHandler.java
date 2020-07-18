/* $Id: LineBreakHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Handles instances of <tt>\\\\</tt>, which forces a line break at the given point.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class LineBreakHandler implements CommandHandler {

    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token) {
        if (builder.isBuildingMathMLIsland()) {
            /* We're doing MathML. It appears that we the best thing to do is emit the
             * actual line feed character (U+000A) and leave it to MathML UA's to interpret.
             */
            builder.appendMathMLTextElement(parentElement, "mtext", Character.toString((char) 10), false);
        }
        else {
            /* It's XHTML. We just emit <br/> as usual */
            builder.appendXHTMLElement(parentElement, "br");
        }
    }
}
