package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.series.dp.Ensemble;
import org.matheclipse.core.numerics.series.dp.SeriesAlgorithm;
import org.matheclipse.core.numerics.series.dp.SeriesAlgorithm.SeriesSolution;
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

    if (ast.isAST2() && ast.arg2().isList3() || ast.arg2().isList4()) {
      // if (Summations.isConvergent(ast)) {
      // ast = ast.apply(S.Sum);
      // IAST preevaledSum = engine.preevalForwardBackwardAST(ast, 1);
      //
      // IExpr temp = evaluateSum(preevaledSum, engine);
      // if (temp.isPresent()) {
      // if (temp.isNumericFunction(true)) {
      // return engine.evalN(temp);
      // }
      // if (!temp.isFree(S.Sum, true)) {
      // temp = F.subst(temp, x -> x == S.Sum ? S.NSum : F.NIL);
      // }
      // return temp;
      // }
      // }

      if (ast.isAST2() && ast.arg2().isList3()) {
        IExpr function = arg1;
        IAST limits = (IAST) engine.evaluate(ast.arg2());
        IExpr variable = limits.arg1();
        IExpr lowerLimit = limits.arg2();
        IExpr upperLimit = limits.arg3();
        IASTMutable copy = ast.copy();
        copy.set(1, function);
        copy.set(2, limits);
        IExpr temp = nsum(function, variable, lowerLimit, upperLimit, copy);
        if (temp.isPresent()) {
          return temp;
        }
      }

    }
    return F.NIL;
  }

  public static IExpr nsum(IExpr function, IExpr variable, IExpr lowerLimit, IExpr upperLimit,
      IAST ast) {
    IExpr temp = sumMinusInfinityToInfinity(ast, function, variable, lowerLimit, upperLimit);
    if (temp.isPresent()) {
      return temp;
    }
    return sumStartToInfinity(function, variable, lowerLimit, upperLimit, ast);
  }

  private static IExpr sumMinusInfinityToInfinity(IAST ast, IExpr function, IExpr variable,
      IExpr lowerLimit, IExpr upperLimit) {
    // transform sym -> -sym and swap the upper_limit = F.CInfinity
    // and lower_limit = - upper_limit
    if (lowerLimit.isNegativeInfinity()) {
      if (upperLimit.isInfinity()) {
        lowerLimit = F.C0;
        IAST limit1 = F.List(variable, lowerLimit, upperLimit);
        IExpr sum1 = sumStartToInfinity(function, variable, lowerLimit, upperLimit,
            F.NSum(ast.arg1(), limit1));
        final IExpr variableNegate = EvalEngine.get().evaluate(F.Negate(variable));
        function = F.subst(function, x -> (x.equals(variable) ? variableNegate : F.NIL));
        lowerLimit = F.C1;
        upperLimit = F.CInfinity;
        IAST limit2 = F.List(variable, lowerLimit, upperLimit);
        IExpr sum2 = sumStartToInfinity(function, variable, lowerLimit, upperLimit,
            F.NSum(function, limit2));

        return F.Plus(sum1, sum2);
      }

      function = F.subst(function, x -> (x.equals(variable) ? variable.negate() : F.NIL));
      lowerLimit = upperLimit.negate();
      upperLimit = F.CInfinity;
      IAST limit3 = F.List(variable, lowerLimit, upperLimit);
      IExpr sum3 =
          sumStartToInfinity(function, variable, lowerLimit, upperLimit, F.NSum(function, limit3));
      return sum3;
    }
    return F.NIL;
  }

  private static IExpr sumStartToInfinity(IExpr function, IExpr variable, IExpr lowerLimit,
      IExpr upperLimit, IAST ast) {
    if (upperLimit.isInfinity()) {
      long start = lowerLimit.toLongDefault();
      if (start != Long.MIN_VALUE) {
        // if (Summations.isConvergent(ast)) {
        LongDoubleFunction longDoubleFunction = new LongDoubleFunction(function, variable);

        Iterable<Double> iter = Sequences.toIterable(longDoubleFunction, start);
        SeriesAlgorithm alg = new Ensemble(1e-8, 1000, 5);
        SeriesSolution limit = alg.limit(iter, true);
        return F.num(limit.limit);

        // LongComplexFunction longComplexFunction = new LongComplexFunction(function, variable);
        //
        // Iterable<Complex> iter = Sequences.toIterable(longComplexFunction, start);
        // SeriesAlgorithm alg = new Ensemble(1e-8, 1000, 5);
        // SeriesSolution limit = alg.limit(iter, true);
        // return F.num(limit.limit);
        // }
      }
    } else {
      long start = lowerLimit.toLongDefault();
      long end = upperLimit.toLongDefault();
      if (start != Long.MIN_VALUE && end != Long.MIN_VALUE) {
        LongDoubleFunction longDoubleFunction = new LongDoubleFunction(function, variable);

        Iterable<Double> iter = Sequences.toIterable(longDoubleFunction, start, end);
        SeriesAlgorithm alg = new Ensemble(1e-8, 1000, 5);
        SeriesSolution limit = alg.limit(iter, true);
        return F.num(limit.limit);

        // LongComplexFunction longComplexFunction = new LongComplexFunction(function, variable);
        // Iterator<Complex> iter = Sequences.toIterable(longComplexFunction, start,
        // end).iterator();
        // Complex result = Complex.ZERO;
        // for (long i = start; i < end; i++) {
        // Complex c = iter.next();
        // result = result.add(c);
        // }
        // return F.complexNum(result);
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
