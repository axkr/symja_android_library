/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.Randomizer;

import static de.lab4inf.math.lapack.LinearAlgebra.normalize;
import static de.lab4inf.math.lapack.LinearAlgebra.rndVector;

/**
 * Helper class to generate random matrices with some desired properties, like
 * symmetry, positive eigenvalues, determinant or a known eigenvalue spectrum.
 *
 * @author nwulff
 * @version $Id: Matrices.java,v 1.23 2014/12/12 17:30:45 nwulff Exp $
 * @since 17.11.2014
 */
public class Matrices extends L4MObject {
    public static final Matrices MATRIXHELPER = new Matrices();
    public static final double EV_MAX = +5.0;
    public static final double EV_MIN = -EV_MAX;
    public static final double POSITIVE_EV_MIN = 0.25;
    public static final double POSITIVE_EV_MAX = 10.0;
    private static final String NEGATIVE = "negative ";
    private double evMin = EV_MIN;
    private double evMax = EV_MAX;
    private double positive_evMin = POSITIVE_EV_MIN;
    private double positive_evMax = POSITIVE_EV_MAX;

    /**
     * Hidden constructor.
     */
    protected Matrices() {
    }

    /**
     * Multiply the two given matrices. As this method is only for internal use
     * square matrices can be safely assumed without further checks. The fact
     * that the diag matrix contains only eigenvalues on the diagonal eliminates
     * one inner loop.
     *
     * @param diag double[] the diagonal elements of the eigenvalue matrix
     * @param rot  double[][] the orthonormal rotation matrix
     * @return matrix A = D*R
     */
    private static double[][] multDR(final double[] diag, final double[][] rot) {
        final int n = diag.length;
        final double[][] a = new double[n][];
        for (int i = 0; i < n; i++) {
            final double[] ai = new double[n];
            for (int j = 0; j < n; j++) {
                ai[j] = diag[i] * rot[i][j];
            }
            a[i] = ai;
        }
        return a;
    }

    /**
     * Do a symmetric rotation of diagonal matrix D performing  Rt*D*R, where
     * Rt is the transpose of the rotation matrix R. As D is diagonal the
     * calculation of one inner loop can be eliminated for R*D*R.
     *
     * @param diag double[] the diagonal eigenvalue matrix
     * @param rot  double[][] the orthonormal rotation matrix
     * @return matrix A = Rt*D*R
     */
    @Symmetric
    private static double[][] multRtDR(final double[] diag, final double[][] rot) {
        final int n = diag.length;
        @Symmetric final double[][] c = new double[n][n];
        double tmp = 0;
        for (int i = 0; i < n; i++) {
            final double[] ri = rot[i];
            for (int j = 0; j <= i; j++) {
                final double[] rj = rot[j];
                for (int k = 0; k < n; k++) {
                    tmp += diag[k] * ri[k] * rj[k];
                }
                c[i][j] = tmp; // as the product is symmetric we need only
                c[j][i] = tmp; // half the j-loop and force symmetry.
                tmp = 0;
            }
        }
        return c;
    }

    /**
     * Get the evMin value.
     *
     * @return the evMin
     */
    public double getEvMin() {
        return evMin;
    }

    /**
     * Set the evMin value.
     *
     * @param value to set
     */
    public void setEvMin(final double value) {
        evMin = value;
    }

    /**
     * Get the evMax value.
     *
     * @return the evMax
     */
    public double getEvMax() {
        return evMax;
    }

    /**
     * Set the evMax value.
     *
     * @param value to set
     */
    public void setEvMax(final double value) {
        evMax = value;
    }

    /**
     * Get the positive_evMin value.
     *
     * @return the positive_evMin
     */
    public double getPositiveEvMin() {
        return positive_evMin;
    }

    /**
     * Set the positive_evMin value.
     *
     * @param value the positive_evMin to set
     */
    public void setPositiveEvMin(final double value) {
        if (value < 0)
            throw new IllegalArgumentException(NEGATIVE + value);
        positive_evMin = value;
    }

    /**
     * Get the positive evMax value.
     *
     * @return the positive evMax
     */
    public double getPositiveEvMax() {
        return positive_evMax;
    }

