package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.IntegrateTimeBudget;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Proofs that a sum is identically <code>0</code>, for kinds of sums which the search of
 * <code>Simplify</code> does not reduce. Each proof is exact; a numeric value only selects the
 * candidates and decides a sign or a multiple of <code>2*Pi</code>.
 *
 * <ul>
 * <li>inverse trigonometric functions of rational numbers and a multiple of <code>Pi</code>:
 * <code>4*ArcTan(1/5)-ArcTan(1/239)-Pi/4</code>, by a product of Gaussian rational numbers;</li>
 * <li>trigonometric functions of rational multiples of <code>Pi</code> and one square root:
 * <code>Tan(Pi/7)*Tan(2*Pi/7)*Tan(3*Pi/7)-Sqrt(7)</code>, by a root of unity and its cyclotomic
 * polynomial;</li>
 * <li>two terms with square roots under assumptions:
 * <code>Sqrt(x-1)*Sqrt(x+1)-Sqrt(x^2-1)</code> for <code>x&gt;1</code>, by their squares and
 * their signs;</li>
 * <li>a sum of <code>ArcTanh</code> terms with arguments in <code>(-1,1)</code> under
 * assumptions, by the addition theorem of <code>Tanh</code>;</li>
 * <li>hyperbolic and trigonometric functions of <code>k*x + c</code> with a constant
 * <code>c</code>, by exponentials with the exact value of <code>E^c</code>.</li>
 * </ul>
 */
public final class ZeroIdentity {

  private ZeroIdentity() {}

  /** The time one statement about the assumptions may take. */
  private static final long PROOF_MILLIS = 2000L;

