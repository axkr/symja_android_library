package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecompositionNonSymmetric;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.linear.LUDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>NDEigenvalues(op, u, {x, a, b}, n)</code> and
 * <code>NDEigensystem(op, u, {x, a, b}, n)</code> - the <code>n</code> eigenvalues of smallest
 * magnitude (and their eigenfunctions) of the linear second order operator
 * <code>op = p(x)*u''(x) + q(x)*u'(x) + r(x)*u(x)</code> on the interval <code>[a, b]</code>.
 *
 * <p>
 * The operator may be given inactive (as <code>DiffusionPDETerm</code> gives it) and together
 * with boundary conditions <code>{op, DirichletCondition(u(x) == 0, pred)}</code>; an end without
 * a condition has the natural condition <code>u'(x) == 0</code>. The domain is
 * <code>{x, a, b}</code> or <code>Element({x}, Line({{a}, {b}}))</code>.
 *
 * <p>
 * The operator is discretized by Chebyshev collocation; the eigenfunctions are normalized to unit
 * <code>L^2</code> norm with a positive value next to the right end, and returned as interpolating
 * functions of <code>x</code> (or as functions if <code>u</code> is given without its argument).
 */
public class NDEigensystem extends AbstractFunctionEvaluator {

  /** The number of uniform points the interpolating functions of the eigenfunctions use. */
  private static final int INTERPOLATION_POINTS = 401;

  /** At least four arguments, options after them. */
  private static final int[] ARGS_4_INFINITY_FOR_OPTIONS = {4, Integer.MAX_VALUE};

  private final boolean eigensystem;

