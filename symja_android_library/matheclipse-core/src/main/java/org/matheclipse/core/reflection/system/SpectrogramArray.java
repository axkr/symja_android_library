package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Implement the SpectrogramArray function.
 * </p>
 * 
 * <pre>
 * SpectrogramArray(list)
 * SpectrogramArray(list, n)
 * SpectrogramArray(list, n, d)
 * SpectrogramArray(list, n, d, wfun)
 * SpectrogramArray(list, n, d, wfun, m)
 * </pre>
 * <p>
 * Returns the discrete Fourier transform (DFT) of partitions of list.
 * </p>
 */
public class SpectrogramArray extends AbstractFunctionEvaluator {

  public SpectrogramArray() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    // SpectrogramArray[list, n, d, wfun, m]
    // Arg 1: list
    // Arg 2: n (partition length), default: Min(Length(list), 256)
    // Arg 3: d (offset), default: 1 (Sliding window of 1 for array generation)
    // Arg 4: wfun (window function), default: DirichletWindow (effectively 1)
    // Arg 5: m (padding length), default: n

    // Force input to numeric precision (N)
    IExpr listExpr = engine.evalN(ast.arg1());
    if (!listExpr.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) listExpr;

    int listLength = list.argSize();
    int n = intArg(ast, 2);
    if (n <= 0) {
      // Default n: commonly 256 or list length if smaller
      n = Math.min(listLength, 256);
    }
    int d = intArg(ast, 3);
    if (d <= 0) {
      // Default d: 1 (Standard SpectrogramArray behavior matches Partition[..., 1])
      // Note: This differs from visual Spectrogram which often defaults to n/2.
      d = 1;
    }
    IExpr wfun = ast.argSize() >= 4 ? ast.arg4() : F.DirichletWindow;
    int m = intArg(ast, 5);
    if (m <= 0) {
      m = n;
    }

    // 6. Generate Partitions
    // We use Partition[list, n, d, {1, 1}, paddingValue]
    // Alignment {1, 1} ensures we start at index 1 and allow overhangs at the end.

    // Determine Padding Value:
    // Observations show SpectrogramArray repeats the last element for padding
    // instead of using 0.
    IExpr paddingValue = F.C0;
    if (list.size() > 1) {
      paddingValue = list.last();
    }

    IAST partitions = F.Partition(list, F.ZZ(n), F.ZZ(d), F.List(F.C1, F.C1), paddingValue);
    IExpr evalPartitions = engine.evaluate(partitions);

    if (!evalPartitions.isList()) {
      return F.NIL;
    }
    IAST partitionList = (IAST) evalPartitions;

    IExpr windowWeights = windowWeights(wfun, n, engine);

    // Process partitions: Apply Window -> Pad -> Fourier
    IASTAppendable result = F.ListAlloc(partitionList.argSize());

    // FourierParameters -> {1, -1} (Signal Processing convention)
    IExpr fourierParamsValue = F.List(F.C1, F.CN1);
    IExpr fourierOptionRule = F.Rule(S.FourierParameters, fourierParamsValue);

    for (IExpr part : partitionList) {
      IExpr currentPartition = part;

      // Apply Window if necessary
      if (windowWeights.isPresent()) {
        currentPartition = F.Times(currentPartition, windowWeights);
      }

      // Apply Padding if m > n (PadRight with 0)
      // Note: The previous padding was for Partitioning logic (filling the window).
      // This padding is for FFT size (m), which is usually zero-padded.
      if (m > n) {
        currentPartition = F.PadRight(currentPartition, F.ZZ(m), F.C0);
      }

      // Compute Fourier Transform
      // Pass the Option Rule
      IAST fourierCall = F.Fourier(currentPartition, fourierOptionRule);
      result.append(fourierCall);
    }

    // Evaluate the final list of Fourier transforms
    return engine.evaluate(result);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_5;
  }

  /**
   * The machine integer at position {@code position} of {@code ast}, or a number {@code <= 0} if
   * there is none, so that the caller uses its default.
   */
  static int intArg(IAST ast, int position) {
    if (ast.argSize() >= position) {
      IExpr arg = ast.get(position);
      if (arg.isInteger()) {
        return arg.toIntDefault();
      }
    }
    return -1;
  }

  /**
   * The numeric weights of the window function {@code wfun} for partitions of length {@code n}:
   * {@code wfun} itself if it is a list, otherwise {@code wfun} sampled from {@code -1/2} to
   * {@code 1/2}. {@code F.NIL} for the rectangular window.
   */
  static IExpr windowWeights(IExpr wfun, int n, EvalEngine engine) {
    if (wfun == S.DirichletWindow || wfun.equals(F.C1)) {
      return F.NIL;
    }
    if (wfun.isList()) {
      return engine.evalN(wfun);
    }
    IExpr range = F.Subdivide(F.CN1D2, F.C1D2, F.ZZ(n - 1));
    return engine.evaluate(F.N(F.Map(wfun, range)));
  }
}
