package org.matheclipse.core.form.tex;

import java.io.StringWriter;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;

import junit.framework.TestCase;

/**
 * Tests LaTeX export function
 */
public class BasicTeXTestCase extends TestCase {
	
	TeXUtilities texUtil;

	public BasicTeXTestCase(String name) {
		super(name);
	}

	/**
	 * Test mathml function
	 */
	public void testTeX001() {
		check("-0.0", "0.0");
		check("0.0", "0.0");
		check("a*b", "a\\,b");

	}

	public void testTeX002() {
		check("a*b+c", "a\\,b+c");
	}

	public void testTeX003() {
		check("1/3", "\\frac{1}{3}");
		check("theta", "\\theta");
		check("Theta", "\\theta");
	}

	public void testTeX004() {
		check("Sum(a,{x,1,z})", "\\sum_{x = 1}^{z} a");
		check("Sum(a,{x,z})", "\\sum_{x = 1}^{z} a");
		check("Sum(a,{x,1,n},{y,1,m})", "\\sum_{x = 1}^{n}\\sum_{y = 1}^{m} a");
		check("Sum(f,i)", "\\sum_{i} f");
	}

	public void testTeX005() {
		check("Product(a,{x,1,z})", "\\prod_{x = 1}^{z} a");
		check("Product(f,i)", "\\prod_{i} f");
	}

	public void testTeX006() {
		check("Integrate(f(x),y)", "\\int  f(x)\\,\\mathrm{d}y");
	}

	public void testTeX007() {
		check("Integrate(f(x),{x,1,10})", "\\int_{1}^{10} f(x)\\,\\mathrm{d}x");
		check("Integrate(E^(-x),{x,0,Infinity})", "\\int_{0}^{\\infty} E^{ - x}\\,\\mathrm{d}x");
	}

	public void testTeX008() {
		check("alpha + beta", "\\alpha+\\beta");
	}

	public void testTeX009() {
		check("Limit(Sin(x),x->0)", "\\lim_{x\\to 0 }\\,{\\sin(x)}");
	}

	public void testTeX010() {
		check("3+x*(4+x*(5+(33+x^2)*x^4))",
				"3+x\\,\\left( 4+x\\,\\left( 5+\\left( 33+x^{2}\\right) \\,x^{4}\\right) \\right) ");
	}

	public void testTeX011() {
		check("x^(3/4*y)", "x^{\\frac{3\\,y}{4}}");
	}

	public void testTeX012() {
//		check("{{{1,2,3},{4,5,6}}}", "\\begin{pmatrix} 1 & 2 & 3 \\\\\n" + " 4 & 5 & 6 \\\\\n" + "\\end{pmatrix}");
		check("MatrixForm({{1,2,3},{4,5,6}})", "\\begin{pmatrix} 1 & 2 & 3 \\\\\n" + " 4 & 5 & 6 \\\\\n" + "\\end{pmatrix}");
	}

	public void testTeX013() {
		check("a*b+c; a*b+c", "a\\,b+c;a\\,b+c");
	}

	public void testTeX014() {
		check("Sin(x)^2", "{\\sin(x)}^{2}");
		check("Sin(2x)^2", "{\\sin(2\\,x)}^{2}");
	}

	public void testTeX015() {
		check("Complex(0, -b)", " - b\\,\\imag");
		check("Complex(-a, 0)", " - a");
		check("Complex(-a, 1)", " - a + \\imag");
		check("Complex(-a, -1)", " - a - \\imag");
		check("Complex(a, -b)", "a - b\\,\\imag");
		check("Complex(a, b)", "a + b\\,\\imag");
		check("Complex(3/4,-(6/7)*I)", "\\frac{3}{4}+\\frac{6}{7}"); 
		check("3/4-(6/7)*I", "\\frac{3}{4}-\\frac{6}{7}\\,i ");
		check("1+2*I", "1+2\\,i ");
		check("1-2*I", "1-2\\,i ");
		check("-2*I", "\\left( -2\\right) \\cdot i ");
		check("1-2*I", "1-2\\,i ");
		check("Complex(1,-2*I)", "1+2");
		check("Complex(3.0, -2.0)", "3.0 + -2.0\\,i ");
		check("Complex(0,-2)", "-2\\,i ");
		check("Complex(0,-1)", "-1\\,i ");
	}

	public void testTeX016() {
		check("(a*d+b*c)*d^(-1)*f^(-1)", "\\frac{b\\,c+a\\,d}{d\\,f}");
	}
	
	public void testTeX017() {
		check("1/4*d^(-1)*f^(-1)", "\\frac{1}{4\\,d\\,f}");
	}
	
	public void testTeX018() {
		check("1/4*a^2*b^(-1)*f^(-1)", "\\frac{a^{2}}{4\\,b\\,f}");
	}
	
	public void testTeX019() {
		check("n!", "n ! ");
		check("n!!", "n !! ");
	}
	
	public void testTeX020() {
		check("Hold(++x)", "\\text{Hold}(\\text{++}x)");
		check("Hold(y^2/.x->3)", "\\text{Hold}(y^{2}\\text{/.}\\,x\\to 3)");
		check("Hold(y^2//.x->3)", "\\text{Hold}(y^{2}\\text{//.}\\,x\\to 3)");
		check("Hold(1 * 5 * x + 1 * 63)", "\\text{Hold}(5\\,x+63)");
		check("10*f(x)", "10\\,f(x)");
		check("Hold((5*3)/2)", "\\text{Hold}(\\frac{3\\cdot 5}{2})");
		check("Integrate(8+5x, {x, 5, 10})", "\\int_{5}^{10} 8+5\\,x\\,\\mathrm{d}x");
	}
	
	public void testTeX021() {
		check("-Infinity", " - \\infty");
		check("GoldenRatio", "\\phi");
		check("Infinity", "\\infty");
		
		check("EulerGamma", "\\gamma");
		check("Pi", "\\pi");
//		check("E", "E");
//		check("Catalan", "");
//		check("Degree", "");
//		
//		
//		check("Glaisher", "");
//		check("I", "");
//		
//		check("Khinchin", "");
		
	}
	
	public void testTeX022() {
		// issue #116
		check("-I*1/2*Sqrt(2)", "\\frac{\\left( -1\\,i \\right) \\,\\sqrt{2}}{2}");
	}
	
	public void testTeX023() {
		// issue #117
		check("5*3^(5*x)*Log(3)", "5\\cdot 3^{5\\,x}\\,\\log(3)");
	}
	
	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		texUtil.toTeX(strEval, stw);
		assertEquals(stw.toString(), strResult);

	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
//			F.initSymbols();
			EvalEngine engine = new EvalEngine();
			texUtil = new TeXUtilities(engine, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
