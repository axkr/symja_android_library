package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import org.hipparchus.complex.Complex;
import org.hipparchus.random.RandomDataGenerator;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.StatisticsFunctions.IRandomVariate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Tensors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

public final class RandomFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.RandomInteger.setEvaluator(new RandomInteger());
      S.RandomPrime.setEvaluator(new RandomPrime());
      S.RandomChoice.setEvaluator(new RandomChoice());
      S.RandomComplex.setEvaluator(new RandomComplex());
      S.RandomReal.setEvaluator(new RandomReal());
      S.RandomSample.setEvaluator(new RandomSample());
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
   * <p>chooses a random <code>arg</code> from the list.
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
     * Sampler for enumerated distributions.
     *
     * @param <T> type of sample space objects
     */
    private final class EnumeratedDistributionSampler {
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
       * <p>For 0 &lt; i &lt; weights.length, the probability that i is selected (on any draw) is
       * weights[i]. If necessary, the weights array is normalized to sum to 1 so that weights[i] is
       * a probability and the array sums to 1.
       *
       * <p>Weights can be 0, but must not be negative, infinite or NaN. At least one weight must be
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
      try {
        if (arg1.isRuleAST()
            && arg1.first().isList()
            && arg1.second().isList()
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
                return Tensors.build(
                    () -> {
                      return items.get(sampler.sample(1)[0] + 1);
                    },
                    dimension);
              }
              int n = arg2.toIntDefault(Integer.MIN_VALUE);
              if (n > 0) {
                IASTAppendable result = F.ListAlloc(n);
                int[] chosen = sampler.sample(n);
                for (int i = 0; i < n; i++) {
                  result.append(items.get(chosen[i] + 1));
                }
                return result;
              }
            }
          }
        } else if (arg1.isList()) {
          IAST list = (IAST) arg1;
          ThreadLocalRandom random = ThreadLocalRandom.current();
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
              return Tensors.build(
                  () -> {
                    randomValue[0] = random.nextInt(listSize);
                    return list.get(randomValue[0] + 1);
                  },
                  dimension);
            }
            int n = arg2.toIntDefault(Integer.MIN_VALUE);
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
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
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
          ThreadLocalRandom tlr = ThreadLocalRandom.current();
          double re = tlr.nextDouble();
          double im = tlr.nextDouble();
          return F.complexNum(re, im);
        } else if (ast.isAST1()) {
          if (ast.arg1().isAST(S.List, 3)) {
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
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            return F.complexNum(tlr.nextDouble(minRe, maxRe), tlr.nextDouble(minIm, maxIm));
          } else {
            Complex max = engine.evalComplex(ast.arg1());
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            return F.complexNum(tlr.nextDouble(max.getReal()), tlr.nextDouble(max.getImaginary()));
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
              arr[i] = F.List(list.get(i));
            }
            return F.ast(arr, S.Table);
          }
        }
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      } catch (RuntimeException rex) {
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
   * <p>create a random integer number between <code>0</code> and <code>n</code>.
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
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        return randomBigInteger(BigInteger.ONE, false, tlr);
      }
      try {
        if (ast.arg1().isAST(S.List, 3)) {
          int min = ast.arg1().first().toIntDefault();
          int max = ast.arg1().second().toIntDefault();
          if (min != Integer.MIN_VALUE && max != Integer.MIN_VALUE) {
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
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            if (ast.isAST2()) {
              IExpr arg2 = ast.arg2();
              if (arg2.isList()) {
                // n1 x n2 x n3 ... array
                int[] dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
                if (dimension == null) {
                  return F.NIL;
                }
                final int min2 = min;
                final int max2 = max;
                return Tensors.build(() -> F.ZZ(tlr.nextInt((max2 - min2) + 1) + min2), dimension);
              }
              int size = arg2.toIntDefault(Integer.MIN_VALUE);
              if (size >= 0) {
                IASTAppendable list = F.ListAlloc(size);
                for (int i = 0; i < size; i++) {
                  list.append(F.ZZ(tlr.nextInt((max - min) + 1) + min));
                }
                return list;
              }
              return F.NIL;
            }
            return F.ZZ(tlr.nextInt((max - min) + 1) + min);
          }
          return F.NIL;
        }
        if (ast.arg1().isInteger()) {
          // RandomInteger(100) gives an integer between 0 and 100
          ThreadLocalRandom tlr = ThreadLocalRandom.current();
          BigInteger upperLimit = ((IInteger) ast.arg1()).toBigNumerator();
          boolean negative = false;
          if (upperLimit.compareTo(BigInteger.ZERO) < 0) {
            upperLimit = upperLimit.negate();
            negative = true;
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
            int size = arg2.toIntDefault(Integer.MIN_VALUE);
            if (size >= 0) {
              IASTAppendable list = F.ListAlloc(size);
              for (int i = 0; i < size; i++) {
                list.append(randomBigInteger(upperLimit, negative, tlr));
              }
              return list;
            }

          } else {
            return randomBigInteger(upperLimit, negative, tlr);
          }
        }
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      }
      return F.NIL;
    }

    private IExpr randomBigInteger(BigInteger upperLimit, boolean negative, ThreadLocalRandom tlr) {
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

  private static final class RandomPrime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        try {
          // RandomPrime(100) gives a prime integer between 2 and 100
          BigInteger upperLimit = ((IInteger) ast.arg1()).toBigNumerator();
          if (upperLimit.compareTo(BigInteger.ZERO) < 0) {
            // Positive integer expected.
            return IOFunctions.printMessage(ast.topHead(), "intp", F.List(), engine);
          }
          if (upperLimit.compareTo(BigInteger.valueOf(2)) < 0) {
            // There are no primes in the specified interval.
            return IOFunctions.printMessage(ast.topHead(), "noprime", F.List(), engine);
          }
          final int nlen = upperLimit.bitLength();
          ThreadLocalRandom tlr = ThreadLocalRandom.current();
          BigInteger randomNumber;
          do {
            randomNumber = new BigInteger(nlen, 32, tlr);
          } while (randomNumber.compareTo(upperLimit) > 0);
          return F.ZZ(randomNumber);
        } catch (ValidateException ve) {
          return engine.printMessage(ast.topHead(), ve);
        } catch (RuntimeException rex) {
          if (FEConfig.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
          // There are no primes in the specified interval.
          return IOFunctions.printMessage(ast.topHead(), "noprime", F.List(), engine);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
   * <p>create a random number between <code>0.0</code> and <code>1.0</code>.
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
  private static final class RandomReal extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST0()) {
          // RandomReal() gives a double value between 0.0 and 1.0
          ThreadLocalRandom tlr = ThreadLocalRandom.current();
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
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
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

        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        return F.num(tlr.nextDouble(min, max));
      } else {
        boolean isNegative = false;
        double max = engine.evalDouble(arg1);
        if (max < 0) {
          isNegative = true;
          max = FastMath.abs(max);
        }
        if (F.isZero(max)) {
          return F.CD0;
        }
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        double nextDouble = tlr.nextDouble(max);
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
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        for (int i = 0; i < array.length; i++) {
          array[i] = tlr.nextDouble(min, max);
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
          max = FastMath.abs(max);
        }
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        for (int i = 0; i < array.length; i++) {
          if (F.isZero(max)) {
            array[i] *= 0.0;
            continue;
          }
          array[i] = tlr.nextDouble(max);
          if (isNegative) {
            array[i] *= -1.0;
          }
        }
      }
      return new ASTRealVector(array, false);
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
   * <p>create a random sample for the arguments of the <code>function</code>.
   *
   * </blockquote>
   *
   * <pre>
   * RandomSample(&lt;function&gt;, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>create a random sample of <code>n</code> elements for the arguments of the <code>function
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
        int n = ast.isAST2() ? ast.arg2().toIntDefault(Integer.MIN_VALUE) : Integer.MAX_VALUE;
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
        IASTAppendable result = list.copyHead();
        for (int j = 0; j < n; j++) {
          result.append(list.get(indexList[j] + 1));
        }
        return result;
      }
      // Create shuffled list.
      return list.copy().setArgs(1, len + 1, i -> list.get(indexList[i - 1] + 1));
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private RandomFunctions() {}
}
