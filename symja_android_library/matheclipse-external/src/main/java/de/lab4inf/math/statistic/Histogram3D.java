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

/**
 * Collects a three dimensional distribution as a histogram and fills
 * the corresponding bins.
 *
 * @author nwulff
 * @version $Id: Histogram3D.java,v 1.11 2011/09/12 14:49:20 nwulff Exp $
 * @since 06.06.2009
 */
public class Histogram3D extends DataCollector3D {
    /**
     * default number of bins for the histogram.
     */
    public static final int DEFAULT_BINS = 25;
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -4129242005895621141L;
    private double[][][] data;
    private int numXBins;
    private int numYBins;
    private int numZBins;
    private long numUnderFlowX;
    private long numOverFlowX;
    private long numUnderFlowY;
    private long numOverFlowY;
    private long numUnderFlowZ;
    private long numOverFlowZ;
    private double underFlowX;
    private double overFlowX;
    private double underFlowY;
    private double overFlowY;
    private double underFlowZ;
    private double overFlowZ;
    private double deltaX;
    private double deltaY;
    private double deltaZ;
    private double lowerX;
    private double upperX;
    private double lowerY;
    private double upperY;
    private double lowerZ;
    private double upperZ;

    /**
     * Default constructor for the unit cube.
     */
    public Histogram3D() {
        this("histogram-3D");
    }

    /**
     * Named default constructor for the unit cube.
     *
     * @param name of the histogram
     */
    public Histogram3D(final String name) {
        this(name, new int[]{DEFAULT_BINS, DEFAULT_BINS, DEFAULT_BINS},
                new double[]{0, 0, 0}, new double[]{1, 1, 1});
    }

