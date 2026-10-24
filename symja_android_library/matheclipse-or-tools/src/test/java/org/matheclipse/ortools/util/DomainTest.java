package org.matheclipse.ortools.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Domain} and {@link SortedDisjointIntervalList}: fixed examples and random
 * domains compared with a set of values.
 */
public class DomainTest {
  private static final int LO = -12;
  private static final int HI = 12;

  @Test
  public void testConstructorsAndToString() {
    assertEquals("[]", new Domain().toString());
    assertEquals("[5]", new Domain(5).toString());
    assertEquals("[1,3]", new Domain(1, 3).toString());
    assertTrue(new Domain(3, 1).isEmpty());
    assertEquals("[0,2][5]", Domain.fromValues(new long[] {5, 0, 2, 1, 1}).toString());
    assertEquals("[0,2][5]", Domain.fromIntervals(new long[][] {{0, 2}, {5}}).toString());
    assertEquals("[-3,4][7,9]",
        Domain.fromFlatIntervals(new long[] {7, 9, -3, 0, 1, 4, 10, 8}).toString());
    assertArrayEquals(new long[] {0, 2, 5, 5}, Domain.fromValues(new long[] {0, 1, 2, 5})
        .flattenedIntervals());
    assertEquals("[" + Long.MIN_VALUE + "," + Long.MAX_VALUE + "]", Domain.allValues().toString());
    assertEquals(Long.MAX_VALUE, Domain.allValues().size());
    assertEquals(Long.MIN_VALUE, Domain.lowerOrEqual(4).min());
    assertEquals(4, Domain.lowerOrEqual(4).max());
    assertEquals(Long.MAX_VALUE, Domain.greaterOrEqual(4).max());
  }

  @Test
  public void testExtremeValues() {
    Domain all = Domain.allValues();
    assertTrue(all.complement().isEmpty());
    assertEquals(all, new Domain().complement());
    // the negation of [MIN, MIN] is empty, of [MIN, 0] it is [0, MAX]
    assertTrue(new Domain(Long.MIN_VALUE).negation().isEmpty());
    assertEquals(new Domain(0, Long.MAX_VALUE), new Domain(Long.MIN_VALUE, 0).negation());
    assertEquals("[" + (Long.MIN_VALUE + 1) + "," + Long.MAX_VALUE + "]", all.negation().toString());
    // additions saturate
    assertEquals(Long.MAX_VALUE,
        new Domain(Long.MAX_VALUE - 10, Long.MAX_VALUE - 1).additionWith(new Domain(0, 5)).max());
    // the sum of two positive intervals whose smallest sum overflows is empty
    assertTrue(new Domain(Long.MAX_VALUE - 1).additionWith(new Domain(5)).isEmpty());
    assertEquals(Long.MAX_VALUE, Domain.capAdd(Long.MAX_VALUE, 1));
    assertEquals(Long.MIN_VALUE, Domain.capSub(Long.MIN_VALUE, 1));
    assertEquals(Long.MAX_VALUE, Domain.capProd(Long.MAX_VALUE / 2, 3));
    assertEquals(Long.MIN_VALUE, Domain.capProd(Long.MAX_VALUE / 2, -3));
    assertEquals(42, Domain.capProd(6, 7));
  }

  private static Domain randomDomain(Random r) {
    List<Long> values = new ArrayList<>();
    int mode = r.nextInt(4);
    for (long x = LO; x <= HI; x++) {
      if (mode == 0 ? r.nextInt(4) == 0 : r.nextInt(3) > 0) {
        values.add(x);
      }
    }
    long[] vs = new long[values.size()];
    for (int i = 0; i < vs.length; i++) {
      vs[i] = values.get(i);
    }
    return Domain.fromValues(vs);
  }

  private static TreeSet<Long> values(Domain d, long lo, long hi) {
    TreeSet<Long> s = new TreeSet<>();
    for (long x = lo; x <= hi; x++) {
      if (d.contains(x)) {
        s.add(x);
      }
    }
    return s;
  }

  private static void assertCanonical(Domain d) {
    long[] f = d.flattenedIntervals();
    assertEquals(0, f.length % 2);
    for (int i = 0; i < f.length; i += 2) {
      assertTrue(f[i] <= f[i + 1]);
      if (i + 2 < f.length) {
        assertTrue(f[i + 1] + 1 < f[i + 2], "adjacent intervals in " + d);
      }
    }
  }

