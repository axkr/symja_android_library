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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * We call <i>domain</i> any subset of Int64 = [Long.MIN_VALUE, Long.MAX_VALUE].
 *
 * <p>
 * This class represents such a set efficiently as a sorted and non-adjacent list of closed
 * intervals. All the operations are safe with respect to integer overflow. Instances are
 * immutable.
 *
 * <p>
 * The public methods have the names of the SWIG generated Java class of OR-Tools; the methods
 * which are only available in C++ are provided with the same lower camel case naming.
 */
public class Domain implements Comparable<Domain> {

  /** Above this number of values or intervals some operations return a relaxed superset. */
  public static final int DOMAIN_COMPLEXITY_LIMIT = 100;

  /** Flattened, sorted and non adjacent bounds {@code [s0, e0, s1, e1, ...]}. */
  private final long[] bounds;

  private Domain(long[] flatSortedNonAdjacent, boolean unused) {
    this.bounds = flatSortedNonAdjacent;
  }

  /** Creates the empty domain. */
  public Domain() {
    this.bounds = new long[0];
  }

  /** Constructor for the common case of a singleton domain. */
  public Domain(long value) {
    this.bounds = new long[] {value, value};
  }

  /**
   * Constructor for the common case of a single interval [left, right]. If left &gt; right, this
   * will result in the empty domain.
   */
  public Domain(long left, long right) {
    this.bounds = left > right ? new long[0] : new long[] {left, right};
  }

