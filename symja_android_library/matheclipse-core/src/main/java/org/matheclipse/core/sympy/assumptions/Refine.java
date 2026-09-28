package org.matheclipse.core.sympy.assumptions;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.sympy.core.Traversal;

/**
 * Handlers for the refinement of expressions with assumptions. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/assumptions/refine.py">sympy/assumptions/refine.py</a>
 *
 * <p>
 * Only the handlers which aren't covered by the evaluation with assumptions are ported. The
 * assumptions have to be set in the evaluation engine.
 */
public class Refine {

  private Refine() {}

  /**
   * Apply the handlers to all subexpressions.
   *
   * @param expr the expression which was already evaluated with the assumptions
   * @param engine the evaluation engine with the assumptions
   * @return the refined expression or <code>expr</code> if no handler could be applied
   */
  public static IExpr refine(IExpr expr, EvalEngine engine) {
    if (expr.isFree(x -> x == S.ArcTan || x == S.Sign || x == S.Sin || x == S.Cos
        || x == S.Floor || x == S.Ceiling || x == S.Re || x == S.Im || x.isPower(), true)) {
      return expr;
    }
    IExpr result = Traversal.bottomUp(expr, x -> refineHandler(x, engine).orElse(x));
    return result.equals(expr) ? expr : engine.evaluate(result);
  }

