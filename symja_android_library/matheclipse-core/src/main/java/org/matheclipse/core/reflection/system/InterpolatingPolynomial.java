package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathArrays.OrderDirection;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * InterpolatingPolynomial(data - list, symbol)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * get the polynomial representation for the given <code>data-list</code>.
 *
 * </blockquote>
 *
 * <p>
 * Newton polynomial interpolation, is the interpolation polynomial for a given set of data points
 * in the Newton form. The Newton polynomial is sometimes called Newton's divided differences
 * interpolation polynomial because the coefficients of the polynomial are calculated using divided
 * differences.
 *
 * <p>
 * See:<br>
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Newton_polynomial">Wikipedia - Newton Polynomial</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; InterpolatingPolynomial({{1,7},{3,11},{5,27}},x)
 * (3/2*x-5/2)*(x-1)+7
 * </pre>
 */
public class InterpolatingPolynomial extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public InterpolatingPolynomial() {}

  /**
   * Check that the given array is sorted for real numbers. If symbolic expressions occur we don't
   * compare.
   *
   * @param val Values.
   * @param dir Ordering direction.
   * @param strict Whether the order should be strict.
   * @param abort Whether to throw an exception if the check fails.
   * @return {@code true} if the array is sorted.
   * @throws MathIllegalArgumentException if the array is not sorted and {@code abort} is {@code
   *     true}.
   */
  public static boolean checkPartialRealOrder(IExpr[] val, OrderDirection dir, boolean strict,
      boolean abort) throws MathIllegalArgumentException {
    IReal previous = F.C0;
    final int max = val.length;
    int start = max;
    for (int i = 0; i < val.length; i++) {
      if (val[i] instanceof IReal) {
        previous = (IReal) val[i];
        start = i + 1;
        break;
      }
    }

    int index;
    ITEM: for (index = start; index < max; index++) {
      if (val[index] instanceof IReal) {
        switch (dir) {
          case INCREASING:
            if (strict) {
              if (((IReal) val[index]).isLE(previous)) {
                break ITEM;
              }
            } else {
              if (((IReal) val[index]).isLT(previous)) {
                break ITEM;
              }
            }
            break;
          case DECREASING:
            if (strict) {
              if (((IReal) val[index]).isGE(previous)) {
                break ITEM;
              }
            } else {
              if (((IReal) val[index]).isGT(previous)) {
                break ITEM;
              }
            }
            break;
          default:
            // Should never happen.
            throw MathRuntimeException.createInternalError();
        }

        previous = (IReal) val[index];
      }
    }

    if (index == max) {
      // Loop completed.
      return true;
    }

    // Loop early exit means wrong ordering.
    if (abort) {
      throw new MathIllegalArgumentException(
          dir == MathArrays.OrderDirection.INCREASING
              ? (strict ? LocalizedCoreFormats.NOT_STRICTLY_INCREASING_SEQUENCE
                  : LocalizedCoreFormats.NOT_INCREASING_SEQUENCE)
              : (strict ? LocalizedCoreFormats.NOT_STRICTLY_DECREASING_SEQUENCE
                  : LocalizedCoreFormats.NOT_DECREASING_SEQUENCE),
          val[index], previous, index, index - 1);
    } else {
      return false;
    }
  }

  /**
   * Check that the interpolation arrays are valid. The arrays features checked by this method are
   * that both arrays have the same length and this length is at least 2.
   *
   * @param x Interpolating points array.
   * @param y Interpolating values array.
   * @param abort Whether to throw an exception if {@code x} is not sorted.
   * @throws MathIllegalArgumentException if the array lengths are different.
   * @throws MathIllegalArgumentException if the number of points is less than 2.
   * @throws org.hipparchus.exception.MathIllegalArgumentException if {@code x} is not sorted in
   *         strictly increasing order and {@code abort} is {@code true}.
   * @return {@code false} if the {@code x} is not sorted in increasing order, {@code true}
   *         otherwise.
   * @see #evaluate(double[], double[], double)
   * @see #computeCoefficients()
   */
  public static boolean verifyInterpolationArray(IExpr x[], IExpr y[], boolean abort)
      throws MathIllegalArgumentException {
    if (x.length < 2) {
      throw new MathIllegalArgumentException(LocalizedCoreFormats.WRONG_NUMBER_OF_POINTS, 2,
          x.length, true);
    }

    return checkPartialRealOrder(x, MathArrays.OrderDirection.INCREASING, true, abort);
  }

  /**
   * Return a copy of the divided difference array.
   *
   * <p>
   * The divided difference array is defined recursively by
   *
   * <pre>
   * f[x0] = f(x0)
   * f[x0,x1,...,xk] = (f[x1,...,xk] - f[x0,...,x[k-1]]) / (xk - x0)
   * </pre>
   *
   * <p>
   * The computational complexity is O(N^2).
   *
   * @param x Interpolating points array.
   * @param y Interpolating values array.
   * @return a fresh copy of the divided difference array.
   * @throws MathIllegalArgumentException if the array lengths are different.
   * @throws MathIllegalArgumentException if the number of points is less than 2.
   * @throws MathIllegalArgumentException if {@code x} is not sorted in strictly increasing order.
   */
  protected static IExpr[] computeDividedDifference(final IExpr x[], final IExpr y[],
      EvalEngine engine) {
    // see org.hipparchus.analysis.interpolation.DividedDifferenceInterpolator
    verifyInterpolationArray(x, y, true);
    final IExpr[] divdiff = y.clone(); // initialization

    final int n = x.length;
    final IExpr[] a = new IExpr[n];
    a[0] = divdiff[0];
    for (int i = 1; i < n; i++) {
      for (int j = 0; j < n - i; j++) {
        final IExpr denominator = engine.evaluate(F.Subtract(x[j + i], x[j]));
        divdiff[j] = engine.evaluate(F.Divide(F.Subtract(divdiff[j + 1], divdiff[j]), denominator));
      }
      a[i] = divdiff[0];
    }

    return a;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList()) {

      final IAST list = (IAST) ast.arg1();
      IExpr z = ast.arg2();
      int size = list.size();
      if (size > 1) {
        int n = size - 1;
        IExpr[] xv = new IExpr[n];
        IExpr[] yv = new IExpr[n];
        int[] dim = list.isMatrix();

        if (dim != null && dim[1] == 2) {
          if (dim[1] != 2) {
            return F.NIL;
          }
          for (int i = 0; i < n; i++) {
            IAST row = list.getAST(i + 1);
            xv[i] = row.arg1();
            yv[i] = row.arg2();
          }
        } else {
          for (int i = 0; i < n; i++) {
            xv[i] = F.ZZ(i + 1);
            yv[i] = list.get(i + 1);
          }
        }
        try {
          IExpr[] c = new IExpr[n - 1];
          System.arraycopy(xv, 0, c, 0, c.length);
          // see org.hipparchus.analysis.interpolation.DividedDifferenceInterpolator
          IExpr[] a = computeDividedDifference(xv, yv, engine);

          // IASTAppendable polynomial = F.PlusAlloc(16);
          n = c.length;
          IExpr value = a[n];
          for (int i = n - 1; i >= 0; i--) {
            value = F.Plus(a[i], F.Times(F.Subtract(z, c[i]), value));
          }

          return value;
        } catch (MathRuntimeException mrex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), mrex);
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
