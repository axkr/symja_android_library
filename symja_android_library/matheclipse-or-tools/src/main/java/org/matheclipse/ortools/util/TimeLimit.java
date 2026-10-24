// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/util/time_limit.h C++
// to Java (wall clock with safety buffer, deterministic time and one external interrupt flag),
// see NOTICE in the module root.

package org.matheclipse.ortools.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A time limit of the solvers of this module, the port of <code>operations_research::TimeLimit
 * </code>: a wall-clock limit started at construction, a deterministic time limit and an optional
 * external interrupt flag. The deterministic time is advanced explicitly by the algorithms.
 *
 * <p>
 * As in C++, the wall-clock part of {@link #limitReached()} returns <code>true</code> once the
 * remaining time is smaller than a safety buffer: the maximum of 0.1 ms and the largest interval
 * between two consecutive calls, so that a caller polling regularly stops before the deadline.
 * Once reached, the wall-clock limit stays reached. A limit of <code>10^9</code> seconds or more,
 * or <code>NaN</code>, is no limit.
 *
 * <p>
 * Not ported: the user-time option, the limits on memory, nested limits and several external
 * flags.
 */
@SuppressFBWarnings(
    value = {"AT_NONATOMIC_OPERATIONS_ON_SHARED_VARIABLE", "AT_STALE_THREAD_WRITE_OF_PRIMITIVE",
        "AT_NONATOMIC_64BIT_PRIMITIVE"},
    justification = "a time limit is used by one thread; only the external flag is shared")
public final class TimeLimit {

  private static final long SAFETY_BUFFER_NS = 100_000L;

  private final long startNs;
  private final double limitInSeconds;
  private final boolean infinite;
  private final long limitNs;
  private long lastNs;
  private long maxIntervalNs = SAFETY_BUFFER_NS;
  private boolean reached;

  private final double deterministicLimit;
  private double elapsedDeterministicTime;
  private AtomicBoolean externalLimit;

  /** Creates a wall-clock limit of <code>limitInSeconds</code> seconds from now (may be infinite). */
  public TimeLimit(double limitInSeconds) {
    this(limitInSeconds, Double.POSITIVE_INFINITY);
  }

  /**
   * Creates a wall-clock limit of <code>limitInSeconds</code> seconds from now and a limit of the
   * deterministic time; both may be infinite.
   */
  public TimeLimit(double limitInSeconds, double deterministicLimit) {
    this.startNs = System.nanoTime();
    this.lastNs = startNs;
    this.limitInSeconds = limitInSeconds;
    // NaN is treated as no limit
    this.infinite = !(limitInSeconds < 1.0e9);
    this.limitNs = infinite ? 0L : startNs + (long) (Math.max(0.0, limitInSeconds) * 1e9);
    this.deterministicLimit = deterministicLimit;
  }

  /** A limit that is never reached (unless an interrupt flag is registered). */
  public static TimeLimit infinite() {
    return new TimeLimit(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  /** Registers a flag; once it is <code>true</code>, the limit is reached. */
  public void registerExternalBooleanAsLimit(AtomicBoolean flag) {
    this.externalLimit = flag;
  }

  /** The wall-clock limit given at construction, in seconds. */
  public double getLimitInSeconds() {
    return limitInSeconds;
  }

  /**
   * Returns <code>true</code> if the external flag is set, the deterministic time is used up or the
   * wall-clock time is (about to be) reached.
   */
  public boolean limitReached() {
    if (externalLimit != null && externalLimit.get()) {
      return true;
    }
    if (getDeterministicTimeLeft() <= 0.0) {
      return true;
    }
    if (infinite) {
      return false;
    }
    if (reached) {
      return true;
    }
    final long currentNs = System.nanoTime();
    maxIntervalNs = Math.max(maxIntervalNs, currentNs - lastNs);
    lastNs = currentNs;
    if (currentNs + maxIntervalNs - limitNs >= 0) {
      // make sure that future calls return true
      reached = true;
      return true;
    }
    return false;
  }

  /** The elapsed wall time in seconds. */
  public double getElapsedTime() {
    return (System.nanoTime() - startNs) * 1e-9;
  }

  /** The remaining wall time in seconds (infinite for an infinite limit). */
  public double getTimeLeft() {
    if (infinite) {
      return Double.POSITIVE_INFINITY;
    }
    if (reached) {
      return 0.0;
    }
    return Math.max(0.0, (limitNs - System.nanoTime()) * 1e-9);
  }

  /** Advances the deterministic time. */
  public void advanceDeterministicTime(double delta) {
    elapsedDeterministicTime += delta;
  }

  /** The elapsed deterministic time. */
  public double getElapsedDeterministicTime() {
    return elapsedDeterministicTime;
  }

  /** The remaining deterministic time (infinite for an infinite limit). */
  public double getDeterministicTimeLeft() {
    return Math.max(0.0, deterministicLimit - elapsedDeterministicTime);
  }
}
