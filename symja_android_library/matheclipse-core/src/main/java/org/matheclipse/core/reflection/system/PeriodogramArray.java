package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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
    int n = -1;
    int d = -1;
    int m = -1;
    IExpr wfun = F.DirichletWindow;

    // Determine n (Partition Length)
    if (ast.argSize() >= 2) {
      IExpr arg2 = ast.arg2();
      if (arg2.isInteger()) {
        n = arg2.toIntDefault();
      }
    }
    if (n <= 0) {
      // Default n: The entire list length
      n = listLength;
    }

    // Determine d (Offset)
    if (ast.argSize() >= 3) {
      IExpr arg3 = ast.arg3();
      if (arg3.isInteger()) {
        d = arg3.toIntDefault();
      }
    }
    if (d <= 0) {
      // Default d: n (non-overlapping partitions) for PeriodogramArray
      d = n;
    }

    // Determine wfun (Window Function)
    if (ast.argSize() >= 4) {
      wfun = ast.arg4();
    }

    // Determine m (Padding Size)
    if (ast.argSize() >= 5) {
      IExpr arg5 = ast.arg5();
      if (arg5.isInteger()) {
        m = arg5.toIntDefault();
      }
    }
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
    IExpr windowWeights = F.NIL;
    double squaredWindowSum = n; // Default for rectangular window (sum of 1^2 * n)

    if (!wfun.equals(F.DirichletWindow) && !wfun.equals(F.C1)) {
      if (wfun.isList()) {
        windowWeights = engine.evalN(wfun);
      } else {
        // Sample the window from -0.5 to 0.5
        IExpr range = F.Subdivide(F.CN1D2, F.C1D2, F.ZZ(n - 1));
        windowWeights = engine.evaluate(F.N(F.Map(wfun, range)));
      }

      // Calculate Sum of Squared Weights for Normalization
      if (windowWeights.isList()) {
        squaredWindowSum = 0.0;
        for (IExpr w : (IAST) windowWeights) {
          // Assuming real weights for standard windows, but Abs^2 is safer general case
          double val = w.evalf();
          squaredWindowSum += val * val;
        }
      }
    }

    // Process partitions
    // FourierParameters -> {1, -1} (Signal Processing convention)
    IExpr fourierParamsValue = F.List(F.C1, F.CN1);
    IExpr fourierOptionRule = F.Rule(S.FourierParameters, fourierParamsValue);

    IExpr sumOfSpectra = F.NIL;

    for (IExpr part : partitionList) {
      IExpr currentPartition = part;

      // Apply Window
      if (windowWeights.isPresent()) {
        currentPartition = F.Times(currentPartition, windowWeights);
      }

      // Apply Padding (to FFT length m)
      if (m > n) {
        currentPartition = F.PadRight(currentPartition, F.ZZ(m), F.C0);
      }

      // Compute Fourier Transform (Numeric)
      IAST fourierCall = F.Fourier(currentPartition, fourierOptionRule);
      IExpr fourierResult = engine.evaluate(fourierCall);

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