package org.matheclipse.core.visit;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;

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

  @Override
  public IExpr visit(IASTMutable ast) {
    return super.visitAST(ast);
  }

  @Override
  public IExpr visit(IComplexNum element) {
    if (useConvergenceMethod) {
      IComplex complexConvergent =
          F.complexConvergent(element.getRealPart(), element.getImaginaryPart());
      if ((complexConvergent.getRealPart().isZero() && !element.re().isZero()) //
          || (complexConvergent.getImaginaryPart().isZero() && !element.im().isZero())) {
        return element;
      }
      return complexConvergent;
    }
    return F.complex(element.getRealPart(), element.getImaginaryPart(), epsilon);
  }

  @Override
  public IExpr visit(INum element) {
    if (useConvergenceMethod) {
      IFraction fractionConvergent = F.fractionConvergent(element.getRealPart());
      if (fractionConvergent.isZero() && !element.isZero()) {
        return element;
      }
      return fractionConvergent;
    }
    return F.fraction(element.getRealPart(), epsilon);
  }
}