    /**
     * Set the positive evMax value.
     *
     * @param value the positive evMax to set
     */
    public void setPositiveEvMax(final double value) {
        if (value < 0)
            throw new IllegalArgumentException(NEGATIVE + value);
        this.positive_evMax = value;
    }

    /**
     * Create a NxN identity matrix with trace (1,...,1).
     *
     * @param n dimension of the matrix
     * @return identity matrix
     */
    @Symmetric
    @PositiveDefinite
    public final double[][] identityMatrix(final int n) {
        final double[][] id = new double[n][n];
        for (int k = 0; k < n; id[k][k] = 1, k++) ;
        return id;
    }

    /**
     * Create a NxN rotation matrix with cosine and sine terms in the four
     * appropriate (j,k) positions. The sin and cos values will be normalized
     * to one, i.e. sin**2 + cos**2 = 1.
     *
     * @param n   dimension of the matrix
     * @param sin the sine value
     * @param cos the cosine value
     * @return rotation matrix
     */
    public final double[][] rotationMatrix(final int n, final int j, final int k, final double sin, final double cos) {
        final double norm = Math.sqrt(cos * cos + sin * sin);
        final double[][] rot = new double[n][n];
        for (int i = 0; i < n; rot[i][i] = 1, i++) ;
        final double c = cos / norm, s = sin / norm;
        rot[j][j] = +c;
        rot[j][k] = -s;
        rot[k][j] = +s;
        rot[k][k] = +c;
        return rot;
    }

    /**
     * Create a n times n random orthonormal symmetric reflection matrix O(n).
     *
     * @param n the dimension of the matrix
     * @return double[][] random orthonormal matrix
     */
    @Symmetric
    public final double[][] rndReflectionMatrix(final int n) {
        @Symmetric
        @PositiveDefinite final double[][] orthonormal = identityMatrix(n);
        final double[] w = rndVector(n);
        double[] o;
        // The Euclidean L2 norm of w must be one.
        normalize(w);
        for (int j = 0; j < n; j++) {
            o = orthonormal[j];
            for (int k = j; k < n; k++) {
                o[k] -= 2 * w[j] * w[k];
                // force symmetry
                orthonormal[k][j] = o[k];
            }
        }
        return orthonormal;
    }

    /**
     * Create a n times n random orthonormal rotation matrix SO(n) with complex
     * eigenvalues. The eigenvalues have absolute magnitude one and come in
     * conjugate pairs or for odd dimensions with one real eigenvalue of 1.
     * <br/>
     * As the product of two reflection matrices is always a rotation matrix
     * this is an easy way of construction from two random reflections.
     *
     * @param n the dimension of the matrix
     * @return double[][] random orthonormal matrix
     */
    public final double[][] rndRotationMatrix(final int n) {
        final double[][] p = rndReflectionMatrix(n);
        double[][] d = rndReflectionMatrix(n);
        d = LinearAlgebra.mult(p, d);
        // d = identityMatrix(n);
        // double c, s;
        // for (int k = 1; k<n; k += 2) {
        // c = Randomizer.rndBox(-1, 1);
        // s = sqrt(1-c*c);
        // d[k-1][k-1] = c;
        // d[k][k] = c;
        // d[k][k-1] = s;
        // d[k-1][k] = -s;
        // }
        // d = LinearAlgebra.multTransposeA(p, d);
        // d = LinearAlgebra.mult(d, p);
        return d;
    }

    /**
     * Create a n times n random orthonormal matrix with real eigenvalues.
     *
     * @param n the dimension of the matrix
     * @return double[][] random orthonormal matrix
     */
    @Symmetric
    public final double[][] rndOrthonormalMatrix(final int n) {
        return rndReflectionMatrix(n);
    }

    /**
     * Create a n times n random matrix with the given eigenvalues. If the
     * symmetric flag is true the matrix is in addition symmetric.
     *
     * @param eigenvalues to use for the matrix
     * @param symmetric   flag to indicate symmetry
     * @return matrix with well known eigenvalues
     */
    public final double[][] rndFromEigenvalues(final double[] eigenvalues, final boolean symmetric) {
        final int n = eigenvalues.length;
        final double[][] rndRotation = rndRotationMatrix(n);
        if (!symmetric) {
            // shuffle the diagonal elements via orthonormal rotation matrix
            return multDR(eigenvalues, rndRotation);
        } else {
            // shuffle two times using also the transposed rotation
            return multRtDR(eigenvalues, rndRotation);
        }
    }

