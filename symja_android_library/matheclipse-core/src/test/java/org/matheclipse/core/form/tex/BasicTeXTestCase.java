package org.matheclipse.core.form.tex;

import java.io.StringWriter;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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
		check("Sum(a,{x,1,z})", //
				"\\sum_{x = 1}^{z} a");
		check("Sum(a,{x,z})", //
				"\\sum_{x = 1}^{z} a");
		check("Sum(a,{x,1,n},{y,1,m})", //
				"\\sum_{x = 1}^{n}\\sum_{y = 1}^{m} a");
		check("Sum(f,i)", //
				"\\sum_{i} f");
	}

	public void testTeX005() {
		check("Product(a,{x,1,z})", //
				"\\prod_{x = 1}^{z} a");
		check("Product(f,i)", //
				"\\prod_{i} f");
	}

	public void testTeX006() {
		check("Integrate(f(x),y)", //
				"\\int  f(x)\\,\\mathrm{d}y");
	}

	public void testTeX007() {
		check("Integrate(f(x),{x,1,10})", //
				"\\int_{1}^{10} f(x)\\,\\mathrm{d}x");
		check("Integrate(E^(-x),{x,0,Infinity})", //
				"\\int_{0}^{\\infty} {e}^{ - x}\\,\\mathrm{d}x");
	}

	public void testTeX008() {
		check("alpha + beta", //
				"\\alpha+\\beta");
	}

	public void testTeX009() {
		check("Limit(Sin(x),x->0)", //
				"\\lim_{x\\to 0 }\\,{\\sin (x)}");
	}

	public void testTeX010() {
		check("3+x*(4+x*(5+(33+x^2)*x^4))", //
				"3 + x\\,\\left( 4 + x\\,\\left( 5 + \\left( 33+{x}^{2}\\right) \\,{x}^{4}\\right) \\right) ");
	}

	public void testTeX011() {
		check("x^(3/4*y)", //
				"{x}^{\\frac{3\\,y}{4}}");
	}

	public void testTeX012a() {
		// check("{{{1,2,3},{4,5,6}}}", "\\begin{pmatrix} 1 & 2 & 3 \\\\\n" + " 4 & 5 & 6 \\\\\n" + "\\end{pmatrix}");
		check("MatrixForm({{1,2,3},{4,5,6}})", //
				"\\begin{pmatrix}\n 1 & 2 & 3 \\\\\n" + " 4 & 5 & 6 \\\\\n" + "\\end{pmatrix}");
	}

	public void testTeX012b() {
		check("TableForm({1,2,3,4,5,6})", //
				"\\begin{array}{c}\n" + //
						" 1 \\\\\n" + //
						" 2 \\\\\n" + //
						" 3 \\\\\n" + //
						" 4 \\\\\n" + //
						" 5 \\\\\n" + //
						" 6 \n"
						+ "\\end{array}");
	}

	public void testTeX012c() {
		check("TableForm({{1,2,3},{4,5,6}})", //
				"\\begin{array}{ccc}\n" + //
						" 1 & 2 & 3 \\\\\n" + //
						" 4 & 5 & 6 \\\\\n" + //
						"\\end{array}");
	}

	public void testTeX013() {
		check("a*b+c; a*b+c", //
				"a\\,b+c;a\\,b+c");
	}

	public void testTeX014() {
		check("Sin(x)^2", //
				"{\\sin (x)}^{2}");
		check("Sin(2*x)^2", //
				"{\\sin (2\\,x)}^{2}");
	}

	public void testTeX015() {

		check("-I", //
				" - i ");
		check("-I*(1.0)", //
				" - i ");
		check("-1*I", //
				" - i ");
		check("I*(-1)", //
				" - i ");

		check("-I*1.0", //
				" - i ");
		check("-1.0*I", //
				" - i ");
		check("I*(-1.0)", //
				" - i ");

		check("-I*2", //
				"-2\\,i ");
		check("-2*I", //
				"-2\\,i ");
		check("I*(-2)", //
				"-2\\,i ");

		check("-I*2.0", //
				"-2.0\\,i ");
		check("-2.0*I", //
				"-2.0\\,i ");
		check("I*(-2.0)", //
				"-2.0\\,i ");

		check("3-I*2", "3 - 2\\,i ");
		check("4-I*5", "4 - 5\\,i ");
		check("3.0-I*2.0", "3.0 - 2.0\\,i ");
		check("3-I*2", "3 - 2\\,i ");
		check("4-I*5", "4 - 5\\,i ");
		check("Complex(0, -b)", " - b\\,\\imag");
		check("Complex(-a, 0)", " - a");
		check("Complex(-a, 1)", " - a + \\imag");
		check("Complex(-a, -1)", " - a - \\imag");
		check("Complex(a, -b)", "a - b\\,\\imag");
		check("Complex(a, b)", "a + b\\,\\imag");

		check("Complex(3/4,-(6/7))", //
				"\\frac{3}{4} - \\frac{6}{7}\\,i ");
		check("Complex(3/4,-(6/7)*I)", //
				"\\frac{45}{28}");

		check("3/4-(6/7)*I", //
				"\\frac{3}{4} - \\frac{6}{7}\\,i ");
		check("1+2*I", //
				"1 + 2\\,i ");
		check("1-2*I", //
				"1 - 2\\,i ");

		check("1-2*I", //
				"1 - 2\\,i ");
		check("Complex(1,-2*I)", "3");
		check("Complex(3.0, -2.0)", "3.0 - 2.0\\,i ");
		check("Complex(0,-2)", "-2\\,i ");
		check("Complex(0,-1)", " - i ");
	}

	public void testTeX016() {
		check("(a*d+b*c)*d^(-1)*f^(-1)", //
				"\\frac{b\\,c + a\\,d}{d\\,f}");
	}

	public void testTeX017() {
		check("1/4*d^(-1)*f^(-1)", //
				"\\frac{1}{4\\,d\\,f}");
	}

	public void testTeX018() {
		check("1/4*a^2*b^(-1)*f^(-1)", //
				"\\frac{{a}^{2}}{4\\,b\\,f}");
	}

	public void testTeX019() {
		check("n!", //
				"n ! ");
		check("n!!", //
				"n !! ");
	}

	public void testTeX020() {
		check("Hold(++x)", //
				"\\text{Hold}(\\text{++}x)");
		check("Hold(y^2/.x->3)", //
				"\\text{Hold}({y}^{2}\\text{/.}\\,x\\to 3)");
		check("Hold(y^2//.x->3)", //
				"\\text{Hold}({y}^{2}\\text{//.}\\,x\\to 3)");
		check("Hold(1 * 5 * x + 1 * 63)", //
				"\\text{Hold}(5\\,x + 63)");
		check("10*f(x)", //
				"10\\,f(x)");
		check("Hold((5*3)/2)", //
				"\\text{Hold}(\\frac{3\\cdot 5}{2})");
		check("Integrate(8+5*x, {x, 5, 10})", //
				"\\int_{5}^{10} 8 + 5\\,x\\,\\mathrm{d}x");
	}

	public void testTeX021() {
		check("\\[Alpha]", //
				"\\alpha");
		check("-Infinity", //
				" - \\infty");
		check("GoldenRatio", //
				"\\phi");
		check("Infinity", //
				"\\infty");

		check("EulerGamma", //
				"\\gamma");
		check("Pi", //
				"\\pi");
		check("E", //
				"e");

		// check("Catalan", "");
		// check("Degree", "");
		//
		//
		// check("Glaisher", "");
		// check("I", "");
		//
		// check("Khinchin", "");

	}

	public void testTeX022() {
		// issue #116
		check("-I*1/2*Sqrt(2)", //
				"\\frac{ - i }{\\sqrt{2}}");
	}

	public void testTeX023() {
		// issue #117
		check("5*3^(5*x)*Log(3)", //
				"5\\cdot {3}^{5\\,x}\\,\\log (3)");
	}

	public void testTeX024() {
		check("\"a hello { % # } _&$ world string\"", //
				"\\textnormal{a hello \\{ \\% \\# \\} \\_\\&\\$ world string}");
		check("\"hello\nthis is & and < to > \\\" world\"", //
				"\\textnormal{hello\n" + //
						"this is \\& and $<$ to $>$ \" world}");
	}

	public void testTeX025() {
		StringWriter stw = new StringWriter();
		TeXUtilities localTexUtil = new TeXUtilities(EvalEngine.get(), true, 5, 7);
		localTexUtil.toTeX("1.3 - 1.0", stw);
		assertEquals(stw.toString(), "0.3");
	}

	public void testTeX026() {
		check("DirectedEdge(a,b)", //
				"a\\to b");
		check("UndirectedEdge(a,b)", //
				"a\\leftrightarrow b");
		check("Graph({1,2,3},{1<->2,2<->3})", //
				"\\text{Graph}(\\{1,2,3\\},\\{1\\leftrightarrow 2,2\\leftrightarrow 3\\})");
		check("Graph({1,2,3},{1->2,2->3})", //
				"\\text{Graph}(\\{1,2,3\\},\\{1\\to 2,2\\to 3\\})");
	}

	public void testTeX027() {
		check(new ASTRealMatrix(new double[][] { { 1.0, 2.0, 3.0 }, { 3.3, 4.4, 5.5 } }, false), //
				"\\begin{pmatrix}\n" + //
						" 1.0 & 2.0 & 3.0 \\\\\n" + //
						" 3.3 & 4.4 & 5.5 \\\\\n" + //
						"\\end{pmatrix}");
	}

	public void testTeX028() {
		check(new ASTRealVector(new double[] { 1.0, 2.0, 3.0 }, false), //
				"\\{1.0,2.0,3.0\\}");
	}

	public void testTeX029() {
		check("Inequality(c,Greater,0,GreaterEqual,a)", //
				"c > 0\\geq a");

		check("Inequality(a,Less,0,LessEqual,b, Equal,c, Unequal,d)", //
				"a < 0\\leq b == c\\neq d");

	}

	public void testTeX30() {
		check("Quantity(3,\"m\")", //
				"\\text{Quantity}(3,\\textnormal{m})");
	}

	public void testTeX31() {
		check("a&&b||c", //
				"a \\land b \\lor c");
	}

	public void testTeX32() {
		check("{{a,b,c},{a,c,b},{c,a,b}}", //
				"\\{\\{a,b,c\\},\\{a,c,b\\},\\{c,a,b\\}\\}");
	}

	public void testTeX033() {
		IExpr expr = EvalEngine.get().evaluate("2.7*6");
		check(expr, //
				"16.2");
	}

	public void testTeX034() {
		IExpr expr = EvalEngine.get().evaluate("ComplexInfinity");
		check(expr, //
				"ComplexInfinity");
	}

	public void testTeX035() {
		IExpr expr = EvalEngine.get().evaluate("a[[1]]");
		check(expr, //
				"a[[1]]");
		expr = EvalEngine.get().evaluate("test[[1,2,3]]");
		check(expr, //
				"\\text{test}[[1,2,3]]");
	}

	public void testTeX036() {
		try {
			IExpr expr = new EvalEngine(true).evaluate("HoldForm(f(x,y))");
			check(expr, //
					"f(x,y)");
			expr = new EvalEngine(true).evaluate("Defer(f(x,y))");
			check(expr, //
					"f(x,y)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testTeX037() {
		IExpr expr = EvalEngine.get().evaluate("f(#,#3,##)");
		check(expr, //
				"f(\\text{$\\#$1},\\text{$\\#$3},\\text{$\\#\\#$1})");
	}

	public void testTeX038() {
		// gitlab #108
		IExpr expr = EvalEngine.get().evaluate("Solve({Log10(x)==21.69},{x})");
		check(expr, //
				"\\{\\{x\\to 4.89779*10^{21}\\}\\}");
	}

	public void testTeX039() {
		// gitlab #108
		IExpr expr = EvalEngine.get().evaluate("Superscript(2,10)");
		check(expr, //
				"{2}^{10}");
	}

	public void testTeX040() {
		IExpr expr = EvalEngine.get().evaluate("Subscript(\"zzz\",36)");
		check(expr, //
				"{\\textnormal{zzz}}_{36}");
	}

	public void testTeX041() {
		IExpr expr = EvalEngine.get().evaluate("Interval({-3.21625*10^-16,5.66554*10^-16})");
		check(expr, //
				"Interval(\\{-3.21625*10^{-16},5.66554*10^{-16}\\})");
	}

	public void testTeX042() {
		IExpr expr = EvalEngine.get().evaluate("Cot(Interval({3*Pi/4,6*Pi/5}))");
		check(expr, //
				"Interval(\\{-\\infty,-1\\},\\{\\sqrt{\\left( 1+\\frac{2}{\\sqrt{5}}\\right) },\\infty\\})");
	}

	public void testTeX043() {
		IExpr expr = EvalEngine.get().evaluate("<|a -> x, b -> y, c -> z|>");
		check(expr, //
				"\\langle|a\\to x,b\\to y,c\\to z|\\rangle");
	}

	public void testTeX044() {
		IExpr expr = EvalEngine.get().evaluate("{{{a->b},{c:>d}},{4,5,6}}");
		((IAST) expr).setEvalFlags(IAST.OUTPUT_MULTILINE);
		check(expr, //
				"\\begin{array}{c}\n" + //
						" \\{\\{a\\to b\\},\\{c:\\to d\\}\\} \\\\\n" + //
						" \\{4,5,6\\} \n" + //
						"\\end{array}");
	}

	public void check(String strEval, String strResult) {
		StringWriter stw = new StringWriter();
		texUtil.toTeX(strEval, stw);
		assertEquals(stw.toString(), strResult);

	}

	public void check(IExpr expr, String strResult) {
		StringWriter stw = new StringWriter();
		texUtil.toTeX(expr, stw);
		assertEquals(stw.toString(), strResult);

	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			// F.initSymbols();
			EvalEngine engine = new EvalEngine(true);
			texUtil = new TeXUtilities(engine, true, 5, 7);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
