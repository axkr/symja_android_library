/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package de.lab4inf.math.lapack;

import java.util.Arrays;
import java.util.Collections;

import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.identity;
import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.lapack.Matrices.MATRIXHELPER;
import static de.lab4inf.math.util.Accuracy.DEPS;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Eigenvalue decomposition of Jacobi. Calculate all eigenvalues und eigenvectors
 * of a symmetric matrix A.
 *
 * @author nwulff
 * @version $Id: JacobiEigenvalueDecomposition.java,v 1.5 2014/12/09 10:35:14 nwulff Exp $
 * @since 07.12.2014
 */
public class JacobiEigenvalueDecomposition extends L4MObject implements EigenValueDecomposition {
    final double MIN = 2 * DEPS;
    final double MAX = 1.0 / sqrt(MIN);
    private double[] d;
    private double[][] v;

    /**
     * Constructor to perform the eigenvalue decomposition A = Vt D V for the
     * symmetric matrix a.
     *
     * @param a the matrix to decompose.
     */
    public JacobiEigenvalueDecomposition(@Symmetric final double[][] a) {
        if (!isSymmetric(a)) {
            throw new IllegalArgumentException("matrix not symmetric");
        }
        decompose(a, MIN);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.EigenValueDecomposition#eigenvalues()
     */
    @Override
    public double[] eigenvalues() {
        return d;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.EigenValueDecomposition#eigenvectors()
     */
    @Override
    public double[][] eigenvectors() {
        return v;
    }

    /**
     * Perform the iterative eigenvalue decomposition due to Jacobi.
     *
     * @param a   matrix to decompose
     * @param eps the precision to reach
     */
    private void decompose(final double[][] a, final double eps) {
        final double[][] aa = copy(a);
        final int n = a.length;
        final int m = 200 * n;
        d = new double[n];
        v = identity(n);
        decomposeCyclic(aa, m, eps);
    }

    private void decomposeCyclic(double[][] a, final int m, final double eps) {
        final int n = a.length;
        boolean convergence = false;
        double tol;
        int ite = 0;
        do {
            for (int j = 0; j < n; d[j] = a[j][j], j++)
                ;
            tol = eps * norm(d) / n;
            convergence = true;
            for (int p = 0; p < n - 1; p++) {
                for (int q = p + 1; q < n; q++) {
                    if (abs(a[p][q]) > tol) {
                        a = givensTransformation(a, p, q);
                        convergence = false;
                    }
                }
            }
        } while (!convergence && ++ite < m);
        if (ite >= m) {
            throw new IllegalStateException(format("no convergence after %d iterations", m));
        } else {
            getLogger().info(format("needed %d iterations for %.2g accuracy", ite, eps));
        }
        v = LinearAlgebra.transpose(v);
        // sort the eigenvalues for absolute magnitude, also swap the eigenvectors
        final SortableEigenvector[] tmpSort = new SortableEigenvector[n];
        for (int j = 0; j < n; j++) {
            final double ev = a[j][j];
            tmpSort[j] = new SortableEigenvector(abs(ev), ev, v[j]);
        }
        Arrays.sort(tmpSort, Collections.reverseOrder());
        for (int j = 0; j < n; j++) {
            v[j] = tmpSort[j].eigenvector;
            d[j] = tmpSort[j].eigenvalue;
            // make the largest entry of the eigenvectors positive
            final int max = LinearAlgebra.maxindex(v[j]);
            if (v[j][max] < 0) {
                final double[] eigenvector = v[j];
                for (int k = 0; k < n; eigenvector[k] = -eigenvector[k], k++)
                    ;
            }
        }
    }

    /**
     * Perform the Givens transformation A = Rt*A*R, with a rotation matrix R
     * for the j and k rows and columns.
     *
     * @param a the matrix to rotate
     * @param j the 1.st index
     * @param k the 2.nd index
     */
    private double[][] givensTransformation(final double[][] a, final int j, final int k) {
        final int n = a.length;
        double cos, sin, tan, tau;
        tau = (a[j][j] - a[k][k]) / (2 * a[j][k]);
        if (abs(tau) < MAX) {
            tan = abs(tau) + sqrt(1.0 + tau * tau);
            tan = 1 / tan;
        } else {
            // getLogger().error(format("tau too big %f", tau));
            tan = 1 / abs(2 * tau);
        }
        if (tau < 0)
            tan = -tan;
        // calculate the rotation matrix R
        cos = 1.0 / sqrt(1 + tan * tan);
        sin = cos * tan;
        tau = sin / (1.0 + cos);
        for (int r = 0; r < n; r++) {
            if (r == j || r == k)
                continue;
            double arj, ark;
            arj = a[r][j] + sin * (a[r][k] - tau * a[r][j]);
            ark = a[r][k] - sin * (a[r][j] + tau * a[r][k]);
            a[j][r] = a[r][j] = arj;
            a[k][r] = a[r][k] = ark;
        }
        a[j][j] += tan * a[j][k];
        a[k][k] -= tan * a[j][k];
        a[k][j] = 0;
        a[j][k] = 0;
        // perform V*R
        // works but might be inefficient for a sparse matrix...
        final double[][] rot = MATRIXHELPER.rotationMatrix(n, j, k, sin, cos);
        v = mult(v, rot);
        return a;
    }

    // Helper class to sort the eigenvalues and corresponding eigenvectors
    // for absolute eigenvalues
    static private class SortableEigenvector implements Comparable<SortableEigenvector> {
        double absev;
        double eigenvalue;
        double[] eigenvector;

        SortableEigenvector(final double abse, final double ev, final double[] vec) {
            absev = abse;
            eigenvalue = ev;
            eigenvector = vec;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(final SortableEigenvector that) {
            if (this.absev < that.absev)
                return -1;
            else if (this.absev > that.absev)
                return +1;
            return 0;

        }
    }

}
 