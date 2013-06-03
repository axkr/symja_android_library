/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Pair list management.
 * The original Buchberger algorithm with criterions 
 * using early pair exclusion.
 * Implemented using GenPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedMinPairlist<C extends RingElem<C> > extends OrderedPairlist<C> {

    private static final Logger logger = Logger.getLogger(OrderedMinPairlist.class);


    /**
     * Constructor.
     */
    public OrderedMinPairlist() {
        super();
    }


    /**
     * Constructor.
     * @param r polynomial factory.
     */
    public OrderedMinPairlist(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedMinPairlist(int m, GenPolynomialRing<C> r) {
        super(m,r);
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new OrderedMinPairlist<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new OrderedMinPairlist<C>(m,r);
    }


    /**
     * Put one Polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(GenPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
            return P.size()-1;
        }
        ExpVector e = p.leadingExpVector();
        int l = P.size();
        BitSet redi = new BitSet();
        redi.set( 0, l ); // [0..l-1] = true
        red.add( redi );
        P.add(  p );
        for ( int j = 0; j < l; j++ ) {
            GenPolynomial<C> pj = P.get(j);
            ExpVector f = pj.leadingExpVector(); 
            if ( moduleVars > 0 ) {
                if ( !reduction.moduleCriterion( moduleVars, e, f) ) {
                    red.get(j).clear(l); 
                    continue; // skip pair
                }
            }
            ExpVector g =  e.lcm( f );
            //System.out.println("g  = " + g);  
            Pair<C> pair = new Pair<C>( pj, p, j, l);
            boolean c = true;
            if ( useCriterion4 ) {
                c = reduction.criterion4( pair.pi, pair.pj, g ); 
            }
            //System.out.println("c4  = " + c);  
            if ( c ) {
                c = criterion3( j, l, g );
                //System.out.println("c3  = " + c); 
            }
            if ( !c ) { // skip pair
                red.get(j).clear(l); 
                //System.out.println("c_skip = " + g); 
                continue; 
            }
            //multiple pairs under same keys -> list of pairs
            LinkedList<Pair<C>> xl = pairlist.get( g );
            if ( xl == null ) {
                xl = new LinkedList<Pair<C>>();
            }
            //xl.addLast( pair ); // first or last ?
            xl.addFirst( pair ); // first or last ? better for d- e-GBs
            pairlist.put( g, xl );
        }
        // System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
        return P.size()-1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized Pair<C> removeNext() { 
        if ( oneInGB ) {
            return null;
        }
        Iterator< Map.Entry<ExpVector,LinkedList<Pair<C>>> > ip 
            = pairlist.entrySet().iterator();

        Pair<C> pair = null;
        boolean c = false;
        int i, j;

        while ( !c && ip.hasNext() )  {
            Map.Entry<ExpVector,LinkedList<Pair<C>>> me = ip.next();
            ExpVector g =  me.getKey();
            LinkedList<Pair<C>> xl = me.getValue();
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
                if ( !red.get(j).get(i) ) {
                    System.out.println("c_y = " + g); // + ", " + red.get(j).get(i)); 
                    continue;
                }
                c = true;
                if ( useCriterion4 ) {
                    c = reduction.criterion4( pair.pi, pair.pj, g ); 
                }
                //System.out.println("c4_x = " + c);  
                if ( c ) {
                    c = criterion3( i, j, g );
                    //System.out.println("c3_x = " + c); 
                }
                if ( !c ) {
                    //System.out.println("c_x = " + g); 
                }
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
            pair.maxIndex(P.size()-1);
            remCount++; // count only real pairs
            if ( logger.isDebugEnabled() ) {
                logger.info("pair(" + pair.j + "," + pair.i + ")");
            }
        }
        return pair; 
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {  
        // assert i < j;
        boolean s;
        s = red.get( j ).get(i); 
        if ( ! s ) { 
            logger.warn("c3.s false for " + j + " " + i); 
            return s;
        }
        s = true;
        boolean m;
        GenPolynomial<C> A;
        ExpVector ek;
        for ( int k = 0; k < P.size(); k++ ) {
            A = P.get( k );
            ek = A.leadingExpVector();
            m = eij.multipleOf(ek) && eij.compareTo(ek) != 0;
            if ( m ) {
                if ( k < i ) {
                    // System.out.println("k < i "+k+" "+i); 
                    s =    red.get( i ).get(k) 
                        || red.get( j ).get(k); 
                }
                if ( i < k && k < j ) {
                    // System.out.println("i < k < j "+i+" "+k+" "+j); 
                    s =    red.get( k ).get(i) 
                        || red.get( j ).get(k); 
                }
                if ( j < k ) {
                    //System.out.println("j < k "+j+" "+k); 
                    s =    red.get( k ).get(i) 
                        || red.get( k ).get(j); 
                }
                //System.out.println("s."+k+" = " + s); 
                if ( ! s ) return s;
            }
        }
        return true;
    }

}
