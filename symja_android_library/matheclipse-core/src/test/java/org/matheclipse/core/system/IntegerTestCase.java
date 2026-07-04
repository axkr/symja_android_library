package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class IntegerTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testSmallPrtimes() {
    IAST smallPrimes = F.ZZ(144).factorSmallPrimes(1, 2);
    assertEquals("2^2*3^1", smallPrimes.toString());
    smallPrimes = F.ZZ(432).factorSmallPrimes(1, 2);
    assertEquals("2^2*3^1*Sqrt(3)", smallPrimes.toString());
  }

  @Test
  public void testIQuo() {
    IInteger a = F.ZZ(23).iquo(F.C4);
    assertEquals("5", a.toString());

    a = F.ZZ(-23).iquo(F.C4);
    assertEquals("-5", a.toString());

    a = F.ZZ(23).iquo(F.CN4);
    assertEquals("-5", a.toString());

    a = F.ZZ(-23).iquo(F.CN4);
    assertEquals("5", a.toString());
  }

  @Test
  public void testIRem() {
    IInteger a = F.ZZ(23).irem(F.C4);
    assertEquals("3", a.toString());

    a = F.ZZ(-23).irem(F.C4);
    assertEquals("-3", a.toString());

    a = F.ZZ(23).irem(F.CN4);
    assertEquals("3", a.toString());

    a = F.ZZ(-23).irem(F.CN4);
    assertEquals("-3", a.toString());
  }

  @Test
  public void testTrunc() {
    IInteger a = F.QQ(-11, 6).trunc();
    assertEquals("-1", a.toString());

    a = F.QQ(11, 6).trunc();
    assertEquals("1", a.toString());
  }

  @Test
  public void testBitAnd() {
    check("Table(BitAnd(n,3), {n,-10,10})", //
        "{2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2}");
    check("BitAnd(61,15)", //
        "13");
    check("BitAnd(3333, 5555, 7777, 9999)", //
        "1025");
    check("BitAnd(-2,-3)", //
        "-4");
  }

  @Test
  public void testBitClear() {
    check("IntegerDigits(36,2)", //
        "{1,0,0,1,0,0}");
    check("BitClear(36,2)", //
        "32");
    check("BitClear(36,-4)", //
        "BitClear(36,-4)");
    check("IntegerDigits(32,2)", //
        "{1,0,0,0,0,0}");

    check("BitClear(42,0)", //
        "42");
    check("BitClear(42,1)", //
        "40");
    check("BitClear(42,2)", //
        "42");
    check("BitClear(42,9)", //
        "42");
    check("BitClear(-42,9)", //
        "-554");
    check("BitClear(-42,2)", //
        "-46");
    check("BitClear(-42,-2)", //
        "BitClear(-42,-2)");
    check("BitClear(42,-2)", //
        "BitClear(42,-2)");
    check("BitClear(-42,-9)", //
        "BitClear(-42,-9)");
  }

  @Test
  public void testBitFlip() {
    check("BitFlip(42,0)", //
        "43");

    check("IntegerDigits(36,2)", //
        "{1,0,0,1,0,0}");
    check("BitFlip(36,2)", //
        "32");
    check("BitFlip(36,-4)", //
        "32");
    check("IntegerDigits(32,2)", //
        "{1,0,0,0,0,0}");

    check("BitFlip(42,0)", //
        "43");
    check("BitFlip(42,1)", //
        "40");
    check("BitFlip(42,2)", //
        "46");
    check("BitFlip(42,9)", //
        "554");
    check("BitFlip(-42,9)", //
        "-554");
    check("BitFlip(-42,2)", //
        "-46");
    check("BitFlip(-42,-2)", //
        "-58");
    check("BitFlip(42,-2)", //
        "58");
    check("BitFlip(-42,-9)", //
        "-42");
  }

  @Test
  public void testBitGet() {
    check("IntegerDigits(36,2)", //
        "{1,0,0,1,0,0}");
    check("BitGet(36,2)", //
        "1");
    check("BitGet(36,-4)", //
        "BitGet(36,-4)");

    check("BitGet(42,0)", //
        "0");
    check("BitGet(42,1)", //
        "1");
    check("BitGet(42,2)", //
        "0");
    check("BitGet(42,9)", //
        "0");
    check("BitGet(-42,9)", //
        "1");
    check("BitGet(-42,2)", //
        "1");
    check("BitGet(-42,-2)", //
        "BitGet(-42,-2)");
    check("BitGet(42,-2)", //
        "BitGet(42,-2)");
  }

  @Test
  public void testBitLength() {
    check("BitLength(1023)", //
        "10");
    check("BitLength(100) ", //
        "7");
    check("BitLength(-5)", //
        "3");
    check("BitLength(0)", //
        "0");
    check("BitLength(2^123-1)", //
        "123");
    check("BitLength(-(2^123-1))", //
        "123");
  }

  @Test
  public void testBitNot() {
    check("Table(BitNot(n), {n,-10,10})", //
        "{9,8,7,6,5,4,3,2,1,0,-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11}");
    check("BitNot(BitNot(test))", //
        "test");
  }

  @Test
  public void testBitOr() {
    check("BitOr(a,<|a->0,b:>1|>,{x,1,-1,-1},{{{}}})", //
        "BitOr(a,<|a->0,b:>1|>,{x,1,-1,-1},{{{}}})");
    check("Table(BitOr(n,3), {n,-10,10})", //
        "{-9,-9,-5,-5,-5,-5,-1,-1,-1,-1,3,3,3,3,7,7,7,7,11,11,11}");
    check("BitOr(61,15)", //
        "63");
    check("BitOr(3333, 5555, 7777, 9999)", //
        "16383");
    check("BitOr(-2,-3)", //
        "-1");
    check("BitOr(-1,2,x)", //
        "-1");
  }

  @Test
  public void testBitSet() {
    check("IntegerDigits(32,2)", //
        "{1,0,0,0,0,0}");
    check("BitSet(32,1)", //
        "34");
    check("IntegerDigits(34,2)", //
        "{1,0,0,0,1,0}");

    // message BitSet: BigInteger would overflow supported range
    check("BitSet(0,2147483647)", //
        "BitSet(0,2147483647)");
    check("BitSet(42,0)", //
        "43");
    check("BitSet(42,1)", //
        "42");
    check("BitSet(42,2)", //
        "46");
    check("BitSet(42,9)", //
        "554");
    check("BitSet(-42,9)", //
        "-42");
    check("BitSet(-42,2)", //
        "-42");
    check("BitSet(-42,-2)", //
        "BitSet(-42,-2)");
    check("BitSet(42,-2)", //
        "BitSet(42,-2)");
  }

  @Test
  public void testBitXor() {
    check("BitXor(-1,3)", //
        "-4");
    check("Table(BitXor(n,3), {n,-10,10})", //
        "{-11,-12,-5,-6,-7,-8,-1,-2,-3,-4,3,2,1,0,7,6,5,4,11,10,9}");
    check("BitXor(61,15)", //
        "50");
    check("BitXor(3333, 5555, 7777, 9999)", //
        "8664");
    check("BitXor(-2,-3)", //
        "3");
  }

  @Test
  public void testDigitSumBase10() {
    check("DigitSum(123)", //
        "6");
    // Absolute values are taken automatically for negative numbers
    check("DigitSum(-123)", //
        "6");
    check("DigitSum(0)", //
        "0");
    check("DigitSum(987654321)", //
        "45");
  }

  @Test
  public void testDigitSumOtherBases() {
    // Base 2: 123 = 1111011_2 -> 1+1+1+1+0+1+1 = 6
    check("DigitSum(123, 2)", //
        "6");

    // Base 16: 255 = FF_16 -> 15 + 15 = 30
    check("DigitSum(255, 16)", //
        "30");

    // Base 36: boundary for the fast string optimization
    // 1295 = 35 * 36 + 35 -> ZZ_36 -> 35 + 35 = 70
    check("DigitSum(1295, 36)", //
        "70");

    // Base > 36: falls back to the BigInteger arbitrary division loop
    // 100 = 2 * 40 + 20 -> digits are 2 and 20 in base 40 -> sum = 22
    check("DigitSum(100, 40)", //
        "22");

    check("DigitSum({1234, 0, 99})", //
        "{10,0,18}");
  }

  @Test
  public void testDigitSumEdgeCases() {
    // Invalid bases should return unevaluated
    check("DigitSum(123, 1)", //
        "DigitSum(123,1)");
    check("DigitSum(123, 0)", //
        "DigitSum(123,0)");
    check("DigitSum(123, -5)", //
        "DigitSum(123,-5)");

    // Non-integer inputs should return unevaluated
    check("DigitSum(12.3)", //
        "DigitSum(12.3)");
    check("DigitSum(x)", //
        "DigitSum(x)");
  }
}
