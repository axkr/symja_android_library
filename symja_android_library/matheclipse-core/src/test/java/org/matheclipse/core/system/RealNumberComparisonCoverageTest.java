package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.apfloat.Apfloat;
import org.apfloat.Aprational;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumStr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;

/** Named data-driven cases with a BigInteger oracle independent of the production comparator. */
@Execution(ExecutionMode.SAME_THREAD)
public class RealNumberComparisonCoverageTest extends ExprEvaluatorTestCase {
  private enum Kind { NEGATIVE_INFINITY, FINITE, POSITIVE_INFINITY, NAN }

  /** Exact test arithmetic: cross multiplication supplies the expected order and arithmetic. */
  private static final class Exact {
    final BigInteger numerator;
    final BigInteger denominator;

    Exact(BigInteger numerator, BigInteger denominator) {
      if (denominator.signum() <= 0) {
        throw new IllegalArgumentException("The test oracle requires a positive denominator");
      }
      BigInteger gcd = numerator.gcd(denominator);
      this.numerator = numerator.divide(gcd);
      this.denominator = denominator.divide(gcd);
    }

    static Exact decimal(String text) {
      return decimal(new BigDecimal(text));
    }

    static Exact decimal(BigDecimal value) {
      return value.scale() < 0
          ? new Exact(value.unscaledValue().multiply(BigInteger.TEN.pow(-value.scale())), BigInteger.ONE)
          : new Exact(value.unscaledValue(), BigInteger.TEN.pow(value.scale()));
    }

    Exact add(Exact other) {
      return new Exact(numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
          denominator.multiply(other.denominator));
    }

    Exact multiply(Exact other) {
      return new Exact(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
    }

    Exact distance(Exact other) {
      return new Exact(numerator.multiply(other.denominator)
          .subtract(other.numerator.multiply(denominator)).abs(), denominator.multiply(other.denominator));
    }

    int compare(Exact other) {
      return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
    }
  }

  private static final class Operand {
    final String name;
    final Supplier<IReal> create;
    final Exact exact;
    final Kind kind;

    Operand(String name, Supplier<IReal> create, Exact exact, Kind kind) {
      this.name = name;
      this.create = create;
      this.exact = exact;
      this.kind = kind;
    }

    int order(Operand other) {
      return kind == Kind.FINITE && other.kind == Kind.FINITE
          ? exact.compare(other.exact) : kind.compareTo(other.kind);
    }
  }

  private static Operand integer(String value) {
    BigInteger number = new BigInteger(value);
    return new Operand("integer " + shortLabel(value), () -> F.ZZ(number),
        new Exact(number, BigInteger.ONE), Kind.FINITE);
  }

  private static Operand fraction(String name, BigInteger numerator, BigInteger denominator) {
    return new Operand("fraction " + name, () -> F.QQ(numerator, denominator),
        new Exact(numerator, denominator), Kind.FINITE);
  }

  private static Operand machine(double value) {
    // BigDecimal(double) captures the exact binary value; no Symja conversion builds the oracle.
    return new Operand("machine " + value, () -> Num.valueOf(value),
        Exact.decimal(new BigDecimal(value)), Kind.FINITE);
  }

  private static Operand apfloat(String value) {
    return new Operand("apfloat " + value, () -> ApfloatNum.valueOf(value, 80),
        Exact.decimal(value), Kind.FINITE);
  }

  private static String shortLabel(String value) {
    return value.length() < 40 ? value : value.substring(0, 12) + "... (" + value.length() + " digits)";
  }

  private static List<Operand> operands() {
    List<Operand> values = new ArrayList<>();
    for (String value : new String[] {"-2", "0", "1", "2", "-9007199254740993",
        "9007199254740993", BigInteger.TEN.pow(400).toString(), BigInteger.TEN.pow(400).negate().toString()}) {
      values.add(integer(value));
    }
    for (int[] pair : new int[][] {{-3, 2}, {1, 3}, {1, 2}, {3, 2}}) {
      values.add(fraction(pair[0] + "/" + pair[1], BigInteger.valueOf(pair[0]), BigInteger.valueOf(pair[1])));
    }
    values.add(fraction("(10^400+1)/3", BigInteger.TEN.pow(400).add(BigInteger.ONE), BigInteger.valueOf(3)));
    values.add(fraction("1/(10^400+1)", BigInteger.ONE, BigInteger.TEN.pow(400).add(BigInteger.ONE)));
    values.add(new Operand("machine -0.0 (sign retained)",
        () -> new NumStr("-0.0").numericValue(), Exact.decimal("0"), Kind.FINITE));
    for (double value : new double[] {0.0, -0.1, 0.1, Double.MIN_VALUE, Double.MIN_NORMAL,
        Double.MAX_VALUE, -Double.MAX_VALUE}) {
      values.add(machine(value));
    }
    for (String value : new String[] {"-1e400", "1e400", "-1e-400", "1e-400",
        "0.999999999999999999999999999999", "1.000000000000000000000000000001", "0", "0.5"}) {
      values.add(apfloat(value));
    }
    values.add(new Operand("-Infinity", () -> Num.valueOf(Double.NEGATIVE_INFINITY), null, Kind.NEGATIVE_INFINITY));
    values.add(new Operand("+Infinity", () -> Num.valueOf(Double.POSITIVE_INFINITY), null, Kind.POSITIVE_INFINITY));
    values.add(new Operand("NaN", () -> Num.valueOf(Double.NaN), null, Kind.NAN));
    return values;
  }

