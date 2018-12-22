/* $Id: SnuggleRuntimeException.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This {@link RuntimeException} is used to indicate a problem within SnuggleTeX which is due to
 * a mis-configuration of SnuggleTeX, rather than a problem with client input or bad logic within
 * SnuggleTeX.
 * <p>
 * I have made this an unchecked Exception since these types of problems are not likely to occur
 * in normal circumstances and software interacting with SnuggleTeX is perhaps unlikely to be able
 * to fix the situation without some work, so the burden of having to declare these types of
 * Exceptions is avoided.
 *  
 * <h2>SnuggleTeX Developer Note</h2>
 * 
 * Throw this Exception for things like unexpected problems with JAXP and such-like. (E.g. not
 * being able to configure a {@link DocumentBuilderFactory}, unable to find or compile one of
 * the internal XSLT stylesheets...)
 * 
 * @see SnuggleLogicException
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SnuggleRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8544806081557772449L;

    public SnuggleRuntimeException() {
        super();
    }

    public SnuggleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnuggleRuntimeException(String message) {
        super(message);
    }

    public SnuggleRuntimeException(Throwable cause) {
        super(cause);
    }
}
