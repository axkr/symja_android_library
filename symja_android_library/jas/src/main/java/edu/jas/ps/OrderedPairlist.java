/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.structure.RingElem;


/**
 * Pair list management. Implemented using MultiVarPowerSeries, TreeMap and
 * BitSet.
 * @author Heinz Kredel
 */

public class OrderedPairlist<C extends RingElem<C>> {


    protected final ArrayList<MultiVarPowerSeries<C>> P;


    protected final TreeMap<ExpVector, LinkedList<Pair<C>>> pairlist;


    protected final ArrayList<BitSet> red;


    protected final MultiVarPowerSeriesRing<C> ring;


    protected final ReductionSeq<C> reduction;


    protected boolean oneInGB = false;


    protected boolean useCriterion4 = true;


    protected boolean useCriterion3 = true;


    protected int putCount;


    protected int remCount;


    protected final int moduleVars;


    private static final Logger logger = Logger.getLogger(OrderedPairlist.class);


    /**
     * Constructor for OrderedPairlist.
     * @param r power series factory.
     */
    public OrderedPairlist(MultiVarPowerSeriesRing<C> r) {
        this(0, r);
    }


    /**
     * Constructor for OrderedPairlist.
     * @param m number of module variables.
     * @param r power series factory.
     */
    public OrderedPairlist(int m, MultiVarPowerSeriesRing<C> r) {
        moduleVars = m;
        ring = r;
        P = new ArrayList<MultiVarPowerSeries<C>>();
        pairlist = new TreeMap<ExpVector, LinkedList<Pair<C>>>(ring.polyRing().tord.getAscendComparator());
        //pairlist = new TreeMap<ExpVector, LinkedList<Pair<C>>>(ring.polyRing().tord.getDescendComparator());
        red = new ArrayList<BitSet>();
        putCount = 0;
        remCount = 0;
        reduction = new ReductionSeq<C>();
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("OrderedPairlist(");
        //s.append("ps="+P.size());
        s.append("#put=" + putCount);
        s.append(", #rem=" + remCount);
        if (pairlist.size() != 0) {
            s.append(", size=" + pairlist.size());
        }
        s.append(")");
        return s.toString();
    }


    /**
     * Put one power Series to the pairlist and reduction matrix.
     * @param p power series.
     * @return the index of the added power series.
     */
    public synchronized int put(MultiVarPowerSeries<C> p) {
        putCount++;
        if (oneInGB) {
            return P.size() - 1;
        }
        ExpVector e = p.orderExpVector();
        int l = P.size();
        for (int j = 0; j < l; j++) {
            MultiVarPowerSeries<C> pj = P.get(j);
            ExpVector f = pj.orderExpVector();
            if (moduleVars > 0) {
                if (!reduction.moduleCriterion(moduleVars, e, f)) {
                    continue; // skip pair
                }
            }
            ExpVector g = e.lcm(f);
            Pair<C> pair = new Pair<C>(pj, p, j, l);
            // redi = (BitSet)red.get(j);
            ///if ( j < l ) redi.set( l );
            // System.out.println("bitset."+j+" = " + redi );  

            //multiple pairs under same keys -> list of pairs
            LinkedList<Pair<C>> xl = pairlist.get(g);
            if (xl == null) {
                xl = new LinkedList<Pair<C>>();
            }
            //xl.addLast( pair ); // first or last ?
            xl.addFirst(pair); // first or last ? better for d- e-GBs
            pairlist.put(g, xl);
        }
        // System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
        P.add(p);
        BitSet redi = new BitSet();
        redi.set(0, l); // jdk 1.4
        red.add(redi);
        return P.size() - 1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Apply the criterions 3 and 4 to see if the S-power-series is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized Pair<C> removeNext() {
        if (oneInGB) {
            return null;
        }
        Iterator<Map.Entry<ExpVector, LinkedList<Pair<C>>>> ip = pairlist.entrySet().iterator();

        Pair<C> pair = null;
        boolean c = false;
        int i, j;

        while (!c && ip.hasNext()) {
            Map.Entry<ExpVector, LinkedList<Pair<C>>> me = ip.next();
            ExpVector g = me.getKey();
            LinkedList<Pair<C>> xl = me.getValue();
            if (logger.isInfoEnabled())
                logger.info("g  = " + g);
            pair = null;
            while (!c && xl.size() > 0) {
                pair = xl.removeFirst();
                // xl is also modified in pairlist 
                i = pair.i;
                j = pair.j;
                // System.out.println("pair(" + j + "," +i+") ");
                c = true;
                if (useCriterion4) {
                    c = reduction.criterion4(pair.pi, pair.pj, g);
                }
                //System.out.println("c4  = " + c);  
                if (c && useCriterion3) {
                    c = criterion3(i, j, g);
                    //System.out.println("c3  = " + c); 
                }
                red.get(j).clear(i); // set(i,false) jdk1.4
            }
            if (xl.size() == 0)
                ip.remove();
            // = pairlist.remove( g );
        }
        if (!c) {
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
     * Get the list of power series.
     * @return the power series list.
     */
    public List<MultiVarPowerSeries<C>> getList() {
        return P;
    }


    /**
     * Get the number of power series put to the pairlist.
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
     * Put to ONE-power-series to the pairlist.
     * @param one power series. (no more required)
     * @return the index of the last power series.
     */
    public synchronized int putOne(MultiVarPowerSeries<C> one) {
        putCount++;
        if (one == null) {
            return P.size() - 1;
        }
        if (!one.isONE()) {
            return P.size() - 1;
        }
        return putOne();
    }


    /**
     * Put the ONE-power-series to the pairlist.
     * @return the index of the last power-series.
     */
    public synchronized int putOne() { 
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add(ring.getONE());
        red.clear();
        return P.size() - 1;
    }


    /**
     * GB criterion 3.
     * @return true if the S-power-series(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {
        // assert i < j;
        boolean s = red.get(j).get(i);
        if (!s) {
            logger.warn("c3.s false for " + j + " " + i);
            return s;
        }
        // now s = true;
        for (int k = 0; k < P.size(); k++) {
            MultiVarPowerSeries<C> A = P.get(k);
            ExpVector ek = A.orderExpVector();
            boolean m = eij.multipleOf(ek);
            if (m) {
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
                //System.out.println("s."+k+" = " + s); 
                if (!s) {
                    return s;
                }
            }
        }
        return true;
    }

}
