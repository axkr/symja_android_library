package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * TrigToExp(expr)
 * </pre>
 *
 * <blockquote>
 * <p>
 * converts trigonometric functions in <code>expr</code> to exponentials. </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TrigToExp(Cos(x))
 * 1/2*E^(I*x)+1/2*E^(-I*x)
 * </pre>
 */
public class TrigToExp extends AbstractEvaluator {

  /** The largest power whose harmonics are compared with the other summands. */
  private static final int MAX_HARMONIC_POWER = 4;

  public TrigToExp() {}

  /**
   * Exponential definitions for trigonometric functions
   *
   * <p>
   * See <a href=
   * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"> List
   * of trigonometric identities - Exponential definitions</a>,<br>
   * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }
    return trigToExp(ast.arg1(), engine);
  }

  /**
   * Convert the trigonometric and hyperbolic functions in <code>expr</code> to exponentials.
   *
   * <p>
   * A single sine or cosine (or its square, possibly times factors free of trigonometric
   * functions), a sum, and a monomial times one transformed sum are expanded. Products of several
   * transformed functions, higher powers of sine and cosine and powers of the quotient functions
   * are kept grouped: <code>Sin(x)^3</code> becomes <code>-I/8*(E^(-I*x)-E^(I*x))^3</code>. A sum
   * whose higher powers share a harmonic with another summand is expanded, so that the harmonics
   * combine.
   *
   * @param expr
   * @param engine
   * @return
   */
  private static IExpr trigToExp(IExpr expr, EvalEngine engine) {
    if (convert(expr, false).equals(expr)) {
      return expr;
    }
    boolean[] flag = new boolean[1];
    int count = grouping(expr, flag);
    boolean grouped = flag[0] && !hasCombiningHarmonics(expr);

    int expandableCount = 0;
    boolean otherTransformedFactor = false;
    int sumFactors = 0;
    IAST factors = expr.isTimes() ? (IAST) expr : F.Times(expr);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      IExpr base = factor.isPower() && factor.exponent().isInteger() && factor.exponent().equals(F.C2)
          ? factor.base()
          : factor;
      if (isSinCosLike(base)) {
        expandableCount++;
      } else if (!convert(factor, false).equals(factor)) {
        otherTransformedFactor = true;
      }
      if (factor.isPlus()) {
        sumFactors++;
      }
    }
    boolean monomialTimesTransformedSum = count == 1 && factors.argSize() > 1 && sumFactors == 1;

