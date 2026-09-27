package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.series.dp.complex.EnsembleComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex.SeriesSolutionComplex;
import org.matheclipse.core.numerics.utils.Sequences;

/**
 * <pre>
 * <code>NProduct(expr, {i, imin, imax})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * evaluates the product of <code>expr</code> numerically for <code>i</code> ranging from
 * <code>imin</code> to <code>imax</code>.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; NProduct(1+1/k^2, {k, 1, Infinity})
 * 3.67608
 * </code>
 * </pre>
 */
public class NProduct extends AbstractFunctionEvaluator {

  /** The maximum number of factors which are multiplied directly. */
  private static final long MAX_DIRECT_RANGE = 100000L;

  public NProduct() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    IExpr limitList = engine.evaluate(ast.arg2());
    if (!limitList.isList3()) {
      return F.NIL;
    }
    IAST limits = (IAST) limitList;
    IExpr variable = limits.arg1();
    if (!variable.isVariable()) {
      // Raw object `1` cannot be used as an iterator.
      return Errors.printMessage(S.NProduct, "itraw", F.List(variable));
    }
    IExpr lowerLimit = limits.arg2();
    IExpr upperLimit = limits.arg3();
    long start = lowerLimit.toLongDefault();
    long end = upperLimit.toLongDefault();
    final boolean finite = F.isPresent(start) && F.isPresent(end);
    if (finite) {
      if (end < start) {
        // empty product
        return F.CD1;
      }
      if (end - start < MAX_DIRECT_RANGE) {
        IExpr temp = directProduct(function, variable, start, end);
        if (temp.isPresent()) {
          return temp;
        }
      }
    }

    // try a symbolic evaluation
    IExpr product = engine.evaluate(F.Product(function, limits));
    if (product.isFree(S.Product, true) && product.isNumericFunction(true)) {
      IExpr temp = engine.evalN(product);
      if (temp.isNumber()) {
        return temp;
      }
    }

    if (!finite && F.isPresent(start) && upperLimit.isInfinity()) {
      // Product(f(k)) == Exp(Sum(Log(f(k))))
      try {
        final IExpr logFunction = F.Log(function);
        Function<Long, Complex> logTerms = k -> {
          try {
            final IExpr value = F.ZZ(k);
            return logFunction.evalfc(x -> x.equals(variable) ? value : F.NIL);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Complex.NaN;
          }
        };
        Complex richardson = richardsonSum(logTerms, start);
        if (richardson != null) {
          return F.inexactNum(richardson.exp());
        }
        Iterable<Complex> seq = Sequences.toIterable(logTerms, start);
        SeriesAlgorithmComplex alg = new EnsembleComplex(1e-13, 2000, 5);
        SeriesSolutionComplex limit = alg.limit(seq, true);
        Complex sum = limit.limit;
        if (sum != null && !sum.isNaN() && !sum.isInfinite()) {
          return F.inexactNum(sum.exp());
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    return F.NIL;
  }

  /**
   * Sum the terms from <code>start</code> to infinity by Richardson extrapolation of the partial
   * sums of <code>8, 16, 32,...</code> terms. The extrapolation assumes that the partial sums have
   * an asymptotic expansion in powers of <code>1/n</code>, which is true for terms with an
   * algebraic decay. For faster decaying terms the partial sums are already constant.
   *
   * @return <code>null</code> if the extrapolated values don't converge
   */
  private static Complex richardsonSum(Function<Long, Complex> terms, long start) {
    final int levels = 11;
    Complex[][] table = new Complex[levels][levels];
    Complex sum = Complex.ZERO;
    long k = start;
    long count = 0;
    long n = 8;
    Complex best = null;
    double bestError = Double.MAX_VALUE;
    for (int j = 0; j < levels; j++) {
      while (count < n) {
        Complex term = terms.apply(k++);
        if (term == null || term.isNaN() || term.isInfinite()) {
          return null;
        }
        sum = sum.add(term);
        count++;
      }
      n *= 2;
      table[j][0] = sum;
      double factor = 1.0;
      for (int m = 1; m <= j; m++) {
        factor *= 2.0;
        table[j][m] = table[j][m - 1]
            .add(table[j][m - 1].subtract(table[j - 1][m - 1]).divide(factor - 1.0));
      }
      if (j > 0) {
        double error = table[j][j].subtract(table[j - 1][j - 1]).norm();
        if (error < bestError) {
          bestError = error;
          best = table[j][j];
        }
      }
    }
    if (best != null && bestError <= 1e-12 * (1.0 + best.norm())) {
      return best;
    }
    return null;
  }

  private static IExpr directProduct(IExpr function, IExpr variable, long start, long end) {
    try {
      Complex result = Complex.ONE;
      for (long i = start; i <= end; i++) {
        final IExpr value = F.ZZ(i);
        Complex factor = function.evalfc(x -> x.equals(variable) ? value : F.NIL);
        if (factor == null || factor.isNaN()) {
          return F.NIL;
        }
        result = result.multiply(factor);
      }
      return F.inexactNum(result);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
