package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.series.dp.complex.DirectComplex;
import org.matheclipse.core.numerics.series.dp.complex.EnsembleComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex.SeriesSolutionComplex;
import org.matheclipse.core.numerics.utils.Sequences;
import org.matheclipse.core.numerics.series.PrecisionSum;
import org.matheclipse.parser.client.ParserConfig;
import org.apfloat.Apcomplex;
import org.matheclipse.core.interfaces.IASTAppendable;

public class NSum extends Sum {

  public NSum() {}

  private static class LongDoubleFunction implements Function<Long, Double> {
    final IExpr unaryFunction;
    final IExpr variable;

    public LongDoubleFunction(final IExpr unaryFunction, final IExpr variable) {
      this.unaryFunction = unaryFunction;
      this.variable = variable;
    }

    @Override
    public Double apply(Long value) {
      return unaryFunction.evalfNaN(x -> x.equals(variable) ? F.ZZ(value) : F.NIL);
    }
  }

  private static class LongComplexFunction implements Function<Long, Complex> {
    final IExpr unaryFunction;
    final IExpr variable;

    public LongComplexFunction(final IExpr unaryFunction, final IExpr variable) {
      this.unaryFunction = unaryFunction;
      this.variable = variable;
    }

    @Override
    public Complex apply(Long value) {
      try {
        return unaryFunction.evalfc(x -> x.equals(variable) ? F.ZZ(value) : F.NIL);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Complex.NaN;
      }
    }
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {

    // NSum(f, {i, ...}, {j, ...}, ..., options)
    int iterators = 1;
    while (iterators + 1 < ast.size() && !ast.get(iterators + 1).isRuleAST()) {
      iterators++;
    }
    iterators--;
    if (iterators < 1) {
      return F.NIL;
    }
    IAST options = ast.subList(iterators + 2);
    for (IExpr option : options) {
      if (!option.isRuleAST()) {
        return F.NIL;
      }
    }
    long digits = ParserConfig.MACHINE_PRECISION;
    for (IExpr option : options) {
      if (option.first() == S.WorkingPrecision) {
        int p = FindRoot.workingPrecision(option.second());
        digits = p > 0 ? p : ParserConfig.MACHINE_PRECISION;
      }
    }
    if (iterators > 1) {
      // the sum over the first iterator of the sums over the others
      IASTAppendable inner = F.ast(S.NSum);
      inner.append(ast.arg1());
      for (int i = 3; i < iterators + 2; i++) {
        inner.append(ast.get(i));
      }
      inner.appendArgs(options);
      IASTAppendable outer = F.ast(S.NSum);
      outer.append(inner);
      outer.append(ast.arg2());
      outer.appendArgs(options);
      IExpr result = engine.evaluate(outer);
      // without a value the call stays as it was written, not as the nested sums
      return result.isAST(S.NSum) && result.first().isAST(S.NSum) ? F.NIL : result;
    }
    IExpr arg1 = ast.arg1();
    if (arg1.isAST() && arg1.isFree(S.NSum, true)) {
      arg1 = F.expand(arg1, false, false, false);
    }

    if (arg1.isPlus()) {
      IASTMutable sums = ((IAST) arg1).apply(S.Plus);
      for (int i = 1; i < sums.size(); i++) {
        sums.set(i, ast.setAtCopy(1, sums.get(i)));
      }
      return sums;
    }

    IExpr limitList = engine.evaluate(ast.arg2());
    if (limitList.isList3()) {
      IAST limits = (IAST) limitList;
      IExpr variable = limits.arg1();
      if (!variable.isVariable()) {
        // Raw object `1` cannot be used as an iterator.
        return Errors.printMessage(S.NSum, "itraw", F.List(variable));
      }
      IExpr lowerLimit = limits.arg2();
      IExpr upperLimit = limits.arg3();
      long start = lowerLimit.toLongDefault();
      long end = upperLimit.toLongDefault();
      // a numerical sum of an infinite series: no search for a closed form, which may take long
      boolean preevaluateSymbolic = false;
      if (F.isPresent(start) && F.isPresent(end) && arg1.isFree(S.NSum, true)) {
        long range = end - start;
        preevaluateSymbolic = range <= 10000 && range >= -10000;
      }
      if (upperLimit.isInfinity() && arg1.has(x -> x.isAST() && (x.head() == S.DivisorSigma
          || x.head() == S.EulerPhi || x.head() == S.MoebiusMu || x.head() == S.LiouvilleLambda),
          true)) {
        // an arithmetic function isn't smooth in the index: no extrapolation of the partial
        // sums is reliable, but its Dirichlet series may have a closed form
        preevaluateSymbolic = true;
      }
      if (preevaluateSymbolic) {
        IAST sum = F.Sum(arg1, limits);
        IAST preevaledSum = engine.preevalForwardBackwardAST(sum, 1);

        IExpr temp = evaluateSum(preevaledSum, engine);
        if (temp.isPresent()) {
          if (temp.isNumericFunction(true)) {
            return digits > ParserConfig.MACHINE_PRECISION ? engine.evalN(temp, digits)
                : engine.evalN(temp);
          }
          if (!temp.isFree(S.Sum, true)) {
            temp = F.subst(temp, x -> x == S.Sum ? S.NSum : F.NIL);
          }
          return temp;
        }
      }

      if (variable.isSymbol()) {
        IExpr precise = precisionSum(arg1, (ISymbol) variable, lowerLimit, upperLimit, digits,
            engine);
        if (precise.isPresent()) {
          return precise;
        }
      }
      if (digits > ParserConfig.MACHINE_PRECISION || !arg1.isFree(S.NSum, true)) {
        // an extrapolation of the partial sums of inner numerical sums isn't reliable
        return F.NIL;
      }
      if (F.isPresent(start) && !(numericTerm(arg1, variable, start, engine)
          && numericTerm(arg1, variable, start + 1, engine))) {
        // the summand has no numeric value
        return F.NIL;
      }
      try {
        IExpr temp = nsum(arg1, variable, lowerLimit, upperLimit);
        if (temp.isPresent() && temp.isNumber()) {
          return temp;
        }
      } catch (RuntimeException rex) {
        // the extrapolation of the partial sums failed: no value
        Errors.rethrowsInterruptException(rex);
      }
    }
    return F.NIL;
  }

