/*
 * $Id$
 */

package edu.jas.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Cartesian product with iterator.
 * @author Heinz Kredel
 */
public class CartesianProduct<E> implements Iterable<List<E>> {


    /**
     * data structure.
     */
    public final List<Iterable<E>> comps;


    /**
     * CartesianProduct constructor.
     * @param comps components of the Cartesian product.
     */
    public CartesianProduct(List<Iterable<E>> comps) {
        if (comps == null) {
            throw new IllegalArgumentException("null components not allowed");
        }
        this.comps = comps;
    }


    //     /**
    //      * CartesianProduct constructor.
    //      * @param comps components of the Cartesian product.
    //      */
    //     public CartesianProduct(List<List<E>> comps) {
    //         this( listToIterable(comps)  );
    //     }


    /**
     * Get an iterator over subsets.
     * @return an iterator.
     */
    public Iterator<List<E>> iterator() {
        return new CartesianProductIterator<E>(comps);
    }


    /**
     * Transform list to iterables.
     * @param comp components of the Cartesian product.
     * @return iterables taken from lists.
     */
    static <E> List<Iterable<E>> listToIterable(List<List<E>> comp) {
        List<Iterable<E>> iter = new ArrayList<Iterable<E>>(comp.size());
        for (List<E> list : comp) {
            iter.add(list);
        }
        return iter;
    }


}


/**
 * Cartesian product iterator.
 * @author Heinz Kredel
 */
class CartesianProductIterator<E> implements Iterator<List<E>> {


    /**
     * data structure.
     */
    final List<Iterable<E>> comps;


    final List<Iterator<E>> compit;


    List<E> current;


    boolean empty;


    /**
     * CartesianProduct iterator constructor.
     * @param comps components of the Cartesian product.
     */
    public CartesianProductIterator(List<Iterable<E>> comps) {
        if (comps == null) {
            throw new IllegalArgumentException("null comps not allowed");
        }
        this.comps = comps;
        current = new ArrayList<E>(comps.size());
        compit = new ArrayList<Iterator<E>>(comps.size());
        empty = false;
        for (Iterable<E> ci : comps) {
            Iterator<E> it = ci.iterator();
            if (!it.hasNext()) {
                empty = true;
                current.clear();
                break;
            }
            current.add(it.next());
            compit.add(it);
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
    public synchronized List<E> next() {
        if (empty) {
            throw new NoSuchElementException("invalid call of next()");
        }
        List<E> res = new ArrayList<E>(current);
        // search iterator which hasNext
        int i = compit.size() - 1;
        for (; i >= 0; i--) {
            Iterator<E> iter = compit.get(i);
            if (iter.hasNext()) {
                break;
            }
        }
        if (i < 0) {
            empty = true;
            return res;
        }
        // update iterators
        for (int j = i + 1; j < compit.size(); j++) {
            Iterator<E> iter = comps.get(j).iterator();
            compit.set(j, iter);
        }
        // update current
        for (int j = i; j < compit.size(); j++) {
            Iterator<E> iter = compit.get(j);
            E el = iter.next();
            current.set(j, el);
        }
        return res;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove tuples");
    }

}
