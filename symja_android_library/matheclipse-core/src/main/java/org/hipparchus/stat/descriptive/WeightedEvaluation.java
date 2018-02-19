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
package org.hipparchus.stat.descriptive;

import org.hipparchus.exception.MathIllegalArgumentException;

/**
 * Weighted evaluation for statistics.
 */
public interface WeightedEvaluation {

    /**
     * Returns the result of evaluating the statistic over the input array,
     * using the supplied weights.
     * <p>
     * The default implementation delegates to
     * <code>evaluate(double[], double[], int, int)</code> in the natural way.
     *
     * @param values  input array
     * @param weights array of weights
     * @return the value of the weighted statistic applied to the input array
     * @throws MathIllegalArgumentException if either array is null, lengths
     *                                      do not match, weights contain NaN, negative or infinite values, or
     *                                      weights does not include at least on positive value
     */
    default double evaluate(double[] values, double[] weights) throws MathIllegalArgumentException {
        return evaluate(values, weights, 0, values.length);
    }

    /**
     * Returns the result of evaluating the statistic over the specified entries
     * in the input array, using corresponding entries in the supplied weights array.
     *
     * @param values  the input array
     * @param weights array of weights
     * @param begin   the index of the first element to include
     * @param length  the number of elements to include
     * @return the value of the weighted statistic applied to the included array entries
     * @throws MathIllegalArgumentException if either array is null, lengths
     *                                      do not match, indices are invalid, weights contain NaN, negative or
     *                                      infinite values, or weights does not include at least on positive value
     */
    double evaluate(double[] values, double[] weights, int begin, int length)
            throws MathIllegalArgumentException;

}
