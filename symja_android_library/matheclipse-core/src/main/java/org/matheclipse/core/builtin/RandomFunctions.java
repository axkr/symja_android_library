package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.Random;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.random.RandomDataGenerator;
import org.hipparchus.util.MathArrays;
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
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Tensors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
        if (arg1 == S.Integer) {
          return S.RandomInteger.evaluate(F.RandomInteger(), engine);
        }
        if (arg1 == S.Real) {
          return S.RandomReal.evaluate(F.RandomReal(), engine);
        }
        if (arg1 == S.Complex) {
          return S.RandomReal.evaluate(F.RandomComplex(), engine);
        }
        return F.NIL;
      }
      if (argSize == 2) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();

        if (arg1 == S.Integer) {
          return S.RandomInteger.evaluate(F.RandomInteger(arg2), engine);
        }
        if (arg1 == S.Real) {
          return S.RandomReal.evaluate(F.RandomReal(arg2), engine);
        }
        if (arg1 == S.Complex) {
          return S.RandomReal.evaluate(F.RandomComplex(arg2), engine);
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

    /** Sampler for enumerated distributions. */
    private static final class EnumeratedDistributionSampler {
      /** Probabilities */
      private final double[] weights;

      /**
       * Create an EnumeratedDistributionSampler from the provided weights.
       *
       * @param weights the weights of the distribution sampler
       */
      EnumeratedDistributionSampler(double[] weights) {
        this.weights = weights;
      }

      /**
       * Generates a random sample of size sampleSize from {0, 1, ... , weights.length - 1}, using
       * weights as probabilities.
       *
       * <p>
       * For 0 &lt; i &lt; weights.length, the probability that i is selected (on any draw) is
       * weights[i]. If necessary, the weights array is normalized to sum to 1 so that weights[i] is
       * a probability and the array sums to 1.
       *
       * <p>
       * Weights can be 0, but must not be negative, infinite or NaN. At least one weight must be
       * positive.
       *
       * @param sampleSize size of sample to generate
       * @return a random array of indexes from the distribution
       */
      public int[] sample(int sampleSize) {
        RandomDataGenerator rg = new RandomDataGenerator();
        return rg.nextSampleWithReplacement(sampleSize, weights);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();

      if (arg1.isRuleAST() && arg1.first().isList() && arg1.second().isList()
          && arg1.first().size() == arg1.second().size()) {
        IAST weights = (IAST) arg1.first();
        IAST items = (IAST) arg1.second();
        double[] itemWeights = weights.toDoubleVector();
        if (itemWeights != null) {
          EnumeratedDistributionSampler sampler = new EnumeratedDistributionSampler(itemWeights);
          if (ast.size() == 2) {
            int[] chosen = sampler.sample(1);
            return items.get(chosen[0] + 1);
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
              return Tensors.build(() -> {
                return items.get(sampler.sample(1)[0] + 1);
              }, dimension);
            }
            int n = arg2.toIntDefault();
            if (n > 0) {

              int[] chosen = sampler.sample(n);
              return F.mapRange(0, n, i -> items.get(chosen[i] + 1));
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
        if (ast.size() == 2) {
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
          int n = arg2.toIntDefault();
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
              if (minIm == maxIm && minRe == maxRe) {
                F.complexNum(minRe, minIm);
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
            int size = arg2.toIntDefault();
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
            int[] dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
            if (dimension == null) {
              return F.NIL;
            }
            final BigInteger upperLimit2 = upperLimit;
            final boolean negative2 = negative;
            return Tensors.build(() -> randomBigInteger(upperLimit2, negative2, tlr), dimension);
          }
          int size = arg2.toIntDefault();
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

      int d = ast.arg1().toIntDefault();

      if (d > 0) {
        IAST randomVariate = F.RandomVariate(F.UniformDistribution(F.list(F.C0, F.C1)), F.ZZ(d));
        if (ast.isAST1()) {
          // one permutation
          IExpr ordering = S.Ordering.of(engine, randomVariate);
          return F.Cycles(F.list(ordering));
        } else {
          int n = ast.arg2().toIntDefault();
          if (n > 0) {
            // a list of n permutations
            return F.mapRange(0, n, i -> F.Cycles(F.list(S.Ordering.of(engine, randomVariate))));

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
          } else {
            // IExpr arg1 = ast.arg1();
            // if (ast.isAST1()) {
            // return randomApfloat(arg1, workingPrecision, engine);
            // } else if (ast.isAST2()) {
            // if (ast.arg2().isList()) {
            // if (ast.arg2().argSize() == 1) {
            // int n = ast.arg2().first().toIntDefault();
            // if (n <= 0) {
            // return F.NIL;
            // }
            // return randomASTRealVector(arg1, n, engine);
            // }
            // IAST list = (IAST) ast.arg2();
            // int[] dimension = Validate.checkListOfInts(ast, list, 1, Integer.MAX_VALUE, engine);
            // if (dimension == null) {
            // return F.NIL;
            // }
            // return Tensors.build(() -> randomApfloat(arg1, workingPrecision, engine),
            // dimension);
            // }
            // int n = ast.arg2().toIntDefault();
            // if (n > 0) {
            // return randomASTRealVector(arg1, n, engine);
            // }
            // }
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
              int n = ast.arg2().first().toIntDefault();
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
          int n = ast.arg2().toIntDefault();
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

    // private static IExpr randomApfloat(IExpr arg1, int workingPrecision, EvalEngine engine) {
    // if (arg1.isList2()) {
    // IReal min = arg1.first().evalReal();
    // IReal max = arg1.second().evalReal();
    // if (min == null || max == null) {
    // return F.NIL;
    // }
    // if (min.isGE(max)) {
    // IReal temp = min;
    // min = max;
    // max = temp;
    // if (min == max) {
    // return min;
    // }
    // }
    //
    // long oldPrecision = engine.getNumericPrecision();
    // try {
    // FixedPrecisionApfloatHelper apfloat = engine.setNumericPrecision(workingPrecision);
    // Apfloat random = apfloat.random();
    // return F.num(random);
    // } finally {
    // engine.setNumericPrecision(oldPrecision);
    // }
    //
    // Random tlr = engine.getRandom();
    // return F.num(boundedNextDouble(tlr, min, max));
    // } else {
    // boolean isNegative = false;
    // IReal max = arg1.evalReal();
    // if (max.isNegative()) {
    // isNegative = true;
    // max = max.abs();
    // }
    // if (max.isZero()) {
    // return F.CD0;
    // }
    // Random tlr = engine.getRandom();
    // double nextDouble = boundedNextDouble(tlr, max);
    // if (isNegative) {
    // nextDouble *= -1;
    // }
    // return F.num(nextDouble);
    // }
    // }

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
            array[i] *= 0.0;
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
   * RandomSample(&lt;function&gt;)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a random sample for the arguments of the <code>function</code>.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample(&lt;function&gt;, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a random sample of <code>n</code> elements for the arguments of the <code>function
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RandomSample(f(1,2,3,4,5))
   * f(3,4,5,1,2)
   *
   * &gt;&gt; RandomSample(f(1,2,3,4,5),3)
   * f(3,4,1)
   * </pre>
   */
  private static final class RandomSample extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        int n = ast.isAST2() ? ast.arg2().toIntDefault() : Integer.MAX_VALUE;
        if (n >= 0) {
          return shuffle((IAST) ast.arg1(), n);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    public static IAST shuffle(IAST list, int n) {
      final int len = list.argSize();

      // Shuffle indices.
      final int[] indexList = MathArrays.natural(len);
      MathArrays.shuffle(indexList);

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
      Random random = engine.getRandom();
      random.setSeed(seedValue);
      return F.ZZ(seedValue);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private RandomFunctions() {}
}
