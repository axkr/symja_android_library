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

package org.hipparchus.analysis.solvers;

import org.hipparchus.analysis.differentiation.DSFactory;
import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.exception.MathIllegalStateException;

/**
 * Provide a default implementation for several functions useful to generic
 * solvers.
 */
public abstract class AbstractUnivariateDifferentiableSolver
        extends BaseAbstractUnivariateSolver<UnivariateDifferentiableFunction>
        implements UnivariateDifferentiableSolver {

    /**
     * Factory for DerivativeStructure instances.
     */
    private final DSFactory factory;
    /**
     * Function to solve.
     */
    private UnivariateDifferentiableFunction function;

    /**
     * Construct a solver with given absolute accuracy.
     *
     * @param absoluteAccuracy Maximum absolute error.
     */
    protected AbstractUnivariateDifferentiableSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
        factory = new DSFactory(1, 1);
    }

    /**
     * Construct a solver with given accuracies.
     *
     * @param relativeAccuracy      Maximum relative error.
     * @param absoluteAccuracy      Maximum absolute error.
     * @param functionValueAccuracy Maximum function value error.
     */
    protected AbstractUnivariateDifferentiableSolver(final double relativeAccuracy,
                                                     final double absoluteAccuracy,
                                                     final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        factory = new DSFactory(1, 1);
    }

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value and derivative at specified point.
     * @throws MathIllegalStateException if the maximal number of evaluations is exceeded.
     */
    protected DerivativeStructure computeObjectiveValueAndDerivative(double point)
            throws MathIllegalStateException {
        incrementEvaluationCount();
        return function.value(factory.variable(0, point));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup(int maxEval, UnivariateDifferentiableFunction f,
                         double min, double max, double startValue) {
        super.setup(maxEval, f, min, max, startValue);
        function = f;
    }
}
