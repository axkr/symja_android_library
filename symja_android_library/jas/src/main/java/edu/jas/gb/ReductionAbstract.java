/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial Reduction abstract class. Implements common S-Polynomial,
 * normalform, criterion 4 module criterion and irreducible set.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class ReductionAbstract<C extends RingElem<C>> implements Reduction<C> {


    private static final Logger logger = Logger.getLogger(ReductionAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public ReductionAbstract() {
    }


    /**
     * S-Polynomial.
     * @param A polynomial.
     * @param B polynomial.
     * @return spol(A,B) the S-polynomial of A and B.
     */
    public GenPolynomial<C> SPolynomial(GenPolynomial<C> A, GenPolynomial<C> B) {
        if (B == null || B.isZERO()) {
            if (A == null) {
                return B;
            }
            return A.ring.getZERO();
        }
        if (A == null || A.isZERO()) {
            return B.ring.getZERO();
        }
        if (debug) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
        }
        Map.Entry<ExpVector, C> ma = A.leadingMonomial();
        Map.Entry<ExpVector, C> mb = B.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        //GenPolynomial<C> App = A.multiply(b, e1);
        //GenPolynomial<C> Bpp = B.multiply(a, f1);
        //GenPolynomial<C> Cp = App.subtract(Bpp);
        GenPolynomial<C> Cp = A.scaleSubtractMultiple(b, e1, a, f1, B);
        return Cp;
    }


    /**
     * S-Polynomial with recording.
     * @param S recording matrix, is modified. <b>Note</b> the negative
     *            S-polynomial is recorded as required by all applications.
     * @param i index of Ap in basis list.
     * @param A a polynomial.
     * @param j index of Bp in basis list.
     * @param B a polynomial.
     * @return Spol(A, B), the S-Polynomial for A and B.
     */
    public GenPolynomial<C> SPolynomial(List<GenPolynomial<C>> S, int i, GenPolynomial<C> A, int j,
                    GenPolynomial<C> B) {
        if (debug) {
            if (B == null || B.isZERO()) {
                throw new ArithmeticException("Spol B is zero");
            }
            if (A == null || A.isZERO()) {
                throw new ArithmeticException("Spol A is zero");
            }
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
        }
        Map.Entry<ExpVector, C> ma = A.leadingMonomial();
        Map.Entry<ExpVector, C> mb = B.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        //GenPolynomial<C> App = A.multiply(b, e1);
        //GenPolynomial<C> Bpp = B.multiply(a, f1);
        //GenPolynomial<C> Cp = App.subtract(Bpp);
        GenPolynomial<C> Cp = A.scaleSubtractMultiple(b, e1, a, f1, B);

        GenPolynomial<C> zero = A.ring.getZERO();
        GenPolynomial<C> As = zero.sum(b.negate(), e1);
        GenPolynomial<C> Bs = zero.sum(a /*correct .negate()*/, f1);
        S.set(i, As);
        S.set(j, Bs);
        return Cp;
    }


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, GenPolynomial<C> A, GenPolynomial<C> B) {
        if (modv == 0) {
            return true;
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        return moduleCriterion(modv, ei, ej);
    }


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param ei ExpVector.
     * @param ej ExpVector.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, ExpVector ei, ExpVector ej) {
        if (modv == 0) {
            return true;
        }
        if (ei.invLexCompareTo(ej, 0, modv) != 0) {
            return false; // skip pair
        }
        return true;
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B, ExpVector e) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
            if (!A.ring.isCommutative()) { //B instanceof GenSolvablePolynomial ) {
                logger.error("GBCriterion4 not applicabable to non-commutative polynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        return criterion4(ei, ej, e);
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings.
     * @param ei exponent vector.
     * @param ej exponent vector.
     * @param e = lcm(ei,ej)
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(ExpVector ei, ExpVector ej, ExpVector e) {
        ExpVector g = ei.sum(ej);
        ExpVector h = g.subtract(e);
        int s = h.signum();
        return s != 0;
    }


    /**
     * GB criterium 4.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.isCommutative() || !B.ring.isCommutative()) { // A instanceof GenSolvablePolynomial
                logger.error("GBCriterion4 not applicabable to non-commutative polynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector e = ei.lcm(ej);
        return criterion4(ei, ej, e);
    }


    /**
     * Normalform Set.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> Pp, List<GenPolynomial<C>> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isEmpty()) {
            return Ap;
        }
        ArrayList<GenPolynomial<C>> red = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> A : Ap) {
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
    public boolean isTopReducible(List<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        for (GenPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingExpVector());
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
    public boolean isReducible(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        return !isNormalform(Pp, Ap);
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked")
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
                j++;
            }
        }
        l = j;
        boolean mt = false;
        for (ExpVector e : Ap.getMap().keySet()) {
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
    public boolean isNormalform(List<GenPolynomial<C>> Pp) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        GenPolynomial<C> Ap;
        List<GenPolynomial<C>> P = new LinkedList<GenPolynomial<C>>(Pp);
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
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> a : Pp) {
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
        ExpVector e;
        ExpVector f;
        GenPolynomial<C> a;
        logger.debug("irr = ");
        while (irr != l) {
            //it = P.listIterator(); 
            //a = P.get(0); //it.next();
            a = P.remove(0);
            e = a.leadingExpVector();
            a = normalform(P, a);
            logger.debug(String.valueOf(irr));
            if (a.length() == 0) {
                l--;
                if (l <= 1) {
                    return P;
                }
            } else {
                f = a.leadingExpVector();
                if (f.signum() == 0) {
                    P = new ArrayList<GenPolynomial<C>>();
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
     * @param row recording matrix.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @param Np nf(Pp,Ap), a normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */

    public boolean isReductionNF(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap,
                    GenPolynomial<C> Np) {
        if (row == null && Pp == null) {
            if (Ap == null) {
                return Np == null;
            }
            return Ap.equals(Np);
        }
        if (row == null || Pp == null) {
            return false;
        }
        if (row.size() != Pp.size()) {
            return false;
        }
        GenPolynomial<C> t = Np;
        //System.out.println("t0 = " + t );
        GenPolynomial<C> r;
        GenPolynomial<C> p;
        for (int m = 0; m < Pp.size(); m++) {
            r = row.get(m);
            p = Pp.get(m);
            if (r != null && p != null) {
                if (t == null) {
                    t = r.multiply(p);
                } else {
                    t = t.sum(r.multiply(p));
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
