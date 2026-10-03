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
    assertAll(smaller + " < " + larger,
        () -> assertTrue(smaller.isLT(larger)),
        () -> assertFalse(smaller.isGT(larger)),
        () -> assertTrue(larger.isGT(smaller)),
        () -> assertFalse(larger.isLT(smaller)),
        () -> assertTrue(smaller.compareTo(larger) < 0),
        () -> assertTrue(larger.compareTo(smaller) > 0));
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
    String[] comparisons = {"10^400 < Pi^1000", "Pi^1000 > 10^400",
        "N[1+10^-30,50] > 1", "1 < N[1+10^-30,50]",
        "N[1-10^-30,50] < 1", "1 > N[1-10^-30,50]",
        "0 < N[10^-400,50]", "N[10^-400,50] > 0",
        "N[-10^-400,50] < 0", "0 > N[-10^-400,50]",
        "N[9007199254740992,50] < 9007199254740993",
        "9007199254740993 > N[9007199254740992,50]",
        "9007199254740993 > 9007199254740992.0",
        "9007199254740992.0 < 9007199254740993"};
    for (String input : comparisons) {
      assertEquals(S.True, evaluator.eval(input), input);
    }
  }

  @Test
  public void testSmallIntegerAndMachineComparisons() {
    assertOrder(ap("0.999999999999999999999999999999"), F.C1);
    assertOrder(F.C1, ap("1.000000000000000000000000000001"));
    assertOrder(ap("0.999999999999999999999999999999"), Num.valueOf(1.0));
    assertOrder(Num.valueOf(1.0), ap("1.000000000000000000000000000001"));
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
    for (IReal real : new IReal[] {ap("-1e400"), F.C0, ap("1e400"),
        F.ZZ(BigInteger.TEN.pow(400)), F.QQ(1, 3)}) {
      assertAll(() -> assertFalse(real.isLT(nan)), () -> assertFalse(real.isGT(nan)),
          () -> assertFalse(nan.isLT(real)), () -> assertFalse(nan.isGT(real)),
          () -> assertTrue(real.compareTo(nan) < 0),
          () -> assertTrue(nan.compareTo(real) > 0));
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
        assertAll(() -> assertEquals(0, left.compareTo(right)),
            () -> assertFalse(left.isLT(right)), () -> assertFalse(left.isGT(right)));
      }
    }
    assertEquals(0, negativeZero.compareTo(0.0));
    assertEquals(0, Num.valueOf(0.0).compareTo(-0.0));
    // Ordering must not rewrite the stored IEEE representation.
    assertEquals(Long.MIN_VALUE, Double.doubleToRawLongBits(negativeZero.doubleValue()));
    assertEquals(0, ap("0.5").compareTo(F.C1D2));
    assertEquals(0, F.C1D2.compareTo(ap("0.5")));
  }

  @Test
  public void testMachineOperandRetainsItsStoredBinaryValue() {
    // The binary double 0.1 is slightly larger than the exact decimal 0.1.
    assertOrder(ap("0.1"), Num.valueOf(0.1));
    assertOrder(Num.valueOf(-0.1), ap("-0.1"));
    assertOrder(ap("1e-324"), Num.valueOf(Double.MIN_VALUE));
  }

  @Test
  public void testCanonicalOrderIsConsistentAcrossNumericTypes() {
    IReal[] ordered = {Num.valueOf(Double.NEGATIVE_INFINITY), ap("-1e400"),
        F.ZZ(BigInteger.TEN.pow(399).negate()), Num.valueOf(-2), F.CN1, F.QQ(-1, 2),
        new NumStr("-0.0"), F.C0, ap("0"), Num.valueOf(0), ap("1e-400"), F.QQ(1, 3),
        ap("0.5"), F.C1D2,
        Num.valueOf(0.5), F.C1, ap("1.000000000000000000000000000001"), Num.valueOf(2),
        ap("1e400"), F.ZZ(BigInteger.TEN.pow(401)), Num.valueOf(Double.POSITIVE_INFINITY),
        Num.valueOf(Double.NaN)};
    for (int i = 0; i < ordered.length; i++) {
      for (int j = i; j < ordered.length; j++) {
        IReal a = ordered[i];
        IReal b = ordered[j];
        int order = Integer.signum(a.compareTo(b));
        assertTrue(order <= 0, () -> a + " <= " + b);
        assertEquals(-order, Integer.signum(b.compareTo(a)));
        if (order == 0) {
          // Values that compare equal must have the same ordering against every third value.
          for (IReal other : ordered) {
            assertEquals(Integer.signum(a.compareTo(other)), Integer.signum(b.compareTo(other)));
          }
        }
      }
    }
  }

  @Test
  public void testExactComparisonValueIsIndependentOfEnginePrecision() {
    evaluator.getEvalEngine().setNumericPrecision(16);
    assertEquals(0, F.QQ(1, 3).exactComparisonValue().compareTo(new Aprational("1/3")));
    assertEquals(0, Num.valueOf(0.1).exactComparisonValue()
        .compareTo(new Aprational("3602879701896397/36028797018963968")));
    ApfloatNum value = ap("1.000000000000000000000000000001");
    assertEquals(0, value.exactComparisonValue()
        .compareTo(new Aprational("1000000000000000000000000000001/1000000000000000000000000000000")));
    assertEquals(80, value.precision());
  }

  @Test
  public void testComparisonRepresentationSupportsOtherRadixes() {
    // Numeric ordering is independent of how a finite mantissa is encoded.
    ApfloatNum binaryHalf = ApfloatNum.valueOf(new Apfloat("0.1", 80, 2));
    ApfloatNum ternaryThird = ApfloatNum.valueOf(new Apfloat("0.1", 80, 3));
    assertEquals(0, binaryHalf.compareTo(ap("0.5")));
    assertEquals(0, ap("0.5").compareTo(binaryHalf));
    assertEquals(0, binaryHalf.compareTo(F.C1D2));
    assertEquals(0, ternaryThird.compareTo(F.QQ(1, 3)));
    assertEquals(0, F.QQ(1, 3).compareTo(ternaryThird));
    assertFalse(ternaryThird.isLT(F.QQ(1, 3)));
    assertFalse(ternaryThird.isGT(F.QQ(1, 3)));
    assertOrder(ternaryThird, binaryHalf);
    assertEquals(0, ApfloatNum.valueOf(new Apfloat("100000", 80, 2)).compareTo(F.ZZ(32)));
    assertEquals(0, ApfloatNum.valueOf(new Apfloat("0", 80, 3)).compareTo(F.C0));
    assertEquals(0, ApfloatNum.valueOf(new Aprational("1/10", 3)).compareTo(F.QQ(1, 3)));
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
