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
import java.util.List;

import de.lab4inf.math.Function;

import static de.lab4inf.math.lapack.LinearAlgebra.diff;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.util.Accuracy.round;
import static de.lab4inf.math.util.Randomizer.rndBox;
import static de.lab4inf.math.util.Randomizer.rndGaussian;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Maximizer using evolutionary strategy.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: EvolutionaryOptimizer.java,v 1.12 2010/09/14 22:12:25 nwulff Exp $
 * @since 14.06.2007
 */
public class EvolutionaryOptimizer extends GenericOptimizer {
    private int mu = 25;
    private int lambda = 100;
    private double range = 10;
    private double sigma = 2;
    private EvolutionaryStrategy strategy = EvolutionaryStrategy.PARENTS_PLUS_CHILDS;
    private List<double[]> parents;
    private List<double[]> population;
    private boolean debug;

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMinimisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMinimisation(final Function fct, final double... guess) {
        setTarget(fct);
        setComparator(new MinComperator());
        return runOptimisation(fct, guess);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMaximisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMaximisation(final Function fct, final double... guess) {
        setTarget(fct);
        setComparator(new MaxComperator());
        return runOptimisation(fct, guess);
    }

    protected double[] statistic(final List<double[]> values) {
        int n = values.size();
        int dim = values.get(0).length;
        double[] sx = new double[dim];
        double[] sx2 = new double[dim];
        double[] ret = new double[2];
        double[] x;
        for (int i = 0; i < n; i++) {
            x = values.get(i);
            for (int j = 0; j < dim; j++) {
                sx[j] += x[j];
                sx2[j] += x[j] * x[j];
            }
        }
        for (int j = 0; j < dim; j++) {
            sx[j] /= n;
            sx2[j] /= n;
            sx2[j] = sqrt(sx2[j] - sx[j] * sx[j]);
        }
        ret[0] = norm(sx);
        ret[1] = norm(sx2);
        return ret;
    }

    private boolean runOptimisation(final Function fct, final double... guess) {
        int iteration = 0;
        assert getMu() >= 1;
        assert getLambda() >= 1;
        assert getLambda() >= getMu();
        setParents(initialParents(guess));
        List<double[]> childs = null;
        double delta = 0;
        double[] winner2, winner1 = findbests(getParents()).get(0);
        double fit1 = fct.f(winner1);
        double fit2, dFit;
        double[] stat = statistic(getParents());
        setPopulation(getParents());
        if (isDebug()) {
            getLogger().info(String.format(
                    "%3d %s r:%5.4f \t\t\t\t\t m:%5.2f sig:%5.2f", iteration,
                    display(winner1), norm(winner1), stat[0], stat[1]));
        }
        informIterationIsFinished(iteration, winner1);
        do {
            iteration++;
            childs = crossover(getParents());
            childs = findbests(childs);
            setPopulation(childs);
            winner2 = childs.get(0);
            delta = diff(winner1, winner2);
            winner1 = winner2;
            fit2 = fct.f(winner1);
            dFit = fit2 - fit1;
            fit1 = fit2;
            stat = statistic(childs);
            if (isDebug()) {
                getLogger().info(String.format(
                        "%3d %s r:%5.4f diff:%5.4f, F:%.4f dF:%.5f, m:%5.2f sig:%5.2f",
                        iteration, display(winner1), norm(winner1), delta,
                        fit1, dFit, stat[0], stat[1]));
            }
            List<double[]> newParents = new ArrayList<double[]>(getMu());
            for (int i = 0; i < getMu(); i++) {
                newParents.add(childs.get(i));
            }
            setParents(newParents);
            informIterationIsFinished(iteration, winner1);
        } while (iteration < getMaxIterations() && delta > getPrecision());
        for (int i = 0; i < guess.length; i++) {
            guess[i] = round(winner1[i], getPrecision());
        }
        informOptimizationIsFinished(iteration, guess);
        return iteration < getMaxIterations();
    }

    /**
     * Create from the list of seed values randomly a list of childs
     * for the next generation. Depending on the EvolutionStrategy
     * also the parents are added to the list.
     *
     * @param seed List with seed values
     * @return List with the childs
     */
    private List<double[]> crossover(final List<double[]> seed) {
        double[] child, parent1, parent2;
        List<double[]> childs = new ArrayList<double[]>();
        for (int i = 0; i < getLambda(); i++) {
            int l = randomParentIndex();
            int k = randomParentIndex();
            parent1 = seed.get(l);
            parent2 = seed.get(k);
            child = childOf(parent1, parent2);
            childs.add(child);
        }
        if (strategy == EvolutionaryStrategy.ONLY_CHILDS) {
            // all parent survive, mu+lambda strategy
            childs.addAll(parents);
        }
        return childs;
    }

    double[] childOf(final double[] p1, final double[] p2) {
        int dimension = p1.length;
        double[] child = new double[dimension];
        double a = 0.25 + 0.5 * rndBox();
        double sig;
        for (int j = 0; j < dimension; j++) {
            sig = abs(p1[j] - p2[j]) / 2;
            child[j] = a * p1[j] + (1 - a) * p2[j];
            child[j] += rndGaussian(0, getSigma() * sig);
        }
        return child;
    }

    int randomParentIndex() {
        double rnd = rndBox(0, getMu());
        int l = (int) rnd;
        return l;
    }

    protected double randomChromosom() {
        return rndBox(-getRange(), getRange());
    }

    List<double[]> initialParents(final double... guess) {
        int dimension = guess.length;
        double[] parent;
        List<double[]> seed = new ArrayList<double[]>(getMu());
        for (int i = 0; i < getMu(); i++) {
            parent = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                parent[j] = randomChromosom();
            }
            seed.add(parent);
        }
        return seed;
    }