  private static void checkDirection(IReal left, IReal right, int expected, boolean unordered) {
    assertAll("comparison and all relational predicates",
        () -> assertEquals(expected, Integer.signum(left.compareTo(right)), "compareTo"),
        () -> assertEquals(!unordered && expected < 0, left.isLT(right), "isLT"),
        () -> assertEquals(!unordered && expected > 0, left.isGT(right), "isGT"),
        () -> assertEquals(!unordered && expected <= 0, left.isLE(right), "isLE"),
        () -> assertEquals(!unordered && expected >= 0, left.isGE(right), "isGE"));
  }

  @TestFactory
  public List<DynamicTest> everyPairOfNumericRepresentations() {
    List<DynamicTest> tests = new ArrayList<>();
    List<Operand> values = operands();
    int[] precisions = {16, 34, 80, 160};
    for (int i = 0; i < values.size(); i++) {
      for (int j = i; j < values.size(); j++) {
        Operand left = values.get(i);
        Operand right = values.get(j);
        int precision = precisions[(i + j) % precisions.length];
        String name = left.name + " vs " + right.name + "; engine precision=" + precision;
        tests.add(dynamicTest(name, () -> {
          evaluator.getEvalEngine().setNumericPrecision(precision);
          IReal a = left.create.get();
          IReal b = right.create.get();
          int expected = Integer.signum(left.order(right));
          boolean unordered = left.kind == Kind.NAN || right.kind == Kind.NAN;
          assertAll(name, () -> checkDirection(a, b, expected, unordered),
              () -> checkDirection(b, a, -expected, unordered));
        }));
      }
    }
    return tests;
  }

  /** Read actual stored digits without invoking exactComparisonValue or the comparator oracle. */
  private static Exact stored(IReal value) {
    if (value instanceof IRational) {
      IRational rational = (IRational) value;
      return new Exact(rational.toBigNumerator(), rational.toBigDenominator());
    }
    if (value instanceof ApfloatNum) {
      return Exact.decimal(value.apfloatValue().toString());
    }
    return Exact.decimal(new BigDecimal(value.doubleValue()));
  }

  private static void assertRoundedValue(Exact expected, IReal actual, int precision) {
    assertInstanceOf(ApfloatNum.class, actual);
    Exact observed = stored(actual);
    BigDecimal exactDecimal = null;
    try {
      exactDecimal = new BigDecimal(expected.numerator).divide(new BigDecimal(expected.denominator));
    } catch (ArithmeticException repeatingDecimal) {
      // A non-terminating rational requires a precision-dependent error bound below.
    }
    if (exactDecimal != null && exactDecimal.stripTrailingZeros().precision() <= precision) {
      assertEquals(0, observed.compare(expected), "Exactly representable result changed");
      return;
    }
    BigDecimal magnitude = new BigDecimal(expected.numerator.abs())
        .divide(new BigDecimal(expected.denominator), new MathContext(1, RoundingMode.DOWN));
    Exact ulp = Exact.decimal(BigDecimal.ONE.scaleByPowerOfTen(magnitude.precision() - magnitude.scale() - precision));
    assertTrue(observed.distance(expected).compare(ulp) <= 0,
        () -> "Error exceeds one ULP at " + precision + " digits; actual=" + actual);
  }

  @TestFactory
  public List<DynamicTest> arithmeticOverloadsAcrossPrecisions() {
    List<Operand> rightOperands = new ArrayList<>();
    for (String value : new String[] {"-2", "0", "1", "9007199254740993"}) {
      rightOperands.add(integer(value));
    }
    rightOperands.add(fraction("1/3", BigInteger.ONE, BigInteger.valueOf(3)));
    rightOperands.add(fraction("1/8", BigInteger.ONE, BigInteger.valueOf(8)));
    rightOperands.add(machine(1.5));
    rightOperands.add(machine(-0.125));
    for (String value : new String[] {"1e400", "-1e400", "1e-400", "-1e-400"}) {
      rightOperands.add(apfloat(value));
    }
    List<DynamicTest> tests = new ArrayList<>();
    for (int precision : new int[] {16, 34, 80, 160}) {
      for (String leftText : new String[] {"0", "1", "-2", "0.125"}) {
        for (Operand right : rightOperands) {
          for (boolean addition : new boolean[] {true, false}) {
            String name = (addition ? "add" : "multiply") + " precision=" + precision
                + "; " + leftText + ", " + right.name;
            tests.add(dynamicTest(name, () -> {
              evaluator.getEvalEngine().setNumericPrecision(precision);
              ApfloatNum left = ApfloatNum.valueOf(leftText, precision);
              IReal operand = right.create.get();
              Exact expected = addition ? Exact.decimal(leftText).add(right.exact)
                  : Exact.decimal(leftText).multiply(right.exact);
              IReal actual = addition ? left.add(operand) : left.multiply(operand);
              assertRoundedValue(expected, actual, precision);
            }));
          }
        }
      }
    }
    return tests;
  }

