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
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.FastMath;

/**
 * Interface for {@link UnivariateSolver (univariate real) root-finding
 * algorithms} that maintain a bracketed solution. There are several advantages
 * to having such root-finding algorithms:
 * <ul>
 * <li>The bracketed solution guarantees that the root is kept within the
 * interval. As such, these algorithms generally also guarantee
 * convergence.</li>
 * <li>The bracketed solution means that we have the opportunity to only
 * return roots that are greater than or equal to the actual root, or
 * are less than or equal to the actual root. That is, we can control
 * whether under-approximations and over-approximations are
 * {@link AllowedSolution allowed solutions}. Other root-finding
 * algorithms can usually only guarantee that the solution (the root that
 * was found) is around the actual root.</li>
 * </ul>
 * <p>
 * <p>For backwards compatibility, all root-finding algorithms must have
 * {@link AllowedSolution#ANY_SIDE ANY_SIDE} as default for the allowed
 * solutions.</p>
 *
 * @param <FUNC> Type of function to solve.
 * @see AllowedSolution
 */
public interface BracketedUnivariateSolver<FUNC extends UnivariateFunction>
        extends BaseUnivariateSolver<FUNC> {

    /**
     * Solve for a zero in the given interval.
     * A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.
     *
     * @param maxEval         Maximum number of evaluations.
     * @param f               Function to solve.
     * @param min             Lower bound for the interval.
     * @param max             Upper bound for the interval.
     * @param allowedSolution The kind of solutions that the root-finding algorithm may
     *                        accept as solutions.
     * @return A value where the function is zero.
     * @throws org.hipparchus.exception.MathIllegalArgumentException if the arguments do not satisfy the requirements specified by the solver.
     * @throws org.hipparchus.exception.MathIllegalStateException    if
     *                                                               the allowed number of evaluations is exceeded.
     */
    double solve(int maxEval, FUNC f, double min, double max,
                 AllowedSolution allowedSolution);

    /**
     * Solve for a zero in the given interval, start at {@code startValue}.
     * A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.
     *
     * @param maxEval         Maximum number of evaluations.
     * @param f               Function to solve.
     * @param min             Lower bound for the interval.
     * @param max             Upper bound for the interval.
     * @param startValue      Start value to use.
     * @param allowedSolution The kind of solutions that the root-finding algorithm may
     *                        accept as solutions.
     * @return A value where the function is zero.
     * @throws org.hipparchus.exception.MathIllegalArgumentException if the arguments do not satisfy the requirements specified by the solver.
     * @throws org.hipparchus.exception.MathIllegalStateException    if
     *                                                               the allowed number of evaluations is exceeded.
     */
    double solve(int maxEval, FUNC f, double min, double max, double startValue,
                 AllowedSolution allowedSolution);

    /**
     * Solve for a zero in the given interval and return a tolerance interval surrounding
     * the root.
     * <p>
     * <p> It is required that the starting interval brackets a root or that the function
     * value at either end point is 0.0.
     *
     * @param maxEval Maximum number of evaluations.
     * @param f       Function to solve.
     * @param min     Lower bound for the interval.
     * @param max     Upper bound for the interval. Must be greater than {@code min}.
     * @return an interval [ta, tb] such that for some t in [ta, tb] f(t) == 0.0 or has a
     * step wise discontinuity that crosses zero. Both end points also satisfy the
     * convergence criteria so either one could be used as the root. That is the interval
     * satisfies the condition (| tb - ta | <= {@link #getAbsoluteAccuracy() absolute}
     * accuracy + max(ta, tb) * {@link #getRelativeAccuracy() relative} accuracy) or (
     * max(|f(ta)|, |f(tb)|) <= {@link #getFunctionValueAccuracy()}) or there are no
     * floating point numbers between ta and tb. The width of the interval (tb - ta) may
     * be zero.
     * @throws MathIllegalArgumentException if the arguments do not satisfy the
     *                                      requirements specified by the solver.
     * @throws MathIllegalStateException    if the allowed number of evaluations is
     *                                      exceeded.
     */
    default Interval solveInterval(int maxEval, FUNC f, double min, double max)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return this.solveInterval(maxEval, f, min, max, min + 0.5 * (max - min));
    }

    /**
     * Solve for a zero in the given interval and return a tolerance interval surrounding
     * the root.
     * <p>
     * <p> It is required that the starting interval brackets a root or that the function
     * value at either end point is 0.0.
     *
     * @param maxEval    Maximum number of evaluations.
     * @param startValue start value to use. Must be in the interval [min, max].
     * @param f          Function to solve.
     * @param min        Lower bound for the interval.
     * @param max        Upper bound for the interval. Must be greater than {@code min}.
     * @return an interval [ta, tb] such that for some t in [ta, tb] f(t) == 0.0 or has a
     * step wise discontinuity that crosses zero. Both end points also satisfy the
     * convergence criteria so either one could be used as the root. That is the interval
     * satisfies the condition (| tb - ta | <= {@link #getAbsoluteAccuracy() absolute}
     * accuracy + max(ta, tb) * {@link #getRelativeAccuracy() relative} accuracy) or (
     * max(|f(ta)|, |f(tb)|) <= {@link #getFunctionValueAccuracy()}) or there are no
     * floating point numbers between ta and tb. The width of the interval (tb - ta) may
     * be zero.
     * @throws MathIllegalArgumentException if the arguments do not satisfy the
     *                                      requirements specified by the solver.
     * @throws MathIllegalStateException    if the allowed number of evaluations is
     *                                      exceeded.
     */
    Interval solveInterval(int maxEval, FUNC f, double min, double max, double startValue)
            throws MathIllegalArgumentException, MathIllegalStateException;

    /**
     * An interval of a function that brackets a root.
     * <p>
     * <p> Contains two end points and the value of the function at the two end points.
     *
     * @see #solveInterval(int, UnivariateFunction, double, double, double)
     */
    class Interval {

        /**
         * Abscissa on the left end of the interval.
         */
        private final double leftAbscissa;
        /**
         * Function value at {@link #leftAbscissa}.
         */
        private final double leftValue;
        /**
         * Abscissa on the right end of the interval, >= {@link #leftAbscissa}.
         */
        private final double rightAbscissa;
        /**
         * Function value at {@link #rightAbscissa}.
         */
        private final double rightValue;

        /**
         * Construct a new interval with the given end points.
         *
         * @param leftAbscissa  is the abscissa value at the left side of the interval.
         * @param leftValue     is the function value at {@code leftAbscissa}.
         * @param rightAbscissa is the abscissa value on the right side of the interval.
         *                      Must be greater than or equal to {@code leftAbscissa}.
         * @param rightValue    is the function value at {@code rightAbscissa}.
         */
        public Interval(final double leftAbscissa,
                        final double leftValue,
                        final double rightAbscissa,
                        final double rightValue) {
            this.leftAbscissa = leftAbscissa;
            this.leftValue = leftValue;
            this.rightAbscissa = rightAbscissa;
            this.rightValue = rightValue;
        }

        /**
         * Get the left abscissa.
         *
         * @return abscissa of the start of the interval.
         */
        public double getLeftAbscissa() {
            return leftAbscissa;
        }

        /**
         * Get the right abscissa.
         *
         * @return abscissa of the end of the interval.
         */
        public double getRightAbscissa() {
            return rightAbscissa;
        }

        /**
         * Get the function value at {@link #getLeftAbscissa()}.
         *
         * @return value of the function at the start of the interval.
         */
        public double getLeftValue() {
            return leftValue;
        }

        /**
         * Get the function value at {@link #getRightAbscissa()}.
         *
         * @return value of the function at the end of the interval.
         */
        public double getRightValue() {
            return rightValue;
        }

        /**
         * Get the abscissa corresponding to the allowed side.
         *
         * @param allowed side of the root.
         * @return the abscissa on the selected side of the root.
         */
        public double getSide(final AllowedSolution allowed) {
            final double xA = this.getLeftAbscissa();
            final double yA = this.getLeftValue();
            final double xB = this.getRightAbscissa();
            switch (allowed) {
                case ANY_SIDE:
                    final double absYA = FastMath.abs(this.getLeftValue());
                    final double absYB = FastMath.abs(this.getRightValue());
                    return absYA < absYB ? xA : xB;
                case LEFT_SIDE:
                    return xA;
                case RIGHT_SIDE:
                    return xB;
                case BELOW_SIDE:
                    return (yA <= 0) ? xA : xB;
                case ABOVE_SIDE:
                    return (yA < 0) ? xB : xA;
                default:
                    // this should never happen
                    throw MathRuntimeException.createInternalError();
            }
        }

    }

}
