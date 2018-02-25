/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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

package de.lab4inf.math.statistic;

import java.io.Serializable;
import java.util.Arrays;

import de.lab4inf.math.L4MObject;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

/**
 * Utility class to calculate mean, variance and correlation
 * for a N-dimensional distribution.
 *
 * @author nwulff
 * @version $Id: DataCollectorND.java,v 1.10 2014/12/12 17:26:39 nwulff Exp $
 * @since 14.08.2009
 */
public class DataCollectorND extends L4MObject implements Serializable {
    private static final String NEGATIV_WEIGHT = "negativ weight %f";
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -996599382194768141L;
    private final int dim;
    private String name;
    private double[] sumX;
    private double[] means;
    private double[] minX;
    private double[] maxX;
    private double[][] covar, sumXY;
    private double sumW;
    private long entries;

    /**
     * Constructor for a N-dimensional statistic collector.
     *
     * @param n dimension of the distribution.
     */
    public DataCollectorND(final int n) {
        this(String.format("statistic-%dD", n), n);
    }

    /**
     * Constructor for a named N-dimensional statistic.
     *
     * @param name of the statistic
     * @param n    dimension of the distribution.
     */
    public DataCollectorND(final String name, final int n) {
        dim = n;
        this.name = name;
        init();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode() ^ ((Arrays.hashCode(means))) ^ ((2 * Arrays.hashCode(maxX))) ^ ((3 * Arrays.hashCode(minX)))
                ^ ((5 * Arrays.deepHashCode(covar)));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (null == o || o.getClass() != this.getClass()) {
            return false;
        }
        DataCollectorND obj = (DataCollectorND) o;
        return name.equals(obj.name) && Arrays.equals(means, obj.means) && Arrays.equals(minX, obj.minX)
                && Arrays.equals(maxX, obj.maxX) && Arrays.deepEquals(covar, obj.covar);
    }

    /**
     * Clear the collector.
     */
    public void init() {
        sumW = 0;
        entries = 0;
        sumX = new double[dim];
        means = new double[dim];
        minX = new double[dim];
        maxX = new double[dim];
        sumXY = new double[dim][dim];
        covar = new double[dim][dim];
        for (int j = 0; j < dim; j++) {
            minX[j] = Double.MAX_VALUE;
            maxX[j] = -Double.MAX_VALUE;
        }
    }

    /**
     * Collect the n-dimensional x values, optional with weight
     * at position n.
     *
     * @param x the values(x_0,...x_n,[weight])
     */
    public void collectNaive(final double... x) {
        double xj, yk, w = 1;
        if (x.length == dim + 1) {
            w = x[dim];
        }
        if (w <= 0) {
            logger.error(String.format(NEGATIV_WEIGHT, w));
            return;
        }
        entries++;
        sumW += w;
        for (int j = 0; j < dim; j++) {
            xj = x[j];
            sumX[j] += w * xj;
            minX[j] = min(minX[j], xj);
            maxX[j] = max(maxX[j], xj);
            for (int k = 0; k < dim; k++) {
                yk = x[k];
                sumXY[j][k] += w * xj * yk;
            }
            means[j] = sumX[j] / sumW;
        }
        for (int j = 0; j < dim; j++) {
            for (int k = 0; k <= j; k++) {
                covar[j][k] = sumXY[j][k] / sumW - means[j] * means[k];
                covar[k][j] = covar[j][k];
            }
        }

    }

    /**
     * Collect the n-dimensional x values, optional with weight
     * at position n.
     *
     * @param x the values(x_0,...x_n,[weight])
     */
    public void collect(final double... x) {
        double xj, yk, mx, my, dx, dy, r, s, w = 1;
        if (x.length == dim + 1) {
            w = x[dim];
        }
        entries++;
        if (w <= 0) {
            logger.error(String.format(NEGATIV_WEIGHT, w));
            return;
        }
        s = sumW;
        sumW += w;
        r = w / sumW;
        s /= sumW;
        for (int j = 0; j < dim; j++) {
            xj = x[j];
            mx = means[j];
            dx = xj - mx;
            minX[j] = min(minX[j], xj);
            maxX[j] = max(maxX[j], xj);
            for (int k = dim - 1; k >= j; k--) {
                yk = x[k];
                my = means[k];
                dy = yk - my;
                covar[j][k] = s * covar[j][k] + (s + r - 1) * mx * my + r * (1 - r) * dx * dy;
                covar[k][j] = covar[j][k];
                sumXY[j][k] += w * xj * yk;
                sumXY[k][j] = sumXY[j][k];
            }
            means[j] += r * dx;
        }
    }

    /**
     * Get the number of collected x tuples.
     *
     * @return the number of entries
     */
    public long getEntries() {
        return entries;
    }

    /**
     * Get the weighted number of collected x tuples.
     *
     * @return the sum of weights
     */
    public double getSumWeights() {
        return sumW;
    }

    /**
     * Get the minimal collected x value for index j.
     *
     * @param j the index of the
     * @return min x[j]
     */
    public double getMin(final int j) {
        return minX[j];
    }

    /**
     * Get the maximal collected x value for index j.
     *
     * @param j the index
     * @return max x[j]
     */
    public double getMax(final int j) {
        return maxX[j];
    }

    /**
     * Get the mean of the collected x values for index j.
     *
     * @param j the index
     * @return mean x[j]
     */
    public double getMean(final int j) {
        return means[j];
    }

    /**
     * Get the mean of the collected x*y values for index i and j.
     *
     * @param i the 1.st index
     * @param j the 2.nd index
     * @return mean x[i]*x[j]
     */
    public double getMean(final int i, final int j) {
        return sumXY[i][j] / sumW;
    }

    /**
     * Get the deviation of the collectoed x values for index j.
     *
     * @param j the index
     * @return sqrt(var(x[j]))
     */
    public double getSigma(final int j) {
        return Math.sqrt(covar[j][j]);
    }

    /**
     * Get the covariance of the collected x tuples between index j and k.
     *
     * @param j first index
     * @param k second index
     * @return the covariance between j and k data
     */
    public double getCovar(final int j, final int k) {
        return covar[j][k];
    }

    /**
     * Get the correlation of the collected x tuples between index j and k.
     *
     * @param j first index
     * @param k second index
     * @return the correlation between j and k data
     */
    public double getCorrelation(final int j, final int k) {
        double vj = covar[j][j];
        double vk = covar[k][k];
        if (vj * vk > 0) {
            return covar[j][k] / sqrt(vj * vk);
        }
        return 0;
    }

    /**
     * Access to the calculated covariance matrix.
     *
     * @return the internal calculated covariance matrix
     */
    public double[][] getCovariance() {
        return covar;
    }

    /**
     * Get the value of the name attribute.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of the name attribute.
     *
     * @param name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
}
 