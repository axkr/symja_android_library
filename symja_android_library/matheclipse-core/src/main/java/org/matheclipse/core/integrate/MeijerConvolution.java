package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.ResidueIntegration.Context;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>Integrate(C*x^alpha*K1(x)*K2(x), {x,0,Infinity})</code> for two kernels, each of which is
 * a Meijer G-function of <code>u = x^2</code>:
 *
 * <pre>
 * BesselJ(nu,b*x)  =             G[{{},{}},{{nu/2},{-nu/2}}](b^2*u/4)
 * BesselY(nu,b*x)  =             G[{{},{-(nu+1)/2}},{{-nu/2,nu/2},{-(nu+1)/2}}](b^2*u/4)
 * BesselK(nu,b*x)  = 1/2       * G[{{},{}},{{nu/2,-nu/2},{}}](b^2*u/4)
 * E^(-p*x)         = 1/Sqrt(Pi)* G[{{},{}},{{0,1/2},{}}](p^2*u/4)
 * E^(-p*x^2)       =             G[{{},{}},{{0},{}}](p*u)
 * Cos(b*x)         = Sqrt(Pi)  * G[{{},{}},{{0},{1/2}}](b^2*u/4)
 * Sin(b*x)         = Sqrt(Pi)  * G[{{},{}},{{1/2},{0}}](b^2*u/4)
 * (a+x^2)^(-r)     = a^(-r)/Gamma(r) * G[{{1-r},{}},{{0},{}}](u/a)
 * </pre>
 *
 * With <code>x^alpha*dx = u^(s-1)*du/2</code>, <code>s = (alpha+1)/2</code>, the power is
 * absorbed into the first kernel (<code>u^c*G(w*u|a;b) = w^(-c)*G(w*u|a+c;b+c)</code>), and the
 * integral of the product of two G-functions over <code>(0,Infinity)</code> is one G-function of
 * the quotient of their arguments. Its reduction to named functions is the one of
 * <code>MeijerG</code>.
 *
 * <p>
 * The conditions of convergence at both ends come from the behaviour of the kernels: the power of
 * <code>x</code> at the origin, and at infinity the decay type (exponential, oscillating with an
 * amplitude <code>x^e</code>, or algebraic <code>x^e</code>). Every frequency has to be positive,
 * because the representations above are the ones of <code>Abs(b)</code>; the algebraic kernel
 * needs <code>Re(a) &gt; 0</code>, which keeps real poles on the path out.
 */
final class MeijerConvolution {

  private static final int EXPONENTIAL = 0;
  private static final int OSCILLATING = 1;
  private static final int ALGEBRAIC = 2;

  /** The time the simplification of one condition may take. */
  private static final long SIMPLIFY_MILLIS = 2000L;

  private MeijerConvolution() {}

  /** The value, the conditions of convergence, and whether no kernel decays exponentially. */
  static final class Result {
    final IExpr value;
    final List<IExpr> gates;
    final boolean oscillating;

    Result(IExpr value, List<IExpr> gates, boolean oscillating) {
      this.value = value;
      this.gates = gates;
      this.oscillating = oscillating;
    }
  }

  /** A kernel <code>coefficient*G[{an,ap},{bm,bq}](w*u)</code>. */
  private static final class Kernel {
    final IExpr coefficient;
    final IExpr[] an;
    final IExpr[] ap;
    final IExpr[] bm;
    final IExpr[] bq;
    final IExpr w;
    /** the power of x at the origin */
    final IExpr atZero;
    /** the power of x of the amplitude at infinity */
    final IExpr atInfinity;
    final int type;
    /** the frequency or rate, which has to be positive; NIL for the algebraic kernel */
    final IExpr frequency;
    final List<IExpr> side = new ArrayList<IExpr>();

    Kernel(IExpr coefficient, IExpr[] an, IExpr[] ap, IExpr[] bm, IExpr[] bq, IExpr w,
        IExpr atZero, IExpr atInfinity, int type, IExpr frequency) {
      this.coefficient = coefficient;
      this.an = an;
      this.ap = ap;
      this.bm = bm;
      this.bq = bq;
      this.w = w;
      this.atZero = atZero;
      this.atInfinity = atInfinity;
      this.type = type;
      this.frequency = frequency;
    }
  }

