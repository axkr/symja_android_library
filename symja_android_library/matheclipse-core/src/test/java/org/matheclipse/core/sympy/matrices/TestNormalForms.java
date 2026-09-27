package org.matheclipse.core.sympy.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestNormalForms extends ExprEvaluatorTestCase {

  private static BigInteger[][] matrix(long[][] values) {
    BigInteger[][] m = new BigInteger[values.length][];
    for (int i = 0; i < values.length; i++) {
      m[i] = new BigInteger[values[i].length];
      for (int j = 0; j < values[i].length; j++) {
        m[i][j] = BigInteger.valueOf(values[i][j]);
      }
    }
    return m;
  }

  /** Check <code>a == s.m.t</code>, that a is in Smith normal form and s, t are unimodular */
  private static void checkDecomposition(BigInteger[][] m) {
    BigInteger[][][] asT = NormalForms.smithNormalDecomp(m);
    assertTrue(NormalForms.isSmithNormalForm(asT[0]));
    IExpr a = NormalForms.toList(asT[0]);
    IExpr s = NormalForms.toList(asT[1]);
    IExpr t = NormalForms.toList(asT[2]);
    assertTrue(a.equals(F.eval(F.Dot(s, NormalForms.toList(m), t))));
    assertEquals("1", F.eval(F.Abs(F.Det(s))).toString());
    assertEquals("1", F.eval(F.Abs(F.Det(t))).toString());
    for (int i = 0; i < Math.min(asT[0].length, asT[0][0].length); i++) {
      assertTrue(asT[0][i][i].signum() >= 0);
    }
  }

  @Test
  public void testSmithNormal() {
    // https://github.com/sympy/sympy/blob/master/sympy/polys/matrices/tests/test_normalforms.py
    BigInteger[][] m = matrix(new long[][] {//
        {12, 6, 4, 8}, //
        {3, 9, 6, 12}, //
        {2, 16, 14, 28}, //
        {20, 10, 10, 20}});
    // smf = DM([[1, 0, 0, 0], [0, 10, 0, 0], [0, 0, 30, 0], [0, 0, 0, 0]], ZZ)
    assertEquals(NormalForms.toList(NormalForms.smithNormalForm(m)).toString(), //
        "{{1,0,0,0},{0,10,0,0},{0,0,30,0},{0,0,0,0}}");
    checkDecomposition(m);

    // >>> m = DomainMatrix([[ZZ(12), ZZ(6), ZZ(4)], [ZZ(3), ZZ(9), ZZ(6)], [ZZ(2), ZZ(16), ZZ(14)]]
    m = matrix(new long[][] {{12, 6, 4}, {3, 9, 6}, {2, 16, 14}});
    assertEquals(NormalForms.toList(NormalForms.smithNormalForm(m)).toString(), //
        "{{1,0,0},{0,10,0},{0,0,30}}");
    checkDecomposition(m);

    // assert smith_normal_form(DM([[2, 4]], ZZ)).to_dense() == DM([[2, 0]], ZZ)
    m = matrix(new long[][] {{2, 4}});
    assertEquals(NormalForms.toList(NormalForms.smithNormalForm(m)).toString(), //
        "{{2,0}}");
    checkDecomposition(m);
    // assert smith_normal_form(DM([[0, -2]], ZZ)).to_dense() == DM([[2, 0]], ZZ)
    m = matrix(new long[][] {{0, -2}});
    assertEquals(NormalForms.toList(NormalForms.smithNormalForm(m)).toString(), //
        "{{2,0}}");
    checkDecomposition(m);
    // assert smith_normal_form(DM([[0], [-2]], ZZ)).to_dense() == DM([[2], [0]], ZZ)
    m = matrix(new long[][] {{0}, {-2}});
    assertEquals(NormalForms.toList(NormalForms.smithNormalForm(m)).toString(), //
        "{{2},{0}}");
    checkDecomposition(m);

    // assert smith_normal_decomp(i11) == (i11, i11, i11)
    assertEquals(NormalForms.smithNormalDecompList(matrix(new long[][] {{1}})).toString(), //
        "{{{1}},{{1}},{{1}}}");
    checkDecomposition(matrix(new long[][] {{0, 0}, {0, 0}}));
    checkDecomposition(matrix(new long[][] {{0, 0, 0}, {0, 0, 7}}));
  }

  @Test
  public void testIsSmithNormalForm() {
    assertTrue(NormalForms.isSmithNormalForm(matrix(new long[][] {{1, 0}, {0, 2}, {0, 0}})));
    assertTrue(NormalForms.isSmithNormalForm(matrix(new long[][] {{2, 0, 0}, {0, 4, 0}})));
    assertTrue(NormalForms.isSmithNormalForm(matrix(new long[][] {{2, 0}, {0, 0}})));
    assertFalse(NormalForms.isSmithNormalForm(matrix(new long[][] {{0, 0}, {0, 2}})));
    assertFalse(NormalForms.isSmithNormalForm(matrix(new long[][] {{2, 0}, {0, 3}})));
    assertFalse(NormalForms.isSmithNormalForm(matrix(new long[][] {{1, 1}, {0, 2}})));
  }

  @Test
  public void testRandomMatrices() {
    Random random = new Random(42);
    for (int n = 0; n < 200; n++) {
      int rows = 1 + random.nextInt(5);
      int cols = 1 + random.nextInt(5);
      long[][] values = new long[rows][cols];
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          values[i][j] = random.nextInt(4) == 0 ? 0 : random.nextInt(41) - 20;
        }
      }
      checkDecomposition(matrix(values));
    }
  }

  @Test
  public void testSmithDecomposition() {
    check("{u,r,v} = SmithDecomposition({{1,2,3},{4,5,6},{7,8,9}});r", //
        "{{1,0,0},{0,3,0},{0,0,0}}");
    check("u.{{1,2,3},{4,5,6},{7,8,9}}.v == r", //
        "True");
    check("Abs(Det(u))*Abs(Det(v))", //
        "1");
    check("SmithDecomposition({{2,4,4},{-6,6,12},{10,4,16}})[[2]]", //
        "{{2,0,0},{0,2,0},{0,0,156}}");
    // only integer matrices are decomposed
    check("Head(SmithDecomposition({{a,2},{3,4}}))", //
        "SmithDecomposition");
  }
}
