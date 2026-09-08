package org.matheclipse.core.patternmatching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;

public class PatternMapTest {

  @BeforeAll
  public static void setUpClass() {
    F.initSymja();
  }

  private static IPatternMap patternMap(IAST lhs) {
    return IPatternMap.determinePatterns(lhs, new int[] {IPatternMap.DEFAULT_RULE_PRIORITY}, null);
  }

  /**
   * <code>PatternMap6.setValue()</code> unwrapped a OneIdentity value of the 6th pattern into the
   * 3rd slot.
   */
  @Test
  public void testSixthSlotOneIdentityValue() {
    IPatternObject[] patterns = {F.a_, F.b_, F.c_, F.d_, F.e_, F.f_};
    IAST lhs = F.ast(patterns, S.g);
    IPatternMap map = patternMap(lhs);
    assertEquals(6, map.size());
    map.initPattern();
    for (int i = 0; i < patterns.length; i++) {
      assertTrue(map.setValue(patterns[i], F.ZZ(i + 1)));
    }
    // Plus(x) is a OneIdentity AST with one argument
    assertTrue(map.setValue(patterns[5], F.unaryAST1(S.Plus, F.x)));
    for (int i = 0; i < patterns.length - 1; i++) {
      assertEquals(F.ZZ(i + 1), map.getValue(patterns[i]), "slot " + (i + 1));
    }
    assertEquals(F.x, map.getValue(patterns[5]));
  }

  /**
   * <code>PatternMap.substitutePatterns()</code> (more than 6 patterns) compared pattern objects
   * with the symbols of the patterns.
   */
  @Test
  public void testSubstitutePatternsWithMoreThanSixPatterns() {
    IPatternObject[] patterns = {F.a_, F.b_, F.c_, F.d_, F.e_, F.f_, F.g_};
    IAST lhs = F.ast(patterns, S.h);
    IPatternMap map = patternMap(lhs);
    assertEquals(7, map.size());
    map.initPattern();
    for (int i = 0; i < patterns.length; i++) {
      assertTrue(map.setValue(patterns[i], F.ZZ(i + 1)));
    }
    IExpr substituted = map.substitutePatterns(lhs, F.CEmptySequence);
    assertEquals("h(1,2,3,4,5,6,7)", substituted.toString());
  }
}
