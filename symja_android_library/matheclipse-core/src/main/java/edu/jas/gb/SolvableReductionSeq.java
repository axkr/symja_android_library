/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Solvable polynomial Reduction algorithm. Implements left, right normalform.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableReductionSeq<C extends RingElem<C>> extends SolvableReductionAbstract<C> {


    private static final Logger logger = Logger.getLogger(SolvableReductionSeq.class);


    /**
     * Constructor.
     */
    public SolvableReductionSeq() {
    }


    /**
     * Left Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                   GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        Map.Entry<ExpVector, C> m;
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        int i;
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new RingElem[l];
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[l];
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                //lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e; //, f;
        C a, b;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        GenSolvablePolynomial<C> Sp;
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            if (logger.isDebugEnabled()) {
                logger.debug("red, e = " + e);
            }
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = (GenSolvablePolynomial<C>) R.sum(a, e);
                //S = (GenSolvablePolynomial<C>) S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                //f = e;
                e = e.subtract(htl[i]);
                //logger.debug("red div = " + e);
                Q = p[i].multiplyLeft(e);
                b = a;
                a = a.divide(Q.leadingBaseCoefficient());
                //Q = Q.multiplyLeft(a);
                //S = (GenSolvablePolynomial<C>) S.subtract(Q);
                ExpVector g1 = S.leadingExpVector();
                Sp = S;
                S = S.subtractMultiple(a, Q);
                //S = S.subtractMultiple(a, e, p[i]);
                ExpVector g2 = S.leadingExpVector();
                if (g1.equals(g2)) {
                    logger.info("g1.equals(g2): Pp       = " + Pp);
                    logger.info("g1.equals(g2): Ap       = " + Ap);
                    logger.info("g1.equals(g2): p[i]     = " + p[i]);
                    logger.info("g1.equals(g2): Q        = " + Q);
                    logger.info("g1.equals(g2): R        = " + R);
                    logger.info("g1.equals(g2): Sp       = " + Sp);
                    logger.info("g1.equals(g2): S        = " + S);
                    throw new RuntimeException("g1.equals(g2): " + g1 + ", a = " + a + ", b = " + b);
                }
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
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C> leftNormalform(List<GenSolvablePolynomial<C>> row,
                                                   List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new RingElem[l];
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
        Map.Entry<ExpVector, C> m;
        int j = 0;
        int i;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                //lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();

        GenSolvablePolynomial<C> fac = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = (GenSolvablePolynomial<C>) R.sum(a, e);
                //S = (GenSolvablePolynomial<C>) S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                //a = a.divide( (C)lbc[i] );
                //Q = p[i].multiplyLeft( a, e );
                Q = p[i].multiplyLeft(e);
                a = a.divide(Q.leadingBaseCoefficient());
                //Q = Q.multiplyLeft(a);
                //S = (GenSolvablePolynomial<C>) S.subtract(Q);
                ExpVector g1 = S.leadingExpVector();
                S = S.subtractMultiple(a, Q);
                ExpVector g2 = S.leadingExpVector();
                if (g1.equals(g2)) {
                    throw new RuntimeException("g1.equals(g2): " + g1 + ", a = " + a + ", lc(S) = "
                            + S.leadingBaseCoefficient());
                }
                fac = row.get(i);
                if (fac == null) {
                    fac = (GenSolvablePolynomial<C>) zero.sum(a, e);
                } else { // doAddTo ?? TODO
                    fac = (GenSolvablePolynomial<C>) fac.sum(a, e);
                }
                row.set(i, fac);
            }
        }
        return R;
    }


    /**
     * Right Normalform.
     *
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return right-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings({"cast", "unchecked"})
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
        int i;
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new RingElem[l];
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                //lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();

        //GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            //logger.info("red = " + e);
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
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
                e = e.subtract(htl[i]);
                //a = a.divide( (C)lbc[i] );
                Q = p[i].multiply(e); // p_i * (a e) TODO
                a = a.divide(Q.leadingBaseCoefficient());
                Q = Q.multiply(a); // p_i * (e a) !!
                ExpVector g1 = S.leadingExpVector();
                S = (GenSolvablePolynomial<C>) S.subtract(Q);
                //S = S.subtractMultiple(Q, a);
                ExpVector g2 = S.leadingExpVector();
                if (g1.equals(g2)) {
                    throw new RuntimeException("g1.equals(g2): " + g1 + ", a = " + a + ", lc(S) = "
                            + S.leadingBaseCoefficient());
                }
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
    @SuppressWarnings({"cast", "unchecked"})
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> row,
                                                    List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l = Pp.size();
        GenSolvablePolynomial<C>[] P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
        synchronized (Pp) {
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new RingElem[l];
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
        Map.Entry<ExpVector, C> m;
        int j = 0;
        int i;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                //lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();

        GenSolvablePolynomial<C> fac = null;
        // GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = (GenSolvablePolynomial<C>) R.sum(a, e);
                //S = (GenSolvablePolynomial<C>) S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                //a = a.divide( (C)lbc[i] );
                //Q = p[i].multiply( a, e );
                Q = p[i].multiply(e); // p_i * (a e) TODO
                a = a.divide(Q.leadingBaseCoefficient());
                Q = Q.multiply(a); // p_i * (e a)
                ExpVector g1 = S.leadingExpVector();
                S = (GenSolvablePolynomial<C>) S.subtract(Q);
                //S = S.subtractMultiple(Q, a);
                ExpVector g2 = S.leadingExpVector();
                if (g1.equals(g2)) {
                    throw new RuntimeException("g1.equals(g2): " + g1 + ", a = " + a + ", lc(S) = "
                            + S.leadingBaseCoefficient());
                }
                fac = row.get(i);
                if (fac == null) {
                    fac = (GenSolvablePolynomial<C>) zero.sum(a, e);
                } else { // doAddTo ??
                    fac = (GenSolvablePolynomial<C>) fac.sum(a, e);
                }
                row.set(i, fac);
            }
        }
        return R;
    }

}
