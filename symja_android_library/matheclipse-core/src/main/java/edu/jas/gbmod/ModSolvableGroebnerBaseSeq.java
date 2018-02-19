/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.List;

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gbufd.SGBFactory;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

// import org.apache.log4j.Logger;


/**
 * Module solvable Groebner Bases sequential class. Implements module solvable
 * Groebner bases and GB test.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @deprecated use respective methods from SolvableGroebnerBaseSeq
 */
@Deprecated
public class ModSolvableGroebnerBaseSeq<C extends GcdRingElem<C>> extends ModSolvableGroebnerBaseAbstract<C> {


    //private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBaseSeq.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Used Solvable Groebner base algorithm.
     */
    protected final SolvableGroebnerBaseAbstract<C> sbb;


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public ModSolvableGroebnerBaseSeq(RingFactory<C> cf) {
        this(SGBFactory.getImplementation(cf));
    }


    /**
     * Constructor.
     *
     * @param sbb solvable Groebner base implementation.
     */
    public ModSolvableGroebnerBaseSeq(SolvableGroebnerBaseAbstract<C> sbb) {
        this.sbb = sbb;
    }


    /**
     * Module left Groebner base test.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return sbb.isLeftGB(modv, F);
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return leftGB(F) a left Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> leftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return sbb.leftGB(modv, F);
    }


    /**
     * Module twosided Groebner base test.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return true, if F is a twosided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return sbb.isTwosidedGB(modv, F);
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return tsGB(F) a twosided Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> twosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return sbb.twosidedGB(modv, F);
    }


    /**
     * Module right Groebner base test.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        return sbb.isRightGB(modv, F);
    }


    /**
     * Right Groebner base using pairlist class.
     *
     * @param modv number of modul variables.
     * @param F    a module basis.
     * @return rightGB(F) a right Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> rightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        if (modv == 0) {
            return sbb.rightGB(modv, F);
        }
        throw new UnsupportedOperationException("modv != 0 not jet implemented");
        // return sbb.rightGB(modv,F);
    }

}
