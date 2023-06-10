package org.matheclipse.io.form.mathml;

import java.io.StringWriter;
import org.apfloat.Apcomplex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import junit.framework.TestCase;

/** Tests MathML presentation function */
public class MathMLPresentationTestCase extends TestCase {

  MathMLUtilities mathUtil;

  public MathMLPresentationTestCase(String name) {
    super(name);
  }

  /** Test mathml function */
  public void testMathMLPresentation() {
    check("TableForm({a,b,c,d})", //
        "<mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable>");

    check("TableForm({{a,b},{c,d}})", //
        "<mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable>");

    // check("-a-b*I",
    // "<mrow><mo>-</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>b</mi></mrow><mrow><mo>-</mo><mi>a</mi></mrow></mrow>");
    IExpr expr = EvalEngine.get().evaluate("-1/2-3/4*I");
    check(expr, //
        "<mrow><mrow><mo>-</mo><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mrow><mo>-</mo><mrow><mfrac><mn>3</mn><mn>4</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");
    expr = EvalEngine.get().evaluate("1/2+3/4*I");
    check(expr,
        "<mrow><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow><mrow><mo>+</mo><mrow><mfrac><mn>3</mn><mn>4</mn></mfrac></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow></mrow>");

    check("-1/2-3/4*I", //
        "<mrow><mfrac><mrow><mrow><mo>(</mo><mn>-3</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mn>4</mn></mfrac><mo>-</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></mrow>");
    check("1/2+3/4*I", //
        "<mrow><mfrac><mrow><mn>3</mn><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mn>4</mn></mfrac><mo>+</mo><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></mrow>");

    check("\"hello\nworld\"",
        "<mtext>hello</mtext><mspace linebreak='newline' /><mtext>world</mtext><mspace linebreak='newline' />");
    check("\"hello\nthis is & and < to > \\\" world\"",
        "<mtext>hello</mtext><mspace linebreak='newline' /><mtext>this&nbsp;is&nbsp;&amp;&nbsp;and&nbsp;&lt;&nbsp;to&nbsp;&gt;&nbsp;&quot;&nbsp;world</mtext><mspace linebreak='newline' />");

    check("x /.y", "<mrow><mi>x</mi><mo>/.</mo><mi>y</mi></mrow>");
    check("f(x_,y_):={x,y}/;x>y", //
        "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mtext>x_</mtext><mo>,</mo><mtext>y_</mtext></mrow><mo>)</mo></mrow></mrow><mo>:=</mo><mrow><mrow><mo>{</mo><mrow><mi>x</mi><mo>,</mo><mi>y</mi></mrow><mo>}</mo></mrow><mo>/;</mo><mrow><mi>x</mi><mo>&gt;</mo><mi>y</mi></mrow></mrow></mrow>");

    check("f(x)&", //
        "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow>");
    check("f(x, #)&[y]", //
        "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi><mo>,</mo><mi>#1</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow><mo>[</mo><mi>y</mi><mo>]</mo>");

    check("f(x)[y][z]", //
        "<mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>[</mo><mi>y</mi><mo>]</mo><mo>[</mo><mi>z</mi><mo>]</mo>");
    check("f'(x)", //
        "<mrow><msup><mi>f</mi><mo>&#8242;</mo></msup><mrow><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow>");
    check(F.Slot1, //
        "<mi>#1</mi>");
    check(F.SlotSequence(2), //
        "<mi>##2</mi>");
    Apcomplex c = new Apcomplex("(-0.5,-4.0)");
    check(F.complexNum(c), //
        "<mrow><mn>-0.5</mn><mo>-</mo><mn>4</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
    check(F.complexNum(-0.5, -4.0), //
        "<mrow><mn>-0.5</mn><mo>-</mo><mn>4.0</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
    check(F.pattern(F.x), //
        "<mtext>x_</mtext>");
    check(F.complexNum(0.5, 4.0), //
        "<mrow><mn>0.5</mn><mo>+</mo><mn>4.0</mn><mo>&#0183;</mo><mi>&#x2148;</mi></mrow>");
    check(F.complexNum(-0.5, -4.0), //
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
    check("DirectedInfinity()", "<mi>ComplexInfinity</mi>");
    check("DirectedInfinity(-1)", "<mrow><mo>-</mo><mi>&#x221E;</mi></mrow>");
    check("DirectedInfinity(I)",
        "<mrow><mrow><mi>&#x2148;</mi></mrow><mo>&#0183;</mo><mi>Infinity</mi></mrow>");
    check("DirectedInfinity(-I)",
        "<mrow><mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>Infinity</mi></mrow>");
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
    check("Abs(-x)",
        "<mrow><mo>&#10072;</mo><mrow><mo>-</mo><mi>x</mi></mrow><mo>&#10072;</mo></mrow>");
    check("a*b*c*d",
        "<mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi><mo>&#0183;</mo><mi>c</mi><mo>&#0183;</mo><mi>d</mi></mrow>");
    check("k/2", "<mfrac><mi>k</mi><mn>2</mn></mfrac>");
    check("Binomial(n,k/2)",
        "<mrow><mo>(</mo><mfrac linethickness=\"0\"><mi>n</mi><mfrac><mi>k</mi><mn>2</mn></mfrac></mfrac><mo>)</mo></mrow>");

    check("a*b+c",
        "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow></mrow>");
    // check("HEllipsis", "<mo>&hellip;</mo>");
    check("MatrixForm({a,b,c,d})",
        "<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

    check("MatrixForm({{a,b},{c,d}})",
        "<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mi>a</mi></mtd><mtd columnalign=\"center\"><mi>b</mi></mtd></mtr><mtr><mtd columnalign=\"center\"><mi>c</mi></mtd><mtd columnalign=\"center\"><mi>d</mi></mtd></mtr></mtable><mo>)</mo></mrow>");

    check("a*b+c",
        "<mrow><mi>c</mi><mo>+</mo><mrow><mi>a</mi><mo>&#0183;</mo><mi>b</mi></mrow></mrow>");
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
    check("4*x+4",
        "<mrow><mn>4</mn><mo>+</mo><mrow><mn>4</mn><mo>&#0183;</mo><mi>x</mi></mrow></mrow>");

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
    check("-0.0", //
        "<mn>0.0</mn>");
    check("0.0", //
        "<mn>0.0</mn>");
    check("Catalan", //
        "<mi>C</mi>");
    check("Pi", //
        "<mi>&#x03C0;</mi>");
    check("Infinity", //
        "<mi>&#x221E;</mi>");
    check("-Infinity", //
        "<mrow><mo>-</mo><mi>&#x221E;</mi></mrow>");
  }

  public void testMathML002() {
    IExpr expr = EvalEngine.get().evaluate("-1/2*Sqrt(1/2)*Sqrt(5+Sqrt(5))");
    check(expr,
        "<mfrac><mrow><mo>-</mo><msqrt><mrow><msqrt><mn>5</mn></msqrt><mo>+</mo><mn>5</mn></mrow></msqrt></mrow><mrow><mn>2</mn><mo>&#0183;</mo><msqrt><mn>2</mn></msqrt></mrow></mfrac>");

    // (-1/3+I)*a
    expr = EvalEngine.get().evaluate("a*((- 1/3 )+x)");
    check(expr,
        "<mrow><mi>a</mi><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mo>-</mo><mrow><mfrac><mn>1</mn><mn>3</mn></mfrac></mrow><mo>)</mo></mrow></mrow></mrow>");
    expr = EvalEngine.get().evaluate("a*((- 1/3 )*I)");
    check(expr,
        "<mfrac><mrow><mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>a</mi></mrow><mn>3</mn></mfrac>");

    expr = EvalEngine.get().evaluate("a*((- 1/3 )+I)");
    System.out.println(expr.toString());
    check(expr,
        "<mfrac><mrow><mrow><mo>(</mo><mrow><mo>-</mo><mn>1</mn></mrow><mrow><mo>+</mo><mrow><mn>3</mn></mrow><mo>&#0183;</mo><mi>&#x2148;</mi></mrow><mo>)</mo></mrow><mo>&#0183;</mo><mi>a</mi></mrow><mn>3</mn></mfrac>");

    // (-I*a)^x
    expr = EvalEngine.get().evaluate("(a*(-I))^x");
    System.out.println(expr.toString());
    check(expr,
        "<msup><mrow><mrow><mo>(</mo><mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>a</mi><mo>)</mo></mrow></mrow><mi>x</mi></msup>");

    // -I*1/2*Pi
    expr = EvalEngine.get().evaluate("ArcTanh(Infinity)");
    System.out.println(expr.toString());
    check(expr, //
        "<mfrac><mrow><mrow><mrow><mo>-</mo><mi>&#x2148;</mi></mrow></mrow><mo>&#0183;</mo><mi>&#x03C0;</mi></mrow><mn>2</mn></mfrac>");

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

  public void testMathML003() {
    check("a/(b*c*Log[F]*((-b*Log[F])/e)^m)", //
        "<mfrac><mi>a</mi><mrow><mi>b</mi><mo>&#0183;</mo><mi>c</mi><mo>&#0183;</mo><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>F</mi><mo>)</mo></mrow><mo>&#0183;</mo><msup><mrow><mo>(</mo><mfrac><mrow><mo>-</mo><mi>b</mi><mo>&#0183;</mo><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>F</mi><mo>)</mo></mrow></mrow><mi>e</mi></mfrac><mo>)</mo></mrow><mi>m</mi></msup></mrow></mfrac>");
  }

  public void testC1() {
    IExpr expr = EvalEngine.get().evaluate("C(1)");
    check(expr, //
        "<msub><mi>c</mi><mn>1</mn></msub>");
  }

  public void testAssociation() {
    IExpr expr = EvalEngine.get().evaluate("<|a -> x, b -> y, c -> z|>");
    check(expr, //
        "<mrow><mo>&lt;|</mo><mrow><mrow><mi>a</mi><mo>-&gt;</mo><mi>x</mi></mrow><mo>,</mo><mrow><mi>b</mi><mo>-&gt;</mo><mi>y</mi></mrow><mo>,</mo><mrow><mi>c</mi><mo>-&gt;</mo><mi>z</mi></mrow></mrow><mo>|&gt;</mo></mrow>");
  }

  public void testCeiling() {
    IExpr expr = EvalEngine.get().evaluate("Ceiling(f(x))");
    check(expr, //
        "<mrow><mo>&#x2308;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>&#x2309;</mo></mrow>");
  }

  public void testFactorial2() {
    IExpr expr = EvalEngine.get().evaluate("f(x)!!");
    check(expr, //
        "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>!!</mo></mrow>");
  }

  public void testN() {
    IExpr expr = EvalEngine.get().evaluate("N(E,30)");
    check(expr, //
        "<mn>2.71828182845904523536028747135</mn>");
  }

  public void testOrAnd() {
    IExpr expr = EvalEngine.get().evaluate("a&&c||a&&d||b&&c||b&&d");
    check(expr, //
        "<mrow><mrow><mo>(</mo><mrow><mi>a</mi><mo>&#x2227;</mo><mi>c</mi></mrow><mo>)</mo></mrow><mo>&#x2228;</mo><mrow><mo>(</mo><mrow><mi>a</mi><mo>&#x2227;</mo><mi>d</mi></mrow><mo>)</mo></mrow><mo>&#x2228;</mo><mrow><mo>(</mo><mrow><mi>b</mi><mo>&#x2227;</mo><mi>c</mi></mrow><mo>)</mo></mrow><mo>&#x2228;</mo><mrow><mo>(</mo><mrow><mi>b</mi><mo>&#x2227;</mo><mi>d</mi></mrow><mo>)</mo></mrow></mrow>");
  }

  public void testNot001() {
    IExpr expr = EvalEngine.get().evaluate("!f(x)");
    check(expr, //
        "<mrow><mo>&#x00AC;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow></mrow>");
  }

  public void testNot002() {
    IExpr expr = EvalEngine.get().evaluate("!(x&&x)");
    check(expr, //
        "<mrow><mo>&#x00AC;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mo>&#x2227;</mo><mi>x</mi><mo>)</mo></mrow></mrow></mrow>");
  }

  public void testRational() {
    IExpr expr = EvalEngine.get().parse("Rational(a, b)");
    check(expr, //
        "<mfrac><mi>a</mi><mi>b</mi></mfrac>");
  }

  public void testSqrt() {
    IExpr expr = EvalEngine.get().parse("Sqrt(f(x))");
    check(expr, //
        "<msqrt><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow></msqrt>");
  }

  public void testFactorial() {
    IExpr expr = EvalEngine.get().evaluate("Factorial(Factorial(x))");
    check(expr, //
        "<mrow><mrow><mo>(</mo><mi>x</mi><mo>!</mo><mo>)</mo></mrow><mo>!</mo></mrow>");
  }

  public void testFloor() {
    IExpr expr = EvalEngine.get().evaluate("Floor(f(x))");
    check(expr, //
        "<mrow><mo>&#x230A;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow><mo>&#x230B;</mo></mrow>");
  }

  public void testDerivative001() {
    IExpr expr = EvalEngine.get().evaluate("1/f''(x)");
    check(expr,
        "<mfrac><mn>1</mn><mrow><msup><mi>f</mi><mo>&#8242;&#8242;</mo></msup><mrow><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow></mfrac>");
  }

  public void testDerivative002() {
    IExpr expr = EvalEngine.get().evaluate("Derivative(2)[10/x^4]");
    check(expr, //
        "<mrow><msup><mrow><mo>(</mo><mfrac><mn>10</mn><msup><mi>x</mi><mn>4</mn></msup></mfrac><mo>)</mo></mrow><mo>&#8242;&#8242;</mo></msup></mrow>");
  }

  public void testDerivative003() {
    IExpr expr = EvalEngine.get().evaluate("Derivative(a)[10/x^4]");
    check(expr, //
        "<mrow><msup><mrow><mo>(</mo><mfrac><mn>10</mn><msup><mi>x</mi><mn>4</mn></msup></mfrac><mo>)</mo></mrow><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></msup></mrow>");
  }

  public void testElement() {
    IExpr expr = EvalEngine.get().evaluate("Element[a, Integers]");
    check(expr, //
        "<mrow><mi>a</mi><mo>&#8712;</mo><mi>&#8484;</mi></mrow>");
    expr = EvalEngine.get().evaluate("Element[a, Complexes]");
    check(expr, //
        "<mrow><mi>a</mi><mo>&#8712;</mo><mi>&#8450;</mi></mrow>");
    expr = EvalEngine.get().evaluate("Element[a, Rationals]");
    check(expr, //
        "<mrow><mi>a</mi><mo>&#8712;</mo><mi>&#8474;</mi></mrow>");
    expr = EvalEngine.get().evaluate("Element[a, Reals]");
    check(expr, //
        "<mrow><mi>a</mi><mo>&#8712;</mo><mi>&#8477;</mi></mrow>");
  }

  public void testSeries001() {
    IExpr expr = EvalEngine.get().evaluate("Series(f(x),{x,a,3})");
    check(expr,
        "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mo>+</mo><mrow><mrow><msup><mi>f</mi><mo>&#8242;</mo></msup><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow></mrow><mo>+</mo><mfrac><mrow><mrow><msup><mi>f</mi><mo>&#8242;&#8242;</mo></msup><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>2</mn></msup></mrow><mn>2</mn></mfrac><mo>+</mo><mfrac><mrow><mrow><msup><mi>f</mi><mrow><mo>(</mo><mn>3</mn><mo>)</mo></mrow></msup><mrow><mo>(</mo><mi>a</mi><mo>)</mo></mrow></mrow><mo>&#0183;</mo><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>3</mn></msup></mrow><mn>6</mn></mfrac><mo>+</mo><msup><mrow><mi>O</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mrow><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow></mrow></mrow><mo>)</mo></mrow></mrow><mn>4</mn></msup></mrow>");
  }

  public void testSeries002() {
    IExpr expr = EvalEngine.get().evaluate("Series(Log(x),{x,a,3})");
    check(expr,
        "<mrow><mrow><mi>log</mi><mo>&#x2061;</mo><mo>(</mo><mi>a</mi><mo>)</mo></mrow><mo>+</mo><mfrac><mrow><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow></mrow><mi>a</mi></mfrac><mo>+</mo><mfrac><mrow><mo>-</mo><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>2</mn></msup></mrow><mrow><mn>2</mn><mo>&#0183;</mo><msup><mi>a</mi><mn>2</mn></msup></mrow></mfrac><mo>+</mo><mfrac><msup><mrow><mrow><mo>(</mo><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow><mo>)</mo></mrow></mrow><mn>3</mn></msup><mrow><mn>3</mn><mo>&#0183;</mo><msup><mi>a</mi><mn>3</mn></msup></mrow></mfrac><mo>+</mo><msup><mrow><mi>O</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mrow><mi>x</mi><mrow><mo>-</mo><mi>a</mi></mrow></mrow></mrow><mo>)</mo></mrow></mrow><mn>4</mn></msup></mrow>");
  }

  public void testEdge() {
    IExpr expr = EvalEngine.get().evaluate("DirectedEdge(a,b)");
    check(expr, "<mrow><mi>a</mi><mo>-&gt;</mo><mi>b</mi></mrow>");
    expr = EvalEngine.get().evaluate("UndirectedEdge(a,b)");
    check(expr, "<mrow><mi>a</mi><mo>&lt;-&gt;</mo><mi>b</mi></mrow>");

    check("Graph({1,2,3},{1<->2,2<->3})", //
        "<mrow><mi>Graph</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mo>{</mo><mrow><mrow><mn>1</mn><mo>&lt;-&gt;</mo><mn>2</mn></mrow><mo>,</mo><mrow><mn>2</mn><mo>&lt;-&gt;</mo><mn>3</mn></mrow></mrow><mo>}</mo></mrow></mrow><mo>)</mo></mrow></mrow>");
    check("Graph({1,2,3},{1->2,2->3})", //
        "<mrow><mi>Graph</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mo>{</mo><mrow><mrow><mn>1</mn><mo>-&gt;</mo><mn>2</mn></mrow><mo>,</mo><mrow><mn>2</mn><mo>-&gt;</mo><mn>3</mn></mrow></mrow><mo>}</mo></mrow></mrow><mo>)</mo></mrow></mrow>");
  }

  public void testInequality() {
    IExpr expr = EvalEngine.get().evaluate("Inequality(c,Less,0,Less,a)");
    check(expr, "<mrow><mi>c</mi><mo>&lt;</mo><mn>0</mn><mo>&lt;</mo><mi>a</mi></mrow>");

    expr = EvalEngine.get().evaluate("Inequality(a,Less,0,LessEqual,b, Equal,c, Unequal,d)");
    check(expr,
        "<mrow><mrow><mi>a</mi><mo>&lt;</mo><mn>0</mn><mo>&lt;=</mo><mi>b</mi><mo>==</mo><mi>c</mi></mrow><mo>&#x2227;</mo><mrow><mi>c</mi><mo>!=</mo><mi>d</mi></mrow></mrow>");
  }

  public void testCenterDot() {
    IExpr expr = EvalEngine.get().evaluate("CenterDot(x+z, f(a))");
    check(expr,
        "<mrow><mrow><mrow><mo>(</mo><mi>z</mi><mo>+</mo><mi>x</mi><mo>)</mo></mrow></mrow><mo>&#183;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>a</mi></mrow><mo>)</mo></mrow></mrow></mrow>");

    expr = EvalEngine.get().evaluate("CircleDot(x+z, f(a))");
    check(expr,
        "<mrow><mrow><mrow><mo>(</mo><mi>z</mi><mo>+</mo><mi>x</mi><mo>)</mo></mrow></mrow><mo>&#8857;</mo><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>a</mi></mrow><mo>)</mo></mrow></mrow></mrow>");
  }

  public void testSubscript() {
    IExpr expr = EvalEngine.get().evaluate("Subscript(\"zzz\",36)");
    check(expr, //
        "<msub><mtext>zzz</mtext><mn>36</mn></msub>");
    expr = EvalEngine.get().evaluate("Subscript(a,1,2,3)");
    check(expr, //
        "<msub><mi>a</mi><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow></msub>");

    expr = EvalEngine.get()
        .evaluate("poly[vars_List, a_, order_] := Module[{n = Length@vars, idx, z},\n"
            + "                                          idx = Cases[Tuples[Range[0, order], n], x_ /; Plus @@ x <= order];\n"
            + "                                          z = Times @@@ (vars^# & /@ idx);\r\n"
            + "  z.((Subscript[a, #]) & /@ idx)\n" + "                                         ];\n"//
            + "poly[{x},C,2]");
    check(expr, //
        "<mrow><mrow><msup><mi>x</mi><mn>2</mn></msup><mo>&#0183;</mo><msub><mi>C</mi><mrow><mo>{</mo><mrow><mn>2</mn></mrow><mo>}</mo></mrow></msub></mrow><mo>+</mo><mrow><mi>x</mi><mo>&#0183;</mo><msub><mi>C</mi><mrow><mo>{</mo><mrow><mn>1</mn></mrow><mo>}</mo></mrow></msub></mrow><mo>+</mo><msub><mi>C</mi><mrow><mo>{</mo><mrow><mn>0</mn></mrow><mo>}</mo></mrow></msub></mrow>");
  }

  public void testSuperscript() {
    IExpr expr = EvalEngine.get().evaluate("Superscript(xx,yy)");
    check(expr, //
        "<msup><mi>xx</mi><mi>yy</mi></msup>");
  }

  public void testInterval001() {
    IExpr expr = EvalEngine.get().evaluate("Interval({-3.21625*10^-16,5.66554*10^-16})");
    check(expr, //
        "<mrow><mi>Interval</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mo>{</mo><mn>-3.21625*10^-16</mn><mo>,</mo><mn>5.66554*10^-16</mn><mo>}</mo></mrow><mo>)</mo></mrow></mrow>");
  }

  public void testInterval002() {
    IExpr expr = EvalEngine.get().evaluate("Cot(Interval({3*Pi/4,6*Pi/5}))");
    check(expr, //
        "<mrow><mi>Interval</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mo>{</mo><mrow><mo>-</mo><mi>&#x221E;</mi></mrow><mo>,</mo><mn>-1</mn><mo>}</mo></mrow><mo>,</mo><mrow><mo>{</mo><msqrt><mrow><mfrac><mn>2</mn><msqrt><mn>5</mn></msqrt></mfrac><mo>+</mo><mn>1</mn></mrow></msqrt><mo>,</mo><mi>&#x221E;</mi><mo>}</mo></mrow><mo>)</mo></mrow></mrow>");
  }

  public void testConjugate() {
    // TODO convert to x^* like in TeX?
    IExpr expr = EvalEngine.get().evaluate("Conjugate(x)");
    check(expr, //
        "<mrow><mi>Conjugate</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>x</mi></mrow><mo>)</mo></mrow></mrow>");
  }

  public void testMatrixForm() {
    IExpr expr = EvalEngine.get().evaluate(
        "SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}) // MatrixForm");
    check(expr, //
        "<mrow><mo>(</mo><mtable columnalign=\"center\"><mtr><mtd columnalign=\"center\"><mn>1</mn></mtd><mtd columnalign=\"center\"><mn>0</mn></mtd><mtd columnalign=\"center\"><mn>4</mn></mtd></mtr><mtr><mtd columnalign=\"center\"><mn>0</mn></mtd><mtd columnalign=\"center\"><mn>2</mn></mtd><mtd columnalign=\"center\"><mn>0</mn></mtd></mtr><mtr><mtd columnalign=\"center\"><mn>0</mn></mtd><mtd columnalign=\"center\"><mn>0</mn></mtd><mtd columnalign=\"center\"><mn>3</mn></mtd></mtr></mtable><mo>)</mo></mrow>");
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

  public void testMissing() {
    IExpr expr = EvalEngine.get().evaluate("Missing(\"test1\", \"test2\")");
    check(expr,
        "<mrow><mi>Missing</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mtext>test1</mtext><mo>,</mo><mtext>test2</mtext></mrow><mo>)</mo></mrow></mrow>");
  }

  /** The JUnit setup method */
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
