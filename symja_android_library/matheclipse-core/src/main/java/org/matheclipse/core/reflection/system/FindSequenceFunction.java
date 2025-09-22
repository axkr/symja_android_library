package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;


public class FindSequenceFunction extends AbstractEvaluator {

  private final static int[] BELLB15 = //
      {1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570, 4213597, 27644437, 190899322,
          1382958545};

  private final static int[] CARMICALLAMBDA100 = //
      {1, 1, 2, 2, 4, 2, 6, 2, 6, 4, 10, 2, 12, 6, 4, 4, 16, 6, 18, 4, 6, 10, 22, 2, 20, 12, 18, 6,
          28, 4, 30, 8, 10, 16, 12, 6, 36, 18, 12, 4, 40, 6, 42, 10, 12, 22, 46, 4, 42, 20, 16, 12,
          52, 18, 20, 6, 18, 28, 58, 4, 60, 30, 6, 16, 12, 10, 66, 16, 22, 12, 70, 6, 72, 36, 20,
          18, 30, 12, 78, 4, 54, 40, 82, 6, 16, 42, 28, 10, 88, 12, 12, 22, 30, 46, 36, 8, 96, 42,
          30, 20};

  private final static int[] CATALAN_NUMBER19 = //
      {1, 2, 5, 14, 42, 132, 429, 1430, 4862, 16796, 58786, 208012, 742900, 2674440, 9694845,
          35357670, 129644790, 477638700, 1767263190};

  private final static int[] EULERPHI100 = //
      {1, 1, 2, 2, 4, 2, 6, 4, 6, 4, 10, 4, 12, 6, 8, 8, 16, 6, 18, 8, 12, 10, 22, 8, 20, 12, 18,
          12, 28, 8, 30, 16, 20, 16, 24, 12, 36, 18, 24, 16, 40, 12, 42, 20, 24, 22, 46, 16, 42, 20,
          32, 24, 52, 18, 40, 24, 36, 28, 58, 16, 60, 30, 36, 32, 48, 20, 66, 32, 44, 24, 70, 24,
          72, 36, 40, 36, 60, 24, 78, 32, 54, 40, 82, 24, 64, 42, 56, 40, 88, 24, 72, 44, 60, 46,
          72, 32, 96, 42, 60, 40};

  private final static int[] FACTORIAL12 = //
      {1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600};

  private final static int[] FACTORIAL219 = //
      {1, 2, 3, 8, 15, 48, 105, 384, 945, 3840, 10395, 46080, 135135, 645120, 2027025, 10321920,
          34459425, 185794560, 654729075};

  private final static int[] HYPERFACTORIAL5 = //
      {1, 4, 108, 27648, 86400000};

  private final static int[] SUBFACTORIAL12 = //
      {0, 1, 2, 9, 44, 265, 1854, 14833, 133496, 1334961, 14684570, 176214841};

  private final static int[] FIBONACCI40 = //
      {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946,
          17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309,
          3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155};

  private final static int[] LUCASL40 = //
      {1, 3, 4, 7, 11, 18, 29, 47, 76, 123, 199, 322, 521, 843, 1364, 2207, 3571, 5778, 9349, 15127,
          24476, 39603, 64079, 103682, 167761, 271443, 439204, 710647, 1149851, 1860498, 3010349,
          4870847, 7881196, 12752043, 20633239, 33385282, 54018521, 87403803, 141422324, 228826127};

  private final static int[] PARTITIONSP40 = //
      {1, 2, 3, 5, 7, 11, 15, 22, 30, 42, 56, 77, 101, 135, 176, 231, 297, 385, 490, 627, 792, 1002,
          1255, 1575, 1958, 2436, 3010, 3718, 4565, 5604, 6842, 8349, 10143, 12310, 14883, 17977,
          21637, 26015, 31185, 37338};

  private final static int[] PARTITIONSQ40 = //
      {1, 1, 2, 2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 22, 27, 32, 38, 46, 54, 64, 76, 89, 104, 122, 142,
          165, 192, 222, 256, 296, 340, 390, 448, 512, 585, 668, 760, 864, 982, 1113};

