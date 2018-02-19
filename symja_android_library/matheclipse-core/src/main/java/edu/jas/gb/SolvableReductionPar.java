/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;
import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;

// import org.apache.log4j.Logger;


/**
 * Solvable polynomial Reduction parallel usable algorithm. Implements left
 * normalform.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableReductionPar<C extends RingElem<C>> extends SolvableReductionAbstract<C> {


    //private static final Logger logger = Logger.getLogger(SolvableReductionPar.class);


    /**
     * Constructor.
     */
    public SolvableReductionPar() {
    }


    /**
     * Left Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("cast")
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                   GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        Map.Entry<ExpVector, C> m;
        GenSolvablePolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
            //P = Pp.toArray();
            for (int j = 0; j < Pp.size(); j++) {
                P[j] = Pp.get(j);
            }
        }
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> Rz = Ap.ring.getZERO().copy();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();

        GenSolvablePolynomial<C> p = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            if (Pp.size() != l) {
                //long t = System.currentTimeMillis();
                synchronized (Pp) { // required, bad in parallel
                    l = Pp.size();
                    P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
                    //P = Pp.toArray();
                    for (int i = 0; i < Pp.size(); i++) {
                        P[i] = Pp.get(i);
                    }
                }
                //t = System.currentTimeMillis()-t;
                //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                S = Ap.copy(); // S.add(R)? // restart reduction ?
                R = Rz.copy();
            }
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (int i = 0; i < P.length; i++) {
                p = P[i];
                f = p.leadingExpVector();
                if (f != null) {
                    mt = e.multipleOf(f);
                    if (mt)
                        break;
                }
            }
            if (!mt) {
                //logger.debug("irred");
                //R = (GenSolvablePolynomial<C>) R.sum(a, e);
                //S = (GenSolvablePolynomial<C>) S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                //logger.debug("red");
                e = e.subtract(f);
                Q = p.multiplyLeft(e);
                a = a.divide(Q.leadingBaseCoefficient());
                //Q = Q.multiplyLeft(a);
                //S = (GenSolvablePolynomial<C>) S.subtract(Q);
                S = S.subtractMultiple(a, Q);
            }
        }
        return R;
    }


    /**
     * LeftNormalform with recording.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the left normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> row,
                                                   List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap) {
        throw new UnsupportedOperationException("normalform with recording not implemented");
    }


    /**
     * Right Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return right-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("cast")
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                    GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        Map.Entry<ExpVector, C> m;
        GenSolvablePolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
            //P = Pp.toArray();
            for (int j = 0; j < Pp.size(); j++) {
                P[j] = Pp.get(j);
            }
        }
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> Rz = Ap.ring.getZERO().copy();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();

        GenSolvablePolynomial<C> p = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            if (Pp.size() != l) {
                //long t = System.currentTimeMillis();
                synchronized (Pp) { // required, bad in parallel
                    l = Pp.size();
                    P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
                    //P = Pp.toArray();
                    for (int i = 0; i < Pp.size(); i++) {
                        P[i] = Pp.get(i);
                    }
                }
                //t = System.currentTimeMillis()-t;
                //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                S = Ap.copy(); // S.add(R)? // restart reduction ?
                R = Rz.copy();
            }
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (int i = 0; i < P.length; i++) {
                p = P[i];
                f = p.leadingExpVector();
                if (f != null) {
                    mt = e.multipleOf(f);
                    if (mt)
                        break;
                }
            }
            if (!mt) {
                //logger.debug("irred");
                //R = (GenSolvablePolynomial<C>) R.sum(a, e);
                //S = (GenSolvablePolynomial<C>) S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                //logger.debug("red");
                e = e.subtract(f);
                Q = p.multiply(e); // p * (a e) TODO
                a = a.divide(Q.leadingBaseCoefficient());
                Q = Q.multiply(a); // p * (e a) !!
                S = (GenSolvablePolynomial<C>) S.subtract(Q);
                //S = S.subtractMultiple(Q, a);
            }
        }
        return R;
    }


    /**
     * RightNormalform with recording.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the right normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> row,
                                                    List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap) {
        throw new UnsupportedOperationException("normalform with recording not implemented");
    }

}
