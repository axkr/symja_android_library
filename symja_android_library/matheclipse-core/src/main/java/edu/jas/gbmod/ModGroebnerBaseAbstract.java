/*
 * $Id$
 */

package edu.jas.gbmod;


import org.apache.log4j.Logger;

import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;


/**
 * Module Groebner Bases abstract class. Implements Groebner bases and GB test.
 *
 * @author Heinz Kredel
 * @deprecated use respective methods from GroebnerBaseAbstract
 */
@Deprecated
public abstract class ModGroebnerBaseAbstract<C extends GcdRingElem<C>> implements ModGroebnerBase<C> {


    private static final Logger logger = Logger.getLogger(ModGroebnerBaseAbstract.class);


    /**
     * isGB.
     *
     * @param M a module basis.
     * @return true, if M is a Groebner base, else false.
     */
    public boolean isGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return isGB(modv, F.list);
    }


    /**
     * GB.
     *
     * @param M a module basis.
     * @return GB(M), a Groebner base of M.
     */
    public ModuleList<C> GB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }

        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols;
        List<GenPolynomial<C>> G = GB(modv, F.list);
        F = new PolynomialList<C>(F.ring, G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        logger.info("terminate not implemented");
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        logger.info("cancel not implemented");
        return 0;
    }

}
