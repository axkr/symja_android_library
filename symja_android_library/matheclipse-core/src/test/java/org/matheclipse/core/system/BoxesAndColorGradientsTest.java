package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Boxes and color gradients. */
public class BoxesAndColorGradientsTest extends ExprEvaluatorTestCase {

  @Test
  public void testTraditionalFormFunctionCall() {
    check("ToBoxes(TraditionalForm(g(u, v) + Cosh(u))) // InputForm", //
        "TagBox(FormBox(RowBox({RowBox({\"cosh\",\"(\",\"u\",\")\"}),\"+\",RowBox({\"g\",\"(\",RowBox({\"u\",\",\",\"v\"}),\")\"})}),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(Tan(y)^k)) // InputForm", //
        "TagBox(FormBox(RowBox({SuperscriptBox(\"tan\",\"k\"),\"(\",\"y\",\")\"}),TraditionalForm),TraditionalForm,Editable->True)");
    // a fractional power of a function stays a superscript of the call
    check("ToBoxes(TraditionalForm(Log(y)^(2/3))) // InputForm", //
        "TagBox(FormBox(SuperscriptBox(RowBox({\"log\",\"(\",\"y\",\")\"}),FractionBox(\"2\",\"3\")),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testTraditionalFormExponentialAndReciprocal() {
    check("ToBoxes(TraditionalForm(E^(2*y))) // InputForm", //
        "TagBox(FormBox(SuperscriptBox(\"\uF74D\",RowBox({\"2\",\" \",\"y\"})),TraditionalForm),TraditionalForm,Editable->True)");
    check("ToBoxes(TraditionalForm(1/Log(y))) // InputForm", //
        "TagBox(FormBox(FractionBox(\"1\",RowBox({\"log\",\"(\",\"y\",\")\"})),TraditionalForm),TraditionalForm,Editable->True)");
    // under the fraction line the power still goes on the function name
    check("ToBoxes(TraditionalForm(1/Log(y)^3)) // InputForm", //
        "TagBox(FormBox(FractionBox(\"1\",RowBox({SuperscriptBox(\"log\",\"3\"),\"(\",\"y\",\")\"})),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testTraditionalFormCurriedCall() {
    check("ToBoxes(TraditionalForm(Apply(h(u), {w}))) // InputForm", //
        "TagBox(FormBox(RowBox({RowBox({\"h\",\"(\",\"u\",\")\"}),\"(\",\"w\",\")\"}),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testPiecewiseBox() {
    check("ToBoxes(Piecewise({{p, q > 0}, {s, q < -1}}, r)) // InputForm", //
        "TagBox(GridBox({{\"\uF361\",GridBox({{\"p\",RowBox({\"q\",\">\",\"0\"})},{\"s\",RowBox({\"q\",\"<\",RowBox({\"-\",\"1\"})})},{\"r\",TagBox(\"True\",\"PiecewiseDefault\",AutoDelete->True)}},"
            + "ColumnAlignments->{Left},ColumnSpacings->1.2`,ColumnWidths->Automatic,AllowedDimensions->{2,Automatic},Selectable->True,Editable->True)}},"
            + "ColumnAlignments->{Left},ColumnSpacings->0.5`,ColumnWidths->Automatic),\"Piecewise\",SyntaxForm->Equal,SelectWithContents->True,StripWrapperBoxes->True,Selectable->False,Editable->False,DeleteWithContents->True)");
    check("ToBoxes(TraditionalForm(Piecewise({{p, q > 0}}))) // InputForm", //
        "TagBox(FormBox(TagBox(GridBox({{\"\uF361\",GridBox({{\"p\",RowBox({\"q\",\">\",\"0\"})},{\"0\",TagBox(\"True\",\"PiecewiseDefault\",AutoDelete->True)}},"
            + "ColumnAlignments->{Left},ColumnSpacings->1.2`,ColumnWidths->Automatic,AllowedDimensions->{2,Automatic},Selectable->True,Editable->True)}},"
            + "ColumnAlignments->{Left},ColumnSpacings->0.5`,ColumnWidths->Automatic),\"Piecewise\",SyntaxForm->Equal,SelectWithContents->True,StripWrapperBoxes->True,Selectable->False,Editable->False,DeleteWithContents->True),TraditionalForm),TraditionalForm,Editable->True)");
  }

  @Test
  public void testBlendLABColor() {
    check("Blend({LABColor(0.1, 0.2, 0.3), LABColor(0.5, 0.0, -0.1)}, 0.5)", //
        "LABColor(0.3,0.1,0.1)");
    // a LAB color among RGB colors is blended as an RGB color
    check("Head(Blend({LABColor(0.5, 0.0, 0.0), RGBColor(0, 0, 1)}, 0.5))", //
        "RGBColor");
  }

  @Test
  public void testBarLegendPlacement() {
    check("Plot(Sin(t), {t, 0, 2}, PlotLegends -> BarLegend(\"TemperatureMap\"))[[2]]", //
        "Placed(BarLegend(TemperatureMap),After)");
    check(
        "{Head(#), #[[2]], #[[1, 2]]}&(Plot({t, 2*t}, {t, 0, 1}, PlotLegends -> {\"p\", \"q\"})[[2]])", //
        "{Placed,After,{p,q}}");
    // a placement the caller gives is kept
    check("Plot(t^2, {t, -1, 1}, PlotLegends -> Placed(BarLegend(\"Rainbow\"), Before))[[2]]", //
        "Placed(BarLegend(Rainbow),Before)");
    // the legends of nested Show calls are drawn without their placements
    check("q1 = Plot(t, {t, 0, 1}, PlotLegends -> {\"first\"}); "
        + "q2 = Plot(t^2, {t, 0, 1}, PlotLegends -> {\"second\"}); "
        + "q3 = Plot(t^3, {t, 0, 1}, PlotLegends -> {\"third\"}); "
        + "s = Show(Show(q1, q2), q3); svg = ExportString(s, \"SVG\"); "
        + "{Length(s[[2]]), Head(s[[2, 1, 1]]), StringContainsQ(svg, \"first\"), StringContainsQ(svg, \"third\"), "
        + "StringContainsQ(svg, \"Placed\")}", //
        "{2,Placed,True,True,False}");
  }

  @Test
  public void testThemeGradient() {
    check("g = \"WL14DefaultDensityGradient\"; c = ColorData(g, 0.6); "
        + "{Head(c), c === ColorData(g)[0.6] === Blend(g, 0.6), ColorData(g, \"Name\"), ColorData(g, \"Range\")}", //
        "{LABColor,True,WL14 default density gradient,{0,1}}");
    check("Length(ColorData(\"WL14DefaultDensityGradient\", \"BlendArgument\"))", //
        "4");
    check(
        "ColorData(\"WL14DefaultDensityGradient\")[1.5] === "
            + "Last(ColorData(\"WL14DefaultDensityGradient\", \"BlendArgument\"))", //
        "True");
  }
}
