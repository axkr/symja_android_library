package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class ArithmeticUtil {

  private ArithmeticUtil() {
    // private constructor to avoid instantiation
  }

  /**
   * Print message <code>Infinite expression `nonZeroNumerator/zeroDenominator` encountered</code>
   * an return {@link F#CComplexInfinity} as result.
   * 
   * @param head the head symbol which should be printed in the message
   * @param nonZeroNumerator the non-zero numerator expression
   * @param zeroDenominator the expression which represents <code>0</code>
   * @return
   */
  public static IExpr printInfy(ISymbol head, final IExpr nonZeroNumerator,
      final IExpr zeroDenominator) {
    // Infinite expression `1` encountered.
    Errors.printMessage(head, "infy", F.list(F.Divide(nonZeroNumerator, zeroDenominator)));
    return F.CComplexInfinity;
  }

  public static IExpr powerComplexComplex(final IBigNumber base, final IComplex exponent,
      EvalEngine engine) {
    if (base.getImaginaryPart().isZero()) {
      IRational a = base.getRealPart();
      IRational b = exponent.getRealPart();
      IRational c = exponent.getImaginaryPart();
      IExpr temp = // [$ b*Arg(a)+1/2*c*Log(a^2) $]
          F.Plus(F.Times(b, F.Arg(a)), F.Times(F.C1D2, c, F.Log(F.Sqr(a)))); // $$;
      temp = temp.eval(engine);
      temp = // [$ (a^2)^(b/2)*E^(-c*Arg(a)) * (Cos(temp)+I* Sin(temp)) $]
          F.Times(F.Power(F.Sqr(a), F.Times(F.C1D2, b)), F.Exp(F.Times(F.CN1, c, F.Arg(a))),
              F.Plus(F.Cos(temp), F.Times(F.CI, F.Sin(temp)))); // $$;
      return temp.eval(engine);
    }
    return F.NIL;
  }

}
