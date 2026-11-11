package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The named color scheme "Atoms": element colors, its range, color function and image. */
public class ColorDataAtomsTest extends ExprEvaluatorTestCase {

  @Test
  public void testElementColors() {
    // by symbol in any case or by atomic number
    check("{ColorData(\"Atoms\", \"Fe\"), ColorData(\"Atoms\")[\"fe\"], ColorData(\"Atoms\", 26)}", //
        "{RGBColor(0.878431,0.4,0.2),RGBColor(0.878431,0.4,0.2),RGBColor(0.878431,0.4,0.2)}");
    // exact zero channels stay exact
    check("ColorData(\"Atoms\", \"Po\")", //
        "RGBColor(0.670588,0.360784,0)");
    // the provisional names of the elements 112 to 118 follow the 118 elements
    check("ColorData(\"Atoms\")[\"Uut\"] == ColorData(\"Atoms\")[120] == ColorData(\"Atoms\", \"Nh\")", //
        "True");
    // no element
    check("Head(Head(ColorData(\"Atoms\")[126]))", //
        "ColorDataFunction");
    check("{ColorData(\"Atoms\", \"Iron\"), ColorData(\"Atoms\", 1.5)}", //
        "{ColorData(Atoms,Iron),ColorData(Atoms,1.5)}");
  }

  @Test
  public void testRangeAndColorList() {
    check("r = ColorData(\"Atoms\", \"Range\"); {Dimensions(r), r[[1, {1, 79, 118}]]}", //
        "{{1,118},{H,Au,Og}}");
    check("l = ColorData(\"Atoms\", \"ColorList\"); {Length(l), l[[79]] === ColorData(\"Atoms\", \"Au\")}", //
        "{118,True}");
    check("ColorData(\"Atoms\", \"ColorFunction\") === ColorData(\"Atoms\")", //
        "True");
    check("Take(ColorData(\"Atoms\"), 2)", //
        "ColorDataFunction(Atoms,Named)");
  }

  @Test
  public void testPeriodicTableImage() {
    check("g = ColorData(\"Atoms\", \"Image\"); {Head(g), Dimensions(g[[1, 2]]), g[[1, 1]], Options(g)}", //
        "{Graphics,{10,18},EdgeForm(Opacity(0.1)),{ContentSelectable->False,PlotRangePadding->None}}");
    // the element in each cell: helium above the noble gases, the f-block in the rows 9 and 10,
    // the 8th row and the 3rd column of the periods 6 and 7 empty
    check("t = Map(If(# === {}, 0, #[[3, 1, 1]]) &, ColorData(\"Atoms\", \"Image\")[[1, 2]], {2}); "
        + "{t[[1, {1, 2, 18}]], t[[6, {2, 3, 4}]], Union(t[[8]]), t[[9, {2, 3, 17, 18}]], t[[10, 3]]}", //
        "{{H,0,He},{Ba,0,Hf},{0},{0,La,Lu,0},Ac}");
    // a cell: the element color, a square of side 0.9 around {column, 11 - row}, the bold label
    check("c = ColorData(\"Atoms\", \"Image\")[[1, 2, 5, 9]]; "
        + "{c[[1]], c[[2]], Head(c[[3]]), Head(c[[3, 1]]), List @@ c[[3, 1]]}", //
        "{RGBColor(0.0793033,0.50362,0.579453),Rectangle({8.55,5.55},{9.45,6.45}),Inset,Style,"
            + "{Rh,FontSize->Scaled(0.022),Bold,FontFamily->Helvetica,GrayLevel(0.85)}}");
    check("Rest(c[[3]])", //
        "Inset({9,6},Alignment->{Center,Center})");
    // a dark label on a light color, a light label on a dark color
    check("Cases(ColorData(\"Atoms\", \"Image\"), Inset(Style(s_, ___, GrayLevel(x_)), ___) :> {s, x}, Infinity)"
        + "[[{1, 8, 26, 47, 67}]]", //
        "{{H,0.2},{O,0.85},{Fe,0.2},{Ag,0.2},{Pb,0.85}}");
  }
}
