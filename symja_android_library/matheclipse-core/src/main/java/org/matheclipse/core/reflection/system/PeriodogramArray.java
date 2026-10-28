package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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

    // Force input to numeric precision (N)
    IExpr listExpr = engine.evalN(ast.arg1());
    if (!listExpr.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) listExpr;

    int listLength = list.argSize();
    int n = SpectrogramArray.intArg(ast, 2);
    if (n <= 0) {
      // Default n: The entire list length
      n = listLength;
    }
    int d = SpectrogramArray.intArg(ast, 3);
    if (d <= 0) {
      // Default d: n (non-overlapping partitions) for PeriodogramArray
      d = n;
    }
    IExpr wfun = ast.argSize() >= 4 ? ast.arg4() : F.DirichletWindow;
    int m = SpectrogramArray.intArg(ast, 5);
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

    // Process partitions
    IExpr sumOfSpectra = F.NIL;

    for (IExpr part : partitionList) {
      // Compute Fourier Transform (Numeric)
      IExpr fourierResult =
          engine.evaluate(SpectrogramArray.windowedFourier(part, windowWeights, n, m));

      // Compute Squared Magnitude: Abs[fft]^2
      IExpr powerSpectrum = F.Map(F.Function(F.Sqr(F.Abs(F.Slot1))), fourierResult);
      powerSpectrum = engine.evaluate(powerSpectrum);

      // Accumulate
      if (sumOfSpectra.isPresent()) {
        sumOfSpectra = F.Plus(sumOfSpectra, powerSpectrum);
      } else {
        sumOfSpectra = powerSpectrum;
      }
    }

    // Evaluate sum
    if (sumOfSpectra.isPresent()) {
      sumOfSpectra = engine.evaluate(sumOfSpectra);
    } else {
      return F.NIL;
    }

    // Normalize and Average
    // Formula: (1 / Count) * (1 / Sum(w_i^2)) * Sum(|DFT|^2)
    double scalingFactor = 1.0 / (partitionCount * squaredWindowSum);

    IExpr result = F.Times(F.num(scalingFactor), sumOfSpectra);

    return engine.evaluate(result);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_5;
  }
}