  /** @param eigensystem <code>true</code> for <code>NDEigensystem</code> */
  public NDEigensystem(boolean eigensystem) {
    this.eigensystem = eigensystem;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 4) {
      return F.NIL;
    }
    int n = ast.arg4().toIntDefault();
    if (n <= 0) {
      return F.NIL;
    }
    IExpr function = ast.arg2();
    IExpr u = function.isAST1() ? function.head() : function;
    if (!u.isSymbol()) {
      return F.NIL;
    }
    double[] interval = new double[2];
    IExpr x = domain(ast.arg3(), interval);
    if (x.isNIL() || function.isAST1() && !function.first().equals(x)) {
      return F.NIL;
    }
    double a = interval[0];
    double b = interval[1];
    if (!(a < b) || Double.isInfinite(a) || Double.isInfinite(b)) {
      return F.NIL;
    }
    IExpr operator = ast.arg1();
    // the ends at x == a and x == b with a Dirichlet condition
    boolean[] dirichlet = new boolean[2];
    if (operator.isList()) {
      IAST list = (IAST) operator;
      if (list.isEmpty()) {
        return F.NIL;
      }
      operator = list.arg1();
      for (int i = 2; i < list.size(); i++) {
        if (!dirichletEnds(list.get(i), x, a, b, dirichlet, engine)) {
          return F.NIL;
        }
      }
    }
    try {
      IExpr[] coefficients = coefficients(operator, (ISymbol) u, x, engine);
      if (coefficients == null) {
        return F.NIL;
      }
      return solve(coefficients, (ISymbol) x, a, b, dirichlet, n, function.isAST1(), engine);
    } catch (MathRuntimeException mrex) {
      Errors.rethrowsInterruptException(mrex);
      return Errors.printMessage(ast.topHead(), mrex, engine);
    }
  }

  /**
   * The variable of <code>{x, a, b}</code> or <code>Element({x}, Line({{a}, {b}}))</code>, with
   * <code>a, b</code> stored in <code>interval</code>.
   */
  private static IExpr domain(IExpr domain, double[] interval) {
    if (domain.isList3() && domain.first().isSymbol()) {
      interval[0] = domain.second().evalfNaN();
      interval[1] = domain.get(3).evalfNaN();
      return domain.first();
    }
    if (domain.isAST(S.Element, 3)) {
      IExpr variables = domain.first();
      IExpr x = variables.isList1() ? variables.first() : variables;
      IExpr region = domain.second();
      if (x.isSymbol() && region.isAST(S.Line, 2) && region.first().isList2()) {
        IExpr p = region.first().first();
        IExpr q = region.first().second();
        if (p.isList1() && q.isList1()) {
          double pDouble = p.first().evalfNaN();
          double qDouble = q.first().evalfNaN();
          interval[0] = Math.min(pDouble, qDouble);
          interval[1] = Math.max(pDouble, qDouble);
          return x;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Mark the ends of <code>[a, b]</code> which the condition
   * <code>DirichletCondition(u(x) == 0, pred)</code> applies to.
   */
  private static boolean dirichletEnds(IExpr condition, IExpr x, double a, double b,
      boolean[] dirichlet, EvalEngine engine) {
    if (!condition.isAST(S.DirichletCondition, 3) || !condition.first().isEqual()) {
      return false;
    }
    IExpr value = condition.first().second();
    if (!value.isZero()) {
      // an eigenvalue problem has homogeneous conditions only
      return false;
    }
    IExpr predicate = condition.second();
    double[] ends = {a, b};
    for (int i = 0; i < 2; i++) {
      IExpr holds = engine.evalN(F.subst(predicate, x, F.num(ends[i])));
      if (holds.isTrue()) {
        dirichlet[i] = true;
      } else if (!holds.isFalse()) {
        return false;
      }
    }
    return true;
  }

  /**
   * The coefficients <code>{r, q, p}</code> of <code>u(x), u'(x), u''(x)</code> in the operator,
   * as expressions in <code>x</code>.
   *
   * @return <code>null</code> if the operator is not linear, homogeneous and of order 2
   */
  private static IExpr[] coefficients(IExpr operator, ISymbol u, IExpr x, EvalEngine engine) {
    IExpr active = engine.evaluate(F.unaryAST1(S.Activate, operator));
    ISymbol[] derivatives = {F.Dummy("u0"), F.Dummy("u1"), F.Dummy("u2")};
    IExpr substituted = active.replaceAll(e -> {
      if (e.isAST1() && e.first().equals(x)) {
        IExpr head = e.head();
        if (head.equals(u)) {
          return derivatives[0];
        }
        if (head.isAST1() && head.first().equals(u) && head.head().isAST(S.Derivative, 2)) {
          int order = head.head().first().toIntDefault();
          if (order == 1 || order == 2) {
            return derivatives[order];
          }
        }
      }
      return F.NIL;
    }).orElse(active);
    substituted = engine.evaluate(substituted);
    if (!substituted.isFree(u)) {
      return null;
    }
    IExpr[] symbolic = new IExpr[3];
    for (int k = 0; k < 3; k++) {
      symbolic[k] = engine.evaluate(F.D(substituted, derivatives[k]));
      for (ISymbol d : derivatives) {
        if (!symbolic[k].isFree(d)) {
          return null;
        }
      }
    }
    IExpr rest = engine.evaluate(F.subst(substituted,
        F.list(F.Rule(derivatives[0], F.C0), F.Rule(derivatives[1], F.C0),
            F.Rule(derivatives[2], F.C0))));
    if (!rest.isPossibleZero(true)) {
      return null;
    }
    if (symbolic[2].isPossibleZero(true)) {
      // not a second order operator
      return null;
    }
    return symbolic;
  }

  /**
   * The <code>n</code> eigenvalues of smallest magnitude, and for <code>NDEigensystem</code> their
   * eigenfunctions, of the operator with the coefficients <code>symbolic</code>.
   *
   * @param applied <code>true</code> for eigenfunctions as expressions in <code>x</code>
   */
  private IExpr solve(IExpr[] symbolic, ISymbol x, double a, double b, boolean[] dirichlet,
      int n, boolean applied, EvalEngine engine) {
    int order = Math.min(400, Math.max(64, 3 * n + 40));
    double[] nodes = new double[order + 1];
    double[][] d1 = chebyshevDifferentiation(order, a, b, nodes);
    double[][] d2 = multiply(d1, d1);
    int size = order + 1;
    double[][] p = new double[3][size];
    for (int k = 0; k < 3; k++) {
      for (int j = 0; j < size; j++) {
        double value = engine.evalN(F.subst(symbolic[k], x, F.num(nodes[j]))).evalfNaN();
        if (!Double.isFinite(value)) {
          return F.NIL;
        }
        p[k][j] = value;
      }
    }
    // the full operator on all nodes
    double[][] full = new double[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        full[i][j] = p[2][i] * d2[i][j] + p[1][i] * d1[i][j] + (i == j ? p[0][i] : 0.0);
      }
    }
    // the nodes run from b (index 0) down to a (index order); the interior nodes are unknowns,
    // the end values follow from the conditions: 0 for Dirichlet, u' == 0 otherwise
    int interior = order - 1;
    double[][] extension = endValues(d1, order, new boolean[] {dirichlet[1], dirichlet[0]});
    if (extension == null) {
      return F.NIL;
    }
    // reduced(i, j) = Sum(full(i + 1, l) * extension(l, j), l) for interior rows
    double[][] reduced = new double[interior][interior];
    for (int i = 0; i < interior; i++) {
      for (int j = 0; j < interior; j++) {
        double s = 0.0;
        for (int l = 0; l < size; l++) {
          s += full[i + 1][l] * extension[l][j];
        }
        reduced[i][j] = s;
      }
    }
    EigenDecompositionNonSymmetric decomposition =
        new EigenDecompositionNonSymmetric(new Array2DRowRealMatrix(reduced, false));
    Complex[] eigenvalues = decomposition.getEigenvalues();
    List<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < eigenvalues.length; i++) {
      Complex lambda = eigenvalues[i];
      if (Math.abs(lambda.getImaginary()) <= 1e-8 * Math.max(1.0, lambda.norm())) {
        indices.add(i);
      }
    }
    indices.sort((i, j) -> Double.compare(Math.abs(eigenvalues[i].getReal()),
        Math.abs(eigenvalues[j].getReal())));
    if (indices.size() < n) {
      return F.NIL;
    }
    double[] values = new double[n];
    IASTAppendable valueList = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      values[i] = eigenvalues[indices.get(i)].getReal();
      valueList.append(F.num(values[i]));
    }
    if (!eigensystem) {
      return valueList;
    }
    double[] weights = clenshawCurtisWeights(order, a, b);
    IASTAppendable functions = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      double[] vector = eigenvector(reduced, decomposition, indices.get(i), values[i]);
      // the values on all nodes
      double[] full2 = new double[size];
      for (int l = 0; l < size; l++) {
        double s = 0.0;
        for (int j = 0; j < interior; j++) {
          s += extension[l][j] * vector[j];
        }
        full2[l] = s;
      }
      double norm = 0.0;
      for (int l = 0; l < size; l++) {
        norm += weights[l] * full2[l] * full2[l];
      }
      norm = Math.sqrt(norm);
      // a positive value next to the right end
      double sign = 1.0;
      for (int l = 0; l < size; l++) {
        if (Math.abs(full2[l]) > 1e-8 * norm) {
          sign = full2[l] < 0.0 ? -1.0 : 1.0;
          break;
        }
      }
      for (int l = 0; l < size; l++) {
        full2[l] *= sign / norm;
      }
      IExpr interpolation = interpolation(nodes, full2, a, b, engine);
      functions.append(applied ? F.unaryAST1(interpolation, x) : interpolation);
    }
    return F.list(valueList, functions);
  }

  /**
   * The eigenvector of <code>lambda</code>: the one of the decomposition, refined by one step of
   * inverse iteration.
   */
  private static double[] eigenvector(double[][] matrix, EigenDecompositionNonSymmetric decomposition,
      int index, double lambda) {
    FieldVector<Complex> v = decomposition.getEigenvector(index);
    int m = matrix.length;
    double[] vector = new double[m];
    for (int i = 0; i < m; i++) {
      vector[i] = v.getEntry(i).getReal();
    }
    double shift = lambda + 1e-10 * Math.max(1.0, Math.abs(lambda));
    RealMatrix shifted = new Array2DRowRealMatrix(matrix);
    for (int i = 0; i < m; i++) {
      shifted.addToEntry(i, i, -shift);
    }
    try {
      double[] refined = new LUDecomposition(shifted, 0.0).getSolver()
          .solve(new org.hipparchus.linear.ArrayRealVector(vector, false)).toArray();
      double max = 0.0;
      for (double r : refined) {
        max = Math.max(max, Math.abs(r));
      }
      if (Double.isFinite(max) && max > 0.0) {
        return refined;
      }
    } catch (MathRuntimeException mrex) {
      // keep the vector of the decomposition
    }
    return vector;
  }

  /**
   * The values on all <code>order + 1</code> nodes as a linear map of the interior values: the
   * identity inside, <code>0</code> at a Dirichlet end and the solution of <code>u' == 0</code>
   * at a natural end.
   *
   * @param dirichlet the conditions at the nodes <code>0</code> and <code>order</code>
   */
  private static double[][] endValues(double[][] d1, int order, boolean[] dirichlet) {
    int size = order + 1;
    int interior = order - 1;
    double[][] extension = new double[size][interior];
    for (int j = 0; j < interior; j++) {
      extension[j + 1][j] = 1.0;
    }
    int[] ends = {0, order};
    if (!dirichlet[0] && !dirichlet[1]) {
      // d1(e, e0)*u(e0) + d1(e, e1)*u(e1) == -Sum(d1(e, j)*u(j), interior j)
      double m00 = d1[0][0];
      double m01 = d1[0][order];
      double m10 = d1[order][0];
      double m11 = d1[order][order];
      double det = m00 * m11 - m01 * m10;
      if (det == 0.0) {
        return null;
      }
      for (int j = 0; j < interior; j++) {
        double r0 = -d1[0][j + 1];
        double r1 = -d1[order][j + 1];
        extension[0][j] = (r0 * m11 - m01 * r1) / det;
        extension[order][j] = (m00 * r1 - m10 * r0) / det;
      }
    } else {
      for (int e = 0; e < 2; e++) {
        if (!dirichlet[e]) {
          int end = ends[e];
          if (d1[end][end] == 0.0) {
            return null;
          }
          for (int j = 0; j < interior; j++) {
            extension[end][j] = -d1[end][j + 1] / d1[end][end];
          }
        }
      }
    }
    return extension;
  }

  /**
   * The Chebyshev differentiation matrix on the nodes <code>(a+b)/2 + (b-a)/2*Cos(Pi*j/order)</code>
   * which are stored in <code>nodes</code>.
   */
  private static double[][] chebyshevDifferentiation(int order, double a, double b,
      double[] nodes) {
    int size = order + 1;
    double[] t = new double[size];
    double[] c = new double[size];
    for (int j = 0; j < size; j++) {
      // Sin form of Cos(Pi*j/order) for symmetric rounding
      t[j] = Math.sin(Math.PI * (order - 2.0 * j) / (2.0 * order));
      nodes[j] = 0.5 * (a + b) + 0.5 * (b - a) * t[j];
      c[j] = ((j == 0 || j == order) ? 2.0 : 1.0) * ((j % 2 == 0) ? 1.0 : -1.0);
    }
    double scale = 2.0 / (b - a);
    double[][] d = new double[size][size];
    for (int i = 0; i < size; i++) {
      double rowSum = 0.0;
      for (int j = 0; j < size; j++) {
        if (i != j) {
          d[i][j] = c[i] / c[j] / (t[i] - t[j]) * scale;
          rowSum += d[i][j];
        }
      }
      // negative sum trick: the derivative of a constant is 0
      d[i][i] = -rowSum;
    }
    return d;
  }

  /** Clenshaw-Curtis quadrature weights on the Chebyshev nodes of <code>[a, b]</code>. */
  private static double[] clenshawCurtisWeights(int order, double a, double b) {
    int size = order + 1;
    double[] w = new double[size];
    for (int j = 0; j < size; j++) {
      double theta = Math.PI * j / order;
      double s = 0.0;
      for (int k = 1; k <= order / 2; k++) {
        double bk = (k == order / 2 && order % 2 == 0) ? 1.0 : 2.0;
        s += bk / (4.0 * k * k - 1.0) * Math.cos(2.0 * k * theta);
      }
      double cj = (j == 0 || j == order) ? 1.0 : 2.0;
      w[j] = cj / order * (1.0 - s) * 0.5 * (b - a);
    }
    return w;
  }

  /**
   * The interpolating function of the Chebyshev interpolant with the given node values, sampled on
   * a uniform grid.
   */
  private static IExpr interpolation(double[] nodes, double[] values, double a, double b,
      EvalEngine engine) {
    int order = nodes.length - 1;
    IASTAppendable points = F.ListAlloc(INTERPOLATION_POINTS);
    for (int i = 0; i < INTERPOLATION_POINTS; i++) {
      double xi = (i == INTERPOLATION_POINTS - 1) ? b
          : a + (b - a) * i / (INTERPOLATION_POINTS - 1);
      points.append(F.list(F.num(xi), F.num(barycentric(nodes, values, order, xi))));
    }
    IExpr function = engine.evaluate(F.Interpolation(points));
    if (function instanceof InterpolatingFunctionExpr) {
      // f(x) stays as it is, like the solution of a differential equation
      ((InterpolatingFunctionExpr<?>) function).withNumericArgumentsOnly();
    }
    return function;
  }

  /** The barycentric formula of the interpolant on the Chebyshev nodes. */
  private static double barycentric(double[] nodes, double[] values, int order, double x) {
    double numerator = 0.0;
    double denominator = 0.0;
    for (int j = 0; j <= order; j++) {
      double difference = x - nodes[j];
      if (difference == 0.0) {
        return values[j];
      }
      double w = ((j == 0 || j == order) ? 0.5 : 1.0) * ((j % 2 == 0) ? 1.0 : -1.0) / difference;
      numerator += w * values[j];
      denominator += w;
    }
    return numerator / denominator;
  }

  private static double[][] multiply(double[][] a, double[][] b) {
    int m = a.length;
    double[][] c = new double[m][m];
    for (int i = 0; i < m; i++) {
      for (int l = 0; l < m; l++) {
        double ail = a[i][l];
        if (ail != 0.0) {
          for (int j = 0; j < m; j++) {
            c[i][j] += ail * b[l][j];
          }
        }
      }
    }
    return c;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_4_INFINITY_FOR_OPTIONS;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
