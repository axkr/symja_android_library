package org.matheclipse.io.form.tex;

import static org.junit.Assert.assertEquals;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.operator.Precedence;

/** Tests LaTeX export function */
public class BasicTeXTestCase {

  TeXUtilities texUtil;

  // pubLIC BASICTEXTESTCASE(STRING NAME) {
  // SUPER(NAME);
  // }

  @Test
  public void testTeX001() {
    check("-0.0", //
        "0.0");
    check("0.0", //
        "0.0");
    check("a*b", //
        "a \\cdot b");
  }

  @Test
  public void testTeX002() {
    check("a*b+c", //
        "a \\cdot b + c");
  }

  @Test
  public void testTeX003() {
    check("1/3", //
        "\\frac{1}{3}");
    check("theta", //
        "\\theta");
    check("Theta", //
        "\\theta");
  }

  @Test
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

  @Test
  public void testTeX005() {
    check("Product(a,{x,1,z})", //
        "\\prod_{x = 1}^{z} a");
    check("Product(f,i)", //
        "\\prod_{i} f");
  }

  @Test
  public void testTeX006() {
    check("Integrate(f(x),y)", //
        "\\int  f(x)\\,\\mathrm{d}y");
  }

  @Test
  public void testTeX007() {
    check("Integrate(f(x),{x,1,10})", //
        "\\int_{1}^{10} f(x)\\,\\mathrm{d}x");
    check("Integrate(E^(-x),{x,0,Infinity})", //
        "\\int_{0}^{\\infty} {e}^{\\left(  - x\\right) }\\,\\mathrm{d}x");
  }

  @Test
  public void testTeX008() {
    check("alpha + beta", //
        "\\alpha + \\beta");
  }

  @Test
  public void testTeX009() {
    check("Limit(Sin(x),x->0)", //
        "\\lim_{x\\to 0 }\\,{\\sin (x)}");
  }

  @Test
  public void testTeX010() {
    check("3+x*(4+x*(5+(33+x^2)*x^4))", //
        "3 + x \\cdot \\left( 4 + x \\cdot \\left( 5 + \\left( 33 + {x}^{2}\\right)  \\cdot {x}^{4}\\right) \\right) ");
  }

  @Test
  public void testTeX011() {
    check("x^(3/4*y)", //
        "{x}^{\\left( \\frac{3}{4} \\cdot y\\right) }");
  }

  @Test
  public void testTeX012a() {
    check(F.MatrixForm(F.List(F.List(1, 2, 3), F.List(3, 4, 5))), //
        "\\left(\n" + //
            "\\begin{array}{ccc}\n" + //
            "1 & 2 & 3 \\\\\n" + //
            "3 & 4 & 5 \\\n" + //
            "\\\\\n" + //
            "\\end{array}\n" + //
            "\\right) ");
  }

  @Test
  public void testTeX012b() {
    check("TableForm({1,2,3,4,5,6})", //
        "\\begin{array}{c}\n" + //
            " 1 \\\\\n" + //
            " 2 \\\\\n" + //
            " 3 \\\\\n" + //
            " 4 \\\\\n" + //
            " 5 \\\\\n" + //
            " 6 \n" + "\\end{array}");
  }

  @Test
  public void testTeX012c() {
    check("TableForm({{1,2,3},{4,5,6}})", //
        "\\begin{array}{ccc}\n" + //
            " 1 & 2 & 3 \\\\\n" + //
            " 4 & 5 & 6 \\\\\n" + //
            "\\end{array}");
  }

  @Test
  public void testTeX013() {
    check("a*b+c; a*b+c", //
        "a \\cdot b + c;a \\cdot b + c");
  }

  @Test
  public void testTeX014() {
    check("Sin(x)^2", //
        "{\\sin (x)}^{2}");
    check("Sin(2*x)^2", //
        "{\\sin (2 \\cdot x)}^{2}");
  }

  @Test
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