  /** Test if the term with index <code>n</code> has a numeric value. */
  private static boolean numericTerm(IExpr function, IExpr variable, long n, EvalEngine engine) {
    return engine.evalN(F.subst(function, x -> x.equals(variable) ? F.ZZ(n) : F.NIL)).isNumber();
  }

  /**
   * The sum of an infinite series with <code>digits</code> digits, see {@link PrecisionSum}: from
   * <code>-Infinity</code> it is the sum of the two halves.
   *
   * @return {@link F#NIL} if the series isn't summed there
   */
  private static IExpr precisionSum(IExpr function, ISymbol variable, IExpr lowerLimit,
      IExpr upperLimit, long digits, EvalEngine engine) {
    if (!upperLimit.isInfinity()) {
      return F.NIL;
    }
    Apcomplex value;
    if (lowerLimit.isNegativeInfinity()) {
      Apcomplex right = PrecisionSum.sum(function, variable, 0L, digits, engine);
      if (right == null) {
        return F.NIL;
      }
      IExpr mirrored = F.subst(function, x -> x.equals(variable) ? F.Negate(variable) : F.NIL);
      Apcomplex left = PrecisionSum.sum(mirrored, variable, 1L, digits, engine);
      if (left == null) {
        return F.NIL;
      }
      value = right.add(left);
    } else {
      long start = lowerLimit.toLongDefault();
      if (F.isNotPresent(start)) {
        return F.NIL;
      }
      value = PrecisionSum.sum(function, variable, start, digits, engine);
      if (value == null) {
        return F.NIL;
      }
    }
    if (digits <= ParserConfig.MACHINE_PRECISION) {
      if (value.imag().signum() == 0) {
        return F.num(value.real().doubleValue());
      }
      return F.complexNum(value.real().doubleValue(), value.imag().doubleValue());
    }
    if (value.imag().signum() == 0) {
      return F.num(value.real().precision(digits));
    }
    return F.complexNum(value.precision(digits));
  }

  /**
   * Evaluate the sum of a function numerically with respect to a variable from a lower limit to an
   * upper limit.
   * 
   * @param function the function to sum
   * @param variable the variable with respect to which the sum is taken
   * @param lowerLimit the lower limit of the sum
   * @param upperLimit the upper limit of the sum
   * @return
   */
  public static IExpr nsum(IExpr function, IExpr variable, IExpr lowerLimit, IExpr upperLimit) {
    IExpr temp = sumMinusInfinityToInfinity(function, variable, lowerLimit, upperLimit);
    if (temp.isPresent()) {
      return temp;
    }
    return sumStartToInfinity(function, variable, lowerLimit, upperLimit);
  }

