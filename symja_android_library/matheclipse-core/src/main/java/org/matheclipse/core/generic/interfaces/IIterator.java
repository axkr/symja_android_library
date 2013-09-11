package org.matheclipse.core.generic.interfaces;

import java.util.Iterator;

/**
 * Interface for an iterator with additional tearDown() method, to run the iterator
 * again
 */
public interface IIterator<E> extends Iterator<E> {
	boolean setUp();

	void tearDown();
}
