package org.matheclipse.core.system;

import java.math.BigInteger;
import java.util.SortedMap;
import java.util.TreeMap;

import org.matheclipse.core.numbertheory.EllipticCurveMethod;

import junit.framework.TestCase;

public class EllipticCurveMethodTest extends TestCase {

	public void testECM() {
		BigInteger big = new BigInteger("8438503049348381100385800049534923490020044110031");
		EllipticCurveMethod ecm1 = new EllipticCurveMethod(big);
		SortedMap<BigInteger, Integer> bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{59=1, 41387=1, 40320271=1, 85708917607365601059185614891297817=1}", bigMap.toString());

		System.out.println();
		big = new BigInteger("8392894255239922239");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();

		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{3=1, 7=1, 457=1, 11717=1, 84053=1, 887987=1}", bigMap.toString());

		System.out.println();
		big = new BigInteger("44343535354351600000003434353");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();

		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{149=1, 329569479697=1, 903019357561501=1}", bigMap.toString());

		System.out.println();
		// 50! * 8392894255239922239
		big = new BigInteger("255262268110991784076989150819008060991712040134738393813423038941626368000000000000");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{2=47, 3=23, 5=12, 7=9, 11=4, 13=3, 17=2, 19=2, 23=2, 29=1, 31=1, "
				+ "37=1, 41=1, 43=1, 47=1, 457=1, 11717=1, 84053=1, 887987=1}", bigMap.toString());

		System.out.println();
		big = new BigInteger(
				"10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals(
				"{73=1, 137=1, 401=1, 1201=1, 1601=1, 1676321=1, "
						+ "5964848081=1, 129694419029057750551385771184564274499075700947656757821537291527196801=1}",
				bigMap.toString());

		System.out.println();
		big = new BigInteger("2010");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{2=1, 3=1, 5=1, 67=1}", bigMap.toString());

		System.out.println();
		big = new BigInteger("65536");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{2=16}", bigMap.toString());

		System.out.println();
		big = new BigInteger("140016480344628383");
		ecm1 = new EllipticCurveMethod(big);
		bigMap = new TreeMap<BigInteger, Integer>();
		ecm1.factorize(bigMap);
		System.out.println(bigMap.toString());
		assertEquals("{373607131=1, 374769293=1}", bigMap.toString());

//		System.out.println();
//		big = new BigInteger("798645312654798147285393218574111453126547981472185139328574111781");
//		ecm1 = new EllipticCurveMethod(big);
//		bigMap = new TreeMap<BigInteger, Integer>();
//		ecm1.factorize(bigMap);
//		System.out.println(bigMap.toString());
//		assertEquals("{61=1, 67=1, 74729=1, 97913387938680010938335707=1, 26706566722753457593818813677521=1}",
//				bigMap.toString());

	}

}