  private static IExpr[] list(IExpr... values) {
    return values;
  }

  /**
   * The integral of <code>ctx.f</code> over <code>(0,Infinity)</code>, or <code>null</code> if the
   * integrand is not a product of two kernels or a condition of convergence is refuted.
   */
  static Result integrate(Context ctx) {
    final IExpr x = ctx.x;
    IExpr f = ctx.f;
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IASTAppendable constant = F.TimesAlloc(4);
    IExpr alpha = F.C0;
    List<Kernel> kernels = new ArrayList<Kernel>();
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        constant.append(factor);
      } else if (factor.equals(x)) {
        alpha = F.Plus(alpha, F.C1);
      } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isFree(x)) {
        alpha = F.Plus(alpha, factor.exponent());
      } else if (factor.isPower() && factor.base().isE()) {
        IExpr expanded = ctx.eval(F.Expand(factor.exponent()));
        IAST terms = expanded.isPlus() ? (IAST) expanded : F.Plus(expanded);
        for (int j = 1; j <= terms.argSize(); j++) {
          IExpr term = terms.get(j);
          if (term.isFree(x)) {
            constant.append(F.Exp(term));
            continue;
          }
          Kernel k = exponential(ctx, term);
          if (k == null) {
            return null;
          }
          kernels.add(k);
        }
      } else if (factor.isPower() && factor.exponent().equals(F.C2)
          && kernel(ctx, factor.base()) != null) {
        Kernel k = kernel(ctx, factor.base());
        kernels.add(k);
        kernels.add(k);
      } else {
        Kernel k = kernel(ctx, factor);
        if (k == null) {
          return null;
        }
        kernels.add(k);
      }
      if (kernels.size() > 2) {
        return null;
      }
    }
    if (kernels.size() != 2) {
      return null;
    }
    alpha = ctx.eval(alpha);
    Kernel k1 = kernels.get(0);
    Kernel k2 = kernels.get(1);
    List<IExpr> gates = new ArrayList<IExpr>();
    boolean exponential = false;
    for (Kernel k : kernels) {
      if (k.frequency.isPresent()) {
        gates.add(F.Greater(k.frequency, F.C0));
      }
      gates.addAll(k.side);
      exponential |= k.type == EXPONENTIAL;
    }
    // at the origin
    gates.add(F.Greater(F.Plus(alpha, k1.atZero, k2.atZero), F.CN1));
    // at infinity
    if (!exponential) {
      IExpr amplitude = F.Plus(alpha, k1.atInfinity, k2.atInfinity);
      if (k1.type == OSCILLATING && k2.type == OSCILLATING) {
        IExpr difference =
            ctx.eval(F.Subtract(F.Sqr(k1.frequency), F.Sqr(k2.frequency)));
        if (difference.isZero()) {
          // the product has a part which does not oscillate
          gates.add(F.Less(amplitude, F.CN1));
        } else {
          gates.add(F.Less(amplitude, F.C0));
          if (!difference.isNumber()) {
            gates.add(F.Unequal(k1.frequency, k2.frequency));
          }
        }
      } else if (k1.type == OSCILLATING || k2.type == OSCILLATING) {
        gates.add(F.Less(amplitude, F.C0));
      } else {
        gates.add(F.Less(amplitude, F.CN1));
      }
    }
    List<IExpr> open = new ArrayList<IExpr>();
    for (IExpr gate : gates) {
      IExpr value = decide(ctx, gate);
      if (value.isFalse()) {
        return null;
      }
      if (!value.isTrue()) {
        open.add(gate);
      }
    }
    IExpr s = ctx.eval(F.Times(F.C1D2, F.Plus(alpha, F.C1)));
    IExpr factor = ctx.eval(F.Times(F.C1D2, constant.oneIdentity1()));
    IExpr first = convolution(ctx, factor, s, k1, k2);
    IExpr second = convolution(ctx, factor, s, k2, k1);
    IExpr value = better(first, second);
    if (value.isNIL()) {
      return null;
    }
    return new Result(value, open, !exponential);
  }

  /** the reduced value with fewer MeijerG, then the smaller one */
  private static IExpr better(IExpr a, IExpr b) {
    if (a.isNIL()) {
      return b;
    }
    if (b.isNIL()) {
      return a;
    }
    boolean ga = !a.isFree(S.MeijerG);
    boolean gb = !b.isFree(S.MeijerG);
    if (ga != gb) {
      return ga ? b : a;
    }
    return b.leafCount() < a.leafCount() ? b : a;
  }

  /**
   * <code>True</code>, <code>False</code> or the condition itself: refined under the assumptions
   * a few times, then simplified.
   */
  private static IExpr decide(Context ctx, IExpr condition) {
    IExpr value = ctx.eval(condition);
    for (int i = 0; i < 4 && !value.isTrue() && !value.isFalse(); i++) {
      IExpr refined = ctx.eval(F.Refine(value));
      if (refined.equals(value)) {
        break;
      }
      value = refined;
    }
    if (!value.isTrue() && !value.isFalse() && ctx.assumptions.isPresent()) {
      final IExpr open = value;
      IExpr simplified = IntegrateTimeBudget.runWithin(
          () -> ctx.eval(F.Simplify(open, F.Rule(S.Assumptions, ctx.assumptions))),
          MachineProfile.millis(SIMPLIFY_MILLIS));
      if (simplified.isTrue() || simplified.isFalse()) {
        value = simplified;
      }
    }
    if (!value.isTrue() && !value.isFalse() && ctx.proves(condition)) {
      return S.True;
    }
    return value;
  }

  private static IExpr convolution(Context ctx, IExpr factor, IExpr s, Kernel k1, Kernel k2) {
    IExpr shift = ctx.eval(F.Subtract(s, F.C1));
    IASTAppendable upperFirst = F.ListAlloc(4);
    IASTAppendable upperRest = F.ListAlloc(4);
    IASTAppendable lowerFirst = F.ListAlloc(4);
    IASTAppendable lowerRest = F.ListAlloc(4);
    for (IExpr b : k1.bm) {
      upperFirst.append(ctx.eval(F.Negate(F.Plus(b, shift))));
    }
    for (IExpr c : k2.an) {
      upperFirst.append(c);
    }
    for (IExpr b : k1.bq) {
      upperRest.append(ctx.eval(F.Negate(F.Plus(b, shift))));
    }
    for (IExpr c : k2.ap) {
      upperRest.append(c);
    }
    for (IExpr a : k1.an) {
      lowerFirst.append(ctx.eval(F.Negate(F.Plus(a, shift))));
    }
    for (IExpr d : k2.bm) {
      lowerFirst.append(d);
    }
    for (IExpr a : k1.ap) {
      lowerRest.append(ctx.eval(F.Negate(F.Plus(a, shift))));
    }
    for (IExpr d : k2.bq) {
      lowerRest.append(d);
    }
    IExpr g = F.MeijerG(F.List(upperFirst, upperRest), F.List(lowerFirst, lowerRest),
        F.Divide(k2.w, k1.w));
    IExpr value = ctx.eval(F.Times(factor, k1.coefficient, k2.coefficient,
        F.Power(k1.w, F.Negate(s)), g));
    if (!value.isFree(S.MeijerG)) {
      IExpr expanded = ctx.eval(F.FunctionExpand(value));
      if (expanded.isPresent() && expanded.isFree(S.FunctionExpand)) {
        value = expanded;
      }
    }
    if (!value.isSpecialsFree() || !value.isFree(ctx.x)) {
      return F.NIL;
    }
    value = withoutZeroParameter(ctx, value);
    value = piecewiseDecided(ctx, value);
    return powerExpanded(ctx, value);
  }

  /**
   * The pieces of a <code>Piecewise</code> in the value whose conditions the assumptions decide:
   * a proved condition selects its piece, a refuted one drops it.
   */
  private static IExpr piecewiseDecided(Context ctx, IExpr value) {
    if (value.isFree(S.Piecewise) || !ctx.assumptions.isPresent()) {
      return value;
    }
    return ctx.eval(F.subst(value, t -> {
      if (!t.isAST(S.Piecewise, 3) || !t.first().isList()) {
        return F.NIL;
      }
      IAST pieces = (IAST) t.first();
      IASTAppendable kept = F.ListAlloc(pieces.argSize());
      for (IExpr piece : pieces) {
        if (!piece.isList2()) {
          return F.NIL;
        }
        IExpr condition = piece.second();
        if (ctx.proves(condition)) {
          if (kept.argSize() == 0) {
            return piece.first();
          }
          kept.append(piece);
          break;
        }
        if (!ctx.proves(F.Not(condition))) {
          kept.append(piece);
        }
      }
      if (kept.argSize() == 0) {
        return t.second();
      }
      if (kept.argSize() == pieces.argSize()) {
        return F.NIL;
      }
      return F.Piecewise(kept, t.second());
    }));
  }

  /**
   * A regularized <code>HypergeometricPFQRegularized(a,b,z)</code> with an upper parameter 0 is
   * its first nonzero term: <code>1/Product(Gamma(b))</code>, or 0 if a lower parameter is a
   * non-positive integer (the series of the regularized function starts after that term).
   */
  private static IExpr withoutZeroParameter(Context ctx, IExpr value) {
    if (value.isFree(S.HypergeometricPFQRegularized)) {
      return value;
    }
    return ctx.eval(F.subst(value, t -> {
      if (t.isAST(S.HypergeometricPFQRegularized, 4) && t.first().isList()
          && t.second().isList() && ((IAST) t.first()).exists(IExpr::isZero)) {
        IAST lower = (IAST) t.second();
        IASTAppendable product = F.TimesAlloc(lower.argSize());
        for (IExpr b : lower) {
          if (b.isInteger() && !b.isPositive()) {
            return F.C0;
          }
          product.append(F.Gamma(b));
        }
        return F.Power(product.oneIdentity1(), F.CN1);
      }
      return F.NIL;
    }));
  }

  /**
   * <code>PowerExpand(value)</code> if every parameter is positive, the expanded form is smaller
   * and it has the same numerical value at sample points of the parameters.
   */
  private static IExpr powerExpanded(Context ctx, IExpr value) {
    for (IExpr parameter : ctx.parameters) {
      if (!value.isFree(parameter) && !ctx.proves(F.Greater(parameter, F.C0))) {
        return value;
      }
    }
    IExpr expanded = ctx.eval(F.PowerExpand(value));
    if (value.isNumericFunction() && value.leafCount() <= expanded.leafCount()) {
      // a product of roots of positive numbers, like Sqrt(Pi)/Sqrt(3*Pi), which the evaluation
      // merges again after PowerExpand: the root of its square, with its sign
      IExpr n = ctx.eval(F.N(value));
      if (n.isReal() && !n.isZero()) {
        expanded = ctx.eval(F.Times(F.Sqrt(F.Sqr(value)), n.isNegative() ? F.CN1 : F.C1));
      }
    }
    if (!expanded.isFree(S.PowerExpand) || expanded.leafCount() >= value.leafCount()) {
      return value;
    }
    IExpr[] samples = {F.QQ(7, 10), F.QQ(13, 10), F.QQ(23, 11)};
    for (int k = 0; k < 2; k++) {
      IASTAppendable rules = F.ListAlloc(ctx.parameters.size());
      int i = k;
      for (IExpr parameter : ctx.parameters) {
        rules.append(F.Rule(parameter, samples[i++ % samples.length]));
      }
      IExpr a = ctx.eval(F.N(F.subst(value, rules)));
      IExpr b = ctx.eval(F.N(F.subst(expanded, rules)));
      if (!a.isNumber() || !b.isNumber()) {
        return value;
      }
      double d = ctx.eval(F.Abs(F.Subtract(a, b))).evalfNaN();
      double scale = ctx.eval(F.Abs(a)).evalfNaN();
      if (!(d <= 1e-10 * (1.0 + scale))) {
        return value;
      }
    }
    return expanded;
  }

  /** <code>b</code> for an argument <code>b*x</code> */
  private static IExpr slope(Context ctx, IExpr argument) {
    if (argument.equals(ctx.x)) {
      return F.C1;
    }
    IExpr b = ctx.eval(F.Divide(argument, ctx.x));
    return b.isFree(ctx.x) && !b.isZero() ? b : null;
  }

  /** the kernel of a term <code>-p*x</code> or <code>-p*x^2</code> in the exponent of E */
  private static Kernel exponential(Context ctx, IExpr term) {
    final IExpr x = ctx.x;
    IExpr linear = ctx.eval(F.Divide(term, x));
    if (linear.isFree(x)) {
      IExpr p = ctx.eval(F.Negate(linear));
      return new Kernel(F.Power(S.Pi, F.CN1D2), list(), list(), list(F.C0, F.C1D2), list(),
          ctx.eval(F.Times(F.C1D4, F.Sqr(p))), F.C0, F.C0, EXPONENTIAL, p);
    }
    IExpr quadratic = ctx.eval(F.Divide(term, F.Sqr(x)));
    if (quadratic.isFree(x)) {
      IExpr p = ctx.eval(F.Negate(quadratic));
      return new Kernel(F.C1, list(), list(), list(F.C0), list(), p, F.C0, F.C0, EXPONENTIAL, p);
    }
    return null;
  }

  private static Kernel kernel(Context ctx, IExpr factor) {
    final IExpr x = ctx.x;
    if (factor.isAST2() && factor.first().isFree(x)) {
      IExpr nu = factor.first();
      IExpr b = slope(ctx, factor.second());
      if (b == null) {
        return null;
      }
      IExpr w = ctx.eval(F.Times(F.C1D4, F.Sqr(b)));
      IExpr half = ctx.eval(F.Times(F.C1D2, nu));
      IExpr minusHalf = ctx.eval(F.Times(F.CN1D2, nu));
      if (factor.isAST(S.BesselJ, 3)) {
        return new Kernel(F.C1, list(), list(), list(half), list(minusHalf), w, nu, F.CN1D2,
            OSCILLATING, b);
      }
      if (factor.isAST(S.BesselY, 3)) {
        IExpr c = ctx.eval(F.Times(F.CN1D2, F.Plus(nu, F.C1)));
        return new Kernel(F.C1, list(), list(c), list(minusHalf, half), list(c), w,
            ctx.eval(F.Negate(F.Abs(nu))), F.CN1D2, OSCILLATING, b);
      }
      if (factor.isAST(S.BesselK, 3)) {
        return new Kernel(F.C1D2, list(), list(), list(half, minusHalf), list(), w,
            ctx.eval(F.Negate(F.Abs(nu))), F.C0, EXPONENTIAL, b);
      }
      return null;
    }
    if ((factor.isCos() || factor.isSin())) {
      IExpr b = slope(ctx, factor.first());
      if (b == null) {
        return null;
      }
      IExpr w = ctx.eval(F.Times(F.C1D4, F.Sqr(b)));
      if (factor.isCos()) {
        return new Kernel(F.Sqrt(S.Pi), list(), list(), list(F.C0), list(F.C1D2), w, F.C0,
            F.C0, OSCILLATING, b);
      }
      return new Kernel(F.Sqrt(S.Pi), list(), list(), list(F.C1D2), list(F.C0), w, F.C1, F.C0,
          OSCILLATING, b);
    }
    if (factor.isPower() && factor.exponent().isFree(x)) {
      // (a+c*x^2)^(-r)
      IExpr base = factor.base();
      if (!base.isPolynomial(x) || !ctx.eval(F.Exponent(base, x)).equals(F.C2)
          || !ctx.eval(F.Coefficient(base, x, F.C1)).isZero()) {
        return null;
      }
      IExpr a = ctx.eval(F.Coefficient(base, x, F.C0));
      IExpr c = ctx.eval(F.Coefficient(base, x, F.C2));
      IExpr r = ctx.eval(F.Negate(factor.exponent()));
      if (a.isZero() || (r.isInteger() && !r.isPositive())) {
        return null;
      }
      // a^(-r)*(1+c/a*x^2)^(-r): the principal values agree for c > 0 and Re(a) > 0
      Kernel k = new Kernel(ctx.eval(F.Divide(F.Power(a, F.Negate(r)), F.Gamma(r))),
          list(ctx.eval(F.Subtract(F.C1, r))), list(), list(F.C0), list(), ctx.eval(F.Divide(c, a)),
          F.C0, ctx.eval(F.Times(F.CN2, r)), ALGEBRAIC, F.NIL);
      k.side.add(F.Greater(F.Re(a), F.C0));
      if (!c.isOne()) {
        k.side.add(F.Greater(c, F.C0));
      }
      return k;
    }
    return null;
  }
}
