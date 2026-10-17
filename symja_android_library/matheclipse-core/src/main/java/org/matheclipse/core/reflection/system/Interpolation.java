package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Interpolation extends AbstractFunctionOptionEvaluator {

  public Interpolation() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (ast.arg1().isVector() > 0 && ast.arg1().isMatrix() == null) {
      // the values {v1, v2, ...} at the points 1, 2, ...
      IAST values = (IAST) ast.arg1();
      IASTAppendable points = F.ListAlloc(values.argSize());
      for (int i = 1; i <= values.argSize(); i++) {
        points.append(F.List(F.ZZ(i), values.get(i)));
      }
      return evaluate(ast.setAtCopy(1, points), argSize, options, engine, originalAST);
    }
    int[] dims = ast.arg1().isMatrix();
    if (dims != null && dims[0] >= 2 && dims[1] >= 2) {
      String method = "";
      IExpr option = options[0];
      if (!option.isAutomatic()) {
        method = option.toString();
      }
      int order = options.length > 1 && !options[1].isAutomatic() ? options[1].toIntDefault() : 3;
      if (order < 1) {
        return F.NIL;
      }
      if (order > dims[0] - 1 && method.isEmpty()) {
        // Requested order is too high; order has been reduced to `1`.
        order = dims[0] - 1;
        Errors.printMessage(S.Interpolation, "inhr", F.list(F.list(F.ZZ(order))), engine);
        IExpr reduced = polynomialPieces((IAST) ast.arg1(), dims[0], order, engine);
        return argSize == 2 && reduced.isPresent() ? F.unaryAST1(reduced, ast.arg2()) : reduced;
      }
      // the call without its options: Interpolation(data) or Interpolation(data, x)
      IAST call = argSize < ast.argSize() ? ast.copyUntil(argSize + 1) : ast;
      IExpr temp = order == 3 ? interpolation(call, dims, method, engine)
          : method.isEmpty() ? polynomialPieces((IAST) ast.arg1(), dims[0], order, engine)
              : F.NIL;
      if (temp instanceof InterpolatingFunctionExpr) {
        if (argSize == 2) {
          InterpolatingFunctionExpr ipf = (InterpolatingFunctionExpr) temp;
          return F.unaryAST1(ipf, ast.arg2());
        }
      }
      return temp;
    }
    return F.NIL;
  }

  public IExpr interpolation(IAST ast, int[] dims, String method, EvalEngine engine) {
    if (!method.isEmpty()) {
      // TODO: if ("Spline".equals(method)) {
      if ("Hermite".equalsIgnoreCase(method)) {
        return hermiteInterpolate((IAST) ast.arg1(), dims, engine);
      }
      return Errors.printMessage(ast.topHead(), "optx", F.list(S.Method, ast), engine);
    }
    if (ast.argSize() == 1 || ast.argSize() == 2) {
      if (dims[1] >= 2) {
        int rowsSize = dims[0];
        if (rowsSize >= 4) {
          return piecewisePolynomialInterpolate(ast, rowsSize, engine);
        }
      }
    }
    return F.NIL;
  }

  public IExpr piecewisePolynomialInterpolate(final IAST ast, int rowsSize, EvalEngine engine) {
    final IAST function = ast;
    if (function.argSize() == 1 || function.argSize() == 2) {

      if (rowsSize >= 4) {
        IAST matrix = (IAST) function.arg1();
        // do a Piecewise polynomial interpolation with InterpolatingPolynomial
        IASTAppendable list1 = F.ListAlloc(rowsSize);
        IASTAppendable minMaxList = F.ListAlloc(rowsSize);
        for (int j = 1; j < matrix.size(); j++) {
          IExpr arg1 = matrix.get(j).first();
          minMaxList.append(arg1);
        }
        double min = engine.evaluate(minMaxList.apply(S.Min, 1)).evalfNaN();
        double max = engine.evaluate(minMaxList.apply(S.Max, 1)).evalfNaN();
        if (Double.isNaN(min) || Double.isNaN(max)) {
          return F.NIL;
        }
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

  /**
   * Interpolation with polynomials of the degree <code>order</code>: one through all points where
   * there are no more than <code>order+1</code>, otherwise between two neighbouring points the
   * polynomial through the <code>order+1</code> points around them.
   */
  private static IExpr polynomialPieces(IAST matrix, int rows, int order, EvalEngine engine) {
    IASTAppendable abscissas = F.ListAlloc(rows);
    for (int j = 1; j < matrix.size(); j++) {
      abscissas.append(matrix.get(j).first());
    }
    double min = engine.evaluate(abscissas.apply(S.Min, 1)).evalfNaN();
    double max = engine.evaluate(abscissas.apply(S.Max, 1)).evalfNaN();
    if (Double.isNaN(min) || Double.isNaN(max)) {
      return F.NIL;
    }
    if (rows <= order + 1) {
      return InterpolatingFunctionExpr
          .newInstance(F.Function(F.InterpolatingPolynomial(matrix, F.Slot1)), min, max, order);
    }
    IASTAppendable pieces = F.ListAlloc(rows);
    for (int j = 1; j < rows; j++) {
      // the interval from point j to point j+1, with the order+1 points around it
      int first = Math.max(1, Math.min(rows - order, j - (order - 1) / 2));
      IASTAppendable data = F.ListAlloc(order + 1);
      for (int k = first; k <= first + order; k++) {
        data.append(matrix.get(k));
      }
      IExpr condition = j < rows - 1 ? F.Less(F.Slot1, matrix.getPart(j + 1, 1)) : S.True;
      pieces.append(F.list(F.InterpolatingPolynomial(data, F.Slot1), condition));
    }
    return InterpolatingFunctionExpr.newInstance(F.Function(F.Piecewise(pieces)), min, max, order);
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

    double min = engine.evaluate(minMaxList.apply(S.Min, 1)).evalfNaN();
    double max = engine.evaluate(minMaxList.apply(S.Max, 1)).evalfNaN();
    if (Double.isNaN(min) || Double.isNaN(max)) {
      return null;
    }
    return InterpolatingFunctionExpr.newInstance(interpolator, min, max);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.Method, S.InterpolationOrder}, //
        new IExpr[] {//
            S.Automatic, S.Automatic});
  }
}
