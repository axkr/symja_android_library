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
        "TagBox(FormBox(TagBox(RowBox({\"\",\"(\",\"w\",\")\"}),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
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
}
