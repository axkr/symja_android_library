package org.matheclipse.core.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.ASCIIPrettyPrinter3;
import org.matheclipse.core.interfaces.IExpr;

import junit.framework.TestCase;

public class ASCIIPrintTest extends TestCase {

	private void check(IExpr expr, String str1, String str2, String str3) {
		ASCIIPrettyPrinter3 strBuffer = new ASCIIPrettyPrinter3();
		strBuffer.convert(expr);
		String[] result = strBuffer.toStringBuilder();
		assertEquals(result[0].toString(), str1);
		assertEquals(result[1].toString(), str2);
		assertEquals(result[2].toString(), str3);
	}

	public void testOne() {
		IExpr expr = F.C1;
		String s1 = " ";
		String s2 = "1";
		String s3 = " ";
		check(expr, s1, s2, s3);
	}

	public void testMinusOne() {
		IExpr expr = F.CN1;
		String s1 = "  ";
		String s2 = "-1";
		String s3 = "  ";
		check(expr, s1, s2, s3);
	}

	public void testTen() {
		IExpr expr = F.C10;
		String s1 = "  ";
		String s2 = "10";
		String s3 = "  ";
		check(expr, s1, s2, s3);
	}

	public void testMinusTen() {
		IExpr expr = F.CN10;
		String s1 = "   ";
		String s2 = "-10";
		String s3 = "   ";
		check(expr, s1, s2, s3);
	}

//	public void testTimes001() {
//		IExpr expr = F.Times(F.C1, F.a);
//		String s1 = "       ";
//		String s2 = "-10 * a";
//		String s3 = "       ";
//		check(expr, s1, s2, s3);
//	}

	public void testTimes002() {
		IExpr expr = F.Times(F.CN1, F.a);
		String s1 = "    ";
		String s2 = " - a";
		String s3 = "    ";
		check(expr, s1, s2, s3);
	}

	
	public void testTimes003() {
		IExpr expr = F.Times(F.CN10, F.a);
		String s1 = "       ";
		String s2 = "-10 * a";
		String s3 = "       ";
		check(expr, s1, s2, s3);
	}

	public void testTimes004() {
		IExpr expr = F.Times(F.CN1D4, F.a);
		String s1 = "  1    ";
		String s2 = "- - * a";
		String s3 = "  4    ";
		check(expr, s1, s2, s3);
	}

	public void testTimes005() {
		IExpr expr = F.Times(F.CN1D4, F.Power(F.a, F.CN2));
		String s1 = "  1    1 ";
		String s2 = "- - * ---";
		String s3 = "  4   a^2";
		check(expr, s1, s2, s3);
	}

	public void testTimes006() {
		IExpr expr = F.Times(F.CN1D4, F.b, F.Power(F.a, F.CN2));
		String s1 = "  1    b ";
		String s2 = "- - * ---";
		String s3 = "  4   a^2";
		check(expr, s1, s2, s3);
	}

	public void testTimes007() {
		IExpr expr = F.Times(F.CN1D4, F.Plus(F.a, F.Power(F.b, F.CN2)));
		// -1/4 * (a + 1/b^2)
		String s1 = "  1           1    ";
		String s2 = "- - *  ( a + --- ) ";
		String s3 = "  4          b^2   ";
		check(expr, s1, s2, s3);
	}

	public void testPower001() {
		IExpr expr = F.Power(F.Cos(F.a), F.C3);
		String s1 = "        ";
		String s2 = "Cos(a)^3";
		String s3 = "        ";
		check(expr, s1, s2, s3);
	}

	public void testPlus001() {
		IExpr expr = F.Plus(F.C2, F.Times(F.CN1D3, F.Sin(F.x)));
		String s1 = "    1         ";
		String s2 = "2 - - * Sin(x)";
		String s3 = "    3         ";
		check(expr, s1, s2, s3);
	}
	
	public void testArcTan001() {
		// ArcTan((-1+2*x)/Sqrt(3))
		IExpr expr = F.ArcTan(F.Times(F.Plus(F.CN1,F.Times(F.C2,F.x)),F.C1DSqrt3));
		String s1 = "       -1+2*x  ";
		String s2 = "ArcTan(-------)";
		String s3 = "       Sqrt(3) ";
		check(expr, s1, s2, s3);
	}
	
	public void testArcTan002() {
		// ArcTan(a, (-1+2*x)/Sqrt(3))
		IExpr expr = F.ArcTan(F.a,F.Times(F.Plus(F.CN1,F.Times(F.C2,F.x)),F.C1DSqrt3));
		String s1 = "          -1+2*x  ";
		String s2 = "ArcTan(a, -------)";
		String s3 = "          Sqrt(3) ";
		check(expr, s1, s2, s3);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.await();
		EvalEngine.get().setRelaxedSyntax(true);
	}
}
