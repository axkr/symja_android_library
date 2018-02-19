/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import edu.jas.gb.SolvableReductionAbstract;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * Polynomial pseudo reduction sequential use algorithm. Coefficients of
 * polynomials must not be from a field, i.e. the fraction free reduction is
 * implemented. Implements normalform.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvablePseudoReductionSeq<C extends GcdRingElem<C>> extends SolvableReductionAbstract<C>
        implements SolvablePseudoReduction<C> {


    private static final Logger logger = Logger.getLogger(SolvablePseudoReductionSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SolvablePseudoReductionSeq() {
    }


    /**
     * Left normalform.
     *
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
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
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new GcdRingElem[l];
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[l];
        int i;
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
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();
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
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                Q = p[i].multiplyLeft(e);
                C c = Q.leadingBaseCoefficient();
                ExpVector g = S.leadingExpVector();
                C ap = a;
                if (a.remainder(c).isZERO()) { // && !c.isConstant()) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, Q);
                } else {
                    R = R.multiplyLeft(c);
                    S = S.scaleSubtractMultiple(c, a, Q);
                }
                ExpVector h = S.leadingExpVector();
                if (g.equals(h)) { // Ore condition not fulfilled
                    logger.info("g==h: g = " + g + ", c = " + c);
                    throw new RuntimeException("g.equals(h): a = " + a + ", ap = " + ap + ", c = " + c);
                }
            }
        }
        return R;
    }


    /**
     * Left normalform recursive.
     *
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<GenPolynomial<C>> leftNormalformRecursive(
            List<GenSolvablePolynomial<GenPolynomial<C>>> Pp,
            GenSolvablePolynomial<GenPolynomial<C>> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        Map.Entry<ExpVector, GenPolynomial<C>> m;
        GenSolvablePolynomial<GenPolynomial<C>>[] P = new GenSolvablePolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        //GenPolynomial<C>[] lbc = (GenPolynomial<C>[]) new GenPolynomial[l];
        GenSolvablePolynomial<GenPolynomial<C>>[] p = new GenSolvablePolynomial[l];
        int i;
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
        ExpVector e, f;
        GenPolynomial<C> a, b;
        boolean mt = false;
        GenSolvablePolynomialRing<GenPolynomial<C>> ring = Ap.ring;
        final boolean commCoeff = ring.coFac.isCommutative();
        final SolvableSyzygyAbstract<C> ssy;
        if (commCoeff) {
            ssy = null;
        } else {
            ssy = new SolvableSyzygySeq<C>(((GenPolynomialRing<C>) ring.coFac).coFac);
        }
        GenSolvablePolynomial<GenPolynomial<C>> R = Ap.ring.getZERO().copy();
        GenSolvablePolynomial<GenPolynomial<C>> Q = null;
        GenSolvablePolynomial<GenPolynomial<C>> S = Ap.copy();
        //GenSolvablePolynomial<GenPolynomial<C>> Sp = null;
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
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                f = e.subtract(htl[i]);
                if (debug) {
                    logger.info("red div = " + f);
                    //logger.info("red a = " + a);
                }
                Q = p[i].multiplyLeft(f);
                //if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                ExpVector g = S.leadingExpVector();
                GenPolynomial<C> ap = a;
                if (commCoeff) {
                    GenPolynomial<C> c = Q.leadingBaseCoefficient();
                    if (!c.isConstant() && PolyUtil.<C>baseSparsePseudoRemainder(a, c).isZERO()) {
                        //a = a.divide(c);
                        b = PolyUtil.<C>basePseudoDivide(a, c);
                        if (a.equals(b.multiply(c))) {
                            S = S.subtractMultiple(b, Q);
                        } else {
                            R = R.multiplyLeft(c);
                            S = S.scaleSubtractMultiple(c, a, Q);
                        }
                    } else {
                        R = R.multiplyLeft(c);
                        S = S.scaleSubtractMultiple(c, a, Q);
                    }
                } else { // use Ore condition
                    GenSolvablePolynomial<C> cs = (GenSolvablePolynomial<C>) Q.leadingBaseCoefficient();
                    GenSolvablePolynomial<C> as = (GenSolvablePolynomial<C>) a;
                    GenPolynomial<C>[] ore = ssy.leftOreCond(cs, as);
                    //System.out.println("cs = " + cs + ", as = " + as);
                    //System.out.println("ore[0] = " + ore[0] + "\nore[1] = " + ore[1]);
                    R = R.multiplyLeft(ore[1]);
                    S = S.scaleSubtractMultiple(ore[1], ore[0], Q);
                }
                ExpVector h = S.leadingExpVector();
                if (g.equals(h)) { // ! Ore cond
                    logger.info("g==h: g = " + g);
                    throw new RuntimeException("g.equals(h): a = " + a + ", ap = " + ap);
                }
            }
        }
        //System.out.println("Ap = " + Ap + ", R = " + R);
        return R;
    }


    /**
     * Left normalform with recording. <b>Note:</b> Only meaningful if all
     * divisions are exact. Compute first the multiplication factor
     * <code>m</code> with <code>normalform(Pp,Ap,m)</code>, then call this
     * method with <code>normalform(row,Pp,m*Ap)</code>.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
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
        //C[] lbc = (C[]) new GcdRingElem[l];
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[l];
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
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> fac = null;
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
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
                //throw new RuntimeException("Syzygy no GB");
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                Q = p[i].multiplyLeft(e);
                C c = Q.leadingBaseCoefficient();
                ExpVector g = S.leadingExpVector();
                C ap = a;
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, Q);
                    //System.out.print("|");
                } else {
                    //System.out.print("*");
                    R = R.multiplyLeft(c);
                    S = S.scaleSubtractMultiple(c, a, Q);
                }
                ExpVector h = S.leadingExpVector();
                if (g.equals(h)) { // Ore condition not fulfilled
                    System.out.println("g = " + g + ", h = " + h);
                    System.out.println("c*ap = " + c.multiply(ap) + ", ap*c = " + ap.multiply(c));
                    throw new RuntimeException("g.equals(h): a = " + a + ", ap = " + ap + ", c = " + c);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
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


    /**
     * Left normalform with factor.
     *
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return (nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     * for Ap.
     */
    @SuppressWarnings("unchecked")
    public PseudoReductionEntry<C> leftNormalformFactor(List<GenSolvablePolynomial<C>> Pp,
                                                        GenSolvablePolynomial<C> Ap) {
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
        Map.Entry<ExpVector, C> m;
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new GcdRingElem[l]; 
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[l];
        int i;
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
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();
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
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                Q = p[i].multiplyLeft(e);
                C c = Q.leadingBaseCoefficient();
                ExpVector g = S.leadingExpVector();
                C ap = a;
                if (a.remainder(c).isZERO()) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, Q);
                } else {
                    mfac = c.multiply(mfac); // left
                    R = R.multiplyLeft(c);
                    S = S.scaleSubtractMultiple(c, a, Q);
                }
                ExpVector h = S.leadingExpVector();
                if (g.equals(h)) { // Ore condition not fulfilled
                    logger.info("g==h: g = " + g + ", c = " + c);
                    throw new RuntimeException("g==h: a = " + a + ", ap = " + ap);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.info("multiplicative factor = " + mfac);
        }
        pf = new PseudoReductionEntry<C>(R, mfac);
        return pf;
    }


    /**
     * Right normalform.
     *
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings({"unchecked"})
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> Pp,
                                                    GenSolvablePolynomial<C> Ap) {
        //throw new UnsupportedOperationException();
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
        ExpVector[] htl = new ExpVector[l];
        //C[] lbc = (C[]) new GcdRingElem[l];
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[l];
        int i;
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
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO().copy();
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
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                // need pi * a * e, but only pi * e * a or a * pi * e available
                Q = p[i].multiply(e);
                assert Q.multiply(a).equals(Q.multiplyLeft(a));
                C c = Q.leadingBaseCoefficient();
                ExpVector g = S.leadingExpVector();
                C ap = a;
                if (a.remainder(c).isZERO()) {
                    a = a.divide(c); // left?
                    //S = S.subtractMultiple(Q,a);
                    S = (GenSolvablePolynomial<C>) S.subtract(Q.multiply(a));
                } else {
                    R = R.multiply(c);
                    S = S.multiply(c);
                    //S = S.scaleSubtractMultiple(c, Q, a);
                    S = (GenSolvablePolynomial<C>) S.subtract(Q.multiply(a));
                }
                ExpVector h = S.leadingExpVector();
                if (g.equals(h)) { // Ore condition not fulfilled
                    logger.info("g==h: g = " + g + ", c = " + c);
                    throw new RuntimeException("g.equals(h): a = " + a + ", ap = " + ap);
                }
            }
        }
        //System.out.println("R = " + R);
        return R;
    }


    /**
     * Right normalform recursive.
     *
     * @param Ap recursive polynomial.
     * @param Pp recursive polynomial list.
     * @return nf(Ap) with respect to Pp. <b>Note: </b> not implemented;
     */
    public GenSolvablePolynomial<GenPolynomial<C>> rightNormalformRecursive(
            List<GenSolvablePolynomial<GenPolynomial<C>>> Pp,
            GenSolvablePolynomial<GenPolynomial<C>> Ap) {
        if (Pp == null || Ap == null) {
            throw new IllegalArgumentException("Pp or Ap == null not supported");
        }
        throw new UnsupportedOperationException(); // TODO
    }


    /**
     * Left normalform with recording. <b>Note:</b> Only meaningful if all
     * divisions are exact. Compute first the multiplication factor
     * <code>m</code> with <code>normalform(Pp,Ap,m)</code>, then call this
     * method with <code>normalform(row,Pp,m*Ap)</code>.
     *
     * @param row recording matrix, is modified.
     * @param Pp  a polynomial list for reduction.
     * @param Ap  a polynomial.
     * @return nf(Pp, Ap), the normal form of Ap wrt. Pp. <b>Note: </b> not
     * implemented;
     */
    public GenSolvablePolynomial<C> rightNormalform(List<GenSolvablePolynomial<C>> row,
                                                    List<GenSolvablePolynomial<C>> Pp, GenSolvablePolynomial<C> Ap) {
        if (row == null || Pp == null || Ap == null) {
            throw new IllegalArgumentException("row, Pp or Ap == null not supported");
        }
        throw new UnsupportedOperationException(); // TODO
    }


    /**
     * Right normalform with multiplication factor.
     *
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return (nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     * for Ap. <b>Note: </b> not implemented;
     */
    public PseudoReductionEntry<C> rightNormalformFactor(List<GenSolvablePolynomial<C>> Pp,
                                                         GenSolvablePolynomial<C> Ap) {
        if (Pp == null || Ap == null) {
            throw new IllegalArgumentException("Pp or Ap == null not supported");
        }
        throw new UnsupportedOperationException(); // TODO
    }

}
