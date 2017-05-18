package org.matheclipse.core.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.matheclipse.core.numbertheory.Primality;

import junit.framework.TestCase;

public class NumberTheoryTestCase extends TestCase {

	public void testFactorInteger() {
		SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
		Primality.factorInteger(BigInteger.valueOf(990), map);
		assertEquals(map.toString(), "{2=1, 3=2, 5=1, 11=1}");

		map.clear();
		Primality.factorInteger(new BigInteger("341550071728321"), map);
		assertEquals(map.toString(), "{10670053=1, 32010157=1}");

		map.clear();
		Primality.factorInteger(BigInteger.valueOf(2010), map);
		assertEquals(map.toString(), "{2=1, 3=1, 5=1, 67=1}");

		map.clear();
		Primality.factorInteger(BigInteger.valueOf(24), map);
		assertEquals(map.toString(), "{2=3, 3=1}");
	}

	public void testFactorize() {
		List<BigInteger> result = Primality.factorize(BigInteger.valueOf(990), new ArrayList<BigInteger>());
		assertEquals(result.toString(), "[2, 3, 3, 5, 11]");

		result = Primality.factorize(new BigInteger("341550071728321"), new ArrayList<BigInteger>());
		assertEquals(result.toString(), "[10670053, 32010157]");

		result = Primality.factorize(BigInteger.valueOf(2010), new ArrayList<BigInteger>());
		assertEquals(result.toString(), "[2, 3, 5, 67]");

		result = Primality.factorize(BigInteger.valueOf(24), new ArrayList<BigInteger>());
		assertEquals(result.toString(), "[2, 2, 2, 3]");
	}

	public void testDivisors() {
		List<BigInteger> result = Primality.divisors(BigInteger.valueOf(990));
		assertEquals(result.toString(),
				"[1, 2, 3, 5, 6, 9, 10, 11, 15, 18, 22, 30, 33, 45, 55, 66, 90, 99, 110, 165, 198, 330, 495, 990]");

		result = Primality.divisors(new BigInteger("341550071728321"));
		assertEquals(result.toString(), "[1, 10670053, 32010157, 341550071728321]");

		result = Primality.divisors(BigInteger.valueOf(2010));
		assertEquals(result.toString(), "[1, 2, 3, 5, 6, 10, 15, 30, 67, 134, 201, 335, 402, 670, 1005, 2010]");

	}

	public void testPrime() {
		long result = Primality.prime(990);
		assertEquals(result, 7829);

		result = Primality.prime(2010);
		assertEquals(result, 17477);

		result = Primality.prime(24);
		assertEquals(result, 89);
		
		result = Primality.prime(167);
		assertEquals(result, 991);
	}

	public void testPrimeOmega() {
		BigInteger result = Primality.primeOmega(BigInteger.valueOf(990));
		assertEquals(result.toString(), "5");

		result = Primality.primeOmega(new BigInteger("341550071728321"));
		assertEquals(result.toString(), "2");

		result = Primality.primeOmega(BigInteger.valueOf(2010));
		assertEquals(result.toString(), "4");

		result = Primality.primeOmega(BigInteger.valueOf(24));
		assertEquals(result.toString(), "4");
	}

	public void testMultiplicativeOrder() {
		BigInteger result = Primality.multiplicativeOrder(BigInteger.valueOf(7), BigInteger.valueOf(108));
		assertEquals(result.toString(), "18");

		result = Primality.multiplicativeOrder(BigInteger.valueOf(-5), BigInteger.valueOf(7));
		assertEquals(result.toString(), "3");

	}
}
