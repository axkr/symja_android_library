package org.matheclipse.external.fastutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.external.fastutil.ints.Int2IntMap;
import org.matheclipse.external.fastutil.ints.Int2IntOpenHashMap;
import org.matheclipse.external.fastutil.ints.Int2IntRBTreeMap;
import org.matheclipse.external.fastutil.ints.Int2ObjectAVLTreeMap;
import org.matheclipse.external.fastutil.ints.Int2ObjectMap;
import org.matheclipse.external.fastutil.longs.LongOpenHashSet;
import org.matheclipse.external.fastutil.objects.Object2IntArrayMap;
import org.matheclipse.external.fastutil.objects.Object2IntMap;
import org.matheclipse.external.fastutil.objects.Object2IntOpenHashMap;
import org.matheclipse.external.fastutil.objects.ObjectSortedSet;

/**
 * Compares the slim map and set replacements against the original {@code it.unimi.dsi.fastutil}
 * implementations (test-scope dependency), including iteration order.
 */
public class MapDifferentialTest {

  @Test
  public void testObject2IntOpenHashMapAgainstFastutil() {
    final Random random = new Random(42);
    final it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<String> expected =
        new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>();
    final Object2IntOpenHashMap<String> actual = new Object2IntOpenHashMap<>();
    for (int step = 0; step < 5000; step++) {
      final String k = "k" + random.nextInt(200);
      switch (random.nextInt(4)) {
        case 0:
          assertEquals(expected.put(k, step), actual.put(k, step));
          break;
        case 1:
          assertEquals(expected.addTo(k, 1), actual.addTo(k, 1));
          break;
        case 2:
          assertEquals(expected.removeInt(k), actual.removeInt(k));
          break;
        default:
          assertEquals(expected.getInt(k), actual.getInt(k));
          assertEquals(expected.containsKey(k), actual.containsKey(k));
          break;
      }
      assertEquals(expected.size(), actual.size());
    }
    // the table layout, and therefore the iteration order, must be identical
    final List<String> expectedOrder = new ArrayList<>();
    for (final it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<String> e : expected
        .object2IntEntrySet()) {
      expectedOrder.add(e.getKey() + "=>" + e.getIntValue());
    }
    final List<String> actualOrder = new ArrayList<>();
    for (final Object2IntMap.Entry<String> e : actual.object2IntEntrySet()) {
      actualOrder.add(e.getKey() + "=>" + e.getIntValue());
    }
    assertEquals(expectedOrder, actualOrder);
  }

  @Test
  public void testDefaultReturnValue() {
    final it.unimi.dsi.fastutil.objects.Object2IntArrayMap<String> expected =
        new it.unimi.dsi.fastutil.objects.Object2IntArrayMap<>();
    final Object2IntArrayMap<String> actual = new Object2IntArrayMap<>();
    expected.defaultReturnValue(-1);
    actual.defaultReturnValue(-1);
    assertEquals(expected.getInt("absent"), actual.getInt("absent"));
    assertEquals(-1, actual.getInt("absent"));
    assertEquals(expected.put("a", 7), actual.put("a", 7));
    assertEquals(expected.getInt("a"), actual.getInt("a"));
    assertEquals(expected.put("a", 8), actual.put("a", 8));
    assertEquals(expected.removeInt("a"), actual.removeInt("a"));
    assertEquals(expected.removeInt("a"), actual.removeInt("a"));
  }

  @Test
  public void testAddToOnAbsentKeyStartsFromDefaultReturnValue() {
    final it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<String> expected =
        new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>();
    final Object2IntOpenHashMap<String> actual = new Object2IntOpenHashMap<>();
    assertEquals(expected.addTo("x", 5), actual.addTo("x", 5));
    assertEquals(expected.getInt("x"), actual.getInt("x"));
    assertEquals(expected.addTo("x", 3), actual.addTo("x", 3));
    assertEquals(expected.getInt("x"), actual.getInt("x"));
    assertEquals(8, actual.getInt("x"));
  }

  @Test
  public void testObject2IntArrayMapKeepsInsertionOrder() {
    final it.unimi.dsi.fastutil.objects.Object2IntArrayMap<String> expected =
        new it.unimi.dsi.fastutil.objects.Object2IntArrayMap<>();
    final Object2IntArrayMap<String> actual = new Object2IntArrayMap<>();
    for (int i = 0; i < 20; i++) {
      expected.put("v" + i, i);
      actual.put("v" + i, i);
    }
    expected.removeInt("v5");
    actual.removeInt("v5");
    final List<String> expectedOrder = new ArrayList<>();
    for (final it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<String> e : expected
        .object2IntEntrySet()) {
      expectedOrder.add(e.getKey() + "=>" + e.getIntValue());
    }
    final List<String> actualOrder = new ArrayList<>();
    for (final Object2IntMap.Entry<String> e : actual.object2IntEntrySet()) {
      actualOrder.add(e.getKey() + "=>" + e.getIntValue());
    }
    assertEquals(expectedOrder, actualOrder);
  }

