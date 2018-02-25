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

package de.lab4inf.math.util;

/**
 * The total antisymmetric "Epsilon" Tensor of Levi-Civita.
 *
 * @author nwulff
 * @version $Id: LeviCivita.java,v 1.1 2014/05/11 08:48:43 nwulff Exp $
 * @since 08.05.2014
 */
public final class LeviCivita {
    /**
     * Hidden constructor for utility class without instances.
     */
    private LeviCivita() {
    }

    /**
     * Calculate the Levi-Civita Tensor for three arguments.
     *
     * @param i first index
     * @param j second index
     * @param k third index
     * @return eps(i, j, k)
     */
    public static int leviCivita(final int i, final int j, final int k) {
        return (i - j) * (i - k) * (k - j) / 2;
    }

    /**
     * Calculate the Levi-Civita Tensor for arbitrary number of arguments.
     *
     * @param index array with the indicies
     * @return eps(index)
     */
    public static int leviCivita(final int... index) {
        final int n = index.length;
        if (n == 3) {
            return leviCivita(index[0], index[1], index[2]);
        }
        int p, q, v = 1, e = 1;
        for (q = 0; e != 0 && q < n; q++) {
            for (p = 0; e != 0 && p < q; p++) {
                e *= (index[p] - index[q]);
                v *= (p - q);
            }
        }
        e /= v;
        return e;
    }

}
 