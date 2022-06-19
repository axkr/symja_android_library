package org.matheclipse.core.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class IntegerTestCase extends ExprEvaluatorTestCase {
  public IntegerTestCase(String name) {
    super(name);
  }

  public void testIQuo() {
    IInteger a = F.ZZ(23).iquo(F.C4);
    assertEquals("5", a.toString());

    a = F.ZZ(-23).iquo(F.C4);
    assertEquals("-5", a.toString());

    a = F.ZZ(23).iquo(F.CN4);
    assertEquals("-5", a.toString());

    a = F.ZZ(-23).iquo(F.CN4);
    assertEquals("5", a.toString());
  }

  public void testIRem() {
    IInteger a = F.ZZ(23).irem(F.C4);
    assertEquals("3", a.toString());

    a = F.ZZ(-23).irem(F.C4);
    assertEquals("-3", a.toString());

    a = F.ZZ(23).irem(F.CN4);
    assertEquals("3", a.toString());

    a = F.ZZ(-23).irem(F.CN4);
    assertEquals("-3", a.toString());
  }

  public void testTrunc() {
    IInteger a = F.QQ(-11, 6).trunc();
    assertEquals("-1", a.toString());

    a = F.QQ(11, 6).trunc();
    assertEquals("1", a.toString());
  }
}
