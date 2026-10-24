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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/util/sorted_interval_list.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a sorted list of disjoint, closed intervals. When an interval is inserted,
 * all intervals that overlap it or are adjacent to it are merged into one. I.e. [0,14] and [15,30]
 * will be merged to [0,30].
 */
public class SortedDisjointIntervalList {

  /** start of an interval to its end; the intervals are disjoint and non adjacent */
  private final TreeMap<Long, Long> intervals = new TreeMap<>();

  /** Creates an empty list. */
  public SortedDisjointIntervalList() {}

  /**
   * Creates a SortedDisjointIntervalList and fills it with intervals [starts[i]..ends[i]]. All
   * intervals must be consistent (starts[i] &lt;= ends[i]).
   */
  public SortedDisjointIntervalList(long[] starts, long[] ends) {
    insertIntervals(starts, ends);
  }

  /**
   * Creates a SortedDisjointIntervalList and fills it with intervals [starts[i]..ends[i]]. All
   * intervals must be consistent (starts[i] &lt;= ends[i]).
   */
  public SortedDisjointIntervalList(int[] starts, int[] ends) {
    insertIntervals(starts, ends);
  }

  /** Builds the complement of the interval list on the interval [start, end]. */
  public SortedDisjointIntervalList buildComplementOnInterval(long start, long end) {
    SortedDisjointIntervalList result = new SortedDisjointIntervalList();
    long nextStart = start;
    Map.Entry<Long, Long> first = firstIntervalGreaterOrEqual(start);
    if (first != null) {
      for (Map.Entry<Long, Long> e : intervals.tailMap(first.getKey(), true).entrySet()) {
        long nextEnd = Domain.capSub(e.getKey(), 1);
        if (nextEnd > end) {
          break;
        }
        if (nextStart <= nextEnd) {
          result.insertInterval(nextStart, nextEnd);
        }
        nextStart = Domain.capAdd(e.getValue(), 1);
      }
    }
    if (nextStart <= end) {
      result.insertInterval(nextStart, end);
    }
    return result;
  }

  /**
   * Adds the interval [start..end] to the list, and merges overlapping or immediately adjacent
   * intervals ([2, 5] and [6, 7] are adjacent, but [2, 5] and [7, 8] are not). An invalid
   * interval (start &gt; end) is ignored.
   */
  public void insertInterval(long start, long end) {
    if (start > end) {
      return;
    }
    long newStart = start;
    long newEnd = end;
    // merge with the previous interval if it overlaps or touches
    Map.Entry<Long, Long> prev = intervals.floorEntry(start);
    if (prev != null && (prev.getValue() == Long.MAX_VALUE || prev.getValue() + 1 >= start)) {
      newStart = prev.getKey();
      newEnd = Math.max(newEnd, prev.getValue());
      intervals.remove(prev.getKey());
    }
    // merge with all following intervals starting at or before end + 1
    while (true) {
      Map.Entry<Long, Long> next = intervals.ceilingEntry(newStart);
      if (next == null) {
        break;
      }
      if (newEnd != Long.MAX_VALUE && next.getKey() > newEnd + 1) {
        break;
      }
      newEnd = Math.max(newEnd, next.getValue());
      intervals.remove(next.getKey());
    }
    intervals.put(newStart, newEnd);
  }

  /** Adds all intervals [starts[i]..ends[i]]. Invalid intervals are ignored. */
  public void insertIntervals(long[] starts, long[] ends) {
    if (starts.length != ends.length) {
      throw new IllegalArgumentException("starts and ends have mismatched lengths");
    }
    for (int i = 0; i < starts.length; i++) {
      insertInterval(starts[i], ends[i]);
    }
  }

  /** Adds all intervals [starts[i]..ends[i]]. Invalid intervals are ignored. */
  public void insertIntervals(int[] starts, int[] ends) {
    if (starts.length != ends.length) {
      throw new IllegalArgumentException("starts and ends have mismatched lengths");
    }
    for (int i = 0; i < starts.length; i++) {
      insertInterval(starts[i], ends[i]);
    }
  }

  /** Returns the number of disjoint intervals in the list. */
  public int numIntervals() {
    return intervals.size();
  }

  /**
   * Returns the first interval containing or above the given value as {@code {start, end}}, or
   * null.
   */
  public long[] firstIntervalGreaterOrEqualBounds(long value) {
    Map.Entry<Long, Long> e = firstIntervalGreaterOrEqual(value);
    return e == null ? null : new long[] {e.getKey(), e.getValue()};
  }

  /**
   * Returns the last interval containing or below the given value as {@code {start, end}}, or
   * null.
   */
  public long[] lastIntervalLessOrEqualBounds(long value) {
    Map.Entry<Long, Long> e = intervals.floorEntry(value);
    return e == null ? null : new long[] {e.getKey(), e.getValue()};
  }

  private Map.Entry<Long, Long> firstIntervalGreaterOrEqual(long value) {
    Map.Entry<Long, Long> prev = intervals.floorEntry(value);
    if (prev != null && prev.getValue() >= value) {
      return prev;
    }
    return intervals.higherEntry(value);
  }

  /** Removes all intervals. */
  public void clear() {
    intervals.clear();
  }

  /** Returns the intervals as a {@link Domain}. */
  public Domain toDomain() {
    long[] flat = new long[intervals.size() * 2];
    int i = 0;
    for (Map.Entry<Long, Long> e : intervals.entrySet()) {
      flat[i++] = e.getKey();
      flat[i++] = e.getValue();
    }
    return Domain.fromFlatIntervals(flat);
  }

  /** Returns a string like "[1,4][6][10,20]" (empty for an empty list). */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Long, Long> e : intervals.entrySet()) {
      if (e.getKey().longValue() == e.getValue().longValue()) {
        sb.append('[').append(e.getKey()).append(']');
      } else {
        sb.append('[').append(e.getKey()).append(',').append(e.getValue()).append(']');
      }
    }
    return sb.toString();
  }
}
