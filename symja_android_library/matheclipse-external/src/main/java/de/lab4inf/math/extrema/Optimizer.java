/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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
package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;

/**
 * Interface to find the minimal or maximal values
 * of a real valued function.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Optimizer.java,v 1.3 2010/02/25 15:31:54 nwulff Exp $
 * @since 14.06.2007
 */
public interface Optimizer {
    /**
     * Indicate if optimizing is asynchronous.
     *
     * @return boolean signaling if running asynchronous
     */
    boolean isRunningAsynchron();

    /**
     * Inform the optimizer to run asynchronous or not.
     *
     * @param mode boolean signal to run asynchronous
     */
    void setRunningAsynchron(boolean mode);

    /**
     * Register a listener.
     *
     * @param listener OptimizerListener to add
     */
    void addListener(OptimizerListener listener);

    /**
     * Deregister a listener.
     *
     * @param listener OptimizerListener to remove
     */
    void removeListener(OptimizerListener listener);

    /**
     * Get the maximal number of iterations.
     *
     * @return int the maximal number of iterations.
     */
    int getMaxIterations();

    /**
     * Set the maximal number of iterations.
     *
     * @param ite int the maximal iterations.
     */
    void setMaxIterations(final int ite);

    /**
     * Get the precision used in calculation.
     *
     * @return double precision.
     */
    double getPrecision();

    /**
     * Set the precision for the calculations.
     *
     * @param eps double the precision
     */
    void setPrecision(final double eps);

    /**
     * Find the maximum of a function. The guess vector is
     * after optimization accurate to the the given precision.
     *
     * @param objective function to maximize
     * @param guess     double[]   start vector and or final result
     * @return boolean   indication if optimization has succeeded
     */
    boolean maximize(final Function objective, final double... guess);

    /**
     * Find the minimum of a function. The guess vector is
     * after optimization accurate to the the given precision.
     *
     * @param objective function to minimize
     * @param guess     double... start vector and or final result
     * @return boolean indication if optimization has succeeded
     */
    boolean minimize(final Function objective, final double... guess);

    /**
     * The target/objective function to optimize.
     *
     * @return the objective
     */
    Function getTarget();
}
 