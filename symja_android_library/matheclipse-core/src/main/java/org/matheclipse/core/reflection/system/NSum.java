package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.series.dp.complex.DirectComplex;
import org.matheclipse.core.numerics.series.dp.complex.EnsembleComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex.SeriesSolutionComplex;
import org.matheclipse.core.numerics.utils.Sequences;

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
      try {
        return unaryFunction.evalf(x -> x.equals(variable) ? F.ZZ(value) : F.NIL);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Double.NaN;
      }
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
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      arg1 = F.expand(arg1, false, false, false);
    }

    if (arg1.isPlus()) {
      return arg1.mapThread(ast, 1);
    }

    if (ast.isAST2() && ast.arg2().isList3()) {
      IAST limits = (IAST) engine.evaluate(ast.arg2());
      IExpr variable = limits.arg1();
      IExpr lowerLimit = limits.arg2();
      IExpr upperLimit = limits.arg3();
      long start = lowerLimit.toLongDefault();
      long end = upperLimit.toLongDefault() + 1;
      boolean preevaluateSymbolic = true;
      if (F.isPresent(start) && F.isPresent(end)) {
        long range = end - start;
        if (range > 10000 || range < -10000) {
          preevaluateSymbolic = false;
        }
      }
      if (preevaluateSymbolic) {
        // if (Summations.isConvergent(ast)) {
        ast = ast.apply(S.Sum);
        IAST preevaledSum = engine.preevalForwardBackwardAST(ast, 1);

        IExpr temp = evaluateSum(preevaledSum, engine);
        if (temp.isPresent()) {
          if (temp.isNumericFunction(true)) {
            return engine.evalN(temp);
          }
          if (!temp.isFree(S.Sum, true)) {
            temp = F.subst(temp, x -> x == S.Sum ? S.NSum : F.NIL);
          }
          return temp;
        }
        // }
      }

      IExpr function = arg1;

      IASTMutable copy = ast.copy();
      copy.set(1, function);
      copy.set(2, limits);
      IExpr temp = nsum(function, variable, lowerLimit, upperLimit);
      if (temp.isPresent()) {
        return temp;
      }

    }
    return F.NIL;
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
        IAST limit1 = F.List(variable, lowerLimit, upperLimit);
        IExpr sum1 = sumStartToInfinity(function, variable, lowerLimit, upperLimit);
        final IExpr variableNegate = EvalEngine.get().evaluate(F.Negate(variable));
        function = F.subst(function, x -> (x.equals(variable) ? variableNegate : F.NIL));
        lowerLimit = F.C1;
        upperLimit = F.CInfinity;
        IAST limit2 = F.List(variable, lowerLimit, upperLimit);
        IExpr sum2 = sumStartToInfinity(function, variable, lowerLimit, upperLimit);

        return F.Plus(sum1, sum2);
      }

      function = F.subst(function, x -> (x.equals(variable) ? variable.negate() : F.NIL));
      lowerLimit = upperLimit.negate();
      upperLimit = F.CInfinity;
      IAST limit3 = F.List(variable, lowerLimit, upperLimit);
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
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
