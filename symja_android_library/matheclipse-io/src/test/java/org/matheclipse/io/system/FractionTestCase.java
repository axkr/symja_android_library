package org.matheclipse.io.system;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.matheclipse.core.expression.AbstractFractionSym.valueOf;
import static org.matheclipse.core.expression.AbstractFractionSym.valueOfConvergent;
import java.util.NoSuchElementException;
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

  @Test
  void testValueOfConvergent() {
    assertEquals("1", valueOfConvergent(1.0));
    assertEquals("0", valueOfConvergent(0.0));
    assertEquals("8", valueOfConvergent(8.0));
    assertEquals("43", valueOfConvergent(43.0));
    assertEquals("10", valueOfConvergent(10.0));
    assertEquals("1000", valueOfConvergent(1000.0));

    assertEquals("1/10", valueOfConvergent(0.1));
    assertEquals("1/5", valueOfConvergent(0.2));
    assertEquals("3/10", valueOfConvergent(0.3));
    assertEquals("2/5", valueOfConvergent(0.4));
    assertEquals("1/2", valueOfConvergent(0.5));
    assertEquals("3/5", valueOfConvergent(0.6));
    assertEquals("7/10", valueOfConvergent(0.7));
    assertEquals("4/5", valueOfConvergent(0.8));
    assertEquals("9/10", valueOfConvergent(0.9));

    assertEquals("1/10", valueOfConvergent(0.1));
    assertEquals("1/100", valueOfConvergent(0.01));
    assertEquals("1/1000", valueOfConvergent(0.001));
    assertEquals("1/10000", valueOfConvergent(0.0001));
    assertEquals("0", valueOfConvergent(0.00001)); // rounded to zero

    assertEquals("3/4", valueOfConvergent(0.75));
    assertEquals("11/5", valueOfConvergent(2.2));

    assertEquals("1943/4", valueOfConvergent(485.75));

    assertEquals("245850922/78256779", valueOfConvergent(Math.PI));

    assertThrows(NoSuchElementException.class, () -> valueOfConvergent(1.1859405905877545));
    assertThrows(NoSuchElementException.class, () -> valueOfConvergent(1.6265889544859224));

    assertEquals("0", valueOfConvergent(4.547476426840269E-13));
    assertEquals("0", valueOfConvergent(8.147473162667989E-47));
    assertEquals("1", valueOfConvergent(0.9999999976191776));
    assertEquals("2", valueOfConvergent(1.9999992626312268));

    assertEquals("703791863104313034795615518720", valueOfConvergent(7.03791863104313E29));
    assertEquals(
        "41443534396435588611213052456670629384928442103203783116298919828494368341312648773632",
        valueOfConvergent(4.144353439643559E85));
    assertEquals(
        "8365327199922553523412142632386866005829989115082364265187442262457444899190991167760856400050282180955268244806847868694420889116879412179164543828412908728421710223689854773971619786645931722628866130880103557100913478166634542534393593856",
        valueOfConvergent(8.365327199922554E240));
  }

  private static void assertEquals(String expected, IFraction actual) {
    Assertions.assertEquals(expected, actual.toBigFraction().toString().replaceAll("[\\s]", ""));
  }
}
