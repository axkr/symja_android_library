package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.eval;
import static org.matheclipse.core.expression.F.x;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class CompareToTestCase extends AbstractTestCase {
	public CompareToTestCase(String name) {
		super(name);
	}

	public void testCT001() {
		IExpr ast1, ast2;
		// x*ArcSin(x)
		ast1 = Times(x, ArcSin(x));
		// (1-x^2)^(1/2)
		ast2 = Power(Subtract(C1, Power(x, C2)), C1D2);
		ast1 = eval(ast1);
		ast2 = eval(ast2);

		int res = ast1.compareTo(ast2);
		assertEquals(1, res);
	}

	public void testCT002() {
		IExpr ast1, ast2;
		// x*ArcCos(x)
		ast1 = Times(x, ArcCos(x));
		// -(1-x^2)^(1/2)
		ast2 = Negate(Power(Subtract(C1, Power(x, C2)), C1D2));
		ast1 = eval(ast1);
		ast2 = eval(ast2);

		int res = ast1.compareTo(ast2);
		assertEquals(1, res);
	}

	public void testCT003() {
		IExpr ast1, ast2;
		// 2*ArcTan(x*(-2*x-x^2)^(-1/2))
		ast1 = Times(C2, ArcTan(Power(Times(x, Subtract(Times(CN2, x), Negate(Power(x, 2)))), CN1D2)));
		// x*ArcSin(1+x)
		ast2 = Times(x, ArcSin(Plus(C1, x)));
		ast1 = eval(ast1);
		ast2 = eval(ast2);

		int res = ast1.compareTo(ast2);
		assertEquals(1, res);
	}

//	public void testCT004() {
//		IExpr ast1, ast2;
//
//		ast1 = Slot1;
//		ast2 = Power(Slot1, C2);
//		// ast1 = eval(ast1);
//		// ast2 = eval(ast2);
//
//		int res = ast1.compareTo(ast2);
//		assertEquals(-1, res);
//		
//		res = ast2.compareTo(ast1);
//		assertEquals(1, res);
//	}

	public void testOut001() {
		IExpr ast1, ast2;
		// -Infinity
		ast1 = CNInfinity;
		// x
		ast2 = x;
		ast1 = eval(ast1);
		ast2 = eval(ast2);

		int res = ast1.compareTo(ast2);
		assertEquals(-1, res);

		check("-Infinity+b+a", "-Infinity+a+b");
	}
	
	public void testCT004() {
		IASTAppendable ast1, ast2;
		ast1 = F.ast(F.f);
		ast2 = F.ast(F.f);
		ast1.append(F.a);
		ast2.append(F.a);
		ast2.append(F.b);

		int res = ast1.compareTo(ast2);
		assertEquals(-1, res);
	}

	public void testIssue122a() {
		ISymbol b = F.$s("b");
		ISymbol c = F.$s("c");
		ISymbol x1 = F.$s("x1");
		ISymbol x3 = F.$s("x3");
		ISymbol x4 = F.$s("x4");
		ISymbol x5 = F.$s("x5");
		IPattern x1_c = F.$p(x1, c);
		IPattern x3_b = F.$p(x3, b);
		IPattern x3_c = F.$p(x3, c);
		IPattern x4_c = F.$p(x4, c);

		IAST ast1 = F.Times(F.CN1, x1_c, x3_b, x3_c);
		IAST ast2 = F.Times(F.CN1, x3, x5, x1_c, x4_c);

		int res = ast1.compareTo(ast2);
		assertEquals(-1, res);
		res = ast2.compareTo(ast1);
		assertEquals(1, res);

		check("-Infinity+b+a", "-Infinity+a+b");
	}

	public void testIssue122b() {
		// x5_c <||> x3*x4_c => -1
		ISymbol b = F.$s("b");
		ISymbol c = F.$s("c");
		ISymbol x1 = F.$s("x1");
		ISymbol x3 = F.$s("x3");
		ISymbol x4 = F.$s("x4");
		ISymbol x5 = F.$s("x5");
		IPattern x1_c = F.$p(x1, c);
		IPattern x3_b = F.$p(x3, b);
		IPattern x3_c = F.$p(x3, c);
		IPattern x4_c = F.$p(x4, c);
		IPattern x5_c = F.$p(x5, c);

		IAST ast2 = F.Times(x3, x4_c);
		int res = x5_c.compareTo(ast2);
		assertEquals(1, res);
		res = ast2.compareTo(x5_c);
		assertEquals(-1, res);

		check("-Infinity+b+a", "-Infinity+a+b");
	}

	public void testIssue122c() {
		ISymbol b = F.$s("b");
		ISymbol c = F.$s("c");
		ISymbol x1 = F.$s("x1");
		ISymbol x3 = F.$s("x3");
		ISymbol x4 = F.$s("x4");
		ISymbol x5 = F.$s("x5");
		IPattern x1_c = F.$p(x1, c);
		IPattern x3_b = F.$p(x3, b);
		IPattern x3_c = F.$p(x3, c);
		IPattern x5_c = F.$p(x5, c);

		IAST ast1 = F.Times(x3, x5, x5_c);
		IAST ast2 = F.Times(F.CN1, x1_c, x3_b, x3_c);

		int res = ast1.compareTo(ast2);
		assertEquals(1, res);
		res = ast2.compareTo(ast1);
		assertEquals(-1, res);

		check("-Infinity+b+a", "-Infinity+a+b");
	}
}
