/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Polynomial E-Reduction sequential use algorithm. Implements normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class EReductionSeq<C extends RingElem<C>> extends DReductionSeq<C> implements EReduction<C> {


    private static final Logger logger = LogManager.getLogger(DReductionSeq.class);


    /**
     * Constructor.
     */
    public EReductionSeq() {
    }


    /**
     * Is top reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    @Override
    public boolean isTopReducible(List<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        C a = A.leadingBaseCoefficient();
        for (GenPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingExpVector());
            if (mt) {
                C b = p.leadingBaseCoefficient();
                C r = a.remainder(b);
                mt = !r.equals(a);
                if (mt) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isNormalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        if (Ap == null || Ap.isZERO()) {
            return true;
        }
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
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
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
        boolean mt = false;
        Map<ExpVector, C> Am = Ap.getMap();
        for (Map.Entry<ExpVector, C> me : Am.entrySet()) {
            ExpVector e = me.getKey();
            C a = me.getValue(); //Am.get(e);
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    C r = a.remainder(lbc[i]);
                    mt = !r.equals(a);
                    if (mt) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Normalform using e-reduction.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return e-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i); //.abs();
            }
        }
        Map.Entry<ExpVector, C> m;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
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
        ExpVector e = null;
        ExpVector f = null;
        C a = null;
        C b = null;
        C r = null;
        GenPolynomialRing<C> pfac = Ap.ring;
        GenPolynomial<C> R = pfac.getZERO();
        //GenPolynomial<C> T = pfac.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        //try { // required to avoid a compiler error in the while loop
        while (S.length() > 0) {
            boolean mt = false;
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    f = e.subtract(htl[i]);
                    //logger.info("red div = {}", f);
                    r = a.remainder(lbc[i]);
                    b = a.divide(lbc[i]);
                    if (f == null) { // compiler produced this case ??? still ?
                        System.out.println("f = null: " + e + ", " + htl[i]);
                        Q = p[i].multiply(b);
                    } else {
                        Q = p[i].multiply(b, f);
                    }
                    S = S.subtract(Q); // ok also with reductum
                    //System.out.println(" r = " + r);
                    a = r;
                    if (r.isZERO()) {
                        break;
                    }
                }
            }
            if (!a.isZERO()) { //! mt ) {
                //logger.debug("irred");
                R = R.sum(a, e);
                //S = S.subtract( a, e ); 
                S = S.reductum();
            }
            //System.out.println(" R = " + R);
            //System.out.println(" S = " + S);
        }
        return R; //.abs(); not ok with e-reduction
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp,
                    GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i); //.abs();
            }
        }
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
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
        ExpVector e = null;
        ExpVector f = null;
        C a = null;
        C b = null;
        C r = null;
        GenPolynomialRing<C> pfac = Ap.ring;
        GenPolynomial<C> R = pfac.getZERO();
        GenPolynomial<C> zero = pfac.getZERO();
        //System.out.println("row_in = " + row);

        GenPolynomial<C> fac = null;
        // GenPolynomial<C> T = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while (S.length() > 0) {
            boolean mt = false;
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            //System.out.println("e = " + e);
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    f = e.subtract(htl[i]);
                    //logger.info("red div = {}", f);
                    r = a.remainder(lbc[i]);
                    b = a.divide(lbc[i]);
                    //if (f == null) { // compiler produced this case ??? still ?
                    //    System.out.println("f = null: " + e + ", " + htl[i]);
                    //    f = pfac.evzero;
                    //}
                    Q = p[i].multiply(b, f);
                    S = S.subtract(Q);
                    fac = row.get(i);
                    if (fac == null) {
                        fac = zero.sum(b, f);
                    } else {
                        fac = fac.sum(b, f);
                    }
                    row.set(i, fac);
                    //System.out.println("i = " + i);
                    //System.out.println("r = " + r);
                    a = r;
                    if (r.isZERO()) {
                        break;
                    }
                }
            }
            if (!a.isZERO()) { //! mt ) {
                //logger.debug("irred");
                R = R.sum(a, e);
                S = S.subtract(a, e);
                //??S = S.reductum();
            }
        }
        return R;
    }


    /**
     * Irreducible set.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    @Override
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        if (Pp == null) {
            return null;
        }
        for (GenPolynomial<C> a : Pp) {
            if (!a.isZERO()) {
                P.add(a);
            }
        }
        int l = P.size();
        if (l <= 1)
            return P;

        int irr = 0;
        ExpVector e;
        ExpVector f;
        C c;
        C d;
        GenPolynomial<C> a;
        //Iterator<GenPolynomial<C>> it;
        logger.debug("irr = ");
        while (irr != l) {
            //it = P.listIterator(); 
            //a = P.get(0); //it.next();
            a = P.remove(0);
            e = a.leadingExpVector();
            c = a.leadingBaseCoefficient();
            a = normalform(P, a);
            logger.debug(String.valueOf(irr));
            if (a.isZERO()) {
                l--;
                if (l <= 1) {
                    return P;
                }
            } else {
                f = a.leadingExpVector();
                d = a.leadingBaseCoefficient();
                if (e.equals(f) && c.equals(d)) {
                    irr++;
                } else {
                    irr = 0;
                }
                P.add(a);
            }
        }
        //System.out.println();
        return P;
    }


    // inherit is okay:
    //public boolean isReductionNF(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap,
    //                 GenPolynomial<C> Np) {

}
