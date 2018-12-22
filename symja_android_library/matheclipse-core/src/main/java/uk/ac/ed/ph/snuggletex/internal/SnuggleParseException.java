/* $Id: SnuggleParseException.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import uk.ac.ed.ph.snuggletex.utilities.MessageFormatter;

/**
 * Internal Exception thrown by {@link SnuggleSession#registerError(InputError)} when a parsing
 * error occurs and the client has requested immediate failure in such circumstances.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SnuggleParseException extends Exception {

    private static final long serialVersionUID = -4623002490712268496L;
    
    private final InputError error;
    
    public SnuggleParseException(InputError error) {
        super(MessageFormatter.formatErrorAsString(error));
        this.error = error;
    }

    public InputError getError() {
        return error;
    }
}
