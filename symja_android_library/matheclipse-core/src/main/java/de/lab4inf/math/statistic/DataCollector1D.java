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
package de.lab4inf.math.statistic;

import java.io.Serializable;

import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.util.Accuracy.isSimilar;
import static java.lang.Math.sqrt;

/**
 * Utility class to calculate mean and variance for a
 * one dimensional distribution.
 *
 * @author nwulff
 * @version $Id: DataCollector1D.java,v 1.15 2014/06/01 16:25:22 nwulff Exp $
 * @since 30.09.2002
 */
public class DataCollector1D extends L4MObject implements Serializable {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = 2999318850932852551L;
    protected double minX, maxX;
    protected long entries;
    private String name;
    private double mean, sigma;
    private double sumX;
    private double sumW;

    /**
     * Default constructor.
     */
    public DataCollector1D() {
        init();
        setName("statistic");
    }

    /**
     * Initialize a new collector run setting all entries to zero.
     */
    public void init() {
        // logger.info("init called");
        entries = 0;
        sumW = 0;
        sumX = 0;
        mean = 0;
        sigma = 0;
        minX = Double.MAX_VALUE - 1;
        maxX = 1 - Double.MAX_VALUE;
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

        DataCollector1D obj = (DataCollector1D) o;
        return name.equals(obj.name) && getEntries() == obj.getEntries()
                && isSimilar(getMean(), obj.getMean())
                && isSimilar(getSigma(), obj.getSigma())
                && isSimilar(getSumX(), obj.getSumX())
                && isSimilar(getSumWeight(), obj.getSumWeight());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        long hc = getEntries() ^ Double.doubleToLongBits(getSigma())
                ^ Double.doubleToLongBits(getSumX())
                ^ Double.doubleToLongBits(getSumWeight());

        return name.hashCode() ^ (int) hc;
    }

    /**
     * Utility method to copy array x.
     *
     * @param x array to copy
     * @return deep copy of x
     */
    public double[] copy(final double[] x) {
        if (null == x) {
            return new double[0];
        }
        int n = x.length;
        double[] y = new double[n];
        System.arraycopy(x, 0, y, 0, n);
        return y;
    }

    /**
     * Collects a data point with weight 1.
     *
     * @param x double the data point
     */
    public void collect(final double x) {
        collect(x, 1.0);
    }

    /**
     * Collects a data point with weight w.
     *
     * @param x double the data point
     * @param w double the weight of the data point
     */
    public void collect(final double x, final double w) {
        if (w <= 0) {
            throw new IllegalArgumentException("weight " + w + " <= 0");
        }
        double dx, wx = w * x;
        entries++;
        sumW += w;
        sumX += wx;
        dx = (x - mean);
        mean += w * dx / sumW;
        if (entries > 1)
            sigma += w * dx * (x - mean);
        if (minX > x)
            minX = x;
        if (maxX < x)
            maxX = x;
    }

    /**
     * Get the mean value.
     *
     * @return the mean of the data points
     */
    public double getMean() {
        return mean;
    }

    /**
     * Get the deviation.
     *
     * @return the sigma of the data points
     */
    public double getSigma() {
        if (entries > 0)
            return sqrt(sigma / sumW);
        return 0;
    }

    /**
     * Get the maximum data value.
     *
     * @return the maximum of the data points
     */
    public double getMax() {
        return maxX;
    }

    /**
     * Get the minimum data value.
     *
     * @return the minimum of the data points
     */
    public double getMin() {
        return minX;
    }

    /**
     * Get the sum of all data values.
     *
     * @return the sum of the data points
     */
    public double getSumX() {
        return sumX;
    }

    /**
     * Get the sum of the weights.
     *
     * @return the sum of the weight for the data points
     */
    public double getSumWeight() {
        return sumW;
    }

    /**
     * Get the number of entries.
     *
     * @return the number of data entries
     */
    public long getEntries() {
        return entries;
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
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }
}
 