/*
 * $Id$
 */

package edu.jas.gbmod;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;


/**
 * Module solvable Groebner Bases abstract class. Implements module solvable
 * Groebner bases and GB test.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @deprecated use respective methods from SolvableGroebnerBaseAbstract
 */
@Deprecated
public abstract class ModSolvableGroebnerBaseAbstract<C extends RingElem<C>> implements
        ModSolvableGroebnerBase<C> {


    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBaseAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Module left Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public boolean isLeftGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return isLeftGB(modv, F.castToSolvableList());
    }


    /**
     * Left Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> leftGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        if (debug) {
            logger.info("F left +++++++++++++++++++ \n" + F);
        }
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G = leftGB(modv, F.castToSolvableList());
        F = new PolynomialList<C>(sring, G);
        if (debug) {
            logger.info("G left +++++++++++++++++++ \n" + F);
        }
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module twosided Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public boolean isTwosidedGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return isTwosidedGB(modv, F.castToSolvableList());
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> twosidedGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G = twosidedGB(modv, F.castToSolvableList());
        F = new PolynomialList<C>(sring, G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module right Groebner base test.
     *
     * @param M a module basis.
     * @return true, if M is a right Groebner base, else false.
     */
    public boolean isRightGB(ModuleList<C> M) {
        if (M == null || M.list == null) {
            return true;
        }
        if (M.rows == 0 || M.cols == 0) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        //System.out.println("F test = " + F);
        return isRightGB(modv, F.castToSolvableList());
    }


    /**
     * Right Groebner base using pairlist class.
     *
     * @param M a module basis.
     * @return rightGB(M) a right Groebner base for M.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> rightGB(ModuleList<C> M) {
        ModuleList<C> N = M;
        if (M == null || M.list == null) {
            return N;
        }
        if (M.rows == 0 || M.cols == 0) {
            return N;
        }
        if (debug) {
            logger.info("M ====================== \n" + M);
        }
        TermOrder to = M.ring.tord;
        if (to.getSplit() <= M.ring.nvar) {
            throw new IllegalArgumentException("extended TermOrders not supported for rightGBs: " + to);
        }
        List<List<GenSolvablePolynomial<C>>> mlist = M.castToSolvableList();
        GenSolvablePolynomialRing<C> sring = (GenSolvablePolynomialRing<C>) M.ring;
        GenSolvablePolynomialRing<C> rring = sring.reverse(true); //true
        sring = rring.reverse(true); // true

        List<List<GenSolvablePolynomial<C>>> nlist = new ArrayList<List<GenSolvablePolynomial<C>>>(M.rows);
        for (List<GenSolvablePolynomial<C>> row : mlist) {
            List<GenSolvablePolynomial<C>> nrow = new ArrayList<GenSolvablePolynomial<C>>(row.size());
            for (GenSolvablePolynomial<C> elem : row) {
                GenSolvablePolynomial<C> nelem = (GenSolvablePolynomial<C>) elem.reverse(rring);
                nrow.add(nelem);
            }
            nlist.add(nrow);
        }
        ModuleList<C> rM = new ModuleList<C>(rring, nlist);
        if (debug) {
            logger.info("rM -------------------- \n" + rM);
        }
        ModuleList<C> rMg = leftGB(rM);
        if (debug) {
            logger.info("rMg -------------------- \n" + rMg);
            logger.info("isLeftGB(rMg) ---------- " + isLeftGB(rMg));
        }
        mlist = rMg.castToSolvableList();
        nlist = new ArrayList<List<GenSolvablePolynomial<C>>>(rMg.rows);
        for (List<GenSolvablePolynomial<C>> row : mlist) {
            List<GenSolvablePolynomial<C>> nrow = new ArrayList<GenSolvablePolynomial<C>>(row.size());
            for (GenSolvablePolynomial<C> elem : row) {
                GenSolvablePolynomial<C> nelem = (GenSolvablePolynomial<C>) elem.reverse(sring);
                nrow.add(nelem);
            }
            nlist.add(nrow);
        }
        ModuleList<C> Mg = new ModuleList<C>(sring, nlist);
        if (debug) {
            logger.info("Mg -------------------- \n" + Mg);
            logger.info("isRightGB(Mg) --------- " + isRightGB(Mg));
        }
        return Mg;
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