  /** Returns the full domain Int64. */
  public static Domain allValues() {
    return new Domain(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  /** Returns the domain [Long.MIN_VALUE, value]. */
  public static Domain lowerOrEqual(long value) {
    return new Domain(Long.MIN_VALUE, value);
  }

  /** Returns the domain [value, Long.MAX_VALUE]. */
  public static Domain greaterOrEqual(long value) {
    return new Domain(value, Long.MAX_VALUE);
  }

  /**
   * Creates a domain from the union of an unsorted list of integer values. Input values may be
   * repeated, with no consequence on the output.
   */
  public static Domain fromValues(long[] values) {
    long[] sorted = values.clone();
    Arrays.sort(sorted);
    LongList result = new LongList(sorted.length * 2);
    for (long v : sorted) {
      if (result.size() == 0 || v > result.last() + 1) {
        result.add(v);
        result.add(v);
      } else {
        result.setLast(v);
      }
      if (result.last() == Long.MAX_VALUE) {
        break;
      }
    }
    return new Domain(result.toArray(), true);
  }

  /**
   * Creates a domain from the union of an unsorted list of intervals ({@code long[][]}, each
   * entry either a single value {@code {v}} or two increasing values {@code {start, end}}).
   * Invalid intervals (start &gt; end) are ignored.
   */
  public static Domain fromIntervals(long[][] intervals) {
    long[] flat = new long[intervals.length * 2];
    for (int i = 0; i < intervals.length; i++) {
      long[] interval = intervals[i];
      if (interval.length == 1) {
        flat[2 * i] = interval[0];
        flat[2 * i + 1] = interval[0];
      } else if (interval.length == 2) {
        flat[2 * i] = interval[0];
        flat[2 * i + 1] = interval[1];
      } else {
        throw new IllegalArgumentException(
            "Domain.fromIntervals: an interval must have 1 or 2 values");
      }
    }
    return fromFlatIntervals(flat);
  }

  /**
   * Same as {@link #fromIntervals(long[][])} for a flattened representation (start, end, start,
   * end, ...). Invalid intervals (start &gt; end) are ignored.
   */
  public static Domain fromFlatIntervals(long[] flatIntervals) {
    if (flatIntervals.length % 2 != 0) {
      throw new IllegalArgumentException(
          "Domain.fromFlatIntervals: the number of bounds must be even");
    }
    List<long[]> list = new ArrayList<>(flatIntervals.length / 2);
    for (int i = 0; i < flatIntervals.length; i += 2) {
      if (flatIntervals[i] <= flatIntervals[i + 1]) {
        list.add(new long[] {flatIntervals[i], flatIntervals[i + 1]});
      }
    }
    return fromIntervalList(list, true);
  }

  /** Sorts (optionally) and merges a list of valid closed intervals. */
  private static Domain fromIntervalList(List<long[]> intervals, boolean sort) {
    if (sort) {
      intervals.sort(Domain::compareInterval);
    }
    LongList result = new LongList(intervals.size() * 2);
    for (long[] current : intervals) {
      if (result.size() == 0) {
        result.add(current[0]);
        result.add(current[1]);
        continue;
      }
      long end = result.last();
      if (end == Long.MAX_VALUE || current[0] <= end + 1) {
        result.setLast(Math.max(current[1], end));
      } else {
        result.add(current[0]);
        result.add(current[1]);
      }
    }
    return new Domain(result.toArray(), true);
  }

  private static int compareInterval(long[] a, long[] b) {
    if (a[0] != b[0]) {
      return a[0] < b[0] ? -1 : 1;
    }
    return Long.compare(a[1], b[1]);
  }

  /**
   * Returns the flattened list of interval bounds of the domain. Thus the domain {0, 1, 2, 5, 8,
   * 9, 10} will return [0, 2, 5, 5, 8, 10].
   */
  public long[] flattenedIntervals() {
    return bounds.clone();
  }

  /** Returns true if this is the empty set. */
  public boolean isEmpty() {
    return bounds.length == 0;
  }

  /** Returns the number of elements in the domain. It is capped at Long.MAX_VALUE. */
  public long size() {
    long size = 0;
    for (int i = 0; i < bounds.length; i += 2) {
      size = capAdd(size, capSub(bounds[i + 1], bounds[i]));
    }
    return capAdd(size, bounds.length / 2);
  }

  /** Returns true if the domain has exactly two values. */
  public boolean hasTwoValues() {
    if (bounds.length == 2) {
      return bounds[0] != Long.MIN_VALUE && bounds[0] + 1 == bounds[1];
    }
    return bounds.length == 4 && bounds[0] == bounds[1] && bounds[2] == bounds[3];
  }

  /** Returns the min value of the domain. The domain must not be empty. */
  public long min() {
    checkNotEmpty();
    return bounds[0];
  }

  /** Returns the max value of the domain. The domain must not be empty. */
  public long max() {
    checkNotEmpty();
    return bounds[bounds.length - 1];
  }

  /** Returns the value closest to zero. If there is a tie, it picks the positive one. */
  public long smallestValue() {
    checkNotEmpty();
    long result = min();
    for (int i = 0; i < bounds.length; i += 2) {
      long start = bounds[i];
      long end = bounds[i + 1];
      if (start <= 0 && end >= 0) {
        return 0;
      }
      for (long b : new long[] {start, end}) {
        if (b > 0 && b <= abs(result)) {
          result = b;
        }
        if (b < 0 && -b < abs(result)) {
          result = b;
        }
      }
    }
    return result;
  }

  /** Returns the value closest to {@code wanted}. If there is a tie, the larger one is picked. */
  public long closestValue(long wanted) {
    checkNotEmpty();
    if (wanted <= bounds[0]) {
      return bounds[0];
    }
    long bestPoint = 0;
    long bestDistance = 0;
    for (int i = 0; i < bounds.length; i += 2) {
      long start = bounds[i];
      long end = bounds[i + 1];
      if (start <= wanted && wanted <= end) {
        return wanted;
      } else if (start >= wanted) {
        return capSub(start, wanted) <= bestDistance ? start : bestPoint;
      } else {
        bestPoint = end;
        bestDistance = capSub(wanted, end);
      }
    }
    return bestPoint;
  }

  /**
   * Returns the largest value of the domain which is &lt;= input, or input itself if there is no
   * such value.
   */
  public long valueAtOrBefore(long input) {
    int it = upperBoundIndex(input);
    if (it == 0) {
      return input;
    }
    --it;
    return input <= bounds[2 * it + 1] ? input : bounds[2 * it + 1];
  }

  /**
   * Returns the smallest value of the domain which is &gt;= input, or input itself if there is no
   * such value.
   */
  public long valueAtOrAfter(long input) {
    int n = bounds.length / 2;
    int it = upperBoundIndex(input);
    if (it == n) {
      return input;
    }
    long candidate = bounds[2 * it];
    if (it == 0) {
      return candidate;
    }
    --it;
    return input <= bounds[2 * it + 1] ? input : candidate;
  }

  /** Returns the interval of the domain containing zero, or the empty domain. */
  public Domain partAroundZero() {
    for (int i = 0; i < bounds.length; i += 2) {
      if (bounds[i] <= 0 && bounds[i + 1] >= 0) {
        return new Domain(bounds[i], bounds[i + 1]);
      }
    }
    return new Domain();
  }

  /** Returns true iff the domain is reduced to a single value. The domain must not be empty. */
  public boolean isFixed() {
    return min() == max();
  }

  /** Returns the value of a fixed domain. */
  public long fixedValue() {
    if (!isFixed()) {
      throw new IllegalStateException("Domain.fixedValue: the domain is not fixed: " + this);
    }
    return bounds[0];
  }

  /** Returns true iff value is in Domain. */
  public boolean contains(long value) {
    int it = upperBoundIndex(value);
    if (it == 0) {
      return false;
    }
    --it;
    return value <= bounds[2 * it + 1];
  }

  /** Returns the distance from the value to the domain (0 if it is contained). */
  public long distance(long value) {
    long minDistance = Long.MAX_VALUE;
    for (int i = 0; i < bounds.length; i += 2) {
      if (value >= bounds[i] && value <= bounds[i + 1]) {
        return 0;
      }
      if (bounds[i] > value) {
        minDistance = Math.min(minDistance, bounds[i] - value);
        break;
      } else {
        minDistance = value - bounds[i + 1];
      }
    }
    return minDistance;
  }

  /** Returns true iff D is included in the given domain. */
  public boolean isIncludedIn(Domain domain) {
    int i = 0;
    long[] others = domain.bounds;
    int othersSize = others.length / 2;
    for (int k = 0; k < bounds.length; k += 2) {
      long start = bounds[k];
      long end = bounds[k + 1];
      for (; i < othersSize && end > others[2 * i + 1]; ++i) {
        // find the unique interval in others that contains this interval
      }
      if (i == othersSize) {
        return false;
      }
      if (start < others[2 * i]) {
        return false;
      }
    }
    return true;
  }

  /** Returns true iff D overlaps with the given domain, that is, the intersection is not empty. */
  public boolean overlapsWith(Domain domain) {
    long[] a = bounds;
    long[] b = domain.bounds;
    int na = a.length / 2;
    int nb = b.length / 2;
    for (int i = 0, j = 0; i < na && j < nb;) {
      if (a[2 * i] <= b[2 * j]) {
        if (a[2 * i + 1] < b[2 * j]) {
          ++i;
        } else {
          return true;
        }
      } else {
        if (b[2 * j + 1] < a[2 * i]) {
          ++j;
        } else {
          return true;
        }
      }
    }
    return false;
  }

  /** Returns the set Int64 &#8726; D. */
  public Domain complement() {
    LongList result = new LongList(bounds.length + 2);
    long nextStart = Long.MIN_VALUE;
    for (int i = 0; i < bounds.length; i += 2) {
      if (bounds[i] != Long.MIN_VALUE) {
        result.add(nextStart);
        result.add(bounds[i] - 1);
      }
      if (bounds[i + 1] == Long.MAX_VALUE) {
        return new Domain(result.toArray(), true);
      }
      nextStart = bounds[i + 1] + 1;
    }
    result.add(nextStart);
    result.add(Long.MAX_VALUE);
    return new Domain(result.toArray(), true);
  }

  /**
   * Returns {x &#8712; Int64, &#8707; e &#8712; D, x = -e}. Note in particular that if the
   * negation of Int64 is not Int64 but Int64 &#8726; {Long.MIN_VALUE}.
   */
  public Domain negation() {
    return new Domain(negate(bounds), true);
  }

  private static long[] negate(long[] b) {
    int n = b.length / 2;
    if (n == 0) {
      return b;
    }
    // reversed intervals
    LongList result = new LongList(b.length);
    for (int i = n - 1; i >= 0; i--) {
      long start = b[2 * i];
      long end = b[2 * i + 1];
      if (i == 0 && end == Long.MIN_VALUE) {
        // corner case: the interval [MIN, MIN] disappears
        break;
      }
      long newStart = end == Long.MIN_VALUE ? Long.MAX_VALUE : -end;
      long newEnd = start == Long.MIN_VALUE ? Long.MAX_VALUE : -start;
      result.add(newStart);
      result.add(newEnd);
    }
    return result.toArray();
  }

  /** Returns the intersection of D and domain. */
  public Domain intersectionWith(Domain domain) {
    LongList result = new LongList(Math.max(bounds.length, domain.bounds.length));
    long[] a = bounds;
    long[] b = domain.bounds;
    int na = a.length / 2;
    int nb = b.length / 2;
    for (int i = 0, j = 0; i < na && j < nb;) {
      long as = a[2 * i];
      long ae = a[2 * i + 1];
      long bs = b[2 * j];
      long be = b[2 * j + 1];
      if (as <= bs) {
        if (ae < bs) {
          ++i;
        } else {
          if (ae <= be) {
            result.add(bs);
            result.add(ae);
            ++i;
          } else {
            result.add(bs);
            result.add(be);
            ++j;
          }
        }
      } else {
        if (be < as) {
          ++j;
        } else {
          if (be <= ae) {
            result.add(as);
            result.add(be);
            ++j;
          } else {
            result.add(as);
            result.add(ae);
            ++i;
          }
        }
      }
    }
    return new Domain(result.toArray(), true);
  }

  /** Returns the union of D and domain. */
  public Domain unionWith(Domain domain) {
    List<long[]> list = new ArrayList<>(bounds.length / 2 + domain.bounds.length / 2);
    addIntervals(list, bounds);
    addIntervals(list, domain.bounds);
    return fromIntervalList(list, true);
  }

  private static void addIntervals(List<long[]> list, long[] b) {
    for (int i = 0; i < b.length; i += 2) {
      list.add(new long[] {b[i], b[i + 1]});
    }
  }

  /** Returns {x &#8712; Int64, &#8707; a &#8712; D, &#8707; b &#8712; domain, x = a + b}. */
  public Domain additionWith(Domain domain) {
    long[] a = bounds;
    long[] b = domain.bounds;
    List<long[]> list = new ArrayList<>((a.length / 2) * (b.length / 2));
    for (int k = 0; k < a.length; k += 2) {
      long is = a[k];
      long ie = a[k + 1];
      for (int l = 0; l < b.length; l += 2) {
        long js = b[l];
        long je = b[l + 1];
        if (is > 0 && js > 0) {
          if (addOverflows(is, js)) {
            continue;
          }
          list.add(new long[] {is + js, capAdd(ie, je)});
        } else if (ie < 0 && je < 0) {
          if (addOverflows(ie, je)) {
            continue;
          }
          list.add(new long[] {capAdd(is, js), ie + je});
        } else {
          list.add(new long[] {capAdd(is, js), capAdd(ie, je)});
        }
      }
    }
    return fromIntervalList(list, true);
  }

  /**
   * Returns a superset of {x &#8712; Int64, &#8707; e &#8712; D, x = e * coeff}. If the domain is
   * too large ({@link #DOMAIN_COMPLEXITY_LIMIT} values), the result is
   * {@link #continuousMultiplicationBy(long)}.
   */
  public Domain multiplicationBy(long coeff) {
    if (bounds.length == 0) {
      return new Domain();
    }
    if (coeff == 0) {
      return new Domain(0);
    }
    long absCoeff = abs(coeff);
    long sizeIfNonTrivial = absCoeff > 1 ? size() : 0;
    if (sizeIfNonTrivial > DOMAIN_COMPLEXITY_LIMIT) {
      return continuousMultiplicationBy(coeff);
    }
    long[] result;
    if (absCoeff > 1) {
      long maxValue = Long.MAX_VALUE / absCoeff;
      long minValue = Long.MIN_VALUE / absCoeff;
      LongList list = new LongList((int) sizeIfNonTrivial * 2);
      for (int i = 0; i < bounds.length; i += 2) {
        for (long v = bounds[i];; ++v) {
          if (v >= minValue && v <= maxValue) {
            long newValue = v * absCoeff;
            list.add(newValue);
            list.add(newValue);
          }
          if (v == bounds[i + 1]) {
            break;
          }
        }
      }
      result = list.toArray();
    } else {
      result = bounds;
    }
    if (coeff < 0) {
      result = negate(result);
    }
    return new Domain(result, true);
  }

  /** Returns a smaller representation of this domain if it has too many intervals. */
  public Domain relaxIfTooComplex() {
    if (numIntervals() > DOMAIN_COMPLEXITY_LIMIT) {
      return new Domain(min(), max());
    }
    return this;
  }

  /**
   * Returns a superset of multiplicationBy() to avoid the explosion in the representation size:
   * each interval [a, b] is mapped to [a * coeff, b * coeff].
   */
  public Domain continuousMultiplicationBy(long coeff) {
    long absCoeff = abs(coeff);
    List<long[]> list = new ArrayList<>(bounds.length / 2);
    for (int i = 0; i < bounds.length; i += 2) {
      list.add(new long[] {capProd(bounds[i], absCoeff), capProd(bounds[i + 1], absCoeff)});
    }
    Domain result = fromIntervalList(list, false);
    return coeff < 0 ? result.negation() : result;
  }

  /**
   * Returns a superset of {x &#8712; Int64, &#8707; e1 &#8712; D, &#8707; e2 &#8712; domain, x =
   * e1 * e2}.
   */
  public Domain continuousMultiplicationBy(Domain domain) {
    List<long[]> list = new ArrayList<>();
    for (int k = 0; k < bounds.length; k += 2) {
      for (int l = 0; l < domain.bounds.length; l += 2) {
        long a = capProd(bounds[k], domain.bounds[l]);
        long b = capProd(bounds[k + 1], domain.bounds[l + 1]);
        long c = capProd(bounds[k], domain.bounds[l + 1]);
        long d = capProd(bounds[k + 1], domain.bounds[l]);
        list.add(new long[] {Math.min(Math.min(a, b), Math.min(c, d)),
            Math.max(Math.max(a, b), Math.max(c, d))});
      }
    }
    return fromIntervalList(list, true);
  }

  /**
   * Returns {x &#8712; Int64, &#8707; e &#8712; D, x = e / coeff} with the division rounded
   * towards zero. {@code coeff} must not be zero.
   */
  public Domain divisionBy(long coeff) {
    if (coeff == 0) {
      throw new ArithmeticException("Domain.divisionBy: zero coefficient");
    }
    long absCoeff = abs(coeff);
    List<long[]> list = new ArrayList<>(bounds.length / 2);
    for (int i = 0; i < bounds.length; i += 2) {
      list.add(new long[] {bounds[i] / absCoeff, bounds[i + 1] / absCoeff});
    }
    Domain result = fromIntervalList(list, false);
    return coeff < 0 ? result.negation() : result;
  }

  /**
   * Returns {x &#8712; Int64, &#8707; e &#8712; D, x * coeff = e}. For coeff == 0 this returns
   * Int64 if 0 is in D and the empty set otherwise.
   */
  public Domain inverseMultiplicationBy(long coeff) {
    if (coeff == 0) {
      return contains(0) ? allValues() : new Domain();
    }
    long absCoeff = abs(coeff);
    LongList list = new LongList(bounds.length);
    for (int i = 0; i < bounds.length; i += 2) {
      long start = ceilRatio(bounds[i], absCoeff);
      long end = floorRatio(bounds[i + 1], absCoeff);
      if (start > end) {
        continue;
      }
      if (list.size() > 0 && start == list.last() + 1) {
        list.setLast(end);
      } else {
        list.add(start);
        list.add(end);
      }
    }
    long[] result = list.toArray();
    if (coeff < 0) {
      result = negate(result);
    }
    return new Domain(result, true);
  }

  /**
   * Returns a superset of {x &#8712; Int64, &#8707; e &#8712; D, &#8707; m &#8712; modulo, x = e
   * % m } where the remainder has the sign of e. All values of modulo must be positive.
   */
  public Domain positiveModuloBySuperset(Domain modulo) {
    if (isEmpty()) {
      return new Domain();
    }
    if (modulo.min() <= 0) {
      throw new IllegalArgumentException("Domain.positiveModuloBySuperset: modulo must be > 0");
    }
    long maxMod = modulo.max() - 1;
    if (max() >= 0 && min() <= 0) {
      return new Domain(Math.max(min(), -maxMod), Math.min(max(), maxMod));
    }
    if (min() > 0) {
      return moduloHelper(min(), max(), modulo);
    }
    return moduloHelper(-max(), -min(), modulo).negation();
  }

  private static Domain moduloHelper(long min, long max, Domain modulo) {
    long maxMod = modulo.max() - 1;
    if (modulo.min() == modulo.max()) {
      long size = max - min;
      long v1 = min % modulo.max();
      if (v1 + size > maxMod) {
        return new Domain(0, maxMod);
      }
      return new Domain(v1, v1 + size);
    }
    return new Domain(0, Math.min(max, maxMod));
  }

  /**
   * Returns a superset of {x &#8712; Int64, &#8707; e &#8712; D, &#8707; d &#8712; divisor
   * &#8726; {0}, x = e / d } (division rounded towards zero). The divisor may contain negative
   * values and zero; the result is empty if D or divisor is empty or if divisor is {0}.
   */
  public Domain divisionBySuperset(Domain divisor) {
    if (isEmpty() || divisor.isEmpty()) {
      return new Domain();
    }
    // The division takes its extreme values either on the extreme values of the dividend and the
    // extreme values of the divisor or the closest the divisor can be to zero.
    if (divisor.min() == 0 && divisor.max() == 0) {
      return new Domain();
    }
    if (divisor.min() >= 0) {
      return positiveDivisionSuperset(min(), max(), Math.max(divisor.min(), 1), divisor.max());
    }
    if (divisor.max() <= 0) {
      return negativeDivisionSuperset(min(), max(), divisor.min(), Math.min(divisor.max(), -1));
    }
    return negativeDivisionSuperset(min(), max(), divisor.min(), divisor.valueAtOrBefore(-1))
        .unionWith(positiveDivisionSuperset(min(), max(), divisor.valueAtOrAfter(1),
            divisor.max()));
  }

  // rmin > 0, rmin <= rmax
  private static Domain positiveDivisionSuperset(long lmin, long lmax, long rmin, long rmax) {
    return new Domain(Math.min(lmin / rmax, lmin / rmin), Math.max(lmax / rmin, lmax / rmax));
  }

  // rmax < 0, rmin <= rmax
  private static Domain negativeDivisionSuperset(long lmin, long lmax, long rmin, long rmax) {
    return new Domain(Math.min(safeDiv(lmax, rmin), safeDiv(lmax, rmax)),
        Math.max(safeDiv(lmin, rmax), safeDiv(lmin, rmin)));
  }

  private static long safeDiv(long a, long b) {
    return (a == Long.MIN_VALUE && b == -1) ? Long.MAX_VALUE : a / b;
  }

  /** Returns a superset of {x &#8712; Int64, &#8707; y &#8712; D, x = y * y }. */
  public Domain squareSuperset() {
    if (isEmpty()) {
      return new Domain();
    }
    Domain positive = new Domain(0, Long.MAX_VALUE);
    Domain absDomain = intersectionWith(positive).unionWith(negation().intersectionWith(positive));
    if (absDomain.size() >= DOMAIN_COMPLEXITY_LIMIT) {
      List<long[]> list = new ArrayList<>();
      for (int i = 0; i < absDomain.bounds.length; i += 2) {
        list.add(new long[] {capProd(absDomain.bounds[i], absDomain.bounds[i]),
            capProd(absDomain.bounds[i + 1], absDomain.bounds[i + 1])});
      }
      return fromIntervalList(list, false);
    }
    LongList values = new LongList((int) absDomain.size());
    for (int i = 0; i < absDomain.bounds.length; i += 2) {
      for (long v = absDomain.bounds[i]; v <= absDomain.bounds[i + 1]; v++) {
        values.add(capProd(v, v));
      }
    }
    return fromValues(values.toArray());
  }

  /**
   * Returns a superset of {x &#8712; Int64, &#8707; y &#8712; D, x = (a*y + b)*(c*y + d) }.
   */
  public Domain quadraticSuperset(long a, long b, long c, long d) {
    if (isEmpty()) {
      return new Domain();
    }
    if (size() < DOMAIN_COMPLEXITY_LIMIT) {
      LongList values = new LongList((int) size());
      for (int i = 0; i < bounds.length; i += 2) {
        for (long v = bounds[i]; v <= bounds[i + 1]; v++) {
          values.add(capProd(capAdd(capProd(a, v), b), capAdd(capProd(c, v), d)));
        }
      }
      return fromValues(values.toArray());
    }
    if (a == 0) {
      return multiplicationBy(capProd(c, b)).additionWith(new Domain(capProd(d, b)));
    }
    if (c == 0) {
      return multiplicationBy(capProd(a, d)).additionWith(new Domain(capProd(d, b)));
    }
    List<long[]> list = new ArrayList<>();
    for (int i = 0; i < bounds.length; i += 2) {
      list.add(evaluateQuadraticProdInterval(a, b, c, d, bounds[i], bounds[i + 1]));
    }
    return fromIntervalList(list, true);
  }

  // Implementation of quadraticSuperset() for a single interval. We have
  // (a*x + b)(c*x + d) = a*c*x*x + (a*d + b*c)*x + b*d; the minimum or maximum is at
  // x = -(a*d + b*c)/(2*a*c). The extreme values of the expression happen when x is one of:
  // variable_min, variable_max, the closest domain value lower or equal to the parabola extreme,
  // the closest domain value higher than the parabola extreme. Each candidate is only used if it
  // is in [variableMin, variableMax].
  private static long[] evaluateQuadraticProdInterval(long a, long b, long c, long d,
      long variableMin, long variableMax) {
    java.math.BigInteger bigA = java.math.BigInteger.valueOf(a);
    java.math.BigInteger bigB = java.math.BigInteger.valueOf(b);
    java.math.BigInteger bigC = java.math.BigInteger.valueOf(c);
    java.math.BigInteger bigD = java.math.BigInteger.valueOf(d);
    java.math.BigInteger nominator = bigA.multiply(bigD).negate().subtract(bigB.multiply(bigC));
    java.math.BigInteger denominator = bigA.multiply(bigC);
    java.math.BigInteger evaluatedMinimumPoint =
        nominator.divide(denominator).divide(java.math.BigInteger.valueOf(2));
    long atMinX = capProd(capAdd(capProd(a, variableMin), b), capAdd(capProd(c, variableMin), d));
    long atMaxX = capProd(capAdd(capProd(a, variableMax), b), capAdd(capProd(c, variableMax), d));
    long minVar = Math.min(atMinX, atMaxX);
    long maxVar = Math.max(atMinX, atMaxX);
    // like the C++ static_cast<int64_t>(int128): the low 64 bits
    long pointAtMinimum = evaluatedMinimumPoint.longValue();
    int rounder = (nominator.signum() > 0) == (denominator.signum() > 0) ? 1 : -1;
    if (pointAtMinimum >= variableMin && pointAtMinimum <= variableMax) {
      long point1 =
          capProd(capAdd(capProd(a, pointAtMinimum), b), capAdd(capProd(c, pointAtMinimum), d));
      minVar = Math.min(minVar, point1);
      maxVar = Math.max(maxVar, point1);
    }
    long point2X = pointAtMinimum + rounder;
    if (point2X >= variableMin && point2X <= variableMax) {
      long point2 = capProd(capAdd(capProd(a, point2X), b), capAdd(capProd(c, point2X), d));
      minVar = Math.min(minVar, point2);
      maxVar = Math.max(maxVar, point2);
    }
    return new long[] {minVar, maxVar};
  }

  /**
   * If D &#8726; other is a singleton, returns its value, otherwise an empty
   * {@link java.util.OptionalLong}. This is similar to checking whether
   * {@code intersectionWith(other.complement())} is fixed, without allocating domains.
   */
  public java.util.OptionalLong uniqueValueNotIn(Domain other) {
    java.util.OptionalLong result = java.util.OptionalLong.empty();
    int i = 0;
    long[] ex = other.bounds;
    int exSize = ex.length / 2;
    for (int k = 0; k < bounds.length; k += 2) {
      long start = bounds[k];
      long end = bounds[k + 1];
      while (start <= end) {
        // find the first interval with ex[i].end >= start
        while (i < exSize && ex[2 * i + 1] < start) {
          ++i;
        }
        if (i < exSize) {
          if (ex[2 * i] <= start) {
            if (ex[2 * i + 1] == Long.MAX_VALUE) {
              return result;
            }
            start = ex[2 * i + 1] + 1;
            continue;
          }
          // if end > start, there is no overflow here
          if (end == start || ex[2 * i] == start + 1) {
            if (result.isPresent()) {
              return java.util.OptionalLong.empty();
            }
            result = java.util.OptionalLong.of(start);
            if (ex[2 * i + 1] == Long.MAX_VALUE) {
              return result;
            }
            start = ex[2 * i + 1] + 1;
            continue;
          }
          return java.util.OptionalLong.empty();
        } else if (end == start) {
          if (result.isPresent()) {
            return java.util.OptionalLong.empty();
          }
          result = java.util.OptionalLong.of(start);
          break; // next interval
        } else {
          return java.util.OptionalLong.empty();
        }
      }
    }
    return result;
  }

  /**
   * Advanced usage. Given some <i>implied</i> information on this domain that is assumed to be
   * always true (i.e. only values in the intersection with implied domain matter), this function
   * returns a simpler domain that has the same intersection with the implied domain.
   */
  public Domain simplifyUsingImpliedDomain(Domain impliedDomain) {
    LongList result = new LongList(bounds.length);
    if (impliedDomain.isEmpty()) {
      return new Domain();
    }
    long[] implied = impliedDomain.bounds;
    int impliedSize = implied.length / 2;
    int i = 0;
    long minPoint = 0;
    long maxPoint = 0;
    boolean started = false;
    for (int k = 0; k < bounds.length; k += 2) {
      long start = bounds[k];
      long end = bounds[k + 1];
      if (started && implied[2 * i] < start) {
        result.add(minPoint);
        result.add(maxPoint);
        started = false;
      }
      for (; i < impliedSize; ++i) {
        long cs = implied[2 * i];
        long ce = implied[2 * i + 1];
        if (ce >= start && cs <= end) {
          long interMax = Math.min(end, ce);
          if (!started) {
            started = true;
            minPoint = Math.max(start, cs);
            maxPoint = interMax;
          } else {
            maxPoint = interMax;
          }
        }
        if (ce > end) {
          break;
        }
      }
      if (i == impliedSize) {
        break;
      }
    }
    if (started) {
      result.add(minPoint);
      result.add(maxPoint);
    }
    return new Domain(result.toArray(), true);
  }

  /** Returns the number of disjoint intervals of the domain. */
  public int numIntervals() {
    return bounds.length / 2;
  }

  /** Returns the start of the i-th interval. */
  public long intervalStart(int i) {
    return bounds[2 * i];
  }

  /** Returns the end of the i-th interval. */
  public long intervalEnd(int i) {
    return bounds[2 * i + 1];
  }

  /**
   * Returns the sum of the k smallest values of the domain (all values if the domain has fewer
   * than k values).
   */
  public static long sumOfKMinValueInDomain(Domain domain, int k) {
    long currentSum = 0;
    int currentIndex = 0;
    for (int i = 0; i < domain.bounds.length; i += 2) {
      if (currentIndex >= k) {
        break;
      }
      for (long v = domain.bounds[i]; v <= domain.bounds[i + 1]; ++v) {
        if (currentIndex >= k) {
          break;
        }
        currentIndex++;
        currentSum += v;
        if (v == Long.MAX_VALUE) {
          break;
        }
      }
    }
    return currentSum;
  }

  /** Returns the sum of the k largest values of the domain. */
  public static long sumOfKMaxValueInDomain(Domain domain, int k) {
    return -sumOfKMinValueInDomain(domain.negation(), k);
  }

  /** Returns a compact string of a vector of intervals like "[1,4][6][10,20]". */
  @Override
  public String toString() {
    if (bounds.length == 0) {
      return "[]";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bounds.length; i += 2) {
      if (bounds[i] == bounds[i + 1]) {
        sb.append('[').append(bounds[i]).append(']');
      } else {
        sb.append('[').append(bounds[i]).append(',').append(bounds[i + 1]).append(']');
      }
    }
    return sb.toString();
  }

  /** Lexicographic order on the flattened interval bounds. */
  @Override
  public int compareTo(Domain other) {
    long[] d1 = bounds;
    long[] d2 = other.bounds;
    int common = Math.min(d1.length, d2.length);
    for (int i = 0; i < common; i++) {
      if (d1[i] != d2[i]) {
        return d1[i] < d2[i] ? -1 : 1;
      }
    }
    return Integer.compare(d1.length, d2.length);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Domain)) {
      return false;
    }
    return Arrays.equals(bounds, ((Domain) obj).bounds);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bounds);
  }

  // index of the first interval whose start is > value (intervals are ordered by start only)
  private int upperBoundIndex(long value) {
    int lo = 0;
    int hi = bounds.length / 2;
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      if (bounds[2 * mid] <= value) {
        lo = mid + 1;
      } else {
        hi = mid;
      }
    }
    return lo;
  }

  private void checkNotEmpty() {
    if (bounds.length == 0) {
      throw new IllegalStateException("empty Domain");
    }
  }

  // Saturated arithmetic, see ortools/util/saturated_arithmetic.h.

  /** Returns a + b saturated at Long.MIN_VALUE / Long.MAX_VALUE. */
  public static long capAdd(long a, long b) {
    long r = a + b;
    if (((a ^ r) & (b ^ r)) < 0) {
      return a < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return r;
  }

  /** Returns a - b saturated at Long.MIN_VALUE / Long.MAX_VALUE. */
  public static long capSub(long a, long b) {
    long r = a - b;
    if (((a ^ b) & (a ^ r)) < 0) {
      return a < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return r;
  }

  /** Returns a * b saturated at Long.MIN_VALUE / Long.MAX_VALUE. */
  public static long capProd(long a, long b) {
    long hi = Math.multiplyHigh(a, b);
    long lo = a * b;
    if ((hi == 0 && lo >= 0) || (hi == -1 && lo < 0)) {
      return lo;
    }
    return ((a < 0) != (b < 0)) ? Long.MIN_VALUE : Long.MAX_VALUE;
  }

  private static boolean addOverflows(long a, long b) {
    long r = a + b;
    return ((a ^ r) & (b ^ r)) < 0;
  }

  private static long abs(long v) {
    return v == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(v);
  }

  private static long ceilRatio(long value, long positiveCoeff) {
    long result = value / positiveCoeff;
    long adjust = result * positiveCoeff < value ? 1 : 0;
    return result + adjust;
  }

  private static long floorRatio(long value, long positiveCoeff) {
    long result = value / positiveCoeff;
    long adjust = result * positiveCoeff > value ? 1 : 0;
    return result - adjust;
  }

  /** Growable list of primitive longs. */
  private static final class LongList {
    private long[] data;
    private int size;

    LongList(int capacity) {
      data = new long[Math.max(2, capacity)];
    }

    void add(long v) {
      if (size == data.length) {
        data = Arrays.copyOf(data, data.length * 2);
      }
      data[size++] = v;
    }

    long last() {
      return data[size - 1];
    }

    void setLast(long v) {
      data[size - 1] = v;
    }

    int size() {
      return size;
    }

    long[] toArray() {
      return Arrays.copyOf(data, size);
    }
  }
}
