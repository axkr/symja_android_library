/*
 * $Id$
 */

package edu.jas.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Cartesian product for Long with iterator. Similar to CartesianProduct but
 * returns only tuples of given total degree.
 * @author Heinz Kredel
 */
public class CartesianProductLong implements Iterable<List<Long>> {


    /**
     * data structure.
     */
    public final List<LongIterable> comps;


    public final long upperBound;


    /**
     * CartesianProduct constructor.
     * @param comps components of the Cartesian product.
     * @param ub an upper bound for the total degree of the elements.
     */
    public CartesianProductLong(List<LongIterable> comps, long ub) {
        if (comps == null) {
            throw new IllegalArgumentException("null components not allowed");
        }
        this.comps = comps;
        this.upperBound = ub;
    }


    /**
     * Get an iterator over subsets.
     * @return an iterator.
     */
    public Iterator<List<Long>> iterator() {
        return new CartesianProductLongIterator(comps, upperBound);
    }

}


/**
 * Cartesian product iterator for Longs. Similar to CartesianProductIterator but
 * returns only tuples of given total degree.
 * @author Heinz Kredel
 */
class CartesianProductLongIterator implements Iterator<List<Long>> {


    /**
     * data structure.
     */
    final List<LongIterable> comps;


    final List<LongIterator> compit;


    List<Long> current;


    boolean empty;


    public final long upperBound;


    /**
     * CartesianProduct iterator constructor.
     * @param comps components of the Cartesian product.
     * @param un an upper bound for the total degree of the elements.
     */
    public CartesianProductLongIterator(List<LongIterable> comps, long ub) {
        if (comps == null) {
            throw new IllegalArgumentException("null comps not allowed");
        }
        this.comps = comps;
        this.upperBound = ub;
        current = new ArrayList<Long>(comps.size());
        compit = new ArrayList<LongIterator>(comps.size());
        empty = false;
        for (LongIterable ci : comps) {
            LongIterator it = (LongIterator) ci.iterator();
            if (it.getUpperBound() < this.upperBound) {
                throw new IllegalArgumentException("each iterator (" + it.getUpperBound()
                                + ") must be able to reach total upper bound " + upperBound);
            }
            if (!it.hasNext()) {
                empty = true;
                current.clear();
                return;
            }
            current.add(it.next());
            compit.add(it);
        }
        // start with last component equal to upper bound
        LongIterator it = compit.get(compit.size() - 1);
        long d = -1L;
        while (it.hasNext()) {
            d = it.next();
            if (d >= upperBound) {
                break;
            }
        }
        if (d >= 0L) {
            current.set(current.size() - 1, d);
        }
        if (totalDegree(current) != upperBound) {
            empty = true;
            current.clear();
        }
        //System.out.println("current = " + current);
    }


    /**
     * Test for availability of a next tuple.
     * @return true if the iteration has more tuples, else false.
     */
    public synchronized boolean hasNext() {
        return !empty;
    }


    /**
     * Get next tuple.
     * @return next tuple.
     */
    public synchronized List<Long> next() {
        if (empty) {
            throw new NoSuchElementException("invalid call of next()");
        }
        List<Long> res = new ArrayList<Long>(current);
        //int waist = 0;
        while (true) {
            // search iterator which hasNext
            int i = compit.size() - 1;
            for (; i >= 0; i--) {
                LongIterator iter = compit.get(i);
                if (iter.hasNext()) {
                    break;
                }
            }
            if (i < 0) {
                empty = true;
                //System.out.println("inner waist = " + waist);
                return res;
            }
            long pd = 0L;
            for (int j = 0; j < i; j++) {
                pd += current.get(j);
            }
            if (pd >= upperBound) {
                if (current.get(0) == upperBound) {
                    empty = true;
                    //System.out.println("inner waist = " + waist);
                    return res;
                }
                pd = upperBound;
            }
            long rd = upperBound - pd;
            // update iterators
            for (int j = i + 1; j < compit.size(); j++) {
                LongIterator iter = (LongIterator) comps.get(j).iterator();
                iter.setUpperBound(rd);
                compit.set(j, iter);
            }
            // update current
            for (int j = i; j < compit.size(); j++) {
                LongIterator iter = compit.get(j);
                Long el = iter.next();
                current.set(j, el);
            }
            long td = totalDegree(current);
            if (td == upperBound) {
                //System.out.println("inner waist = " + waist);
                return res;
            }
            //System.out.println("current = " + current + ", td = " + td);
            if (td > upperBound) {
                //waist++;
                continue;
            }
            // adjust last component to total degree
            LongIterator it = compit.get(compit.size() - 1);
            long d = -1L;
            while (td < upperBound && it.hasNext()) {
                td++;
                d = it.next();
            }
            //System.out.println("d = " + d);
            if (d >= 0L) {
                current.set(current.size() - 1, d);
            }
            if (td == upperBound) {
                //System.out.println("inner waist = " + waist);
                return res;
            }
            //waist++;
            // continue search
        }
        //return res;
    }


    /**
     * Total degree of a tuple.
     * @param e list of Longs.
     * @return sum of all elements in e.
     */
    public long totalDegree(List<Long> e) {
        long d = 0L;
        for (Long i : e) {
            d += i;
        }
        return d;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove tuples");
    }

}
