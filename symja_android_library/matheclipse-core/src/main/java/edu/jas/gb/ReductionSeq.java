/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial reduction sequential use algorithm. Implements normalform.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ReductionSeq<C extends RingElem<C>> // should be FieldElem<C>>
        extends ReductionAbstract<C> {


    private static final Logger logger = Logger.getLogger(ReductionSeq.class);


    /**
     * Constructor.
     */
    public ReductionSeq() {
    }


    /**
     * Normalform.
     *
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
        if (!Ap.ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        Map.Entry<ExpVector, C> m;
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        Object[] lbc = new Object[l]; // want C[]
        GenPolynomial<C>[] p = new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO().copy();

        //GenPolynomial<C> T = null;
        //GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap.copy();
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
                logger.debug("irred");
                //R = R.sum( a, e );
                //S = S.subtract( a, e ); 
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                a = a.divide((C) lbc[i]);
                //logger.info("red div: e = " + e + ", a = " + a);
                //Q = p[i].multiply( a, e );
                //S = S.subtract( Q );
                S = S.subtractMultiple(a, e, p[i]);
            }
        }
        return R;
    }


    /**
     * Normalform with respect to marked head terms.
     *
     * @param Mp leading monomial list.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return nf(Ap) with respect to Mp+Pp.
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalformMarked(List<Monomial<C>> Mp, List<GenPolynomial<C>> Pp,
                                             GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Mp == null || Mp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        Map.Entry<ExpVector, C> m;
        int l;
        GenPolynomial<C>[] P;
        Monomial<C>[] M;
        synchronized (Pp) {
            l = Pp.size();
            if (Mp.size() != l) {
                throw new IllegalArgumentException("#Mp != #Pp: " + l + ", " + Mp.size());
            }
            P = new GenPolynomial[l];
            M = new Monomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
                M[i] = Mp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        RingElem[] lbc = new RingElem[l];
        GenPolynomial<C>[] p = new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            if (M[i] != null) {
                p[j] = p[i];
                htl[j] = M[i].exponent();
                lbc[j] = M[i].coefficient();
                j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a, b;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO().copy();
        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            //System.out.println("NF a = " + a + ", e = " + e);
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                logger.debug("irred");
                R.doAddTo(a, e); // needed, or sum
                //R.doPutToMap(e, a); // not here
                //S = S.subtract( a, e ); 
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                //System.out.println("i = "+i+", htl[i] = " + Ap.ring.toScript(htl[i]) + ", lbc[i] = " + lbc[i]  + ", p[i] = " + p[i].ring.toScript(p[i].leadingExpVector()));
                f = e.subtract(htl[i]);
                b = a.divide((C) lbc[i]);
                //logger.info("red div: e = " + e + ", a = " + a + ", f = " + f + ", b = " + b);
                //Q = p[i].multiply( a, e );
                //S = S.subtract( Q );
                S.doRemoveFromMap(e, a);
                //S.doAddTo(a.negate(), e);
                S = S.subtractMultiple(b, f, p[i]);
                if (e.equals(S.leadingExpVector())) {
                    throw new RuntimeException(
                            "something is wrong: ht not descending e = " + e + ", S = " + S);
                }
                //System.out.println("NF R = " + R.leadingExpVector() + ", S = " + S.leadingExpVector() + ", e = " + e + ", f = " + f + ", #S = " + S.length());
            }
            //System.out.println("NF R = " + R + ", S = " + S);
        }
        //System.out.println("NF Ap = " + Ap + " ==> " + R);
        return R;
    }


    /**
     * Normalform with recording.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp,
                                       GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        int l = Pp.size();
        GenPolynomial<C>[] P = new GenPolynomial[l];
        synchronized (Pp) {
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        Object[] lbc = new Object[l]; // want C[]
        GenPolynomial<C>[] p = new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
        int j = 0;
        int i;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO().copy();

        GenPolynomial<C> fac = null;
        // GenPolynomial<C> T = null;
        //GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap.copy();
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
                //R = R.sum( a, e );
                //S = S.subtract( a, e ); 
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                C c = (C) lbc[i];
                a = a.divide(c);
                //Q = p[i].multiply( a, e );
                //S = S.subtract( Q );
                S = S.subtractMultiple(a, e, p[i]);
                fac = row.get(i);
                if (fac == null) {
                    fac = zero.sum(a, e);
                } else {
                    fac = fac.sum(a, e);
                }
                row.set(i, fac);
            }
        }
        return R;
    }

}
