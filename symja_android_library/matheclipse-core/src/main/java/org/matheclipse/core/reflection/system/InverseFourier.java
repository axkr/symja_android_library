package org.matheclipse.core.reflection.system;

import org.hipparchus.complex.Complex;
import org.hipparchus.transform.DftNormalization;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Implement the InverseFourier function.
 * </p>
 * *
 * 
 * <pre>
 * InverseFourier(list)
 * InverseFourier(list, {p1, p2, ...})
 * </pre>
 * <p>
 * Options: FourierParameters -> {a, b} (default {0, 1})
 * </p>
 */
public class InverseFourier extends AbstractFunctionOptionEvaluator {

  public InverseFourier() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    IExpr arg1 = ast.arg1();
    IExpr listExpr = engine.evalN(arg1);

    if (!listExpr.isList()) {
      return F.NIL;
    }

    // Parse FourierParameters Option
    // Default {0, 1}
    double paramA = 0.0;
    double paramB = 1.0;

    final IExpr fourierParameters = options[0];
    if (fourierParameters.isList2()) {
      IAST params = (IAST) fourierParameters;
      paramA = params.arg1().evalf();
      paramB = params.arg2().evalf();
    }

    try {
      // Handle InverseFourier[list, {p1, p2, ...}] (Sparse / Selective Extraction)
      if (ast.argSize() >= 2 && ast.arg2().isList()) {
        IAST positions = (IAST) ast.arg2();
        return computeSparseInverseFourier(listExpr, positions, paramA, paramB, originalAST);
      }

      // 3. Handle standard InverseFourier[list]
      return computeInverseFourierRecursive(listExpr, paramA, paramB);

    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /**
   * Recursive helper to handle multi-dimensional Inverse Fourier transforms.
   * <p>
   * Logic: 1. If 1D list of numbers, apply 1D Transform. 2. If N-D list: a. Recursively transform
   * all inner dimensions (elements of the list). b. Transform the current outer dimension.
   */
  private IExpr computeInverseFourierRecursive(IExpr expr, double a, double b) {
    if (!expr.isList()) {
      return expr;
    }
    IAST list = (IAST) expr;

    // Check if it's a list of numbers (1D) or list of lists (ND)
    // Base Case: 1D
    if (list.size() > 1 && list.arg1().isNumber()) {
      return compute1D(list, a, b);
    }

    // N-Dimensional Case
    if (list.size() > 1) {
      // Apply InverseFourier recursively to each sub-tensor (transform inner dimensions)
      IASTAppendable subTransformed = F.ListAlloc(list.argSize());
      for (IExpr row : list) {
        subTransformed.append(computeInverseFourierRecursive(row, a, b));
      }

      return transformOuterDimension(subTransformed, a, b);
    }

    if (list.isEmpty()) {
      return F.CEmptyList;
    }
    return compute1D(list, a, b);
  }

  /**
   * Transforms the outermost dimension of a multidimensional tensor.
   * <p>
   * It uses Transpose to peel off dimensions until it reaches a 1D list, applies the transform, and
   * then reverses the transposes.
   */
  private IExpr transformOuterDimension(IAST list, double a, double b) {
    // If the elements are numbers, we are at the bottom -> Apply 1D Transform
    if (list.size() > 1 && list.arg1().isNumber()) {
      return compute1D(list, a, b);
    }

    // If elements are lists, we need to apply the transform along the list axis.
    // We use Transpose to swap Dim 1 (Outer) with Dim 2.
    // list is [N1, N2, ...]
    // transposed is [N2, N1, ...]
    IExpr transposedExpr = F.eval(F.Transpose(list));

    if (!transposedExpr.isList()) {
      // Fallback for ragged arrays or errors, though Fourier input should be rectangular
      return list;
    }
    IAST transposed = (IAST) transposedExpr;

    IASTAppendable result = F.ListAlloc(transposed.argSize());
    // Now iterate over the new outer dimension (originally Dim 2).
    // Each element is a tensor where the Outer Dim is our original Dim 1.
    for (IExpr row : transposed) {
      if (row.isList()) {
        result.append(transformOuterDimension((IAST) row, a, b));
      } else {
        result.append(row);
      }
    }

    // Transpose back to restore original dimension order
    return F.eval(F.Transpose(result));
  }

  /**
   * Core 1D Inverse Fourier Logic.
   */
  private IExpr compute1D(IAST list, double a, double b) {
    int n = list.argSize();
    Complex[] input = Convert.list2Complex(list);
    if (input == null) {
      return F.NIL;
    }

    Complex[] output;

    // Calculate Target Scaling and Exponent Sign
    // Wolfram InverseFourier: 1 / n^((1+a)/2) * Sum[ v_s * exp( -2pi * i * b * ... ) ]
    double prefactorExp = (1.0 + a) / 2.0;
    double targetScale = 1.0 / Math.pow(n, prefactorExp);

    // The sign of the exponent is determined by -b.
    // If b=1 (default), exponent is negative (-i). Matches FFT Forward convention.
    // If b=-1 (Signal), exponent is positive (+i). Matches FFT Inverse convention.

    // Optimization: Use Hipparchus FFT if n is power of 2 and b is +/- 1
    if (isPowerOfTwo(n) && (b == 1.0 || b == -1.0)) {
      FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

      // Determine Transform Type based on -b
      // Hipparchus Forward: exp(-i...)
      // Hipparchus Inverse: exp(+i...)
      TransformType type;
      double hipparchusScale;

      if (b == 1.0) {
        // We want exp(-i...). Use FORWARD.
        type = TransformType.FORWARD;
        // Hipparchus Standard Forward has scale 1.0.
        hipparchusScale = 1.0;
      } else {
        // b == -1.0. We want exp(+i...). Use INVERSE.
        type = TransformType.INVERSE;
        // Hipparchus Standard Inverse has scale 1/n.
        hipparchusScale = 1.0 / n;
      }

      output = fft.transform(input, type);

      // Adjust Scaling
      double correction = targetScale / hipparchusScale;
      if (Math.abs(correction - 1.0) > 1e-15) {
        for (int i = 0; i < n; i++) {
          output[i] = output[i].multiply(correction);
        }
      }

    } else {
      // Direct DFT implementation for general N and {a,b}
      // Formula: targetScale * Sum[ u_r * exp( -2pi * i * b * r * s / n ) ]
      output = new Complex[n];
      double constant = -2.0 * Math.PI * b / n; // Note negative sign

      for (int s = 0; s < n; s++) {
        double sumRe = 0;
        double sumIm = 0;

        for (int r = 0; r < n; r++) {
          // exp( i * constant * r * s )
          double theta = constant * r * s;
          double wRe = Math.cos(theta);
          double wIm = Math.sin(theta);

          double uRe = input[r].getReal();
          double uIm = input[r].getImaginary();

          sumRe += (uRe * wRe - uIm * wIm);
          sumIm += (uRe * wIm + uIm * wRe);
        }
        output[s] = new Complex(targetScale * sumRe, targetScale * sumIm);
      }
    }

    return Convert.toVector(output);
  }

  private IExpr computeSparseInverseFourier(IExpr listExpr, IAST positions, double a, double b,
      IAST originalAST) {
    IAST list = (IAST) listExpr;

    if (list.size() > 1 && list.arg1().isList()) {
      IExpr fullResult = computeInverseFourierRecursive(listExpr, a, b);
      return F.Extract(fullResult, positions);
    }

    int n = list.argSize();
    Complex[] input = Convert.list2Complex(list);
    if (input == null)
      return F.NIL;

    IASTAppendable result = F.ListAlloc(positions.argSize());

    // InverseFourier Scaling: n^(-(1+a)/2)
    double prefactorExp = (1.0 + a) / 2.0;
    double scale = 1.0 / Math.pow(n, prefactorExp);

    // Positive sign for default b=1
    double basePhase = 2.0 * Math.PI * b / n;

    for (IExpr p : positions) {
      if (!p.isInteger())
        return F.NIL;

      int pInt = p.toIntDefault();
      if (Math.abs(pInt) > list.argSize()) {
        // Position specification `1` in `2` is not applicable.
        return Errors.printMessage(S.InverseFourier, "psl1", F.List(positions, originalAST));
      }

      // Map to 0-based index s
      int s = (pInt - 1) % n;
      if (s < 0)
        s += n;

      double sumRe = 0.0;
      double sumIm = 0.0;

      for (int r = 0; r < n; r++) {
        double theta = basePhase * r * s;
        double wRe = Math.cos(theta);
        double wIm = Math.sin(theta);

        double uRe = input[r].getReal();
        double uIm = input[r].getImaginary();

        sumRe += (uRe * wRe - uIm * wIm);
        sumIm += (uRe * wIm + uIm * wRe);
      }
      result.append(F.complexNum(scale * sumRe, scale * sumIm));
    }

    return result;
  }

  private boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.FourierParameters}, //
        new IExpr[] {F.List(F.C0, F.C1)});
  }
}