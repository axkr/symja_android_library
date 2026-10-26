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
 * Numeric discrete Fourier transform shared by <code>Fourier</code> and
 * <code>InverseFourier</code>.
 *
 * <p>
 * With <code>FourierParameters -> {a, b}</code> and the direction <code>d</code> (see
 * {@link #direction()}) a list <code>u</code> of length <code>n</code> is transformed into
 *
 * <pre>
 * v[s] = n^(-(1 - d*a)/2) * Sum(u[r] * Exp(2*Pi*I*d*b*(r-1)*(s-1)/n), {r, 1, n})
 * </pre>
 *
 * A rectangular array is transformed along every dimension, with the factor of all dimensions
 * applied once. <code>f(list, {p1, p2, ...})</code> of a one-dimensional list returns only the
 * given positions, computed with <code>d = 1</code> in the exponent for both functions.
 */
public abstract class AbstractFourier extends AbstractFunctionOptionEvaluator {

  /**
   * The sign of the exponent: <code>1</code> for <code>Fourier</code>, <code>-1</code> for
   * <code>InverseFourier</code>.
   */
  protected abstract int direction();

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();
    // Force numeric evaluation of the input list
    IExpr listExpr = engine.evalN(arg1);
    if (!listExpr.isList()) {
      return F.NIL;
    }

    try {
      // FourierParameters, default {0, 1}
      double paramA = 0.0;
      double paramB = 1.0;
      final IExpr fourierParameters = options[0];
      if (fourierParameters.isList2()) {
        IAST params = (IAST) fourierParameters;
        paramA = params.arg1().evalfNaN();
        paramB = params.arg2().evalfNaN();
        if (Double.isNaN(paramA) || Double.isNaN(paramB)) {
          return F.NIL;
        }
      }

      // f(list, {p1, p2, ...}): selected positions only
      if (ast.argSize() >= 2 && ast.arg2().isList()) {
        IAST positions = (IAST) ast.arg2();
        return computeSparse(listExpr, positions, paramA, paramB, originalAST);
      }

      if (((IAST) listExpr).exists(x -> x.isList())) {
        // a rectangular array of numbers, transformed along every dimension without a factor, and
        // the factor of all the dimensions applied once: rounding then leaves 5. as 5.
        if (!S.ArrayQ.ofQ(engine, listExpr, F.$b(), S.NumericQ)) {
          // Argument `1` is not a non-empty list or rectangular array of numeric quantities.
          return Errors.printMessage(ast.topHead(), "fftl", F.list(arg1), engine);
        }
        // a = direction() makes the factor of each one-dimensional step 1
        IExpr transformed = computeRecursive(listExpr, direction(), paramB);
        double scale =
            1.0 / Math.pow(countElements(listExpr), (1.0 - direction() * paramA) / 2.0);
        return scale == 1.0 ? transformed : engine.evaluate(F.Times(F.num(scale), transformed));
      }
      return computeRecursive(listExpr, paramA, paramB);

    } catch (RuntimeException rex) {
      // Likely numeric errors or interrupt
      return F.NIL;
    }
  }

  /**
   * Computes specific positions of the DFT. A one-dimensional list uses the direct summation for
   * the requested indices; a multidimensional array is transformed completely and the positions
   * are extracted.
   */
  private IExpr computeSparse(IExpr listExpr, IAST positions, double a, double b,
      IAST originalAST) {
    IAST list = (IAST) listExpr;
    if (list.size() > 1 && list.arg1().isList()) {
      IExpr fullResult = computeRecursive(listExpr, a, b);
      return F.Extract(fullResult, positions);
    }

    int n = list.argSize();
    Complex[] input = Convert.list2Complex(list);
    if (input == null) {
      return F.NIL;
    }

    IASTAppendable result = F.ListAlloc(positions.argSize());
    double scale = 1.0 / Math.pow(n, (1.0 - direction() * a) / 2.0);
    // selected positions use the exponent Exp(2*Pi*I*b*r*s/n) for both functions; only the factor
    // follows the direction
    double basePhase = 2.0 * Math.PI * b / n;

    for (IExpr p : positions) {
      int pInt = p.toIntDefault();
      if (F.isNotPresent(pInt)) {
        return F.NIL;
      }
      if (Math.abs(pInt) > list.argSize()) {
        // Position specification `1` in `2` is not applicable.
        return Errors.printMessage(originalAST.topHead(), "psl1",
            F.List(positions, originalAST));
      }

      // 0-based index s; a negative position counts from the end, -1 is the last element
      int s = pInt < 0 ? n + pInt : pInt - 1;
      if (s < 0) {
        s += n;
      }
      double[] term = dftTerm(input, basePhase * s, scale);
      result.append(F.complexNum(term[0], term[1]));
    }
    return result;
  }

  /**
   * <code>scale * Sum(input[r] * Exp(I * phase * r), {r, 0, n-1})</code>
   *
   * @return the real and the imaginary part
   */
  private static double[] dftTerm(Complex[] input, double phase, double scale) {
    double sumRe = 0.0;
    double sumIm = 0.0;
    for (int r = 0; r < input.length; r++) {
      double theta = phase * r;
      double wRe = Math.cos(theta);
      double wIm = Math.sin(theta);
      double uRe = input[r].getReal();
      double uIm = input[r].getImaginary();
      sumRe += (uRe * wRe - uIm * wIm);
      sumIm += (uRe * wIm + uIm * wRe);
    }
    return new double[] {scale * sumRe, scale * sumIm};
  }

  /** The number of elements of a rectangular array. */
  private static long countElements(IExpr expr) {
    if (!expr.isList()) {
      return 1L;
    }
    long count = 0L;
    for (IExpr part : (IAST) expr) {
      count += countElements(part);
    }
    return count;
  }

  /**
   * Multidimensional transform: a list of numbers is transformed directly; otherwise all inner
   * dimensions are transformed recursively and then the outer dimension.
   */
  private IExpr computeRecursive(IExpr expr, double a, double b) {
    if (!expr.isList()) {
      return expr;
    }
    IAST list = (IAST) expr;

    // Base Case: 1D List of Numbers
    if (list.size() > 1 && list.arg1().isNumber()) {
      return compute1D(list, a, b);
    }

    // N-Dimensional Case
    if (list.size() > 1) {
      // transform the inner dimensions of each sub-tensor
      IASTAppendable subTransformed = F.ListAlloc(list.argSize());
      for (IExpr row : list) {
        subTransformed.append(computeRecursive(row, a, b));
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

    // swap the outer dimension with the second one: list is [N1, N2, ...], transposed [N2, N1, ...]
    IExpr transposedExpr = F.eval(F.Transpose(list));
    if (!transposedExpr.isList()) {
      // Fallback for ragged arrays or errors
      return list;
    }
    IAST transposed = (IAST) transposedExpr;

    IASTAppendable result = F.ListAlloc(transposed.argSize());
    // each element of the new outer dimension (originally Dim 2) is a tensor whose outer dimension
    // is the original Dim 1
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

  private IExpr compute1D(IAST list, double a, double b) {
    int n = list.argSize();
    Complex[] input = Convert.list2Complex(list);
    if (input == null) {
      return F.NIL;
    }

    Complex[] output;
    double targetScale = 1.0 / Math.pow(n, (1.0 - direction() * a) / 2.0);
    double sign = direction() * b;

    if (isPowerOfTwo(n) && (sign == 1.0 || sign == -1.0)) {
      FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
      // Hipparchus FORWARD is exp(-i...) with scale 1, INVERSE is exp(+i...) with scale 1/n
      TransformType type = sign == 1.0 ? TransformType.INVERSE : TransformType.FORWARD;
      double hipparchusScale = type == TransformType.INVERSE ? 1.0 / n : 1.0;
      output = fft.transform(input, type);
      double correction = targetScale / hipparchusScale;
      if (Math.abs(correction - 1.0) > 1e-15) {
        for (int i = 0; i < n; i++) {
          output[i] = output[i].multiply(correction);
        }
      }
    } else {
      // Direct DFT for general n and {a,b}
      output = new Complex[n];
      double constant = sign * 2.0 * Math.PI / n;
      for (int s = 0; s < n; s++) {
        double[] term = dftTerm(input, constant * s, targetScale);
        output[s] = new Complex(term[0], term[1]);
      }
    }
    return Convert.toVector(output);
  }

  private static boolean isPowerOfTwo(int n) {
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
