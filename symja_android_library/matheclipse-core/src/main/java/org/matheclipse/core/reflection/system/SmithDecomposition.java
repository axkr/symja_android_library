package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.matrices.NormalForms;

/**
 * <pre>
 * <code>SmithDecomposition(matrix)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * calculate the Smith normal form decomposition <code>{u, r, v}</code> of an integer
 * <code>matrix</code>, where <code>u</code> and <code>v</code> are unimodular matrices,
 * <code>r</code> is the Smith normal form and <code>u.matrix.v == r</code>.
 * </p>
 * </blockquote>
 */
public class SmithDecomposition extends AbstractFunctionEvaluator {

  public SmithDecomposition() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dimensions = arg1.isMatrix();
    if (dimensions == null || dimensions[0] == 0 || dimensions[1] == 0) {
      return F.NIL;
    }
    try {
      BigInteger[][] matrix = Convert.list2BigIntegerMatrix(arg1);
      if (matrix == null) {
        return F.NIL;
      }
      BigInteger[][][] asT = NormalForms.smithNormalDecomp(matrix);
      // {u, r, v}
      return F.List(NormalForms.toList(asT[1]), NormalForms.toList(asT[0]),
          NormalForms.toList(asT[2]));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.SmithDecomposition, rex);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
