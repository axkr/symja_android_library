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
