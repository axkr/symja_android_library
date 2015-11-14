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
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.Word;
import edu.jas.structure.RingElem;


/**
 * Pair list management of word polynomials. Implemented using
 * GenWordPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedWordPairlist<C extends RingElem<C>> implements WordPairList<C> {


    protected final List<GenWordPolynomial<C>> P;


    protected final SortedMap<Word, LinkedList<WordPair<C>>> pairlist;


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
        pairlist = new TreeMap<Word, LinkedList<WordPair<C>>>(ring.alphabet.getAscendComparator());
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
        s.append("#put=" + putCount);
        s.append(", #rem=" + remCount);
        if (pairlist != null && pairlist.size() != 0) {
            s.append(", size=" + pairlist.size());
        }
        //if (red != null ) {
        //    s.append(", bitmask=" + red);
        //}
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
        if (oneInGB) {
            return P.size() - 1;
        }
        if (p.isONE()) {
            return putOne();
        }
        Word e = p.leadingWord();
        Word g;
        int l = P.size();
        BitSet redi = new BitSet();
        //redi.set(0, l); // from -- to
        for (int j = 0; j < l; j++) {
            GenWordPolynomial<C> pj = P.get(j);
            Word f = pj.leadingWord();

            // pj, p
            g = f.lcm(e);
            //System.out.println("g(f,g) = " + g);
            if (g != null) {
                WordPair<C> pair = new WordPair<C>(pj, p, j, l);
                //System.out.println("pair.new      = " + pair);
                //multiple pairs under same keys -> list of pairs
                LinkedList<WordPair<C>> xl = pairlist.get(g);
                if (xl == null) {
                    xl = new LinkedList<WordPair<C>>();
                }
                //xl.addLast( pair ); // first or last ?
                xl.addFirst(pair); // first or last ? better for d- e-GBs
                pairlist.put(g, xl);
                redi.set(j); // = red.get(l).set(j);
            }

            // p, pj
            g = e.lcm(f);
            //System.out.println("g(e,f) = " + g);
            if (g != null) {
                WordPair<C> pair = new WordPair<C>(p, pj, l, j);
                //System.out.println("pair.new      = " + pair);
                //multiple pairs under same keys -> list of pairs
                LinkedList<WordPair<C>> xl = pairlist.get(g);
                if (xl == null) {
                    xl = new LinkedList<WordPair<C>>();
                }
                //xl.addLast( pair ); // first or last ?
                xl.addFirst(pair); // first or last ? better for d- e-GBs
                pairlist.put(g, xl);
                red.get(j).set(l);
            }
        }
        red.add(redi);
        //System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
        //System.out.println("#pairlist = " + pairlist.size() );  
        P.add(p);
        //System.out.println("pairlist.key = " + pairlist.keySet() );  
        return l; //P.size() - 1;
    }


    /**
     * Put all word polynomials in F to the pairlist and reduction matrix.
     * @param F word polynomial list.
     * @return the index of the last added word polynomial.
     */
    public int put(List<GenWordPolynomial<C>> F) {
        //System.out.println("pairlist.F = " + F );  
        int i = 0;
        for (GenWordPolynomial<C> p : F) {
            i = put(p);
        }
        return i;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterion 3 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized WordPair<C> removeNext() {
        if (oneInGB) {
            return null;
        }
        Iterator<Map.Entry<Word, LinkedList<WordPair<C>>>> ip = pairlist.entrySet().iterator();

        WordPair<C> pair = null;
        //boolean c = false;
        int i, j;

        //while (!c && ip.hasNext()) {
        if (ip.hasNext()) {
            Map.Entry<Word, LinkedList<WordPair<C>>> me = ip.next();
            Word g = me.getKey();
            LinkedList<WordPair<C>> xl = me.getValue();
            if (logger.isInfoEnabled()) {
                logger.info("g  = " + g);
            }
            pair = null;
            //while (!c && xl.size() > 0) {
            if (xl.size() > 0) {
                pair = xl.removeFirst();
                // xl is also modified in pairlist 
                i = pair.i;
                j = pair.j;
                // System.out.println("pair(" + j + "," +i+") ");
                //c = criterion3(i, j, g); // to check
                //if ( !c ) {
                //    System.out.println("criterion 3");
                //}
                red.get(j).clear(i);
            }
            if (xl.size() == 0) {
                ip.remove();
            }
        }
        //if (!c) {
        //    pair = null;
        //} else {
        remCount++; // count only real pairs
        //}
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
        if (one == null) {
            return P.size() - 1;
        }
        if (!one.isONE()) {
            return P.size() - 1;
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
        return P.size() - 1;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, Word eij) {
        boolean s = red.get(j).get(i);
        //if (s) {
        //   logger.warn("c3.s true for " + i + " " + j);
        //   //return s;
        //}
        for (int k = 0; k < P.size(); k++) {
            // System.out.println("i , k , j "+i+" "+k+" "+j); 
            if (i != k && j != k) {
                GenWordPolynomial<C> A = P.get(k);
                Word ek = A.leadingWord();
                boolean m = eij.multipleOf(ek);
                if (m) {
                    if (i < j) {
                        if (k < i) {
                            // System.out.println("k < i "+k+" "+i); 
                            s = red.get(i).get(k) || red.get(j).get(k);
                        } else if (i < k && k < j) {
                            // System.out.println("i < k < j "+i+" "+k+" "+j); 
                            s = red.get(k).get(i) || red.get(j).get(k);
                        } else if (j < k) {
                            //System.out.println("j < k "+j+" "+k); 
                            s = red.get(k).get(i) || red.get(k).get(j);
                        }
                    } else { // j < i
                        if (k < j) {
                            //System.out.println("k < j "+k+" "+j); 
                            s = red.get(k).get(j) || red.get(k).get(i);
                        } else if (j < k && k < i) {
                            //System.out.println("j < k < i "+j+" "+k+" "+i); 
                            s = red.get(j).get(k) || red.get(k).get(i);
                        } else if (i < k) {
                            //System.out.println("i < k "+i+" "+k); 
                            s = red.get(j).get(k) || red.get(i).get(k);
                        }
                    }
                    //System.out.println("s."+k+" = " + s); 
                    if (!s) {
                        return s;
                    }
                }
            }
        }
        return true;
    }

}
