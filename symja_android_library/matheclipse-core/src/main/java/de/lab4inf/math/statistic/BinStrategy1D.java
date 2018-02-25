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
import java.util.ArrayList;

import static de.lab4inf.math.util.Accuracy.isSimilar;

/**
 * Helper class  to calculate the optimal bins for
 * a histogram from the first maxCount entries.
 * Therefore this class is only package internal
 * visible.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: BinStrategy1D.java,v 1.14 2011/09/12 14:49:20 nwulff Exp $
 * @since 30.09.2002
 */
class BinStrategy1D extends DataCollector1D {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -6181895308026717826L;
    private int maxCount;
    private int numBins;
    private ArrayList<WeightedPoint> data;
    private Histogram1D collector;

    /**
     * Constructor for a HistoCollector with the maximal
     * of initial data values before the bins are calculated.
     *
     * @param maxCount the maximal number of data entries
     * @param numBins  the desired number of bins
     */
    BinStrategy1D(final Histogram1D histo, final int maxCount, final int numBins) {
        this.collector = histo;
        this.maxCount = maxCount;
        this.numBins = numBins;
        data = new ArrayList<WeightedPoint>(maxCount);
    }

    /**
     * Get the associated histogram.
     *
     * @return histogram
     */
    Histogram1D getHistoCollector() {
        return collector;
    }

    /**
     * Set the associated histogram.
     *
     * @param histo to associate
     */
    void setHistogram(final Histogram1D histo) {
        this.collector = histo;
    }

    /**
     * rebin the associated histogram.
     */
    void rebin() {
        double xMin = getMin();
        double xMax = getMax();
        if (data.size() < 2 || isSimilar(xMin, xMax)) {
            xMin = 0;
            xMax = xMin + 1;
        }

        double delta = xMax - xMin;
        if (numBins > 0) {
            double dx = delta / numBins;
            double eps = dx / 2;
            xMin -= eps;
            xMax += eps;
            // numBins++;
        }

        collector.init(numBins, xMin, xMax);
        for (WeightedPoint wp : data) {
            collector.collect(wp.x, wp.w);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#collect(double, double)
     */
    @Override
    public void collect(final double x, final double weight) {
        super.collect(x, weight);
        data.add(new WeightedPoint(x, weight));
        if (data.size() == maxCount) {
            rebin();
        }
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return collector.equals(((BinStrategy1D) o).collector);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ collector.hashCode();
    }

    /**
     * Internal helper class to hold weighted data values.
     */
    private static class WeightedPoint implements Serializable {
        /**
         * reference to the serialVersionUID attribute.
         */
        private static final long serialVersionUID = 3668921963738910716L;
        final double x, w;

        public WeightedPoint(final double x, final double w) {
            this.x = x;
            this.w = w;
        }
    }
}
 