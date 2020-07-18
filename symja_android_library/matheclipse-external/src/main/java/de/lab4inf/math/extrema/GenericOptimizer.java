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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Solver;
import de.lab4inf.math.differentiation.Gradient;
import de.lab4inf.math.differentiation.GradientApproximator;
import de.lab4inf.math.differentiation.Hessian;
import de.lab4inf.math.differentiation.HessianApproximator;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.Aitken;

import static java.lang.Math.signum;
import static java.lang.String.format;

/**
 * Abstract base implementation of an optimizer.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GenericOptimizer.java,v 1.27 2014/09/03 13:17:01 nwulff Exp $
 * @since 16.06.2007
 */
public abstract class GenericOptimizer extends L4MObject implements Optimizer {
    private static volatile int threadCounter = 0;
    protected Solver laSolver;
    private Collection<OptimizerListener> listeners;
    private Comparator<double[]> comparator;
    private double precision = 1.E-4;
    private int maxIterations = 100;
    private boolean runAsynchron;
    private Function target;

    /**
     * Sole constructor.
     */
    public GenericOptimizer() {
        laSolver = resolve(Solver.class);
        assert null != laSolver : "no linear algebra solver found";
        listeners = Collections.synchronizedCollection(new ArrayList<OptimizerListener>());
    }

    /**
     * Increment the internal thread counter.
     *
     * @return actual threads started
     */
    private static int incrementThreadCounter() {
        return ++threadCounter;
    }

    /**
     * Return the Gradient for this function.
     * This implementation uses an approximation, derived implementations
     * should provide an analytical gradient if possible.
     *
     * @param fct to use
     * @return Gradient grad(f)
     */
    protected Gradient getGradient(final Function fct) {
        return new GradientApproximator(fct);
    }

    /**
     * Return the Hessian for this function.
     * This implementation uses an approximation, derived implementations
     * should provide an analytical Hessian if possible.
     *
     * @param fct to use
     * @return Hessian hess(f)
     */
    protected Hessian getHessian(final Function fct) {
        return new HessianApproximator(fct);
    }

    /**
     * Get the target function to optimize.
     *
     * @return the target
     */
    public final Function getTarget() {
        return target;
    }

    /**
     * Set the target function to optimize.
     *
     * @param target to optimize
     */
    protected final void setTarget(final Function target) {
        this.target = target;
    }

    /**
     * Get the asynchron flag value
     *
     * @return the asynchron flag
     */
    public final boolean isRunningAsynchron() {
        return runAsynchron;
    }