    IExpr transformed = engine.evaluate(convert(expr, grouped));
    if (!grouped && ((expandableCount == 1 && !otherTransformedFactor)
        || sinCosDegree(factors) == 2 || monomialTimesTransformedSum || expr.isPlus())) {
      return expandExponents(engine.evaluate(F.Expand(transformed)), engine);
    }
    return transformed;
  }

  /**
   * Expand the exponents of {@link S#E} which the expansion of products and powers of exponentials
   * created: <code>E^(2*(I*x+I*y))</code> becomes <code>E^(2*I*x+2*I*y)</code>.
   *
   * @param expr
   * @param engine
   * @return
   */
  private static IExpr expandExponents(IExpr expr, EvalEngine engine) {
    IExpr result = expr.replaceAll(x -> {
      if (x.isPower() && x.base().isE() && x.exponent().isAST()) {
        IExpr exponent = F.evalExpand(x.exponent());
        if (!exponent.equals(x.exponent())) {
          return F.Power(S.E, exponent);
        }
      }
      return F.NIL;
    });
    return result.isPresent() ? engine.evaluate(result) : expr;
  }

  /**
   * Count the trigonometric and hyperbolic functions reachable through {@link ID#Plus},
   * {@link ID#Times} and {@link ID#Power}. <code>grouped[0]</code> is set if the expression contains
   * a product of several such functions, a power greater than 2 of a sine or cosine, or a power of
   * a quotient function like {@link ID#Tan}.
   *
   * @param expr
   * @param grouped
   * @return
   */
  private static int grouping(IExpr expr, boolean[] grouped) {
    if (isTrigFunction(expr)) {
      return 1;
    }
    if (expr.isPlus() || expr.isTimes() || expr.isPower()) {
      IAST ast = (IAST) expr;
      int count = 0;
      for (int i = 1; i < ast.size(); i++) {
        count += grouping(ast.get(i), grouped);
      }
      if (expr.isPower()) {
        int n = expr.exponent().toIntDefault();
        if (n != Integer.MIN_VALUE) {
          IExpr base = expr.base();
          if ((n > 1 && isQuotientLike(base)) || (n > 2 && isSinCosLike(base))) {
            grouped[0] = true;
          }
        }
      } else if (expr.isTimes() && count > 1 && sinCosDegree((IAST) expr) != 2) {
        grouped[0] = true;
      }
      return count;
    }
    return 0;
  }

  /**
   * The total degree of the sine and cosine like factors of the product <code>times</code>, which
   * have the same angle: <code>2</code> for <code>Sin(x)*Cos(x)</code>. <code>-1</code> if the
   * angles differ or another factor contains a trigonometric function.
   *
   * @param times
   * @return
   */
  private static int sinCosDegree(IAST times) {
    int degree = 0;
    IExpr angle = null;
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      IExpr function = factor.isPower() ? factor.base() : factor;
      if (isSinCosLike(function)) {
        if (angle != null && !angle.equals(function.first())) {
          return -1;
        }
        angle = function.first();
      }
      if (isSinCosLike(factor)) {
        degree++;
      } else if (factor.isPower() && isSinCosLike(factor.base())
          && factor.exponent().isPositiveResult() && factor.exponent().isInteger()) {
        int n = factor.exponent().toIntDefault();
        if (n == Integer.MIN_VALUE) {
          return -1;
        }
        degree += n;
      } else if (grouping(factor, new boolean[1]) > 0) {
        return -1;
      }
    }
    return degree;
  }

  /**
   * Test if a sum contains powers greater than 2 of sines or cosines whose harmonics all occur in
   * another summand. Then the expanded exponentials combine.
   *
   * @param expr
   * @return
   */
  private static boolean hasCombiningHarmonics(IExpr expr) {
    if (!expr.isPlus()) {
      return false;
    }
    IAST plus = (IAST) expr;
    List<Integer> powers = new ArrayList<>();
    List<List<IExpr>> spectra = new ArrayList<>();
    for (int i = 1; i < plus.size(); i++) {
      IExpr term = plus.get(i);
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      List<IExpr> spectrum = null;
      for (int j = 1; j < factors.size(); j++) {
        IExpr factor = factors.get(j);
        if (grouping(factor, new boolean[1]) == 0) {
          continue;
        }
        if (spectrum != null) {
          return false;
        }
        IExpr base = factor;
        int n = 1;
        if (factor.isPower()) {
          n = factor.exponent().toIntDefault();
          base = factor.base();
        }
        if (n < 1 || n > MAX_HARMONIC_POWER || !isSinCosLike(base)) {
          return false;
        }
        boolean hyperbolic = base.isSinh() || base.isCosh();
        spectrum = new ArrayList<>();
        for (int k = -n; k <= n; k += 2) {
          if (k != 0) {
            IExpr angle = F.evalExpand(F.Times(F.ZZ(k), base.first()));
            spectrum.add(hyperbolic ? F.Sinh(angle) : angle);
          }
        }
        powers.add(n);
        spectra.add(spectrum);
      }
    }
    boolean hasPower = false;
    for (int i = 0; i < spectra.size(); i++) {
      if (powers.get(i) <= 2) {
        continue;
      }
      hasPower = true;
      boolean shared = false;
      for (int j = 0; j < spectra.size() && !shared; j++) {
        if (i != j) {
          for (IExpr harmonic : spectra.get(i)) {
            if (spectra.get(j).contains(harmonic)) {
              shared = true;
              break;
            }
          }
        }
      }
      if (!shared) {
        return false;
      }
    }
    return hasPower;
  }

  /**
   * Convert <code>expr</code> without evaluation. Arguments of functions which aren't built-in
   * symbols are left unchanged.
   *
   * @param expr
   * @param grouped keep single sines and cosines as constant times sum of exponentials
   * @return
   */
  private static IExpr convert(IExpr expr, boolean grouped) {
    if (!expr.isAST()) {
      return expr;
    }
    if (grouped && isSinCosLike(expr)) {
      return F.Times(scalar(expr), unscaledSum(expr, grouped));
    }
    if (expr.isPower() && isSinCosLike(expr.base())) {
      int n = expr.exponent().toIntDefault();
      if (n >= 2) {
        IExpr base = expr.base();
        return F.Times(F.Power(scalar(base), expr.exponent()),
            F.Power(unscaledSum(base, grouped), expr.exponent()));
      }
    }
    IAST ast = (IAST) expr;
    if (isTrigFunction(expr)) {
      return exponentialForm(ast.headID(), convert(ast.arg1(), grouped));
    }
    if (!ast.head().isBuiltInSymbol()) {
      return expr;
    }
    IAST result = ast.map(x -> convert(x, grouped), 1);
    IExpr rewritten = result.rewrite(ID.Exp);
    if (rewritten.isPresent()) {
      return rewritten;
    }
    return result.rewrite(ID.Log).orElse(result);
  }

  /** <code>I/2</code> for {@link ID#Sin}, <code>1/2</code> for the other functions. */
  private static IExpr scalar(IExpr sinCosLike) {
    return sinCosLike.isSin() ? F.CC(0L, 1L, 1L, 2L) : F.C1D2;
  }

  /** The sum of exponentials of a sine or cosine like function divided by {@link #scalar(IExpr)}. */
  private static IExpr unscaledSum(IExpr sinCosLike, boolean grouped) {
    IExpr[] e = exponentials(isHyperbolic(sinCosLike.headID()),
        convert(sinCosLike.first(), grouped));
    switch (sinCosLike.headID()) {
      case ID.Sin:
        return F.Subtract(e[1], e[0]);
      case ID.Sinh:
        return F.Subtract(e[0], e[1]);
      default:
        return F.Plus(e[1], e[0]);
    }
  }

  /**
   * <code>{E^(I*arg), E^(-I*arg)}</code> for circular functions, <code>{E^arg, E^(-arg)}</code> for
   * hyperbolic functions. The exponents are expanded: <code>E^(I*x+I*y)</code>.
   */
  private static IExpr[] exponentials(boolean hyperbolic, IExpr arg) {
    IExpr phase = hyperbolic ? arg : F.Times(F.CI, arg);
    return new IExpr[] {F.Exp(F.evalExpand(phase)), F.Exp(F.evalExpand(F.Negate(phase)))};
  }

  private static IExpr exponentialForm(int headID, IExpr arg) {
    IExpr[] e = exponentials(isHyperbolic(headID), arg);
    IExpr ePos = e[0];
    IExpr eNeg = e[1];
    switch (headID) {
      case ID.Cos:
      case ID.Cosh:
        return F.Plus(F.Times(F.C1D2, eNeg), F.Times(F.C1D2, ePos));
      case ID.Sin:
        return F.Plus(F.Times(F.CC(0L, 1L, 1L, 2L), eNeg), F.Times(F.CC(0L, 1L, -1L, 2L), ePos));
      case ID.Sinh:
        return F.Plus(F.Times(F.CN1D2, eNeg), F.Times(F.C1D2, ePos));
      case ID.Tan:
        return F.Times(F.CI, F.Subtract(eNeg, ePos), F.Power(F.Plus(eNeg, ePos), F.CN1));
      case ID.Cot:
        return F.Times(F.CNI, F.Plus(eNeg, ePos), F.Power(F.Subtract(eNeg, ePos), F.CN1));
      case ID.Sec:
      case ID.Sech:
        return F.Times(F.C2, F.Power(F.Plus(eNeg, ePos), F.CN1));
      case ID.Csc:
        return F.Times(F.CN2, F.CI, F.Power(F.Subtract(eNeg, ePos), F.CN1));
      case ID.Csch:
        return F.Times(F.C2, F.Power(F.Subtract(ePos, eNeg), F.CN1));
      case ID.Tanh:
        return F.Plus(F.Times(F.CN1, eNeg, F.Power(F.Plus(eNeg, ePos), F.CN1)),
            F.Times(ePos, F.Power(F.Plus(eNeg, ePos), F.CN1)));
      case ID.Coth:
        return F.Times(F.Plus(eNeg, ePos), F.Power(F.Subtract(ePos, eNeg), F.CN1));
      default:
        return F.NIL;
    }
  }

  private static boolean isHyperbolic(int headID) {
    switch (headID) {
      case ID.Sinh:
      case ID.Cosh:
      case ID.Tanh:
      case ID.Coth:
      case ID.Sech:
      case ID.Csch:
        return true;
      default:
        return false;
    }
  }

  /** Test for the twelve circular and hyperbolic functions with one argument. */
  private static boolean isTrigFunction(IExpr expr) {
    return isSinCosLike(expr) || isQuotientLike(expr);
  }

  /** Test for {@link ID#Sin}, {@link ID#Cos}, {@link ID#Sinh} or {@link ID#Cosh}. */
  private static boolean isSinCosLike(IExpr expr) {
    return expr.isSin() || expr.isCos() || expr.isSinh() || expr.isCosh();
  }

  /** Test for the quotient and reciprocal circular and hyperbolic functions. */
  private static boolean isQuotientLike(IExpr expr) {
    if (expr.isAST1()) {
      switch (expr.headID()) {
        case ID.Tan:
        case ID.Cot:
        case ID.Sec:
        case ID.Csc:
        case ID.Tanh:
        case ID.Coth:
        case ID.Sech:
        case ID.Csch:
          return true;
        default:
          return false;
      }
    }
    return false;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
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
