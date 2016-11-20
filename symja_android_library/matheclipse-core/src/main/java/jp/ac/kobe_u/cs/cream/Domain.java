/*
 * @(#)Domain.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Domains.
 * This is an abstract class for domains.
 * @see	Variable
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Domain implements Cloneable {
	int size = 0;

    /**
     * Returns the size of the domain.
     * @return the size
     */
    public int size() {
    	return size;
    }

    /**
     * Returns true when the domain is empty.
     * @return true when the domain is empty
     */
    public boolean isEmpty() {
    	return size() == 0;
    }

    /**
     * Returns true when the domain is empty.
     * @return true when the domain is empty
     */
    public abstract Object clone();

    /**
     * Returns true when the domain is equal to another domain <tt>d</tt>.
     * @param d another domain
     * @return true when the domain is equal to <tt>d</tt>
     */
    public abstract boolean equals(Domain d);

    /**
     * Returns the iterator of domain elements.
     * @return the iterator of domain elements
     */
    public abstract Iterator<Domain> elements();

    /**
     * Returns the only element when the domain is a singleton.
     * @return the only element
     * @throws NoSuchElementException
     */
    public abstract Object element() throws NoSuchElementException;

    /**
     * Returns true when the domain contains the element <tt>o</tt>.
     * @param o the element to be checked
     * @return true when the domain contains <tt>o</tt>
     */
    public abstract boolean contains(Object o);

    /**
     * Returns a new domain obtained by adding <tt>o</tt> as a new element
     * (optional operation).
     * @param o an element to be added
     * @return the new domain
     */
    public abstract Domain insert(Object o);

    /**
     * Returns a new domain obtained by deleting the element <tt>o</tt>
     * (optional operation).
     * @param o an element to be deleted
     * @return the new domain
     */
    public abstract Domain delete(Object o);

    /**
     * Returns a new domain of the intersection
     * (optional operation).
     * @param d another domain
     * @return the intersection
     */
    public abstract Domain cap(Domain d);

    /**
     * Returns a new domain of the union
     * (optional operation).
     * @param d another domain
     * @return the union
     */
    public abstract Domain cup(Domain d);

    /**
     * Returns a new domain of the difference
     * (optional operation).
     * @param d another domain
     * @return the difference
     */
    public abstract Domain difference(Domain d);

}
