package org.matheclipse.external.fastutil;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.external.fastutil.ints.IntList;

/**
 * Compares {@link IntArrayList} against the original {@code it.unimi.dsi.fastutil} implementation
 * (test-scope dependency) on randomized operation sequences.
 */
public class IntArrayListDifferentialTest {

  private static void assertSame(it.unimi.dsi.fastutil.ints.IntArrayList expected,
      IntArrayList actual) {
    assertEquals(expected.size(), actual.size());
    assertEquals(expected.isEmpty(), actual.isEmpty());
    assertArrayEquals(expected.toIntArray(), actual.toIntArray());
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected.hashCode(), actual.hashCode());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.getInt(i), actual.getInt(i));
    }
  }

  @Test
  public void testRandomOperationSequence() {
    final Random random = new Random(0xC0FFEE);
    final it.unimi.dsi.fastutil.ints.IntArrayList expected =
        new it.unimi.dsi.fastutil.ints.IntArrayList();
    final IntArrayList actual = new IntArrayList();
    for (int step = 0; step < 20000; step++) {
      final int k = random.nextInt(50) - 25;
      switch (random.nextInt(8)) {
        case 0:
          assertEquals(expected.add(k), actual.add(k));
          break;
        case 1:
          if (!expected.isEmpty()) {
            final int index = random.nextInt(expected.size());
            expected.add(index, k);
            actual.add(index, k);
          }
          break;
        case 2:
          if (!expected.isEmpty()) {
            final int index = random.nextInt(expected.size());
            assertEquals(expected.set(index, k), actual.set(index, k));
          }
          break;
        case 3:
          if (!expected.isEmpty()) {
            final int index = random.nextInt(expected.size());
            assertEquals(expected.removeInt(index), actual.removeInt(index));
          }
          break;
        case 4:
          assertEquals(expected.rem(k), actual.rem(k));
          break;
        case 5:
          assertEquals(expected.indexOf(k), actual.indexOf(k));
          assertEquals(expected.lastIndexOf(k), actual.lastIndexOf(k));
          assertEquals(expected.contains(k), actual.contains(k));
          break;
        case 6:
          final int[] elements = {k, k + 1, k + 2};
          final int index = expected.isEmpty() ? 0 : random.nextInt(expected.size());
          expected.addElements(index, elements, 0, elements.length);
          actual.addElements(index, elements, 0, elements.length);
          break;
        default:
          if (expected.size() > 4) {
            final int from = random.nextInt(expected.size() - 2);
            final int to = from + 1 + random.nextInt(expected.size() - from - 1);
            assertArrayEquals(expected.subList(from, to).toIntArray(),
                actual.subList(from, to).toIntArray());
          }
          break;
      }
      assertSame(expected, actual);
    }
  }

  @Test
  public void testGrowthAndCapacityConstructors() {
    assertEquals(0, new IntArrayList(8000).size());
    final int[] a = {3, 1, 2};
    assertArrayEquals(new it.unimi.dsi.fastutil.ints.IntArrayList(a).toIntArray(),
        new IntArrayList(a).toIntArray());
    assertArrayEquals(it.unimi.dsi.fastutil.ints.IntArrayList.of(7, 8).toIntArray(),
        IntArrayList.of(7, 8).toIntArray());
    assertArrayEquals(it.unimi.dsi.fastutil.ints.IntList.of(7, 8).toIntArray(),
        IntList.of(7, 8).toIntArray());
  }

  @Test
  public void testSubListIsAView() {
    final it.unimi.dsi.fastutil.ints.IntArrayList expected =
        it.unimi.dsi.fastutil.ints.IntArrayList.of(0, 1, 2, 3, 4);
    final IntArrayList actual = IntArrayList.of(0, 1, 2, 3, 4);
    expected.subList(1, 4).set(0, 99);
    actual.subList(1, 4).set(0, 99);
    assertArrayEquals(expected.toIntArray(), actual.toIntArray());
    assertEquals(99, actual.getInt(1));
  }

  @Test
  public void testIteratorAndForEach() {
    final IntArrayList list = IntArrayList.of(4, 5, 6);
    int sum = 0;
    final org.matheclipse.external.fastutil.ints.IntIterator iterator = list.iterator();
    while (iterator.hasNext()) {
      sum += iterator.nextInt();
    }
    assertEquals(15, sum);
    final int[] boxedSum = {0};
    list.forEach((int value) -> boxedSum[0] += value);
    assertEquals(15, boxedSum[0]);
  }

  @Test
  public void testRemoveOverloadsKeepFastutilSemantics() {
    // remove(int) removes by index, rem(int) removes by value - as in fastutil
    final IntArrayList list = IntArrayList.of(10, 11, 12);
    assertEquals(Integer.valueOf(11), list.remove(1));
    assertArrayEquals(new int[] {10, 12}, list.toIntArray());
    assertTrue(list.rem(12));
    assertFalse(list.rem(12));
    assertArrayEquals(new int[] {10}, list.toIntArray());
  }

  @Test
  public void testSerializationRoundTrip() throws Exception {
    final IntArrayList list = IntArrayList.of(1, 2, 3, 4, 5);
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(list);
    }
    try (ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      final IntArrayList read = (IntArrayList) in.readObject();
      assertArrayEquals(list.toIntArray(), read.toIntArray());
    }
  }
}
