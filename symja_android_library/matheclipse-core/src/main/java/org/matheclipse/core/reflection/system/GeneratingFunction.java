package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * GeneratingFunction(expr, n, x)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Gives the ordinary generating function in <code>x</code> for the sequence whose <code>n</code>-th
 * series coefficient is given by the expression <code>expr</code>.
 * </p>
 * </blockquote>
 */
public class GeneratingFunction extends AbstractFunctionOptionEvaluator {

  /** Maximum index shift <code>k</code> which is expanded by the shift rules. */
  private static final int MAX_SHIFT = 1024;

  public GeneratingFunction() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (ast.argSize() < 3) {
      return F.NIL;
    }

    IExpr expr = ast.arg1();
    IExpr n = ast.arg2();
    IExpr x = ast.arg3();

    // Handle multidimensional generating functions
    if (n.isList() && x.isList()) {
      IAST nList = (IAST) n;
      IAST xList = (IAST) x;
      if (nList.argSize() != xList.argSize()) {
        return F.NIL;
      }
      IExpr result = expr;
      for (int i = 1; i <= nList.argSize(); i++) {
        result = engine
            .evaluate(F.ternaryAST3(S.GeneratingFunction, result, nList.get(i), xList.get(i)));
      }
      return result;
    }

    if (n.isVariable() && x.isVariable()) {
      if (!isSummable(expr, n, engine)) {
        // the sequence isn't defined for n == 0
        return F.NIL;
      }
      IExpr result = generatingFunction(expr, n, x, engine);
      if (result.isPresent()) {
        return normalize(result, x, engine);
      }
      return F.NIL;
    }