  /**
   * Evaluate the sum of a function numerically with respect to a variable from minus infinity to
   * plus infinity.
   * 
   * @param function the function to sum
   * @param variable the variable with respect to which the sum is taken
   * @param lowerLimit the lower limit of the sum, which must be minus infinity
   * @param upperLimit the upper limit of the sum, which must be plus infinity
   * @return {@link F#NIL} if the sum cannot be evaluated or the limits are not infinities,
   *         otherwise the result of the sum.
   */
  private static IExpr sumMinusInfinityToInfinity(IExpr function, IExpr variable, IExpr lowerLimit,
      IExpr upperLimit) {
    // transform sym -> -sym and swap the upper_limit = F.CInfinity
    // and lower_limit = - upper_limit
    if (lowerLimit.isNegativeInfinity()) {
      if (upperLimit.isInfinity()) {
        lowerLimit = F.C0;
        IExpr sum1 = sumStartToInfinity(function, variable, lowerLimit, upperLimit);
        final IExpr variableNegate = EvalEngine.get().evaluate(F.Negate(variable));
        function = F.subst(function, x -> (x.equals(variable) ? variableNegate : F.NIL));
        lowerLimit = F.C1;
        upperLimit = F.CInfinity;
        IExpr sum2 = sumStartToInfinity(function, variable, lowerLimit, upperLimit);

        return F.Plus(sum1, sum2);
      }

      function = F.subst(function, x -> (x.equals(variable) ? variable.negate() : F.NIL));
      lowerLimit = upperLimit.negate();
      upperLimit = F.CInfinity;
      IExpr sum3 = sumStartToInfinity(function, variable, lowerLimit, upperLimit);
      return sum3;
    }
    return F.NIL;
  }

  private static IExpr sumStartToInfinity(IExpr function, IExpr variable, IExpr lowerLimit,
      IExpr upperLimit) {
    if (upperLimit.isInfinity()) {
      long start = lowerLimit.toLongDefault();
      if (F.isPresent(start)) {
        // if (Summations.isConvergent(ast)) {

        LongComplexFunction longComplexFunction = new LongComplexFunction(function, variable);
        Iterable<Complex> seq = Sequences.toIterable(longComplexFunction, start);
        SeriesAlgorithmComplex alg = new EnsembleComplex(1e-8, 1000, 5);
        SeriesSolutionComplex limit = alg.limit(seq, true);
        return F.inexactNum(limit.limit);

        // LongDoubleFunction longDoubleFunction = new LongDoubleFunction(function, variable);
        //
        // Iterable<Double> seq = Sequences.toIterable(longDoubleFunction, start);
        // SeriesAlgorithm alg = new Ensemble(1e-8, 1000, 5);
        // SeriesSolution limit = alg.limit(seq, true);
        // return F.num(limit.limit);
      }
    } else {
      long start = lowerLimit.toLongDefault();
      long end = upperLimit.toLongDefault() + 1;
      if (F.isPresent(start) && F.isPresent(end) && start < end) {


        long range = end + 1 - start;
        if (range < Integer.MAX_VALUE) {
          LongComplexFunction longComplexFunction = new LongComplexFunction(function, variable);
          Iterable<Complex> seq = Sequences.toIterable(longComplexFunction, start, end);
          SeriesAlgorithmComplex alg = new DirectComplex(1e-8, (int) range, (int) range);
          SeriesSolutionComplex limit = alg.limit(seq, true);
          Complex result = limit.limit;
          if (result.isNaN()) {
            alg = new EnsembleComplex(1e-8, 1000, 5);
            limit = alg.limit(seq, true);
            return F.inexactNum(limit.limit);
          }
          return F.inexactNum(result);
          // Complex result = Complex.ZERO;
          // for (final Complex e : seq) {
          // result = result.add(e);
          // }
          // return F.complexNum(result);

          // LongDoubleFunction longDoubleFunction = new LongDoubleFunction(function, variable);
          // Iterable<Double> seq = Sequences.toIterable(longDoubleFunction, start, end);
          // double result = 0.0;
          // for (final double e : seq) {
          // result += e;
          // }
          // return F.num(result);
        } else {
          LongComplexFunction longComplexFunction = new LongComplexFunction(function, variable);
          Iterable<Complex> seq = Sequences.toIterable(longComplexFunction, start, end);
          SeriesAlgorithmComplex alg = new EnsembleComplex(1e-8, 1000, 5);
          SeriesSolutionComplex limit = alg.limit(seq, true);
          return F.inexactNum(limit.limit);

          // LongDoubleFunction longDoubleFunction = new LongDoubleFunction(function, variable);
          // Iterable<Double> seq = Sequences.toIterable(longDoubleFunction, start, end);
          // SeriesAlgorithm alg = new Ensemble(1e-8, 1000, 5);
          // SeriesSolution limit = alg.limit(seq, true);
          // return F.num(limit.limit);
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  /** Evaluate built-in rules and define Attributes for a function. */
  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
