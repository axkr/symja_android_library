package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;


/**
 * The <code>FullSimplify</code> form of a univariate polynomial with integer coefficients. The
 * candidates, cheapest by the complexity function, with ties going to the earlier one:
 *
 * <ol>
 * <li>the expanded form;
 * <li>a genuine factorization (two or more non-constant factors, or a power of one), taken as soon
 * as it is no more expensive than the expanded form, each factor searched in turn:
 * <code>6-5*x+x^2</code> becomes <code>(-3+x)*(-2+x)</code> although <code>6+x*(-5+x)</code> is
 * cheaper;
 * <li>the content times the searched primitive part: <code>100+200*x+300*x^2</code> becomes
 * <code>100*(1+x*(2+3*x))</code>;
 * <li>the terms below some power plus the searched remainder, with the content, the sign and the
 * power of <code>x</code> pulled out of the remainder: <code>1+2*x-3*x^2</code> becomes
 * <code>1+x*(2-3*x)</code>, <code>4+x-6*x^2+3*x^3</code> becomes <code>4+x+3*x^2*(-2+x)</code>.
 * Past the constant term a split needs a common content in the remainder; without a constant term
 * the only split pulls out the lowest power: <code>7*x^2+x^3-3*x^4</code> becomes
 * <code>x^2*(7+x-3*x^2)</code>.
 * </ol>
 *
 * Candidates 3 and 4 must be strictly cheaper than the best so far, so <code>2+4*x+6*x^2</code>
 * stays expanded, except that pulling out the content or a power of <code>x</code> wins a tie with
 * the expanded form: <code>2+2*x</code> becomes <code>2*(1+x)</code>.
 */
final class UnivariatePolynomialForms {

  /** The search calls {@link F#Factor} once per state and is quadratic in the degree. */
  private static final int MAX_DEGREE = 24;

  private final IExpr x;

  private final Function<IExpr, Long> complexity;

  private final EvalEngine engine;

  /** The searched form of every coefficient list seen so far. */
  private final Map<List<IInteger>, IExpr> memo = new HashMap<>();

  private UnivariatePolynomialForms(IExpr x, Function<IExpr, Long> complexity,
      EvalEngine engine) {
    this.x = x;
    this.complexity = complexity;
    this.engine = engine;
  }

  /**
   * The <code>FullSimplify</code> form of a sum which is a univariate polynomial with integer
   * coefficients.
   *
   * @param expr the sum
   * @param complexity the measure the candidates compete in
   * @param engine the evaluation engine
   * @return the cheapest form, or {@link F#NIL} if <code>expr</code> is no such polynomial
   */
  static IExpr bestForm(IExpr expr, Function<IExpr, Long> complexity, EvalEngine engine) {
    if (!expr.isPlus()) {
      return F.NIL;
    }
    VariablesSet variables = new VariablesSet(expr);
    if (variables.size() != 1) {
      return F.NIL;
    }
    IExpr x = variables.getArrayList().get(0);
    List<IInteger> coefficients = integerCoefficients(expr, x, engine);
    if (coefficients == null || coefficients.size() > MAX_DEGREE + 1) {
      return F.NIL;
    }
    return new UnivariatePolynomialForms(x, complexity, engine).search(coefficients);
  }

