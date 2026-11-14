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
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.series.dp.complex.EnsembleComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex;
import org.matheclipse.core.numerics.series.dp.complex.SeriesAlgorithmComplex.SeriesSolutionComplex;
import org.matheclipse.core.numerics.utils.Sequences;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.core.eval.TimeLimits;

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

  /** The time budget of the closed form of the product. */
  private static final long SYMBOLIC_MILLIS = 3000L;

  /** The maximum number of factors which are multiplied directly. */
  private static final long MAX_DIRECT_RANGE = 100000L;

  public NProduct() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() >= 3 && !ast.arg3().isRuleAST()) {
      return severalIterators(ast, engine);
    }
    if (ast.isAST3()) {
      return workingPrecisionProduct(ast, engine);
    }
    IExpr function = ast.arg1();
    IExpr limitList = ast.arg2();
    if (limitList.isList() && limitList.argSize() >= 2 && limitList.first().isSymbol()) {
      // the bounds are evaluated, the index keeps its name: a global value of it doesn't leak
      IASTMutable bounds = ((IAST) limitList).copy();
      for (int i = 2; i < bounds.size(); i++) {
        bounds.set(i, engine.evaluate(bounds.get(i)));
      }
      limitList = bounds;
    } else {
      limitList = engine.evaluate(limitList);
    }
    if (limitList.isList1()) {
      // a count is an iterator of Product, not of a numerical product
      // Raw object `1` cannot be used as an iterator.
      return Errors.printMessage(S.NProduct, "itraw", F.List(limitList.first()));
    }
    if (!limitList.isList3() && !limitList.isList4()) {
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
    final long step = limits.isList4() ? limits.arg4().toLongDefault() : 1L;
    if (!F.isPresent(step) || step <= 0L) {
      return F.NIL;
    }
    final boolean finite = F.isPresent(start) && F.isPresent(end);
    if (finite) {
      if (end < start) {
        // empty product
        return F.CD1;
      }
      if ((end - start) / step < MAX_DIRECT_RANGE) {
        IExpr temp = directProduct(function, variable, start, end, step);
        if (temp.isPresent()) {
          return temp;
        }
      }
    }

    // try a symbolic evaluation
    // quietly: the search for a closed form compares the complex roots of the factor
    // with a time budget, and an iteration or size limit of a Factorial of the bounds is no
    // answer: the numerical product follows
    IExpr product = TimeLimits.evaluateOrNil(F.Product(function, limits),
        org.matheclipse.core.basic.MachineProfile.millis(SYMBOLIC_MILLIS));
    if (product.isFree(S.Product, true) && product.isNumericFunction(true)) {
      IExpr temp = engine.evalN(product);
      if (temp.isNumber()) {
        return temp;
      }
    }

    if (!finite && step == 1L && F.isPresent(start) && upperLimit.isInfinity()) {
      // Product(f(k)) == Exp(Sum(Log(f(k))))
      try {
        final IExpr logFunction = logOfFactor(function, variable, start, engine);
        if (start > 1000L) {
          // far out the terms of the sum of the logarithms are tiny, and an extrapolation of its
          // partial sums sees too few of them: the numerical sum has the tail of 1/k^2 right
          ISymbol j = F.Dummy("j");
          IExpr logSum = engine.evalQuiet(F.NSum(F.subst(logFunction, variable, j),
              F.List(j, F.ZZ(start), F.CInfinity)));
          if (logSum.isNumber()) {
            return engine.evalN(F.Exp(logSum));
          }
        }
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
   * The logarithm of the factor <code>f == 1 + g</code> as <code>2*ArcTanh(g/(2 + g))</code>, with
   * <code>g</code> formed symbolically: <code>Log(1.0 + g)</code> keeps only the digits of a small
   * <code>g</code> which survive the addition, and is 0 from <code>g &lt; 10^-16</code> on, so the
   * far terms of the sum of the logarithms, <code>1 + 1/k^2</code> at <code>k == 10^6</code>, are
   * lost. A factor which is -1 at the start has no such form: <code>Log(f)</code> then.
   */
  private static IExpr logOfFactor(IExpr function, IExpr variable, long start,
      EvalEngine engine) {
    final IExpr log = F.Log(function);
    try {
      IExpr g = engine.evalBlock(() -> engine.evaluate(F.Together(F.Subtract(function, F.C1))),
          F.list(variable));
      if (g.isZero()) {
        return log;
      }
      IExpr form = F.Times(F.C2, F.ArcTanh(F.Divide(g, F.Plus(F.C2, g))));
      final IExpr first = F.ZZ(start);
      Complex value = form.evalfc(x -> x.equals(variable) ? first : F.NIL);
      if (value.isNaN() || value.isInfinite()) {
        return log;
      }
      return form;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return log;
    }
  }

  /**
   * <code>NProduct(f, {i, ...}, {j, ...}, ...)</code>: the product over the first iterator of the
   * products over the others.
   *
   * @return {@link F#NIL} if the product has no value
   */
  private static IExpr severalIterators(IAST ast, EvalEngine engine) {
    int iterators = 1;
    while (iterators + 2 <= ast.argSize() && !ast.get(iterators + 2).isRuleAST()) {
      iterators++;
    }
    IExpr lastIterator = engine.evaluate(ast.get(iterators + 1));
    if (lastIterator.isList1()) {
      // Raw object `1` cannot be used as an iterator.
      return Errors.printMessage(S.NProduct, "itraw", F.List(lastIterator.first()));
    }
    IASTAppendable inner = F.ast(S.NProduct);
    inner.append(ast.arg1());
    for (int i = 3; i <= ast.argSize(); i++) {
      inner.append(ast.get(i));
    }
    IASTAppendable outer = F.ast(S.NProduct);
    outer.append(inner);
    outer.append(ast.arg2());
    for (int i = iterators + 2; i <= ast.argSize(); i++) {
      outer.append(ast.get(i));
    }
    IExpr result = engine.evaluate(outer);
    return result.isAST(S.NProduct) ? F.NIL : result;
  }

  /**
   * <code>NProduct(f, {k, a, b}, WorkingPrecision -> p)</code>: for a machine precision the product
   * without the option; else the product of the factors with <code>p</code> digits, or for
   * <code>b = Infinity</code> <code>Exp(NSum(Log(f), {k, a, Infinity}, WorkingPrecision -> p))</code>.
   *
   * @return {@link F#NIL} if the product has no value with <code>p</code> digits
   */
  private IExpr workingPrecisionProduct(IAST ast, EvalEngine engine) {
    IExpr option = ast.arg3();
    if (!option.isRuleAST() || option.first() != S.WorkingPrecision) {
      return F.NIL;
    }
    int precision = FindRoot.workingPrecision(option.second());
    if (precision <= ParserConfig.MACHINE_PRECISION) {
      return evaluate(F.binaryAST2(S.NProduct, ast.arg1(), ast.arg2()), engine);
    }
    IExpr limitList = engine.evaluate(ast.arg2());
    if (!(limitList.isList3() || limitList.isList4()) || !limitList.first().isSymbol()) {
      return F.NIL;
    }
    IAST limits = (IAST) limitList;
    long start = limits.arg2().toLongDefault();
    long end = limits.arg3().toLongDefault();
    long step = limits.isList4() ? limits.arg4().toLongDefault() : 1L;
    if (F.isNotPresent(step) || step <= 0L) {
      return F.NIL;
    }
    if (F.isPresent(start) && F.isPresent(end)) {
      if (step == 1L && end - start < MAX_DIRECT_RANGE) {
        IExpr product = engine.evalN(F.Product(ast.arg1(), limits), precision);
        if (product.isNumber()) {
          return product;
        }
      }
      return factorProduct(ast.arg1(), limits.arg1(), start, end, step, precision, engine);
    }
    if (step != 1L || !F.isPresent(start) || !limits.arg3().isInfinity()) {
      return F.NIL;
    }
    IExpr sum = engine.evaluate(F.ternaryAST3(S.NSum, F.Log(ast.arg1()), limits,
        F.Rule(S.WorkingPrecision, F.ZZ(precision))));
    if (!sum.isNumber()) {
      return F.NIL;
    }
    IExpr product = engine.evaluate(F.Exp(sum));
    return product.isNumber() ? product : F.NIL;
  }

  /** The largest number of factors which are multiplied with a working precision. */
  private static final long MAX_PRECISION_FACTORS = 10_000_000L;

  /**
   * The product of the factors for <code>k = start, start + step, ..., end</code>, each with
   * <code>precision</code> digits and guard digits.
   *
   * @return {@link F#NIL} if a factor isn't a number or there are too many
   */
  private static IExpr factorProduct(IExpr function, IExpr variable, long start, long end,
      long step, int precision, EvalEngine engine) {
    if (end < start) {
      return F.num(new org.apfloat.Apfloat(1, precision));
    }
    if ((end - start) / step >= MAX_PRECISION_FACTORS) {
      return F.NIL;
    }
    // the rounding errors of n factors add up to about Log10(n) digits
    long digits = precision + 10L + (long) Math.ceil(Math.log10((end - start) / step + 1.0));
    org.apfloat.FixedPrecisionApcomplexHelper h =
        new org.apfloat.FixedPrecisionApcomplexHelper(digits);
    org.apfloat.Apcomplex product = new org.apfloat.Apfloat(1, digits);
    long count = 0L;
    for (long k = start; k <= end; k += step) {
      if ((++count & 255L) == 0L) {
        engine.checkTimeLimit();
      }
      final IExpr value = F.ZZ(k);
      IExpr factor =
          engine.evalN(F.subst(function, x -> x.equals(variable) ? value : F.NIL), digits);
      if (!factor.isNumber()) {
        return F.NIL;
      }
      product = h.multiply(product, ((org.matheclipse.core.interfaces.INumber) factor)
          .apcomplexValue());
    }
    product = product.precision(precision);
    return product.imag().signum() == 0 ? F.num(product.real()) : F.complexNum(product);
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

  private static IExpr directProduct(IExpr function, IExpr variable, long start, long end,
      long step) {
    try {
      Complex result = Complex.ONE;
      for (long i = start; i <= end; i += step) {
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
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
