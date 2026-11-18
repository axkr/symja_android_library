package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** TraditionalForm notation. */
public class TraditionalFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testTraditionalFormMergedFractions() {
    // a product of quotients is one fraction; a group of several factors is bracketed when it
    // shares its side of the line
    check("ToBoxes(TraditionalForm(HoldForm((u/v)*(w/z)))) // InputForm", //
        "TagBox(FormBox(TagBox(FractionBox(RowBox({\"u\",\" \",\"w\"}),RowBox({\"v\",\" \",\"z\"})),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm((u/(v*w))*(p/(q*r))))) // InputForm", //
        "TagBox(FormBox(TagBox(FractionBox(RowBox({\"u\",\" \",\"p\"}),RowBox({RowBox({\"(\",RowBox({\"v\",\" \",\"w\"}),\")\"}),\" \",RowBox({\"(\",RowBox({\"q\",\" \",\"r\"}),\")\"})})),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(-(k^3/(3*s))*w))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"-\",FractionBox(RowBox({SuperscriptBox(\"k\",\"3\"),\" \",\"w\"}),RowBox({\"3\",\" \",\"s\"}))}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    // a negative coefficient puts its sign in front of the fraction
    check("ToBoxes(HoldForm(-5*(p/q))) // InputForm", //
        "TagBox(RowBox({\"-\",FractionBox(RowBox({\"5\",\" \",\"p\"}),\"q\")}),HoldForm)");
  }

  @Test
  public void testHeldProductKeepsParentheses() {
    check("ToBoxes(HoldForm(u*(v*w))) // InputForm", //
        "TagBox(RowBox({\"u\",\" \",RowBox({\"(\",RowBox({\"v\",\" \",\"w\"}),\")\"})}),HoldForm)");
    check("ToBoxes(HoldForm(5*(7*w))) // InputForm", //
        "TagBox(RowBox({\"5\",\" \",RowBox({\"(\",RowBox({\"7\",\" \",\"w\"}),\")\"})}),HoldForm)");
    // quotients merge into one fraction in StandardForm too
    check("ToBoxes(HoldForm((u/v)*(w/z))) // InputForm", //
        "TagBox(FractionBox(RowBox({\"u\",\" \",\"w\"}),RowBox({\"v\",\" \",\"z\"})),HoldForm)");
  }

