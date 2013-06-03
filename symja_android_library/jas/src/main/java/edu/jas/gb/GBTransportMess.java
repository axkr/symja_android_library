/*
 * $Id$
 */

package edu.jas.gb;


import java.io.Serializable;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Distributed GB transport message.  This class and its subclasses
 * are used for transport of polynomials and pairs and as markers in
 * distributed GB algorithms.
 */

public class GBTransportMess implements Serializable {


    public GBTransportMess() {
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}


/**
 * Distributed GB transport message for requests.
 */

final class GBTransportMessReq extends GBTransportMess {


    public GBTransportMessReq() {
    }
}


/**
 * Distributed GB transport message for termination.
 */

final class GBTransportMessEnd extends GBTransportMess {


    public GBTransportMessEnd() {
    }
}


/**
 * Distributed GB transport message for polynomial.
 */

final class GBTransportMessPoly<C extends RingElem<C>> extends GBTransportMess {


    /**
     * The polynomial to transport.
     */
    public final GenPolynomial<C> pol;


    /**
     * GBTransportMessPoly.
     * @param p polynomial to transfered.
     */
    public GBTransportMessPoly(GenPolynomial<C> p) {
        this.pol = p;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + pol + " )";
    }
}


/**
 * Distributed GB transport message for pairs.
 */

final class GBTransportMessPair<C extends RingElem<C>> extends GBTransportMess {


    public final Pair<C> pair;


    /**
     * GBTransportMessPair.
     * @param p pair for transfer.
     */
    public GBTransportMessPair(Pair<C> p) {
        this.pair = p;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + pair + " )";
    }
}


/**
 * Distributed GB transport message for index pairs.
 */

final class GBTransportMessPairIndex extends GBTransportMess {


    public final int i;


    public final int j;


    public final int s; 


    /**
     * GBTransportMessPairIndex.
     * @param p pair for transport.
     */
    public GBTransportMessPairIndex(Pair p) {
        this(p.i,p.j,p.s);
    }


    /**
     * GBTransportMessPairIndex.
     * @param i first index.
     * @param j second index.
     */
    @Deprecated
    public GBTransportMessPairIndex(int i, int j) {
        this(i,j,0);
    }


    /**
     * GBTransportMessPairIndex.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public GBTransportMessPairIndex(int i, int j, int s) {
        this.i = i;
        this.j = j;
        s = Math.max(this.i,s);
        s = Math.max(this.j,s);
        this.s = s;
    }


    /**
     * GBTransportMessPairIndex.
     * @param i first index.
     * @param j second index.
     * @param s maximal index.
     */
    public GBTransportMessPairIndex(Integer i, Integer j, Integer s) {
        this(i.intValue(),j.intValue(),s.intValue());
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + i + "," + j + "," + s + ")";
    }

}