    /**
     * Signal to run asynchron in own thread.
     *
     * @param mode if try run asynchron
     */
    public final void setRunningAsynchron(final boolean mode) {
        runAsynchron = mode;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.extrema.Optimizer#addListener(de.lab4inf.extrema.OptimizerListener)
     */
    public final void addListener(final OptimizerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.extrema.Optimizer#removeListener(de.lab4inf.extrema.OptimizerListener)
     */
    public final void removeListener(final OptimizerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Inform all listeners that the iteration has finished.
     *
     * @param iteration   the counter
     * @param actualguess of iteration
     */
    protected final void informIterationIsFinished(final int iteration,
                                                   final double... actualguess) {
        synchronized (listeners) {
            for (OptimizerListener l : listeners) {
                l.iterationFinished(iteration, actualguess);
            }
        }
    }

    /**
     * Inform all listeners that the optimization has finished.
     *
     * @param iteration final counter value
     * @param bestguess of iteration after finish
     */
    protected final void informOptimizationIsFinished(final int iteration,
                                                      final double... bestguess) {
        synchronized (listeners) {
            for (OptimizerListener l : listeners) {
                l.optimizationFinished(iteration, bestguess);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.extrema.Optimizer#minimize(de.lab4inf.extrema.Computable,
     *      double[])
     */
    public final boolean minimize(final Function fct, final double... guess) {
        if (isRunningAsynchron()) {
            return startMinimize(fct, guess);
        }
        return runMinimisation(fct, guess);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.extrema.Optimizer#maximize(de.lab4inf.extrema.Computable, double[])
     */
    public final boolean maximize(final Function fct, final double... guess) {
        if (isRunningAsynchron()) {
            return startMaximize(fct, guess);
        }
        return runMaximisation(fct, guess);
    }

    /**
     * Run the maximization.
     *
     * @param fct   function to maximize
     * @param guess the initial starting guess for the solution
     * @return flag if maximization already finished
     */
    protected abstract boolean runMaximisation(Function fct, double... guess);

    /**
     * Run the minimization.
     *
     * @param fct   function to minimize
     * @param guess the initial starting guess for the solution
     * @return flag if minimization already finished
     */
    protected abstract boolean runMinimisation(Function fct, double... guess);

    /**
     * Start the asynchron minimization within an external thread.
     *
     * @param fct   to minimize
     * @param guess initial guess of the solution
     * @return boolean flag if minimization already finished, which is false
     * for an asynchron run.
     */
    protected final boolean startMinimize(final Function fct, final double... guess) {
        Thread t = new WorkerThread(true, format("Minimizer-%d",
                incrementThreadCounter()), fct, guess);
        t.start();
        return false;
    }

    /**
     * Start the asynchron maximization within an external thread.
     *
     * @param fct   to maximize
     * @param guess initial guess of the solution
     * @return boolean flag if maximization already finished, which is false
     * for an asynchron run
     */
    protected final boolean startMaximize(final Function fct, final double... guess) {
        Thread t = new WorkerThread(false, format("Maximizer-%d",
                incrementThreadCounter()), fct, guess);
        t.start();
        return false;
    }

    /**
     * Aitken delta method acceleration of a convergent series.
     *
     * @param x double[] x the vector
     * @param a Aitken[] array of accelerators
     * @return double[] the Aitken guess
     */
    @SuppressWarnings("rawtypes")
    protected final double[] aitkenAccelerate(final double[] x, final Aitken[] a) {
        int n = x.length;
        assert n == a.length : "length missmatch";
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = a[i].next(x[i]);
        }
        return y;
    }

    /**
     * Sort the guesses list according to the fitness comparator.
     *
     * @param guesses List with guesses
     * @return List sorted for fitness
     */
    protected final List<double[]> findbests(final List<double[]> guesses) {
        Collections.sort(guesses, getComparator());
        return guesses;
    }

    /**
     * Get the comparator value.
     *
     * @return the comparator
     */
    public final Comparator<double[]> getComparator() {
        return comparator;
    }

    /**
     * Set the comparator value.
     *
     * @param comparator the comparator to set
     */
    public final void setComparator(final Comparator<double[]> comparator) {
        this.comparator = comparator;
    }

    protected String display(final double... x) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < x.length; i++) {
            s.append(format("x[%d]:%+7.4f ", i, x[i]));
        }
        return s.toString();
    }

    /**
     * Get the precision value.
     *
     * @return the precision
     */
    public final double getPrecision() {
        return precision;
    }

    /**
     * Set the precision value.
     *
     * @param precision the precision to set
     */
    public final void setPrecision(final double precision) {
        this.precision = precision;
    }

    /**
     * Get the maxIterations value.
     *
     * @return the maxIterations
     */
    public final int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Set the maxIterations value.
     *
     * @param maxIterations the maxIterations to set
     */
    public final void setMaxIterations(final int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Internal minimizer function decorator.
     */
    protected static class MinimizerFct implements Function {
        private final Function fct;

        public MinimizerFct(final Function fct) {
            this.fct = fct;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.extrema.Computable#f(double[])
         */
        public double f(final double... x) {
            return -fct.f(x);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Internal worker thread for asynchron optimization.
     */
    private class WorkerThread extends Thread {
        private final boolean minimize;
        private final Function fct;
        private final double[] guess;

        WorkerThread(final boolean minimize, final String name,
                     final Function fct, final double... guess) {
            super(name);
            this.minimize = minimize;
            this.fct = fct;
            this.guess = guess;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            if (getLogger().isInfoEnabled()) {
                getLogger().info("asynchron optimization start of " + fct);
            }
            if (minimize) {
                runMinimisation(fct, guess);
            } else {
                runMaximisation(fct, guess);
            }
            if (getLogger().isInfoEnabled()) {
                getLogger().info("asynchron optimization finished");
            }
        }
    }

    /**
     * Maximum comparator decorating function.
     */
    protected class MaxComperator implements Comparator<double[]> {
        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final double[] x, final double[] y) {
            return (int) signum(getTarget().f(y) - getTarget().f(x));
        }
    }

    /**
     * Minimum comparator decorating function.
     */
    protected class MinComperator implements Comparator<double[]> {
        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final double[] x, final double[] y) {
            return (int) signum(getTarget().f(x) - getTarget().f(y));
        }
    }
}
 