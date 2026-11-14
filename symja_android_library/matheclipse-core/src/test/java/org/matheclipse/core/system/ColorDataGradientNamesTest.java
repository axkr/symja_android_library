package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The gradient collection of ColorData and the names its gradients are described by. */
public class ColorDataGradientNamesTest extends ExprEvaluatorTestCase {

  @Test
  public void testGradientCollection() {
    check("Length(ColorData(\"Gradients\"))", //
        "51");
    check("ColorData(\"Gradients\")[[{1, 14, 33, 51}]]", //
        "{AlpineColors,CMYKColors,Pastel,WatermelonColors}");
    // every gradient of the collection is a colour function
    check("Union(Map(Head(ColorData(#)[0.7]) &, ColorData(\"Gradients\")))", //
        "{RGBColor}");
  }

  @Test
  public void testChartingCollection() {
    // the numbers of the indexed schemes for charts
    check("c = ColorData(\"Charting\"); {Length(c), c[[{1, 19, 20, 49, 50, -1}]], "
        + "Complement(Range(44, 119), c)}", //
        "{72,{44,62,64,93,97,119},{63,94,95,96}}");
    // every one of them has a color list
    check("Union(Map(Head(ColorData(#)[1]) &, ColorData(\"Charting\")))", //
        "{GrayLevel,Hue,LABColor,RGBColor}");
    // hues keep their exact components, and a list starts over after its last color
    check("{ColorData(67)[4], ColorData(67)[11], ColorData(88, \"Range\"), "
        + "ColorData(114)[6]}", //
        "{Hue(0.05,0.8,1),Hue(0,1,1),{1,10,1},LABColor(0.816797,0.145001,0.828253)}");
    // listed colors, then a blend without end; scheme 115 blends in L*a*b*
    check("{ColorData(117, \"Range\"), ColorData(117)[12], ColorData(117)[13], "
        + "ColorData(115)[9]}", //
        "{{1,Infinity,1},RGBColor(0.9,0.45,0.45),RGBColor(0.763219,0.81744,0.0),"
            + "RGBColor(0.944965,0.769367,0.0)}");
    // the schemes 44 to 62 are listed ones, named by their number
    check("Map(Length(ColorData(#, \"ColorList\")) &, Range(44, 62))", //
        "{13,11,15,12,10,10,11,10,10,13,21,13,12,15,14,14,21,9,13}");
    check("{ColorData(47, \"Name\"), ColorData(47, \"Range\"), ColorData(47)[2], "
        + "ColorData(47)[14], ColorData(56)[11], ColorData(62)[0]}", //
        "{47,{1,12,1},GrayLevel(0.660639),GrayLevel(0.660639),"
            + "RGBColor(0.823529,0.00888075,0.0356909),RGBColor(0.338262,0.480644,0.141192)}");
  }

  @Test
  public void testGradientName() {
    check("Map(ColorData(#, \"Name\") &, {\"IslandColors\", \"RedBlueTones\", \"CMYKColors\", "
        + "\"LightTemperatureMap\", \"GreenBrownTerrain\"})", //
        "{island colors,red-blue tones,CMYK colors,light temperature map,green-brown terrain}");
    // the same description for a shorter name and from the colour function
    check("{ColorData(\"Neon\", \"Name\"), ColorData(\"StarryNight\")[\"Name\"]}", //
        "{neon colors,starry night colors}");
    // a scheme outside the collection
    check("ColorData(\"Parula\", \"Name\")", //
        "parula");
  }

  @Test
  public void testAddedGradientEnds() {
    // the ends of a gradient are its first and last table colour
    check("ColorData(\"RustTones\", 0)", //
        "RGBColor(0.0,0.00505074,0.191104,1.0)");
    check("ColorData(\"LakeColors\")[1]", //
        "RGBColor(0.941176,0.906538,0.834043,1.0)");
  }
}