  /**
   * Apply the handler for the head of the expression.
   *
   * @return {@link F#NIL} if no handler could be applied
   */
  private static IExpr refineHandler(IExpr x, EvalEngine engine) {
    try {
      if (x.isAST(S.ArcTan, 3)) {
        return refineAtan2((IAST) x, engine);
      }
      if (x.isPower()) {
        if (x.base().equals(S.E)) {
          return refineExp((IAST) x, engine);
        }
        return refinePow((IAST) x, engine);
      }
      if (x.isAST(S.Sign, 2)) {
        return refineSign((IAST) x, engine);
      }
      if (x.isSin() || x.isCos()) {
        return refineSinCos((IAST) x, engine);
      }
      if (x.isAST(S.Floor, 2) || x.isAST(S.Ceiling, 2)) {
        return refineFloorCeiling((IAST) x, engine);
      }
      if (x.isAST(S.Re, 2) || x.isAST(S.Im, 2)) {
        return refineReIm((IAST) x, engine);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  private static boolean isEven(IExpr expr, EvalEngine engine) {
    return isEven(expr, engine, 2);
  }

  private static boolean isOdd(IExpr expr, EvalEngine engine) {
    return isOdd(expr, engine, 2);
  }

  private static boolean isInteger(IExpr expr, EvalEngine engine) {
    return isInteger(expr, engine, 2);
  }

  /** <code>expr</code> is even, if <code>expr/2</code> is an integer */
  private static boolean isEven(IExpr expr, EvalEngine engine, int depth) {
    if (expr.isInteger()) {
      return ((IInteger) expr).isEven();
    }
    if (expr.isNumber()) {
      return false;
    }
    return isInteger(engine.evaluate(F.Times(F.C1D2, expr)), engine, depth - 1);
  }

  /** <code>expr</code> is odd, if <code>(expr-1)/2</code> is an integer */
  private static boolean isOdd(IExpr expr, EvalEngine engine, int depth) {
    if (expr.isInteger()) {
      return ((IInteger) expr).isOdd();
    }
    if (expr.isNumber()) {
      return false;
    }
    return isInteger(engine.evaluate(F.Times(F.C1D2, F.Plus(F.CN1, expr))), engine, depth - 1);
  }

  private static boolean isInteger(IExpr expr, EvalEngine engine, int depth) {
    if (expr.isInteger() || expr.isIntegerResult()) {
      return true;
    }
    if (expr.isNumber() || depth <= 0) {
      return false;
    }
    if (isZero(expr, engine)) {
      return true;
    }
    return isEven(expr, engine, depth) || isOdd(expr, engine, depth);
  }

  /**
   * Handler for instances of <code>Power</code>.
   */
  public static IExpr refinePow(IAST power, EvalEngine engine) {
    // >>> refine_Pow((-1)**x, Q.even(x))
    // 1
    // >>> refine_Pow((-1)**x, Q.odd(x))
    // -1
    // For powers of -1, individual terms can be removed:
    // >>> refine_Pow((-1)**(x+y), Q.even(x))
    // (-1)**y
    // >>> refine_Pow((-1)**(x+y+z), Q.odd(x) & Q.odd(z))
    // (-1)**y
    // >>> refine_Pow((-1)**(x+y+2), Q.odd(x))
    // (-1)**(y + 1)
    // >>> refine_Pow((-1)**(x+3), True)
    // (-1)**(x + 1)
    IExpr base = power.base();
    IExpr exponent = power.exponent();
    if (base.isAbs()) {
      // (Abs(arg))^e -> arg^e for even e and real arg
      if (isEven(exponent, engine) && base.first().isRealResult()) {
        return F.Power(base.first(), exponent);
      }
      return F.NIL;
    }
    if (!base.isReal()) {
      // the rule for a base expr.base.base**expr.base.exp with a rational exponent isn't ported,
      // because Abs(b)**(p*r) is wrong for odd p (for example ((-1)**3)**(1/3))
      return F.NIL;
    }
    if (!base.isMinusOne()) {
      if (base.isNegative()) {
        if (isEven(exponent, engine)) {
          // abs(expr.base) ** expr.exp
          return F.Power(base.negate(), exponent);
        }
        if (isOdd(exponent, engine)) {
          // sign(expr.base) * abs(expr.base) ** expr.exp
          return F.Negate(F.Power(base.negate(), exponent));
        }
      }
      return F.NIL;
    }
    if (isEven(exponent, engine)) {
      return F.C1;
    }
    if (isOdd(exponent, engine)) {
      return F.CN1;
    }
    if (!exponent.isPlus()) {
      return F.NIL;
    }
    // For powers of (-1) we can remove
    // - even terms
    // - pairs of odd terms
    // - a single odd term + 1
    // - A numerical constant N can be replaced with mod(N,2)
    IAST plus = (IAST) exponent;
    IExpr coeff = F.C0;
    IASTAppendable terms = F.PlusAlloc(plus.argSize());
    int oddTerms = 0;
    boolean removed = false;
    for (int i = 1; i < plus.size(); i++) {
      IExpr t = plus.get(i);
      if (t.isRational()) {
        coeff = coeff.plus(t);
      } else if (isEven(t, engine)) {
        removed = true;
      } else if (isOdd(t, engine)) {
        oddTerms++;
        removed = true;
      } else {
        terms.append(t);
      }
    }
    IExpr newCoeff = (oddTerms & 1) == 1 ? coeff.plus(F.C1) : coeff;
    newCoeff = engine.evaluate(F.Mod(newCoeff, F.C2));
    IExpr newExponent = exponent;
    if (removed || !newCoeff.equals(coeff)) {
      terms.append(newCoeff);
      newExponent = engine.evaluate(terms);
    }
    // Handle (-1)**((-1)**n/2 + m/2)
    IExpr e2 = engine.evaluate(F.Expand(F.Times(F.C2, newExponent)));
    if (e2.isPlus() && e2.argSize() == 2 && e2.first().isInteger()) {
      IExpr i = e2.first();
      IExpr p = e2.second();
      if (p.isPower() && p.base().isMinusOne() && isInteger(p.exponent(), engine)) {
        i = engine.evaluate(F.Times(F.C1D2, F.Plus(i, F.C1)));
        if (isEven(i, engine)) {
          return F.Power(F.CN1, p.exponent());
        } else if (isOdd(i, engine)) {
          return F.Power(F.CN1, F.Plus(p.exponent(), F.C1));
        }
        return F.Power(F.CN1, F.Plus(p.exponent(), i));
      }
    }
    if (!newExponent.equals(exponent)) {
      return F.Power(F.CN1, newExponent);
    }
    return F.NIL;
  }

  /**
   * Handler for exponential function: <code>E^(I*Pi*k) -&gt; (-1)^k</code> for the integer terms
   * <code>k</code> of the coefficient of <code>I*Pi</code>.
   */
  public static IExpr refineExp(IAST exp, EvalEngine engine) {
    // >>> refine_exp(exp(pi*I*2*x), Q.integer(x))
    // 1
    // >>> refine_exp(exp(pi*I*2*(x + 1/2)), Q.integer(x))
    // -1
    // >>> refine_exp(exp(pi*I*2*(x + 1/4)), Q.integer(x))
    // I
    // >>> refine_exp(exp(pi*I*2*(x + 3/4)), Q.integer(x))
    // -I
    IExpr coeff = engine.evaluate(F.Expand(F.Times(exp.exponent(), F.Power(F.Times(F.CI, S.Pi), F.CN1))));
    if (!coeff.isFree(S.Pi, true) || !coeff.isFree(t -> t.isNumber() && !t.isReal(), true)) {
      return F.NIL;
    }
    IAST terms = coeff.isPlus() ? (IAST) coeff : F.Plus(coeff);
    IASTAppendable integerTerms = F.PlusAlloc(terms.argSize());
    IASTAppendable remainingTerms = F.PlusAlloc(terms.argSize());
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      if (isInteger(term, engine)) {
        integerTerms.append(term);
      } else {
        remainingTerms.append(term);
      }
    }
    if (integerTerms.argSize() == 0) {
      return F.NIL;
    }
    // (-1)**integer_part * phase
    IAST minusOnePower = F.Power(F.CN1, engine.evaluate(integerTerms.oneIdentity0()));
    IExpr sign = refinePow(minusOnePower, engine).orElse(minusOnePower);
    return F.Times(sign, F.Exp(F.Times(F.CI, S.Pi, remainingTerms.oneIdentity0())));
  }

  /**
   * Handler for <code>Sin</code> and <code>Cos</code>: remove the multiples of <code>Pi/2</code>
   * with known parity from the argument.
   */
  public static IExpr refineSinCos(IAST expr, EvalEngine engine) {
    // >>> refine_sin_cos(cos(n*pi), Q.even(n))
    // 1
    // >>> refine_sin_cos(sin(n*pi/2), Q.odd(n))
    // (-1)**(n/2 + 3/2)
    // >>> refine_sin_cos(cos(x + n*pi), Q.odd(n))
    // -cos(x)
    IExpr arg = expr.arg1();
    final boolean isSin = expr.isSin();
    if (isZero(arg, engine)) {
      return isSin ? F.C0 : F.C1;
    }
    IAST terms = engine.evaluate(F.Expand(arg)).isPlus() ? (IAST) engine.evaluate(F.Expand(arg))
        : F.Plus(engine.evaluate(F.Expand(arg)));
    IASTAppendable remainingTerms = F.PlusAlloc(terms.argSize());
    IExpr known = F.C0;
    IExpr unknown = F.C0;
    boolean knownIsEven = true;
    boolean found = false;
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      IExpr coeffOfPiHalf = F.NIL;
      if (!term.isFree(S.Pi, false)) {
        IExpr c = engine.evaluate(F.Times(F.C2, term, F.Power(S.Pi, F.CN1)));
        if (c.isFree(S.Pi, true) && isInteger(c, engine)) {
          coeffOfPiHalf = c;
        }
      }
      if (coeffOfPiHalf.isNIL()) {
        remainingTerms.append(term);
        continue;
      }
      found = true;
      if (isEven(coeffOfPiHalf, engine)) {
        known = known.plus(coeffOfPiHalf);
      } else if (isOdd(coeffOfPiHalf, engine)) {
        known = known.plus(coeffOfPiHalf);
        knownIsEven = !knownIsEven;
      } else {
        unknown = unknown.plus(coeffOfPiHalf);
      }
    }
    known = engine.evaluate(known);
    if (!found || known.isZero()) {
      return F.NIL;
    }
    // Treat sin as a phase-shifted cosine so a single logic path can handle both.
    IExpr k = isSin ? engine.evaluate(F.Subtract(known, F.C1)) : known;
    boolean kIsEven = isSin ? !knownIsEven : knownIsEven;
    IExpr rem = F.Plus(remainingTerms.oneIdentity0(), F.Times(unknown, S.Pi, F.C1D2));
    IExpr remEvaled = engine.evaluate(rem);
    if (!remEvaled.isZero() && remEvaled.isNumericFunction(true)) {
      // deviation from sympy, confirmed with WMA: Refine(Sin(Pi*(1/4+m)), Element(m, Integers))
      // stays Sin((1/4+m)*Pi)
      return F.NIL;
    }
    // If k is even: `cos(rem + k*pi/2)` -> `(-1)^(k/2) * cos(rem)`
    // If k is odd: `cos(rem + k*pi/2)` -> `(-1)^((k+1)/2) * sin(rem)`
    IAST powExpr = kIsEven ? F.Power(F.CN1, engine.evaluate(F.Times(F.C1D2, k)))
        : F.Power(F.CN1, engine.evaluate(F.Times(F.C1D2, F.Plus(k, F.C1))));
    IExpr sign = refinePow(powExpr, engine).orElse(powExpr);
    return F.Times(sign, kIsEven ? F.Cos(rem) : F.Sin(rem));
  }

  /**
   * Handler for <code>Floor</code> and <code>Ceiling</code>: move the integer terms out of the
   * function.
   */
  public static IExpr refineFloorCeiling(IAST expr, EvalEngine engine) {
    // >>> refine_floor_ceiling(floor(x + y), Q.integer(x))
    // x + floor(y)
    IExpr arg = expr.arg1();
    if (isInteger(arg, engine)) {
      return arg;
    }
    if (arg.isPlus()) {
      IAST plus = (IAST) arg;
      IASTAppendable integerTerms = F.PlusAlloc(plus.argSize());
      IASTAppendable otherTerms = F.PlusAlloc(plus.argSize());
      for (int i = 1; i < plus.size(); i++) {
        IExpr term = plus.get(i);
        if (isInteger(term, engine) || term.isAST(S.Floor, 2) || term.isAST(S.Ceiling, 2)) {
          integerTerms.append(term);
        } else {
          otherTerms.append(term);
        }
      }
      if (integerTerms.argSize() > 0) {
        return F.Plus(integerTerms.oneIdentity0(),
            F.unaryAST1(expr.head(), otherTerms.oneIdentity0()));
      }
    }
    return F.NIL;
  }

  /**
   * Handler for <code>Re</code> and <code>Im</code>: expand the complex argument, if all variables
   * are real.
   */
  public static IExpr refineReIm(IAST expr, EvalEngine engine) {
    // >>> refine(re(1/(x + I*y)), Q.real(x) & Q.real(y))
    // x/(x**2 + y**2)
    IExpr arg = expr.arg1();
    IExpr variables = engine.evaluate(F.Variables(arg));
    if (!variables.isList() || variables.argSize() == 0) {
      return F.NIL;
    }
    IAST list = (IAST) variables;
    for (int i = 1; i < list.size(); i++) {
      IExpr v = list.get(i);
      if (!v.isSymbol() || !v.isRealResult()) {
        return F.NIL;
      }
    }
    // expr.expand(complex = True)
    // multiply numerator and denominator with the conjugate denominator, because ComplexExpand
    // returns a polar form for quotients
    IExpr together = engine.evaluate(F.Together(arg));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    IExpr conjugate = engine.evaluate(F.ComplexExpand(F.Conjugate(denominator)));
    IExpr newNumerator = engine.evaluate(F.Expand(F.Times(numerator, conjugate)));
    IExpr newDenominator = engine.evaluate(F.Expand(F.ComplexExpand(F.Times(denominator, conjugate))));
    if (!newDenominator.isFree(S.I, true) || !newDenominator.isFree(t -> t.isNumber() && !t.isReal(), true)) {
      return F.NIL;
    }
    IExpr part = engine.evaluate(F.ComplexExpand(F.unaryAST1(expr.head(), newNumerator)));
    if (part.isFree(S.Re, true) && part.isFree(S.Im, true) && part.isFree(S.Arg, true)
        && part.isFree(S.Abs, true)) {
      return engine.evaluate(F.Together(F.Times(part, F.Power(newDenominator, F.CN1))));
    }
    return F.NIL;
  }

  /**
   * Handler for the two argument form <code>ArcTan(x, y)</code>.
   */
  public static IExpr refineAtan2(IAST arcTan, EvalEngine engine) {
    // >>> refine_atan2(atan2(y,x), Q.real(y) & Q.positive(x))
    // atan(y/x)
    // >>> refine_atan2(atan2(y,x), Q.negative(y) & Q.negative(x))
    // atan(y/x) - pi
    // >>> refine_atan2(atan2(y,x), Q.positive(y) & Q.negative(x))
    // atan(y/x) + pi
    // >>> refine_atan2(atan2(y,x), Q.zero(y) & Q.negative(x))
    // pi
    // >>> refine_atan2(atan2(y,x), Q.positive(y) & Q.zero(x))
    // pi/2
    // >>> refine_atan2(atan2(y,x), Q.negative(y) & Q.zero(x))
    // -pi/2
    // >>> refine_atan2(atan2(y,x), Q.zero(y) & Q.zero(x))
    // nan
    IExpr x = arcTan.arg1();
    IExpr y = arcTan.arg2();
    if (isZero(x, engine) && isZero(y, engine)) {
      return S.Indeterminate;
    }
    if (x.isPositiveResult()) {
      if (y.isRealResult()) {
        return F.ArcTan(F.Times(y, F.Power(x, F.CN1)));
      }
    } else if (x.isNegativeResult()) {
      if (y.isNegativeResult()) {
        return F.Plus(F.ArcTan(F.Times(y, F.Power(x, F.CN1))), F.Negate(S.Pi));
      } else if (y.isPositiveResult()) {
        return F.Plus(F.ArcTan(F.Times(y, F.Power(x, F.CN1))), S.Pi);
      } else if (isZero(y, engine)) {
        return S.Pi;
      }
    } else if (isZero(x, engine)) {
      if (y.isPositiveResult()) {
        return F.CPiHalf;
      } else if (y.isNegativeResult()) {
        return F.CNPiHalf;
      }
    }
    return F.NIL;
  }

  private static boolean isZero(IExpr expr, EvalEngine engine) {
    if (expr.isZero()) {
      return true;
    }
    if (expr.isNumber()) {
      return false;
    }
    return engine.evaluate(F.Equal(expr, F.C0)).isTrue();
  }

  private static boolean isNonZero(IExpr expr, EvalEngine engine) {
    if (expr.isNumber()) {
      return !expr.isZero();
    }
    if (expr.isAbs()) {
      return isNonZero(expr.first(), engine);
    }
    return engine.evaluate(F.Unequal(expr, F.C0)).isTrue();
  }

  /**
   * Handler for sign.
   */
  public static IExpr refineSign(IAST sign, EvalEngine engine) {
    // >>> refine_sign(sign(x), Q.positive(x) & Q.nonzero(x))
    // 1
    // >>> refine_sign(sign(x), Q.negative(x) & Q.nonzero(x))
    // -1
    // >>> refine_sign(sign(x), Q.zero(x))
    // 0
    // >>> refine_sign(sign(y), Q.positive(im(y)))
    // I
    // >>> refine_sign(sign(y), Q.negative(im(y)))
    // -I
    IExpr arg = sign.arg1();
    if (isZero(arg, engine)) {
      return F.C0;
    }
    if (arg.isRealResult()) {
      if (arg.isPositiveResult()) {
        return F.C1;
      }
      if (arg.isNegativeResult()) {
        return F.CN1;
      }
      if (isNonZero(arg, engine)) {
        if (arg.isNonNegativeResult()) {
          return F.C1;
        }
        if (arg.isNonPositiveResult()) {
          return F.CN1;
        }
      }
      return F.NIL;
    }
    if (isZero(F.Re(arg), engine)) {
      // imaginary argument
      IExpr im = engine.evaluate(F.Im(arg));
      if (im.isPositiveResult() || engine.evaluate(F.Greater(F.Im(arg), F.C0)).isTrue()) {
        return F.CI;
      }
      if (im.isNegativeResult() || engine.evaluate(F.Less(F.Im(arg), F.C0)).isTrue()) {
        return F.CNI;
      }
    }
    return F.NIL;
  }
}