  private final static int[] POLYGONAL_NUMBER40 = //
      {1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 231, 253,
          276, 300, 325, 351, 378, 406, 435, 465, 496, 528, 561, 595, 630, 666, 703, 741, 780, 820};

  private final static int[] PRIME40 = //
      {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
          97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173};

  private final static int[] PRIMEPI100 = //
      {0, 1, 2, 2, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 10, 10,
          11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 13, 13, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15,
          16, 16, 16, 16, 16, 16, 17, 17, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19, 20, 20, 21, 21,
          21, 21, 21, 21, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24,
          25, 25, 25, 25};

  private static IExpr compareSequence(IExpr intFunction, int[] startSequence,
      IInteger[] inputSequence, IInteger factor, IInteger addend, IExpr variable,
      EvalEngine engine) {
    if (inputSequence.length <= startSequence.length) {
      for (int i = 0; i < inputSequence.length; i++) {
        if (!inputSequence[i].equalsInt(startSequence[i])) {
          return F.NIL;
        }
      }
    }
    if (inputSequence.length > startSequence.length) {
      for (int i = startSequence.length; i < inputSequence.length; i++) {
        IAST function = F.unaryAST1(F.Function(intFunction), F.ZZ(i));
        IExpr bigValue = engine.evaluate(function);
        if (!inputSequence[i - 1].equals(bigValue)) {
          return F.NIL;
        }
      }
    }

    if (!addend.isZero()) {
      IExpr plus = F.Plus(addend, intFunction);
      IAST function = F.Function(plus);
      return createFunction(function, variable, engine);
    }
    IExpr times = factor.isOne() ? intFunction : F.Times(factor, intFunction);
    IAST function = F.Function(times);
    return createFunction(function, variable, engine);
  }

  private static IExpr createFunction(IAST function, IExpr variable, EvalEngine engine) {
    if (variable.isPresent()) {
      return engine.evaluate(F.unaryAST1(function, variable));
    }
    return function;
  }

  private static IExpr findIntegerFunction(IInteger[] sequence, IInteger startValue, IExpr variable,
      EvalEngine engine) {
    IInteger factor = F.C1;
    IInteger addend = F.C0;
    if (!sequence[0].equals(startValue)) {
      boolean hasFactor = false;
      IInteger[] newSequence = null;
      if (!startValue.isZero() && sequence[0].mod(startValue).equals(F.C0)) {
        factor = sequence[0].divideBy(startValue).numerator();
        if (!factor.isZero()) {
          hasFactor = true;
          newSequence = new IInteger[sequence.length];
          for (int i = 0; i < sequence.length; i++) {
            IRational divideBy = sequence[i].divideBy(factor);
            if (!divideBy.denominator().isOne()) {
              hasFactor = false;
              factor = F.C1;
              break;
            }
            newSequence[i] = divideBy.numerator();
          }
        }
      }
      if (!hasFactor) {
        addend = sequence[0].subtract(startValue);
        newSequence = new IInteger[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
          IInteger d = sequence[i].subtract(addend);
          newSequence[i] = d;
        }
      }
      sequence = newSequence;
    }
    if (startValue.isZero()) {
      return findIntegerFunction0(sequence, variable, factor, addend, engine);
    }
    if (startValue.isOne()) {
      return findIntegerFunction1(sequence, variable, factor, addend, engine);
    }
    if (startValue.isNumEqualInteger(F.C2)) {
      return findIntegerFunction2(sequence, variable, factor, addend, engine);
    }
    return F.NIL;
  }

