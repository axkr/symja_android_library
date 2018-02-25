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

/**
 * Eigenvalue decomposition of a NxN dimensional square matrix A into the N
 * eigenvectors v and eigenvalues d. For these eigenvectors and eigenvalues the
 * equation:
 * <pre>
 *
 *         A*v = d*v
 *
 * </pre
 * should hold, and the matrix A is decomposed as:
 * <pre>
 *
 *         A =  Vt*D*V
 *
 * </pre
 * where V is the NxN matrix of eigenvectors v[j] = 1...n, and D is a diagonal
 * matrix trace{d[1],...,d[n]} of the eigenvalues d[j].
 *
 *
 * @author nwulff
 * @since 07.12.2014
 * @version $Id: EigenValueDecomposition.java,v 1.2 2015/02/11 13:52:17 nwulff Exp $
 */
public interface EigenValueDecomposition {
    /**
     * Return the (positive!) eigenvalues of matrix A in downward order.
     *
     * @return all eigenvalues of A
     */
    double[] eigenvalues();

    /**
     * Return the (normalized!) eigenvalues of matrix A order the same way
     * as the eigenvalues. I.e. if d[j] is an eigenvalue of A than v[j] is
     * the corresponding eigenvector with A*v[j] = d[j]*v[j].
     *
     * @return an array of eigenvectors
     */
    double[][] eigenvectors();
}
 