/* $Id:ErrorToken.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;

/**
 * Special token encapsulating a client error in parsing.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class ErrorToken extends FlowToken {
    
    private final InputError error;

    public ErrorToken(final InputError error, final LaTeXMode latexMode) {
        super(error.getSlice(), TokenType.ERROR, latexMode, TextFlowContext.START_NEW_XHTML_BLOCK);
        this.error = error;
    }

    public InputError getError() {
        return error;
    }
}
