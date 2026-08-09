package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * MomentOfInertia(region)
 *
 * MomentOfInertia(region, point)
 *
 * MomentOfInertia(region, point, vector)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the moment of inertia matrix of the <code>region</code> around its centroid, around
 * <code>point</code>, or the scalar moment of inertia for the rotation around the axis through
 * <code>point</code> in the direction <code>vector</code>. A uniform unit density is assumed.
 * </p>
 * </blockquote>
 */
public class MomentOfInertia extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (!arg1.isAST()) {
      return F.NIL;
    }
    IAST reg = (IAST) arg1;

    int dimension = RegionEmbeddingDimension.getEmbeddingDimension(reg);
    if (dimension != 2 && dimension != 3) {
      return F.NIL;
    }

    IExpr point;
    if (ast.argSize() >= 2) {
      point = ast.arg2();
    } else {
      // without an explicit point the moments are taken around the centroid
      point = S.RegionCentroid.funEval(engine, reg);
      if (!point.isPresent()) {
        return F.NIL;
      }
    }
    if (!point.isList() || point.argSize() != dimension) {
      return F.NIL;
    }

    IExpr tensor = inertiaTensor(reg, (IAST) point, dimension, engine);
    if (!tensor.isPresent()) {
      return F.NIL;
    }
    if (ast.argSize() < 3) {
      return tensor;
    }

    IExpr v = ast.arg3();
    if (!v.isList() || v.argSize() != dimension) {
      return F.NIL;
    }
    // the moment around the normalized axis direction v
    return engine.evaluate(F.Divide(F.Dot(v, F.Dot(tensor, v)), F.Dot(v, v)));
  }

  /**
   * <code>{{Integral(y^2+z^2), -Integral(x*y), -Integral(x*z)}, ...}</code> with all coordinates
   * taken relative to <code>point</code>. In two dimensions the tensor reduces to
   * <code>{{Integral(y^2), -Integral(x*y)}, {-Integral(x*y), Integral(x^2)}}</code>.
   */
  private IExpr inertiaTensor(IAST reg, IAST point, int dimension, EvalEngine engine) {
    IExpr[][] moment = new IExpr[dimension][dimension];
    for (int i = 0; i < dimension; i++) {
      for (int j = i; j < dimension; j++) {
        int[] exponents = new int[dimension];
        exponents[i]++;
        exponents[j]++;
        IExpr value = RegionPrimitives.shiftedMoment(reg, exponents, point, engine);
        if (!value.isPresent()) {
          return F.NIL;
        }
        moment[i][j] = value;
        moment[j][i] = value;
      }
    }

    IASTAppendable matrix = F.ListAlloc(dimension);
    for (int i = 0; i < dimension; i++) {
      IASTAppendable row = F.ListAlloc(dimension);
      for (int j = 0; j < dimension; j++) {
        if (i == j) {
          IASTAppendable sum = F.PlusAlloc(dimension);
          for (int k = 0; k < dimension; k++) {
            if (k != i) {
              sum.append(moment[k][k]);
            }
          }
          row.append(sum);
        } else {
          row.append(F.Negate(moment[i][j]));
        }
      }
      matrix.append(row);
    }
    return engine.evaluate(matrix);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
