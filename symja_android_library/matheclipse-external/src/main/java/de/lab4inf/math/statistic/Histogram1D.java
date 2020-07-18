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

//import de.lab4inf.math.util.Accuracy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import static de.lab4inf.math.util.Accuracy.isSimilar;

/**
 * Collects a one dimensional distribution as a histogram and fills
 * the corresponding bins. If no bins are specified it will be done
 * by analysis of the first entries.
 *
 * @author Dr. Nikolaus Wulff
 * @version $Id: Histogram1D.java,v 1.16 2014/12/09 15:51:40 nwulff Exp $
 * @since 30.09.2002
 */
public class Histogram1D extends DataCollector1D {
    /**
     * default number of bin entries for automatic binning.
     */
    public static final int DEFAULT_ENTRIES = 100;
    /**
     * default number of bins for the histogram.
     */
    public static final int DEFAULT_BINS = 50;
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = 2203050021823125781L;
    private int numBins;
    private long numUnderFlow;
    private long numOverFlow;
    private double underFlow;
    private double overFlow;
    private double lowerLimit;
    private double upperLimit;
    private double[] bins;
    private transient double delta;
    private transient BinStrategy1D binStrategy;
    private transient double[] cdf;

    /**
     * Default constructor with an automatic bin strategy.
     */
    public Histogram1D() {
        binStrategy = new BinStrategy1D(this, DEFAULT_ENTRIES, DEFAULT_BINS);
    }

    /**
     * Constructor using the specified bin strategy.
     *
     * @param bs the bin strategy to use.
     */
    public Histogram1D(final BinStrategy1D bs) {
        logger.info("ctor with " + bs);
        binStrategy = bs;
        binStrategy.setHistogram(this);
    }

