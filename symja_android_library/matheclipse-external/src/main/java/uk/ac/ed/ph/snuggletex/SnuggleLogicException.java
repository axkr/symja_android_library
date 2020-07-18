/* $Id:SnuggleLogicException.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

/**
 * This {@link RuntimeException} is thrown if SnuggleTeX has detected something amiss with its
 * logic. This must NOT be used to indicate an error in client-supplied input.
 * <p>
 * This is unchecked, since it is not expected to occur in normal circumstances and users of
 * SnuggleTeX can't really do anything useful to alleviate the underlying problem!
 * 
 * <h2>SnuggleTeX Developer Note</h2>
 * 
 * Throw this Exception when SnuggleTeX's code does silly things. (E.g. unexpected switch case,
 * impossible state.) Raise an error using {@link SnuggleSession#registerError(InputError)}
 * if the error is down to bad client input.
 * <p>
 * Any occurrences of this error "in the wild" indicate a bug with SnuggleTeX that needs to
 * be diagnosed and fixed!
 * 
 * @see SnuggleRuntimeException
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class SnuggleLogicException extends RuntimeException {

    private static final long serialVersionUID = -8544806081557772449L;

    public SnuggleLogicException() {
        super();
    }

    public SnuggleLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnuggleLogicException(String message) {
        super(message);
    }

    public SnuggleLogicException(Throwable cause) {
        super(cause);
    }
}
