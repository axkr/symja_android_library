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

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Pair list management. For the Buchberger algorithm following the syzygy
 * criterions by Gebauer &amp; M&ouml;ller. Implemented using GenPolynomial,
 * TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedSyzPairlist<C extends RingElem<C>> extends OrderedPairlist<C> {


    private static final Logger logger = Logger.getLogger(OrderedSyzPairlist.class);


    /**
     * Constructor.
     */
    public OrderedSyzPairlist() {
        super();
    }


    /**
     * Constructor.
     * @param r polynomial factory.
     */
    public OrderedSyzPairlist(GenPolynomialRing<C> r) {
        this(0, r);
    }


    /**
     * Constructor.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedSyzPairlist(int m, GenPolynomialRing<C> r) {
        super(m, r);
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    @Override
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new OrderedSyzPairlist<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    @Override
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new OrderedSyzPairlist<C>(m, r);
    }


    /**
     * Put one Polynomial to the pairlist and reduction matrix. Removes all
     * unnecessary pairs identified by the syzygy criterion and criterion 4.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    @Override
    public synchronized int put(GenPolynomial<C> p) {
        putCount++;
        if (oneInGB) {
            return P.size() - 1;
        }
        ExpVector e = p.leadingExpVector();
        int ps = P.size();
        BitSet redi = new BitSet(); // all zeros
        //redi.set( 0, ps ); // [0..ps-1] = true, i.e. all ones
        red.add(redi);
        P.add(p);
        // remove from existing pairs:
        List<ExpVector> es = new ArrayList<ExpVector>();
        for (Map.Entry<ExpVector, LinkedList<Pair<C>>> me : pairlist.entrySet()) {
            ExpVector g = me.getKey();
            if (moduleVars > 0) {
                if (!reduction.moduleCriterion(moduleVars, e, g)) {
                    continue; // skip pair
                }
            }
            ExpVector ge = g.lcm(e);
            LinkedList<Pair<C>> ll = me.getValue();
            if (g.compareTo(ge) == 0) {
                LinkedList<Pair<C>> lle = new LinkedList<Pair<C>>();
                for (Pair<C> pair : ll) {
                    ExpVector eil = pair.pi.leadingExpVector().lcm(e);
                    if (g.compareTo(eil) == 0) {
                        continue;
                    }
                    ExpVector ejl = pair.pj.leadingExpVector().lcm(e);
                    if (g.compareTo(ejl) == 0) {
                        continue;
                    }
                    // g == ge && g != eil && g != ejl  
                    red.get(pair.j).clear(pair.i);
                    lle.add(pair);
                }
                if (lle.size() > 0) {
                    for (Pair<C> pair : lle) {
                        ll.remove(pair);
                    }
                    if (!es.contains(g)) {
                        es.add(g);
                    }
                }
            }
        }
        for (ExpVector ei : es) {
            LinkedList<Pair<C>> ll = pairlist.get(ei);
            if (ll != null && ll.size() == 0) {
                ll = pairlist.remove(ei);
            }
        }
        // generate new pairs:
        SortedMap<ExpVector, LinkedList<Pair<C>>> npl = new TreeMap<ExpVector, LinkedList<Pair<C>>>(
                        ring.tord.getAscendComparator());
        for (int j = 0; j < ps; j++) {
            GenPolynomial<C> pj = P.get(j);
            ExpVector f = pj.leadingExpVector();
            if (moduleVars > 0) {
                if (!reduction.moduleCriterion(moduleVars, e, f)) {
                    //red.get(j).clear(l); 
                    continue; // skip pair
                }
            }
            ExpVector g = e.lcm(f);
            Pair<C> pair = new Pair<C>(pj, p, j, ps);
            //System.out.println("pair.new      = " + pair);
            //multiple pairs under same keys -> list of pairs
            LinkedList<Pair<C>> xl = npl.get(g);
            if (xl == null) {
                xl = new LinkedList<Pair<C>>();
            }
            //xl.addLast( pair ); // first or last ?
            xl.addFirst(pair); // first or last ? better for d- e-GBs
            npl.put(g, xl);
        }
        //System.out.println("npl.new      = " + npl.keySet());
        // skip by divisibility:
        es = new ArrayList<ExpVector>(npl.size());
        for (ExpVector eil : npl.keySet()) {
            for (ExpVector ejl : npl.keySet()) {
                if (eil.compareTo(ejl) == 0) {
                    continue;
                }
                if (eil.multipleOf(ejl)) {
                    if (!es.contains(eil)) {
                        es.add(eil);
                    }
                }
            }
        }
        //System.out.println("npl.skip div = " + es);
        for (ExpVector ei : es) {
            LinkedList<Pair<C>> ignored = npl.remove(ei);
        }
        // skip by criterion 4:
        if (useCriterion4) {
            es = new ArrayList<ExpVector>(npl.size());
            for (Map.Entry<ExpVector, LinkedList<Pair<C>>> me : npl.entrySet()) {
                ExpVector ei = me.getKey();
                LinkedList<Pair<C>> exl = me.getValue(); //npl.get( ei );
                //System.out.println("exl = " + exl ); 
                boolean c = true;
                for (Pair<C> pair : exl) {
                    c = c && reduction.criterion4(pair.pi, pair.pj, pair.e);
                }
                if (c) {
                    if (exl.size() > 1) {
                        Pair<C> pair = exl.getFirst(); // or exl.getLast();
                        exl.clear();
                        exl.add(pair);
                        //npl.put(ei,exl);
                    }
                } else {
                    if (!es.contains(ei)) {
                        es.add(ei);
                    }
                }
            }
            //System.out.println("npl.skip c4  = " + es);
            for (ExpVector ei : es) {
                LinkedList<Pair<C>> ignored = npl.remove(ei);
            }
        }
        // add to existing pairlist:
        //System.out.println("npl.put new  = " + npl.keySet() );  
        for (Map.Entry<ExpVector, LinkedList<Pair<C>>> me : npl.entrySet()) {
            ExpVector ei = me.getKey();
            LinkedList<Pair<C>> exl = me.getValue(); //npl.get( ei );
            for (Pair<C> pair : exl) {
                red.get(pair.j).set(pair.i);
            }
            LinkedList<Pair<C>> ex = pairlist.get(ei); // wrong in findbugs
            if (ex != null) {
                exl.addAll(ex); // add new pairs first 
                ex = exl;
                //ex.addAll(exl); // add old pairs first
            } else {
                ex = exl;
            }
            pairlist.put(ei, ex); // replace ex
        }
        return P.size() - 1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    @Override
    public synchronized Pair<C> removeNext() {
        if (oneInGB) {
            return null;
        }
        Iterator<Map.Entry<ExpVector, LinkedList<Pair<C>>>> ip = pairlist.entrySet().iterator();
        Pair<C> pair = null;
        //boolean c = false;
        int i, j;
        while (ip.hasNext()) {
            Map.Entry<ExpVector, LinkedList<Pair<C>>> me = ip.next();
            ExpVector g = me.getKey();
            LinkedList<Pair<C>> xl = me.getValue();
            if (logger.isInfoEnabled())
                logger.info("g  = " + g);
            pair = null;
            while (xl.size() > 0) {
                pair = xl.removeFirst(); // xl is also modified in pairlist 
                i = pair.i;
                j = pair.j;
                //System.out.println("pair.remove = " + pair );
                if (!red.get(j).get(i)) { // should not happen
                    System.out.println("c_red.get(" + j + ").get(" + i + ") = " + g);
                    pair = null;
                    continue;
                }
                red.get(j).clear(i);
                break;
            }
            if (xl.size() == 0) {
                ip.remove(); // = pairlist.remove( g );
            }
            if (pair != null) {
                break;
            }
        }
        if ( pair != null ) {
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
    @Override
    public boolean criterion3(int i, int j, ExpVector eij) {
        throw new UnsupportedOperationException("not used in " + this.getClass().getName());
    }

}
