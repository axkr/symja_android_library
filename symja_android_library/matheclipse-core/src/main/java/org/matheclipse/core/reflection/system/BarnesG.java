package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of the Barnes G-function.
 * <p>
 */
public class BarnesG extends AbstractFunctionEvaluator {


  public BarnesG() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {

    IExpr arg = ast.arg1();

    if (arg.isInteger()) {
      return evaluateInteger((IInteger) arg);
    }
    if (arg.isFraction()) {
      return evaluateFraction((IFraction) arg);
    }
    if (arg.isInfinity()) {
      return F.CInfinity;
    }

    return F.NIL;
  }

  /**
   * Symbolic evaluation for rational arguments using reduction to range (0, 1]. Handles G(n/2),
   * G(n/4), etc.
   */
  private IExpr evaluateFraction(IFraction rat) {
    IInteger denom = rat.denominator();
    if (!denom.equals(F.C2) && !denom.equals(F.C4)) {
      return F.NIL;
    }
    if (rat.equals(F.C1D2)) {
      // (2^(1/24)*E^(1/8))/(Glaisher^(3/2)*Pi^(1/4))
      return F.Times(F.Power(F.C2, F.QQ(1L, 24L)), F.Exp(F.QQ(1L, 8L)),
          F.Power(F.Times(F.Power(F.Glaisher, F.QQ(3L, 2L)), F.Power(F.Pi, F.C1D4)), F.CN1));
    }
    if (rat.equals(F.C1D4)) {
      // E^(3/32-Catalan/(4*Pi))/(Glaisher^(9/8)*Gamma(1/4)^(3/4))
      return F.Times(
          F.Exp(F.Plus(F.QQ(3L, 32L),
              F.Times(F.CN1, F.Catalan, F.Power(F.Times(F.C4, F.Pi), F.CN1)))),
          F.Power(
              F.Times(F.Power(F.Glaisher, F.QQ(9L, 8L)), F.Power(F.Gamma(F.C1D4), F.QQ(3L, 4L))),
              F.CN1));

    }
    // We define the base range as (0, 1].
    // Calculate the floor to determine how many steps to shift.
    // offset = floor(rat)
    // if rat is 3/2 (1.5), floor is 1. base is 0.5.
    // if rat is -1/2 (-0.5), floor is -1. base is 0.5.

    IInteger floor = rat.floor();
    IRational base = rat.subtract(floor);

    // Calculate the factor accumulated by shifting G(z) to G(base)
    // Recurrence: G(z+1) = Gamma(z)*G(z)

    IExpr factor = F.C1;
    int n = floor.toIntDefault();
    if (n == Integer.MIN_VALUE) {
      // Safety: Integer too small/large to iterate
      return F.NIL;
    }

    try {
      if (n > 0) {
        // Shifting down: G(base + n)
        // G(base + n) = G(base) * Product_{k=0}^{n-1} Gamma(base + k)
        for (int k = 0; k < n; k++) {
          // argK = base + k
          IExpr argK = base.add(F.ZZ(k));
          factor = factor.multiply(F.Gamma(argK));
        }
      } else if (n < 0) {
        // Shifting up: G(base - m) where m = -n
        // G(base) = G((base-1) + 1) = Gamma(base-1) * G(base-1)
        // => G(base-1) = G(base) / Gamma(base-1)
        // General: G(base - m) = G(base) / Product_{k=1}^{m} Gamma(base - k)

        int m = -n;
        IExpr denominator = F.C1;
        for (int k = 1; k <= m; k++) {
          // argK = base - k
          IExpr argK = base.subtract(F.ZZ(k));
          denominator = denominator.multiply(F.Gamma(argK));
        }
        factor = factor.divide(denominator);
      }

      // 2. Evaluate the base G(base) if known
      IExpr baseValue = getBaseValue(base);

      // Combine: result = factor * baseValue
      return factor.multiply(baseValue);

    } catch (ArithmeticException e) {
      return F.NIL;
    }
  }

  /**
   * Returns the symbolic value for G(z) where z is in (0, 1). Currently implements G(1/2). Returns
   * F.BarnesG(z) for others.
   */
  private IExpr getBaseValue(IExpr base) {
    // Case: 1/2
    if (base.equals(F.C1D2)) {
      // Formula: G(1/2) = e^(1/8) * 2^(1/24) * Pi^(-1/4) * A^(-3/2)
      // A is the Glaisher constant.

      IExpr A = F.Glaisher; // Assumes F.Glaisher is the standard symbol

      // Terms
      IExpr t1 = F.Power(F.E, F.QQ(1, 8));
      IExpr t2 = F.Power(F.C2, F.QQ(1, 24));
      IExpr t3 = F.Power(F.Pi, F.QQ(-1, 4));
      IExpr t4 = F.Power(A, F.QQ(-3, 2));

      return F.Times(t1, t2, t3, t4);
    }

    // Case: 1/4, 3/4
    // Closed forms for G(1/4) involve Catalan's constant and are quite complex/non-standard
    // in basic simplification unless specifically requested.
    // We return the symbolic function call for the reduced base.
    return F.unaryAST1(S.BarnesG, base);
  }

  /**
   * Symbolic evaluation for integer arguments. G(n) = Product_{i=0}^{n-2} i!
   */
  private IExpr evaluateInteger(IInteger n) {
    // G(1) = 1, G(2) = 1
    if (n.isOne() || n.equalsInt(2)) {
      return F.C1;
    }
    // G(n) = 0 for integers <= 0 (zeros of the function)
    if (!n.isPositive()) {
      return F.C0;
    }

    try {
      // For very large n, this loop can be slow and produce huge numbers.
      // Symja typically handles huge integers, but we might want a limit or check for interrupt.
      // Here we implement the product of factorials.
      int val = n.toInt();
      // Limit symbolic computation to avoid freezing for very large n (e.g. > 10000)
      if (val > 5000) {
        return F.NIL; // Let it remain symbolic or handle elsewhere
      }

      IInteger product = F.C1;
      IInteger factorial = F.C1;
      // G(n) = 0! * 1! * ... * (n-2)!
      for (int i = 1; i <= val - 2; i++) {
        factorial = factorial.multiply(F.ZZ(i));
        product = product.multiply(factorial);
      }
      return product;
    } catch (ArithmeticException e) {
      // n is too large for int, or other issue
      return F.NIL;
    }
  }

  @Override
  public IExpr numericFunction(IAST ast, final EvalEngine engine) {
    if (ast.argSize() == 1) {
      try {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.barnesG();
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.BarnesG, rex);
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
