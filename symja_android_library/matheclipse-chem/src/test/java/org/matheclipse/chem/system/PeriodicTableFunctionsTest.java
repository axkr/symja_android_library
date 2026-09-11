package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/**
 * <code>PeriodicTablePlot</code>, which moved here from <code>matheclipse-core</code>.
 *
 * <p>
 * Everything it draws about the elements now comes from <code>ElementData</code> rather than from
 * tables of its own, so these assert the two are in step.
 */
public class PeriodicTableFunctionsTest extends AbstractTestCase {

  @Test
  public void testPeriodicTablePlotIsAGraphicsObject() {
    check("Head(PeriodicTablePlot())", //
        "Graphics");
    // 118 element cells and the four markers standing in for the lanthanides and actinides
    check("Count(PeriodicTablePlot(), _Rectangle, Infinity)", //
        "122");
  }

  @Test
  public void testCellColourFollowsTheClassOfElements() {
    // hydrogen is a nonmetal and iron a transition metal, according to the table itself - the
    // colours used to come from atomic numbers hard coded here, and are now the reference's own
    check("PeriodicTablePlot()[[1,1,1,1]]", //
        "RGBColor(0.493332,0.733333,0.866667)");
    check("PeriodicTablePlot()[[1,26,1,1]]", //
        "RGBColor(0.866667,0.6,0.84)");
  }

  /**
   * The plain table is coloured by a finer classification than the <code>Series</code> of
   * <code>ElementData</code>, both refinements read off the table rather than tabulated again.
   */
  @Test
  public void testTheClassesOfTheReference() {
    // the chalcogens are their own class, brown, rather than being spread over the nonmetals
    // (O, S, Se), the metalloids (Te) and the poor metals (Po) as their series has them
    for (String chalcogen : new String[] {"8", "16", "34", "52", "84"}) {
      check("PeriodicTablePlot()[[1," + chalcogen + ",1,1]]", //
          "RGBColor(0.848053,0.621035,0.401591)");
    }
    // livermorium is in group 16 as well, but is counted with the poor metals of its period
    check("PeriodicTablePlot()[[1,116,1,1]]", //
        "RGBColor(1.0,0.833333,0.333333)");
    // lutetium and lawrencium are the two f-block elements the table gives a group, and are
    // counted with the transition metals rather than the lanthanides and actinides
    check(
        "PeriodicTablePlot()[[1,71,1,1]] === PeriodicTablePlot()[[1,103,1,1]] ==="
            + " PeriodicTablePlot()[[1,26,1,1]]", //
        "True");
    // an element whose series the table does not know takes the class of its group
    check("PeriodicTablePlot()[[1,117,1,1]] === PeriodicTablePlot()[[1,9,1,1]]", //
        "True");
    check("PeriodicTablePlot()[[1,118,1,1]] === PeriodicTablePlot()[[1,2,1,1]]", //
        "True");
    // so every cell of the plain table has a class, and none is drawn as missing data
    check("FreeQ(PeriodicTablePlot(), Missing)", //
        "True");
  }

  @Test
  public void testCategoricalPropertyKeepsALegend() {
    // Series takes ten values across the table, so ten colours and ten labels. The elements the
    // table has no series for are not an eleventh category: they are drawn grey and left unnamed
    check("Length(PeriodicTablePlot(\"Series\")[[2,2]])", //
        "10");
    check("Head(PeriodicTablePlot(\"Series\")[[2]])", //
        "SwatchLegend");
    check("FreeQ(PeriodicTablePlot(\"Series\")[[2]], Missing)", //
        "True");
  }

  /** The second of the reference's basic examples, which needs the computed `Phase` property. */
  @Test
  public void testPhase() {
    check("Length(PeriodicTablePlot(\"Phase\")[[2,2]])", //
        "3");
    check("PeriodicTablePlot(\"Phase\")[[2,2]]", //
        "{Gas,Solid,Liquid}");
    // the fifteen heaviest elements have no phase to report, and are drawn grey
    check("PeriodicTablePlot(\"Phase\")[[1,1,118,1,1,1]]", //
        "RGBColor(0.862745,0.862745,0.862745)");
  }

  @Test
  public void testMeasuredPropertyIsAScaleWithABarLegend() {
    // A melting point takes 118 different values. Giving each its own colour from a ten colour
    // cycle said nothing, so a measurement is drawn on a scale instead - and the legend beside it
    // is that scale, labelled with the range the table covers
    check("PeriodicTablePlot(\"MeltingPoint\")[[2]]", //
        "BarLegend({ColorDataFunction(TEMPERATURE,Gradients,{0,1}),{-272.2,3641.85}})");
    // the value itself stays reachable on every cell
    check("Count(PeriodicTablePlot(\"MeltingPoint\"), _Tooltip, Infinity)", //
        "118");
  }

