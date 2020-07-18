/* $Id:XMLBlockElementBuilder.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.dombuilding;

/**
 * Builds custom block XML elements.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class XMLBlockElementHandler extends AbstractCustomXMLElementHandler {
    
    @Override
    protected boolean isBlock() {
        return true;
    }
}
