/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.util;

import de.lab4inf.math.L4MObject;

/**
 * Utility class for time measurements. A typical scenario would be:
 * <pre>
 *   Timing t = new Timing();
 *
 *   t.beg();
 *   for(...) {
 *       // do something repeated times and sample the timing
 *       t.sample();
 *   }
 *   t.end();
 *   System.out.printf("Mean: %.2f", t.getMean());
 *   System.out.printf("Devi: %.2f", t.getSigma());
 * </pre>
 * Instances of this class can be used in a multi-threaded environment, as
 * the internal state uses ThreadLocal variables.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Timing.java,v 1.16 2014/11/16 21:47:23 nwulff Exp $
 * @since 29.10.2006
 */
public final class Timing extends L4MObject {
    /**
     * conversion factor from nano to milli seconds.
     */
    private static final double NANO_TO_MILLIS = 1E6;
    /**
     * conversion factor from nano to micro seconds.
     */
    private static final double NANO_TO_MICRO = 1E3;
    private final ThreadLocal<TimingContainer> container;

    /**
     * Sole constructor.
     */
    public Timing() {
        container = new ThreadLocal<TimingContainer>();
    }

    /**
     * Return the elapsed time in milli seconds between
     * two invocations, e.g. between seed (in nano seconds!)
     * and now. If seed &le; 0 the start time in nano seconds
     * is returned.
     * Thus a typical invocation would be:
     * <pre>
     *   long start = diffInMillis(0);
     *   // do some heavy work
     *   long diff  = diffInMillis(start);
     * </pre>
     *
     * @param seed long the start time in nano seconds
     * @return long the difference seed - now in milli seconds
     */
    public static long diffInMillis(final long seed) {
        if (seed <= 0) {
            return System.nanoTime();
        }
        return (long) ((System.nanoTime() - seed) / NANO_TO_MILLIS);
    }

    /**
     * Return the elapsed time in micro seconds between
     * two invocations, e.g. between seed (in nano seconds!)
     * and now. If seed &le; 0 the start time in nano seconds
     * is returned.
     * Thus a typical invocation would be:
     * <pre>
     *   long start = diffInMicros(0);
     *   // do some heavy work
     *   long diff  = diffInMicros(start);
     * </pre>
     *
     * @param seed long the start time in nano seconds
     * @return long the difference seed - now in micro seconds
     */
    public static long diffInMicros(final long seed) {
        if (seed <= 0) {
            return System.nanoTime();
        }
        return (long) ((System.nanoTime() - seed) / NANO_TO_MICRO);
    }

    /**
     * Start a new sampling run.
     */
    public void beg() {
        container.set(new TimingContainer());
        final TimingContainer tc = container.get();
        tc.sumT = 0.0;
        tc.sumT2 = 0.0;
        tc.entries = 0L;
        tc.time = System.nanoTime();
        tc.begsample = tc.time;
    }

    /**
     * Sample a single time stamp.
     */
    public void sample() {
        double dT;
        long endsample;
        final long tstamp = System.nanoTime();
        final TimingContainer tc = container.get();
        endsample = tstamp - tc.begsample;
        tc.begsample = tstamp;
        tc.entries++;
        dT = endsample / NANO_TO_MICRO;
        tc.sumT += dT;
        tc.sumT2 += dT * dT;
    }

    /**
     * End a sampling run and calculate the running time since beginning.
     *
     * @return the running time in microseconds
     */
    public long end() {
        final TimingContainer tc = container.get();
        tc.time = diffInMicros(tc.time);
        return tc.time;
    }

    /**
     * Calculate the mean sampling rate in microseconds.
     *
     * @return mean value in in microseconds
     */
    public double getMean() {
        final TimingContainer tc = container.get();
        double m = 0;
        if (tc.entries > 0) {
            m = tc.sumT / tc.entries;
        }
        return m;
    }

    /**
     * Calculate the deviation of the mean sampling rate in microseconds.
     *
     * @return deviation value in microseconds
     */
    public double getDeviation() {
        final TimingContainer tc = container.get();
        double v = 0, m = 0;
        if (tc.entries > 0) {
            m = tc.sumT / tc.entries;
            v = tc.sumT2 / tc.entries;
            v = Math.sqrt(v - m * m);
        }
        return v;
    }

    /**
     * Get the number of sampling entries between beg and end call.
     *
     * @return sampling count
     */
    public long getEntries() {
        final TimingContainer tc = container.get();
        return tc.entries;
    }

    private static class TimingContainer {
        long entries, time, begsample;
        double sumT, sumT2;
    }

}
 