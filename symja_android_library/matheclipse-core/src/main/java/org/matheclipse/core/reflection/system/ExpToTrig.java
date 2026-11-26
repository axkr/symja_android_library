package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorReplaceAll;

/**
 *
 *
 * <pre>
 * ExpToTrig(expr)
 * </pre>
 */
public class ExpToTrig extends AbstractEvaluator {

  public ExpToTrig() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr arg1 = ast.arg1();

    // (E^A)^B -> E^(A*B) holds only while -Pi < Im(A) <= Pi (or for an integer B, which is
    // merged by the evaluator already); otherwise the outer power is kept.
    IExpr flattened = arg1.replaceAll(x -> {
      if (x.isPower()) {
        IExpr base = x.base();
        if (base.isPower() && base.base() == S.E && isInPrincipalStrip(base.exponent(), engine)) {
          return F.Power(S.E, engine.evaluate(F.Times(base.exponent(), x.exponent())));
        }
      }
      return F.NIL;
    });

    if (flattened.isPresent()) {
      arg1 = engine.evaluate(flattened);
    }

    // the sums Cosh(..)+Sinh(..) the conversion creates, which a product is distributed over
    final java.util.Set<IExpr> converted =
        java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    VisitorReplaceAll visitor = new VisitorReplaceAll(x -> {
      if (x.isPower()) {
        IExpr base = x.base();
        if (base == S.E) {
          // E^(a+I*b) -> Cosh(a+I*b)+Sinh(a+I*b): the exponent isn't split into its real and
          // imaginary parts
          return coshPlusSinh(x.exponent(), converted, engine);
        }
        if (base.isNumber() && !(base.isPositive() && x.exponent().isNumericFunction(true)
            && x.exponent().isRealResult())) {
          // base^exponent => E ^(exponent*Log(base)); a positive real number like Sqrt(2) stays as
          // it is, while (-1)^(1/3) becomes Cos(Pi/3)+I*Sin(Pi/3)
          if (base.isNegative() && !base.isMinusOne()) {
            // (-b)^e -> (-1)^e * b^e, each converted on its own
            IExpr sign = coshPlusSinh(S.Expand.of(engine, F.Times(x.exponent(), F.Log(F.CN1))),
                converted, engine);
            IExpr magnitude = x.exponent().isNumericFunction(true) && x.exponent().isRealResult()
                ? F.Power(base.negate(), x.exponent())
                : coshPlusSinh(S.Expand.of(engine, F.Times(x.exponent(), F.Log(base.negate()))),
                    converted, engine);
            return F.Times(sign, magnitude);
          }
          return coshPlusSinh(S.Expand.of(engine, F.Times(x.exponent(), F.Log(base))), converted,
              engine);
        }
      } else if (x.isPlus()) {
        IExpr logRes = convertLogDifferences((IAST) x, engine);
        if (logRes.isPresent()) {
          return logRes;
        }
      }
      return F.NIL;
    });

    visitor.setPostProcessing(x -> {
      if (x.isTimes() && x.arg1().isNumber() && x.arg2().isPlus()
          && ((IAST) x).forAll(factor -> !isSumFactor(factor) || hasConverted(factor, converted),
              1)) {
        // c*(Cos(x)+I*Sin(x)+...) is expanded so that converted terms cancel; a sum without a
        // converted part keeps the product
        return F.Expand(x);
      }
      if (x.isTimes()) {
        // a*(Cos(x)+I*Sin(x)) -> a*Cos(x)+I*a*Sin(x): the other factors are distributed over the
        // one converted sum, so that terms of several exponentials combine; a factor that is a sum
        // or a power of a sum keeps the product (a+b)*(Cos(x)+I*Sin(x))
        IAST times = (IAST) x;
        int index = -1;
        for (int i = 1; i < times.size(); i++) {
          IExpr factor = times.get(i);
          if (converted.contains(factor)) {
            if (index > 0) {
              return x;
            }
            index = i;
          } else if (isSumFactor(factor) || hasConverted(factor, converted)) {
            // a sum, a power of a sum, or a product with converted sums of its own like the one
            // of (-b)^e
            return x;
          }
        }
        if (index > 0) {
          IAST sum = (IAST) times.get(index);
          IAST rest = times.removeAtCopy(index);
          IExpr factor = rest.argSize() == 1 ? rest.arg1() : rest;
          return sum.map(term -> F.Times(factor, term), 1);
        }
      }
      return x;
    });

    temp = arg1.accept(visitor);

