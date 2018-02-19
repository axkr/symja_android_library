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

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.FastMath;

/**
 * Base class for all bracketing <em>Secant</em>-based methods for root-finding
 * (approximating a zero of a univariate real function).
 * <p>
 * <p>Implementation of the {@link RegulaFalsiSolver <em>Regula Falsi</em>} and
 * {@link IllinoisSolver <em>Illinois</em>} methods is based on the
 * following article: M. Dowell and P. Jarratt,
 * <em>A modified regula falsi method for computing the root of an
 * equation</em>, BIT Numerical Mathematics, volume 11, number 2,
 * pages 168-174, Springer, 1971.</p>
 * <p>
 * <p>Implementation of the {@link PegasusSolver <em>Pegasus</em>} method is
 * based on the following article: M. Dowell and P. Jarratt,
 * <em>The "Pegasus" method for computing the root of an equation</em>,
 * BIT Numerical Mathematics, volume 12, number 4, pages 503-508, Springer,
 * 1972.</p>
 * <p>
 * <p>The {@link SecantSolver <em>Secant</em>} method is <em>not</em> a
 * bracketing method, so it is not implemented here. It has a separate
 * implementation.</p>
 */
public abstract class BaseSecantSolver
        extends AbstractUnivariateSolver
        implements BracketedUnivariateSolver<UnivariateFunction> {

    /**
     * Default absolute accuracy.
     */
    protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;
    /**
     * The <em>Secant</em>-based root-finding method to use.
     */
    private final Method method;
    /**
     * The kinds of solutions that the algorithm may accept.
     */
    private AllowedSolution allowed;

    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     * @param method           <em>Secant</em>-based root-finding method to use.
     */
    protected BaseSecantSolver(final double absoluteAccuracy, final Method method) {
        super(absoluteAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param method           <em>Secant</em>-based root-finding method to use.
     */
    protected BaseSecantSolver(final double relativeAccuracy,
                               final double absoluteAccuracy,
                               final Method method) {
        super(relativeAccuracy, absoluteAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy      Maximum relative error.
     * @param absoluteAccuracy      Maximum absolute error.
     * @param functionValueAccuracy Maximum function value error.
     * @param method                <em>Secant</em>-based root-finding method to use
     */
    protected BaseSecantSolver(final double relativeAccuracy,
                               final double absoluteAccuracy,
                               final double functionValueAccuracy,
                               final Method method) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double solve(final int maxEval, final UnivariateFunction f,
                        final double min, final double max,
                        final AllowedSolution allowedSolution) {
        return solve(maxEval, f, min, max, min + 0.5 * (max - min), allowedSolution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double solve(final int maxEval, final UnivariateFunction f,
                        final double min, final double max, final double startValue,
                        final AllowedSolution allowedSolution) {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max, startValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double solve(final int maxEval, final UnivariateFunction f,
                        final double min, final double max, final double startValue) {
        return solve(maxEval, f, min, max, startValue, AllowedSolution.ANY_SIDE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Interval solveInterval(final int maxEval,
                                  final UnivariateFunction f,
                                  final double min,
                                  final double max,
                                  final double startValue) throws MathIllegalArgumentException, MathIllegalStateException {
        setup(maxEval, f, min, max, startValue);
        this.allowed = null;
        return doSolveInterval();
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalStateException if the algorithm failed due to finite
     *                                   precision.
     */
    @Override
    protected final double doSolve() throws MathIllegalStateException {
        return doSolveInterval().getSide(allowed);
    }

    /**
     * Find a root and return the containing interval.
     *
     * @return an interval containing the root such that the selected end point meets the
     * convergence criteria.
     * @throws MathIllegalStateException if convergence fails.
     */
    protected final Interval doSolveInterval() throws MathIllegalStateException {
        // Get initial solution
        double x0 = getMin();
        double x1 = getMax();
        double f0 = computeObjectiveValue(x0);
        double f1 = computeObjectiveValue(x1);

        // If one of the bounds is the exact root, return it. Since these are
        // not under-approximations or over-approximations, we can return them
        // regardless of the allowed solutions.
        if (f0 == 0.0) {
            return new Interval(x0, f0, x0, f0);
        }
        if (f1 == 0.0) {
            return new Interval(x1, f1, x1, f1);
        }

        // Verify bracketing of initial solution.
        verifyBracketing(x0, x1);

        // Get accuracies.
        final double ftol = getFunctionValueAccuracy();
        final double atol = getAbsoluteAccuracy();
        final double rtol = getRelativeAccuracy();

        // Keep track of inverted intervals, meaning that the left bound is
        // larger than the right bound.
        boolean inverted = false;

        // Keep finding better approximations.
        while (true) {
            // Calculate the next approximation.
            final double x = x1 - ((f1 * (x1 - x0)) / (f1 - f0));
            final double fx = computeObjectiveValue(x);

            // If the new approximation is the exact root, return it. Since
            // this is not an under-approximation or an over-approximation,
            // we can return it regardless of the allowed solutions.
            if (fx == 0.0) {
                return new Interval(x, fx, x, fx);
            }

            // Update the bounds with the new approximation.
            if (f1 * fx < 0) {
                // The value of x1 has switched to the other bound, thus inverting
                // the interval.
                x0 = x1;
                f0 = f1;
                inverted = !inverted;
            } else {
                switch (method) {
                    case ILLINOIS:
                        f0 *= 0.5;
                        break;
                    case PEGASUS:
                        f0 *= f1 / (f1 + fx);
                        break;
                    case REGULA_FALSI:
                        // Detect early that algorithm is stuck, instead of waiting
                        // for the maximum number of iterations to be exceeded.
                        if (x == x1) {
                            throw new MathIllegalStateException(LocalizedCoreFormats.CONVERGENCE_FAILED);
                        }
                        break;
                    default:
                        // Should never happen.
                        throw MathRuntimeException.createInternalError();
                }
            }
            // Update from [x0, x1] to [x0, x].
            x1 = x;
            f1 = fx;

            // If the current interval is within the given accuracies, we
            // are satisfied with the current approximation.
            if (FastMath.abs(x1 - x0) < FastMath.max(rtol * FastMath.abs(x1), atol) ||
                    (FastMath.abs(f1) < ftol && (allowed == AllowedSolution.ANY_SIDE ||
                            (inverted && allowed == AllowedSolution.LEFT_SIDE) ||
                            (!inverted && allowed == AllowedSolution.RIGHT_SIDE) ||
                            (f1 <= 0.0 && allowed == AllowedSolution.BELOW_SIDE) ||
                            (f1 >= 0.0 && allowed == AllowedSolution.ABOVE_SIDE)))) {
                if (inverted) {
                    return new Interval(x1, f1, x0, f0);
                } else {
                    return new Interval(x0, f0, x1, f1);
                }
            }
        }
    }

    /**
     * <em>Secant</em>-based root-finding methods.
     */
    protected enum Method {

        /**
         * The {@link RegulaFalsiSolver <em>Regula Falsi</em>} or
         * <em>False Position</em> method.
         */
        REGULA_FALSI,

        /**
         * The {@link IllinoisSolver <em>Illinois</em>} method.
         */
        ILLINOIS,

        /**
         * The {@link PegasusSolver <em>Pegasus</em>} method.
         */
        PEGASUS;

    }
}
