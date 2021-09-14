/*
 * $Id$
 */

package edu.jas.vector;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * Linear algebra methods. Implements linear algebra computations and tests,
 * mainly based on Gauss elimination. Partly based on <a href=
 * "https://en.wikipedia.org/wiki/LU_decomposition">LU_decomposition</a>.
 * Computation of Null space basis, row echelon form, inverses and ranks.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class LinAlg<C extends RingElem<C>> implements Serializable {


    private static final Logger logger = LogManager.getLogger(LinAlg.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public LinAlg() {
    }


    /**
     * Matrix LU decomposition. Matrix A is replaced by its LU decomposition. A
     * contains a copy of both matrices L-E and U as A=(L-E)+U such that
     * P*A=L*U. The permutation matrix is not stored as a matrix, but in an
     * integer vector P of size N+1 containing column indexes where the
     * permutation matrix has "1". The last element P[N]=S+N, where S is the
     * number of row exchanges needed for determinant computation, det(P)=(-1)^S
     * @param A a n&times;n matrix.
     * @return permutation vector P and modified matrix A.
     */
    public List<Integer> decompositionLU(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        int M = ring.cols;
        int NM = Math.min(N,M);
        if (N != M) {
            logger.warn("nosquare matrix");
        }
        List<Integer> P = new ArrayList<Integer>(NM + 1);
        for (int i = 0; i <= NM; i++) {
            P.add(i); //Unit permutation matrix, P[NM] initialized with NM
        }
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < NM; i++) {
            int imax = i;
            C maxA = ring.coFac.getZERO();
            for (int k = i; k < N; k++) {
                // absA = fabs(A[k][i])
                C absA = mat.get(k).get(i).abs();
                if (absA.compareTo(maxA) > 0) {
                    maxA = absA;
                    imax = k;
                    break; // first
                }
            }
            if (maxA.isZERO()) {
                logger.warn("matrix is degenerate at col " + i);
                mat.get(i).set(i, ring.coFac.getZERO()); // already zero
                //continue;
                P.clear();
                return P; //failure, matrix is degenerate
            }
            if (imax != i) {
                //pivoting P
                int j = P.get(i);
                P.set(i, P.get(imax));
                P.set(imax, j);
                //System.out.println("new pivot " + imax); // + ", P = " + P);
                //pivoting rows of A
                ArrayList<C> ptr = mat.get(i);
                mat.set(i, mat.get(imax));
                mat.set(imax, ptr);
                //counting pivots starting from NM (for determinant)
                P.set(NM, P.get(NM) + 1);
            }
            C dd = mat.get(i).get(i).inverse();
            for (int j = i + 1; j < N; j++) {
                // A[j][i] /= A[i][i];
                C d = mat.get(j).get(i).multiply(dd); //divide(dd);
                mat.get(j).set(i, d);
                for (int k = i + 1; k < M; k++) {
                    // A[j][k] -= A[j][i] * A[i][k];
                    C a = mat.get(j).get(i).multiply(mat.get(i).get(k));
                    mat.get(j).set(k, mat.get(j).get(k).subtract(a));
                }
            }
            //System.out.println("row(last) = " + mat.get(NM-1));
        }
        return P;
    }


    /**
     * Solve with LU decomposition.
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @param b right hand side vector.
     * @return x solution vector of A*x = b.
     */
    public GenVector<C> solveLU(GenMatrix<C> A, List<Integer> P, GenVector<C> b) {
        if (A == null || b == null) {
            return null;
        }
        if (P.size() == 0) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        GenVectorModul<C> xfac = new GenVectorModul<C>(ring.coFac, N);
        GenVector<C> x = new GenVector<C>(xfac);
        List<C> vec = x.val;
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < N; i++) {
            //x[i] = b[P[i]];
            vec.set(i, b.get(P.get(i)));
            C xi = vec.get(i);
            for (int k = 0; k < i; k++) {
                //x[i] -= A[i][k] * x[k];
                C ax = mat.get(i).get(k).multiply(vec.get(k));
                xi = xi.subtract(ax);
            }
            vec.set(i, xi);
        }
        //System.out.println("vec = " + vec);
        for (int i = N - 1; i >= 0; i--) {
            C xi = vec.get(i);
            for (int k = i + 1; k < N; k++) {
                //x[i] -= A[i][k] * x[k];
                C ax = mat.get(i).get(k).multiply(vec.get(k));
                xi = xi.subtract(ax);
            }
            vec.set(i, xi);
            //x[i] /= A[i][i];
            vec.set(i, xi.divide(mat.get(i).get(i)));
        }
        return x;
    }


    /**
     * Solve linear system of equations.
     * @param A a n&times;n matrix.
     * @param b right hand side vector.
     * @return x solution vector of A*x = b.
     */
    public GenVector<C> solve(GenMatrix<C> A, GenVector<C> b) {
        if (A == null || b == null) {
            return null;
        }
        GenMatrix<C> Ap = A.copy();
        List<Integer> P = decompositionLU(Ap);
        if (P.size() == 0) {
            System.out.println("undecomposable");
            return b.modul.getZERO();
        }
        GenVector<C> x = solveLU(Ap, P, b);
        return x;
    }


    /**
     * Determinant with LU decomposition.
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @return d determinant of A.
     */
    public C determinantLU(GenMatrix<C> A, List<Integer> P) {
        if (A == null) {
            return null;
        }
        if (P.size() == 0) {
            return A.ring.coFac.getZERO();
        }
        int N = A.ring.rows; //P.size() - 1 - 1;
        ArrayList<ArrayList<C>> mat = A.matrix;
        // det = A[0][0];
        C det = mat.get(0).get(0);
        for (int i = 1; i < N; i++) {
            //det *= A[i][i];
            det = det.multiply(mat.get(i).get(i));
        }
        //return (P[N] - N) % 2 == 0 ? det : -det
        int s = P.get(N) - N;
        if (s % 2 != 0) {
            det = det.negate();
        }
        return det;
    }


    /**
     * Inverse with LU decomposition.
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @return inv(A) with A * inv(A) == 1.
     */
    public GenMatrix<C> inverseLU(GenMatrix<C> A, List<Integer> P) {
        GenMatrixRing<C> ring = A.ring;
        GenMatrix<C> inv = new GenMatrix<C>(ring);
        int N = ring.rows; //P.size() - 1 - 1;
        ArrayList<ArrayList<C>> mat = A.matrix;
        ArrayList<ArrayList<C>> imat = inv.matrix;
        for (int j = 0; j < N; j++) {
            // transform right hand vector with L matrix
            for (int i = 0; i < N; i++) {
                //IA[i][j] = P[i] == j ? 1.0 : 0.0;
                C e = (P.get(i) == j) ? ring.coFac.getONE() : ring.coFac.getZERO();
                imat.get(i).set(j, e);
                C b = e; //imat.get(i).get(j);
                for (int k = 0; k < i; k++) {
                    //IA[i][j] -= A[i][k] * IA[k][j];
                    C a = mat.get(i).get(k).multiply(imat.get(k).get(j));
                    b = b.subtract(a);
                }
                imat.get(i).set(j, b);
            }
            // solve inverse matrix column with U matrix
            for (int i = N - 1; i >= 0; i--) {
                C b = imat.get(i).get(j);
                for (int k = i + 1; k < N; k++) {
                    //IA[i][j] -= A[i][k] * IA[k][j];
                    C a = mat.get(i).get(k).multiply(imat.get(k).get(j));
                    b = b.subtract(a);
                }
                imat.get(i).set(j, b);
                //IA[i][j] /= A[i][i];
                C e = b; //imat.get(i).get(j);
                e = e.divide(mat.get(i).get(i));
                imat.get(i).set(j, e);
            }
        }
        return inv;
    }


    /**
     * Matrix Null Space basis, cokernel. From the transpose matrix At it
     * computes the kernel with At*v_i = 0.
     * @param A a n&times;n matrix.
     * @return V a list of basis vectors (v_1, ..., v_k) with v_i*A == 0.
     */
    public List<GenVector<C>> nullSpaceBasis(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        int M = ring.cols;
        if (N != M) {
            logger.warn("nosquare matrix");
        }
        List<GenVector<C>> nspb = new ArrayList<GenVector<C>>();
        GenVectorModul<C> vfac = new GenVectorModul<C>(ring.coFac, M);
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < N; i++) {
            C maxA, absA;
            // search privot imax
            int imax = i;
            maxA = ring.coFac.getZERO();
            for (int k = i; k < M; k++) { // k = 0 ?
                // absA = fabs(A[k][i])
                absA = mat.get(i).get(k).abs();
                if (absA.compareTo(maxA) > 0 && maxA.isZERO()) {
                    maxA = absA;
                    imax = k;
                }
            }
            logger.info("pivot: " + imax + ", i = " + i + ", maxA = " + maxA);
            if (maxA.isZERO()) {
                // check for complete zero row or left pivot
                int imaxl = i;
                for (int k = 0; k < i; k++) { // k = 0 ?
                    // absA = fabs(A[k][i])
                    absA = mat.get(i).get(k).abs();
                    if (absA.compareTo(maxA) > 0) { // first or last imax: && maxA.isZERO()
                        imaxl = k;
                        // check if upper triangular column is zero
                        boolean iszero = true;
                        for (int m = 0; m < i; m++) {
                            C amm = mat.get(m).get(imaxl).abs();
                            if (!amm.isZERO()) {
                                iszero = false;
                                break;
                            }
                        }
                        if (iszero) { // left pivot okay
                            imax = imaxl;
                            logger.info("pivot*: " + imax + ", i = " + i + ", absA = " + absA);
                            maxA = ring.coFac.getONE();
                        }
                    }
                }
                if (maxA.isZERO()) { // complete zero row
                    continue;
                }
            }
            if (imax < M) { //!= i
                //normalize column i
                C mp = mat.get(i).get(imax).inverse();
                for (int k = 0; k < N; k++) { // k = i ?
                    C b = mat.get(k).get(imax);
                    b = b.multiply(mp);
                    mat.get(k).set(imax, b);
                }
                //pivoting columns of A
                if (imax != i) {
                    for (int k = 0; k < N; k++) {
                        C b = mat.get(k).get(i);
                        mat.get(k).set(i, mat.get(k).get(imax));
                        mat.get(k).set(imax, b);
                    }
                }
                //eliminate rest of row i via column operations
                for (int j = 0; j < M; j++) {
                    if (i == j) { // is already normalized
                        continue;
                    }
                    C mm = mat.get(i).get(j);
                    for (int k = 0; k < N; k++) { // or k = 0
                        C b = mat.get(k).get(j);
                        C c = mat.get(k).get(i);
                        C d = b.subtract(c.multiply(mm));
                        mat.get(k).set(j, d);
                    }
                }
            }
        }
        // convert to A-I
        for (int i = 0; i < N; i++) {
            C b = mat.get(i).get(i);
            b = b.subtract(ring.coFac.getONE());
            mat.get(i).set(i, b);
        }
        //System.out.println("mat-1 = " + A);
        // read off non zero rows of A
        for (int i = 0; i < N; i++) {
            List<C> row = mat.get(i);
            boolean iszero = true;
            for (int k = 0; k < M; k++) {
                if (!row.get(k).isZERO()) {
                    iszero = false;
                    break;
                }
            }
            if (!iszero) {
                GenVector<C> v = new GenVector<C>(vfac, row);
                nspb.add(v);
            }
        }
        return nspb;
    }


    /**
     * Rank via null space.
     * @param A a n&times;n matrix.
     * @return r rank of A.
     */
    public long rankNS(GenMatrix<C> A) {
        if (A == null) {
            return -1l;
        }
        GenMatrix<C> Ap = A.copy();
        long n = Math.min(A.ring.rows, A.ring.cols);
        List<GenVector<C>> ns = nullSpaceBasis(Ap);
        long s = ns.size();
        return n - s;
    }


    /**
     * Matrix row echelon form construction. Matrix A is replaced by its row
     * echelon form, an upper triangle matrix.
     * @param A a n&times;m matrix.
     * @return A row echelon form of A, matrix A is modified.
     */
    public GenMatrix<C> rowEchelonForm(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        int M = ring.cols;
        if (N != M) {
            logger.warn("nosquare matrix");
        }
        int kmax = 0;
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < N;) {
            int imax = i;
            C maxA = ring.coFac.getZERO();
            // search non-zero rows
            for (int k = i; k < N; k++) {
                // absA = fabs(A[k][i])
                C absA = mat.get(k).get(kmax).abs();
                if (absA.compareTo(maxA) > 0) {
                    maxA = absA;
                    imax = k;
                    break; // first
                }
            }
            if (maxA.isZERO()) {
                //System.out.println("matrix is zero at col " + kmax);
                kmax++;
                if (kmax >= M) {
                    break;
                }
                continue;
            }
            //System.out.println("matrix is non zero at row " + imax);
            if (imax != i) {
                //swap pivoting rows of A
                ArrayList<C> ptr = mat.get(i);
                mat.set(i, mat.get(imax));
                mat.set(imax, ptr);
            }
            // A[j][i] /= A[i][i];
            C dd = mat.get(i).get(kmax).inverse();
            //System.out.println("matrix is non zero at row " + imax + ", dd = " + dd);
            for (int k = kmax; k < M; k++) {
                C d = mat.get(i).get(k).multiply(dd); //divide(dd);
                mat.get(i).set(k, d);
            }
            for (int j = i + 1; j < N; j++) {
                for (int k = kmax; k < M; k++) {
                    // A[j][k] -= A[j][k] * A[i][k];
                    C a = mat.get(j).get(k).multiply(mat.get(i).get(k));
                    if (a.isZERO()) {
                        continue;
                    }
                    mat.get(j).set(k, mat.get(j).get(k).subtract(a));
                }
            }
            mat.get(i).set(kmax, ring.coFac.getONE());
            i++;
            kmax++;
            if (kmax >= M) {
                break;
            }
            //System.out.println("rowEch(last) = " + mat.get(N-1));
        }
        return A;
    }


    /**
     * Rank via row echelon form.
     * @param A a n&times;n matrix.
     * @return r rank of A.
     */
    public long rankRE(GenMatrix<C> A) {
        if (A == null) {
            return -1l;
        }
        long n = A.ring.rows;
        long m = A.ring.cols;
        ArrayList<ArrayList<C>> mat = A.matrix;
        // count non-zero rows
        long r = 0;
        for (int i = 0; i < n; i++) {
            ArrayList<C> row = mat.get(i);
            for (int j = i; j < m; j++) {
                if (!row.get(j).isZERO()) {
                    r++;
                    break;
                }
            }
        }
        return r;
    }


    /**
     * Matrix row echelon form construction. Matrix A is replaced by
     * its row echelon form, an upper triangle matrix with less
     * non-zero entries. No column swaps and transforms are performed
     * as with the Gauss-Jordan algorithm.
     * @param A a n&times;m matrix.
     * @return A sparse row echelon form of A, matrix A is modified.
     */
    public GenMatrix<C> rowEchelonFormSparse(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        int M = ring.cols;
        if (N != M) {
            logger.warn("nosquare matrix");
        }
        int i, imax, kmax;
        C maxA, absA;
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (i = N - 1; i > 0; i--) {
            imax = i;
            maxA = ring.coFac.getZERO();
            //System.out.println("matrix row " + A.getRow(i));
            kmax = -1;
            // search non-zero entry in row i
            for (int k = i; k < M; k++) {
                // absA = fabs(A[i][k])
                absA = mat.get(i).get(k).abs();
                if (absA.compareTo(maxA) > 0) {
                    //System.out.println("absA(" + i +"," + k + ") = " + absA);
                    maxA = absA;
                    kmax = k;
                    break; // first
                }
            }
            if (maxA.isZERO()) {
                continue;
            }
            // reduce upper rows
            for (int j = imax - 1; j >= 0; j--) {
                for (int k = kmax; k < M; k++) {
                    // A[j,k] -= A[j,k] * A[imax,k]
                    C mjk = mat.get(j).get(k);
                    if (mjk.isZERO()) {
                        continue;
                    }
                    C mk = mat.get(imax).get(k);
                    if (mk.isZERO()) {
                        continue;
                    }
                    C a = mk.multiply(mjk);
                    //System.out.println("mjk(" + j +"," + k + ") = " + mjk + ", mk = " + mk);
                    mjk = mjk.subtract(a);
                    mat.get(j).set(k,mjk);
                }
            }
        }
        return A;
    }


    /**
     * Matrix fraction free Gauss elimination. Matrix A is replaced by
     * its fraction free LU decomposition. A contains a copy of both
     * matrices L-E and U as A=(L-E)+U such that P*A=L*U. Todo: L is
     * not computed but 0. The permutation matrix is not stored as a
     * matrix, but in an integer vector P of size N+1 containing
     * column indexes where the permutation matrix has "1". The last
     * element P[N]=S+N, where S is the number of row exchanges needed
     * for determinant computation, det(P)=(-1)^S
     * @param A a n&times;n matrix.
     * @return permutation vector P and modified matrix A.
     */
    public List<Integer> fractionfreeGaussElimination(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        int M = ring.cols;
        int NM = Math.min(N,M);
        if (N != M) {
            logger.warn("nosquare matrix");
        }
        List<Integer> P = new ArrayList<Integer>(NM + 1);
        for (int i = 0; i <= NM; i++) {
            P.add(i); //Unit permutation matrix, P[NM] initialized with NM
        }
        int r = 0;
        C divisor = ring.coFac.getONE();
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < NM && r < N; i++) {
            int imax = i;
            C maxA = ring.coFac.getZERO();
            for (int k = i; k < N; k++) {
                // absA = fabs(A[k][i])
                C absA = mat.get(k).get(i).abs();
                if (absA.compareTo(maxA) > 0) {
                    maxA = absA;
                    imax = k;
                    break; // first
                }
            }
            if (maxA.isZERO()) {
                logger.warn("matrix is degenerate at col " + i);
                mat.get(i).set(i, ring.coFac.getZERO()); //already zero
                //continue;
                P.clear();
                return P; //failure, matrix is degenerate
            }
            if (imax != i) {
                //pivoting P
                int j = P.get(i);
                P.set(i, P.get(imax));
                P.set(imax, j);
                //System.out.println("new pivot " + imax); // + ", P = " + P);
                //pivoting rows of A
                ArrayList<C> ptr = mat.get(i);
                mat.set(i, mat.get(imax));
                mat.set(imax, ptr);
                //counting pivots starting from NM (for determinant)
                P.set(NM, P.get(NM) + 1);
            }
            //C dd = mat.get(i).get(i).inverse();
            for (int j = r + 1; j < N; j++) {
                // A[j][i] /= A[i][i];
                //C d = mat.get(j).get(i).multiply(dd); //divide(dd);
                //mat.get(j).set(i, d);
                for (int k = i + 1; k < M; k++) { //+ 1
                    // A[j][k] -= A[j][i] * A[i][k];
                    //C a = mat.get(j).get(i).multiply(mat.get(i).get(k));
                    //mat.get(j).set(k, mat.get(j).get(k).subtract(a));
                    //System.out.println("i = " + i + ", r = " + r + ", j = " + j + ", k = " + k);
                    C a = mat.get(r).get(i).multiply(mat.get(j).get(k));
                    C b = mat.get(r).get(k).multiply(mat.get(j).get(i));
                    C d = a.subtract(b).divide(divisor);
                    //System.out.println(", a = " + a + ", b = " + b + ", d = " + d);
                    mat.get(j).set(k, d);
                }
            }
            for (int j = r + 1; j < N; j++) { // set L-E = 0
                mat.get(j).set(i, ring.coFac.getZERO());
            }
            divisor = mat.get(r).get(i);
            r++;
            //System.out.println("divisor = " + divisor);
            //System.out.println("mat = " + mat);
            //System.out.println("row(last) = " + mat.get(NM-1));
        }
        return P;
    }

}
