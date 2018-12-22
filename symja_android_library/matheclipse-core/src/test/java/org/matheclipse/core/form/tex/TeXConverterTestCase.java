package org.matheclipse.core.form.tex;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import junit.framework.TestCase;

/**
 * Tests LaTeX export function
 */
public class TeXConverterTestCase extends TestCase {

	TeXParser texConverter;

	public TeXConverterTestCase(String name) {
		super(name);
	}

	public void testTeX001() {
		check("\\frac{x}{\\sqrt{5}}", //
				"x/Sqrt(5)");
	}

	public void testTeX002() {
		check("1+y*z", //
				"1+y*z");
	}

	public void testTeX003() {
		check("(1+y)*z", //
				"(1+y)*z");
	}

	public void testTeX004() {
		check("\\frac{x}{\\sqrt{5}}", //
				"x/Sqrt(5)");
	}

	public void testTeX005() {
		check("f(x,1+y)", //
				"f(x,1+y)");
	}

	public void testTeX006() {
		check("\\operatorname { sin } 30 ^ { \\circ }", //
				"Sin(30*Degree)");
	}

	public void testTeX007() {
		check("\\operatorname { sin } \\frac { \\pi } { 2 }", //
				"Sin(Pi*1/2)");
	}

	public void testTeX008() {
		check("1 + 2 - 3 \\times 4 \\div 5", //
				"1+2-(3*4)/5");
	}

	public void testTeX009() {
		check("x ^ { 2 } + 1 = 2", //
				"1+x^2==2");
	}
	
	public void testTeX010() {
		check("\\left ( 1+2 \\right )^{2}", //
				"(1+2)^2");
	}
	
	

	public void check(String strEval, String strResult) {
		IExpr expr = texConverter.toExpression(strEval);
		assertEquals(expr.toString(), strResult);
	}

	public void checkEval(String strEval, String strResult) {
		IExpr expr = texConverter.toExpression(strEval);
		expr = EvalEngine.get().evaluate(expr);
		assertEquals(expr.toString(), strResult);
	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			// F.initSymbols();
			F.await();
			EvalEngine.get().setRelaxedSyntax(true);
			texConverter = new TeXParser(EvalEngine.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