  @Test
  public void testTraditionalFormBracketsAndNames() {
    // a built-in which is no mathematical function keeps its brackets
    check("ToBoxes(TraditionalForm(HoldForm({Total(w), lcm(u, v), ArcCosh(w)}))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"{\",RowBox({RowBox({\"Total\",\"[\",\"w\",\"]\"}),\",\",RowBox({\"lcm\",\"(\",RowBox({\"u\",\",\",\"v\"}),\")\"}),\",\",RowBox({SuperscriptBox(\"cosh\",RowBox({\"-\",\"1\"})),\"(\",\"w\",\")\"})}),\"}\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testTraditionalFormDerivative() {
    check("ToBoxes(TraditionalForm(HoldForm(D(g(r, s), s)))) // InputForm", //
        "TagBox(FormBox(TagBox(FractionBox(RowBox({\"∂\",RowBox({\"g\",\"(\",RowBox({\"r\",\",\",\"s\"}),\")\"})}),RowBox({\"∂\",\"s\"}),MultilineFunction->None),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(D(g(r, s), r, {s, 3})))) // InputForm", //
        "TagBox(FormBox(TagBox(FractionBox(RowBox({SuperscriptBox(\"∂\",\"4\"),RowBox({\"g\",\"(\",RowBox({\"r\",\",\",\"s\"}),\")\"})}),RowBox({RowBox({\"∂\",\"r\"}),\" \",RowBox({\"∂\",SuperscriptBox(\"s\",\"3\")})}),MultilineFunction->None),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    // a sum is bracketed above the line; a quotient is differentiated by the operator in front
    check("ToBoxes(TraditionalForm(HoldForm(D(r^2 + 1, r)))) // InputForm", //
        "TagBox(FormBox(TagBox(FractionBox(RowBox({\"∂\",RowBox({\"(\",RowBox({SuperscriptBox(\"r\",\"2\"),\"+\",\"1\"}),\")\"})}),RowBox({\"∂\",\"r\"}),MultilineFunction->None),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(D(3/r, r)))) // InputForm", //
        "TagBox(FormBox(TagBox(TagBox(RowBox({FractionBox(\"∂\",RowBox({\"∂\",\"r\"})),FractionBox(\"3\",\"r\")}),D),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testTraditionalFormSpecialNotation() {
    check("ToBoxes(TraditionalForm(HoldForm(Binomial(m, 3)))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"(\",GridBox({{\"m\"},{\"3\"}}),\")\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(Hypergeometric2F1(p, q, r, w)))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({RowBox({SubscriptBox(\"\",\"2\"),SubscriptBox(\"F\",\"1\")}),\"(\",RowBox({\"p\",\",\",\"q\",\";\",\"r\",\";\",\"w\"}),\")\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(Div(h, {u, v, w})))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({SubscriptBox(\"∇\",RowBox({\"{\",RowBox({\"u\",\",\",\"v\",\",\",\"w\"}),\"}\"})),\"⋅\",\"h\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(HoldForm(E(w)))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"\\[ExponentialE]\",\"(\",\"w\",\")\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testSumOrder() {
    // the higher power first, the number last
    check("ToBoxes(w^4 + 2 + w, TraditionalForm) // InputForm", //
        "FormBox(RowBox({SuperscriptBox(\"w\",\"4\"),\"+\",\"w\",\"+\",\"2\"}),TraditionalForm)");
    // a symbolic exponent before a numeric one, a square root between the powers 1 and 0
    check("ToBoxes(r^3 + r^k + Sqrt(r), TraditionalForm) // InputForm", //
        "FormBox(RowBox({SuperscriptBox(\"r\",\"k\"),\"+\",SuperscriptBox(\"r\",\"3\"),\"+\",SqrtBox(\"r\")}),TraditionalForm)");
    // variables before functions, sin before cos before tan
    check("ToBoxes(Cos(r) + Tan(r) + 4*r + Sin(r), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"4\",\" \",\"r\"}),\"+\",RowBox({\"sin\",\"(\",\"r\",\")\"}),\"+\",RowBox({\"cos\",\"(\",\"r\",\")\"}),\"+\",RowBox({\"tan\",\"(\",\"r\",\")\"})}),TraditionalForm)");
    // a symbol in the argument of a function has degree 1
    check("ToBoxes(w*Sin(w) + w^3 + Sin(w), TraditionalForm) // InputForm", //
        "FormBox(RowBox({SuperscriptBox(\"w\",\"3\"),\"+\",RowBox({\"w\",\" \",RowBox({\"sin\",\"(\",\"w\",\")\"})}),\"+\",RowBox({\"sin\",\"(\",\"w\",\")\"})}),TraditionalForm)");
    check("ToBoxes(v*Cos(u) + u*Sin(u), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"v\",\" \",RowBox({\"cos\",\"(\",\"u\",\")\"})}),\"+\",RowBox({\"u\",\" \",RowBox({\"sin\",\"(\",\"u\",\")\"})})}),TraditionalForm)");
    // exp and log before the trigonometric functions; an inverse just before its function
    check("ToBoxes(Cos(r) + Log(r) + ArcTan(r) + Tan(r), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"log\",\"(\",\"r\",\")\"}),\"+\",RowBox({\"cos\",\"(\",\"r\",\")\"}),\"+\",RowBox({SuperscriptBox(\"tan\",RowBox({\"-\",\"1\"})),\"(\",\"r\",\")\"}),\"+\",RowBox({\"tan\",\"(\",\"r\",\")\"})}),TraditionalForm)");
    // the same function by its argument; an absolute value before the symbols
    check("ToBoxes(Cos(3*k) + Cos(k), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"cos\",\"(\",\"k\",\")\"}),\"+\",RowBox({\"cos\",\"(\",RowBox({\"3\",\" \",\"k\"}),\")\"})}),TraditionalForm)");
    check("ToBoxes(w^3 + Abs(w), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"Abs\",\"(\",\"w\",\")\"}),\"+\",SuperscriptBox(\"w\",\"3\")}),TraditionalForm)");
    check("ToBoxes(Abs(v) + u, TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"Abs\",\"(\",\"v\",\")\"}),\"+\",\"u\"}),TraditionalForm)");
    // a function without a fixed rank before the ranked ones; a power of a function as the
    // function
    check("ToBoxes(Cosh(r) + q(r), TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"q\",\"(\",\"r\",\")\"}),\"+\",RowBox({\"cosh\",\"(\",\"r\",\")\"})}),TraditionalForm)");
    check("ToBoxes(Cos(k)^3 + k, TraditionalForm) // InputForm", //
        "FormBox(RowBox({\"k\",\"+\",RowBox({SuperscriptBox(\"cos\",\"3\"),\"(\",\"k\",\")\"})}),TraditionalForm)");
    // the functions of a product in the same order
    check("ToBoxes(Cos(v)*Sin(u)*w, TraditionalForm) // InputForm", //
        "FormBox(RowBox({\"w\",\" \",RowBox({\"sin\",\"(\",\"u\",\")\"}),\" \",RowBox({\"cos\",\"(\",\"v\",\")\"})}),TraditionalForm)");
    check("ToString(w^2 + 1, TraditionalForm)", //
        "\\!\\(\\*FormBox[RowBox[{SuperscriptBox[\"w\", \"2\"], \"+\", \"1\"}], TraditionalForm]\\)");
  }

  @Test
  public void testSumSigns() {
    // a sum of two terms does not begin with its negative term
    check("ToBoxes(4 - v^3, TraditionalForm) // InputForm", //
        "FormBox(RowBox({\"4\",\"-\",SuperscriptBox(\"v\",\"3\")}),TraditionalForm)");
    // a negative first term is the minus sign in front of the term without its own sign
    check("ToBoxes(5 - u^2 - w, TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"-\",RowBox({SuperscriptBox(\"u\",\"2\")})}),\"-\",\"w\",\"+\",\"5\"}),TraditionalForm)");
    check("ToBoxes(-3*v - 7, TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"-\",RowBox({RowBox({\"3\"}),\" \",\"v\"})}),\"-\",\"7\"}),TraditionalForm)");
    check("ToBoxes(-v - w, TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"-\",RowBox({\"v\"})}),\"-\",\"w\"}),TraditionalForm)");
  }

  @Test
  public void testHeldSumKeepsItsOrder() {
    check("ToBoxes(HoldForm(2 + w + w^3), TraditionalForm) // InputForm", //
        "FormBox(TagBox(RowBox({\"2\",\"+\",\"w\",\"+\",SuperscriptBox(\"w\",\"3\")}),HoldForm),TraditionalForm)");
    check("ToBoxes(HoldForm(-3*v + 7), TraditionalForm) // InputForm", //
        "FormBox(TagBox(RowBox({RowBox({RowBox({\"-\",\"3\"}),\" \",\"v\"}),\"+\",\"7\"}),HoldForm),TraditionalForm)");
    // a held exponential stays a call of exp, and -k r is the product of -k and r
    check("ToBoxes(TraditionalForm(HoldForm(Exp(-k*r)))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"exp\",\"(\",RowBox({RowBox({\"-\",\"k\"}),\" \",\"r\"}),\")\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
    check("FullForm(Hold(Exp(w)))", //
        "Hold(Exp(w))");
    check("ReleaseHold(Hold(Exp(w)))", //
        "E^w");
  }

  @Test
  public void testScriptMarks() {
    check("ToBoxes(SuperDagger(m)) // InputForm", //
        "SuperscriptBox(\"m\",\"†\")");
    check("ToBoxes(TraditionalForm(HoldForm(Subscript(q, k)^3))) // InputForm", //
        "TagBox(FormBox(TagBox(SubsuperscriptBox(\"q\",\"k\",\"3\"),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }

  /** A subscripted variable is ordered among the factors of a product by its base symbol. */
  @Test
  public void testSubscriptedFactorOrder() {
    check("ToBoxes(TraditionalForm(v*Subscript(a, 5))) // InputForm", //
        "TagBox(FormBox(RowBox({SubscriptBox(\"a\",\"5\"),\" \",\"v\"}),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(y*Subscript(b, 4)*x)) // InputForm", //
        "TagBox(FormBox(RowBox({SubscriptBox(\"b\",\"4\"),\" \",\"x\",\" \",\"y\"}),TraditionalForm),TraditionalForm,Editable->True)");
    // the number stays in front
    check("ToBoxes(TraditionalForm(7*u*Subscript(d, 1))) // InputForm", //
        "TagBox(FormBox(RowBox({\"7\",\" \",SubscriptBox(\"d\",\"1\"),\" \",\"u\"}),TraditionalForm),TraditionalForm,Editable->True)");
    // a power is ordered by its base
    check("ToBoxes(TraditionalForm(u^3*Subscript(d, 1))) // InputForm", //
        "TagBox(FormBox(RowBox({SubscriptBox(\"d\",\"1\"),\" \",SuperscriptBox(\"u\",\"3\")}),TraditionalForm),TraditionalForm,Editable->True)");
    // the variables come before the functions
    check("ToBoxes(TraditionalForm(y*Sin(v)*Subscript(b, 4))) // InputForm", //
        "TagBox(FormBox(RowBox({SubscriptBox(\"b\",\"4\"),\" \",\"y\",\" \",RowBox({\"sin\",\"(\",\"v\",\")\"})}),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(Subscript(b, 7)*Subscript(b, 2))) // InputForm", //
        "TagBox(FormBox(RowBox({SubscriptBox(\"b\",\"2\"),\" \",SubscriptBox(\"b\",\"7\")}),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(a*Subscript(v, 6))) // InputForm", //
        "TagBox(FormBox(RowBox({\"a\",\" \",SubscriptBox(\"v\",\"6\")}),TraditionalForm),TraditionalForm,Editable->True)");
    // StandardForm and a held product keep the canonical order
    check("ToBoxes(v*Subscript(a, 5)) // InputForm", //
        "RowBox({\"v\",\" \",SubscriptBox(\"a\",\"5\")})");
    check("ToBoxes(TraditionalForm(HoldForm(v*Subscript(a, 5)))) // InputForm", //
        "TagBox(FormBox(TagBox(RowBox({\"v\",\" \",SubscriptBox(\"a\",\"5\")}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }

  /** <code>Mod(m, n)</code> is the template of the notation <code>m mod n</code>. */
  @Test
  public void testModTemplate() {
    check("ToBoxes(TraditionalForm(Mod(y, 5))) // InputForm", //
        "TagBox(FormBox(TemplateBox({\"y\",\"5\"},\"Mod\"),TraditionalForm),TraditionalForm,Editable->True)");
    // a product or a sum as operand is put in parentheses
    check("ToBoxes(TraditionalForm(Mod(3*y, k)))", //
        "TagBox(FormBox(TemplateBox({RowBox({(,RowBox({3, ,y}),)}),k},Mod),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(Mod(y, c + d)))", //
        "TagBox(FormBox(TemplateBox({y,RowBox({(,RowBox({c,+,d}),)})},Mod),TraditionalForm),TraditionalForm,Editable->True)");
    // the call with an offset has no such notation
    check("ToBoxes(TraditionalForm(Mod(y, 5, 1)))", //
        "TagBox(FormBox(RowBox({Mod,(,RowBox({y,,,5,,,1}),)}),TraditionalForm),TraditionalForm,Editable->True)");
    // the template reads back as the call
    check("ToExpression(TemplateBox({RowBox({\"3\", \" \", \"y\"}), \"5\"}, \"Mod\"))", //
        "Mod(3*y,5)");
    // the text mod in a row is a factor like any other name (the symbol Mod for the relaxed
    // parser of this test), and the number behind it another factor
    check("ToExpression(FormBox(RowBox({RowBox({\"(\", RowBox({\"3\", \" \", \"y\"}), \")\"}), \"mod\", \"5\"}), TraditionalForm), TraditionalForm)", //
        "15*Mod*y");
  }

  /**
   * A fraction times a sum is one fraction, unless the sum shows a fraction itself or the
   * fraction is negative: then the fraction goes in front.
   */
  @Test
  public void testFractionInFrontOfSum() {
    check("ToBoxes(5/3*(a + b), TraditionalForm) // InputForm", //
        "FormBox(FractionBox(RowBox({\"5\",\" \",RowBox({\"(\",RowBox({\"a\",\"+\",\"b\"}),\")\"})}),\"3\"),TraditionalForm)");
    check("ToBoxes(5/3*(a + b/7), TraditionalForm) // InputForm", //
        "FormBox(RowBox({FractionBox(\"5\",\"3\"),\" \",RowBox({\"(\",RowBox({\"a\",\"+\",FractionBox(\"b\",\"7\")}),\")\"})}),TraditionalForm)");
    check("ToBoxes(2/11*(a + 1/b)^3, TraditionalForm) // InputForm", //
        "FormBox(RowBox({FractionBox(\"2\",\"11\"),\" \",SuperscriptBox(RowBox({\"(\",RowBox({\"a\",\"+\",FractionBox(\"1\",\"b\")}),\")\"}),\"3\")}),TraditionalForm)");
    check("ToBoxes(-2/11*(a + b)^3, TraditionalForm) // InputForm", //
        "FormBox(RowBox({RowBox({\"-\",FractionBox(\"2\",\"11\")}),\" \",SuperscriptBox(RowBox({\"(\",RowBox({\"a\",\"+\",\"b\"}),\")\"}),\"3\")}),TraditionalForm)");
    check("ToBoxes(5/3*a^4, TraditionalForm) // InputForm", //
        "FormBox(FractionBox(RowBox({\"5\",\" \",SuperscriptBox(\"a\",\"4\")}),\"3\"),TraditionalForm)");
    // in StandardForm the product stays one fraction
    check("ToBoxes(5/3*(a + b/7)) // InputForm", //
        "FractionBox(RowBox({\"5\",\" \",RowBox({\"(\",RowBox({\"a\",\"+\",FractionBox(\"b\",\"7\")}),\")\"})}),\"3\")");
  }
}
