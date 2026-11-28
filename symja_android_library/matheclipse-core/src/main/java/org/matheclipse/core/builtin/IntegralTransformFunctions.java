package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Integral transforms and fractional derivatives: <code>FourierTransform</code>,
 * <code>InverseFourierTransform</code>, <code>InverseFourierCosTransform</code>,
 * <code>InverseFourierSinTransform</code>, <code>MellinTransform</code>,
 * <code>InverseMellinTransform</code>, <code>ListZTransform</code>,
 * <code>DiscreteConvolve</code>, <code>CaputoD</code> and <code>FractionalD</code>.
 *
 * <p>
 * The transforms use the rules for sums, constant factors, powers of the variable and shifts, and a
 * table of standard pairs. An integrand which none of them covers is integrated; the result is
 * taken only if it has neither an <code>Integrate</code> nor a condition left.
 */
public final class IntegralTransformFunctions {

  private static class Initializer {

    private static void init() {
      S.FourierTransform.setEvaluator(new FourierTransform(false));
      S.InverseFourierTransform.setEvaluator(new FourierTransform(true));
      S.InverseFourierCosTransform.setEvaluator(new InverseFourierCosSin(S.FourierCosTransform));
      S.InverseFourierSinTransform.setEvaluator(new InverseFourierCosSin(S.FourierSinTransform));
      S.MellinTransform.setEvaluator(new MellinTransform());
      S.InverseMellinTransform.setEvaluator(new InverseMellinTransform());
      S.ListZTransform.setEvaluator(new ListZTransform());
      S.DiscreteConvolve.setEvaluator(new DiscreteConvolve());
      S.CaputoD.setEvaluator(new FractionalDerivative(true));
      S.FractionalD.setEvaluator(new FractionalDerivative(false));
    }
  }

  /** <code>{constant factor, rest}</code> of a product with respect to <code>x</code>. */
  private static IExpr[] splitConstant(IExpr f, IExpr x) {
    if (f.isFree(x)) {
      return new IExpr[] {f, F.C1};
    }
    if (!f.isTimes()) {
      return new IExpr[] {F.C1, f};
    }
    IASTAppendable constant = F.TimesAlloc(f.argSize());
    IASTAppendable rest = F.TimesAlloc(f.argSize());
    for (IExpr factor : (IAST) f) {
      (factor.isFree(x) ? constant : rest).append(factor);
    }
    return new IExpr[] {constant.oneIdentity1(), rest.oneIdentity1()};
  }

  /** The coefficients <code>{c0, c1, ...}</code> of a polynomial in <code>x</code>, or null. */
  private static IAST coefficients(IExpr p, IExpr x, EvalEngine engine) {
    if (!p.isPolynomial(F.list(x))) {
      return null;
    }
    IExpr list = engine.evaluate(F.CoefficientList(p, x));
    return list.isList() ? (IAST) list : null;
  }

  /** An integral which was evaluated without a condition, or {@link F#NIL}. */
  private static @AllowNIL IExpr closedIntegral(IExpr integral, EvalEngine engine) {
    IExpr result = engine.evalQuiet(integral);
    if (result.has(x -> x == S.Integrate || x == S.ConditionalExpression, true)) {
      return F.NIL;
    }
    return result;
  }

  // ---------------------------------------------------------------------------------------------
  // Fourier transform

  /**
   * <code>FourierTransform(f, t, w)</code> -
   * <code>Sqrt(Abs(b)/(2 Pi)^(1-a)) Integrate(f E^(I b w t), {t, -Infinity, Infinity})</code>
   * with the option <code>FourierParameters -&gt; {a, b}</code>, by default <code>{0, 1}</code>.
   *
   * <p>
   * <code>InverseFourierTransform(g, w, t)</code> - the inverse transform, which is the transform
   * with the parameters <code>{-a, -b}</code>.
   */
  private static final class FourierTransform extends AbstractFunctionEvaluator {

    private final boolean inverse;