    if (temp.isPresent()) {
      // Evaluate the generated AST so elements like 1/2*I*Sin(x) and -1/2*I*Sin(x) naturally cancel
      return engine.evaluate(temp);
    }
    return arg1;
  }

  /** Whether <code>factor</code> is a sum or a power of a sum. */
  private static boolean isSumFactor(IExpr factor) {
    return factor.isPlus() || (factor.isPower() && factor.base().isPlus());
  }

  /** Whether <code>expr</code> is or contains a sum of {@link #coshPlusSinh}. */
  private static boolean hasConverted(IExpr expr, java.util.Set<IExpr> converted) {
    return converted.contains(expr) || expr.has(converted::contains, false);
  }

  /** <code>Cosh(exponent)+Sinh(exponent)</code>, which is recorded in <code>converted</code>. */
  private static IAST coshPlusSinh(IExpr exponent, java.util.Set<IExpr> converted,
      EvalEngine engine) {
    IExpr cosh = engine.evaluate(F.Cosh(exponent));
    if (cosh.isTimes()) {
      cosh = F.evalExpand(cosh);
    }
    IExpr sinh = engine.evaluate(F.Sinh(exponent));
    if (sinh.isTimes()) {
      sinh = F.evalExpand(sinh);
    }
    IAST sum = F.Plus(cosh, sinh);
    converted.add(sum);
    return sum;
  }

  /**
   * Whether the imaginary part of <code>exponent</code> is known to lie in <code>(-Pi, Pi]</code>,
   * so that <code>Log(E^exponent) == exponent</code>.
   */
  private static boolean isInPrincipalStrip(IExpr exponent, EvalEngine engine) {
    if (exponent.isRealResult()) {
      return true;
    }
    IExpr im = engine.evaluate(F.Times(F.CNI, exponent));
    if (!im.isRealResult()) {
      return false;
    }
    IExpr range = im;
    if (!im.isNumericFunction(true)) {
      IAssumptions assumptions = engine.getAssumptions();
      if (assumptions == null) {
        return false;
      }
      range = im.isAST() ? AbstractAssumptions.assumeFunctionValue((IAST) im, false)
          : assumptions.intervalData(im);
    }
    if (range.isIntervalData()) {
      IAST intervals = (IAST) range;
      if (intervals.argSize() == 0) {
        return false;
      }
      for (int i = 1; i <= intervals.argSize(); i++) {
        if (!intervals.get(i).isList4()) {
          return false;
        }
        IAST interval = (IAST) intervals.get(i);
        IExpr lower = interval.arg1();
        IExpr upper = interval.arg4();
        boolean lowerOk = engine.evalTrue(F.Greater(lower, F.CNPi))
            || (interval.arg2() == S.Less && engine.evalTrue(F.Equal(lower, F.CNPi)));
        if (!lowerOk || !engine.evalTrue(F.LessEqual(upper, S.Pi))) {
          return false;
        }
      }
      return true;
    }
    return range.isPresent() && range.isNumericFunction(true)
        && engine.evalTrue(F.Greater(range, F.CNPi)) && engine.evalTrue(F.LessEqual(range, S.Pi));
  }

  /**
   * Identifies pairs of Log expressions C*Log(A) and -C*Log(B) inside a Plus AST and maps them to
   * their inverse Hyperbolic/Trigonometric identity: C*Log(A) - C*Log(B) -> 2*C*ArcTanh((A - B) /
   * (A + B))
   */
  private static IExpr convertLogDifferences(IAST plus, EvalEngine engine) {
    IExpr[][] extracted = new IExpr[plus.argSize() + 1][];
    int logCount = 0;
    for (int i = 1; i <= plus.argSize(); i++) {
      IExpr arg = plus.get(i);
      IExpr[] logExtract = extractLog(arg);
      if (logExtract != null) {
        extracted[i] = logExtract;
        logCount++;
      }
    }

    if (logCount < 2)
      return F.NIL;

    boolean[] used = new boolean[plus.argSize() + 1];
    boolean matchedAny = false;
    IASTAppendable newPlus = F.PlusAlloc(plus.argSize());

    for (int i = 1; i <= plus.argSize(); i++) {
      if (extracted[i] == null || used[i])
        continue;
      IExpr c_i = extracted[i][0];
      IExpr a_i = extracted[i][1];

      for (int j = i + 1; j <= plus.argSize(); j++) {
        if (extracted[j] == null || used[j])
          continue;
        IExpr c_j = extracted[j][0];
        IExpr a_j = extracted[j][1];

        IExpr sum = engine.evaluate(F.Plus(c_i, c_j));
        if (sum.isZero()) {
          IExpr num = S.Expand.of(engine, F.Subtract(a_i, a_j));
          IExpr den = S.Expand.of(engine, F.Plus(a_i, a_j));
          IExpr ratio = engine.evaluate(F.Divide(num, den));

          // Log(A) - Log(B) = 2 * ArcTanh((A - B) / (A + B))
          IExpr newTerm = engine.evaluate(F.Times(F.C2, c_i, F.ArcTanh(ratio)));

          IExpr before = engine.evaluate(
              F.Plus(F.Times(c_i, F.Log(a_i)), F.Times(c_j, F.Log(a_j))));
          if (!sameValue(before, newTerm, engine)) {
            continue;
          }

          newPlus.append(newTerm);
          used[i] = true;
          used[j] = true;
          matchedAny = true;
          break;
        }
      }
    }

    if (!matchedAny)
      return F.NIL;

    for (int i = 1; i <= plus.argSize(); i++) {
      if (!used[i]) {
        newPlus.append(plus.get(i));
      }
    }

    return engine.evaluate(newPlus.oneIdentity0());
  }

  /** Where the rewrite is looked at, chosen to sit on both sides of the origin. */
  private static final IExpr[] SAMPLE_POINTS =
      new IExpr[] {F.QQ(17, 13), F.QQ(-7, 5), F.QQ(11, 3), F.QQ(-23, 7), F.QQ(5, 9)};

  /**
   * Whether the rewritten term is the same function as the pair of logarithms it replaces.
   *
   * <p>
   * <code>Log(A) - Log(B) == Log(A/B)</code>, which is what the ArcTanh identity rests on, holds
   * only while <code>Arg(A) - Arg(B)</code> stays in <code>(-Pi, Pi]</code>; outside it the two
   * differ by <code>2*Pi*I</code>. Whether that happens cannot be read off the expressions -
   * <code>Log(2+x) - Log(2-x)</code> is safe for every real <code>x</code>, and so is
   * <code>Log(1+I*x) - Log(1-I*x)</code>, while <code>Log(a+x) - Log(x-a)</code> is not once
   * <code>a</code> is one of the fourth roots of <code>-1</code> and <code>x</code> is negative.
   * That last one is what a sum over the roots of <code>x^4+1</code> is made of, and it used to be
   * rewritten into an ArcTanh which is a different number.
   *
   * <p>
   * So the rewrite is looked at instead of assumed. A point where the two disagree settles it: the
   * identity does not hold and the pair is left alone. Points where nothing numeric comes out say
   * nothing either way, and if no point says anything the rewrite is not made.
   */
  private static boolean sameValue(IExpr before, IExpr after, EvalEngine engine) {
    if (before.equals(after)) {
      return true;
    }
    IASTAppendable symbols = F.ListAlloc();
    collectSymbols(before, symbols);
    collectSymbols(after, symbols);
    boolean seenAgreeing = false;
    for (int k = 0; k < SAMPLE_POINTS.length; k++) {
      IASTAppendable rules = F.ListAlloc(symbols.argSize());
      for (int m = 1; m <= symbols.argSize(); m++) {
        // A different value for each symbol, so that two of them cannot cancel by coincidence.
        rules.append(F.Rule(symbols.get(m), SAMPLE_POINTS[(k + m) % SAMPLE_POINTS.length]));
      }
      IExpr difference;
      try {
        difference = engine.evaluate(
            F.N(F.Abs(F.Subtract(F.subst(after, rules), F.subst(before, rules)))));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (difference.isReal()) {
        if (difference.evalf() > 1.0e-8) {
          return false;
        }
        seenAgreeing = true;
      }
    }
    return seenAgreeing;
  }

  /** The free symbols of an expression, which are what the sampling above substitutes. */
  private static void collectSymbols(IExpr expr, IASTAppendable symbols) {
    if (expr.isSymbol()) {
      if (!((ISymbol) expr).isBuiltInSymbol() && !symbols.contains(expr)) {
        symbols.append(expr);
      }
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        collectSymbols(ast.get(i), symbols);
      }
    }
  }

  /**
   * Extracts the coefficient and argument of a Log term. C*Log(A) -> {C, A}
   */
  private static IExpr[] extractLog(IExpr expr) {
    if (expr.isAST1() && expr.head() == S.Log) {
      return new IExpr[] {F.C1, expr.first()};
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      int logIndex = -1;
      int logCount = 0;
      for (int i = 1; i <= times.argSize(); i++) {
        if (times.get(i).isAST1() && times.get(i).head() == S.Log) {
          logIndex = i;
          logCount++;
        }
      }
      if (logCount == 1) {
        IExpr c = times.removeAtCopy(logIndex).oneIdentity1();
        return new IExpr[] {c, times.get(logIndex).first()};
      }
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
  }
}
