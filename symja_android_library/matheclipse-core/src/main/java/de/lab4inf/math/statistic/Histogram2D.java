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

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Collects a two dimensional distribution as a histogram and fills
 * the corresponding bins.
 *
 * @author nwulff
 * @version $Id: Histogram2D.java,v 1.10 2010/02/25 15:31:54 nwulff Exp $
 * @since 28.03.2009
 */
public class Histogram2D extends DataCollector2D {
    /**
     * default number of bins for the histogram.
     */
    public static final int DEFAULT_BINS = 50;
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = 4554910356873283986L;
    private double[][] bins;
    private int numXBins;
    private int numYBins;
    private long numUnderFlowX;
    private long numOverFlowX;
    private long numUnderFlowY;
    private long numOverFlowY;
    private double underFlowX;
    private double overFlowX;
    private double underFlowY;
    private double overFlowY;
    private double deltaX;
    private double deltaY;
    private double lowerX;
    private double upperX;
    private double lowerY;
    private double upperY;

    /**
     * Default constructor for the unit square.
     */
    public Histogram2D() {
        this("2d-histogram");
    }

    /**
     * Named default constructor for the unit square.
     *
     * @param name of the histogram
     */
    public Histogram2D(final String name) {
        this(name, DEFAULT_BINS, 0, 1, DEFAULT_BINS, 0, 1);
    }

