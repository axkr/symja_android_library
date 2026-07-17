package org.matheclipse.core.visit;

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
  double epsilon;
  boolean useConvergenceMethod;

  /**
   * Create a new visitor to rationalize numeric numbers {@link INum} and {@link IComplexNum} in an
   * expression.
   *
   * @param epsilon the epsilon value used for the rationalization
   * @param useConvergenceMethod if true, use the convergence method for rationalization
   */
  public RationalizeNumericsVisitor(double epsilon, boolean useConvergenceMethod) {
    super();
    this.epsilon = epsilon;
    this.useConvergenceMethod = useConvergenceMethod;
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
  private static IFraction rationalizePart(double part, boolean isZeroPart, long precision) {
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

  @Override
  public IExpr visit(IComplexNum element) {
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
    if (useConvergenceMethod) {
      IFraction fraction =
          rationalizePart(element.getRealPart(), element.isZero(), element.precision());
      return fraction != null ? fraction : element;
    }
    return F.fraction(element.getRealPart(), epsilon);
  }
}