  /**
   * The quotient of the searched forms of the expanded numerator and denominator. A search of the
   * whole quotient can't find these: <code>(4+4*x-5*x^2)/(5-5*x+5*x^2-5*x^3)</code> becomes
   * <code>(-4+x*(-4+5*x))/(5*(-1+x)*(1+x^2))</code>.
   *
   * @param quotient the quotient
   * @param complexity the measure the candidates compete in
   * @param engine the evaluation engine
   * @return the quotient of the two forms, or {@link F#NIL} if numerator or denominator isn't a
   *         univariate polynomial with integer coefficients
   */
  static IExpr bestQuotientForm(IExpr quotient, Function<IExpr, Long> complexity,
      EvalEngine engine) {
    if (!quotient.isTimes() || !((IAST) quotient)
        .exists(x -> x.isPower() && x.base().isAST() && x.exponent().isNegativeResult())) {
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(F.Denominator(quotient));
    if (denominator.isNumber()) {
      return F.NIL;
    }
    // the denominator first: a numerator search is wasted if the denominator doesn't qualify
    denominator = bestForm(engine.evaluate(F.Expand(denominator)), complexity, engine);
    if (denominator.isNIL()) {
      return F.NIL;
    }
    IExpr numerator =
        bestForm(engine.evaluate(F.Expand(F.Numerator(quotient))), complexity, engine);
    if (numerator.isNIL()) {
      return F.NIL;
    }
    // unevaluated, in the shape the simplifier's own quotient candidates have, so that the
    // complexities compare like for like
    return F.Times(F.Power(denominator, F.CN1), numerator);
  }

  /**
   * @return the coefficients of <code>poly</code> from the constant term upwards, or
   *         <code>null</code> if it isn't a polynomial in <code>x</code> with at least two integer
   *         terms
   */
  private static List<IInteger> integerCoefficients(IExpr poly, IExpr x, EvalEngine engine) {
    if (!poly.isPolynomial(F.list(x))) {
      return null;
    }
    IExpr list = engine.evaluate(F.CoefficientList(poly, x));
    if (!list.isList() || list.argSize() < 2 || list.argSize() > MAX_DEGREE + 1) {
      return null;
    }
    List<IInteger> coefficients = new ArrayList<>(list.argSize());
    int nonZero = 0;
    for (IExpr c : (IAST) list) {
      if (!c.isInteger()) {
        return null;
      }
      coefficients.add((IInteger) c);
      if (!c.isZero()) {
        nonZero++;
      }
    }
    return nonZero < 2 ? null : coefficients;
  }

  private IExpr search(List<IInteger> input) {
    List<IInteger> coeffs = new ArrayList<>(input);
    while (coeffs.size() > 1 && coeffs.get(coeffs.size() - 1).isZero()) {
      coeffs.remove(coeffs.size() - 1);
    }
    IExpr cached = memo.get(coeffs);
    if (cached != null) {
      return cached;
    }
    engine.checkTimeLimit();
    final int n = coeffs.size();
    final IExpr plain = engine.evaluate(polynomial(coeffs, n));
    List<Integer> nonZero = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      if (!coeffs.get(i).isZero()) {
        nonZero.add(i);
      }
    }
    if (nonZero.size() < 2) {
      memo.put(coeffs, plain);
      return plain;
    }
    IExpr best = plain;
    long bestCost = complexity.apply(best);

    // 2. a genuine factorization, searched factor by factor
    IExpr factored = engine.evaluate(F.Factor(plain));
    if (isGenuineFactorization(factored) && complexity.apply(factored) <= bestCost) {
      IExpr result = searchFactors(factored);
      memo.put(coeffs, result);
      return result;
    }

    // 3. the content times the searched primitive part
    IInteger content = gcd(coeffs, 0);
    if (content.isGT(F.C1)) {
      for (IInteger d : new IInteger[] {content, content.negate()}) {
        IExpr candidate = engine.evaluate(F.Times(d, search(divide(coeffs, 0, d))));
        long cost = complexity.apply(candidate);
        if (cost < bestCost || (cost == bestCost && best == plain)) {
          best = candidate;
          bestCost = cost;
        }
      }
    }

    // 4. the low order terms plus the searched remainder
    for (int k = 1; k < n; k++) {
      final boolean lowIsZero = k <= nonZero.get(0);
      if (lowIsZero && k != nonZero.get(0)) {
        continue;
      }
      List<Integer> upper = new ArrayList<>();
      for (int i = k; i < n; i++) {
        if (!coeffs.get(i).isZero()) {
          upper.add(i);
        }
      }
      if (upper.size() < 2 && !lowIsZero) {
        continue;
      }
      final int m = upper.get(0);
      IInteger g = gcd(coeffs, m);
      if (k >= 2 && g.isOne() && !lowIsZero) {
        // past the constant term only a common content makes a split:
        // -1+x-x^2+2*x^3 stays although -1+x+x^2*(-1+2*x) is cheaper
        continue;
      }
      IExpr low = polynomial(coeffs, k);
      // on a tie the minus is pulled only out of a remainder with a negative leading coefficient
      boolean negativeLead = coeffs.get(upper.get(upper.size() - 1)).isNegative();
      for (IInteger d : negativeLead ? new IInteger[] {g.negate(), g}
          : new IInteger[] {g, g.negate()}) {
        IExpr inner = search(divide(coeffs, m, d));
        IASTAppendable product = F.TimesAlloc(3);
        if (!d.isOne()) {
          product.append(d);
        }
        product.append(F.Power(x, F.ZZ(m)));
        product.append(inner);
        IExpr candidate = engine.evaluate(lowIsZero ? product : F.Plus(low, product));
        long cost = complexity.apply(candidate);
        if (cost < bestCost || (lowIsZero && cost == bestCost && best == plain)) {
          best = candidate;
          bestCost = cost;
        }
      }
    }
    memo.put(coeffs, best);
    return best;
  }