  /**
   * Test if the sum is identically 0.
   *
   * @param sum the sum
   * @param assumptions the assumptions of the call, or {@link F#NIL}
   * @param full the proofs of <code>FullSimplify</code> are tried as well
   * @return <code>true</code> only with a proof
   */
  public static boolean isZero(IAST sum, IExpr assumptions, boolean full, EvalEngine engine) {
    if (!sum.isPlus() || sum.leafCount() > 400) {
      return false;
    }
    try {
      final boolean numeric = new VariablesSet(sum).isEmpty();
      if (numeric) {
        if (!full || !isNumericallyZero(sum, engine)) {
          return false;
        }
        return inverseTrigSum(sum, engine) || rootsOfUnity(sum, engine);
      }
      if (assumptions.isPresent()) {
        if (sum.argSize() == 2 && radicalPair(sum, assumptions, engine)) {
          return true;
        }
        if (full && arcTanhSum(sum, assumptions, engine)) {
          return true;
        }
      }
      return constantPhases(sum, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  private static boolean isNumericallyZero(IExpr expr, EvalEngine engine) {
    IExpr value = CompareUtil.evalPrecise(expr, 40, engine);
    if (!value.isNumber()) {
      return false;
    }
    double magnitude = engine.evalQuiet(F.N(F.Abs(value))).evalfNaN();
    return magnitude < 1.0e-30;
  }

  // ---------------------------------------------------------------------------------------------
  // inverse trigonometric functions of rational numbers
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>Sum(c(i)*ArcTan(t(i))) + q*Pi == 0</code> with rational numbers: for the common
   * denominator <code>m</code> of the <code>c(i)</code> and <code>q</code> the product of the
   * <code>(1+I*t(i))^(m*c(i))</code> is a real number with the sign of <code>(-1)^(m*q)</code>,
   * so <code>m</code> times the sum is a multiple of <code>2*Pi</code>, and it is numerically 0.
   * <code>ArcSin(s)</code> and <code>ArcCos(s)</code> take part if <code>Sqrt(1-s^2)</code> is
   * rational.
   */
  private static boolean inverseTrigSum(IAST sum, EvalEngine engine) {
    List<IExpr> points = new ArrayList<>();
    List<IRational> coefficients = new ArrayList<>();
    IRational piFactor = F.C0;
    for (IExpr term : sum) {
      IExpr coefficient = F.C1;
      IExpr function = term;
      if (term.isTimes() && term.argSize() == 2 && term.first().isRational()) {
        coefficient = term.first();
        function = term.second();
      }
      if (function.equals(S.Pi)) {
        piFactor = piFactor.add((IRational) coefficient);
        continue;
      }
      if (!function.isAST1() || !function.first().isRational()) {
        return false;
      }
      IRational t = (IRational) function.first();
      IExpr point;
      if (function.isAST(S.ArcTan, 2)) {
        point = F.Plus(F.C1, F.Times(F.CI, t));
      } else if (function.isAST(S.ArcSin, 2) || function.isAST(S.ArcCos, 2)) {
        IExpr other = engine.evaluate(F.Sqrt(F.Subtract(F.C1, F.Sqr(t))));
        if (!other.isRational()) {
          return false;
        }
        point = function.isAST(S.ArcSin, 2) ? F.Plus(other, F.Times(F.CI, t))
            : F.Plus(t, F.Times(F.CI, other));
      } else {
        return false;
      }
      points.add(point);
      coefficients.add((IRational) coefficient);
    }
    if (points.isEmpty()) {
      return false;
    }
    IInteger multiplier = piFactor.denominator();
    for (IRational c : coefficients) {
      multiplier = multiplier.lcm(c.denominator());
    }
    IASTAppendable product = F.TimesAlloc(points.size());
    for (int i = 0; i < points.size(); i++) {
      IRational power = coefficients.get(i).multiply(multiplier);
      if (!power.isInteger() || power.abs().isGT(F.ZZ(96))) {
        return false;
      }
      product.append(F.Power(points.get(i), power));
    }
    IExpr value = engine.evaluate(product);
    if (!value.isRational() || value.isZero()) {
      return false;
    }
    IRational turns = piFactor.multiply(multiplier);
    boolean oddTurns = turns.isInteger() && ((IInteger) turns).isOdd();
    // the arguments add up to an even or an odd multiple of Pi, as the Pi term does
    return value.isNegative() == oddTurns;
  }

  // ---------------------------------------------------------------------------------------------
  // trigonometric functions of rational multiples of Pi
  // ---------------------------------------------------------------------------------------------

  /**
   * A numeric expression of <code>Sin</code>, <code>Cos</code>, <code>Tan</code>, ... at rational
   * multiples of <code>Pi</code>, rational numbers and one square root <code>Sqrt(d)</code>: with
   * <code>z = E^(I*Pi/n)</code> it is <code>A(z) + B(z)*Sqrt(d)</code> for rational functions
   * <code>A</code>, <code>B</code>, which are 0 if their numerators are divisible by the
   * cyclotomic polynomial of <code>z</code>. With a root, <code>A^2 == d*B^2</code> and the
   * numeric values of <code>A</code> and <code>B*Sqrt(d)</code> have opposite signs.
   */
  private static boolean rootsOfUnity(IAST sum, EvalEngine engine) {
    final IInteger[] order = {F.C2};
    final IExpr[] radicand = {F.NIL};
    final boolean[] supported = {true};
    final boolean[] trigonometric = {false};
    sum.forEach(term -> scan(term, order, radicand, supported, trigonometric));
    if (!supported[0] || !trigonometric[0] || order[0].isGT(F.ZZ(120))) {
      return false;
    }
    final int n = order[0].toIntDefault();
    final ISymbol z = F.Dummy("z");
    final ISymbol r = F.Dummy("r");
    // I == z^(n/2), n is even
    final IExpr imaginaryUnit = F.Power(z, F.ZZ(n / 2));
    IExpr rational = F.subst(sum, t -> {
      if (t.isAST1() && isCircular(t)) {
        IExpr turn = engine.evaluate(F.Times(F.ZZ(n), F.Divide(t.first(), S.Pi)));
        IExpr plus = F.Power(z, turn);
        IExpr minus = F.Power(z, turn.negate());
        IExpr sin = F.Times(F.C1D2, F.Power(imaginaryUnit, F.CN1), F.Subtract(plus, minus));
        IExpr cos = F.Times(F.C1D2, F.Plus(plus, minus));
        switch (t.headID()) {
          case org.matheclipse.core.expression.ID.Sin:
            return sin;
          case org.matheclipse.core.expression.ID.Cos:
            return cos;
          case org.matheclipse.core.expression.ID.Tan:
            return F.Divide(sin, cos);
          case org.matheclipse.core.expression.ID.Cot:
            return F.Divide(cos, sin);
          case org.matheclipse.core.expression.ID.Sec:
            return F.Power(cos, F.CN1);
          default:
            return F.Power(sin, F.CN1);
        }
      }
      if (t.isSqrt() && t.base().equals(radicand[0])) {
        return r;
      }
      if (t.isPower() && t.base().equals(radicand[0]) && t.exponent().equals(F.CN1D2)) {
        return F.Power(r, F.CN1);
      }
      return F.NIL;
    });
    IExpr cyclotomic = engine.evaluate(F.binaryAST2(S.Cyclotomic, F.ZZ(2L * n), z));
    if (radicand[0].isNIL()) {
      return isMultipleOf(rational, cyclotomic, z, engine);
    }
    // 1/r == r/d
    rational = engine.evaluate(F.Together(F.subst(rational,
        t -> t.isPower() && t.base().equals(r) && t.exponent().isMinusOne()
            ? F.Divide(r, radicand[0])
            : F.NIL)));
    IExpr numerator = engine.evaluate(F.Expand(F.Numerator(rational)));
    IExpr denominator = engine.evaluate(F.Denominator(rational));
    if (!denominator.isFree(r) || !numerator.isPolynomial(r)
        || engine.evaluate(F.Exponent(numerator, r)).toIntDefault() > 1) {
      return false;
    }
    IExpr a = engine.evaluate(F.Coefficient(numerator, r, F.C0));
    IExpr b = engine.evaluate(F.Coefficient(numerator, r, F.C1));
    IExpr squares = F.Subtract(F.Sqr(a), F.Times(radicand[0], F.Sqr(b)));
    if (!isMultipleOf(squares, cyclotomic, z, engine)) {
      return false;
    }
    // A == +-B*Sqrt(d): the sum is numerically 0, so it is the minus sign unless both are 0
    IExpr root = F.Power(F.CN1, F.QQ(1, n));
    double value = engine.evalQuiet(F.N(F.Abs(F.subst(a, z, root)))).evalfNaN();
    if (!Double.isNaN(value) && value > 1.0e-6) {
      return true;
    }
    return isMultipleOf(a, cyclotomic, z, engine) && isMultipleOf(b, cyclotomic, z, engine);
  }

  private static boolean isCircular(IExpr t) {
    return t.isSin() || t.isCos() || t.isTan() || t.isAST(S.Cot, 2) || t.isAST(S.Sec, 2)
        || t.isAST(S.Csc, 2);
  }

  /** Collect the denominators of the angles and the one radicand; anything else is declined. */
  private static void scan(IExpr expr, IInteger[] order, IExpr[] radicand, boolean[] supported,
      boolean[] trigonometric) {
    if (expr.isRational()) {
      return;
    }
    if (expr.isAST1() && isCircular(expr)) {
      IExpr turn = EvalEngine.get().evaluate(F.Divide(expr.first(), S.Pi));
      if (!turn.isRational()) {
        supported[0] = false;
        return;
      }
      trigonometric[0] = true;
      order[0] = order[0].lcm(((IRational) turn).denominator());
      return;
    }
    if (expr.isPower() && expr.base().isRational() && expr.base().isPositive()
        && (expr.exponent().equals(F.C1D2) || expr.exponent().equals(F.CN1D2))) {
      if (radicand[0].isNIL()) {
        radicand[0] = expr.base();
      } else if (!radicand[0].equals(expr.base())) {
        supported[0] = false;
      }
      return;
    }
    if (expr.isPlus() || expr.isTimes()
        || (expr.isPower() && expr.exponent().isInteger())) {
      for (IExpr arg : (IAST) expr) {
        scan(arg, order, radicand, supported, trigonometric);
      }
      return;
    }
    supported[0] = false;
  }

  /** The numerator of the rational function of <code>z</code> is a multiple of the polynomial. */
  private static boolean isMultipleOf(IExpr rational, IExpr polynomial, ISymbol z,
      EvalEngine engine) {
    IExpr numerator = engine.evaluate(F.Expand(F.Numerator(F.Together(rational))));
    if (numerator.isZero()) {
      return true;
    }
    if (!numerator.isPolynomial(z)) {
      return false;
    }
    return engine.evaluate(F.PolynomialRemainder(numerator, polynomial, z)).isZero();
  }

  // ---------------------------------------------------------------------------------------------
  // under assumptions
  // ---------------------------------------------------------------------------------------------

  /** The statement follows from the assumptions over the reals. */
  private static boolean proves(IExpr assumptions, IExpr statement, EvalEngine engine) {
    IExpr condition = F.And(assumptions, F.Not(statement));
    IAST variables = new VariablesSet(condition).getVarList();
    if (variables.argSize() == 0 || variables.argSize() > 3) {
      return false;
    }
    final long millis = (long) (PROOF_MILLIS * MachineProfile.getScale());
    // the assumptions are a part of the statement: with the same assumptions in force they
    // would evaluate to True and be lost
    final org.matheclipse.core.eval.util.IAssumptions inForce = engine.getAssumptions();
    engine.setAssumptions(null);
    try {
      IExpr reduced = IntegrateTimeBudget
          .runWithin(() -> engine.evalQuiet(F.Reduce(condition, variables, S.Reals)), millis);
      return reduced.isPresent() && reduced.isFalse();
    } finally {
      engine.setAssumptions(inForce);
    }
  }

  /**
   * <code>p - q == 0</code> for two terms with square roots: both are products of factors which
   * are not negative under the assumptions, and their squares are equal.
   */
  private static boolean radicalPair(IAST sum, IExpr assumptions, EvalEngine engine) {
    if (sum.isFree(t -> t.isPower() && t.exponent().isFraction(), true)) {
      return false;
    }
    IExpr p = sum.arg1();
    IExpr q = engine.evaluate(F.Negate(sum.arg2()));
    if (!isNonNegative(p, assumptions, engine)) {
      // both terms with the other sign
      p = engine.evaluate(F.Negate(p));
      q = sum.arg2();
      if (!isNonNegative(p, assumptions, engine)) {
        return false;
      }
    }
    if (!isNonNegative(q, assumptions, engine)) {
      return false;
    }
    IExpr difference = engine.evaluate(F.Together(F.Subtract(F.Sqr(p), F.Sqr(q))));
    return difference.isZero()
        || engine.evaluate(F.Expand(F.Numerator(difference))).isZero();
  }

  /** A product of factors each of which is not negative under the assumptions. */
  private static boolean isNonNegative(IExpr term, IExpr assumptions, EvalEngine engine) {
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    for (IExpr factor : factors) {
      if (factor.isReal()) {
        if (factor.isNegative()) {
          return false;
        }
        continue;
      }
      if (factor.isPower() && (factor.exponent().equals(F.C1D2)
          || factor.exponent().equals(F.CN1D2))) {
        // a square root of a number which is not negative
        IExpr radicand = factor.base();
        if (!proves(assumptions, factor.exponent().isNegative() ? F.Greater(radicand, F.C0)
            : F.GreaterEqual(radicand, F.C0), engine)) {
          return false;
        }
        continue;
      }
      if (!factor.isFree(t -> t.isPower() && t.exponent().isFraction(), true)
          || !proves(assumptions, F.GreaterEqual(factor, F.C0), engine)) {
        return false;
      }
    }
    return true;
  }

  /**
   * <code>Sum(k(i)*ArcTanh(u(i))) == 0</code> with integers <code>k(i)</code> and arguments in
   * <code>(-1,1)</code> under the assumptions: the sum is real, and its <code>Tanh</code>, a
   * rational function of the arguments by the addition theorem, is 0.
   */
  private static boolean arcTanhSum(IAST sum, IExpr assumptions, EvalEngine engine) {
    IExpr tanh = F.C0;
    int count = 0;
    for (IExpr term : sum) {
      int k = 1;
      IExpr function = term;
      if (term.isTimes() && term.argSize() == 2 && term.first().isInteger()) {
        k = term.first().toIntDefault();
        function = term.second();
      }
      if (!function.isAST(S.ArcTanh, 2) || k == Integer.MIN_VALUE || Math.abs(k) > 4) {
        return false;
      }
      IExpr u = function.first();
      if (!proves(assumptions, F.And(F.Greater(u, F.CN1), F.Less(u, F.C1)), engine)) {
        return false;
      }
      IExpr signed = k < 0 ? F.Negate(u) : u;
      for (int i = 0; i < Math.abs(k); i++) {
        // Tanh(a+b) == (Tanh(a)+Tanh(b))/(1+Tanh(a)*Tanh(b))
        tanh = engine.evaluate(F.Together(
            F.Divide(F.Plus(tanh, signed), F.Plus(F.C1, F.Times(tanh, signed)))));
        count++;
      }
    }
    if (count < 2) {
      return false;
    }
    return engine.evaluate(F.Expand(F.Numerator(tanh))).isZero();
  }

  // ---------------------------------------------------------------------------------------------
  // constant phases
  // ---------------------------------------------------------------------------------------------

  /**
   * Hyperbolic and trigonometric functions whose arguments have a constant part, as
   * <code>Tanh(x+I*Pi/3)</code>: written with exponentials, <code>E^(k*x+c)</code> is
   * <code>E^c*E^(k*x)</code> with the exact value of <code>E^c</code>, and the sum is 0 if the
   * numerator of the resulting fraction is.
   */
  private static boolean constantPhases(IAST sum, EvalEngine engine) {
    IAST variables = new VariablesSet(sum).getVarList();
    final boolean[] phase = {false};
    sum.isFree(t -> {
      if (t.isAST1() && (t.isTanh() || t.isSinh() || t.isCosh() || t.isAST(S.Coth, 2)
          || t.isAST(S.Sech, 2) || t.isAST(S.Csch, 2)) && t.first().isPlus()
          && !t.first().isFree(S.Pi)) {
        phase[0] = true;
      }
      return false;
    }, true);
    if (!phase[0] || variables.argSize() == 0) {
      return false;
    }
    IExpr exponential = engine.evaluate(F.TrigToExp(sum));
    IExpr split = F.subst(exponential, t -> {
      if (t.isPower() && t.base().isE()) {
        IExpr exponent = engine.evaluate(F.Expand(t.exponent()));
        IAST terms = exponent.isPlus() ? (IAST) exponent : F.Plus(exponent);
        IASTAppendable constant = F.PlusAlloc(terms.argSize());
        IASTAppendable variable = F.PlusAlloc(terms.argSize());
        for (IExpr term : terms) {
          (term.isFree(v -> variables.contains(v), true) ? constant : variable).append(term);
        }
        if (constant.argSize() == 0 || variable.argSize() == 0) {
          return F.NIL;
        }
        IExpr value = engine.evaluate(F.Exp(constant.oneIdentity0()));
        if (value.isPower() && value.base().isE()) {
          return F.NIL;
        }
        return F.Times(value, F.Exp(variable.oneIdentity0()));
      }
      return F.NIL;
    });
    IExpr together = engine.evaluate(F.Together(split));
    if (together.isZero()) {
      return true;
    }
    IExpr numerator = engine.evaluate(F.ExpandAll(F.Numerator(together)));
    return numerator.isZero();
  }
}
