/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.gb.ReductionAbstract;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction sequential use algorithm. Coefficients of
 * polynomials must not be from a field, i.e. the fraction free reduction is
 * implemented. Implements normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class PseudoReductionPar<C extends RingElem<C>> extends ReductionAbstract<C> implements
                PseudoReduction<C> {


    private static final Logger logger = Logger.getLogger(PseudoReductionPar.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public PseudoReductionPar() {
    }


    /**
     * Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        GenPolynomial<C>[] P = new GenPolynomial[0];
        List<GenPolynomial<C>> Ppp;
        synchronized (Pp) {
            Ppp = new ArrayList<GenPolynomial<C>>(Pp); // sic
        }
        P = Ppp.toArray(P);
        int ll = Ppp.size();
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz.copy();

        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            if (Pp.size() != ll) {
                //System.out.println("Pp.size() = " + Pp.size() + ", ll = " + ll);
                //long t = System.currentTimeMillis();
                synchronized (Pp) {
                    Ppp = new ArrayList<GenPolynomial<C>>(Pp); // sic
                }
                P = Ppp.toArray(P);
                ll = Ppp.size();
                //ll = P.length; // wrong
                //t = System.currentTimeMillis()-t;
                //logger.info("Pp.toArray(): size() = " + l + ", ll = " + ll);
                S = Ap.copy(); // S.add(R)? // restart reduction ?
                R = Rz.copy();
            }
            boolean mt = false;
            Map.Entry<ExpVector, C> m = S.leadingMonomial();
            ExpVector e = m.getKey();
            C a = m.getValue();
            ExpVector f = null;
            int i;
            for (i = 0; i < ll; i++) {
                f = P[i].leadingExpVector();
                mt = e.multipleOf(f);
                if (mt)
                    break;
            }
            if (!mt) {
                //System.out.println("m = " + m);
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(f);
                //logger.info("red div = " + e);
                C c = P[i].leadingBaseCoefficient();
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, e, P[i]);
                } else {
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, e, P[i]);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
            }
        }
        return R;
    }


    /**
     * Normalform.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
     */
    @SuppressWarnings("unchecked")
    public PseudoReductionEntry<C> normalformFactor(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Ap == null) {
            return null;
        }
        C mfac = Ap.ring.getONECoefficient();
        PseudoReductionEntry<C> pf = new PseudoReductionEntry<C>(Ap, mfac);
        if (Pp == null || Pp.isEmpty()) {
            return pf;
        }
        if (Ap.isZERO()) {
            return pf;
        }
        GenPolynomial<C>[] P = new GenPolynomial[0];
        List<GenPolynomial<C>> Ppp;
        synchronized (Pp) {
            Ppp = new ArrayList<GenPolynomial<C>>(Pp); // sic
        }
        P = Ppp.toArray(P);
        int l = Ppp.size();
        boolean mt = false;
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz.copy();
        //GenPolynomial<C> T = null;
        //GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            if (Pp.size() != l) {
                //long t = System.currentTimeMillis();
                synchronized (Pp) {
                    Ppp = new ArrayList<GenPolynomial<C>>(Pp);
                }
                P = Ppp.toArray(P);
                l = Ppp.size();
                //t = System.currentTimeMillis()-t;
                //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                S = Ap.copy(); // S.add(R)? // restart reduction ?
                R = Rz.copy();
            }
            Map.Entry<ExpVector, C> m = S.leadingMonomial();
            ExpVector e = m.getKey();
            C a = m.getValue();
            ExpVector f = null;
            int i;
            for (i = 0; i < l; i++) {
                f = P[i].leadingExpVector();
                mt = e.multipleOf(f);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(f);
                //logger.info("red div = " + e);
                C c = P[i].leadingBaseCoefficient();
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, e, P[i]);
                } else {
                    mfac = mfac.multiply(c);
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, e, P[i]);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("multiplicative factor = " + mfac);
        }
        pf = new PseudoReductionEntry<C>(R, mfac);
        return pf;
    }


    /**
     * Normalform with recording. <b>Note:</b> Only meaningful if all divisions
     * are exact. Compute first the multiplication factor <code>m</code> with
     * <code>normalform(Pp,Ap,m)</code>, then call this method with
     * <code>normalform(row,Pp,m*Ap)</code>.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp,
                    GenPolynomial<C> Ap) {
        throw new RuntimeException("normalform with recording not implemented");
    }


    /**
     * Normalform recursive.
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<GenPolynomial<C>> normalformRecursive(List<GenPolynomial<GenPolynomial<C>>> Pp, GenPolynomial<GenPolynomial<C>> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        GenPolynomial<GenPolynomial<C>>[] P = new GenPolynomial[0];
        List<GenPolynomial<GenPolynomial<C>>> Ppp;
        synchronized (Pp) {
            Ppp = new ArrayList<GenPolynomial<GenPolynomial<C>>>(Pp); // sic
        }
        P = Ppp.toArray(P);
        int ll = Ppp.size();
        GenPolynomial<GenPolynomial<C>> Rz = Ap.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> R = Rz.copy();

        GenPolynomial<GenPolynomial<C>> S = Ap.copy();
        while (S.length() > 0) {
            if (Pp.size() != ll) {
                //System.out.println("Pp.size() = " + Pp.size() + ", ll = " + ll);
                //long t = System.currentTimeMillis();
                synchronized (Pp) {
                    Ppp = new ArrayList<GenPolynomial<GenPolynomial<C>>>(Pp); // sic
                }
                P = Ppp.toArray(P);
                ll = Ppp.size();
                //ll = P.length; // wrong
                //t = System.currentTimeMillis()-t;
                //logger.info("Pp.toArray(): size() = " + l + ", ll = " + ll);
                S = Ap.copy(); // S.add(R)? // restart reduction ?
                R = Rz.copy();
            }
            boolean mt = false;
            Map.Entry<ExpVector, GenPolynomial<C>> m = S.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> a = m.getValue();
            ExpVector f = null;
            int i;
            for (i = 0; i < ll; i++) {
                f = P[i].leadingExpVector();
                mt = e.multipleOf(f);
                if (mt)
                    break;
            }
            if (!mt) {
                //System.out.println("m = " + m);
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                f = e.subtract(f);
                if (debug) {
                    logger.info("red div = " + e);
                }
                GenPolynomial<C> c = P[i].leadingBaseCoefficient();
                //if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                if (PolyUtil.<C> baseSparsePseudoRemainder(a,c).isZERO()) { 
                    if (debug) {
                        logger.info("red c = " + c);
                    }
                    //a = a.divide(c);
                    GenPolynomial<C> b = PolyUtil.<C> basePseudoDivide(a,c);
                    GenPolynomial<GenPolynomial<C>> Sp = S.subtractMultiple(b, f, P[i]);
                    if (e.equals(Sp.leadingExpVector())) { // TODO: avoid
                        //throw new RuntimeException("degree not descending");
                        logger.info("degree not descending: S = " + S + ", Sp = " + Sp);
                        R = R.multiply(c);
                        //S = S.multiply(c);
                        Sp = S.scaleSubtractMultiple(c, a, f, P[i]);
                    }
                    S = Sp; 
                } else {
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, f, P[i]);
                }
                //Q = p[i].multiply(a, f);
                //S = S.subtract(Q);
            }
        }
        return R;
    }

}
