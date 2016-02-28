package org.matheclipse.core.system;

import org.matheclipse.core.interfaces.IExpr;

import static org.matheclipse.core.expression.F.*;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class CompareToTestCases extends AbstractTestCase {
	public CompareToTestCases(String name) {
		super(name);
	}

	public void testCT001() {
		IExpr ast1, ast2;
		// x*ArcSin(x)
		ast1 = Times(x, ArcSin(x));
		// (1-x^2)^(1/2)
		ast2 = Power(Subtract(C1, Power(x,C2)),C1D2);
		ast1 = eval(ast1);
		ast2 = eval(ast2);
		
		int res=ast1.compareTo(ast2);
		assertEquals(4, res);
	} 
 
	public void testCT002() {
		IExpr ast1, ast2;
		// x*ArcCos(x)
		ast1 = Times(x, ArcCos(x));
		// -(1-x^2)^(1/2)
		ast2 = Negate(Power(Subtract(C1, Power(x,C2)),C1D2));
		ast1 = eval(ast1);
		ast2 = eval(ast2);
		
		int res=ast1.compareTo(ast2);
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
		
		int res=ast1.compareTo(ast2);
		assertEquals(1, res);
	} 
	
	public void testOut001() {
		IExpr ast1, ast2;
		//  -Infinity
		ast1 = CNInfinity;
		// x 
		ast2 = x;
		ast1 = eval(ast1);
		ast2 = eval(ast2);
		
		int res=ast1.compareTo(ast2);
		assertEquals(-1, res);
		
		check("-Infinity+b+a", "-Infinity+a+b");
	} 
}
