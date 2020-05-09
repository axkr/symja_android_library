package org.matheclipse.core.expression;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.hipparchus.fraction.BigFraction;
import org.junit.Test;
import org.matheclipse.core.interfaces.IComplex;

public class ComplexSymTest {

	@Test
	public void testIntegerPartDivisionGaussian1() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(11), BigInteger.valueOf(10));

		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(4), BigInteger.valueOf(1));

		ComplexSym expected = ComplexSym.valueOf(BigInteger.valueOf(3), BigInteger.valueOf(2));

		IComplex resultDevision = c1.integerPartDivisionGaussian(c2);

		System.out.println(c1);
		System.out.println(c2);
		System.out.println(resultDevision);

		assertEquals(expected, resultDevision);

	}

	@Test
	public void testIntegerPartDivisionGaussian2() {

		ComplexSym c1 = ComplexSym.valueOf(new BigFraction(0.5), new BigFraction(0.5));

		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(10), BigInteger.valueOf(10));

		ComplexSym expected = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

		IComplex resultDevision = c1.integerPartDivisionGaussian(c2);

		System.out.println(c1);
		System.out.println(c2);
		System.out.println(resultDevision);

		assertEquals(expected, resultDevision);

	}

	@Test
	public void testIntegerPartDivisionGaussian3() {

		ComplexSym c1 = ComplexSym.valueOf(new BigFraction(1), new BigFraction(1));

		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(1));

		ComplexSym expected = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));

		IComplex resultDevision = c1.integerPartDivisionGaussian(c2);

		System.out.println(c1);
		System.out.println(c2);
		System.out.println(resultDevision);

		assertEquals(expected, resultDevision);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testIntegerPartDivisionGaussian_zeroDivision() {

		ComplexSym c1 = ComplexSym.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(1));

		ComplexSym c2 = ComplexSym.valueOf(BigInteger.valueOf(0), BigInteger.valueOf(0));

		c1.integerPartDivisionGaussian(c2);

	}

}
