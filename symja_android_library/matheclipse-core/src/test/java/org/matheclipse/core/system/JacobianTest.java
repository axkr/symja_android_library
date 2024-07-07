package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.util.Pair;
import org.junit.Test;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.MultivariateJacobianGradient;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.parser.ExprParser;

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

    Pair<RealVector, RealMatrix> pair = jacobian.value(vector1.toRealVector());
    IASTAppendable vector = Convert.vector2List(pair.getFirst());
    IASTAppendable matrix = Convert.matrix2List(pair.getSecond());
    assertEquals(//
        "{1.0,5.0,2.0,0.841471}", //
        vector.toString());
    assertEquals(//
        "{{1.0,0.0,0.0},\n" //
            + " {0.0,0.0,5.0},\n" //
            + " {0.0,8.0,-2.0},\n" //
            + " {0.540302,0.0,0.841471}}", //
        matrix.toString());
  }

}