  @Test
  public void testInt2IntOpenHashMapAgainstFastutil() {
    final Random random = new Random(7);
    final it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap expected =
        new it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap();
    final Int2IntOpenHashMap actual = new Int2IntOpenHashMap();
    for (int step = 0; step < 5000; step++) {
      final int k = random.nextInt(150) - 20;
      switch (random.nextInt(4)) {
        case 0:
          assertEquals(expected.put(k, step), actual.put(k, step));
          break;
        case 1:
          assertEquals(expected.computeIfAbsent(k, x -> x * 2),
              actual.computeIfAbsent(k, x -> x * 2));
          break;
        case 2:
          assertEquals(expected.remove(k), actual.remove(k));
          break;
        default:
          assertEquals(expected.get(k), actual.get(k));
          assertEquals(expected.containsKey(k), actual.containsKey(k));
          break;
      }
      assertEquals(expected.size(), actual.size());
    }
    final List<String> expectedOrder = new ArrayList<>();
    for (final it.unimi.dsi.fastutil.ints.Int2IntMap.Entry e : expected.int2IntEntrySet()) {
      expectedOrder.add(e.getIntKey() + "=>" + e.getIntValue());
    }
    final List<String> actualOrder = new ArrayList<>();
    for (final Int2IntMap.Entry e : actual.int2IntEntrySet()) {
      actualOrder.add(e.getIntKey() + "=>" + e.getIntValue());
    }
    assertEquals(expectedOrder, actualOrder);
  }

  @Test
  public void testInt2IntRBTreeMapIsAscending() {
    final it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap expected =
        new it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap();
    final Int2IntRBTreeMap actual = new Int2IntRBTreeMap();
    final Random random = new Random(11);
    for (int i = 0; i < 500; i++) {
      final int k = random.nextInt(100);
      assertEquals(expected.put(k, i), actual.put(k, i));
    }
    final List<String> expectedOrder = new ArrayList<>();
    for (final it.unimi.dsi.fastutil.ints.Int2IntMap.Entry e : expected.int2IntEntrySet()) {
      expectedOrder.add(e.getIntKey() + "=>" + e.getIntValue());
    }
    final List<String> actualOrder = new ArrayList<>();
    for (final Int2IntMap.Entry e : actual.int2IntEntrySet()) {
      actualOrder.add(e.getIntKey() + "=>" + e.getIntValue());
    }
    assertEquals(expectedOrder, actualOrder);
    assertEquals(expected.size(), actual.size());
    // the entries are also plain Map.Entry instances, as Symja relies on in Primality
    for (final java.util.Map.Entry<Integer, Integer> e : actual.int2IntEntrySet()) {
      assertEquals(actual.get(e.getKey().intValue()), e.getValue().intValue());
    }
  }

  @Test
  public void testInt2ObjectAVLTreeMapIsAscending() {
    final it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap<String> expected =
        new it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap<>();
    final Int2ObjectAVLTreeMap<String> actual = new Int2ObjectAVLTreeMap<>();
    final Random random = new Random(13);
    for (int i = 0; i < 500; i++) {
      final int k = random.nextInt(100);
      expected.put(k, "v" + i);
      actual.put(k, "v" + i);
    }
    for (int i = 0; i < 50; i++) {
      final int k = random.nextInt(100);
      expected.remove(k);
      actual.remove(k);
    }
    final List<String> expectedOrder = new ArrayList<>();
    for (final it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<String> e : expected
        .int2ObjectEntrySet()) {
      expectedOrder.add(e.getIntKey() + "=>" + e.getValue());
    }
    final ObjectSortedSet<Int2ObjectMap.Entry<String>> set = actual.int2ObjectEntrySet();
    final List<String> actualOrder = new ArrayList<>();
    for (final Int2ObjectMap.Entry<String> e : set) {
      actualOrder.add(e.getIntKey() + "=>" + e.getValue());
    }
    assertEquals(expectedOrder, actualOrder);
    assertEquals(expected.size(), actual.size());
    assertEquals(expected.get(1), actual.get(1));
  }

  @Test
  public void testLongOpenHashSetAgainstFastutil() {
    final Random random = new Random(99);
    final it.unimi.dsi.fastutil.longs.LongOpenHashSet expected =
        new it.unimi.dsi.fastutil.longs.LongOpenHashSet();
    final LongOpenHashSet actual = new LongOpenHashSet();
    for (int step = 0; step < 5000; step++) {
      final long k = random.nextInt(300) - 50;
      switch (random.nextInt(3)) {
        case 0:
          assertEquals(expected.add(k), actual.add(k));
          break;
        case 1:
          assertEquals(expected.remove(k), actual.remove(k));
          break;
        default:
          assertEquals(expected.contains(k), actual.contains(k));
          break;
      }
      assertEquals(expected.size(), actual.size());
      assertEquals(expected.isEmpty(), actual.isEmpty());
    }
    final List<Long> expectedOrder = new ArrayList<>();
    final it.unimi.dsi.fastutil.longs.LongIterator i = expected.iterator();
    while (i.hasNext()) {
      expectedOrder.add(i.nextLong());
    }
    final List<Long> actualOrder = new ArrayList<>();
    final org.matheclipse.external.fastutil.longs.LongIterator j = actual.longIterator();
    while (j.hasNext()) {
      actualOrder.add(j.nextLong());
    }
    assertEquals(expectedOrder, actualOrder);
    final LongOpenHashSet clone = actual.clone();
    assertEquals(actual.size(), clone.size());
    assertTrue(clone.add(Long.MAX_VALUE));
    assertFalse(actual.contains(Long.MAX_VALUE));
  }

  @Test
  public void testLongArrayListAgainstFastutil() {
    final it.unimi.dsi.fastutil.longs.LongArrayList expected =
        new it.unimi.dsi.fastutil.longs.LongArrayList();
    final org.matheclipse.external.fastutil.longs.LongArrayList actual =
        new org.matheclipse.external.fastutil.longs.LongArrayList();
    final Random random = new Random(5);
    for (int i = 0; i < 2000; i++) {
      final long k = random.nextLong();
      expected.add(k);
      actual.add(k);
    }
    org.junit.jupiter.api.Assertions.assertArrayEquals(expected.toLongArray(),
        actual.toLongArray());
    assertEquals(expected.size(), actual.size());
    assertEquals(expected.getLong(17), actual.getLong(17));
    assertEquals(expected.toString(), actual.toString());
  }
}
