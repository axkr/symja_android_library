package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.linalg.SpectralDeconvolution;

/**
 * <code>ListDeconvolve(kernel, list)</code> and <code>ListDeconvolve(kernel, list, Method -&gt;
 * m)</code> - the regularized deconvolution of a vector or a matrix of real numbers by a kernel of
 * the same rank, see {@link SpectralDeconvolution}.
 *
 * <p>
 * The methods are <code>"DampedLS"</code> (the default), <code>"Tikhonov"</code>,
 * <code>"Wiener"</code> and <code>"TSVD"</code>; <code>{method, lambda}</code> gives the
 * regularization parameter (the noise to signal ratio of <code>"Wiener"</code>, the truncation of
 * <code>"TSVD"</code>), which is otherwise chosen by generalized cross validation (the ratio
 * <code>0.01</code> for <code>"Wiener"</code>). Other methods leave the call unevaluated.
 */
public class ListDeconvolve extends AbstractFunctionEvaluator {

  /** The method of a deconvolution and its regularization parameter (NaN: automatic). */
  public static final class Spec {
    public final SpectralDeconvolution.Method method;
    public final double lambda;

    Spec(SpectralDeconvolution.Method method, double lambda) {
      this.method = method;
      this.lambda = lambda;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
    if (options.isInvalidPosition(2)) {
      return options.printNonopt(ast, 2, engine);
    }
    final IExpr kernelExpr = ast.arg1();
    final IExpr listExpr = ast.arg2();
    if (rank(kernelExpr) > 2 || rank(listExpr) > 2) {
      // arrays of higher rank are not supported
      return F.NIL;
    }
    double[][] kernel = realArray(kernelExpr);
    if (kernel == null) {
      // Expecting a nonempty real numeric array instead of `1`.
      return Errors.printMessage(S.ListDeconvolve, "arginv", F.list(kernelExpr), engine);
    }
    double[][] list = realArray(listExpr);
    if (list == null) {
      return Errors.printMessage(S.ListDeconvolve, "arginv", F.list(listExpr), engine);
    }
    final int rank = rank(kernelExpr);
    if (rank != rank(listExpr)) {
      // The kernel `1` and list `2` are not both nonempty numerical arrays with the same tensor
      // rank.
      return Errors.printMessage(S.ListDeconvolve, "dims", F.list(kernelExpr, listExpr), engine);
    }
    if (kernel.length > list.length || kernel[0].length > list[0].length) {
      return Errors.printMessage(S.ListDeconvolve, "kldims", F.list(kernelExpr, listExpr), engine);
    }
    Spec spec = methodSpec(options.getOption(S.Method), S.ListDeconvolve, engine);
    if (spec == null) {
      return F.NIL;
    }
    double[][] result =
        SpectralDeconvolution.deconvolve(kernel, list, spec.method, spec.lambda, rank == 1);
    if (rank == 1) {
      return F.List(result[0]);
    }
    IASTAppendable rows = F.ListAlloc(result.length);
    for (double[] row : result) {
      rows.append(F.List(row));
    }
    return rows;
  }

  /**
   * The method of the option value <code>Method -&gt; value</code>.
   *
   * @param value the value of the option, {@link F#NIL} if there is none
   * @param head the function whose message an invalid regularization parameter prints
   * @return <code>null</code> if the method is not supported or its parameter is invalid
   */
  public static Spec methodSpec(IExpr value, ISymbol head, EvalEngine engine) {
    if (value.isNIL() || value == S.Automatic) {
      return new Spec(SpectralDeconvolution.Method.DAMPED_LS, Double.NaN);
    }
    IExpr name = value;
    IExpr parameter = S.Automatic;
    if (value.isList2() && value.first().isString()) {
      name = value.first();
      parameter = value.second();
    } else if (!value.isString()) {
      return null;
    }
    SpectralDeconvolution.Method method;
    switch (name.toString()) {
      case "DampedLS":
        method = SpectralDeconvolution.Method.DAMPED_LS;
        break;
      case "Tikhonov":
        method = SpectralDeconvolution.Method.TIKHONOV;
        break;
      case "Wiener":
        method = SpectralDeconvolution.Method.WIENER;
        break;
      case "TSVD":
        method = SpectralDeconvolution.Method.TSVD;
        break;
      default:
        return null;
    }
    double lambda = Double.NaN;
    if (parameter != S.Automatic) {
      lambda = parameter.isReal() ? parameter.evalfNaN() : Double.NaN;
      if (Double.isNaN(lambda) || lambda < 0.0 || Double.isInfinite(lambda)) {
        // The value of regularization parameter `1` should be a non-negative real number or
        // Automatic.
        Errors.printMessage(head, "param", F.list(parameter), engine);
        return null;
      }
    }
    return new Spec(method, lambda);
  }

  /** The tensor rank of a nonempty rectangular array, or <code>0</code>. */
  private static int rank(IExpr expr) {
    int rank = 0;
    while (expr.isList() && expr.argSize() > 0) {
      rank++;
      expr = expr.first();
    }
    return rank;
  }

  /**
   * A vector (as one row) or a matrix of real numbers.
   *
   * @return <code>null</code> if <code>expr</code> is no nonempty vector or rectangular matrix of
   *         real numbers
   */
  public static double[][] realArray(IExpr expr) {
    if (!expr.isList() || expr.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) expr;
    if (!list.arg1().isList()) {
      double[] row = realVector(list);
      return row == null ? null : new double[][] {row};
    }
    double[][] result = new double[list.argSize()][];
    for (int i = 1; i < list.size(); i++) {
      IExpr row = list.get(i);
      if (!row.isList()) {
        return null;
      }
      result[i - 1] = realVector((IAST) row);
      if (result[i - 1] == null || result[i - 1].length != result[0].length) {
        return null;
      }
    }
    return result;
  }

  private static double[] realVector(IAST list) {
    if (list.argSize() == 0) {
      return null;
    }
    double[] result = new double[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      IExpr element = list.get(i);
      if (element.isList()) {
        return null;
      }
      double value = element.isReal() || element.isNumericFunction(true) //
          ? element.evalfNaN()
          : Double.NaN;
      if (!Double.isFinite(value)) {
        return null;
      }
      result[i - 1] = value;
    }
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, F.list(F.Rule(S.Method, S.Automatic)));
    newSymbol.putMessage(IPatternMatcher.SET, "arginv",
        F.stringx("Expecting a nonempty real numeric array instead of `1`."));
    newSymbol.putMessage(IPatternMatcher.SET, "dims", F.stringx(
        "The kernel `1` and list `2` are not both nonempty numerical arrays with the same tensor rank."));
    newSymbol.putMessage(IPatternMatcher.SET, "kldims",
        F.stringx("The dimensions of a kernel cannot be larger than the dimensions of a list."));
    newSymbol.putMessage(IPatternMatcher.SET, "param", F.stringx(
        "The value of regularization parameter `1` should be a non-negative real number or Automatic."));
  }
}
