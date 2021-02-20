/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;


/**
 * Spliterator over monomials of a polynomial. 
 * Adaptor for val.entrySet().spliterator().
 * @author Heinz Kredel
 */

public class PolySpliterator<C extends RingElem<C> > 
    implements Spliterator< Monomial<C> > {


    /** 
     * Internal spliterator over polynomial map.
     */
    protected Spliterator< Map.Entry<ExpVector,C> > ms;


    /** 
     * Polynomial sorted map.
     */
    protected SortedMap<ExpVector,C> sm;

    
    /** 
     * Constructor of polynomial spliterator.
     * @param m SortedMap of a polynomial.
     */
    public PolySpliterator( SortedMap<ExpVector,C> m ) {
        //this(Spliterators.spliterator(m.entrySet(), Spliterator.NONNULL), m);
        this(m.entrySet().spliterator(), m);
    }


    /** 
     * Constructor of polynomial spliterator.
     * @param mse Spliterator a polynomial.
     * @param m SortedMap of a polynomial.
     */
    protected PolySpliterator( Spliterator<Map.Entry<ExpVector,C>> mse, SortedMap<ExpVector,C> m ) {
        sm = m;
        ms = mse;
    }

    
    /**
     * String representation of PolySpliterator.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PolySpliterator(" + estimateSize() + ", " + characteristics() + ")";
    }

    
    /** 
     * Returns a set of characteristics of this Spliterator and its
     * elements.
     * @return ORed value of the characteristics.
     */
    public int characteristics() {
        return ms.characteristics();
    }

    
    /** 
     * Returns an estimate of the number of elements of this Spliterator.
     * @return size of the sorted map.
     */
    public long estimateSize() {
        return ms.estimateSize();
    }

    
    /** 
     * Get the monomial comparator.
     * @return monomial comparator.
     */
    public Comparator<Monomial<C>> getComparator() {
        return new Comparator<Monomial<C>>() {
            @Override
            public int compare(Monomial<C> a, Monomial<C> b) {
                if (sm == null) {
                    throw new RuntimeException("sm == null");
                }
                int s = sm.comparator().compare(a.e, b.e);
                if (s != 0) { // always true
                    return s;
                }
                return a.c.compareTo(b.c);
            }
        };
    }


    /** 
     * Try to split this spliterator.
     * @return polynomial spliterator or null.
     */
    public PolySpliterator<C> trySplit() {
        Spliterator< Map.Entry<ExpVector,C> > part = ms.trySplit();
        //System.out.println("PolySplit(" + part.characteristics() + ")");
        return new PolySpliterator<C>(part, sm);
    }


    /** 
     * If a remaining element exists perform the action on it.
     * @return true if the polynomial spliterator could be advanced, else false.
     */
    public boolean tryAdvance(Consumer<? super Monomial<C>> action) {
        Consumer<Map.Entry<ExpVector,C>> mact =
            new Consumer<Map.Entry<ExpVector,C>>() {
                public void accept(Map.Entry<ExpVector,C> me) {
                    action.accept( new Monomial<C>(me.getKey(), me.getValue()) );
                }
            };
        return ms.tryAdvance(mact);
    }

}
