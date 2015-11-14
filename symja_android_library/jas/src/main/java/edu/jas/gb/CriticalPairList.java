/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Critical pair list management.
 * Makes some effort to produce the same sequence of critical pairs 
 * as in the sequential case, when used in parallel.
 * However already reduced pairs are not re-reduced if new
 * polynomials appear.
 * Implemented using GenPolynomial, SortedSet / TreeSet and BitSet.
 * @author Heinz Kredel
 */

public class CriticalPairList<C extends RingElem<C>> extends OrderedPairlist<C> {

    protected final SortedSet< CriticalPair<C> > pairlist; // hide super

    protected int recordCount;

    private static final Logger logger = Logger.getLogger(CriticalPairList.class);


    /**
     * Constructor for CriticalPairList.
     */
    public CriticalPairList() {
        super();
        pairlist = null;
    }


    /**
     * Constructor for CriticalPairList.
     * @param r polynomial factory.
     */
    public CriticalPairList(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor for CriticalPairList.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public CriticalPairList(int m, GenPolynomialRing<C> r) {
        super(m,r);
        Comparator< AbstractPair<C> > cpc; 
        cpc = new CriticalPairComparator<C>( ring.tord ); 
        pairlist = new TreeSet< CriticalPair<C> >( cpc );
        recordCount = 0;
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new CriticalPairList<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new CriticalPairList<C>(m,r);
    }


    /**
     * Put a polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(GenPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
           return P.size()-1;
        }
        ExpVector e = p.leadingExpVector(); 
        int len = P.size();
        for ( int j = 0; j < len; j++ ) {
            GenPolynomial<C> pj = P.get(j);
            ExpVector f = pj.leadingExpVector(); 
            if ( moduleVars > 0 ) { // test moduleCriterion
            if ( !reduction.moduleCriterion( moduleVars, e, f) ) {
                  continue; // skip pair
               }
            }
            ExpVector g =  e.lcm( f );
            CriticalPair<C> pair = new CriticalPair<C>( g, pj, p, j, len );
            //System.out.println("put pair = " + pair );
            pairlist.add( pair );
        }
        P.add( p );
        BitSet redi = new BitSet();
        redi.set( 0, len ); // >= jdk 1.4
        red.add( redi );
        if ( recordCount < len ) {
            recordCount = len;
        }
        return len;
    }


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public synchronized boolean hasNext() { 
          return pairlist.size() > 0;
    }


    /**
     * Get and remove the next required pair from the pairlist.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * The pair is not removed from the pair list.
     * @return the next pair if one exists, otherwise null.
     */
    public Pair<C> removeNext() { 
        CriticalPair<C> cp = getNext();
        if ( cp == null ) {
            return null;
        }
        return new Pair<C>(cp.pi,cp.pj,cp.i,cp.j);
    }


    /**
     * Get the next required pair from the pairlist.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * The pair is not removed from the pair list.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized CriticalPair<C> getNext() { 
        if ( oneInGB ) {
           return null;
        }
        CriticalPair<C> pair = null;
        Iterator< CriticalPair<C> > ip = pairlist.iterator();
        boolean c = false;
        while ( !c && ip.hasNext() )  { // findbugs
           pair = ip.next();
           if ( pair.getInReduction() ) {
               continue;
           }
           if ( pair.getReductum() != null ) {
               continue;
           }
           if ( logger.isInfoEnabled() ) {
              logger.info("" + pair);
           }
           if ( useCriterion4 ) {
              c = reduction.criterion4( pair.pi, pair.pj, pair.e ); 
              // System.out.println("c4  = " + c); 
           } else {
              c = true;
           }
           if ( c ) {
              c = criterion3( pair.i, pair.j, pair.e );
              // System.out.println("c3  = " + c); 
           }
           red.get( pair.j ).clear( pair.i ); // set(i,false) jdk1.4
           if ( ! c ) { // set done
               pair.setReductum( ring.getZERO() );
           }
        }
        if ( ! c ) {
           pair = null;
        } else {
           remCount++; // count only real pairs
           pair.setInReduction(); // set to work
        }
        return pair; 
    }


    /**
     * Record reduced polynomial.
     * @param pair the corresponding critical pair.
     * @param p polynomial.
     * @return index of recorded polynomial, or -1 if not added.
     */
    public int record(CriticalPair<C> pair, GenPolynomial<C> p) { 
        if ( p == null ) {
            p = ring.getZERO();
        }
        pair.setReductum(p);
        // trigger thread
        if ( ! p.isZERO() && ! p.isONE() ) {
           recordCount++;
           return recordCount;
        }
        return -1;
    }


    /**
     * Record reduced polynomial and update critical pair list. 
     * Note: it is better to use record and uptate separately.
     * @param pair the corresponding critical pair.
     * @param p polynomial.
     * @return index of recorded polynomial
     */
    public int update(CriticalPair<C> pair, GenPolynomial<C> p) { 
        if ( p == null ) {
            p = ring.getZERO();
        }
        pair.setReductum(p);
        if ( ! p.isZERO() && ! p.isONE() ) {
           recordCount++;
        }
        int c = update();
        if (c < 0) { // use for findbugs 
            System.out.println("c < 0");
        }
        if ( ! p.isZERO() && ! p.isONE() ) {
           return recordCount;
        } 
        return -1;
    }


    /**
     * Update pairlist.
     * Preserve the sequential pair sequence.
     * Remove pairs with completed reductions.
     * @return the number of added polynomials.
     */
    public synchronized int update() { 
        int num = 0;
        if ( oneInGB ) {
           return num;
        }
        while ( pairlist.size() > 0 ) {
            CriticalPair<C> pair = pairlist.first();
            GenPolynomial<C> p = pair.getReductum();
            if ( p != null ) {
               pairlist.remove( pair );
               num++;
               if ( ! p.isZERO() ) {
                  if ( p.isONE() ) {
                     putOne(); // sets size = 1
                  } else {
                     put( p ); // changes pair list
                  }
               }
            } else {
               break;
            }
        }
        return num;
    }


    /**
     * In work pairs. List pairs which are currently reduced.
     * @return list of critical pairs which are in reduction.
     */
    public synchronized List<CriticalPair<C>> inWork() { 
        List<CriticalPair<C>> iw;
        iw = new ArrayList<CriticalPair<C>>();
        if ( oneInGB ) {
            return iw;
        }
        for ( CriticalPair<C> pair : pairlist ) {
            if ( pair.getInReduction() ) {
               iw.add( pair );
            }
        }
        return iw;
    }


    /**
     * Update pairlist, several pairs at once. 
     * This version does not preserve the sequential pair sequence.
     * Remove pairs with completed reductions.
     * @return the number of added polynomials.
     */
    public synchronized int updateMany() { 
        int num = 0;
        if ( oneInGB ) {
           return num;
        }
        List<CriticalPair<C>> rem = new ArrayList<CriticalPair<C>>();
        for ( CriticalPair<C> pair : pairlist ) {
            if ( pair.getReductum() != null ) {
               rem.add( pair );
               num++;
            } else {
               break;
            }
        }
        // must work on a copy to avoid concurrent modification
        for ( CriticalPair<C> pair : rem ) {
            // System.out.println("update = " + pair); 
            pairlist.remove( pair );
            GenPolynomial<C> p = pair.getReductum();
            if ( ! p.isZERO() ) {
               if ( p.isONE() ) {
                  putOne();
               } else {
                  put( p );
               }
            }
        }
        return num;
    }


    /**
     * Put the ONE-Polynomial to the pairlist.
     * @return the index of the last polynomial.
     */
    public synchronized int putOne() { 
        super.putOne();
        pairlist.clear();
        return 0;
    }

}
