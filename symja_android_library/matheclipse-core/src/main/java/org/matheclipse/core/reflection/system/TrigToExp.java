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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
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

  /** The largest number of summands of a distributed product whose harmonics are compared. */
  private static final int MAX_DISTRIBUTED_TERMS = 16;

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
   * functions), a sum, and a monomial times one transformed sum are expanded. A product of sines
   * and cosines of low degree, whose angles are rational multiples of one angle, is expanded too,
   * even times a sum free of trigonometric functions: <code>Cos(x)*Cos(2*x)</code> (see
   * {@link #isSharedAngleProduct(IAST)}). Other products of several transformed functions, higher
   * powers of sine and cosine, powers of the quotient functions and other products with a sum free
   * of trigonometric functions are kept grouped: <code>Sin(x)^3</code> becomes
   * <code>-I/8*(E^(-I*x)-E^(I*x))^3</code>. A sum whose higher powers share a harmonic with another
   * summand is expanded, so that the harmonics combine; so is a product with a sum, whose
   * distribution gives such a sum.
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

    int expandableCount = 0;
    boolean otherTransformedFactor = false;
    boolean algebraicSumFactor = false;
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
        if (grouping(factor, new boolean[1]) > 0) {
          sumFactors++;
        } else {
          algebraicSumFactor = true;
        }
      }
    }
    boolean monomialTimesTransformedSum = count == 1 && factors.argSize() > 1 && sumFactors == 1;
    boolean sharedAngleProduct = isSharedAngleProduct(factors);
    boolean groupedCandidate = flag[0] || (algebraicSumFactor && !sharedAngleProduct);
    boolean expandable = (expandableCount == 1 && !otherTransformedFactor) || sharedAngleProduct
        || monomialTimesTransformedSum || expr.isPlus();
    // combining harmonics only matter if they cancel the grouping or enable the expansion
    boolean combiningHarmonics =
        (groupedCandidate || !expandable) && hasCombiningHarmonics(expr, engine);
    boolean grouped = groupedCandidate && !combiningHarmonics;

    IExpr transformed = engine.evaluate(convert(expr, grouped));
    if (!grouped && (combiningHarmonics || expandable)) {
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
   * a product of several such functions, which isn't a {@link #isSharedAngleProduct(IAST)}, a power
   * greater than 2 of a sine or cosine, or a power of a quotient function like {@link ID#Tan}.
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
      } else if (expr.isTimes() && count > 1 && !isSharedAngleProduct((IAST) expr)) {
        grouped[0] = true;
      }
      return count;
    }
    return 0;
  }

  /**
   * Test if <code>times</code> contains at least two sine and cosine like factors (or their
   * squares) of one family, whose angles are rational multiples of one angle:
   * <code>Cos(x/2)*Cos(3*x/2)</code>. The other factors must be free of trigonometric functions. A
   * product of degree 2 always qualifies, a product of degree 3 only if two of its harmonic
   * combinations coincide: <code>Cos(x)^2*Cos(2*x)</code> but not <code>Cos(x)^2*Cos(3*x)</code>.
   *
   * @param times
   * @return
   */
  private static boolean isSharedAngleProduct(IAST times) {
    IExpr angle = null;
    boolean hyperbolic = false;
    int count = 0;
    int degree = 0;
    List<IRational> harmonics = new ArrayList<>();
    harmonics.add(F.C0);
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      IExpr base = factor.isPower() ? factor.base() : factor;
      if (!isSinCosLike(base)) {
        if (grouping(factor, new boolean[1]) > 0) {
          return false;
        }
        continue;
      }
      int n = factor.isPower() ? factor.exponent().toIntDefault() : 1;
      if (n < 1 || n > 2) {
        return false;
      }
      degree += n;
      if (degree > 3) {
        return false;
      }
      IExpr unscaled = unscaledAngle(base.first());
      if (unscaled.isNIL()) {
        return false;
      }
      boolean isHyperbolic = base.isSinh() || base.isCosh();
      if (angle == null) {
        angle = unscaled;
        hyperbolic = isHyperbolic;
      } else if (hyperbolic != isHyperbolic || !angle.equals(unscaled)) {
        return false;
      }
      IRational scale = angleScale(base.first());
      List<IRational> next = new ArrayList<>();
      for (IRational previous : harmonics) {
        for (int k = -n; k <= n; k += 2) {
          next.add(previous.add(scale.multiply(k)));
        }
      }
      harmonics = next;
      count++;
    }
    if (count < 2) {
      return false;
    }
    if (degree == 3) {
      for (int i = 0; i < harmonics.size(); i++) {
        for (int j = i + 1; j < harmonics.size(); j++) {
          if (harmonics.get(i).equals(harmonics.get(j))) {
            return true;
          }
        }
      }
      return false;
    }
    return true;
  }

  /**
   * The product of the rational factors of <code>angle</code>: <code>3/2</code> for
   * <code>3/2*x*y</code>.
   *
   * @param angle
   * @return
   */
  private static IRational angleScale(IExpr angle) {
    IRational scale = F.C1;
    IAST factors = angle.isTimes() ? (IAST) angle : F.Times(angle);
    for (int i = 1; i < factors.size(); i++) {
      if (factors.get(i).isRational()) {
        scale = scale.multiply((IRational) factors.get(i));
      }
    }
    return scale;
  }

  /**
   * The angle without its rational factors: <code>x*y</code> for <code>3/2*x*y</code>.
   * {@link F#NIL} for a numeric angle or an angle containing a sum.
   *
   * @param angle
   * @return
   */
  private static IExpr unscaledAngle(IExpr angle) {
    IAST factors = angle.isTimes() ? (IAST) angle : F.Times(angle);
    IASTAppendable rest = F.TimesAlloc(factors.argSize());
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isPlus()) {
        return F.NIL;
      }
      if (!factor.isRational()) {
        rest.append(factor);
      }
    }
    return rest.isAST0() ? F.NIL : rest.oneIdentity1();
  }

  /**
   * Test if a sum contains powers greater than 2 of sines or cosines whose harmonics all occur in
   * another summand. Then the expanded exponentials combine. A summand with a single
   * {@link ID#Tan}, {@link ID#Cot}, {@link ID#Tanh} or {@link ID#Coth} is ignored.
   *
   * <p>
   * A product with a sum containing trigonometric functions is distributed first. Then the
   * summands may be products of sines and cosines of total degree at most
   * {@link #MAX_HARMONIC_POWER}; at least one summand must be a single power of degree 2 or more,
   * and the harmonics of all summands of degree 2 or more must occur in another summand:
   * <code>(Sin(x)+Cos(x))*Cos(x)</code>.
   *
   * @param expr
   * @param engine
   * @return
   */
  private static boolean hasCombiningHarmonics(IExpr expr, EvalEngine engine) {
    IAST terms;
    boolean productInput = false;
    if (expr.isPlus()) {
      terms = (IAST) expr;
    } else if (expr.isTimes()) {
      IExpr distributed = distributeTransformedSum((IAST) expr, engine);
      if (!distributed.isPlus()) {
        return false;
      }
      terms = (IAST) distributed;
      productInput = true;
    } else {
      return false;
    }
    List<Integer> degrees = new ArrayList<>();
    List<List<IExpr>> spectra = new ArrayList<>();
    boolean singlePower = false;
    terms: for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      int trigCount = 0;
      for (int j = 1; j < factors.size(); j++) {
        if (grouping(factors.get(j), new boolean[1]) > 0) {
          trigCount++;
        }
      }
      if (!productInput && trigCount > 1) {
        return false;
      }
      int degree = 0;
      int family = 0;
      List<IExpr> harmonics = new ArrayList<>();
      harmonics.add(F.C0);
      for (int j = 1; j < factors.size(); j++) {
        IExpr factor = factors.get(j);
        if (grouping(factor, new boolean[1]) == 0) {
          continue;
        }
        IExpr base = factor;
        int n = 1;
        if (factor.isPower()) {
          n = factor.exponent().toIntDefault();
          base = factor.base();
        }
        if (n == 1 && trigCount == 1 && isTangentLike(base)) {
          continue terms;
        }
        if (n < 1 || !isSinCosLike(base)) {
          return false;
        }
        degree += n;
        if (degree > MAX_HARMONIC_POWER) {
          return false;
        }
        int baseFamily = base.isSinh() || base.isCosh() ? 2 : 1;
        if (family != 0 && family != baseFamily) {
          return false;
        }
        family = baseFamily;
        List<IExpr> next = new ArrayList<>();
        for (IExpr previous : harmonics) {
          for (int k = -n; k <= n; k += 2) {
            next.add(F.evalExpand(F.Plus(previous, F.Times(F.ZZ(k), base.first()))));
          }
        }
        harmonics = next;
      }
      if (family != 0) {
        singlePower |= trigCount == 1 && degree >= 2;
        List<IExpr> spectrum = new ArrayList<>();
        for (IExpr angle : harmonics) {
          if (!angle.isZero()) {
            spectrum.add(family == 2 ? F.Sinh(angle) : angle);
          }
        }
        degrees.add(degree);
        spectra.add(spectrum);
      }
    }
    if (productInput && !singlePower) {
      return false;
    }
    int threshold = productInput ? 2 : 3;
    boolean hasPower = false;
    for (int i = 0; i < spectra.size(); i++) {
      if (degrees.get(i) < threshold) {
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
   * Expand the product <code>times</code>, if it contains a sum with trigonometric functions and
   * the expansion has at most {@link #MAX_DISTRIBUTED_TERMS} summands.
   *
   * @param times
   * @param engine
   * @return {@link F#NIL} if the product isn't distributed
   */
  private static IExpr distributeTransformedSum(IAST times, EvalEngine engine) {
    boolean transformedSum = false;
    long terms = 1;
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (factor.isPlus()) {
        transformedSum |= grouping(factor, new boolean[1]) > 0;
        terms *= factor.argSize();
      } else if (factor.isPower() && factor.base().isPlus()) {
        return F.NIL;
      }
      if (terms > MAX_DISTRIBUTED_TERMS) {
        return F.NIL;
      }
    }
    return transformedSum ? engine.evaluate(F.Expand(times)) : F.NIL;
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

  /** Test for {@link ID#Tan}, {@link ID#Cot}, {@link ID#Tanh} or {@link ID#Coth}. */
  private static boolean isTangentLike(IExpr expr) {
    if (expr.isAST1()) {
      switch (expr.headID()) {
        case ID.Tan:
        case ID.Cot:
        case ID.Tanh:
        case ID.Coth:
          return true;
        default:
          return false;
      }
    }
    return false;
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
