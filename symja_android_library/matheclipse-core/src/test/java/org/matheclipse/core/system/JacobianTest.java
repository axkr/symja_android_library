package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.MultivariateJacobianGradient;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.parser.ExprParser;

import static org.junit.Assert.assertEquals;

public class JacobianTest extends ExprEvaluatorTestCase {

  @Test
  public void testJacobianMatrix() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IAST functions = (IAST) parser.parse("{x1,5*x3,4*x2^2-2*x3,x3*Sin(x1)}");
    IAST variables = (IAST) parser.parse("{x1,x2,x3}");
    MultivariateJacobianGradient jacobian = new MultivariateJacobianGradient(functions, variables);
    assertEquals("{x1,5*x3,4*x2^2-2*x3,x3*Sin(x1)}", //
        jacobian.getFunction().toString());
    assertEquals("{{1,0,0},{0,0,5},{0,8*x2,-2},{x3*Cos(x1),0,Sin(x1)}}", //
        jacobian.getJacobianMatrix().toString());

    IAST vector1 = (IAST) parser.parse("{1.0,1.0,1.0}");
    // compare with hipparchus string output
    assertEquals(//
        "[{1; 5; 2; 0.8414709848}, " //
            + "Array2DRowRealMatrix{" //
            + "{1.0,0.0,0.0}," //
            + "{0.0,0.0,5.0}," //
            + "{0.0,8.0,-2.0}," //
            + "{0.5403023059,0.0,0.8414709848}}]", //
        jacobian.value(vector1.toRealVector()).toString());
  }


}
