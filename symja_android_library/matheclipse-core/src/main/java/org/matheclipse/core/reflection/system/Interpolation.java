package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Interpolation extends AbstractEvaluator {

  public Interpolation() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int[] dims = ast.arg1().isMatrix();
    if (dims != null && dims[0] > 2 && dims[1] >= 2) {
      String method = "";
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      IExpr option = options.getOption(S.Method);
      if (option.isPresent()) {
        method = option.toString();
      }
      if (!method.isEmpty()) {
        // TODO: if ("Spline".equals(method)) {
        if ("Hermite".equals(method)) {
          return hermiteInterpolate((IAST) ast.arg1(), dims, engine);
        }
        return Errors.printMessage(ast.topHead(), "optx", F.list(S.Method, ast), engine);
      }
      if (ast.isAST1()) {
        if (dims[1] >= 2) {
          int rowsSize = dims[0];
          if (rowsSize >= 4) {
            return piecewisePolynomialInterpolate(ast, rowsSize, engine);
          }
        }

        return F.NIL;
      }
    }
    return F.NIL;
  }

  public IExpr piecewisePolynomialInterpolate(final IAST ast, int rowsSize, EvalEngine engine) {
    final IAST function = ast;
    if (function.isAST1()) {

      if (rowsSize >= 4) {
        IAST matrix = (IAST) function.arg1();
        // do a Piecewise polynomial interpolation with InterpolatingPolynomial
        IASTAppendable list1 = F.ListAlloc(rowsSize);
        IASTAppendable minMaxList = F.ListAlloc(rowsSize);
        for (int j = 1; j < matrix.size(); j++) {
          IExpr arg1 = matrix.get(j).first();
          minMaxList.append(arg1);
        }
        double min = engine.evaluate(minMaxList.apply(S.Min, 1)).evalf();
        double max = engine.evaluate(minMaxList.apply(S.Max, 1)).evalf();
        if (rowsSize <= 5) {
          IAST interpolator = F.Function(F.InterpolatingPolynomial(matrix, F.Slot1));
          return InterpolatingFunctionExpr.newInstance(interpolator, min, max);
        }

        int i = 1;
        for (int j = i + 3; j <= rowsSize; j++) {
          IAST compare;
          compare = createComparator(matrix, i, j, rowsSize);
          IASTAppendable data = F.ListAlloc(4);
          for (int k = i; k <= j; k++) {
            data.append(matrix.get(k));
          }
          IAST list = F.list(F.InterpolatingPolynomial(data, F.Slot1), compare);
          list1.append(list);
          i++;
        }
        IAST interpolator = F.Function(F.Piecewise(list1));
        return InterpolatingFunctionExpr.newInstance(interpolator, min, max);
      }
    }
    return F.NIL;
  }

  private IAST createComparator(IAST matrix, int i, int j, int size) {
    if (i == 1) {
      // # < matrix[i+2, 1]
      return F.Less(F.Slot1, matrix.getPart(i + 2, 1));
    } else {
      if (j < size) {
        // matrix[i+1, 1] <= # < matrix[i+2, 1]
        return F.And(F.LessEqual(matrix.getPart(i + 1, 1), F.Slot1),
            F.Less(F.Slot1, matrix.getPart(i + 2, 1)));
      } else {
        // # >= matrix[i+1, 1]
        return F.GreaterEqual(F.Slot1, matrix.getPart(i + 1, 1));
      }
    }
  }

  private InterpolatingFunctionExpr hermiteInterpolate(IAST matrixAST, int[] dims,
      EvalEngine engine) {

    int colDim = dims[1];
    FieldHermiteInterpolator<IExpr> interpolator = new FieldHermiteInterpolator<IExpr>();

    IASTAppendable minMaxList = F.mapList(matrixAST, t -> {
      final IAST row = (IAST) t;
      IExpr[] arr = new IExpr[colDim - 1];
      for (int j = 0; j < arr.length; j++) {
        arr[j] = row.get(j + 2);
      }
      IExpr arg1 = row.arg1();
      interpolator.addSamplePoint(arg1, arr);
      return arg1;
    });

    double min = engine.evaluate(minMaxList.apply(S.Min, 1)).evalf();
    double max = engine.evaluate(minMaxList.apply(S.Max, 1)).evalf();
    return InterpolatingFunctionExpr.newInstance(interpolator, min, max);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_5;
  }
}
