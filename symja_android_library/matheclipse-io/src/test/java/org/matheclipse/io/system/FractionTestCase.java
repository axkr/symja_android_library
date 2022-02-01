package org.matheclipse.io.system;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.matheclipse.core.expression.AbstractFractionSym.valueOf;
import static org.matheclipse.core.expression.AbstractFractionSym.valueOfExact;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IExpr;
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
  void testValueOfExact_standardValues() {
    List<Double> values = List.of(5.7532E-65, 1.296E-17, 1.0, 0.0, 5.0, 43., Math.PI, Math.E, -10.,
        -500.0, -7.0, 500.0, 1234563535345345345.0, 83798521.23587982125E237, 0.1E-11, 0.1E-10,
        0.001, 0.1, 10.0, 1000.0, Double.MIN_VALUE, Double.MIN_NORMAL, Double.MAX_VALUE, 0.7);

    for (Double value : values) {
      assertExactSameValue(value, valueOfExact(value));
    }
  }

  @Test
  void testValueOfExact_difficultValues() {

    List<Double> values = List.of(1.1859405905877545, 1.6265889544859224, 8.147473162667989E-47,
        4.144353439643559E85, 8.365327199922554E240, 3.541103771908122E290, 3.541103771908122E290,
        8.365327199922554E240, 4.350876748659001E186, 9.080123462136902E262, 9.85153538497719E110,
        7.050202128713718E198, 7.03791863104313E29, 2.9140032466682753E83, 4.62223123539841E-33,
        1.414950700041274E-73, 4.547476426840269E-13, 1.9999992626312268, 4.000002871012173,
        1.3262500221902385E-300, 4.568398424383824E304, 9.182693112741447E-309, 0.9999999976191776,
        2.131197356516773E-255, 2.771536903091114E-299, 8.379852123587982E244,
        5.104659969936841E-297, 1.6490812741030425E-293);

    for (Double value : values) {
      assertExactSameValue(value, valueOfExact(value));
    }
  }

  @Test
  void testValueOfExact_randomValues() {
    Random rnd = new Random();
    int maxExp = 308;
    IntStream.range(0, 10_000)
        .mapToDouble(i -> rnd.nextDouble() * Math.pow(10, rnd.nextInt(2 * maxExp) - maxExp))
        .forEach(value -> {
          assertExactSameValue(value, valueOfExact(value));
        });
  }

  private static void assertEquals(String expected, IFraction actual) {
    Assertions.assertEquals(expected, actual.toString());
  }

  private static void assertExactSameValue(double value, IExpr approximation) {
    Assertions.assertEquals(value, approximation.evalDouble(),
        () -> "Not exact representation of value " + value + " by expression: " + approximation);
  }
}
