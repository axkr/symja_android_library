/* $Id: UnitsHandler.java 525 2010-01-05 14:07:36Z davemckain $
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
 * Handles <tt>\\units</tt>, generating an <tt>mi</tt> element of the form:
 * 
 * <pre><![CDATA[
 *   <mi mathvariant="normal" class="MathML-Unit">...</mi>
 * ]]></pre>
 * 
 * @see <a href="http://www.w3.org/TR/mathml-units/">Units in MathML</a>
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class UnitsHandler implements CommandHandler {
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        Element mi = builder.appendMathMLIdentifierElement(parentElement, builder.extractStringValue(token.getArguments()[0]));
        mi.setAttribute("mathvariant", "normal");
        mi.setAttribute("class", "MathML-Unit");
    }
}
