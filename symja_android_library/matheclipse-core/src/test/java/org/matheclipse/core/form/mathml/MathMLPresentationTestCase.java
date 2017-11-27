package org.matheclipse.core.form.mathml;

import java.io.StringWriter;

import org.apfloat.Apcomplex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import junit.framework.TestCase;

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
		// check("-a-b*I",
		// "<mrow><mo>-</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>b</mi></mrow><mrow><mo>-</mo><mi>a</mi></mrow></mrow>");
		IExpr expr = EvalEngine.get().evaluate("-1/2-3/4*I");
		check(expr,
				"<mrow><mrow><mo>-</mo><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mrow><mo>-</mo><mrow><mfrac><mn>3</mn><mn>4</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");
		expr = EvalEngine.get().evaluate("1/2+3/4*I");
		check(expr,
				"<mrow><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mrow><mo>+</mo><mrow><mfrac><mn>3</mn><mn>4</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");

		check("-1/2-3/4*I",
				"<mrow><mfrac><mrow><mrow><mo>(</mo><mn>-3</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mn>4</mn></mfrac><mo>-</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></mrow>");
		check("1/2+3/4*I",
				"<mrow><mfrac><mrow><mn>3</mn><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mn>4</mn></mfrac><mo>+</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></mrow>");

		check("\"hello\nworld\"",
				"<mtext>hello</mtext><mspace linebreak='newline' /><mtext>world</mtext><mspace linebreak='newline' />");
		check("x /.y", "<mrow><mi>x</mi><mo>/.</mo><mi>y</mi></mrow>");
		check("f(x_,y_):={x,y}/;x>y",
				"<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mtext>x_</mtext><mspace linebreak='newline' /><mo>,</mo><mtext>y_</mtext><mspace linebreak='newline' /></mrow><mo>)</mo></mrow></mrow><mo>:=</mo><mrow><mrow><mo>{</mo><mrow><mi>x</mi><mo>,</mo><mi>y</mi></mrow><mo>}</mo></mrow><mo>/;</mo><mrow><mi>x</mi><mo>&gt;</mo><mi>y</mi></mrow></mrow></mrow>");

		check("f(x)&",
				"<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow>");
		check("f(x, #)&[y]",
				"<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi><mo>,</mo><mi>#1</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow><mo>[</mo><mi>y</mi><mo>]</mo>");

		check("f(x)[y][z]",
				"<mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>[</mo><mi>y</mi><mo>]</mo><mo>[</mo><mi>z</mi><mo>]</mo>");
		check("f'(x)", "<mrow><mi>f</mi><mo>'</mo><mrow><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow>");
		check(F.Slot1, "<mi>#1</mi>");
		check(F.SlotSequence(2), "<mi>##2</mi>");
		Apcomplex c = new Apcomplex("(-0.5,-4.0)");
		check(F.complexNum(c), "<mrow><mn>-5e-1</mn><mo>-</mo><mn>4</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
		check(F.complexNum(-0.5, -4.0),
				"<mrow><mn>-0.5</mn><mo>-</mo><mn>4.0</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
		check(F.pattern(F.x), "<mtext>x_</mtext><mspace linebreak='newline' />");
		check(F.complexNum(0.5, 4.0),
				"<mrow><mn>0.5</mn><mo>+</mo><mn>4.0</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
		check(F.complexNum(-0.5, -4.0),
				"<mrow><mn>-0.5</mn><mo>-</mo><mn>4.0</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");

		check("0.5-0.75*x",
				"<mrow><mrow><mrow><mo>(</mo><mn>-0.75</mn><mo>)</mo></mrow><mo>&#0183;</mo><mi>x</mi></mrow><mo>+</mo><mn>0.5</mn></mrow>");

		check("-0.33-0.75*y",
				"<mrow><mrow><mrow><mo>(</mo><mn>-0.75</mn><mo>)</mo></mrow><mo>&#0183;</mo><mi>y</mi></mrow><mo>-</mo><mn>0.33</mn></mrow>");

		check("Cos(x^3)",
				"<mrow><mi>cos</mi><mo>&#x2061;</mo><mo>(</mo><msup><mi>x</mi><mn>3</mn></msup><mo>)</mo></mrow>");

		check("Sqrt(-1/2+2/3*I)",
				"<msqrt><mrow><mfrac><mrow><mn>2</mn><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mn>3</mn></mfrac><mo>-</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></mrow></msqrt>");

		check("Sqrt(-4*I)",
				"<msqrt><mrow><mrow><mo>(</mo><mn>-4</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow></msqrt>");
		check("DirectedInfinity()",
				"<mrow><mi>DirectedInfinity</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow></mrow><mo>)</mo></mrow></mrow>");
		check("Integrate(f(x), x)",
				"<mo>&#x222B;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

		check("-Infinity/Log(a)",
				"<mfrac><mrow><mo>-</mo><mi>&#x221E;</mi></mrow><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mfrac>");
		check("x*(1+x)^(-2)",
				"<mfrac><mi>x</mi><msup><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow><mn>2</mn></msup></mfrac>");
		check("x/(1+x)/(1+x)",
				"<mfrac><mi>x</mi><mrow><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mo>+</mo><mn>1</mn><mo>)</mo></mrow></mrow></mrow></mfrac>");
		check("Sqrt(x)", "<msqrt><mi>x</mi></msqrt>");
		check("x^(1/3)", "<mroot><mi>x</mi><mn>3</mn></mroot>");
		check("x^(2/3)", "<mroot><msup><mi>x</mi><mn>2</mn></msup><mn>3</mn></mroot>");
		check("x^y", "<msup><mi>x</mi><mi>y</mi></msup>");
		check("Abs(-x)", "<mrow><mo>&#10072;</mo><mrow><mo>-</mo><mi>x</mi></mrow><mo>&#10072;</mo></mrow>");
		check("a*b*c*d",
				"<mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi><mo>&#0183;</mo><mi>c</mi><mo>&#0183;</mo><mi>d</mi></mrow>");
		check("k/2", "<mfrac><mi>k</mi><mn>2</mn></mfrac>");
		check("Binomial(n,k/2)",
				"<mrow><mo>(</mo><mfrac linethickness=\"0\"><mi>n</mi><mfrac><mi>k</mi><mn>2</mn></mfrac></mfrac><mo>)</mo></mrow>");

		check("a*b+c", "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow></mrow>");
		// check("HEllipsis", "<mo>&hellip;</mo>");
		check("MatrixForm({a,b,c,d})",
				"<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

		check("MatrixForm({{a,b},{c,d}})",
				"<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

		check("a*b+c", "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow></mrow>");
		check("a*b+c-2",
				"<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow><mo>-</mo><mn>2</mn></mrow>");
		check("a*b+c-2-d",
				"<mrow><mrow><mo>-</mo><mi>d</mi></mrow><mo>+</mo><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow><mo>-</mo><mn>2</mn></mrow>");

		check("a*(b+c)",
				"<mrow><mi>a</mi><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>c</mi><mo>+</mo><mi>b</mi><mo>)</mo></mrow></mrow></mrow>");

		check("-Infinity/Log(a)",
				"<mfrac><mrow><mo>-</mo><mi>&#x221E;</mi></mrow><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mfrac>");
		check("I", "<mrow><mi>&#x2148;</mi></mrow>");
		check("2*I", "<mrow><mn>2</mn><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow>");
		check("2/3", "<mrow><mfrac><mn>2</mn><mn>3</mn></mfrac></mrow>");

		check("a+b", "<mrow><mi>b</mi><mo>+</mo><mi>a</mi></mrow>");
		check("a*b", "<mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow>");
		check("a^b", "<msup><mi>a</mi><mi>b</mi></msup>");
		check("n!", "<mrow><mi>n</mi><mo>!</mo></mrow>");
		check("4*x+4", "<mrow><mn>4</mn><mo>+</mo><mrow><mn>4</mn><mo>&#0183;</mo><mi>x</mi></mrow></mrow>");

		check("x^2+4*x+4==0",
				"<mrow><mrow><mn>4</mn><mo>+</mo><mrow><mn>4</mn><mo>&#0183;</mo><mi>x</mi></mrow><mo>+</mo><msup><mi>x</mi><mn>2</mn></msup></mrow><mo>==</mo><mn>0</mn></mrow>");

		check("n!", "<mrow><mi>n</mi><mo>!</mo></mrow>");
		check("Sum(i, {i,1,n}, {j,1,m})",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mrow><munderover><mo>&#x2211;</mo><mrow><mi>j</mi><mo>=</mo><mn>1</mn></mrow><mi>m</mi></munderover><mi>i</mi></mrow></mrow>");

		check("Sum(i, {i,1,n})",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mi>i</mi></mrow>");
		check("Sum(i, i)",
				"<mrow><munderover><mo>&#x2211;</mo><mrow><mi>i</mi></mrow><mi></mi></munderover><mi>i</mi></mrow>");

		check("Product(i, {i,10,n,1}, {j,m})",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi><mo>=</mo><mn>10</mn></mrow><mi>n</mi></munderover><mrow><munderover><mo>&#x220F;</mo><mrow><mi>j</mi><mo>=</mo><mn>1</mn></mrow><mi>m</mi></munderover><mi>i</mi></mrow></mrow>");

		check("Product(i, {i,1,n})",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mi>n</mi></munderover><mi>i</mi></mrow>");

		check("Product(i, i)",
				"<mrow><munderover><mo>&#x220F;</mo><mrow><mi>i</mi></mrow><mi></mi></munderover><mi>i</mi></mrow>");

		check("Integrate(Sin(x), x)",
				"<mo>&#x222B;</mo><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

		check("Integrate(Sin(x), {x,a,b}, {y,c,d})",
				"<msubsup><mo>&#x222B;</mo><mi>a</mi><mi>b</mi></msubsup><msubsup><mo>&#x222B;</mo><mi>c</mi><mi>d</mi></msubsup><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mrow><mo>&#x2146;</mo><mi>y</mi></mrow><mrow><mo>&#x2146;</mo><mi>x</mi></mrow>");

	}

	public void testMathML001() {
		check("-0.0", "<mn>0.0</mn>");
		check("0.0", "<mn>0.0</mn>");
		check("Catalan", "<mi>C</mi>");
		check("Pi", "<mi>&#x03C0;</mi>");
		check("Infinity", "<mi>&#x221E;</mi>");
		check("-Infinity", "<mrow><mo>-</mo><mi>&#x221E;</mi></mrow>");
	}

	public void testMathML002() {
		IExpr expr = EvalEngine.get().evaluate("-1/2*Sqrt(1/2)*Sqrt(5+Sqrt(5))");
		check(expr, "<mfrac><mrow><mo>-</mo><msqrt><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></msqrt><mo>&#0183;</mo><msqrt><mrow><msqrt><mn>5</mn></msqrt><mo>+</mo><mn>5</mn></mrow></msqrt></mrow><mn>2</mn></mfrac>");

		// (-1/3+I)*a
		expr = EvalEngine.get().evaluate("a*((- 1/3 )+x)");
		check(expr,
				"<mrow><mi>a</mi><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mo>-</mo><mrow><mfrac><mn>1</mn><mn>3</mn></mfrac></mrow><mo>)</mo></mrow></mrow></mrow>");
		expr = EvalEngine.get().evaluate("a*((- 1/3 )*I)");
		check(expr,
				"<mrow><mrow><mrow><mo>-</mo><mrow><mfrac><mn>1</mn><mn>3</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>a</mi></mrow>");

		expr = EvalEngine.get().evaluate("a*((- 1/3 )+I)");
		System.out.println(expr.toString());
		check(expr,
				"<mrow><mrow><mo>(</mo><mrow><mo>-</mo><mfrac><mn>1</mn><mn>3</mn></mfrac></mrow><mrow><mo>+</mo><mi>&#x2148;</mi></mrow><mo>)</mo></mrow><mo>&#0183;</mo><mi>a</mi></mrow>");

		// (-I*a)^x
		expr = EvalEngine.get().evaluate("(a*(-I))^x");
		System.out.println(expr.toString());
		check(expr,
				"<msup><mrow><mrow><mo>(</mo><mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>a</mi><mo>)</mo></mrow></mrow><mi>x</mi></msup>");

		// -I*1/2*Pi
		expr = EvalEngine.get().evaluate("ArcTanh(Infinity)");
		System.out.println(expr.toString());
		check(expr,
				"<mrow><mrow><mrow><mo>-</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>&#x03C0;</mi></mrow>");

		expr = EvalEngine.get().evaluate("1-I");
		check(expr, "<mrow><mrow><mn>1</mn></mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow>");

		expr = EvalEngine.get().evaluate("-(1/3)+I");
		check(expr,
				"<mrow><mrow><mo>-</mo><mfrac><mn>1</mn><mn>3</mn></mfrac></mrow><mrow><mo>+</mo><mi>&#x2148;</mi></mrow></mrow>");
		expr = EvalEngine.get().evaluate("1-4*I");
		check(expr,
				"<mrow><mrow><mn>1</mn></mrow><mrow><mo>-</mo><mrow><mn>4</mn></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");
		expr = EvalEngine.get().evaluate("1-(1/2)*I");
		check(expr,
				"<mrow><mrow><mn>1</mn></mrow><mrow><mo>-</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");
		expr = EvalEngine.get().evaluate("1.0-0.5*I");
		check(expr, "<mrow><mn>1.0</mn><mo>-</mo><mn>0.5</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");

		expr = EvalEngine.get().evaluate("(1.0-0.5*I)*a");
		check(expr,
				"<mrow><mrow><mo>(</mo><mn>1.0</mn><mo>-</mo><mn>0.5</mn><mo>&#0183;</mo><mi>&#x2148;</mi><mo>)</mo></mrow><mo>&#0183;</mo><mi>a</mi></mrow>");

	}

	public void testDerivatve001() {
		IExpr expr = EvalEngine.get().evaluate("1/f''(x)");
		check(expr,
				"<mfrac><mn>1</mn><mrow><mi>f</mi><mo>''</mo><mrow><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow></mfrac>");

	}

	public void testSeries001() {
		IExpr expr = EvalEngine.get().evaluate("Series(f(x),{x,a,3})");
		check(expr,
				"<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mo>+</mo><mrow><mrow><mi>f</mi><mo>'</mo><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow></mrow><mo>+</mo><mfrac><mrow><mrow><mi>f</mi><mo>''</mo><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>2</mn></msup></mrow><mn>2</mn></mfrac><mo>+</mo><mfrac><mrow><mrow><msup><mi>f</mi><mrow><mo>(</mo><mn>3</mn><mo>)</mo></mrow></msup><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>3</mn></msup></mrow><mn>6</mn></mfrac><mo>+</mo><msup><mrow><mi>O</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mrow><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow></mrow></mrow><mo>)</mo></mrow></mrow><mn>4</mn></msup></mrow>");

	}

	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		mathUtil.toMathML(strEval, stw);
		assertEquals(stw.toString(), "<?xml version=\"1.0\"?>\n"
				+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
				+ "<math mode=\"display\">\n" + strResult + "</math>");

	}

	public void check(IExpr expr, String strResult) {
		StringWriter stw = new StringWriter();
		mathUtil.toMathML(expr, stw);
		assertEquals(stw.toString(), "<?xml version=\"1.0\"?>\n"
				+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
				+ "<math mode=\"display\">\n" + strResult + "</math>");

	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			// F.initSymbols();
			EvalEngine engine = new EvalEngine(true);
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
