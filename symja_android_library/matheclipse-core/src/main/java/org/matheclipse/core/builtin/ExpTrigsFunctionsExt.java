package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Helpers of {@link ExpTrigsFunctions} which answer a <i>scale free</i> question about a number and
 * therefore must not use the numerical tolerance of {@link IExpr#isZero()}.
 */
public final class ExpTrigsFunctionsExt {

  private ExpTrigsFunctionsExt() {
    // utility class, not instantiable
  }

  /**
   * Test if <code>expr</code> is exactly the number <code>0</code>.
   *
   * <p>
   * {@link IExpr#isZero()} is <em>tolerance</em> based for inexact numbers: {@link Num} compares
   * against {@link Config#DOUBLE_TOLERANCE} (about <code>1.11*10^-15</code>), {@link ApfloatNum}
   * against <code>10^-precision</code>. That answers the question &quot;did this numeric
   * computation cancel to zero?&quot; and it is the right default nearly everywhere - but not for a
   * question which is scale free. <code>ArcTan(x,y)</code> for example is the polar angle of the
   * point <code>(x,y)</code> and that angle is scale invariant:
   * <code>(3.0*10^-20,4.0*10^-20)</code> is the very same 3-4-5 direction as <code>(3,4)</code>.
   * Only the true origin has no angle, so the origin test must not use a tolerance.
   * </p>
   *
   * <p>
   * A tolerance of <code>0.0</code> switches {@link INumber#isZero(double)} to an exact comparison
   * for every inexact number type: <code>F#isFuzzyEquals</code> keeps its <code>a == b</code> case,
   * so the machine zeros <code>0.0</code> and <code>-0.0</code> are still zero. The exact number
   * types ignore the tolerance argument, and every expression which is not an {@link INumber} keeps
   * the plain {@link IExpr#isZero()}.
   * </p>
   *
   * @param expr the expression to test
   * @return <code>true</code> if <code>expr</code> is exactly the number <code>0</code>
   */
  public static boolean isExactZero(IExpr expr) {
    if (expr instanceof INumber) {
      return ((INumber) expr).isZero(0.0);
    }
    return expr.isZero();
  }

  /**
   * Calculate <code>y/x</code> for a denominator which {@link #isExactZero(IExpr)} has already
   * accepted as non zero.
   *
   * <p>
   * {@link F#Divide(IExpr, IExpr)} rejects its denominator with the same tolerance based
   * {@link IExpr#isZero()} and answers {@link S#Indeterminate} for a tiny but perfectly
   * representable denominator such as <code>3.0*10^-20</code> - the quadrant the caller just
   * determined correctly would be thrown away again. Only in that narrow window (a number which is
   * &quot;tolerance zero&quot; but not exactly zero) the quotient is built here directly as
   * <code>y*(1/x)</code>, which is the same calculation {@link F#Divide(IExpr, IExpr)} performs for
   * a number denominator. Every other denominator - in particular a real zero, with its
   * <code>Power::indet</code> message - keeps the established {@link F#Divide(IExpr, IExpr)}
   * behaviour.
   * </p>
   *
   * @param y the numerator
   * @param x the denominator
   * @return <code>y/x</code>
   */
  public static IExpr divideExact(IExpr y, IExpr x) {
    if (x.isNumber() && x.isZero() && !isExactZero(x)) {
      // x is zero for the tolerance based test only, so form the quotient without that test.
      // Power(x,-1) must not be used for the reciprocal: the Power evaluator applies the same
      // tolerance to its base and would answer ComplexInfinity.
      IExpr inverseX = ((INumber) x).inverse();
      if (y.isNumber()) {
        // the same shortcut F#Divide() takes for a number numerator
        return y.times(inverseX);
      }
      return F.Times(y, inverseX);
    }
    return F.Divide(y, x);
  }
}
