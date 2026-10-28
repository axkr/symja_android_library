package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FourierSinTransform extends AbstractFunctionEvaluator {

  public FourierSinTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr expr = ast.arg1();
    IExpr t = ast.arg2();
    IExpr omega = ast.arg3();

    // Try lookup table before falling back to integration
    IExpr result = lookupFST(expr, t, omega, engine);
    if (result.isPresent()) {
      return result;
    }

    return FourierCosTransform.halfLineTransform(F.Times(expr, F.Sin(F.Times(omega, t))), t,
        omega, engine);
  }

  /**
   * Lookup table for known Fourier sine transform pairs. Checked before falling back to symbolic
   * integration. Convention: FST[f] = Sqrt(2/Pi) * Integrate(f(t) * Sin(w*t), {t, 0, Infinity})
   *
   * <p>
   * Supported patterns:
   *
   * <ul>
   * <li>FST[c * Cos(a*t) / t] = c * Sqrt(Pi/2) * HeavisideTheta(w - a)
   * </ul>
   */
  private IExpr lookupFST(IExpr f, IExpr t, IExpr omega, EvalEngine engine) {
    // FST(c * Cos(a*t) / t) = c * Sqrt(Pi/2)*(1 + Sign(w-omega)) / 2
    //
    // Derivation: Sin(wt)*Cos(at) = (Sin((w+a)t) + Sin((w-a)t))/2
    // ∫₀^∞ Cos(at)*Sin(wt)/t dt = (1/2)*[π/2*Sign(w+a) + π/2*Sign(w-a)]
    // For a>0,w>0: Sign(w+a)=1, so = (π/4)*(1+Sign(w-a)) = (π/2)*(1 + Sign(w-a)) / 2
    // Multiplied by Sqrt(2/Pi): Sqrt(Pi/2)*(1 + Sign(w-a)) / 2
    IExpr[] match = FourierCosTransform.matchConstTimesTrigOverT(f, t, S.Cos);
    if (match != null) {
      return FourierCosTransform.halfStep(match[0], F.Subtract(omega, match[1]), engine);
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
