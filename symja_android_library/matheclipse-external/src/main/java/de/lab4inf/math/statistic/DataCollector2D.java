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

import de.lab4inf.math.util.Accuracy;

/**
 * Utility class to calculate mean, deviation and correlation
 * of a sampled two dimensional distribution.
 *
 * @author nwulff
 * @version $Id: DataCollector2D.java,v 1.11 2010/02/25 15:31:54 nwulff Exp $
 * @since 30.09.2002
 */
public class DataCollector2D implements Serializable {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = 4788886571124454655L;
    protected DataCollector1D xCollector;
    protected DataCollector1D yCollector;
    protected boolean finished;
    protected String name;
    protected double sumXY;
    protected double covarXY, correlXY;

    /**
     * Default constructor.
     */
    public DataCollector2D() {
        this("statistic");
    }

    /**
     * Constructor for a name collector.
     *
     * @param name of this collector
     */
    public DataCollector2D(final String name) {
        xCollector = new DataCollector1D();
        yCollector = new DataCollector1D();
        init();
        setName(name);
    }

    /**
     * Initialize this collector, i.e. clear
     * its internal state.
     */
    public void init() {
        sumXY = 0;
        xCollector.init();
        yCollector.init();
        finished = false;
    }

    /**
     * Utility method to copy matrix x.
     *
     * @param x array to copy
     * @return deep copy of x
     */
    public double[][] copy(final double[][] x) {
        if (null == x) {
            return new double[0][0];
        }
        int m, n = x.length;
        double[][] y = new double[n][];
        for (int i = 0; i < n; i++) {
            m = x[i].length;
            y[i] = new double[m];
            System.arraycopy(x[i], 0, y[i], 0, m);
        }
        return y;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        long hc = name.hashCode() ^ Double.doubleToLongBits(sumXY)
                ^ Double.doubleToLongBits(getCorrelation())
                ^ Double.doubleToLongBits(getCovXY());
        return xCollector.hashCode() ^ yCollector.hashCode() ^ (int) hc;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (null == o)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass())
            return false;
        DataCollector2D obj = (DataCollector2D) o;
        return name.equals(obj.getName()) && Accuracy.isSimilar(sumXY, obj.sumXY)
                && xCollector.equals(obj.xCollector)
                && yCollector.equals(obj.yCollector);
    }

    /**
     * Get the name of this collector.
     *
     * @return the name of this statistic collector
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for this statistic collector.
     *
     * @param name the collectors name
     */
    public void setName(final String name) {
        this.name = name;
        xCollector.setName(String.format("%s-x", name));
        yCollector.setName(String.format("%s-y", name));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Internal calculation of covariance and correlation.
     */
    private void calculate() {
        if (finished)
            return;

        finished = true;
        if (getEntries() > 0) {
            covarXY = sumXY / getSumWeights();
            covarXY -= getMeanX() * getMeanY();
            correlXY = covarXY / (getDevX() * getDevY());
        }
    }

    /**
     * Collect the (x,y)-tuple with the given weight factor.
     *
     * @param x the x argument
     * @param y the y argument
     * @param w the weight
     */
    public void collect(final double x, final double y, final double w) {
        sumXY += w * x * y;
        xCollector.collect(x, w);
        yCollector.collect(y, w);
        finished = false;
    }

    /**
     * Collect the (x,y)-tuple with weight equal to one.
     *
     * @param x the x argument
     * @param y the y argument
     */
    public void collect(final double x, final double y) {
        collect(x, y, 1);
    }

    /**
     * Get the minimal collected x value.
     *
     * @return min x
     */
    public double getMinX() {
        return xCollector.getMin();
    }

    /**
     * Get the maximal collected x value.
     *
     * @return max x
     */
    public double getMaxX() {
        return xCollector.getMax();
    }

    /**
     * Get the sum of the sampled x values.
     *
     * @return sum x
     */
    public double getSumX() {
        return xCollector.getSumX();
    }

    /**
     * Get the mean of the collected x values.
     *
     * @return mean x
     */
    public double getMeanX() {
        return xCollector.getMean();
    }

    /**
     * Get the deviation in x.
     *
     * @return sqrt(var(x))
     */
    public double getDevX() {
        return xCollector.getSigma();
    }

    /**
     * Get the minimal collected y value.
     *
     * @return min y
     */
    public double getMinY() {
        return yCollector.getMin();
    }

    /**
     * Get the maximal collected y value.
     *
     * @return max y
     */
    public double getMaxY() {
        return yCollector.getMax();
    }

    /**
     * Get the sum of the sampled y values.
     *
     * @return sum y
     */
    public double getSumY() {
        return yCollector.getSumX();
    }

    /**
     * Get the mean of the collected y values.
     *
     * @return mean y
     */
    public double getMeanY() {
        return yCollector.getMean();
    }

    /**
     * Get the deviation in y.
     *
     * @return sqrt(var(y))
     */
    public double getDevY() {
        return yCollector.getSigma();
    }

    /**
     * Get the covariance of the collected (x,y) tuples.
     *
     * @return the covariance
     */
    public double getCovXY() {
        calculate();
        return covarXY;
    }

    /**
     * Get the correlation of the collected (x,y) tuples.
     *
     * @return the correlation
     */
    public double getCorrelation() {
        calculate();
        return correlXY;
    }

    /**
     * Get the number of collected (x,y) tuples.
     *
     * @return the number of entries
     */
    public long getEntries() {
        return xCollector.getEntries();
    }

    /**
     * Get the weighted number of collected (x,y) tuples.
     *
     * @return the sum of weights
     */
    public double getSumWeights() {
        return xCollector.getSumWeight();
    }
}
 