/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.List;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GBFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Module Groebner Bases sequential algorithm. Implements Groebner bases and GB
 * test.
 *
 * @author Heinz Kredel
 * @deprecated use respective methods from GroebnerBaseSeq
 */
@Deprecated
public class ModGroebnerBaseSeq<C extends GcdRingElem<C>> extends ModGroebnerBaseAbstract<C> {


    //private static final Logger logger = Logger.getLogger(ModGroebnerBaseSeq.class);


    /**
     * Used Groebner base algorithm.
     */
    protected final GroebnerBaseAbstract<C> bb;


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public ModGroebnerBaseSeq(RingFactory<C> cf) {
        this(GBFactory.getImplementation(cf));
    }


    /**
     * Constructor.
     *
     * @param bb Groebner base algorithm.
     */
    public ModGroebnerBaseSeq(GroebnerBaseAbstract<C> bb) {
        this.bb = bb;
    }


    /**
     * Module Groebner base test.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {
        return bb.isGB(modv, F);
    }


    /**
     * Groebner base using pairlist class.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        return bb.GB(modv, F);
    }

}
