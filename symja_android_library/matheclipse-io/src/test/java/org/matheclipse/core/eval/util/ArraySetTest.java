package org.matheclipse.core.eval.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;

public class ArraySetTest  {

  @Test
  public void test001() {
    ArraySet<ISymbol> set = new ArraySet<ISymbol>();

    assertTrue(set.isEmpty());
    set.add(F.z);
    assertFalse(set.isEmpty());
    set.add(F.x);
    assertFalse(set.isEmpty());
    set.add(F.y);
    assertTrue(set.contains(F.x));
    assertFalse(set.contains(F.a));

    assertEquals(F.x, set.get(0));
    assertEquals(F.y, set.get(1));
    assertEquals(F.z, set.get(2));
    assertEquals(null, set.get(3));

    for (ISymbol iSymbol : set) {
      System.out.println(iSymbol.toString());
    }
  }
}
