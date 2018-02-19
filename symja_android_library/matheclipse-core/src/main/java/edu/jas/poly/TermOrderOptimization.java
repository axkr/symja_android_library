/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.arith.BigInteger;
import edu.jas.structure.RingElem;
import edu.jas.vector.BasicLinAlg;


/**
 * Term order optimization. See mas10/maspoly/DIPTOO.m{di}.
 *
 * @author Heinz Kredel
 */

public class TermOrderOptimization {


    private static final Logger logger = Logger.getLogger(TermOrderOptimization.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Degree matrix.
     *
     * @param A polynomial to be considered.
     * @return degree matrix.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<BigInteger>> degreeMatrix(GenPolynomial<C> A) {

        List<GenPolynomial<BigInteger>> dem = null;
        if (A == null) {
            return dem;
        }

        BigInteger cfac = new BigInteger();
        GenPolynomialRing<BigInteger> ufac = new GenPolynomialRing<BigInteger>(cfac, 1);

        int nvar = A.numberOfVariables();
        dem = new ArrayList<GenPolynomial<BigInteger>>(nvar);

        for (int i = 0; i < nvar; i++) {
            dem.add(ufac.getZERO());
        }
        if (A.isZERO()) {
            return dem;
        }

        for (ExpVector e : A.getMap().keySet()) {
            dem = expVectorAdd(dem, e);
        }
        return dem;
    }


    /**
     * Degree matrix exponent vector add.
     *
     * @param dm degree matrix.
     * @param e  exponent vector.
     * @return degree matrix + e.
     */
    public static List<GenPolynomial<BigInteger>> expVectorAdd(List<GenPolynomial<BigInteger>> dm, ExpVector e) {
        for (int i = 0; i < dm.size() && i < e.length(); i++) {
            GenPolynomial<BigInteger> p = dm.get(i);
            long u = e.getVal(i);
            ExpVector f = ExpVector.create(1, 0, u);
            p = p.sum(p.ring.getONECoefficient(), f);
            dm.set(i, p);
        }
        return dm;
    }


    /**
     * Degree matrix of coefficient polynomials.
     *
     * @param A polynomial to be considered.
     * @return degree matrix for the coeficients.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<BigInteger>> degreeMatrixOfCoefficients(
            GenPolynomial<GenPolynomial<C>> A) {
        if (A == null) {
            throw new IllegalArgumentException("polynomial must not be null");
        }
        return degreeMatrix(A.getMap().values());
    }


    /**
     * Degree matrix.
     *
     * @param L list of polynomial to be considered.
     * @return degree matrix.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<BigInteger>> degreeMatrix(
            Collection<GenPolynomial<C>> L) {
        if (L == null) {
            throw new IllegalArgumentException("list must be non null");
        }
        BasicLinAlg<GenPolynomial<BigInteger>> blas = new BasicLinAlg<GenPolynomial<BigInteger>>();
        List<GenPolynomial<BigInteger>> dem = null;
        for (GenPolynomial<C> p : L) {
            List<GenPolynomial<BigInteger>> dm = degreeMatrix(p);
            if (dem == null) {
                dem = dm;
            } else {
                dem = blas.vectorAdd(dem, dm);
            }
        }
        return dem;
    }


    /**
     * Degree matrix of coefficient polynomials.
     *
     * @param L list of polynomial to be considered.
     * @return degree matrix for the coeficients.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<BigInteger>> degreeMatrixOfCoefficients(
            Collection<GenPolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            throw new IllegalArgumentException("list must not be null");
        }
        BasicLinAlg<GenPolynomial<BigInteger>> blas = new BasicLinAlg<GenPolynomial<BigInteger>>();
        List<GenPolynomial<BigInteger>> dem = null;
        for (GenPolynomial<GenPolynomial<C>> p : L) {
            List<GenPolynomial<BigInteger>> dm = degreeMatrixOfCoefficients(p);
            if (dem == null) {
                dem = dm;
            } else {
                dem = blas.vectorAdd(dem, dm);
            }
        }
        return dem;
    }


    /**
     * Optimal permutation for the Degree matrix.
     *
     * @param D degree matrix.
     * @return optimal permutation for D.
     */
    public static List<Integer> optimalPermutation(List<GenPolynomial<BigInteger>> D) {
        if (D == null) {
            throw new IllegalArgumentException("list must be non null");
        }
        List<Integer> P = new ArrayList<Integer>(D.size());
        if (D.size() == 0) {
            return P;
        }
        if (D.size() == 1) {
            P.add(0);
            return P;
        }
        SortedMap<GenPolynomial<BigInteger>, List<Integer>> map = new TreeMap<GenPolynomial<BigInteger>, List<Integer>>();
        int i = 0;
        for (GenPolynomial<BigInteger> p : D) {
            List<Integer> il = map.get(p);
            if (il == null) {
                il = new ArrayList<Integer>(3);
            }
            il.add(i);
            map.put(p, il);
            i++;
        }
        List<List<Integer>> V = new ArrayList<List<Integer>>(map.values());
        //System.out.println("V = " + V);
        if (logger.isDebugEnabled()) {
            logger.info("V = " + V);
        }
        //for ( int j = V.size()-1; j >= 0; j-- ) {
        for (int j = 0; j < V.size(); j++) {
            List<Integer> v = V.get(j);
            for (Integer k : v) {
                P.add(k);
            }
        }
        return P;
    }


    /**
     * Inverse of a permutation.
     *
     * @param P permutation.
     * @return S with S*P = id.
     */
    public static List<Integer> inversePermutation(List<Integer> P) {
        if (P == null || P.size() <= 1) {
            return P;
        }
        List<Integer> ip = new ArrayList<Integer>(P); // ensure size and content
        for (int i = 0; i < P.size(); i++) {
            ip.set(P.get(i), i); // inverse
        }
        return ip;
    }


    /**
     * Test for identity permutation.
     *
     * @param P permutation.
     * @return true , if P = id, else false.
     */
    public static boolean isIdentityPermutation(List<Integer> P) {
        if (P == null || P.size() <= 1) {
            return true;
        }
        for (int i = 0; i < P.size(); i++) {
            if (P.get(i).intValue() != i) {
                return false;
            }
        }
        return true;
    }


    /**
     * Multiplication permutations.
     *
     * @param P permutation.
     * @param S permutation.
     * @return P*S.
     */
    public static List<Integer> multiplyPermutation(List<Integer> P, List<Integer> S) {
        if (P == null || S == null) {
            return null;
        }
        if (P.size() != S.size()) {
            throw new IllegalArgumentException("#P != #S: P =" + P + ", S = " + S);
        }
        List<Integer> ip = new ArrayList<Integer>(P); // ensure size and content
        for (int i = 0; i < P.size(); i++) {
            ip.set(i, S.get(P.get(i)));
        }
        return ip;
    }


    /**
     * Permutation of a list.
     *
     * @param L list.
     * @param P permutation.
     * @return P(L).
     */
    @SuppressWarnings("cast")
    public static <T> List<T> listPermutation(List<Integer> P, List<T> L) {
        if (L == null || L.size() <= 1) {
            return L;
        }
        List<T> pl = new ArrayList<T>(L.size());
        for (Integer i : P) {
            pl.add(L.get((int) i));
        }
        return pl;
    }


    /*
     * Permutation of an array. Compiles, but does not work, requires JDK 1.6 to
     * work.
     * @param a array.
     * @param P permutation.
     * @return P(a).
    @SuppressWarnings({ "unchecked", "cast" })
    public static <T> T[] arrayPermutation(List<Integer> P, T[] a) {
        if (a == null || a.length <= 1) {
            return a;
        }
        T[] b = (T[]) new Object[a.length]; // jdk 1.5 
        //T[] b = Arrays.<T>copyOf( a, a.length ); // jdk 1.6, works
        int j = 0;
        for (Integer i : P) {
            b[j] = a[(int) i];
            j++;
        }
        return b;
    }
     */


    /**
     * Permutation of polynomial exponent vectors.
     *
     * @param A polynomial.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(A).
     */
    public static <C extends RingElem<C>> GenPolynomial<C> permutation(List<Integer> P,
                                                                       GenPolynomialRing<C> R, GenPolynomial<C> A) {
        if (A == null) {
            return A;
        }
        GenPolynomial<C> B = R.getZERO().copy();
        Map<ExpVector, C> Bv = B.val; //getMap();
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            ExpVector f = e.permutation(P);
            //System.out.println("f = " + f);
            Bv.put(f, a); // assert f not in Bv
        }
        return B;
    }


