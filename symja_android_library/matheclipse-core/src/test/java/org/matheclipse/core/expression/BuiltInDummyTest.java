package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.ISymbol;

/** {@link BuiltInDummy} answers like {@link Symbol} for the parts they share. */
class BuiltInDummyTest {

  @BeforeAll
  static void initSymja() {
    F.initSymja();
  }

  @Test
  void testIsValueOwnValues() {
    BuiltInDummy dummy = new BuiltInDummy("dummyOwnValueProbe");
    assertFalse(dummy.isValue(ISymbol.OWN_VALUES_PRESENT));
    dummy.assignValue(F.ZZ(17), false);
    try {
      assertTrue(dummy.isValue(ISymbol.OWN_VALUES_PRESENT));
    } finally {
      dummy.clearValue(null);
    }
  }

  @Test
  void testDefinitionToStringLikeSymbol() throws Exception {
    BuiltInDummy dummy = new BuiltInDummy("dummyDefinitionProbe");
    dummy.assignValue(F.Plus(F.C1, F.Sqrt(F.C2)), false);
    try {
      Symbol symbol = new Symbol("dummyDefinitionProbe", Context.DUMMY);
      symbol.assignValue(F.Plus(F.C1, F.Sqrt(F.C2)), false);
      assertEquals(symbol.definitionToString(), dummy.definitionToString());
    } finally {
      dummy.clearValue(null);
    }
  }
}
