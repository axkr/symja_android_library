package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class IntegerTestCase extends ExprEvaluatorTestCase {

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
}
