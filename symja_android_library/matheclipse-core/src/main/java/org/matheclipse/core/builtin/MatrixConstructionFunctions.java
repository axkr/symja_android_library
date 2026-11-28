package org.matheclipse.core.builtin;

import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.QRDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.SingularValueDecomposition;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Matrices built from data and geometric transformation matrices: <code>BlockDiagonalMatrix</code>,
 * <code>LowerTriangularMatrix</code>, <code>UpperTriangularMatrix</code>,
 * <code>CauchyMatrix</code>, <code>SymmetrizedArray</code>, <code>ReflectionMatrix</code>,
 * <code>ScalingMatrix</code>, <code>ShearingMatrix</code>, <code>RollPitchYawMatrix</code>,
 * <code>TransformationMatrix</code>, <code>RescalingTransform</code>,
 * <code>LinearFractionalTransform</code>, <code>FindGeometricTransform</code> and
 * <code>NormalizedSquaredEuclideanDistance</code>.
 *
 * <p>
 * Symja has no structured arrays: the matrix constructors give the dense matrix.
 */
public final class MatrixConstructionFunctions {

  private static class Initializer {

    private static void init() {
      S.BlockDiagonalMatrix.setEvaluator(new BlockDiagonalMatrix());
      S.LowerTriangularMatrix.setEvaluator(new TriangularMatrix(true));
      S.UpperTriangularMatrix.setEvaluator(new TriangularMatrix(false));
      S.CauchyMatrix.setEvaluator(new CauchyMatrix());
      S.SymmetrizedArray.setEvaluator(new SymmetrizedArray());
      S.ReflectionMatrix.setEvaluator(new LinearPart(S.ReflectionTransform, 1, 1));
      S.ScalingMatrix.setEvaluator(new ScalingMatrix());
      S.ShearingMatrix.setEvaluator(new LinearPart(S.ShearingTransform, 3, 3));
      S.RollPitchYawMatrix.setEvaluator(new RollPitchYawMatrix());
      S.TransformationMatrix.setEvaluator(new TransformationMatrix());
      S.RescalingTransform.setEvaluator(new RescalingTransform());
      S.LinearFractionalTransform.setEvaluator(new LinearFractionalTransform());
      S.FindGeometricTransform.setEvaluator(new FindGeometricTransform());
      S.NormalizedSquaredEuclideanDistance
          .setEvaluator(new NormalizedSquaredEuclideanDistance());
    }
  }