    FourierTransform(boolean inverse) {
      this.inverse = inverse;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = F.C0;
      IExpr b = F.C1;
      for (int i = 4; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (!option.isRuleAST() || option.first() != S.FourierParameters || !option.second().isList2()) {
          return F.NIL;
        }
        a = option.second().first();
        b = option.second().second();
      }
      if (inverse) {
        a = F.Negate(a);
        b = F.Negate(b);
      }
      final IExpr f = ast.arg1();
      final IExpr t = ast.arg2();
      final IExpr w = ast.arg3();
      if (!t.isSymbol() || f.isList()) {
        return F.NIL;
      }
      ISymbol omega = F.Dummy("omega");
      IExpr kernel = kernel(f, t, omega, engine, 0);
      if (kernel.isNIL()) {
        kernel = closedIntegral(F.Integrate(F.Times(f, F.Exp(F.Times(F.CI, omega, t))),
            F.list(t, F.CNInfinity, F.CInfinity),
            F.Rule(S.Assumptions, F.Element(omega, S.Reals))), engine);
        if (kernel.isNIL()) {
          return F.NIL;
        }
      }
      // 2^e and Pi^e as separate factors, so that a Sqrt(Pi) of the table cancels
      IExpr exponent = F.Times(F.C1D2, F.Subtract(a, F.C1));
      IExpr factor =
          F.Times(F.Sqrt(F.Abs(b)), F.Power(F.C2, exponent), F.Power(S.Pi, exponent));
      IExpr transform = engine.evaluate(F.Times(factor, F.subst(kernel, omega, F.Times(b, w))));
      // the table gives the pairs in a form which still has to be simplified
      IExpr simplified = engine.evalQuiet(F.Simplify(transform));
      return simplified.leafCount() <= transform.leafCount() ? simplified : transform;
    }

