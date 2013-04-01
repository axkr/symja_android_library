package org.matheclipse.core.form.mathml;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLContentUtilities;
import org.matheclipse.core.expression.F;

/**
 * Tests MathML content export function
 */
public class MathMLContentTestCase extends TestCase {
	MathMLContentUtilities mathUtil;

	public MathMLContentTestCase(String name) {
		super(name);
		F.initSymbols(null, null, false);
	}

	/**
	 * Test mathml function
	 */
	public void testMathMLContent() {
		check(
				"Sin[a]+Cos[b]",
				"<math:apply><math:plus /><math:apply><math:sin /><math:ci>a</math:ci></math:apply><math:apply><math:cos /><math:ci>b</math:ci></math:apply></math:apply>");
		check(
				"a*b+c",
				"<math:apply><math:plus /><math:apply><math:times /><math:ci>a</math:ci><math:ci>b</math:ci></math:apply><math:ci>c</math:ci></math:apply>");
		check("2", "<math:cn type=\"integer\">2</math:cn>");

		check(
				"I",
				"<math:cn type=\"complex-cartesian\"><math:cn type=\"rational\">0<math:sep />1</math:cn><math:sep /><math:cn type=\"rational\">1<math:sep />1</math:cn></math:cn>");

		check("2/3", "<math:cn type=\"rational\">2<math:sep />3</math:cn>");

		check("a+b", "<math:apply><math:plus /><math:ci>a</math:ci><math:ci>b</math:ci></math:apply>");
		check("a*b", "<math:apply><math:times /><math:ci>a</math:ci><math:ci>b</math:ci></math:apply>");
		check("a^b", "<math:apply><math:power /><math:ci>a</math:ci><math:ci>b</math:ci></math:apply>");
		// check("n!", "<m:mrow><m:mi>n</m:mi><m:mo>!</m:mo></m:mrow>");
		// check("k/2", "<m:mfrac><m:mi>k</m:mi><m:mn>2</m:mn></m:mfrac>");
		// check(
		// "Binomial[n,k/2]",
		// "<m:mrow><m:mo>(</m:mo><m:mfrac linethickness=\"0\"><m:mi>n</m:mi><m:mfrac><m:mi>k</m:mi><m:mn>2</m:mn></m:mfrac></m:mfrac><m:mo>)</m:mo></m:mrow>");
		//
		// check(
		// "x^2+4*x+4==0",
		// "<m:mrow><m:mrow><m:msup><m:mi>x</m:mi><m:mn>2</m:mn></m:msup><m:mo>+</m:mo><m:mrow><m:mn>4</m:mn><m:mo>&#x2062;</m:mo><m:mi>x</m:mi></m:mrow><m:mo>+</m:mo><m:mn>4</m:mn></m:mrow><m:mo>=</m:mo><m:mn>0</m:mn></m:mrow>");
		//
		// check("n!", "<m:mrow><m:mi>n</m:mi><m:mo>!</m:mo></m:mrow>");

	}

	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		mathUtil.toMathML(strEval, stw);
		assertEquals(stw.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<math:math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" + strResult + "\n</math:math>");
	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			EvalEngine engine = new EvalEngine();
			mathUtil = new MathMLContentUtilities(engine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