  /** The arguments before the first option rule. */
  private static int positionalCount(IAST ast) {
    int count = 0;
    for (int i = 1; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        break;
      }
      count++;
    }
    return count;
  }

  /** The matrix of zeros with <code>rows</code> rows and <code>columns</code> columns. */
  private static IExpr[][] zeros(int rows, int columns) {
    IExpr[][] m = new IExpr[rows][columns];
    for (IExpr[] row : m) {
      java.util.Arrays.fill(row, F.C0);
    }
    return m;
  }

  private static IAST toList(IExpr[][] a) {
    IASTAppendable rows = F.ListAlloc(a.length);
    for (IExpr[] row : a) {
      rows.append(F.List(row));
    }
    return rows;
  }

  /** The linear part of a homogeneous matrix: without the last row and column. */
  private static @AllowNIL IExpr linearPart(IExpr homogeneous) {
    int[] dims = homogeneous.isMatrix(false);
    if (dims == null || dims[0] != dims[1] || dims[0] < 2) {
      return F.NIL;
    }
    IAST m = (IAST) homogeneous.normal(false);
    int n = dims[0] - 1;
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      result.append(((IAST) m.get(i)).copyFrom(1, n + 1));
    }
    return result;
  }

  /**
   * <code>BlockDiagonalMatrix({b1, b2, ...})</code> - the matrix with the matrices
   * <code>b1, b2, ...</code> on the diagonal and zeros elsewhere.
   */
  private static final class BlockDiagonalMatrix extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (positionalCount(ast) != 1 || !ast.arg1().isList() || ast.arg1().argSize() == 0) {
        return F.NIL;
      }
      IAST blocks = (IAST) ast.arg1();
      int rows = 0;
      int columns = 0;
      for (IExpr block : blocks) {
        int[] dims = block.isMatrix(false);
        if (dims == null || dims[0] != dims[1]) {
          // the blocks are square matrices
          return F.NIL;
        }
        rows += dims[0];
        columns += dims[1];
      }
      IExpr[][] m = zeros(rows, columns);
      int r = 0;
      int c = 0;
      for (IExpr block : blocks) {
        int[] dims = block.isMatrix(false);
        IAST b = (IAST) block.normal(false);
        for (int i = 0; i < dims[0]; i++) {
          for (int j = 0; j < dims[1]; j++) {
            m[r + i][c + j] = b.getPart(i + 1, j + 1);
          }
        }
        r += dims[0];
        c += dims[1];
      }
      return toList(m);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>LowerTriangularMatrix(m)</code>, <code>UpperTriangularMatrix(m)</code> - the triangular
   * matrix <code>m</code>; a matrix with non-zero entries on the other side of the diagonal stays
   * unevaluated.
   */
  private static final class TriangularMatrix extends AbstractFunctionEvaluator {
    final boolean lower;

    TriangularMatrix(boolean lower) {
      this.lower = lower;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (positionalCount(ast) != 1) {
        return F.NIL;
      }
      int[] dims = ast.arg1().isMatrix(false);
      if (dims == null) {
        return F.NIL;
      }
      IAST m = (IAST) ast.arg1().normal(false);
      for (int i = 1; i <= dims[0]; i++) {
        for (int j = 1; j <= dims[1]; j++) {
          if ((lower ? j > i : j < i) && !m.getPart(i, j).isZero()) {
            // The matrix m = `1` is not a lower (upper) triangular matrix ...
            return Errors.printMessage(ast.topHead(), lower ? "ltmat" : "utmat",
                F.list(ast.arg1()), engine);
          }
        }
      }
      return m;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>CauchyMatrix(x, y)</code> - the matrix with the entries <code>1/(x(i) + y(j))</code>;
   * <code>CauchyMatrix(x)</code> is <code>CauchyMatrix(x, x)</code>.
   */
  private static final class CauchyMatrix extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int count = positionalCount(ast);
      if (count < 1 || count > 2) {
        return F.NIL;
      }
      IExpr x = ast.arg1();
      IExpr y = count == 2 ? ast.arg2() : x;
      if (x.isVector() < 1 || y.isVector() < 1) {
        return F.NIL;
      }
      IAST xs = (IAST) x.normal(false);
      IAST ys = (IAST) y.normal(false);
      IASTAppendable rows = F.ListAlloc(xs.argSize());
      for (IExpr xi : xs) {
        IASTAppendable row = F.ListAlloc(ys.argSize());
        for (IExpr yj : ys) {
          row.append(F.Power(F.Plus(xi, yj), F.CN1));
        }
        rows.append(row);
      }
      return engine.evaluate(rows);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>SymmetrizedArray({pos1 -> v1, ...}, dims, sym)</code> - the array of dimensions
   * <code>dims</code> whose entries follow from the given ones by the symmetry
   * <code>Symmetric({slots})</code> or <code>Antisymmetric({slots})</code>: every permutation of the
   * indices in the slots gives the same entry, times the sign of the permutation for
   * <code>Antisymmetric</code>.
   */
  private static final class SymmetrizedArray extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST3() || !ast.arg1().isListOfRules(false) || ast.arg2().isVector() < 1) {
        return F.NIL;
      }
      IAST dimsList = (IAST) ast.arg2().normal(false);
      int rank = dimsList.argSize();
      int[] dims = new int[rank];
      for (int i = 0; i < rank; i++) {
        dims[i] = dimsList.get(i + 1).toIntDefault();
        if (dims[i] <= 0) {
          return F.NIL;
        }
      }
      IExpr symmetry = ast.arg3();
      boolean antisymmetric = symmetry.isAST(S.Antisymmetric, 2);
      if (!antisymmetric && !symmetry.isAST(S.Symmetric, 2)) {
        return F.NIL;
      }
      int[] slots;
      if (symmetry.first() == S.All) {
        slots = new int[rank];
        for (int i = 0; i < rank; i++) {
          slots[i] = i;
        }
      } else if (symmetry.first().isList()) {
        IAST slotList = (IAST) symmetry.first();
        slots = new int[slotList.argSize()];
        for (int i = 0; i < slots.length; i++) {
          slots[i] = slotList.get(i + 1).toIntDefault() - 1;
          if (slots[i] < 0 || slots[i] >= rank || dims[slots[i]] != dims[slots[0]]) {
            return F.NIL;
          }
        }
      } else {
        return F.NIL;
      }
      long size = 1;
      for (int d : dims) {
        size *= d;
        if (size > 1_000_000L) {
          return F.NIL;
        }
      }
      IExpr[] entries = new IExpr[(int) size];
      java.util.Arrays.fill(entries, F.C0);
      for (IExpr rule : (IAST) ast.arg1()) {
        IExpr position = rule.first();
        if (position.isVector() != rank) {
          return F.NIL;
        }
        int[] index = new int[rank];
        for (int i = 0; i < rank; i++) {
          index[i] = position.getAt(i + 1).toIntDefault() - 1;
          if (index[i] < 0 || index[i] >= dims[i]) {
            return F.NIL;
          }
        }
        IExpr value = rule.second();
        permute(slots, 0, index, 1, value, antisymmetric, dims, entries, engine);
      }
      return engine.evaluate(F.ArrayReshape(F.List(entries), F.List(dims)));
    }

    /** Store <code>sign*value</code> at every permutation of the indices in the slots. */
    private static void permute(int[] slots, int k, int[] index, int sign, IExpr value,
        boolean antisymmetric, int[] dims, IExpr[] entries, EvalEngine engine) {
      if (k == slots.length) {
        int offset = 0;
        for (int i = 0; i < dims.length; i++) {
          offset = offset * dims[i] + index[i];
        }
        entries[offset] = antisymmetric && sign < 0 ? engine.evaluate(F.Negate(value)) : value;
        return;
      }
      engine.checkTimeLimit();
      for (int i = k; i < slots.length; i++) {
        swap(index, slots[k], slots[i]);
        permute(slots, k + 1, index, i == k ? sign : -sign, value, antisymmetric, dims, entries,
            engine);
        swap(index, slots[k], slots[i]);
      }
    }

    private static void swap(int[] index, int a, int b) {
      int temp = index[a];
      index[a] = index[b];
      index[b] = temp;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * The linear part of the homogeneous matrix of a transformation:
   * <code>ReflectionMatrix(v)</code> of <code>ReflectionTransform(v)</code> and
   * <code>ShearingMatrix(theta, v, n)</code> of <code>ShearingTransform(theta, v, n)</code>.
   */
  private static final class LinearPart extends AbstractFunctionEvaluator {
    final IExpr transform;
    final int min;
    final int max;

    LinearPart(IExpr transform, int min, int max) {
      this.transform = transform;
      this.min = min;
      this.max = max;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = engine.evaluate(ast.setAtCopy(0, transform));
      if (function.isAST(S.TransformationFunction, 2)) {
        return linearPart(function.first());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return new int[] {min, max};
    }
  }

  /**
   * <code>ScalingMatrix({s1, s2, ...})</code> - the diagonal matrix of the factors;
   * <code>ScalingMatrix(s, v)</code> - the scaling by <code>s</code> along the vector
   * <code>v</code>.
   */
  private static final class ScalingMatrix extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return ast.arg1().isVector() > 0 ? engine.evaluate(F.DiagonalMatrix(ast.arg1())) : F.NIL;
      }
      IExpr function = engine.evaluate(F.binaryAST2(S.ScalingTransform, ast.arg1(), ast.arg2()));
      if (function.isAST(S.TransformationFunction, 2)) {
        return linearPart(function.first());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>RollPitchYawMatrix({a, b, c}, {i, j, k})</code> - the rotation
   * <code>R(k, c) . R(j, b) . R(i, a)</code> about the coordinate axes <code>i, j, k</code>, by
   * default <code>{3, 2, 1}</code>.
   */
  private static final class RollPitchYawMatrix extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isVector() != 3) {
        return F.NIL;
      }
      IAST angles = (IAST) ast.arg1().normal(false);
      int[] axes = {3, 2, 1};
      if (ast.isAST2()) {
        if (ast.arg2().isVector() != 3) {
          return F.NIL;
        }
        for (int i = 0; i < 3; i++) {
          axes[i] = ast.arg2().getAt(i + 1).toIntDefault();
          if (axes[i] < 1 || axes[i] > 3) {
            return F.NIL;
          }
        }
      }
      IExpr result = F.NIL;
      for (int i = 0; i < 3; i++) {
        IASTAppendable axis = F.ListAlloc(3);
        for (int k = 1; k <= 3; k++) {
          axis.append(k == axes[i] ? F.C1 : F.C0);
        }
        IExpr rotation = F.binaryAST2(S.RotationMatrix, angles.get(i + 1), axis);
        result = result.isNIL() ? rotation : F.Dot(rotation, result);
      }
      return engine.evaluate(result);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>TransformationMatrix(TransformationFunction(m))</code> - the homogeneous matrix
   * <code>m</code>.
   */
  private static final class TransformationMatrix extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST(S.TransformationFunction, 2)) {
        return ast.arg1().first();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>RescalingTransform({{xmin, xmax}, ...})</code> - the affine map of the box onto the unit
   * cube; <code>RescalingTransform(box, target)</code> onto the box <code>target</code>.
   */
  private static final class RescalingTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dims = ast.arg1().isMatrix(false);
      if (dims == null || dims[1] != 2) {
        return F.NIL;
      }
      int n = dims[0];
      IAST box = (IAST) ast.arg1().normal(false);
      IAST target;
      if (ast.isAST2()) {
        int[] targetDims = ast.arg2().isMatrix(false);
        if (targetDims == null || targetDims[0] != n || targetDims[1] != 2) {
          return F.NIL;
        }
        target = (IAST) ast.arg2().normal(false);
      } else {
        IASTAppendable unit = F.ListAlloc(n);
        for (int i = 0; i < n; i++) {
          unit.append(F.list(F.C0, F.C1));
        }
        target = unit;
      }
      IExpr[][] m = zeros(n + 1, n + 1);
      for (int i = 0; i < n; i++) {
        IExpr min = box.getPart(i + 1, 1);
        IExpr max = box.getPart(i + 1, 2);
        IExpr tmin = target.getPart(i + 1, 1);
        IExpr tmax = target.getPart(i + 1, 2);
        IExpr scale = engine.evaluate(F.Divide(F.Subtract(tmax, tmin), F.Subtract(max, min)));
        if (!scale.isFree(x -> x.isIndeterminate() || x.isDirectedInfinity(), true)) {
          return F.NIL;
        }
        m[i][i] = scale;
        m[i][n] = engine.evaluate(F.Together(F.Subtract(tmin, F.Times(scale, min))));
      }
      m[n][n] = F.C1;
      return F.TransformationFunction(toList(m));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>LinearFractionalTransform({m, v, w, b})</code> - the projective map
   * <code>x -&gt; (m.x + v)/(w.x + b)</code>; <code>LinearFractionalTransform(h)</code> with the
   * homogeneous matrix <code>h</code>.
   */
  private static final class LinearFractionalTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg = ast.arg1();
      int[] dims = arg.isMatrix(false);
      if (dims != null && dims[0] == dims[1] && dims[0] > 1) {
        return F.TransformationFunction(arg);
      }
      if (!arg.isList() || arg.argSize() != 4) {
        return F.NIL;
      }
      IExpr m = arg.first();
      IExpr v = arg.second();
      IExpr w = ((IAST) arg).arg3();
      IExpr b = ((IAST) arg).arg4();
      int[] md = m.isMatrix(false);
      if (md == null || md[0] != md[1] || v.isVector() != md[0] || w.isVector() != md[0]) {
        return F.NIL;
      }
      int n = md[0];
      IAST mm = (IAST) m.normal(false);
      IASTAppendable rows = F.ListAlloc(n + 1);
      for (int i = 1; i <= n; i++) {
        IASTAppendable row = ((IAST) mm.get(i)).copyAppendable();
        row.append(v.getAt(i));
        rows.append(row);
      }
      IASTAppendable last = ((IAST) w.normal(false)).copyAppendable();
      last.append(b);
      rows.append(last);
      return F.TransformationFunction(rows);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>FindGeometricTransform(pts1, pts2)</code> - <code>{error, TransformationFunction(m)}</code>
   * with the transformation that maps the points <code>pts2</code> onto <code>pts1</code> as
   * closely as possible in the least squares sense and the mean distance <code>error</code> of the
   * mapped points from <code>pts1</code>. The option <code>TransformationClass</code> is
   * <code>"Translation"</code>, <code>"Rigid"</code>, <code>"Similarity"</code>,
   * <code>"Affine"</code>, <code>"Perspective"</code> (plane points only) or
   * <code>Automatic</code>: the most general class the number of points determines.
   */
  private static final class FindGeometricTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (positionalCount(ast) != 2) {
        return F.NIL;
      }
      int[] d1 = ast.arg1().isMatrix(false);
      int[] d2 = ast.arg2().isMatrix(false);
      if (d1 == null || d2 == null || d1[0] != d2[0] || d1[1] != d2[1] || d1[0] == 0) {
        return F.NIL;
      }
      String transformationClass = "Automatic";
      for (int i = 3; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (option.isRuleAST() && option.first() == S.TransformationClass) {
          IExpr value = option.second();
          transformationClass = value.isString() ? value.toString() : "Automatic";
        } else {
          return F.NIL;
        }
      }
      final int count = d1[0];
      final int dim = d1[1];
      double[][] to;
      double[][] from;
      try {
        to = engine.evaluate(F.N(ast.arg1())).toDoubleMatrix(false);
        from = engine.evaluate(F.N(ast.arg2())).toDoubleMatrix(false);
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      if (to == null || from == null) {
        return F.NIL;
      }
      if (transformationClass.equals("Automatic")) {
        if (count == 1) {
          transformationClass = "Translation";
        } else if (count < dim + 1) {
          transformationClass = "Similarity";
        } else if (dim == 2 && count >= 4) {
          transformationClass = "Perspective";
        } else {
          transformationClass = "Affine";
        }
      }
      double[][] h;
      switch (transformationClass) {
        case "Translation":
          h = translation(from, to, dim);
          break;
        case "Rigid":
          h = procrustes(from, to, dim, false);
          break;
        case "Similarity":
          h = procrustes(from, to, dim, true);
          break;
        case "Affine":
          if (count < dim + 1) {
            return F.NIL;
          }
          h = affine(from, to, dim);
          break;
        case "Perspective":
          if (dim != 2 || count < 4) {
            return F.NIL;
          }
          h = perspective(from, to);
          break;
        default:
          return F.NIL;
      }
      if (h == null) {
        return F.NIL;
      }
      double error = 0.0;
      for (int p = 0; p < count; p++) {
        double[] image = apply(h, from[p], dim);
        double sum = 0.0;
        for (int i = 0; i < dim; i++) {
          double diff = image[i] - to[p][i];
          sum += diff * diff;
        }
        error += Math.sqrt(sum);
      }
      error /= count;
      final double[][] hm = h;
      // rounding noise of an exact fit is set to 0
      return engine.evaluate(F.Chop(F.list(F.num(error),
          F.TransformationFunction(F.matrix((i, j) -> F.num(hm[i][j]), hm.length, hm.length)))));
    }

    private static double[] apply(double[][] h, double[] x, int dim) {
      double[] w = new double[dim + 1];
      for (int i = 0; i <= dim; i++) {
        double sum = h[i][dim];
        for (int j = 0; j < dim; j++) {
          sum += h[i][j] * x[j];
        }
        w[i] = sum;
      }
      double[] result = new double[dim];
      for (int i = 0; i < dim; i++) {
        result[i] = w[i] / w[dim];
      }
      return result;
    }

    private static double[] mean(double[][] points, int dim) {
      double[] m = new double[dim];
      for (double[] p : points) {
        for (int i = 0; i < dim; i++) {
          m[i] += p[i] / points.length;
        }
      }
      return m;
    }

    private static double[][] identity(int n) {
      double[][] h = new double[n][n];
      for (int i = 0; i < n; i++) {
        h[i][i] = 1.0;
      }
      return h;
    }

    private static double[][] translation(double[][] from, double[][] to, int dim) {
      double[] mf = mean(from, dim);
      double[] mt = mean(to, dim);
      double[][] h = identity(dim + 1);
      for (int i = 0; i < dim; i++) {
        h[i][dim] = mt[i] - mf[i];
      }
      return h;
    }

    /** The rotation (and for a similarity the uniform scale) of the orthogonal Procrustes fit. */
    private static double[][] procrustes(double[][] from, double[][] to, int dim,
        boolean scaled) {
      double[] mf = mean(from, dim);
      double[] mt = mean(to, dim);
      double[][] cross = new double[dim][dim];
      double variance = 0.0;
      for (int p = 0; p < from.length; p++) {
        for (int i = 0; i < dim; i++) {
          double a = from[p][i] - mf[i];
          variance += a * a;
          for (int j = 0; j < dim; j++) {
            cross[i][j] += a * (to[p][j] - mt[j]);
          }
        }
      }
      SingularValueDecomposition svd =
          new SingularValueDecomposition(new Array2DRowRealMatrix(cross));
      RealMatrix u = svd.getU();
      RealMatrix v = svd.getV();
      RealMatrix vut = v.multiply(u.transpose());
      double det = new org.hipparchus.linear.LUDecomposition(vut).getDeterminant();
      double[] diag = new double[dim];
      java.util.Arrays.fill(diag, 1.0);
      diag[dim - 1] = det < 0 ? -1.0 : 1.0;
      RealMatrix d = new Array2DRowRealMatrix(dim, dim);
      for (int i = 0; i < dim; i++) {
        d.setEntry(i, i, diag[i]);
      }
      RealMatrix r = v.multiply(d).multiply(u.transpose());
      double scale = 1.0;
      if (scaled) {
        if (variance == 0.0) {
          return null;
        }
        double[] s = svd.getSingularValues();
        double trace = 0.0;
        for (int i = 0; i < dim; i++) {
          trace += s[i] * diag[i];
        }
        scale = trace / variance;
      }
      double[][] h = identity(dim + 1);
      for (int i = 0; i < dim; i++) {
        double shift = mt[i];
        for (int j = 0; j < dim; j++) {
          h[i][j] = scale * r.getEntry(i, j);
          shift -= h[i][j] * mf[j];
        }
        h[i][dim] = shift;
      }
      return h;
    }

    /** The least squares affine map. */
    private static double[][] affine(double[][] from, double[][] to, int dim) {
      int count = from.length;
      RealMatrix design = new Array2DRowRealMatrix(count, dim + 1);
      for (int p = 0; p < count; p++) {
        for (int j = 0; j < dim; j++) {
          design.setEntry(p, j, from[p][j]);
        }
        design.setEntry(p, dim, 1.0);
      }
      org.hipparchus.linear.DecompositionSolver solver =
          new QRDecomposition(design).getSolver();
      double[][] h = identity(dim + 1);
      for (int i = 0; i < dim; i++) {
        double[] rhs = new double[count];
        for (int p = 0; p < count; p++) {
          rhs[p] = to[p][i];
        }
        double[] row;
        try {
          row = solver.solve(new ArrayRealVector(rhs)).toArray();
        } catch (RuntimeException rex) {
          return null;
        }
        System.arraycopy(row, 0, h[i], 0, dim + 1);
      }
      return h;
    }

    /** The least squares homography with <code>h(3,3) = 1</code> of plane points. */
    private static double[][] perspective(double[][] from, double[][] to) {
      int count = from.length;
      RealMatrix design = new Array2DRowRealMatrix(2 * count, 8);
      double[] rhs = new double[2 * count];
      for (int p = 0; p < count; p++) {
        double x = from[p][0];
        double y = from[p][1];
        double u = to[p][0];
        double v = to[p][1];
        double[] r1 = {x, y, 1, 0, 0, 0, -u * x, -u * y};
        double[] r2 = {0, 0, 0, x, y, 1, -v * x, -v * y};
        design.setRow(2 * p, r1);
        design.setRow(2 * p + 1, r2);
        rhs[2 * p] = u;
        rhs[2 * p + 1] = v;
      }
      double[] s;
      try {
        s = new QRDecomposition(design).getSolver().solve(new ArrayRealVector(rhs)).toArray();
      } catch (RuntimeException rex) {
        return null;
      }
      return new double[][] {{s[0], s[1], s[2]}, {s[3], s[4], s[5]}, {s[6], s[7], 1.0}};
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>NormalizedSquaredEuclideanDistance(u, v)</code> -
   * <code>Norm((u-Mean(u)) - (v-Mean(v)))^2 / (2*(Norm(u-Mean(u))^2 + Norm(v-Mean(v))^2))</code>.
   */
  private static final class NormalizedSquaredEuclideanDistance
      extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg1().isVector();
      if (n < 1 || ast.arg2().isVector() != n) {
        return F.NIL;
      }
      IExpr u = F.Subtract(ast.arg1(), F.Mean(ast.arg1()));
      IExpr v = F.Subtract(ast.arg2(), F.Mean(ast.arg2()));
      IExpr numerator = engine.evaluate(F.Sqr(F.Norm(F.Subtract(u, v))));
      if (numerator.isZero()) {
        return numerator;
      }
      IExpr denominator = F.Times(F.C2, F.Plus(F.Sqr(F.Norm(u)), F.Sqr(F.Norm(v))));
      return engine.evaluate(F.Together(F.Divide(numerator, denominator)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private MatrixConstructionFunctions() {}
}
