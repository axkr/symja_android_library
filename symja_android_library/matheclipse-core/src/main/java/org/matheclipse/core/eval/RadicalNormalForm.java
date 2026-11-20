package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A normal form of a rational function of square roots of polynomials: every square root
 * <code>Sqrt(p)</code> is a new variable <code>r</code> with <code>r^2==p</code>, the denominator
 * is made free of the roots by multiplying with conjugates, and the common factor of the
 * numerator coefficients and the denominator is cancelled. In this form
 * <code>-1/Sqrt(a)-y/(a*Sqrt(a+y)-Sqrt(a)*(a+y))</code> is <code>1/Sqrt(a+y)</code>.
 * <p>
 * Subexpressions which are not sums, products, integer powers or such square roots, like
 * <code>E^(3*x)</code> or <code>Log(x)</code>, are opaque variables of the form. The rewrite is an
 * identity wherever the conjugates don't vanish, and a form whose denominator keeps a factor
 * which the input's denominator doesn't have is refused, so the form has no new poles.
 */
public final class RadicalNormalForm {

  /** At most this many different square roots, each one doubles the size of a conjugate. */
  private static final int MAX_RADICALS = 4;

  private static final int MAX_LEAFCOUNT = 400;

  /**
   * Simplify offers the form for every candidate, so a form which takes longer isn't offered; the
   * time on the baseline machine.
   */
  private static final long MILLIS = 500L;

  private final EvalEngine engine;

  /** The square root variables with their radicands, in the order of their creation. */
  private final List<ISymbol> radicals = new ArrayList<>();

  private final Map<IExpr, ISymbol> radicalOfRadicand = new HashMap<>();

  private final Map<ISymbol, IExpr> radicandOf = new HashMap<>();

  private final Map<IExpr, ISymbol> atomOf = new HashMap<>();

  /** The rules which replace the variables of the form by the subexpressions they stand for. */
  private final IASTAppendable backRules = F.ListAlloc();

  private boolean failed = false;

  private RadicalNormalForm(EvalEngine engine) {
    this.engine = engine;
  }

  /**
   * Whether <code>expr</code> has a sum with a square root of a non-constant radicand in a
   * denominator, the shape for which the normal form can be smaller than its input. Square roots of
   * numbers alone are left to the denesting and conjugate rewrites of numbers.
   */
  public static boolean isCandidate(IExpr expr) {
    return expr.isAST() && expr.leafCount() < MAX_LEAFCOUNT
        && expr.has(x -> x.isPower() && x.exponent().isNegative() && x.base().isPlus()
            && x.base().has(RadicalNormalForm::isSymbolicSquareRoot, true), true);
  }

  private static boolean isSymbolicSquareRoot(IExpr x) {
    return isSquareRoot(x) && !x.base().isNumericFunction(true);
  }

  private static boolean isSquareRoot(IExpr x) {
    return x.isPower() && x.exponent().isFraction()
        && ((IFraction) x.exponent()).denominator().equals(F.C2);
  }

  /**
   * The normal form of <code>expr</code>.
   *
   * @return {@link F#NIL} if <code>expr</code> has no square root, more than
   *         {@link #MAX_RADICALS} of them, a number which isn't rational, or the time limit ran out
   */
  public static IExpr of(IExpr expr, EvalEngine engine) {
    if (!isCandidate(expr)) {
      return F.NIL;
    }
    return TimeLimits.callOrNil(engine, () -> new RadicalNormalForm(engine).normalForm(expr),
        MachineProfile.millis(MILLIS));
  }

