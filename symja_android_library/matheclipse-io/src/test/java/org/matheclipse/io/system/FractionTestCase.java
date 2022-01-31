package org.matheclipse.io.system;

import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.interfaces.IFraction;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class FractionTestCase extends AbstractTestCase {
  public FractionTestCase(String name) {
    super(name);
  }

  public void testCTor() {
    IFraction a = AbstractFractionSym.valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MAX_VALUE, Integer.MIN_VALUE);
    assertEquals("-2147483647/2147483648", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertEquals("-2147483648/2147483647", a.toString());

    a = AbstractFractionSym.valueOf(Long.MIN_VALUE, Long.MIN_VALUE);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Long.MAX_VALUE, Long.MAX_VALUE);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Long.MAX_VALUE, Long.MIN_VALUE);
    assertEquals("-9223372036854775807/9223372036854775808", a.toString());

    a = AbstractFractionSym.valueOf(Long.MIN_VALUE, Long.MAX_VALUE);
    assertEquals("-9223372036854775808/9223372036854775807", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MIN_VALUE - 1L, Integer.MIN_VALUE - 1L);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MAX_VALUE + 1L, Integer.MAX_VALUE + 1L);
    assertEquals("1", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MAX_VALUE + 1L, Integer.MIN_VALUE - 1L);
    assertEquals("-2147483648/2147483649", a.toString());

    a = AbstractFractionSym.valueOf(Integer.MIN_VALUE - 1L, Integer.MAX_VALUE + 1L);
    assertEquals("-2147483649/2147483648", a.toString());
  }

  public void testCTor0() {
    IFraction a = AbstractFractionSym.valueOf(0, Integer.MIN_VALUE);
    assertEquals("0", a.toString());

    try {
      a = AbstractFractionSym.valueOf(Integer.MAX_VALUE, 0);
      fail();
    } catch (ArgumentTypeException ate) {

    }

    try {
      a = AbstractFractionSym.valueOf(Integer.MIN_VALUE, 0);
      fail();
    } catch (ArgumentTypeException ate) {

    }
    a = AbstractFractionSym.valueOf(0, Integer.MAX_VALUE);
    assertEquals("0", a.toString());

    // Long

    a = AbstractFractionSym.valueOf(0, Long.MIN_VALUE);
    assertEquals("0", a.toString());

    try {
      a = AbstractFractionSym.valueOf(Long.MAX_VALUE, 0);
      fail();
    } catch (ArgumentTypeException ate) {

    }

    try {
      a = AbstractFractionSym.valueOf(Long.MIN_VALUE, 0);
      fail();
    } catch (ArgumentTypeException ate) {

    }
    a = AbstractFractionSym.valueOf(0, Long.MAX_VALUE);
    assertEquals("0", a.toString());
  }
}
