/* $Id: InputError.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;
import uk.ac.ed.ph.snuggletex.utilities.MessageFormatter;

import java.util.Arrays;

/**
 * Encapsulates an error in the LaTeX input, providing information about what the error is and
 * where it occurred.
 * <p>
 * See the {@link MessageFormatter} for methods formatting instances of this class in various
 * ways.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class InputError {
    
    /** Slice the error occurred in. This may be null in certain circumstances */
    private final FrozenSlice slice;
    
    /** Error code */
    private final ErrorCode errorCode;
    
    /** 
     * Any additional arguments about the error. These are passed to {@link MessageFormatter}
     * when formatting errors in a readable form.
     */
    private final Object[] arguments;
    
    private String stringRepresentation;
    
    public InputError(final ErrorCode errorCode, final FrozenSlice slice, final Object... arguments) {
        this.slice = slice;
        this.errorCode = errorCode;
        this.arguments = arguments;
    }
    
    /**
     * Returns the {@link ErrorCode} for this error.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the {@link FrozenSlice} indicating where the error occurred in the original
     * {@link SnuggleInput}.
     */
    public FrozenSlice getSlice() {
        return slice;
    }

    /**
     * Returns any additional arguments providing information about the error.
     */
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        if (stringRepresentation==null) {
            stringRepresentation = buildStringRepresentation();
        }
        return stringRepresentation;
    }
    
    private String buildStringRepresentation() {
        return getClass().getSimpleName()
            + "(errorCode=" + errorCode.toString()
            + ",slice=" + slice.toString()
            + ",arguments=" + Arrays.toString(arguments)
            + ")";
    }
}
