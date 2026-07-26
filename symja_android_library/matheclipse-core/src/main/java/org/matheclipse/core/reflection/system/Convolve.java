package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * Convolve(f, g, x, y)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the convolution of <code>f</code> and <code>g</code> with respect to the variable
 * <code>x</code>. The result is expressed in the variable <code>y</code>:
 * <code>Convolve(f,g,x,y) = Integrate(f(x)*g(y-x), {x, -Infinity, Infinity})</code>.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Convolution">Wikipedia - Convolution</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Convolve(UnitBox(x), UnitBox(x), x, y)
 * UnitTriangle(y)
 * </pre>
 */
public class Convolve extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final IExpr f = ast.arg1();
    final IExpr g = ast.arg2();
    final IExpr x = ast.arg3();
    final IExpr y = ast.arg4();
    if (!x.isSymbol() || x.equals(y)) {
      return F.NIL;
    }

    // 1. DiracDelta sifting: Convolve(DiracDelta(x-a), g, x, y) == g(y-a)
    IExpr result = diracDeltaConvolve(f, g, x, y, engine);
    if (result.isPresent()) {
      return result;
    }
    result = diracDeltaConvolve(g, f, x, y, engine);
    if (result.isPresent()) {
      return result;
    }

    // 2. UnitBox(x) (*) UnitBox(x) == UnitTriangle(y)
    if (f.isAST(S.UnitBox, 2) && f.first().equals(x) //
        && g.isAST(S.UnitBox, 2) && g.first().equals(x)) {
      return F.unaryAST1(S.UnitTriangle, y);
    }

    // 3. one-sided convolution of two causal signals f0(x)*UnitStep(x) and g0(x)*UnitStep(x):
    // Convolve == UnitStep(y) * Integrate(f0(x)*g0(y-x), {x, 0, y})
    final IExpr fAmplitude = causalAmplitude(f, x);
    final IExpr gAmplitude = causalAmplitude(g, x);
    if (fAmplitude.isPresent() && gAmplitude.isPresent()) {
      IExpr gShifted = F.subst(gAmplitude, x, F.Subtract(y, x));
      IExpr inner =
          engine.evaluate(F.Integrate(F.Times(fAmplitude, gShifted), F.list(x, F.C0, y)));
      if (inner.isPresent() && !inner.isIndeterminate() && inner.isFree(S.Integrate)) {
        return engine.evaluate(F.Times(F.UnitStep(y), inner));
      }
    }

    // 4. Gaussian (*) Gaussian:
    // Convolve(A*E^(-a*x^2), B*E^(-b*x^2), x, y) == A*B*Sqrt(Pi/(a+b))*E^(-(a*b/(a+b))*y^2)
    final IExpr[] fGauss = gaussianParameters(f, x, engine);
    final IExpr[] gGauss = gaussianParameters(g, x, engine);
    if (fGauss != null && gGauss != null) {
      IExpr sum = F.Plus(fGauss[1], gGauss[1]);
      IExpr amplitude = F.Times(fGauss[0], gGauss[0], F.Sqrt(F.Divide(S.Pi, sum)));
      IExpr exponent =
          F.Times(F.CN1, F.Divide(F.Times(fGauss[1], gGauss[1]), sum), F.Power(y, F.C2));
      return engine.evaluate(F.Times(amplitude, F.Exp(exponent)));
    }

    // 5. general definition: Convolve(f,g,x,y) == Integrate(f(x)*g(y-x), {x, -Infinity, Infinity})
    IExpr gShifted = F.subst(g, x, F.Subtract(y, x));
    IExpr integral = engine
        .evaluate(F.Integrate(F.Times(f, gShifted), F.list(x, F.CNInfinity, F.CInfinity)));
    if (integral.isPresent() && integral.isFree(S.Integrate) && integral.isFree(x)
        && !integral.isIndeterminate() && integral.isFree(S.DirectedInfinity)) {
      return engine.evaluate(F.Simplify(integral));
    }
    return F.NIL;
  }

  /**
   * If <code>maybeDelta</code> is <code>DiracDelta(x-a)</code> (with <code>a</code> free of
   * <code>x</code>) return the convolution <code>other(y-a)</code> by the sifting property
   * <code>Integrate(DiracDelta(x-a)*other(y-x), {x,-Infinity,Infinity}) == other(y-a)</code>.
   * Otherwise return {@link F#NIL}.
   */
  private static IExpr diracDeltaConvolve(IExpr maybeDelta, IExpr other, IExpr x, IExpr y,
      EvalEngine engine) {
    if (maybeDelta.isAST(S.DiracDelta, 2)) {
      IExpr arg = maybeDelta.first();
      // solve arg == x - a for a
      IExpr a = engine.evaluate(F.Subtract(x, arg));
      if (a.isFree(x)) {
        return engine.evaluate(F.subst(other, x, F.Subtract(y, a)));
      }
    }
    return F.NIL;
  }

  /**
   * If <code>f</code> is a causal signal <code>f0(x)*UnitStep(x)</code> return the amplitude
   * <code>f0(x)</code> (which may still depend on <code>x</code>). Otherwise return {@link F#NIL}.
   */
  private static IExpr causalAmplitude(IExpr f, IExpr x) {
    if (f.isAST(S.UnitStep, 2) && f.first().equals(x)) {
      return F.C1;
    }
    if (f.isTimes()) {
      IAST times = (IAST) f;
      int index = times.indexOf(t -> t.isAST(S.UnitStep, 2) && t.first().equals(x));
      if (index > 0) {
        return times.removeAtCopy(index).oneIdentity1();
      }
    }
    return F.NIL;
  }

  /**
   * If <code>f</code> is a centered Gaussian <code>A*E^(-a*x^2)</code> (with <code>A</code> and
   * <code>a</code> free of <code>x</code>) return the array <code>{A, a}</code>. Otherwise return
   * <code>null</code>.
   */
  private static IExpr[] gaussianParameters(IExpr f, IExpr x, EvalEngine engine) {
    IExpr amplitude = F.C1;
    IExpr exponent = F.NIL;
    if (f.isTimes()) {
      IAST times = (IAST) f;
      IASTAppendable rest = F.TimesAlloc(times.size());
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        IExpr candidate = gaussianExponent(factor);
        if (candidate.isPresent() && !candidate.isFree(x) && exponent.isNIL()) {
          exponent = candidate;
        } else if (factor.isFree(x)) {
          rest.append(factor);
        } else {
          return null;
        }
      }
      amplitude = rest.oneIdentity1();
    } else {
      exponent = gaussianExponent(f);
    }
    if (exponent.isNIL()) {
      return null;
    }
    // exponent must equal -a*x^2 with a free of x (no linear or constant term in x)
    IExpr a2 = engine.evaluate(F.Coefficient(exponent, x, F.C2));
    if (!a2.isFree(x) || a2.isZero() || !a2.isNegativeResult()) {
      return null;
    }
    IExpr remainder = engine.evaluate(F.Subtract(exponent, F.Times(a2, F.Power(x, F.C2))));
    if (!remainder.isZero()) {
      return null;
    }
    return new IExpr[] {amplitude, a2.negate()};
  }

  /** Return the exponent of an <code>E^(...)</code> or <code>Exp(...)</code> expression. */
  private static IExpr gaussianExponent(IExpr factor) {
    if (factor.isPower() && factor.base().equals(S.E)) {
      return factor.exponent();
    }
    if (factor.isAST(S.Exp, 2)) {
      return factor.first();
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_4_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
