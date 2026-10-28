package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import java.util.TreeMap;
import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GridInterpolatingFunctionExpr;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class Interpolation extends AbstractFunctionOptionEvaluator {

  public Interpolation() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr result = interpolate(ast, argSize, options, engine, originalAST);
    if (result instanceof InterpolatingFunctionExpr
        && !(result instanceof GridInterpolatingFunctionExpr)) {
      // points at exact abscissae keep them exact in the domain
      IExpr[] domain = exactDomain(ast.arg1());
      if (domain != null) {
        ((InterpolatingFunctionExpr<?>) result).withExactDomain(domain[0], domain[1]);
      }
    }
    return result;
  }

  /**
   * The smallest and the largest abscissa of the points <code>{{x1, ...}, {x2, ...}, ...}</code>,
   * or <code>null</code> unless all of them are rational numbers.
   */
  private static IExpr[] exactDomain(IExpr data) {
    if (!data.isList() || data.argSize() == 0) {
      return null;
    }
    IRational min = null;
    IRational max = null;
    for (IExpr point : (IAST) data) {
      if (!point.isList() || point.argSize() < 2 || !point.first().isRational()) {
        return null;
      }
      IRational x = (IRational) point.first();
      if (min == null || x.isLT(min)) {
        min = x;
      }
      if (max == null || x.isGT(max)) {
        max = x;
      }
    }
    return new IExpr[] {min, max};
  }

  private IExpr interpolate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
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
    int[] rowDims = ast.arg1().isMatrix();
    if (rowDims != null && rowDims[1] == 1) {
      // rows {v} of one element are the values themselves
      return evaluate(ast.setAtCopy(1, ((IAST) ast.arg1()).map(row -> row.first())), argSize,
          options, engine, originalAST);
    }
    if (ast.arg1().isList() && ast.arg1().argSize() > 0) {
      IAST data = (IAST) ast.arg1();
      String method = options[0].isAutomatic() ? "" : options[0].toString();
      IExpr orderOption = options.length > 1 ? options[1] : S.Automatic;
      IExpr function = F.NIL;
      if ("Spline".equals(method)) {
        function = spline(data, orderOption, engine);
        if (function.isNIL()) {
          return F.NIL;
        }
      } else if (method.isEmpty() && data.forAll(row -> row.isList() && row.argSize() >= 2
          && row.first().isList())) {
        // points {{x1, x2, ...}, f, f', f'', ...}
        function = localGrid(data, true, orderOption, engine);
        if (function.isNIL()) {
          return F.NIL;
        }
      } else if (method.isEmpty() && data.forAll(row -> row.isList() && row.argSize() >= 3)) {
        // rows {x1, x2, ..., f} of a function of several variables
        function = localGrid(data, false, orderOption, engine);
      }
      if (function.isPresent()) {
        return argSize == 2 ? engine.evaluate(F.unaryAST1(function, ast.arg2())) : function;
      }
    }
    IExpr tensorValued = tensorValued(ast, argSize, originalAST, engine);
    if (tensorValued.isPresent()) {
      return tensorValued;
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

  /**
   * The interpolation orders of the <code>dimension</code> axes from the
   * <code>InterpolationOrder</code> option: <code>3</code> by default, one order for all axes or a
   * list of one order for each.
   *
   * @return <code>null</code> if the option isn't one of these
   */
  private static int[] interpolationOrders(IExpr option, int dimension) {
    int[] orders = new int[dimension];
    if (option.isAutomatic()) {
      Arrays.fill(orders, 3);
      return orders;
    }
    if (option.isList()) {
      if (option.argSize() != dimension) {
        return null;
      }
      for (int i = 0; i < dimension; i++) {
        orders[i] = option.getAt(i + 1).toIntDefault();
        if (orders[i] < 1) {
          return null;
        }
      }
      return orders;
    }
    int order = option.toIntDefault();
    if (order < 1) {
      return null;
    }
    Arrays.fill(orders, order);
    return orders;
  }

  /**
   * The values in <code>data</code> must all be of one shape: numbers, or tensors of the same
   * dimensions.
   */
  private static boolean sameShape(IExpr value, IExpr first, EvalEngine engine) {
    if (!value.isList() && !first.isList()) {
      return true;
    }
    return engine.evaluate(F.Dimensions(value)).equals(engine.evaluate(F.Dimensions(first)));
  }

  /**
   * Local polynomial interpolation of the points of a complete grid, in one variable also through
   * the derivatives given at the points.
   *
   * @param data the points <code>{{x1, x2, ...}, f}</code> or in one variable
   *        <code>{{x}, f, f', f'', ...}</code> if <code>pairs</code>, otherwise the rows
   *        <code>{x1, x2, ..., f}</code>
   * @return {@link F#NIL} if the points don't make up a complete grid
   */
  private static IExpr localGrid(IAST data, boolean pairs, IExpr orderOption,
      EvalEngine engine) {
    final int rows = data.argSize();
    int dimension = -1;
    IAST[] tuples = new IAST[rows];
    IExpr[][] conditions = new IExpr[rows][];
    for (int r = 0; r < rows; r++) {
      IAST row = (IAST) data.get(r + 1);
      IAST tuple = pairs ? (IAST) row.first() : row.copyUntil(row.argSize());
      int valueStart = pairs ? 2 : row.argSize();
      if (dimension < 0) {
        dimension = tuple.argSize();
      }
      if (tuple.argSize() != dimension || dimension == 0 || !tuple.forAll(x -> x.isReal())) {
        return F.NIL;
      }
      IExpr[] values = new IExpr[row.argSize() - valueStart + 1];
      if (dimension > 1 && values.length != 1) {
        // only a function of one variable takes derivatives at its points
        return F.NIL;
      }
      for (int i = 0; i < values.length; i++) {
        values[i] = row.get(valueStart + i);
        if (!sameShape(values[i], r == 0 ? values[0] : conditions[0][0], engine)) {
          return F.NIL;
        }
      }
      tuples[r] = tuple;
      conditions[r] = values;
    }
    // the coordinates along each axis, in increasing order
    IExpr[][] coordinates = new IExpr[dimension][];
    double[][] coordinatesN = new double[dimension][];
    int total = 1;
    for (int axis = 0; axis < dimension; axis++) {
      TreeMap<Double, IExpr> distinct = new TreeMap<Double, IExpr>();
      for (IAST tuple : tuples) {
        distinct.putIfAbsent(tuple.get(axis + 1).evalf(), tuple.get(axis + 1));
      }
      if (distinct.size() < 2) {
        return F.NIL;
      }
      coordinates[axis] = distinct.values().toArray(new IExpr[0]);
      coordinatesN[axis] = new double[distinct.size()];
      int i = 0;
      for (Double x : distinct.keySet()) {
        coordinatesN[axis][i++] = x;
      }
      total *= distinct.size();
      if (total > rows) {
        return F.NIL;
      }
    }
    if (total != rows) {
      // not a complete grid
      return F.NIL;
    }
    IExpr[][] gridData = new IExpr[rows][];
    for (int r = 0; r < rows; r++) {
      int index = 0;
      for (int axis = 0; axis < dimension; axis++) {
        int position =
            Arrays.binarySearch(coordinatesN[axis], tuples[r].get(axis + 1).evalf());
        index = index * coordinatesN[axis].length + position;
      }
      if (gridData[index] != null) {
        // a point given twice
        return F.NIL;
      }
      gridData[index] = conditions[r];
    }
    int[] orders = interpolationOrders(orderOption, dimension);
    if (orders == null) {
      return F.NIL;
    }
    return GridInterpolatingFunctionExpr.newLocal(coordinates, gridData, orders, engine);
  }

  /**
   * The interpolating B-spline of the degree <code>InterpolationOrder</code> through the points
   * <code>{{x1, f1}, {x2, f2}, ...}</code>.
   */
  private static IExpr spline(IAST data, IExpr orderOption, EvalEngine engine) {
    final int n = data.argSize();
    if (n < 2) {
      return F.NIL;
    }
    int[] orders = interpolationOrders(orderOption, 1);
    if (orders == null) {
      return F.NIL;
    }
    TreeMap<Double, IExpr[]> points = new TreeMap<Double, IExpr[]>();
    IExpr first = null;
    for (IExpr row : data) {
      if (!row.isList2()) {
        return F.NIL;
      }
      IExpr x = row.first();
      if (x.isList1()) {
        x = x.first();
      }
      IExpr y = row.second();
      if (!x.isReal()) {
        return F.NIL;
      }
      if (first == null) {
        first = y;
      } else if (!sameShape(y, first, engine)) {
        return F.NIL;
      }
      if (points.put(x.evalf(), new IExpr[] {x, y}) != null) {
        // an abscissa given twice
        return F.NIL;
      }
    }
    IExpr[] xs = new IExpr[n];
    IExpr[] ys = new IExpr[n];
    int i = 0;
    for (IExpr[] point : points.values()) {
      xs[i] = point[0];
      ys[i] = point[1];
      i++;
    }
    GridInterpolatingFunctionExpr function =
        GridInterpolatingFunctionExpr.newSpline(xs, ys, orders[0], engine);
    return function == null ? F.NIL : function;
  }

  /**
   * <code>Interpolation({{x1, v1}, {x2, v2}, ...})</code> with values which are vectors or
   * matrices of one shape: each component is interpolated on its own, and the result gives the
   * tensor of the component values.
   *
   * @return {@link F#NIL} if the values aren't lists of one shape
   */
  private static IExpr tensorValued(IAST ast, int argSize, IAST originalAST, EvalEngine engine) {
    IExpr data = ast.arg1();
    if (!data.isList() || data.argSize() < 2) {
      return F.NIL;
    }
    IExpr shape = null;
    for (IExpr point : (IAST) data) {
      if (!point.isList2() || !point.second().isList()) {
        return F.NIL;
      }
      IExpr pointShape = F.Dimensions(point.second());
      pointShape = engine.evaluate(pointShape);
      if (shape == null) {
        shape = pointShape;
      } else if (!shape.equals(pointShape)) {
        return F.NIL;
      }
    }
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (IExpr point : (IAST) data) {
      double abscissa = point.first().evalfNaN();
      if (Double.isNaN(abscissa)) {
        return F.NIL;
      }
      min = Math.min(min, abscissa);
      max = Math.max(max, abscissa);
    }
    IExpr components =
        componentFunctions((IAST) data, F.CEmptyList, (IAST) shape, originalAST, engine);
    if (components.isNIL()) {
      return F.NIL;
    }
    IExpr function =
        InterpolatingFunctionExpr.newInstance((IAST) F.Function(components), min, max);
    return argSize == 2 ? engine.evaluate(F.unaryAST1(function, ast.arg2())) : function;
  }

  /**
   * The tensor of the interpolations of the components at the positions below
   * <code>position</code>, each applied to <code>#1</code>.
   */
  private static IExpr componentFunctions(IAST data, IAST position, IAST shape, IAST originalAST,
      EvalEngine engine) {
    int depth = position.argSize();
    if (depth == shape.argSize()) {
      int[] indices = new int[depth];
      for (int i = 0; i < depth; i++) {
        indices[i] = position.get(i + 1).toIntDefault();
      }
      IASTAppendable componentData = F.ListAlloc(data.argSize());
      for (IExpr point : data) {
        componentData.append(F.list(point.first(), ((IAST) point.second()).getPart(indices)));
      }
      IASTAppendable call = F.ast(S.Interpolation, originalAST.argSize());
      call.append(componentData);
      for (int i = 2; i < originalAST.size(); i++) {
        if (originalAST.get(i).isRuleAST()) {
          call.append(originalAST.get(i));
        }
      }
      IExpr interpolation = engine.evaluate(call);
      if (interpolation.isAST(S.Interpolation)) {
        return F.NIL;
      }
      return F.unaryAST1(interpolation, F.Slot1);
    }
    int size = shape.get(depth + 1).toIntDefault();
    IASTAppendable result = F.ListAlloc(size);
    for (int i = 1; i <= size; i++) {
      IExpr component =
          componentFunctions(data, position.appendClone(F.ZZ(i)), shape, originalAST, engine);
      if (component.isNIL()) {
        return F.NIL;
      }
      result.append(component);
    }
    return result;
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
    // symbolic values are weighted linearly with machine number weights, as an interpolating
    // function evaluates numerically
    final boolean symbolicLinear =
        order == 1 && !matrix.forAll(row -> row.second().isNumericFunction(true));
    if (rows <= order + 1) {
      return InterpolatingFunctionExpr.newInstance(
          F.Function(symbolicLinear ? linearPiece(matrix.getAST(1), matrix.getAST(2))
              : F.InterpolatingPolynomial(matrix, F.Slot1)),
          min, max, order);
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
      pieces.append(F.list(symbolicLinear ? linearPiece(data.getAST(1), data.getAST(2))
          : F.InterpolatingPolynomial(data, F.Slot1), condition));
    }
    return InterpolatingFunctionExpr.newInstance(F.Function(F.Piecewise(pieces)), min, max, order);
  }

  /**
   * The line through the points <code>{x1, y1}</code> and <code>{x2, y2}</code> as a function of
   * <code>#</code>: <code>y2 + N((x2 - #)/(x2 - x1))*(y1 - y2)</code>.
   */
  private static IExpr linearPiece(IAST point1, IAST point2) {
    IExpr weight = F.N(F.Divide(F.Subtract(point2.first(), F.Slot1),
        F.Subtract(point2.first(), point1.first())));
    return F.Plus(point2.second(), F.Times(weight, F.Subtract(point1.second(), point2.second())));
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
