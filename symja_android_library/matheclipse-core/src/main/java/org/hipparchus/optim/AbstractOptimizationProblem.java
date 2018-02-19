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

import org.hipparchus.util.Incrementor;

/**
 * Base class for implementing optimization problems. It contains the boiler-plate code
 * for counting the number of evaluations of the objective function and the number of
 * iterations of the algorithm, and storing the convergence checker.
 *
 * @param <PAIR> Type of the point/value pair returned by the optimization algorithm.
 */
public abstract class AbstractOptimizationProblem<PAIR>
        implements OptimizationProblem<PAIR> {

    /**
     * max evaluations
     */
    private final int maxEvaluations;
    /**
     * max iterations
     */
    private final int maxIterations;
    /**
     * Convergence checker.
     */
    private final ConvergenceChecker<PAIR> checker;

    /**
     * Create an {@link AbstractOptimizationProblem} from the given data.
     *
     * @param maxEvaluations the number of allowed model function evaluations.
     * @param maxIterations  the number of allowed iterations.
     * @param checker        the convergence checker.
     */
    protected AbstractOptimizationProblem(final int maxEvaluations,
                                          final int maxIterations,
                                          final ConvergenceChecker<PAIR> checker) {
        this.maxEvaluations = maxEvaluations;
        this.maxIterations = maxIterations;
        this.checker = checker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Incrementor getEvaluationCounter() {
        return new Incrementor(this.maxEvaluations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Incrementor getIterationCounter() {
        return new Incrementor(this.maxIterations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConvergenceChecker<PAIR> getConvergenceChecker() {
        return checker;
    }
}
