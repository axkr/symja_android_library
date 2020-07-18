/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.lvq;

import java.util.List;

/**
 * Listener of a vector quantizer are informed
 * if an iteration or the optimization is finished.
 *
 * @param <V> the type of the vector quantizer (and listener)
 * @author nwulff
 * @version $Id: VQListener.java,v 1.3 2010/02/25 15:31:55 nwulff Exp $
 * @since 28.04.2009
 */
public interface VQListener<V> {
    /**
     * Signal the listener that one iteration has
     * finished with the given actual code book.
     *
     * @param n        the current iteration counter
     * @param codebook the actual iteration code book
     */
    void iterationFinished(int n, List<V> codebook);

    /**
     * Signal the listener that the optimization has
     * finished with the given final code book.
     *
     * @param n        the final number of iterations
     * @param codebook the final code book found
     */
    void optimizationFinished(int n, List<V> codebook);
}
 