package org.matheclipse.core.form.mathml;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.F;

/**
 * Tests MathML presentation function
 */
public class MathMLPresentationTestCase extends TestCase {

	MathMLUtilities mathUtil;

	public MathMLPresentationTestCase(String name) {
		super(name);
	}

	/**
	 * Test mathml function
	 */
	public void testMathMLPresentation() {
		check("Integrate[f[x], x]",
				"<mo>&#x222B;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

		// check("-Infinity/Log[a]",
		// "<mfrac><mrow><mo>-</mo><mi>∞</mi></mrow><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mfrac>");
		check("x*(1+x)^(-2)",
				"<mfrac><mi>x</mi><msup><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow><mn>2</mn></msup></mfrac>");
		check("x/(1+x)/(1+x)",
				"<mfrac><mi>x</mi><mrow><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow><mo> </mo><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow></mrow></mfrac>");
		check("Sqrt[x]", "<msqrt><mi>x</mi></msqrt>");
		check("x^(1/3)", "<mroot><mi>x</mi><mn>3</mn></mroot>");
		check("x^(2/3)", "<mroot><msup><mi>x</mi><mn>2</mn></msup><mn>3</mn></mroot>");
		check("x^y", "<msup><mi>x</mi><mi>y</mi></msup>");
		check("Abs[-x]",
				"<mrow><mo>&LeftBracketingBar;</mo><mrow><mo>-</mo><mi>x</mi></mrow><mo>&RightBracketingBar;</mo></mrow>");
		check("a*b*c*d", "<mrow><mi>a</mi><mo> </mo><mi>b</mi><mo> </mo><mi>c</mi><mo> </mo><mi>d</mi></mrow>");
		check("k/2", "<mfrac><mi>k</mi><mn>2</mn></mfrac>");
		check("Binomial[n,k/2]",
				"<mrow><mo>(</mo><mfrac linethickness=\"0\"><mi>n</mi><mfrac><mi>k</mi><mn>2</mn></mfrac></mfrac><mo>)</mo></mrow>");

		check("a*b+c", "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo> </mo><mi>b</mi></mrow></mrow>");
		// check("HEllipsis", "<mo>&hellip;</mo>");
		check("MatrixForm[{a,b,c,d}]",
				"<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

		check("MatrixForm[{{a,b},{c,d}}]",
				"<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

		check("a*b+c", "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo> </mo><mi>b</mi></mrow></mrow>");
		check("a*b+c-2",
				"<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo> </mo><mi>b</mi></mrow><mo>-</mo><mn>2</mn></mrow>");
		check("a*b+c-2-d",
				"<mrow><mrow><mo>-</mo><mi>d</mi></mrow><mo>+</mo><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo> </mo><mi>b</mi></mrow><mo>-</mo><mn>2</mn></mrow>");

		check("a*(b+c)",
				"<mrow><mi>a</mi><mo> </mo><mrow><mrow><mo>(</mo><mi>c</mi><mo>+</mo><mi>b</mi><mo>)</mo></mrow></mrow></mrow>");
		check("I", "<mrow><mi>i</mi></mrow>");
		check("2*I", "<mrow><mn>2</mn><mo> </mo><mrow><mi>i</mi></mrow></mrow>");
		check("2/3", "<mfrac><mn>2</mn><mn>3</mn></mfrac>");

		check("a+b", "<mrow><mi>b</mi><mo>+</mo><mi>a</mi></mrow>");
		check("a*b", "<mrow><mi>a</mi><mo> </mo><mi>b</mi></mrow>");
		check("a^b", "<msup><mi>a</mi><mi>b</mi></msup>");
		check("n!", "<mrow><mi>n</mi><mo>!</mo></mrow>");
		check("4*x+4", "<mrow><mn>4</mn><mo>+</mo><mrow><mn>4</mn><mo> </mo><mi>x</mi></mrow></mrow>");

		check("x^2+4*x+4==0",
				"<mrow><mrow><mn>4</mn><mo>+</mo><mrow><mn>4</mn><mo> </mo><mi>x</mi></mrow><mo>+</mo><msup><mi>x</mi><mn>2</mn></msup></mrow><mo>=</mo><mn>0</mn></mrow>");

		check("n!", "<mrow><mi>n</mi><mo>!</mo></mrow>");
		check("Sum[i, {i,1,n}, {j,1,m}]",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mrow><munderover><mo>&#x2211;</mo><mrow><mi>j</mi><mo>=</mo><mn>1</mn></mrow><mi>m</mi></munderover><mi>i</mi></mrow></mrow>");

		check("Sum[i, {i,1,n}]",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mi>i</mi></mrow>");
		check("Sum[i, i]",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi></mrow><mi></mi></munderover><mi>i</mi></mrow>");

		check("Product[i, {i,10,n,1}, {j,m}]",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi><mo>=</mo><mn>10</mn></mrow><mi>n</mi></munderover><mrow><munderover><mo>&#x220F;</mo><mrow><mi>j</mi><mo>=</mo><mn>1</mn></mrow><mi>m</mi></munderover><mi>i</mi></mrow></mrow>");

		check("Product[i, {i,1,n}]",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mi>i</mi></mrow>");

		check("Product[i, i]",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi></mrow><mi></mi></munderover><mi>i</mi></mrow>");

		check("Integrate[Sin[x], x]",
				"<mo>&#x222B;</mo><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

		check("Integrate[Sin[x], {x,a,b}, {y,c,d}]",
				"<msubsup><mo>&#x222B;</mo><mi>a</mi><mi>b</mi></msubsup><msubsup><mo>&#x222B;</mo><mi>c</mi><mi>d</mi></msubsup><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mrow><mo>&#x2146;</mo><mi>y</mi></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

	}

	public void testMathML001() {
		check("Catalan", "<mi>C</mi>");
		check("Pi", "<mi>π</mi>");
		check("Infinity", "<mi>∞</mi>");
		check("-Infinity", "<mrow><mo>-</mo><mi>∞</mi></mrow>");
	}

	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		mathUtil.toMathML(strEval, stw);
		// fParser.initialize(strEval);
		// Object obj = fParser.start();
		// StringBuffer buf = new StringBuffer();
		// fMathMLFactory.convert(buf, obj, 0);
		assertEquals(stw.toString(), "<?xml version=\"1.0\"?>\n"
				+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
				+ "<math mode=\"display\">\n" + strResult + "</math>");

	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			// F.initSymbols();
			EvalEngine engine = new EvalEngine(false);
			mathUtil = new MathMLUtilities(engine, false, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// fParser = new Parser(null);
		// ExpressionFactory factory = new ExpressionFactory(new Namespace());
		// fParser.setFactory(factory);
		// fMathMLFactory = new MathMLFormFactory(factory);
	}

}
