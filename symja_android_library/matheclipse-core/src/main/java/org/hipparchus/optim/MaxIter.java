/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.optim;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;

/**
 * Maximum number of iterations performed by an (iterative) algorithm.
 */
public class MaxIter implements OptimizationData {
    /**
     * Allowed number of evalutations.
     */
    private final int maxIter;

    /**
     * @param max Allowed number of iterations.
     * @throws MathIllegalArgumentException if {@code max <= 0}.
     */
    public MaxIter(int max) {
        if (max <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED,
                    max, 0);
        }

        maxIter = max;
    }

    /**
     * Factory method that creates instance of this class that represents
     * a virtually unlimited number of iterations.
     *
     * @return a new instance suitable for allowing {@link Integer#MAX_VALUE}
     * evaluations.
     */
    public static MaxIter unlimited() {
        return new MaxIter(Integer.MAX_VALUE);
    }

    /**
     * Gets the maximum number of evaluations.
     *
     * @return the allowed number of evaluations.
     */
    public int getMaxIter() {
        return maxIter;
    }
}
