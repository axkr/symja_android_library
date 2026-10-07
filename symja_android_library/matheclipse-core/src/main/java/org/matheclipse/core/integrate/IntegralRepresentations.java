package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.ResidueIntegration.Context;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Definite integrals which are the integral representation of a special function: the integrand
 * has no antiderivative in closed form, but its shape is the one of a known formula.
 *
 * <ul>
 * <li><code>{x,0,Infinity}</code>: derivatives of the Gamma function
 * (<code>x^(s-1)*E^(-p*x)*Log(x)^k</code>), <code>LerchPhi</code> and <code>HurwitzZeta</code>
 * (<code>x^(s-1)*E^(-c*x)/(1-z*E^(-x))</code>), the Laplace transform of <code>BesselJ</code>,
 * <code>BesselK</code> (<code>E^(-a*Cosh(x))*Cosh(n*x)</code> and
 * <code>x^(nu-1)*E^(-a*x^m-b/x^m)</code>), <code>AiryAi</code> (<code>Cos(c*x^3+a*x)</code>), the
 * cosine transform of a Gaussian, and the Laplace transform of a trigonometric polynomial divided
 * by <code>x</code> or <code>x^2</code>.
 * <li><code>{x,0,1}</code>: Euler's integral
 * <code>x^(a-1)*(1-x)^(b-1)*(g+h*x)^d</code>.
 * <li><code>{x,0,Pi/2}</code> and <code>{x,0,Pi}</code>: the trigonometric form of the Beta
 * function, and <code>Log(p+q*Cos(x))</code>.
 * </ul>
 *
 * <p>
 * A formula is used under the conditions of its convergence. A condition which the assumptions do
 * not prove is returned with the result in a <code>ConditionalExpression</code>. Every result is
 * compared with the numerical value of the integral at sample points of the parameters.
 */
final class IntegralRepresentations {

  /** The time the simplification of a result may take. */
  private static final long SIMPLIFY_MILLIS = 2000L;

  private IntegralRepresentations() {}

  /** A closed form and the conditions under which the integral is this closed form. */
  private static final class Answer {
    final IExpr value;
    final List<IExpr> gates = new ArrayList<IExpr>();
    /**
     * an integrand with the same integral over <code>{x,0,Infinity}</code>, for the numerical
     * reference of an oscillating integrand
     */
    IExpr reference = F.NIL;
    /** the formula holds for complex values of the parameters without a condition on them */
    boolean complexParameters = false;
    /**
     * no numerical integral confirms the value: the integrand oscillates without decay, or its
     * quadrature takes too long. The formula is a table entry which the tests confirm.
     */
    boolean tabulated = false;
    /** simplify the value with the search of <code>Simplify</code> */
    boolean simplify = false;

    Answer(IExpr value, IExpr... gates) {
      this.value = value;
      for (IExpr gate : gates) {
        this.gates.add(gate);
      }
    }
  }

  /**
   * The factors of an integrand: <code>constant*x^power*E^(Sum(c(m)*x^m)+rest)*others</code>.
   */
  private static final class Shape {
    final IASTAppendable constant = F.TimesAlloc(4);
    IExpr power = F.C0;
    /** the coefficients <code>c(m)</code> of the monomials in the exponent of <code>E</code> */
    final Map<IExpr, IExpr> exponent = new HashMap<IExpr, IExpr>();
    /** the terms in the exponent of <code>E</code> which are no monomials */
    final List<IExpr> exponentRest = new ArrayList<IExpr>();
    final List<IExpr> others = new ArrayList<IExpr>();

    static Shape of(Context ctx, IExpr f) {
      final ISymbol x = ctx.x;
      Shape shape = new Shape();
      // (Sin(b*x)/x)^2 as Sin(b*x)^2/x^2
      IExpr distributed = ctx.eval(F.subst(f, t -> {
        if (t.isPower() && t.base().isTimes() && t.exponent().isInteger()) {
          final IExpr n = t.exponent();
          return ((IAST) t.base()).map(factor -> F.Power(factor, n), 1);
        }
        return F.NIL;
      }));
      IAST factors = distributed.isTimes() ? (IAST) distributed : F.Times(distributed);
      for (int i = 1; i <= factors.argSize(); i++) {
        IExpr factor = factors.get(i);
        if (factor.isFree(x)) {
          shape.constant.append(factor);
        } else if (factor.equals(x)) {
          shape.power = F.Plus(shape.power, F.C1);
        } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isFree(x)) {
          shape.power = F.Plus(shape.power, factor.exponent());
        } else if (factor.isPower() && factor.base().isE()) {
          IExpr expanded = ctx.eval(F.Expand(factor.exponent()));
          IAST terms = expanded.isPlus() ? (IAST) expanded : F.Plus(expanded);
          for (int j = 1; j <= terms.argSize(); j++) {
            IExpr term = terms.get(j);
            IExpr[] monomial = monomial(term, x);
            if (monomial == null) {
              shape.exponentRest.add(term);
            } else if (monomial[1].isZero()) {
              shape.constant.append(F.Exp(monomial[0]));
            } else {
              IExpr old = shape.exponent.get(monomial[1]);
              shape.exponent.put(monomial[1],
                  old == null ? monomial[0] : ctx.eval(F.Plus(old, monomial[0])));
            }
          }
        } else {
          shape.others.add(factor);
        }
      }
      shape.power = ctx.eval(shape.power);
      return shape;
    }

    IExpr constant() {
      return constant.oneIdentity1();
    }

    /** Whether the exponent of <code>E</code> has monomials of exactly these degrees. */
    boolean hasExponents(IExpr... degrees) {
      if (!exponentRest.isEmpty() || exponent.size() != degrees.length) {
        return false;
      }
      for (IExpr degree : degrees) {
        if (!exponent.containsKey(degree)) {
          return false;
        }
      }
      return true;
    }