    /**
     * Getter for the debug variable.
     *
     * @return boolean
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug variable.
     *
     * @param debug debug to set
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Get the number of parents.
     *
     * @return the mu
     */
    public int getMu() {
        return mu;
    }

    /**
     * Set the number of parent instances.
     *
     * @param mu the number of parents to set
     */
    public void setMu(final int mu) {
        this.mu = mu;
    }

    /**
     * Get the number of childs.
     *
     * @return the number of childs
     */
    public int getLambda() {
        return lambda;
    }

    /**
     * Set the number of childs.
     *
     * @param lambda the number of childs to set
     */
    public void setLambda(final int lambda) {
        this.lambda = lambda;
    }

    /**
     * Get the range value.
     *
     * @return the range
     */
    public double getRange() {
        return range;
    }

    /**
     * Set the range value.
     *
     * @param range the range to set
     */
    public void setRange(final double range) {
        this.range = range;
    }

    /**
     * Get the sigma value.
     *
     * @return the sigma
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * Set the sigma value.
     *
     * @param sigma the sigma to set
     */
    public void setSigma(final double sigma) {
        this.sigma = sigma;
    }

    /**
     * Get the strategy value.
     *
     * @return the strategy
     */
    public EvolutionaryStrategy getStrategy() {
        return strategy;
    }

    /**
     * Set the strategy value.
     *
     * @param strategy the strategy to set
     */
    public void setStrategy(final EvolutionaryStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Get the current population.
     *
     * @return the population
     */
    List<double[]> getPopulation() {
        return population;
    }

    /**
     * Set the current population.
     *
     * @param population to set
     */
    void setPopulation(final List<double[]> population) {
        this.population = population;
    }

    /**
     * Get the parents value.
     *
     * @return the parents
     */
    List<double[]> getParents() {
        return parents;
    }

    /**
     * Set the parents value.
     *
     * @param parents the parents to set
     */
    void setParents(final List<double[]> parents) {
        this.parents = parents;
    }
}
 