    /**
     * <code>Integrate(f E^(I w t), {t, -Infinity, Infinity})</code> from the rules and the table,
     * or {@link F#NIL}.
     */
    private static @AllowNIL IExpr kernel(IExpr f, IExpr t, IExpr w, EvalEngine engine, int depth) {
      if (depth > 20) {
        return F.NIL;
      }
      engine.checkTimeLimit();
      if (f.isFree(t)) {
        return F.Times(F.C2, S.Pi, f, F.DiracDelta(w));
      }
      if (f.isPlus()) {
        IASTAppendable sum = F.PlusAlloc(f.argSize());
        for (IExpr term : (IAST) f) {
          IExpr k = kernel(term, t, w, engine, depth + 1);
          if (k.isNIL()) {
            return F.NIL;
          }
          sum.append(k);
        }
        return sum;
      }
      IExpr[] split = splitConstant(f, t);
      if (!split[0].isOne()) {
        IExpr k = kernel(split[1], t, w, engine, depth + 1);
        return k.isNIL() ? F.NIL : F.Times(split[0], k);
      }
      if (f.isTimes()) {
        IAST times = (IAST) f;
        for (int i = 1; i < times.size(); i++) {
          IExpr factor = times.get(i);
          IExpr rest = times.removeAtCopy(i).oneIdentity1();
          // t^n f: (-I)^n D(K(f), {w, n})
          if (factor.equals(t) || (factor.isPower() && factor.base().equals(t)
              && factor.exponent().isInteger() && factor.exponent().isPositive())) {
            int n = factor.equals(t) ? 1 : factor.exponent().toIntDefault();
            IExpr k = kernel(rest, t, w, engine, depth + 1);
            if (k.isNIL() || n > 20) {
              return F.NIL;
            }
            return F.Times(F.Power(F.CNI, F.ZZ(n)), F.D(engine.evaluate(k), F.list(w, F.ZZ(n))));
          }
          // E^(c0 + c1 t) f: the transform of f at w - I c1
          if (factor.isExp()) {
            IAST c = coefficients(factor.exponent(), t, engine);
            if (c != null && c.argSize() == 2) {
              if (rest.isAST(S.HeavisideTheta, 2) && rest.first().equals(t)) {
                // HeavisideTheta(t) E^(c0 + c1 t): E^c0/(-c1 - I w)
                return F.Times(F.Exp(c.arg1()),
                    F.Power(F.Subtract(F.Negate(c.arg2()), F.Times(F.CI, w)), F.CN1));
              }
              IExpr k = kernel(rest, t, w, engine, depth + 1);
              if (k.isNIL()) {
                return F.NIL;
              }
              return F.Times(F.Exp(c.arg1()), F.subst(k, w, F.Subtract(w, F.Times(F.CI, c.arg2()))));
            }
          }
          // Sin(a t)/t: Pi*Sign(a) for Abs(w) < Abs(a), Pi/2 at the ends
          if (factor.isPower() && factor.base().equals(t) && factor.exponent().isMinusOne()
              && rest.isSin()) {
            IAST c = coefficients(rest.first(), t, engine);
            if (c != null && c.argSize() == 2 && c.arg1().isZero()) {
              return F.Times(F.C1D2, S.Pi,
                  F.Plus(F.Sign(F.Subtract(c.arg2(), w)), F.Sign(F.Plus(c.arg2(), w))));
            }
          }
        }
      }
      if (f.isExp()) {
        IExpr exponent = f.exponent();
        IAST c = coefficients(exponent, t, engine);
        if (c != null && c.argSize() == 3) {
          // a Gaussian: Sqrt(Pi/(-c2)) E^(c0 - (c1 + I w)^2/(4 c2))
          IExpr c0 = c.arg1();
          IExpr c1 = c.arg2();
          IExpr c2 = c.arg3();
          return F.Times(F.Sqrt(F.Times(S.Pi, F.Power(F.Negate(c2), F.CN1))),
              F.Exp(F.Subtract(c0, F.Times(F.Sqr(F.Plus(c1, F.Times(F.CI, w))),
                  F.Power(F.Times(F.C4, c2), F.CN1)))));
        }
        if (c != null && c.argSize() == 2) {
          // E^(c0 + I a t): 2 Pi E^c0 DiracDelta(w + a)
          IExpr a = engine.evaluate(F.Times(F.CNI, c.arg2()));
          if (a.isRealResult()) {
            return F.Times(F.C2, S.Pi, F.Exp(c.arg1()), F.DiracDelta(F.Plus(w, a)));
          }
          return F.NIL;
        }
        IExpr[] parts = splitConstant(exponent, t);
        if (parts[1].isAST(S.Abs, 2) && parts[1].first().equals(t)) {
          // E^(-a Abs(t)): 2 a/(a^2 + w^2)
          IExpr a = F.Negate(parts[0]);
          return F.Times(F.C2, a, F.Power(F.Plus(F.Sqr(a), F.Sqr(w)), F.CN1));
        }
        return F.NIL;
      }
      if (f.isAST(S.DiracDelta, 2)) {
        IAST c = coefficients(f.first(), t, engine);
        if (c != null && c.argSize() == 2) {
          // DiracDelta(c1 t + c0) = DiracDelta(t + c0/c1)/Abs(c1)
          return F.Times(F.Power(F.Abs(c.arg2()), F.CN1),
              F.Exp(F.Times(F.CNI, w, c.arg1(), F.Power(c.arg2(), F.CN1))));
        }
        return F.NIL;
      }
      if (f.isAST(S.HeavisideTheta, 2) && f.first().equals(t)) {
        return F.Plus(F.Times(F.CI, F.Power(w, F.CN1)), F.Times(S.Pi, F.DiracDelta(w)));
      }
      if (f.isAST(S.Sign, 2) && f.first().equals(t)) {
        return F.Times(F.C2, F.CI, F.Power(w, F.CN1));
      }
      if (f.isPower() && f.exponent().isMinusOne()) {
        IAST c = coefficients(f.base(), t, engine);
        if (c != null && c.argSize() == 3 && c.arg2().isZero()) {
          // 1/(c0 + c2 t^2): Pi/Sqrt(c0 c2) E^(-Sqrt(c0/c2) Abs(w))
          IExpr c0 = c.arg1();
          IExpr c2 = c.arg3();
          return F.Times(S.Pi, F.Power(F.Sqrt(F.Times(c0, c2)), F.CN1),
              F.Exp(F.Times(F.CN1, F.Sqrt(F.Times(c0, F.Power(c2, F.CN1))), F.Abs(w))));
        }
      }
      if (f.has(x -> x.isSin() || x.isCos(), true)) {
        IExpr exponential = engine.evaluate(F.Expand(F.TrigToExp(f)));
        if (!exponential.equals(f) && !exponential.has(x -> x.isSin() || x.isCos(), true)) {
          return kernel(exponential, t, w, engine, depth + 1);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  /**
   * <code>InverseFourierCosTransform(g, w, t)</code> and
   * <code>InverseFourierSinTransform(g, w, t)</code> - the inverse cosine and sine transforms.
   * With the default parameters a transform is its own inverse; with
   * <code>FourierParameters -&gt; {a, b}</code> the inverse is
   * <code>2 Sqrt(Abs(b)/(2 Pi)^(1+a)) Sqrt(Pi/2)</code> times the default transform at
   * <code>b t</code>.
   */
  private static final class InverseFourierCosSin extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol forward;

    InverseFourierCosSin(IBuiltInSymbol forward) {
      this.forward = forward;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = F.C0;
      IExpr b = F.C1;
      if (ast.argSize() == 4) {
        IExpr option = ast.arg4();
        if (!option.isRuleAST() || option.first() != S.FourierParameters || !option.second().isList2()) {
          return F.NIL;
        }
        a = option.second().first();
        b = option.second().second();
      }
      IExpr transform = engine.evaluate(F.ternaryAST3(forward, ast.arg1(), ast.arg2(), ast.arg3()));
      if (transform.isAST(forward)) {
        return F.NIL;
      }
      if (a.isZero() && b.isOne()) {
        return transform;
      }
      IExpr factor = F.Times(F.C2,
          F.Sqrt(F.Times(F.Abs(b), F.Power(F.Times(F.C2, S.Pi), F.Negate(F.Plus(F.C1, a))))),
          F.Sqrt(F.Times(S.Pi, F.C1D2)));
      return F.Times(factor, F.subst(transform, ast.arg3(), F.Times(b, ast.arg3())));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Mellin transform

  /**
   * <code>MellinTransform(f, x, s)</code> - <code>Integrate(x^(s-1) f, {x, 0, Infinity})</code>.
   */
  private static final class MellinTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr f = ast.arg1();
      final IExpr x = ast.arg2();
      final IExpr s = ast.arg3();
      if (!x.isSymbol()) {
        return F.NIL;
      }
      IExpr result = mellin(f, x, s, engine, 0);
      if (result.isNIL()) {
        result = closedIntegral(F.Integrate(F.Times(F.Power(x, F.Subtract(s, F.C1)), f),
            F.list(x, F.C0, F.CInfinity)), engine);
      }
      return result;
    }

    private static @AllowNIL IExpr mellin(IExpr f, IExpr x, IExpr s, EvalEngine engine, int depth) {
      if (depth > 20 || f.isFree(x)) {
        // a constant has no Mellin transform
        return F.NIL;
      }
      engine.checkTimeLimit();
      if (f.isPlus()) {
        IASTAppendable sum = F.PlusAlloc(f.argSize());
        for (IExpr term : (IAST) f) {
          IExpr m = mellin(term, x, s, engine, depth + 1);
          if (m.isNIL()) {
            return F.NIL;
          }
          sum.append(m);
        }
        return sum;
      }
      IExpr[] split = splitConstant(f, x);
      if (!split[0].isOne()) {
        IExpr m = mellin(split[1], x, s, engine, depth + 1);
        return m.isNIL() ? F.NIL : F.Times(split[0], m);
      }
      if (f.isTimes()) {
        IAST times = (IAST) f;
        for (int i = 1; i < times.size(); i++) {
          IExpr factor = times.get(i);
          IExpr exponent = factor.equals(x) ? F.C1
              : factor.isPower() && factor.base().equals(x) && factor.exponent().isFree(x)
                  ? factor.exponent()
                  : F.NIL;
          if (exponent.isPresent()) {
            // x^a f: the transform of f at s + a
            IExpr m = mellin(times.removeAtCopy(i).oneIdentity1(), x, s, engine, depth + 1);
            return m.isNIL() ? F.NIL : F.subst(m, s, F.Plus(s, exponent));
          }
        }
        return F.NIL;
      }
      if (f.isExp()) {
        // E^(-c x^p): Gamma(s/p)/(p c^(s/p))
        IExpr[] parts = splitConstant(f.exponent(), x);
        IExpr p = power(parts[1], x);
        if (p.isPresent()) {
          IExpr c = F.Negate(parts[0]);
          IExpr sp = F.Times(s, F.Power(p, F.CN1));
          return F.Times(F.Gamma(sp), F.Power(F.Times(p, F.Power(c, sp)), F.CN1));
        }
        return F.NIL;
      }
      if (f.isSin() || f.isCos()) {
        // Sin(a x), Cos(a x): Gamma(s) Sin(Pi s/2)/a^s, Gamma(s) Cos(Pi s/2)/a^s
        IAST c = coefficients(f.first(), x, engine);
        if (c != null && c.argSize() == 2 && c.arg1().isZero()) {
          IExpr trig = f.isSin() ? F.Sin(F.Times(F.C1D2, S.Pi, s)) : F.Cos(F.Times(F.C1D2, S.Pi, s));
          return F.Times(F.Gamma(s), trig, F.Power(c.arg2(), F.Negate(s)));
        }
        return F.NIL;
      }
      if (f.isPower() && f.exponent().isFree(x)) {
        // (c0 + c1 x^p)^-v: c0^-v (c1/c0)^(-s/p) Gamma(s/p) Gamma(v - s/p)/(p Gamma(v))
        IExpr base = f.base();
        if (base.isPlus() && base.argSize() == 2) {
          IExpr[] parts = splitConstant(base.second(), x);
          IExpr c0 = base.first();
          IExpr p = power(parts[1], x);
          if (c0.isFree(x) && p.isPresent()) {
            IExpr v = F.Negate(f.exponent());
            IExpr sp = F.Times(s, F.Power(p, F.CN1));
            IExpr gammas = v.isOne() ? F.Times(S.Pi, F.Csc(F.Times(S.Pi, sp)))
                : F.Times(F.Gamma(sp), F.Gamma(F.Subtract(v, sp)), F.Power(F.Gamma(v), F.CN1));
            return F.Times(F.Power(c0, F.Negate(v)),
                F.Power(F.Times(parts[0], F.Power(c0, F.CN1)), F.Negate(sp)), gammas,
                F.Power(p, F.CN1));
          }
        }
      }
      return F.NIL;
    }

    /** <code>p</code> if <code>e</code> is <code>x</code> or <code>x^p</code>, else NIL. */
    private static @AllowNIL IExpr power(IExpr e, IExpr x) {
      if (e.equals(x)) {
        return F.C1;
      }
      if (e.isPower() && e.base().equals(x) && e.exponent().isFree(x)) {
        return e.exponent();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * <code>InverseMellinTransform(g, s, x)</code> - the function whose Mellin transform is
   * <code>g</code>, from the reversed table: <code>Gamma(s)</code>, <code>c^-s Gamma(s)</code>,
   * <code>Gamma(s + a)</code>, <code>Gamma(s/2)</code>, <code>Pi Csc(Pi s)</code>,
   * <code>Beta(s, a - s)</code>, <code>Gamma(s) Sin(Pi s/2)</code> and
   * <code>Gamma(s) Cos(Pi s/2)</code>, with constant factors and sums.
   */
  private static final class InverseMellinTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr g = ast.arg1();
      final IExpr s = ast.arg2();
      final IExpr x = ast.arg3();
      if (!s.isSymbol()) {
        return F.NIL;
      }
      return inverse(engine.evaluate(g), s, x, engine, 0);
    }

    private static @AllowNIL IExpr inverse(IExpr g, IExpr s, IExpr x, EvalEngine engine, int depth) {
      if (depth > 20 || g.isFree(s)) {
        return F.NIL;
      }
      if (g.isPlus()) {
        IASTAppendable sum = F.PlusAlloc(g.argSize());
        for (IExpr term : (IAST) g) {
          IExpr f = inverse(term, s, x, engine, depth + 1);
          if (f.isNIL()) {
            return F.NIL;
          }
          sum.append(f);
        }
        return sum;
      }
      IExpr[] split = splitConstant(g, s);
      if (!split[0].isOne()) {
        IExpr f = inverse(split[1], s, x, engine, depth + 1);
        return f.isNIL() ? F.NIL : F.Times(split[0], f);
      }
      // the factors of the remaining product, without the factor c^-s
      IAST factors = g.isTimes() ? (IAST) g : F.list(g);
      IExpr scale = F.C1;
      IASTAppendable rest = F.TimesAlloc(factors.argSize());
      for (IExpr factor : factors) {
        if (factor.isPower() && factor.base().isFree(s)) {
          IAST c = coefficients(factor.exponent(), s, engine);
          if (c != null && c.argSize() == 2 && c.arg1().isZero() && c.arg2().isMinusOne()) {
            scale = factor.base();
            continue;
          }
        }
        rest.append(factor);
      }
      IExpr h = rest.oneIdentity1();
      IExpr f = table(h, s, x, engine);
      if (f.isNIL()) {
        return F.NIL;
      }
      // c^-s M(s) is the transform of f(c x)
      return scale.isOne() ? f : F.subst(f, x, F.Times(scale, x));
    }

    private static @AllowNIL IExpr table(IExpr h, IExpr s, IExpr x, EvalEngine engine) {
      if (h.isAST(S.Gamma, 2)) {
        IAST c = coefficients(h.first(), s, engine);
        if (c != null && c.argSize() == 2) {
          // Gamma(s/p + a): p x^(a p) E^(-x^p)
          IExpr p = F.Power(c.arg2(), F.CN1);
          IExpr a = c.arg1();
          return F.Times(p, F.Power(x, F.Times(a, p)), F.Exp(F.Negate(F.Power(x, p))));
        }
        return F.NIL;
      }
      if (h.isTimes() && h.argSize() == 2 && h.first().isAST(S.Gamma, 2)
          && h.second().isAST(S.Gamma, 2)) {
        // Gamma(s) Gamma(a - s): Gamma(a) (1 + x)^-a
        IExpr first = h.first().first();
        IExpr second = h.second().first();
        IExpr a = engine.evaluate(F.Plus(first, second));
        if (a.isFree(s) && (first.equals(s) || second.equals(s))) {
          return F.Times(F.Gamma(a), F.Power(F.Plus(F.C1, x), F.Negate(a)));
        }
      }
      if (h.isAST(S.Beta, 3)) {
        // Beta(s, a - s): (1 + x)^-a
        IExpr a = engine.evaluate(F.Plus(h.first(), h.second()));
        if (h.first().equals(s) && a.isFree(s)) {
          return F.Power(F.Plus(F.C1, x), F.Negate(a));
        }
        return F.NIL;
      }
      if (h.equals(F.Times(S.Pi, F.Csc(F.Times(S.Pi, s))))
          || h.equals(engine.evaluate(F.Times(S.Pi, F.Csc(F.Times(S.Pi, s)))))) {
        return F.Power(F.Plus(F.C1, x), F.CN1);
      }
      if (h.isAST(S.Csc, 2) && h.first().equals(engine.evaluate(F.Times(S.Pi, s)))) {
        return F.Times(F.Power(S.Pi, F.CN1), F.Power(F.Plus(F.C1, x), F.CN1));
      }
      if (h.isTimes() && h.argSize() == 2 && h.first().isAST(S.Gamma, 2)
          && h.first().first().equals(s)) {
        // Gamma(s) Sin(Pi s/2) and Gamma(s) Cos(Pi s/2): Sin(x) and Cos(x)
        IExpr trig = h.second();
        IExpr half = engine.evaluate(F.Times(F.C1D2, S.Pi, s));
        if (trig.isSin() && trig.first().equals(half)) {
          return F.Sin(x);
        }
        if (trig.isCos() && trig.first().equals(half)) {
          return F.Cos(x);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // discrete transforms

  /**
   * <code>ListZTransform({a0, a1, ...}, z)</code> - <code>Sum(a_k z^-k)</code>;
   * <code>ListZTransform(list, z, n)</code> - <code>Sum(a_k z^-(k+n))</code>.
   */
  private static final class ListZTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      if (list.isEmpty()) {
        return F.CEmptyList;
      }
      int shift = 0;
      if (ast.isAST3()) {
        shift = ast.arg3().toIntDefault();
        if (shift == Integer.MIN_VALUE) {
          return F.NIL;
        }
      }
      IASTAppendable sum = F.PlusAlloc(list.argSize());
      for (int k = 0; k < list.argSize(); k++) {
        sum.append(F.Times(list.get(k + 1), F.Power(ast.arg2(), F.ZZ(-k - shift))));
      }
      return sum;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>DiscreteConvolve(f, g, n, m)</code> - <code>Sum(f(k) g(m - k), {k, -Infinity, Infinity})</code>
   * where <code>f(k)</code> is <code>f</code> with <code>n</code> replaced by <code>k</code>. Two
   * sequences which vanish for negative <code>n</code> (a factor <code>UnitStep(n)</code>) give
   * <code>Piecewise({{Sum(f(k) g(m - k), {k, 0, m}), m >= 0}}, 0)</code>.
   */
  private static final class DiscreteConvolve extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr f = ast.arg1();
      final IExpr g = ast.arg2();
      final IExpr n = ast.arg3();
      final IExpr m = ast.arg4();
      if (!n.isSymbol()) {
        return F.NIL;
      }
      // a DiscreteDelta shifts the other sequence
      IExpr delta = deltaShift(f, n, engine);
      if (delta.isPresent()) {
        IExpr[] split = splitConstant(f, n);
        return F.Times(split[0], F.subst(g, n, F.Subtract(m, delta)));
      }
      delta = deltaShift(g, n, engine);
      if (delta.isPresent()) {
        IExpr[] split = splitConstant(g, n);
        return F.Times(split[0], F.subst(f, n, F.Subtract(m, delta)));
      }
      ISymbol k = F.Dummy("k");
      IExpr causalF = withoutUnitStep(f, n);
      IExpr causalG = withoutUnitStep(g, n);
      IExpr sum;
      if (causalF.isPresent() && causalG.isPresent()) {
        // the sum for m >= 0, and 0 for negative m
        IExpr partial = engine.evalQuiet(F.Sum(F.Times(F.subst(causalF, n, k),
            F.subst(causalG, n, F.Subtract(m, k))), F.list(k, F.C0, m)));
        if (partial.has(x -> x == S.Sum, true)) {
          return F.NIL;
        }
        IExpr expanded = engine.evalQuiet(F.Expand(partial));
        if (expanded.leafCount() < partial.leafCount()) {
          partial = expanded;
        }
        return F.Piecewise(F.list(F.list(partial, F.GreaterEqual(m, F.C0))), F.C0);
      } else {
        sum = F.Sum(F.Times(F.subst(f, n, k), F.subst(g, n, F.Subtract(m, k))),
            F.list(k, F.CNInfinity, F.CInfinity));
      }
      IExpr result = engine.evalQuiet(sum);
      return result.has(x -> x == S.Sum, true) ? F.NIL : result;
    }

    /** <code>c</code> if the sequence is a constant times <code>DiscreteDelta(n - c)</code>. */
    private static @AllowNIL IExpr deltaShift(IExpr f, IExpr n, EvalEngine engine) {
      IExpr rest = splitConstant(f, n)[1];
      if (rest.isAST(S.DiscreteDelta, 2)) {
        IAST c = coefficients(rest.first(), n, engine);
        if (c != null && c.argSize() == 2 && c.arg2().isOne()) {
          return F.Negate(c.arg1());
        }
      }
      return F.NIL;
    }

    /** The sequence without its factor <code>UnitStep(n)</code>, or NIL if it has none. */
    private static @AllowNIL IExpr withoutUnitStep(IExpr f, IExpr n) {
      if (f.isAST(S.UnitStep, 2) && f.first().equals(n)) {
        return F.C1;
      }
      if (f.isTimes()) {
        IAST times = (IAST) f;
        for (int i = 1; i < times.size(); i++) {
          if (times.get(i).isAST(S.UnitStep, 2) && times.get(i).first().equals(n)) {
            return times.removeAtCopy(i).oneIdentity1();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // fractional derivatives

  /**
   * <code>CaputoD(f, {x, a})</code> - the Caputo fractional derivative of order <code>a</code>;
   * <code>FractionalD(f, {x, a})</code> - the Riemann-Liouville fractional derivative. Both act
   * term by term: <code>x^p</code> gives <code>Gamma(p+1)/Gamma(p+1-a) x^(p-a)</code> -
   * <code>CaputoD</code> of a power with a nonnegative integer exponent below
   * <code>Ceiling(a)</code> is <code>0</code> - and <code>E^(c x)</code> gives a
   * <code>MittagLefflerE</code> function. An integer order is the ordinary derivative.
   */
  private static final class FractionalDerivative extends AbstractFunctionEvaluator {

    private final boolean caputo;

    FractionalDerivative(boolean caputo) {
      this.caputo = caputo;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList2() || !ast.arg2().first().isSymbol()) {
        return F.NIL;
      }
      final IExpr x = ast.arg2().first();
      final IExpr a = ast.arg2().second();
      if (a.isInteger() && !a.isNegative()) {
        return F.D(ast.arg1(), F.list(x, a));
      }
      if (caputo && !(a.isReal() && a.isPositive())) {
        return F.NIL;
      }
      return derivative(ast.arg1(), x, a, engine, 0);
    }

    private @AllowNIL IExpr derivative(IExpr f, IExpr x, IExpr a, EvalEngine engine, int depth) {
      if (depth > 20) {
        return F.NIL;
      }
      engine.checkTimeLimit();
      if (f.isPlus()) {
        IASTAppendable sum = F.PlusAlloc(f.argSize());
        for (IExpr term : (IAST) f) {
          IExpr d = derivative(term, x, a, engine, depth + 1);
          if (d.isNIL()) {
            return F.NIL;
          }
          sum.append(d);
        }
        return sum;
      }
      if (f.isFree(x)) {
        // a constant is x^0
        return caputo ? F.C0 : F.Times(f, power(F.C0, x, a));
      }
      IExpr[] split = splitConstant(f, x);
      if (!split[0].isOne()) {
        IExpr d = derivative(split[1], x, a, engine, depth + 1);
        return d.isNIL() ? F.NIL : F.Times(split[0], d);
      }
      if (f.equals(x)) {
        return powerTerm(F.C1, x, a);
      }
      if (f.isPower() && f.base().equals(x) && f.exponent().isNumber()) {
        return powerTerm(f.exponent(), x, a);
      }
      if (f.isExp()) {
        IAST c = coefficients(f.exponent(), x, engine);
        if (c != null && c.argSize() == 2 && c.arg1().isZero()) {
          // E^(c x): c^n x^(n-a) MittagLefflerE(1, n-a+1, c x), n = Ceiling(a) for CaputoD and
          // 0 for FractionalD
          IExpr cx = F.Times(c.arg2(), x);
          IExpr n = caputo ? F.Ceiling(a) : F.C0;
          IExpr order = F.Subtract(n, a);
          return F.Times(F.Power(c.arg2(), n), F.Power(x, order),
              F.ternaryAST3(S.MittagLefflerE, F.C1, F.Plus(order, F.C1), cx));
        }
      }
      return F.NIL;
    }

    /** The derivative of <code>x^p</code>. */
    private IExpr powerTerm(IExpr p, IExpr x, IExpr a) {
      if (caputo && p.isInteger() && !p.isNegative()) {
        IExpr ceiling = EvalEngine.get().evaluate(F.Ceiling(a));
        if (p.lessThan(ceiling).isTrue()) {
          return F.C0;
        }
      }
      return power(p, x, a);
    }

    /** <code>Gamma(p+1)/Gamma(p+1-a) x^(p-a)</code>. */
    private static IExpr power(IExpr p, IExpr x, IExpr a) {
      return F.Times(F.Gamma(F.Plus(p, F.C1)), F.Power(F.Gamma(F.Subtract(F.Plus(p, F.C1), a)),
          F.CN1), F.Power(x, F.Subtract(p, a)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private IntegralTransformFunctions() {}
}
