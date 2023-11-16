package org.matheclipse.core.sympy.ntheory;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

import static org.junit.Assert.assertEquals;

public class TestGenerate extends ExprEvaluatorTestCase {

  @Test
  public void testPrimeRange() {
    assertEquals(Generate.primeRange(F.C10, F.C1).toString(), //
        "{}");
    assertEquals(Generate.primeRange(F.C5, F.C9).toString(), //
        "{5,7}");
    assertEquals(Generate.primeRange(F.C2, F.ZZ(13)).toString(), //
        "{2,3,5,7,11}");
    assertEquals(Generate.primeRange(F.ZZ(13)).toString(), //
        "{2,3,5,7,11}");
    assertEquals(Generate.primeRange(F.ZZ(8)).toString(), //
        "{2,3,5,7}");
    assertEquals(Generate.primeRange(F.CN2).toString(), //
        "{}");
    assertEquals(Generate.primeRange(F.ZZ(29)).toString(), //
        "{2,3,5,7,11,13,17,19,23}");
    assertEquals(Generate.primeRange(F.ZZ(34)).toString(), //
        "{2,3,5,7,11,13,17,19,23,29,31}");
  }

}
