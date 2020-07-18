/* $Id: MathEnvironmentHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;

import org.w3c.dom.Element;

/**
 * Builds LaTeX math environments. Note that this might be inside an mbox inside
 * an existing math environment.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathEnvironmentHandler implements EnvironmentHandler {
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        BuiltinEnvironment environment = token.getEnvironment();
        if (builder.isBuildingMathMLIsland()) {
            /* We're putting maths inside maths (e.g. \mbox{$ $}) so make a <mrow/> */
            Element mrow = builder.appendMathMLElement(parentElement, "mrow");
            builder.handleTokens(mrow, token.getContent(), false);
        }
        else {
            boolean isDisplayMath = environment==CorePackageDefinitions.ENV_DISPLAYMATH;
            builder.buildMathElement(parentElement, token, token.getContent(), isDisplayMath);
        }
    }
}