    /**
     * Constructor using the specified min, max values and number of bins.
     *
     * @param numBins the number of bins
     * @param min     the minimal data value
     * @param max     the maximal data value
     */
    public Histogram1D(final int numBins, final double min, final double max) {
        init(numBins, min, max);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#hashCode()
     */
    @Override
    public int hashCode() {
        final long hc = numOverFlow ^ numUnderFlow ^ Arrays.hashCode(bins) ^ Double.doubleToLongBits(underFlow)
                ^ Double.doubleToLongBits(overFlow) ^ Double.doubleToLongBits(lowerLimit)
                ^ Double.doubleToLongBits(upperLimit);

        return (int) (hc) ^ super.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o))
            return false;
        final Histogram1D obj = (Histogram1D) o;
        return numBins == (obj.numBins) && isSimilar(underFlow, obj.getUnderflow()) && isSimilar(overFlow, obj.getOverflow())
                && isSimilar(lowerLimit, obj.getLowerLimit()) && isSimilar(upperLimit, obj.getUpperLimit())
                && Arrays.equals(bins, obj.bins);
    }

    /**
     * Specialized read method for object de-serialization.
     *
     * @param stream to read from
     * @throws IOException            in case of an error
     * @throws ClassNotFoundException in case of an error
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        numBins = stream.readInt();
        numUnderFlow = stream.readLong();
        numOverFlow = stream.readLong();
        underFlow = stream.readDouble();
        overFlow = stream.readDouble();
        lowerLimit = stream.readDouble();
        upperLimit = stream.readDouble();
        bins = (double[]) stream.readObject();
        delta = (upperLimit - lowerLimit);
        if (delta <= 0) {
            delta = 1;
            upperLimit = lowerLimit + delta;
        }
        delta /= numBins;
        binStrategy = null;
        cdf = null;
    }

    /**
     * Specialized write method for object serialization.
     *
     * @param stream to write to
     * @throws IOException in case of an error
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.writeInt(numBins);
        stream.writeLong(numUnderFlow);
        stream.writeLong(numOverFlow);
        stream.writeDouble(underFlow);
        stream.writeDouble(overFlow);
        stream.writeDouble(lowerLimit);
        stream.writeDouble(upperLimit);
        stream.writeObject(bins);
    }

    /**
     * Internal setup reseting the histogram.
     *
     * @param nb  the number of bins
     * @param min the minimal bin value
     * @param max the maximal bin value
     */
    public void init(final int nb, final double min, final double max) {
        super.init();
        binStrategy = null;
        cdf = null;
        lowerLimit = min;
        upperLimit = max;
        if (lowerLimit > upperLimit) {
            lowerLimit = max;
            upperLimit = min;
        }
        this.numBins = nb;
        if (numBins <= 0) {
            throw new IllegalArgumentException("Number of bins " + numBins + " not allowed");
        }
        delta = (upperLimit - lowerLimit);
        if (delta <= 0) {
            delta = 1;
            upperLimit = lowerLimit + delta;
        }
        delta /= numBins;
        bins = new double[numBins];
        numUnderFlow = 0;
        numOverFlow = 0;
        underFlow = 0;
        overFlow = 0;
    }

    /**
     * Calculate the index of the bin for value x.
     *
     * @param x the data value
     * @return the int index for value x
     */
    public int bin(final double x) {
        return (int) ((x - lowerLimit) / delta);
    }

    /**
     * Calculate the x value of bin.
     *
     * @param bin the index
     * @return the x value
     */
    public double xValue(final int bin) {
        return lowerLimit + (bin + 0.5) * delta;
    }

    /**
     * Get the accumulated weight contents of the bin.
     *
     * @param bin the index
     * @return the content
     */
    public double wValue(final int bin) {
        return bins[bin];
    }

    /**
     * Collects a data point with weight w.
     *
     * @param x the data point
     * @param w the weight of the data point
     */
    @Override
    public void collect(final double x, final double w) {
        if (binStrategy == null) {
            super.collect(x, w);
            final int bin = bin(x);
            if (bin < 0) {
                numUnderFlow++;
                underFlow += w;
            } else if (bin >= numBins) {
                numOverFlow++;
                overFlow += w;
            } else {
                bins[bin] += w;
            }
        } else {
            binStrategy.collect(x, w);
        }
    }

    /**
     * Get the number of bins in the histogram.
     *
     * @return the number of bins.
     */
    public int getNumBins() {
        if (binStrategy != null)
            binStrategy.rebin();
        return numBins;
    }

    /**
     * Calculate the discretized culmative probability
     * distribution function (CDF).
     *
     * @return the CDF
     */
    public double[] getCdf() {
        if (binStrategy != null)
            binStrategy.rebin();
        if (cdf == null) {
            final double norm = getSumWeight();
            cdf = new double[numBins];
            cdf[0] = bins[0] / norm;
            for (int i = 1; i < numBins; i++) {
                cdf[i] = cdf[i - 1] + bins[i] / norm;
            }
        }
        return copy(cdf);
    }

    /**
     * Get the histogram weights distribution, e.g. the
     * discretised probability distribution function (PDF).
     *
     * @return double[] array with the distribution weights
     */
    public double[] getPdf() {
        if (binStrategy != null)
            binStrategy.rebin();
        return copy(bins);
    }

    private void setPdf(final double[] pdf) {
        bins = pdf;
    }

    /**
     * Get the histogram bin center values.
     *
     * @return double[] array with the bin centers
     */
    public double[] getCenters() {
        if (binStrategy != null)
            binStrategy.rebin();
        final double[] center = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            center[i] = xValue(i);
        }

        return center;
    }

    /**
     * Get the weighted number of overflows.
     *
     * @return weighted overflows
     */
    public double getOverflow() {
        if (binStrategy != null)
            binStrategy.rebin();
        return overFlow;
    }

    /**
     * Get the weighted number of underflows.
     *
     * @return double with weighted underflows
     */
    public double getUnderflow() {
        if (binStrategy != null)
            binStrategy.rebin();
        return underFlow;
    }

    /**
     * Get the none weighted number of overflows.
     *
     * @return number of overflows
     */
    public long getNumOverflow() {
        if (binStrategy != null)
            binStrategy.rebin();
        return numOverFlow;
    }

    /**
     * Get the none weighted number of underflows.
     *
     * @return number of underflows
     */
    public long getNumUnderflow() {
        if (binStrategy != null)
            binStrategy.rebin();
        return numUnderFlow;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getMean()
     */
    @Override
    public double getMean() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getMean();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getSigma()
     */
    @Override
    public double getSigma() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getSigma();
    }

    /**
     * Get the lower value of the bins.
     *
     * @return the minimum of the bins
     */
    public double getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Get the upper value of the bins.
     *
     * @return the maximum of the bins
     */
    public double getUpperLimit() {
        return upperLimit;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getMax()
     */
    @Override
    public double getMax() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getMax();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getMin()
     */
    @Override
    public double getMin() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getMin();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getSumX()
     */
    @Override
    public double getSumX() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getSumX();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getSumWeight()
     */
    @Override
    public double getSumWeight() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getSumWeight();
    }

    /*
     * (non-Javadoc)calmative
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#getEntries()
     */
    @Override
    public long getEntries() {
        if (binStrategy != null)
            binStrategy.rebin();
        return super.getEntries();
    }

    /**
     * Get the maximal bin weighted sum.
     *
     * @return the maximal weight of the data points
     */
    public double getMaxW() {
        if (binStrategy != null)
            binStrategy.rebin();
        double maxW = 0;
        for (int i = 0; i < numBins; i++) {
            if (bins[i] > maxW) {
                maxW = bins[i];
            }
        }
        return maxW;
    }

    /**
     * Add/merge two histogram distribution.
     *
     * @param histo histogram to add.
     * @return the merged histogram
     */
    public Histogram1D join(final Histogram1D histo) {
        final double xmin = Math.min(getLowerLimit(), histo.getLowerLimit());
        final double xmax = Math.max(getUpperLimit(), histo.getUpperLimit());
        final int num = Math.max(getNumBins(), histo.getNumBins());
        final Histogram1D join = new Histogram1D(num, xmin, xmax);

        String name;
        if (getName().equals(histo.getName())) {
            name = getName();
        } else {
            name = getName() + "/" + histo.getName();
        }
        join.setName(name);

        double[] data = getPdf();

        for (int i = 0; i < data.length; i++) {
            final double x = xValue(i);
            final double w = data[i];
            if (w > 0)
                join.collect(x, w);
        }

        data = histo.getPdf();
        for (int i = 0; i < data.length; i++) {
            final double x = histo.xValue(i);
            final double w = data[i];
            if (w > 0)
                join.collect(x, w);
        }

        return join;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.statistic.DataCollector1D#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "(" + " numBins=" + numBins + " numUnderFlow=" + numUnderFlow + " numOverFlow=" + numOverFlow
                + " underFlow=" + underFlow + " overFlow=" + overFlow + " delta=" + delta + " lowerLimit=" + lowerLimit + " upperLimit="
                + upperLimit + " binningStrategy=" + binStrategy + ")";
    }

    /**
     * Calculate the difference between two histogram distribution.
     *
     * @param histo histogram to compare to.
     * @return the difference histogram
     */
    public Histogram1D diff(final Histogram1D histo) {
        if (getNumBins() != histo.getNumBins() || !isSimilar(getLowerLimit(), histo.getLowerLimit())
                || !isSimilar(getUpperLimit(), histo.getUpperLimit())) {
            throw new IllegalArgumentException("not comparable");
        }
        final Histogram1D diff = new Histogram1D(getNumBins(), getLowerLimit(), getUpperLimit());
        String name;
        if (getName().equals(histo.getName())) {
            name = getName();
        } else {
            name = getName() + "-" + histo.getName();
        }
        diff.setName(name);
        final double[] data1 = getPdf();
        final double[] data2 = histo.getPdf();
        final double[] dpdf = new double[data1.length];
        for (int i = 0; i < data1.length; i++) {
            final double w = data1[i] - data2[i];
            dpdf[i] = w;
        }
        diff.minX = minX;
        diff.maxX = maxX;
        diff.entries = entries;
        diff.setPdf(dpdf);
        return diff;
    }
}
 