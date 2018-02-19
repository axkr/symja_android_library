/*
 * $Id$
 */

package edu.jas.gbmod;


// import org.apache.log4j.Logger;

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gbufd.SGBFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Module solvable Groebner Bases parallel class. Implements module solvable
 * Groebner bases and GB test.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @deprecated use respective methods from SolvableGroebnerBaseParallel
 */
@Deprecated
public class ModSolvableGroebnerBasePar<C extends GcdRingElem<C>> extends ModSolvableGroebnerBaseSeq<C> {


    //private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBasePar.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public ModSolvableGroebnerBasePar(RingFactory<C> cf) {
        this(SGBFactory.getProxy(cf));
    }


    /**
     * Constructor.
     *
     * @param sbb parallel solvable Groebner base algorithm.
     */
    public ModSolvableGroebnerBasePar(SolvableGroebnerBaseAbstract<C> sbb) {
        super(sbb);
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        sbb.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        return sbb.cancel();
    }

}
