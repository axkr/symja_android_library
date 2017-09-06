/**
 * jcombinatorics:
 * Java Combinatorics Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 *
 * This software is made available under the terms of the MIT License.
 * See LICENSE.txt.
 *
 * Created Sep 4, 2009
 */
package org.matheclipse.combinatoric.util;

import java.util.Iterator;

/**
 * @param <T>
 *            a type
 * @author Alistair A. Israel
 */
public abstract class ReadOnlyIterator<T> implements Iterator<T> {

    /**
     * {@value #REMOVE_OPERATION_NOT_SUPPORTED_MESSAGE}.
     */
    public static final String REMOVE_OPERATION_NOT_SUPPORTED_MESSAGE = "remove() operation not supported!";

    /**
     * Not supported. Throws {@link UnsupportedOperationException} exception.
     */
    @Override
	public final void remove() {
        throw new UnsupportedOperationException(REMOVE_OPERATION_NOT_SUPPORTED_MESSAGE);
    }

}