    return Errors.printMessage(ast.topHead(), "ivar", F.List(n), engine);
  }

  /**
   * Test if the sequence <code>expr</code> has a finite value at <code>n == 0</code>. Otherwise the
   * generating function series <code>Sum(expr*x^n, {n,0,Infinity})</code> isn't defined.
   */
  private static boolean isSummable(IExpr expr, IExpr n, EvalEngine engine) {
    IExpr atZero = engine.evalQuiet(F.subst(expr, n, F.C0));
    return !(atZero.isIndeterminate() || atZero.isDirectedInfinity());
  }

  /**
   * Determine the ordinary generating function <code>Sum(expr*x^n, {n,0,Infinity})</code>.
   *
   * @return {@link F#NIL} if no closed form could be determined
   */
  private static IExpr generatingFunction(IExpr expr, IExpr n, IExpr x, EvalEngine engine) {
    // constant sequence: Sum(a*x^n, {n,0,Infinity}) == a/(1-x)
    if (expr.isFree(n, true)) {
      return F.Divide(expr, F.Subtract(F.C1, x));
    }

    IExpr result = knownSequence(expr, n, x, engine);
    if (result.isPresent()) {
      return result;
    }

    result = zTransform(expr, n, x, engine);
    if (result.isPresent()) {
      return result;
    }

    result = shiftRule(expr, n, x, engine);
    if (result.isPresent()) {
      return result;
    }

    // linearity: pull out all factors which are free of n
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      IASTAppendable constants = F.TimesAlloc(times.argSize());
      IASTAppendable rest = F.TimesAlloc(times.argSize());
      times.forEach(factor -> {
        if (factor.isFree(n, true)) {
          constants.append(factor);
        } else {
          rest.append(factor);
        }
      });
      if (constants.argSize() > 0 && rest.argSize() > 0) {
        IExpr gf = generatingFunction(engine.evaluate(rest), n, x, engine);
        if (gf.isPresent()) {
          return engine.evaluate(F.Times(constants, gf));
        }
      }
      return F.NIL;
    }

    // linearity: the generating function of a sum is the sum of the generating functions
    if (expr.isPlus()) {
      IAST plus = (IAST) expr;
      IASTAppendable sum = F.PlusAlloc(plus.argSize());
      for (int i = 1; i < plus.size(); i++) {
        IExpr gf = generatingFunction(plus.get(i), n, x, engine);
        if (gf.isNIL()) {
          return F.NIL;
        }
        sum.append(gf);
      }
      return engine.evaluate(sum);
    }

    return F.NIL;
  }

  /**
   * Closed forms for a set of well known sequences.
   *
   * @return {@link F#NIL} if <code>expr</code> isn't one of the supported sequences
   */
  private static IExpr knownSequence(IExpr expr, IExpr n, IExpr x, EvalEngine engine) {
    if (expr.isAST1() && expr.first().equals(n)) {
      // -1 + x + x^2
      IAST fibonacciDenominator = F.Plus(F.CN1, x, F.Sqr(x));
      if (expr.isAST(S.Fibonacci)) {
        // -x / (-1+x+x^2)
        return F.Divide(F.Negate(x), fibonacciDenominator);
      }
      if (expr.isAST(S.LucasL)) {
        // (-2+x) / (-1+x+x^2)
        return F.Divide(F.Plus(F.CN2, x), fibonacciDenominator);
      }
      if (expr.isAST(S.CatalanNumber)) {
        // 2 / (1+Sqrt(1-4*x))
        return F.Divide(F.C2, F.Plus(F.C1, F.Sqrt(F.Subtract(F.C1, F.Times(F.C4, x)))));
      }
      if (expr.isAST(S.HarmonicNumber)) {
        // Log(1-x) / (-1+x)
        return F.Divide(F.Log(F.Subtract(F.C1, x)), F.Plus(F.CN1, x));
      }
    }

    // Binomial(2*n, n) => 1/Sqrt(1-4*x)
    if (expr.isAST(S.Binomial, 3) && expr.second().equals(n)
        && engine.evaluate(F.Subtract(expr.first(), F.Times(F.C2, n))).isZero()) {
      return F.Power(F.Subtract(F.C1, F.Times(F.C4, x)), F.CN1D2);
    }

    if (expr.isPower()) {
      IExpr base = expr.base();
      IExpr exponent = expr.exponent();
      if (base.isAST(S.Factorial, 2) && base.first().equals(n)) {
        if (exponent.isMinusOne()) {
          // 1/n! => E^x
          return F.Exp(x);
        }
        if (exponent.equals(F.CN2)) {
          // 1/(n!)^2 => BesselI(0, 2*Sqrt(x))
          return F.BesselI(F.C0, F.Times(F.C2, F.Sqrt(x)));
        }
      }
      if (exponent.isMinusOne()) {
        int k = linearShift(base, n);
        if (k > 0) {
          // 1/(n+k) with a positive integer k
          return reciprocalLinear(k, x);
        }
      }
    }

    return F.NIL;
  }

  /**
   * The generating function of <code>1/(n+k)</code> for a positive integer <code>k</code>.
   *
   * <p>
   * <code>Sum(x^n/(n+1), {n,0,Infinity}) == -Log(1-x)/x</code> and the higher indices are derived
   * from the index shift rule
   * <code>Sum(x^n/(n+1+s)) == (Sum(x^n/(n+1)) - Sum(x^j/(j+1), {j,0,s-1})) / x^s</code>.
   */
  private static IExpr reciprocalLinear(int k, IExpr x) {
    // -Log(1-x)/x
    IExpr harmonicGF = F.Times(F.CN1, F.Power(x, F.CN1), F.Log(F.Subtract(F.C1, x)));
    int shift = k - 1;
    if (shift == 0) {
      return harmonicGF;
    }
    if (shift > MAX_SHIFT) {
      return F.NIL;
    }
    IASTAppendable numerator = F.PlusAlloc(shift + 1);
    numerator.append(harmonicGF);
    for (int j = 0; j < shift; j++) {
      numerator.append(F.Times(F.QQ(-1, j + 1), F.Power(x, j)));
    }
    return F.Divide(numerator, F.Power(x, shift));
  }

  /**
   * Index shift rule for an arbitrary sequence <code>f</code>:
   * <code>GeneratingFunction(f(n+k), n, x) ==
   * (GeneratingFunction(f(n), n, x) - Sum(f(j)*x^j, {j,0,k-1})) / x^k</code>.
   *
   * @return {@link F#NIL} if <code>expr</code> isn't of the form <code>f(n+k)</code>
   */
  private static IExpr shiftRule(IExpr expr, IExpr n, IExpr x, EvalEngine engine) {
    if (!expr.isAST1() || !expr.head().isSymbol()) {
      return F.NIL;
    }
    int k = linearShift(expr.first(), n);
    if (k <= 0 || k > MAX_SHIFT) {
      return F.NIL;
    }
    IExpr head = expr.head();
    IExpr baseGF =
        engine.evaluate(F.ternaryAST3(S.GeneratingFunction, F.unaryAST1(head, n), n, x));
    IASTAppendable numerator = F.PlusAlloc(k + 1);
    numerator.append(baseGF);
    for (int j = 0; j < k; j++) {
      numerator.append(
          F.Negate(F.Times(engine.evaluate(F.unaryAST1(head, F.ZZ(j))), F.Power(x, j))));
    }
    return F.Divide(numerator, F.Power(x, k));
  }

  /**
   * If <code>expr</code> has the form <code>n + k</code> for a positive integer <code>k</code>
   * return <code>k</code>.
   *
   * @return <code>-1</code> if <code>expr</code> isn't of the form <code>n + k</code>
   */
  private static int linearShift(IExpr expr, IExpr n) {
    if (expr.isPlus() && expr.size() == 3) {
      IAST plus = (IAST) expr;
      IExpr shift = F.NIL;
      if (plus.arg1().equals(n)) {
        shift = plus.arg2();
      } else if (plus.arg2().equals(n)) {
        shift = plus.arg1();
      }
      if (shift.isInteger() && shift.isPositive()) {
        int k = shift.toIntDefault();
        if (k > 0) {
          return k;
        }
      }
    }
    return -1;
  }

  /**
   * Determine the generating function with the {@link S#ZTransform} of the sequence:
   * <code>GeneratingFunction(expr, n, x) == ZTransform(expr, n, z) /. z -> 1/x</code>.
   *
   * @return {@link F#NIL} if the Z transform of <code>expr</code> couldn't be determined
   */
  private static IExpr zTransform(IExpr expr, IExpr n, IExpr x, EvalEngine engine) {
    IExpr zDummy = F.Dummy("zGF");
    IExpr zTrans = engine.evalQuiet(F.ZTransform(expr, n, zDummy));
    if (zTrans.isPresent() && zTrans.isFreeAST(S.ZTransform) && !zTrans.isIndeterminate()
        && !zTrans.isDirectedInfinity()) {
      return engine.evaluate(F.subst(zTrans, zDummy, F.Power(x, F.CN1)));
    }
    return F.NIL;
  }

  /**
   * If the result is a rational function in <code>x</code> return it with an expanded numerator and
   * a factored denominator. Otherwise return <code>result</code> unchanged.
   */
  private static IExpr normalize(IExpr result, IExpr x, EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(result));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (engine.evalTrue(F.PolynomialQ(numerator, x))
        && engine.evalTrue(F.PolynomialQ(denominator, x))) {
      IExpr quotient =
          engine.evaluate(F.Divide(F.Expand(numerator), F.Factor(denominator)));
      // expand the numerator again, because Factor() may have moved a sign into it
      return engine.evaluate(
          F.Divide(F.Expand(F.Numerator(quotient)), F.Denominator(quotient)));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.READPROTECTED);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions}, //
        new IExpr[] {S.$Assumptions, S.False});
    super.setUp(newSymbol);
  }


}
