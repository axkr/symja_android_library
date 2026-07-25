package org.matheclipse.core.integrate;

import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Collect the terms of an antiderivative by their <code>x</code>-dependent part and denest the
 * algebraic coefficients that come out of the collection.
 *
 * <p>
 * Splitting an integral by linearity hands the same transcendental back several times with
 * coefficients written over different radical extensions, and <code>Plus</code> cannot add those:
 *
 * <pre>
 * -Sqrt(2/5)/Sqrt(1+Sqrt(5))*ArcTan(u) + 2/5*Sqrt(5*(2+Sqrt(5)))*ArcTan(u)
 *     - 1/5*Sqrt(-10+5*Sqrt(5))*ArcTan(u)
 * </pre>
 *
 * is one term, <code>Sqrt(1/2*(1+Sqrt(5)))*ArcTan(u)</code>. Summing the coefficients is only half
 * of it: the sum has to be recognized as a square root again. Each coefficient here is the square
 * root of an algebraic number of degree 2, so squaring it drops back into that quadratic field,
 * where <code>RootReduce</code> plus <code>ToRadicals</code> gives a clean radical - taking the
 * square root of that (with the sign read off numerically) denests the coefficient.
 *
 * <p>
 * The pass is gated on a nested radical actually occurring in a collected coefficient and only
 * replaces the expression when it gets smaller, so it is inert for the ordinary output of the rules.
 */
public class RadicalCoefficients {

  /** Re-entrancy guard: the pass evaluates expressions and must not collect its own intermediates. */
  private static final ThreadLocal<Boolean> RUNNING = ThreadLocal.withInitial(() -> Boolean.FALSE);

  /** Coefficients above this size are not run through {@code RootReduce}. */
  private static final int MAX_COEFFICIENT_LEAFCOUNT = 80;

  private RadicalCoefficients() {}

  /**
   * Collect {@code antiderivative} by the {@code x}-dependent part of its terms and denest the
   * resulting coefficients.
   *
   * @return the collected expression, or {@link F#NIL} if there is nothing to collect or the result
   *         would not be simpler
   */
  public static IExpr collect(IExpr antiderivative, IExpr x, EvalEngine engine) {
    if (!antiderivative.isPlus() || antiderivative.isFree(x, true)) {
      return F.NIL;
    }
    // Re-evaluating a partial result would send its unevaluated Integrate() parts through the
    // integrator again, so only a finished antiderivative is collected.
    if (!antiderivative.isFreeAST(S.Integrate) || !antiderivative.isSpecialsFree()) {
      return F.NIL;
    }
    // Only nested radicals produce coefficients that Plus cannot add; without one there is nothing
    // this pass can improve, and every other result stays untouched (and unslowed).
    if (RUNNING.get() || !hasNestedRadical(antiderivative)) {
      return F.NIL;
    }
    RUNNING.set(Boolean.TRUE);
    try {
      return collectPlus((IAST) antiderivative, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      RUNNING.set(Boolean.FALSE);
    }
  }

  private static IExpr collectPlus(IAST plusAST, IExpr x, EvalEngine engine) {
    // group the terms by their x-dependent part
    Map<IExpr, IASTAppendable> groups = new LinkedHashMap<>();
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      IExpr coefficient = F.C1;
      IExpr rest = term;
      if (term.isTimes()) {
        IAST times = (IAST) term;
        IASTAppendable free = F.TimesAlloc(times.size());
        IASTAppendable nonfree = F.TimesAlloc(times.size());
        times.filter(free, nonfree, factor -> factor.isFree(x, true));
        coefficient = free.oneIdentity1();
        rest = nonfree.oneIdentity1();
      } else if (term.isFree(x, true)) {
        coefficient = term;
        rest = F.C1;
      }
      groups.computeIfAbsent(rest, key -> F.PlusAlloc(4)).append(coefficient);
    }
    boolean collectable = false;
    for (IASTAppendable coefficients : groups.values()) {
      if (coefficients.argSize() > 1) {
        collectable = true;
        break;
      }
    }
    if (!collectable) {
      return F.NIL;
    }

    IASTAppendable result = F.PlusAlloc(groups.size());
    for (Map.Entry<IExpr, IASTAppendable> group : groups.entrySet()) {
      IExpr coefficient = engine.evaluate(group.getValue().oneIdentity0());
      if (group.getValue().argSize() > 1 && hasNestedRadical(coefficient)) {
        // a coefficient that was actually summed has to come back out as a single radical -
        // otherwise the collection only trades one unwieldy form for another, so drop the whole
        // rewrite and leave the rules' output alone
        IExpr denested = denest(coefficient, engine);
        if (denested.isNIL()) {
          return F.NIL;
        }
        coefficient = denested;
      }
      result.append(F.Times(coefficient, group.getKey()));
    }
    IExpr collected = engine.evaluate(result);
    return collected.leafCount() < plusAST.leafCount() ? collected : F.NIL;
  }

