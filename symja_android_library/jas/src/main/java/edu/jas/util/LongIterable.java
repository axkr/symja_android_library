/*
 * $Id$
 */

package edu.jas.util;


import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Iterable for Long.
 * @author Heinz Kredel
 */
public class LongIterable implements Iterable<Long> {


    private boolean nonNegative = true;


    private long upperBound = Long.MAX_VALUE;


    /**
     * Constructor.
     */
    public LongIterable() {
    }


    /**
     * Constructor.
     */
    public LongIterable(long ub) {
        upperBound = ub;
    }


    /**
     * Set the upper bound for the iterator.
     * @param ub an upper bound for the iterator elements.
     */
    public void setUpperBound(long ub) {
        upperBound = ub;
    }


    /**
     * Get the upper bound for the iterator.
     * @return the upper bound for the iterator elements.
     */
    public long getUpperBound() {
        return upperBound;
    }


    /**
     * Set the iteration algorithm to all elements.
     */
    public void setAllIterator() {
        nonNegative = false;
    }


    /**
     * Set the iteration algorithm to non-negative elements.
     */
    public void setNonNegativeIterator() {
        nonNegative = true;
    }


    /**
     * Get an iterator over Long.
     * @return an iterator.
     */
    public Iterator<Long> iterator() {
        return new LongIterator(nonNegative, upperBound);
    }

}


/**
 * Long iterator.
 * @author Heinz Kredel
 */
class LongIterator implements Iterator<Long> {


    /**
     * data structure.
     */
    long current;


    boolean empty;


    final boolean nonNegative;


    protected long upperBound;


    /**
     * Set the upper bound for the iterator.
     * @param ub an upper bound for the iterator elements.
     */
    public void setUpperBound(long ub) {
        upperBound = ub;
    }


    /**
     * Get the upper bound for the iterator.
     * @return the upper bound for the iterator elements.
     */
    public long getUpperBound() {
        return upperBound;
    }


    /**
     * Long iterator constructor.
     */
    public LongIterator() {
        this(false, Long.MAX_VALUE);
    }


    /**
     * Long iterator constructor.
     * @param nn true for an iterator over non-negative longs, false for all
     *            elements iterator.
     * @param ub an upper bound for the entries.
     */
    public LongIterator(boolean nn, long ub) {
        current = 0L;
        //System.out.println("current = " + current);
        empty = false;
        nonNegative = nn;
        upperBound = ub;
        //System.out.println("upperBound = " + upperBound);
    }


    /**
     * Test for availability of a next long.
     * @return true if the iteration has more Longs, else false.
     */
    public synchronized boolean hasNext() {
        return !empty;
    }


    /**
     * Get next Long.
     * @return next Long.
     */
    public synchronized Long next() {
        if (empty) {
            throw new NoSuchElementException("invalid call of next()");
        }
        Long res = Long.valueOf(current);
        if (nonNegative) {
            current++;
        } else if (current > 0L) {
            current = -current;
        } else {
            current = -current;
            current++;
        }
        if (current > upperBound) {
            empty = true;
        }
        return res;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }

}
