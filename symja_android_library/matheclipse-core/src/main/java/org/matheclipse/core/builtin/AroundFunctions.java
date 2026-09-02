package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@link S#Around} - a value carrying an uncertainty - and the first-order propagation of that
 * uncertainty through arithmetic.
 *
 * <p>
 * Every occurrence of an {@code Around} is treated as an independent measurement, which is what
 * makes {@code Around(2,0.1) + Around(2,0.1)} differ from {@code 2*Around(2,0.1)}: the first adds
 * two uncertainties in quadrature and gives {@code 0.1*Sqrt(2)}, the second scales one and gives
 * {@code 0.2}. Correlated propagation, where a variable stands for one value however often it
 * appears, is {@link S#AroundReplace} instead.
 */
public class AroundFunctions {

  private static class Initializer {

    private static void init() {
      S.Around.setEvaluator(new Around());
      S.MeanAround.setEvaluator(new MeanAround());
      S.AroundReplace.setEvaluator(new AroundReplace());
      S.VectorAround.setEvaluator(new VectorAround());
    }
  }

  /**
   * <code>Around(x, delta)</code> - the value <code>x</code> with uncertainty <code>delta</code>.
   *
   * <p>
   * The expression stays inert; it is the arithmetic that reads it. Both arguments are made
   * numeric, so <code>Around(2, 1/10)</code> is <code>Around(2.0, 0.1)</code> - and a quantity
   * argument keeps its unit rather than being folded into the magnitude, as in
   * <code>Around(Quantity(3,"Meters"), Quantity(5,"Centimeters"))</code>.
   */
  private static final class Around extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isDistribution()) {
          // the mean of the distribution, with the standard deviation as its uncertainty
          return F.binaryAST2(S.Around, F.Mean(arg1), F.StandardDeviation(arg1));
        }
        if (isFlatVector(arg1)) {
          return F.binaryAST2(S.Around, F.Mean(arg1), F.StandardDeviation(arg1));
        }
        // a list of vectors is a VectorAround - a mean vector with a covariance - not an Around
        // whose two arguments are lists
        return F.NIL;
      }
      if (ast.isAST2()) {
        IExpr value = numeric(ast.arg1(), engine);
        IExpr uncertainty = numeric(ast.arg2(), engine);
        if (value.isNIL() && uncertainty.isNIL()) {
          // already numeric: a fixed point, or this would evaluate for ever
          return F.NIL;
        }
        return F.binaryAST2(S.Around, value.orElse(ast.arg1()), uncertainty.orElse(ast.arg2()));
      }
      return F.NIL;
    }

    /**
     * The numeric form of <code>expr</code>, or {@link F#NIL} if it is numeric already.
     *
     * <p>
     * An asymmetric uncertainty <code>{deltaMinus, deltaPlus}</code> is numericized elementwise.
     */
    private static IExpr numeric(IExpr expr, EvalEngine engine) {
      IExpr numeric = engine.evalN(expr);
      return numeric.equals(expr) ? F.NIL : numeric;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>MeanAround(list)</code> - the mean of <code>list</code>, tagged with the uncertainty of
   * that mean.
   *
   * <p>
   * For plain numbers that uncertainty is the standard error, <code>sigma/Sqrt(n)</code>, with the
   * sample standard deviation - NOT the standard deviation itself, which is what
   * <code>Around(list)</code> reports. For a list of VECTORS the answer is a
   * {@link S#VectorAround} instead, carrying the covariance of the mean. For a list of {@link S#Around} values it is instead the
   * inverse-variance weighted mean: each element is weighted by <code>1/delta^2</code>, and the
   * uncertainty of the result is <code>1/Sqrt(Sum(weights))</code>.
   */
  private static final class MeanAround extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!isFlatVector(arg1)) {
        return meanOfVectors(arg1, engine);
      }
      IAST list = (IAST) arg1;
      if (list.forAll(x -> isAround(x))) {
        return weightedMean(list, engine);
      }
      if (list.exists(x -> isAround(x))) {
        // a list mixing measured and exact values has no agreed weighting here
        return F.NIL;
      }
      if (list.argSize() == 1) {
        // one plain value has no spread to report, and Mathematica answers the value itself
        return list.arg1();
      }
      return F.binaryAST2(S.Around, engine.evaluate(F.N(F.Mean(list))), engine.evaluate(
          F.N(F.Divide(F.StandardDeviation(list), F.Sqrt(F.ZZ(list.argSize()))))));
    }

    /**
     * The mean of a list of vectors, as a {@link S#VectorAround} carrying the covariance OF THAT
     * MEAN - the sample covariance of the data divided by the number of observations.
     *
     * <p>
     * The answer stays exact: <code>MeanAround({{1,2},{3,5},{4,4},{2,3}})</code> is
     * <code>VectorAround({5/2,7/2}, {{5/12,1/3},{1/3,5/12}})</code>, not its decimal form.
     *
     * @return {@link F#NIL} unless the argument is a matrix of at least two rows
     */
    private static IExpr meanOfVectors(IExpr data, EvalEngine engine) {
      int[] dimension = data.isMatrix();
      if (dimension == null || dimension[0] < 2 || dimension[1] < 1) {
        return F.NIL;
      }
      IExpr mean = engine.evaluate(F.Mean(data));
      IExpr covariance = engine.evaluate(F.unaryAST1(S.Covariance, data));
      if (!mean.isList() || covariance.isNIL() || covariance.isMatrix() == null) {
        return F.NIL;
      }
      // the covariance OF THE MEAN, which is the sample covariance divided by the sample size
      IExpr covarianceOfMean = engine.evaluate(F.Divide(covariance, F.ZZ(dimension[0])));
      return F.binaryAST2(S.VectorAround, mean, covarianceOfMean);
    }

    /**
     * The inverse-variance weighted mean of a list of {@link S#Around} values: a measurement with
     * a smaller uncertainty counts for more, in proportion to <code>1/delta^2</code>.
     */
    private static IExpr weightedMean(IAST list, EvalEngine engine) {
      IASTAppendable weights = F.PlusAlloc(list.size());
      IASTAppendable weightedValues = F.PlusAlloc(list.size());
      for (int i = 1; i < list.size(); i++) {
        IAST around = (IAST) list.get(i);
        IExpr weight = F.Power(F.Sqr(around.arg2()), F.CN1);
        weights.append(weight);
        weightedValues.append(F.Times(weight, around.arg1()));
      }
      IExpr totalWeight = engine.evaluate(weights);
      if (totalWeight.isZero()) {
        return F.NIL;
      }
      return F.binaryAST2(S.Around, engine.evaluate(F.N(F.Divide(weightedValues, totalWeight))),
          engine.evaluate(F.N(F.Power(F.Sqrt(totalWeight), F.CN1))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>AroundReplace(expr, rules)</code> - propagate uncertainty through <code>expr</code> by
   * replacing each variable with an {@link S#Around}.
   *
   * <p>
   * This is the CORRELATED counterpart of plain {@code Around} arithmetic: a variable stands for
   * one value however often it appears, so <code>AroundReplace(s + s, s -> Around(2,0.1))</code>
   * differentiates to <code>2</code> and reports <code>0.2</code>, where
   * <code>Around(2,0.1) + Around(2,0.1)</code> treats the two as independent measurements and
   * reports <code>0.1*Sqrt(2)</code>. Different rules are still assumed uncorrelated with each
   * other, so their contributions add in quadrature.
   *
   * <p>
   * The optional third argument is the series order. Order 1, the default, keeps the centre at
   * <code>f(x)</code>; order 2 moves it by <code>f''(x)*delta^2/2</code> and adds the matching
   * term to the uncertainty. Higher orders are not implemented and leave the expression
   * unevaluated rather than quietly answering to order 2.
   */
  private static final class AroundReplace extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr expr = ast.arg1();
      IAST rules = ast.arg2().isRuleAST() ? F.List(ast.arg2())
          : (ast.arg2().isListOfRules(false) ? (IAST) ast.arg2() : F.NIL);
      if (rules.isNIL() || rules.argSize() == 0) {
        return F.NIL;
      }
      int order = 1;
      if (ast.isAST3()) {
        order = ast.arg3().toIntDefault();
        if (order < 1 || order > 2) {
          // only the first and second order are known; see #secondOrder
          return F.NIL;
        }
      }
      if (order == 2) {
        IExpr second = secondOrder(expr, rules, engine);
        if (second.isPresent()) {
          return second;
        }
        return F.NIL;
      }

      IASTAppendable substitutions = F.ListAlloc(rules.size());
      for (int i = 1; i < rules.size(); i++) {
        IAST rule = (IAST) rules.get(i);
        IExpr replacement = rule.arg2();
        substitutions
            .append(F.Rule(rule.arg1(), isAround(replacement) ? ((IAST) replacement).arg1()
                : replacement));
      }
      IExpr value = engine.evaluate(F.ReplaceAll(expr, substitutions));
      if (!value.isNumericFunction(true)) {
        return F.NIL;
      }

      IASTAppendable squares = F.PlusAlloc(rules.size());
      for (int i = 1; i < rules.size(); i++) {
        IAST rule = (IAST) rules.get(i);
        if (!isAround(rule.arg2())) {
          // an exact replacement contributes no uncertainty
          continue;
        }
        IExpr variable = rule.arg1();
        IExpr derivative = engine.evaluate(F.D(expr, variable));
        if (!derivative.isFree(S.D)) {
          return F.NIL;
        }
        IExpr slope = engine.evaluate(F.ReplaceAll(derivative, substitutions));
        if (!slope.isNumericFunction(true)) {
          return F.NIL;
        }
        squares.append(F.Sqr(F.Times(slope, ((IAST) rule.arg2()).arg2())));
      }
      if (squares.argSize() == 0) {
        return value;
      }
      return F.binaryAST2(S.Around, engine.evaluate(F.N(value)),
          engine.evaluate(F.N(F.Sqrt(squares))));
    }

    /**
     * Second-order propagation through a single uncertain variable.
     *
     * <p>
     * The centre moves off <code>f(x)</code>, which is what distinguishes this from first order:
     * <code>f(x) + f''(x)*delta^2/2</code>, with uncertainty
     * <code>Sqrt(f'(x)^2*delta^2 + f''(x)^2*delta^4/2)</code>. Pinned to Mathematica by
     * <code>AroundReplace(s^2, s -> Around(2,0.1), 2)</code> being
     * <code>Around(4.01, 0.40024992192379005)</code>, where first order would answer
     * <code>Around(4., 0.4)</code>.
     *
     * <p>
     * Only ONE uncertain rule is handled. Several would need the mixed second derivatives as well,
     * and no measurement of that case exists.
     *
     * @return {@link F#NIL} if there is not exactly one uncertain rule, or a derivative is unusable
     */
    private static IExpr secondOrder(IExpr expr, IAST rules, EvalEngine engine) {
      IAST uncertain = F.NIL;
      IASTAppendable substitutions = F.ListAlloc(rules.size());
      for (int i = 1; i < rules.size(); i++) {
        IAST rule = (IAST) rules.get(i);
        IExpr replacement = rule.arg2();
        if (isAround(replacement)) {
          if (uncertain.isPresent()) {
            return F.NIL;
          }
          uncertain = rule;
          substitutions.append(F.Rule(rule.arg1(), ((IAST) replacement).arg1()));
        } else {
          substitutions.append(F.Rule(rule.arg1(), replacement));
        }
      }
      if (uncertain.isNIL() || isAsymmetric(uncertain.arg2())) {
        return F.NIL;
      }
      IExpr variable = uncertain.arg1();
      IExpr delta = ((IAST) uncertain.arg2()).arg2();

      IExpr firstDerivative = engine.evaluate(F.D(expr, variable));
      IExpr secondDerivative = engine.evaluate(F.D(F.D(expr, variable), variable));
      if (!firstDerivative.isFree(S.D) || !secondDerivative.isFree(S.D)) {
        return F.NIL;
      }
      IExpr value = engine.evaluate(F.ReplaceAll(expr, substitutions));
      IExpr slope = engine.evaluate(F.ReplaceAll(firstDerivative, substitutions));
      IExpr curvature = engine.evaluate(F.ReplaceAll(secondDerivative, substitutions));
      if (!value.isNumericFunction(true) || !slope.isNumericFunction(true)
          || !curvature.isNumericFunction(true)) {
        return F.NIL;
      }

      IExpr centre = F.Plus(value, F.Times(F.C1D2, curvature, F.Sqr(delta)));
      IExpr variance = F.Plus(F.Times(F.Sqr(slope), F.Sqr(delta)),
          F.Times(F.C1D2, F.Sqr(curvature), F.Power(delta, F.C4)));
      return F.binaryAST2(S.Around, engine.evaluate(F.N(centre)),
          engine.evaluate(F.N(F.Sqrt(variance))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /** Whether <code>expr</code> is a non-empty list none of whose elements is itself a list. */
  private static boolean isFlatVector(IExpr expr) {
    return expr.isNonEmptyList() && ((IAST) expr).forAll(x -> !x.isList());
  }

  /**
   * <code>VectorAround(values, uncertainties)</code> - a vector of values with their uncertainties.
   *
   * <p>
   * The expression stays inert, and unlike {@link S#Around} its arguments are NOT made numeric:
   * {@code MeanAround} of exact vectors answers an exact {@code VectorAround}.
   *
   * <p>
   * Of the documented uncertainty spellings, the two that describe a covariance indirectly - the
   * pair-with-a-correlation-factor {@code {{d1,d2}, rho}} and uncertainties with a correlation
   * matrix {@code {{d1,...}, {{1,R12,...},...}}} - are normalized to the covariance matrix they
   * stand for. A plain list of uncertainties and an explicit covariance matrix are kept as
   * written.
   */
  private static final class VectorAround extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isFlatVector(ast.arg1()) || !ast.arg2().isNonEmptyList()) {
        return F.NIL;
      }
      IAST spec = (IAST) ast.arg2();
      // Two of the documented uncertainty spellings become the covariance matrix they stand for.
      // The plain list of uncertainties and an explicit covariance matrix are kept as written.
      if (spec.argSize() == 2 && isFlatVector(spec.arg1())) {
        IAST uncertainties = (IAST) spec.arg1();
        // {{delta1, delta2}, rho} - a PAIR with a correlation factor
        if (uncertainties.argSize() == 2 && !spec.arg2().isList()) {
          return covarianceOf(ast.arg1(), uncertainties,
              F.list(F.list(F.C1, spec.arg2()), F.list(spec.arg2(), F.C1)), engine);
        }
        // {{delta1, ...}, {{1, R12, ...}, ...}} - uncertainties with a correlation MATRIX
        int[] dimension = spec.arg2().isMatrix();
        if (dimension != null && dimension[0] == uncertainties.argSize()
            && dimension[1] == uncertainties.argSize()) {
          return covarianceOf(ast.arg1(), uncertainties, (IAST) spec.arg2(), engine);
        }
      }
      return F.NIL;
    }

    /**
     * The {@code VectorAround} whose uncertainty is the covariance matrix that
     * <code>uncertainties</code> and <code>correlations</code> stand for:
     * <code>Cov(i,j) = delta(i)*delta(j)*R(i,j)</code>.
     *
     * <p>
     * The pair-with-a-correlation-factor spelling is the two by two case of this, with
     * <code>R = {{1,rho},{rho,1}}</code> - which reproduces the Mathematica answer
     * <code>{{0.09,0.06},{0.06,0.16}}</code> for <code>{{0.3,0.4},0.5}</code>.
     */
    private static IExpr covarianceOf(IExpr values, IAST uncertainties, IAST correlations,
        EvalEngine engine) {
      int size = uncertainties.argSize();
      IASTAppendable covariance = F.ListAlloc(size);
      for (int i = 1; i <= size; i++) {
        IASTAppendable row = F.ListAlloc(size);
        for (int j = 1; j <= size; j++) {
          row.append(F.Times(uncertainties.get(i), uncertainties.get(j),
              correlations.get(i).getAt(j)));
        }
        covariance.append(row);
      }
      return F.binaryAST2(S.VectorAround, values, engine.evaluate(covariance));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /** Whether <code>expr</code> is an {@code Around(value, uncertainty)}. */
  public static boolean isAround(IExpr expr) {
    return expr.isAST(S.Around, 3);
  }

  /** Whether <code>expr</code> is an {@code Around} whose uncertainty is a {lower, upper} pair. */
  public static boolean isAsymmetric(IExpr expr) {
    return isAround(expr) && ((IAST) expr).arg2().isList2();
  }

  /** The downward uncertainty of an {@code Around}; the only one, when it is symmetric. */
  private static IExpr lower(IAST around) {
    return isAsymmetric(around) ? ((IAST) around.arg2()).arg1() : around.arg2();
  }

  /** The upward uncertainty of an {@code Around}; the only one, when it is symmetric. */
  private static IExpr upper(IAST around) {
    return isAsymmetric(around) ? ((IAST) around.arg2()).arg2() : around.arg2();
  }

  /**
   * An {@code Around} with separate downward and upward uncertainties.
   *
   * <p>
   * The pair is kept even when the two sides come out equal: Mathematica answers
   * {@code Around[2,{0.1,0.3}] - Around[2,{0.1,0.3}]} as
   * {@code Around[0., {0.316..., 0.316...}]}, not as the one-uncertainty spelling.
   */
  private static IExpr around(IExpr value, IExpr lower, IExpr upper) {
    return F.binaryAST2(S.Around, value, F.list(lower, upper));
  }

  /**
   * The sum of a {@link S#Plus} in which at least one {@link S#Around} is asymmetric.
   *
   * <p>
   * The two sides propagate independently: downward uncertainties add in quadrature with each
   * other, and upward ones with each other. Nothing here can flip a side, because addition is
   * increasing in every operand.
   */
  private static IExpr plusAsymmetric(IAST plusAST, EvalEngine engine) {
    IASTAppendable values = F.PlusAlloc(plusAST.size());
    IASTAppendable lowerSquares = F.PlusAlloc(plusAST.size());
    IASTAppendable upperSquares = F.PlusAlloc(plusAST.size());
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr arg = plusAST.get(i);
      if (isAround(arg)) {
        IAST around = (IAST) arg;
        values.append(around.arg1());
        lowerSquares.append(F.Sqr(lower(around)));
        upperSquares.append(F.Sqr(upper(around)));
      } else {
        values.append(arg);
      }
    }
    return around(engine.evaluate(values), engine.evaluate(F.Sqrt(lowerSquares)),
        engine.evaluate(F.Sqrt(upperSquares)));
  }

  /**
   * The product of a {@link S#Times} in which at least one {@link S#Around} is asymmetric.
   *
   * <p>
   * Each uncertain factor contributes through the product of the others. Where that partial
   * derivative is NEGATIVE the two sides swap - Mathematica answers {@code -Around[2,{0.1,0.3}]}
   * as {@code Around[-2.,{0.3,0.1}]}. A general unary function does NOT swap them, which is
   * inconsistent with this but is what Mathematica does; see {@code #propagate}.
   *
   * @return {@link F#NIL} if the sign of a partial derivative cannot be decided
   */
  private static IExpr timesAsymmetric(IAST timesAST, EvalEngine engine) {
    IASTAppendable values = F.TimesAlloc(timesAST.size());
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr arg = timesAST.get(i);
      values.append(isAround(arg) ? ((IAST) arg).arg1() : arg);
    }

    IASTAppendable lowerSquares = F.PlusAlloc(timesAST.size());
    IASTAppendable upperSquares = F.PlusAlloc(timesAST.size());
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr arg = timesAST.get(i);
      if (!isAround(arg)) {
        continue;
      }
      IASTAppendable others = F.TimesAlloc(timesAST.size());
      for (int j = 1; j < timesAST.size(); j++) {
        if (j != i) {
          IExpr other = timesAST.get(j);
          others.append(isAround(other) ? ((IAST) other).arg1() : other);
        }
      }
      IExpr partial = engine.evaluate(others);
      IExpr sign = engine.evaluate(F.Sign(partial));
      if (!sign.isOne() && !sign.isMinusOne() && !sign.isZero()) {
        return F.NIL;
      }
      IAST around = (IAST) arg;
      boolean flip = sign.isMinusOne();
      lowerSquares.append(F.Sqr(F.Times(partial, flip ? upper(around) : lower(around))));
      upperSquares.append(F.Sqr(F.Times(partial, flip ? lower(around) : upper(around))));
    }
    return around(engine.evaluate(values), engine.evaluate(F.Sqrt(lowerSquares)),
        engine.evaluate(F.Sqrt(upperSquares)));
  }

  /** Whether <code>expr</code> is a {@code VectorAround(values, uncertainties)}. */
  public static boolean isVectorAround(IExpr expr) {
    return expr.isAST(S.VectorAround, 3);
  }

  /**
   * The sum of a {@link S#Plus} holding at least one {@link S#VectorAround}.
   *
   * <p>
   * Componentwise: the value vectors add, and the uncertainties add in quadrature - each
   * {@code VectorAround} is an independent measurement, exactly as for a scalar {@link S#Around}.
   * Every operand has to be a {@code VectorAround}; a plain vector added to one is threaded by
   * {@code Plus} into separate sums before this is reached, which is Symja's existing behaviour
   * for a list and is left as it is.
   *
   * <p>
   * Both spellings of the uncertainty are handled, but not mixed together: a sum of
   * list-of-uncertainties operands answers a list, a sum of covariance-matrix operands adds the
   * matrices. Mixing the two would have to pick one of them for the answer, and which one is not
   * known, so that case is left alone.
   *
   * @return {@link F#NIL} if there is nothing to combine, or the operands do not agree
   */
  public static IExpr plusVectorAround(IAST plusAST, EvalEngine engine) {
    IExpr firstUncertainty = F.NIL;
    int length = -1;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr arg = plusAST.get(i);
      if (isVectorAround(arg)) {
        IExpr values = ((IAST) arg).arg1();
        if (length < 0) {
          length = values.argSize();
          firstUncertainty = ((IAST) arg).arg2();
        } else if (values.argSize() != length) {
          return F.NIL;
        }
      }
    }
    if (length < 0) {
      return F.NIL;
    }
    // all operands have to spell the uncertainty the same way
    boolean covariance = firstUncertainty.isMatrix() != null;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr arg = plusAST.get(i);
      if (isVectorAround(arg)) {
        if ((((IAST) arg).arg2().isMatrix() != null) != covariance) {
          return F.NIL;
        }
      } else {
        // every operand has to be a VectorAround. A plain vector never reaches here anyway: Plus
        // threads over a List first, so {1,1} + VectorAround(...) is already two sums by then.
        return F.NIL;
      }
    }

    IASTAppendable values = F.PlusAlloc(plusAST.size());
    IASTAppendable uncertainties = F.PlusAlloc(plusAST.size());
    for (int i = 1; i < plusAST.size(); i++) {
      IAST vectorAround = (IAST) plusAST.get(i);
      values.append(vectorAround.arg1());
      // covariances add; independent uncertainties add in quadrature, so their squares add
      uncertainties.append(covariance ? vectorAround.arg2() : F.Sqr(vectorAround.arg2()));
    }
    IExpr summedUncertainty = engine.evaluate(uncertainties);
    return F.binaryAST2(S.VectorAround, engine.evaluate(values),
        covariance ? summedUncertainty : engine.evaluate(F.Sqrt(summedUncertainty)));
  }

  /**
   * A {@link S#VectorAround} scaled by a number.
   *
   * <p>
   * The values scale by the factor and the uncertainties by its absolute value:
   * {@code 2*VectorAround({1.8,2.4},{0.3,0.4})} is
   * {@code VectorAround({3.6,4.8},{0.6,0.8})}. In the covariance spelling the matrix scales by the
   * SQUARE of the factor instead, since that is what a covariance does under scaling.
   *
   * <p>
   * Only one uncertain factor is handled. A product of two {@code VectorAround}s would be an
   * elementwise product of two measured vectors, which is left alone.
   *
   * @return {@link F#NIL} if there is not exactly one {@code VectorAround} factor
   */
  public static IExpr timesVectorAround(IAST timesAST, EvalEngine engine) {
    IAST vectorAround = F.NIL;
    IASTAppendable scalars = F.TimesAlloc(timesAST.size());
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr arg = timesAST.get(i);
      if (isVectorAround(arg)) {
        if (vectorAround.isPresent()) {
          return F.NIL;
        }
        vectorAround = (IAST) arg;
      } else {
        scalars.append(arg);
      }
    }
    if (vectorAround.isNIL()) {
      return F.NIL;
    }
    IExpr scalar = engine.evaluate(scalars);
    if (!scalar.isNumericFunction(true)) {
      return F.NIL;
    }
    IExpr uncertainty = vectorAround.arg2();
    boolean covariance = uncertainty.isMatrix() != null;
    return F.binaryAST2(S.VectorAround,
        engine.evaluate(F.Times(scalar, vectorAround.arg1())),
        engine.evaluate(F.Times(covariance ? F.Sqr(scalar) : F.Abs(scalar), uncertainty)));
  }

  /**
   * The sum of a {@link S#Plus} holding at least one {@link S#Around}.
   *
   * <p>
   * The values add; the uncertainties add in quadrature, because each occurrence is an independent
   * measurement. This has to run BEFORE <code>Plus</code> collects equal terms, or
   * <code>Around(2,0.1) + Around(2,0.1)</code> would become <code>2*Around(2,0.1)</code> and pick
   * up the scaled uncertainty <code>0.2</code> instead of <code>0.1*Sqrt(2)</code>.
   *
   * @return {@link F#NIL} if there is nothing to combine
   */
  public static IExpr plus(IAST plusAST, EvalEngine engine) {
    if (plusAST.exists(x -> isAsymmetric(x))) {
      return plusAsymmetric(plusAST, engine);
    }
    IASTAppendable values = F.PlusAlloc(plusAST.size());
    IASTAppendable squares = F.PlusAlloc(plusAST.size());
    int aroundCount = 0;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr arg = plusAST.get(i);
      if (isAround(arg)) {
        aroundCount++;
        values.append(((IAST) arg).arg1());
        squares.append(F.Sqr(((IAST) arg).arg2()));
      } else {
        values.append(arg);
      }
    }
    if (aroundCount == 0) {
      return F.NIL;
    }
    return F.binaryAST2(S.Around, engine.evaluate(values), engine.evaluate(F.Sqrt(squares)));
  }

  /**
   * The product of a {@link S#Times} holding at least one {@link S#Around}.
   *
   * <p>
   * Each uncertain factor contributes the product of the other factors times its own uncertainty,
   * and those contributions add in quadrature - the first-order propagation of a product. A single
   * uncertain factor therefore just scales: <code>2*Around(2,0.1)</code> is
   * <code>Around(4.0, 0.2)</code>.
   *
   * @return {@link F#NIL} if there is nothing to combine
   */
  public static IExpr times(IAST timesAST, EvalEngine engine) {
    if (timesAST.exists(x -> isAsymmetric(x))) {
      return timesAsymmetric(timesAST, engine);
    }
    IASTAppendable values = F.TimesAlloc(timesAST.size());
    int aroundCount = 0;
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr arg = timesAST.get(i);
      values.append(isAround(arg) ? ((IAST) arg).arg1() : arg);
      if (isAround(arg)) {
        aroundCount++;
      }
    }
    if (aroundCount == 0) {
      return F.NIL;
    }
    IExpr product = engine.evaluate(values);

    IASTAppendable squares = F.PlusAlloc(aroundCount);
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr arg = timesAST.get(i);
      if (isAround(arg)) {
        // the partial derivative of the product with respect to this factor is the product of all
        // the others, which is built directly rather than as product/value so that a zero value
        // cannot divide
        IASTAppendable others = F.TimesAlloc(timesAST.size());
        for (int j = 1; j < timesAST.size(); j++) {
          if (j != i) {
            IExpr other = timesAST.get(j);
            others.append(isAround(other) ? ((IAST) other).arg1() : other);
          }
        }
        squares.append(F.Sqr(F.Times(others, ((IAST) arg).arg2())));
      }
    }
    return F.binaryAST2(S.Around, product, engine.evaluate(F.Sqrt(squares)));
  }

  /**
   * The value of a unary function at an {@link S#Around}, propagated to first order:
   * <code>f(Around(x, delta))</code> is <code>Around(f(x), Abs(f'(x))*delta)</code>.
   *
   * @param head the function being applied
   * @param around the uncertain argument
   * @return {@link F#NIL} if the derivative cannot be found
   */
  public static IExpr mapFunction(IExpr head, IAST around, EvalEngine engine) {
    ISymbol x = F.Dummy("x");
    // D(Abs(x),x) is the unusable Abs'(x). An Around holds a real value, where Abs is RealAbs, and
    // D(RealAbs(x),x) is x/RealAbs(x) - so the slope is taken from that instead.
    IExpr slopeHead = head == S.Abs && around.arg1().isReal() ? S.RealAbs : head;
    return propagate(F.unaryAST1(head, x), F.unaryAST1(slopeHead, x), x, around, engine);
  }

  /**
   * A {@link S#Power} with an {@link S#Around} as its base or its exponent, propagated to first
   * order. Handles <code>Around(x,d)^n</code> and <code>b^Around(x,d)</code>, which is what
   * <code>Sqrt</code> and <code>Exp</code> of an uncertain value reduce to.
   *
   * @return {@link F#NIL} if neither argument is an {@code Around}, or the derivative is unusable
   */
  public static IExpr power(IAST powerAST, EvalEngine engine) {
    IExpr base = powerAST.arg1();
    IExpr exponent = powerAST.arg2();
    ISymbol x = F.Dummy("x");
    if (isAround(base) && !isAround(exponent)) {
      IExpr power = F.Power(x, exponent);
      return propagate(power, power, x, (IAST) base, engine);
    }
    if (isAround(exponent) && !isAround(base)) {
      IExpr power = F.Power(base, x);
      return propagate(power, power, x, (IAST) exponent, engine);
    }
    return F.NIL;
  }

  /**
   * First-order propagation of an uncertainty through <code>expression</code>, in which
   * <code>x</code> stands for the uncertain value: the result is
   * <code>Around(expression(value), Abs(d slopeExpression/dx at value) * delta)</code>.
   *
   * <p>
   * <code>slopeExpression</code> is normally <code>expression</code> itself; it differs only where
   * the value is computed by one function and the derivative is better taken from another - Abs
   * and RealAbs.
   *
   * @return {@link F#NIL} if the derivative cannot be found or is not numeric at the value
   */
  private static IExpr propagate(IExpr expression, IExpr slopeExpression, ISymbol x, IAST around,
      EvalEngine engine) {
    IExpr value = around.arg1();
    IExpr derivative = engine.evaluate(F.D(slopeExpression, x));
    if (derivative.isIndeterminate() || !derivative.isFree(S.D)) {
      return F.NIL;
    }
    IExpr slope = engine.evaluate(F.subst(derivative, x, value));
    if (!slope.isNumericFunction(true)) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.subst(expression, x, value));
    if (!isAsymmetric(around)) {
      return F.binaryAST2(S.Around, result,
          engine.evaluate(F.Times(F.Abs(slope), around.arg2())));
    }
    // Each side is scaled by the magnitude of the slope and stays where it is. A decreasing
    // function does NOT swap them, even though reading the pair as an interval says it should:
    // Mathematica answers 1/Around[2,{0.1,0.3}] as Around[0.5,{0.025,0.075}], keeping 0.1 on the
    // downward side. Multiplication by a negative number is the one place that does swap - see
    // #timesAsymmetric - so the two are deliberately not unified.
    IExpr magnitude = F.Abs(slope);
    return around(result, engine.evaluate(F.Times(magnitude, lower(around))),
        engine.evaluate(F.Times(magnitude, upper(around))));
  }

  public static void initialize() {
    Initializer.init();
  }

  private AroundFunctions() {}
}
