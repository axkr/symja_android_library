/*
 * $Id: TabularHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.ErrorToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Handles the (rather complex) <tt>tabular</tt> environment.
 * It also handles the <tt>\\hline</tt> command.
 * 
 * TODO: This is legal inside $\mbox{...}$ so needs to output MathML in this case. Eeek!!!
 * TODO: No support for \vline and friends!!!
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class TabularHandler implements CommandHandler, EnvironmentHandler {

    /**
     * The command matched here is <tt>\\hline</tt>.
     * <p>
     * This case will have been dealt with explicitly by the tabular environment when
     * used correctly so, if we end up here, then client has made a boo-boo. 
     */
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        /* Error: \hline (or similar) outside a table) */
        builder.appendOrThrowError(parentElement, token, CoreErrorCode.TDETB3,
                token.getCommand().getTeXName());
    }
    
    public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
            throws SnuggleParseException {
        /* Compute the geometry of the table and make sure its content model is OK */
        int[] geometry = computeTableDimensions(token.getContent());
        int numColumns = geometry[1];
        
        /* Parse the cell alignment and vertical border properties.
         * 
         * We are currently only supporting the characters '|lrc'.
         * Double borders are currently ignored.
         * 
         * NOTE: We'll apply borders to the left of cells by default. We keep track of whether
         * the final cell in a row needs a right border separately.
         * 
         */
        List<List<String>> columnClasses = new ArrayList<List<String>>();
        ArgumentContainerToken specToken = token.getArguments()[0];
        CharSequence specData = token.getArguments()[0].getSlice().extract(); /* This is something like "|c|" */
        char c;
        String cellAlign = null;
        boolean borderFlag = false; /* Gets set when we find '|' to indicate that next cell should include left border */
        for (int i=0; i<specData.length(); i++) {
            c = specData.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            switch(c) {
                case 'c':
                    cellAlign = "align-center";
                    break;
                    
                case 'l':
                    cellAlign = "align-left";
                    break;
                    
                case 'r':
                    cellAlign = "align-right";
                    break;
                
                case '|':
                    borderFlag = true;
                    break;
                    
                default:
                    /* Error: can't handle column spec */
                    builder.appendOrThrowError(parentElement, specToken, CoreErrorCode.TDETB1,
                            String.valueOf(c));
                    break;
            }
            if (cellAlign!=null) {
                /* We've specified a whole column, so record which CSS classes it should get */
                List<String> classes = new ArrayList<String>();
                classes.add(cellAlign);
                if (borderFlag) {
                    classes.add("left-border");
                }
                columnClasses.add(classes);
                cellAlign = null;
                borderFlag = false;
            }
        }
        
        /* Make sure we actually specified a column! */
        if (columnClasses.isEmpty()) {
            /* Error: no columns! */
            builder.appendOrThrowError(parentElement, specToken, CoreErrorCode.TDETB2);
            /* We'll bail out here as nothing good will come of continuing! */
            return;
        }
        
        /* If we had a trailing '|' then that means we want a right border on the last column */
        if (borderFlag) {
            columnClasses.get(columnClasses.size()-1).add("right-border");
        }
        
        /* LaTeX gives an error if the table has more columns than specified here, so we'll
         * do the same.
         */
        if (columnClasses.size() < numColumns) {
            /* Error: More columns than expected */
            builder.appendOrThrowError(parentElement, specToken, CoreErrorCode.TDETB0,
                    Integer.valueOf(columnClasses.size()), Integer.valueOf(numColumns));
        }

        /* Build XHTML table */
        Element tableElement = builder.appendXHTMLElement(parentElement, "table");
        builder.applyCSSStyle(tableElement, "tabular");
        
        Element tbodyElement = builder.appendXHTMLElement(tableElement, "tbody");
        Element trElement, tdElement;
        List<String> tdClasses = new ArrayList<String>(3); /* (We should only need 3 classes at the mo!) */
        int columnIndex, columnsInRow;
        int rowIndex;
        FlowToken rowToken;
        List<FlowToken> tableContents = token.getContent().getContents();
        boolean topBorderFlag = false;
        boolean bottomBorderFlag = false;
        
        /* We'll iterate over each "row", which might include \\hline's which are not real rows */
        for (rowIndex=0; rowIndex<tableContents.size(); rowIndex++) {
            rowToken = tableContents.get(rowIndex);
            if (rowToken.isCommand(CorePackageDefinitions.CMD_HLINE)) {
                /* If we've got an \\hline, flag it to be added as a top border for the next proper row */
                topBorderFlag = true;
                continue;
            }
            else if (rowToken.isCommand(CorePackageDefinitions.CMD_TABLE_ROW)) {
                /* This is a proper table row. Let's see if all of the remaining "rows" are
                 * \\hline and, if they are, add a bottom border.
                 */
                bottomBorderFlag = false;
                for (int i=rowIndex+1; i<tableContents.size(); i++) {
                    if (tableContents.get(i).isCommand(CorePackageDefinitions.CMD_HLINE)) {
                        bottomBorderFlag = true;
                    }
                    else {
                        bottomBorderFlag = false;
                        break;
                    }
                }
                /* Now build the row */
                List<FlowToken> columns = ((CommandToken) rowToken).getArguments()[0].getContents();
                trElement = builder.appendXHTMLElement(tbodyElement, "tr");
                columnsInRow = columns.size();
                
                /* Create a cell for each entry. Note that some entries may be empty if nothing
                 * has been specified for them and not all with have corresponding column specs.
                 */
                for (columnIndex=0; columnIndex<numColumns; columnIndex++) {
                    tdElement = builder.appendXHTMLElement(trElement, "td");
                    tdClasses.clear();
                    tdClasses.add("tabular");
                    if (columnIndex<columnClasses.size()) {
                        tdClasses.addAll(columnClasses.get(columnIndex));
                    }
                    if (topBorderFlag) {
                        tdClasses.add("top-border");
                    }
                    if (bottomBorderFlag) {
                        tdClasses.add("bottom-border");
                    }
                    builder.applyCSSStyle(tdElement, tdClasses.toArray(new String[tdClasses.size()]));
                    if (columnIndex<columnsInRow) {
                        builder.handleTokens(tdElement, ((CommandToken) columns.get(columnIndex)).getArguments()[0].getContents(), true);
                    }
                }
                topBorderFlag = false;
                if (bottomBorderFlag) {
                    /* That was the last proper row, so stop iterating now */
                    break;
                }
            }
            else if (rowToken.getType()==TokenType.ERROR) {
                trElement = builder.appendXHTMLElement(tbodyElement, "tr");
                tdElement = builder.appendXHTMLElement(trElement, "td");
                builder.appendErrorElement(tdElement, (ErrorToken) rowToken);
            }
            else {
                throw new SnuggleLogicException("Expected table contents to be \\hline or table rows");
            }
        }
        /* NOTE: topBorderFlag might be true here, in which case we had a table containing an \\hline
         * but no proper rows. LaTeX just ignores this so we'll do the same!
         */
    }
    
    /**
     * Computes the dimensions of the table by looking at its content.
     * 
     * @param tableContent content of the <tt>tabular</tt> environment.
     * 
     * @return { rowCount, columnCount } pair
     */
    protected static int[] computeTableDimensions(ArgumentContainerToken tableContent) {
        int maxColumns = 0;
        int rowCount = 0;
        int colCountWithinRow = 0;
        for (FlowToken contentToken : tableContent) {
            if (contentToken.isCommand(CorePackageDefinitions.CMD_HLINE) || contentToken.getType()==TokenType.ERROR) {
                continue;
            }
            else if (contentToken.isCommand(CorePackageDefinitions.CMD_TABLE_ROW)) {
                rowCount++;
                colCountWithinRow = 0;
                CommandToken rowToken = (CommandToken) contentToken;
                ArgumentContainerToken rowContents = rowToken.getArguments()[0];
                for (FlowToken rowContentToken : rowContents) {
                    if (rowContentToken.isCommand(CorePackageDefinitions.CMD_TABLE_COLUMN)) {
                        colCountWithinRow++;
                    }
                    else {
                        throw new SnuggleLogicException("Did not expect to find token "
                                + rowContentToken
                                + " within a table row");
                    }
                }
                if (colCountWithinRow>maxColumns) {
                    maxColumns = colCountWithinRow;
                }
            }
            else {
                throw new SnuggleLogicException("Did not expect to find token "
                        + contentToken
                        + " within a top-level table content");
            }
        }
        return new int[] { rowCount, maxColumns };
    }
}
