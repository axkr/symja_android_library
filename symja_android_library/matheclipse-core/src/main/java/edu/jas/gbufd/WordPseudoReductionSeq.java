/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import edu.jas.gb.WordReductionAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.Word;
import edu.jas.structure.RingElem;


/**
 * Polynomial word reduction sequential use algorithm. Implements normalform.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordPseudoReductionSeq<C extends RingElem<C>> extends WordReductionAbstract<C> implements
        WordPseudoReduction<C> {


    private static final Logger logger = Logger.getLogger(WordPseudoReductionSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public WordPseudoReductionSeq() {
    }


    /**
     * Normalform.
     *
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    //@SuppressWarnings("unchecked")
    public GenWordPolynomial<C> normalform(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficient ring not commutative");
        }
        Map.Entry<Word, C> m;
        GenWordPolynomial<C>[] P = new GenWordPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        Word[] htl = new Word[l];
        C[] lbc = (C[]) new RingElem[l];
        GenWordPolynomial<C>[] p = new GenWordPolynomial[l];
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
        Word e;
        C a;
        boolean mt = false;
        GenWordPolynomial<C> R = Ap.ring.getZERO().copy();
        C cone = Ap.ring.coFac.getONE();
        GenWordPolynomial<C> Q = null;
        GenWordPolynomial<C> S = Ap.copy();
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
            } else {
                Word[] elr = e.divideWord(htl[i]);
                e = elr[0];
                Word f = elr[1];
                if (debug) {
                    logger.info("red divideWord: e = " + e + ", f = " + f);
                }
                C c = lbc[i];
                if (a.remainder(c).isZERO()) {
                    a = a.divide(c);
                    Q = p[i].multiply(a, e, cone, f);
                } else {
                    R = R.multiply(c);
                    S = S.multiply(c);
                    Q = p[i].multiply(a, e, cone, f);
                }
                S = S.subtract(Q);
            }
        }
        return R;
    }


    /**
     * Normalform with left and right recording.
     *
     * @param lrow left recording matrix, is modified.
     * @param rrow right recording matrix, is modified.
     * @param Pp   a polynomial list for reduction.
     * @param Ap   a polynomial.
     * @return nf(Pp, Ap), the normal form of Ap wrt. Pp.
     */
    //@SuppressWarnings("unchecked")
    public GenWordPolynomial<C> normalform(List<GenWordPolynomial<C>> lrow, List<GenWordPolynomial<C>> rrow,
                                           List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficient ring not commutative");
        }
        GenWordPolynomial<C>[] P = new GenWordPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        Word[] htl = new Word[l];
        C[] lbc = (C[]) new RingElem[l];
        GenWordPolynomial<C>[] p = new GenWordPolynomial[l];
        Map.Entry<Word, C> m;
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
        Word e;
        C a;
        boolean mt = false;
        GenWordPolynomial<C> zero = Ap.ring.getZERO().copy();
        GenWordPolynomial<C> R = Ap.ring.getZERO();
        C cone = Ap.ring.coFac.getONE();
        GenWordPolynomial<C> fac = null;
        GenWordPolynomial<C> Q = null;
        GenWordPolynomial<C> S = Ap.copy();
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
            } else {
                Word[] elr = e.divideWord(htl[i]);
                e = elr[0];
                Word f = elr[1];
                if (debug) {
                    logger.info("redRec divideWord: e = " + e + ", f = " + f);
                }
                C c = lbc[i];
                if (a.remainder(c).isZERO()) {
                    a = a.divide(c);
                    Q = p[i].multiply(a, e, cone, f);
                } else {
                    R = R.multiply(c);
                    S = S.multiply(c);
                    Q = p[i].multiply(a, e, cone, f);
                }
                S = S.subtract(Q);
                // left row
                fac = lrow.get(i);
                if (fac == null) {
                    fac = zero.sum(cone, e);
                } else {
                    fac = fac.sum(cone, e);
                }
                lrow.set(i, fac);
                // right row
                fac = rrow.get(i);
                if (fac == null) {
                    fac = zero.sum(a, f);
                } else {
                    fac = fac.sum(a, f);
                }
                rrow.set(i, fac);
            }
        }
        return R;
    }


    /**
     * Normalform with multiplication factor.
     *
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return (nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     * for Ap.
     */
    public WordPseudoReductionEntry<C> normalformFactor(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        throw new UnsupportedOperationException("normalformFactor not imlemented");
    }


    /**
     * Normalform with polynomial coefficients.
     *
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    //@SuppressWarnings("unchecked")
    public GenWordPolynomial<GenPolynomial<C>> normalformRecursive(
            List<GenWordPolynomial<GenPolynomial<C>>> Pp, GenWordPolynomial<GenPolynomial<C>> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficient ring not commutative");
        }
        Map.Entry<Word, GenPolynomial<C>> m;
        GenWordPolynomial<GenPolynomial<C>>[] P = new GenWordPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        Word[] htl = new Word[l];
        GenPolynomial<C>[] lbc = new GenPolynomial[l]; //(GenPolynomial<C>[]) 
        GenWordPolynomial<GenPolynomial<C>>[] p = new GenWordPolynomial[l];
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
        Word e, fr, fl;
        GenPolynomial<C> a, b = null;
        boolean mt = false;
        GenWordPolynomial<GenPolynomial<C>> R = Ap.ring.getZERO().copy();
        GenPolynomial<C> cone = Ap.ring.coFac.getONE();
        GenWordPolynomial<GenPolynomial<C>> Q = null;
        GenWordPolynomial<GenPolynomial<C>> S = Ap.copy();
        GenWordPolynomial<GenPolynomial<C>> Sp;
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
            } else {
                Word[] elr = e.divideWord(htl[i]);
                fl = elr[0];
                fr = elr[1];
                if (debug) {
                    logger.info("redRec divideWord: e = " + e + ", fl = " + fl + ", fr = " + fr);
                }
                GenPolynomial<C> c = lbc[i];
                if (PolyUtil.<C>baseSparsePseudoRemainder(a, c).isZERO()) {
                    b = PolyUtil.<C>basePseudoDivide(a, c);
                    Q = p[i].multiply(b, fl, cone, fr);
                } else {
                    R = R.multiply(c);
                    S = S.multiply(c);
                    Q = p[i].multiply(a, fl, cone, fr);
                }
                Sp = S.subtract(Q);
                if (e.equals(Sp.leadingWord())) { // TODO: avoid not possible in general
                    //logger.info("redRec: e = " + e + ", hti = " + htl[i] + ", fl = " + fl + ", fr = " + fr);
                    //logger.info("redRec: S = " + S + ", Sp = " + Sp + ", a = " + a + ", b = " + b + ", c = " + c);
                    //throw new RuntimeException("degree not descending");
                    R = R.multiply(c);
                    S = S.multiply(c);
                    Q = p[i].multiply(a, fl, cone, fr);
                    Sp = S.subtract(Q);
                }
                S = Sp;
            }
        }
        return R;
    }


    @Override
    public GenWordPolynomial<C> leftNormalform(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        throw new UnsupportedOperationException("leftNormalform not imlemented");
    }


    @Override
    public GenWordPolynomial<C> leftNormalform(List<GenWordPolynomial<C>> lrow,
                                               List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        throw new UnsupportedOperationException("leftNormalform not imlemented");
    }


    @Override
    public GenWordPolynomial<C> rightNormalform(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        throw new UnsupportedOperationException("rightNormalform not imlemented");
    }


    @Override
    public GenWordPolynomial<C> rightNormalform(List<GenWordPolynomial<C>> rrow,
                                                List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        throw new UnsupportedOperationException("rightNormalform not imlemented");
    }

}
