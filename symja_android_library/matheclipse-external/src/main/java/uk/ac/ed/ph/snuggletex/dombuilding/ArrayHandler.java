/* $Id: ArrayHandler.java 525 2010-01-05 14:07:36Z davemckain $
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
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Handles the LaTeX <tt>array</tt> environment.
 * <p>
 * This can only be used in MATH mode and generates a <tt>mtable</tt> as a result.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class ArrayHandler implements EnvironmentHandler {
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        /* Compute the geometry of the table and make sure its content model is OK */
        int[] geometry = TabularHandler.computeTableDimensions(token.getContent());
        int numColumns = geometry[1];
        
        /* Parse the cell alignment properties.
         * 
         * We only support the characters 'lrc'.
         */
        ArgumentContainerToken alignSpecToken = token.getArguments()[0];
        CharSequence alignSpecData = token.getArguments()[0].getSlice().extract(); /* This is something like "ccc" */
        
        /* Make sure we got at least one column */
        if (alignSpecData.length()==0) {
            /* Error: no columns! */
            builder.appendOrThrowError(parentElement, alignSpecToken, CoreErrorCode.TDEMA1);
            /* We'll bail out here as nothing good will come of continuing! */
            return;
        }
        
        /* Check that we've got l,r,c only */
        char c;
        List<String> alignments = new ArrayList<String>(alignSpecData.length());
        for (int i=0; i<alignSpecData.length(); i++) {
            c = alignSpecData.charAt(i);
            switch(c) {
                case 'c': alignments.add("center"); break;
                case 'l': alignments.add("left"); break;
                case 'r': alignments.add("right"); break;
                default:
                    builder.appendOrThrowError(parentElement, alignSpecToken, CoreErrorCode.TDEMA0,
                            String.valueOf(c));
                    alignments.add("center"); /* We'll use this as a default */
                    break;
            }
        }
        int maxColumns = alignments.size();
        
        /* Make sure number of columns specified is at least as much as what was calculated */
        if (maxColumns < numColumns) {
            /* Error: More columns than expected */
            builder.appendOrThrowError(parentElement, alignSpecToken, CoreErrorCode.TDEMA2,
                    Integer.valueOf(alignSpecData.length()), Integer.valueOf(numColumns));
        }

        /* Right, now we generate the resulting <mtable/> to the output */
        Element mtableElement = builder.appendMathMLElement(parentElement, "mtable");
        Element mtrElement, mtdElement;
        FlowToken columnToken;
        int rowColumns;
        for (FlowToken rowToken : token.getContent()) {
            mtrElement = builder.appendMathMLElement(mtableElement, "mtr");
            List<FlowToken> columns = ((CommandToken) rowToken).getArguments()[0].getContents();
            rowColumns = columns.size();
            for (int i=0; i<maxColumns && i<rowColumns; i++) {
                columnToken = columns.get(i);
                mtdElement = builder.appendMathMLElement(mtrElement, "mtd");
                mtdElement.setAttribute("columnalign", alignments.get(i));
                builder.handleTokens(mtdElement, ((CommandToken) columnToken).getArguments()[0].getContents(), true);
            }
            /* Add empty <td/> for missing columns */
            for (int i=0; i<maxColumns-rowColumns; i++) {
                builder.appendMathMLElement(mtrElement, "mtd");
            }
        }
    }
}