  @Test
  public void testRandomSetOperations() {
    Random r = new Random(42);
    for (int iter = 0; iter < 400; iter++) {
      Domain a = randomDomain(r);
      Domain b = randomDomain(r);
      TreeSet<Long> sa = values(a, LO, HI);
      TreeSet<Long> sb = values(b, LO, HI);
      assertEquals(sa.size(), a.size());
      if (!sa.isEmpty()) {
        assertEquals((long) sa.first(), a.min());
        assertEquals((long) sa.last(), a.max());
      }
      Domain inter = a.intersectionWith(b);
      Domain union = a.unionWith(b);
      Domain comp = a.complement();
      Domain neg = a.negation();
      Domain sum = a.additionWith(b);
      for (Domain d : new Domain[] {inter, union, comp, neg, sum}) {
        assertCanonical(d);
      }
      for (long x = 3 * LO; x <= 3 * HI; x++) {
        boolean ina = sa.contains(x);
        boolean inb = sb.contains(x);
        assertEquals(ina && inb, inter.contains(x));
        assertEquals(ina || inb, union.contains(x));
        assertEquals(!ina, comp.contains(x));
        assertEquals(sa.contains(-x), neg.contains(x));
        boolean s = false;
        for (long y : sa) {
          s |= sb.contains(x - y);
        }
        assertEquals(s, sum.contains(x), a + " + " + b + " at " + x);
      }
      assertEquals(sb.containsAll(sa), a.isIncludedIn(b));
      TreeSet<Long> common = new TreeSet<>(sa);
      common.retainAll(sb);
      assertEquals(!common.isEmpty(), a.overlapsWith(b));
      assertEquals(sa.size() == 2, a.hasTwoValues());
      // ordering and equality are consistent with the flat representation
      assertEquals(a.equals(b), sa.equals(sb));
      assertEquals(a.compareTo(b) == 0, a.equals(b));
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomArithmetic() {
    Random r = new Random(7);
    for (int iter = 0; iter < 300; iter++) {
      Domain a = randomDomain(r);
      TreeSet<Long> sa = values(a, LO, HI);
      long coeff = r.nextInt(9) - 4;
      Domain mul = a.multiplicationBy(coeff);
      assertCanonical(mul);
      for (long x = -60; x <= 60; x++) {
        boolean expected = false;
        for (long y : sa) {
          expected |= y * coeff == x;
        }
        assertEquals(expected, mul.contains(x), a + " * " + coeff + " at " + x);
      }
      if (coeff != 0) {
        Domain inv = a.inverseMultiplicationBy(coeff);
        Domain div = a.divisionBy(coeff);
        Domain cont = a.continuousMultiplicationBy(coeff);
        for (long x = 2 * LO; x <= 2 * HI; x++) {
          assertEquals(sa.contains(x * coeff), inv.contains(x), a + " / " + coeff + " at " + x);
        }
        for (long y : sa) {
          assertTrue(div.contains(y / coeff), "division " + a + " / " + coeff);
          assertTrue(cont.contains(y * coeff));
        }
        // the division by a coefficient only contains quotients of values of its intervals
        for (long x = 2 * LO; x <= 2 * HI; x++) {
          if (div.contains(x)) {
            boolean ok = false;
            for (int i = 0; i < a.numIntervals(); i++) {
              long q1 = a.intervalStart(i) / coeff;
              long q2 = a.intervalEnd(i) / coeff;
              ok |= x >= Math.min(q1, q2) && x <= Math.max(q1, q2);
            }
            assertTrue(ok);
          }
        }
      }
      if (sa.isEmpty()) {
        continue;
      }
      Domain sq = a.squareSuperset();
      for (long y : sa) {
        assertTrue(sq.contains(y * y));
      }
      long qa = r.nextInt(5) - 2;
      long qb = r.nextInt(5) - 2;
      long qc = r.nextInt(5) - 2;
      long qd = r.nextInt(5) - 2;
      Domain quad = a.quadraticSuperset(qa, qb, qc, qd);
      for (long y : sa) {
        assertTrue(quad.contains((qa * y + qb) * (qc * y + qd)));
      }
      Domain modulo = new Domain(1 + r.nextInt(4), 4 + r.nextInt(3));
      Domain mod = a.positiveModuloBySuperset(modulo);
      Domain pdiv = a.divisionBySuperset(modulo);
      for (long y : sa) {
        for (long m = modulo.min(); m <= modulo.max(); m++) {
          assertTrue(mod.contains(y % m), a + " % " + modulo);
          assertTrue(pdiv.contains(y / m), a + " / " + modulo);
        }
      }
      Domain b = randomDomain(r);
      Domain cm = a.continuousMultiplicationBy(b);
      for (long y : sa) {
        for (long z : values(b, LO, HI)) {
          assertTrue(cm.contains(y * z));
        }
      }
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testValueQueries() {
    Random r = new Random(9);
    for (int iter = 0; iter < 300; iter++) {
      Domain a = randomDomain(r);
      TreeSet<Long> sa = values(a, LO, HI);
      if (sa.isEmpty()) {
        continue;
      }
      long smallest = sa.first();
      for (long y : sa) {
        if (Math.abs(y) < Math.abs(smallest) || (Math.abs(y) == Math.abs(smallest) && y > 0)) {
          smallest = y;
        }
      }
      assertEquals(smallest, a.smallestValue());
      for (long x = 2 * LO; x <= 2 * HI; x++) {
        Long after = sa.ceiling(x);
        Long before = sa.floor(x);
        assertEquals(after == null ? x : after, a.valueAtOrAfter(x));
        assertEquals(before == null ? x : before, a.valueAtOrBefore(x));
        long dist = Long.MAX_VALUE;
        long closest = 0;
        for (long y : sa) {
          long d = Math.abs(y - x);
          if (d <= dist) {
            dist = d;
            closest = y;
          }
        }
        assertEquals(closest, a.closestValue(x), a + " closest to " + x);
        assertEquals(dist, a.distance(x));
      }
      // the domain around zero
      Domain part = a.partAroundZero();
      if (sa.contains(0L)) {
        assertTrue(part.contains(0));
        assertTrue(part.isIncludedIn(a));
        assertEquals(1, part.numIntervals());
      } else {
        assertTrue(part.isEmpty());
      }
      // sums of the k smallest and largest values
      int k = 1 + r.nextInt(sa.size());
      long minSum = 0;
      long maxSum = 0;
      List<Long> sorted = new ArrayList<>(sa);
      for (int i = 0; i < k; i++) {
        minSum += sorted.get(i);
        maxSum += sorted.get(sorted.size() - 1 - i);
      }
      assertEquals(minSum, Domain.sumOfKMinValueInDomain(a, k));
      assertEquals(maxSum, Domain.sumOfKMaxValueInDomain(a, k));
    }
  }

  @Test
  public void testSimplifyUsingImpliedDomain() {
    Random r = new Random(11);
    for (int iter = 0; iter < 300; iter++) {
      Domain a = randomDomain(r);
      Domain implied = randomDomain(r);
      Domain s = a.simplifyUsingImpliedDomain(implied);
      assertCanonical(s);
      assertEquals(a.intersectionWith(implied), s.intersectionWith(implied), a + " / " + implied);
      assertTrue(s.numIntervals() <= a.numIntervals() || a.isEmpty());
    }
  }

  @Test
  public void testRelaxIfTooComplex() {
    long[] values = new long[300];
    for (int i = 0; i < values.length; i++) {
      values[i] = 2 * i;
    }
    Domain d = Domain.fromValues(values);
    assertEquals(300, d.numIntervals());
    assertEquals(new Domain(0, 598), d.relaxIfTooComplex());
    Domain small = Domain.fromValues(new long[] {1, 3});
    assertEquals(small, small.relaxIfTooComplex());
    // large domains are multiplied as intervals
    assertEquals(new Domain(0, 2000), new Domain(0, 1000).multiplicationBy(2));
    assertEquals(Domain.fromValues(new long[] {0, 3, 6}), new Domain(0, 2).multiplicationBy(3));
  }

  @Test
  public void testDivisionBySupersetRandom() {
    // divisors with negative values and zero: every quotient e / d (d != 0) must be contained
    Random r = new Random(17);
    for (int iter = 0; iter < 400; iter++) {
      Domain a = randomDomain(r);
      Domain divisor = randomDomain(r);
      Domain q = a.divisionBySuperset(divisor);
      assertCanonical(q);
      TreeSet<Long> sa = values(a, LO, HI);
      TreeSet<Long> sd = values(divisor, LO, HI);
      sd.remove(0L);
      if (sa.isEmpty() || sd.isEmpty()) {
        if (sa.isEmpty() || divisor.isEmpty()) {
          assertTrue(q.isEmpty());
        }
        continue;
      }
      for (long y : sa) {
        for (long d : sd) {
          assertTrue(q.contains(y / d), a + " / " + divisor + " misses " + y + "/" + d);
        }
      }
    }
  }

  @Test
  public void testDivisionBySupersetFixedCases() {
    assertTrue(new Domain(1, 5).divisionBySuperset(new Domain()).isEmpty());
    assertTrue(new Domain().divisionBySuperset(new Domain(1, 5)).isEmpty());
    assertTrue(new Domain(1, 5).divisionBySuperset(new Domain(0)).isEmpty());
    assertEquals(new Domain(1, 10), new Domain(10).divisionBySuperset(new Domain(0, 10)));
    assertEquals(new Domain(-10, -1), new Domain(10).divisionBySuperset(new Domain(-10, 0)));
    assertEquals(Domain.fromIntervals(new long[][] {{-10, -5}, {2, 10}}),
        new Domain(10).divisionBySuperset(Domain.fromIntervals(new long[][] {{-2, -1}, {1, 5}})));
    // kint64min / -1 saturates at kint64max
    assertEquals(new Domain(Long.MAX_VALUE),
        new Domain(Long.MIN_VALUE).divisionBySuperset(new Domain(-1)));
    assertEquals(new Domain(-Long.MAX_VALUE, Long.MAX_VALUE),
        new Domain(Long.MIN_VALUE, Long.MAX_VALUE).divisionBySuperset(new Domain(-1)));
  }

  @Test
  public void testQuadraticSupersetExtremeNextToBound() {
    // (5x - 28)^2 has its minimum at x = 5.6; with x in [5, 200] (more values than the
    // complexity limit, so the interval formula is used) the minimum 4 is reached at x = 6,
    // next to the truncated extreme point 5 == variable_min
    Domain d = new Domain(5, 200);
    Domain q = d.quadraticSuperset(5, -28, 5, -28);
    for (long x = 5; x <= 200; x++) {
      assertTrue(q.contains((5 * x - 28) * (5 * x - 28)), "missing value at x = " + x);
    }
    assertEquals(4, q.min());
    // same parabola on [-200, 5]: the candidate x = 6 is outside and must not be used
    Domain q2 = new Domain(-200, 5).quadraticSuperset(5, -28, 5, -28);
    assertEquals(9, q2.min());
    assertEquals((5 * -200L - 28) * (5 * -200L - 28), q2.max());
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testQuadraticSupersetRandomLargeDomains() {
    Random r = new Random(23);
    for (int iter = 0; iter < 300; iter++) {
      long start = r.nextInt(400) - 200;
      long end = start + 100 + r.nextInt(200);
      Domain d = new Domain(start, end);
      long a = r.nextInt(11) - 5;
      long b = r.nextInt(61) - 30;
      long c = r.nextInt(11) - 5;
      long e = r.nextInt(61) - 30;
      Domain q = d.quadraticSuperset(a, b, c, e);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long x = start; x <= end; x++) {
        long v = (a * x + b) * (c * x + e);
        assertTrue(q.contains(v), d + " (" + a + "x+" + b + ")(" + c + "x+" + e + ") at " + x);
        lo = Math.min(lo, v);
        hi = Math.max(hi, v);
      }
      if (a != 0 && c != 0) {
        // the hull is exact for a single interval
        assertEquals(lo, q.min());
        assertEquals(hi, q.max());
      }
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testUniqueValueNotIn() {
    assertEquals(java.util.OptionalLong.of(3),
        new Domain(1, 5).uniqueValueNotIn(Domain.fromValues(new long[] {1, 2, 4, 5})));
    assertEquals(java.util.OptionalLong.empty(), new Domain(1, 5).uniqueValueNotIn(new Domain(1, 5)));
    assertEquals(java.util.OptionalLong.empty(), new Domain(1, 5).uniqueValueNotIn(new Domain(2, 3)));
    assertEquals(java.util.OptionalLong.of(7), new Domain(7).uniqueValueNotIn(new Domain()));
    assertEquals(java.util.OptionalLong.empty(), new Domain().uniqueValueNotIn(new Domain()));
    assertEquals(java.util.OptionalLong.of(Long.MIN_VALUE),
        Domain.allValues().uniqueValueNotIn(Domain.greaterOrEqual(Long.MIN_VALUE + 1)));
    assertEquals(java.util.OptionalLong.of(Long.MAX_VALUE),
        Domain.allValues().uniqueValueNotIn(Domain.lowerOrEqual(Long.MAX_VALUE - 1)));
    Random r = new Random(31);
    for (int iter = 0; iter < 2000; iter++) {
      Domain a = randomDomain(r);
      Domain b = randomDomain(r);
      if (r.nextInt(2) == 0) {
        // make the difference small: everything of a but at most one value
        long keep = LO + r.nextInt(HI - LO + 1);
        b = a.unionWith(b).intersectionWith(new Domain(keep).complement());
      }
      TreeSet<Long> diff = values(a, LO, HI);
      diff.removeAll(values(b, LO, HI));
      java.util.OptionalLong expected =
          diff.size() == 1 ? java.util.OptionalLong.of(diff.first()) : java.util.OptionalLong.empty();
      assertEquals(expected, a.uniqueValueNotIn(b), a + " \\ " + b);
    }
  }

  @Test
  public void testSortedDisjointIntervalList() {
    SortedDisjointIntervalList list = new SortedDisjointIntervalList();
    assertEquals(0, list.numIntervals());
    list.insertInterval(10, 20);
    list.insertInterval(30, 40);
    assertEquals("[10,20][30,40]", list.toString());
    list.insertInterval(21, 29); // adjacent on both sides
    assertEquals("[10,40]", list.toString());
    list.insertInterval(50, 50);
    list.insertInterval(5, 8);
    assertEquals("[5,8][10,40][50]", list.toString());
    list.insertInterval(9, 9);
    assertEquals("[5,40][50]", list.toString());
    list.insertInterval(3, 1); // invalid: ignored
    assertEquals(2, list.numIntervals());
    assertEquals("[0,4][41,49][51,60]", list.buildComplementOnInterval(0, 60).toString());
    assertEquals("[41,45]", list.buildComplementOnInterval(20, 45).toString());
    assertEquals("", list.buildComplementOnInterval(6, 30).toString());
    SortedDisjointIntervalList other =
        new SortedDisjointIntervalList(new long[] {1, 7, 3}, new long[] {2, 9, 5});
    assertEquals("[1,5][7,9]", other.toString());
    other.insertIntervals(new int[] {6}, new int[] {6});
    assertEquals("[1,9]", other.toString());
    assertEquals(new Domain(1, 9), other.toDomain());
    assertArrayEquals(new long[] {1, 9}, other.firstIntervalGreaterOrEqualBounds(4));
    assertArrayEquals(null, other.firstIntervalGreaterOrEqualBounds(10));
    assertArrayEquals(new long[] {1, 9}, other.lastIntervalLessOrEqualBounds(20));
    other.insertInterval(Long.MIN_VALUE, 0);
    other.insertInterval(10, Long.MAX_VALUE);
    assertEquals(1, other.numIntervals());
    other.clear();
    assertEquals(0, other.numIntervals());
  }

  @Test
  public void testRandomIntervalLists() {
    Random r = new Random(5);
    for (int iter = 0; iter < 200; iter++) {
      SortedDisjointIntervalList list = new SortedDisjointIntervalList();
      TreeSet<Long> set = new TreeSet<>();
      for (int k = 0; k < 6; k++) {
        long a = r.nextInt(30);
        long b = a + r.nextInt(5);
        list.insertInterval(a, b);
        for (long x = a; x <= b; x++) {
          set.add(x);
        }
      }
      Domain d = list.toDomain();
      assertEquals(set, values(d, -5, 40));
      assertEquals(d.numIntervals(), list.numIntervals());
      SortedDisjointIntervalList comp = list.buildComplementOnInterval(0, 35);
      for (long x = 0; x <= 35; x++) {
        assertEquals(!set.contains(x), comp.toDomain().contains(x));
      }
      assertFalse(comp.toDomain().contains(36));
    }
  }
}
