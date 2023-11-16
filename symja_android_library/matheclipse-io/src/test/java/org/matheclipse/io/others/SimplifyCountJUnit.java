package org.matheclipse.io.others;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.io.system.AbstractTestCase;

import static org.junit.Assert.assertEquals;

public class SimplifyCountJUnit extends AbstractTestCase {


  @Override
  public void check(String evalString, String expectedResult) {
    check(fScriptEngine, evalString, expectedResult, -1);
  }

  @Test
  public void test001() {

    long c = F.CN1.leafCountSimplify();
    assertEquals(c, 2);

    c = F.C1.leafCountSimplify();
    assertEquals(c, 1);

    c = F.C0.leafCountSimplify();
    assertEquals(c, 1);

    c = F.ZZ(-100).leafCountSimplify();
    assertEquals(c, 4);

    c = F.ZZ(100).leafCountSimplify();
    assertEquals(c, 3);

    c = F.CN1D4.leafCountSimplify();
    assertEquals(c, 4);

    c = F.C1D4.leafCountSimplify();
    assertEquals(c, 3);

    c = F.x.leafCountSimplify();
    assertEquals(c, 1);

    c = F.Pi.leafCountSimplify();
    assertEquals(c, 1);

    c = F.headAST0(F.f).leafCountSimplify();
    assertEquals(c, 1);

    c = F.binaryAST2(F.f, F.x, F.y).leafCountSimplify();
    assertEquals(c, 3);

    c = F.num(100.123).leafCountSimplify();
    assertEquals(c, 2);

    c = F.CNI.leafCountSimplify();
    assertEquals(c, 4);

    c = F.CI.leafCountSimplify();
    assertEquals(c, 3);

    c = F.Power(F.ZZ(17), F.C1D2).leafCountSimplify();
    assertEquals(c, 6);
  }
}
