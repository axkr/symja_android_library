/* $Id: XMLNameOrIdHandler.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;

import org.w3c.dom.Element;

/**
 * Trivial handler for the <tt>\\xmlName</tt> and <tt>\\xmlId</tt> commands.
 * All this does is check that the argument is a proper XML Name or ID and then outputs it
 * as normal.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class XMLNameOrIdHandler implements CommandHandler {
    
    public static final int NAME = 0;
    public static final int ID = 1;
    
    /** Set to check for an ID, leave false to just test for an XML Name. */
    private final int checkType;
    
    public XMLNameOrIdHandler(final int checkType) {
        if (checkType!=NAME && checkType!=ID) {
            throw new IllegalArgumentException("Bad check type " + checkType);
        }
        this.checkType = checkType;
    }
    
    public void handleCommand(DOMBuilder builder, Element parentElement, CommandToken token)
            throws SnuggleParseException {
        ArgumentContainerToken nameArgument = token.getArguments()[0];
        String rawName = nameArgument.getSlice().extract().toString().trim();

        /* Call back on DOMBuilder to validate the name/ID */
        String result;
        if (checkType==ID) {
            result = builder.validateXMLId(parentElement, nameArgument, rawName);
        }
        else {
            result = builder.validateXMLName(parentElement, nameArgument, rawName);
        }
        
        if (result!=null) {
            /* Name was OK, so append to output as normal */
            builder.appendTextNode(parentElement, rawName, true);
        }
    }
}
