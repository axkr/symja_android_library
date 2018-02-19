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

import org.hipparchus.RealFieldElement;
import org.hipparchus.analysis.RealFieldUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;

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
 * @param <T> the type of the field elements
 * @see AllowedSolution
 */
public interface BracketedRealFieldUnivariateSolver<T extends RealFieldElement<T>> {

    /**
     * Get the maximum number of function evaluations.
     *
     * @return the maximum number of function evaluations.
     */
    int getMaxEvaluations();

    /**
     * Get the number of evaluations of the objective function.
     * The number of evaluations corresponds to the last call to the
     * {@code optimize} method. It is 0 if the method has not been
     * called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    int getEvaluations();

    /**
     * Get the absolute accuracy of the solver.  Solutions returned by the
     * solver should be accurate to this tolerance, i.e., if &epsilon; is the
     * absolute accuracy of the solver and {@code v} is a value returned by
     * one of the {@code solve} methods, then a root of the function should
     * exist somewhere in the interval ({@code v} - &epsilon;, {@code v} + &epsilon;).
     *
     * @return the absolute accuracy.
     */
    T getAbsoluteAccuracy();

    /**
     * Get the relative accuracy of the solver.  The contract for relative
     * accuracy is the same as {@link #getAbsoluteAccuracy()}, but using
     * relative, rather than absolute error.  If &rho; is the relative accuracy
     * configured for a solver and {@code v} is a value returned, then a root
     * of the function should exist somewhere in the interval
     * ({@code v} - &rho; {@code v}, {@code v} + &rho; {@code v}).
     *
     * @return the relative accuracy.
     */
    T getRelativeAccuracy();