    /** <code>-c(m)</code>: the decay rate of the factor <code>E^(c(m)*x^m)</code>. */
    IExpr rate(Context ctx, IExpr degree) {
      return ctx.eval(F.Negate(exponent.get(degree)));
    }
  }

  /**
   * <code>{c, m}</code> for a term <code>c*x^m</code> with a rational number <code>m</code>, or
   * <code>null</code>.
   */
  private static IExpr[] monomial(IExpr term, ISymbol x) {
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
    IExpr degree = F.C0;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        coefficient.append(factor);
      } else if (factor.equals(x)) {
        degree = degree.plus(F.C1);
      } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isRational()) {
        degree = degree.plus(factor.exponent());
      } else {
        return null;
      }
    }
    return new IExpr[] {coefficient.oneIdentity1(), degree};
  }

  /** <code>k</code> for an argument <code>k*x</code> with an <code>x</code>-free <code>k</code>. */
  private static IExpr slope(Context ctx, IExpr argument) {
    if (!argument.isPolynomialOfMaxDegree(ctx.x, 1)
        || !ctx.eval(F.Coefficient(argument, ctx.x, F.C0)).isZero()) {
      return F.NIL;
    }
    IExpr k = ctx.eval(F.Coefficient(argument, ctx.x, F.C1));
    return k.isZero() || !k.isFree(ctx.x) ? F.NIL : k;
  }

  /**
   * <code>Integrate(f, {x, lower, upper})</code> for the integrand and the assumptions of the
   * context.
   *
   * @return {@link F#NIL} if the integrand has none of the known shapes
   */
  static IExpr integrate(Context ctx, IExpr lower, IExpr upper) {
    Answer answer = recognize(ctx, lower, upper);
    if (answer == null) {
      return F.NIL;
    }
    return finish(ctx, answer, lower, upper);
  }

  private static Answer recognize(Context ctx, IExpr lower, IExpr upper) {
    final IExpr f = ctx.f;
    final boolean halfLine = lower.isZero() && upper.isInfinity();
    final boolean wholeLine = lower.isNegativeInfinity() && upper.isInfinity();
    if (halfLine || wholeLine) {
      Shape shape = Shape.of(ctx, f);
      Answer answer = gaussianCosine(ctx, shape, wholeLine);
      if (answer != null || wholeLine) {
        return answer;
      }
      if ((answer = gammaLogarithm(ctx, shape)) != null
          || (answer = lerch(ctx, shape)) != null
          || (answer = laplaceBessel(ctx, shape)) != null
          || (answer = besselKCosh(ctx, shape)) != null
          || (answer = besselKReciprocal(ctx, shape)) != null
          || (answer = airy(ctx, shape)) != null
          || (answer = laplaceTrigonometric(ctx, shape)) != null
          || (answer = mellin(ctx, shape)) != null) {
        return answer;
      }
      return null;
    }
    if (!lower.isZero()) {
      return null;
    }
    if (upper.isOne()) {
      // without a parameter the antiderivative may be found, and its value is elementary
      return ctx.parameters.isEmpty() ? null : euler(ctx, f);
    }
    final boolean quarter = upper.equals(F.CPiHalf);
    if (quarter || upper.equals(S.Pi) || upper.equals(F.C2Pi)) {
      Answer answer = upper.equals(F.C2Pi) ? null : trigonometricBeta(ctx, f, quarter);
      if (answer == null && !quarter) {
        answer = logarithmOfCosine(ctx, f, upper.equals(F.C2Pi));
      }
      return answer;
    }
    return null;
  }

  /**
   * The conditions which the assumptions do not prove are returned with the value; the value is
   * compared with the numerical integral at sample points which satisfy all of the conditions.
   */
  private static IExpr finish(Context ctx, Answer answer, IExpr lower, IExpr upper) {
    List<IExpr> open = new ArrayList<IExpr>();
    for (IExpr gate : answer.gates) {
      IExpr value = ctx.eval(gate);
      if (value.isFalse()) {
        return F.NIL;
      }
      if (!value.isTrue() && !ctx.proves(gate)) {
        open.add(value);
      }
    }
    if (!ctx.assumptions.isPresent() && !answer.complexParameters) {
      // nothing is assumed: the formulas are the ones for real parameters
      for (IExpr parameter : ctx.parameters) {
        boolean stated = false;
        for (IExpr gate : open) {
          stated |= !gate.isFree(parameter);
        }
        if (!stated) {
          open.add(F.Element(parameter, S.Reals));
        }
      }
    }
    IExpr assumptions = ctx.assumptions;
    if (!open.isEmpty()) {
      IASTAppendable and = F.ast(S.And, open.size() + 1);
      if (assumptions.isPresent()) {
        and.append(assumptions);
      }
      and.appendAll(open);
      assumptions = and.oneIdentity1();
    }
    IExpr integrand = answer.reference.orElse(ctx.f);
    Context checked = ctx;
    if (!open.isEmpty() || answer.reference.isPresent()) {
      checked = new Context(integrand, ctx.x, assumptions, false, ctx.engine);
      checked.parameters.addAll(ctx.parameters);
    }
    if (!answer.tabulated && checked.samples.isEmpty()
        && !ResidueIntegration.samplePoints(checked)) {
      return F.NIL;
    }
    IExpr value = ctx.eval(answer.value);
    if (assumptions.isPresent()) {
      IExpr refined = ctx.eval(F.Refine(value, assumptions));
      if (refined.isFree(S.Refine)) {
        value = refined;
      }
    }
    value = withoutAbs(checked, value);
    if (answer.simplify) {
      checked.simplifyDeadline = System.currentTimeMillis() + SIMPLIFY_MILLIS;
      IExpr simplified = ResidueIntegration.bounded(checked, assumptions.isPresent() //
          ? F.Simplify(value, F.Rule(S.Assumptions, assumptions)) //
          : F.Simplify(value));
      if (simplified.isPresent() && simplified.leafCount() < value.leafCount()
          && (simplified.isFree(S.ArcTanh) || !value.isFree(S.ArcTanh))) {
        value = withoutAbs(checked, simplified);
      }
    }
    if (!answer.tabulated && allParametersPositive(checked, value)) {
      // Sqrt(a*b) and Log(a^2) of positive parameters
      // positive parameters do not make a difference of them positive: the expanded form is
      // used only if it is confirmed
      IExpr expanded = ctx.eval(F.PowerExpand(value));
      if (expanded.isFree(S.PowerExpand) && !expanded.equals(value)
          && expanded.leafCount() <= value.leafCount() && expanded.isSpecialsFree()
          && ResidueIntegration.check(checked, expanded, lower, upper)) {
        return conditional(expanded, open);
      }
    }
    if (!value.isSpecialsFree() || !value.isFree(ctx.x)
        || !(answer.tabulated || ResidueIntegration.check(checked, value, lower, upper))) {
      return F.NIL;
    }
    return conditional(value, open);
  }

  private static IExpr conditional(IExpr value, List<IExpr> open) {
    if (open.isEmpty()) {
      return value;
    }
    IASTAppendable condition = F.ast(S.And, open.size());
    condition.appendAll(open);
    return F.ConditionalExpression(value, condition.oneIdentity1());
  }

  /** <code>Abs(e)</code> as <code>e</code> or <code>-e</code> where the assumptions decide. */
  private static IExpr withoutAbs(Context ctx, IExpr value) {
    if (value.isFree(S.Abs) || !ctx.assumptions.isPresent()) {
      return value;
    }
    return ctx.eval(F.subst(value, t -> {
      if (t.isAbs()) {
        IExpr e = t.first();
        if (ctx.proves(F.GreaterEqual(e, F.C0))) {
          return e;
        }
        if (ctx.proves(F.LessEqual(e, F.C0))) {
          return F.Negate(e);
        }
      }
      return F.NIL;
    }));
  }

  /** Whether the value has parameters and the assumptions say that all of them are positive. */
  private static boolean allParametersPositive(Context ctx, IExpr value) {
    if (!ctx.assumptions.isPresent()) {
      return false;
    }
    boolean found = false;
    for (IExpr parameter : ctx.parameters) {
      if (!value.isFree(parameter)) {
        if (!ctx.proves(positive(parameter))) {
          return false;
        }
        found = true;
      }
    }
    return found;
  }

  /** <code>Sqrt(expr)</code>, for the square of a positive <code>u</code> as <code>u</code>. */
  private static IExpr sqrt(Context ctx, IExpr expr) {
    if (expr.isPower() && expr.exponent().equals(F.C2) && ctx.proves(positive(expr.base()))) {
      return expr.base();
    }
    return F.Sqrt(expr);
  }

  private static IExpr positive(IExpr expr) {
    return F.Greater(expr, F.C0);
  }

  // --------------------------------------------------------------------------------------------
  // {x, 0, Infinity}
  // --------------------------------------------------------------------------------------------

  /**
   * <code>Integrate(x^(s-1)*E^(-p*x)*Log(x)^k, {x,0,Infinity}) == D(Gamma(s)/p^s, {s,k})</code>
   * for <code>s &gt; 0</code> and <code>p &gt; 0</code>.
   */
  private static Answer gammaLogarithm(Context ctx, Shape shape) {
    if (shape.others.size() != 1 || !shape.hasExponents(F.C1)) {
      return null;
    }
    IExpr log = shape.others.get(0);
    int k = 1;
    if (log.isPower() && log.exponent().isInteger()) {
      k = log.exponent().toIntDefault();
      log = log.base();
    }
    if (!log.isLog() || !log.first().equals(ctx.x) || k < 1 || k > 3) {
      return null;
    }
    IExpr p = shape.rate(ctx, F.C1);
    IExpr s = ctx.eval(F.Plus(shape.power, F.C1));
    ISymbol t = F.Dummy("t");
    IExpr derivative = ctx.eval(F.D(F.Times(F.Gamma(t), F.Power(p, F.Negate(t))), F.List(t, F.ZZ(k))));
    if (!derivative.isFree(S.D)) {
      return null;
    }
    return new Answer(F.Times(shape.constant(), F.subst(derivative, t, s)), positive(s),
        positive(p));
  }

  /**
   * <code>Integrate(x^(s-1)*E^(-c*x)/(1-z*E^(-x)), {x,0,Infinity}) == Gamma(s)*LerchPhi(z,s,c)</code>
   * for <code>c &gt; 0</code>, <code>-1 &lt;= z &lt;= 1</code> and <code>s &gt; 0</code>; for
   * <code>z == 1</code> it is <code>Gamma(s)*HurwitzZeta(s,c)</code> and <code>s &gt; 1</code>.
   * The denominator is <code>g+h*E^(m*x)</code> with a positive or a negative <code>m</code>.
   */
  private static Answer lerch(Context ctx, Shape shape) {
    final ISymbol x = ctx.x;
    if (shape.others.size() != 1 || !shape.exponentRest.isEmpty() || shape.exponent.size() > 1
        || (shape.exponent.size() == 1 && !shape.exponent.containsKey(F.C1))) {
      return null;
    }
    IExpr factor = shape.others.get(0);
    if (!factor.isPower() || !factor.exponent().isMinusOne() || !factor.base().isPlus()
        || factor.base().argSize() != 2) {
      return null;
    }
    IExpr g = factor.base().first();
    IExpr exponential = factor.base().second();
    if (!g.isFree(x)) {
      g = factor.base().second();
      exponential = factor.base().first();
    }
    if (!g.isFree(x)) {
      return null;
    }
    IAST factors = exponential.isTimes() ? (IAST) exponential : F.Times(exponential);
    IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
    IExpr m = F.NIL;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr part = factors.get(i);
      if (part.isFree(x)) {
        coefficient.append(part);
      } else if (m.isNIL() && part.isPower() && part.base().isE()) {
        m = slope(ctx, part.exponent());
        if (m.isNIL()) {
          return null;
        }
      } else {
        return null;
      }
    }
    if (m.isNIL()) {
      return null;
    }
    IExpr h = coefficient.oneIdentity1();
    IExpr c = shape.exponent.isEmpty() ? F.C0 : shape.rate(ctx, F.C1);
    IExpr z;
    IExpr front;
    if (ctx.proves(F.Less(m, F.C0))) {
      // 1/(g+h*E^(-mu*x)) == 1/(g*(1-z*E^(-mu*x))) with z == -h/g
      m = ctx.eval(F.Negate(m));
      z = ctx.eval(F.Negate(F.Divide(h, g)));
      front = F.Power(g, F.CN1);
    } else if (ctx.proves(positive(m))) {
      // 1/(g+h*E^(mu*x)) == E^(-mu*x)/(h*(1-z*E^(-mu*x))) with z == -g/h
      z = ctx.eval(F.Negate(F.Divide(g, h)));
      c = ctx.eval(F.Plus(c, m));
      front = F.Power(h, F.CN1);
    } else {
      return null;
    }
    IExpr s = ctx.eval(F.Plus(shape.power, F.C1));
    IExpr ratio = ctx.eval(F.Divide(c, m));
    IExpr series;
    Answer answer;
    if (z.isOne()) {
      series = ratio.isOne() ? F.Zeta(s) : F.HurwitzZeta(s, ratio);
      answer = new Answer(F.NIL, F.Greater(s, F.C1), positive(c));
    } else {
      series = ratio.isOne() ? F.Divide(F.PolyLog(s, z), z) : F.LerchPhi(z, s, ratio);
      answer = new Answer(F.NIL, positive(s), positive(c), F.LessEqual(z, F.C1),
          F.GreaterEqual(z, F.CN1));
    }
    IExpr value =
        F.Times(shape.constant(), front, F.Power(m, F.Negate(s)), F.Gamma(s), series);
    Answer result = new Answer(value);
    result.gates.addAll(answer.gates);
    return result;
  }

  /**
   * <code>Integrate(E^(-p*x)*BesselJ(nu,q*x), {x,0,Infinity}) ==
   * (Sqrt(p^2+q^2)-p)^nu/(q^nu*Sqrt(p^2+q^2))</code> for <code>p &gt; 0</code>,
   * <code>q &gt; 0</code> and <code>nu &gt; -1</code>.
   */
  private static Answer laplaceBessel(Context ctx, Shape shape) {
    if (shape.others.size() != 1 || !shape.others.get(0).isAST(S.BesselJ, 3)
        || !shape.hasExponents(F.C1) || !shape.power.isZero()) {
      return null;
    }
    IAST bessel = (IAST) shape.others.get(0);
    IExpr nu = bessel.arg1();
    IExpr q = slope(ctx, bessel.arg2());
    if (q.isNIL() || !nu.isFree(ctx.x)) {
      return null;
    }
    IExpr p = shape.rate(ctx, F.C1);
    IExpr root = F.Sqrt(F.Plus(F.Sqr(p), F.Sqr(q)));
    IExpr value = F.Times(shape.constant(), F.Power(F.Subtract(root, p), nu),
        F.Power(q, F.Negate(nu)), F.Power(root, F.CN1));
    return new Answer(value, positive(p), positive(q), F.Greater(nu, F.CN1));
  }

  /**
   * <code>Integrate(E^(-a*Cosh(x))*Cosh(n*x), {x,0,Infinity}) == BesselK(n,a)</code> for
   * <code>a &gt; 0</code>.
   */
  private static Answer besselKCosh(Context ctx, Shape shape) {
    final ISymbol x = ctx.x;
    if (!shape.exponent.isEmpty() || shape.exponentRest.size() != 1 || !shape.power.isZero()
        || shape.others.size() > 1) {
      return null;
    }
    IExpr term = shape.exponentRest.get(0);
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
    boolean cosh = false;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        coefficient.append(factor);
      } else if (!cosh && factor.isAST(S.Cosh, 2) && factor.first().equals(x)) {
        cosh = true;
      } else {
        return null;
      }
    }
    if (!cosh) {
      return null;
    }
    IExpr n = F.C0;
    if (shape.others.size() == 1) {
      IExpr other = shape.others.get(0);
      if (!other.isAST(S.Cosh, 2)) {
        return null;
      }
      n = slope(ctx, other.first());
      if (n.isNIL()) {
        return null;
      }
    }
    IExpr a = ctx.eval(F.Negate(coefficient.oneIdentity1()));
    return new Answer(F.Times(shape.constant(), F.BesselK(n, a)), positive(a));
  }

  /**
   * <code>Integrate(x^(nu-1)*E^(-a*x-b/x), {x,0,Infinity}) ==
   * 2*(b/a)^(nu/2)*BesselK(nu, 2*Sqrt(a*b))</code> for <code>a &gt; 0</code> and
   * <code>b &gt; 0</code>; with <code>x^m</code> for <code>x</code> in the exponent after the
   * substitution <code>t == x^m</code>.
   */
  private static Answer besselKReciprocal(Context ctx, Shape shape) {
    if (!shape.others.isEmpty() || !shape.exponentRest.isEmpty() || shape.exponent.size() != 2) {
      return null;
    }
    IExpr m = F.NIL;
    for (IExpr degree : shape.exponent.keySet()) {
      if (degree.isPositive()) {
        m = degree;
      }
    }
    if (m.isNIL() || !shape.exponent.containsKey(m.negate())) {
      return null;
    }
    IExpr a = shape.rate(ctx, m);
    IExpr b = shape.rate(ctx, m.negate());
    IExpr nu = ctx.eval(F.Divide(F.Plus(shape.power, F.C1), m));
    IExpr value;
    if (nu.equals(F.C1D2) || nu.equals(F.CN1D2)) {
      // BesselK(1/2, z) == Sqrt(Pi/(2*z))/E^z
      value = F.Times(shape.constant(), F.Power(m, F.CN1), F.Sqrt(S.Pi),
          F.Power(sqrt(ctx, nu.isPositive() ? a : b), F.CN1),
          F.Exp(F.Times(F.CN2, sqrt(ctx, a), sqrt(ctx, b))));
    } else {
      value = F.Times(shape.constant(), F.C2, F.Power(m, F.CN1),
          F.Power(F.Divide(b, a), F.Times(F.C1D2, nu)),
          F.BesselK(nu, F.Times(F.C2, F.Sqrt(F.Times(a, b)))));
    }
    return new Answer(value, positive(a), positive(b));
  }

  /**
   * <code>Integrate(E^(-a*x^2)*Cos(b*x), {x,0,Infinity}) ==
   * Sqrt(Pi/a)/2*E^(-b^2/(4*a))</code> for <code>a &gt; 0</code>; twice the value over the whole
   * line.
   */
  private static Answer gaussianCosine(Context ctx, Shape shape, boolean wholeLine) {
    if (shape.others.size() != 1 || !shape.others.get(0).isCos() || !shape.hasExponents(F.C2)
        || !shape.power.isZero()) {
      return null;
    }
    IExpr b = slope(ctx, shape.others.get(0).first());
    if (b.isNIL()) {
      return null;
    }
    IExpr a = shape.rate(ctx, F.C2);
    IExpr value = F.Times(shape.constant(), wholeLine ? F.C1 : F.C1D2,
        F.Sqrt(S.Pi), F.Power(sqrt(ctx, a), F.CN1),
        F.Exp(F.Times(F.CN1D4, F.Sqr(b), F.Power(a, F.CN1))));
    Answer answer = new Answer(value, positive(a));
    // an entire function of b
    answer.complexParameters = true;
    return answer;
  }

  /**
   * <code>Integrate(Cos(c*x^3+a*x), {x,0,Infinity}) == Pi/k*AiryAi(a/k)</code> with
   * <code>k == (3*c)^(1/3)</code>, for <code>c &gt; 0</code>.
   *
   * <p>
   * The numerical reference is the same integral along the ray <code>x == s*E^(I*Pi/6)</code>,
   * where the integrand decays.
   */
  private static Answer airy(Context ctx, Shape shape) {
    final ISymbol x = ctx.x;
    if (shape.others.size() != 1 || !shape.others.get(0).isCos() || !shape.exponent.isEmpty()
        || !shape.exponentRest.isEmpty() || !shape.power.isZero()) {
      return null;
    }
    IExpr argument = shape.others.get(0).first();
    if (!argument.isPolynomialOfMaxDegree(x, 3)) {
      return null;
    }
    IExpr c = ctx.eval(F.Coefficient(argument, x, F.C3));
    IExpr a = ctx.eval(F.Coefficient(argument, x, F.C1));
    if (c.isZero() || a.isZero() || !ctx.eval(F.Coefficient(argument, x, F.C2)).isZero()
        || !ctx.eval(F.Coefficient(argument, x, F.C0)).isZero()) {
      return null;
    }
    IExpr k = F.Power(F.Times(F.C3, c), F.C1D3);
    Answer answer = new Answer(
        F.Times(shape.constant(), S.Pi, F.Power(k, F.CN1), F.AiryAi(F.Divide(a, k))), positive(c));
    // Re(E^(I*Pi/6)*E^(-c*s^3+I*a*s*E^(I*Pi/6)))
    IExpr angle = F.Times(F.C1D2, F.Sqrt(F.C3), a, x);
    answer.reference = F.Times(shape.constant(),
        F.Exp(F.Plus(F.Times(F.CN1, c, F.Power(x, F.C3)), F.Times(F.CN1D2, a, x))),
        F.Subtract(F.Times(F.C1D2, F.Sqrt(F.C3), F.Cos(angle)), F.Times(F.C1D2, F.Sin(angle))));
    return answer;
  }

  /**
   * The Laplace transform of a trigonometric polynomial <code>T(x)</code> divided by
   * <code>x</code> or <code>x^2</code>, where the division leaves no pole at <code>0</code>.
   * With <code>T(x) == Sum(c(j)*Cos(k(j)*x)) + Sum(d(j)*Sin(l(j)*x))</code> and
   * <code>a &gt; 0</code>:
   *
   * <pre>
   * Integrate(E^(-a*x)*T(x)/x, {x,0,Infinity})
   *     == Sum(-c(j)/2*Log(a^2+k(j)^2)) + Sum(d(j)*ArcTan(l(j)/a))        if Sum(c(j)) == 0
   * Integrate(E^(-a*x)*T(x)/x^2, {x,0,Infinity})
   *     == Sum(c(j)*(a/2*Log(a^2+k(j)^2)-k(j)*ArcTan(k(j)/a)))
   *        - Sum(d(j)*(a*ArcTan(l(j)/a)+l(j)/2*Log(a^2+l(j)^2)))
   *                                           if Sum(c(j)) == 0 and Sum(d(j)*l(j)) == 0
   * </pre>
   *
   * which follow from the transform of <code>T</code> by integrating once or twice over
   * <code>a</code>, the constants fixed by the decay for <code>a -&gt; Infinity</code>.
   */
  private static Answer laplaceTrigonometric(Context ctx, Shape shape) {
    final ISymbol x = ctx.x;
    final boolean square = shape.power.equals(F.CN2);
    if (shape.others.isEmpty() || !shape.hasExponents(F.C1)
        || !(square || shape.power.isMinusOne())) {
      return null;
    }
    IASTAppendable factors = F.TimesAlloc(shape.others.size());
    factors.appendAll(shape.others);
    final IExpr product = factors.oneIdentity1();
    if (!product.isFree(t -> t.isAST() && !t.isFree(x) && !t.isPlus() && !t.isTimes() && !t.isSin()
        && !t.isCos() && !(t.isPower() && t.exponent().isInteger() && t.exponent().isPositive()),
        true) || product.isFree(t -> t.isSin() || t.isCos(), true)) {
      return null;
    }
    IExpr reduced = ctx.eval(F.Expand(F.TrigReduce(F.Expand(product))));
    IAST terms = reduced.isPlus() ? (IAST) reduced : F.Plus(reduced);
    final IExpr a = shape.rate(ctx, F.C1);
    IASTAppendable sum = F.PlusAlloc(terms.argSize());
    IASTAppendable cosines = F.PlusAlloc(terms.argSize());
    IASTAppendable sines = F.PlusAlloc(terms.argSize());
    for (int i = 1; i <= terms.argSize(); i++) {
      IExpr term = terms.get(i);
      IAST termFactors = term.isTimes() ? (IAST) term : F.Times(term);
      IASTAppendable coefficient = F.TimesAlloc(termFactors.argSize());
      IExpr trig = F.NIL;
      for (int j = 1; j <= termFactors.argSize(); j++) {
        IExpr factor = termFactors.get(j);
        if (factor.isFree(x)) {
          coefficient.append(factor);
        } else if (trig.isNIL() && (factor.isSin() || factor.isCos())) {
          trig = factor;
        } else {
          return null;
        }
      }
      IExpr c = coefficient.oneIdentity1();
      IExpr k = trig.isNIL() ? F.C0 : slope(ctx, trig.first());
      if (k.isNIL()) {
        return null;
      }
      IExpr log = F.Log(F.Plus(F.Sqr(a), F.Sqr(k)));
      IExpr arcTan = F.ArcTan(F.Divide(k, a));
      if (trig.isPresent() && trig.isSin()) {
        sines.append(F.Times(c, k));
        sum.append(square //
            ? F.Times(F.CN1, c, F.Plus(F.Times(a, arcTan), F.Times(F.C1D2, k, log)))
            : F.Times(c, arcTan));
      } else {
        cosines.append(c);
        sum.append(square //
            ? F.Times(c, F.Subtract(F.Times(F.C1D2, a, log), F.Times(k, arcTan)))
            : F.Times(F.CN1D2, c, log));
      }
    }
    if (!ctx.eval(F.Expand(cosines)).isZero() || (square && !ctx.eval(F.Expand(sines)).isZero())) {
      return null;
    }
    Answer answer = new Answer(F.Times(shape.constant(), sum), positive(a));
    answer.simplify = true;
    return answer;
  }

  // --------------------------------------------------------------------------------------------
  // Mellin transforms
  // --------------------------------------------------------------------------------------------

  /** The transform of <code>f(x)</code> at <code>s</code> with the strip in which it exists. */
  private static Answer transform(IExpr value, boolean tabulated, IExpr... gates) {
    Answer answer = new Answer(value, gates);
    answer.tabulated = tabulated;
    return answer;
  }

  private static IExpr gamma(IExpr... halves) {
    // Gamma(Sum(halves)/2)
    return F.Gamma(F.Times(F.C1D2, F.Plus(halves)));
  }

  /**
   * <code>Integrate(c*x^(s-1)*f(k*x), {x,0,Infinity}) == c*k^(-s)*M(s)</code> for the functions
   * <code>f</code> whose Mellin transform <code>M</code> is a quotient of Gamma functions: the
   * Bessel, Struve and Airy functions, the sine and cosine integrals,
   * <code>ExpIntegralE</code>, <code>EllipticK</code>, <code>Hypergeometric2F1</code>,
   * <code>Csch</code> and <code>Sech</code>, and the products <code>BesselJ*BesselJ</code>,
   * <code>BesselK*BesselK</code>. The strip of <code>s</code> in which the integral converges is
   * the condition of the result.
   */
  private static Answer mellin(Context ctx, Shape shape) {
    if (shape.others.isEmpty() || shape.others.size() > 2 || !shape.exponentRest.isEmpty()) {
      return null;
    }
    final IExpr s = ctx.eval(F.Plus(shape.power, F.C1));
    final IExpr f = shape.others.get(0);
    if (!f.isAST()) {
      return null;
    }
    if (ctx.parameters.isEmpty() && s.isInteger()
        && (f.isSin() || f.isCos() || f.isPower() || f.isAST(S.Csch, 2) || f.isAST(S.Sech, 2))) {
      // elementary, with an elementary value which is found without the table
      return null;
    }
    if (!shape.exponent.isEmpty()) {
      if (shape.others.size() != 1) {
        return null;
      }
      return f.isSin() || f.isCos() ? dampedTrigonometric(ctx, shape, s)
          : dampedBesselJ(ctx, shape, s);
    }
    if (shape.others.size() == 2) {
      IExpr g = shape.others.get(1);
      if (!f.isAST2() || !g.isAST2() || !f.head().equals(g.head())) {
        return null;
      }
      IExpr k = slope(ctx, f.second());
      if (k.isNIL() || !slope(ctx, g.second()).equals(k)) {
        return null;
      }
      return scaled(shape, productTransform((IAST) f, f.first(), g.first(), s), k, F.C1, s);
    }
    // f(k*x^m): the substitution u == k*x^m gives the transform of f at s/m, divided by Abs(m)
    IExpr argument;
    int kind = SINGLE;
    if (f.isPower()) {
      if (isReciprocal(f, S.Sinh) || isReciprocal(f, S.Cosh)) {
        argument = f.base().first();
      } else if (f.exponent().equals(F.C2) && f.base().isAST(S.ArcTan, 2)) {
        argument = f.base().first();
        kind = ARCTAN_SQUARED;
      } else if ((f.base().isSin() || f.base().isCos()) && f.exponent().isInteger()) {
        argument = f.base().first();
        kind = TRIGONOMETRIC_POWER;
      } else {
        return null;
      }
    } else if (f.isLog()) {
      // Log(1+k*x^m)
      argument = ctx.eval(F.Expand(F.Subtract(f.first(), F.C1)));
      kind = LOG_ONE_PLUS;
    } else if (f.isPlus()) {
      // Coth(u)-1 and 1-Tanh(u)
      IExpr coth = ctx.eval(F.Plus(f, F.C1));
      IExpr tanh = ctx.eval(F.Subtract(F.C1, f));
      if (coth.isAST(S.Coth, 2)) {
        argument = coth.first();
        kind = COTH_MINUS_ONE;
      } else if (tanh.isAST(S.Tanh, 2)) {
        argument = tanh.first();
        kind = ONE_MINUS_TANH;
      } else {
        return null;
      }
    } else {
      argument = ((IAST) f).last();
    }
    if (f.isAST(S.EllipticK, 2) || f.isAST(S.Hypergeometric2F1, 5)) {
      // these are transformed as functions of -x
      argument = F.Negate(argument);
    }
    IExpr[] monomial = monomial(ctx.eval(argument), ctx.x);
    if (monomial == null || monomial[1].isZero() || monomial[0].isZero()) {
      return null;
    }
    final IExpr k = ctx.eval(monomial[0]);
    final IExpr m = monomial[1];
    final IExpr sm = ctx.eval(F.Divide(s, m));
    Answer transform;
    switch (kind) {
      case ARCTAN_SQUARED:
        transform = transform(
            F.Times(S.Pi, F.Power(F.Times(F.C2, sm), F.CN1), F.Csc(F.Times(F.C1D2, S.Pi, sm)),
                F.Subtract(F.PolyGamma(F.C0, F.Times(F.C1D2, F.Subtract(F.C1, sm))),
                    F.PolyGamma(F.C0, F.C1D2))),
            false, F.Greater(sm, F.CN2), F.Less(sm, F.C0));
        break;
      case TRIGONOMETRIC_POWER:
        transform = trigonometricPower(ctx, f, sm);
        break;
      case LOG_ONE_PLUS:
        transform = transform(F.Times(S.Pi, F.Power(sm, F.CN1), F.Csc(F.Times(S.Pi, sm))), false,
            F.Greater(sm, F.CN1), F.Less(sm, F.C0));
        break;
      case COTH_MINUS_ONE:
        transform = transform(
            F.Times(F.Power(F.C2, F.Subtract(F.C1, sm)), F.Gamma(sm), F.Zeta(sm)), false,
            F.Greater(sm, F.C1));
        break;
      case ONE_MINUS_TANH:
        transform = transform(F.Times(F.Power(F.C2, F.Subtract(F.C1, sm)), F.Gamma(sm),
            F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, sm))), F.Zeta(sm)), false,
            positive(sm), F.Unequal(sm, F.C1));
        break;
      default:
        transform = singleTransform(f, sm);
        break;
    }
    Answer answer = scaled(shape, transform, k, m, sm);
    if (answer != null && kind == COTH_MINUS_ONE) {
      // the same function without the difference of two numbers which are both 1 for large x
      answer.reference = F.Times(shape.constant(), F.Power(ctx.x, shape.power), F.C2,
          F.Power(F.Subtract(F.Exp(F.Times(F.C2, argument)), F.C1), F.CN1));
    }
    return answer;
  }

  private static final int SINGLE = 0;
  private static final int ARCTAN_SQUARED = 1;
  private static final int TRIGONOMETRIC_POWER = 2;
  private static final int LOG_ONE_PLUS = 3;
  private static final int COTH_MINUS_ONE = 4;
  private static final int ONE_MINUS_TANH = 5;

  /**
   * <code>constant*k^(-s)*M(s)/Abs(m)</code> for the transform <code>M</code> at
   * <code>s</code>, which is the exponent divided by <code>m</code> already.
   */
  private static Answer scaled(Shape shape, Answer transform, IExpr k, IExpr m, IExpr s) {
    if (transform == null) {
      return null;
    }
    Answer answer = new Answer(F.Times(shape.constant(), F.Power(F.Abs(m), F.CN1),
        F.Power(k, F.Negate(s)), transform.value));
    answer.gates.add(positive(k));
    answer.gates.addAll(transform.gates);
    answer.tabulated = transform.tabulated;
    return answer;
  }

  /**
   * The transform of <code>Sin(x)^n</code> or <code>Cos(x)^n</code>, <code>n &gt;= 2</code>, at
   * <code>s</code>: the power is a sum of sines or cosines of multiples of <code>x</code>, and
   * its constant term has no transform. The strip comes from the function itself: it is
   * <code>x^n</code> or <code>1</code> at the origin and oscillates around its mean.
   */
  private static Answer trigonometricPower(Context ctx, IExpr f, IExpr s) {
    final int n = f.exponent().toIntDefault();
    final boolean sine = f.base().isSin();
    if (n < 2 || n > 12 || (!sine && (n & 1) == 0)) {
      // an even power of the cosine is 1 at the origin and has a mean: no strip
      return null;
    }
    final ISymbol x = ctx.x;
    IExpr reduced =
        ctx.eval(F.Expand(F.TrigReduce(F.Power(sine ? F.Sin(x) : F.Cos(x), F.ZZ(n)))));
    IAST terms = reduced.isPlus() ? (IAST) reduced : F.Plus(reduced);
    IASTAppendable sum = F.PlusAlloc(terms.argSize());
    final IExpr half = F.Times(F.C1D2, S.Pi, s);
    for (int i = 1; i <= terms.argSize(); i++) {
      IExpr term = terms.get(i);
      if (term.isFree(x)) {
        continue;
      }
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
      IExpr wave = F.NIL;
      for (int j = 1; j <= factors.argSize(); j++) {
        IExpr factor = factors.get(j);
        if (factor.isFree(x)) {
          coefficient.append(factor);
        } else if (wave.isNIL() && (factor.isSin() || factor.isCos())) {
          wave = factor;
        } else {
          return null;
        }
      }
      if (wave.isNIL()) {
        return null;
      }
      IExpr frequency = slope(ctx, wave.first());
      if (frequency.isNIL() || !frequency.isPositive()) {
        return null;
      }
      sum.append(F.Times(coefficient.oneIdentity1(), F.Power(frequency, F.Negate(s)),
          wave.isSin() ? F.Sin(half) : F.Cos(half)));
    }
    if (sum.argSize() == 0) {
      return null;
    }
    IExpr lower = sine ? F.ZZ(-n) : F.C0;
    IExpr upper = (n & 1) == 0 ? F.C0 : F.C1;
    return transform(F.Times(F.Gamma(s), sum), true, F.Greater(s, lower), F.Less(s, upper));
  }

  /**
   * <code>Integrate(x^(s-1)*E^(-a*x)*Sin(b*x), {x,0,Infinity})</code> and the same with the
   * cosine: <code>Gamma(s)*(a^2+b^2)^(-s/2)</code> times the sine or cosine of
   * <code>s*ArcTan(b/a)</code>, for <code>a &gt; 0</code> and <code>s &gt; -1</code> or
   * <code>s &gt; 0</code>.
   */
  private static Answer dampedTrigonometric(Context ctx, Shape shape, IExpr s) {
    IExpr f = shape.others.get(0);
    if (!shape.hasExponents(F.C1)) {
      return null;
    }
    IExpr b = slope(ctx, f.first());
    if (b.isNIL()) {
      return null;
    }
    IExpr a = shape.rate(ctx, F.C1);
    IExpr angle = F.Times(s, F.ArcTan(F.Divide(b, a)));
    IExpr value = F.Times(shape.constant(), F.Gamma(s),
        F.Power(F.Plus(F.Sqr(a), F.Sqr(b)), F.Times(F.CN1D2, s)),
        f.isSin() ? F.Sin(angle) : F.Cos(angle));
    return new Answer(value, positive(a), f.isSin() ? F.Greater(s, F.CN1) : positive(s));
  }

  private static Answer singleTransform(IExpr f, IExpr s) {
    final IExpr pi = S.Pi;
    final IExpr half = F.Times(F.C1D2, pi, s);
    if (f.isSin()) {
      return transform(F.Times(F.Gamma(s), F.Sin(half)), true, F.Greater(s, F.CN1),
          F.Less(s, F.C1));
    }
    if (f.isCos()) {
      return transform(F.Times(F.Gamma(s), F.Cos(half)), true, positive(s), F.Less(s, F.C1));
    }
    if (f.isAST(S.ArcTan, 2)) {
      return transform(F.Times(F.CN1D2, pi, F.Power(s, F.CN1), F.Sec(half)), false,
          F.Greater(s, F.CN1), F.Less(s, F.C0));
    }
    if (f.isAST(S.ArcCot, 2)) {
      return transform(F.Times(F.C1D2, pi, F.Power(s, F.CN1), F.Sec(half)), false, positive(s),
          F.Less(s, F.C1));
    }
    if (f.isAST(S.SinIntegral, 2)) {
      return transform(F.Times(F.CN1, F.Gamma(s), F.Sin(half), F.Power(s, F.CN1)), true,
          F.Greater(s, F.CN1D2), F.Less(s, F.C0));
    }
    if (f.isAST(S.CosIntegral, 2)) {
      return transform(F.Times(F.CN1, F.Gamma(s), F.Cos(half), F.Power(s, F.CN1)), true,
          positive(s), F.Less(s, F.C1));
    }
    if (f.isAST(S.AiryAi, 2)) {
      IExpr third = F.Times(F.C1D3, F.Plus(s, F.C2));
      return transform(F.Times(F.Gamma(s), F.Power(F.C3, F.Negate(third)),
          F.Power(F.Gamma(third), F.CN1)), true, positive(s));
    }
    if (f.isAST(S.Csch, 2) || isReciprocal(f, S.Sinh)) {
      return transform(F.Times(F.C2, F.Gamma(s), F.Subtract(F.C1, F.Power(F.C2, F.Negate(s))),
          F.Zeta(s)), false, F.Greater(s, F.C1));
    }
    if (f.isAST(S.Sech, 2) || isReciprocal(f, S.Cosh)) {
      return transform(F.Times(F.Power(F.C2, F.Subtract(F.C1, F.Times(F.C2, s))), F.Gamma(s),
          F.Subtract(F.HurwitzZeta(s, F.C1D4), F.HurwitzZeta(s, F.QQ(3, 4)))), false, positive(s));
    }
    if (f.isAST(S.EllipticK, 2)) {
      return transform(F.Times(F.C1D2, F.Gamma(s), F.Sqr(F.Gamma(F.Subtract(F.C1D2, s))),
          F.Power(F.Gamma(F.Subtract(F.C1, s)), F.CN1)), true, positive(s), F.Less(s, F.C1D2));
    }
    if (f.isAST(S.Hypergeometric2F1, 5)) {
      // of the argument -x
      IAST h = (IAST) f;
      IExpr a = h.arg1();
      IExpr b = h.arg2();
      IExpr c = h.arg3();
      Answer answer = transform(
          F.Times(F.Gamma(c), F.Gamma(s), F.Gamma(F.Subtract(a, s)), F.Gamma(F.Subtract(b, s)),
              F.Power(F.Times(F.Gamma(a), F.Gamma(b), F.Gamma(F.Subtract(c, s))), F.CN1)),
          true, positive(s), F.Less(s, a), F.Less(s, b));
      return answer;
    }
    if (!f.isAST2()) {
      return null;
    }
    final IExpr nu = f.first();
    if (f.isAST(S.BesselK, 3)) {
      return transform(
          F.Times(F.Power(F.C2, F.Subtract(s, F.C2)), gamma(s, F.Negate(nu)), gamma(s, nu)),
          false, positive(F.Subtract(s, nu)), positive(F.Plus(s, nu)));
    }
    if (f.isAST(S.ExpIntegralE, 3)) {
      IExpr shifted = F.Plus(s, nu, F.CN1);
      return transform(F.Times(F.Gamma(s), F.Power(shifted, F.CN1)), true, positive(s),
          positive(shifted));
    }
    if (f.isAST(S.BesselJ, 3)) {
      return transform(
          F.Times(F.Power(F.C2, F.Subtract(s, F.C1)), gamma(nu, s),
              F.Power(gamma(F.C2, nu, F.Negate(s)), F.CN1)),
          true, positive(F.Plus(s, nu)), F.Less(s, F.QQ(3, 2)));
    }
    if (f.isAST(S.BesselY, 3)) {
      return transform(
          F.Times(F.CN1, F.Power(F.C2, F.Subtract(s, F.C1)), F.Power(pi, F.CN1), gamma(s, nu),
              gamma(s, F.Negate(nu)), F.Cos(F.Times(F.C1D2, pi, F.Subtract(s, nu)))),
          true, positive(F.Subtract(s, nu)), positive(F.Plus(s, nu)), F.Less(s, F.QQ(3, 2)));
    }
    if (f.isAST(S.StruveH, 3)) {
      return transform(
          F.Times(F.Power(F.C2, F.Subtract(s, F.C1)), gamma(s, nu),
              F.Tan(F.Times(F.C1D2, pi, F.Plus(s, nu))),
              F.Power(gamma(F.C2, nu, F.Negate(s)), F.CN1)),
          true, positive(F.Plus(s, nu)), F.Less(F.Plus(s, nu), F.C1), F.Less(s, F.QQ(3, 2)));
    }
    return null;
  }

  /** Whether <code>f</code> is <code>1/head(u)</code>. */
  private static boolean isReciprocal(IExpr f, ISymbol head) {
    return f.isPower() && f.exponent().isMinusOne() && f.base().isAST(head, 2);
  }

  /** The transform of <code>BesselJ(mu,x)*BesselJ(nu,x)</code> or of the product with K. */
  private static Answer productTransform(IAST f, IExpr mu, IExpr nu, IExpr s) {
    IExpr minusS = F.Negate(s);
    if (f.isAST(S.BesselJ, 3)) {
      return transform(
          F.Times(F.Power(F.C2, F.Subtract(s, F.C1)), F.Gamma(F.Subtract(F.C1, s)),
              gamma(mu, nu, s),
              F.Power(F.Times(gamma(F.C2, mu, F.Negate(nu), minusS),
                  gamma(F.C2, nu, F.Negate(mu), minusS), gamma(F.C2, mu, nu, minusS)), F.CN1)),
          true, positive(F.Plus(s, mu, nu)), F.Less(s, F.C1),
          F.Greater(F.Plus(mu, nu, F.Times(F.C2, s)), F.CN1));
    }
    if (f.isAST(S.BesselK, 3)) {
      return transform(
          F.Times(F.Power(F.C2, F.Subtract(s, F.C3)), gamma(s, mu, nu), gamma(s, mu, F.Negate(nu)),
              gamma(s, F.Negate(mu), nu), gamma(s, F.Negate(mu), F.Negate(nu)),
              F.Power(F.Gamma(s), F.CN1)),
          false, positive(F.Plus(s, mu, nu)), positive(F.Plus(s, mu, F.Negate(nu))),
          positive(F.Plus(s, F.Negate(mu), nu)), positive(F.Subtract(s, F.Plus(mu, nu))));
    }
    return null;
  }

  /**
   * <code>Integrate(x^(s-1)*E^(-a*x)*BesselJ(nu,b*x), {x,0,Infinity})</code> and the same with
   * <code>E^(-a*x^2)</code>: a <code>Hypergeometric2F1</code> of <code>-b^2/a^2</code> and a
   * <code>Hypergeometric1F1</code> of <code>-b^2/(4*a)</code>, for <code>a &gt; 0</code>,
   * <code>b &gt; 0</code> and <code>s+nu &gt; 0</code>.
   */
  private static Answer dampedBesselJ(Context ctx, Shape shape, IExpr s) {
    IExpr f = shape.others.get(0);
    if (!f.isAST(S.BesselJ, 3) || shape.exponent.size() != 1) {
      return null;
    }
    IExpr nu = f.first();
    IExpr b = slope(ctx, f.second());
    if (b.isNIL()) {
      return null;
    }
    IExpr sum = F.Plus(s, nu);
    IExpr value;
    IExpr a;
    if (shape.hasExponents(F.C1)) {
      a = shape.rate(ctx, F.C1);
      value = F.Times(F.Power(F.Times(F.C1D2, b), nu), F.Gamma(sum),
          F.Power(F.Times(F.Power(a, sum), F.Gamma(F.Plus(nu, F.C1))), F.CN1),
          F.Hypergeometric2F1(F.Times(F.C1D2, sum), F.Times(F.C1D2, F.Plus(sum, F.C1)),
              F.Plus(nu, F.C1), F.Negate(F.Divide(F.Sqr(b), F.Sqr(a)))));
    } else if (shape.hasExponents(F.C2)) {
      a = shape.rate(ctx, F.C2);
      value = F.Times(F.Power(b, nu), gamma(s, nu),
          F.Power(F.Times(F.Power(F.C2, F.Plus(nu, F.C1)), F.Power(a, F.Times(F.C1D2, sum)),
              F.Gamma(F.Plus(nu, F.C1))), F.CN1),
          F.Hypergeometric1F1(F.Times(F.C1D2, sum), F.Plus(nu, F.C1),
              F.Negate(F.Divide(F.Sqr(b), F.Times(F.C4, a)))));
    } else {
      return null;
    }
    Answer answer =
        new Answer(F.Times(shape.constant(), value), positive(a), positive(b), positive(sum));
    // the quadrature of the Bessel function at the sample points takes seconds
    answer.tabulated = true;
    return answer;
  }

  // --------------------------------------------------------------------------------------------
  // finite ranges
  // --------------------------------------------------------------------------------------------

  /**
   * Euler's integral <code>Integrate(x^(a-1)*(1-x)^(b-1)*(g+h*x)^d, {x,0,1}) ==
   * g^d*Beta(a,b)*Hypergeometric2F1(-d, a, a+b, -h/g)</code> for <code>a &gt; 0</code>,
   * <code>b &gt; 0</code>, <code>g &gt; 0</code> and <code>g+h &gt; 0</code>; for
   * <code>d == -a-b</code> the hypergeometric function is <code>(1+h/g)^(-a)</code>.
   */
  private static Answer euler(Context ctx, IExpr f) {
    final ISymbol x = ctx.x;
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IASTAppendable constant = F.TimesAlloc(factors.argSize());
    IExpr a = F.C1;
    IExpr b = F.C1;
    IExpr g = F.NIL;
    IExpr h = F.NIL;
    IExpr d = F.NIL;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        constant.append(factor);
        continue;
      }
      IExpr base = factor.isPower() ? factor.base() : factor;
      IExpr exponent = factor.isPower() ? factor.exponent() : F.C1;
      if (!exponent.isFree(x) || !base.isPolynomialOfMaxDegree(x, 1)) {
        return null;
      }
      IExpr c0 = ctx.eval(F.Coefficient(base, x, F.C0));
      IExpr c1 = ctx.eval(F.Coefficient(base, x, F.C1));
      if (c0.isZero() && c1.isOne()) {
        a = F.Plus(a, exponent);
      } else if (c0.isOne() && c1.isMinusOne()) {
        b = F.Plus(b, exponent);
      } else if (d.isNIL()) {
        g = c0;
        h = c1;
        d = exponent;
      } else {
        return null;
      }
    }
    a = ctx.eval(a);
    b = ctx.eval(b);
    if (d.isNIL() || (a.isInteger() && b.isInteger() && d.isInteger())) {
      // a Beta function, or a rational function: these are found without a formula
      return null;
    }
    IExpr sum = F.Plus(a, b);
    IExpr ratio = F.Divide(h, g);
    IExpr hypergeometric = ctx.eval(F.Expand(F.Plus(d, sum))).isZero() //
        ? F.Power(F.Plus(F.C1, ratio), F.Negate(a)) //
        : F.Hypergeometric2F1(F.Negate(d), a, sum, F.Negate(ratio));
    IExpr value =
        F.Times(constant.oneIdentity1(), F.Power(g, d), F.Beta(a, b), hypergeometric);
    return new Answer(value, positive(a), positive(b), positive(g), positive(F.Plus(g, h)));
  }

  /**
   * <code>Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/2}) == Beta((1+a)/2, (1+b)/2)/2</code> for
   * <code>a &gt; -1</code> and <code>b &gt; -1</code>; without the cosine twice the value over
   * <code>{x,0,Pi}</code>.
   */
  private static Answer trigonometricBeta(Context ctx, IExpr f, boolean quarter) {
    final ISymbol x = ctx.x;
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IASTAppendable constant = F.TimesAlloc(factors.argSize());
    IExpr a = F.C0;
    IExpr b = F.C0;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        constant.append(factor);
        continue;
      }
      IExpr base = factor.isPower() ? factor.base() : factor;
      IExpr exponent = factor.isPower() ? factor.exponent() : F.C1;
      if (!exponent.isFree(x) || !base.isAST1() || !base.first().equals(x)) {
        return null;
      }
      if (base.isSin()) {
        a = F.Plus(a, exponent);
      } else if (base.isCos()) {
        b = F.Plus(b, exponent);
      } else if (base.isTan()) {
        a = F.Plus(a, exponent);
        b = F.Subtract(b, exponent);
      } else if (base.isAST(S.Cot, 2)) {
        a = F.Subtract(a, exponent);
        b = F.Plus(b, exponent);
      } else if (base.isAST(S.Csc, 2)) {
        a = F.Subtract(a, exponent);
      } else if (base.isAST(S.Sec, 2)) {
        b = F.Subtract(b, exponent);
      } else {
        return null;
      }
    }
    a = ctx.eval(a);
    b = ctx.eval(b);
    if ((a.isInteger() && b.isInteger()) || (!quarter && !b.isZero())) {
      return null;
    }
    IExpr beta = F.Beta(F.Times(F.C1D2, F.Plus(F.C1, a)), F.Times(F.C1D2, F.Plus(F.C1, b)));
    IExpr value = F.Times(constant.oneIdentity1(), quarter ? F.C1D2 : F.C1, beta);
    return new Answer(value, F.Greater(a, F.CN1), F.Greater(b, F.CN1));
  }

  /**
   * <code>Integrate(Log(p+q*Cos(x)), {x,0,Pi}) == Pi*Log((p+Sqrt(p^2-q^2))/2)</code> for
   * <code>p &gt;= Abs(q)</code>, <code>p &gt; 0</code>; twice the value over <code>{x,0,2*Pi}</code>.
   */
  private static Answer logarithmOfCosine(Context ctx, IExpr f, boolean period) {
    final ISymbol x = ctx.x;
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IASTAppendable constant = F.TimesAlloc(factors.argSize());
    IExpr argument = F.NIL;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        constant.append(factor);
      } else if (argument.isNIL() && factor.isLog()) {
        argument = factor.first();
      } else {
        return null;
      }
    }
    if (argument.isNIL()) {
      return null;
    }
    ISymbol cos = F.Dummy("c");
    IExpr polynomial = F.subst(ctx.eval(F.Expand(argument)), F.Cos(x), cos);
    if (!polynomial.isFree(x) || !polynomial.isPolynomialOfMaxDegree(cos, 1)) {
      return null;
    }
    IExpr p = ctx.eval(F.Coefficient(polynomial, cos, F.C0));
    IExpr q = ctx.eval(F.Coefficient(polynomial, cos, F.C1));
    if (q.isZero()) {
      return null;
    }
    IExpr root = F.Sqrt(F.Expand(F.Subtract(F.Sqr(p), F.Sqr(q))));
    IExpr value = F.Times(constant.oneIdentity1(), period ? F.C2 : F.C1, S.Pi,
        F.Log(F.Times(F.C1D2, F.Plus(p, root))));
    // at p == Abs(q) the logarithm has an integrable singularity, and the formula holds
    Answer answer = new Answer(value, positive(p),
        F.GreaterEqual(ctx.eval(F.Factor(F.Subtract(p, q))), F.C0),
        F.GreaterEqual(ctx.eval(F.Factor(F.Plus(p, q))), F.C0));
    answer.simplify = true;
    return answer;
  }
}
