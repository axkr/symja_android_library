package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.Random;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Tensors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.parser.client.ParserConfig;

public final class RandomFunctions {

  protected static int boundedNextInt(Random rng, int origin, int bound) {
    if (origin >= bound) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    int r = rng.nextInt();
    // It's not case (1).
    final int n = bound - origin;
    final int m = n - 1;
    if ((n & m) == 0) {
      // It is case (2): length of range is a power of 2.
      r = (r & m) + origin;
    } else if (n > 0) {
      // It is case (3): need to reject over-represented candidates.
      for (int u = r >>> 1; u + m - (r = u % n) < 0; u = rng.nextInt() >>> 1);
      r += origin;
    } else {
      // It is case (4): length of range not representable as long.
      while (r < origin || r >= bound) {
        r = rng.nextInt();
      }
    }
    return r;
  }

  protected static double boundedNextDouble(Random rng, double origin, double bound) {
    if (origin < bound) {
      double r = rng.nextDouble();
      r = r * (bound - origin) + origin;
      if (r >= bound) {
        // may need to correct a rounding problem
        r = Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
      }
      return r;
    }
    throw new IllegalArgumentException("bound must be greater than origin");
  }

  protected static double boundedNextDouble(Random rng, double bound) {
    // Specialize boundedNextDouble for origin == 0, bound > 0
    if (!(bound > 0.0 && bound < Double.POSITIVE_INFINITY)) {
      // The specification `1` is not a random distribution recognized by the system..
      throw new ArgumentTypeException("udist", F.List(F.num(bound)));
    }
    double r = rng.nextDouble();
    r = r * bound;
    if (r >= bound) {
      // may need to correct a rounding problem
      r = Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
    }
    return r;
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.SeedRandom.setEvaluator(new SeedRandom());
      S.Random.setEvaluator(new RandomSymbol());
      S.RandomInteger.setEvaluator(new RandomInteger());
      S.RandomPrime.setEvaluator(new RandomPrime());
      S.RandomChoice.setEvaluator(new RandomChoice());
      S.RandomComplex.setEvaluator(new RandomComplex());
      S.RandomPermutation.setEvaluator(new RandomPermutation());
      S.RandomReal.setEvaluator(new RandomReal());
      S.RandomSample.setEvaluator(new RandomSample());
    }
  }

  private static final class RandomSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      if (argSize == 0) {
        return S.RandomReal.evaluate(F.RandomReal(), engine);
      }
      if (argSize == 1) {
        IExpr arg1 = ast.arg1();
        if (arg1.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) arg1).ordinal()) {
            case ID.Integer:
              return S.RandomInteger.evaluate(F.RandomInteger(), engine);
            case ID.Real:
              return S.RandomReal.evaluate(F.RandomReal(), engine);
            case ID.Complex:
              return S.RandomReal.evaluate(F.RandomComplex(), engine);
          }
        }
        return F.NIL;
      }
      if (argSize == 2) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg1.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) arg1).ordinal()) {
            case ID.Integer:
              return S.RandomInteger.evaluate(F.RandomInteger(arg2), engine);
            case ID.Real:
              return S.RandomReal.evaluate(F.RandomReal(arg2), engine);
            case ID.Complex:
              return S.RandomReal.evaluate(F.RandomComplex(arg2), engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.DEPRECATED;
    }
  }

  /**
   *
   *
   * <pre>
   * RandomChoice({arg1, arg2, arg3,...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * chooses a random <code>arg</code> from the list.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RandomChoice({1,2,3,4,5,6,7})
   * 5
   * </pre>
   */
  private static final class RandomChoice extends AbstractFunctionEvaluator {

    /**
     * Draws indices with probability proportional to the weights, <b>with replacement</b>: every
     * choice is made from the whole list, which is what tells <code>RandomChoice</code> from
     * <code>RandomSample</code>.
     *
     * <p>
     * Through the generator handed in, which is the engine's. This used to be hipparchus's
     * {@code RandomDataGenerator}, one of its own making that <code>SeedRandom</code> cannot
     * reach - so a weighted choice was the one draw in this file that could not be reproduced,
     * while the unweighted one beside it always could.
     */
    private static final class WeightedSampler {
      /** The running sums of the weights, so a draw is one comparison walk. */
      private final double[] cumulative;
      private final Random random;

      /**
       * @return a sampler, or <code>null</code> when the weights are not usable: each has to be a
       *         number, non-negative and finite, and at least one of them positive
       */
      static WeightedSampler of(IAST weightList, int length, Random random) {
        double[] weights = weightList.toDoubleVector();
        if (weights == null || weights.length != length) {
          return null;
        }
        double[] cumulative = new double[length];
        double sum = 0.0;
        for (int i = 0; i < length; i++) {
          if (!(weights[i] >= 0.0) || Double.isInfinite(weights[i])) {
            return null;
          }
          sum += weights[i];
          cumulative[i] = sum;
        }
        if (!(sum > 0.0)) {
          return null;
        }
        return new WeightedSampler(cumulative, random);
      }

      private WeightedSampler(double[] cumulative, Random random) {
        this.cumulative = cumulative;
        this.random = random;
      }

      /** One index, drawn by the weights. */
      int next() {
        double point = random.nextDouble() * cumulative[cumulative.length - 1];
        for (int i = 0; i < cumulative.length; i++) {
          if (point < cumulative[i]) {
            return i;
          }
        }
        // the running sum can fall a rounding short of the total
        return cumulative.length - 1;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();

      if (arg1.isRuleAST() && arg1.first().isList() && arg1.second().isList()
          && arg1.first().size() == arg1.second().size()) {
        IAST weights = (IAST) arg1.first();
        IAST items = (IAST) arg1.second();
        WeightedSampler sampler =
            WeightedSampler.of(weights, items.argSize(), engine.getRandom());
        if (sampler != null) {
          if (ast.isAST1()) {
            return items.get(sampler.next() + 1);
          }
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            //
            if (arg2.isList()) {
              // n1 x n2 x n3 ... array
              int[] dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
              if (dimension == null) {
                return F.NIL;
              }
              return Tensors.build(() -> items.get(sampler.next() + 1), dimension);
            }
            int n = arg2.toMachineInt();
            if (n > 0) {
              return F.mapRange(0, n, i -> items.get(sampler.next() + 1));
            }
          }
        }
      } else if (arg1.isList()) {
        IAST list = (IAST) arg1;
        Random random = engine.getRandom();
        final int listSize = list.argSize();
        if (listSize == 0) {
          return F.NIL;
        }
        int randomIndex = random.nextInt(listSize);
        if (ast.isAST1()) {
          return list.get(randomIndex + 1);
        }
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          //
          if (arg2.isList()) {
            // n1 x n2 x n3 ... array
            int[] dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
            if (dimension == null) {
              return F.NIL;
            }
            int[] randomValue = new int[1];
            return Tensors.build(() -> {
              randomValue[0] = random.nextInt(listSize);
              return list.get(randomValue[0] + 1);
            }, dimension);
          }
          int n = arg2.toMachineInt();
          if (n > 0) {
            IASTAppendable result = F.ListAlloc(n);
            for (int i = 0; i < n; i++) {
              result.append(list.get(randomIndex + 1));
              randomIndex = random.nextInt(listSize);
            }
            return result;
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class RandomComplex extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST0()) {
          // RandomReal() gives a double value between 0.0 and 1.0
          Random tlr = engine.getRandom();
          double re = tlr.nextDouble();
          double im = tlr.nextDouble();
          return F.complexNum(re, im);
        } else if (ast.isAST1()) {
          if (ast.arg1().isList2()) {
            Complex min = engine.evalComplex(ast.arg1().first());
            Complex max = engine.evalComplex(ast.arg1().second());
            double minRe = min.getReal();
            double minIm = min.getImaginary();
            double maxRe = max.getReal();
            double maxIm = max.getImaginary();
            if (minRe >= maxRe) {
              double temp = minRe;
              minRe = maxRe;
              maxRe = temp;
              if (minRe == maxRe) {
                // return S.num(min);
              }
            }
            if (minIm >= maxIm) {
              double temp = minIm;
              minIm = maxIm;
              maxIm = temp;
              if (F.isEqual(minIm, maxIm) && F.isEqual(minRe, maxRe)) {
                return F.complexNum(minRe, minIm);
              }
            }
            Random tlr = engine.getRandom();
            return F.complexNum(boundedNextDouble(tlr, minRe, maxRe),
                boundedNextDouble(tlr, minIm, maxIm));
          } else {
            Complex max = engine.evalComplex(ast.arg1());
            Random tlr = engine.getRandom();
            return F.complexNum(boundedNextDouble(tlr, max.getReal()),
                boundedNextDouble(tlr, max.getImaginary()));
          }
        } else if (ast.isAST2()) {
          if (ast.arg2().isList()) {
            IAST list = (IAST) ast.arg2();
            int[] dimension = Validate.checkListOfInts(ast, list, 1, Integer.MAX_VALUE, engine);
            if (dimension == null) {
              return F.NIL;
            }
            IExpr[] arr = new IExpr[list.size()];
            arr[0] = F.RandomComplex(ast.arg1());
            for (int i = 1; i < list.size(); i++) {
              arr[i] = F.list(list.get(i));
            }
            return F.ast(arr, S.Table);
          }
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   *
   *
   * <pre>
   * RandomInteger(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a random integer number between <code>0</code> and <code>n</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RandomInteger(100)
   * 88
   * </pre>
   */
  private static final class RandomInteger extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        Random tlr = engine.getRandom();
        return randomBigInteger(BigInteger.ONE, false, tlr);
      }

      if (ast.arg1().isList2()) {
        int min = ast.arg1().first().toIntDefault();
        int max = ast.arg1().second().toIntDefault();
        if (F.isPresent(min) && F.isPresent(max)) {
          if (min >= max) {
            int temp = min;
            min = max;
            max = temp;
            if (min == max) {
              return F.ZZ(min);
            }
          }
          if (max == Integer.MAX_VALUE) {
            return F.NIL;
          }
          Random tlr = engine.getRandom();
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            if (arg2.isList()) {
              // n1 x n2 x n3 ... array
              int[] dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
              if (dimension == null || dimension.length == 0) {
                return F.NIL;
              }
              final int min2 = min;
              final int max2 = max;
              return Tensors.build(() -> F.ZZ(tlr.nextInt((max2 - min2) + 1) + min2), dimension);
            }
            int size = arg2.toMachineInt();
            if (size >= 0) {
              final int minimum = min;
              final int randomBound = (max - minimum) + 1;
              return F.mapRange(0, size, i -> F.ZZ(tlr.nextInt(randomBound) + minimum));
            }
            return F.NIL;
          }
          return F.ZZ(tlr.nextInt((max - min) + 1) + min);
        }
        return F.NIL;
      }
      if (ast.arg1().isInteger()) {
        // RandomInteger(100) gives an integer between 0 and 100
        final Random tlr = engine.getRandom();
        BigInteger upperLimit = ((IInteger) ast.arg1()).toBigNumerator();
        final boolean negative;
        if (upperLimit.signum() < 0) {
          upperLimit = upperLimit.negate();
          negative = true;
        } else {
          negative = false;
        }
        if (ast.isAST2() && !ast.arg2().isEmptyList()) {
          IExpr arg2 = ast.arg2();
          if (arg2.isList()) {
            // n1 x n2 x n3 ... array
            int[] dimension = Validate.checkListOfInts(ast, arg2, 0, Integer.MAX_VALUE, engine);
            if (dimension == null) {
              return F.NIL;
            }
            final BigInteger upperLimit2 = upperLimit;
            final boolean negative2 = negative;
            return Tensors.build(() -> randomBigInteger(upperLimit2, negative2, tlr), dimension);
          }
          int size = arg2.toMachineInt();
          if (size >= 0) {
            BigInteger limit = upperLimit;
            return F.mapRange(0, size, i -> randomBigInteger(limit, negative, tlr));
          }

        } else {
          return randomBigInteger(upperLimit, negative, tlr);
        }
      }

      return F.NIL;
    }

    private IExpr randomBigInteger(BigInteger upperLimit, boolean negative, Random tlr) {
      BigInteger r;
      final int nlen = upperLimit.bitLength();
      do {
        r = new BigInteger(nlen, tlr);
      } while (r.compareTo(upperLimit) > 0);
      return F.ZZ(negative ? r.negate() : r);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class RandomPermutation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int d = ast.arg1().toMachineInt();

      if (d > 0) {
        IAST randomVariate = F.RandomVariate(F.UniformDistribution(F.list(F.C0, F.C1)), F.ZZ(d));
        final IExpr ordering = S.Ordering.of(engine, randomVariate);
        if (ast.isAST1()) {
          // one permutation
          return F.Cycles(F.list(ordering));
        } else {
          int n = ast.arg2().toMachineInt();
          if (n > 0) {
            // a list of n permutations
            return F.mapRange(0, n, i -> F.Cycles(F.list(ordering)));

          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class RandomPrime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      boolean parametersChecked = false;
      BigInteger TWO = BigInteger.valueOf(2L);
      BigInteger lowerLimit = TWO;
      BigInteger upperLimit = BigInteger.ONE;
      if (arg1.isInteger()) {
        upperLimit = ((IInteger) arg1).toBigNumerator();
        if (upperLimit.compareTo(TWO) < 0) {
          // Positive integer expected.
          return Errors.printMessage(ast.topHead(), "intp", F.CEmptyList, engine);
        }
        if (upperLimit.compareTo(TWO) < 0) {
          // There are no primes in the specified interval.
          return Errors.printMessage(ast.topHead(), "noprime", F.CEmptyList, engine);
        }
        parametersChecked = true;
      } else if (arg1.isList2() && arg1.first().isInteger() && arg1.second().isInteger()) {
        lowerLimit = ((IInteger) arg1.first()).toBigNumerator();
        upperLimit = ((IInteger) arg1.second()).toBigNumerator();
        if (lowerLimit.compareTo(TWO) < 0) {
          // Positive integer expected.
          return Errors.printMessage(ast.topHead(), "intp", F.CEmptyList, engine);
        }
        if (upperLimit.compareTo(TWO) < 0) {
          // Positive integer expected.
          return Errors.printMessage(ast.topHead(), "intp", F.CEmptyList, engine);
        }
        if (upperLimit.compareTo(lowerLimit) < 0) {
          // There are no primes in the specified interval.
          return Errors.printMessage(ast.topHead(), "noprime", F.CEmptyList, engine);
        }
        if (!lowerLimit.isProbablePrime(32)
            && upperLimit.compareTo(lowerLimit.nextProbablePrime()) < 0) {
          // There are no primes in the specified interval.
          return Errors.printMessage(ast.topHead(), "noprime", F.CEmptyList, engine);
        }
        parametersChecked = true;
      } else {
        // Positive integer expected.
        Errors.printMessage(ast.topHead(), "intp", F.CEmptyList, engine);
        return F.NIL;
      }
      if (parametersChecked) {
        try {
          if (ast.isAST2()) {
            int[] dimension = Validate.checkDimension(ast, ast.arg2(), engine);
            if (dimension == null) {
              return F.NIL;
            }
            final BigInteger lowLimit = lowerLimit;
            final BigInteger highLimit = upperLimit;
            return Tensors.build(() -> randomPrime(lowLimit, highLimit, engine), dimension);
          }
          return randomPrime(lowerLimit, upperLimit, engine);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // There are no primes in the specified interval.
          return Errors.printMessage(ast.topHead(), "noprime", F.CEmptyList, engine);
        }
      }
      return F.NIL;
    }

    private static IExpr randomPrime(BigInteger lowerLimit, BigInteger upperLimit,
        EvalEngine engine) {

      if (lowerLimit.isProbablePrime(32)
          && upperLimit.compareTo(lowerLimit.nextProbablePrime()) < 0) {
        return F.ZZ(lowerLimit);
      }

      final int llen = lowerLimit.bitLength();
      final int ulen = upperLimit.bitLength();
      Random tlr = engine.getRandom();
      BigInteger randomNumber;
      long counter = 0;
      do {
        int blen = boundedNextInt(tlr, llen, ulen + 1);
        randomNumber = new BigInteger(blen, 32, tlr);
        if (counter++ > 100000) {
          randomNumber = lowerLimit.nextProbablePrime();
          break;
        }

      } while (randomNumber.compareTo(upperLimit) > 0 || randomNumber.compareTo(lowerLimit) < 0);
      return F.ZZ(randomNumber);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * RandomReal()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a random number between <code>0.0</code> and <code>1.0</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RandomReal( )
   * 0.53275
   * </pre>
   */
  private static final class RandomReal extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize >= 0 && argSize < ast.size()) {
        ast = ast.copyUntil(argSize + 1);
      }
      if (options[0].isReal()) {
        int workingPrecision = options[0].toIntDefault();
        if (workingPrecision <= 0) {
          return F.NIL;
        }
        if (ParserConfig.MACHINE_PRECISION <= workingPrecision) {
          // WorkingPrecision
          if (ast.isAST0()) {
            // RandomReal() gives a value between 0.0 and 1.0
            long oldPrecision = engine.getNumericPrecision();
            try {
              FixedPrecisionApfloatHelper apfloat = engine.setNumericPrecision(workingPrecision);
              Apfloat random = apfloat.random();
              return F.num(random);
            } finally {
              engine.setNumericPrecision(oldPrecision);
            }
          }
          return F.NIL;
        }

      }

      if (ast.isAST0()) {
        // RandomReal() gives a double value between 0.0 and 1.0
        Random tlr = engine.getRandom();
        double r = tlr.nextDouble();
        return F.num(r);
      } else {
        IExpr arg1 = ast.arg1();
        if (ast.isAST1()) {
          return randomReal(arg1, engine);
        } else if (ast.isAST2()) {
          if (ast.arg2().isList()) {
            if (ast.arg2().argSize() == 1) {
              int n = ast.arg2().first().toMachineInt();
              if (n <= 0) {
                return F.NIL;
              }
              return randomASTRealVector(arg1, n, engine);
            }
            IAST list = (IAST) ast.arg2();
            int[] dimension = Validate.checkListOfInts(ast, list, 1, Integer.MAX_VALUE, engine);
            if (dimension == null) {
              return F.NIL;
            }
            return Tensors.build(() -> randomReal(arg1, engine), dimension);
          }
          int n = ast.arg2().toMachineInt();
          if (n > 0) {
            return randomASTRealVector(arg1, n, engine);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr randomReal(IExpr arg1, EvalEngine engine) {
      if (arg1.isList2()) {
        double min = engine.evalDouble(arg1.first());
        double max = engine.evalDouble(arg1.second());
        if (min >= max) {
          double temp = min;
          min = max;
          max = temp;
          if (min == max) {
            return F.num(min);
          }
        }

        Random tlr = engine.getRandom();
        return F.num(boundedNextDouble(tlr, min, max));
      } else {
        boolean isNegative = false;
        double max = engine.evalDouble(arg1);
        if (max < 0) {
          isNegative = true;
          max = Math.abs(max);
        }
        if (F.isZero(max)) {
          return F.CD0;
        }
        Random tlr = engine.getRandom();
        double nextDouble = boundedNextDouble(tlr, max);
        if (isNegative) {
          nextDouble *= -1;
        }
        return F.num(nextDouble);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    private static IExpr randomASTRealVector(final IExpr arg1, int n, EvalEngine engine) {

      if (Config.MAX_AST_SIZE < n) {
        ASTElementLimitExceeded.throwIt(n);
      }
      Random tlr = engine.getRandom();
      double[] array = new double[n];
      if (arg1.isList2()) {
        double min = engine.evalDouble(arg1.first());
        double max = engine.evalDouble(arg1.second());
        if (min >= max) {
          double temp = min;
          min = max;
          max = temp;
          if (min == max) {
            return F.num(min);
          }
        }
        for (int i = 0; i < array.length; i++) {
          array[i] = boundedNextDouble(tlr, min, max);
        }

      } else {
        if (arg1.isAST()) {
          IAST dist = (IAST) arg1;
          if (dist.head().isSymbol()) {
            ISymbol head = (ISymbol) dist.head();
            if (head instanceof IBuiltInSymbol) {
              IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
              if (evaluator instanceof IRandomVariate) {
                // TODO refactor/optimize call
                return S.RandomVariate.ofNIL(engine, arg1, F.ZZ(n));
              }
            }
          }
        }
        boolean isNegative = false;
        double max = engine.evalDouble(arg1);
        if (max < 0) {
          isNegative = true;
          max = Math.abs(max);
        }
        for (int i = 0; i < array.length; i++) {
          if (F.isZero(max)) {
            continue;
          }
          array[i] = boundedNextDouble(tlr, max);
          if (isNegative) {
            array[i] *= -1.0;
          }
        }
      }
      return new ASTRealVector(array, false);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.WorkingPrecision};
      IExpr[] optionValues = new IExpr[] {S.Automatic};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }

  /**
   *
   *
   * <pre>
   * RandomSample(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * a random permutation of the elements of <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * a sample of <code>n</code> of the elements, drawn <b>without replacement</b>: no element is
   * sampled twice, and each is sampled with equal probability. There is no answer to give when
   * <code>n</code> is larger than the number of elements, so that is reported rather than
   * shortened - use <code>UpTo(n)</code> to ask for as many as are available.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample(list, UpTo(n))
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * a sample of <code>n</code> of the elements, or of as many as there are.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample({w1, w2, ...} -&gt; {e1, e2, ...}, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * a sample drawn by the elements' weights: each draw takes an element with probability
   * proportional to its weight among those left, and that element is then gone. An element of
   * weight <code>0</code> is drawn only once nothing with weight remains.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample(i;;j;;k, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * a sample of the <code>Span</code> from <code>i</code> to <code>j</code> in steps of
   * <code>k</code>.
   *
   * </blockquote>
   *
   * <p>
   * A dataset is sampled by its rows and gives a dataset back. The draw is made through the
   * engine's own generator, so <code>SeedRandom</code> governs it - including the generator
   * <code>SeedRandom</code>'s <code>Method</code> option selects.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SeedRandom(1); RandomSample({a,b,c,d})
   * {d,a,b,c}
   *
   * &gt;&gt; SeedRandom(1); RandomSample({a,b,c,d}, 2)
   * {d,a}
   *
   * &gt;&gt; SeedRandom(1); RandomSample({1,2,3}, UpTo(10))
   * {2,3,1}
   *
   * &gt;&gt; SeedRandom(1); RandomSample(1;;10;;2, 3)
   * {5,7,3}
   *
   * &gt;&gt; SeedRandom(1); RandomSample({1,2,3}-&gt;{a,b,c}, 2)
   * {c,b}
   * </pre>
   */
  private static final class RandomSample extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST weights = null;
      if (arg1.isRuleAST()) {
        // "RandomSample[{w1,w2,…}->{e1,e2,…},n]" - the elements with the weights to draw them by
        if (!arg1.first().isList() || !arg1.second().isList()
            || arg1.first().size() != arg1.second().size()) {
          return F.NIL;
        }
        weights = (IAST) arg1.first();
        arg1 = arg1.second();
      } else if (arg1.isAST(S.Span)) {
        // "RandomSample[i;;j;;k, n] may be used to sample the Span from i to j in steps of k"
        arg1 = spanAsList((IAST) arg1, engine);
      }
      if (!arg1.isList() && !arg1.isDataset()) {
        return F.NIL;
      }
      final int available =
          arg1.isDataset() ? ((IASTDataset) arg1).rowCount() : ((IAST) arg1).argSize();
      int n = available;
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        // "RandomSample[{e1,e2,…},UpTo[n]] gives a sample of n of the ei, or as many as are
        // available" - so UpTo is the one form that may ask for more than there is
        boolean upTo = arg2.isAST(S.UpTo, 2);
        IExpr count = upTo ? arg2.first() : arg2;
        if (upTo && count.isInfinity()) {
          n = available;
        } else {
          int requested = count.toIntDefault();
          if (requested < 0) {
            return F.NIL;
          }
          if (upTo) {
            n = Math.min(requested, available);
          } else {
            if (requested > available) {
              // The elements of `1` are not compatible with the sample size `2`.
              return Errors.printMessage(ast.topHead(), "smplen", F.List(ast.arg1(), arg2), engine);
            }
            n = requested;
          }
        }
      }
      if (arg1.isDataset()) {
        // the rows of the dataset, and a dataset back - the implementation is in
        // matheclipse-dataset and is reached through the interface core owns
        return ((IASTDataset) arg1).randomSample(n, engine.getRandom());
      }
      if (weights != null) {
        return weightedShuffle(weights, (IAST) arg1, n, engine.getRandom());
      }
      return shuffle((IAST) arg1, n, engine.getRandom());
    }

    /**
     * A sample drawn by the elements' weights and still without replacement: each draw takes an
     * element with probability proportional to its weight <b>among those left</b>, and that element
     * is then gone. Through the engine's own generator, so that <code>SeedRandom</code> governs a
     * weighted draw exactly as it governs a plain one.
     *
     * @return {@link F#NIL} when the weights are not usable - not all numbers, or one of them
     *         negative or not finite
     */
    private static IExpr weightedShuffle(IAST weightList, IAST items, int n, Random random) {
      final int len = items.argSize();
      double[] weights = weightList.toDoubleVector();
      if (weights == null || weights.length != len) {
        return F.NIL;
      }
      for (int i = 0; i < len; i++) {
        if (!(weights[i] >= 0.0) || Double.isInfinite(weights[i])) {
          return F.NIL;
        }
      }
      boolean[] taken = new boolean[len];
      IASTAppendable result = items.copyHead(n);
      for (int draw = 0; draw < n; draw++) {
        double total = 0.0;
        int remaining = 0;
        int last = -1;
        for (int i = 0; i < len; i++) {
          if (!taken[i]) {
            total += weights[i];
            remaining++;
            last = i;
          }
        }
        int chosen = -1;
        if (total > 0.0) {
          double point = random.nextDouble() * total;
          double sum = 0.0;
          for (int i = 0; i < len; i++) {
            if (taken[i]) {
              continue;
            }
            sum += weights[i];
            if (point < sum) {
              chosen = i;
              break;
            }
          }
          if (chosen < 0) {
            // the accumulated sum can fall a rounding short of the total
            chosen = last;
          }
        } else {
          // every weight left is zero, so nothing tells the remaining elements apart and they are
          // drawn evenly - the alternative would be to refuse a sample the caller can still have
          int k = random.nextInt(remaining);
          for (int i = 0; i < len; i++) {
            if (!taken[i] && k-- == 0) {
              chosen = i;
              break;
            }
          }
        }
        taken[chosen] = true;
        result.append(items.get(chosen + 1));
      }
      return result;
    }

    /**
     * The integers a <code>Span</code> runs over, as a list, or {@link F#NIL} when the span is not
     * one of concrete integers - <code>1;;10;;2</code> is <code>{1,3,5,7,9}</code>.
     */
    private static IExpr spanAsList(IAST span, EvalEngine engine) {
      if (span.size() < 3) {
        return F.NIL;
      }
      IExpr from = span.arg1();
      IExpr to = span.arg2();
      IExpr step = span.size() > 3 ? span.arg3() : F.C1;
      if (!from.isInteger() || !to.isInteger() || !step.isInteger() || step.isZero()) {
        return F.NIL;
      }
      IExpr range = engine.evaluate(F.Range(from, to, step));
      return range.isList() ? range : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    public static IAST shuffle(IAST list, int n, Random random) {
      final int len = list.argSize();

      // Shuffle indices.
      final int[] indexList = shuffledIndices(len, random);

      if (n < len) {
        IASTAppendable result = list.copyHead(n);
        for (int j = 0; j < n; j++) {
          result.append(list.get(indexList[j] + 1));
        }
        return result;
      }
      // Create shuffled list.
      return list.copy().setArgs(1, len + 1, i -> list.get(indexList[i - 1] + 1));
    }
  }

  private static final class SeedRandom extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      long seedValue = ast.arg1().toLongDefault();
      if (seedValue <= 0L) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(ast.topHead(), "intnm", F.List(F.C1, ast), engine);
      }
      if (ast.isAST2()) {
        // "A Method option to SeedRandom can be given to specify the pseudorandom generator used"
        IExpr option = ast.arg2();
        if (!option.isRuleAST() || option.first() != S.Method) {
          // Unknown option `1` in `2`.
          return Errors.printMessage(ast.topHead(), "optx", F.List(option, ast), engine);
        }
        Random generator = generatorFor(option.second());
        if (generator == null) {
          // The method `1` is not one of "Congruential", "Legacy" or "MersenneTwister".
          return Errors.printMessage(ast.topHead(), "seedm", F.List(option.second()), engine);
        }
        engine.setRandom(generator);
      }
      Random random = engine.getRandom();
      random.setSeed(seedValue);
      return F.ZZ(seedValue);
    }

    /**
     * The generator a <code>Method</code> names, or <code>null</code> when it names none of them.
     *
     * <p>
     * <code>"Legacy"</code> and <code>"Congruential"</code> are the linear congruential generator
     * of {@link Random}, which is what every random built-in draws from unless this says otherwise.
     * The reference lists further methods - <code>"ExtendedCA"</code>, <code>"MKL"</code>,
     * <code>"Rule30CA"</code> - which have no counterpart here and are declined rather than
     * quietly answered with a generator that is not the one asked for.
     */
    private static Random generatorFor(IExpr method) {
      if (!method.isString()) {
        return null;
      }
      String name = method.toString();
      if (name.equalsIgnoreCase("MersenneTwister")) {
        return new GeneratorRandom(new org.hipparchus.random.MersenneTwister());
      }
      if (name.equalsIgnoreCase("Legacy") || name.equalsIgnoreCase("Congruential")) {
        return new Random();
      }
      return null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * A {@link Random} that draws from one of hipparchus's generators, so that the built-ins - all of
   * which ask the engine for a <code>Random</code> - can be pointed at a different one.
   *
   * <p>
   * Only {@code next(int)} is overridden, and deliberately: every other method of
   * <code>Random</code> is defined in terms of it, so the distributions stay the ones
   * <code>Random</code> documents and only the stream of bits changes.
   */
  private static final class GeneratorRandom extends Random {
    private static final long serialVersionUID = 1L;

    private final org.hipparchus.random.RandomGenerator generator;

    GeneratorRandom(org.hipparchus.random.RandomGenerator generator) {
      this.generator = generator;
    }

    @Override
    protected int next(int bits) {
      return generator.nextInt() >>> (32 - bits);
    }

    @Override
    public synchronized void setSeed(long seed) {
      // called by Random's own constructor, before the field is assigned
      if (generator != null) {
        generator.setSeed(seed);
      }
    }
  }

  /**
   * The numbers <code>0..len-1</code> in a random order.
   *
   * <p>
   * Through the engine's own generator, which is what <code>SeedRandom</code> seeds. This used to
   * be <code>MathArrays.shuffle</code>, whose generator is hipparchus's and which
   * <code>SeedRandom</code> therefore did not reach - so <code>RandomSample</code> was the one
   * function in this file that could not be made reproducible, while <code>RandomInteger</code>,
   * <code>RandomReal</code> and the rest could.
   *
   * <p>
   * Shared with <code>matheclipse-dataset</code>, so that sampling the rows of a dataset and
   * sampling a list of the same rows are the same operation rather than two that happen to agree.
   */
  public static int[] shuffledIndices(int len, Random random) {
    final int[] indexList = new int[len];
    for (int i = 0; i < len; i++) {
      indexList[i] = i;
    }
    // Fisher-Yates
    for (int i = len - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      int swap = indexList[i];
      indexList[i] = indexList[j];
      indexList[j] = swap;
    }
    return indexList;
  }

  public static void initialize() {
    Initializer.init();
  }

  private RandomFunctions() {}
}