    /**
     * Constructor for a named histogram with given bounds.
     *
     * @param name of this histogram
     * @param bins number of bins for the different coordinates
     * @param min  minimal values for each coordinate
     * @param max  maximal values for each coordinate
     */
    public Histogram3D(final String name, final int[] bins, final double[] min,
                       final double[] max) {
        super(name);
        init(bins, min, max);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return Arrays.deepEquals(data, ((Histogram3D) o).data);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.deepHashCode(data);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollectorND#init()
     */
    @Override
    public void init() {
        super.init();
        init(new int[]{DEFAULT_BINS, DEFAULT_BINS, DEFAULT_BINS},
                new double[]{0, 0, 0}, new double[]{1, 1, 1});

    }

    /**
     * Initialization resetting the histogram.
     *
     * @param bins number of bins for the different coordinates
     * @param min  minimal values for each coordinate
     * @param max  maximal values for each coordinate
     */
    private void init(final int[] bins, final double[] min, final double[] max) {
        super.init();
        numXBins = bins[0];
        numYBins = bins[1];
        numZBins = bins[2];
        if (numXBins <= 0) {
            throw new IllegalArgumentException("Number of x bins " + numXBins);
        }
        if (numYBins <= 0) {
            throw new IllegalArgumentException("Number of y bins " + numYBins);
        }
        if (numZBins <= 0) {
            throw new IllegalArgumentException("Number of z bins " + numZBins);
        }
        lowerX = Math.min(min[0], max[0]);
        upperX = Math.max(min[0], max[0]);
        lowerY = Math.min(min[1], max[1]);
        upperY = Math.max(min[1], max[1]);
        lowerZ = Math.min(min[2], max[2]);
        upperZ = Math.max(min[2], max[2]);
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
        deltaZ = (upperZ - lowerZ);
        if (deltaZ <= 0) {
            deltaZ = 1;
            upperZ = lowerZ + deltaZ;
        }
        deltaX /= numXBins;
        deltaY /= numYBins;
        deltaZ /= numZBins;
        underFlowX = 0;
        overFlowX = 0;
        underFlowY = 0;
        overFlowY = 0;
        underFlowZ = 0;
        overFlowZ = 0;
        data = new double[numXBins][numYBins][numZBins];
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
     * Calculate the index of the bin for value z.
     *
     * @param z the data value
     * @return the index for value z
     */
    public int binz(final double z) {
        return (int) ((z - lowerZ) / deltaZ);
    }

    /**
     * Calculate the z value of bin.
     *
     * @param bin the index
     * @return the z value
     */
    public double zValue(final int bin) {
        return lowerZ + (bin + 0.5) * deltaZ;
    }

    /**
     * Get the accumulated weight within the selected bin.
     *
     * @param ix the x index
     * @param iy the y index
     * @param iz the z index
     * @return the weight content
     */
    public double wValue(final int ix, final int iy, final int iz) {
        return data[ix][iy][iz];
    }

    /**
     * Get the sampled (none normalized) probability distribution.
     *
     * @return the pdf
     */
    public double[][][] getPdf() {
        return copy(data);
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
     * Get the lower value of the z bins.
     *
     * @return the minimum of the z bins
     */
    public double getLowerZ() {
        return lowerZ;
    }

    /**
     * Get the upper value of the z bins.
     *
     * @return the maximum of the z bins
     */
    public double getUpperZ() {
        return upperZ;
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
     * Get the none weighted number of underflows.
     *
     * @return number of underflows
     */
    public long getNumUnderflowY() {
        return numUnderFlowY;
    }

    /**
     * Get the weighted number of overflows for z.
     *
     * @return weighted z overflows
     */
    public double getOverflowZ() {
        return overFlowZ;
    }

    /**
     * Get the weighted number of z underflows.
     *
     * @return weighted z underflows
     */
    public double getUnderflowZ() {
        return underFlowZ;
    }

    /**
     * Get the none weighted number of z overflows.
     *
     * @return number z of overflows
     */
    public long getNumOverflowZ() {
        return numOverFlowZ;
    }

    /**
     * Get the none weighted number of z underflows.
     *
     * @return number of underflows
     */
    public long getNumUnderflowZ() {
        return numUnderFlowZ;
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
     * Get the number of z bins in the histogram.
     *
     * @return the number of z bins.
     */
    public int getNumZBins() {
        return numZBins;
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
                for (int k = 0; k < numZBins; k++) {
                    if (data[i][j][k] > maxW) {
                        maxW = data[i][j][k];
                    }
                }
            }
        }
        return maxW;
    }

    /**
     * Collects a (x,y,z) data tuple optional weight w.
     * If the dimension of the data is 4, x[3] is the weight.
     *
     * @param x the x data tuple
     */
    @Override
    public void collect(final double... x) {
        double w = 1;
        int n = x.length;
        if (n == 4) {
            w = x[3];
        }
        collect(x[0], x[1], x[2], w);
    }

    /**
     * Collects a (x,y,z) data tuple with weight w.
     *
     * @param x the x data point
     * @param y the y data point
     * @param z the z data point
     * @param w the weight of the data tuple
     */
    //@Override
    public void collect(final double x, final double y, final double z,
                        final double w) {
        super.collect(x, y, z, w);
        int ix = binx(x);
        int iy = biny(y);
        int iz = binz(z);
        collectOverUnderflows(w, ix, iy, iz);
        if (0 <= ix && ix < numXBins && 0 <= iy && iy < numYBins && 0 <= iz && iz < numZBins) {
            data[ix][iy][iz] += w;
        }
    }

    /**
     * Collects a (x,y,z) data tuple with weight 1.
     *
     * @param x the x data point
     * @param y the y data point
     * @param z the z data point
     */
    // @Override
    public void collect(final double x, final double y, final double z) {
        collect(x, y, z, 1);
    }

    /**
     * Internal helper for bookkeeping of under- and overflows.
     *
     * @param w  the weight factor
     * @param ix the x bin index
     * @param iy the y bin index
     * @param iz the z bin index
     */
    private void collectOverUnderflows(final double w, final int ix,
                                       final int iy, final int iz) {
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
        if (iz < 0) {
            numUnderFlowZ++;
            underFlowZ += w;
        } else if (iz >= numZBins) {
            numOverFlowZ++;
            overFlowZ += w;
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
                for (int k = 0; k < numZBins; k++) {
                    if (data[i][j][k] > 0) {
                        xp.collect(x, data[i][j][k]);
                    }
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
                for (int k = 0; k < numZBins; k++) {
                    if (data[j][i][k] > 0) {
                        yp.collect(y, data[j][i][k]);
                    }
                }
            }
        }
        return yp;
    }

    /**
     * Make a projection onto the z axis.
     *
     * @return a z projected one dimensional histogram
     */
    public Histogram1D getZProjection() {
        double z;
        Histogram1D zp = new Histogram1D(numZBins, getLowerZ(), getUpperZ());
        for (int i = 0; i < numZBins; i++) {
            z = zValue(i);
            for (int j = 0; j < numXBins; j++) {
                for (int k = 0; k < numYBins; k++) {
                    if (data[j][k][i] > 0) {
                        zp.collect(z, data[j][k][i]);
                    }
                }
            }
        }
        return zp;
    }

    /**
     * Make a 2D projection onto the XY-plane.
     *
     * @return a XY projected two dimensional histogram
     */
    public Histogram2D getXYProjection() {
        String xyName = String.format("%s-XY", getName());
        double x, y;
        Histogram2D xyp = new Histogram2D(xyName, numXBins, getLowerX(),
                getUpperX(), numYBins, getLowerY(), getUpperY());
        for (int i = 0; i < numXBins; i++) {
            x = xValue(i);
            for (int j = 0; j < numYBins; j++) {
                y = yValue(j);
                for (int k = 0; k < numZBins; k++) {
                    if (data[i][j][k] > 0) {
                        xyp.collect(x, y, data[i][j][k]);
                    }
                }
            }
        }
        return xyp;
    }

    /**
     * Make a 2D projection onto the XZ-plane.
     *
     * @return a XZ projected two dimensional histogram
     */
    public Histogram2D getXZProjection() {
        String xzName = String.format("%s-XZ", getName());
        double x, z;
        Histogram2D xzp = new Histogram2D(xzName, numXBins, getLowerX(),
                getUpperX(), numZBins, getLowerZ(), getUpperZ());
        for (int i = 0; i < numXBins; i++) {
            x = xValue(i);
            for (int j = 0; j < numZBins; j++) {
                z = zValue(j);
                for (int k = 0; k < numYBins; k++) {
                    if (data[i][k][j] > 0) {
                        xzp.collect(x, z, data[i][k][j]);
                    }
                }
            }
        }
        return xzp;
    }

    /**
     * Make a 2D projection onto the YZ-plane.
     *
     * @return a YZ projected two dimensional histogram
     */
    public Histogram2D getYZProjection() {
        String yzName = String.format("%s-YZ", getName());
        double y, z;
        Histogram2D yzp = new Histogram2D(yzName, numYBins, getLowerY(),
                getUpperY(), numZBins, getLowerZ(), getUpperZ());
        for (int i = 0; i < numYBins; i++) {
            y = yValue(i);
            for (int j = 0; j < numZBins; j++) {
                z = zValue(j);
                for (int k = 0; k < numXBins; k++) {
                    if (data[k][i][j] > 0) {
                        yzp.collect(y, z, data[k][i][j]);
                    }
                }
            }
        }
        return yzp;
    }

}
 