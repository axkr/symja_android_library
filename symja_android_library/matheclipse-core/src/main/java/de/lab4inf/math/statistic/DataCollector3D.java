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

/**
 * Utility class to calculate mean and variance for a
 * three dimensional distribution.
 *
 * @author nwulff
 * @version $Id: DataCollector3D.java,v 1.9 2013/02/15 21:10:54 nwulff Exp $
 * @since 06.06.2009
 */
public class DataCollector3D extends DataCollectorND {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -5889078209702924189L;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    /**
     * Default java bean constructor.
     */
    public DataCollector3D() {
        this("statistic-3D");
    }

    /**
     * Constructor for a named collector.
     *
     * @param name of the collector
     */
    public DataCollector3D(final String name) {
        super(name, 3);
    }

    /**
     * Utility method to copy 3D-matrix x.
     *
     * @param x array to copy
     * @return deep copy of x
     */
    public double[][][] copy(final double[][][] x) {
        if (null == x) {
            return new double[0][0][0];
        }
        int k, m, n = x.length;
        double[][][] y = new double[n][][];
        for (int i = 0; i < n; i++) {
            m = x[i].length;
            y[i] = new double[m][];
            for (int j = 0; j < m; j++) {
                k = x[i][j].length;
                y[i][j] = new double[k];
                System.arraycopy(x[i][j], 0, y[i][j], 0, k);
            }
        }
        return y;
    }

    /**
     * Get the minimal collected z value.
     *
     * @return min z
     */
    public double getMinZ() {
        return getMin(Z);
    }

    /**
     * Get the maximal collected z value.
     *
     * @return max z
     */
    public double getMaxZ() {
        return getMax(Z);
    }

    /**
     * Get the sum of the sampled x values.
     *
     * @return sum x
     */
    public double getSumX() {
        return getSumWeights() * getMeanX();
    }

    /**
     * Get the sum of the sampled y values.
     *
     * @return sum y
     */
    public double getSumY() {
        return getSumWeights() * getMeanY();
    }

    /**
     * Get the sum of the sampled z values.
     *
     * @return sum z
     */
    public double getSumZ() {
        return getSumWeights() * getMeanZ();
    }

    /**
     * Get the max X of the collected values.
     *
     * @return max X
     */
    public double getMaxX() {
        return getMax(X);
    }

    /**
     * Get the min X of the collected values.
     *
     * @return min X
     */
    public double getMinX() {
        return getMin(X);
    }

    /**
     * Get the max Y of the collected values.
     *
     * @return max Y
     */
    public double getMaxY() {
        return getMax(Y);
    }

    /**
     * Get the min Y of the collected values.
     *
     * @return min Y
     */
    public double getMinY() {
        return getMin(Y);
    }

    /**
     * Get the mean of the collected x values.
     *
     * @return mean x
     */
    public double getMeanX() {
        return getMean(X);
    }

    /**
     * Get the deviation in x.
     *
     * @return sqrt(var(x))
     */
    public double getDevX() {
        return getSigma(X);
    }

    /**
     * Get the mean of the collected y values.
     *
     * @return mean y
     */
    public double getMeanY() {
        return getMean(Y);
    }

    /**
     * Get the deviation in y.
     *
     * @return sqrt(var(y))
     */
    public double getDevY() {
        return getSigma(Y);
    }

    /**
     * Get the mean of the collected z values.
     *
     * @return mean z
     */
    public double getMeanZ() {
        return getMean(Z);
    }

    /**
     * Get the deviation in z.
     *
     * @return sqrt(var(z))
     */
    public double getDevZ() {
        return getSigma(Z);
    }

    /**
     * Get the XY covariance of the collected (x,y,z) tuples.
     *
     * @return the XY covariance
     */
    public double getCovXY() {
        return getCovar(X, Y);
    }

    /**
     * Get the XY correlation of the collected (x,y,z) tuples.
     *
     * @return the XY correlation
     */
    public double getCorrelationXY() {
        return getCorrelation(X, Y);
    }

    /**
     * Get the XZ covariance of the collected (x,y,z) tuples.
     *
     * @return the XZ covariance
     */
    public double getCovXZ() {
        return getCovar(X, Z);
    }

    /**
     * Get the XZ correlation of the collected (x,y,z) tuples.
     *
     * @return the XZ correlation
     */
    public double getCorrelationXZ() {
        return getCorrelation(X, Z);
    }

    /**
     * Get the YZ covariance of the collected (x,y,z) tuples.
     *
     * @return the YZ covariance
     */
    public double getCovYZ() {
        return getCovar(Y, Z);
    }

    /**
     * Get the YZ correlation of the collected (x,y,z) tuples.
     *
     * @return the YZ correlation
     */
    public double getCorrelationYZ() {
        return getCorrelation(Y, Z);
    }
}
 