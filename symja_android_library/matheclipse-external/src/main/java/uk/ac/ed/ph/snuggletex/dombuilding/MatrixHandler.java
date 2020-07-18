/* $Id: MatrixHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Handles the AMSLaTeX <tt>cases</tt> and the various <tt>matrix</tt> environments.
 * <p>
 * This can only be used in MATH mode and generates a <tt>mtable</tt> as a result, wrapped
 * inside an <tt>mfenced</tt> for the matrix/cases delimiters as required.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MatrixHandler implements EnvironmentHandler {

    /** Fence Opener, which may be blank. null indicates "no opener and closer" */
    private final String opener;
    
    /** Fence Closer, which may be blank. null indicates "no opener and closer" */
    private final String closer;
    
    /** 
     * Maximum number of allowed columns. (Used for cases environment, which is always 2 columns)
     * Zero signifies no restriction.
     */
    private final int maxColumns;
    
    public MatrixHandler() {
        this(0, null, null);
    }
    
    public MatrixHandler(final String opener, final String closer) {
        this(0, opener, closer);
    }

    public MatrixHandler(int maxColumns, final String opener, final String closer) {
        this.maxColumns = maxColumns;
        this.opener = opener;
        this.closer = closer;
    }

    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        /* Maybe create outer <mfenced/> */
        Element tableContainer;
        if (opener!=null) {
            tableContainer = builder.appendMathMLElement(parentElement, "mfenced");
            tableContainer.setAttribute("open", opener);
            tableContainer.setAttribute("close", closer);
        }
        else {
            tableContainer = parentElement;
        }
        
        /* Compute the geometry of the table and make sure its content model is OK */
        int[] geometry = TabularHandler.computeTableDimensions(token.getContent());
        int numColumns = geometry[1];
        
        /* Now we generate the resulting <mtable/> inside the <mfrenced/> */
        Element mtableElement = builder.appendMathMLElement(tableContainer, "mtable");
        Element mtrElement, mtdElement;
        FlowToken columnToken;
        int rowColumns;
        for (FlowToken rowToken : token.getContent()) {
            mtrElement = builder.appendMathMLElement(mtableElement, "mtr");
            List<FlowToken> columns = ((CommandToken) rowToken).getArguments()[0].getContents();
            rowColumns = columns.size();
            if (maxColumns>0 && rowColumns>maxColumns) {
                /* Error: Each row in a this environment must have no more than NN columns */
                builder.appendOrThrowError(mtrElement, rowToken, CoreErrorCode.TDEMM0, token.getEnvironment().getTeXName(), maxColumns);
                continue;
            }
            for (int i=0; i<numColumns && i<rowColumns; i++) {
                columnToken = columns.get(i);
                mtdElement = builder.appendMathMLElement(mtrElement, "mtd");
                builder.handleTokens(mtdElement, ((CommandToken) columnToken).getArguments()[0].getContents(), true);
            }
            /* Add empty <td/> for missing columns */
            for (int i=0; i<numColumns-rowColumns; i++) {
                builder.appendMathMLElement(mtrElement, "mtd");
            }
        }
    }
}
