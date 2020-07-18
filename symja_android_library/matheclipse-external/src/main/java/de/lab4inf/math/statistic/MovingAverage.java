/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Time series analysis with different types of averaging methods,
 * like the simple (SMA), the cumulative (CMA), the weighted (WMA) and
 * the exponential (EMA) moving average and in addition the
 * simple moving median (SMM).
 *
 * @author nwulff
 * @version $Id: MovingAverage.java,v 1.8 2012/06/25 11:15:21 nwulff Exp $
 * @since 11.12.2010
 */

public class MovingAverage extends DataCollector1D {
    public static final int DEFAULT_FRAME_SIZE = 50;
    /**
     * serial uid for this class.
     */
    private static final long serialVersionUID = 69435889592938630L;
    private final double[] data;
    private final double[] averages;
    private final ArrayList<Double> sorted;
    private int p, f, k;
    private double n, sn, sm, a, sma, smm, wma, ema, mmin, mmax;

    /**
     * POJO constructor, using the default frame size.
     */
    public MovingAverage() {
        this(DEFAULT_FRAME_SIZE);
    }

    /**
     * Constructor, using the given frame size.
     * For the EMA alpha=2/(frameSize +1) is used.
     *
     * @param frameSize to use for the moving window
     */
    public MovingAverage(final int frameSize) {
        this(frameSize, 2.0 / (frameSize + 1));
    }

    /**
     * Constructor, using the given frame size and alpha coefficient.
     *
     * @param frameSize to use for the moving window
     * @param alpha     value for the exponential moving average
     */
    public MovingAverage(final int frameSize, final double alpha) {
        f = frameSize;
        data = new double[frameSize];
        averages = new double[frameSize];
        sorted = new ArrayList<Double>(frameSize);
        a = alpha;
        clear();
    }

    /**
     * Set all accumulated sums to zero.
     */
    public void clear() {
        init();
        n = 0;
        p = 0;
        sn = 0;
        sma = 0;
        wma = 0;
        ema = 0;
        mmin = Double.MAX_VALUE;
        mmax = -mmin;
        sorted.clear();
        for (int i = 0; i < f; data[i] = 0, averages[i] = 0, i++) ;
    }

    /**
     * Collect the given data value.
     *
     * @param x value to add
     * @param w double the weight of the data point,
     *          which should be one for the moving average
     */
    @Override
    public void collect(final double x, final double w) {
        super.collect(x, w);
        n++;
        sm = sn;
        sn += n;
        wma = (n * x + sm * wma) / sn;
        ema += a * (x - ema);
        if (n <= f) {
            sma = getMean();
            mmin = Math.min(mmin, x);
            mmax = Math.max(mmax, x);
        } else {
            sma += (x - data[p]) / f;
            //int i;
            //for(data[p] = x, sma=0, i=0;i<f;sma += data[i]/f,i++);
        }
        k = sorted.size();
        if (k >= f) {
            assert sorted.contains(data[p]) : sorted + " " + data[p];
            sorted.remove(data[p]);
        }
        sorted.add(x);
        k = sorted.size();
        if (k > 2) {
            Collections.sort(sorted);
            mmin = sorted.get(0);
            mmax = sorted.get(k - 1);
        }
        if ((k & 1) == 0) {
            smm = (sorted.get(k / 2) + sorted.get(k / 2 - 1)) / 2;
        } else {
            smm = sorted.get(k / 2);
        }
        data[p] = x;
        averages[p] = sma;
        p++;
        p %= f;
    }

    /**
     * Access to the calculated simple moving averages.
     *
     * @return array with SMA values
     */
    public double[] getAverages() {
        return averages.clone();
    }

    /**
     * Return the moving minimum value.
     *
     * @return MMIN
     */
    public double movingMin() {
        return mmin;
    }

    /**
     * Return the moving maximum value.
     *
     * @return MMAX
     */
    public double movingMax() {
        return mmax;
    }

    /**
     * Return the simple moving average.
     *
     * @return SMA
     */
    public double simpleMA() {
        return sma;
    }

    /**
     * Return the simple moving median.
     *
     * @return SMM
     */
    public double simpleMM() {
        return smm;
    }

    /**
     * Return the mean value, which is the cumulative average.
     *
     * @return CMA
     */
    public double cumulativeMA() {
        return getMean();
    }

    /**
     * Return the weighted average.
     *
     * @return WMA
     */
    public double weightedMA() {
        return wma;
    }

    /**
     * Return the exponential average.
     *
     * @return EMA
     */
    public double exponentialMA() {
        return ema;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            MovingAverage anOther = (MovingAverage) obj;
            return Arrays.equals(data, anOther.data);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.statistic.DataCollector1D#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(data);
    }
}
 