    /**
     * Constructor for a named histogram with given bounds.
     *
     * @param name  of the histogram
     * @param xbins number of bins in the x direction
     * @param xmin  minimal x value
     * @param xmax  maximal x value
     * @param ybins number of bins in the y direction
     * @param ymin  minimal y value
     * @param ymax  maximal y value
     */
    public Histogram2D(final String name, final int xbins, final double xmin,
                       final double xmax, final int ybins, final double ymin,
                       final double ymax) {
        super(name);
        init(xbins, xmin, xmax, ybins, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return Arrays.deepEquals(bins, ((Histogram2D) o).bins);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.deepHashCode(bins);
    }

    /**
     * Initialization resetting the histogram.
     *
     * @param xbins number of bins in the x direction
     * @param xmin  minimal x value
     * @param xmax  maximal x value
     * @param ybins number of bins in the y direction
     * @param ymin  minimal y value
     * @param ymax  maximal y value
     */
    public void init(final int xbins, final double xmin, final double xmax,
                     final int ybins, final double ymin, final double ymax) {
        super.init();
        numXBins = xbins;
        numYBins = ybins;
        if (numXBins <= 0) {
            throw new IllegalArgumentException("Number of x bins " + numXBins);
        }
        if (numYBins <= 0) {
            throw new IllegalArgumentException("Number of y bins " + numYBins);
        }
        lowerX = min(xmin, xmax);
        upperX = max(xmin, xmax);
        lowerY = min(ymin, ymax);
        upperY = max(ymin, ymax);

        deltaX = (upperX - lowerX);
        if (deltaX <= 0) {
            deltaX = 1;
            upperX = lowerX + deltaX;
        }
        deltaY = (upperY - lowerY);
        if (deltaY <= 0) {
            deltaY = 1;
            upperY = lowerY + deltaY;
        }
        deltaX /= numXBins;
        deltaY /= numYBins;
        underFlowX = 0;
        overFlowX = 0;
        underFlowY = 0;
        overFlowY = 0;
        bins = new double[numXBins][numYBins];
    }

    /**
     * Calculate the index of the bin for value x.
     *
     * @param x the data value
     * @return the index for value x
     */
    public int binx(final double x) {
        return (int) ((x - lowerX) / deltaX);
    }

    /**
     * Calculate the x value of bin.
     *
     * @param bin the index
     * @return the x value
     */
    public double xValue(final int bin) {
        return lowerX + (bin + 0.5) * deltaX;
    }

    /**
     * Calculate the index of the bin for value y.
     *
     * @param y the data value
     * @return the index for value y
     */
    public int biny(final double y) {
        return (int) ((y - lowerY) / deltaY);
    }

    /**
     * Calculate the y value of bin.
     *
     * @param bin the index
     * @return the y value
     */
    public double yValue(final int bin) {
        return lowerY + (bin + 0.5) * deltaY;
    }

    /**
     * Get the accumulated weight within the selected bin.
     *
     * @param ix the x index
     * @param iy the y index
     * @return the weight content
     */
    public double wValue(final int ix, final int iy) {
        return bins[ix][iy];
    }

    /**
     * Get the sampled (none normalized) probability distribution.
     *
     * @return the pdf
     */
    public double[][] getPdf() {
        return copy(bins);
    }

    /**
     * Get the lower value of the x bins.
     *
     * @return the minimum of the x bins
     */
    public double getLowerX() {
        return lowerX;
    }

    /**
     * Get the upper value of the x bins.
     *
     * @return the maximum of the x bins
     */
    public double getUpperX() {
        return upperX;
    }

    /**
     * Get the lower value of the y bins.
     *
     * @return the minimum of the y bins
     */
    public double getLowerY() {
        return lowerY;
    }

    /**
     * Get the upper value of the y bins.
     *
     * @return the maximum of the y bins
     */
    public double getUpperY() {
        return upperY;
    }

    /**
     * Get the weighted number of overflows for x.
     *
     * @return weighted x overflows
     */
    public double getOverflowX() {
        return overFlowX;
    }

    /**
     * Get the weighted number of x underflows.
     *
     * @return weighted x underflows
     */
    public double getUnderflowX() {
        return underFlowX;
    }

    /**
     * Get the none weighted number of x overflows.
     *
     * @return number x of overflows
     */
    public long getNumOverflowX() {
        return numOverFlowX;
    }

    /**
     * Get the none weighted number of underflows.
     *
     * @return number of underflows
     */
    public long getNumUnderflowX() {
        return numUnderFlowX;
    }

    /**
     * Get the weighted number of overflows for y.
     *
     * @return weighted y overflows
     */
    public double getOverflowY() {
        return overFlowY;
    }

    /**
     * Get the weighted number of x underflows.
     *
     * @return weighted y underflows
     */
    public double getUnderflowY() {
        return underFlowY;
    }

    /**
     * Get the none weighted number of y overflows.
     *
     * @return number y of overflows
     */
    public long getNumOverflowY() {
        return numOverFlowY;
    }

    /**
     * Get the none weighted number of y underflows.
     *
     * @return number of underflows
     */
    public long getNumUnderflowY() {
        return numUnderFlowY;
    }

    /**
     * Get the number of x bins in the histogram.
     *
     * @return the number of x bins.
     */
    public int getNumXBins() {
        return numXBins;
    }

    /**
     * Get the number of y bins in the histogram.
     *
     * @return the number of y bins.
     */
    public int getNumYBins() {
        return numYBins;
    }

    /**
     * Get the maximal bin weight value.
     *
     * @return the maximal weighted bin value
     */
    public double getMaxW() {
        double maxW = 0;
        for (int i = 0; i < numXBins; i++) {
            for (int j = 0; j < numYBins; j++) {
                if (bins[i][j] > maxW) {
                    maxW = bins[i][j];
                }
            }
        }
        return maxW;
    }

    /**
     * Collects a (x,y) data tuple with weight w.
     *
     * @param x the x data point
     * @param y the y data point
     * @param w the weight of the data tuple
     */
    @Override
    public void collect(final double x, final double y, final double w) {
        super.collect(x, y, w);
        int ix = binx(x);
        int iy = biny(y);
        if (ix < 0) {
            numUnderFlowX++;
            underFlowX += w;
        } else if (ix >= numXBins) {
            numOverFlowX++;
            overFlowX += w;
        }
        if (iy < 0) {
            numUnderFlowY++;
            underFlowY += w;
        } else if (iy >= numYBins) {
            numOverFlowY++;
            overFlowY += w;
        }
        if (0 <= ix && ix < numXBins && 0 <= iy && iy < numYBins) {
            bins[ix][iy] += w;
        }
    }

    /**
     * Make a projection onto the x axis.
     *
     * @return a x projected one dimensional histogram
     */
    public Histogram1D getXProjection() {
        double x;
        Histogram1D xp = new Histogram1D(numXBins, getLowerX(), getUpperX());
        for (int i = 0; i < numXBins; i++) {
            x = xValue(i);
            for (int j = 0; j < numYBins; j++) {
                if (bins[i][j] > 0) {
                    xp.collect(x, bins[i][j]);
                }
            }
        }
        return xp;
    }

    /**
     * Make a projection onto the y axis.
     *
     * @return a y projected one dimensional histogram
     */
    public Histogram1D getYProjection() {
        double y;
        Histogram1D yp = new Histogram1D(numYBins, getLowerY(), getUpperY());
        for (int i = 0; i < numYBins; i++) {
            y = yValue(i);
            for (int j = 0; j < numXBins; j++) {
                if (bins[j][i] > 0) {
                    yp.collect(y, bins[j][i]);
                }
            }
        }
        return yp;
    }
}
 