  /**
   * Rewrite a real algebraic {@code coefficient} built from nested square roots as
   * {@code +-Sqrt(q)} with {@code q} in the underlying quadratic field. Returns {@link F#NIL} if
   * that does not apply or does not shorten the coefficient.
   */
  private static IExpr denest(IExpr coefficient, EvalEngine engine) {
    if (coefficient.isNumber() || !hasNestedRadical(coefficient)
        || coefficient.leafCount() > MAX_COEFFICIENT_LEAFCOUNT) {
      return F.NIL;
    }
    double value = numericValue(coefficient, engine);
    if (Double.isNaN(value) || value == 0.0) {
      return F.NIL;
    }
    final IExpr square = F.Sqr(coefficient);
    // Neither simplifier gets all of these: Simplify does not always reach the quadratic field, and
    // RootReduce sometimes returns a non-minimal (quartic) Root that ToRadicals then blows up.
    for (int strategy = 0; strategy < 2; strategy++) {
      IExpr candidate = strategy == 0 //
          ? timeConstrained(F.Simplify(square), engine)
          : timeConstrained(F.unaryAST1(S.ToRadicals, F.unaryAST1(S.RootReduce, square)), engine);
      // The square has to land in the quadratic field: a candidate that still nests radicals is
      // just the coefficient rewritten, and Sqrt() would collapse straight back to it.
      if (candidate.isNIL() || !candidate.isFreeAST(S.Root) || !candidate.isNumericFunction(true)
          || hasNestedRadical(candidate) || candidate.leafCount() >= coefficient.leafCount()) {
        continue;
      }
      // the two simplifiers spell the same number differently (1/2+Sqrt(5)/2 vs 1/2*(1+Sqrt(5)));
      // normalize so a result does not mix both spellings
      IExpr normalized = timeConstrained(F.Simplify(candidate), engine);
      if (normalized.isPresent() && normalized.leafCount() <= candidate.leafCount()
          && !hasNestedRadical(normalized)) {
        candidate = normalized;
      }
      IExpr denested = engine.evaluate(F.Sqrt(candidate));
      if (value < 0.0) {
        denested = engine.evaluate(F.Negate(denested));
      }
      if (denested.leafCount() >= coefficient.leafCount()) {
        continue;
      }
      double denestedValue = numericValue(denested, engine);
      if (Double.isNaN(denestedValue)
          || Math.abs(denestedValue - value) > 1.0e-9 * (1.0 + Math.abs(value))) {
        continue;
      }
      // the numeric agreement above only picks the branch; this is the exact check
      if (engine.evaluate(F.PossibleZeroQ(F.Subtract(square, candidate))).isTrue()) {
        return denested;
      }
    }
    return F.NIL;
  }

  /** Evaluate {@code expr} with a time limit, {@link F#NIL} if it does not finish. */
  private static IExpr timeConstrained(IExpr expr, EvalEngine engine) {
    IExpr result = engine.evaluate(F.TimeConstrained(expr, F.num(2.0), S.$Aborted));
    return result == S.$Aborted ? F.NIL : result;
  }

  /** The {@code double} value of a real, number-valued expression, or {@code NaN}. */
  private static double numericValue(IExpr expr, EvalEngine engine) {
    try {
      IExpr number = engine.evalN(expr);
      return number.isReal() ? number.evalf() : Double.NaN;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
  }

  /** True iff {@code expr} contains a fractional power whose base contains a fractional power. */
  private static boolean hasNestedRadical(IExpr expr) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (isFractionalPower(ast) && containsFractionalPower(ast.base())) {
      return true;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (hasNestedRadical(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  private static boolean containsFractionalPower(IExpr expr) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (isFractionalPower(ast)) {
      return true;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (containsFractionalPower(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  private static boolean isFractionalPower(IAST ast) {
    return ast.isPower() && ast.exponent().isRational() && !ast.exponent().isInteger();
  }
}
