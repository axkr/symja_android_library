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
  public void testScriptMarks() {
    check("ToBoxes(SuperDagger(m)) // InputForm", //
        "SuperscriptBox(\"m\",\"†\")");
    check("ToBoxes(TraditionalForm(HoldForm(Subscript(q, k)^3))) // InputForm", //
        "TagBox(FormBox(TagBox(SubsuperscriptBox(\"q\",\"k\",\"3\"),HoldForm),TraditionalForm),TraditionalForm,Editable->True)");
  }
}
