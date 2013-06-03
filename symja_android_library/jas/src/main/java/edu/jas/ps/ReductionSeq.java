/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Multivariate power series reduction sequential use algorithm. Implements Mora
 * normal-form algorithm.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ReductionSeq<C extends RingElem<C>> // should be FieldElem<C>>
/*todo: extends ReductionAbstract<C>*/{


    private static final Logger logger = Logger.getLogger(ReductionSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public ReductionSeq() {
    }


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param A power series.
     * @param B power series.
     * @return true if the module S-power-series(i,j) is required.
     */
    public boolean moduleCriterion(int modv, MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B) {
        if (modv == 0) {
            return true;
        }
        ExpVector ei = A.orderExpVector();
        ExpVector ej = B.orderExpVector();
        return moduleCriterion(modv, ei, ej);
    }


    /**
     * Module criterion.
     * @param modv number of module variables.
     * @param ei ExpVector.
     * @param ej ExpVector.
     * @return true if the module S-power-series(i,j) is required.
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
     * GB criterion 4. Use only for commutative power series rings.
     * @param A power series.
     * @param B power series.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-power-series(i,j) is required, else false.
     */
    public boolean criterion4(MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B, ExpVector e) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
            if (!A.ring.isCommutative()) {
                logger.error("GBCriterion4 not applicabable to non-commutative power series");
                return true;
            }
        }
        ExpVector ei = A.orderExpVector();
        ExpVector ej = B.orderExpVector();
        ExpVector g = ei.sum(ej);
        // boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        return !(s == 0);
    }


    /**
     * S-Power-series, S-polynomial.
     * @param A power series.
     * @param B power series.
     * @return spol(A,B) the S-power-series of A and B.
     */
    public MultiVarPowerSeries<C> SPolynomial(MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B) {
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
        Map.Entry<ExpVector, C> ma = A.orderMonomial();
        Map.Entry<ExpVector, C> mb = B.orderMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        MultiVarPowerSeries<C> Ap = A.multiply(b, e1);
        MultiVarPowerSeries<C> Bp = B.multiply(a, f1);
        MultiVarPowerSeries<C> C = Ap.subtract(Bp);
        return C;
    }


    /**
     * Top normal-form with Mora's algorithm.
     * @param Ap power series.
     * @param Pp power series list.
     * @return top-nf(Ap) with respect to Pp.
     */
    public MultiVarPowerSeries<C> normalform(List<MultiVarPowerSeries<C>> Pp, MultiVarPowerSeries<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        if (!Ap.ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        List<MultiVarPowerSeries<C>> P = new ArrayList<MultiVarPowerSeries<C>>(Pp.size());
        synchronized (Pp) {
            P.addAll(Pp);
        }
        ArrayList<ExpVector> htl = new ArrayList<ExpVector>(P.size());
        ArrayList<C> lbc = new ArrayList<C>(P.size());
        ArrayList<MultiVarPowerSeries<C>> p = new ArrayList<MultiVarPowerSeries<C>>(P.size());
        ArrayList<Long> ecart = new ArrayList<Long>(P.size());
        Map.Entry<ExpVector, C> m;
        int j = 0;
        for (int i = 0; i < P.size(); i++) {
            m = P.get(i).orderMonomial();
            //System.out.println("m_i = " + m);
            if (m != null) {
                p.add(P.get(i));
                //System.out.println("e = " + m.getKey().toString(Ap.ring.vars));
                htl.add(m.getKey());
                lbc.add(m.getValue());
                ecart.add(P.get(i).ecart());
                j++;
            }
        }
        //int ll = j;
        MultiVarPowerSeries<C> S = Ap;
        //S.setTruncate(Ap.ring.truncate()); // ??
        m = S.orderMonomial();
        while (true) {
            //System.out.println("m = " + m);
            //System.out.println("S = " + S);
            if (m == null) {
                return S;
            }
            if (S.isZERO()) {
                return S;
            }
            ExpVector e = m.getKey();
            if (debug) {
                logger.debug("e = " + e.toString(Ap.ring.vars));
            }
            // search ps with ht(ps) | ht(S)
            List<Integer> li = new ArrayList<Integer>();
            int i;
            for (i = 0; i < htl.size(); i++) {
                if (e.multipleOf(htl.get(i))) {
                    //System.out.println("m = " + m);
                    li.add(i);
                }
            }
            if (li.isEmpty()) {
                return S;
            }
            //System.out.println("li = " + li);
            // select ps with smallest ecart
            long mi = Long.MAX_VALUE;
            //String es = "";
            for (int k = 0; k < li.size(); k++) {
                int ki = li.get(k);
                long x = ecart.get(ki); //p.get( ki ).ecart();
                //es = es + x + " ";
                if (x < mi) { // first < or last <= ?
                    mi = x;
                    i = ki;
                }
            }
            //System.out.println("li = " + li + ", ecarts = " + es);
            //System.out.println("i = " + i + ", p_i = " + p.get(i));
            //if ( i <= ll ) {
            //} else {
            //    System.out.println("i = " + i + ", ll = " + ll);
            //}
            long si = S.ecart();
            if (mi > si) {
                //System.out.println("ecart_i = " + mi + ", ecart_S = " + si + ", S+ = " + S);
                p.add(S);
                htl.add(m.getKey());
                lbc.add(m.getValue());
                ecart.add(si);
            }
            e = e.subtract(htl.get(i));
            C a = m.getValue().divide(lbc.get(i));
            MultiVarPowerSeries<C> Q = p.get(i).multiply(a, e);
            S = S.subtract(Q);
            m = S.orderMonomial();
        }
    }


    /**
     * Total reduced normal-form with Mora's algorithm.
     * @param A power series.
     * @param P power series list.
     * @return total-nf(A) with respect to P.
     */
    public MultiVarPowerSeries<C> totalNormalform(List<MultiVarPowerSeries<C>> P, MultiVarPowerSeries<C> A) {
        if (P == null || P.isEmpty()) {
            return A;
        }
        if (A == null) {
            return A;
        }
        MultiVarPowerSeries<C> R = normalform(P, A);
        if (R.isZERO()) {
            return R;
        }
        MultiVarCoefficients<C> Rc = new MultiVarCoefficients<C>(A.ring) {


            @Override
            public C generate(ExpVector i) { // will not be used
                return pfac.coFac.getZERO();
            }
        };
        GenPolynomialRing<C> pfac = A.lazyCoeffs.pfac;
        while (!R.isZERO()) {
            Map.Entry<ExpVector, C> m = R.orderMonomial();
            if (m == null) {
                break;
            }
            R = R.reductum();
            ExpVector e = m.getKey();
            long t = e.totalDeg();
            GenPolynomial<C> p = Rc.coeffCache.get(t);
            if (p == null) {
                p = pfac.getZERO();
            }
            p = p.sum(m.getValue(), e);
            Rc.coeffCache.put(t, p);
            // zeros need never update

            R = normalform(P, R);
        }
        R = R.sum(Rc);
        //System.out.println("R+Rc = " + R);
        return R;
    }


    /**
     * Total reduced normalform with Mora's algorithm.
     * @param P power series list.
     * @return total-nf(p) for p with respect to P\{p}.
     */
    public List<MultiVarPowerSeries<C>> totalNormalform(List<MultiVarPowerSeries<C>> P) {
        if (P == null || P.isEmpty()) {
            return P;
        }
        //StandardBaseSeq<C> std = new StandardBaseSeq<C>();
        List<MultiVarPowerSeries<C>> R = new ArrayList<MultiVarPowerSeries<C>>(P.size());
        List<MultiVarPowerSeries<C>> S = new ArrayList<MultiVarPowerSeries<C>>(P);
        for (MultiVarPowerSeries<C> a : P) {
            //S.remove(a);
            //if ( !std.isSTD(S) ) {
            //    System.out.println("a = " + a);
            //}
            Map.Entry<ExpVector, C> m = a.orderMonomial();
            if (m == null) {
                //S.add(a);
                continue;
            }
            MultiVarPowerSeries<C> r = a.reductum();

            MultiVarPowerSeries<C> b = normalform(S, r);
            // need also unit of reduction: u r --> b
            // b = b.multiply(u);
            b = b.sum(m);
            if (!b.isZERO()) {
                R.add(b);
            }
        }
        return R;
    }


    /**
     * Is top reducible.
     * @param A power series.
     * @param P power series list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<MultiVarPowerSeries<C>> P, MultiVarPowerSeries<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null) {
            return false;
        }
        ExpVector e = A.orderExpVector();
        if (e == null) {
            return false;
        }
        for (MultiVarPowerSeries<C> p : P) {
            ExpVector ep = p.orderExpVector();
            if (ep == null) { // found by findbugs
                continue;
            }
            if (e.multipleOf(ep)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Ideal containment. Test if each b in B is contained in ideal S.
     * @param S standard base.
     * @param B list of power series
     * @return true, if each b in B is contained in ideal(S), else false
     */
    public boolean contains(List<MultiVarPowerSeries<C>> S, List<MultiVarPowerSeries<C>> B) {
        if (B == null || B.size() == 0) {
            return true;
        }
        if (S == null || S.size() == 0) {
            return true;
        }
        for (MultiVarPowerSeries<C> b : B) {
            if (b == null) {
                continue;
            }
            MultiVarPowerSeries<C> z = normalform(S, b);
            if (!z.isZERO()) {
                System.out.println("contains nf(b) != 0: " + b + ", z = " + z);
                return false;
            }
        }
        return true;
    }

}
