package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.apfloat.Apfloat;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumStr;
import org.matheclipse.core.expression.RealNumberComparison;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;

/** Regression coverage for comparisons and arithmetic that must not narrow Apfloat operands. */
public class RealNumberComparisonTest extends ExprEvaluatorTestCase {
  private static ApfloatNum ap(String value) {
    return ApfloatNum.valueOf(value, 80);
  }

  private static void assertOrder(IReal smaller, IReal larger) {
    // Check both directions: Orderless sorting and predicates use different entry points.
    assertAll(smaller + " < " + larger, () -> assertTrue(smaller.isLT(larger)),
        () -> assertFalse(smaller.isGT(larger)), () -> assertTrue(larger.isGT(smaller)),
        () -> assertFalse(larger.isLT(smaller)), () -> assertTrue(smaller.compareTo(larger) < 0),
        () -> assertTrue(larger.compareTo(smaller) > 0));
  }

  private static void assertSameValue(IReal a, IReal b) {
    assertAll(a + " same as " + b, () -> assertEquals(0, RealNumberComparison.compare(a, b)),
        () -> assertEquals(0, RealNumberComparison.compare(b, a)), () -> assertFalse(a.isLT(b)),
        () -> assertFalse(a.isGT(b)), () -> assertTrue(a.isLE(b)), () -> assertTrue(a.isGE(b)));
  }

  private static void assertValue(String expected, IReal actual) {
    // Compare the stored value independently of the Symja comparison implementation under test.
    ApfloatNum result = assertInstanceOf(ApfloatNum.class, actual);
    assertEquals(0, result.apfloatValue().compareTo(new Apfloat(expected, 80)));
  }

  @Test
  public void testMinAndMaxOutsideMachineRange() {
    INumber expectedMin = F.ZZ(BigInteger.TEN.pow(400));
    for (String input : new String[] {"Min[Pi^1000,10^400]", "Min[10^400,Pi^1000]"}) {
      assertEquals(expectedMin, evaluator.eval(input), input);
    }
    check("Max[Pi^1000,10^400]", "Pi^1000");
    check("Max[10^400,Pi^1000]", "Pi^1000");
  }

  @Test
  public void testEvaluatorPreservesSmallDifferencesAndExactOperands() {
    String[] comparisons = {"10^400 < Pi^1000", "Pi^1000 > 10^400", "N[1+10^-30,50] > 1",
        "1 < N[1+10^-30,50]", "N[1-10^-30,50] < 1", "1 > N[1-10^-30,50]", "0 < N[10^-400,50]",
        "N[10^-400,50] > 0", "N[-10^-400,50] < 0", "0 > N[-10^-400,50]",
        "N[9007199254740992,50] < 9007199254740993", "9007199254740993 > N[9007199254740992,50]"};
    for (String input : comparisons) {
      assertEquals(S.True, evaluator.eval(input), input);
    }
    // next to a machine number the exact operand is compared at machine precision, where both are
    // the same number: 0.1 <= 1/10 is True for the same reason
    for (String input : new String[] {"9007199254740993 > 9007199254740992.0",
        "9007199254740992.0 < 9007199254740993", "0.1 > 1/10"}) {
      assertEquals(S.False, evaluator.eval(input), input);
    }
  }

  @Test
  public void testMachineRootOutsideMachineRange() {
    // the root of an integer beyond the double range is an arbitrary precision number of machine
    // precision, not Infinity, which made N[(10^800+1)^(1/2)/10^400] Indeterminate
    IReal root = assertInstanceOf(ApfloatNum.class,
        ApfloatNum.intPowerFractionNumeric(
            F.Power(F.ZZ(BigInteger.valueOf(7).pow(900).add(BigInteger.ONE)), F.C1D2),
            evaluator.getEvalEngine()));
    // 16 digits: within a relative 10^-12 of 7^450
    BigInteger exact = BigInteger.valueOf(7).pow(450);
    BigInteger slack = exact.divide(BigInteger.TEN.pow(12));
    assertOrder(F.ZZ(exact.subtract(slack)), root);
    assertOrder(root, F.ZZ(exact.add(slack)));
  }

  @Test
  public void testSmallIntegerAndMachineComparisons() {
    assertOrder(ap("0.999999999999999999999999999999"), F.C1);
    assertOrder(F.C1, ap("1.000000000000000000000000000001"));
    // next to a machine number the other operand is taken at machine precision (the lower
    // precision governs), as long as it has a machine value
    assertSameValue(ap("0.999999999999999999999999999999"), Num.valueOf(1.0));
    assertSameValue(Num.valueOf(1.0), ap("1.000000000000000000000000000001"));
    assertOrder(ap("0.999"), Num.valueOf(1.0));
    assertOrder(F.C0, ap("1e-400"));
    assertOrder(ap("-1e-400"), Num.valueOf(0.0));
  }

