package org.matheclipse.core.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.math.BigInteger;
import org.junit.Test;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.numbertheory.GaussianInteger;

public class ComplexSymTest {

  @Test
  public void testSqrt1() {
    // https://math.stackexchange.com/a/44414
    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-7), BigInteger.valueOf(24));
    IInteger[] parm1 = c1.gaussianIntegers();

    // c + d*I
    IInteger c = parm1[0];
    IInteger d = parm1[1];

    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(3), BigInteger.valueOf(4));
    IInteger[] expected = c2.gaussianIntegers();

    IExpr val1 = c.multiply(c).add(d.multiply(d)).sqrt();
    if (val1.isInteger()) {
      IExpr a = c.add((IInteger) val1).divide(F.C2).sqrt();
      if (a.isInteger()) {
        assertEquals(expected[0], a);

        IExpr val2 = ((IInteger) val1).subtract(c).divide(F.C2).sqrt();
        if (val2.isInteger()) {
          // Sqrt(c + d*I) -> a + b*I
          IExpr b = ((IInteger) val2).multiply(d.complexSign());
          assertEquals(expected[1], b);
          return;
        }
      }
    }
    fail("testSqrt1()");
  }

  @Test
  public void testIntegerPartDivisionGaussian1() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(3));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(8));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-4));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian2() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(32), BigInteger.valueOf(9));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(4), BigInteger.valueOf(11));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-2));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-5));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian3() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(4), BigInteger.valueOf(11));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-5));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-2), BigInteger.valueOf(1));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(3), BigInteger.valueOf(-1));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian4() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-5));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(3), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian5() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(3), BigInteger.valueOf(-1));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(3));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian6() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(1));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian7() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntegerPartDivisionGaussian8() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian9() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testIntegerPartDivisionGaussian10() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-11), BigInteger.valueOf(11));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-11), BigInteger.valueOf(0));
    ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] expected = c3.gaussianIntegers();
    IInteger[] expected2 = c4.gaussianIntegers();

    IInteger[] result = GaussianInteger.quotientRemainder(parm1, parm2);

    assertEquals(expected[0], result[0]);
    assertEquals(expected[1], result[1]);
    assertEquals(expected2[0], result[2]);
    assertEquals(expected2[1], result[3]);
  }

  @Test
  public void testGcd1() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd2() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd3() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd4() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-332323), BigInteger.valueOf(223232));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd5() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-332323), BigInteger.valueOf(223232));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd6() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(32), BigInteger.valueOf(9));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(4), BigInteger.valueOf(11));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd7() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(3));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(8));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-2));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd8() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(14), BigInteger.valueOf(21));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);
    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd9() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(16));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(10), BigInteger.valueOf(11));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-2), BigInteger.valueOf(3));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd10() {

    ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-15), BigInteger.valueOf(-7));
    ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(-10), BigInteger.valueOf(11));
    ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

    IInteger[] parm1 = c1.gaussianIntegers();
    IInteger[] parm2 = c2.gaussianIntegers();
    IInteger[] parm3 = c3.gaussianIntegers();

    IInteger[] result = GaussianInteger.gcd(parm1, parm2);

    IInteger expected = parm3[1];

    assertEquals(expected, result[1]);
  }

  @Test
  public void testGcd11() {

    IInteger[] one = ComplexSym.valueOf(BigInteger.ONE, BigInteger.ZERO).gaussianIntegers();
    IInteger[] minusOne =
        ComplexSym.valueOf(BigInteger.ONE.negate(), BigInteger.ZERO).gaussianIntegers();
    IInteger[] I = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE).gaussianIntegers();
    IInteger[] minusI =
        ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE.negate()).gaussianIntegers();

    // gcd(1,1) ==> 1
    IInteger[] result = GaussianInteger.gcd(one, one);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(1,-1) ==> 1
    result = GaussianInteger.gcd(one, minusOne);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(-1,-1) ==> 1
    result = GaussianInteger.gcd(minusOne, minusOne);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(I,1) ==> 1
    result = GaussianInteger.gcd(I, one);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(-I,1) ==> 1
    result = GaussianInteger.gcd(minusI, one);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(1,I) ==> 1
    result = GaussianInteger.gcd(I, I);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(1,-I) ==> 1
    result = GaussianInteger.gcd(one, minusI);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(-I,I) ==> 1
    result = GaussianInteger.gcd(minusI, I);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(I,-I) ==> 1
    result = GaussianInteger.gcd(I, minusI);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);

    // gcd(-I,-I) ==> 1
    result = GaussianInteger.gcd(minusI, minusI);
    assertEquals(F.C1, result[0]);
    assertEquals(F.C0, result[1]);
  }
}
