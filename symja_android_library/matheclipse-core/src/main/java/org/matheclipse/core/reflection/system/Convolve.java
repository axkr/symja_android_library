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
      return F.UnitTriangle(y);
    }

    // 3. one-sided convolution of two causal signals f0(x)*UnitStep(x) and g0(x)*UnitStep(x):
    // Convolve == UnitStep(y) * Integrate(f0(x)*g0(y-x), {x, 0, y})
    final IExpr fAmplitude = causalAmplitude(f, x);
    final IExpr gAmplitude = causalAmplitude(g, x);
    if (fAmplitude.isPresent() && gAmplitude.isPresent()) {
      IExpr gShifted = F.subst(gAmplitude, x, F.Subtract(y, x));
      IExpr inner = engine.evaluate(F.Integrate(F.Times(fAmplitude, gShifted), F.list(x, F.C0, y)));
      if (inner.isPresent() && !inner.isIndeterminate() && inner.isFree(S.Integrate)) {
        return engine.evaluate(F.Times(F.UnitStep(y), inner));
      }
    }

    // 4. Gaussian (*) Gaussian, shifted or not:
    // Convolve(A*E^(-a*(x-m)^2), B*E^(-b*(x-n)^2), x, y)
    // == A*B*Sqrt(Pi/(a+b))*E^(-(a*b/(a+b))*(y-m-n)^2)
    final IExpr[] fGauss = gaussianParameters(f, x, engine);
    final IExpr[] gGauss = gaussianParameters(g, x, engine);
    if (fGauss != null && gGauss != null) {
      IExpr sum = engine.evaluate(F.Together(F.Plus(fGauss[1], gGauss[1])));
      // Sqrt(Pi/2) for a number, Sqrt(Pi)/Sqrt(a+b) for a symbolic width, as Mathematica writes them
      IExpr root = sum.isNumber() ? F.Sqrt(F.Divide(S.Pi, sum))
          : F.Divide(F.Sqrt(S.Pi), F.Sqrt(sum));
      IExpr amplitude = F.Times(fGauss[0], gGauss[0], root);
      IExpr shift = F.Subtract(y, F.Plus(fGauss[2], gGauss[2]));
      IExpr width = F.Together(F.Divide(F.Times(fGauss[1], gGauss[1]), sum));
      IExpr exponent = F.Times(F.CN1, width, F.Power(shift, F.C2));
      return engine.evaluate(F.Times(amplitude, F.Exp(exponent)));
    }

    // 5. general definition: Convolve(f,g,x,y) == Integrate(f(x)*g(y-x), {x, -Infinity, Infinity})
    IExpr gShifted = F.subst(g, x, F.Subtract(y, x));
    IExpr integral =
        engine.evaluate(F.Integrate(F.Times(f, gShifted), F.list(x, F.CNInfinity, F.CInfinity)));
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
   * If <code>f</code> is a Gaussian <code>A*E^(-a*(x-m)^2)</code> return the array
   * <code>{A, a, m}</code>, with <code>A</code>, <code>a</code> and <code>m</code> free of
   * <code>x</code>. Otherwise return <code>null</code>.
   *
   * <p>
   * The exponent may be any quadratic polynomial <code>c2*x^2 + c1*x + c0</code> in <code>x</code>,
   * spread over several <code>E^(...)</code> factors: completing the square gives
   * <code>a = -c2</code>, <code>m = c1/(2*a)</code>, and the constant
   * <code>E^(c0 + c1^2/(4*a))</code> moves into <code>A</code>. This covers the shifted
   * <code>E^(-(x-2)^2)</code> and the densities <code>PDF(NormalDistribution(m, s), x)</code>. A
   * symbolic <code>c2</code> is accepted unless it is known to be non-negative, since the integral
   * only converges for <code>a > 0</code>.
   */
  private static IExpr[] gaussianParameters(IExpr f, IExpr x, EvalEngine engine) {
    IASTAppendable amplitude = F.TimesAlloc(4);
    IASTAppendable exponent = F.PlusAlloc(4);
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      IExpr candidate = gaussianExponent(factor);
      if (candidate.isPresent() && !candidate.isFree(x)) {
        exponent.append(candidate);
      } else if (factor.isFree(x)) {
        amplitude.append(factor);
      } else {
        return null;
      }
    }
    if (exponent.argSize() == 0) {
      return null;
    }
    IExpr quadratic = engine.evaluate(F.Expand(exponent.oneIdentity0()));
    IExpr c2 = engine.evaluate(F.Coefficient(quadratic, x, F.C2));
    IExpr c1 = engine.evaluate(F.Coefficient(quadratic, x, F.C1));
    IExpr c0 = engine.evaluate(F.Coefficient(quadratic, x, F.C0));
    if (!c2.isFree(x) || !c1.isFree(x) || !c0.isFree(x) || c2.isZero() || c2.isPositiveResult()
        || (c2.isReal() && !c2.isNegative())) {
      return null;
    }
    IExpr remainder = engine.evaluate(F.Expand(F.Subtract(quadratic,
        F.Plus(F.Times(c2, F.Power(x, F.C2)), F.Times(c1, x), c0))));
    if (!remainder.isZero()) {
      return null;
    }
    IExpr a = engine.evaluate(F.Negate(c2));
    IExpr m = engine.evaluate(F.Together(F.Divide(c1, F.Times(F.C2, a))));
    IExpr constant =
        engine.evaluate(F.Together(F.Plus(c0, F.Divide(F.Sqr(c1), F.Times(F.C4, a)))));
    if (!constant.isZero()) {
      amplitude.append(F.Exp(constant));
    }
    return new IExpr[] {engine.evaluate(amplitude.oneIdentity1()), a, m};
  }

  /** Return the exponent of an <code>E^(...)</code> or <code>Exp(...)</code> expression. */
  private static IExpr gaussianExponent(IExpr factor) {
    if (factor.isPower() && factor.base() == S.E) {
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
