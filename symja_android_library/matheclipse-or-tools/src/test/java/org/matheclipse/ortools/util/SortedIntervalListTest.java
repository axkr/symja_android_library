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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/javatests/com/google/ortools/util/SortedIntervalListTest.java (Truth assertions replaced by JUnit 5 assertions, no native library), see NOTICE in the module root.

package org.matheclipse.ortools.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class SortedIntervalListTest {
  @Test
  public void testSortedIntervalList_insert() throws Exception {
    SortedDisjointIntervalList i = new SortedDisjointIntervalList();
    i.insertInterval(10, 20);
    i.insertInterval(30, 40);
    assertEquals(2, i.numIntervals());
    assertEquals("[10,20][30,40]", i.toString());
  }

  @Test
  public void testInsertAll() throws Exception {
    SortedDisjointIntervalList i = new SortedDisjointIntervalList();
    int[] starts = new int[] {10, 30};
    int[] ends = new int[] {20, 40};
    i.insertIntervals(starts, ends);
    assertEquals(2, i.numIntervals());
    assertEquals("[10,20][30,40]", i.toString());
  }

  @Test
  public void testCtor() throws Exception {
    int[] starts = new int[] {10, 30};
    int[] ends = new int[] {20, 40};
    SortedDisjointIntervalList i = new SortedDisjointIntervalList(starts, ends);
    assertEquals(2, i.numIntervals());
    assertEquals("[10,20][30,40]", i.toString());
  }

  @Test
  public void testDomainCtor() throws Exception {
    Domain domain = new Domain(0, 1);
    assertEquals(2, domain.size());
    assertEquals(0, domain.min());
    assertEquals(1, domain.max());
    assertTrue(domain.contains(0));
    assertFalse(domain.contains(2));
    assertEquals("[0,1]", domain.toString());
  }

  @Test
  public void testFromValues() throws Exception {
    Domain domain = Domain.fromValues(new long[] {1, 3, -5, 5});
    assertEquals(4, domain.size());
    assertEquals(-5, domain.min());
    assertEquals(5, domain.max());
    assertArrayEquals(new long[] {-5, -5, 1, 1, 3, 3, 5, 5}, domain.flattenedIntervals());
    assertTrue(domain.contains(1));
    assertFalse(domain.contains(0));
  }

  @Test
  public void testFromIntervals() throws Exception {
    Domain domain = Domain.fromIntervals(new long[][] {{2, 4}, {-2, 0}});
    assertEquals(6, domain.size());
    assertEquals(-2, domain.min());
    assertEquals(4, domain.max());
    assertArrayEquals(new long[] {-2, 0, 2, 4}, domain.flattenedIntervals());
  }

  @Test
  public void testFromFlatIntervals() throws Exception {
    Domain domain = Domain.fromFlatIntervals(new long[] {2, 4, -2, 0});
    assertEquals(6, domain.size());
    assertEquals(-2, domain.min());
    assertEquals(4, domain.max());
    assertArrayEquals(new long[] {-2, 0, 2, 4}, domain.flattenedIntervals());
  }

  @Test
  public void testNegation() throws Exception {
    Domain domain = new Domain(5, 20);
    assertArrayEquals(new long[] {-20, -5}, domain.negation().flattenedIntervals());
  }

  @Test
  public void testUnion() throws Exception {
    Domain d1 = new Domain(0, 5);
    Domain d2 = new Domain(10, 15);
    Domain d3 = d1.unionWith(d2);
    assertArrayEquals(new long[] {0, 5}, d1.flattenedIntervals());
    assertArrayEquals(new long[] {10, 15}, d2.flattenedIntervals());
    assertArrayEquals(new long[] {0, 5, 10, 15}, d3.flattenedIntervals());
  }

  @Test
  public void testIntersection() throws Exception {
    Domain d1 = new Domain(0, 10);
    Domain d2 = new Domain(5, 15);
    Domain d3 = d1.intersectionWith(d2);
    assertArrayEquals(new long[] {0, 10}, d1.flattenedIntervals());
    assertArrayEquals(new long[] {5, 15}, d2.flattenedIntervals());
    assertArrayEquals(new long[] {5, 10}, d3.flattenedIntervals());
  }

  @Test
  public void testAddition() throws Exception {
    Domain d1 = new Domain(0, 5);
    Domain d2 = new Domain(10, 15);
    Domain d3 = d1.additionWith(d2);
    assertArrayEquals(new long[] {0, 5}, d1.flattenedIntervals());
    assertArrayEquals(new long[] {10, 15}, d2.flattenedIntervals());
    assertArrayEquals(new long[] {10, 20}, d3.flattenedIntervals());
  }

  @Test
  public void testComplement() throws Exception {
    Domain d1 = new Domain(Long.MIN_VALUE, 5);
    Domain d2 = d1.complement();
    assertArrayEquals(new long[] {Long.MIN_VALUE, 5}, d1.flattenedIntervals());
    assertArrayEquals(new long[] {6, Long.MAX_VALUE}, d2.flattenedIntervals());
  }
}
