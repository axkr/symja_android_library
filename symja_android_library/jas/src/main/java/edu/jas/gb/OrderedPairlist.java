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

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.structure.RingElem;

/**
 * Pair list management.
 * The original Buchberger algorithm with criterions following 
 * Winkler in SAC-1, Kredel in ALDES/SAC-2, Kredel in MAS.
 * Implemented using GenPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedPairlist<C extends RingElem<C> > implements PairList<C> {

    protected final List<GenPolynomial<C>> P;
    protected final SortedMap<ExpVector,LinkedList<Pair<C>>> pairlist;
    protected final List<BitSet> red;
    protected final GenPolynomialRing<C> ring;
    protected final Reduction<C> reduction;
    protected boolean oneInGB = false;
    protected boolean useCriterion4 = true;
    protected int putCount;
    protected int remCount;
    protected final int moduleVars;

    private static final Logger logger = Logger.getLogger(OrderedPairlist.class);


    /**
     * Constructor.
     */
    public OrderedPairlist() {
        moduleVars = 0;
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
     * @param r polynomial factory.
     */
    public OrderedPairlist(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedPairlist(int m, GenPolynomialRing<C> r) {
        moduleVars = m;
        ring = r;
        P = new ArrayList<GenPolynomial<C>>();
        pairlist = new TreeMap<ExpVector,LinkedList<Pair<C>>>( ring.tord.getAscendComparator() );
        //pairlist = new TreeMap( to.getSugarComparator() );
        red = new ArrayList<BitSet>();
        putCount = 0;
        remCount = 0;
        if ( !ring.isCommutative() ) {//ring instanceof GenSolvablePolynomialRing ) {
            useCriterion4 = false;
        }
        reduction = new ReductionSeq<C>();
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new OrderedPairlist<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new OrderedPairlist<C>(m,r);
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
    public synchronized int put(GenPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
            return P.size()-1;
        }
        ExpVector e = p.leadingExpVector();
        int l = P.size();
        for ( int j = 0; j < l; j++ ) {
            GenPolynomial<C> pj = P.get(j);
            ExpVector f = pj.leadingExpVector(); 
            if ( moduleVars > 0 ) {
                if ( !reduction.moduleCriterion( moduleVars, e, f) ) {
                    continue; // skip pair
                }
            }
            ExpVector g =  e.lcm( f );
            Pair<C> pair = new Pair<C>( pj, p, j, l);
            //System.out.println("pair.new      = " + pair);
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
                if ( useCriterion4 ) {
                    c = reduction.criterion4( pair.pi, pair.pj, g ); 
                } else {
                    c = true;
                }
                //System.out.println("c4_o  = " + c);  
                if ( c ) {
                    c = criterion3( i, j, g );
                    //System.out.println("c3_o  = " + c); 
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
    public List<GenPolynomial<C>> getList() { 
        return P;
    }


    /**
     * Get the size of the list of polynomials.
     * @return size of the polynomial list.
     */
    public int size() {
        return P.size();
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
    public synchronized int putOne(GenPolynomial<C> one) { 
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
        logger.info("outOne " + this.toString());
        return P.size()-1;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {  
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
                GenPolynomial<C> A = P.get( k );
                ExpVector ek = A.leadingExpVector();
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