  private IExpr normalForm(IExpr expr) {
    IExpr abstracted = abstractExpr(expr);
    if (failed || radicals.isEmpty() || radicals.size() > MAX_RADICALS) {
      return F.NIL;
    }
    IExpr quotient = engine.evaluate(F.Together(abstracted));
    IExpr numerator = reduce(engine.evaluate(F.Numerator(quotient)));
    IExpr denominator = reduce(engine.evaluate(F.Denominator(quotient)));
    final IExpr inputDenominator = denominator;
    for (ISymbol r : radicals) {
      if (denominator.isFree(r)) {
        continue;
      }
      // denominator == d0 + d1*r with r^2 == p: multiply with d0 - d1*r
      IExpr d0 = engine.evaluate(F.Coefficient(denominator, r, F.C0));
      IExpr d1 = engine.evaluate(F.Coefficient(denominator, r, F.C1));
      IExpr conjugate = F.Subtract(d0, F.Times(d1, r));
      numerator = reduce(engine.evaluate(F.Expand(F.Times(numerator, conjugate))));
      denominator = reduce(engine.evaluate(F.Expand(F.Times(denominator, conjugate))));
      if (denominator.isZero() || !denominator.isFree(r)) {
        return F.NIL;
      }
    }
    // The opaque variables can still be related, like E^x and E^(2*x), so the rationalized
    // denominator may vanish once they are replaced back: then a conjugate vanished on a region
    // where the input doesn't, and the form isn't the input there.
    IExpr backDenominator = engine.evaluate(F.ReplaceAll(denominator, backRules));
    if (engine.evalQuiet(F.PossibleZeroQ(backDenominator)).isTrue()) {
      return F.NIL;
    }
    IExpr cancelled = engine.evaluate(F.Cancel(F.Divide(numerator, denominator)));
    if (!isInputFactor(engine.evaluate(F.Denominator(cancelled)), inputDenominator)) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.ReplaceAll(cancelled, backRules));
    if (!result.isFree(x -> x.isIndeterminate() || x.isDirectedInfinity(), true)) {
      return F.NIL;
    }
    return result;
  }

  /**
   * Whether the denominator of the form is, up to radicands and a number, a factor of the
   * denominator of the input. A remaining factor of a conjugate's norm can vanish where the
   * input is defined: <code>-v*Sqrt(t^2*v)/(t-Sqrt(t^2*v))</code> would get the denominator
   * <code>t*(v-1)</code>, but is <code>1/2</code> at <code>v==1, t==-1</code>.
   */
  private boolean isInputFactor(IExpr formDenominator, IExpr inputDenominator) {
    IExpr rest = formDenominator;
    for (ISymbol r : radicals) {
      IExpr p = radicandOf.get(r);
      if (p.isNumber()) {
        continue;
      }
      for (int i = 0; i < 8; i++) {
        IExpr quotient = engine.evaluate(F.Cancel(F.Divide(rest, p)));
        if (!engine.evaluate(F.Denominator(quotient)).isOne()) {
          break;
        }
        rest = quotient;
      }
    }
    // the denominator of the form is free of the square root variables, and so is its quotient
    return engine.evaluate(F.Denominator(F.Cancel(F.Divide(inputDenominator, rest)))).isNumber();
  }

  /** Reduce the powers of every square root variable <code>r</code> with <code>r^2==p</code>. */
  private IExpr reduce(IExpr polynomial) {
    IExpr result = engine.evaluate(F.Expand(polynomial));
    for (ISymbol r : radicals) {
      if (!result.isFree(r)) {
        result = engine.evaluate(F.Expand(F.PolynomialRemainder(result,
            F.Subtract(F.Sqr(r), radicandOf.get(r)), r)));
      }
    }
    return result;
  }

  /**
   * The expression in the variables of the form: square roots of polynomials become square root
   * variables, every other subexpression which isn't a sum, product or integer power becomes an
   * opaque variable.
   */
  private IExpr abstractExpr(IExpr expr) {
    if (failed) {
      return expr;
    }
    if (expr.isNumber()) {
      if (!expr.isRational()) {
        failed = true;
      }
      return expr;
    }
    if (expr.isSymbol()) {
      return expr.isVariable() ? expr : atom(expr);
    }
    if (expr.isPlus() || expr.isTimes()) {
      IAST ast = (IAST) expr;
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      for (int i = 1; i < ast.size(); i++) {
        result.append(abstractExpr(ast.get(i)));
      }
      return result;
    }
    if (expr.isPower()) {
      IExpr exponent = expr.exponent();
      if (exponent.isInteger()) {
        return F.Power(abstractExpr(expr.base()), exponent);
      }
      if (isSquareRoot(expr)) {
        IExpr radicand = engine.evaluate(abstractExpr(expr.base()));
        if (!failed && isPolynomial(radicand)) {
          ISymbol r = radical(radicand, expr.base());
          // p^(k/2) == p^((k-1)/2) * r for an odd k
          IExpr k = ((IFraction) exponent).numerator();
          return F.Times(F.Power(radicand, k.subtract(F.C1).divide(F.C2)), r);
        }
      }
      if (exponent.isRational()) {
        // another root, or the square root of a quotient: algebraic over the other variables,
        // Sqrt(-e/d)^2 == -e/d, which an opaque variable doesn't know
        failed = true;
        return expr;
      }
    }
    return atom(expr);
  }

  private boolean isPolynomial(IExpr expr) {
    if (expr.isRational()) {
      return true;
    }
    if (expr.isSymbol()) {
      return !radicandOf.containsKey(expr);
    }
    if (expr.isPlus() || expr.isTimes()) {
      return ((IAST) expr).forAll(this::isPolynomial);
    }
    if (expr.isPower()) {
      return expr.exponent().isInteger() && expr.exponent().isPositive()
          && isPolynomial(expr.base());
    }
    return false;
  }

  private ISymbol radical(IExpr radicand, IExpr originalRadicand) {
    ISymbol r = radicalOfRadicand.get(radicand);
    if (r == null) {
      r = F.Dummy("r" + radicals.size());
      radicals.add(r);
      radicalOfRadicand.put(radicand, r);
      radicandOf.put(r, radicand);
      backRules.append(F.Rule(r, F.Sqrt(originalRadicand)));
    }
    return r;
  }

  private ISymbol atom(IExpr expr) {
    ISymbol a = atomOf.get(expr);
    if (a == null) {
      a = F.Dummy("a" + atomOf.size());
      atomOf.put(expr, a);
      backRules.append(F.Rule(a, expr));
    }
    return a;
  }
}