  @Test
  public void testLargeIntegerAndFractionComparisons() {
    assertOrder(F.ZZ(BigInteger.TEN.pow(399)), ap("1e400"));
    assertOrder(ap("-1e400"), F.ZZ(BigInteger.TEN.pow(399).negate()));
    assertOrder(F.ZZ(new BigInteger("9007199254740992")), ap("9007199254740993"));
    assertOrder(ap("0.3333333333333333333333333333333333333333"), F.QQ(1, 3));
    assertOrder(F.QQ(1, 3), ap("0.3333333333333333333333333333333333333334"));
    BigInteger denominator = BigInteger.TEN.pow(60);
    IReal fraction = F.QQ(denominator.add(BigInteger.TWO), denominator);
    assertOrder(ap("1.000000000000000000000000000000000000000000000000000000000001"), fraction);
    assertOrder(F.QQ(BigInteger.TEN.pow(400), BigInteger.valueOf(3)), ap("1e400"));
  }

  @Test
  public void testComparisonDoesNotRoundExactFractionAtEnginePrecision() {
    evaluator.getEvalEngine().setNumericPrecision(16);
    // The decimal is strictly below 1/3 even though converting 1/3 at 16 digits loses that fact.
    assertOrder(ap("0.3333333333333333333333333333333333333333"), F.QQ(1, 3));
  }

  @Test
  public void testFiniteValuesStayBelowInfinityAndAboveNegativeInfinity() {
    IReal positiveInfinity = Num.valueOf(Double.POSITIVE_INFINITY);
    IReal negativeInfinity = Num.valueOf(Double.NEGATIVE_INFINITY);
    for (IReal finite : new IReal[] {ap("1e400"), ap("2e400"), ap("-1e400"),
        F.ZZ(BigInteger.TEN.pow(400)), F.QQ(BigInteger.TEN.pow(400), BigInteger.valueOf(3))}) {
      assertOrder(negativeInfinity, finite);
      assertOrder(finite, positiveInfinity);
    }
    assertOrder(ap("1e400"), ap("2e400"));
  }

  @Test
  public void testNaNIsUnorderedForPredicatesAndLastForSorting() {
    IReal nan = Num.valueOf(Double.NaN);
    for (IReal real : new IReal[] {ap("-1e400"), F.C0, ap("1e400"), F.ZZ(BigInteger.TEN.pow(400)),
        F.QQ(1, 3)}) {
      assertAll(() -> assertFalse(real.isLT(nan)), () -> assertFalse(real.isGT(nan)),
          () -> assertFalse(nan.isLT(real)), () -> assertFalse(nan.isGT(real)),
          () -> assertTrue(real.compareTo(nan) < 0), () -> assertTrue(nan.compareTo(real) > 0));
    }
  }

  @Test
  public void testEqualValuesAndSignedZero() {
    // Parsing retains the negative-zero bit, unlike Num.valueOf(-0.0), which normalizes it.
    Num negativeZero = (Num) new NumStr("-0.0").numericValue();
    assertEquals(Long.MIN_VALUE, Double.doubleToRawLongBits(negativeZero.doubleValue()));
    IReal[] zeros = {negativeZero, Num.valueOf(0.0), F.C0, ap("0")};
    for (IReal left : zeros) {
      for (IReal right : zeros) {
        assertAll(() -> assertEquals(0, RealNumberComparison.compare(left, right)),
            () -> assertFalse(left.isLT(right)), () -> assertFalse(left.isGT(right)));
      }
    }
    assertEquals(0, negativeZero.compareTo(0.0));
    assertEquals(0, Num.valueOf(0.0).compareTo(-0.0));
    // Ordering must not rewrite the stored IEEE representation.
    assertEquals(Long.MIN_VALUE, Double.doubleToRawLongBits(negativeZero.doubleValue()));
    assertEquals(0, RealNumberComparison.compare(ap("0.5"), F.C1D2));
    assertEquals(0, RealNumberComparison.compare(F.C1D2, ap("0.5")));
  }

  @Test
  public void testOperandNextToAMachineNumberIsTakenAtMachinePrecision() {
    // The binary double 0.1 is slightly larger than the decimal 0.1 and than 1/10, but both have
    // the machine value 0.1: answers 0.3 >= 3/10 and IntervalMemberQ(Interval({3/10,1}),0.3)
    // with True.
    assertSameValue(ap("0.1"), Num.valueOf(0.1));
    assertSameValue(Num.valueOf(-0.1), ap("-0.1"));
    assertSameValue(F.QQ(3, 10), Num.valueOf(0.3));
    // 2^53+1 has the machine value 2.^53, 2^53+4 is a machine number of its own
    assertSameValue(F.ZZ(9007199254740993L), Num.valueOf(0x1p53));
    assertOrder(Num.valueOf(0x1p53), F.ZZ(9007199254740996L));
    // without a machine value the stored value is compared exactly
    assertOrder(ap("1e-324"), Num.valueOf(Double.MIN_VALUE));
    assertOrder(Num.valueOf(Double.MAX_VALUE), F.ZZ(BigInteger.TEN.pow(400)));
    assertOrder(F.QQ(BigInteger.ONE, BigInteger.TEN.pow(400)), Num.valueOf(Double.MIN_VALUE));
  }