  @Test
  public void testTeX016() {
    check("(a*d+b*c)*d^(-1)*f^(-1)", //
        "\\frac{b \\cdot c + a \\cdot d}{d \\cdot f}");
  }

  @Test
  public void testTeX017() {
    check("1/4*d^(-1)*f^(-1)", //
        "\\frac{1}{4 \\cdot d \\cdot f}");
  }

  @Test
  public void testTeX018() {
    check("1/4*a^2*b^(-1)*f^(-1)", //
        "\\frac{{a}^{2}}{4 \\cdot b \\cdot f}");
  }

  @Test
  public void testTeX019() {
    check("n!", //
        "n ! ");
    check("n!!", //
        "n !! ");
  }

  @Test
  public void testTeX020() {
    check("Integrate(8+5*x, {x, 5, 10})", //
        "\\int_{5}^{10} 8+5 \\cdot x\\,\\mathrm{d}x");
    check("Hold(1 * 5 * x + 1 * 63)", //
        "\\text{Hold}(1 \\cdot 5 \\cdot x + 1 \\cdot 63)");
    check("Hold(++x)", //
        "\\text{Hold}(\\text{++}x)");
    check("Hold(y^2/.x->3)", //
        "\\text{Hold}({y}^{2}\\text{/.}\\,x\\to 3)");
    check("Hold(y^2//.x->3)", //
        "\\text{Hold}({y}^{2}\\text{//.}\\,x\\to 3)");

    check("10*f(x)", //
        "10 \\cdot f(x)");
    check("Hold((5*3)/2)", //
        "\\text{Hold}(\\frac{1}{2} \\cdot 3 \\cdot 5)");
  }

  @Test
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

  @Test
  public void testTeX022() {
    check("-I*1/2*Sqrt(2)", //
        " - \\frac{i }{\\sqrt{2}}");
  }

  @Test
  public void testTeX023() {
    // issue #117
    check("5*3^(5*x)*Log(3)", //
        "5 \\cdot {3}^{\\left( 5 \\cdot x\\right) } \\cdot \\log (3)");
  }

  @Test
  public void testTeX024() {
    check("\"a hello { % # } _&$ world string\"", //
        "\\textnormal{a hello \\{ \\% \\# \\} \\_\\&\\$ world string}");
    check("\"hello\nthis is & and < to > \\\" world\"", //
        "\\textnormal{hello\n" + //
            "this is \\& and $<$ to $>$ \" world}");
  }

  @Test
  public void testTeX025() {
    StringWriter stw = new StringWriter();
    TeXUtilities localTexUtil = new TeXUtilities(EvalEngine.get(), true);
    localTexUtil.toTeX("1.3 - 1.0", stw, true);
    assertEquals(stw.toString(), "0.3");
  }

  @Test
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

  @Test
  public void testTeX027() {
    check(new ASTRealMatrix(new double[][] {{1.0, 2.0, 3.0}, {3.3, 4.4, 5.5}}, false), //
        "\\left(\n" //
            + "\\begin{array}{ccc}\n"//
            + "1.0 & 2.0 & 3.0 \\\\\n"//
            + "3.3 & 4.4 & 5.5 \\\\\n"//
            + "\\end{array}\n"//
            + "\\right) ");
  }

  @Test
  public void testTeX028() {
    check(new ASTRealVector(new double[] {1.0, 2.0, 3.0}, false), //
        "\\{1.0,2.0,3.0\\}");
  }

  @Test
  public void testTeX029() {
    check("Inequality(c,Greater,0,GreaterEqual,a)", //
        "c > 0\\geq a");

    check("Inequality(a,Less,0,LessEqual,b, Equal,c, Unequal,d)", //
        "a < 0\\leq b == c\\neq d");
  }

  @Test
  public void testTeX30() {
    check("Quantity(3,\"m\")", //
        "\\text{Quantity}(3,\\textnormal{m})");
  }