  /** <code>Sum(coeffs[i]*x^i, i = 0 .. size-1)</code>, unevaluated. */
  private IExpr polynomial(List<IInteger> coeffs, int size) {
    IASTAppendable sum = F.PlusAlloc(size);
    for (int i = 0; i < size; i++) {
      IInteger c = coeffs.get(i);
      if (!c.isZero()) {
        sum.append(i == 0 ? c : F.Times(c, F.Power(x, F.ZZ(i))));
      }
    }
    return sum.oneIdentity0();
  }

  /** The greatest common divisor of the coefficients from <code>start</code> on. */
  private static IInteger gcd(List<IInteger> coeffs, int start) {
    IInteger g = F.C0;
    for (int i = start; i < coeffs.size(); i++) {
      g = g.gcd(coeffs.get(i));
    }
    return g.abs();
  }

  /** The coefficients from <code>start</code> on, divided exactly by <code>d</code>. */
  private static List<IInteger> divide(List<IInteger> coeffs, int start, IInteger d) {
    List<IInteger> result = new ArrayList<>(coeffs.size() - start);
    for (int i = start; i < coeffs.size(); i++) {
      result.add(coeffs.get(i).div(d));
    }
    return result;
  }

  /**
   * Whether {@link F#Factor} split the polynomial into two or more non-constant factors, or a power
   * of one, rather than only pulling out a number or a power of the variable.
   */
  private static boolean isGenuineFactorization(IExpr factored) {
    IAST factors = factored.isTimes() ? (IAST) factored : F.list(factored);
    int sums = 0;
    for (IExpr factor : factors) {
      if (factor.isPower() && factor.base().isPlus()) {
        return true;
      }
      if (factor.isPlus()) {
        sums++;
      }
    }
    return sums >= 2;
  }

  /** Search every sum of a {@link F#Factor} result, keeping the shape of the product. */
  private IExpr searchFactors(IExpr factored) {
    if (factored.isTimes() || factored.isPower()) {
      IAST ast = (IAST) factored;
      IASTAppendable rebuilt = ast.copyHead(ast.size());
      boolean changed = false;
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        IExpr searched = factored.isPower() && i == 2 ? arg : searchFactors(arg);
        changed |= searched != arg;
        rebuilt.append(searched);
      }
      // an unchanged product keeps the order Factor gave it
      return changed ? engine.evaluate(rebuilt) : factored;
    }
    if (factored.isPlus()) {
      List<IInteger> coefficients = integerCoefficients(factored, x, engine);
      if (coefficients != null) {
        IExpr searched = search(coefficients);
        return searched.equals(factored) ? factored : searched;
      }
    }
    return factored;
  }
}