    /**
     * Get the function value accuracy of the solver.  If {@code v} is
     * a value returned by the solver for a function {@code f},
     * then by contract, {@code |f(v)|} should be less than or equal to
     * the function value accuracy configured for the solver.
     *
     * @return the function value accuracy.
     */
    T getFunctionValueAccuracy();

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
    T solve(int maxEval, RealFieldUnivariateFunction<T> f, T min, T max,
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
    T solve(int maxEval, RealFieldUnivariateFunction<T> f, T min, T max, T startValue,
            AllowedSolution allowedSolution);

    /**
     * Solve for a zero in the given interval and return a tolerance interval surrounding
     * the root.
     * <p>
     * <p> It is required that the starting interval brackets a root.
     *
     * @param maxEval Maximum number of evaluations.
     * @param f       Function to solve.
     * @param min     Lower bound for the interval. f(min) != 0.0.
     * @param max     Upper bound for the interval. f(max) != 0.0.
     * @return an interval [ta, tb] such that for some t in [ta, tb] f(t) == 0.0 or has a
     * step wise discontinuity that crosses zero. Both end points also satisfy the
     * convergence criteria so either one could be used as the root. That is the interval
     * satisfies the condition (| tb - ta | <= {@link #getAbsoluteAccuracy() absolute}
     * accuracy + max(ta, tb) * {@link #getRelativeAccuracy() relative} accuracy) or (
     * max(|f(ta)|, |f(tb)|) <= {@link #getFunctionValueAccuracy()}) or there are no
     * numbers in the field between ta and tb. The width of the interval (tb - ta) may be
     * zero.
     * @throws MathIllegalArgumentException if the arguments do not satisfy the
     *                                      requirements specified by the solver.
     * @throws MathIllegalStateException    if the allowed number of evaluations is
     *                                      exceeded.
     */
    default Interval<T> solveInterval(int maxEval,
                                      RealFieldUnivariateFunction<T> f,
                                      T min,
                                      T max)
            throws MathIllegalArgumentException, MathIllegalStateException {
        return this.solveInterval(maxEval, f, min, max, min.add(max.subtract(min).multiply(0.5)));
    }

    /**
     * Solve for a zero in the given interval and return a tolerance interval surrounding
     * the root.
     * <p>
     * <p> It is required that the starting interval brackets a root.
     *
     * @param maxEval    Maximum number of evaluations.
     * @param startValue start value to use.
     * @param f          Function to solve.
     * @param min        Lower bound for the interval. f(min) != 0.0.
     * @param max        Upper bound for the interval. f(max) != 0.0.
     * @return an interval [ta, tb] such that for some t in [ta, tb] f(t) == 0.0 or has a
     * step wise discontinuity that crosses zero. Both end points also satisfy the
     * convergence criteria so either one could be used as the root. That is the interval
     * satisfies the condition (| tb - ta | <= {@link #getAbsoluteAccuracy() absolute}
     * accuracy + max(ta, tb) * {@link #getRelativeAccuracy() relative} accuracy) or (
     * max(|f(ta)|, |f(tb)|) <= {@link #getFunctionValueAccuracy()}) or numbers in the
     * field between ta and tb. The width of the interval (tb - ta) may be zero.
     * @throws MathIllegalArgumentException if the arguments do not satisfy the
     *                                      requirements specified by the solver.
     * @throws MathIllegalStateException    if the allowed number of evaluations is
     *                                      exceeded.
     */
    Interval<T> solveInterval(int maxEval,
                              RealFieldUnivariateFunction<T> f,
                              T min,
                              T max,
                              T startValue)
            throws MathIllegalArgumentException, MathIllegalStateException;

    /**
     * An interval of a function that brackets a root.
     * <p>
     * Contains two end points and the value of the function at the two end points.
     *
     * @param <T> the element type
     * @see #solveInterval(int, RealFieldUnivariateFunction, RealFieldElement,
     * RealFieldElement)
     */
    class Interval<T extends RealFieldElement<T>> {

        /**
         * Abscissa on the left end of the interval.
         */
        private final T leftAbscissa;
        /**
         * Function value at {@link #leftAbscissa}.
         */
        private final T leftValue;
        /**
         * Abscissa on the right end of the interval, >= {@link #leftAbscissa}.
         */
        private final T rightAbscissa;
        /**
         * Function value at {@link #rightAbscissa}.
         */
        private final T rightValue;

        /**
         * Construct a new interval with the given end points.
         *
         * @param leftAbscissa  is the abscissa value at the left side of the interval.
         * @param leftValue     is the function value at {@code leftAbscissa}.
         * @param rightAbscissa is the abscissa value on the right side of the interval.
         *                      Must be greater than or equal to {@code leftAbscissa}.
         * @param rightValue    is the function value at {@code rightAbscissa}.
         */
        public Interval(final T leftAbscissa,
                        final T leftValue,
                        final T rightAbscissa,
                        final T rightValue) {
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
        public T getLeftAbscissa() {
            return leftAbscissa;
        }

        /**
         * Get the right abscissa.
         *
         * @return abscissa of the end of the interval.
         */
        public T getRightAbscissa() {
            return rightAbscissa;
        }

        /**
         * Get the function value at {@link #getLeftAbscissa()}.
         *
         * @return value of the function at the start of the interval.
         */
        public T getLeftValue() {
            return leftValue;
        }

        /**
         * Get the function value at {@link #getRightAbscissa()}.
         *
         * @return value of the function at the end of the interval.
         */
        public T getRightValue() {
            return rightValue;
        }

        /**
         * Get the abscissa corresponding to the allowed side.
         *
         * @param allowed side of the root.
         * @return the abscissa on the selected side of the root.
         */
        public T getSide(final AllowedSolution allowed) {
            final T xA = this.getLeftAbscissa();
            final T yA = this.getLeftValue();
            final T xB = this.getRightAbscissa();
            switch (allowed) {
                case ANY_SIDE:
                    final T absYA = this.getLeftValue().abs();
                    final T absYB = this.getRightValue().abs();
                    return absYA.subtract(absYB).getReal() < 0 ? xA : xB;
                case LEFT_SIDE:
                    return xA;
                case RIGHT_SIDE:
                    return xB;
                case BELOW_SIDE:
                    return (yA.getReal() <= 0) ? xA : xB;
                case ABOVE_SIDE:
                    return (yA.getReal() < 0) ? xB : xA;
                default:
                    // this should never happen
                    throw MathRuntimeException.createInternalError();
            }
        }

    }

}
