/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.Word;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.OverlapList;
import edu.jas.poly.Overlap;
import edu.jas.structure.RingElem;


/**
 * Polynomial word reduction abstract class. Implements common S-Polynomial,
 * normalform, module criterion and irreducible set.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class WordReductionAbstract<C extends RingElem<C>> implements WordReduction<C> {


    private static final Logger logger = Logger.getLogger(WordReductionAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public WordReductionAbstract() {
    }


    /**
     * S-Polynomials of non-commutative polynomials.
     * @param Ap word polynomial.
     * @param Bp word polynomial.
     * @return list of all spol(Ap,Bp) the S-polynomials of Ap and Bp.
     */
    public List<GenWordPolynomial<C>> SPolynomials(GenWordPolynomial<C> Ap, 
                                                   GenWordPolynomial<C> Bp) {
	List<GenWordPolynomial<C>> sp = new ArrayList<GenWordPolynomial<C>>();
        Map.Entry<Word, C> ma = Ap.leadingMonomial();
        Map.Entry<Word, C> mb = Bp.leadingMonomial();
        Word e = ma.getKey();
        Word f = mb.getKey();
        C a = ma.getValue();
        C b = mb.getValue();
        OverlapList oll = e.overlap(f); 
        if ( oll.ols.isEmpty() ) {
            return sp;
        }
        for ( Overlap ol : oll.ols ) {
	    GenWordPolynomial<C> s = SPolynomial(ol, b, Ap, a, Bp);
            sp.add(s);
        }
        return sp;
    }


    /**
     * S-Polynomials of non-commutative polynomials.
     * @param a leading base coefficient of B.
     * @param l1 word.
     * @param A word polynomial.
     * @param r1 word.
     * @param b leading base coefficient of A.
     * @param l2 word.
     * @param B word polynomial.
     * @param r2 word.
     * @return list of all spol(Ap,Bp) the S-polynomials of Ap and Bp.
     */
    public GenWordPolynomial<C> SPolynomial(C a, Word l1, GenWordPolynomial<C> A, Word r1,
                                            C b, Word l2, GenWordPolynomial<C> B, Word r2) {
        C one = A.ring.coFac.getONE();
	GenWordPolynomial<C> s1 = A.multiply(a,l1,one,r1);
	GenWordPolynomial<C> s2 = B.multiply(b,l2,one,r2);
	GenWordPolynomial<C> s = s1.subtract(s2);
        return s;
    }


    /**
     * S-Polynomials of non-commutative polynomials.
     * @param ol Overlap tuple.
     * @param a leading base coefficient of B.
     * @param A word polynomial.
     * @param b leading base coefficient of A.
     * @param B word polynomial.
     * @return list of all spol(Ap,Bp) the S-polynomials of Ap and Bp.
     */
    public GenWordPolynomial<C> SPolynomial(Overlap ol, C a, GenWordPolynomial<C> A,
                                                        C b, GenWordPolynomial<C> B) {
        C one = A.ring.coFac.getONE();
	GenWordPolynomial<C> s1 = A.multiply(a,ol.l1,one,ol.r1);
	GenWordPolynomial<C> s2 = B.multiply(b,ol.l2,one,ol.r2);
	GenWordPolynomial<C> s = s1.subtract(s2);
        return s;
    }


    /**
     * Normalform Set.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenWordPolynomial<C>> normalform(List<GenWordPolynomial<C>> Pp, List<GenWordPolynomial<C>> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isEmpty()) {
            return Ap;
        }
        ArrayList<GenWordPolynomial<C>> red = new ArrayList<GenWordPolynomial<C>>();
        for (GenWordPolynomial<C> A : Ap) {
            A = normalform(Pp, A);
            red.add(A);
        }
        return red;
    }


    /**
     * Is top reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<GenWordPolynomial<C>> P, GenWordPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        Word e = A.leadingWord();
        for (GenWordPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingWord());
            if (mt) {
                return true;
            }
        }
        return false;
    }


    /**
     * Is reducible.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is reducible with respect to Pp.
     */
    public boolean isReducible(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        return !isNormalform(Pp, Ap);
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public boolean isNormalform(List<GenWordPolynomial<C>> Pp, GenWordPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        if (Ap == null || Ap.isZERO()) {
            return true;
        }
        int l;
        GenWordPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenWordPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        Word[] htl = new Word[l];
        GenWordPolynomial<C>[] p = new GenWordPolynomial[l];
        Map.Entry<Word, C> m;
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                j++;
            }
        }
        l = j;
        boolean mt = false;
        for (Word e : Ap.getMap().keySet()) {
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Is in Normalform.
     * @param Pp polynomial list.
     * @return true if each Ap in Pp is in normalform with respect to Pp\{Ap}.
     */
    public boolean isNormalform(List<GenWordPolynomial<C>> Pp) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        GenWordPolynomial<C> Ap;
        List<GenWordPolynomial<C>> P = new LinkedList<GenWordPolynomial<C>>(Pp);
        int s = P.size();
        for (int i = 0; i < s; i++) {
            Ap = P.remove(i);
            if (!isNormalform(P, Ap)) {
                return false;
            }
            P.add(Ap);
        }
        return true;
    }


    /**
     * Irreducible set.
     * @param Pp polynomial list.
     * @return a list P of monic polynomials which are in normalform wrt. P and
     *         with ideal(Pp) = ideal(P).
     */
    public List<GenWordPolynomial<C>> irreducibleSet(List<GenWordPolynomial<C>> Pp) {
        ArrayList<GenWordPolynomial<C>> P = new ArrayList<GenWordPolynomial<C>>();
        for (GenWordPolynomial<C> a : Pp) {
            if (a.length() != 0) {
                a = a.monic();
                if (a.isONE()) {
                    P.clear();
                    P.add(a);
                    return P;
                }
                P.add(a);
            }
        }
        int l = P.size();
        if (l <= 1)
            return P;

        int irr = 0;
        Word e;
        Word f;
        GenWordPolynomial<C> a;
        logger.debug("irr = ");
        while (irr != l) {
            //it = P.listIterator(); 
            //a = P.get(0); //it.next();
            a = P.remove(0);
            e = a.leadingWord();
            a = normalform(P, a);
            logger.debug(String.valueOf(irr));
            if (a.length() == 0) {
                l--;
                if (l <= 1) {
                    return P;
                }
            } else {
                f = a.leadingWord();
                if (f.signum() == 0) {
                    P = new ArrayList<GenWordPolynomial<C>>();
                    P.add(a.monic());
                    return P;
                }
                if (e.equals(f)) {
                    irr++;
                } else {
                    irr = 0;
                    a = a.monic();
                }
                P.add(a);
            }
        }
        //System.out.println();
        return P;
    }


    /**
     * Is reduction of normal form.
     * @param lrow left recording matrix.
     * @param rrow right recording matrix.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @param Np nf(Pp,Ap), a normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */
    public boolean isReductionNF(List<GenWordPolynomial<C>> lrow, List<GenWordPolynomial<C>> rrow, 
                                 List<GenWordPolynomial<C>> Pp, 
                                 GenWordPolynomial<C> Ap, GenWordPolynomial<C> Np) {
        if (lrow == null && rrow == null && Pp == null) {
            if (Ap == null) {
                return (Np == null);
            }
            return Ap.equals(Np);
        }
        if (lrow == null || rrow == null || Pp == null) {
            return false;
        }
        if (lrow.size() != Pp.size() || rrow.size() != Pp.size()) {
            return false;
        }
        GenWordPolynomial<C> t = Np;
        //System.out.println("t0 = " + t );
        GenWordPolynomial<C> r, rl, rr;
        GenWordPolynomial<C> p;
        for (int m = 0; m < Pp.size(); m++) {
            rl = lrow.get(m);
            rr = rrow.get(m);
            p = Pp.get(m);
            if (rl != null && rr != null && p != null) {
                if (t == null) {
                    t = p.multiply(rl,rr);
                } else {
                    t = t.sum(p.multiply(rl,rr));
                }
            }
            //System.out.println("r = " + r );
            //System.out.println("p = " + p );
        }
        //System.out.println("t+ = " + t );
        if (t == null) {
            if (Ap == null) {
                return true;
            }
            return Ap.isZERO();
        }
        r = t.subtract(Ap);
        boolean z = r.isZERO();
        if (!z) {
            logger.info("t = " + t);
            logger.info("a = " + Ap);
            logger.info("t-a = " + r);
        }
        return z;
    }
}
