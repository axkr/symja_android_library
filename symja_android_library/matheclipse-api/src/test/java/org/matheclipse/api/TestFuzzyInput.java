package org.matheclipse.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

public class TestFuzzyInput {
	static {
		Config.FUZZY_PARSER = true;
	}
	@Test
	public void testExpand001() {
		IExpr expr = Pods.parseInput("(a+b)^4 expanded", EvalEngine.get());
		assertEquals("ExpandAll((a+b)^4)", expr.toString());
	}
	
	@Test
	public void testExpand002() {
		IExpr expr = Pods.parseInput("expanding f((a+b)^4)", EvalEngine.get());
		assertEquals("ExpandAll(f((a+b)^4))", expr.toString());
	}
	
	@Test
	public void testIntegerate001() {
		IExpr expr = Pods.parseInput("(a+b)^4 integrated", EvalEngine.get());
		assertEquals("Integrate((a+b)^4)", expr.toString());
	}
	
	@Test
	public void testSimplify001() {
		IExpr expr = Pods.parseInput("Simplify x^2+4*x+4", EvalEngine.get());
		assertEquals("FullSimplify(x^2+4*x+4)", expr.toString());
	}
}