    /**
     * Create a n times n random matrix with eigenvalues in the range [evMin,evMax].
     * If the symmetric flag is true the matrix is in addition symmetric.
     *
     * @param n         the dimension of the matrix
     * @param evMin     minimal eigenvalue
     * @param evMax     maximal eigenvalue
     * @param symmetric flag indicating symmetry or not
     * @return matrix with eigenvalues between evMin and evNax
     */
    public final double[][] rndMatrix(final int n, final double evMin, final double evMax, final boolean symmetric) {
        final double[] ev = new double[n];
        // force evMin &le; evMax
        ev[0] = Math.min(evMin, evMax);
        ev[n - 1] = Math.max(evMin, evMax);
        for (int k = 1; k < n - 1; k++)
            ev[k] = Randomizer.rndBox(evMin, evMax);
        return rndFromEigenvalues(ev, symmetric);
    }

    /**
     * Create a n times n random matrix with eigenvalues in the range [evMin,evMax].
     *
     * @param n     the dimension of the matrix
     * @param evMin minimal eigenvalue
     * @param evMax maximal eigenvalue
     * @return matrix with eigenvalues between evMin and evMax
     */
    public double[][] rndMatrix(final int n, final double evMin, final double evMax) {
        return rndMatrix(n, evMin, evMax, false);
    }

    /**
     * Create a n times n symmetric random matrix with eigenvalues in the
     * range [evMin,evMax].
     *
     * @param n     the dimension of the matrix
     * @param evMin minimal eigenvalue
     * @param evMax maximal eigenvalue
     * @return symmetric matrix with eigenvalues between evMin and evMax
     */
    @Symmetric
    public double[][] rndSymmetricMatrix(final int n, final double evMin, final double evMax) {
        return rndMatrix(n, evMin, evMax, true);
    }

    /**
     * Create a n times n random positive definite matrix with positive random
     * eigenvalues between default 0.25 and 10 (or the setter values).
     *
     * @param n the dimension of the matrix
     * @return matrix with positive random eigenvalues
     */
    @PositiveDefinite
    public double[][] rndPositiveDefiniteMatrix(final int n) {
        final double emin = Randomizer.rndBox(getPositiveEvMin(), 2 * getPositiveEvMin());
        final double emax = Randomizer.rndBox(2 * getPositiveEvMin(), getPositiveEvMax());
        return rndMatrix(n, emin, emax, false);
    }

    /**
     * Create a n times n symmetric random positive definite matrix with positive random
     * eigenvalues between default 0.25 and 10 (or the setter values).
     *
     * @param n the dimension of the matrix
     * @return matrix with positive random eigenvalues
     */
    @Symmetric
    @PositiveDefinite
    public double[][] rndSymmetricPositiveDefiniteMatrix(final int n) {
        final double emin = Randomizer.rndBox(getPositiveEvMin(), 2 * getPositiveEvMin());
        final double emax = Randomizer.rndBox(2 * getPositiveEvMin(), getPositiveEvMax());
        return rndMatrix(n, emin, emax, true);
    }

    /**
     * Create a n times n random  matrix with random eigenvalues between
     * -5 and 5 (or the setter values).
     *
     * @param n the dimension of the matrix
     * @return matrix with arbitrary random eigenvalues
     */
    public double[][] rndMatrix(final int n) {
        final double emin = Randomizer.rndBox(getEvMin(), getEvMax());
        final double emax = Randomizer.rndBox(getEvMin(), getEvMax());
        return rndMatrix(n, emin, emax, false);
    }

    /**
     * Create a n times n random  matrix with random eigenvalues between
     * -5 and 5 (or the setter values).
     *
     * @param n the dimension of the matrix
     * @return symmetric matrix with arbitrary random eigenvalues
     */
    @Symmetric
    public double[][] rndSymmetricMatrix(final int n) {
        final double emin = Randomizer.rndBox(getEvMin(), getEvMax());
        final double emax = Randomizer.rndBox(getEvMin(), getEvMax());
        return rndMatrix(n, emin, emax, true);
    }
}
 