package org.matheclipse.core.visit;

import java.math.BigDecimal;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.apfloat.Apfloat;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Rationalize numeric numbers {@link INum} and {@link IComplexNum} in expression <code>expr</code>.
 */
public class RationalizeNumericsVisitor extends VisitorExpr {
  /**
   * A machine number <code>x</code> is a fraction <code>p/q</code> only up to
   * <code>x*2^(-52)</code>, and the test for a fraction, <code>|p/q-x| &lt; 10^(-4)/q^2</code>,
   * says something as long as that is the smaller number: for <code>p*q</code> up to
   * <code>2^38</code>. No fraction of a larger number passes it.
   */
  private static final double MAX_DETERMINED = 0x1.0p38;

  double epsilon;
  boolean useConvergenceMethod;
  /** A number beyond {@link #MAX_DETERMINED} is left as it is, and is not the integer it is. */
  final boolean keepLargeNumbers;

  /**
   * Create a new visitor to rationalize numeric numbers {@link INum} and {@link IComplexNum} in an
   * expression.
   *
   * @param epsilon the epsilon value used for the rationalization
   * @param useConvergenceMethod if true, use the convergence method for rationalization
   */
  public RationalizeNumericsVisitor(double epsilon, boolean useConvergenceMethod) {
    this(epsilon, useConvergenceMethod, false);
  }

  /**
   * @param keepLargeNumbers with the convergence method: a number whose digits do not determine a
   *        fraction - a machine number beyond <code>2^38</code>, a number with fewer digits than
   *        it has in front of the decimal point - is left as it is. Otherwise it is converted
   *        to the integer which it is, for a caller which needs exact numbers.
   */
  public RationalizeNumericsVisitor(double epsilon, boolean useConvergenceMethod,
      boolean keepLargeNumbers) {
    super();
    this.epsilon = epsilon;
    this.useConvergenceMethod = useConvergenceMethod;
    this.keepLargeNumbers = keepLargeNumbers;
  }

  /**
   * Rationalize a single real or imaginary <code>part</code> of a number with the convergence
   * method.
   *
   * <p>
   * The convergence method tests <code>|p*q - part*q^2|</code> against an absolute limit, which the
   * very first convergent <code>0/1</code> already satisfies for every
   * <code>|part| &lt; 5*10^-5</code>. For such a part the exact decimal value is used instead of
   * giving up on it &ndash; but only if the number really is of machine precision, because
   * <code>part</code> is a lossy <code>double</code> view of a number of higher precision and an
   * &quot;exact&quot; fraction derived from it would silently drop digits.
   *
   * @param part the real or imaginary part to rationalize
   * @param isZeroPart <code>true</code> if <code>part</code> is considered to be zero
   * @param precision the precision of the number <code>part</code> was taken from
   * @return <code>null</code> if <code>part</code> can't be rationalized faithfully
   */
  private IFraction rationalizePart(double part, boolean isZeroPart, long precision) {
    final double abs = Math.abs(part);
    if (keepLargeNumbers && abs > MAX_DETERMINED) {
      return null;
    }
    if (abs >= AbstractFractionSym.MIN_INTEGER_DOUBLE) {
      // an integer; of a number with more digits the double is not the value
      return precision <= ParserConfig.MACHINE_PRECISION
          ? AbstractFractionSym.valueOf(new BigDecimal(part).toBigIntegerExact())
          : null;
    }
    IFraction fractionConvergent = F.fractionConvergent(part);
    if (fractionConvergent.isZero() && !isZeroPart) {
      return precision <= ParserConfig.MACHINE_PRECISION ? F.fractionDecimal(part) : null;
    }
    return fractionConvergent;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    return super.visitAST(ast);
  }

  /**
   * A number with no finite value has no rational approximation, so it is left as it is.
   *
   * <p>
   * Without this the conversion reaches {@code new BigFraction(Double.POSITIVE_INFINITY)}, which
   * answers a {@code MathIllegalArgumentException} that escapes as far as whichever built-in asked
   * to rationalize its argument. {@code Integrate(-Infinity, 2)} crashed that way instead of
   * reporting that {@code 2} is not something to integrate over.
   *
   * @param value a real or imaginary part
   * @return {@code true} if {@code value} can be rationalized at all
   */
  private static boolean isRationalizable(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }

  @Override
  public IExpr visit(IComplexNum element) {
    if (!isRationalizable(element.getRealPart()) || !isRationalizable(element.getImaginaryPart())) {
      return element;
    }
    if (useConvergenceMethod) {
      long precision = element.precision();
      IFraction realPart = rationalizePart(element.getRealPart(), element.re().isZero(), precision);
      IFraction imaginaryPart =
          rationalizePart(element.getImaginaryPart(), element.im().isZero(), precision);
      if (realPart == null || imaginaryPart == null) {
        return element;
      }
      return F.complex(realPart, imaginaryPart);
    }
    return F.complex(element.getRealPart(), element.getImaginaryPart(), epsilon);
  }

  @Override
  public IExpr visit(INum element) {
    if (element instanceof ApfloatNum) {
      // an integer with more digits than a double has: the number itself
      Apfloat value = ((ApfloatNum) element).apfloatValue();
      if (value.scale() > 15 && value.isInteger()) {
        if (keepLargeNumbers && useConvergenceMethod && value.precision() < value.scale()) {
          // not every digit of the integer is known
          return element;
        }
        return F.ZZ(value.floor().toBigInteger());
      }
    }
    if (!isRationalizable(element.getRealPart())) {
      return element;
    }
    if (useConvergenceMethod) {
      IFraction fraction =
          rationalizePart(element.getRealPart(), element.isZero(), element.precision());
      return fraction != null ? fraction : element;
    }
    return F.fraction(element.getRealPart(), epsilon);
  }
}
