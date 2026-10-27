package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Evaluation and filtering of the packed real arrays {@link ASTRealVector} and
 * {@link ASTRealMatrix}.
 */
class PackedRealArrayTest {

  @BeforeAll
  static void initSymja() {
    F.initSymja();
  }

  @Test
  void testArbitraryPrecisionNonSquareMatrix() {
    double[][] values = {{0.25, -1.75, 3.0}, {8.5, 0.125, -6.0}};
    ASTRealMatrix matrix = new ASTRealMatrix(values, false);
    EvalEngine engine = EvalEngine.get();
    boolean numericMode = engine.isNumericMode();
    long precision = engine.getNumericPrecision();
    int figures = engine.getSignificantFigures();
    try {
      engine.setNumericMode(true, 40, 40);
      IExpr result = matrix.evaluate(engine);
      assertTrue(result.isList());
      IAST rows = (IAST) result;
      assertEquals(2, rows.argSize());
      for (int i = 1; i <= 2; i++) {
        IAST row = (IAST) rows.get(i);
        assertEquals(3, row.argSize());
        for (int j = 1; j <= 3; j++) {
          assertTrue(row.get(j) instanceof ApfloatNum);
          assertEquals(values[i - 1][j - 1], row.get(j).evalf(), 0.0);
        }
      }
    } finally {
      engine.setNumericMode(numericMode, precision, figures);
    }
  }

  @Test
  void testFilterVector() {
    ASTRealVector vector = new ASTRealVector(new double[] {2.5, -0.5, 7.0, -3.25, 0.75}, false);
    IAST positive = vector.filter(F.ListAlloc(), x -> x.isPositive());
    assertEquals("{2.5,7.0,0.75}", positive.toString());

    IASTAppendable negative = F.ListAlloc();
    IAST selected = vector.filter(F.ListAlloc(), negative, x -> x.isPositive());
    assertEquals("{2.5,7.0,0.75}", selected.toString());
    assertEquals("{-0.5,-3.25}", negative.toString());
  }

  @Test
  void testFilterMatrix() {
    ASTRealMatrix matrix =
        new ASTRealMatrix(new double[][] {{0.5, 1.0}, {4.0, -2.0}, {9.5, 3.0}}, false);
    IAST rows = matrix.filter(F.ListAlloc(), row -> row.first().evalf() > 1.0);
    assertEquals(2, rows.argSize());
    assertEquals("{4.0,-2.0}", rows.arg1().toString());
    assertEquals("{9.5,3.0}", rows.arg2().toString());

    IASTAppendable rest = F.ListAlloc();
    matrix.filter(F.ListAlloc(), rest, row -> row.first().evalf() > 1.0);
    assertEquals(1, rest.argSize());
    assertEquals("{0.5,1.0}", rest.arg1().toString());
  }
}
