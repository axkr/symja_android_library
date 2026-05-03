package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FourierCosTransform extends AbstractFunctionEvaluator {

  public FourierCosTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr f = ast.arg1();
    IExpr t = ast.arg2();
    IExpr omega = ast.arg3();

    // Try lookup table before falling back to integration
    IExpr result = lookupFCT(f, t, omega, engine);
    if (result.isPresent()) {
      return result;
    }

    IExpr assumptions = F.Rule(S.Assumptions, F.Greater(omega, F.C0));
    IAST integral = //
        F.Times(F.Sqrt(F.Divide(F.C2, S.Pi)), //
            F.Integrate(F.Times(f, F.Cos(F.Times(omega, t))), //
                F.List(t, F.C0, F.CInfinity), //
                assumptions));
    IExpr FF = engine.evaluate(integral);
    if (FF.isFree(x -> x == S.Integrate, true)) {
      return FF;
    }
    return F.NIL;
  }

  /**
   * Lookup table for known Fourier cosine transform pairs. Checked before falling back to symbolic
   * integration. Convention: FCT[f] = Sqrt(2/Pi) * Integrate(f(t) * Cos(w*t), {t, 0, Infinity})
   *
   * <p>
   * Supported patterns:
   *
   * <ul>
   * <li>FCT[c * Sin(a*t) / t] = c * Sqrt(Pi/2) * HeavisideTheta(a - w)
   * </ul>
   */
  private IExpr lookupFCT(IExpr f, IExpr t, IExpr omega, EvalEngine engine) {
    // FCT[ c * Sin(a*t) / t ] = c * Sqrt(Pi/2) * (1 + Sign(a-omega)) / 2
    //
    // Derivation: ∫₀^∞ Sin(at)*Cos(wt)/t dt = (π/4)*(1+Sign(a-w)) = (π/2)*HeavisideTheta(a-w)
    // Multiplied by Sqrt(2/Pi): Sqrt(2/Pi)*(π/2)*(1 + Sign(a-w)) / 2 =
    // Sqrt(Pi/2)*(1 + Sign(a-w)) / 2
    IExpr[] match = matchConstTimesTrigOverT(f, t, S.Sin);
    if (match != null) {
      IExpr c = match[0];
      IExpr a = match[1];
      return engine.evaluate(
          F.Times(c, F.Sqrt(F.Divide(S.Pi, F.C2)), F.C1D2,
              F.Plus(F.C1, F.Sign(F.Subtract(a, omega)))));
    }
    return F.NIL;
  }

  /**
   * Tries to match {@code f} against the pattern {@code c * TrigHead(a*t) / t}, where {@code c} and
   * {@code a} are free of {@code t}.
   *
   * @param f the expression to match
   * @param t the integration variable
   * @param trigHead {@link S#Sin} or {@link S#Cos}
   * @return a 2-element array {@code {c, a}} on success, or {@code null} on failure
   */
  static IExpr[] matchConstTimesTrigOverT(IExpr f, IExpr t, IBuiltInSymbol trigHead) {
    if (!f.isTimes()) {
      return null;
    }
    IAST times = (IAST) f;
    boolean foundInvT = false;
    IExpr trigFactor = null;
    IASTAppendable constFactors = F.TimesAlloc(times.size());

    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (!foundInvT && factor.isPower() && factor.base().equals(t)
          && factor.exponent().isMinusOne()) {
        foundInvT = true;
      } else if (trigFactor == null && factor.isAST(trigHead, 2)) {
        trigFactor = factor;
      } else if (factor.isFree(t)) {
        constFactors.append(factor);
      } else {
        return null; // unexpected t-dependent factor — bail out
      }
    }

    if (foundInvT && trigFactor != null) {
      IExpr arg = trigFactor.first();
      IExpr a = linearCoefficientInT(arg, t);
      if (a.isPresent()) {
        return new IExpr[] {constFactors.oneIdentity1(), a};
      }
    }
    return null;
  }

  /**
   * If {@code arg} is of the form {@code a*t} (linear in {@code t} with a constant coefficient),
   * returns the coefficient {@code a}. Returns {@link F#NIL} otherwise.
   *
   * <p>
   * Examples: {@code t} → {@code 1}, {@code 3*t} → {@code 3}, {@code a*t} → {@code a}, {@code t^2}
   * → {@link F#NIL}.
   */
  static IExpr linearCoefficientInT(IExpr arg, IExpr t) {
    if (arg.equals(t)) {
      return F.C1;
    }
    if (arg.isTimes()) {
      IAST times = (IAST) arg;
      IASTAppendable coeff = F.TimesAlloc(times.size());
      boolean foundT = false;
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.equals(t)) {
          foundT = true;
        } else if (factor.isFree(t)) {
          coeff.append(factor);
        } else {
          return F.NIL; // non-linear in t
        }
      }
      if (foundT) {
        return coeff.oneIdentity1();
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