  @Test
  public void testPropertyAndElementArguments() {
    // a mapped property comes with the scale it is drawn on, as Legended[Graphics(...), legend]
    check("Head(PeriodicTablePlot(EntityProperty(\"Element\", \"MassDensity\")))", //
        "Legended");
    check("Head(PeriodicTablePlot(\"Iron\"))", //
        "Graphics");
    check("Head(PeriodicTablePlot({\"Fe\", \"Cu\"}))", //
        "Graphics");
    check("Head(PeriodicTablePlot(EntityClass(\"Element\", \"NobleGas\")))", //
        "Graphics");
    // an element that was not asked for is dimmed rather than dropped
    check("PeriodicTablePlot(Entity(\"Element\", \"Carbon\"))[[1,6,1,1]]", //
        "RGBColor(0.493332,0.733333,0.866667)");
    check("PeriodicTablePlot(Entity(\"Element\", \"Carbon\"))[[1,1,1,1]]", //
        "RGBColor(0.924,0.96,0.98)");
  }

  @Test
  public void testGraphicsOptionsReachTheGraphics() {
    // they used to be read into an array of values and then looked for among the arguments as
    // rules, where there were none, so every one of them was dropped
    check("PeriodicTablePlot(ImageSize->500, PlotLabel->\"Table\")[[2;;]]", //
        "Graphics(ImageSize->500,PlotLabel->Table,PlotRange->All)");
    check("PeriodicTablePlot(Background->StandardBlue)[[2]]", //
        "Background->RGBColor(0.4,0.6,1.0)");
    // PlotRange defaults to All here and to Automatic in Graphics, so it is always said
    check("PeriodicTablePlot()[[2]]", //
        "PlotRange->All");
  }

  @Test
  public void testPlotLegends() {
    // the plain table carries no legend until one is asked for, and then it names the series
    check("FreeQ(PeriodicTablePlot(), PlotLegends)", //
        "True");
    check("PeriodicTablePlot(PlotLegends->True)[[2,2]]", //
        "{Nonmetal,NobleGas,AlkaliMetal,AlkalineEarthMetal,Metalloid,Chalcogen,Halogen,PoorMetal,"
            + "TransitionMetal,Lanthanide,Actinide}");
    check("PeriodicTablePlot(PlotLegends->True)[[2,2,1]]", //
        "Nonmetal");
    // and the labels a caller gives are the labels of the legend
    check("PeriodicTablePlot(PlotLegends->{\"a\",\"b\"})[[2,2]]", //
        "{a,b}");
    check("PeriodicTablePlot(\"Series\", PlotLegends->False)[[2;;]]", //
        "Graphics(PlotRange->All)");
  }

  @Test
  public void testColorFunction() {
    // a named scheme colours the series bands of the plain table
    check("PeriodicTablePlot(ColorFunction->\"AvocadoColors\")[[1,1,1,1]]", //
        "RGBColor(0.0,0.0,0.0,1.0)");
    check("PeriodicTablePlot(ColorFunction->\"BrightBands\")[[1,1,1,1]]", //
        "RGBColor(0.90222,0.101808,0.198306,1.0)");
    // lettering follows the cell, so it does not disappear into the dark end of a scale
    check("PeriodicTablePlot(ColorFunction->\"AvocadoColors\")[[1,1,1,3,1,3]]", //
        "RGBColor(1,1,1)");
    // asking for an indexed scheme says the values are categories, even when they are numbers
    check(
        "Head(PeriodicTablePlot(EntityProperty(\"Element\",\"Group\"),"
            + " ColorFunction->ColorData(97))[[2]])", //
        "SwatchLegend");
    // a mapped property puts its value on the cell, so the cell sits inside a Tooltip
    check(
        "PeriodicTablePlot(EntityProperty(\"Element\",\"Group\"),"
            + " ColorFunction->ColorData(97))[[1,1,1,1,1,1]]", //
        "RGBColor(0.368627,0.505882,0.709804,1.0)");
    // a scale the caller names is the one the legend beside it shows
    check("PeriodicTablePlot(\"AtomicRadius\", ColorFunction->\"AvocadoColors\")[[2]]", //
        "BarLegend({ColorDataFunction(AVOCADO,Gradients,{0,1}),{31.0,260.0}})");
    // and a setting that is not a colour function is reported rather than quietly ignored
    check("PeriodicTablePlot(ColorFunction->42)[[1,1,1,1]]", //
        "RGBColor(0.493332,0.733333,0.866667)");
  }

  @Test
  public void testArgumentsThatNameNothing() {
    // Entity and EntityProperty with one argument used to reach for a second and throw
    check("PeriodicTablePlot(Entity(\"Element\"))", //
        "PeriodicTablePlot(Entity(Element))");
    check("PeriodicTablePlot(EntityProperty(\"Element\"))", //
        "PeriodicTablePlot(EntityProperty(Element))");
    // a name that is neither a property nor an element is said to be one, not drawn grey
    check("PeriodicTablePlot(\"Bogus\")", //
        "PeriodicTablePlot(Bogus)");
    check("PeriodicTablePlot(EntityClass(\"Element\", \"Bogus\"))", //
        "PeriodicTablePlot(EntityClass(Element,Bogus))");
    check("PeriodicTablePlot(1, 2)", //
        "PeriodicTablePlot(1,2)");
  }
}
