package org.matheclipse.io.system;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.matheclipse.core.expression.AbstractFractionSym.valueOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IFraction;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
class FractionTestCase {

  @Test
  void testCTor() {
    assertEquals("1", valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE));

    assertEquals("1", valueOf(Integer.MAX_VALUE, Integer.MAX_VALUE));

    assertEquals("-2147483647/2147483648", valueOf(Integer.MAX_VALUE, Integer.MIN_VALUE));

    assertEquals("-2147483648/2147483647", valueOf(Integer.MIN_VALUE, Integer.MAX_VALUE));

    assertEquals("1", valueOf(Long.MIN_VALUE, Long.MIN_VALUE));

    assertEquals("1", valueOf(Long.MAX_VALUE, Long.MAX_VALUE));

    assertEquals("-9223372036854775807/9223372036854775808",
        valueOf(Long.MAX_VALUE, Long.MIN_VALUE));

    assertEquals("-9223372036854775808/9223372036854775807",
        valueOf(Long.MIN_VALUE, Long.MAX_VALUE));

    assertEquals("1", valueOf(Integer.MIN_VALUE - 1L, Integer.MIN_VALUE - 1L));

    assertEquals("1", valueOf(Integer.MAX_VALUE + 1L, Integer.MAX_VALUE + 1L));

    assertEquals("-2147483648/2147483649", valueOf(Integer.MAX_VALUE + 1L, Integer.MIN_VALUE - 1L));

    assertEquals("-2147483649/2147483648", valueOf(Integer.MIN_VALUE - 1L, Integer.MAX_VALUE + 1L));
  }

  @Test
  void testCTor0() {
    assertEquals("0", valueOf(0, Integer.MIN_VALUE));

    assertThrows(ArgumentTypeException.class, () -> valueOf(Integer.MAX_VALUE, 0));

    assertThrows(ArgumentTypeException.class, () -> valueOf(Integer.MIN_VALUE, 0));

    assertEquals("0", valueOf(0, Integer.MAX_VALUE));

    // Long

    assertEquals("0", valueOf(0, Long.MIN_VALUE));

    assertThrows(ArgumentTypeException.class, () -> valueOf(Long.MAX_VALUE, 0));

    assertThrows(ArgumentTypeException.class, () -> valueOf(Long.MIN_VALUE, 0));

    assertEquals("0", valueOf(0, Long.MAX_VALUE));
  }

  private static void assertEquals(String expected, IFraction actual) {
    Assertions.assertEquals(expected, actual.toString());
  }
}
