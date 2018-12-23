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
		check("x=\\frac{1+y}{1+2*z^2}", //
				"x==(1+y)/(1+2*z^2)");
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

	public void testTeX011() {
		check("\\int_0^\\infty a dx", //
				"Integrate(a,{x,0,Infinity})");
		check("\\int_0^\\infty e^{-x^2} dx=\\frac{\\sqrt{\\pi}}{2}", //
				"Integrate(e^(-x^2),{x,0,Infinity})==Sqrt(Pi)*1/2");
	}

	public void testTeX012() {
		check("\\sin x", //
				"Sin(x)");
	}

	public void testTeX013() {
		check("\\exp x", //
				"Exp(x)");
	}

	public void testTeX014() {
		check("\\sum_{n=1}^{\\infty} 2^{-n} = 1", //
				"Sum(2^(-n),{n,1,Infinity})==1");
	}

	public void testTeX015() {
		check("\\prod_{i=a}^{b} f(i)", //
				"Product(f(i),{i,a,b})");
	}

	public void testTeX016() {
		check("\\frac{x}{\\sqrt{0.75}}", //
				"x/Sqrt(0.75)");
	}

	public void testTeX017() {
		check("a\\leq b \\le c", //
				"a<=b<=c");
	}

	public void testTeX018() {
		check("a\\geq b \\ge c", //
				"a>=b>=c");
	}

	public void testTeX019() {
		check("a < b < c", //
				"a<b<c");
	}

	public void testTeX020() {
		check("a > b > c", //
				"a>b>c");
	}

	public void testTeX021() {
		check("((a \\lor \\lnot b) \\land (c\\wedge a))", //
				"(a||!b)&&c&&a");
	}

	public void testTeX022() {
		// Rightarrow
		check("((a \\lor \\neg b) \\land (c\\Rightarrow a))", //
				"(a||!b)&&Implies(c,a)");

		// vs rightarrow
		check("((a \\lor \\neg b) \\land (c\\rightarrow a))", //
				"(a||!b)&&(c->a)");
	}

	public void testTeX023() {
		check("a \\to b", //
				"a->b");
	}

	public void testTeX024() {
		check("a \\Leftrightarrow b", //
				"Equivalent(a,b)");
	}

	public void testTeX025() {
		check("a!", //
				"a!");
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
