package org.matheclipse.core.form.tex;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;

/**
 * Tests LaTeX export function
 */
public class BasicTeXTestCase extends TestCase {
	TeXUtilities texUtil;

	public BasicTeXTestCase(String name) {
		super(name);
		F.initSymbols(null, null, false);
	}

	/**
	 * Test mathml function
	 */
	public void testTeX001() {
		check("a*b", "a\\,b");

	}

	public void testTeX002() {
		check("a*b+c", "a\\,b + c");
	}

	public void testTeX003() {
		check("1/3", "\\frac{1}{3}");
	}

	public void testTeX004() {
		check("Sum[a,{x,1,z}]", "\\sum_{x = 1}^{z}a");
	}

	public void testTeX005() {
		check("Product[a,{x,1,z}]", "\\prod_{x = 1}^{z}a");
	}

	public void testTeX006() {
		check("Integrate[f[x],y]", "\\int f\\left( x \\right)\\,dy");
	}

	public void testTeX007() {
		check("Integrate[f[x],{x,1,10}]", "\\int_{1}^{10}f\\left( x \\right)\\,dx");
	}

	public void testTeX008() {
		check("alpha + beta", "\\alpha + \\beta");
	}

	public void testTeX009() {
		check("Limit[Sin[x],x->0]", "\\mathop {\\lim }\\limits_{x \\to 0}\\sin(x)");
	}

	public void testTeX010() {
		check("3+x*(4+x*(5+(33+x^2)*x^4))", 
				"3 + x\\,\\left( 4 + x\\,\\left( 5 + \\left( 33 + {x}^{2}\\right) \\,{x}^{4}\\right) \\right) ");
	}
	
	public void testTeX011() {
		check("x^(3/4*y)", "{x}^{\\left( \\frac{3}{4}\\,y\\right) }");
	}
	
	public void testTeX012() {
		check("MatrixForm[{{1,2,3},{4,5,6}}]", "\\begin{pmatrix} 1 & 2 & 3 \\\\\n" + 
				" 4 & 5 & 6 \\\\\n" + 
				"\\end{pmatrix}");
	}
	
	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		texUtil.toTeX(strEval, stw);
		assertEquals(stw.toString(), strResult);

	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			EvalEngine engine = new EvalEngine(); 
			texUtil = new TeXUtilities(engine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