    /**
     * Permutation of polynomial exponent vectors.
     *
     * @param L list of polynomials.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(L).
     */
    public static <C extends RingElem<C>> List<GenPolynomial<C>> permutation(List<Integer> P,
                                                                             GenPolynomialRing<C> R, List<GenPolynomial<C>> L) {
        if (L == null || L.size() == 0) {
            return L;
        }
        List<GenPolynomial<C>> K = new ArrayList<GenPolynomial<C>>(L.size());
        for (GenPolynomial<C> a : L) {
            GenPolynomial<C> b = permutation(P, R, a);
            K.add(b);
        }
        return K;
    }


    /**
     * Permutation of polynomial exponent vectors of coefficient polynomials.
     *
     * @param A polynomial.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(A).
     */
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> permutationOnCoefficients(
            List<Integer> P, GenPolynomialRing<GenPolynomial<C>> R, GenPolynomial<GenPolynomial<C>> A) {
        if (A == null) {
            return A;
        }
        GenPolynomial<GenPolynomial<C>> B = R.getZERO().copy();
        GenPolynomialRing<C> cf = (GenPolynomialRing<C>) R.coFac;
        Map<ExpVector, GenPolynomial<C>> Bv = B.val; //getMap();
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            //System.out.println("e = " + e);
            GenPolynomial<C> b = permutation(P, cf, a);
            //System.out.println("b = " + b);
            Bv.put(e, b); // assert e not in Bv
        }
        return B;
    }


    /**
     * Permutation of polynomial exponent vectors of coefficients.
     *
     * @param L list of polynomials.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(L).
     */
    public static <C extends RingElem<C>> List<GenPolynomial<GenPolynomial<C>>> permutationOnCoefficients(
            List<Integer> P, GenPolynomialRing<GenPolynomial<C>> R,
            List<GenPolynomial<GenPolynomial<C>>> L) {
        if (L == null || L.size() == 0) {
            return L;
        }
        List<GenPolynomial<GenPolynomial<C>>> K = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L.size());
        for (GenPolynomial<GenPolynomial<C>> a : L) {
            GenPolynomial<GenPolynomial<C>> b = permutationOnCoefficients(P, R, a);
            K.add(b);
        }
        return K;
    }


    /**
     * Permutation of polynomial ring variables.
     *
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(R).
     */
    public static <C extends RingElem<C>> GenPolynomialRing<C> permutation(List<Integer> P,
                                                                           GenPolynomialRing<C> R) {
        return R.permutation(P);
    }


    /**
     * Optimize variable order.
     *
     * @param R polynomial ring.
     * @param L list of polynomials.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> OptimizedPolynomialList<C> optimizeTermOrder(
            GenPolynomialRing<C> R, List<GenPolynomial<C>> L) {
        List<GenPolynomial<C>> Lp = new ArrayList<GenPolynomial<C>>(L);
        if (R instanceof GenSolvablePolynomialRing) { // look also on solvable relations
            GenSolvablePolynomialRing<C> Rs = (GenSolvablePolynomialRing<C>) R;
            Lp.addAll(Rs.table.relationList());
        }
        List<Integer> perm = optimalPermutation(degreeMatrix(Lp));
        GenPolynomialRing<C> pring = R.permutation(perm);
        List<GenPolynomial<C>> ppolys = permutation(perm, pring, L);
        OptimizedPolynomialList<C> op = new OptimizedPolynomialList<C>(perm, pring, ppolys);
        return op;
    }


    /**
     * Optimize variable order.
     *
     * @param P polynomial list.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> OptimizedPolynomialList<C> optimizeTermOrder(PolynomialList<C> P) {
        if (P == null) {
            return null;
        }
        return optimizeTermOrder(P.ring, P.list);
    }


    /**
     * Optimize variable order on coefficients.
     *
     * @param P polynomial list.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> OptimizedPolynomialList<GenPolynomial<C>> optimizeTermOrderOnCoefficients(
            PolynomialList<GenPolynomial<C>> P) {
        return optimizeTermOrderOnCoefficients(P.ring, P.list);
    }


    /**
     * Optimize variable order on coefficients.
     *
     * @param ring polynomial ring.
     * @param L    list of polynomials.
     * @return optimized polynomial list.
     */
    @SuppressWarnings("cast")
    public static <C extends RingElem<C>> OptimizedPolynomialList<GenPolynomial<C>> optimizeTermOrderOnCoefficients(
            GenPolynomialRing<GenPolynomial<C>> ring, List<GenPolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenPolynomial<GenPolynomial<C>>> Lp = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L);
        //GenPolynomialRing<GenPolynomial<C>> ring = P.ring;
        if (ring instanceof GenSolvablePolynomialRing) { // look also on solvable relations
            GenSolvablePolynomialRing<GenPolynomial<C>> Rs = (GenSolvablePolynomialRing<GenPolynomial<C>>) ring;
            Lp.addAll(Rs.table.relationList());
        }
        List<Integer> perm = optimalPermutation(degreeMatrixOfCoefficients(Lp));

        GenPolynomialRing<C> coFac = (GenPolynomialRing<C>) ring.coFac;
        GenPolynomialRing<C> pFac = coFac.permutation(perm);
        GenSolvablePolynomialRing<GenPolynomial<C>> sring, psring;
        GenPolynomialRing<GenPolynomial<C>> pring;
        if (ring instanceof GenSolvablePolynomialRing) { // permute also solvable relations
            sring = (GenSolvablePolynomialRing<GenPolynomial<C>>) ring;
            psring = new GenSolvablePolynomialRing<GenPolynomial<C>>(pFac, sring);
            List<GenPolynomial<GenPolynomial<C>>> ir = PolynomialList
                    .<GenPolynomial<C>>castToList(sring.table.relationList());
            ir = permutationOnCoefficients(perm, psring, ir);
            psring.addRelations(ir);
            pring = (GenPolynomialRing<GenPolynomial<C>>) psring;
        } else {
            pring = new GenPolynomialRing<GenPolynomial<C>>(pFac, ring);
        }
        List<GenPolynomial<GenPolynomial<C>>> ppolys;
        ppolys = permutationOnCoefficients(perm, pring, L);

        OptimizedPolynomialList<GenPolynomial<C>> op;
        op = new OptimizedPolynomialList<GenPolynomial<C>>(perm, pring, ppolys);
        return op;
    }


    /**
     * Optimize variable order.
     *
     * @param P module list.
     * @return optimized module list.
     */
    public static <C extends RingElem<C>> OptimizedModuleList<C> optimizeTermOrder(ModuleList<C> P) {
        if (P == null) {
            return null;
        }
        return optimizeTermOrderModule(P.ring, P.list);
    }


    /**
     * Optimize variable order.
     *
     * @param R polynomial ring.
     * @param L list of lists of polynomials.
     * @return optimized module list.
     */
    public static <C extends RingElem<C>> OptimizedModuleList<C> optimizeTermOrderModule(
            GenPolynomialRing<C> R, List<List<GenPolynomial<C>>> L) {
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>();
        for (List<GenPolynomial<C>> ll : L) {
            M.addAll(ll);
        }
        if (R instanceof GenSolvablePolynomialRing) { // look also on solvable relations
            GenSolvablePolynomialRing<C> Rs = (GenSolvablePolynomialRing<C>) R;
            M.addAll(Rs.table.relationList());
        }
        List<Integer> perm = optimalPermutation(degreeMatrix(M));
        GenPolynomialRing<C> pring = R.permutation(perm);
        List<List<GenPolynomial<C>>> mpolys = new ArrayList<List<GenPolynomial<C>>>();
        List<GenPolynomial<C>> pp;
        for (List<GenPolynomial<C>> ll : L) {
            pp = permutation(perm, pring, ll);
            mpolys.add(pp);
        }
        OptimizedModuleList<C> op = new OptimizedModuleList<C>(perm, pring, mpolys);
        return op;
    }


    /**
     * Optimize variable order on coefficients.
     *
     * @param P module list.
     * @return optimized module list.
     */
    @SuppressWarnings("cast")
    public static <C extends RingElem<C>> OptimizedModuleList<GenPolynomial<C>> optimizeTermOrderOnCoefficients(
            ModuleList<GenPolynomial<C>> P) {
        if (P == null) {
            return null;
        }
        GenPolynomialRing<GenPolynomial<C>> ring = P.ring;
        List<GenPolynomial<GenPolynomial<C>>> M = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for (List<GenPolynomial<GenPolynomial<C>>> ll : P.list) {
            M.addAll(ll);
        }
        if (ring instanceof GenSolvablePolynomialRing) { // look also on solvable relations
            GenSolvablePolynomialRing<GenPolynomial<C>> Rs = (GenSolvablePolynomialRing<GenPolynomial<C>>) ring;
            M.addAll(Rs.table.relationList());
        }
        List<Integer> perm = optimalPermutation(degreeMatrixOfCoefficients(M));

        GenPolynomialRing<C> coFac = (GenPolynomialRing<C>) ring.coFac;
        GenPolynomialRing<C> pFac = coFac.permutation(perm);
        GenSolvablePolynomialRing<GenPolynomial<C>> sring, psring;
        GenPolynomialRing<GenPolynomial<C>> pring;
        if (ring instanceof GenSolvablePolynomialRing) { // permute also solvable relations
            sring = (GenSolvablePolynomialRing<GenPolynomial<C>>) ring;
            psring = new GenSolvablePolynomialRing<GenPolynomial<C>>(pFac, sring);
            List<GenPolynomial<GenPolynomial<C>>> ir = PolynomialList
                    .<GenPolynomial<C>>castToList(sring.table.relationList());
            ir = permutationOnCoefficients(perm, psring, ir);
            psring.addRelations(ir);
            pring = (GenPolynomialRing<GenPolynomial<C>>) psring;
        } else {
            pring = new GenPolynomialRing<GenPolynomial<C>>(pFac, ring);
        }
        List<GenPolynomial<GenPolynomial<C>>> pp;
        List<List<GenPolynomial<GenPolynomial<C>>>> mpolys;
        mpolys = new ArrayList<List<GenPolynomial<GenPolynomial<C>>>>();
        for (List<GenPolynomial<GenPolynomial<C>>> ll : P.list) {
            pp = permutationOnCoefficients(perm, pring, ll);
            mpolys.add(pp);
        }
        OptimizedModuleList<GenPolynomial<C>> op = new OptimizedModuleList<GenPolynomial<C>>(perm, pring,
                mpolys);
        return op;
    }

}