  @Test
  public void testTeX31() {
    check("a&&b||c", //
        "\\left( a \\land b\\right)  \\lor c");
  }

  @Test
  public void testTeX32() {
    check("{{a,b,c},{a,c,b},{c,a,b}}", //
        "\\left(\n" //
            + "\\begin{array}{ccc}\n" //
            + "a & b & c \\\\\n" //
            + "a & c & b \\\\\n" //
            + "c & a & b \\\\\n" //
            + "\\end{array}\n" //
            + "\\right) ");
  }

  @Test
  public void testTeX033() {
    IExpr expr = EvalEngine.get().evaluate("2.7*6");
    check(expr, //
        "16.2");
  }

  @Test
  public void testTeX034() {
    IExpr expr = EvalEngine.get().evaluate("ComplexInfinity");
    check(expr, //
        "ComplexInfinity");
  }

  @Test
  public void testTeX035() {
    IExpr expr = EvalEngine.get().evaluate("a[[1]]");
    check(expr, //
        "a[[1]]");
    expr = EvalEngine.get().evaluate("test[[1,2,3]]");
    check(expr, //
        "\\text{test}[[1,2,3]]");
  }

  @Test
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

  @Test
  public void testTeX037() {
    IExpr expr = EvalEngine.get().evaluate("f(#,#3,##)");
    check(expr, //
        "f(\\text{$\\#$1},\\text{$\\#$3},\\text{$\\#\\#$1})");
  }

  @Test
  public void testTeX038() {
    // gitlab #108
    IExpr expr = EvalEngine.get().evaluate("Solve({Log10(x)==21.69},{x})");
    check(expr, //
        "\\{\\{x\\to 4.89779*10^{21}\\}\\}");
  }

  @Test
  public void testTeX039() {
    // gitlab #108
    IExpr expr = EvalEngine.get().evaluate("Superscript(2,10)");
    check(expr, //
        "{2}^{10}");
  }

  @Test
  public void testTeX040() {
    IExpr expr = EvalEngine.get().evaluate("Subscript(\"zzz\",36)");
    check(expr, //
        "{\\textnormal{zzz}}_{36}");
    expr = EvalEngine.get().evaluate("Subscript(a,1,2,3)");
    check(expr, //
        "{a}_{1,2,3}");
  }

  @Test
  public void testTeX041() {
    IExpr expr = EvalEngine.get().evaluate("Interval({-3.21625*10^-16,5.66554*10^-16})");
    check(expr, //
        "Interval(\\{-3.21625*10^{-16},5.66554*10^{-16}\\})");
  }

  @Test
  public void testTeX042() {
    IExpr expr = EvalEngine.get().evaluate("Cot(Interval({3*Pi/4,6*Pi/5}))");
    check(expr, //
        "Interval(\\{-\\infty,-1\\},\\{\\sqrt{\\left( 1 + \\frac{2}{\\sqrt{5}}\\right) },\\infty\\})");
  }

  @Test
  public void testTeX043() {
    IExpr expr = EvalEngine.get().evaluate("<|a -> x, b -> y, c -> z|>");
    check(expr, //
        "\\langle|a\\to x,b\\to y,c\\to z|\\rangle");
  }

  @Test
  public void testTeX044() {
    IExpr expr = EvalEngine.get().evaluate("{{{a->b},{c:>d}},{4,5,6}}");
    ((IAST) expr).setEvalFlags(IAST.OUTPUT_MULTILINE);
    check(expr, //
        "\\begin{array}{c}\n" + //
            " \\{\\{a\\to b\\},\\{c:\\to d\\}\\} \\\\\n" + //
            " \\{4,5,6\\} \n" + //
            "\\end{array}");
  }

  @Test
  public void testTeX045() {
    IExpr expr = EvalEngine.get().evaluate("Derivative(2)[10/x^4]");
    check(expr, //
        "\\left( \\frac{10}{{x}^{4}}\\right) ''");
  }

