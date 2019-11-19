package org.matheclipse.core.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.AbstractTestCase;

import static org.matheclipse.core.convert.rings.SymjaRings.*;

/**
 *
 */
public class SymjaRingsTest extends AbstractTestCase {
	public SymjaRingsTest(String name) {
		super(name);
	}

	private static void assertEqualExpressions(EvalEngine engine, IExpr a, IExpr b) {
		IExpr subtract = a.subtract(b);
		boolean isZero = engine.evalTrue(F.PossibleZeroQ(subtract));
		assertTrue(subtract.toString(), isZero);
	}

	public void test1() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("F[a]^2 - Sin[x]^2");
		IExpr expected = ee.evaluate("-(F[a]+Sin[x])*(-F[a]+Sin[x])");
		assertEqualExpressions(ee, expected, FactorOverQ(expr, false));
	}

	public void test2() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("a/b - c/d");
		IExpr expected = ee.evaluate("(a*d - b*c)/b/d");
		assertEqualExpressions(ee, expected, TogetherOverQ(expr));
	}

	public void test3() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("x^2/(x^2 - 1) + x/(x^2 - 1)");
		IExpr expected = ee.evaluate("x/(x-1)");
		assertEqualExpressions(ee, expected, TogetherOverQ(expr));
	}

	public void test4() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("x^2 + 2");

		IExpr[] extensions = { ee.evaluate("(-2)^(1/2)") };
		IExpr expected = ee.evaluate("(x - I*(2)^(1/2)) * (x + I*(2)^(1/2))");
		IExpr actual = FactorOverExtension(expr, extensions, true, false);
		assertEqualExpressions(ee, expected, actual);
	}

	public void test5() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("3 * x^3 + I * 2");

		IExpr[] extensions = { ee.evaluate("(2 / 3)^(1/3)") };
		IExpr expected = ee.evaluate("(x+(-1)*I*(2/3)^(1/3))*(x^2-((2/3)^(1/3))^2+I*(2/3)^(1/3)*x)*3");
		IExpr actual = FactorOverExtension(expr, extensions, true, false);
		assertEqualExpressions(ee, expected, actual);

		expected = ee.evaluate("(2/3*I+x^3)*3");
		actual = FactorOverExtension(expr, extensions, false, false);
		assertEqualExpressions(ee, expected, actual);
	}

	public void test6() {
		EvalEngine ee = EvalEngine.get();
		IExpr a = ee.evaluate("3 * x^3 + p * 2");
		IExpr b = ee.evaluate("3 * x^3 - q * 2");
		IExpr var = ee.evaluate("x");

		IExpr expected = ee.evaluate(" {1,{1/(2*q+2*p),-1/(2*p+2*q)}}");
		IExpr actual = PolynomialExtendedGCDOverQ(a, b, var);
		assertEqualExpressions(ee, expected.getAt(1), actual.getAt(1));
		assertEqualExpressions(ee, expected.getAt(2).getAt(1), actual.getAt(2).getAt(1));
		assertEqualExpressions(ee, expected.getAt(2).getAt(2), actual.getAt(2).getAt(2));
	}

	public void test7() {
		EvalEngine ee = EvalEngine.get();
		IExpr a = ee.evaluate("3 * x^3 + I * 2");
		IExpr b = ee.evaluate("3 * x^3 - I * 2");
		IExpr var = ee.evaluate("x");

		IExpr expected = ee.evaluate(" {1,{-I/4, I/4}}");
		IExpr actual = PolynomialExtendedGCDOverExtension(a, b, var, new IExpr[0], true);

		assertEqualExpressions(ee, expected.getAt(1), actual.getAt(1));
		assertEqualExpressions(ee, expected.getAt(2).getAt(1), actual.getAt(2).getAt(1));
		assertEqualExpressions(ee, expected.getAt(2).getAt(2), actual.getAt(2).getAt(2));
	}

	public void test8() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("6 + 3*x^2");
		IExpr expected = ee.evaluate("3*(2 + x^2)");
		assertEqualExpressions(ee, expected, FactorOverQ(expr, false));
	}

	public void test9() {
		EvalEngine ee = EvalEngine.get();
		IExpr expr = ee.evaluate("3/4*x^2+9/16");
		IExpr expected = ee.evaluate("3/16*(3+4*x^2)");
		assertEqualExpressions(ee, expected, FactorOverQ(expr, false));
	}
}
