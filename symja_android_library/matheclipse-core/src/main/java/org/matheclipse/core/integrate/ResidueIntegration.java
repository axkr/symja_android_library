package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Definite integrals by the residue theorem and its relatives, for integrands which have no
 * antiderivative in closed form, or one whose limits at the ends of the range are not found.
 * <ul>
 * <li><b>Fourier</b>: <code>R(x)*{Cos, Sin, Exp}(k*x)</code> over the real line, closed in the
 * half plane where the exponential decays;
 * <li><b>keyhole</b>: <code>R(x)*Log(x)^k</code> and <code>x^p*R(x)*Log(x)^k</code> over
 * <code>(0, Infinity)</code>, around the cut of the logarithm;
 * <li><b>unit circle</b>: <code>R(Cos(t), Sin(t))</code> over a period;
 * <li><b>strip</b>: <code>P(x, E^x)/Cosh(b*x)</code> and <code>P(x, E^x)/Sinh(b*x)</code> over the
 * real line, from <code>Integrate(E^(u*x)/Cosh(b*x)) == Pi/b*Sec(u*Pi/(2*b))</code>;
 * <li><b>Beta</b>: <code>x^p/(d+e*x^n)^q</code> over <code>(0, Infinity)</code>.
 * </ul>
 * <code>R</code> is a rational function. Every family declines ({@link F#NIL}) unless the
 * integrand is of its form and the conditions for convergence are proved under the assumptions.
 * <p>
 * The position of a pole relative to the contour is read at sample points of the parameters which
 * satisfy the assumptions; the poles have to lie the same way at every sample point. Before a
 * result is returned it is compared with the numerical value of the integral at a sample point.
 */
public final class ResidueIntegration {

  private ResidueIntegration() {}

  /** The values the parameters of an integrand are given at the sample points. */
  private static final IExpr[] SAMPLE_VALUES =
      {F.QQ(3, 4), F.QQ(11, 5), F.QQ(1, 3), F.QQ(17, 4), F.QQ(7, 5), F.C3, F.QQ(13, 2)};

  /** The number of parameters an integrand may have. */
  private static final int MAX_PARAMETERS = 4;

  /**
   * The number of sample points for one combination of signs of the parameters. One point does
   * not show that a pole stays on its side of the contour: the poles of 1/(x^2-c*x+1) are complex
   * for 0 &lt; c &lt; 2 and on the positive axis for c &gt; 2.
   */
  private static final int SAMPLES_PER_SIGNS = 3;

  /** The number of points which are tried for one combination of signs of the parameters. */
  private static final int SAMPLE_ATTEMPTS = 21;

  /** The number of sample points at which a result is confirmed by the numerical integral. */
  private static final int MAX_CHECKS = 3;

  private static final double EPS = 1e-9;

  /** The time the simplification of a result may take, all of its steps together. */
  private static final long SIMPLIFY_MILLIS = 6000L;

  /** The state of one call. */
  static final class Context {
    final IExpr f;
    final ISymbol x;
    final IExpr assumptions;
    final boolean principalValue;
    final EvalEngine engine;
    /** the parameters of the integrand */
    final List<IExpr> parameters = new ArrayList<IExpr>();
    /** substitution rules for the parameters which satisfy the assumptions */
    final List<IAST> samples = new ArrayList<IAST>();
    /** the family has no reliable numerical reference: an oscillating integrand */
    boolean oscillating = false;
    /** a family declined because the sample points disagree: the sign of a parameter decides */
    boolean signDependent = false;
    /** the simple poles on the path of integration which a principal value passes */
    final List<IExpr> pathPoles = new ArrayList<IExpr>();
    /** the time at which the simplification of the result has to end */
    long simplifyDeadline;

    Context(IExpr f, ISymbol x, IExpr assumptions, boolean principalValue, EvalEngine engine) {
      this.f = f;
      this.x = x;
      this.assumptions = assumptions;
      this.principalValue = principalValue;
      this.engine = engine;
    }

    IExpr eval(IExpr expr) {
      return engine.evalQuiet(expr);
    }

    /** Whether the condition holds for all values of the parameters the assumptions allow. */
    boolean proves(IExpr condition) {
      IExpr value = eval(condition);
      if (value.isTrue()) {
        return true;
      }
      if (value.isFalse() || parameters.isEmpty()) {
        return false;
      }
      value = eval(F.Refine(condition));
      if (value.isTrue()) {
        return true;
      }
      if (value.isFalse() || !assumptions.isPresent()) {
        return false;
      }
      if (isAssumed(condition)) {
        return true;
      }
      IASTAppendable variables = F.ListAlloc(parameters.size());
      variables.appendAll(parameters);
      // the statement carries its assumptions itself; those of the engine are not for the
      // quantified variables
      IAssumptions engineAssumptions = engine.getAssumptions();
      try {
        engine.setAssumptions(null);
        return eval(F.binaryAST2(S.Resolve,
            F.ForAll(variables, assumptions, condition), S.Reals)).isTrue();
      } finally {
        engine.setAssumptions(engineAssumptions);
      }
    }

    /** <code>a &lt; b &lt; c</code> as <code>a &lt; b &amp;&amp; b &lt; c</code>. */
    IExpr binaryRelations(IExpr expr) {
      return F.subst(expr, t -> {
        if (t.isAST() && t.argSize() > 2 && (t.isAST(S.Less) || t.isAST(S.LessEqual)
            || t.isAST(S.Greater) || t.isAST(S.GreaterEqual))) {
          IAST chain = (IAST) t;
          IASTAppendable and = F.ast(S.And, chain.argSize());
          for (int i = 1; i < chain.argSize(); i++) {
            and.append(F.binaryAST2(chain.head(), chain.get(i), chain.get(i + 1)));
          }
          return and;
        }
        return F.NIL;
      });
    }

    /** Whether the condition is one of the assumptions as it stands: <code>a &lt; Pi</code>. */
    boolean isAssumed(IExpr condition) {
      if (!condition.isAST2()) {
        return false;
      }
      IExpr flat = eval(F.unaryAST1(S.LogicalExpand, binaryRelations(assumptions)));
      IAST conjuncts = flat.isAnd() ? (IAST) flat : F.List(flat);
      IExpr difference = difference(condition);
      if (difference.isNIL()) {
        return false;
      }
      for (int i = 1; i <= conjuncts.argSize(); i++) {
        IExpr assumed = difference(conjuncts.get(i));
        if (assumed.isPresent() && eval(F.Expand(F.Subtract(assumed, difference))).isZero()) {
          return true;
        }
      }
      return false;
    }

    /** <code>d</code> for a relation which says <code>d &gt; 0</code>. */
    private IExpr difference(IExpr relation) {
      if (relation.isAST(S.Greater, 3)) {
        return F.Subtract(relation.first(), relation.second());
      }
      if (relation.isAST(S.Less, 3)) {
        return F.Subtract(relation.second(), relation.first());
      }
      return F.NIL;
    }

    /** The numerical value at a sample point, or <code>null</code>. */
    Complex numeric(IExpr expr, IAST sample) {
      try {
        IExpr value = engine.evalQuiet(F.N(F.subst(expr, sample)));
        if (value instanceof INumber) {
          Complex c = ((INumber) value).evalfc();
          if (Double.isFinite(c.getReal()) && Double.isFinite(c.getImaginary())) {
            return c;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      return null;
    }
  }

  /**
   * <code>Integrate(f, {x, lower, upper})</code>
   *
   * @param assumptions the assumptions of the call as an expression, or {@link F#NIL}
   * @param principalValue <code>true</code> for the Cauchy principal value: a simple pole on the
   *        positive real axis is passed on both sides
   * @return {@link F#NIL} if no family applies
   */
  public static IExpr integrate(IExpr f, IExpr x, IExpr lower, IExpr upper, IExpr assumptions,
      boolean principalValue, EvalEngine engine) {
    if (!x.isSymbol() || f.isFree(x) || !f.isAST() || ACTIVE.get()) {
      // not from inside of the numerical check of a result, which may come back here
      return F.NIL;
    }
    ACTIVE.set(Boolean.TRUE);
    try {
      Context ctx = new Context(f, (ISymbol) x, assumptions, principalValue, engine);
      IExpr result = integrate(ctx, lower, upper);
      if (result.isNIL() && ctx.signDependent) {
        result = bySignOfParameter(ctx, lower, upper);
      }
      if (result.isNIL() && !principalValue) {
        result = IntegralRepresentations.integrate(ctx, lower, upper);
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      ACTIVE.set(Boolean.FALSE);
    }
  }

  /** The integral under the assumptions of the context, which is filled on the way. */
  private static IExpr integrate(Context ctx, IExpr lower, IExpr upper) {
    final IExpr f = ctx.f;
    final ISymbol x = ctx.x;
    VariablesSet variables = new VariablesSet(F.List(f, lower, upper));
    for (IExpr variable : variables.getArrayList()) {
      if (!variable.equals(x)) {
        ctx.parameters.add(variable);
      }
    }
    final boolean wholeLine = lower.isNegativeInfinity() && upper.isInfinity();
    final boolean halfLine = lower.isZero() && upper.isInfinity();
    final boolean period = !wholeLine && !halfLine && !ctx.parameters.isEmpty()
        && lower.isFree(x) && upper.isFree(x)
        && ctx.eval(F.Subtract(F.Subtract(upper, lower), F.C2Pi)).isZero();
    if (!(wholeLine || halfLine || period) || !samplePoints(ctx)) {
      return F.NIL;
    }
    IExpr result = F.NIL;
    if (wholeLine) {
      result = wholeLine(ctx, f);
    } else if (halfLine) {
      result = keyhole(ctx);
      if (result.isNIL()) {
        result = beta(ctx);
      }
      if (result.isNIL() && isEven(ctx, f)) {
        IExpr whole = wholeLine(ctx, f);
        if (whole.isPresent()) {
          result = F.Times(F.C1D2, whole);
        }
      }
    } else {
      result = unitCircle(ctx);
    }
    if (result.isNIL()) {
      return F.NIL;
    }
    result = simplify(ctx, result);
    if (!result.isSpecialsFree() || !result.isFree(x) || !result.isFree(S.Residue)
        || !result.isFree(S.Limit)) {
      return F.NIL;
    }
    final boolean realIntegrand = !hasNonRealNumber(f);
    if ((realIntegrand && hasNonRealNumber(result))
        || (ctx.parameters.isEmpty() && result.leafCount() > 150)) {
      // a sum of residues which did not simplify to a real expression is no answer
      return F.NIL;
    }
    if (!check(ctx, result, lower, upper)) {
      return F.NIL;
    }
    return result;
  }

  /**
   * The integral where the assumptions leave the sign of one parameter <code>p</code> open, and
   * the sign decides how the contour is closed: <code>Cos(k*x)/(x^2+1)</code> for
   * <code>k != 0</code>. The cases <code>p &gt; 0</code> and <code>p &lt; 0</code> are integrated
   * apart and put together: a result which is even in <code>p</code> is written with
   * <code>Abs(p)</code>, an odd one with <code>Sign(p)</code> as well, anything else as
   * <code>Piecewise</code>.
   */
  private static IExpr bySignOfParameter(Context ctx, IExpr lower, IExpr upper) {
    for (int i = 0; i < ctx.parameters.size(); i++) {
      final IExpr p = ctx.parameters.get(i);
      boolean positive = false;
      boolean negative = false;
      for (IAST sample : ctx.samples) {
        IExpr value = sample.get(i + 1).second();
        positive |= value.isPositive();
        negative |= value.isNegative();
      }
      if (!positive || !negative) {
        continue;
      }
      IExpr[] cases = new IExpr[2];
      for (int k = 0; k < 2; k++) {
        IExpr sign = k == 0 ? F.Greater(p, F.C0) : F.Less(p, F.C0);
        Context caseContext = new Context(ctx.f, ctx.x, F.And(ctx.assumptions, sign),
            ctx.principalValue, ctx.engine);
        cases[k] = integrate(caseContext, lower, upper);
        if (cases[k].isNIL()) {
          break;
        }
      }
      if (cases[0].isNIL() || cases[1] == null || cases[1].isNIL()) {
        continue;
      }
      IExpr combined = combine(ctx, p, cases[0], cases[1], lower, upper);
      // at every sample point the combined expression is the result of its case
      for (IAST sample : ctx.samples) {
        Complex value = ctx.numeric(combined, sample);
        Complex expected =
            ctx.numeric(sample.get(i + 1).second().isPositive() ? cases[0] : cases[1], sample);
        if (value == null || expected == null
            || value.subtract(expected).norm() > 1e-9 * (1.0 + expected.norm())) {
          return F.NIL;
        }
      }
      return isStatedReal(ctx.assumptions, p) ? combined
          : F.ConditionalExpression(combined, F.Element(p, S.Reals));
    }
    return F.NIL;
  }

  private static IExpr combine(Context ctx, IExpr p, IExpr positive, IExpr negative, IExpr lower,
      IExpr upper) {
    IExpr piecewise = F.Piecewise(F.List(F.List(positive, F.Greater(p, F.C0)),
        F.List(negative, F.Less(p, F.C0))), S.Undefined);
    ctx.simplifyDeadline = System.currentTimeMillis() + SIMPLIFY_MILLIS;
    IExpr mirrored = F.subst(positive, p, F.Negate(p));
    IExpr combined;
    if (isZero(ctx, F.Subtract(negative, mirrored))) {
      combined = F.subst(positive, p, F.Abs(p));
    } else if (isZero(ctx, F.Plus(negative, mirrored))) {
      combined = F.Times(F.Sign(p), F.subst(positive, p, F.Abs(p)));
    } else {
      return piecewise;
    }
    // Abs(p)^2 is p^2
    combined = ctx.eval(F.subst(combined, t -> t.isPower() && t.base().equals(F.Abs(p))
        && t.exponent().isInteger() && ((org.matheclipse.core.interfaces.IInteger) t.exponent()).isEven()
            ? F.Power(p, t.exponent())
            : F.NIL));
    // The point p == 0 belongs to neither case. The closed form is taken if the assumptions
    // exclude the point, or if it gives the integral there.
    IAST zero = F.List(F.Rule(p, F.C0));
    if (ctx.eval(F.subst(ctx.assumptions, zero)).isFalse() || ctx.proves(F.Unequal(p, F.C0))) {
      return combined;
    }
    IExpr integrand = ctx.eval(F.subst(ctx.f, zero));
    IExpr atZero = ctx.eval(F.subst(combined, zero));
    if (integrand.isZero()) {
      return atZero.isZero() ? combined : piecewise;
    }
    // the numerical integral at p == 0, at the sample points of the other parameters
    int confirmed = 0;
    for (int i = 0; i < ctx.samples.size() && confirmed < MAX_CHECKS; i++) {
      IAST sample = ctx.samples.get(i);
      Complex value = ctx.numeric(atZero, sample);
      Complex reference = ctx.numeric(F.NIntegrate(F.subst(integrand, sample),
          F.List(ctx.x, F.subst(lower, sample), F.subst(upper, sample))), sample);
      if (value == null || reference == null
          || value.subtract(reference).norm() > 1e-6 * (1.0 + reference.norm())) {
        return piecewise;
      }
      confirmed++;
    }
    if (confirmed > 0) {
      return combined;
    }
    return piecewise;
  }

  private static boolean isZero(Context ctx, IExpr expr) {
    IExpr value = ctx.eval(F.Together(expr));
    if (value.isZero()) {
      return true;
    }
    IExpr reduced = reduceSingleRadical(ctx, value);
    if (reduced.isPresent()) {
      if (reduced.isZero()) {
        return true;
      }
      value = reduced;
    }
    return bounded(ctx, F.Simplify(value)).isZero();
  }

  /** Whether the assumptions say that <code>p</code> is a real number. */
  private static boolean isStatedReal(IExpr assumptions, IExpr p) {
    return !assumptions.isFree(t -> {
      if (t.isAST(S.Element, 3) && t.second() == S.Reals) {
        return !t.first().isFree(p);
      }
      return (t.isAST(S.Less) || t.isAST(S.LessEqual) || t.isAST(S.Greater)
          || t.isAST(S.GreaterEqual)) && !t.isFree(p);
    }, true);
  }

  private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Evaluate with this stage switched off: for an integral which was declined here already and
   * is handed on in another form.
   */
  public static IExpr withoutResidues(java.util.function.Supplier<IExpr> evaluation) {
    final boolean active = ACTIVE.get();
    ACTIVE.set(Boolean.TRUE);
    try {
      return evaluation.get();
    } finally {
      ACTIVE.set(active);
    }
  }

  private static IExpr wholeLine(Context ctx, IExpr f) {
    IExpr result = strip(ctx, f);
    if (result.isNIL() && !ctx.parameters.isEmpty()) {
      result = fourier(ctx, f);
    }
    return result;
  }

  /**
   * Whether <code>f(-x) == f(x)</code>: as expressions, or at a few points - the result is
   * compared with the numerical integral anyway.
   */
  private static boolean isEven(Context ctx, IExpr f) {
    IExpr difference = ctx.eval(F.Expand(F.Subtract(f, F.subst(f, ctx.x, F.Negate(ctx.x)))));
    if (difference.isZero()) {
      return true;
    }
    IAST sample = ctx.samples.get(0);
    for (IExpr point : SAMPLE_VALUES) {
      if (!point.isPositive()) {
        continue;
      }
      Complex value = ctx.numeric(F.subst(difference, ctx.x, point), sample);
      Complex scale = ctx.numeric(F.subst(f, ctx.x, point), sample);
      if (value == null || scale == null || value.norm() > 1e-10 * (1.0 + scale.norm())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sample points of the parameters which satisfy the assumptions: a few for every combination of
   * signs of the parameters which the assumptions allow. The place of a pole and the sign of a
   * frequency are read at these points and have to be the same at all of them, so an assumption
   * like <code>k != 0</code>, which leaves the sign of <code>k</code> open, makes the families
   * decline. Without parameters there is one sample, the empty substitution.
   */
  static boolean samplePoints(Context ctx) {
    final int size = ctx.parameters.size();
    if (size == 0) {
      ctx.samples.add(F.CEmptyList);
      return true;
    }
    if (!ctx.assumptions.isPresent() || size > MAX_PARAMETERS) {
      return false;
    }
    final int base = SAMPLE_VALUES.length;
    for (int signs = 0; signs < (1 << size); signs++) {
      int found = 0;
      for (int attempt = 0; attempt < SAMPLE_ATTEMPTS; attempt++) {
        IASTAppendable rules = F.ListAlloc(size);
        for (int i = 0; i < size; i++) {
          IExpr value = SAMPLE_VALUES[(attempt * (i + 1) + 3 * i) % base];
          rules.append(F.Rule(ctx.parameters.get(i),
              (signs & (1 << i)) == 0 ? value : value.negate()));
        }
        if (!ctx.samples.contains(rules) && ctx.eval(F.subst(ctx.assumptions, rules)).isTrue()) {
          ctx.samples.add(rules);
          if (++found == SAMPLES_PER_SIGNS) {
            break;
          }
        }
      }
    }
    return !ctx.samples.isEmpty();
  }

  private static boolean hasNonRealNumber(IExpr expr) {
    return !expr.isFree(t -> t.isNumber() && !t.isReal(), true);
  }

  /** Whether <code>ComplexExpand</code> gave an expression without <code>Re, Im, Arg</code>. */
  private static boolean isRealAndImaginaryPartFree(IExpr expr) {
    return expr.isPresent() && expr.isFree(S.Re) && expr.isFree(S.Im) && expr.isFree(S.Arg);
  }

  /** The result under the assumptions, in a readable form. */
  private static IExpr simplify(Context ctx, IExpr result) {
    ctx.simplifyDeadline = System.currentTimeMillis() + SIMPLIFY_MILLIS;
    IExpr evaluated = ctx.eval(result);
    IExpr reduced = reduceSingleRadical(ctx, evaluated);
    if (reduced.isPresent()) {
      evaluated = reduced;
    }
    if (ctx.parameters.isEmpty()) {
      // residues at roots of unity: real and imaginary part first. The search of Simplify does
      // not end on the sum of the ten residues of Log(x)/(x^5-1).
      IExpr expanded = ctx.eval(F.ComplexExpand(evaluated));
      if (isRealAndImaginaryPartFree(expanded)) {
        evaluated = expanded;
      }
    }
    IExpr simplified = bounded(ctx, F.Simplify(evaluated));
    if (simplified.isNIL()) {
      return evaluated;
    }
    if (simplified.isPresent() && (simplified.leafCount() > 30 || !simplified.isFree(S.Sqrt)
        || !simplified.isFree(t -> t.isPower() && t.exponent().isFraction(), true))) {
      // residues at poles which are radicals: the full search finds (a^2-b^2)^(5/2)
      IExpr full = bounded(ctx, F.FullSimplify(simplified));
      if (full.isPresent() && full.leafCount() < simplified.leafCount()) {
        simplified = full;
      }
    }
    if (hasNonRealNumber(simplified)) {
      // the residues are complex numbers, their sum is real
      IExpr expanded = bounded(ctx, F.Simplify(F.ComplexExpand(simplified)));
      if (isRealAndImaginaryPartFree(expanded) && expanded.leafCount() <= simplified.leafCount()) {
        simplified = expanded;
      }
    }
    if (simplified.isPresent() && !ctx.parameters.isEmpty()
        && hasNonRealNumber(simplified)) {
      // Sqrt(b^2-a^2) for a > b: the root of a negative number, written with I. In the root of
      // the positive number a^2-b^2 the I cancels against the one of the residues.
      IExpr positive = F.subst(simplified, t -> {
        if (t.isPower() && t.exponent().isFraction() && !t.base().isNumber()
            && isNegativeAtSamples(ctx, t.base())) {
          return F.Times(F.Power(F.CN1, t.exponent()),
              F.Power(ctx.eval(F.Expand(F.Negate(t.base()))), t.exponent()));
        }
        return F.NIL;
      });
      if (!positive.equals(simplified)) {
        positive = bounded(ctx, F.Simplify(positive));
        if (positive.isPresent() && positive.leafCount() <= simplified.leafCount() + 4) {
          simplified = positive;
        }
      }
    }
    if (simplified.isPresent() && hasNonRealNumber(simplified)) {
      // over a common denominator the imaginary parts of the residues cancel
      IExpr together = bounded(ctx, F.Simplify(F.Together(simplified)));
      if (together.isPresent() && !hasNonRealNumber(together)) {
        simplified = together;
      }
    }
    return simplified.isPresent() ? simplified : evaluated;
  }

  /**
   * A rational expression in the parameters and one square root <code>Sqrt(b)</code>, as the
   * residues at the roots of a quadratic are, written as <code>p+q*Sqrt(b)</code> with rational
   * <code>p</code> and <code>q</code>: over a common denominator, the powers of the root reduced
   * by <code>r^2 == b</code>, and the root removed from the denominator. This is polynomial
   * arithmetic, where the search of Simplify factors over an algebraic extension for seconds.
   *
   * @return {@link F#NIL} if the expression has no square root, or more than one
   */
  private static IExpr reduceSingleRadical(Context ctx, IExpr expr) {
    final IExpr[] base = new IExpr[1];
    final boolean[] several = new boolean[1];
    final ISymbol r = F.Dummy("r");
    IExpr replaced = F.subst(expr, t -> {
      if (t.isPower() && t.exponent().isFraction() && !t.base().isNumber()
          && ((IFraction) t.exponent()).denominator().equals(F.C2)) {
        if (base[0] == null) {
          base[0] = t.base();
        } else if (!base[0].equals(t.base())) {
          several[0] = true;
        }
        return F.Power(r, ((IFraction) t.exponent()).numerator());
      }
      return F.NIL;
    });
    if (base[0] == null || several[0]) {
      return F.NIL;
    }
    IExpr together = ctx.eval(F.Together(replaced));
    IExpr numerator = ctx.eval(F.Numerator(together));
    IExpr denominator = ctx.eval(F.Denominator(together));
    if (!numerator.isPolynomial(r) || !denominator.isPolynomial(r)) {
      return F.NIL;
    }
    IExpr modulus = F.Subtract(F.Sqr(r), base[0]);
    numerator = ctx.eval(F.PolynomialRemainder(numerator, modulus, r));
    denominator = ctx.eval(F.PolynomialRemainder(denominator, modulus, r));
    IExpr d0 = ctx.eval(F.Coefficient(denominator, r, F.C0));
    IExpr d1 = ctx.eval(F.Coefficient(denominator, r, F.C1));
    // (n0+n1*r)*(d0-d1*r)/(d0^2-d1^2*b)
    IExpr conjugate = F.Subtract(d0, F.Times(d1, r));
    numerator = ctx.eval(F.PolynomialRemainder(F.Expand(F.Times(numerator, conjugate)), modulus, r));
    IExpr norm = ctx.eval(F.Expand(F.Subtract(F.Sqr(d0), F.Times(F.Sqr(d1), base[0]))));
    if (norm.isZero() || !numerator.isPolynomial(r)) {
      return F.NIL;
    }
    IExpr p = ctx.eval(F.Factor(F.Together(F.Divide(F.Coefficient(numerator, r, F.C0), norm))));
    IExpr q = ctx.eval(F.Factor(F.Together(F.Divide(F.Coefficient(numerator, r, F.C1), norm))));
    IExpr value = ctx.eval(F.Plus(p, F.Times(q, F.Sqrt(base[0]))));
    return value.isSpecialsFree() ? value : F.NIL;
  }

  /** A simplification with a limit of time, or {@link F#NIL}. */
  static IExpr bounded(Context ctx, IAST simplification) {
    long remaining = ctx.simplifyDeadline - System.currentTimeMillis();
    if (remaining <= 0) {
      return F.NIL;
    }
    // not TimeConstrained: inside of an evaluation which has a limit of its own that is no limit
    IExpr value = IntegrateTimeBudget.runWithin(() -> ctx.eval(simplification), remaining);
    if (value == null || value.isNIL() || value.equals(S.$Aborted)
        || !value.isFree(simplification.head())) {
      return F.NIL;
    }
    return value;
  }

  private static boolean isNegativeAtSamples(Context ctx, IExpr expr) {
    for (IAST sample : ctx.samples) {
      Complex value = ctx.numeric(expr, sample);
      if (value == null || Math.abs(value.getImaginary()) > EPS || !(value.getReal() < -EPS)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The result compared with the numerical value of the integral at sample points. A sample
   * point at which the numerical integration gives no number proves nothing, but at one of them
   * at least the result has to be confirmed.
   */
  static boolean check(Context ctx, IExpr result, IExpr lower, IExpr upper) {
    int confirmed = 0;
    for (int i = 0; i < ctx.samples.size() && confirmed < MAX_CHECKS; i++) {
      IAST sample = ctx.samples.get(i);
      Complex value = ctx.numeric(result, sample);
      if (value == null) {
        return false;
      }
      IExpr f = F.subst(ctx.f, sample);
      IExpr a = F.subst(lower, sample);
      IExpr b = F.subst(upper, sample);
      double tolerance = 1e-4;
      Complex reference;
      if (ctx.principalValue && !ctx.pathPoles.isEmpty()) {
        reference = principalValue(ctx, f, a, b, sample);
        tolerance = 1e-2;
      } else {
        reference = numericIntegral(ctx, f, a, b, sample);
        if (reference == null && ctx.oscillating) {
          // An oscillating integrand over an infinite range: the part of it over a long finite
          // range. The rest is small, of the order 1/TRUNCATION for a decay like 1/x.
          IExpr from = a.isNegativeInfinity() ? F.ZZ(-TRUNCATION) : a;
          IExpr to = b.isInfinity() ? F.ZZ(TRUNCATION) : b;
          reference = numericIntegral(ctx, f, from, to, sample);
          tolerance = 3e-2;
        }
      }
      if (reference == null) {
        continue;
      }
      if (value.subtract(reference).norm() > tolerance * (1.0 + reference.norm())) {
        return false;
      }
      confirmed++;
    }
    return confirmed > 0;
  }

  /** The numerical integral; of a complex integrand by its real and its imaginary part. */
  private static Complex numericIntegral(Context ctx, IExpr f, IExpr lower, IExpr upper,
      IAST sample) {
    final Complex[] result = new Complex[1];
    // a reference which takes long is no reference: the quadrature of a Bessel function of a
    // large argument runs for minutes
    IntegrateTimeBudget.runWithin(() -> {
      result[0] = numericIntegralUnbounded(ctx, f, lower, upper, sample);
      return F.NIL;
    }, NUMERIC_MILLIS);
    return result[0];
  }

  /** The time the numerical integral at one sample point may take. */
  private static final long NUMERIC_MILLIS = 3000L;

  private static Complex numericIntegralUnbounded(Context ctx, IExpr f, IExpr lower, IExpr upper,
      IAST sample) {
    IAST range = F.List(ctx.x, lower, upper);
    Complex value = ctx.numeric(F.NIntegrate(f, range), sample);
    if (value != null || !hasNonRealNumber(f)) {
      return value;
    }
    IExpr re = ctx.eval(F.ComplexExpand(F.Re(f)));
    IExpr im = ctx.eval(F.ComplexExpand(F.Im(f)));
    if (!isRealAndImaginaryPartFree(re) || !isRealAndImaginaryPartFree(im)) {
      return null;
    }
    Complex real = ctx.numeric(F.NIntegrate(re, range), sample);
    Complex imaginary = im.isZero() ? Complex.ZERO : ctx.numeric(F.NIntegrate(im, range), sample);
    if (real == null || imaginary == null) {
      return null;
    }
    return new Complex(real.getReal(), imaginary.getReal());
  }

  /** Where the numerical reference of an oscillating integrand over an infinite range is cut. */
  private static final int TRUNCATION = 200;

  /**
   * The numerical principal value: the integral without an interval of the same small width on
   * both sides of every pole on the path.
   */
  private static Complex principalValue(Context ctx, IExpr f, IExpr lower, IExpr upper,
      IAST sample) {
    final double width = 1e-3;
    List<Double> poles = new ArrayList<Double>();
    for (IExpr pole : ctx.pathPoles) {
      Complex point = ctx.numeric(pole, sample);
      if (point == null) {
        return null;
      }
      poles.add(point.getReal());
    }
    java.util.Collections.sort(poles);
    Complex sum = Complex.ZERO;
    IExpr from = lower;
    for (int i = 0; i <= poles.size(); i++) {
      IExpr to = i < poles.size() ? F.num(poles.get(i) - width) : upper;
      Complex part = ctx.numeric(F.NIntegrate(f, F.List(ctx.x, from, to)), sample);
      if (part == null) {
        return null;
      }
      sum = sum.add(part);
      if (i < poles.size()) {
        from = F.num(poles.get(i) + width);
      }
    }
    return sum;
  }

  // --------------------------------------------------------------------------------------------
  // poles
  // --------------------------------------------------------------------------------------------

  private static final class Pole {
    final IExpr root;
    final int multiplicity;

    Pole(IExpr root, int multiplicity) {
      this.root = root;
      this.multiplicity = multiplicity;
    }
  }

  /** The roots of a polynomial <code>denominator</code> in <code>z</code>. */
  private static final class Poles extends ArrayList<Pole> {
    private static final long serialVersionUID = 1L;
    final IExpr denominator;
    final IExpr z;

    Poles(IExpr denominator, IExpr z) {
      this.denominator = denominator;
      this.z = z;
    }

    boolean hasRoot(IExpr root) {
      for (Pole pole : this) {
        if (pole.root.equals(root)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * The distinct roots of the polynomial <code>denominator</code> with their multiplicities, or
   * <code>null</code> if not all of them are found.
   */
  private static Poles roots(Context ctx, IExpr denominator, IExpr z) {
    Poles roots = new Poles(denominator, z);
    if (denominator.isFree(z)) {
      return roots;
    }
    int n = ctx.eval(F.Exponent(denominator, z)).toIntDefault();
    if (n < 1) {
      return null;
    }
    // the square free factors apart: (b*z^2+2*a*z+b)^3 is solved as a quadratic, expanded it is
    // a polynomial of degree 6 whose roots are not found
    IExpr factored = ctx.eval(F.Factor(denominator));
    IAST product = factored.isTimes() ? (IAST) factored : F.Times(factored);
    int count = 0;
    for (int k = 1; k <= product.argSize(); k++) {
      IExpr factor = product.get(k);
      int exponent = 1;
      if (factor.isPower() && factor.exponent().isInteger() && factor.exponent().isPositive()) {
        exponent = factor.exponent().toIntDefault();
        factor = factor.base();
      }
      if (factor.isFree(z)) {
        continue;
      }
      if (exponent < 1 || !factor.isPolynomial(z)) {
        return null;
      }
      IExpr solutions = ctx.eval(F.Solve(F.Equal(factor, F.C0), z));
      if (!solutions.isListOfLists()) {
        return null;
      }
      IAST list = (IAST) solutions;
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr solution = list.get(i);
        if (!solution.isList1() || !solution.first().isRuleAST()
            || !solution.first().first().equals(z)) {
          return null;
        }
        IExpr root = solution.first().second();
        if (!root.isFree(z) || !root.isFree(S.Root) || !root.isFree(S.ConditionalExpression)) {
          return null;
        }
        if (roots.hasRoot(root)) {
          continue;
        }
        // the order of the root in this factor, by its derivatives
        IExpr derivative = factor;
        int m = 0;
        for (; m <= n; m++) {
          if (!isZeroAt(ctx, derivative, z, root)) {
            break;
          }
          derivative = ctx.eval(F.D(derivative, z));
        }
        if (m == 0) {
          return null;
        }
        roots.add(new Pole(root, m * exponent));
        count += m * exponent;
      }
    }
    // every root has to be found: the sum of the multiplicities is the degree
    return count == n ? roots : null;
  }

  /** Whether the polynomial vanishes at the root: at every sample point, to rounding. */
  private static boolean isZeroAt(Context ctx, IExpr polynomial, IExpr z, IExpr root) {
    IExpr value = F.subst(polynomial, z, root);
    for (IAST sample : ctx.samples) {
      Complex c = ctx.numeric(value, sample);
      Complex scale = ctx.numeric(F.subst(polynomial, z, F.Plus(root, F.C1)), sample);
      if (c == null || scale == null || c.norm() > 1e-8 * (1.0 + scale.norm())) {
        return false;
      }
    }
    return true;
  }

  /**
   * The residue of <code>numerator/denominator</code> at the <code>index</code>-th root, where
   * the numerator is analytic at the root. For a root <code>z0</code> of multiplicity
   * <code>m</code> the denominator is <code>(z-z0)^m*Q(z)</code>, and the residue is the
   * <code>(m-1)</code>-th derivative of <code>numerator/Q</code> at <code>z0</code> by
   * <code>(m-1)!</code>. Only the first <code>m</code> Taylor coefficients of <code>Q</code> at
   * <code>z0</code> matter, and those are derivatives of the denominator: no division by a
   * polynomial whose root is a radical.
   */
  private static IExpr residue(Context ctx, IExpr numerator, Poles poles, int index) {
    final IExpr z = poles.z;
    final IExpr root = poles.get(index).root;
    final int m = poles.get(index).multiplicity;
    IASTAppendable q = F.PlusAlloc(m);
    IExpr derivative = ctx.eval(F.D(poles.denominator, F.List(z, F.ZZ(m))));
    for (int j = 0; j < m; j++) {
      // Q^(j)(z0)/j! == D^(m+j)(z0)/(m+j)!
      IExpr coefficient =
          F.Times(F.subst(derivative, z, root), F.Power(F.Factorial(F.ZZ(m + j)), F.CN1));
      q.append(j == 0 ? coefficient : F.Times(coefficient, F.Power(F.Subtract(z, root), F.ZZ(j))));
      derivative = ctx.eval(F.D(derivative, z));
    }
    IExpr function = F.Times(numerator, F.Power(q.oneIdentity0(), F.CN1));
    if (m > 1) {
      function = F.Times(F.Power(F.Factorial(F.ZZ(m - 1)), F.CN1),
          F.D(function, F.List(z, F.ZZ(m - 1))));
      function = ctx.eval(function);
    }
    IExpr value = ctx.eval(F.subst(function, z, root));
    if (value.isNIL() || !value.isSpecialsFree() || !value.isFree(z) || !value.isFree(S.D)
        || !value.isFree(S.Derivative)) {
      return F.NIL;
    }
    return value;
  }

  private interface Classifier {
    /** The class of the point, or <code>Integer.MIN_VALUE</code> if it is on a boundary. */
    int classOf(Complex point);
  }

  /**
   * The class of every root, the same at all sample points.
   *
   * @return <code>null</code> if a root has no value, lies on a boundary, or changes its class
   */
  private static int[] classify(Context ctx, Poles roots, Classifier classifier) {
    int[] classes = new int[roots.size()];
    for (int s = 0; s < ctx.samples.size(); s++) {
      for (int i = 0; i < roots.size(); i++) {
        Complex point = ctx.numeric(roots.get(i).root, ctx.samples.get(s));
        if (point == null) {
          return null;
        }
        int c = classifier.classOf(point);
        if (c == Integer.MIN_VALUE) {
          return null;
        }
        if (s > 0 && c != classes[i]) {
          ctx.signDependent = true;
          return null;
        }
        classes[i] = c;
      }
    }
    return classes;
  }

  /** <code>{numerator, denominator}</code>, both polynomials in <code>z</code>, or null. */
  private static IExpr[] rational(Context ctx, IExpr r, IExpr z) {
    IExpr together = ctx.eval(F.Together(r));
    IExpr numerator = ctx.eval(F.Numerator(together));
    IExpr denominator = ctx.eval(F.Denominator(together));
    if (!numerator.isPolynomial(z) || !denominator.isPolynomial(z)) {
      return null;
    }
    return new IExpr[] {numerator, denominator};
  }

  private static int degree(Context ctx, IExpr polynomial, IExpr z) {
    if (polynomial.isFree(z)) {
      return 0;
    }
    return ctx.eval(F.Exponent(polynomial, z)).toIntDefault();
  }

  // --------------------------------------------------------------------------------------------
  // Fourier
  // --------------------------------------------------------------------------------------------

  private static IExpr fourier(Context ctx, IExpr f) {
    final ISymbol x = ctx.x;
    if (f.isFree(t -> t.isSin() || t.isCos() || (t.isPower() && t.base().isE()), true)) {
      return F.NIL;
    }
    ctx.oscillating = true;
    IExpr expanded = ctx.eval(F.Expand(F.TrigToExp(f)));
    IAST terms = expanded.isPlus() ? (IAST) expanded : F.Plus(expanded);
    // the terms c(x)*E^(I*k*x) by the sign of k: {rational coefficient, exponential}
    List<IExpr[]> upper = new ArrayList<IExpr[]>();
    List<IExpr[]> lower = new ArrayList<IExpr[]>();
    for (int i = 1; i <= terms.argSize(); i++) {
      IAST factors = terms.get(i).isTimes() ? (IAST) terms.get(i) : F.Times(terms.get(i));
      IExpr frequency = F.C0;
      IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
      IASTAppendable exponential = F.TimesAlloc(factors.argSize());
      for (int j = 1; j <= factors.argSize(); j++) {
        IExpr factor = factors.get(j);
        if (factor.isPower() && factor.base().isE() && !factor.exponent().isFree(x)) {
          IExpr exponent = factor.exponent();
          if (!exponent.isPolynomialOfMaxDegree(x, 1)) {
            return F.NIL;
          }
          IExpr slope = ctx.eval(F.Coefficient(exponent, x, F.C1));
          frequency = ctx.eval(F.Plus(frequency, F.Times(F.CNI, slope)));
          exponential.append(factor);
        } else {
          coefficient.append(factor);
        }
      }
      IExpr[] parts = rational(ctx, coefficient.oneIdentity1(), x);
      if (parts == null) {
        return F.NIL;
      }
      // the frequency is real, and has the same sign at all sample points
      int sign = 0;
      for (IAST sample : ctx.samples) {
        Complex k = ctx.numeric(frequency, sample);
        if (k == null || Math.abs(k.getImaginary()) > EPS || Math.abs(k.getReal()) < EPS) {
          return F.NIL;
        }
        int sgn = k.getReal() > 0 ? 1 : -1;
        if (sign != 0 && sign != sgn) {
          ctx.signDependent = true;
          return F.NIL;
        }
        sign = sgn;
      }
      // Jordan's lemma
      if (degree(ctx, parts[1], x) - degree(ctx, parts[0], x) < 1) {
        return F.NIL;
      }
      (sign > 0 ? upper : lower)
          .add(new IExpr[] {parts[0], parts[1], exponential.oneIdentity1()});
    }
    IExpr sum = F.C0;
    for (int half = 1; half >= -1; half -= 2) {
      List<IExpr[]> group = half > 0 ? upper : lower;
      if (group.isEmpty()) {
        continue;
      }
      IExpr denominator = F.C1;
      for (IExpr[] term : group) {
        denominator = ctx.eval(F.binaryAST2(S.PolynomialLCM, denominator, term[1]));
      }
      Poles roots = roots(ctx, denominator, x);
      if (roots == null) {
        return F.NIL;
      }
      // the numerator over the common denominator: polynomials times exponentials
      IASTAppendable numerator = F.PlusAlloc(group.size());
      for (IExpr[] term : group) {
        IExpr cofactor = ctx.eval(F.PolynomialQuotient(denominator, term[1], x));
        numerator.append(F.Times(term[0], cofactor, term[2]));
      }
      final int h = half;
      int[] classes = classify(ctx, roots, point -> Math.abs(point.getImaginary()) < EPS ? 0
          : (point.getImaginary() * h > 0 ? 1 : -1));
      if (classes == null) {
        return F.NIL;
      }
      for (int i = 0; i < roots.size(); i++) {
        if (classes[i] < 0) {
          continue;
        }
        IExpr root = roots.get(i).root;
        IExpr residue = residue(ctx, numerator.oneIdentity0(), roots, i);
        if (residue.isNIL()) {
          return F.NIL;
        }
        if (classes[i] == 0) {
          // A pole on the path: the integrand itself has to be finite there, and the pole of
          // this half of it simple - the numerator vanishes to the order which is missing. Then
          // the indented contour takes half of the residue.
          IExpr atPole = ctx.eval(F.Limit(ctx.f, F.Rule(x, root)));
          if (!atPole.isFree(S.Limit) || !atPole.isSpecialsFree()) {
            return F.NIL;
          }
          IExpr derivative = numerator.oneIdentity0();
          for (int k = 0; k < roots.get(i).multiplicity - 1; k++) {
            if (!ctx.eval(F.Simplify(F.subst(derivative, x, root))).isZero()) {
              return F.NIL;
            }
            derivative = ctx.eval(F.D(derivative, x));
          }
          sum = F.Plus(sum, F.Times(F.ZZ(half), F.CI, S.Pi, residue));
        } else {
          sum = F.Plus(sum, F.Times(F.ZZ(2 * half), F.CI, S.Pi, residue));
        }
      }
    }
    return sum;
  }

  // --------------------------------------------------------------------------------------------
  // keyhole
  // --------------------------------------------------------------------------------------------

  /**
   * <code>Integrate(R(x)*Log(x)^k, {x,0,Infinity})</code> for <code>k = 1, 2</code>, and
   * <code>Integrate(x^p*R(x)*Log(x)^k, {x,0,Infinity})</code> for <code>k = 0, 1</code>.
   * <p>
   * With <code>L(z) == Log(-z)+I*Pi</code>, the logarithm whose cut is the positive real axis, and
   * <code>S(j)</code> the sum of the residues of <code>R*L^j</code>:
   *
   * <pre>
   * I0 == Integrate(R)          == -S(1)
   * I1 == Integrate(R*Log(x))   == -S(2)/2 - I*Pi*I0
   * I2 == Integrate(R*Log(x)^2) == -S(3)/3 - 2*Pi*I*I1 + 4/3*Pi^2*I0
   * </pre>
   *
   * and <code>Integrate(x^(s-1)*R) == Pi/Sin(Pi*s)*Sum(Res((-z)^(s-1)*R))</code>, differentiated
   * by <code>s</code> for a factor <code>Log(x)</code>.
   */
  private static IExpr keyhole(Context ctx) {
    final ISymbol x = ctx.x;
    IAST factors = ctx.f.isTimes() ? (IAST) ctx.f : F.Times(ctx.f);
    int logPower = 0;
    IExpr power = F.NIL;
    IASTAppendable rest = F.TimesAlloc(factors.argSize());
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isLog() && factor.first().equals(x)) {
        logPower += 1;
      } else if (factor.isPower() && factor.base().isLog() && factor.base().first().equals(x)
          && factor.exponent().isInteger() && factor.exponent().isPositive()) {
        logPower += factor.exponent().toIntDefault();
      } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isFree(x)
          && !factor.exponent().isInteger()) {
        if (power.isPresent()) {
          return F.NIL;
        }
        power = factor.exponent();
      } else if (factor.isSqrt() && factor.base().equals(x)) {
        power = F.C1D2;
      } else {
        rest.append(factor);
      }
    }
    if (logPower == 0 && (power.isNIL() || ctx.parameters.isEmpty()) && !ctx.principalValue) {
      // a power alone, without parameters: the antiderivative is found
      return F.NIL;
    }
    IExpr r = rest.oneIdentity1();
    if (!r.isFree(S.Log)) {
      return F.NIL;
    }
    IExpr[] parts = rational(ctx, r, x);
    if (parts == null) {
      return F.NIL;
    }
    int degree = degree(ctx, parts[1], x) - degree(ctx, parts[0], x);
    ISymbol z = F.Dummy("z");
    Poles roots = roots(ctx, F.subst(parts[1], x, z), z);
    if (roots == null) {
      return F.NIL;
    }
    IExpr nz = F.subst(parts[0], x, z);
    // 0: off the cut, 1: on the cut (the positive real axis)
    int[] classes = classify(ctx, roots, point -> point.norm() < EPS ? Integer.MIN_VALUE
        : (Math.abs(point.getImaginary()) < EPS && point.getReal() > 0 ? 1 : 0));
    if (classes == null) {
      return F.NIL;
    }
    if (power.isPresent()) {
      if (logPower > 1) {
        return F.NIL;
      }
      for (int c : classes) {
        if (c != 0) {
          return F.NIL;
        }
      }
      // convergence: 0 < s < degree, with s == power+1
      IExpr s0 = F.Plus(power, F.C1);
      if (!ctx.proves(F.Greater(s0, F.C0)) || !ctx.proves(F.Less(s0, F.ZZ(degree)))) {
        return F.NIL;
      }
      ISymbol s = F.Dummy("s");
      IExpr sum = F.C0;
      for (int i = 0; i < roots.size(); i++) {
        IExpr residue =
            residue(ctx, F.Times(F.Power(F.Negate(z), F.Subtract(s, F.C1)), nz), roots, i);
        if (residue.isNIL()) {
          return F.NIL;
        }
        sum = F.Plus(sum, residue);
      }
      IExpr mellin = F.Times(S.Pi, F.Csc(F.Times(S.Pi, s)), sum);
      if (logPower == 1) {
        mellin = ctx.eval(F.D(mellin, s));
      }
      return F.subst(mellin, s, s0);
    }

    if (logPower > 2 || degree < 2) {
      return F.NIL;
    }
    boolean onCut = false;
    // a pole on the cut is passed on both sides. It has to be the simple pole at 1, which the
    // logarithm cancels in the integrand.
    for (int i = 0; i < roots.size(); i++) {
      if (classes[i] == 1) {
        onCut = true;
        ctx.pathPoles.add(roots.get(i).root);
        if (roots.get(i).multiplicity != 1
            || !(ctx.principalValue || (logPower > 0 && roots.get(i).root.isOne()))) {
          return F.NIL;
        }
      }
    }
    IExpr logarithm = F.Plus(F.Log(F.Negate(z)), F.Times(F.CI, S.Pi));
    IExpr[] sums = new IExpr[logPower + 2];
    for (int j = 1; j <= logPower + 1; j++) {
      IExpr sum = F.C0;
      for (int i = 0; i < roots.size(); i++) {
        IExpr root = roots.get(i).root;
        IExpr residue;
        if (classes[i] == 1) {
          // the mean of the two sides of the cut: Log(x0)^j and (Log(x0)+2*Pi*I)^j
          IExpr r0 = residue(ctx, nz, roots, i);
          if (r0.isNIL()) {
            return F.NIL;
          }
          residue = F.Times(F.C1D2, r0, F.Plus(F.Power(F.Log(root), F.ZZ(j)),
              F.Power(F.Plus(F.Log(root), F.Times(F.C2, S.Pi, F.CI)), F.ZZ(j))));
        } else {
          residue = residue(ctx, F.Times(F.Power(logarithm, F.ZZ(j)), nz), roots, i);
          if (residue.isNIL()) {
            return F.NIL;
          }
        }
        sum = F.Plus(sum, residue);
      }
      sums[j] = sum;
    }
    IExpr i0 = F.Negate(sums[1]);
    if (logPower == 0) {
      // the principal value of a rational function; without a pole on the path the
      // antiderivative gives the integral
      return onCut ? i0 : F.NIL;
    }
    IExpr i1 = F.Subtract(F.Times(F.CN1D2, sums[2]), F.Times(F.CI, S.Pi, i0));
    if (logPower == 1) {
      return i1;
    }
    return F.Plus(F.Times(F.QQ(-1, 3), sums[3]), F.Times(F.CN2, S.Pi, F.CI, i1),
        F.Times(F.QQ(4, 3), F.Sqr(S.Pi), i0));
  }

  // --------------------------------------------------------------------------------------------
  // unit circle
  // --------------------------------------------------------------------------------------------

  private static IExpr unitCircle(Context ctx) {
    final ISymbol t = ctx.x;
    if (ctx.f.isFree(u -> (u.isSin() || u.isCos()) && u.first().equals(t), true)) {
      return F.NIL;
    }
    ISymbol z = F.Dummy("z");
    IExpr cos = F.Times(F.C1D2, F.Plus(z, F.Power(z, F.CN1)));
    IExpr sin = F.Times(F.CNI, F.C1D2, F.Subtract(z, F.Power(z, F.CN1)));
    IExpr substituted = F.subst(ctx.f, u -> {
      if (u.isCos() && u.first().equals(t)) {
        return cos;
      }
      if (u.isSin() && u.first().equals(t)) {
        return sin;
      }
      return F.NIL;
    });
    if (!substituted.isFree(t)) {
      return F.NIL;
    }
    IExpr g = F.Times(substituted, F.Power(F.Times(F.CI, z), F.CN1));
    IExpr[] parts = rational(ctx, g, z);
    if (parts == null) {
      return F.NIL;
    }
    Poles roots = roots(ctx, parts[1], z);
    if (roots == null) {
      return F.NIL;
    }
    int[] classes = classify(ctx, roots, point -> Math.abs(point.norm() - 1.0) < 1e-7
        ? Integer.MIN_VALUE : (point.norm() < 1.0 ? 1 : 0));
    if (classes == null) {
      return F.NIL;
    }
    IExpr sum = F.C0;
    for (int i = 0; i < roots.size(); i++) {
      if (classes[i] == 1) {
        IExpr residue = residue(ctx, parts[0], roots, i);
        if (residue.isNIL()) {
          return F.NIL;
        }
        sum = F.Plus(sum, residue);
      }
    }
    return F.Times(F.C2, S.Pi, F.CI, sum);
  }

  // --------------------------------------------------------------------------------------------
  // strip
  // --------------------------------------------------------------------------------------------

  /**
   * <code>Integrate(N(x)/Cosh(b*x), {x,-Infinity,Infinity})</code> and the same with
   * <code>Sinh(b*x)</code>, where <code>N</code> is a sum of terms <code>c*x^k*E^(u*x)</code>:
   *
   * <pre>
   * Integrate(E^(u*x)/Cosh(b*x)) == Pi/b*Sec(u*Pi/(2*b))
   * Integrate(E^(u*x)/Sinh(b*x)) == Pi/b*Tan(u*Pi/(2*b))   (principal value)
   * </pre>
   *
   * for <code>Abs(Re(u)) &lt; b</code>, and their derivatives by <code>u</code> for the powers of
   * <code>x</code>. With <code>Sinh</code> the numerator has to vanish at <code>0</code>.
   */
  private static IExpr strip(Context ctx, IExpr f) {
    final ISymbol x = ctx.x;
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IExpr argument = F.NIL;
    boolean cosh = false;
    int order = 1;
    IASTAppendable rest = F.TimesAlloc(factors.argSize());
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      IExpr head = F.NIL;
      IExpr arg = F.NIL;
      int n = 1;
      IExpr base = factor;
      if (factor.isPower() && factor.exponent().isInteger()) {
        base = factor.base();
        n = factor.exponent().toIntDefault();
      }
      if (n >= 1 && (base.isAST(S.Sech, 2) || (n == 1 && base.isAST(S.Csch, 2)))) {
        head = base.isAST(S.Sech, 2) ? S.Cosh : S.Sinh;
        arg = base.first();
      } else if (n <= -1 && (base.isAST(S.Cosh, 2) || (n == -1 && base.isAST(S.Sinh, 2)))) {
        head = base.head();
        arg = base.first();
        n = -n;
      }
      if (head.isPresent() && argument.isNIL() && !arg.isFree(x) && n <= MAX_STRIP_ORDER) {
        argument = arg;
        cosh = head == S.Cosh;
        order = n;
      } else {
        rest.append(factor);
      }
    }
    if (argument.isNIL() || !argument.isPolynomialOfMaxDegree(x, 1)
        || !ctx.eval(F.Coefficient(argument, x, F.C0)).isZero()) {
      return F.NIL;
    }
    IExpr b = ctx.eval(F.Coefficient(argument, x, F.C1));
    IExpr sign = F.C1;
    if (!ctx.proves(F.Greater(b, F.C0))) {
      if (!ctx.proves(F.Less(b, F.C0))) {
        return F.NIL;
      }
      b = ctx.eval(F.Negate(b));
      sign = cosh ? F.C1 : F.CN1;
    }
    IExpr numerator = rest.oneIdentity1();
    if (!numerator.isFree(t -> t.isAST() && !t.isPlus() && !t.isTimes() && !t.isPower()
        && !t.isFunctionID(ID.Cosh,
            ID.Sinh, ID.Cos,
            ID.Sin)
        && !t.isFree(x), true)) {
      return F.NIL;
    }
    if (!cosh && !ctx.eval(F.subst(numerator, x, F.C0)).isZero()) {
      return F.NIL;
    }
    IExpr expanded = ctx.eval(F.Expand(F.TrigToExp(numerator)));
    IAST terms = expanded.isPlus() ? (IAST) expanded : F.Plus(expanded);
    ISymbol u = F.Dummy("u");
    IExpr kernel = order == 1
        ? F.Times(S.Pi, F.Power(b, F.CN1),
            F.unaryAST1(cosh ? S.Sec : S.Tan, F.Times(u, S.Pi, F.Power(F.Times(F.C2, b), F.CN1))))
        : coshPowerKernel(order, b, u);
    final IExpr width = ctx.eval(F.Times(F.ZZ(order), b));
    IExpr sum = F.C0;
    for (int i = 1; i <= terms.argSize(); i++) {
      IAST termFactors = terms.get(i).isTimes() ? (IAST) terms.get(i) : F.Times(terms.get(i));
      int k = 0;
      IExpr slope = F.C0;
      IASTAppendable coefficient = F.TimesAlloc(termFactors.argSize());
      for (int j = 1; j <= termFactors.argSize(); j++) {
        IExpr factor = termFactors.get(j);
        if (factor.isFree(x)) {
          coefficient.append(factor);
        } else if (factor.equals(x)) {
          k += 1;
        } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isInteger()
            && factor.exponent().isPositive()) {
          k += factor.exponent().toIntDefault();
        } else if (factor.isPower() && factor.base().isE()
            && factor.exponent().isPolynomialOfMaxDegree(x, 1)) {
          slope = F.Plus(slope, ctx.eval(F.Coefficient(factor.exponent(), x, F.C1)));
          coefficient.append(F.Exp(ctx.eval(F.Coefficient(factor.exponent(), x, F.C0))));
        } else {
          return F.NIL;
        }
      }
      if (k < 0 || k > 6) {
        return F.NIL;
      }
      slope = ctx.eval(slope);
      // convergence: Abs(Re(u)) < b
      if (hasNonRealNumber(slope)) {
        // E^(I*a*x)/Cosh(b*x): the numerical integration of the oscillation is not reliable
        ctx.oscillating = true;
      }
      IExpr re = ctx.eval(F.ComplexExpand(F.Re(slope)));
      if (!re.isZero()
          && !(ctx.proves(F.Less(re, width)) && ctx.proves(F.Greater(re, F.Negate(width))))) {
        return F.NIL;
      }
      IExpr value = k == 0 ? kernel : ctx.eval(F.D(kernel, F.List(u, F.ZZ(k))));
      if (slope.isZero() && order % 2 == 0) {
        // the kernel of an even order has a removable singularity at u == 0
        value = ctx.eval(F.Limit(value, F.Rule(u, F.C0)));
        if (!value.isFree(S.Limit) || !value.isSpecialsFree()) {
          return F.NIL;
        }
      } else {
        value = F.subst(value, u, slope);
      }
      sum = F.Plus(sum, F.Times(coefficient.oneIdentity1(), value));
    }
    return F.Times(sign, sum);
  }

  /** The highest power of <code>Cosh</code> in the denominator of a strip integral. */
  private static final int MAX_STRIP_ORDER = 6;

  /**
   * <code>Integrate(E^(u*x)/Cosh(b*x)^n, {x,-Infinity,Infinity}) ==
   * 2^(n-1)/b*Gamma(n/2+v)*Gamma(n/2-v)/Gamma(n)</code> with <code>v == u/(2*b)</code>, for
   * <code>Abs(Re(u)) &lt; n*b</code>. The two Gamma functions are a polynomial in <code>v</code>
   * times <code>Pi*Sec(Pi*v)</code> for an odd <code>n</code>, and times
   * <code>Pi*v*Csc(Pi*v)</code> for an even one.
   */
  private static IExpr coshPowerKernel(int n, IExpr b, IExpr u) {
    final int m = n / 2;
    IExpr v = F.Times(u, F.Power(F.Times(F.C2, b), F.CN1));
    IASTAppendable product = F.TimesAlloc(2 * m + 4);
    product.append(F.Power(F.C2, F.ZZ(n - 1)));
    product.append(F.Power(F.Times(b, F.Factorial(F.ZZ(n - 1))), F.CN1));
    if (n % 2 == 1) {
      // Gamma(m+1/2+v)*Gamma(m+1/2-v) == Product((j+1/2)^2-v^2, {j,0,m-1})*Pi*Sec(Pi*v)
      for (int j = 0; j < m; j++) {
        product.append(F.Subtract(F.Sqr(F.QQ(2 * j + 1, 2)), F.Sqr(v)));
      }
      product.append(S.Pi);
      product.append(F.Sec(F.Times(S.Pi, v)));
    } else {
      // Gamma(m+v)*Gamma(m-v) == Product(j^2-v^2, {j,1,m-1})*Pi*v*Csc(Pi*v)
      for (int j = 1; j < m; j++) {
        product.append(F.Subtract(F.ZZ(j * j), F.Sqr(v)));
      }
      product.append(S.Pi);
      product.append(v);
      product.append(F.Csc(F.Times(S.Pi, v)));
    }
    return product;
  }

  // --------------------------------------------------------------------------------------------
  // Beta
  // --------------------------------------------------------------------------------------------

  /**
   * <code>Integrate(c*x^p/(d+e*x^n)^q, {x,0,Infinity}) ==
   * c*d^(-q)*(d/e)^(s/n)/n*Beta(s/n, q-s/n)</code> with <code>s == p+1</code>, for
   * <code>d, e, n &gt; 0</code> and <code>0 &lt; s/n &lt; q</code>.
   */
  private static IExpr beta(Context ctx) {
    final ISymbol x = ctx.x;
    IAST factors = ctx.f.isTimes() ? (IAST) ctx.f : F.Times(ctx.f);
    IExpr p = F.C0;
    IExpr q = F.NIL;
    IExpr base = F.NIL;
    IASTAppendable constant = F.TimesAlloc(factors.argSize());
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x)) {
        constant.append(factor);
      } else if (factor.equals(x)) {
        p = F.Plus(p, F.C1);
      } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isFree(x)) {
        p = F.Plus(p, factor.exponent());
      } else if (factor.isPower() && factor.base().isPlus() && factor.base().argSize() == 2
          && factor.exponent().isFree(x) && base.isNIL()) {
        base = factor.base();
        q = ctx.eval(F.Negate(factor.exponent()));
      } else {
        return F.NIL;
      }
    }
    if (base.isNIL()) {
      return F.NIL;
    }
    IExpr d = base.first();
    IExpr power = base.second();
    if (!d.isFree(x)) {
      d = base.second();
      power = base.first();
    }
    if (!d.isFree(x)) {
      return F.NIL;
    }
    IExpr e = F.C1;
    if (power.isTimes()) {
      IASTAppendable coefficient = F.TimesAlloc(power.argSize());
      IExpr monomial = F.NIL;
      for (int i = 1; i <= power.argSize(); i++) {
        IExpr factor = ((IAST) power).get(i);
        if (factor.isFree(x)) {
          coefficient.append(factor);
        } else if (monomial.isNIL()) {
          monomial = factor;
        } else {
          return F.NIL;
        }
      }
      e = coefficient.oneIdentity1();
      power = monomial;
    }
    IExpr n;
    if (power.isPresent() && power.equals(x)) {
      n = F.C1;
    } else if (power.isPresent() && power.isPower() && power.base().equals(x)
        && power.exponent().isFree(x)) {
      n = power.exponent();
    } else {
      return F.NIL;
    }
    p = ctx.eval(p);
    if (p.isInteger() && n.isInteger() && q.isInteger()) {
      // a rational function: its antiderivative is found
      return F.NIL;
    }
    IExpr s = ctx.eval(F.Plus(p, F.C1));
    IExpr ratio = ctx.eval(F.Divide(s, n));
    if (!ctx.proves(F.Greater(d, F.C0)) || !ctx.proves(F.Greater(e, F.C0))
        || !ctx.proves(F.Greater(n, F.C0)) || !ctx.proves(F.Greater(s, F.C0))
        || !ctx.proves(F.Less(s, F.Times(n, q)))) {
      return F.NIL;
    }
    IExpr special = q.isOne() //
        ? F.Times(S.Pi, F.Csc(F.Times(S.Pi, ratio)))
        : F.Times(F.Gamma(ratio), F.Gamma(F.Subtract(q, ratio)), F.Power(F.Gamma(q), F.CN1));
    return F.Times(constant.oneIdentity1(), F.Power(d, F.Negate(q)),
        F.Power(F.Divide(d, e), ratio), F.Power(n, F.CN1), special);
  }
}
