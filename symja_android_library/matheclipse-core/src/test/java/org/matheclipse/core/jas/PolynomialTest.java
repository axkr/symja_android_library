package org.matheclipse.core.jas;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.AbstractTestCase;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

public class PolynomialTest extends AbstractTestCase {
	JASConvert<BigRational> fJAS;

	public PolynomialTest(String name) {
		super(name);
	}

	public void testPoly001() {
		ISymbol x = new Symbol("x");

		IAST f = F.Plus(IntegerSym.valueOf(10), x);
		try {
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);
			assertEquals("x + 10 ", poly.toString());

			// convert back to MathEclipse expression
			IAST ast = fJAS.rationalPoly2Expr(poly);
			// 1*x^1+10
			assertEquals("Plus[Times[1/1, Power[x, 1]], Times[10/1]]", ast.toString());
			// evaluated expression
			check(ast, "x+10");
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testPoly002() {
		ISymbol x = new Symbol("x");
		ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
		variables.add(x);
		try {
			IAST f = F.Power(x, IntegerSym.valueOf(3));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);
			assertEquals("x^3", poly.toString());

			// convert back to MathEclipse expression
			IAST ast = fJAS.rationalPoly2Expr(poly);
			// 1*x^3
			assertEquals("Plus[Times[1/1, Power[x, 3]]]", ast.toString());
			// evaluated expression
			check(ast, "x^3");
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testPoly003() {
		ISymbol x = new Symbol("x");
		try {
			IAST f = F.Times(x, IntegerSym.valueOf(3));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);
			assertEquals("3 x", poly.toString());

			// convert back to MathEclipse expression
			IAST ast = fJAS.rationalPoly2Expr(poly);
			// 3*x^1
			assertEquals("Plus[Times[3/1, Power[x, 1]]]", ast.toString());
			// evaluated expression
			check(ast, "3*x");
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testPoly004() {
		ISymbol x = new Symbol("x");
		try {
			IAST f = F.Plus(IntegerSym.valueOf(10), x);
			f.add(F.Power(x, IntegerSym.valueOf(3)));
			f.add(F.Times(x, IntegerSym.valueOf(3)));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);

			assertEquals("x^3 + 4 x + 10 ", poly.toString());
			// convert back to MathEclipse expression
			IAST ast = fJAS.rationalPoly2Expr(poly);
			// 1*x^3 + 4*x^1 + 10
			assertEquals("Plus[Times[1/1, Power[x, 3]], Times[4/1, Power[x, 1]], Times[10/1]]", ast.toString());
			// evaluated expression
			check(ast, "x^3+4*x+10");
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testDivideRemainder001() {
		ISymbol x = new Symbol("x");
		try {
			IAST f = F.Plus(IntegerSym.valueOf(10), x);
			f.add(F.Power(x, IntegerSym.valueOf(3)));
			f.add(F.Times(x, IntegerSym.valueOf(3)));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);

			assertEquals("x^3 + 4 x + 10 ", poly.toString());
			GenPolynomial<BigRational>[] result = poly.divideAndRemainder(poly);
			assertEquals("1 ", result[0].toString());
			assertEquals("0", result[1].toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testDivideRemainder002() {
		ISymbol x = new Symbol("x");
		// -5-4*x+x^2
		try {
			IAST f1 = F.Plus(IntegerSym.valueOf(-5), F.Times(x, IntegerSym.valueOf(-4)));
			f1.add(F.Power(x, IntegerSym.valueOf(2)));
			GenPolynomial<BigRational> poly1 = fJAS.expr2JAS(f1);
			// -5+x
			IAST f2 = F.Plus(IntegerSym.valueOf(-5), x);
			GenPolynomial<BigRational> poly2 = fJAS.expr2JAS(f2);

			assertEquals("x^2 - 4 x - 5 ", poly1.toString());
			assertEquals("x - 5 ", poly2.toString());
			GenPolynomial<BigRational>[] result = poly1.divideAndRemainder(poly2);
			assertEquals("x + 1 ", result[0].toString());
			assertEquals("0", result[1].toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testEGCD001() {
		ISymbol x = new Symbol("x");
		try {
			IAST f = F.Plus(IntegerSym.valueOf(10), x);
			f.add(F.Power(x, IntegerSym.valueOf(3)));
			f.add(F.Times(x, IntegerSym.valueOf(3)));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);

			assertEquals("x^3 + 4 x + 10 ", poly.toString());
			GenPolynomial<BigRational>[] result = poly.egcd(poly);
			assertEquals("x^3 + 4 x + 10 ", result[0].toString());
			assertEquals("0", result[1].toString());
			assertEquals("1 ", result[2].toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testEGCD002() {
		ISymbol x = new Symbol("x");
		try {
			// -5-4*x+x^2
			IAST f1 = F.Plus(IntegerSym.valueOf(-5), F.Times(x, IntegerSym.valueOf(-4)));
			f1.add(F.Power(x, IntegerSym.valueOf(2)));
			GenPolynomial<BigRational> poly1 = fJAS.expr2JAS(f1);
			// -5+x
			IAST f2 = F.Plus(IntegerSym.valueOf(-5), x);
			GenPolynomial<BigRational> poly2 = fJAS.expr2JAS(f2);

			assertEquals("x^2 - 4 x - 5 ", poly1.toString());
			assertEquals("x - 5 ", poly2.toString());
			GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
			assertEquals("x - 5 ", result[0].toString());
			assertEquals("0", result[1].toString());
			assertEquals("1 ", result[2].toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testGCD001() {
		ISymbol x = new Symbol("x");

		try {
			IAST f = F.Plus(IntegerSym.valueOf(10), x);
			f.add(F.Power(x, IntegerSym.valueOf(3)));
			f.add(F.Times(x, IntegerSym.valueOf(3)));
			GenPolynomial<BigRational> poly = fJAS.expr2JAS(f);

			assertEquals("x^3 + 4 x + 10 ", poly.toString());
			GenPolynomial<BigRational> result = poly.gcd(poly);
			assertEquals("x^3 + 4 x + 10 ", result.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testGCD002() {
		ISymbol x = new Symbol("x");

		try {
			// -5-4*x+x^2
			IAST f1 = F.Plus(IntegerSym.valueOf(-5), F.Times(x, IntegerSym.valueOf(-4)));
			f1.add(F.Power(x, IntegerSym.valueOf(2)));
			GenPolynomial<BigRational> poly1 = fJAS.expr2JAS(f1);
			// -5+x
			IAST f2 = F.Plus(IntegerSym.valueOf(-5), x);
			GenPolynomial<BigRational> poly2 = fJAS.expr2JAS(f2);

			assertEquals("x^2 - 4 x - 5 ", poly1.toString());
			assertEquals("x - 5 ", poly2.toString());
			GenPolynomial<BigRational> result = poly1.gcd(poly2);
			assertEquals("x - 5 ", result.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}

	}

	public void testFactor001() {
		ISymbol x = new Symbol("x");
		try {
			// -5-4*x+x^2
			IAST f1 = F.Plus(IntegerSym.valueOf(-5), F.Times(x, IntegerSym.valueOf(-4)));
			f1.add(F.Power(x, IntegerSym.valueOf(2)));
			GenPolynomial<BigRational> poly1 = fJAS.expr2JAS(f1);
			assertEquals("x^2 - 4 x - 5 ", poly1.toString());

			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
			SortedMap<GenPolynomial<BigRational>, Long> map = factorAbstract.baseFactors(poly1);
			StringBuffer buf = new StringBuffer();
			buf.append('{');
			for (SortedMap.Entry<GenPolynomial<BigRational>, Long> entry : map.entrySet()) {
				GenPolynomial<BigRational> key = entry.getKey();
				buf.append('(');
				buf.append(key.toString());
				buf.append(")^");
				Long val = entry.getValue();
				buf.append(val.toString());
				buf.append(", ");
			}
			buf.append('}');
			assertEquals("{(x - 5 )^1, (x + 1 )^1, }", buf.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testFactor002() {
		ISymbol x = new Symbol("x");
		try {
			// -5-4*x+x^2
			IAST f1 = F.Plus(FractionSym.valueOf(-5, 2), F.Times(x, IntegerSym.valueOf(-2)));
			f1.add(F.Times(F.C1D2, F.Power(x, IntegerSym.valueOf(2))));
			GenPolynomial<BigRational> poly1 = fJAS.expr2JAS(f1);
			assertEquals("1/2 x^2 - 2 x - 5/2 ", poly1.toString());

			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
			SortedMap<GenPolynomial<BigRational>, Long> map = factorAbstract.baseFactors(poly1);
			StringBuffer buf = new StringBuffer();
			buf.append('{');
			for (SortedMap.Entry<GenPolynomial<BigRational>, Long> entry : map.entrySet()) {
				GenPolynomial<BigRational> key = entry.getKey();
				buf.append('(');
				buf.append(key.toString());
				buf.append(")^");
				Long val = entry.getValue();
				buf.append(val.toString());
				buf.append(", ");
			}
			buf.append('}');
			assertEquals("{(1/2 )^1, (x - 5 )^1, (x + 1 )^1, }", buf.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testFactor003() {
		ISymbol x = new Symbol("x");
		try {
			ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
			variables.add(x);
			ModIntegerRing mod2IntegerRing = new ModIntegerRing(2, true);
			JASConvert jas003 = new JASConvert(variables, mod2IntegerRing);
			// -1+x^10
			IAST f1 = F.Plus(IntegerSym.valueOf(-1), F.Power(x, IntegerSym.valueOf(10)));
			GenPolynomial<ModInteger> poly1 = jas003.expr2JAS(f1);
			assertEquals("x^10 + 1 ", poly1.toString());

			FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(mod2IntegerRing);
			SortedMap<GenPolynomial<ModInteger>, Long> map = factorAbstract.baseFactors(poly1);
			StringBuffer buf = new StringBuffer();
			buf.append("{");
			for (SortedMap.Entry<GenPolynomial<ModInteger>, Long> entry : map.entrySet()) {
				GenPolynomial<ModInteger> key = entry.getKey();
				buf.append("(");
				buf.append(key.toString());
				buf.append(")^");
				Long val = entry.getValue();
				buf.append(val.toString());
				buf.append(", ");
			}
			buf.append("}");
			assertEquals("{(x + 1 )^2, (x^4 + x^3 + x^2 + x + 1 )^2, }", buf.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	public void testFactor004() {
		ISymbol x = new Symbol("x");
		try {
			ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
			variables.add(x);
			ModIntegerRing mod17IntegerRing = new ModIntegerRing(17, true);
			JASConvert jas003 = new JASConvert(variables, mod17IntegerRing);
			// -1+x^10
			IAST f1 = F.Plus(IntegerSym.valueOf(-3), F.Power(x, IntegerSym.valueOf(10)));
			GenPolynomial<ModInteger> poly1 = jas003.expr2JAS(f1);
			assertEquals("x^10 + 14 ", poly1.toString());

			FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(mod17IntegerRing);
			SortedMap<GenPolynomial<ModInteger>, Long> map = factorAbstract.baseFactors(poly1);
			StringBuffer buf = new StringBuffer();
			buf.append('{');
			for (SortedMap.Entry<GenPolynomial<ModInteger>, Long> entry : map.entrySet()) {
				GenPolynomial<ModInteger> key = entry.getKey();
				buf.append('(');
				buf.append(key.toString());
				buf.append(")^");
				Long val = entry.getValue();
				buf.append(val.toString());
				buf.append(", ");
			}
			buf.append('}');
			assertEquals("{(x^2 + 5 )^1, (x^4 + 3 x^3 + 2 x^2 + 2 x + 8 )^1, (x^4 + 14 x^3 + 2 x^2 + 15 x + 8 )^1, }", buf.toString());
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	// public void testFactor005() {
	// ISymbol x = new Symbol("x");
	// ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
	// variables.add(x);
	// BigComplex complexRing = new BigComplex();
	// JASConvert jas003 = new JASConvert(variables, complexRing);
	// IAST f1 = F.Plus(IntegerSym.valueOf(1), F.Power(x, IntegerSym.valueOf(2)));
	// GenPolynomial<BigComplex> poly1 = jas003.expr2Poly(f1);
	// assertEquals("x^2 + 1 ", poly1.toString());
	//
	// FactorAbstract<BigComplex> factorAbstract =
	// FactorFactory.getImplementation(complexRing);
	// SortedMap<GenPolynomial<BigComplex>, Long> map =
	// factorAbstract.baseFactors(poly1);
	// StringBuffer buf = new StringBuffer();
	// buf.append("{");
	// for (SortedMap.Entry<GenPolynomial<BigComplex>, Long> entry :
	// map.entrySet()) {
	// GenPolynomial<BigComplex> key = entry.getKey();
	// buf.append("(");
	// buf.append(key.toString());
	// buf.append(")^");
	// Long val = entry.getValue();
	// buf.append(val.toString());
	// buf.append(", ");
	// }
	// buf.append("}");
	// assertEquals("{(x^2 + 5 )^1, (x^4 + 3 x^3 + 2 x^2 + 2 x + 8 )^1, (x^4 + 14
	// x^3 + 2 x^2 + 15 x + 8 )^1, }", buf.toString());
	// }

	public void testPoly999() {
		ISymbol x = new Symbol("x");
		ISymbol y = new Symbol("y");
		ISymbol z = new Symbol("z");
		ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
		variables.add(x);
		variables.add(y);
		variables.add(z);

		try {
			IAST f = F.Plus(IntegerSym.valueOf(10), x);
			f.add(F.Power(y, IntegerSym.valueOf(3)));
			f.add(F.Times(z, IntegerSym.valueOf(3)));
			f.add(F.Times(IntegerSym.valueOf(4), x, z));
			JASConvert jas999 = new JASConvert(variables, BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas999.expr2JAS(f);

			// assertEquals("4 x * z + x + y^3 + 3 z + 10 ", poly.toString());^
			IAST ast = jas999.rationalPoly2Expr(poly);
			// 
			assertEquals(
					"Plus[Times[4/1, Power[x, 1], Power[z, 1]], Times[1/1, Power[x, 1]], Times[1/1, Power[y, 3]], Times[3/1, Power[z, 1]], Times[10/1]]",
					ast.toString());
			// evaluated expression
			check(ast, "4*x*z+3*z+y^3+x+10");
		} catch (JASConversionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test taken from JAS example: "compute the first ten Legendre polynomials"
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Legendre_polynomials" for
	 * reference values
	 */
	public void testLegendre() {
		BigRational fac = new BigRational();
		String[] var = new String[] { "x" };
		GenPolynomialRing<BigRational> ring = new GenPolynomialRing<BigRational>(fac, 1, var);

		int n = 11;
		List<GenPolynomial<BigRational>> P = new ArrayList<GenPolynomial<BigRational>>(n);
		GenPolynomial<BigRational> t, one, x, xc;
		BigRational n21, nn;

		one = ring.getONE();
		x = ring.univariate(0);

		P.add(one);
		P.add(x);
		for (int i = 2; i < n; i++) {
			n21 = new BigRational(2 * i - 1);
			xc = x.multiply(n21);
			t = xc.multiply(P.get(i - 1)); // (2n-1) x P[n-1]
			nn = new BigRational(i - 1);
			xc = P.get(i - 2).multiply(nn); // (n-1) P[n-2]
			t = t.subtract(xc);
			nn = new BigRational(1, i);
			t = t.multiply(nn); // 1/n t
			P.add(t);
		}

		IAST ast = fJAS.rationalPoly2Expr(P.get(0));
		check(ast, "1");

		ast = fJAS.rationalPoly2Expr(P.get(1));
		check(ast, "x");

		Object[] obj3 = fJAS.factorTerms(P.get(2));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		IAST result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/2*(3*x^2-1)");

		obj3 = fJAS.factorTerms(P.get(3));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/2*(5*x^3-3*x)");

		obj3 = fJAS.factorTerms(P.get(4));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/8*(35*x^4-30*x^2+3)");

		obj3 = fJAS.factorTerms(P.get(5));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/8*(63*x^5-70*x^3+15*x)");

		obj3 = fJAS.factorTerms(P.get(6));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/16*(231*x^6-315*x^4+105*x^2-5)");

		obj3 = fJAS.factorTerms(P.get(7));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/16*(429*x^7-693*x^5+315*x^3-35*x)");

		obj3 = fJAS.factorTerms(P.get(8));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/128*(6435*x^8-12012*x^6+6930*x^4-1260*x^2+35)");

		obj3 = fJAS.factorTerms(P.get(9));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/128*(12155*x^9-25740*x^7+18018*x^5-4620*x^3+315*x)");

		obj3 = fJAS.factorTerms(P.get(10));
		ast = fJAS.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) obj3[2]);
		result = F.Times(F.fraction((java.math.BigInteger) obj3[0], (java.math.BigInteger) obj3[1]), ast);
		check(result, "1/256*(46189*x^10-109395*x^8+90090*x^6-30030*x^4+3465*x^2-63)");
	}

	@Override
	protected void setUp() {
		super.setUp();
		ISymbol x = new Symbol("x");
		ArrayList<ISymbol> variables = new ArrayList<ISymbol>();
		variables.add(x);
		fJAS = new JASConvert(variables, BigRational.ZERO);
	}
}
