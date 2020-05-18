package org.matheclipse.core.expression;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.hipparchus.fraction.BigFraction;
import org.junit.Test;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IInteger;

public class ComplexSymTest {

	@Test
	public void testIntegerPartDivisionGaussian1() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(3));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(8));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
		ComplexSym c4 = ComplexSym.valueOf(BigInteger.valueOf(2), BigInteger.valueOf(-4));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();
		
		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();
		
		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] expected =  c3.gaussianIntegers();
		IInteger[] expected2 =  c4.gaussianIntegers();

		IInteger[] result = c1.integerAndRemainderDivisionGuassian(parm2);

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

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);
		
		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd2() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
		

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		
		IInteger[] result = c1.gcd(parm2);
		
		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd3() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);
		
		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd4() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-332323), BigInteger.valueOf(223232));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-1));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(0));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd5() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-332323), BigInteger.valueOf(223232));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(-1));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-1), BigInteger.valueOf(0));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd6() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(32), BigInteger.valueOf(9));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(4), BigInteger.valueOf(11));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(-1));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd7() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(3));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(8));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(-2));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd8() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(14), BigInteger.valueOf(21));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-3), BigInteger.valueOf(2));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd9() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(16));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(10), BigInteger.valueOf(11));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(-2), BigInteger.valueOf(3));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}

	@Test
	public void testGcd10() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(-15), BigInteger.valueOf(-7));
		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(-10), BigInteger.valueOf(11));
		ComplexSym c3 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(1));

		IInteger[] parm2 = c2.gaussianIntegers();
		IInteger[] parm3 =  c3.gaussianIntegers();

		IInteger[] result = c1.gcd(parm2);

		IInteger expected = parm3[1];

		assertEquals(expected, result[1]);

	}
}