  @Test
  public void testTeX046() {
    IExpr expr = EvalEngine.get().evaluate("Derivative(a)[10/x^4]");
    check(expr, //
        "\\left( \\frac{10}{{x}^{4}}\\right) ^{(a)}");
  }

  @Test
  public void testTeX047() {
    IExpr expr = EvalEngine.get().evaluate("Log(b,z)");
    check(expr, //
        "\\frac{\\log (z)}{\\log (b)}");
  }

  @Test
  public void testTeX048() {
    IExpr expr = EvalEngine.get().evaluate("s^(-2)");
    check(expr, //
        "\\frac{1}{{s}^{2}}");
  }

  @Test
  public void testTeX049() {
    IExpr expr = EvalEngine.get().evaluate("Conjugate(x)");
    check(expr, //
        "x^*");
  }

  @Test
  public void testTeX050() {
    StringBuilder sb = new StringBuilder();
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    fTeXFactory.convert(sb, F.symjify(-2).multiply(F.symjify("x")), Precedence.NO_PRECEDENCE);
    Assertions.assertEquals("-2 \\cdot x", sb.toString());
  }

  @Test
  public void testTeX050_TimesOperator() {
    StringBuilder sb = new StringBuilder();
    TeXFormFactory fTeXFactory = new TeXFormFactory(" \\, ");
    fTeXFactory.convert(sb, F.symjify(-2).multiply(F.symjify("x")), Precedence.NO_PRECEDENCE);
    Assertions.assertEquals("-2 \\, x", sb.toString());
  }

  @Test
  public void testTeX050_TimesPower() {
    StringBuilder sb = new StringBuilder();
    TeXFormFactory fTeXFactory = new TeXFormFactory(" \\, ");
    fTeXFactory.convert(sb, F.symjify(-2).multiply(F.symjify("x")).power(2),
        Precedence.NO_PRECEDENCE);
    Assertions.assertEquals("{\\left( -2 \\, x\\right) }^{2}", sb.toString());
  }

  @Test
  public void testTeX051() {
    StringBuilder sb = new StringBuilder();
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    fTeXFactory.convert(sb, F.symjify("x").add(F.symjify(-2).divide(F.symjify(4))),
        Precedence.NO_PRECEDENCE);
    Assertions.assertEquals("-\\frac{1}{2} + x", sb.toString());
  }

  @Test
  public void testMatrixForm() {
    IExpr expr = EvalEngine.get().evaluate(
        "SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}) // MatrixForm");
    check(expr, //
        "\\left(\n" + "\\begin{array}{ccc}\n" + "1 & 0 & 4 \\\\\n" + "0 & 2 & 0 \\\\\n"
            + "0 & 0 & 3 \\\n" + "\\\\\n" + "\\end{array}\n" + "\\right) ");
  }

  @Test
  public void testPower10() {
    StringBuilder sb = new StringBuilder();
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    fTeXFactory.convert(sb, F.symjify("x").power(10), 0);
    Assertions.assertEquals("{x}^{10}", sb.toString());
  }

  @Test
  public void testStyle() {
    IExpr expr = EvalEngine.get().parse("Style(1/Sqrt(7), Red)");
    check(expr, //
        "\\textcolor{red}{\\frac{1}{\\sqrt{7}}}");
  }

  public void check(String strEval, String strResult) {
    StringWriter stw = new StringWriter();
    texUtil.toTeX(strEval, stw, true);
    assertEquals(stw.toString(), strResult);
  }

  public void check(IExpr expr, String strResult) {
    StringWriter stw = new StringWriter();
    texUtil.toTeX(expr, stw);
    assertEquals(stw.toString(), strResult);
  }

  /** The JUnit setup method */
  @Before
  public void setUp() {
    try {
      // F.initSymbols();
      EvalEngine engine = new EvalEngine(true);
      texUtil = new TeXUtilities(engine, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
