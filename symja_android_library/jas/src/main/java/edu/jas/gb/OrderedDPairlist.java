/*
 * $Id$
 */

package edu.jas.gb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Pair list management for d-Groebner bases.
 * Implemented using GenPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedDPairlist<C extends RingElem<C> > 
    extends OrderedPairlist<C> {

    private static final Logger logger = Logger.getLogger(OrderedDPairlist.class);

    protected final DReduction<C> dreduction;


    /**
     * Constructor for OrderedDPairlist.
     * @param r polynomial factory.
     */
    public OrderedDPairlist(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor for OrderedDPairlist.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedDPairlist(int m, GenPolynomialRing<C> r) {
        super(m,r);
        dreduction = new DReductionSeq<C>();
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new OrderedDPairlist<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new OrderedDPairlist<C>(m,r);
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * The results of the application of the criterions 3 and 4 to see if the S-polynomial 
     * is required are recorded in the Pair.
     * @return the next pair if one exists, otherwise null.
     */
    @Override
    public synchronized Pair<C> removeNext() { 
        if ( oneInGB ) {
            return null;
        }
        Iterator< Map.Entry<ExpVector,LinkedList<Pair<C>>> > ip 
            = pairlist.entrySet().iterator();

        Pair<C> pair = null;
        boolean c = false;
        int i, j;

        if  ( ip.hasNext() )  {
            Map.Entry<ExpVector,LinkedList<Pair<C>>> me = ip.next();
            ExpVector g =  me.getKey();
            LinkedList<Pair<C>> xl = me.getValue();
            if ( logger.isInfoEnabled() ) {
                logger.info("g  = " + g);
            }
            pair = null;
            if ( xl.size() > 0 ) {
                pair = xl.removeFirst();
                // xl is also modified in pairlist 
                i = pair.i; 
                j = pair.j; 
                // System.out.println("pair(" + j + "," +i+") ");
                if ( useCriterion4 ) {
                    c = dreduction.criterion4( pair.pi, pair.pj, g ); 
                } else {
                    c = true;
                }
                pair.setUseCriterion4(c);
                //System.out.println("c4  = " + c);  
                if ( c ) {
                    c = criterion3( i, j, g );
                    //System.out.println("c3  = " + c); 
                    pair.setUseCriterion3(c);
                }
                red.get( j ).clear(i); // set(i,false) jdk1.4
            }
            if ( xl.size() == 0 ) {
                ip.remove(); // = pairlist.remove( g );
            }
        }
        remCount++; // count pairs
        return pair; 
    }


    /**
     * GB criterium 3 with coefficient division test.
     * @return true if the S-polynomial(i,j) is required.
     */
    @Override
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
        C ci = P.get(i).leadingBaseCoefficient();
        C cj = P.get(j).leadingBaseCoefficient();
        C c = ci.gcd(cj);
        for ( int k = 0; k < P.size(); k++ ) {
            A = P.get( k );
            ek = A.leadingExpVector();
            m = eij.multipleOf(ek);
            if ( m ) {
                C ck = A.leadingBaseCoefficient();
                C r = c.remainder(ck);
                m = r.isZERO();
            }
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
