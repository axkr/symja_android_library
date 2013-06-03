/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.poly.Word;
import edu.jas.poly.Overlap;
import edu.jas.poly.OverlapList;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Pair list management of word polynomials.
 * Implemented using GenWordPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedWordPairlist<C extends RingElem<C> > implements WordPairList<C> {

    protected final List<GenWordPolynomial<C>> P;
    protected final SortedMap<Word,LinkedList<WordPair<C>>> pairlist;
    protected final List<BitSet> red;
    protected final GenWordPolynomialRing<C> ring;
    protected final WordReduction<C> reduction;
    protected boolean oneInGB = false;
    protected int putCount;
    protected int remCount;

    private static final Logger logger = Logger.getLogger(OrderedWordPairlist.class);


    /**
     * Constructor.
     */
    public OrderedWordPairlist() {
        ring = null;
        P = null;
        pairlist = null; 
        red = null;
        reduction = null;
        putCount = 0;
        remCount = 0;
    }


    /**
     * Constructor.
     * @param r word polynomial factory.
     */
    public OrderedWordPairlist(GenWordPolynomialRing<C> r) {
        ring = r;
        P = new ArrayList<GenWordPolynomial<C>>();
        pairlist = new TreeMap<Word,LinkedList<WordPair<C>>>( ring.alphabet.getAscendComparator() );
        red = new ArrayList<BitSet>();
        putCount = 0;
        remCount = 0;
        reduction = new WordReductionSeq<C>();
    }


    /**
     * Create a new WordPairList.
     * @param r word polynomial ring.
     */
    public WordPairList<C> create(GenWordPolynomialRing<C> r) {
        return new OrderedWordPairlist<C>(r);
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(this.getClass().getSimpleName() + "(");
        //s.append("polys="+P.size());
        s.append("#put="+putCount);
        s.append(", #rem="+remCount);
        if ( pairlist != null && pairlist.size() != 0 ) {
            s.append(", size="+pairlist.size());
        }
        s.append(")");
        return s.toString();
    }


    /**
     * Put one Polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(GenWordPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
            return P.size()-1;
        }
        Word e = p.leadingWord();
        int l = P.size();
        for ( int j = 0; j < l; j++ ) {
            GenWordPolynomial<C> pj = P.get(j);
            Word f = pj.leadingWord();
            Word g = f.lcm(e);
            //System.out.println("g = " + g);
            if ( g == null ) {
                //System.out.println("criterion 4");
                continue; // criterion 4
            }
            WordPair<C> pair = new WordPair<C>( pj, p, j, l);
            //System.out.println("pair.new      = " + pair);
            //multiple pairs under same keys -> list of pairs
            LinkedList<WordPair<C>> xl = pairlist.get( g );
            if ( xl == null ) {
                xl = new LinkedList<WordPair<C>>();
            }
            //xl.addLast( pair ); // first or last ?
            xl.addFirst( pair ); // first or last ? better for d- e-GBs
            pairlist.put( g, xl );
        }
        //System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
        //System.out.println("#pairlist = " + pairlist.size() );  
        P.add(  p );
        BitSet redi = new BitSet();
        redi.set( 0, l ); 
        red.add( redi );
        //System.out.println("pairlist.set = " + red); //.get( pair.j )); //pair);
        //System.out.println("pairlist.key = " + pairlist.keySet() );  
        return P.size()-1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized WordPair<C> removeNext() { 
        if ( oneInGB ) {
            return null;
        }
        Iterator< Map.Entry<Word,LinkedList<WordPair<C>>> > ip 
            = pairlist.entrySet().iterator();

        WordPair<C> pair = null;
        boolean c = false;
        int i, j;

        while ( !c && ip.hasNext() )  {
            Map.Entry<Word,LinkedList<WordPair<C>>> me = ip.next();
            Word g =  me.getKey();
            LinkedList<WordPair<C>> xl = me.getValue();
            if ( logger.isInfoEnabled() ) {
                logger.info("g  = " + g);
            }
            pair = null;
            while ( !c && xl.size() > 0 ) {
                pair = xl.removeFirst();
                // xl is also modified in pairlist 
                i = pair.i; 
                j = pair.j; 
                // System.out.println("pair(" + j + "," +i+") ");
                c = criterion3( i, j, g ); // should be okay
                //if ( !c ) {
                //    System.out.println("criterion 3");
                //}
                //System.out.println("c3_o  = " + c); 
                red.get( j ).clear(i); // set(i,false) jdk1.4
            }
            if ( xl.size() == 0 ) {
                ip.remove(); 
                // = pairlist.remove( g );
            }
        }
        if ( ! c ) {
            pair = null;
        } else {
            remCount++; // count only real pairs
        }
        return pair; 
    }


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public synchronized boolean hasNext() { 
        return pairlist.size() > 0;
    }


    /**
     * Get the list of polynomials.
     * @return the polynomial list.
     */
    public List<GenWordPolynomial<C>> getList() { 
        return P;
    }


    /**
     * Get the number of polynomials put to the pairlist.
     * @return the number of calls to put.
     */
    public synchronized int putCount() { 
        return putCount;
    }


    /**
     * Get the number of required pairs removed from the pairlist.
     * @return the number of non null pairs delivered.
     */
    public synchronized int remCount() { 
        return remCount;
    }


    /**
     * Put the ONE-Polynomial to the pairlist.
     * @param one polynomial. (no more required)
     * @return the index of the last polynomial.
     */
    public synchronized int putOne(GenWordPolynomial<C> one) { 
        if ( one == null ) {
            return P.size()-1;
        }
        if ( ! one.isONE() ) {
            return P.size()-1;
        }
        return putOne();
    }


    /**
     * Put the ONE-Polynomial to the pairlist.
     * @return the index of the last polynomial.
     */
    public synchronized int putOne() { 
        putCount++;
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add(ring.getONE());
        red.clear();
        return P.size()-1;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, Word eij) {  
        // assert i < j;
        boolean s = red.get( j ).get(i); 
        if ( ! s ) { 
            logger.warn("c3.s false for " + j + " " + i); 
            return s;
        }
        // now s = true;
        for ( int k = 0; k < P.size(); k++ ) {
            // System.out.println("i , k , j "+i+" "+k+" "+j); 
            if ( i != k && j != k ) {
                GenWordPolynomial<C> A = P.get( k );
                Word ek = A.leadingWord();
                boolean m = eij.multipleOf(ek);
                if ( m ) {
                    if ( k < i ) {
                        // System.out.println("k < i "+k+" "+i); 
                        s =    red.get( i ).get(k) 
                            || red.get( j ).get(k); 
                    } else if ( i < k && k < j ) {
                        // System.out.println("i < k < j "+i+" "+k+" "+j); 
                        s =    red.get( k ).get(i) 
                            || red.get( j ).get(k); 
                    } else if ( j < k ) {
                        //System.out.println("j < k "+j+" "+k); 
                        s =    red.get( k ).get(i) 
                            || red.get( k ).get(j); 
                    }
                    //System.out.println("s."+k+" = " + s); 
                    if ( ! s ) {
                        return s;
                    }
                }
            }
        }
        return true;
    }

}
