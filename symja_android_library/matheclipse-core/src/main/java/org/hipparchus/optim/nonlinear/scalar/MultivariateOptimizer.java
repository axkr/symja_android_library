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
package org.hipparchus.optim.nonlinear.scalar;

import org.hipparchus.analysis.MultivariateFunction;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.optim.BaseMultivariateOptimizer;
import org.hipparchus.optim.ConvergenceChecker;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;

/**
 * Base class for a multivariate scalar function optimizer.
 */
public abstract class MultivariateOptimizer
        extends BaseMultivariateOptimizer<PointValuePair> {
    /**
     * Objective function.
     */
    private MultivariateFunction function;
    /**
     * Type of optimization.
     */
    private GoalType goal;

    /**
     * @param checker Convergence checker.
     */
    protected MultivariateOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    /**
     * {@inheritDoc}
     *
     * @param optData Optimization data. In addition to those documented in
     *                {@link BaseMultivariateOptimizer#parseOptimizationData(OptimizationData[])
     *                BaseMultivariateOptimizer}, this method will register the following data:
     *                <ul>
     *                <li>{@link ObjectiveFunction}</li>
     *                <li>{@link GoalType}</li>
     *                </ul>
     * @return {@inheritDoc}
     * @throws MathIllegalStateException if the maximal number of
     *                                   evaluations is exceeded.
     */
    @Override
    public PointValuePair optimize(OptimizationData... optData)
            throws MathIllegalStateException {
        // Set up base class and perform computation.
        return super.optimize(optData);
    }

    /**
     * Scans the list of (required and optional) optimization data that
     * characterize the problem.
     *
     * @param optData Optimization data.
     *                The following data will be looked for:
     *                <ul>
     *                <li>{@link ObjectiveFunction}</li>
     *                <li>{@link GoalType}</li>
     *                </ul>
     */
    @Override
    protected void parseOptimizationData(OptimizationData... optData) {
        // Allow base class to register its own data.
        super.parseOptimizationData(optData);

        // The existing values (as set by the previous call) are reused if
        // not provided in the argument list.
        for (OptimizationData data : optData) {
            if (data instanceof GoalType) {
                goal = (GoalType) data;
                continue;
            }
            if (data instanceof ObjectiveFunction) {
                function = ((ObjectiveFunction) data).getObjectiveFunction();
                continue;
            }
        }
    }

    /**
     * @return the optimization type.
     */
    public GoalType getGoalType() {
        return goal;
    }

    /**
     * Computes the objective function value.
     * This method <em>must</em> be called by subclasses to enforce the
     * evaluation counter limit.
     *
     * @param params Point at which the objective function must be evaluated.
     * @return the objective function value at the specified point.
     * @throws MathIllegalStateException if the maximal number of
     *                                   evaluations is exceeded.
     */
    public double computeObjectiveValue(double[] params) {
        super.incrementEvaluationCount();
        return function.value(params);
    }
}
