/* $Id:EqnArrayBuilder.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder.MathContentBuilderCallback;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Handles the <tt>eqnarray*</tt> environment.
 * 
 * @see MathEnvironmentHandler
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class EqnArrayHandler implements EnvironmentHandler {
    
    static final String[] COLUMN_ALIGNMENTS = {
        "right",
        "center",
        "left"
    };

    public void handleEnvironment(final DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        /* Compute the geometry of the table and make sure its content model is OK */
        int[] geometry = TabularHandler.computeTableDimensions(token.getContent());
        final int numColumns = geometry[1];
        if (numColumns>3) {
            /* Error: eqnarray must have no more than 3 columns */
            builder.appendOrThrowError(parentElement, token, CoreErrorCode.TDEM01, numColumns);
            return;
        }
        
        /* Create callback to generate the actual content for the MathML */
        MathContentBuilderCallback callback = new MathContentBuilderCallback() {
            public void buildMathElementContent(Element contentContainerElement,
                    ArgumentContainerToken mathContentToken, boolean isAnnotated)
                    throws SnuggleParseException {
                /* Build <mtable/> */
                Element mtableElement = builder.appendMathMLElement(contentContainerElement, "mtable");
                Element mtrElement, mtdElement;
                int columnIndex;
                for (FlowToken rowToken : mathContentToken) {
                    mtrElement = builder.appendMathMLElement(mtableElement, "mtr");
                    List<FlowToken> columns = ((CommandToken) rowToken).getArguments()[0].getContents();
                    columnIndex = 0;
                    for (FlowToken columnToken : columns) {
                        mtdElement = builder.appendMathMLElement(mtrElement, "mtd");
                        mtdElement.setAttribute("columnalign", COLUMN_ALIGNMENTS[columnIndex++]);
                        builder.handleTokens(mtdElement, ((CommandToken) columnToken).getArguments()[0].getContents(), true);
                    }
                    /* Add empty <td/> for missing columns */
                    for (int i=0; i<numColumns-columns.size(); i++) {
                        builder.appendMathMLElement(mtrElement, "mtd");
                    }
                }
                
            }
        };
        
        /* Now general MathML and any required annotations */
        builder.buildMathElement(parentElement, token, token.getContent(), true, callback);
    }
}