  @Test
  public void testCanonicalOrderIsConsistentAcrossNumericTypes() {
    IReal[] ordered = {Num.valueOf(Double.NEGATIVE_INFINITY), ap("-1e400"),
        F.ZZ(BigInteger.TEN.pow(399).negate()), Num.valueOf(-2), F.CN1, F.QQ(-1, 2),
        new NumStr("-0.0"), F.C0, ap("0"), Num.valueOf(0), ap("1e-400"), F.QQ(1, 3), ap("0.5"),
        F.C1D2, Num.valueOf(0.5), F.C1, ap("1.000000000000000000000000000001"), Num.valueOf(2),
        ap("1e400"), F.ZZ(BigInteger.TEN.pow(401)), Num.valueOf(Double.POSITIVE_INFINITY),
        Num.valueOf(Double.NaN)};
    for (int i = 0; i < ordered.length; i++) {
      for (int j = i; j < ordered.length; j++) {
        IReal a = ordered[i];
        IReal b = ordered[j];
        int order = Integer.signum(RealNumberComparison.compare(a, b));
        assertTrue(order <= 0, () -> a + " <= " + b);
        assertEquals(-order, Integer.signum(RealNumberComparison.compare(b, a)));
        if (order == 0) {
          // Values that compare equal must have the same ordering against every third value.
          for (IReal other : ordered) {
            assertEquals(Integer.signum(RealNumberComparison.compare(a, other)),
                Integer.signum(RealNumberComparison.compare(b, other)));
          }
        }
      }
    }
  }

  @Test
  public void testExactComparisonValueIsIndependentOfEnginePrecision() {
    evaluator.getEvalEngine().setNumericPrecision(16);
    assertEquals(0, RealNumberComparison.exactValue(F.QQ(1, 3)).compareTo(new Aprational("1/3")));
    assertEquals(0, RealNumberComparison.exactValue(Num.valueOf(0.1))
        .compareTo(new Aprational("3602879701896397/36028797018963968")));
    ApfloatNum value = ap("1.000000000000000000000000000001");
    assertEquals(0, RealNumberComparison.exactValue(value).compareTo(
        new Aprational("1000000000000000000000000000001/1000000000000000000000000000000")));
    assertEquals(80, value.precision());
  }

  @Test
  public void testComparisonRepresentationSupportsOtherRadixes() {
    // Numeric ordering is independent of how a finite mantissa is encoded.
    ApfloatNum binaryHalf = ApfloatNum.valueOf(new Apfloat("0.1", 80, 2));
    ApfloatNum ternaryThird = ApfloatNum.valueOf(new Apfloat("0.1", 80, 3));
    assertEquals(0, RealNumberComparison.compare(binaryHalf, ap("0.5")));
    assertEquals(0, RealNumberComparison.compare(ap("0.5"), binaryHalf));
    assertEquals(0, RealNumberComparison.compare(binaryHalf, F.C1D2));
    assertEquals(0, RealNumberComparison.compare(ternaryThird, F.QQ(1, 3)));
    assertEquals(0, RealNumberComparison.compare(F.QQ(1, 3), ternaryThird));
    assertFalse(ternaryThird.isLT(F.QQ(1, 3)));
    assertFalse(ternaryThird.isGT(F.QQ(1, 3)));
    assertOrder(ternaryThird, binaryHalf);
    assertEquals(0,
        RealNumberComparison.compare(ApfloatNum.valueOf(new Apfloat("100000", 80, 2)), F.ZZ(32)));
    assertEquals(0,
        RealNumberComparison.compare(ApfloatNum.valueOf(new Apfloat("0", 80, 3)), F.C0));
    assertEquals(0,
        RealNumberComparison.compare(ApfloatNum.valueOf(new Aprational("1/10", 3)), F.QQ(1, 3)));
  }

  @Test
  public void testIRealArithmeticPreservesLargeIntegers() {
    evaluator.getEvalEngine().setNumericPrecision(80);
    IReal exact = F.ZZ(new BigInteger("9007199254740993"));
    assertValue("9007199254740994", ap("1").add(exact));
    assertValue("18014398509481986", ap("2").multiply(exact));
    assertValue("-9007199254740992", ap("1").add((IReal) exact.negate()));
  }

  @Test
  public void testIRealArithmeticPreservesRangeAndOverloadResults() {
    evaluator.getEvalEngine().setNumericPrecision(80);
    IReal tiny = ap("1e-400");
    IReal huge = ap("1e400");
    assertValue("2e-400", ap("2").multiply(tiny));
    assertValue("1e400", ap("0").add(huge));
    assertValue("2e400", ap("2").multiply(huge));
    assertValue("1.125", ap("1").add((IReal) F.QQ(1, 8)));
    assertValue("0.25", ap("2").multiply((IReal) F.QQ(1, 8)));
    assertValue("2.5", ap("1").add((IReal) Num.valueOf(1.5)));
    assertValue("3", ap("2").multiply((IReal) Num.valueOf(1.5)));
    assertEquals(ap("2").multiply(ap("1e-400")), ap("2").multiply(tiny));
  }
}
