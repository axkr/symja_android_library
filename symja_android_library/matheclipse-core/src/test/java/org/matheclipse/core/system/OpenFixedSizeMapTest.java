package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import junit.framework.TestCase;

public class OpenFixedSizeMapTest extends TestCase {

  @Test
  public void testPut() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);
    myHashMap.put(2, 3);
    assertTrue(myHashMap.get(2).equals(3));
  }

  @Test
  public void testPutWithSameKey() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);
    myHashMap.put(2, 3);
    myHashMap.put(2, 4);

    assertTrue(myHashMap.get(2).equals(4));
  }

  @Test
  public void testGetByNotExistingKey() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);
    myHashMap.put(2, 3);

    assertNull(myHashMap.get(5));
  }

  @Test
  public void testCollision() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);
    myHashMap.put(2, 3);
    myHashMap.put(5, 4);

    assertTrue(myHashMap.get(5).equals(4));
  }

  @Test
  public void testGetByNotExistingKeyWhenTableIsFull() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);
    myHashMap.put(2, 3);
    myHashMap.put(5, 4);
    myHashMap.put(3, 4);

    assertNull(myHashMap.get(1));
  }

  @Test
  public void testSize() {
    OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(3);

    assertTrue(myHashMap.size() == 0);

    myHashMap.put(1, 1);
    assertTrue(myHashMap.size() == 1);

    myHashMap.put(2, 1);
    assertTrue(myHashMap.size() == 2);

    myHashMap.put(3, 1);
    assertTrue(myHashMap.size() == 3);
  }

  public void testPutWhenMapIsFull() {
    try {
      OpenFixedSizeMap<Integer, Integer> myHashMap = new OpenFixedSizeMap<Integer, Integer>(2);
      myHashMap.put(1, 1);
      myHashMap.put(2, 1);
      myHashMap.put(3, 1);
      assertEquals("Map should be full!", "");
    } catch (IllegalStateException ise) {
      assertEquals("Map is full!", ise.getMessage());
    }
  }
}