  /**
   * Tries to find a function for the given integer sequence assuming the sequence starts at n=0.
   * 
   * @param sequenceq
   * @param variable
   * @param factor
   * @param addend
   * @param engine
   * @return
   */
  private static IExpr findIntegerFunction0(IInteger[] sequence, IExpr variable, IInteger factor,
      IInteger addend, EvalEngine engine) {
    IExpr result =
        compareSequence(F.PrimePi(F.Slot1), PRIMEPI100, sequence, factor, addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.Subfactorial(F.Slot1), SUBFACTORIAL12, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Tries to find a function for the given integer sequence assuming the sequence starts at n=1.
   * 
   * @param sequence
   * @param variable
   * @param factor
   * @param addend
   * @param engine
   * @return
   */
  private static IExpr findIntegerFunction1(IInteger[] sequence, IExpr variable, IInteger factor,
      IInteger addend, EvalEngine engine) {
    IExpr result = compareSequence(F.Factorial(F.Slot1), FACTORIAL12, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }
    result = compareSequence(F.Factorial2(F.Slot1), FACTORIAL219, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }
    result = compareSequence(F.Fibonacci(F.Slot1), FIBONACCI40, sequence, factor, addend, variable,
        engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.CatalanNumber(F.Slot1), CATALAN_NUMBER19, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.PolygonalNumber(F.Slot1), POLYGONAL_NUMBER40, sequence, factor,
        addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result =
        compareSequence(F.LucasL(F.Slot1), LUCASL40, sequence, factor, addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.BellB(F.Slot1), BELLB15, sequence, factor, addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.PartitionsP(F.Slot1), PARTITIONSP40, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.PartitionsQ(F.Slot1), PARTITIONSQ40, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.EulerPhi(F.Slot1), EULERPHI100, sequence, factor, addend, variable,
        engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.CarmichaelLambda(F.Slot1), CARMICALLAMBDA100, sequence, factor,
        addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = compareSequence(F.Hyperfactorial(F.Slot1), HYPERFACTORIAL5, sequence, factor, addend,
        variable, engine);
    if (result.isPresent()) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Tries to find a function for the given integer sequence assuming the sequence starts at n=2.
   * 
   * @param sequence
   * @param variable
   * @param factor
   * @param addend
   * @param engine
   * @return
   */
  private static IExpr findIntegerFunction2(IInteger[] sequence, IExpr variable, IInteger factor,
      IInteger addend, EvalEngine engine) {
    IExpr result =
        compareSequence(F.Prime(F.Slot1), PRIME40, sequence, factor, addend, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    return F.NIL;
  }

  /**
   * Tries to find a polynomial function for the given sequence. Works by taking successive
   * differences. If the d-th difference is constant, then the sequence is a polynomial of degree d.
   *
   * Returns a polynomial representation of the function, or {@link F#NIL} if not a simple
   * polynomial.
   *
   * Limitations: Assumes the sequence starts at n=0 or n=1 and provides enough terms. For example,
   * if a_n is evaluated at n, you need a_0, a_1, a_2, ..., a_d to uniquely determine a polynomial
   * of degree d.
   */
  private static IExpr findPolynomialFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    int n = sequence.length;
    if (n == 0) {
      return F.NIL;
    }
    if (n == 1) {
      return sequence[0];
    }

    IASTAppendable diffs = F.ListAlloc(n);
    diffs.append(F.List(sequence));

    int degree = 0;
    while (diffs.get(degree + 1).size() > 1 && degree < n - 1) {
      IAST currentDiffs = getDifferences((IAST) diffs.get(degree + 1));
      diffs.append(currentDiffs);
      if (isConstant(currentDiffs)) {
        degree = diffs.size() - 1; // Degree is found
        break;
      }
      degree++;
    }

    if (!isConstant((IAST) diffs.get(degree)) || diffs.get(degree).isEmpty()) {
      // Not a simple polynomial or not enough data to determine
      return F.NIL;
    }

    // Simple approach for integer sequences by using finite differences
    // A_n = sum_{k=1}^d (Binomial(n-1,k-1) * diffs[k][1]) where diffs[k][1] is the k-th difference
    // at the first term.

    IASTAppendable result = F.PlusAlloc(degree + 1);
    for (int k = 1; k < degree + 1; k++) {
      IInteger firstTermOfDiff = (IInteger) ((IAST) diffs.get(k)).get(1);
      if (firstTermOfDiff.isZero()) {
        continue;
      }

      if (k == 1) {
        result.append(firstTermOfDiff);
      } else {
        IAST binomialPolynomial = NumberTheory.binomialPolynomial(F.Plus(F.CN1, variable), k - 1);
        result.append(F.Times(firstTermOfDiff, binomialPolynomial));
      }
    }

    return engine.evaluate(F.Factor(result.oneIdentity0()));
  }

  /**
   * Tries to find a function for the given integer sequence.
   * 
   * @param sequence the integer sequence
   * @param variable the variable to use in the function
   * @param engine the evaluation engine
   * @return
   */
  private static IExpr findSequenceFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    // Check for arithmetic sequence
    IInteger diff = sequence[1].subtract(sequence[0]);
    if (isArithmetic(sequence, diff)) {
      IInteger constant = sequence[0].subtract(diff);
      IExpr times = diff.isOne() ? F.Slot1 : F.Times(diff, F.Slot1);
      IExpr plus = constant.isZero() ? times : F.Plus(constant, times);
      IAST function = F.Function(plus);
      return createFunction(function, variable, engine);
    }

    if (!sequence[0].isZero()) {
      // Check for geometric sequence
      IRational ratio = sequence[1].divideBy(sequence[0]);
      if (!ratio.isOne()) {
        if (isGeometric(sequence, ratio)) {
          IRational constant = sequence[0].divideBy(ratio);
          IExpr power = F.Power(ratio, F.Slot1);
          IExpr times = constant.isOne() ? power : F.Times(constant, power);
          IAST function = F.Function(times);
          return createFunction(function, variable, engine);
        }
      }
    }

    IExpr result = findIntegerFunction(sequence, F.C0, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = findIntegerFunction(sequence, F.C1, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    result = findIntegerFunction(sequence, F.C2, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    if (!isLEOrdered(sequence)) {
      return F.NIL;
    }
    if (variable.isNIL()) {
      variable = F.Slot1;
    }
    result = findPolynomialFunction(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Computes the first-order differences of a sequence. e.g., [1, 3, 7, 13] -> [2, 4, 6]
   */
  private static IAST getDifferences(IAST sequence) {
    if (sequence.size() < 2) {
      return F.CEmptyList;
    }
    IASTAppendable differences = F.ListAlloc(sequence.size() - 1);
    for (int i = 1; i < sequence.size() - 1; i++) {
      differences.append(sequence.get(i + 1).subtract(sequence.get(i)));
    }
    return differences;
  }

  /** Checks if the sequence is arithmetic with the given difference. */
  private static boolean isArithmetic(IInteger[] sequence, IInteger diff) {
    for (int i = 2; i < sequence.length; i++) {
      if (!sequence[i - 1].add(diff).equals(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the sequence is constant.
   */
  public static boolean isConstant(IAST sequence) {
    if (sequence.isEmpty())
      return false;
    if (sequence.size() == 1)
      return true;
    IInteger first = (IInteger) sequence.get(1);
    for (int i = 2; i < sequence.size(); i++) {
      if (!sequence.get(i).equals(first)) {
        return false;
      }
    }
    return true;
  }

  /** Checks if the sequence is geometric with the given ratio. */
  private static boolean isGeometric(IInteger[] sequence, IRational ratio) {
    for (int i = 2; i < sequence.length; i++) {
      if (sequence[i - 1].isZero() || !sequence[i].divideBy(sequence[i - 1]).equals(ratio)) {
        return false;
      }
    }
    return true;
  }

  /** Checks if the sequence is less or equal ordered. */
  private static boolean isLEOrdered(IInteger[] sequence) {
    for (int i = 1; i < sequence.length; i++) {
      if (!sequence[i - 1].isLE(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  public FindSequenceFunction() {
    // default ctor
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList()) {
      IAST list = (IAST) ast.arg1();
      IExpr variable = F.NIL;
      if (ast.isAST2()) {
        variable = ast.arg2();
      }
      IInteger[][] sequences = Convert.toRationalArray(list);
      if (sequences != null) {
        if (sequences[1] == null) {
          if (sequences[0].length > 2) {
            return findSequenceFunction(sequences[0], variable, engine);
          }
        } else {
          if (sequences[0].length > 2 && sequences[1].length > 2) {
            IExpr numeratorFunction = findSequenceFunction(sequences[0], variable, engine);
            if (numeratorFunction.isPresent()) {
              IExpr denominatorFunction = findSequenceFunction(sequences[1], variable, engine);
              if (denominatorFunction.isPresent()) {
                return F.Divide(numeratorFunction, denominatorFunction);
              }
            }
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /** {@inheritDoc} */
  @Override
  public void setUp(final ISymbol newSymbol) {}


  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