  @TestFactory
  public List<DynamicTest> exactComparisonRepresentationInEverySupportedRadix() {
    List<DynamicTest> tests = new ArrayList<>();
    for (int radix = 2; radix <= 36; radix++) {
      final int base = radix;
      BigInteger b = BigInteger.valueOf(base);
      String[] inputs = {"0", "0.1", "10.01", "-100"};
      Exact[] expected = {Exact.decimal("0"), new Exact(BigInteger.ONE, b),
          new Exact(b.pow(3).add(BigInteger.ONE), b.pow(2)), new Exact(b.pow(2).negate(), BigInteger.ONE)};
      for (int i = 0; i < inputs.length; i++) {
        String input = inputs[i];
        Exact oracle = expected[i];
        tests.add(dynamicTest("radix=" + base + "; stored=" + input, () -> {
          evaluator.getEvalEngine().setNumericPrecision(16);
          ApfloatNum number = ApfloatNum.valueOf(new Apfloat(input, 80, base));
          long originalPrecision = number.precision();
          Apfloat representation = number.exactComparisonValue();
          assertEquals(10, representation.radix());
          Exact actual = representation instanceof Aprational
              ? new Exact(((Aprational) representation).numerator().toBigInteger(),
                  ((Aprational) representation).denominator().toBigInteger())
              : Exact.decimal(representation.toString());
          assertEquals(0, actual.compare(oracle));
          IReal rational = F.QQ(oracle.numerator, oracle.denominator);
          checkDirection(number, rational, 0, false);
          checkDirection(rational, number, 0, false);
          assertEquals(originalPrecision, number.precision(), "Comparison changed source precision");
        }));
      }
    }
    return tests;
  }

  @TestFactory
  public List<DynamicTest> evaluatorOrderingAcrossExponentAndPrecisionBoundaries() {
    List<DynamicTest> tests = new ArrayList<>();
    for (int precision : new int[] {30, 50, 80}) {
      for (int exponent : new int[] {-400, -324, -308, -10, 0, 15, 309, 400}) {
        String approximate = "N[10^(" + exponent + ")," + precision + "]";
        String exact = "10^(" + exponent + ")+10^(" + (exponent - precision / 2) + ")";
        Exact smaller = Exact.decimal(BigDecimal.ONE.scaleByPowerOfTen(exponent));
        Exact larger = smaller.add(Exact.decimal(BigDecimal.ONE.scaleByPowerOfTen(exponent - precision / 2)));
        for (String operation : new String[] {"Less", "Greater", "Min", "Max"}) {
          boolean reversed = operation.equals("Greater") || operation.equals("Max");
          String input = operation + "[" + (reversed ? exact + "," + approximate : approximate + "," + exact) + "]";
          tests.add(dynamicTest(input, () -> {
            evaluator.getEvalEngine().setNumericPrecision(16);
            if (operation.equals("Less") || operation.equals("Greater")) {
              assertEquals(S.True, evaluator.eval(input));
            } else {
              IReal actual = assertInstanceOf(IReal.class, evaluator.eval(input));
              assertEquals(0, stored(actual).compare(operation.equals("Min") ? smaller : larger));
            }
          }));
        }
      }
    }
    return tests;
  }

  @TestFactory
  public List<DynamicTest> sortingMixedRepresentationsIsConsistent() {
    List<DynamicTest> tests = new ArrayList<>();
    for (int seed = 0; seed < 12; seed++) {
      final int shuffleSeed = seed;
      tests.add(dynamicTest("mixed total order; permutation=" + seed, () -> {
        evaluator.getEvalEngine().setNumericPrecision(16);
        // More than 32 elements exercises TimSort merges, with repeated equivalent representations.
        List<Operand> inputs = new ArrayList<>();
        for (int repetition = 0; repetition < 3; repetition++) {
          inputs.addAll(operands());
        }
        Collections.shuffle(inputs, new Random(shuffleSeed));
        List<IReal> numbers = new ArrayList<>();
        for (Operand input : inputs) {
          numbers.add(input.create.get());
        }
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
          order.add(i);
        }
        order.sort((a, b) -> numbers.get(a).compareTo(numbers.get(b)));
        for (int i = 1; i < order.size(); i++) {
          Operand previous = inputs.get(order.get(i - 1));
          Operand next = inputs.get(order.get(i));
          assertTrue(previous.order(next) <= 0, () -> previous.name + " must precede " + next.name);
        }
      }));
    }
    return tests;
  }
}
