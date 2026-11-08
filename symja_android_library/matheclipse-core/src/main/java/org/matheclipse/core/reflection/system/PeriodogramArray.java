package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * <p>
 * Implement the PeriodogramArray function.
 * </p>
 * 
 * <pre>
 * PeriodogramArray(list)
 * PeriodogramArray(list, n)
 * PeriodogramArray(list, n, d)
 * PeriodogramArray(list, n, d, wfun)
 * PeriodogramArray(list, n, d, wfun, m)
 * </pre>
 * <p>
 * Returns the squared magnitude of the discrete Fourier transform (power spectrum) of partitions of
 * list. This is a numeric function; inputs are converted to machine-precision numbers.
 * </p>
 */
public class PeriodogramArray extends AbstractFunctionEvaluator {

  public PeriodogramArray() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    // PeriodogramArray[list, n, d, wfun, m]
    // Arg 1: list
    // Arg 2: n (partition length), default: Length(list)
    // Arg 3: d (offset), default: n (non-overlapping)
    // Arg 4: wfun (window function), default: DirichletWindow (effectively 1)
    // Arg 5: m (padding length), default: n

    // FourierParameters -> {a, b} scales the spectrum by n^a; the default is {0, 1}
    IExpr[] fourierParameters = {F.C0, F.C1};
    int positional = ast.argSize();
    while (positional > 1 && ast.get(positional).isRuleAST()) {
      IExpr option = ast.get(positional--);
      if (!option.first().equals(S.FourierParameters) || !option.second().isList2()) {
        return F.NIL;
      }
      fourierParameters[0] = option.second().first();
      fourierParameters[1] = option.second().second();
    }
    double a = fourierParameters[0].evalfNaN();
    if (!Double.isFinite(a) || !Double.isFinite(fourierParameters[1].evalfNaN())) {
      return F.NIL;
    }
    final IAST positionalArgs = ast.copyUntil(positional + 1);

    // Force input to numeric precision (N)
    IExpr listExpr = engine.evalN(ast.arg1());
    if (!listExpr.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) listExpr;

    int[] dimensions = list.isMatrix();
    if (positional == 1 && dimensions != null) {
      // the periodogram of the whole array
      double total = (double) dimensions[0] * dimensions[1];
      IExpr fourier = engine.evaluate(
          SpectrogramArray.windowedFourier(list, F.NIL, 1, 1, fourierParameters[1]));
      if (!fourier.isList()) {
        return F.NIL;
      }
      double scale = Math.pow(total, a) / total;
      return ((IAST) fourier).map(row -> spectrum(row, scale));
    }

    int listLength = list.argSize();
    int n = SpectrogramArray.intArg(positionalArgs, 2);
    if (n <= 0) {
      // Default n: The entire list length
      n = listLength;
    }
    int d = SpectrogramArray.intArg(positionalArgs, 3);
    if (d <= 0) {
      // Default d: n (non-overlapping partitions) for PeriodogramArray
      d = n;
    }
    IExpr wfun = positional >= 4 ? positionalArgs.arg4() : F.DirichletWindow;
    int m = SpectrogramArray.intArg(positionalArgs, 5);
    if (m <= 0) {
      m = n;
    }

    // Generate Partitions
    IAST partitions = F.Partition(list, F.ZZ(n), F.ZZ(d));
    IExpr evalPartitions = engine.evaluate(partitions);

    if (!evalPartitions.isList()) {
      return F.NIL;
    }
    IAST partitionList = (IAST) evalPartitions;
    int partitionCount = partitionList.argSize();

    if (partitionCount == 0) {
      return F.NIL;
    }

    // Prepare Window Weights (Numerically) and Calculate Squared Sum
    IExpr windowWeights = SpectrogramArray.windowWeights(wfun, n, engine);
    double squaredWindowSum = n; // Default for rectangular window (sum of 1^2 * n)

    // Calculate Sum of Squared Weights for Normalization
    if (windowWeights.isList()) {
      squaredWindowSum = 0.0;
      for (IExpr w : (IAST) windowWeights) {
        // Assuming real weights for standard windows, but Abs^2 is safer general case
        double val = w.evalfNaN();
        squaredWindowSum += val * val;
      }
    }

    // Formula: (n^a / Count) * (1 / Sum(w_i^2)) * Sum(|DFT|^2)
    double scale = Math.pow(n, a) / (partitionCount * squaredWindowSum);
    double[] power = new double[m];
    for (IExpr part : partitionList) {
      IExpr fourierResult = engine.evaluate(
          SpectrogramArray.windowedFourier(part, windowWeights, n, m, fourierParameters[1]));
      if (!fourierResult.isList() || fourierResult.argSize() != m) {
        return F.NIL;
      }
      for (int i = 0; i < m; i++) {
        double squared = squaredMagnitude(fourierResult.getAt(i + 1));
        if (Double.isNaN(squared)) {
          return F.NIL;
        }
        power[i] += squared;
      }
    }
    return F.mapRange(0, m, i -> F.num(power[i] * scale));
  }

  /** The list of the squared magnitudes of <code>row</code> multiplied by <code>scale</code>. */
  private static IExpr spectrum(IExpr row, double scale) {
    return ((IAST) row).map(x -> F.num(squaredMagnitude(x) * scale));
  }

  /** The squared magnitude of a number, free of the rounding of the root in its absolute value. */
  private static double squaredMagnitude(IExpr z) {
    if (z.isNumber()) {
      double re = ((INumber) z).reDoubleValue();
      double im = ((INumber) z).imDoubleValue();
      return re * re + im * im;
    }
    double x = z.evalfNaN();
    return x * x;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_5;
  }
}
